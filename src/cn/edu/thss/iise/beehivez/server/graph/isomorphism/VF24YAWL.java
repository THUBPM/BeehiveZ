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
package cn.edu.thss.iise.beehivez.server.graph.isomorphism;

import java.util.ArrayList;

import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * use VF2 algorithm to check whether one YAWL model is a sub model of the other
 * YAWL model.
 * 
 * The VF2 algorithm is implemented according to "Luigi P. Cordella, Pasquale
 * Foggia, Carlo Sansone, Mario Vento: A (Sub)Graph Isomorphism Algorithm for
 * Matching Large Graphs. IEEE Trans. Pattern Anal. Mach. Intell. (PAMI)
 * 26(10):1367-1372 (2004)"
 * 
 * @author Tao Jin
 * 
 * @date 2012-3-5
 * 
 */
public class VF24YAWL {

	public static boolean subGraphIsomorphism(YNet sub, YNet sup) {
		ArrayList<YExternalNetElement> subElements = YAWLUtil
				.getNetElements(sub);
		ArrayList<YExternalNetElement> supElements = YAWLUtil
				.getNetElements(sup);

		int nSubElements = subElements.size();
		int nSupElements = supElements.size();

		if (nSubElements > nSupElements) {
			return false;
		}

		if (nSubElements == 0) {
			return true;
		}

		boolean[][] elementMapMatrix = new boolean[nSubElements][nSupElements];
		if (!YAWLUtil.initializeMap(elementMapMatrix, subElements, supElements)) {
			return false;
		}

		// use VF2 algorithm to check whether there is an one-one map.
		VF2SubState4YAWL s0 = new VF2SubState4YAWL(sub, sup, elementMapMatrix);
		int[] pn = new int[1];
		pn[0] = 0;
		int[] c1 = new int[nSubElements];
		int[] c2 = new int[nSubElements];

		return match(s0, pn, c1, c2);
	}

	/*-------------------------------------------------------------
	 * bool match(s0, pn, c1, c2)
	 * Finds a matching between two graph, if it exists, given the
	 * initial state of the matching process.
	 * Returns true a match has been found.
	 * pn[0] is assigned the number of matched nodes, and
	 * c1 and c2 will contain the ids of the corresponding nodes
	 * in the two graphs
	 ------------------------------------------------------------*/
	private static boolean match(VF2SubState4YAWL s0, int[] pn, int[] c1,
			int[] c2) {
		return match(pn, c1, c2, s0);
	}

	/*-------------------------------------------------------------
	 * static bool match(pn, c1, c2, s)
	 * Finds a matching between two graphs, if it exists, starting
	 * from state s.
	 * Returns true a match has been found.
	 * pn[0] is assigned the number of matched nodes, and
	 * c1 and c2 will contain the ids of the corresponding nodes
	 * in the two graphs.
	 ------------------------------------------------------------*/
	private static boolean match(int[] pn, int[] c1, int[] c2,
			VF2SubState4YAWL s) {
		if (s.isGoal()) {
			pn[0] = s.coreLen();
			s.getCoreSet(c1, c2);
			return true;
		}

		if (s.isDead())
			return false;

		int[] indexsub = new int[1], indexsup = new int[1];
		indexsub[0] = -1;
		indexsup[0] = -1;
		boolean found = false;
		while (!found
				&& s.nextPair(indexsub, indexsup, indexsub[0], indexsup[0])) {
			if (s.isFeasiblePair(indexsub[0], indexsup[0])) {
				VF2SubState4YAWL s1 = new VF2SubState4YAWL(s);
				s1.addPair(indexsub[0], indexsup[0]);
				found = match(pn, c1, c2, s1);
				s1.backTrack();
			}
		}
		return found;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
