package cs6140.hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cs6140.hw3.Email;

public class DecisionStumps {
	public ArrayList<Email> trainingSet;
	public int optimumFeature;
	
	//distribution
	private Vector<Double> d= new Vector<Double>();
	
	public Vector<Double> getD() {
		return d;
	}

	public void setD(Vector<Double> d) {
		this.d = d;
	}

	public ArrayList<Vector<ThresholdToErrorItems>> thresholdForFeature = new ArrayList<Vector<ThresholdToErrorItems>>();
	
	public ArrayList<Email> getTrainingSet() {
		return trainingSet;
	}

	public DecisionStumps(ArrayList<Email> trainingSet) {
		this.trainingSet= trainingSet;
		initD();
	}
	
	public int train(Vector<Double> distribute){
		return 0;
	}
	
	public void initD(){
		int m=trainingSet.size();
		for (int i = 0; i < m; i++) {
			d.add(1.0/m);
		}
	}
	
	public void backToOriginalTrainingSet(){
		Collections.sort(trainingSet,new EmailFeatureComparator(MyConstant.INDEX_FOR_DATA_ID));
	}
	
	public Solution getGlobalOptimalSolution(){
        ArrayList<Solution> container = new ArrayList<Solution>();
		for(int i=0;i<57;i++){
			container.add(getOptimalSolutionOfOneFeature(i));
		}
		return extractOptimalSolution(container);
	}
	
	public Solution getOptimalSolutionOfOneFeature(int featureIndex){
		Vector<ThresholdToErrorItems> thresholds = thresholdForFeature.get(featureIndex);
        ArrayList<Solution> container = new ArrayList<Solution>();
		for(ThresholdToErrorItems item: thresholds){
			double errorRateWeighted=item.errorRate(d);
			double threshold=item.getThreshold();
			Solution so=new Solution(featureIndex, errorRateWeighted, threshold, (double)item.getErrorIds().size()/trainingSet.size());
			container.add(so);
		}
		return extractOptimalSolution(container);
	}
	
	private Solution extractOptimalSolution(List<Solution> container) {
		Solution min= Collections.min(container, new SolutionComparator());
	   	Solution max= Collections.max(container, new SolutionComparator());
	   	if(Math.abs(0.5-min.getErrorRateWeighted())>Math.abs(0.5- max.getErrorRateWeighted()))
	   	  return  min;
	   	else
	   	  return max;
	}
	
	public  void generateEntireDictionary(){
		backToOriginalTrainingSet();
		for (int i = 0; i < 57; i++) {
			Vector<ThresholdToErrorItems> allThresholds  =this.thresholdForFeature.get(i);
			for (int j = 0; j < allThresholds.size(); j++) {
				ThresholdToErrorItems thresholdItem = allThresholds.get(j);
				getWrongList(thresholdItem);
			}
		}
	}
	
	private void getWrongList(ThresholdToErrorItems thresholdObj){
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
	
	public void initSortedFeatureArray(){
		for (int i = 0; i < 57; i++) {
			sortByFeature(i);
		}
	}
	
	private  void sortByFeature(int fIndex){
	    Collections.sort(trainingSet,new EmailFeatureComparator(fIndex));
	    Vector<ThresholdToErrorItems> oneFeatureThresholdVec = new Vector<ThresholdToErrorItems>();
	    double previousFeatureValue=trainingSet.get(0).get(fIndex);
	    oneFeatureThresholdVec.add(new ThresholdToErrorItems(Double.NEGATIVE_INFINITY, fIndex));
	    for(Email e:trainingSet){
	    	double currentFeatureValue=e.get(fIndex);
	    	
	    	if(currentFeatureValue>previousFeatureValue){
	    		double midPoint =(previousFeatureValue+currentFeatureValue)/2;
	    		oneFeatureThresholdVec.add(new ThresholdToErrorItems(midPoint, fIndex));
	    		previousFeatureValue=currentFeatureValue;
	    	}
	    }
	    oneFeatureThresholdVec.add(new ThresholdToErrorItems(Double.POSITIVE_INFINITY, fIndex));
	    thresholdForFeature.add(oneFeatureThresholdVec);
	}
	
}
