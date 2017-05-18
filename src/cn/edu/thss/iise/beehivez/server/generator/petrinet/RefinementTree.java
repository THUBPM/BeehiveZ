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

/**
 * programmed according to the paper "A Top-Down Petri Net-Based Approach for Dynamic Workflow
 *  Modeling" published in BPM2003
 *  
 *  only record the history information about the basic refinement rules
 * 
 * used to log the history information how a Petri net model can be refined from a place 
 * step by step.
 * 
 * there are two kind of nodes in the tree: 
 * (1) internal node : used to record the rules used during the refinement process.
 * (2) leaf node: used to record the places or transitions in the model.
 * 
 * For internal nodes, there would be three children at most
 * 
 * For both internal modes and leaf nodes, they can get their parents directly.
 * 
 * Because a child pointer can point to both internal nodes and leaf nodes, we define an 
 * abstract class Node used as the parent class for internal nodes and leaf nodes
 * 
 * there must be one internal node and three leaf nodes at least.
 */
package cn.edu.thss.iise.beehivez.server.generator.petrinet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 */
public class RefinementTree {
	private static final boolean debug = false;

	// sequential place split and sequential transition split belong to the
	// same operation
	public static final int sequentialSplit = 1;
	public static final int andSplit = 2;
	public static final int orSplit = 3;
	public static final int loopSplit = 4;

	// the root of this tree
	private InternalNode root = null;

	// the mapping between the PNNode and the LeafNode of this tree
	private Vector<PNNode> pnNodes = new Vector<PNNode>();
	private Vector<LeafNode> leafNodes = new Vector<LeafNode>();

	// start with an atomic process, including a source place, a sink place and
	// a transition
	public RefinementTree(Place source, Transition t, Place sink) {
		// create the root node
		root = new InternalNode(RefinementTree.sequentialSplit, this);

		// create the first leaf nodes
		LeafNode leaf1 = new LeafNode(source, this);
		LeafNode leaf2 = new LeafNode(t, this);
		LeafNode leaf3 = new LeafNode(sink, this);

		// organize them in the tree
		root.addChild(leaf1);
		root.addChild(leaf2);
		root.addChild(leaf3);
	}

