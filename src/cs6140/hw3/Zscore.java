package cs6140.hw3;

import java.util.ArrayList;
import java.util.Vector;

public class Zscore {

	/**
	 * @param args
	 */
	private double[] means = new double[MyConstant.SIZE_OF_NORMALIZED_FEATURE];
	private double[] sd = new double[MyConstant.SIZE_OF_NORMALIZED_FEATURE];
	public Zscore() {
		init();
	}
	private void init(){
	}
	
	public double[] getMeans() {
		return means;
	}
	public void setMeans(double[] means) {
		this.means = means;
	}
	
	
	public double[] getSd() {
		return sd;
	}
	public void setSd(double[] sd) {
		this.sd = sd;
	}
	public void calculateMean(ArrayList<Email> trainingSet){
		//reset means
		means=new double[MyConstant.SIZE_OF_NORMALIZED_FEATURE];
		double totalNum= trainingSet.size();
		
		for(Vector<Double> mail:trainingSet){
			for(int fIndex=0;fIndex<MyConstant.SIZE_OF_NORMALIZED_FEATURE;fIndex++){
				means[fIndex]+=mail.get(fIndex)/totalNum;
			}
		}
		
	}
	
	public void calculateSD(ArrayList<Email> trainingSet){
		sd = new double[MyConstant.SIZE_OF_NORMALIZED_FEATURE];
		calculateMean(trainingSet);
		double[] sum=new double[MyConstant.SIZE_OF_NORMALIZED_FEATURE];
		double totalNum= trainingSet.size();
		
		for(Email mail:trainingSet){
			for(int fIndex=0;fIndex<MyConstant.SIZE_OF_NORMALIZED_FEATURE;fIndex++){
				double mui=means[fIndex];
				double x=mail.get(fIndex);
				sum[fIndex]+=Math.pow(x-mui, 2);
			}
		}
		
		for(int fIndex=0;fIndex<MyConstant.SIZE_OF_NORMALIZED_FEATURE;fIndex++){
			sd[fIndex]= Math.sqrt(sum[fIndex]/(totalNum));
		}
		
	}
	public ArrayList<Email> getNormalizedData(ArrayList<Email> unnormalized){
		ArrayList<Email> normalizedSet= new ArrayList<Email>();
		for(Email mail:unnormalized){
			Email normalizedMail= new Email();
			for(int i=0;i<MyConstant.SIZE_OF_NORMALIZED_FEATURE;i++){
		         normalizedMail.add(getZscoreForX(mail.get(i), means[i], sd[i]));
		    }
			//add spam label
			normalizedMail.add(mail.get(57));
			normalizedSet.add(normalizedMail);
		}
		return normalizedSet;
	}
	public double getZscoreForX(double x, double mui, double fai){
		return (x-mui)/fai;
		
	}

}
