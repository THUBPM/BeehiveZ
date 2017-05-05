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
package cn.edu.thss.iise.beehivez.server.petrinetwithdata.parallelize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogEvents;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cn.edu.thss.iise.beehivez.server.petrinetwithdata.DataItem;
import cn.edu.thss.iise.beehivez.server.petrinetwithdata.PetriNetWithData;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * Based on the dependence between data writing and reading, parallelize the
 * Petri net as much as possible. There are 4 steps.
 * 
 * Step 1: read the relations between transitions in the original Petri net 
 * Step 2: analyze the dependence between data writing and reading 
 * Step 3: change some relations between the transitions 
 *     change 1: change direct causal relation into parallel relation 
 *     change 2: change indirect causal relation into causal relation 
 *     change 3: change indirect causal relation into direct causal relation 
 * Step 4: use alpha build a new Petri net based on the updated relations between transitions.
 * 
 * The input process model must be sound structure workflow net
 * 
 * @author Tao Jin
 * 
 * @date 2012-4-27
 * 
 */
public class PetriNetParallelize {

	public static String parallelize(PetriNetWithData dpn,
			PetriNetWithData newdpn) {

		PetriNet pn = dpn.getPetriNet();

		// build events
		LogEvents events = new LogEvents();
		ArrayList<Transition> transitions = pn.getTransitions();
		int nT = transitions.size();
		for (int i = 0; i < nT; i++) {
			Transition t = transitions.get(i);
			LogEvent event = new LogEvent(t.getIdentifier(), "complete");
			events.add(event);
		}

		// step 1: compute the relations between transitions
		DoubleMatrix1D startModelElements = DoubleFactory1D.sparse.make(nT);
		DoubleMatrix1D finalModelElements = DoubleFactory1D.sparse.make(nT);
		DoubleMatrix1D oneLoop = DoubleFactory1D.sparse.make(nT);
		DoubleMatrix2D causalRelations = DoubleFactory2D.sparse.make(nT, nT, 0);
		DoubleMatrix2D causalRelationsClosure = DoubleFactory2D.sparse.make(nT,
				nT, 0);
		DoubleMatrix2D parallelRelations = DoubleFactory2D.sparse.make(nT, nT,
				0);
		DoubleMatrix2D unrelatedRelations = DoubleFactory2D.sparse.make(nT, nT,
				0);

		computeLogRelationsFromPetriNet(pn, startModelElements,
				finalModelElements, oneLoop, causalRelations,
				causalRelationsClosure, parallelRelations, unrelatedRelations);
		// print the relations in the original model
		String msg = "the relations in the original model";
		msg += printRelations(transitions, startModelElements,
				finalModelElements, oneLoop, causalRelations,
				unrelatedRelations, parallelRelations);

		// step 2: analyze the dependence between data operations
		DoubleMatrix2D dataDependenceCore = DoubleFactory2D.sparse.make(nT, nT,
				0);
		DoubleMatrix2D dataDependenceClosure = DoubleFactory2D.sparse.make(nT,
				nT, 0);
		msg += analyzeDataDependence(dpn, causalRelationsClosure,
				dataDependenceCore, dataDependenceClosure);

		// step 3: update the relations between transitions
		msg += updateEventRelations(pn, causalRelations,
				causalRelationsClosure, parallelRelations, unrelatedRelations,
				dataDependenceCore, dataDependenceClosure);

		// print the relations after updation
		msg += "\n\nthe relations after updation";
		msg += printRelations(transitions, startModelElements,
				finalModelElements, oneLoop, causalRelations,
				unrelatedRelations, parallelRelations);

		// step 4: build a new Petri net
		PetriNet newpn = mineWithAlpha(events, startModelElements,
				finalModelElements, oneLoop, causalRelations,
				unrelatedRelations, parallelRelations);
		
		// postprocessing for special transitions
		postProcessing(newpn);

		newdpn.setPetriNet(newpn);

		return msg;
	}

