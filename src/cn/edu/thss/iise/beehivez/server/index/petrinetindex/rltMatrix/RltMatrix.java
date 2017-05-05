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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.framework.models.fsm.FSMState;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.State;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.spsd.SimpleParallelStructure;

/**
 * a relationship matrix between transitions
 * 
 * @author zhougz 2010.03.31
 */
public class RltMatrix extends StateSpace {
	public BasicRltMatrix brm = new BasicRltMatrix();

	public RltMatrix(PetriNet net) {
		super(net);
		initRltMatrix();
	}

	public void fillCycleRlt() {
		// transfer to JGraph class
		DirectedGraph<State, DefaultEdge> dirGraph = new DefaultDirectedGraph<State, DefaultEdge>(
				DefaultEdge.class);
		Iterator itVertice = this.getVerticeList().iterator();
		while (itVertice.hasNext()) {
			dirGraph.addVertex((State) itVertice.next());
		}
		Iterator itEdges = this.getEdges().iterator();
		FSMTransition curTransition = null;
		while (itEdges.hasNext()) {
			curTransition = (FSMTransition) itEdges.next();
			State source = (State) curTransition.getSource();
			State dest = (State) curTransition.getDest();
			dirGraph.addEdge(source, dest);
		}

		// get the Strong Connectivity Components according to cycles
		StrongConnectivityInspector<State, DefaultEdge> inspector = new StrongConnectivityInspector<State, DefaultEdge>(
				dirGraph);
		List<Set<State>> components = inspector.stronglyConnectedSets();

		Set<State> cycleStates = new HashSet<State>();
		Object cycleStatesList[] = null;
		Set<FSMTransition> cycleTransitions = new HashSet<FSMTransition>();
		for (Set<State> component : components) {
			cycleStates.clear();
			cycleTransitions.clear();
			if (component.size() > 1) {
				// cycle
				cycleStates.addAll(component);
			} else {
				State s = component.iterator().next();
				if (dirGraph.containsEdge(s, s)) {
					// self-loop
					cycleStates.add(s);
				}
			}

			// fill cycle relationship
			if (cycleStates.size() > 0) {
				// get states set in cycle
				cycleStatesList = cycleStates.toArray();
				for (int i = 0; i < cycleStatesList.length; i++) {
					for (int j = i; j < cycleStatesList.length; j++) {
						cycleTransitions.addAll(this.getEdgesBetween(
								(State) cycleStatesList[i],
								(State) cycleStatesList[j]));
						cycleTransitions.addAll(this.getEdgesBetween(
								(State) cycleStatesList[j],
								(State) cycleStatesList[i]));
					}
				}

				// get transitions set in cycle
				HashSet<Integer> cycleTransitionIndexSet = new HashSet<Integer>();
				for (FSMTransition t : cycleTransitions) {
					Transition cur = (Transition) t.object;
					cycleTransitionIndexSet.add(getTransitionIndex(cur));

					if ((cur.object2 != null)
							&& (cur.object2 instanceof SimpleParallelStructure)) {
						SimpleParallelStructure sps = (SimpleParallelStructure) cur.object2;
						Set<Transition> transInSps = sps.getAllTransitions();
						Iterator<Transition> it = transInSps.iterator();
						while (it.hasNext()) {
							cycleTransitionIndexSet.add(getTransitionIndex(it
									.next()));
						}

					}

				}
				Iterator it = cycleTransitionIndexSet.iterator();
				int[] arrCycleTransitionIndexs = new int[cycleTransitionIndexSet
						.size()];
				int k = 0;
				while (it.hasNext()) {
					arrCycleTransitionIndexs[k++] = (Integer) it.next();
				}
				int row = 0;
				int col = 0;
				for (int i = 0; i < k; i++) {
					row = arrCycleTransitionIndexs[i];
					for (int j = i; j < k; j++) {
						col = arrCycleTransitionIndexs[j];
						this.setRltIndirCasual(row, col);
						this.setRltIndirCasual(col, row);
						this.setRltIndirSucc(row, col);
						this.setRltIndirSucc(col, row);
						this.setRltCycle(row, col);
					}
				}
			}
		}
		// release
		dirGraph = null;
		itVertice = null;
		itEdges = null;
		inspector = null;
		components = null;
		cycleTransitions = null;
	}

