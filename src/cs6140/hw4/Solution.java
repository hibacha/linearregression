package cs6140.hw4;

import cs6140.hw3.Email;

public class Solution {
	private int featureIndex;
	private double errorRateWeighted;
	private double threshold;
	private double errorRateOriginal;
	
	public double getErrorRateOriginal() {
		return errorRateOriginal;
	}
	public void setErrorRateOriginal(double errorRateOriginal) {
		this.errorRateOriginal = errorRateOriginal;
	}
	public Solution(int featureIndex, double errorRateWeighted, double threshold, double errorRateOriginal) {
		this.featureIndex=featureIndex;
		this.errorRateWeighted= errorRateWeighted;
		this.threshold = threshold;
		this.errorRateOriginal= errorRateOriginal;
	}
	

	@Override
	public String toString() {
		return "Solution [featureIndex=" + featureIndex
				+ ", errorRateWeighted=" + errorRateWeighted + ", threshold="
				+ threshold + ", errorRateOriginal=" + errorRateOriginal + "]";
	}
	
	public double output(Email email){
		double output = alphaT();
		if(email.get(featureIndex)<threshold){
			return output*-1;
		}else{
			return output*1;
		}
	}
	
	public double alphaT(){
		return 0.5*Math.log((1-errorRateWeighted)/errorRateWeighted);
	}
	
	
	public int getFeatureIndex() {
		return featureIndex;
	}
	public void setFeatureIndex(int featureIndex) {
		this.featureIndex = featureIndex;
	}
	public double getErrorRateWeighted() {
		return errorRateWeighted;
	}
	public void setErrorRateWeighted(double errorRateWeighted) {
		this.errorRateWeighted = errorRateWeighted;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
}
