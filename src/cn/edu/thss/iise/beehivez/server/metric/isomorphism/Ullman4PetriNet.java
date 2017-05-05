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
 * use the idea of ullman's algorithm.
 * use the refine criterion, if (d,k) fails, set m(d,k) to 0
 * also use the refine criterion to check whether all edges preserved
 * the sequence of map process should be adjusted according to special case
 * operate on petri net
 * transitions' name make sense while places' name make no sense.
 * use the transitions' name and degree of nodes to get the initial map.
 */
package cn.edu.thss.iise.beehivez.server.metric.isomorphism;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.util.MathUtil;

/**
 * @author JinTao
 * 
 *         used for petri net subgraph isomorphism
 * 
 */
public class Ullman4PetriNet {

	public static boolean subGraphIsomorphism(PetriNet sub, PetriNet sup) {
		if (sub.getTransitions().size() > sup.getTransitions().size()
				|| sub.getPlaces().size() > sup.getPlaces().size()) {
			return false;
		}

		// create map matrix
		// record whether two nodes can be mapped
		int nTransitionSub = sub.getTransitions().size();
		int nTransitionSup = sup.getTransitions().size();
		int nPlaceSub = sub.getPlaces().size();
		int nPlaceSup = sup.getPlaces().size();
		boolean[][] transitionMapMatrix = new boolean[nTransitionSub][nTransitionSup];
		for (int i = 0; i < nTransitionSub; i++) {
			for (int j = 0; j < nTransitionSup; j++) {
				transitionMapMatrix[i][j] = false;
			}
		}
		boolean[][] placeMapMatrix = new boolean[nPlaceSub][nPlaceSup];
		for (int i = 0; i < nPlaceSub; i++) {
			for (int j = 0; j < nPlaceSup; j++) {
				placeMapMatrix[i][j] = false;
			}
		}

		// initialize the map matrix
		// there maybe some nodes in sub petri net mapped into the same node in
		// sup petri net
		if (!initialize(transitionMapMatrix, placeMapMatrix, sub, sup)) {
			return false;
		}

		// refine the initial map matrix
		if (!refine(transitionMapMatrix, placeMapMatrix, sub, sup)) {
			return false;
		}

		// check whether all the arcs can be mapped according to the transition
		// map and place map, it's a problem of tree search

		return canGetInjectiveMap(transitionMapMatrix, placeMapMatrix, sub, sup);
	}

	// if cannot map every node in sub to sup, return false;
	private static boolean initialize(boolean[][] transitionMapMatrix,
			boolean[][] placeMapMatrix, PetriNet sub, PetriNet sup) {
		// map transition according to transition name
		ArrayList<Transition> subTransitions = sub.getTransitions();
		ArrayList<Transition> supTransitions = sup.getTransitions();
		for (int i = 0; i < subTransitions.size(); i++) {
			boolean flagMap = false;
			for (int j = 0; j < supTransitions.size(); j++) {
				if (canTransitionMap(subTransitions.get(i), supTransitions
						.get(j))) {
					transitionMapMatrix[i][j] = true;
					flagMap = true;
				}
			}
			if (flagMap == false) {
				return false;
			}
		}

		// map place according to input and output transitions
		ArrayList<Place> subPlaces = sub.getPlaces();
		ArrayList<Place> supPlaces = sup.getPlaces();
		for (int i = 0; i < subPlaces.size(); i++) {
			boolean flagMap = false;
			for (int j = 0; j < supPlaces.size(); j++) {
				// according to the input and output transitions
				if (canPlaceMap(subPlaces.get(i), supPlaces.get(j))) {
					placeMapMatrix[i][j] = true;
					flagMap = true;
				}
			}
			if (flagMap == false) {
				return false;
			}
		}

		return true;
	}