	/**
	 * now, because this function is used for adding communication and
	 * synchronization, node1 and node2 must be of the same type, which means
	 * that they must both be place or transition.
	 * 
	 * complete parallel means that two nodes both appear in the same run or
	 * neither appear in some runs
	 * 
	 * @param node1
	 *            the first node, maybe place or transition
	 * @param node2
	 *            the second node, maybe place or transition
	 * @return true if node1 and node2 are on parallel threads, false if not
	 * 
	 */
	public boolean isCompleteParallel(PNNode node1, PNNode node2) {
		if (debug) {
			System.out.println("isCompleteParallel begin");
		}
		// two nodes must be of the same type
		if (!node1.getClass().equals(node2.getClass())) {
			return false;
		}

		if (node1 == node2) {
			return false;
		}
		// //////////////////////////////
		// first find the corresponding leaf
		// /////////////////////////////
		int i1 = this.pnNodes.indexOf(node1);
		int i2 = this.pnNodes.indexOf(node2);
		if (i1 < 0 || i2 < 0) {
			return false;
		}
		LeafNode leaf1 = this.leafNodes.get(i1);
		LeafNode leaf2 = this.leafNodes.get(i2);

		if (leaf1 == null || leaf2 == null) {
			System.out
					.println("there must be something wrong with the mapping between pnNodes and leafNodes");
			return false;
		}

		// ///////////////////////////////////////////////////////////
		// get the path between these two leaf in the refinement tree
		// ////////////////////////////////////////////////////////////

		// record the ancestors before find a path
		Vector<InternalNode> ancestors1 = new Vector<InternalNode>();
		Vector<InternalNode> ancestors2 = new Vector<InternalNode>();

		InternalNode parent1 = leaf1.getParent();
		InternalNode parent2 = leaf2.getParent();
		// trace parent to the same level
		if (parent1.getLevel() > parent2.getLevel()) {
			while (parent1.getLevel() > parent2.getLevel()) {
				ancestors1.add(parent1);
				parent1 = parent1.getParent();
			}
		} else if (parent1.getLevel() < parent2.getLevel()) {
			while (parent2.getLevel() > parent1.getLevel()) {
				ancestors2.add(parent2);
				parent2 = parent2.getParent();
			}
		}

		ancestors1.add(parent1);
		ancestors2.add(parent2);

		// try to find the nearest common ancestor
		while (parent1 != parent2) {
			parent1 = parent1.getParent();
			parent2 = parent2.getParent();
			ancestors1.add(parent1);
			ancestors2.add(parent2);
		}
		// at last, parent1 must be the same as parent2
		// In the worst case, the common ancestor is the root node.

		// ////////////////////////////////////////////////////////////////////////////////
		// analyse the path and judge whether the two nodes chosen are on
		// parallel threads
		// /////////////////////////////////////////////////////////////////////////////////

		// the peak node on the path must be the and split
		if (parent1.operationCode != RefinementTree.andSplit) {
			return false;
		}

		// if loop split is the last split, and the nodes are transition, it is
		// not complete parallel
		if ((ancestors1.get(0).operationCode == RefinementTree.loopSplit || ancestors2
				.get(0).operationCode == RefinementTree.loopSplit)
				&& node1 instanceof Transition) {
			return false;
		}

		// if or split appears on the path, it must not be complete parallel.
		// loop split cannot be followed by a transition split
		for (int i = 0; i < ancestors1.size(); i++) {
			if (ancestors1.get(i).operationCode == RefinementTree.orSplit) {
				return false;
			}
			if (ancestors1.get(i).operationCode == RefinementTree.loopSplit
					&& i > 0) {
				if (ancestors1.get(i - 1) == ancestors1.get(i).children.get(0)) {
					return false;
				}
			}
		}
		for (int i = 0; i < ancestors2.size(); i++) {
			if (ancestors2.get(i).operationCode == RefinementTree.orSplit) {
				return false;
			}
			if (ancestors2.get(i).operationCode == RefinementTree.loopSplit
					&& i > 0) {
				if (ancestors2.get(i - 1) == ancestors2.get(i).children.get(0)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param nodeChosen
	 *            the node chosen to be split
	 * @param operation
	 *            the operation used for refinement
	 * @param newPNNodes
	 *            the new nodes generated with the refinement, they must be in
	 *            inverted sequence
	 */
	public void recordRefinement(PNNode nodeChosen, int operation,
			Vector<PNNode> newPNNodes) {

		// try to find the corresponding leaf node in the refinement tree.
		// if cannot, return immediately
		int i = this.pnNodes.indexOf(nodeChosen);
		if (i < 0) {
			// System.out
			// .println("the node chosen to be split does not exist in the refinement tree");
			return;
		}
		LeafNode leafChosen = this.leafNodes.get(i);
		if (leafChosen == null) {
			System.out
					.println("there are must be something wrong with the mapping between the pnNodes and leafNodes");
			return;
		}

		InternalNode parent = leafChosen.getParent();
		if (parent == null) {
			System.out
					.println("there must be something wrong with the refinement tree");
			return;
		}

		// the index of leaf in the parent node
		int indexInParent = parent.children.indexOf(leafChosen);

		// check whether the operation is the same as the parent's operation
		if (operation == parent.operationCode) {
			// need not to generate new internal node
			// just insert the new nodes at the indexInParent of parent
			for (PNNode newPNNode : newPNNodes) {
				// create new leaf node
				LeafNode newLeaf = new LeafNode(newPNNode, this);

				// insert into the tree
				parent.addChild(indexInParent, newLeaf);
			}
		} else {
			// need to generate new internal node
			// create new internal node
			InternalNode newInNode = new InternalNode(operation, this);

			// insert the new internal node into the tree
			// and insert the leaf node into this new internal node
			parent.setChild(indexInParent, newInNode);
			newInNode.addChild(leafChosen);
			for (PNNode newPNNode : newPNNodes) {
				// create new leaf node
				LeafNode newLeaf = new LeafNode(newPNNode, this);

				// insert into the tree
				newInNode.addChild(0, newLeaf);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private class RFTNode {
		// refinement tree contains this node
		protected RefinementTree owner;

		// record the level of this node in the tree
		// the level of root node is 0
		// if a node is not set parent, it is looked on as a root node
		protected int level = 0;

		protected InternalNode parent = null;

		/**
		 * @return the parent
		 */
		public InternalNode getParent() {
			return parent;
		}

		/**
		 * at the same time, calculate the level of this node. the level of root
		 * node is 0.
		 * 
		 * this function will be called when a new node is inserted into the
		 * refinement tree, moreover, it will be called by addChild or setChild
		 * automatically
		 * 
		 * never call this function by yourself
		 * 
		 * @param parent
		 *            the parent to set
		 */
		protected void setParent(InternalNode parent) {
			this.parent = parent;
			if (parent == null) {
				this.level = 0;
			} else {
				this.level = parent.getLevel() + 1;
			}
		}

		/**
		 * @return the level
		 */
		public int getLevel() {
			return level;
		}
	}

	private class InternalNode extends RFTNode {
		// used to record the refinement operation
		// use the static member of RefinementTree
		private int operationCode;

		// used to record the children
		// the child maybe a internal node or a leaf node.
		private Vector<RFTNode> children = new Vector<RFTNode>();

		// will create the root node by default
		// a non-root node must be inserted into the refinment tree
		// by inserted as a child of the existing internal node.
		// In this situation, the function addChild or setChild must
		// be called
		public InternalNode(int operation, RefinementTree ownerTree) {
			this.owner = ownerTree;
			this.operationCode = operation;
		}

		/**
		 * change the index th child of parent to a new child
		 * 
		 * @param index
		 *            the index of child in the parent
		 * @param child
		 *            the new child
		 */
		public void setChild(int index, RFTNode child) {
			this.children.set(index, child);
			child.setParent(this);
		}

		// the new child will be inserted at the designated place.
		public void addChild(int index, RFTNode child) {
			this.children.add(index, child);
			child.setParent(this);
		}

		// the new child will be inserted as the last child
		public void addChild(RFTNode child) {
			this.children.add(child);
			child.setParent(this);
		}
	}

	private class LeafNode extends RFTNode {
		private PNNode pnNode;

		// While a new leaf node is created, the mapping between the PNNode and
		// the LeafNode must be updated
		public LeafNode(PNNode pnNode, RefinementTree ownerTree) {
			this.owner = ownerTree;
			this.pnNode = pnNode;
			this.owner.pnNodes.add(pnNode);
			this.owner.leafNodes.add(this);
		}
	}
}
