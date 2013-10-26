package cs6140.hw4;

import java.util.ArrayList;
import java.util.Vector;

public class BaseRunner {
    public static final int  ITERATION_ROUND_MAX = 50000;
	public static void run(boolean isOptimal) {
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
		ArrayList<Point> plotPoints=null;
		double previousAuc=0;
		ConvergenceJudge c=new ConvergenceJudge(30, 0.0001);
		for(int t=0;t<ITERATION_ROUND_MAX;t++){
			Solution globalOptimum;

			if (isOptimal) {
				globalOptimum = ds.getGlobalOptimalSolution();
			} else {
				// using randomly select stump decison
				globalOptimum = ds.getRandomOptimalSolution();
			}
		   	//System.out.print("Round"+t+":"+globalOptimum.toString());
		   	System.out.print(t+","+globalOptimum.getErrorRateWeighted()+"\t");
		   	params.add(globalOptimum);
		   	Vector<Double> newD = AdaBoosting.updateD(globalOptimum, ds.getD(), trainingSet);
		   	ds.setD(newD);
		   	
		   	double tesingErrorNum = AdaBoosting.testDataSet(testingSet, params);
		   	double trainingErrorNum = AdaBoosting.testDataSet(trainingSet, params);
		   	System.out.print("\ttestErrorRate:"+tesingErrorNum/testingSet.size());
		  	System.out.print("\ttrainingErrorRate:"+trainingErrorNum/trainingSet.size());
		   	
		   	plotPoints = ROC.plotROC(testingSet);
		   	double currentAuc = AUC.calAUC(plotPoints);
		   	if(c.isConvergeAfterAddNewDiff(Math.abs(previousAuc-currentAuc))){
		   		break;
		   	}
		   	previousAuc=currentAuc;
		   	
		}
		for(Point p:plotPoints){
		  System.out.println(p.toString());
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
			//set placeholder for tau
			trainingSet.get(i).add(new Double(0));
		}
	}
}
