/**
 * by hhw 
*/

package cn.edu.thss.iise.beehivez.server.basicprocess;

public class AstarTree {
	private AstarTreeNode root = null;

	/************ AstarTree ***************/
	public AstarTree(int vol1, int vol2,int n) {
		root = new AstarTreeNode(vol1, vol2,n);
	}

	/************ utilize ***************/
	public AstarTreeNode getRoot() {
		return root;
	}

	public void setRoot(AstarTreeNode root) {
		this.root = root;
	}
	
}
