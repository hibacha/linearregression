package cs6140.hw4;

import java.util.ArrayList;
import java.util.Vector;

public class Runner {
    public static final int  ITERATION_ROUND = 5000;
	public static void main(String[] args) {
		//partition entire dataset into 10 folds
		KCrossValidation k = new KCrossValidation(10);
		
		//set test dataset index
		k.extractTestingSetByIndex(0);
		//
		ArrayList<Email> trainingSet = k.getTrainingData();
		ArrayList<Email> testingSet = k.getTestingData();
		
		//	normalize testing set and training data set
		normalize(trainingSet);
		normalize(testingSet);
	
		//store optimal solution returned by each round
		Vector<Solution> params=new Vector<Solution>();

		DecisionStumps ds=new DecisionStumps(trainingSet); 
		ds.getRandomOptimalSolution();
		ArrayList<Point> plotPoints;
		double previousAuc=0;
		ConvergenceJudge c=new ConvergenceJudge(30, 0.0001);
		for(int t=0;t<ITERATION_ROUND;t++){
			Solution globalOptimum = ds.getGlobalOptimalSolution();
			
			// using randomly select stump decison
//			Solution globalOptimum = ds.getRandomOptimalSolution();
			
		   	System.out.println("Round"+t+":"+globalOptimum.toString());
		   	params.add(globalOptimum);
		   	Vector<Double> newD = AdaBoosting.updateD(globalOptimum, ds.getD(), trainingSet);
		   	ds.setD(newD);
		   	
		   	double errorNum = AdaBoosting.testDataSet(testingSet, params);
		   	System.out.println("testErrorRate:"+errorNum/testingSet.size());
		   	
		   	plotPoints = ROC.plotROC(testingSet);
		   	double currentAuc = AUC.calAUC(plotPoints);
		   	if(c.isConvergeAfterAddNewDiff(Math.abs(previousAuc-currentAuc))){
		   		break;
		   	}
		   	previousAuc=currentAuc;
		   	
		}
		
	}

    public static boolean isConverge(double previous, double current, double tolerance){
    	return (Math.abs(previous - current) < tolerance);
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
