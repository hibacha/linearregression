package cs6140.hw4;

import java.util.Vector;

public class ThresholdToErrorItem {
	
	private  int featureIndex;
	private double threshold;
	private Vector<Integer> errorIds=new Vector<Integer>();
	
	public int getFeatureIndex() {
		return featureIndex;
	}

	public void setFeatureIndex(int featureIndex) {
		this.featureIndex = featureIndex;
	}

	public ThresholdToErrorItem(double threshold, int featureIndex) {
		this.threshold=threshold;
		this.featureIndex = featureIndex;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public Vector<Integer> getErrorIds() {
		return errorIds;
	}
	public void addWrongPredictDataPoint(Integer dataId){
		errorIds.add(dataId);
	}
	public void setErrorIds(Vector<Integer> errorIds) {
		this.errorIds = errorIds;
	}
	
	public double errorRate(Vector<Double> distribution){
		double sum=0;
	    for (Integer id : errorIds) {
			sum+=distribution.get(id);
		}
	    return sum;
	}
}
