package cs6140.hw3;

import java.util.Arrays;

public class LogisticRegressionStochastic extends GradientDescent{
	private double gz(double x){
		return 1/(1+Math.exp(-1*x));
	}
	
	protected double rmse(){
		double sum = 0;
		for (Email mail : normalizedSet) {
			sum += Math.pow(gz(h(newWeight, mail)) - mail.get(57), 2);
		}
		return Math.sqrt(sum / normalizedSet.size());
	}
	
	public void trainData(double alpha) {
		boolean isConverge = false;
		double oldRMSE = rmse();
		System.out.println("rmse:"+oldRMSE);
		double newRMSE = 0;
		while (!isConverge) {
			for (Email mail : normalizedSet) {
				weight = Arrays.copyOf(newWeight, 57);
				for (int j = 0; j < 57; j++) {
					double h=h(weight, mail);
					double diff = mail.get(57) - gz(h);
					newWeight[j] = weight[j] + alpha * diff *gz(h)*(1-gz(h))* mail.get(j);
				}
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,0.00001);
			oldRMSE=newRMSE;
			//System.out.println("rmse"+oldRMSE);
		}
		System.out.println("rmse"+oldRMSE);
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		LogisticRegressionStochastic sgd = new LogisticRegressionStochastic();
		sgd.prepareTrainingDataSet();
		sgd.trainData(0.001);

		System.out.print("@@@@@@@@@");
		for (double a : sgd.getNewWeight()) {
			System.out.println("weight:" + a);
		}
		System.out.println(sgd.rmse());
		sgd.predict();
		sgd.plotROC();
		System.out.println(sgd.AUC());
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
	}
}