	private static void postProcessing(PetriNet pn) {
		ArrayList<Place> pstarts = new ArrayList<Place>();
		for (PNNode node : pn.getNodes()) {
			if (node.inDegree() == 0) {
				if (node instanceof Place) {
					pstarts.add((Place) node);
				} else if (node instanceof Transition) {
					Place p = new Place("ap" + pn.getPlaces().size(), pn);
					pn.addPlace(p);
					pn.addEdge(p, (Transition) node);
					pstarts.add(p);
				}
			}
		}

		ArrayList<Place> pends = new ArrayList<Place>();
		for (PNNode node : pn.getNodes()) {
			if (node.outDegree() == 0) {
				if (node instanceof Place) {
					pends.add((Place) node);
				} else if (node instanceof Transition) {
					Place p = new Place("ap" + pn.getPlaces().size(), pn);
					pn.addPlace(p);
					pn.addEdge((Transition) node, p);
					pends.add(p);
				}
			}
		}

		if (pstarts.size() > 1) {
			Place p = new Place("apstart", pn);
			pn.addPlace(p);
			Transition t = new Transition("atstart", pn);
			pn.addTransition(t);
			pn.addEdge(p, t);
			for (Place pp : pstarts) {
				pn.addEdge(t, pp);
			}
		}

		if (pends.size() > 1) {
			Place p = new Place("apend", pn);
			pn.addPlace(p);
			Transition t = new Transition("atend", pn);
			pn.addTransition(t);
			pn.addEdge(t, p);
			for (Place pp : pends) {
				pn.addEdge(pp, t);
			}
		}
	}
	
	private static String printRelations(ArrayList<Transition> transitions,
			DoubleMatrix1D startModelElements,
			DoubleMatrix1D finalModelElements, DoubleMatrix1D oneLoop,
			DoubleMatrix2D causalRelations, DoubleMatrix2D unrelatedRelations,
			DoubleMatrix2D parallelRelations) {

		String msg = "\nstart events: ";
		for (int i = 0; i < transitions.size(); i++) {
			if (startModelElements.get(i) > 0) {
				msg += transitions.get(i).getIdentifier() + " ";
			}
		}

		msg += "\nend events: ";
		for (int i = 0; i < transitions.size(); i++) {
			if (finalModelElements.get(i) > 0) {
				msg += transitions.get(i).getIdentifier() + " ";
			}
		}

		msg += "\nlength one loop events: ";
		for (int i = 0; i < transitions.size(); i++) {
			if (oneLoop.get(i) > 0) {
				msg += transitions.get(i).getIdentifier() + " ";
			}
		}

		msg += "\ncausal relations: ";
		for (int i = 0; i < transitions.size(); i++) {
			for (int j = 0; j < transitions.size(); j++) {
				if (causalRelations.get(i, j) > 0) {
					msg += "<" + transitions.get(i).getIdentifier() + ","
							+ transitions.get(j).getIdentifier() + "> ";
				}
			}
		}

		msg += "\nparallel relations: ";
		for (int i = 0; i < transitions.size(); i++) {
			for (int j = 0; j < transitions.size(); j++) {
				if (parallelRelations.get(i, j) > 0) {
					msg += "<" + transitions.get(i).getIdentifier() + ","
							+ transitions.get(j).getIdentifier() + "> ";
				}
			}
		}

		msg += "\nunrelated relations: ";
		for (int i = 0; i < transitions.size(); i++) {
			for (int j = 0; j < transitions.size(); j++) {
				if (unrelatedRelations.get(i, j) > 0) {
					msg += "<" + transitions.get(i).getIdentifier() + ","
							+ transitions.get(j).getIdentifier() + "> ";
				}
			}
		}

		return msg;
	}

