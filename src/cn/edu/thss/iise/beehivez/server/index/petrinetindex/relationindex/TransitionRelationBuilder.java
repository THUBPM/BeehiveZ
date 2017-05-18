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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex;

import java.util.ArrayList;
import java.util.HashSet;

import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.State;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.Event;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.OrderingRelation;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;

/**
 * @author Tao Jin
 * 
 */
public class TransitionRelationBuilder {

	private static final byte BITPARALLELWITH = 1;
	private static final byte BITPRECEDE = 2;
	private static final byte BITEXCLUDE = 4;

	private HashSet<TransitionLabelPair> rParallel = null;
	private HashSet<TransitionLabelPair> rPrecede = null;
	private HashSet<TransitionLabelPair> rExclude = null;

	private PetriNet pn = null;
	private byte[][] relation = null;

	public TransitionRelationBuilder(PetriNet pn) {
		this.pn = pn;
	}

	public HashSet<TransitionLabelPair> getParallel() {
		return rParallel;
	}

	public HashSet<TransitionLabelPair> getPrecede() {
		return rPrecede;
	}

	public HashSet<TransitionLabelPair> getExclude() {
		return rExclude;
	}

	public void calculateOnCFP() {
		rParallel = new HashSet<TransitionLabelPair>();
		rPrecede = new HashSet<TransitionLabelPair>();
		rExclude = new HashSet<TransitionLabelPair>();

		CompleteFinitePrefix cfp = PetriNetUtil.buildCompleteFinitePrefix(pn);
		OrderingRelation[][] orderingRelations = cfp.getOrderingRelations();
		ArrayList<Transition> events = cfp.getTransitions();

		// used for mark during check of local concurrency
		int mark = 0;

		// deal with ordering relations directly
		for (int i = 0; i < orderingRelations.length; i++) {
			String first = events.get(i).getIdentifier();
			for (int j = i; j < orderingRelations.length; j++) {
				String second = events.get(j).getIdentifier();
				if (orderingRelations[i][j] == OrderingRelation.CONCURRENCY) {
					rParallel.add(new TransitionLabelPair(first, second));
					rParallel.add(new TransitionLabelPair(second, first));
				} else if (orderingRelations[i][j] == OrderingRelation.CONFLICT) {
					rExclude.add(new TransitionLabelPair(first, second));
					rExclude.add(new TransitionLabelPair(second, first));
				} else if (orderingRelations[i][j] == OrderingRelation.PRECEDENCE) {
					rPrecede.add(new TransitionLabelPair(first, second));
					if (orderingRelations[j][i] == OrderingRelation.PRECEDENCE) {
						rPrecede.add(new TransitionLabelPair(second, first));

						// check whether it is local concurrency
						Event event1 = (Event) events.get(i);
						Event event2 = (Event) events.get(j);
						if (PetriNetUtil.canBeExecutedParallelly(event1,
								event2, mark, mark + 1)) {
							rParallel
									.add(new TransitionLabelPair(first, second));
							rParallel
									.add(new TransitionLabelPair(second, first));
						}
						mark += 2;
					}
				} else if (orderingRelations[i][j] == OrderingRelation.NONE) {
					if (orderingRelations[j][i] == OrderingRelation.PRECEDENCE) {
						rPrecede.add(new TransitionLabelPair(second, first));
					}
				}
			}
		}
		cfp.destroyCFP();
	}

