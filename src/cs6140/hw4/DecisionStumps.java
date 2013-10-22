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

	//this array store the sorted data by each feature value
	public ArrayList<Vector<Integer>> sortedByFeatureData = new ArrayList<Vector<Integer>>();
	
	//this array store the threshold value for each feature value
	public ArrayList<Vector<Double>> thresholdForFeature = new ArrayList<Vector<Double>>();
	
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
	
	public void initSortedFeatureArray(){
		for (int i = 0; i < 57; i++) {
			sortByFeature(i);
		}
	}
	
	public void initD(){
		int m=trainingSet.size();
		for (int i = 0; i < m; i++) {
			d.add(1.0/m);
		}
	}
	
	public Solution getWeightedOptimalSolution(){
		List<Solution> container = new ArrayList<Solution>();
		for(int i=0;i<57;i++){
			Solution os = buildErrorMatrixForOneFeature(i,thresholdForFeature.get(i),sortedByFeatureData.get(i));
		    container.add(os);
		}
	   	return extractOptimalSolution(container);
	}
	public Solution buildErrorMatrixForOneFeature(int featureIndex, Vector<Double> threshold, Vector<Integer> sortedDataPoint){
		//backToOriginalTrainingSet();
		Iterator<Double> it = threshold.iterator();
	   	List<Solution> container = new ArrayList<Solution>();
	   	while(it.hasNext()){
	   		Double athreshold = it.next();
	   		Solution os = calculateErrorRateByWeight(featureIndex,
					athreshold, sortedDataPoint);
	   		container.add(os);
	   		
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
	
	private Solution calculateErrorRateByWeight(int featureIndex, double threshold, Vector<Integer> sortedDataPoint){
	    Iterator<Integer> it = sortedDataPoint.iterator();
	   	double errorRateWeight=0;
	   	double errorNum=0;
	   	while(it.hasNext()){
	   		Integer dataSeqId = it.next();
	   		//TODO improvement
	   		if(trainingSet.get(dataSeqId).get(featureIndex)<threshold){
				if (checkError(dataSeqId, -1.0) > 0) {
					errorRateWeight += checkError(dataSeqId, -1.0);
					errorNum++;
				}
	   		} else{
				if (checkError(dataSeqId, 1.0) > 0) {
					errorRateWeight += checkError(dataSeqId, 1.0);
					errorNum++;
				}
	   		}
	   	}
	   	return new Solution(featureIndex,errorRateWeight,threshold,errorNum/sortedDataPoint.size());
	}
	
	private double checkError(Integer dataSeqId, double predict) {
		if(trainingSet.get(dataSeqId).get(MyConstant.INDEX_EMAIL_SPAM_LABEL)!=predict){
			return d.get(dataSeqId);
		}else{
			return 0;
		}
	}

	public void backToOriginalTrainingSet(){
		Collections.sort(trainingSet,new EmailFeatureComparator(MyConstant.INDEX_FOR_DATA_ID));
	}
	
	private  void sortByFeature(int fIndex){
		
	    Collections.sort(trainingSet,new EmailFeatureComparator(fIndex));
	    
	    Vector<Integer> sortedDataPointSeq = new Vector<Integer>();
	    Vector<Double> threshold = new Vector<Double>();
	    
	    double previousFeatureValue=trainingSet.get(0).get(fIndex);
	    threshold.add(Double.NEGATIVE_INFINITY);
	    for(Email e:trainingSet){
	    	sortedDataPointSeq.add((int)e.get(MyConstant.INDEX_FOR_DATA_ID).doubleValue());
	    	double currentFeatureValue=e.get(fIndex);
	    	
	    	if(currentFeatureValue>previousFeatureValue){
	    		double midPoint =(previousFeatureValue+currentFeatureValue)/2;
	    		threshold.add(midPoint);
	    		previousFeatureValue=currentFeatureValue;
	    		//System.out.println("midPoint"+ midPoint);
	    	}
	    	//System.out.println(e.get(MyConstant.INDEX_FOR_DATA_ID) + "->"+e.get(0));
	    }
	    threshold.add(Double.POSITIVE_INFINITY);
	    
	    sortedByFeatureData.add(sortedDataPointSeq);
	    thresholdForFeature.add(threshold);
	}
	
}
