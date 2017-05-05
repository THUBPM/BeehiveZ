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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.Marking;
import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.State;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.InitialPlaceMarker;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.spsd.SimpleParallelStructure;
import cn.edu.thss.iise.beehivez.server.spsd.SimpleParallelStructureDetector;
import cn.edu.thss.iise.beehivez.server.spsd.TokenTree;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * build the rltMatrix with simple parallel structure detector
 * 
 * @author zhougz 2010.03.31
 * 
 */
public class RltMatrixBuilderWithSPSD implements RltConstants {
	private static int numberOfFire = 0;
	private static long process_id = 0;

	public synchronized static RltMatrix build(PetriNet net, long id) {
		// The assumption here is that this is a marked PetriNet
		numberOfFire = 0;
		process_id = id;

		Map<Transition, SimpleParallelStructure> spsMap = getAllSPS(net);
		if (spsMap.size() > 0)
			foldSPS(net, spsMap);

		InitialPlaceMarker.mark(net, 1);
		RltMatrix rltMatrix = new RltMatrix(net);
		State s = new State(rltMatrix);
		// Create the new state
		Iterator it2 = net.getPlaces().iterator();
		while (it2.hasNext()) {
			Place p = (Place) it2.next();
			if (p.getNumberOfTokens() > 0) {
				s.addPlace(p, p.getNumberOfTokens());
			}
		}
		rltMatrix.addState(s);
		rltMatrix.setStartState(s);
		HashSet newNodes = new HashSet();
		newNodes.add(s);
		do {
			extendrltMatrix(net, rltMatrix, newNodes);
		} while (!newNodes.isEmpty());
		if (rltMatrix == null)
			return null;
		// System.out.println("\nAfter Fill PARALLEL and DIRSUCC");
		// rltMatrix.print();
		if (spsMap.size() > 0)
			unfoldSPS(net, rltMatrix, spsMap);
		// rltMatrix.printFinal();
		rltMatrix.fillCycleRlt();
		// System.out.println("\nAfter Fill CYCLE");
		// rltMatrix.print();
		// rltMatrix.fillDirectSucc();
		// System.out.println("\nAfter Fill DIRSUCC");
		// rltMatrix.print();
		rltMatrix.fillIndirectSucc();
		// System.out.println("\nAfter Fill INDIRSUCC");
		// rltMatrix.print();
		rltMatrix.fillDirectCasual();
		// System.out.println("\nAfter Fill DIRCASUAL");
		// rltMatrix.print();
		rltMatrix.fillIndirectCasual();
		// System.out.println("\nAfter Fill INDIRCASUAL");
		// rltMatrix.print();
		rltMatrix.fillMutex();
		// System.out.println("\nAfter Fill MUTEX");
		// rltMatrix.print();
		rltMatrix.mergeBySameLabel();
		// System.out.println("\nAfter Merge Label");
		// rltMatrix.printFinal();
		return rltMatrix;
	}

