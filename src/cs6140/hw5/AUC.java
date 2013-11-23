package cs6140.hw5;

import java.util.ArrayList;

public class AUC {
	
	public static double calAUC(ArrayList<Point> plotPoints) {
		double sum = 0;
		for (int i = 1; i < plotPoints.size(); i++) {
			sum += (plotPoints.get(i).getX() - plotPoints.get(i - 1).getX())
					* (plotPoints.get(i).getY() + plotPoints.get(i - 1).getY());
		}
		double auc = sum * 0.5;
		System.out.println("\tAUC:" + auc);
		return auc;
	}
}
