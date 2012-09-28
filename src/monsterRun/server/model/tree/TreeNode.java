package monsterRun.server.model.tree;

import java.util.ArrayList;

/**
 *
 * Class that represents each node in a tree
 * 
 */
public class TreeNode {
	private String data;
	private ArrayList<TreeNode> children;
	private TreeNode parent;

	/**
	 * Constructor
	 * 
	 * @param data
	 *            the string data that is stored in each node
	 * @param parent
	 *            the parent element of each element
	 */
	public TreeNode(String data, TreeNode parent) {
		this.data = data;
		this.parent = parent;
		this.children = new ArrayList<TreeNode>();
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public String getData() {
		return data;
	}

	public void setData(String position) {
		this.data = position;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}

	public void addChild(TreeNode node) {
		this.children.add(node);
	}
}