	// can places map according to their input and output transitions
	// there maybe some transitions mapped into the same transition
	private static boolean canPlaceMap(Place pSub, Place pSup) {
		if (pSub.inDegree() > pSup.inDegree()
				|| pSub.outDegree() > pSup.outDegree()) {
			return false;
		}
		// can input transition map?
		Iterator itSub = pSub.getPredecessors().iterator();
		while (itSub.hasNext()) {
			Transition tSub = (Transition) itSub.next();
			boolean flagMap = false;
			Iterator itSup = pSup.getPredecessors().iterator();
			while (itSup.hasNext()) {
				Transition tSup = (Transition) itSup.next();
				if (canTransitionMap(tSub, tSup)) {
					flagMap = true;
					break;
				}
			}
			if (flagMap == false) {
				return false;
			}
		}

		// can output transition map?
		itSub = pSub.getSuccessors().iterator();
		while (itSub.hasNext()) {
			Transition tSub = (Transition) itSub.next();
			boolean flagMap = false;
			Iterator itSup = pSup.getSuccessors().iterator();
			while (itSup.hasNext()) {
				Transition tSup = (Transition) itSup.next();
				if (canTransitionMap(tSub, tSup)) {
					flagMap = true;
					break;
				}
			}
			if (flagMap == false) {
				return false;
			}
		}

		return true;
	}

	// can transitions map according to their name and their in degrees and out
	// degrees
	private static boolean canTransitionMap(Transition tSub, Transition tSup) {
		if (!tSub.getIdentifier().equals(tSup.getIdentifier())) {
			return false;
		}
		if (tSub.inDegree() > tSup.inDegree()
				|| tSub.outDegree() > tSup.outDegree()) {
			return false;
		}

		return true;
	}

	// can nodes map
	private static boolean canNodeMap(PNNode subNode, PNNode supNode) {
		if (subNode instanceof Transition && supNode instanceof Transition) {
			return canTransitionMap((Transition) subNode, (Transition) supNode);
		}

		if (subNode instanceof Place && supNode instanceof Place) {
			return canPlaceMap((Place) subNode, (Place) supNode);
		}

		return false;
	}