	private static PetriNet mineWithAlpha(LogEvents events,
			DoubleMatrix1D startModelElements,
			DoubleMatrix1D finalModelElements, DoubleMatrix1D oneLoop,
			DoubleMatrix2D causalRelations, DoubleMatrix2D unrelatedRelations,
			DoubleMatrix2D parallelRelations) {

		final PetriNet petrinet = new PetriNet();
		int nme = events.size();
		final ArrayList<Transition> transitions = new ArrayList<Transition>(nme);

		// First, we can write all transitions
		for (int i = 0; i < nme; i++) {
			LogEvent e = events.getEvent(i);
			transitions.add(new Transition(e, petrinet));
		}

		findPlaces(petrinet, transitions, events, startModelElements,
				finalModelElements, oneLoop, causalRelations,
				unrelatedRelations, parallelRelations);
		petrinet.makeClusters();

		return petrinet;
	}

	private static void findPlaces(PetriNet petrinet,
			ArrayList<Transition> transitions, LogEvents events,
			DoubleMatrix1D startModelElements,
			DoubleMatrix1D finalModelElements, DoubleMatrix1D oneLoop,
			DoubleMatrix2D causalRelations, DoubleMatrix2D unrelatedRelations,
			DoubleMatrix2D parallelRelations) {

		ArrayList tuples = new ArrayList();
		ArrayList<Place> places = new ArrayList();
		int nme = transitions.size();
		for (int i = 0; i < nme; i++) {
			// Skip loops of length one
			if (oneLoop.get(i) > 0) {
				continue;
			}

			//
			for (int j = 0; j < nme; j++) {
				if (oneLoop.get(j) > 0) {
					continue;
				}
				if (causalRelations.get(i, j) == 0) {
					continue;
				}

				IntArrayList A = new IntArrayList();
				A.add(i);

				// j is a causal follower of i
				IntArrayList B = new IntArrayList();
				B.add(j);
				// Now, we have a startingpoint to expand the tree,
				// since {i} -> {j}
				ExpandTree(tuples, A, B, 0, 0, petrinet, places, transitions,
						oneLoop, causalRelations, unrelatedRelations,
						parallelRelations);
			}
			// In tuples, we now have a collection of ArrayList[2]'s each of
			// which
			// contains information to build the places
		}

		// Second, we can write all places (check for duplicates)
		// RemoveDuplicates(tuples);

		Place pstart = petrinet.addPlace("pstart");
		// pstart.addToken(new Token());
		Place pend = petrinet.addPlace("pend");

		// Third, we write all arcs not for one loops

		// ///////////////////////////////////////////////

		// Fourth, write all one loop arc's

		for (int i = 0; i < nme; i++) {
			if (oneLoop.get(i) == 0) {
				continue;
			}
			IntArrayList pre_i = new IntArrayList();
			IntArrayList suc_i = new IntArrayList();
			for (int j = 0; j < nme; j++) {
				if (causalRelations.get(j, i) > 0) {
					pre_i.add(j);
				}
			}
			for (int j = 0; j < nme; j++) {
				if (causalRelations.get(i, j) > 0) {
					suc_i.add(j);
				}
			}

			IntArrayList tupleNumbers = getTupleNumbersContaining(tuples,
					pre_i, suc_i);
			// Now we can write the arcs from and to the place.

			for (int tupleNumber = 0; tupleNumber < tupleNumbers.size(); tupleNumber++) {
				petrinet.addEdge(getTransition(i, transitions, petrinet),
						places.get(tupleNumbers.get(tupleNumber)));
				petrinet.addEdge(places.get(tupleNumbers.get(tupleNumber)),
						getTransition(i, transitions, petrinet));
			}
		}

		for (int i = 0; i < nme; i++) {
			if (startModelElements.get(i) == 0) {
				continue;
			}
			petrinet.addEdge(pstart, getTransition(i, transitions, petrinet));
		}

		for (int i = 0; i < nme; i++) {
			if (finalModelElements.get(i) == 0) {
				continue;
			}
			petrinet.addEdge(getTransition(i, transitions, petrinet), pend);
		}

		// Now write clusters.

		for (int i = 0; i < nme; i++) {
			LogEvent e = events.getEvent(i);
			if (petrinet.findRandomTransition(e) == null) {
				petrinet.addTransition(new Transition(e, petrinet));
			}
		}

		petrinet.Test("AlphaMinerResult");

		return;

	}

