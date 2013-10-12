package cs6140.hw3;

import java.util.Arrays;



public class StochasticGradientDescent extends BaseGradientDescent{

	/**
	 * @param args
	 */

	
    @Override
	public void trainData(double alpha, double convergeTolerance) {
		boolean isConverge = false;
		double oldRMSE = rmse();
		System.out.println("rmse:"+oldRMSE);
		double newRMSE = 0;
		int iterationCount=0;
		while (!isConverge) {
			iterationCount++;
			for (Email mail : normalizedSet) {
				//update weight with newly calculated weight
				weight = Arrays.copyOf(newWeight, 57);
				for (int j = 0; j < 57; j++) {
					double diff = mail.get(57) - h(weight, mail);
					newWeight[j] = weight[j] + alpha * diff * mail.get(j);
				}
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,convergeTolerance);
			oldRMSE=newRMSE;
			System.out.println(iterationCount+","+oldRMSE);
		}
		System.out.println("Final:"+oldRMSE);
	}


	
	
	public static void main(String[] args) {
		double lambda =0.001;
		
		double convergeTolerance=0.00001;
		boolean isPrintRMSE = true;
		boolean isPrintWeight = true;
		runner(new StochasticGradientDescent(), lambda, convergeTolerance, isPrintWeight, isPrintRMSE);
	}

}