	// non recursive calls version
	// first consider the transitions, then the places. So the index of the
	// place should be handled according to depth
	private static boolean canGetInjectiveMap(boolean[][] transitionMapMatrix,
			boolean[][] placeMapMatrix, PetriNet sub, PetriNet sup) {

		int nPlaceSub = sub.getPlaces().size();
		int nPlaceSup = sup.getPlaces().size();
		int nTransitionSub = sub.getTransitions().size();
		int nTransitionSup = sup.getTransitions().size();

		// record transition map information
		int[] subTransitionMapTo = new int[nTransitionSub];
		for (int i = 0; i < nTransitionSub; i++) {
			subTransitionMapTo[i] = -1;
		}
		boolean[] supTransitionMapped = new boolean[nTransitionSup];
		for (int i = 0; i < nTransitionSup; i++) {
			supTransitionMapped[i] = false;
		}

		// record place map information
		int[] subPlaceMapTo = new int[nPlaceSub];
		for (int i = 0; i < nPlaceSub; i++) {
			subPlaceMapTo[i] = -1;
		}
		boolean[] supPlaceMapped = new boolean[nPlaceSup];
		for (int i = 0; i < nPlaceSup; i++) {
			supPlaceMapped[i] = false;
		}

		// to enhance the efficiency, visit the nodes in order of decreasing
		// degree
		// place index numbered as (place index places + nTransitions)
		Vector<Integer> vIndex = new Vector<Integer>();
		// deal with transition first and then place
		for (int i = 0; i < nTransitionSub; i++) {
			Transition t = sub.getTransitions().get(i);
			int j = 0;
			for (j = 0; j < vIndex.size(); j++) {
				PNNode node;
				int index = vIndex.get(j).intValue();
				if (index < nTransitionSub) {
					// transition
					node = sub.getTransitions().get(index);
				} else {
					// place
					index = index - nTransitionSub;
					node = sub.getPlaces().get(index);
				}
				if (t.inDegree() + t.outDegree() > node.inDegree()
						+ node.outDegree()) {
					break;
				}
			}
			vIndex.add(j, i);
		}
		for (int i = 0; i < nPlaceSub; i++) {
			Place p = sub.getPlaces().get(i);
			int j = 0;
			for (j = 0; j < vIndex.size(); j++) {
				int index = vIndex.get(j).intValue();
				PNNode node;
				if (index < nTransitionSub) {
					// transition
					node = sub.getTransitions().get(index);
				} else {
					// place
					index = index - nTransitionSub;
					node = sub.getPlaces().get(index);
				}
				if (p.inDegree() + p.outDegree() > node.inDegree()
						+ node.outDegree()) {
					break;
				}
			}
			vIndex.add(j, i + nTransitionSub);
		}

		// store the map matrix during the mapping process
		Vector<boolean[][]> vTMap = new Vector<boolean[][]>();
		Vector<boolean[][]> vPMap = new Vector<boolean[][]>();

		// record when tree search
		// depth refer to the index of vIndex
		int depth = 0;
		vTMap.add(depth, transitionMapMatrix);
		vPMap.add(depth, placeMapMatrix);
		while (depth < vIndex.size()) {
			transitionMapMatrix = MathUtil.twoDimensionalArrayClone(vTMap
					.get(depth));
			placeMapMatrix = MathUtil
					.twoDimensionalArrayClone(vPMap.get(depth));
			int index = vIndex.get(depth).intValue();
			if (index < nTransitionSub) {
				// deal with transition map
				if (subTransitionMapTo[index] >= 0) {
					supTransitionMapped[subTransitionMapTo[index]] = false;
				}
				do {
					subTransitionMapTo[index]++;
				} while (subTransitionMapTo[index] < nTransitionSup
						&& (!transitionMapMatrix[index][subTransitionMapTo[index]] || supTransitionMapped[subTransitionMapTo[index]]));
				if (subTransitionMapTo[index] >= nTransitionSup) {
					// cannot map
					subTransitionMapTo[index] = -1;
					depth--;
					if (depth < 0) {
						return false;
					}
				} else {
					// can map
					// check use the refine criterion
					int d = index;
					int k = subTransitionMapTo[d];
					for (int i = 0; i < nTransitionSup; i++) {
						if (i != k) {
							transitionMapMatrix[d][i] = false;
						}
					}
					if (!refine(transitionMapMatrix, placeMapMatrix, sub, sup)) {
						// not map
					} else {
						// map, continue mapping process
						depth++;
						vTMap.add(depth, transitionMapMatrix);
						vPMap.add(depth, placeMapMatrix);
					}
				}
			} else if (index < nTransitionSub + nPlaceSub) {
				// deal with place map
				// get the index of place
				int indexPlace = index - nTransitionSub;
				if (subPlaceMapTo[indexPlace] >= 0) {
					supPlaceMapped[subPlaceMapTo[indexPlace]] = false;
				}
				do {
					subPlaceMapTo[indexPlace]++;
				} while (subPlaceMapTo[indexPlace] < nPlaceSup
						&& (!placeMapMatrix[indexPlace][subPlaceMapTo[indexPlace]] || supPlaceMapped[subPlaceMapTo[indexPlace]]));
				if (subPlaceMapTo[indexPlace] >= nPlaceSup) {
					// cannot map
					subPlaceMapTo[indexPlace] = -1;
					depth--;
				} else {
					// can map
					int d = indexPlace;
					int k = subPlaceMapTo[d];
					for (int i = 0; i < nPlaceSup; i++) {
						if (i != k) {
							placeMapMatrix[d][i] = false;
						}
					}
					if (!refine(transitionMapMatrix, placeMapMatrix, sub, sup)) {
						// not map
					} else {
						// can map, continue the mapping process
						depth++;
						vTMap.add(depth, transitionMapMatrix);
						vPMap.add(depth, placeMapMatrix);
					}
				}
			}
		}
		return true;
	}

