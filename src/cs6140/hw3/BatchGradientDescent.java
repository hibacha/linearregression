package cs6140.hw3;

import java.util.Arrays;

public class BatchGradientDescent extends BaseGradientDescent{

	@Override
	public void trainData(double alpha, double convergeTolerance) {
		// TODO Auto-generated method stub
		boolean isConverge = false;
		double oldRMSE= rmse();
		System.out.println("Initial RMSE:"+oldRMSE);
		double newRMSE= 0;
		while(!isConverge){
			for(int j=0;j<57;j++){
				double sum=0;
				//update weight with newly calculated weight
				weight = Arrays.copyOf(newWeight, 57);
				for(Email mail: normalizedSet){
					double diff = mail.get(57) - h(weight, mail);
					sum+= diff * mail.get(j);
				}
				newWeight[j] =  weight[j]+ alpha*sum;
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,convergeTolerance);
			oldRMSE=newRMSE;
		}
	}
	
	
	public static void main(String[] args) {
		double lambda = 0.00001;
		double convergeTolerance=0.00001;
		boolean isPrintRMSE = true;
		boolean isPrintWeight = true;
		runner(new BatchGradientDescent(), lambda, convergeTolerance, isPrintWeight, isPrintRMSE);
	}

}
