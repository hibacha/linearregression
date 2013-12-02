package cs6140.project;

import java.util.HashMap;
import java.util.Iterator;

public class TreeNode {

	/**
	 * @param args
	 */
	public boolean isLeaf=false;
	public HashMap<String,TreeNode> children = new HashMap<String, TreeNode>();
	public TreeNode parent=null;
	public int attIndex;
	public String label;
	public int trainedInstancesNumber;
	public int errorNumber;
	public String fakeLabel;
	public TreeNode() {
		// TODO Auto-generated constructor stub
	}
    public void addToHash(String attValue,TreeNode childNode){
    	children.put(attValue, childNode);
    }
    
    public boolean isPrune(){
    	double et=errorNumber+0.5;
    	double eT=getLeafError()+getLeafNumber()/(double)2;
    	double se=Math.sqrt(eT*(trainedInstancesNumber-eT)/trainedInstancesNumber);
    	return et<=eT+se;
    }
    public int getLeafError(){
    	if(!isLeaf){
    		int sum=0;
    		Iterator<TreeNode> it=children.values().iterator();
    		while(it.hasNext()){
    			sum+=it.next().getLeafError();
    		}
    		return sum;
    	}
    	else{
    		return errorNumber;
    	}
    }
    public int getLeafNumber(){
    	if(!isLeaf){
    		int sum=0;
    		Iterator<TreeNode> it=children.values().iterator();
    		while(it.hasNext()){
    			sum+=it.next().getLeafNumber();
    		}
    		return sum;
    	}
    	else{
    		return 1;
    	}
    }
    
}
