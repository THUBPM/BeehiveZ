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
import java.util.Iterator;
import java.util.LinkedList;

import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcess;
import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcessSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTree;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTreeGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTreeNode;
import cn.edu.thss.iise.beehivez.server.basicprocess.ExecuteLog;
import cn.edu.thss.iise.beehivez.server.basicprocess.GVBElement;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

/**
 * we use the BTSGenerator class to generate the basic transition sequence
 * 
 * @author student
 * 
 */
public class BTSGenerator {
	// used to record the execute info
	private ExecuteLog log = null;

	public BTSGenerator(ExecuteLog log) {
		this.log = log;
	}

	public LinkedList<BasicProcessSet> getBTS(MyPetriNet myPetrinet) {
		// TODO Auto-generated method stub
		LinkedList<BasicProcessSet> result = new LinkedList<BasicProcessSet>();
		BasicProcessSet bips = new BasicProcessSet("BasicIncreaseProcessSet");
		BasicProcessSet bdps = new BasicProcessSet("BasicDecreaseProcessSet");
		BasicProcessSet bups = new BasicProcessSet("BasicUnchangingProcessSet");
		BasicProcessSet btps = new BasicProcessSet("BasicTransmitProcess");
		BasicProcessSet bops = new BasicProcessSet("BasicOpenProcessSet");
		result.add(bips);
		result.add(bdps);
		result.add(bups);
		result.add(btps);
		result.add(bops);
		CTreeGenerator generator = new CTreeGenerator(myPetrinet);
		CTree ctree = generator.generateCTree();
		log.writeCTree(ctree);
		HashSet<GVBElement> gvbes = ctree.getSs();
		log.writeCutOffDotPairSet(gvbes);
		if (gvbes.size() <= 0) {
			log.writeBPS(result);
			return result;
		}
		CTreeNode root = ctree.getRoot();
		ArrayList<CTreeNode> leafs = ctree.getLeafNodes();
		Iterator<GVBElement> it = gvbes.iterator();

		while (it.hasNext()) {
			GVBElement temp = (GVBElement) it.next();
			CTreeNode from = temp.getFrom();
			CTreeNode to = temp.getTo();
			if (from == root && leafs.contains(to)) {
				BasicProcess bp = ctree.getBasicProcessBetweenVertexs(from, to);
				int x[] = myPetrinet.getTVector(bp.getProcess());
				int cx[] = myPetrinet.getCX(x);
				if (equalsZero(cx)) {
					bups.add(bp);
				} else if (largerZero(cx)) {
					bips.add(bp);
				} else if (smallerZero(cx)) {
					bdps.add(bp);
				} else {
					if (from.getMarking().containsW()) {
						btps.add(bp);
					} else {
						bops.add(bp);
					}
				}
			} else if (from == root && !leafs.contains(to)) {
				BasicProcess bp = ctree.getBasicProcessBetweenVertexs(from, to);
				bops.add(bp);
			} else {
				BasicProcess bp = ctree.getBasicProcessBetweenVertexs(from, to);
				int x[] = myPetrinet.getTVector(bp.getProcess());
				int cx[] = myPetrinet.getCX(x);
				if (equalsZero(cx)) {
					bups.add(bp);
				} else if (largerZero(cx)) {
					bips.add(bp);
				} else if (smallerZero(cx)) {
					bdps.add(bp);
				} else {
					if (from.getMarking().containsW()) {
						btps.add(bp);
					} else {
						bops.add(bp);
					}
				}
			}
		}
		log.writeBPS(result);
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
