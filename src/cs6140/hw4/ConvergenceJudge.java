package cs6140.hw4;

import java.util.LinkedList;

public class ConvergenceJudge {
	private LinkedList<Double> differentList=new LinkedList<Double>();
	private int recordSize=0;
	private double tolerance=0;
	public ConvergenceJudge(int recordSize, double tolerance){
		this.recordSize = recordSize;
		this.tolerance = tolerance;
	}
	
	public boolean isConvergeAfterAddNewDiff(Double diff){
		differentList.addFirst(diff);
		if(differentList.size()>recordSize){
			differentList.removeLast();
		}
		double sum=0;
		for(int i=0;i<differentList.size();i++){
			sum+=differentList.get(i);
		}
		return sum/differentList.size()<tolerance;
	}
	
}