	private static boolean ExpandTree(ArrayList tuples, IntArrayList A,
			IntArrayList B, int sA, int sB, PetriNet net,
			ArrayList<Place> places, ArrayList<Transition> transitions,
			DoubleMatrix1D oneLoop, DoubleMatrix2D causalRelations,
			DoubleMatrix2D unrelatedRelations, DoubleMatrix2D parallelRelations) {

		boolean expanded = false;

		int s = sA;
		if (sB < s) {
			s = sB;
			// Look for an element that can be added to A, such that
			// it has no relation with any task in A, and is a causal
			// predecessor of all tasks in B
		}
		int nme = transitions.size();
		for (int i = s; i < nme; i++) {
			if (oneLoop.get(i) > 0) {
				continue;
			}
			// this is not a loop of length one
			boolean c = (i >= sA) && !A.contains(i);
			if (c) {
				for (int j = 0; j < A.size(); j++) {
					c = c && (causalRelations.get(i, A.get(j)) == 0)
							&& (causalRelations.get(A.get(j), i) == 0)
							&& (parallelRelations.get(i, A.get(j)) == 0);
					// c == i does not have a relation with any element of A
				}
			}
			if (c) {
				for (int j = 0; j < B.size(); j++) {
					c = c && (causalRelations.get(i, B.get(j)) > 0);
					// c == i is a causal predecessor of all elements of B

				}
			}
			boolean d = (i >= sB) && !B.contains(i);
			if (d) {
				for (int j = 0; j < B.size(); j++) {
					d = d && (causalRelations.get(i, B.get(j)) == 0)
							&& (causalRelations.get(B.get(j), i) == 0)
							&& (parallelRelations.get(i, B.get(j)) == 0);
					// d == i does not have a relation with any element of B
				}
			}
			if (d) {
				for (int j = 0; j < A.size(); j++) {
					d = d && (causalRelations.get(A.get(j), i) > 0);
					// d == i is a causal successor of all elements of A

				}
			}
			IntArrayList tA = (IntArrayList) A.clone();
			IntArrayList tB = (IntArrayList) B.clone();

			if (c) {
				// i can be added to A
				A.add(i);
				expanded = ExpandTree(tuples, A, B, i + 1, sB, net, places,
						transitions, oneLoop, causalRelations,
						unrelatedRelations, parallelRelations);
				A = tA;
			}
			if (d) {
				// i can be added to A
				B.add(i);
				expanded = ExpandTree(tuples, A, B, sA, i + 1, net, places,
						transitions, oneLoop, causalRelations,
						unrelatedRelations, parallelRelations);
				B = tB;
			}
		}
		if (!expanded) {
			IntArrayList[] t = new IntArrayList[2];
			t[0] = (IntArrayList) A.clone();
			t[1] = (IntArrayList) B.clone();

			if (!removeSmallerThan(tuples, t, tuples.size() - 1, net, places)) {
				tuples.add(t);
				places.add(addPlaceForTuple(t, net, transitions));
			}

			expanded = true;
		}
		return expanded;
	}

	private static boolean removeSmallerThan(ArrayList tuples,
			IntArrayList[] tuple, int tupleIndex, PetriNet net,
			ArrayList<Place> places) {
		int i = tupleIndex;
		boolean foundLarger = false;
		while (i >= 0 && !foundLarger) {

			IntArrayList[] tuple_i = ((IntArrayList[]) (tuples.get(i)));

			// Now check whether tuple_i is a subset of tuple
			if (tuple[0].toList().containsAll(tuple_i[0].toList())
					&& tuple[1].toList().containsAll(tuple_i[1].toList())) {

				// tuple contains tuple_i
				tuples.remove(i);
				net.delPlace(places.get(i));
				places.remove(i);
			} else if (tuple_i[0].toList().containsAll(tuple[0].toList())
					&& tuple_i[1].toList().containsAll(tuple[1].toList())) {
				// tuple_i contains tuple (hence, there are no smaller tuples
				// before
				foundLarger = true;
			}

			i--;
		}
		return foundLarger;
	}

