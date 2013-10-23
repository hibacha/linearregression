package cs6140.hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cs6140.hw3.Email;

public class DecisionStumps {
	
	//store training dataset 
	public ArrayList<Email> trainingSet;
	
	//distribution for weighted error rate
	private Vector<Double> distribution= new Vector<Double>();
    
	//a list indexed feature containing a list of ThresholdToErrorItem Object
	private ArrayList<Vector<ThresholdToErrorItem>> allFeatureToThresholdsDic = new ArrayList<Vector<ThresholdToErrorItem>>();
	
	//initialize as uniform distribution
	public void initDistribution(){
		int m=trainingSet.size();
		for (int i = 0; i < m; i++) {
			distribution.add(1.0/m);
		}
	}
	
	//sort training set using data Id to make it consistent with distribution
	public void backToOriginalTrainingSet(){
		Collections.sort(trainingSet,new EmailFeatureComparator(MyConstant.INDEX_FOR_DATA_ID));
	}
	
	//get global optimal solution among all feature's thresholds
	public Solution getGlobalOptimalSolution(){
        ArrayList<Solution> container = new ArrayList<Solution>();
		for(int i=0;i<57;i++){
			container.add(getOptimalSolutionOfOneFeature(i));
		}
		return extractOptimalSolution(container);
	}
	
	//get optimal solution by given feature
	public Solution getOptimalSolutionOfOneFeature(int featureIndex){
		Vector<ThresholdToErrorItem> thresholds = allFeatureToThresholdsDic.get(featureIndex);
        ArrayList<Solution> container = new ArrayList<Solution>();
		for(ThresholdToErrorItem item: thresholds){
			double errorRateWeighted=item.errorRate(distribution);
			double threshold=item.getThreshold();
			Solution so=new Solution(featureIndex, errorRateWeighted, threshold, (double)item.getErrorIds().size()/trainingSet.size());
			container.add(so);
		}
		return extractOptimalSolution(container);
	}
	
	//extract optimal from either max weighted error rate or min weighted error rate
	private Solution extractOptimalSolution(List<Solution> container) {
		Solution min= Collections.min(container, new SolutionComparator());
	   	Solution max= Collections.max(container, new SolutionComparator());
	   	return Math.abs(0.5-min.getErrorRateWeighted())>Math.abs(0.5- max.getErrorRateWeighted())? min:max;
	}
	
	//generate entire feature -> threshold dictionary
	public void generateEntireDictionary(){
		backToOriginalTrainingSet();
		for (int i = 0; i < 57; i++) {
			Vector<ThresholdToErrorItem> allThresholds  =this.allFeatureToThresholdsDic.get(i);
			for (int j = 0; j < allThresholds.size(); j++) {
				ThresholdToErrorItem thresholdItem = allThresholds.get(j);
				setWrongList(thresholdItem);
			}
		}
	}
	
	//set wrong predicted data Id into ThresholdToErrorItem
	private void setWrongList(ThresholdToErrorItem thresholdObj){
		Iterator<Email> it=trainingSet.iterator();
		int featureIndex = thresholdObj.getFeatureIndex();
		double threshold = thresholdObj.getThreshold();
		while(it.hasNext()){
			Email email = it.next();
			double predict=0;
			predict= email.get(featureIndex)< threshold?-1:1;
			if(email.get(MyConstant.INDEX_EMAIL_SPAM_LABEL)!=predict){
				thresholdObj.addWrongPredictDataPoint((int)email.get(MyConstant.INDEX_FOR_DATA_ID).doubleValue());
			}
		}
	}
	
	//calculate all thresholds
	public void initSortedFeatureArray(){
		for (int i = 0; i < 57; i++) {
			sortByFeature(i);
		}
	}
	
	//set mid point as threshold for given feature
	private void sortByFeature(int fIndex){
	    Collections.sort(trainingSet,new EmailFeatureComparator(fIndex));
	    Vector<ThresholdToErrorItem> oneFeatureThresholdVec = new Vector<ThresholdToErrorItem>();
	    double previousFeatureValue=trainingSet.get(0).get(fIndex);
	    oneFeatureThresholdVec.add(new ThresholdToErrorItem(Double.NEGATIVE_INFINITY, fIndex));
	    for(Email e:trainingSet){
	    	double currentFeatureValue=e.get(fIndex);
	    	
	    	if(currentFeatureValue>previousFeatureValue){
	    		double midPoint =(previousFeatureValue+currentFeatureValue)/2;
	    		oneFeatureThresholdVec.add(new ThresholdToErrorItem(midPoint, fIndex));
	    		previousFeatureValue=currentFeatureValue;
	    	}
	    }
	    oneFeatureThresholdVec.add(new ThresholdToErrorItem(Double.POSITIVE_INFINITY, fIndex));
	    allFeatureToThresholdsDic.add(oneFeatureThresholdVec);
	}
	
	public Vector<Double> getD() {
		return distribution;
	}

	public void setD(Vector<Double> d) {
		this.distribution = d;
	}

	
	public ArrayList<Email> getTrainingSet() {
		return trainingSet;
	}

	public DecisionStumps(ArrayList<Email> trainingSet) {
		this.trainingSet= trainingSet;
		initDistribution();
	}
	
}
