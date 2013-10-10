package cs6140.hw3;

public class BatchGradientDescent extends GradientDescent{

	@Override
	public void trainData(double alpha) {
		// TODO Auto-generated method stub
		boolean isConverge = false;
		double oldRMSE= rmse();
		System.out.println("rmse:"+oldRMSE);
		double newRMSE= 0;
		while(!isConverge){
			for(int j=0;j<57;j++){
				double sum=0;
				for(Email mail: normalizedSet){
					
					double diff = mail.get(57) - h(weight, mail);
					sum+= diff * mail.get(j);
				}
				newWeight[j] =  weight[j]+ alpha*sum;
			}
			newRMSE = rmse();
			isConverge= isConverge(oldRMSE,newRMSE,0.00001);
			oldRMSE=newRMSE;
		}
		System.out.println("rmse"+oldRMSE);
	}
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		BatchGradientDescent bgd = new BatchGradientDescent();
		bgd.prepareTrainingDataSet();
		bgd.trainData(0.001);

		System.out.print("@@@@@@@@@");
		for (double a : bgd.getNewWeight()) {
			System.out.println("weight:" + a);
		}
		System.out.println(bgd.rmse());
		bgd.predict();
		bgd.plotROC();
		System.out.println(bgd.AUC());
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
	}

}
