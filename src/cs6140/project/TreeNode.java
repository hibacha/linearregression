package cs6140.project;

import java.util.HashMap;

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
    
    public int getLeafNumber(){
    	
    	if(!isLeaf){
    		int sum=0;
    		return sum;
    	}
    	else{
    		return 1;
    	}
    }
    
}
