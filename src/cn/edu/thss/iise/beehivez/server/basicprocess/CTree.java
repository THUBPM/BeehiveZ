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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * the coverability tree
 * 
 * @author He tengfei
 * 
 */
public class CTree {
	private CTreeNode root;
	private ArrayList<CTreeNode> leafNodes = null;
	private ArrayList<CTreeNode> allNodes = null;
	private HashMap<String, MyPetriTransition> edges = null;

	public CTree(CTreeNode root) {
		this.root = root;
		leafNodes = new ArrayList<CTreeNode>();
		edges = new HashMap<String, MyPetriTransition>();
		allNodes = new ArrayList<CTreeNode>();
	}

	public CTreeNode getRoot() {
		return root;
	}

	public void setRoot(CTreeNode root) {
		this.root = root;
	}

	public ArrayList<CTreeNode> getNewNodes() {
		ArrayList<CTreeNode> newNodes = new ArrayList<CTreeNode>();
		getLeafNodes();
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = (CTreeNode) leafNodes.get(i);
			if (leaf.getType() == 1) {
				newNodes.add(leaf);
			}
		}
		return newNodes;
	}

	/**
	 * here we alwalys reget the leaf nodes, because the leaf nodes may be
	 * changed by some methods.
	 * 
	 * @return
	 */
	public ArrayList<CTreeNode> getLeafNodes() {
		// TODO Auto-generated method stub
		leafNodes.clear();
		setLeafNodes(root);
		return leafNodes;
	}

	private void setLeafNodes(CTreeNode r) {
		if (r != null) {
			ArrayList<CTreeNode> children = r.getChild();
			if (children == null) {
				leafNodes.add(r);
			} else {
				for (int i = 0; i < children.size(); i++) {
					setLeafNodes((CTreeNode) children.get(i));
				}
			}
		}
	}

	/**
	 * �жϸ�ڵ㵽�½ڵ�֮��Ľڵ��Ƿ���½ڵ��ظ�
	 * 
	 * @param newNode
	 * @return
	 */
	public boolean markingRepeat(CTreeNode newNode) {
		// TODO Auto-generated method stub
		boolean repeat = false;
		Marking newNodeMark = newNode.getMarking();
		CTreeNode parent = newNode.getParent();
		while (parent != null) {
			Marking parentMark = parent.getMarking();
			if (newNodeMark.equals(parentMark)) {
				repeat = true;
				break;
			}
			parent = parent.getParent();
		}
		return repeat;
	}

	public void addNewEdge(CTreeNode newNode, CTreeNode newCreatedNode,
			MyPetriTransition petriTransition) {
		// TODO Auto-generated method stub
		String id1 = String.valueOf(newNode.getId());
		String id2 = String.valueOf(newCreatedNode.getId());
		String value = petriTransition.getName();
		StringBuffer sb = new StringBuffer();
		sb.append(id1 + " ");
		sb.append(id2);
		String key = sb.toString();
		edges.put(key, petriTransition);
	}

	public MyPetriTransition getEdge(CTreeNode from, CTreeNode to) {
		String id1 = String.valueOf(from.getId());
		String id2 = String.valueOf(to.getId());
		StringBuffer sb = new StringBuffer();
		sb.append(id1 + " ");
		sb.append(id2);
		String key = sb.toString();
		return (MyPetriTransition) edges.get(key);
	}

	public ArrayList<CTreeNode> getAllNodes() {
		allNodes.clear();
		preOrder(root);
		return allNodes;
	}

	private void preOrder(CTreeNode root) {
		if (root != null) {
			allNodes.add(root);
			ArrayList list = root.getChild();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					preOrder((CTreeNode) list.get(i));
				}
			}
		}
	}

	/**
	 * ������ȱ�����
	 * 
	 * @return
	 */
	public LinkedList<CTreeNode> bfs() {
		LinkedList<CTreeNode> result = new LinkedList<CTreeNode>();
		LinkedList<CTreeNode> list = new LinkedList<CTreeNode>();
		list.add(root);
		while (list.size() > 0) {
			CTreeNode node = (CTreeNode) list.remove();
			result.add(node);
			ArrayList<CTreeNode> child = node.getChild();
			if (child.size() > 0) {
				for (int i = 0; i < child.size(); i++) {
					list.add(child.get(i));
				}
			}
		}
		return result;
	}

	/**
	 * CT���Ĳ�����������Ҷ�ӽڵ㼯�ϵ���ڵ㼯��
	 * 
	 * @return
	 */
	public HashSet<CTreeNode> getDirectImageVertexsOfleafNodes() {
		HashSet<CTreeNode> set = null;
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = (CTreeNode) leafNodes.get(i);
			CTreeNode parent = leaf.getParent();
			while (parent != null) {
				if (parent.getMarking().lessOrEqualThan(leaf.getMarking())) {
					if (set == null) {
						set = new HashSet<CTreeNode>();
					}
					set.add(parent);
					break;
				}
				parent = parent.getParent();
			}
		}
		return set;
	}

	public CTreeNode getDirectImageVertexOfleafNode(CTreeNode leaf) {
		CTreeNode result = null;
		CTreeNode parent = leaf.getParent();
		while (parent != null) {
			if (parent.getMarking().lessOrEqualThan(leaf.getMarking())) {
				result = parent;
				break;
			}
			parent = parent.getParent();
		}
		return result;
	}

	/**
	 * CT���Ĳ�������ֽ�㼯��
	 * 
	 * @return
	 */
	public HashSet<CTreeNode> getCutOffVertexs(
			HashSet<CTreeNode> imageVertexsOfleafNodes) {
		HashSet<CTreeNode> set = new HashSet<CTreeNode>();
		set.add(root);
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = (CTreeNode) leafNodes.get(i);
			set.add(leaf);
		}
		if (imageVertexsOfleafNodes == null) {
			return set;
		}
		Iterator<CTreeNode> it = imageVertexsOfleafNodes.iterator();
		while (it.hasNext()) {
			CTreeNode imageVertexs = (CTreeNode) it.next();
			set.add(imageVertexs);
		}
		return set;
	}

	/**
	 * CT���Ĳ�����ȡ�۵㼯��
	 * 
	 * @return
	 */
	public HashSet<CTreeNode> getClusterVertexs() {
		HashSet<CTreeNode> set = null;
		this.getLeafNodes();
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = (CTreeNode) leafNodes.get(i);
			CTreeNode parent = leaf.getParent();
			while (parent != null) {
				if (parent.getMarking().containsW()
						&& parent.getMarking().equals(leaf.getMarking())) {
					if (set == null) {
						set = new HashSet<CTreeNode>();
					}
					set.add(parent);
				}
				parent = parent.getParent();
			}
		}
		/*
		 * if(set == null){ System.out.println("�۵�ĸ���: 0"); } else
		 * System.out.println("�۵�ĸ���: "+set.size());
		 */
		return set;
	}

	/**
	 * CT���Ĳ�������ȡ�ֽ��Լ�
	 * 
	 * @return
	 */
	public HashSet<GVBElement> getCutoffDotPairSet() {
		HashSet<GVBElement> set = null;
		this.getLeafNodes();
		HashSet<CTreeNode> imageVertexsOfleafNodes = getDirectImageVertexsOfleafNodes();
		HashSet<CTreeNode> columnElements = getCutOffVertexs(imageVertexsOfleafNodes);
		HashSet<CTreeNode> rowElements = new HashSet<CTreeNode>();
		rowElements.add(root);
		if (imageVertexsOfleafNodes != null) {
			rowElements.addAll(imageVertexsOfleafNodes);
		}
		Iterator<CTreeNode> it = columnElements.iterator();
		while (it.hasNext()) {
			CTreeNode node = (CTreeNode) it.next();
			CTreeNode parent = node.getParent();
			while (parent != null) {
				if (rowElements.contains(parent)) {
					GVBElement gvbE = new GVBElement(parent, node);
					if (set == null) {
						set = new HashSet();
					}
					set.add(gvbE);
					break;
				}
				parent = parent.getParent();
			}
		}
		return set;
	}

	/**
	 * ��ȡCRT���Ļ��̶�
	 * 
	 * @param cutOffPairSet
	 *            �ֽ��Լ���
	 * @param clusterVertexSet
	 *            �۵㼯��
	 * @return ����˳�������������䡢���ݡ���
	 */
	public LinkedList<BasicProcessSet> getBasicProcessOfPetrinet(
			HashSet<GVBElement> cutOffPairSet,
			HashSet<CTreeNode> clusterVertexSet) {
		LinkedList<BasicProcessSet> result = new LinkedList<BasicProcessSet>();
		BasicProcessSet bips = new BasicProcessSet("BasicIncreaseProcessSet");
		BasicProcessSet bdps = new BasicProcessSet("BasicDecreaseProcessSet");
		BasicProcessSet bupc = new BasicProcessSet("BasicUnchangingProcessSet");
		BasicProcessSet btps = new BasicProcessSet("BasicTransmitProcess");
		BasicProcessSet bops = new BasicProcessSet("BasicOpenProcessSet");
		result.add(bips);
		result.add(bdps);
		result.add(bupc);
		result.add(btps);
		result.add(bops);
		if (cutOffPairSet == null) {
			return result;
		}
		Iterator<?> it = cutOffPairSet.iterator();
		while (it.hasNext()) {
			GVBElement gvbE = (GVBElement) it.next();
			CTreeNode from = gvbE.getFrom();
			CTreeNode to = gvbE.getTo();
			if (to.getMarking().greaterThan(from.getMarking())) {
				bips.add(getBasicProcessBetweenVertexs(from, to));
			} else if (clusterVertexSet != null
					&& clusterVertexSet.contains(from)
					&& to.getMarking().lessThan(from.getMarking())) {
				bdps.add(getBasicProcessBetweenVertexs(from, to));
			} else if (clusterVertexSet != null
					&& (clusterVertexSet.contains(from) || clusterVertexSet
							.contains(to))
					&& !to.getMarking().equals(from.getMarking())
					&& !to.getMarking().lessThan(from.getMarking())
					&& !to.getMarking().greaterThan(from.getMarking())) {
				btps.add(getBasicProcessBetweenVertexs(from, to));
			} else if (leafNodes.contains(to)
					&& from.getMarking().equals(to.getMarking())) {
				bupc.add(getBasicProcessBetweenVertexs(from, to));
			} else {
				bops.add(getBasicProcessBetweenVertexs(from, to));
			}
		}

		return result;
	}

	/**
	 * CRT���Ĳ���,�õ��ڵ��Ļ��̶�
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public BasicProcess getBasicProcessBetweenVertexs(CTreeNode from,
			CTreeNode to) {
		Stack<MyPetriTransition> stack = new Stack<MyPetriTransition>();
		CTreeNode parent = to.getParent();
		CTreeNode child = to;
		MyPetriTransition transition = null;
		while (parent != null && parent != from) {
			transition = getEdge(parent, child);
			stack.add(transition);
			child = parent;
			parent = parent.getParent();
		}
		if (parent != null) {
			stack.add(getEdge(parent, child));
		}
		BasicProcess basicProcess = new BasicProcess();
		while (!stack.empty()) {
			transition = (MyPetriTransition) stack.pop();
			basicProcess.addTransition(transition);
		}
		return basicProcess;
	}

	public HashMap<String, MyPetriTransition> getAllEdges() {
		// TODO Auto-generated method stub
		return edges;
	}

	/**
	 * get the Ss set of ctree,the definition of Ss is in paper 3.0
	 * 
	 * @return
	 */
	public HashSet<GVBElement> getSs() {
		// TODO Auto-generated method stub
		HashSet<GVBElement> result = new HashSet<GVBElement>();
		if (leafNodes == null) {
			this.getLeafNodes();
		}
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = leafNodes.get(i);
			CTreeNode imageNode = getDirectImageVertexOfleafNode(leaf);
			if (imageNode == null) {
				GVBElement gvbElement = new GVBElement(root, leaf);
				result.add(gvbElement);
			} else {
				GVBElement gvbElement = null;
				// the imageNode and the root node may be equal.
				if (imageNode != root) {
					gvbElement = new GVBElement(root, imageNode);
					result.add(gvbElement);
				}
				for (int j = 0; j < leafNodes.size(); j++) {
					if (reachable(imageNode, leafNodes.get(j))) {
						gvbElement = new GVBElement(imageNode, leafNodes.get(j));
						result.add(gvbElement);
					}
				}
			}
		}
		return result;
	}

	/**
	 * used to judge whether there is a path from v1 to v2
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	private boolean reachable(CTreeNode v1, CTreeNode v2) {
		// TODO Auto-generated method stub
		if (v1 == v2)
			return false;
		else if (v1 == root) {
			return true;
		}
		CTreeNode parent = v2.getParent();
		while (parent != null && parent != root) {
			if (v1 == parent) {
				return true;
			}
			parent = parent.getParent();
		}

		return false;
	}
}
