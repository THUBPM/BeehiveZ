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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.Marking;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.State;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.InitialPlaceMarker;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * build the rltMatrix
 * 
 * @author zhougz 2010.03.31
 * 
 */
public class RltMatrixBuilder {
	private static int numberOfFire = 0;
	private static long process_id = 0;

	public synchronized static RltMatrix build(PetriNet net, long id) {
		// The assumption here is that this is a marked PetriNet
		numberOfFire = 0;
		process_id = id;
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
		// System.out.println("\nAfter Fill PARALLEL");
		// rltMatrix.print();
		rltMatrix.fillCycleRlt();
		// System.out.println("\nAfter Fill CYCLE");
		// rltMatrix.print();
		rltMatrix.fillDirectSucc();
		// rltMatrix.printFinal();
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
				// numberOfFire ++;
				// //skip process that has more than 10 parallel transitions
				// if(numberOfFire > 1024)
				// {
				// System.out.println("Petri Net " + process_id +
				// " fireed more than 1024 times. Delete process!!!");
				// newNodes.clear();
				// DataManager.getInstance().delProcess(process_id);
				// rltMatrixSoFar = null;
				// return;
				// }
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
