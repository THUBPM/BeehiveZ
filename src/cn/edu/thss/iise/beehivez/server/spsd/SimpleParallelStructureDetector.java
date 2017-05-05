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

package cn.edu.thss.iise.beehivez.server.spsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

public class SimpleParallelStructureDetector {
	private static SimpleParallelStructureDetector instance = new SimpleParallelStructureDetector();

	public static SimpleParallelStructureDetector getInstance() {
		return instance;
	}

	public boolean getSimpleParallelStructure(PetriNet net,
			Transition forkCandidate, SimpleParallelStructure sps) {
		TokenTree root = new TokenTree();
		root.addTransition(forkCandidate);
		Iterator<Object> itSucc = forkCandidate.getSuccessors().iterator();
		Object curVertex = null;
		PNNode curNode = null;
		Transition joinCandidate = null;
		boolean result = false;
		while (itSucc.hasNext()) {
			curVertex = itSucc.next();
			TokenTree son = new TokenTree();
			root.addChild(son);
			if (curVertex instanceof PNNode) {
				curNode = (PNNode) curVertex;
				while (curNode.inDegree() == 1 && curNode.outDegree() == 1) {
					if (curNode instanceof Transition) {
						son.addTransition((Transition) curNode);
					} else if (curNode instanceof Place) {
						son.addPlace((Place) curNode);
					}
					curNode = (PNNode) curNode.getSuccessors().iterator()
							.next();
				}
				if (joinCandidate == null && (curNode instanceof Transition)) {
					joinCandidate = (Transition) curNode;
					if (joinCandidate.inDegree() != forkCandidate.outDegree()) {
						return false;
					}
				}
				if (curNode instanceof Place) {
					return false;
				}
				if (joinCandidate != null && (curNode instanceof Transition)) {
					if (!joinCandidate.equals(curNode)) {
						return false;
					}
				}
			}
		}

		boolean justPlacePrarallel = true;
		Iterator itChild = root.getChildren().iterator();
		while (itChild.hasNext() && justPlacePrarallel == true) {
			TokenTree c = (TokenTree) itChild.next();
			if (c.getSequence().size() > 0) {
				justPlacePrarallel = false;
			}
		}

		sps.setFork(forkCandidate);
		sps.setJoin(joinCandidate);
		sps.setBranches(root);
		return !justPlacePrarallel;

	}

	public Map<Transition, SimpleParallelStructure> getAllSPS(PetriNet net) {
		Vector<Transition> forkCandidates = new Vector<Transition>();
		Set<Integer> joinCandidateOutdegrees = new HashSet<Integer>();
		Map<Transition, SimpleParallelStructure> result = new HashMap<Transition, SimpleParallelStructure>();
		ArrayList<Transition> transitions = net.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			if (t.outDegree() > 1) {
				forkCandidates.add(t);
			}
			if (t.inDegree() > 1) {
				joinCandidateOutdegrees.add(t.inDegree());
			}
		}
		Iterator<Transition> it = forkCandidates.iterator();
		while (it.hasNext()) {
			Transition t = it.next();
			if (!joinCandidateOutdegrees.contains(t.outDegree())) {
				it.remove();
			}
		}
		Iterator<Transition> it2 = forkCandidates.iterator();
		while (it2.hasNext()) {
			SimpleParallelStructure sps = new SimpleParallelStructure();
			Transition t = it2.next();
			if (getSimpleParallelStructure(net, t, sps)) {
				result.put(t, sps);
			}
		}
		return result;
	}

}