	private static Place addPlaceForTuple(IntArrayList[] tuple,
			PetriNet petrinet, ArrayList<Transition> transitions) {
		Place p = new Place(Arrays.toString(tuple), petrinet);

		petrinet.addPlace(p);
		for (int j = 0; j < tuple[0].size(); j++) {
			petrinet.addEdge(getTransition(tuple[0].get(j), transitions,
					petrinet), p);
		}
		for (int j = 0; j < tuple[1].size(); j++) {
			petrinet.addEdge(p, getTransition(tuple[1].get(j), transitions,
					petrinet));
		}
		return p;
	}

	private static Transition getTransition(int i,
			ArrayList<Transition> transitions, PetriNet net) {
		Transition t = transitions.get(i);
		if (net.findTransition(t) == null) {
			net.addTransition(t);
		}
		return t;
	}

	private static IntArrayList getTupleNumbersContaining(ArrayList tuples,
			IntArrayList A, IntArrayList B) {
		int i = -1;
		IntArrayList r = new IntArrayList();
		while (i < tuples.size() - 1) {
			i++;
			IntArrayList[] tuple = ((IntArrayList[]) (tuples.get(i)));
			IntArrayList tA = (IntArrayList) A.clone();
			IntArrayList tB = (IntArrayList) B.clone();
			if ((tA.toList().containsAll(tuple[0].toList()) && tB.toList()
					.containsAll(tuple[1].toList()))) {
				// A and B are contained in this tuple
				r.add(i);
			}
		}
		return r;
	}

	private static String updateEventRelations(PetriNet pn,
			DoubleMatrix2D causalRelations,
			DoubleMatrix2D causalRelationsClosure,
			DoubleMatrix2D parallelRelations,
			DoubleMatrix2D unrelatedRelations,
			DoubleMatrix2D dataDependenceCore,
			DoubleMatrix2D dataDependenceClosure) {

		String msg = "\n\nupdate relations according to data operating dependences";
		ArrayList<Transition> transitions = pn.getTransitions();
		int nT = causalRelations.rows();
		for (int i = 0; i < nT; i++) {
			for (int j = 0; j < nT; j++) {
				if (i == j) {
					continue;
				}
				// change causal relations to parallel relations
				if (causalRelations.get(i, j) > 0
						&& dataDependenceClosure.get(i, j) == 0) {
					parallelRelations.set(i, j, 1);
					parallelRelations.set(j, i, 1);
					causalRelations.set(i, j, 0);
					msg += "\nchange causal relation to parallel relation: <"
							+ transitions.get(i).getIdentifier() + ","
							+ transitions.get(j).getIdentifier() + ">";
				}

				// change transitive causal relations to direct causal relations
				else if (causalRelations.get(i, j) == 0
						&& causalRelationsClosure.get(i, j) > 0
						&& dataDependenceCore.get(i, j) > 0) {

					causalRelations.set(i, j, 1);
					unrelatedRelations.set(i, j, 0);
					unrelatedRelations.set(j, i, 0);
					parallelRelations.set(i, j, 0);
					parallelRelations.set(j, i, 0);
					msg += "\nchange transitive causal realtion to direct causal relation: <"
							+ transitions.get(i).getIdentifier()
							+ ","
							+ transitions.get(j).getIdentifier() + ">";
				}

				// change transitive causal relations to parallel relations
				else if (causalRelations.get(i, j) == 0
						&& causalRelationsClosure.get(i, j) > 0
						&& dataDependenceClosure.get(i, j) == 0) {

					parallelRelations.set(i, j, 1);
					parallelRelations.set(j, i, 1);
					unrelatedRelations.set(i, j, 0);
					unrelatedRelations.set(j, i, 0);
					msg += "\nchange transitive causal relation to parallel relation: <"
							+ transitions.get(i).getIdentifier()
							+ ", "
							+ transitions.get(j).getIdentifier() + ">";
				}
			}
		}
		return msg;
	}