	public void fillDirectSucc() {
		final String FLAG_VISITED = "Visited";
		ArrayList<FSMState> lstStates = new ArrayList();
		FSMState curState = this.getStartState();
		curState.setAttribute(FLAG_VISITED, FLAG_VISITED);
		lstStates.addAll(curState.getSuccessors());
		ArrayList inEdges = null;
		ArrayList outEdges = null;
		FSMTransition in = null;
		FSMTransition out = null;
		int inIndex = -1;
		int outIndex = -1;
		while (!lstStates.isEmpty()) {
			curState = (FSMState) lstStates.get(0);
			lstStates.remove(0);
			if (curState.getAttribute(FLAG_VISITED) != null) {
				continue;
			}
			curState.setAttribute(FLAG_VISITED, FLAG_VISITED);
			lstStates.addAll(curState.getSuccessors());
			inEdges = curState.getInEdges();
			outEdges = curState.getOutEdges();
			if (inEdges == null || outEdges == null)
				continue;
			for (int i = 0; i < inEdges.size(); i++) {
				if (inEdges.get(i) == null
						|| !(inEdges.get(i) instanceof FSMTransition)) {
					continue;
				}
				in = (FSMTransition) inEdges.get(i);
				inIndex = getTransitionIndex((Transition) in.object);
				for (int j = 0; j < outEdges.size(); j++) {
					if (outEdges.get(j) == null
							|| !(outEdges.get(j) instanceof FSMTransition)) {
						continue;
					}
					out = (FSMTransition) outEdges.get(j);
					outIndex = getTransitionIndex((Transition) out.object);
					setRltDirSucc(inIndex, outIndex);
				}
			}
		}

		// release
		lstStates = null;
		curState = null;
		inEdges = null;
		outEdges = null;
		in = null;
		out = null;
	}

	public void fillIndirectSucc() {
		boolean flag = true;
		// int i=0;
		while (flag) {
			flag = false;

			for (int row = 0; row < brm.transitionNum; row++) {
				for (int col = 0; col < brm.transitionNum; col++) {
					if (!isRltIndirSucc(row, col)) {
						for (int k = 0; k < brm.transitionNum; k++) {
							// i++;
							if (row != col
									&& (isRltDirSucc(row, k) || isRltIndirSucc(
											row, k))
									&& (isRltDirSucc(k, col) || isRltIndirSucc(
											k, col))) // &&k!=row && k!=col
							{
								setRltIndirSucc(row, col);
								// System.out.println("[" + row + ", " + col +
								// "]  = " + "[" + row + ", " + k + "] + [" + k+
								// ", " + col + "]");
								flag = true;
								break;
							}
						}
					}
				}
			}
		}
		// System.out.println("i:=" + i);
	}

	public void fillDirectCasual() {
		for (int i = 0; i < brm.transitionNum; i++) {
			for (int j = 0; j < brm.transitionNum; j++) {
				if (isRltDirSucc(i, j) && !isRltParallel(i, j)) {
					setRltDirCasual(i, j);
				}
			}
		}
	}

	public void fillIndirectCasual() {
		boolean flag = true;
		// int i=0;
		while (flag) {
			flag = false;
			for (int row = 0; row < brm.transitionNum; row++) {
				for (int col = 0; col < brm.transitionNum; col++) {

					if (!isRltIndirCasual(row, col)) {
						for (int k = 0; k < brm.transitionNum; k++) {
							// if row == col, means self_indirect_casual, so the
							// transition must be in cycle that has been
							// processed in fillCycle() function.
							if (row != col
									&& (isRltDirCasual(row, k) || isRltIndirCasual(
											row, k))
									&& (isRltDirCasual(k, col) || isRltIndirCasual(
											k, col))) {
								// i++;
								setRltIndirCasual(row, col);
								flag = true;
								break;
							}
						}
					}
				}
			}
		}
		// System.out.println("j:=" + i);
	}

