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
		if(args.length!=1){
			System.err.println("Please give your data file path as first parameter!");
			System.exit(1);
		}
		KCrossValidation k = new KCrossValidation(10,args[0]);
		k.extractTestingSetByIndex(0);

		List<Car> trainingDataSet = k.getTrainingData();
		List<Car> testDataSet = k.getTestingData();
		
		DecisonTree decision = new DecisonTree();
		TreeNode trainedNode = decision.train(trainingDataSet,
				Arrays.asList(0, 1, 2, 3, 4, 5));
		
		pruneTree(trainedNode,2);
		
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
		
		calAllStatistic(confusionMaxtrix,1);
		calAllStatistic(confusionMaxtrix,2);
		calAllStatistic(confusionMaxtrix,3);
		
	}
	
	/**
	 * print our confusion matrix 
	 * @param matrix
	 */
	public static void printArray(int[][] matrix){
		for(int i=0;i<matrix.length;i++){
			for (int j = 0; j < matrix[i].length; j++) {
				int num = matrix[i][j];
				System.out.print(num+"\t");
			}
			System.out.println();
		}
	}
	
	/**
	 * calculate precision
	 * @param matrix
	 * @param classifierIndex
	 * @return
	 */
	public static double calPrecision(int[][] matrix, int classifierIndex){
		double tp=matrix[classifierIndex][classifierIndex];
		double sum=0;
		for(int i=0;i<matrix.length;i++){
			sum+=matrix[classifierIndex][i];
		}
		double precision=tp/sum;
		System.out.format("precision:%.5f%n",precision);
		return precision;
	}
	
	/**
	 * calculate false positive rate
	 * @param matrix
	 * @param classifierIndex
	 * @return
	 */
	public static double calFPR(int[][] matrix, int classifierIndex) {
		double negative = 0;
		for (int i = 0; i < matrix.length; i++) {
			if (i == classifierIndex) {
				continue;
			}
			for (int j = 0; j < matrix.length; j++) {
				negative += matrix[j][i];
			}
		}
		double fp=0;
		for(int k=0;k<matrix.length;k++){
			if(k==classifierIndex){
				continue;
			}
			fp+=matrix[classifierIndex][k];
		}
		double fpr=fp/negative;
		System.out.format("fpr:%.5f%n",fpr);
		return fpr;
	}

	public static void calAllStatistic(int[][] matrix, int label) {
		int size = matrix.length;
		for (int i = 0; i < size; i++) {
			switch(label){
			case 1:
				calTPR(matrix, i);
				break;
			case 2:
				calFPR(matrix, i);
				break;
			case 3:
				calPrecision(matrix,i);
			}
		}
	}
	
	/**
	 * calculate true positive rate
	 * @param matrix
	 * @param classifierIndex
	 * @return
	 */
	public static double calTPR(int[][] matrix, int classifierIndex){
		double sum=0;
		for(int i=0;i<matrix.length;i++){
		    sum+=matrix[i][classifierIndex];	
		}
		double tpr=matrix[classifierIndex][classifierIndex]/sum;
		System.out.format("tpr:%.5f%n",tpr);
		return tpr;
	}
	
	/**
	 * 
	 * @param decisionTree
	 * @param testCar
	 * @return predicted label
	 */
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
    
    /**
     * prune tree base on given error number
     * @param node
     * @param errorNumTreshold
     */
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
    
    /**
     * transform node into leaf 
     * @param node
     */
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
	
	/**
	 * print out the tree structure
	 * @param node
	 * @param indent
	 */
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

	/**
	 * print out the indent
	 * @param indent
	 * @return
	 */
	public static String outputIndent(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("-");
		}
		return sb.toString();
	}

}
