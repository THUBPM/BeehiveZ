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
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Element;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.resourcing.ResourceMap;

import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.MathUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * use the ullman subgraph isomorphism algorithm to test whether a given YAWL
 * model is the sub graph of the other one
 * 
 * implement ullman subgraph isomorphism algorithm according to "JR Ullmann, An
 * algorithm for subgraph isomorphism, Journal of the ACM (JACM), 1976"
 * 
 * @author Tao Jin
 * 
 */
public class Ullman4YAWL {

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

		// create map matrix from sub to sup,
		// record whether two nodes can be mapped
		boolean[][] elementMapMatrix = new boolean[nSubElements][nSupElements];
		for (int i = 0; i < nSubElements; i++) {
			for (int j = 0; j < nSupElements; j++) {
				elementMapMatrix[i][j] = false;
			}
		}

		// initialize the map matrix
		// there maybe some elements in sub model mapped into the same element
		// in sup model
		if (!YAWLUtil.initializeMap(elementMapMatrix, subElements, supElements)) {
			return false;
		}

		// refine the initial map matrix
		if (!refine(elementMapMatrix, subElements, supElements)) {
			return false;
		}

		// check whether all the arcs can be mapped according to the node map,
		// it's a problem of tree search
		return canGetInjectiveMap(elementMapMatrix, subElements, supElements);
	}

	// non recursive calls version
	private static boolean canGetInjectiveMap(boolean[][] elementMapMatrix,
			ArrayList<YExternalNetElement> subElements,
			ArrayList<YExternalNetElement> supElements) {

		int nSubElements = subElements.size();
		int[] subElementMapTo = new int[nSubElements];
		for (int i = 0; i < nSubElements; i++) {
			subElementMapTo[i] = -1;
		}
		int nSupElements = supElements.size();
		boolean[] supElementMapped = new boolean[nSupElements];
		for (int i = 0; i < nSupElements; i++) {
			supElementMapped[i] = false;
		}

		// store the mapping information during the mapping process
		// ArrayList<boolean[][]> eMaps = new ArrayList<boolean[][]>();
		HashMap<Integer, boolean[][]> eMaps = new HashMap<Integer, boolean[][]>();
		int depth = 0;
		eMaps.put(depth, elementMapMatrix);
		while (depth < nSubElements) {
			elementMapMatrix = MathUtil.twoDimensionalArrayClone(eMaps
					.get(depth));
			do {
				subElementMapTo[depth]++;
			} while (subElementMapTo[depth] < nSupElements
					&& (!elementMapMatrix[depth][subElementMapTo[depth]] || supElementMapped[subElementMapTo[depth]]));
			if (subElementMapTo[depth] >= nSupElements) {
				// cannot map
				subElementMapTo[depth] = -1;
				depth--;
				if (depth < 0) {
					return false;
				}
				if (subElementMapTo[depth] >= 0) {
					supElementMapped[subElementMapTo[depth]] = false;
				}
			} else {
				// can map
				// check the refine criterion
				int k = subElementMapTo[depth];
				for (int i = 0; i < nSupElements; i++) {
					if (i != k) {
						elementMapMatrix[depth][i] = false;
					}
				}
				supElementMapped[k] = true;
				if (refine(elementMapMatrix, subElements, supElements)) {
					depth++;
					eMaps.put(depth, elementMapMatrix);
				}
			}
		}

		return true;
	}

	// for a given element, check if the edge connected to it be preserved
	// during the mapping process
	private static boolean canEdgesPreserved(YExternalNetElement eSub,
			YExternalNetElement eSup, boolean[][] elementMapMatrix,
			ArrayList<YExternalNetElement> subElements,
			ArrayList<YExternalNetElement> supElements) {

		// check the input edge
		YExternalNetElement[] preESubs = eSub.getPresetElements().toArray(
				new YExternalNetElement[0]);
		YExternalNetElement[] preESups = eSup.getPresetElements().toArray(
				new YExternalNetElement[0]);

		if (preESubs.length > preESups.length) {
			return false;
		}

		if (preESubs.length > 0) {
			boolean[][] map = new boolean[preESubs.length][preESups.length];
			for (int i = 0; i < preESubs.length; i++) {
				boolean flagMap = false;
				for (int j = 0; j < preESups.length; j++) {
					int x = subElements.indexOf(preESubs[i]);
					int y = supElements.indexOf(preESups[j]);
					if (elementMapMatrix[x][y]) {
						map[i][j] = true;
						flagMap = true;
					} else {
						map[i][j] = false;
					}
				}
				if (!flagMap) {
					return false;
				}
			}
			if (!ToolKit.existOneOneMap(map)) {
				return false;
			}
		}

		// check the output edge
		YExternalNetElement[] postESubs = eSub.getPostsetElements().toArray(
				new YExternalNetElement[0]);
		YExternalNetElement[] postESups = eSup.getPostsetElements().toArray(
				new YExternalNetElement[0]);

		if (postESubs.length > postESups.length) {
			return false;
		}

		if (postESubs.length > 0) {
			boolean[][] map = new boolean[postESubs.length][postESups.length];
			for (int i = 0; i < postESubs.length; i++) {
				boolean flagMap = false;
				for (int j = 0; j < postESups.length; j++) {
					int x = subElements.indexOf(postESubs[i]);
					int y = supElements.indexOf(postESups[j]);
					if (elementMapMatrix[x][y]) {
						map[i][j] = true;
						flagMap = true;
					} else {
						map[i][j] = false;
					}
				}
				if (!flagMap) {
					return false;
				}
			}
			if (!ToolKit.existOneOneMap(map)) {
				return false;
			}
		}

		return true;

		// // check the input elements
		// Iterator<YExternalNetElement> itPreESub = eSub.getPresetElements()
		// .iterator();
		// while (itPreESub.hasNext()) {
		// YExternalNetElement ePreSub = itPreESub.next();
		// int x = subElements.indexOf(ePreSub);
		// boolean flagMap = false;
		// Iterator<YExternalNetElement> itPreESup = eSup.getPresetElements()
		// .iterator();
		// while (itPreESup.hasNext()) {
		// YExternalNetElement ePreSup = itPreESup.next();
		// int y = supElements.indexOf(ePreSup);
		// if (elementMapMatrix[x][y]) {
		// flagMap = true;
		// break;
		// }
		// }
		// if (!flagMap) {
		// return false;
		// }
		// }
		//
		// // check the output elements
		// Iterator<YExternalNetElement> itPostESub = eSub.getPostsetElements()
		// .iterator();
		// while (itPostESub.hasNext()) {
		// YExternalNetElement ePostSub = itPostESub.next();
		// int x = subElements.indexOf(ePostSub);
		// boolean flagMap = false;
		// Iterator<YExternalNetElement> itPostESup = eSup
		// .getPostsetElements().iterator();
		// while (itPostESup.hasNext()) {
		// YExternalNetElement ePostSup = itPostESup.next();
		// int y = supElements.indexOf(ePostSup);
		// if (elementMapMatrix[x][y]) {
		// flagMap = true;
		// break;
		// }
		// }
		// if (!flagMap) {
		// return false;
		// }
		// }
		//
		// return true;

	}

	// refine on the map matrix, try to change some true to false,
	// if some row contains only false, return false; otherwise return true;
	// if in one iteration, no true changed to false, terminated.
	// it is possible to find an one-one map, which means that there is at least
	// one 'true' for every row.
	private static boolean refine(boolean[][] elementMapMatrix,
			ArrayList<YExternalNetElement> subElements,
			ArrayList<YExternalNetElement> supElements) {
		boolean changed = false;
		do {
			changed = false;
			for (int i = 0; i < subElements.size(); i++) {
				boolean rowChanged = false;
				YExternalNetElement eSub = subElements.get(i);
				boolean remainUnchanged = false;
				for (int j = 0; j < supElements.size(); j++) {
					if (elementMapMatrix[i][j]) {
						YExternalNetElement eSup = supElements.get(j);
						if (!canEdgesPreserved(eSub, eSup, elementMapMatrix,
								subElements, supElements)) {
							elementMapMatrix[i][j] = false;
							changed = true;
							rowChanged = true;
						} else {
							remainUnchanged = true;
						}
					}
				}
				if (rowChanged && !remainUnchanged) {
					return false;
				}
			}
		} while (changed);
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		YNet a = YAWLUtil.getYNetFromFile("e:/test/a.yawl");
		YNet b = YAWLUtil.getYNetFromFile("e:/test/b.yawl");
		// YNet c = YAWLUtil.getYNetFromFile("e:/test/c.yawl");
		long s = System.currentTimeMillis();
		boolean ret = Ullman4YAWL.subGraphIsomorphism(a, b);
		long t = System.currentTimeMillis() - s;
		System.out.println("a is sub graph of b:");
		System.out.println(ret);
		System.out.println("time cost: " + t + "ms");
		// System.out.println("a is sub graph of c:");
		// System.out.println(Ullman4YAWL.subGraphIsomorphism(a, c));
		// System.out.println("b is sub graph of c:");
		// System.out.println(Ullman4YAWL.subGraphIsomorphism(b, c));
	}

}
