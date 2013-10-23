package cs6140.hw4;

import java.util.ArrayList;
import java.util.Vector;

import cs6140.hw3.Email;

public class AdaBoosting {
	
	
	public static Vector<Double> updateD(Solution os, Vector<Double> oldD, ArrayList<Email> trainingSet){
		double sum =0;
		Vector<Double> newD=new Vector<Double>();
		for(Email e: trainingSet){
			double realClass= e.get(MyConstant.INDEX_EMAIL_SPAM_LABEL);
			double predict= e.get(os.getFeatureIndex()) < os.getThreshold()?-1:1;
			double factor=1;
			if(predict==realClass){
				factor=Math.sqrt(os.getErrorRateWeighted()/(1-os.getErrorRateWeighted()));
			}else{
				factor=Math.sqrt((1-os.getErrorRateWeighted())/os.getErrorRateWeighted());
			}
			
			double id=e.get(MyConstant.INDEX_FOR_DATA_ID);
			double nominator=oldD.get((int)id)*factor;
			newD.add((int)id,nominator);
			sum+=nominator;
			
		}
		for(int i=0;i<newD.size();i++){
			newD.set(i, newD.get(i)/sum);
		}
	
		return newD;
	}
}
