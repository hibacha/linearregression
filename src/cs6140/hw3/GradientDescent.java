package cs6140.hw3;

import static cs6140.hw3.MyConstant.EMAIL_FEATURE_NUM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public abstract class GradientDescent {
	protected double[] weight = new double[EMAIL_FEATURE_NUM];
	protected double[] newWeight = new double[EMAIL_FEATURE_NUM];
	private ArrayList<Email> normalizedSet;
	private ArrayList<Email> testDataSet;
	private Zscore zscore;
	List<Point> plotPoints;
	public abstract void trainData(double alpha);
	
	private boolean isConverge(double oldRMSE, double newRMSE, double tolerance) {
		double absDiff = Math.abs(oldRMSE-newRMSE);
		return absDiff/oldRMSE<tolerance;
	}
	
	
	protected double rmse(){
		double sum = 0;
		for (Email mail : normalizedSet) {
			sum += Math.pow(h(newWeight, mail) - mail.get(57), 2);
		}
		return Math.sqrt(sum / normalizedSet.size());
	}
	
	public void prepareTrainingDataSet() {
		KCrossValidation kcross = new KCrossValidation(1);
		kcross.extractTestingSetByIndex(-1);
		ArrayList<Email> overallDataSet = kcross.getTrainingData();

		zscore = new Zscore();
		zscore.calculateSD(overallDataSet);

		KCrossValidation kcross2 = new KCrossValidation(10);
		kcross2.extractTestingSetByIndex(0);
		ArrayList<Email> partionedSet = kcross2
				.getRandomTrainingData();
		normalizedSet = zscore.getNormalizedData(partionedSet);
		testDataSet = kcross2.getTestingData();
	}
	
	private double h(double[] weight, Email x) {
		double sum = 0;
		for (int i = 0; i < weight.length; i++) {
			sum += weight[i] * x.get(i);
		}
		return sum;
	}
	
	public void predict() {

		ArrayList<Email> normalizedTestData = zscore
				.getNormalizedData(testDataSet);
		List<Double> taus = new ArrayList<Double>();

		for (int i = 0; i < normalizedTestData.size(); i++) {
			Email oneMail = normalizedTestData.get(i);
			double tau = h(newWeight, oneMail);
			oneMail.add(tau);
			oneMail.add(testDataSet.get(i).get(57));
		}

		Collections.sort(normalizedTestData, new Comparator<Vector<Double>>() {
			@Override
			public int compare(Vector<Double> o1, Vector<Double> o2) {
				if (o1.get(58) > o2.get(58))
					return -1;
				else if (o1.get(58) < o2.get(58))
					return 1;
				else
					return 0;
			}
		});
		
		for (Email mail : normalizedTestData) {
			System.out.println(mail.get(58) + "->" + mail.get(59));
		}
		plotPoints =new ArrayList<Point>();
		for(int i=-1;i<normalizedTestData.size();i++){
			
			int fnNum = 0;
			int fpNum = 0;
			int tnNum = 0;
			int tpNum = 0;
			for(int spamStart=0;spamStart<=i;spamStart++){
				//if spam
				if(normalizedTestData.get(spamStart).get(59)==1){
					tpNum++;
				}else{
					fpNum++;
				}
			}
			
			for(int nonspamStart=i+1;nonspamStart<normalizedTestData.size();nonspamStart++){
				if(normalizedTestData.get(nonspamStart).get(59)==1){
				   fnNum++;
				}else{
				   tnNum++;
				}
			}
			double tpr = tpr(fnNum, fpNum, tnNum, tpNum);
			double fpr = fpr(fnNum, fpNum, tnNum, tpNum);
			Point point = new Point(fpr, tpr);
			plotPoints.add(point);
			System.out.println("error rate"+(double)(fpNum + fnNum) / normalizedTestData.size());
		}
		
	}
	public void plotROC(){
		for (Point p : plotPoints) {
			System.out.println(p.getX() + "," + p.getY());
		}
	}
	// TODO errorRate
	public void errorRate() {

	}

	protected double AUC(){
		double sum=0;
		for(int i=1;i<plotPoints.size();i++){
			sum+=(plotPoints.get(i).getX()-plotPoints.get(i-1).getX())*(plotPoints.get(i).getY()+plotPoints.get(i-1).getY());
		}
		return sum*0.5;
	}
	/**
	 * 
	 * @param fnNum
	 * @param fpNum
	 * @param tnNum
	 * @param tpNum
	 * @return true positive rate value
	 */
	public double tpr(int fnNum, int fpNum, int tnNum, int tpNum){
		return (double)(tpNum)/(double)(tpNum+fnNum);
	}
	/**
	 * 
	 * @param fnNum
	 * @param fpNum
	 * @param tnNum
	 * @param tpNum
	 * @return false positive value
	 */
	public double fpr(int fnNum, int fpNum, int tnNum, int tpNum){
		return (double)(fpNum)/(double)(fpNum+tnNum);
	}
	
	/**
	 * return new weight after iteration
	 * @return
	 */
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
	
}
