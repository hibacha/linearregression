package cs6140.hw3;

import java.util.Arrays;

public class LogisticRegressionStochastic extends BaseGradientDescent{
	
	@Override
	/**
	 * Logistic regression formulation 
	 */
	protected double rmse(){
		double sum = 0;
		for (Email mail : normalizedSet) {
			sum += Math.pow(gz(h(newWeight, mail)) - mail.get(57), 2);
		}
		return Math.sqrt(sum / normalizedSet.size());
	}
	
	public void trainData(double alpha, double convergeTolerance) {
		boolean isConverge = false;
		double oldRMSE = rmse();
		System.out.println("init RMSE:"+oldRMSE);
		double newRMSE = 0;
		while (!isConverge) {
			for (Email mail : normalizedSet) {
				//update weight with newly calculated weight
				weight = Arrays.copyOf(newWeight, 57);
				for (int j = 0; j < 57; j++) {
					double h=h(weight, mail);
					double diff = mail.get(57) - gz(h);
					newWeight[j] = weight[j] + alpha * diff *gz(h)*(1-gz(h))* mail.get(j);
				}
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,convergeTolerance);
			oldRMSE=newRMSE;
		}
		System.out.println("Final RMSE:"+oldRMSE);
	}
	
	public static void main(String[] args) {
		double lambda =0.001;
		double convergeTolerance=0.0001;
		boolean isPrintRMSE = true;
		boolean isPrintWeight = true;
		runner(new LogisticRegressionStochastic(), lambda, convergeTolerance, isPrintWeight, isPrintRMSE);
	}
}
