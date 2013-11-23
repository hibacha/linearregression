package cs6140.hw5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cs6140.hw5.AUC;
import cs6140.hw5.ROC;

public class MixGaussian {

	/**
	 * @param args
	 */
	public double pSpam=(double)1631/4140;
	public double pNonSpam=(double)2509/4140;
	
	public GaussianModel[][] spamGaussian=new GaussianModel[57][9];
	public GaussianModel[][] nonSpamGaussian=new GaussianModel[57][9];
	public boolean predict(Email email){
		double logSum4Spam=0;
		double logSum4NonSpam=0;
		for(int featureIndex=0;featureIndex<57;featureIndex++){
			GaussianModel[] mixedForOneFeatureSpam=spamGaussian[featureIndex];
			GaussianModel[] mixedForOneFeatureNonSpam=nonSpamGaussian[featureIndex];
			double sum4Spam=0;
			double sum4NonSpam=0;
			for(int i=0;i<mixedForOneFeatureSpam.length;i++){
				sum4Spam+=mixedForOneFeatureSpam[i].weightPdf(email.get(featureIndex));
				sum4NonSpam+=mixedForOneFeatureNonSpam[i].weightPdf(email.get(featureIndex));
			}
			logSum4Spam+=Math.log(sum4Spam);
			logSum4NonSpam+=Math.log(sum4NonSpam);
		}
		logSum4Spam+=Math.log(pSpam);
		logSum4NonSpam+=Math.log(pNonSpam);
		double tau=logSum4Spam-logSum4NonSpam;
		email.add(58, tau);
		return tau>0;
	}
	
	public void readFile(String path, boolean isSpam) throws IOException {
		File file = new File(path);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String str = "";
		int count = 0;
		while ((str = br.readLine()) != null) {
			str.trim();
			String[] mu = str.split("\\s+");
			str = br.readLine();
			String[] weight = str.split("\\s+");
			str = br.readLine();
			String[] sigma = str.split("\\s+");

			for (int g = 0; g < 9; g++) {
				GaussianModel gauModel=null;
                try{
				     gauModel = new GaussianModel(
						Double.parseDouble(mu[g]),
						Double.parseDouble(sigma[g]),
						Double.parseDouble(weight[g]));
				
                }catch(Exception e){
                	System.out.println(e.toString());
                	System.out.println("check feature "+(count+1));
                	gauModel=new GaussianModel(0.0,0.0,0.0);
                }
                
                if (isSpam) {
					spamGaussian[count][g] = gauModel;
				} else {
					nonSpamGaussian[count][g] = gauModel;
				}
			}

			// read empty or end mark
			str = br.readLine();
			if (str.equals("END")) {
				break;
			}
			count++;
		}
	}
	
	public static ArrayList<double[]> featureContainer=new ArrayList<double[]>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		KCrossValidation k = new KCrossValidation(10);
		// set test dataset index
		k.extractTestingSetByIndex(0);
		//
		ArrayList<Email> trainingSet = k.getTrainingData();
		ArrayList<Email> testingSet = k.getTestingData();
		
		
		MixGaussian mix=new MixGaussian();
		mix.readFile("/Users/zhouyf/Dropbox/machine HW/hw5/spamTeta.txt", true);
		mix.readFile("/Users/zhouyf/Dropbox/machine HW/hw5/nonSpamTeta.txt", false);
		mix.predictAll(testingSet);
		//extractSeparateFeatureForAll(trainingSet);
		//generateFileForFeature(trainingSet,56);
	}
	
	
	public void predictAll(ArrayList<Email> testingSet){
		int right=0;
		for(Email email: testingSet){
			if(predict(email)&&email.get(MyConstant.INDEX_EMAIL_SPAM_LABEL)==1){
				right++;
			}else if(predict(email)==false&&email.get(MyConstant.INDEX_EMAIL_SPAM_LABEL)==0){
				right++;
			}
		}
		System.out.println(right);
		ArrayList<Point> plotPoints = ROC.plotROC(testingSet);
		for(Point p:plotPoints){
			System.out.println(p);
		}
		double currentAuc = AUC.calAUC(plotPoints);
		System.out.print(currentAuc);
		
	}
	
	private static void generateFileForFeature(ArrayList<Email> trainingSet,int featureIndex)
			throws IOException {
		File fileSpam= new File("/Users/zhouyf/Dropbox/machine HW/hw5/"+featureIndex+"feaSpam.data");
		File fileNonSpam= new File("/Users/zhouyf/Dropbox/machine HW/hw5/"+featureIndex+"feaNonSpam.data");
		FileWriter fwSpam=new  FileWriter(fileSpam);
		FileWriter fwNonSpam=new  FileWriter(fileNonSpam);
		BufferedWriter bwSpam=new BufferedWriter(fwSpam);
		BufferedWriter bwNonSpam=new BufferedWriter(fwNonSpam);
		
		for(Email email:trainingSet){
			double label = email.get(57);
			//spam
			if(label==1){
				bwSpam.write(String.valueOf(email.get(featureIndex))+"\n");
			}else{
				bwNonSpam.write(String.valueOf(email.get(featureIndex))+"\n");

			}
		}
		bwSpam.flush();
		bwSpam.close();
		bwSpam.close();
		
		bwNonSpam.flush();
		bwNonSpam.close();
		bwNonSpam.close();
	}
//	private static void extractSeparateFeatureForAll(
//			ArrayList<Email> trainingSet) {
//		int dataIndex=0;
//		for(Email email:trainingSet){
//			for(int featureIndex=0;featureIndex<57;featureIndex++){
//				if(featureContainer.size()<=featureIndex){
//					double[] data= new double[trainingSet.size()];
//					featureContainer.add(data);
//					data[dataIndex]=email.get(featureIndex);
//				}else{
//					double[] data=featureContainer.get(featureIndex);
//				   data[dataIndex]=email.get(featureIndex);
//				}
//			}
//			dataIndex++;
//		}
//	}

}
