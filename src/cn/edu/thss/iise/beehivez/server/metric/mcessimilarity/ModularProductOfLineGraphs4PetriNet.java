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
package cn.edu.thss.iise.beehivez.server.metric.mcessimilarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;

/**
 * Given two Petri nets, the line graphs can be computed, and then the modular
 * product graph can be computed. Based on the modular product graph, all the
 * cliques can be detected, which is the maximum common edge subgraph of the two
 * given Petri nets. It is not necessary that the cliques must be connected.
 * 
 * The modular product graph of line graphs of Petri nets is based on simple
 * graph.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-17
 * 
 */
public class ModularProductOfLineGraphs4PetriNet {

	// store the modular product graph of line graphs for Petri net
	private SimpleGraph<MNode, DefaultEdge> graph = null;

	// store the two given Petri nets
	private PetriNet pn1 = null;
	private PetriNet pn2 = null;

	// store the task lines of the two given Petri nets
	private ArrayList<TaskLine4PetriNet> tls1 = null;
	private ArrayList<TaskLine4PetriNet> tls2 = null;

	public ModularProductOfLineGraphs4PetriNet(PetriNet pn1, PetriNet pn2) {
		this.pn1 = pn1;
		this.pn2 = pn2;
		tls1 = TaskLine4PetriNet.getAllTaskLinesOfPetriNet(pn1);
		tls2 = TaskLine4PetriNet.getAllTaskLinesOfPetriNet(pn2);
		buildRGraph();
	}

	/**
	 * build the modular product of line graphs for Petri nets, which is based
	 * on RGraph
	 */
	private void buildRGraph() {
		this.graph = new SimpleGraph<MNode, DefaultEdge>(DefaultEdge.class);
		nodeConstructor();
		arcConstructor();
	}

	/**
	 * construct the nodes for modular product graph, every node is a pair of
	 * task lines. if two task lines are equal, it means that the source and
	 * detination of the lines are equal respectively.
	 */
	private void nodeConstructor() {
		for (int i = 0; i < tls1.size(); i++) {
			TaskLine4PetriNet tl1 = tls1.get(i);
			for (int j = 0; j < tls2.size(); j++) {
				TaskLine4PetriNet tl2 = tls2.get(j);
				boolean addNewNode = false;
				if (GlobalParameter.isEnableSimilarLabel()) {
					if (StringSimilarityUtil.semanticSimilarity(tl1
							.getSrcTransition().getIdentifier(), tl2
							.getSrcTransition().getIdentifier()) >= GlobalParameter
							.getLabelSemanticSimilarity()
							&& StringSimilarityUtil.semanticSimilarity(tl1
									.getDestTransition().getIdentifier(), tl2
									.getDestTransition().getIdentifier()) >= GlobalParameter
									.getLabelSemanticSimilarity()) {
						addNewNode = true;
					}
				} else {
					if (tl1.getSrcTransition().getIdentifier().equals(
							tl2.getSrcTransition().getIdentifier())
							&& tl1.getDestTransition().getIdentifier().equals(
									tl2.getDestTransition().getIdentifier())) {
						addNewNode = true;
					}
				}

				if (addNewNode) {
					graph.addVertex(new MNode(i, j));
				}
			}
		}
	}

	/**
	 * Build edges of the modular product graph. If two line share the same
	 * transition respectively, the nodes in modular graph are connected.
	 */
	private void arcConstructor() {
		// each node is incompatible with himself
		TaskLine4PetriNet a1 = null;
		TaskLine4PetriNet a2 = null;
		TaskLine4PetriNet b1 = null;
		TaskLine4PetriNet b2 = null;

		// two nodes are connected if their adjacency
		// relationship are equivalent in pn1 and pn2
		// else they are not connected.
		MNode[] gvs = graph.vertexSet().toArray(new MNode[0]);
		for (int i = 0; i < gvs.length; i++) {
			MNode x = gvs[i];
			for (int j = i + 1; j < gvs.length; j++) {
				MNode y = gvs[j];
				a1 = tls1.get(x.getId1());
				a2 = tls2.get(x.getId2());
				b1 = tls1.get(y.getId1());
				b2 = tls2.get(y.getId2());

				boolean isConnected = false;
				if (!a1.equals(b1) && !a2.equals(b2)) {
					TaskLine4PetriNet tl1 = adjacency(a1, b1);
					TaskLine4PetriNet tl2 = adjacency(a2, b2);
					if (tl1 == a1 && tl2 == a2 || tl1 == b1 && tl2 == b2) {
						Transition adj1 = tl1.getDestTransition();
						Transition adj2 = tl2.getDestTransition();
						if (GlobalParameter.isEnableSimilarLabel()) {
							if (StringSimilarityUtil.semanticSimilarity(adj1
									.getIdentifier(), adj2.getIdentifier()) >= GlobalParameter
									.getLabelSemanticSimilarity()) {
								isConnected = true;
							}
						} else {
							if (adj1.getIdentifier().equals(
									adj2.getIdentifier())) {
								isConnected = true;
							}
						}
					} else if (tl1 == null && tl2 == null) {
						isConnected = true;
					}
				}
				if (isConnected) {
					graph.addEdge(x, y);
				}
			}
		}
	}

	/**
	 * determine whether two given task lines are adjacent to each other, if so
	 * return the common transition, else return null. To take the direction
	 * into consideration, return the task line whose destTransition is the
	 * common transition.
	 * 
	 * @param tl1
	 * @param tl2
	 * @return
	 */
	private TaskLine4PetriNet adjacency(TaskLine4PetriNet tl1,
			TaskLine4PetriNet tl2) {
		TaskLine4PetriNet res = null;
		if (tl1.getDestTransition() == tl2.getSrcTransition()) {
			res = tl1;
		} else if (tl2.getDestTransition() == tl1.getSrcTransition()) {
			res = tl2;
		}
		return res;
	}

	private Collection<Set<MNode>> getBiggestMaximalCliques() {
		BronKerboschCliqueFinder<MNode, DefaultEdge> finder = new BronKerboschCliqueFinder<MNode, DefaultEdge>(
				graph);
		Collection<Set<MNode>> cliques = finder.getBiggestMaximalCliques();
		return cliques;
	}

	public int getNumberOfVerticesInvolvedInBiggestClique() {

		BronKerboschCliqueFinder<MNode, DefaultEdge> finder = new BronKerboschCliqueFinder<MNode, DefaultEdge>(
				graph);
		Collection<Set<MNode>> cliques = finder.getAllMaximalCliques();
		Iterator<Set<MNode>> it = cliques.iterator();
		int max = 0;
		while (it.hasNext()) {
			int size = it.next().size();
			if (size > max) {
				max = size;
			}
		}

		return max;
	}

	public int getNumberOfTaskLinesOfPN1() {
		return tls1.size();
	}

	public int getNumberOfTaskLinesOfPN2() {
		return tls2.size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
