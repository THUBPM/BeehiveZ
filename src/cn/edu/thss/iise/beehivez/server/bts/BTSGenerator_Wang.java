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

package cn.edu.thss.iise.beehivez.server.bts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcess;
import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcessSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTree;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTreeGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTreeNode;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

/**
 * similarity measuring between petri nets basic on bts designed by mr wang.
 * 
 * @author
 * 
 */
public class BTSGenerator_Wang {

	public LinkedList<BasicProcessSet> getBTS(MyPetriNet myPetrinet) {
		// TODO Auto-generated method stub
		LinkedList<BasicProcessSet> result = new LinkedList<BasicProcessSet>();
		BasicProcessSet pbts = new BasicProcessSet("Primary BTS");
		BasicProcessSet fbts = new BasicProcessSet("Finite repeatable BTS");
		BasicProcessSet ibts = new BasicProcessSet("Infinite repeatable BTS");
		result.add(pbts);
		result.add(fbts);
		result.add(ibts);
		CTreeGenerator generator = new CTreeGenerator(myPetrinet);
		CTree ctree = generator.generateCTree();
		CTreeNode root = ctree.getRoot();
		/*
		 * HashSet<CTreeNode> anchors =
		 * ctree.getDirectImageVertexsOfleafNodes(); ArrayList<CTreeNode>
		 * leafNodes = ctree.getLeafNodes(); CTreeNode root = ctree.getRoot();
		 * Vector<GVBElement> innerResult = null; for (int i = 0; i <
		 * leafNodes.size(); i++) { CTreeNode leaf = leafNodes.get(i);
		 * innerResult = new Vector<GVBElement>(); CTreeNode preIndex = leaf;
		 * CTreeNode anchor = null; // judge whether exists anchor node from v0
		 * to vf boolean flag = false; if (anchors == null) {
		 * pbts.add(ctree.getBasicProcessBetweenVertexs(root, leaf)); } else {
		 * while ((anchor = getAnchorNode(preIndex, root, anchors)) != null) {
		 * GVBElement element = new GVBElement(anchor, preIndex);
		 * innerResult.add(element); preIndex = anchor; flag = true; } if (flag)
		 * { if(preIndex != root){ innerResult.add(new GVBElement(root,
		 * preIndex)); } } if (!flag) { innerResult.add(new GVBElement(root,
		 * leaf)); } if (leaf.getType() == 3) { for (int j = 0; j <
		 * innerResult.size(); j++) { GVBElement gvb = innerResult.get(j);
		 * BasicProcess bp = ctree.getBasicProcessBetweenVertexs( gvb.getFrom(),
		 * gvb.getTo()); pbts.add(bp); } } else { for (int j = 0; j <
		 * innerResult.size(); j++) { GVBElement gvb = innerResult.get(j);
		 * BasicProcess bp = ctree.getBasicProcessBetweenVertexs( gvb.getFrom(),
		 * gvb.getTo()); int x[] = myPetrinet.getTVector(bp.getProcess()); int
		 * cx[] = myPetrinet.getCX(x); if (smallerZero(cx)) { fbts.add(bp); }
		 * else if (largerZero(cx) || equalsZero(cx)) { ibts.add(bp); } else {
		 * pbts.add(bp); } } } } }
		 */
		ArrayList<CTreeNode> leafNodes = ctree.getLeafNodes();
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = leafNodes.get(i);
			if (leaf.getType() == 3) {
				pbts.add(ctree.getBasicProcessBetweenVertexs(root, leaf));
			} else {
				CTreeNode anchorNode = ctree
						.getDirectImageVertexOfleafNode(leaf);
				if (anchorNode != root) {
					BasicProcess bp1 = ctree.getBasicProcessBetweenVertexs(
							root, anchorNode);
					pbts.add(bp1);
				}
				BasicProcess bp2 = ctree.getBasicProcessBetweenVertexs(
						anchorNode, leaf);
				int x[] = myPetrinet.getTVector(bp2.getProcess());
				int cx[] = myPetrinet.getCX(x);
				if (smallerZero(cx)) {
					fbts.add(bp2);
				} else if (largerZero(cx) || equalsZero(cx)) {
					ibts.add(bp2);
				} else {
					pbts.add(bp2);
				}
			}
		}
		return result;
	}

	/**
	 * get the basic transition sequence from vo to vf, and add them to the
	 * basci process set
	 * 
	 * @param leaf
	 * @param root
	 * @param anchors
	 * @return
	 */
	/*
	 * private void cutAndAdd(CTreeNode leaf, CTreeNode root, HashSet<CTreeNode>
	 * anchors) { // TODO Auto-generated method stub Vector<GVBElement> result =
	 * new Vector<GVBElement>(); CTreeNode preIndex = leaf; CTreeNode anchor =
	 * null; // judge whether exists anchor node from v0 to vf boolean flag =
	 * false; if(anchors == null){
	 * pbts.add(ctree.getBasicProcessBetweenVertexs(root,leaf)); } else{ while
	 * ((anchor = getAnchorNode(preIndex, root, anchors)) != null) { GVBElement
	 * element = new GVBElement(anchor, preIndex); result.add(element); preIndex
	 * = anchor; flag = true; } if (flag) { result.add(new GVBElement(root,
	 * preIndex)); } if (!flag) { result.add(new GVBElement(root, leaf)); } if
	 * (leaf.getType() == 3) { for (int i = 0; i < result.size(); i++) {
	 * GVBElement gvb = result.get(i); BasicProcess bp =
	 * ctree.getBasicProcessBetweenVertexs(gvb .getFrom(), gvb.getTo());
	 * pbts.add(bp); } } else { for (int i = 0; i < result.size(); i++) {
	 * GVBElement gvb = result.get(i); BasicProcess bp =
	 * ctree.getBasicProcessBetweenVertexs(gvb .getFrom(), gvb.getTo()); int x[]
	 * = myPetrinet.getTVector(bp.getProcess()); int cx[] = myPetrinet.getCX(x);
	 * if (smallerZero(cx)) { fbts.add(bp); } else if (largerZero(cx) ||
	 * equalsZero(cx)) { ibts.add(bp); } else { pbts.add(bp); } } } } }
	 */

	private CTreeNode getAnchorNode(CTreeNode curNode, CTreeNode root,
			HashSet<CTreeNode> anchors) {
		CTreeNode parent = curNode.getParent();
		CTreeNode result = null;
		while (parent != null) {
			if (anchors.contains(parent)) {
				result = parent;
				break;
			}
			parent = parent.getParent();
		}
		return result;
	}

	private boolean smallerZero(int[] cx) {
		// TODO Auto-generated method stub
		for (int i = 0; i < cx.length; i++) {
			if (cx[i] > 0) {
				return false;
			}
		}
		if (equalsZero(cx)) {
			return false;
		}
		return true;
	}

	private boolean largerZero(int[] cx) {
		// TODO Auto-generated method stub
		for (int i = 0; i < cx.length; i++) {
			if (cx[i] < 0) {
				return false;
			}
		}
		if (equalsZero(cx)) {
			return false;
		}
		return true;
	}

	private boolean equalsZero(int[] cx) {
		// TODO Auto-generated method stub
		for (int i = 0; i < cx.length; i++) {
			if (cx[i] != 0) {
				return false;
			}
		}
		return true;
	}
}
