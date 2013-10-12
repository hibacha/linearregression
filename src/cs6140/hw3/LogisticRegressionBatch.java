package cs6140.hw3;

import java.util.Arrays;

public class LogisticRegressionBatch extends BaseGradientDescent{

	protected double rmse(){
		double sum = 0;
		for (Email mail : normalizedSet) {
			sum += Math.pow(gz(h(newWeight, mail)) - mail.get(57), 2);
		}
		return Math.sqrt(sum / normalizedSet.size());
	}
	
	@Override
	public void trainData(double alpha, double convergeTolerance) {
		boolean isConverge = false;
		double oldRMSE= rmse();
		System.out.println("Initial RMSE:"+oldRMSE);
		double newRMSE= 0;
		int iterationCount=0;
		while(!isConverge){
			iterationCount++;
			for(int j=0;j<57;j++){
				double sum=0;
				//update weight with newly calculated weight
				weight = Arrays.copyOf(newWeight, 57);
				for(Email mail: normalizedSet){
					double h= h(weight, mail);
					double diff = mail.get(57) - gz(h);
					sum+= diff * gz(h)*(1-gz(h))*mail.get(j);
				}
				newWeight[j] =  weight[j]+ alpha*sum;
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,convergeTolerance);
			oldRMSE=newRMSE;
			System.out.println(iterationCount+","+oldRMSE);
		}
	}
	
	
	public static void main(String[] args) {
		double lambda =0.01;
		double convergeTolerance=0.00001;
		boolean isPrintRMSE = true;
		boolean isPrintWeight = true;
		runner(new LogisticRegressionBatch(), lambda, convergeTolerance, isPrintWeight, isPrintRMSE);
	}

}
