package cs6140.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DecisonTree {

	public static final int LABEL_INDEX=6;
	public static final String UNDECIDED_LABEL="";
	
	/**
	 * if all labels in trainData are the same 
	 * return the only label. Otherwise, return a fake label
	 * @param trainData
	 * @return
	 */
	public String checkLabel(List<Car> trainData){
		Set<String> set= new HashSet<String>();
		for(Car car:trainData){
			set.add(car.get(LABEL_INDEX));
		}
		if(set.size()==1)
			return trainData.get(0).get(LABEL_INDEX);
		else
			return UNDECIDED_LABEL;
	}
	
	/**
	 * majority vote 
	 * return the most frequent label 
	 * @param trainData
	 * @return
	 */
	public Object[] majorityVote(List<Car> trainData){
		HashMap<String, Integer> labelCounterMap=new HashMap<String, Integer>();
		for(Car car:trainData){
			String label = car.get(LABEL_INDEX);
			if(labelCounterMap.get(label)==null){
			   labelCounterMap.put(label, 1);
			}else{
			   int num=labelCounterMap.get(label);
			   labelCounterMap.put(label, num+1);
			}
		}
		String maxLabel = getMostFrequentLabel(labelCounterMap);
		Integer occurTimes = labelCounterMap.get(maxLabel);
		Object[] reObj=new Object[2];
		reObj[0]=maxLabel;
		reObj[1]=occurTimes;
		return reObj;
	}

	private String getMostFrequentLabel(HashMap<String, Integer> labelCounterMap) {
		int maxNum=0;
		String maxLabel="";
		Iterator<String> it=labelCounterMap.keySet().iterator();
		while(it.hasNext()){
			String label=it.next();
			int currentNum = labelCounterMap.get(label);
			if(currentNum>=maxNum){
				maxNum=currentNum;
				maxLabel=label;
			}
		}
		return maxLabel;
	}
	
	public TreeNode train(List<Car> trainData, List<Integer> attributeList) {
		if(attributeList.size()==0){
			TreeNode node=new TreeNode();
			node.isLeaf=true;
			node.label=(String)majorityVote(trainData)[0];
			node.errorNumber=trainData.size()-(Integer)majorityVote(trainData)[1];
			node.trainedInstancesNumber=trainData.size();
			return node;
		}
		
		String preCheckLabel=checkLabel(trainData);
		if(!preCheckLabel.equals(UNDECIDED_LABEL)){
			return createLeafNode(preCheckLabel,trainData.size());
		}
		
		///??????
		double entropyTotal = entropy(trainData, LABEL_INDEX);
		double maxGainRatio=0;
		HashMap<String, List<Car>> maxHashMap=null;
		int maxAttIndex=-1;
		
		for(int attIndex:attributeList){
			HashMap<String, List<Car>> countMap = generateMapByGivenIndex(trainData, attIndex);
			Double[] result = expectedEntropyByAtt(countMap,trainData.size());
			double gainRatio=(entropyTotal-result[0])/result[1];
//			double gainRatio=(entropyTotal-result[0]);
			System.out.println(attIndex+" gain ratio :"+gainRatio);
			if(gainRatio>=maxGainRatio){
				maxGainRatio=gainRatio;
				maxAttIndex=attIndex;
				maxHashMap=countMap;
			}
		}
		
		TreeNode node=new TreeNode();
		node.attIndex=maxAttIndex;
		//TODO:??????
		Iterator<String> it=maxHashMap.keySet().iterator();
		while(it.hasNext()){
			String branchAttValue = it.next();
			List<Car>  dividedTrainData= maxHashMap.get(branchAttValue);
			TreeNode subTreeNode = train(dividedTrainData, filterOutSelectAtt(attributeList,maxAttIndex));
			node.addToHash(branchAttValue, subTreeNode);
			node.trainedInstancesNumber=trainData.size();
			
			Object[] reObj=majorityVote(trainData);
			String fakeString = (String)reObj[0];
			Integer majorityNum = (Integer)reObj[1];
			node.fakeLabel=fakeString;
			node.errorNumber=trainData.size()-majorityNum;
		}
		
		return node;
	}


	
	private TreeNode createLeafNode(String preCheckLabel,int instanceNumber) {
		TreeNode node=new TreeNode();
		node.isLeaf=true;
		node.label=preCheckLabel;
		node.errorNumber=0;
		node.trainedInstancesNumber=instanceNumber;
		return node;
	}
	
	private List<Integer> filterOutSelectAtt(List<Integer> originalAttList,int selectedAtt){
		List<Integer> newAttList=new ArrayList<Integer>(originalAttList);
		newAttList.remove(new Integer(selectedAtt));
		return newAttList;
	}
    /**
     * 
     * @param countMap
     * @param total
     * @return  first element is expectedSum 
     * 		   second element is splitInfo
     */
	private Double[] expectedEntropyByAtt(HashMap<String, List<Car>> countMap,int total){
		double expectedSum=0;
		double splitInfo=0;
		Iterator<String> oneAttValuesIt = countMap.keySet().iterator();
		while(oneAttValuesIt.hasNext()){
			String attValue= oneAttValuesIt.next();
			List<Car> listOfCarInOneValue = countMap.get(attValue);
			double probability =((double)listOfCarInOneValue.size()/total);
			expectedSum+=probability*entropy(listOfCarInOneValue, LABEL_INDEX);
			if(probability==1){
			 System.err.println("error");
			}
			splitInfo+=-1*probability*log2(probability);
		}
		Double[] result = new Double[2];
		result[0]=expectedSum;
		result[1]=splitInfo;
		return result;
	}
	public double entropy(List<Car> data,int indexOfKey) {
		HashMap<String, List<Car>> countMap = generateMapByGivenIndex(data,
				indexOfKey);

		Set<String> labels = countMap.keySet();
		int[] nums = new int[labels.size()];
		Iterator<String> it = labels.iterator();
		int index = 0;
		while (it.hasNext()) {
			int count4label = countMap.get(it.next()).size();
			nums[index] = count4label;
			index++;
		}

//		for (int a : nums) {
//			System.out.println("a:" + a);
//		}

		return entropyHelper(nums,data.size());
	}

	private HashMap<String, List<Car>> generateMapByGivenIndex(List<Car> data,
			int indexOfAtrribute) {
		HashMap<String, List<Car>> countMap = new HashMap<String, List<Car>>();
		for (Car car : data) {
			String label = car.get(indexOfAtrribute);
			if (countMap.get(label) == null) {
				List<Car> list = new ArrayList<Car>();
				list.add(car);
				countMap.put(label, list);
			} else {
				List<Car> list = countMap.get(label);
				list.add(car);
			}
		}
		return countMap;
	}

	private double entropyHelper(int[] nums, int total) {

		double entropySum = 0;
		for (int i : nums) {
			double p = (double) i / total;
			entropySum += -1 * p * log2(p);
		}
		return entropySum;
	}

	private double log2(double a) {
		return Math.log10(a) / Math.log10(2);
	}

}