	// for a given node, check if the edge connected to it be preserved during
	// the mapping process
	private static boolean canEdgesPreserved(PNNode subNode, PNNode supNode,
			boolean[][] transitionMapMatrix, boolean[][] placeMapMatrix,
			PetriNet sub, PetriNet sup) {
		// check according to the type of node
		ArrayList<Place> subPlaces = sub.getPlaces();
		ArrayList<Transition> subTransitions = sub.getTransitions();
		ArrayList<Place> supPlaces = sup.getPlaces();
		ArrayList<Transition> supTransitions = sup.getTransitions();

		if (subNode instanceof Place && supNode instanceof Place) {
			Place subPlace = (Place) subNode;
			Place supPlace = (Place) supNode;

			Iterator itPreSub = subPlace.getPredecessors().iterator();
			while (itPreSub.hasNext()) {
				Transition tPreSub = (Transition) itPreSub.next();
				int x = subTransitions.indexOf(tPreSub);
				boolean flagMap = false;
				Iterator itPreSup = supPlace.getPredecessors().iterator();
				while (itPreSup.hasNext()) {
					Transition tPreSup = (Transition) itPreSup.next();
					int y = supTransitions.indexOf(tPreSup);
					if (transitionMapMatrix[x][y]) {
						flagMap = true;
						break;
					}
				}
				if (!flagMap) {
					return false;
				}
			}

			Iterator itSucSub = subPlace.getSuccessors().iterator();
			while (itSucSub.hasNext()) {
				Transition tSucSub = (Transition) itSucSub.next();
				int x = subTransitions.indexOf(tSucSub);
				boolean flagMap = false;
				Iterator itSucSup = supPlace.getSuccessors().iterator();
				while (itSucSup.hasNext()) {
					Transition tSucSup = (Transition) itSucSup.next();
					int y = supTransitions.indexOf(tSucSup);
					if (transitionMapMatrix[x][y]) {
						flagMap = true;
						break;
					}
				}
				if (!flagMap) {
					return false;
				}
			}
			return true;
		} else if (subNode instanceof Transition
				&& supNode instanceof Transition) {
			Transition tSub = (Transition) subNode;
			Transition tSup = (Transition) supNode;
			Iterator itPreSub = tSub.getPredecessors().iterator();
			while (itPreSub.hasNext()) {
				Place pPreSub = (Place) itPreSub.next();
				int x = subPlaces.indexOf(pPreSub);
				boolean flagMap = false;
				Iterator itPreSup = tSup.getPredecessors().iterator();
				while (itPreSup.hasNext()) {
					Place pPreSup = (Place) itPreSup.next();
					int y = supPlaces.indexOf(pPreSup);
					if (placeMapMatrix[x][y]) {
						flagMap = true;
						break;
					}
				}
				if (!flagMap) {
					return false;
				}
			}

			Iterator itSucSub = tSub.getSuccessors().iterator();
			while (itSucSub.hasNext()) {
				Place pSucSub = (Place) itSucSub.next();
				int x = subPlaces.indexOf(pSucSub);
				boolean flagMap = false;
				Iterator itSucSup = tSup.getSuccessors().iterator();
				while (itSucSup.hasNext()) {
					Place pSucSup = (Place) itSucSup.next();
					int y = supPlaces.indexOf(pSucSup);
					if (placeMapMatrix[x][y]) {
						flagMap = true;
						break;
					}
				}
				if (!flagMap) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// refine on the map matrix, try to change some 1 to 0,
	// if some row contains only 0, return false; otherwise return true;
	// if in one iteration, no 1 changed to 0, terminated
	private static boolean refine(boolean[][] transitionMapMatrix,
			boolean[][] placeMapMatrix, PetriNet sub, PetriNet sup) {

		ArrayList<Transition> subTransitions = sub.getTransitions();
		ArrayList<Transition> supTransitions = sup.getTransitions();
		ArrayList<Place> subPlaces = sub.getPlaces();
		ArrayList<Place> supPlaces = sup.getPlaces();
		boolean changedT = false;
		boolean changedP = false;
		do {
			changedT = false;
			changedP = false;
			// deal with transitions first
			for (int i = 0; i < subTransitions.size(); i++) {
				Transition tSub = subTransitions.get(i);
				for (int j = 0; j < supTransitions.size(); j++) {
					if (transitionMapMatrix[i][j]) {
						Transition tSup = supTransitions.get(j);
						if (!canEdgesPreserved(tSub, tSup, transitionMapMatrix,
								placeMapMatrix, sub, sup)) {
							transitionMapMatrix[i][j] = false;
							changedT = true;
						}
					}
				}

				// check if the row only contains 0
				if (changedT) {
					boolean cont = false;
					for (int j = 0; j < supTransitions.size(); j++) {
						if (transitionMapMatrix[i][j]) {
							cont = true;
							break;
						}
					}
					if (!cont) {
						return false;
					}
				}
			}

			// deal with places
			for (int i = 0; i < subPlaces.size(); i++) {
				Place pSub = subPlaces.get(i);
				for (int j = 0; j < supPlaces.size(); j++) {
					if (placeMapMatrix[i][j]) {
						Place pSup = supPlaces.get(j);
						if (!canEdgesPreserved(pSub, pSup, transitionMapMatrix,
								placeMapMatrix, sub, sup)) {
							placeMapMatrix[i][j] = false;
							changedP = true;
						}
					}
				}
				if (changedP) {
					boolean cont = false;
					for (int j = 0; j < supPlaces.size(); j++) {
						if (placeMapMatrix[i][j]) {
							cont = true;
							break;
						}
					}
					if (!cont) {
						return false;
					}
				}
			}
		} while (changedT || changedP);
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// boolean[][] a = {{true, false},{true, false}};
		// boolean[][] b = MathUtil.twoDimensionalArrayClone(a);
		// b[1][1] = true;
		// System.out.println("a");
		// for (int i = 0; i < 2; i++) {
		// for (int j =0; j < 2; j++) {
		// System.out.print(a[i][j]);
		// }
		// }
		// System.out.println("\nb");
		// for (int i = 0; i < 2; i++) {
		// for (int j =0; j < 2; j++) {
		// System.out.print(b[i][j]);
		// }
		// }
		// TODO Auto-generated method stub
		PnmlImport pi = new PnmlImport();
		try {
			FileInputStream fina = new FileInputStream("e:/a.pnml");
			PetriNet pna = ((PetriNetResult) pi.importFile(fina)).getPetriNet();
			FileInputStream finb = new FileInputStream("e:/b.pnml");
			PetriNet pnb = ((PetriNetResult) pi.importFile(finb)).getPetriNet();
			FileInputStream finc = new FileInputStream("e:/c.pnml");
			PetriNet pnc = ((PetriNetResult) pi.importFile(finc)).getPetriNet();

			long s = System.currentTimeMillis();
			boolean ret = Ullman4PetriNet.subGraphIsomorphism(pna, pna);
			long e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pna pna sub graph matched");
			}
			System.out.println("time cose: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Ullman4PetriNet.subGraphIsomorphism(pnb, pnb);
			e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pnb pnb sub graph matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Ullman4PetriNet.subGraphIsomorphism(pnc, pnc);
			e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pnc pnc sub graph matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Ullman4PetriNet.subGraphIsomorphism(pna, pnb);
			e = System.currentTimeMillis();
			if (!ret) {
				System.out.println("pna  pnb sub graph not matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Ullman4PetriNet.subGraphIsomorphism(pnb, pnc);
			e = System.currentTimeMillis();
			if (!ret) {
				System.out.println("pnb  pnc sub graph not matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Ullman4PetriNet.subGraphIsomorphism(pna, pnc);
			e = System.currentTimeMillis();
			if (!ret) {
				System.out.println("pna  pnc sub graph not matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
