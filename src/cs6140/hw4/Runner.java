package cs6140.hw4;

import java.util.ArrayList;
import java.util.Vector;

import cs6140.hw4.Email;

public class Runner {

	public static void main(String[] args) {
		//partition entire dataset into 10 folds
		KCrossValidation k = new KCrossValidation(10);
		
		//set test dataset index
		k.extractTestingSetByIndex(0);
		//
		ArrayList<Email> trainingSet = k.getTrainingData();
		ArrayList<Email> testingSet = k.getTestingData();
		
		//
		normalize(trainingSet);
		normalize(testingSet);
		//normalize testing set
		Vector<Solution> params=new Vector<Solution>();

		DecisionStumps ds=new DecisionStumps(trainingSet);
		ds.initSortedFeatureArray();
		ds.backToOriginalTrainingSet();
		ds.generateEntireDictionary();

		for(int t=0;t<300;t++){
			Solution globalOptimum = ds.getGlobalOptimalSolution();
		   	System.out.println("Round"+t+":"+globalOptimum.toString());
		   	params.add(globalOptimum);
		   	Vector<Double> newD = AdaBoosting.updateD(globalOptimum, ds.getD(), trainingSet);
		   	ds.setD(newD);
		   	
		   	double errorNum=0;
		   	for(Email e:testingSet){
		   		if(!isPredictRight(predictAEmail(e, params), e.get(MyConstant.INDEX_EMAIL_SPAM_LABEL))){
		   			errorNum++;
		   		}
		   	}
		   	System.out.println("testErrorRate:"+errorNum/testingSet.size());
		}
	}
	
	
	public static boolean isPredictRight(double predictValue, double real){
		return predictValue*real>0;
	}
	public static double predictAEmail(Email email, Vector<Solution> params){
		double sum=0;
		for(Solution s:params){
			sum+=s.output(email);
		}
		return sum;
	}
    /**
     * add label Seq Id to each data point
     * add transform spam label 0 to -1
     * 
     * @param trainingSet
     */
	public static void normalize(ArrayList<Email> trainingSet) {
		for (int i = 0; i < trainingSet.size(); i++) {
			if (trainingSet.get(i).get(MyConstant.INDEX_EMAIL_SPAM_LABEL) == 0) {
				trainingSet.get(i).set(MyConstant.INDEX_EMAIL_SPAM_LABEL, -1.0);
			}
			trainingSet.get(i).add(new Double(i));
		}
	}
}
