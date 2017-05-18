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
package cn.edu.thss.iise.beehivez.server.generator.petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * Petri net generator, the rules come from the paper
 * 
 * T Murata, Petri nets: Properties, analysis and applications
 * 
 * @author Tao Jin
 * 
 */
public class MurataGenerator extends PetriNetGenerator {

	public PetriNet generateModel(int minTransitionsPerNet,
			int maxTransitionsPerNet, int maxDegree, int maxTransitionNameLength) {
		PetriNet pn = generateAtomicModel(maxTransitionNameLength);

		// determine the number of tasks in the model
		int nTasks = minTransitionsPerNet
				+ rand.nextInt(maxTransitionsPerNet - minTransitionsPerNet + 1);

		while (pn.getTransitions().size() < nTasks) {
			int choice = rand.nextInt(5);
			switch (choice) {
			case 0:
				ALT(pn, maxTransitionNameLength);
				break;
			case 1:
				FPP(pn);
				break;
			case 2:
				FPT(pn, maxTransitionNameLength);
				break;
			case 3:
				FSP(pn, maxTransitionNameLength);
				break;
			case 4:
				FST(pn, maxTransitionNameLength);
				break;
			}
		}

		return pn;
	}

	/**
	 * fission of series places, choose one existing place, one new place and
	 * one new transition are added, the input flows of the chosen place are
	 * redistributed, the chosen place becomes the rear one.
	 * 
	 * @param pn
	 * @param maxTransitionNameLength
	 */
	private void FSP(PetriNet pn, int maxTransitionNameLength) {
		Place pChosen = getRandomPlace(pn);
		if (pChosen == null) {
			return;
		}

		// used for redistribution later
		HashSet prets = pChosen.getPredecessors();
		Transition[] preTs = new Transition[prets.size()];
		Iterator<Transition> it = prets.iterator();
		int pi = 0;
		while (it.hasNext()) {
			preTs[pi] = it.next();
			pi++;
		}

		// add new transiton and new place
		Transition newTransition = generateNewTransition(pn,
				maxTransitionNameLength);
		pn.addTransition(newTransition);

		Place newPlace = new Place("p" + pn.getPlaces().size(), pn);
		pn.addPlace(newPlace);

		pn.addEdge(newPlace, newTransition);
		pn.addEdge(newTransition, pChosen);

		// redistribute the input flows for the chosen place
		int n = rand.nextInt(preTs.length) + 1;
		HashSet<Transition> ts = new HashSet<Transition>();
		for (int i = 0; i < n; i++) {
			Transition t = preTs[rand.nextInt(preTs.length)];
			if (ts.add(t)) {
				pn.addEdge(t, newPlace);
				pn.delEdge(t, pChosen);
			}
		}
	}

	/**
	 * fission of series of transitions, choose one transition, one new
	 * transition and one new place are added, the chosen transition becomes the
	 * front one, the output flows of the chosen transition are redistributed.
	 * 
	 * @param pn
	 * @param maxTransitionNameLength
	 */
	private void FST(PetriNet pn, int maxTransitionNameLength) {
		// choose one transition
		Transition tChosen = getRandomTransition(pn);

		// record the output places, used for redistributed laber
		HashSet pss = tChosen.getSuccessors();
		Place[] postPs = new Place[pss.size()];
		Iterator<Place> it = pss.iterator();
		int ti = 0;
		while (it.hasNext()) {
			postPs[ti] = it.next();
			ti++;
		}

		// add a new transition and a new place
		Place newPlace = new Place("p" + pn.getPlaces().size(), pn);
		pn.addPlace(newPlace);

		Transition newTransition = generateNewTransition(pn,
				maxTransitionNameLength);
		pn.addTransition(newTransition);

		pn.addEdge(tChosen, newPlace);
		pn.addEdge(newPlace, newTransition);

		// redistribute the output flows of the chosen transition
		int n = rand.nextInt(postPs.length) + 1;
		HashSet<Place> ps = new HashSet<Place>();
		for (int i = 0; i < n; i++) {
			Place p = postPs[rand.nextInt(postPs.length)];
			if (ps.add(p)) {
				pn.addEdge(newTransition, p);
				pn.delEdge(tChosen, p);
			}
		}
	}

