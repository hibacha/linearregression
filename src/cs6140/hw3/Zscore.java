package cs6140.hw3;

import java.util.ArrayList;
import java.util.Vector;

public class Zscore {

	/**
	 * @param args
	 */
	private double[] means = new double[57];
	private double[] sd = new double[57];
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KCrossValidation kcross= new KCrossValidation(1);
		kcross.extractTestingSetByIndex(-1);
		
		Zscore zscore = new Zscore();
		ArrayList<Vector<Double>> trainingSet = kcross.getRandomTrainingData();
		zscore.calculateMean(trainingSet);
		zscore.calculateSD(trainingSet);
		
		zscore.printArray(zscore.getMeans());
		zscore.printArray(zscore.getSd());
//		(0.5 - 0.10455)/0.30536 = 1.295
		System.out.println(zscore.getZscoreForX(0.5,0.10455,0.30536));
		
	}
	public void printArray(double[] t){
		int i=0;
		for(double a:t){
			 System.out.println(++i+":"+a);
		}
	}
	
	
	public double[] getSd() {
		return sd;
	}
	public void setSd(double[] sd) {
		this.sd = sd;
	}
	public void calculateMean(ArrayList<Vector<Double>> trainingSet){
		//reset means
		means=new double[57];
		double totalNum= trainingSet.size();
		
		for(Vector<Double> mail:trainingSet){
			for(int fIndex=0;fIndex<57;fIndex++){
				means[fIndex]+=mail.get(fIndex)/totalNum;
			}
		}
		
	}
	
	public void calculateSD(ArrayList<Vector<Double>> trainingSet){
		sd = new double[57];
		calculateMean(trainingSet);
		double[] sum=new double[57];
		double totalNum= trainingSet.size();
		
		for(Vector<Double> mail:trainingSet){
			for(int fIndex=0;fIndex<57;fIndex++){
				double mui=means[fIndex];
				double x=mail.get(fIndex);
				sum[fIndex]+=Math.pow(x-mui, 2);
			}
		}
		
		for(int fIndex=0;fIndex<57;fIndex++){
			sd[fIndex]= Math.sqrt(sum[fIndex]/(totalNum));
		}
		
	}
	public ArrayList<Vector<Double>> getNormalizedData(ArrayList<Vector<Double>> unnormalized){
		ArrayList<Vector<Double>> normalizedSet= new ArrayList<Vector<Double>>();
		for(Vector<Double> mail:unnormalized){
		    Vector<Double> normalizedMail= new Vector<Double>();
			for(int i=0;i<57;i++){
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
