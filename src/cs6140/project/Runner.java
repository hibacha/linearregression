package cs6140.project;

import java.util.Arrays;
import java.util.List;

public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KCrossValidation k=new KCrossValidation(10);
		k.extractTestingSetByIndex(-1);
		
		List<Car> trainingDataSet = k.getTrainingData();
		DecisonTree decision = new DecisonTree();
		TreeNode node = decision.train(trainingDataSet, Arrays.asList(0,1,2,3,4,5));
		System.out.println(node.attIndex);
//		System.out.println("I:"+decision.entropy(trainingDataSet,6));
	}

}