	/**
	 * fission of parallel places, choose one place, one new place is added.
	 * 
	 * @param pn
	 */
	private void FPP(PetriNet pn) {
		// choose one place
		Place pChosen = getRandomPlace(pn);
		if (pChosen == null) {
			return;
		}

		// add new place
		Place newPlace = new Place("p" + pn.getPlaces().size(), pn);
		pn.addPlace(newPlace);

		// copy the input flows
		Iterator<Transition> itTPre = pChosen.getPredecessors().iterator();
		while (itTPre.hasNext()) {
			pn.addEdge(itTPre.next(), newPlace);
		}

		// copy the output flows
		Iterator<Transition> itTPost = pChosen.getSuccessors().iterator();
		while (itTPost.hasNext()) {
			pn.addEdge(newPlace, itTPost.next());
		}
	}

	/**
	 * fission of parallel transition, choose one transition, one new transition
	 * is added.
	 * 
	 * @param pn
	 * @param maxTransitionNameLength
	 */
	private void FPT(PetriNet pn, int maxTransitionNameLength) {
		// choose one transition
		Transition tChosen = getRandomTransition(pn);

		// add new transition
		Transition newTransition = generateNewTransition(pn,
				maxTransitionNameLength);
		pn.addTransition(newTransition);

		// copy the input flows
		Iterator<Place> itPPre = tChosen.getPredecessors().iterator();
		while (itPPre.hasNext()) {
			pn.addEdge(itPPre.next(), newTransition);
		}

		// copy the output flows
		Iterator<Place> itPPost = tChosen.getSuccessors().iterator();
		while (itPPost.hasNext()) {
			pn.addEdge(newTransition, itPPost.next());
		}
	}

	/**
	 * add self-loop transition, choose one place, add one new transition.
	 * 
	 * @param pn
	 * @param maxTransitionNameLength
	 */
	private void ALT(PetriNet pn, int maxTransitionNameLength) {
		// choose one place
		Place pChosen = getRandomPlace(pn);
		if (pChosen == null) {
			return;
		}

		// add one new transition
		Transition newTransition = generateNewTransition(pn,
				maxTransitionNameLength);
		pn.addTransition(newTransition);

		// add edges
		pn.addEdge(pChosen, newTransition);
		pn.addEdge(newTransition, pChosen);
	}

	private Transition getRandomTransition(PetriNet pn) {
		ArrayList<Transition> transitions = pn.getTransitions();
		int nTransition = transitions.size();
		int index = rand.nextInt(nTransition);
		Transition t = transitions.get(index);
		return t;
	}

	private Place getRandomPlace(PetriNet pn) {
		ArrayList<Place> places = pn.getPlaces();
		int nPlace = places.size();
		int index = rand.nextInt(nPlace);
		Place p = places.get(index);
		if (p.hasIdentifier("pSource") || p.hasIdentifier("pSink")) {
			return null;
		} else {
			return p;
		}
	}

	private PetriNet generateAtomicModel(int maxTransitionNameLength) {
		PetriNet pn = new PetriNet();
		pn.setIdentifier(String.valueOf(System.nanoTime()));

		Place pSource = new Place("pSource", pn);
		pn.addPlace(pSource);

		Place pSink = new Place("pSink", pn);
		pn.addPlace(pSink);

		Transition t = generateNewTransition(pn, maxTransitionNameLength);
		pn.addTransition(t);

		pn.addEdge(pSource, t);
		pn.addEdge(t, pSink);

		return pn;
	}

	private Transition generateNewTransition(PetriNet pn,
			int maxTransitionNameLength) {
		String tName = getRandomString(maxTransitionNameLength);
		Transition tNew = new Transition(tName, pn);
		LogEvent le = new LogEvent(tName, "auto");
		tNew.setLogEvent(le);
		return tNew;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
			System.out.println(i);
			MurataGenerator generator = new MurataGenerator();
			PetriNet pn = generator.generateModel(1, 50, -1, 3);
			PetriNetUtil.export2pnml(pn, "e:/test/" + pn.getIdentifier()
					+ ".pnml");
		}
	}

	@Override
	public boolean supportDegreeConfiguration() {
		return false;
	}

}
