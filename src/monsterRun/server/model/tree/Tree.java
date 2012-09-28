package monsterRun.server.model.tree;

import java.util.ArrayList;

/**
 *
 * Class that represents the tree structure
 * 
 */
public class Tree {
	private TreeNode root;
	private int size;

	/**
	 * Constructor
	 * 
	 * @param pos
	 *            the data that is stored in the root element of the tree
	 */
	public Tree(String pos) {
		root = new TreeNode(pos, null);
		size = 1;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Check if a piece of data exists in the tree
	 * 
	 * @param data
	 *            the string element to find in the tree
	 * @return boolean value suggesting if the data is on the tree or not
	 */
	public boolean exists(String data) {
		return this.exists(data, root);
	}

	/**
	 * Check if a piece of data exists in the tree
	 * 
	 * @param data
	 *            the string element to find on the tree
	 * @param node
	 *            the tree node which along with its children is going to be
	 *            checked for the element
	 * @return boolean value suggesting if the data exists on the tree
	 */
	private boolean exists(String data, TreeNode node) {
		if (node.getData().equals(data)) {
			return true;
		}
		boolean found = false;
		if (node != null) {
			ArrayList<TreeNode> children = node.getChildren();
			for (TreeNode child : children) {
				found = this.exists(data, child);
				if (found) {
					return found;
				}
			}
		}
		return found;
	}

	/**
	 * Get the tree node with a specified string data in it
	 * 
	 * @param data
	 *            the string data to be searched for
	 * @param node
	 *            the tree node which along with its children is going to be
	 *            checked for the piece of data
	 * @return TreeNode object that contains the piece of data that is being
	 *         searched
	 */
	private TreeNode getNode(String data, TreeNode node) {
		if (node.getData().equals(data)) {
			return node;
		}
		TreeNode target = null;
		if (node != null) {
			ArrayList<TreeNode> children = node.getChildren();
			for (TreeNode child : children) {
				target = this.getNode(data, child);
				if (target != null) {
					return target;
				}

			}
		}
		return null;
	}

	/**
	 * Add a node to the tree, the node has to be added as a child to the node
	 * containing the other string element that is passed in
	 * 
	 * @param parent
	 *            the string element which is the data in the parent element of
	 *            the node to be added
	 * @param child
	 *            the data of the node to be added to the tree
	 * @throws Exception
	 *             this is thrown when the parent node for the new node cannot
	 *             be found on the tree
	 */
	public void addChild(String parent, String child) throws Exception {
		TreeNode parentNode = this.getNode(parent, this.root);
		if (parentNode != null) {
			TreeNode childNode = new TreeNode(child, parentNode);
			parentNode.addChild(childNode);
			this.size++;
		} else {
			throw new Exception("Parent node does not exist in tree");
		}
	}

	/**
	 * Find the path from the node with the given string data to the root of the
	 * tree
	 * 
	 * @param data
	 *            the string element of the node from which the path to the root
	 *            has to be calculated
	 * @return ArrayList which is the list of nodes between the node with the
	 *         data and the root, this list is the path
	 */
	public ArrayList<String> getPathToRoot(String data) {
		ArrayList<String> path = new ArrayList<String>();
		TreeNode node = this.getNode(data, this.root);
		if (node != null) {
			path.add(node.getData());
			while (node.getParent() != null) {
				path.add(node.getParent().getData());
				node = node.getParent();
			}
		}
		path.remove(path.size() - 1);
		return path;
	}
}