	private static synchronized void extendrltMatrix(PetriNet net,
			RltMatrix rltMatrixSoFar, HashSet newNodes) {

		// Chose a state.
		State state = (State) newNodes.iterator().next();
		// set the state of this Petri net to state
		InitialPlaceMarker.mark(net, 0);
		Iterator it = state.iterator();
		while (it.hasNext()) {
			Place p = (Place) it.next();
			for (int i = 0; i < state.getOccurances(p); i++) {
				p.addToken(new Token());
			}
		}

		Marking sinkMarking = new Marking();
		ModelGraphVertex sink = net.getSink();
		if (sink instanceof Place) {
			sinkMarking.addPlace((Place) sink, 1);
		}

		it = net.getTransitions().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();

			if (t.isEnabled()) {
				// 只有token数大于2的时候才考虑并行关系，否则不存在并行
				if (state.getTokenCount() > 1) {
					// Do fork fire in order to fill parallel relationship
					// relate to current transition
					Transition candicateTransition = null;
					int curTransitionIndex = rltMatrixSoFar
							.getTransitionIndex(t);
					int rltTransitionIndex = -1;
					byte curRltValue = 0;

					t.forkFire();
					Iterator<Transition> itTransitions = net.getTransitions()
							.iterator();
					while (itTransitions.hasNext()) {
						candicateTransition = (Transition) itTransitions.next();
						rltTransitionIndex = rltMatrixSoFar
								.getTransitionIndex(candicateTransition);
						if (!rltMatrixSoFar.isRltParallel(curTransitionIndex,
								rltTransitionIndex)
								&& candicateTransition.isEnabled()) {
							rltMatrixSoFar.setRltParallel(curTransitionIndex,
									rltTransitionIndex);
						}
					}
					t.forkUnfire();
				}
				// do true fire to caculate the reachability graph
				t.fireQuick();
				numberOfFire++;
				// skip process that has more than 10 parallel transitions
				if (numberOfFire > 1024) {
					System.out
							.println("Petri Net "
									+ process_id
									+ " fireed more than 1024 times. Delete process!!!");
					newNodes.clear();
					DataManager.getInstance().delProcess(process_id);
					rltMatrixSoFar = null;
					return;
				}

				Iterator<Transition> itt = net.getTransitions().iterator();
				while (itt.hasNext()) {
					Transition curt = itt.next();
					if (curt.isEnabled()) {
						rltMatrixSoFar.setRltDirSucc(rltMatrixSoFar
								.getTransitionIndex(t), rltMatrixSoFar
								.getTransitionIndex(curt));
					}

				}

				State s = new State(rltMatrixSoFar);
				// Create the new state
				Iterator it2 = net.getPlaces().iterator();
				while (it2.hasNext()) {
					Place p = (Place) it2.next();
					if (p.getNumberOfTokens() > 0) {
						s.addPlace(p, p.getNumberOfTokens());
					}
				}
				// check whether the graph contains this state
				int i = rltMatrixSoFar.getVerticeList().indexOf(s);
				if (i != -1) {
					s = (State) rltMatrixSoFar.getVerticeList().get(i);
					FSMTransition e = new FSMTransition(state, s, t
							.getIdentifier());
					rltMatrixSoFar.addEdge(e);
					e.object = t;
				} else {
					rltMatrixSoFar.addState(s);
					// HV
					if (s.getMarking().equals(sinkMarking)) {
						rltMatrixSoFar.addAcceptState(s);
					}
					FSMTransition e = new FSMTransition(state, s, t
							.getIdentifier());
					rltMatrixSoFar.addEdge(e);
					e.object = t;
					newNodes.add(s);
				}
				t.unFireQuick();
			}
		}
		newNodes.remove(state);
	}

	private static void foldSPS(PetriNet net,
			Map<Transition, SimpleParallelStructure> spsMap) {
		for (Map.Entry<Transition, SimpleParallelStructure> spsEntry : spsMap
				.entrySet()) {
			SimpleParallelStructure sps = spsEntry.getValue();
			ArrayList test = net.getEdges();

			Transition joinnode = sps.getJoin();
			Transition forknode = sps.getFork();
			forknode.object2 = sps;

			ArrayList joinOutEdges = joinnode.getOutEdges();
			for (int i = 0; i < joinOutEdges.size(); i++) {
				PNEdge e = (PNEdge) joinOutEdges.get(i);
				PNEdge newEdge = new PNEdge(forknode, (Place) e.getDest());
				net.addEdge(newEdge);
			}

			Set<Transition> transitions = sps.getAllTransitions();

			Iterator<Transition> itTransition = transitions.iterator();
			while (itTransition.hasNext()) {
				net.delTransition((itTransition.next()));
			}

			Set<Place> places = sps.getAllPlaces();
			Iterator<Place> itPlace = places.iterator();
			while (itPlace.hasNext()) {
				net.delPlace(itPlace.next());
			}

		}
	}

	private static void unfoldSPS(PetriNet net, RltMatrix matrix,
			Map<Transition, SimpleParallelStructure> spsMap) {

		Map<Integer, Transition> newInt2Transition = new HashMap<Integer, Transition>();
		// init matrix
		BasicRltMatrix newbrm = new BasicRltMatrix();
		newbrm.transition2int = new HashMap<Transition, Integer>();
		Set<Transition> allTransitions = new HashSet<Transition>();
		Set<Transition> transitionsInSPS = new HashSet<Transition>();
		allTransitions.addAll(net.getTransitions());
		Iterator<SimpleParallelStructure> itSPS1 = spsMap.values().iterator();
		SimpleParallelStructure curSPS1 = null;
		while (itSPS1.hasNext()) {
			curSPS1 = itSPS1.next();
			allTransitions.addAll(curSPS1.getAllTransitions());
			transitionsInSPS.addAll(curSPS1.getAllTransitions());
			transitionsInSPS.add(curSPS1.getFork());
		}
		Iterator<Transition> itTransition = allTransitions.iterator();

		int index = 0;
		while (itTransition.hasNext()) {
			Transition t = itTransition.next();
			newbrm.transition2int.put(t, index);
			newInt2Transition.put(index, t);
			index++;
		}
		itTransition = null;
		allTransitions = null;
		newbrm.transitionNum = index;
		newbrm.rltMatrix = new byte[index][index];

		// revert old transition2int map to int2transition map
		Map<Integer, Transition> oldInt2Transition = new HashMap<Integer, Transition>();
		for (Map.Entry<Transition, Integer> oldTran2Int : matrix.brm.transition2int
				.entrySet()) {
			oldInt2Transition.put(oldTran2Int.getValue(), oldTran2Int.getKey());
		}

		// copy relation that not do no business with sps
		for (int i = 0; i < index; i++)
			for (int j = 0; j < index; j++) {
				newbrm.rltMatrix[i][j] = 0;
				Transition rowTran = newInt2Transition.get(i);
				Transition colTran = newInt2Transition.get(j);
				if (!transitionsInSPS.contains(rowTran)
						&& !transitionsInSPS.contains(colTran)) {
					newbrm.rltMatrix[i][j] = matrix.brm
							.getRlt(rowTran, colTran);
				}
			}

		// do unfold
		// TODO:optimise method, by translate branches to index in advance,
		// in order to avoid doing transition to index transform each time .
		for (Map.Entry<Transition, SimpleParallelStructure> spsEntry : spsMap
				.entrySet()) {
			SimpleParallelStructure sps = spsEntry.getValue();
			Vector<TokenTree> branches = sps.getBranches().getChildren();
			int oldIndexOfFork = matrix.getTransitionIndex(sps.getFork());
			int newIndexOfFork = newbrm.getTransitionIndex(sps.getFork());
			int newIndexofJoin = newbrm.getTransitionIndex(sps.getJoin());
			// unfold || and > relation in sps
			for (int i = 0; i < branches.size(); i++) {
				Vector<Transition> sequenceRow = branches.get(i).getSequence();
				// write dirsucc in same branch
				for (int indexInRow = 0; indexInRow < sequenceRow.size() - 1; indexInRow++) {
					newbrm.setRltDirSucc(sequenceRow.get(indexInRow),
							sequenceRow.get(indexInRow + 1));
				}
				// write dirsucc between forknode and first node of branche
				newbrm.setRltDirSucc(newIndexOfFork, newbrm
						.getTransitionIndex(sequenceRow.get(0)));
				// write dirsucc between last node of branche and joinnode
				newbrm.setRltDirSucc(newbrm.getTransitionIndex(sequenceRow
						.get(sequenceRow.size() - 1)), newIndexofJoin);

				for (int indexInRow = 0; indexInRow < sequenceRow.size(); indexInRow++) {
					for (int j = i + 1; j < branches.size(); j++) {
						Vector<Transition> sequenceCol = branches.get(j)
								.getSequence();
						for (int indexInCol = 0; indexInCol < sequenceCol
								.size(); indexInCol++) {
							newbrm.setRltParallel(sequenceRow.get(indexInRow),
									sequenceCol.get(indexInCol));
							newbrm.setRltParallel(sequenceCol.get(indexInCol),
									sequenceRow.get(indexInRow));
							newbrm.setRltDirSucc(sequenceRow.get(indexInRow),
									sequenceCol.get(indexInCol));
							newbrm.setRltDirSucc(sequenceCol.get(indexInCol),
									sequenceRow.get(indexInRow));
						}
					}
				}
			}

			Set<Transition> spsTransitions = sps.getAllTransitions();
			spsTransitions.add(sps.getFork());
			for (int oldTransitionIndex = 0; oldTransitionIndex < matrix.brm.transitionNum; oldTransitionIndex++) {
				// parallel
				if (matrix.isRltParallel(oldTransitionIndex, oldIndexOfFork)) {
					Iterator<Transition> it = spsTransitions.iterator();
					int newTransitionIndex = newbrm
							.getTransitionIndex(oldInt2Transition
									.get(oldTransitionIndex));
					while (it.hasNext()) {
						Transition t = it.next();
						int curTransitionIndex = newbrm.getTransitionIndex(t);
						newbrm.setRltParallel(curTransitionIndex,
								newTransitionIndex);
						newbrm.setRltParallel(newTransitionIndex,
								curTransitionIndex);
						newbrm.setRltDirSucc(curTransitionIndex,
								newTransitionIndex);
						newbrm.setRltDirSucc(newTransitionIndex,
								curTransitionIndex);
					}
				}
				// Ti>Ts
				if (matrix.isRltDirSucc(oldTransitionIndex, oldIndexOfFork)) {
					newbrm.setRltDirSucc(newbrm
							.getTransitionIndex(oldInt2Transition
									.get(oldTransitionIndex)), newIndexOfFork);
				}

				// Ts>Ti
				if (matrix.isRltDirSucc(oldIndexOfFork, oldTransitionIndex)) {
					newbrm.setRltDirSucc(newIndexofJoin, newbrm
							.getTransitionIndex(oldInt2Transition
									.get(oldTransitionIndex)));
				}

			}
		}

		matrix.brm = null;
		matrix.brm = newbrm;
	}

	private static Map<Transition, SimpleParallelStructure> getAllSPS(
			PetriNet net) {
		SimpleParallelStructureDetector spsDetector = SimpleParallelStructureDetector
				.getInstance();
		return spsDetector.getAllSPS(net);
	}

	public static void main(String[] args) {
		String fileName = "C:\\SPS_TEST\\Parallel20.xml";
		PetriNet net = PetriNetUtil.getPetriNetFromPnmlFile(fileName);
		long start = System.currentTimeMillis();
		RltMatrix m = build(net, 0);
		long end = System.currentTimeMillis();
		System.out.println("Time Cost=" + (end - start));
		m.printFinal();
	}
}
