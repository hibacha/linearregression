package cs6140.hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cs6140.hw3.Email;

public class DecisionStumps {
	public ArrayList<Email> trainingSet;
	public int optimumFeature;
	
	//distribution
	public Vector<Double> d= new Vector<Double>();
	
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
	
	public void calculateNewErrorRate(){
		
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
	
	
	public void buildErrorMatrixForOneFeature(int featureIndex, Vector<Double> threshold, Vector<Integer> sortedDataPoint){
		backToOriginalTrainingSet();
		Iterator<Double> it = threshold.iterator();
	   	List<Double> container = new ArrayList<Double>();
	   	while(it.hasNext()){
	   		Double athreshold = it.next();
	   		
	   		container.add(calculateErrorRateByWeight(featureIndex,
					athreshold, sortedDataPoint));		
	   		
	   	}
	   	System.out.println(Collections.min(container));
	}
	
	private double calculateErrorRateByWeight(int featureIndex, double threshold, Vector<Integer> sortedDataPoint){
	    Iterator<Integer> it = sortedDataPoint.iterator();
	   	double errorRateWeight=0;
	   	while(it.hasNext()){
	   		Integer dataSeqId = it.next();
	   		if(trainingSet.get(dataSeqId).get(featureIndex)<threshold){
	   			errorRateWeight+=checkError(dataSeqId,-1.0);
	   		} else{
	   			errorRateWeight+=checkError(dataSeqId, 1.0);
	   		}
	   	}
	   	return errorRateWeight;
	}
	
	private double checkError(Integer dataSeqId, double predict) {
		if(trainingSet.get(dataSeqId).get(MyConstant.INDEX_EMAIL_SPAM_LABEL)!=predict){
			return d.get(dataSeqId);
		}else{
			return 0;
		}
		
	}

	public void backToOriginalTrainingSet(){
		sortByFeature(MyConstant.INDEX_FOR_DATA_ID);
	}
	
	private  void sortByFeature(int fIndex){
		
	    Collections.sort(trainingSet,new AdaComparator(fIndex));
	    
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