	/**
	 * based on coverability graph
	 */
	public void calculateOnCG() {
		rParallel = new HashSet<TransitionLabelPair>();
		rPrecede = new HashSet<TransitionLabelPair>();
		rExclude = new HashSet<TransitionLabelPair>();

		ArrayList<Transition> transitions = pn.getTransitions();
		int nT = transitions.size();
		boolean[][] follow = new boolean[nT][nT];
		boolean[][] reach = new boolean[nT][nT];
		relation = new byte[nT][nT];
		for (int i = 0; i < nT; i++) {
			for (int j = 0; j < nT; j++) {
				follow[i][j] = false;
				reach[i][j] = false;
				relation[i][j] = 0;
			}
		}

		StateSpace cg = PetriNetUtil.buildCoverabilityGraph(this.pn);
		ArrayList<ModelGraphVertex> states = cg.getVerticeList();
		ArrayList cgEdges = cg.getEdges();
		int nCGEdges = cgEdges.size();
		boolean[][] cgEdgeConnect = new boolean[nCGEdges][nCGEdges];
		for (int i = 0; i < nCGEdges; i++) {
			for (int j = 0; j < nCGEdges; j++) {
				cgEdgeConnect[i][j] = false;
			}
		}

		// get the follow relations
		for (int i = 0; i < states.size(); i++) {
			State state = (State) states.get(i);
			ArrayList inEdges = state.getInEdges();
			if (inEdges == null) {
				continue;
			}
			ArrayList outEdges = state.getOutEdges();
			if (outEdges == null) {
				continue;
			}
			for (int m = 0; m < inEdges.size(); m++) {
				ModelGraphEdge inEdge = (ModelGraphEdge) inEdges.get(m);
				for (int n = 0; n < outEdges.size(); n++) {
					ModelGraphEdge outEdge = (ModelGraphEdge) outEdges.get(n);

					int indexFirstCGEdge = cgEdges.indexOf(inEdge);
					int indexSecondCGEdge = cgEdges.indexOf(outEdge);
					if (indexFirstCGEdge < 0 || indexSecondCGEdge < 0) {
						continue;
					}
					cgEdgeConnect[indexFirstCGEdge][indexSecondCGEdge] = true;

					Transition inTransition = (Transition) inEdge.object;
					if (inTransition == null) {
						continue;
					}
					Transition outTransition = (Transition) outEdge.object;
					if (outTransition == null) {
						continue;
					}

					int indexFirst = transitions.indexOf(inTransition);
					int indexSecond = transitions.indexOf(outTransition);
					follow[indexFirst][indexSecond] = true;
					reach[indexFirst][indexSecond] = true;
				}
			}
		}

		// get the reach relations
		for (int k = 0; k < nCGEdges; k++) {
			for (int i = 0; i < nCGEdges; i++) {
				for (int j = 0; j < nCGEdges; j++) {
					if (cgEdgeConnect[i][k] && cgEdgeConnect[k][j]) {
						cgEdgeConnect[i][j] = true;
					}
				}
			}
		}

		for (int i = 0; i < nCGEdges; i++) {
			for (int j = 0; j < nCGEdges; j++) {
				if (cgEdgeConnect[i][j]) {
					ModelGraphEdge firstEdge = (ModelGraphEdge) cgEdges.get(i);
					ModelGraphEdge secondEdge = (ModelGraphEdge) cgEdges.get(j);

					Transition firstTransition = (Transition) firstEdge.object;
					Transition secondTransition = (Transition) secondEdge.object;

					if (firstTransition != null && secondTransition != null) {
						int indexFirst = transitions.indexOf(firstTransition);
						int indexSecond = transitions.indexOf(secondTransition);
						reach[indexFirst][indexSecond] = true;
					}
				}
			}
		}

		// calculate the unique relations
		for (int i = 0; i < nT; i++) {
			for (int j = 0; j < nT; j++) {
				if (follow[i][j] && follow[j][i]) {
					if (i == j) {
						// length one loop
						relation[i][j] = BITPRECEDE;
					} else {
						// length two loop possible
						Transition firstTransition = transitions.get(i);
						Transition secondTransition = transitions.get(j);

						if (PetriNetUtil.isInLengthTwoLoop(firstTransition,
								secondTransition)) {
							relation[i][j] = BITPRECEDE;
							relation[j][i] = BITPRECEDE;
						} else {
							relation[i][j] = BITPARALLELWITH;
							relation[j][i] = BITPARALLELWITH;
						}
					}
				} else if (!follow[i][j] && follow[j][i]) {
					relation[j][i] = BITPRECEDE;
				} else if (follow[i][j] && !follow[j][i]) {
					relation[i][j] = BITPRECEDE;
				} else if (!follow[i][j] && !follow[j][i]) {
					if (reach[i][j]) {
						relation[i][j] = BITPRECEDE;
					}
					if (reach[j][i]) {
						relation[j][i] = BITPRECEDE;
					}
					if (!reach[i][j] && !reach[j][i]) {
						relation[i][j] = BITEXCLUDE;
						relation[j][i] = BITEXCLUDE;
					}
				}
			}
		}
		cg.destroyStateSpace();

		// build the relation of transition label pair
		for (int i = 0; i < nT; i++) {
			for (int j = 0; j < nT; j++) {
				String first = transitions.get(i).getIdentifier();
				String second = transitions.get(j).getIdentifier();

				switch (relation[i][j]) {
				case BITPRECEDE:
					rPrecede.add(new TransitionLabelPair(first, second));
					break;
				case BITPARALLELWITH:
					rParallel.add(new TransitionLabelPair(first, second));
					break;
				case BITEXCLUDE:
					rExclude.add(new TransitionLabelPair(first, second));
					break;
				}
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
