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
import static cs6140.hw5.MatrixMatBuilder.ArrayScanOp;
import static cs6140.hw5.MatrixMatBuilder.getCol;
import static cs6140.hw5.MatrixMatBuilder.tranpose;
public class Em {

	/**
	 * @param args
	 */
	public  double tol=0;
	public  String path= "";
	public  int n=0;
	public  int dimension=0;
	public  double[][] data=null;
	public  int clusterNum=0;
	public  List<double[]> mu= new ArrayList<double[]>(); 
	public  List<double[][]> sigma=new ArrayList<double[][]>();
	public  double[][] logGama=null;
	public  double[][] gama=null;
	public  double[] w=null;
	public  double[] nj=null;
	public  MatlabProxy proxy=null;
	public  MatlabTypeConverter processor=null;
	public  double loglikelyhood=0;
	
	public Em(double tol,int n, int dimension, List<double[]>mu, List<double[][]> sigma,double[] w,int clusterNum,String path) throws IOException, MatlabConnectionException{
		this.tol=tol;
		this.n=n;
		this.dimension=dimension;
		this.mu=mu;
		this.sigma=sigma;
		this.w=w;
		this.clusterNum=clusterNum;
		this.path=path;
		this.init();
	}
	private  void init() throws IOException, MatlabConnectionException {
		data = new double[dimension][n];
		readData(path);
		setUpMatlabProvider();
		updateNJByW();
	}
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException, IOException
	{
		
		List<double[][]> sigma = new ArrayList<double[][]>();
		sigma.add(new double[][] { { 0.4, 0 }, { 0, 2 } });
		sigma.add(new double[][] { { 0.5, 0.3 }, { 0.3, 0.7 } });
		sigma.add(new double[][] { { 1.5, 0.5 }, { 0.5, 1.6 } });
		List<double[]> mu = new ArrayList<double[]>();
		mu.add(new double[] { 1, 2 });
		mu.add(new double[] { 5, 2 });
		mu.add(new double[] { 8, 9 });
		
		double[] w={0.3,0.4,0.3};
		double tol=0.0001;
		String path= "/Users/zhouyf/Dropbox/machine HW/hw5/3gaussian.txt";
		int n=10000;
		int dimension=2;
		int clusterNum=3;
		Em em=new Em(tol, n, dimension, mu, sigma, w, clusterNum, path);
		em.run();
		
		List<double[][]> sigma2 = new ArrayList<double[][]>();
		sigma2.add(new double[][] { { 0.4, 0.1 }, { 0.1, 2 } });
		sigma2.add(new double[][] { { 0.5, 0.3 }, { 0.3, 0.7 } });
		List<double[]> mu2 = new ArrayList<double[]>();
		mu2.add(new double[] { 1, 1 });
		mu2.add(new double[] { 5, 2 });
		double[] w2={0.5,0.5};
		double tol2=0.0001;
		String path2= "/Users/zhouyf/Dropbox/machine HW/hw5/2gaussian.txt";
		int n2=6000;
		int dimension2=2;
		int clusterNum2=2;
		Em em2=new Em(tol2, n2, dimension2, mu2, sigma2, w2, clusterNum2, path2);
		em2.run();
	    
	}
	public  void run() throws MatlabConnectionException, MatlabInvocationException{
		boolean converge=false;
		int count=0;
		while(!converge){
			System.out.println("iteration:"+(count+1));
			double estepLog=estep();
			System.out.println(estepLog);
			double mstepLog=mstep();
			System.out.println(mstepLog);
			count++;
			if(Math.abs((mstepLog-estepLog))<tol){
				System.out.println("count:"+count);
				printArrayMu(mu);
				printArraySigma(sigma);
				printW(w);
				break;
			}
		}
	    close();
	}
	private  void setUpMatlabProvider() throws MatlabConnectionException {
		MatlabProxyFactory factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
		processor = new MatlabTypeConverter(proxy);
	}
	private  void close(){
		proxy.disconnect();
	}
	public  double[] logpdf(double[][] data, double[] mu, double[][] sigma,int kIndex) throws MatlabConnectionException, MatlabInvocationException{
    
		  processor.setNumericArray("mydata", new MatlabNumericArray(data,null));
		  proxy.setVariable("mu", mu);
		  processor.setNumericArray("Sigma", new MatlabNumericArray(sigma,null));
		  
		  proxy.eval("d=size(mydata,1);");
		  proxy.eval("mu="+tranpose("mu")+";");
		  proxy.eval("mydata="+ArrayScanOp(MatrixMatBuilder.SUB, "mydata", "mu")+";");
		  proxy.eval("n = size(mydata,2);");
		  proxy.eval("result=zeros(1,n);");
		  //calculate log likelihood, we don't calculate inverse matrix for accuracy
		  proxy.eval("for i =1:n tmp=Sigma\\mydata(:,i);fai=mydata(:,i)'*tmp; result(i)=-0.5*(d*log(2*pi)+log(det(Sigma)))+(-0.5*fai);end");
		  proxy.eval("logGama(:,"+kIndex+")=result");
		  double[] logpdf=((double[]) proxy.getVariable("result"));
		  return logpdf;
	}
	public  void readData(String path) throws IOException{
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

	public  double estep() throws MatlabConnectionException, MatlabInvocationException{
		proxy.eval("logGama=zeros("+n+","+clusterNum+")");
		for(int i=0;i<clusterNum;i++){
			logpdf(data, mu.get(i), sigma.get(i),(i+1));
		}
		double[] sum4e = calculateGama();
		return getLoglikelihood(sum4e);
	}
	private  double[] calculateGama() throws MatlabInvocationException {
		proxy.setVariable("w", w);
		proxy.eval("logGama="+ArrayScanOp(MatrixMatBuilder.ADD, "logGama", "log(w)")+";");
		proxy.eval("maxele=max(logGama,[],2)");
		proxy.eval("normalLogGama="+ArrayScanOp(MatrixMatBuilder.SUB, "logGama", "maxele")+";");
		proxy.eval("s4e=log(sum(exp(normalLogGama),2))+maxele");
		double[] sum4e = (double[])proxy.getVariable("s4e");
		proxy.eval("logGama="+ArrayScanOp(MatrixMatBuilder.SUB, "logGama", "s4e")+";");
		proxy.eval("gama=exp(logGama)");
		logGama = processor.getNumericArray("logGama").getRealArray2D();
	    gama = processor.getNumericArray("gama").getRealArray2D();
		return sum4e;
	}
	public  double getLoglikelihood(double[] dataLikelihood){
		 double sum = 0;
		 for(double l:dataLikelihood){
			 sum+=l;
		 }
		 return sum/n;
	}
	public  double mstep() throws MatlabInvocationException, MatlabConnectionException{
		reCalculateWeight();
		reCalculateMu();
		reCalculateSigma();
		double loglikehoodObj=calObjLogLikelyhood();
		printArrayMu(mu);
		printArraySigma(sigma);
		printW(w);
		return loglikehoodObj;
	}

	private  double calObjLogLikelyhood() throws MatlabConnectionException, MatlabInvocationException {
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
	private  void reCalculateSigma() throws MatlabInvocationException {
		processor.setNumericArray("mydata", new MatlabNumericArray(data,null));
		proxy.eval("Sigma = zeros("+dimension+","+dimension+");");
		for(int i=0;i<clusterNum;i++){
			proxy.eval("Sigma = zeros("+dimension+","+dimension+");");
			proxy.setVariable("mu", mu.get(i));
			proxy.setVariable("nj", nj[i]);
			proxy.eval("mu="+tranpose("mu")+";");
			proxy.eval("Xfea="+ArrayScanOp(MatrixMatBuilder.SUB, "mydata", "mu")+";");
			proxy.eval("sr=sqrt(gama)");
			proxy.eval("Xfea="+ArrayScanOp(MatrixMatBuilder.MUL, "Xfea", tranpose(getCol("sr",i+1)))+";");
			proxy.eval("Sigma=Xfea*Xfea'/nj;");
			proxy.eval("Sigma=Sigma+eye(size(mydata,1))*(1e-5)");
			MatlabNumericArray sigmaKFromMat = processor.getNumericArray("Sigma");
		    double[][] sigmaK=sigmaKFromMat.getRealArray2D();
		    sigma.set(i, sigmaK);
		}
		
	}
	private  void reCalculateMu() {
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
	private  void reCalculateWeight() {
		for (int col = 0; col < clusterNum; col++) {
			double sum = 0;
			for (int i = 0; i < n; i++) {
				sum += gama[i][col];
			}
			w[col] = sum / n;
		}
		updateNJByW();
	}
	private  void updateNJByW(){
		double[] updatedNJ=new double[w.length];
		for(int i=0;i<updatedNJ.length;i++){
			updatedNJ[i]=w[i]*n;
		}
		nj=updatedNJ;
	}
	
	private void printW(double[] w){
		System.out.println("w\n");
		for(double i:w){
			System.out.print(String.format("%.3f",i)+" ");
		}
	}
    private   void printArrayMu(List<double[]> mu){
    	System.out.println("MU\n");
    	for(double[] muk:mu){
    		for(double d:muk){
    			System.out.print(String.format("%.3f",d) +" ");
    		}
    		System.out.println("\n");
    	}
    }
    private  void printArraySigma(List<double[][]> sigma){
    	System.out.println("Sigma\n");
    	for(double[][] matirx:sigma){
    		for(int i=0;i<matirx.length;i++){
    			for(int j=0;j<matirx[0].length;j++){
    				System.out.print(" "+String.format("%.3f",matirx[i][j]));
    			}
    		}
    	    System.out.println("\n");	
    	}
    }
}
