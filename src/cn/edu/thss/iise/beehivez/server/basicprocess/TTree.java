
/**
 * by hhw 
*/

/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.ArrayList;

public class TTree {

	private TTreeNode root;
	private ArrayList<TTreeNode> leafNodes = new ArrayList<TTreeNode>();
	private int nodeNumber = 0;

	/************ TTree ***************/
	public TTree() {
		root = new TTreeNode();
		incNodeNum();
	}
	public TTree(TTreeNode root) {
		this.root = root;
		//leafNodes.add(root);
		incNodeNum();
	}

	/************ root ***************/
	public void setRoot(TTreeNode root) {
		this.root = root;
	}

	public TTreeNode getRoot() {
		return root;
	}

	/************ utilize ***************/
	public void insertChild(TTreeNode parent, TTreeNode child) {
		parent.addChild(child);
		child.setParent(parent);
		incNodeNum();
	}

	public void incNodeNum() {
		nodeNumber++;
	}

	public int getNodeNum() {
		return nodeNumber;
	}

	/************ leafNodes ***************/
	public ArrayList<TTreeNode> getLeafNodes() {
		leafNodes.clear();
		setLeafNodes(getRoot());

		return leafNodes;
	}

	private void setLeafNodes(TTreeNode r) {
		if(r != null) {
			ArrayList<TTreeNode> children = r.getChild();
			if(children == null) {
				leafNodes.add(r);
			} else {
				for(int i = 0; i < children.size(); i++) {
					setLeafNodes((TTreeNode) children.get(i));
				}
			}
		}
	}
}


