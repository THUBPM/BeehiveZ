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

package cn.edu.thss.iise.beehivez.client.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;

public class ParallelStructureDetector {
	private PetriNet net = null;
	private Set<Transition> setCandidateForkNode = new HashSet<Transition>();
	private Set<Transition> setCandidateJoinNode = new HashSet<Transition>();
	private Vector<Place> setPlaceHasToken = new Vector<Place>();
	private Set<Transition> setEnabledTransition = new HashSet<Transition>();
	private Set<Transition> setEnabledTransition2 = new HashSet<Transition>();

	private Set<Transition> setTrsVisited = new HashSet<Transition>();
	private Set<Transition> setTrsInParallel = new HashSet<Transition>();

	private Transition forkNode = null;
	private Transition joinNode = null;

	public Set<Transition> getParallelStructur(PetriNet net) {
		boolean isMinStructuralParallel = true;
		if (net == null)
			return null;
		this.net = net;
		this.getCandidateStarter();
		this.getCandidateEnder();
		if (setCandidateForkNode.isEmpty() || setCandidateJoinNode.isEmpty())
			return null;
		Iterator<Transition> itCS = setCandidateForkNode.iterator();

		while (!setCandidateForkNode.isEmpty()) {
			isMinStructuralParallel = true;
			setTrsInParallel.clear();

			forkNode = itCS.next();
			itCS.remove();
			setTrsVisited.add(forkNode);
			TokenTree root = new TokenTree();
			fireStart(forkNode, root);
			// Iterator<Place> itPHT = setPlaceHasToken.iterator();
			while (!setPlaceHasToken.isEmpty() && isMinStructuralParallel) {
				Place p = setPlaceHasToken.firstElement();
				setPlaceHasToken.removeElement(p);
				// if(p.getNumberOfTokens() > 1)
				// {
				// isMinStructuralParallel = false;
				// break;
				// }
				Iterator<PNEdge> itOE = p.getOutEdgesIterator();
				while (itOE.hasNext()) {
					Transition t = (Transition) itOE.next().getDest();
					// meet fork_node, meaning process model has subParallel
					// structure, restart
					if (t.outDegree() > 1) {
						isMinStructuralParallel = false;
						break;
					} else {
						// meet join_node stop current token to wait for other
						// branch finish
						if (t.inDegree() > 1 && !t.isEnabled()) {
							break;
						}

						if (t.isEnabled()) {
							if (t.inDegree() > 1) {
								if (t.inDegree() == root.getChildrenNum()) {
									joinNode = t;
									System.out.println("FORK: " + forkNode
											+ "  JOIN: " + joinNode);
									Iterator<TokenTree> itChildren = root
											.getChildren().iterator();
									int i = 0;
									while (itChildren.hasNext()) {
										System.out.println("  Branch_"
												+ i
												+ ": "
												+ itChildren.next()
														.getSequence());
										i++;
									}
									return setTrsInParallel;
								} else {
									isMinStructuralParallel = false;
									break;
								}
							} else {
								setEnabledTransition.add(t);
								setTrsInParallel.add(t);
							}
						}
					}
					setEnabledTransition.removeAll(setTrsVisited);
					if (setEnabledTransition.isEmpty()) {
						isMinStructuralParallel = false;
					}
					if (!isMinStructuralParallel)
						break;
				}
				Iterator itET = setEnabledTransition.iterator();
				while (itET.hasNext()) {
					Transition t2 = (Transition) itET.next();
					fireTransition(t2);
					itET.remove();
					setTrsVisited.add(t2);
				}
				p.removeAllTokens();
			}
		}

		return null;
	}

	private void fireTransition(Transition t) {
		Iterator it = t.getInEdgesIterator();
		TokenTree token = null;
		while (it.hasNext()) {
			Place p = (Place) ((PNEdge) it.next()).getSource();
			token = (TokenTree) p.getRandomAvailableToken();
			token.addTransition(t);
			break;
		}
		if (t.getOutEdges() != null) {
			it = t.getOutEdges().iterator();
			while (it.hasNext()) {
				Place p2 = (Place) ((PNEdge) it.next()).getDest();
				p2.addToken(token);
				setPlaceHasToken.add(p2);
			}
		}

	}

	private void fireStart(Transition t, TokenTree tt) {
		Iterator<PNEdge> itIE = t.getOutEdgesIterator();
		while (itIE.hasNext()) {
			Place p = (Place) itIE.next().getDest();
			TokenTree newTT = new TokenTree();
			newTT.setParent(tt);
			tt.addChild(newTT);
			p.addToken(newTT);
			setPlaceHasToken.add(p);
		}
	}

	private void getCandidateStarter() {
		setCandidateForkNode.clear();
		for (Transition t : net.getTransitions()) {
			if (t.getOutEdges().size() > 1) {
				setCandidateForkNode.add(t);
			}
		}
	}

	private void getCandidateEnder() {
		setCandidateJoinNode.clear();
		for (Transition t : net.getTransitions()) {
			if (t.getInEdges().size() > 1) {
				setCandidateJoinNode.add(t);
			}
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		DataManager dm = DataManager.getInstance();
		long end = System.currentTimeMillis();
		System.out.println("DB open time cost :" + (end - start));

		start = System.currentTimeMillis();
		ParallelStructureDetector d = new ParallelStructureDetector();
		end = System.currentTimeMillis();
		System.out
				.println("Detector instantiation time cost :" + (end - start));

		start = System.currentTimeMillis();
		// change ProcessID 6 to test your example
		PetriNet net = dm.getProcessPetriNet(21);
		end = System.currentTimeMillis();
		System.out.println("Get process from DB time cost: " + (end - start));
		start = System.currentTimeMillis();
		Set ttt = d.getParallelStructur(net);
		end = System.currentTimeMillis();
		if (ttt == null || ttt.size() < 1)
			System.out.println("No parallel structure detected.");
		else
			System.out.println("Transitions in parallel :" + ttt);
		System.out.println("Parallel structure detecting time cost :"
				+ (end - start));
	}

}
