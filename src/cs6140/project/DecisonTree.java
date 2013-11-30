package cs6140.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DecisonTree {

	
	public String checkLabel(List<Car> trainData){
		Set<String> set= new HashSet<String>();
		for(Car car:trainData){
			set.add(car.get(6));
		}
		if(set.size()==1)
			return trainData.get(0).get(6);
		else
			return "";
	}
	public String majorityVote(List<Car> trainData){
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		for(Car car:trainData){
			String label = car.get(6);
			if(map.get(label)==null){
				map.put(label, 1);
			}else{
				int num=map.get(label);
				map.put(label, num+1);
			}
		}
		int maxNum=0;
		String maxLabel="";
		Iterator<String> it=map.keySet().iterator();
		while(it.hasNext()){
			String label=it.next();
			int currentNum = map.get(label);
			if(currentNum>maxNum){
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
			node.label=majorityVote(trainData);
			return node;
		}
		
		String preCheckLabel=checkLabel(trainData);
		if(!preCheckLabel.equals("")){
			TreeNode node=new TreeNode();
			node.isLeaf=true;
			node.label=preCheckLabel;
			return node;
		}
		
		///??????
		double entropyTotal = entropy(trainData, 6);
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
		}
		
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
			expectedSum+=probability*entropy(listOfCarInOneValue, 6);
			if(probability==1){
			//	break;
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
			int indexOfKey) {
		HashMap<String, List<Car>> countMap = new HashMap<String, List<Car>>();
		for (Car car : data) {
			String label = car.get(indexOfKey);
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
