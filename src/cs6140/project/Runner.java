package cs6140.project;

import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KCrossValidation k = new KCrossValidation(10);
		k.extractTestingSetByIndex(0);

		List<Car> trainingDataSet = k.getTrainingData();
		List<Car> testDataSet = k.getTestingData();
		
		DecisonTree decision = new DecisonTree();
		TreeNode trainedNode = decision.train(trainingDataSet,
				Arrays.asList(0, 1, 2, 3, 4, 5));
		System.out.println(trainedNode.attIndex);
		printAll(trainedNode,0);
		// System.out.println("I:"+decision.entropy(trainingDataSet,6));
		
//		vhigh,vhigh,2,2,small,low,unacc
		Car car=new Car();
		car.add("vhigh");
		car.add("vhigh");
		car.add("2");
		car.add("2");
		car.add("small");
		car.add("low");
		car.add("good");
		
		int rightPrediction=0;
		for(int i=0;i<testDataSet.size();i++){
			String predictLabel = test(trainedNode,testDataSet.get(i));
			 
			 if(predictLabel.equals(testDataSet.get(i).get(DecisonTree.LABEL_INDEX))){
				 rightPrediction++;
			 }
		}
		System.out.println((double)rightPrediction/testDataSet.size());
		System.out.println(rightPrediction);

	}
    public static String test(TreeNode decisionTree, Car testCar){
    	TreeNode current=decisionTree;
    	while(!current.isLeaf){
    		String decisionAttribute  = testCar.get(current.attIndex);
    		current = current.children.get(decisionAttribute);
    	}
    	return current.label;
    	
    } 
	public static void printAll(TreeNode node, int indent) {
		
		if (!node.isLeaf) {
//			System.out.println(outputIndent(indent)+"Parent("+node.errorNumber+")("+node.fakeLabel+")"+node.errorNumber+":"+node.getLeafError()+":"+node.getLeafNumber()+":"+node.trainedInstancesNumber);
			boolean isPrune=node.isPrune();
			System.out.println(outputIndent(indent)+"Parent("+node.errorNumber+")("+node.fakeLabel+")"+isPrune);
			if(node.errorNumber<=5&&isPrune){
				node.label=node.fakeLabel;
				node.isLeaf=true;
				node.children=null;
				return;
			}
			Iterator<String> it = node.children.keySet().iterator();
			while (it.hasNext()) {
				String value=it.next();
				System.out.print(outputIndent(indent+1)+"Node:" + node.attIndex+"=");
				System.out.println(value);
				printAll(node.children.get(value),indent+1);
			}
		}else{
			System.out.println(outputIndent(indent)+"Leaf:" +node.label+"(error:"+node.errorNumber+")(total:"+node.trainedInstancesNumber);
		}
	}

	public static String outputIndent(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("-");
		}
		return sb.toString();
	}

}
