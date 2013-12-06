package cs6140.project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Runner {

	/**
	 * @param args
	 */
	
	public static int[][] confusionMaxtrix=new int[4][4];
	public static HashMap<String, Integer> classMapToIndex=new HashMap<String, Integer>();
	static {
		classMapToIndex.put("unacc", 0);
		classMapToIndex.put("acc", 1);
		classMapToIndex.put("vgood", 2);
		classMapToIndex.put("good", 3);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KCrossValidation k = new KCrossValidation(10);
		k.extractTestingSetByIndex(0);

		List<Car> trainingDataSet = k.getTrainingData();
		List<Car> testDataSet = k.getTestingData();
		
		DecisonTree decision = new DecisonTree();
		TreeNode trainedNode = decision.train(trainingDataSet,
				Arrays.asList(0, 1, 2, 3, 4, 5));
		
//		printMutualRatio(trainedNode);
		pruneTree(trainedNode,12);
		printMutualRatio(trainedNode);

	//	printAll(trainedNode,0);
		
		int rightPrediction = 0;
		for (int i = 0; i < testDataSet.size(); i++) {
			String predictLabel = test(trainedNode, testDataSet.get(i));
			String actualLabel = testDataSet.get(i).get(DecisonTree.LABEL_INDEX);
			confusionMaxtrix[classMapToIndex.get(predictLabel)][classMapToIndex.get(actualLabel)]+=1;
			if (predictLabel.equals(actualLabel)) {
				rightPrediction++;
			}
		}
		System.out.println((double) rightPrediction / testDataSet.size());
		System.out.println(rightPrediction + " out of "+ testDataSet.size());
		System.out.println("leaf number:"+trainedNode.getLeafNumber());
		printArray(confusionMaxtrix);
	}
	public static void printArray(int[][] matrix){
		for(int i=0;i<matrix.length;i++){
			for (int j = 0; j < matrix[i].length; j++) {
				int num = matrix[i][j];
				System.out.print(num+"\t");
			}
			System.out.println();
		}
	}
    public static String test(TreeNode decisionTree, Car testCar){
    	TreeNode current=decisionTree;
    	while(!current.isLeaf){
    		String decisionAttribute  = testCar.get(current.attIndex);
    		if(current.children.get(decisionAttribute)==null){
    			return current.fakeLabel;
    		}
    		current = current.children.get(decisionAttribute);
    	}
    	return current.label;
    } 
    
    public static void pruneTree(TreeNode node, int errorNumTreshold){
    	if(!node.isLeaf){
    		boolean isPrune=node.isPrune();
    		if(node.errorNumber<=errorNumTreshold && isPrune){
    			transformIntoLeaf(node);
				return;
    		}
    		Iterator<TreeNode> it = node.children.values().iterator();
    		while(it.hasNext()){
    			TreeNode childNode=it.next();
    			pruneTree(childNode, errorNumTreshold);
    		}
    	}
    }
	private static void transformIntoLeaf(TreeNode node) {
		node.label=node.fakeLabel;
		node.isLeaf=true;
		node.children=null;
	}
	
	public static void printMutualRatio(TreeNode node){
		LinkedList<TreeNode> queue=new LinkedList<TreeNode>();
		queue.add(node);
		while(queue.size()!=0){
			TreeNode dequeueNode = queue.remove(0);
			if(!dequeueNode.isLeaf){
				System.out.println(dequeueNode.mutualInfo);
				Iterator<TreeNode> it=dequeueNode.children.values().iterator();
				while(it.hasNext()){
					queue.add(it.next());
				}
			}
		}
	}
	
	public static void printAll(TreeNode node, int indent) {
		if (!node.isLeaf) {
			boolean isPrune=node.isPrune();
			System.out.println(outputIndent(indent)+"Parent("+node.errorNumber+")("+node.fakeLabel+")"+isPrune);
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
