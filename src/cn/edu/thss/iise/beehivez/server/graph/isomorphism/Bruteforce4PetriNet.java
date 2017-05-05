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
 * use the tree search method
 * travel all the pathes in the tree search space
 * operate on petri net
 * transitions' name make sense while places' name make no sense.
 * use the transitions' name and degree of nodes to get the initial map.
 * 
 * get one-to-one map between nodes and then check whether corresponding edge preserved
 */
package cn.edu.thss.iise.beehivez.server.graph.isomorphism;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

/**
 * @author JinTao
 * 
 *         used for petri net subgraph isomorphism
 * 
 */
public class Bruteforce4PetriNet {

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

		// check whether all the arcs can be mapped according to the transition
		// map and place map, it's a problem of tree search

		return canGetInjectiveMap(transitionMapMatrix, placeMapMatrix, sub, sup);
	}

	// check whether all edges preserved
	private static boolean canAllArcsMapped(PetriNet sub, PetriNet sup,
			int[] subTransitionMapTo, int[] subPlaceMapTo) {
		ArrayList<PNEdge> subEdges = (ArrayList<PNEdge>) sub.getEdges();
		for (int i = 0; i < subEdges.size(); i++) {
			PNEdge subEdge = subEdges.get(i);
			if (subEdge.isPT()) {
				Place pSub = (Place) subEdge.getSource();
				Transition tSub = (Transition) subEdge.getDest();
				int index = subPlaceMapTo[sub.getPlaces().indexOf(pSub)];
				if (index < 0) {
					return false;
				}
				Place pSup = sup.getPlaces().get(index);
				index = subTransitionMapTo[sub.getTransitions().indexOf(tSub)];
				if (index < 0) {
					return false;
				}
				Transition tSup = sup.getTransitions().get(index);
				if (sup.getEdgesBetween(pSup, tSup).size() <= 0) {
					return false;
				}
			} else if (subEdge.isTP()) {
				Transition tSub = (Transition) subEdge.getSource();
				Place pSub = (Place) subEdge.getDest();
				int index = subPlaceMapTo[sub.getPlaces().indexOf(pSub)];
				if (index < 0) {
					return false;
				}
				Place pSup = sup.getPlaces().get(index);
				index = subTransitionMapTo[sub.getTransitions().indexOf(tSub)];
				if (index < 0) {
					return false;
				}
				Transition tSup = sup.getTransitions().get(index);
				if (sup.getEdgesBetween(tSup, pSup).size() <= 0) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
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

		// record when tree search
		int depth = 0;
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

		do {
			if (depth < nTransitionSub) {
				// deal with transition map
				if (subTransitionMapTo[depth] >= 0) {
					supTransitionMapped[subTransitionMapTo[depth]] = false;
				}
				do {
					subTransitionMapTo[depth]++;
				} while (subTransitionMapTo[depth] < nTransitionSup
						&& (!transitionMapMatrix[depth][subTransitionMapTo[depth]] || supTransitionMapped[subTransitionMapTo[depth]]));
				if (subTransitionMapTo[depth] >= nTransitionSup) {
					// cannot map
					subTransitionMapTo[depth] = -1;
					depth--;
					if (depth < 0) {
						return false;
					}
				} else {
					// can map
					supTransitionMapped[subTransitionMapTo[depth]] = true;
					depth++;
				}
			} else if (depth < nTransitionSub + nPlaceSub) {
				// deal with place map
				// get the index of place
				int indexPlace = depth - nTransitionSub;
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
					supPlaceMapped[subPlaceMapTo[indexPlace]] = true;
					depth++;
				}

			} else {
				// check whether all edges can be preserved
				if (canAllArcsMapped(sub, sup, subTransitionMapTo,
						subPlaceMapTo)) {
					return true;
				} else {
					// back track
					depth--;
				}
			}
		} while (true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			boolean ret = Bruteforce4PetriNet.subGraphIsomorphism(pna, pna);
			long e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pna pna sub graph matched");
			}
			System.out.println("time cose: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Bruteforce4PetriNet.subGraphIsomorphism(pnb, pnb);
			e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pnb pnb sub graph matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Bruteforce4PetriNet.subGraphIsomorphism(pnc, pnc);
			e = System.currentTimeMillis();
			if (ret) {
				System.out.println("pnc pnc sub graph matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Bruteforce4PetriNet.subGraphIsomorphism(pna, pnb);
			e = System.currentTimeMillis();
			if (!ret) {
				System.out.println("pna  pnb sub graph not matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Bruteforce4PetriNet.subGraphIsomorphism(pnb, pnc);
			e = System.currentTimeMillis();
			if (!ret) {
				System.out.println("pnb  pnc sub graph not matched");
			}
			System.out.println("time cost: " + (e - s) + " ms");

			s = System.currentTimeMillis();
			ret = Bruteforce4PetriNet.subGraphIsomorphism(pna, pnc);
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