	private static String analyzeDataDependence(PetriNetWithData dpn,
			DoubleMatrix2D causalRelationsClosure,
			DoubleMatrix2D dataDependenceCore,
			DoubleMatrix2D dataDependenceClosure) {

		String msg = "\n\ndata operating dependence";
		HashMap<DataItem, HashSet<Transition>> dataWritten = dpn
				.getDataWritten();
		HashMap<DataItem, HashSet<Transition>> dataRead = dpn.getDataRead();
		PetriNet pn = dpn.getPetriNet();
		ArrayList<Transition> transitions = pn.getTransitions();

		// analyze w-w dependence
		msg += "\nw-w dependence: ";
		for (DataItem di : dataWritten.keySet()) {
			Transition[] ts = dataWritten.get(di).toArray(new Transition[0]);
			for (int i = 0; i < ts.length; i++) {
				for (int j = i + 1; j < ts.length; j++) {
					int iti = transitions.indexOf(ts[i]);
					int itj = transitions.indexOf(ts[j]);
					if (causalRelationsClosure.get(iti, itj) > 0) {
						dataDependenceCore.set(iti, itj, 1);
						msg += "[" + transitions.get(iti).getIdentifier() + ","
								+ transitions.get(itj).getIdentifier() + "] ";
					}
					if (causalRelationsClosure.get(itj, iti) > 0) {
						dataDependenceCore.set(itj, iti, 1);
						msg += "[" + transitions.get(itj).getIdentifier() + ","
								+ transitions.get(iti).getIdentifier() + "] ";
					}
				}
			}
		}

		// analyze w-r dependence
		msg += "\n w-r r-w dependences: ";
		for (DataItem di : dataWritten.keySet()) {
			Transition[] tsw = dataWritten.get(di).toArray(new Transition[0]);
			Transition[] tsr = dataRead.get(di).toArray(new Transition[0]);
			for (int ir = 0; ir < tsr.length; ir++) {
				for (int iw = 0; iw < tsw.length; iw++) {
					int itr = transitions.indexOf(tsr[ir]);
					int itw = transitions.indexOf(tsw[iw]);
					if (itr != itw) {
						if (causalRelationsClosure.get(itw, itr) > 0) {
							dataDependenceCore.set(itw, itr, 1);
							msg += "[" + transitions.get(itw).getIdentifier()
									+ ","
									+ transitions.get(itr).getIdentifier()
									+ "] ";
						}
						if (causalRelationsClosure.get(itr, itw) > 0) {
							dataDependenceCore.set(itr, itw, 1);
							msg += "[" + transitions.get(itr).getIdentifier()
									+ ","
									+ transitions.get(itw).getIdentifier()
									+ "] ";
						}
					}
				}
			}
		}

		// compute the dependence closure and update the dependence core
		msg += "\n transitive data operating dependence: ";
		dataDependenceClosure.assign(dataDependenceCore);
		int nT = transitions.size();
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < nT; i++) {
				for (int j = 0; j < nT; j++) {
					if (i == j) {
						continue;
					}
					for (int k = 0; k < nT; k++) {
						if (i == k || j == k) {
							continue;
						}
						if (dataDependenceClosure.get(i, k) > 0
								&& dataDependenceClosure.get(k, j) > 0
								&& (dataDependenceClosure.get(i, j) == 0 || dataDependenceCore
										.get(i, j) > 0)) {

							dataDependenceClosure.set(i, j, 1);
							dataDependenceCore.set(i, j, 0);
							change = true;
							msg += "[" + transitions.get(i).getIdentifier()
									+ "," + transitions.get(j).getIdentifier()
									+ "] ";
						}
					}
				}
			}
		}
		return msg;
	}

	/*
	 * the input Petri net must be sound structure workflow net
	 */
	private static void computeLogRelationsFromPetriNet(PetriNet pn,
			DoubleMatrix1D startModelElements,
			DoubleMatrix1D finalModelElements, DoubleMatrix1D oneLoop,
			DoubleMatrix2D causalRelations,
			DoubleMatrix2D causalRelationsClosure,
			DoubleMatrix2D parallelRelations, DoubleMatrix2D unrelatedRelations) {

		ArrayList<Transition> transitions = pn.getTransitions();
		int nT = transitions.size();
		ArrayList<Place> places = pn.getPlaces();

		// set length one loop events
		for (int i = 0; i < nT; i++) {
			Transition t = transitions.get(i);
			if (t.getPredecessors().containsAll(t.getSuccessors())
					&& t.getSuccessors().containsAll(t.getPredecessors())) {
				oneLoop.set(i, 1);
			}
		}

		for (Place p : places) {
			Transition[] preTs = (Transition[]) p.getPredecessors().toArray(
					new Transition[0]);
			Transition[] sucTs = (Transition[]) p.getSuccessors().toArray(
					new Transition[0]);

			// set the start events
			if (preTs.length == 0 && sucTs.length > 0) {
				for (Transition t : sucTs) {
					int index = transitions.indexOf(t);
					if (oneLoop.get(index) > 0) {
						continue;
					}
					startModelElements.set(index, 1);
				}
			}

			// set the end events
			if (sucTs.length == 0 && preTs.length > 0) {
				for (Transition t : preTs) {
					int index = transitions.indexOf(t);
					if (oneLoop.get(index) > 0) {
						continue;
					}
					finalModelElements.set(index, 1);
				}
			}

			// set causal relations
			if (preTs.length > 0 && sucTs.length > 0) {
				for (Transition preT : preTs) {
					int indexPre = transitions.indexOf(preT);
					for (Transition sucT : sucTs) {
						int indexSuc = transitions.indexOf(sucT);
						if (indexPre == indexSuc) {
							continue;
						}
						causalRelations.set(indexPre, indexSuc, 1);
					}
				}
			}
		}

		// compute causal relation closure
		causalRelationsClosure.assign(causalRelations);
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < nT; i++) {
				for (int j = 0; j < nT; j++) {
					if (i == j) {
						continue;
					}
					for (int k = 0; k < nT; k++) {
						if (i == k || j == k) {
							continue;
						}
						if (causalRelationsClosure.get(i, k) > 0
								&& causalRelationsClosure.get(k, j) > 0
								&& causalRelationsClosure.get(i, j) == 0) {
							causalRelationsClosure.set(i, j, 1);
							change = true;
						}
					}
				}
			}
		}

		// set parallel relations
		for (int i = 0; i < nT; i++) {
			Transition t1 = transitions.get(i);
			for (int j = i + 1; j < nT; j++) {
				if (causalRelations.get(i, j) > 0
						|| causalRelations.get(j, i) > 0) {
					continue;
				}
				Transition t2 = transitions.get(j);
				if (PetriNetUtil.isParallel(t1, t2, pn)) {
					parallelRelations.set(i, j, 1);
					parallelRelations.set(j, i, 1);
				}
			}
		}

		// set unrelated relations
		for (int i = 0; i < nT; i++) {
			for (int j = i + 1; j < nT; j++) {

				if (causalRelations.get(i, j) > 0
						|| causalRelations.get(j, i) > 0
						|| parallelRelations.get(i, j) > 0
						|| parallelRelations.get(j, i) > 0) {
					continue;
				}
				unrelatedRelations.set(i, j, 1);
				unrelatedRelations.set(j, i, 1);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
