package cs6140.hw4;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import cs6140.hw3.Email;

public class DecisionStumps {
	public Vector<Email> trainingSet;
	public int optimumFeature;
	public Vector<Double> d;
	
	public int train(Vector<Double> distribute){
		return 0;
	}
	
	public void calculateNewErrorRate(){
		
	}
	
	public void sortByFeature(int fIndex){
	    Collections.sort(null,new AdaComparator(fIndex));
	    
	}
}
