package cs6140.hw5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class Em {

	/**
	 * @param args
	 */
	public static double tol=0;
	public static String path= "/Users/zhouyf/Dropbox/machine HW/hw5/3gaussian.txt";
	public static int n=10000;
	public static int dimension=2;
	public static double[][] data=null;
	public static int clusterNum=0;
	public static List<double[]> mu= new ArrayList<double[]>(); 
	public static List<double[][]> sigma=new ArrayList<double[][]>();
	public static double[][] logR=null;
	public static double[][] gama=null;
	public static double[] w={0.3,0.4,0.3};
	public static double[] nj=null;
	public static MatlabProxy proxy=null;
	public static MatlabTypeConverter processor=null;
	public static double loglikelyhood=0;
	
	private static void init() throws IOException, MatlabConnectionException {
		// TODO Auto-generated method stub
		data = new double[dimension][n];
		//mu = new double[dimension][k];
		readData(path);
		setUpMatlabProvider();
		updateNJByW();
	}
	private static void setUpMatlabProvider() throws MatlabConnectionException {
		MatlabProxyFactory factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
		processor = new MatlabTypeConverter(proxy);
	}
	private static void close(){
		proxy.disconnect();
	}
	public static double[] logpdf(double[][] data, double[] mu, double[][] sigma,int kIndex) throws MatlabConnectionException, MatlabInvocationException{
    
		  processor.setNumericArray("mydata", new MatlabNumericArray(data,null));
		  proxy.setVariable("mu", mu);
		  processor.setNumericArray("Sigma", new MatlabNumericArray(sigma,null));
		  
		  proxy.eval("d=size(mydata,1);");
		  proxy.eval("mu=mu';");
		  proxy.eval("mydata=bsxfun(@minus,mydata,mu);");
		  proxy.eval("n = size(mydata,2);");
		  proxy.eval("result=zeros(1,n);");
		  //calculate log likelihood, we don't calculate inverse matrix for accuracy
		  proxy.eval("for i =1:n tmp=Sigma\\mydata(:,i);fai=mydata(:,i)'*tmp; result(i)=-0.5*(d*log(2*pi)+log(det(Sigma)))+(-0.5*fai);end");
		  proxy.eval("logR(:,"+kIndex+")=result");
		  double[] logpdf=((double[]) proxy.getVariable("result"));
		  return logpdf;
	}
	public static void readData(String path) throws IOException{
		File file =new File(path);
		FileReader fr = new FileReader(file);
		BufferedReader bf = new BufferedReader(fr);
		String str = "";
		int count=0;
		while ((str = bf.readLine()) != null) {
			String[] strArray=str.split("\\s+");
			data[0][count]=Double.parseDouble(strArray[0]);
			data[1][count]=Double.parseDouble(strArray[1]);
			count++;
		}
	}

	public static double estep() throws MatlabConnectionException, MatlabInvocationException{
		proxy.eval("logR=zeros("+n+","+clusterNum+")");
		for(int i=0;i<clusterNum;i++){
			double[] logpdfForK = logpdf(data, mu.get(i), sigma.get(i),(i+1));
//			for(int ri=0;ri<logpdfForK.length;ri++){
//				System.out.print("\t"+logpdfForK[ri]);
//			}
//			System.out.println("\n");
		}
		proxy.setVariable("w", w);
		proxy.eval("logR=bsxfun(@plus,logR,log(w))");
		proxy.eval("maxele=max(logR,[],2)");
		proxy.eval("normalLogR=bsxfun(@minus,logR,maxele)");
		proxy.eval("s4e=log(sum(exp(normalLogR),2))+maxele");
		double[] sum4e = (double[])proxy.getVariable("s4e");

	    //proxy.eval("denomi=log(sum(exp(logR),2))");
		proxy.eval("logR=bsxfun(@minus,logR,s4e)");
		proxy.eval("gama=exp(logR)");
		
		logR = processor.getNumericArray("logR").getRealArray2D();
	    gama = processor.getNumericArray("gama").getRealArray2D();
		return getLoglikelihood(sum4e);
	}
	public static double getLoglikelihood(double[] dataLikelihood){
		 double sum = 0;
		 for(double l:dataLikelihood){
			 sum+=l;
		 }
		 return sum/n;
	}
	public static double mstep() throws MatlabInvocationException, MatlabConnectionException{
		reCalculateWeight();
		reCalculateMu();
		reCalculateSigma();
		double loglikehoodObj=calObjLogLikelyhood();
		return loglikehoodObj;
	}

	private static double calObjLogLikelyhood() throws MatlabConnectionException, MatlabInvocationException {
		List<double[]> container=new ArrayList<double[]>();
		
		for(int k=0;k<clusterNum;k++){
			double[] logpdf4K=logpdf(data, mu.get(k), sigma.get(k),k+1);
			container.add(logpdf4K);
		}
		double logSum=0;
		for(int i=0;i<n;i++){
		   double ksum=0;
		   for(int k=0;k<clusterNum;k++){
			   	ksum+=Math.exp(container.get(k)[i])*w[k];
		   }
		   logSum+=Math.log(ksum);
		}
		
		return logSum/n;
	}
	private static void reCalculateSigma() throws MatlabInvocationException {
		processor.setNumericArray("mydata", new MatlabNumericArray(data,null));
		proxy.eval("Sigma = zeros("+dimension+","+dimension+");");
		for(int i=0;i<clusterNum;i++){
			proxy.eval("Sigma = zeros("+dimension+","+dimension+");");
			proxy.setVariable("mu", mu.get(i));
			proxy.setVariable("nj", nj[i]);
			proxy.eval("mu=mu'");
			proxy.eval("Xo=bsxfun(@minus,mydata,mu)");
			proxy.eval("sr=sqrt(gama)");
			proxy.eval("Xo=bsxfun(@times,Xo,sr(:,"+(i+1)+")');");
			proxy.eval("Sigma=Xo*Xo'/nj;");
			proxy.eval("Sigma=Sigma+eye(size(mydata,1))*(1e-5)");
			MatlabNumericArray sigmaKFromMat = processor.getNumericArray("Sigma");
		    double[][] sigmaK=sigmaKFromMat.getRealArray2D();
		    sigma.set(i, sigmaK);
		}
		
	}
	private static void reCalculateMu() {
		// TODO Auto-generated method stub
		for(int kIndex=0;kIndex<clusterNum;kIndex++){
			double[] muK=new double[dimension];
			for(int i=0;i<n;i++){
				for(int j=0;j<dimension;j++){
					muK[j]+=data[j][i]*gama[i][kIndex]/(w[kIndex]*n);
				}
			}
			mu.set(kIndex,muK);
		}
	}
	private static void reCalculateWeight() {
		for (int col = 0; col < clusterNum; col++) {
			double sum = 0;
			for (int i = 0; i < n; i++) {
				sum += gama[i][col];
			}
			w[col] = sum / n;
		}
		updateNJByW();
	}
	private static void updateNJByW(){
		double[] updatedNJ=new double[w.length];
		for(int i=0;i<updatedNJ.length;i++){
			updatedNJ[i]=w[i]*n;
		}
		nj=updatedNJ;
	}
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException, IOException
	{
	    
		tol=Double.parseDouble(args[0]);
		clusterNum=Integer.parseInt(args[1]);
		sigma.add(new double[][]{{0.4,0},{0,2}});
		sigma.add(new double[][]{{0.5,0.3},{0.3,0.7}});
		sigma.add(new double[][]{{1.5,0.5},{0.5,1.6}});
		mu.add(new double[]{1,2});
		mu.add(new double[]{5,2});
		mu.add(new double[]{8,9});
		
		
		init();
		boolean converge=false;
		double tolerance=0.0001;
		int count=0;
		while(!converge){
			double estepLog=estep();
			System.out.println(estepLog);
			double mstepLog=mstep();
			System.out.println(mstepLog);
			count++;
			if(Math.abs((mstepLog-estepLog))<tolerance){
				System.out.println("count:"+count);
				printArrayMu(mu);
				printArraySigma(sigma);
				break;
			}
		}
	    close();
	    
	}
	
    private  static void printArrayMu(List<double[]> mu){
    	System.out.println("MU\n");
    	for(double[] muk:mu){
    		for(double d:muk){
    			System.out.print(d +"\t");
    		}
    		System.out.println("\n");
    	}
    }
    private static void printArraySigma(List<double[][]> sigma){
    	System.out.println("Sigma\n");
    	for(double[][] matirx:sigma){
    		for(int i=0;i<matirx.length;i++){
    			for(int j=0;j<matirx[0].length;j++){
    				System.out.print("\tmatrix("+i+","+j+"):"+matirx[i][j]);
    			}
    		}
    	    System.out.println("\n");	
    	}
    }
}
