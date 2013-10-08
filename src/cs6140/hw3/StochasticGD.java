package cs6140.hw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class StochasticGD {

	/**
	 * @param args
	 */
	private double[] weight = new double[57];
	private double[] newWeight = new double[57];
	private ArrayList<Vector<Double>> normalizedSet;
	private ArrayList<Vector<Double>> testDataSet;
	private Zscore zscore;
	public double[] getNewWeight() {
		return newWeight;
	}

	public void setNewWeight(double[] newWeight) {
		this.newWeight = newWeight;
	}

	public double[] getWeight() {
		return weight;
	}

	public void setWeight(double[] weight) {
		this.weight = weight;
	}

	private double h(double[] weight, Vector<Double> x) {
		double sum = 0;
		for (int i = 0; i < weight.length; i++) {
			sum += weight[i] * x.get(i);
		}
		return sum;
	}

	private boolean isConverge(double theta1, double theta2) {
		System.out.println(Math.abs(theta1 - theta2));
		return Math.abs(theta1 - theta2) < 0.002;
	}
	private boolean isConvergeForAll(){
		boolean result=true;
		for(int i=0;i<57;i++){
			if(!isConverge(weight[i],newWeight[i])){
				result=false;
				break;
			}
		}
		return result;
	}
	private void initWeight(){
		weight = new double[57];
		newWeight = new double[57];
	}
	public void trainData(double alpha) {
		initWeight();
		boolean isConverge = false;
		int k=500;
		while (k-->0) {
			for (Vector<Double> mail : normalizedSet) {
				weight = Arrays.copyOf(newWeight, 57);
				for (int j = 0; j < 57; j++) {
					double diff= mail.get(57) - h(weight, mail);
					newWeight[j] = weight[j] + alpha
							* diff * mail.get(j);
				}
				
			}
			
//			isConverge= isConvergeForAll();
		}
		double rmse=jw();
		System.out.println(rmse);
	}

	public void prepareTrainingDataSet(){
		KCrossValidation kcross=new KCrossValidation(1);
		kcross.extractTestingSetByIndex(-1);
		ArrayList<Vector<Double>> overallDataSet = kcross.getTrainingData();
		
	    zscore = new Zscore();
		zscore.calculateSD(overallDataSet);
		
		KCrossValidation kcross2=new KCrossValidation(10);
		kcross2.extractTestingSetByIndex(0);
		ArrayList<Vector<Double>> partionedSet = kcross2.getRandomTrainingData();
		normalizedSet = zscore.getNormalizedData(partionedSet);
		testDataSet =  kcross2.getTestingData();
	}
	
	public double jw(){
		double sum=0;
		for(Vector<Double> mail: normalizedSet){
		   sum+=Math.pow(h(newWeight, mail)-mail.get(57),2);
		}
		return Math.sqrt(sum/normalizedSet.size());
	}
	
	public void predict(){
		
	   ArrayList<Vector<Double>> normalizedTestData = zscore.getNormalizedData(testDataSet);
	   List<Double> taus = new ArrayList<Double>();
	   
	   for(int i=0;i<normalizedTestData.size();i++){
		   Vector<Double> oneMail = normalizedTestData.get(i);
		   double tau = h(newWeight, oneMail);
		   oneMail.add(tau);
		   oneMail.add(testDataSet.get(i).get(57));
	   }
	   
	   for(Vector<Double> mail:normalizedTestData){
		  System.out.println( mail.get(58)+"->"+mail.get(59));
	   }
	   
	}
	//TODO errorRate
	public void errorRate(){
		
	}
	//TODO plotROC
	public void plotROC(){
		
	}
	public static void main(String[] args) {
		
		StochasticGD sgd=new StochasticGD();
		sgd.prepareTrainingDataSet();
		sgd.trainData(0.001);
		
		System.out.print("@@@@@@@@@");
		for(double a:sgd.getNewWeight()){
			System.out.println("weight:"+a);
		}
	    System.out.println(sgd.jw());	
		sgd.predict();
	}

}
