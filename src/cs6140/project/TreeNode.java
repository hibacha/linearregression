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
	public TreeNode() {
		// TODO Auto-generated constructor stub
	}
    public void addToHash(String attValue,TreeNode childNode){
    	children.put(attValue, childNode);
    }
}
