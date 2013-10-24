package cs6140.hw4;

import java.util.ArrayList;
import java.util.Collections;

public class ROC {
	
	public static ArrayList<Point> plotROC(ArrayList<Email> testingSet) {
		Collections.sort(testingSet,Collections.reverseOrder(new EmailFeatureComparator(MyConstant.INDEX_FOR_TAU)));
		
		ArrayList<Point> plotPoints =new ArrayList<Point>();
		
		for(int i=0;i<testingSet.size();i++){
			int fnNum = 0;
			int fpNum = 0;
			int tnNum = 0;
			int tpNum = 0;
			for(int spamStart=0;spamStart<i;spamStart++){
				if(testingSet.get(spamStart).get(MyConstant.INDEX_EMAIL_SPAM_LABEL)==1){
					tpNum++;
				}else{
					fpNum++;
				}
			}
			
			for(int nonspamStart=i;nonspamStart<testingSet.size();nonspamStart++){
				if(testingSet.get(nonspamStart).get(MyConstant.INDEX_EMAIL_SPAM_LABEL)==1){
				   fnNum++;
				}else{
				   tnNum++;
				}
			}
			double tpr = tpr(fnNum, fpNum, tnNum, tpNum);
			double fpr = fpr(fnNum, fpNum, tnNum, tpNum);
			Point point = new Point(fpr, tpr);
			plotPoints.add(point);
		}
		return plotPoints;
	}
	
	private static double tpr(int fnNum, int fpNum, int tnNum, int tpNum){
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
	private static double fpr(int fnNum, int fpNum, int tnNum, int tpNum){
		return (double)(fpNum)/(double)(fpNum+tnNum);
	}
}