	public void fillMutex() {
		for (int row = 0; row < brm.transitionNum; row++) {
			for (int col = 0; col < brm.transitionNum; col++) {
				if ((!isRltIndirCasual(row, col) && !isRltIndirCasual(col, row))
						&& (!isRltDirCasual(row, col) && !isRltDirCasual(col,
								row))
						&& (!isRltDirSucc(row, col) && !isRltDirSucc(col, row))
						&& row != col) {
					setRltMutex(row, col);
				}
			}
		}
	}

	public void mergeBySameLabel() {
		brm.mergeBySameLabel();
	}

	private void initRltMatrix() {
		brm.transition2int = new HashMap<Transition, Integer>();
		Iterator<Transition> itTransition = this.getPetriNet().getTransitions()
				.iterator();
		int index = 0;
		while (itTransition.hasNext()) {
			brm.transition2int.put(itTransition.next(), index);
			index++;
		}
		brm.transitionNum = index;
		brm.rltMatrix = new byte[index][index];
		for (int i = 0; i < index; i++)
			for (int j = 0; j < index; j++)
				brm.rltMatrix[i][j] = 0;
		itTransition = null;
	}

	public int getTransitionIndex(Transition t) {
		return brm.transition2int.get(t);
	}

	public byte getRlt(Transition t1, Transition t2) {
		return brm.getRlt(t1, t2);
	}

	public void setRlt(Transition t1, Transition t2, byte value) {
		brm.setRlt(t1, t2, value);
	}

	public byte getRlt(int i1, int i2) {
		return brm.getRlt(i1, i2);
	}

	public void setRlt(int i1, int i2, byte value) {
		brm.setRlt(i1, i2, value);
	}

	public void setRltParallel(int i1, int i2) {
		brm.setRltParallel(i1, i2);
	}

	public void setRltDirSucc(int i1, int i2) {
		brm.setRltDirSucc(i1, i2);
	}

	public void setRltIndirSucc(int i1, int i2) {
		brm.setRltIndirSucc(i1, i2);
	}

	public void setRltDirCasual(int i1, int i2) {
		brm.setRltDirCasual(i1, i2);
	}

	public void setRltIndirCasual(int i1, int i2) {
		brm.setRltIndirCasual(i1, i2);
	}

	public void setRltCycle(int i1, int i2) {
		brm.setRltCycle(i1, i2);
	}

	public void setRltMutex(int i1, int i2) {
		brm.setRltMutex(i1, i2);
	}

	public boolean isRltParallel(int i1, int i2) {
		return brm.isRltParallel(i1, i2);
	}

	public boolean isRltDirSucc(int i1, int i2) {
		return brm.isRltDirSucc(i1, i2);
	}

	public boolean isRltIndirSucc(int i1, int i2) {
		return brm.isRltIndirSucc(i1, i2);
	}

	public boolean isRltDirCasual(int i1, int i2) {
		return brm.isRltDirCasual(i1, i2);
	}

	public boolean isRltIndirCasual(int i1, int i2) {
		return brm.isRltIndirCasual(i1, i2);
	}

	public boolean isRltMutex(int i1, int i2) {
		return brm.isRltMutex(i1, i2);
	}

	public Iterator<Map.Entry<Transition, Integer>> getTrnEntrysIterator() {
		return brm.getTrnEntrysIterator();
	}

	public void print() {
		brm.print();
	}

	public void printFinal() {
		brm.printBySortedLabel();
	}

	public void setProcessId(long id) {
		brm.process_id = id;
	}

	public long getProcessId() {
		return brm.process_id;
	}

	public byte[] serializeTran2IntMap() {
		return brm.serializeTran2IntMap();
	}

	public byte[] serializeLabel2IntMap() {
		return brm.serializeLabel2IntMap();
	}

	public boolean deserializeTran2IntMap(byte[] input) {
		return brm.deserializeTran2IntMap(input);
	}

	public boolean deserializeLabel2IntMap(byte[] input) {
		return brm.deserializeLabel2IntMap(input);
	}

	public byte[] serializeMatrix() {
		return brm.serializeMatrix();
	}

	public byte[] serializeMatrixByLabel() {
		return brm.serializeMatrixByLabel();
	}

	public boolean deserializeMatrix(byte[] input) {
		return brm.deserializeMatrix(input);
	}

	public boolean deserializeMatrixByLabel(byte[] input) {
		return brm.deserializeMatrixByLabel(input);
	}

}
