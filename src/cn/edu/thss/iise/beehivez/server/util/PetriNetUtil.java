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
 * only contains static functions
 * used for petri net analysis
 */
package cn.edu.thss.iise.beehivez.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.State;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.CoverabilityGraphBuilder;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import att.grappa.Edge;
import att.grappa.Node;

import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.ResourcePetriNet;
import cn.edu.thss.iise.beehivez.server.metric.mcessimilarity.MCESSimilarity4PetriNet;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.Condition;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.Event;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.OrderingRelation;

/**
 * @author Tao Jin
 * 
 */
public class PetriNetUtil {
	private static final boolean debug = false;

	public static byte[] getPnmlBytesFromFile(String file) {
		byte[] ret = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ret = ToolKit.getBytesFromInputStream(fis);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static byte[] getPnmlBytes(PetriNet pn) {
		byte[] ret = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStreamWriter ow = new OutputStreamWriter(bos);
			BufferedWriter bw = new BufferedWriter(ow);

			PnmlWriter.write(false, true, pn, bw);

			bw.close();
			ow.close();
			ret = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static PetriNet getPetriNetFromPnmlBytes(byte[] pnml) {
		PetriNet pn = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(pnml);
			pn = getPetriNetFromPnml(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pn;
	}

	public static float tarSimilarity(HashSet<TransitionLabelPair> tars1,
			HashSet<TransitionLabelPair> tars2) {

		HashSet<TransitionLabelPair> numerator = new HashSet<TransitionLabelPair>();
		HashSet<TransitionLabelPair> denominator = new HashSet<TransitionLabelPair>();

		numerator.addAll(tars1);
		denominator.addAll(tars1);

		numerator.retainAll(tars2);
		denominator.addAll(tars2);

		float ret = (float) numerator.size() / (float) denominator.size();

		// numerator = null;
		// denominator = null;
		// System.gc();

		return ret;
	}

	// calculate the similarity between two petri nets based on tar
	public static float tarSimilarity(PetriNet pn1, PetriNet pn2) {
		HashSet<TransitionLabelPair> tars1 = getTARSFromPetriNetByCFP(pn1);
		HashSet<TransitionLabelPair> tars2 = getTARSFromPetriNetByCFP(pn2);

		float ret = tarSimilarity(tars1, tars2);

		// tars1 = null;
		// tars2 = null;
		// System.gc();

		return ret;
	}

	public static void export2pnml(PetriNet pn, String filename) {
		if (filename != null && !filename.equals("")) {
			try {
				FileWriter fw = new FileWriter(filename, false);
				BufferedWriter bw = new BufferedWriter(fw);
				PnmlWriter.write(false, true, pn, bw);
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * compute the causal relations using unfolding technology
	 * 
	 * @param PetriNet
	 */
	public static HashSet<TransitionLabelPair> getCauRelationsFromPetriNet(
			PetriNet pn) {
		CompleteFinitePrefix cfp = buildCompleteFinitePrefix(pn);
		HashSet<TransitionLabelPair> result = new HashSet<TransitionLabelPair>();
		OrderingRelation[][] orderingRelations = cfp.getOrderingRelations();
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = i + 1; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.PRECEDENCE) {
					result.add(new TransitionLabelPair(cfp.getTransitions()
							.get(i).getIdentifier(), cfp.getTransitions()
							.get(j).getIdentifier()));
					result.add(new TransitionLabelPair(cfp.getTransitions()
							.get(j).getIdentifier(), cfp.getTransitions()
							.get(i).getIdentifier()));
				}
			}
		}
		return result;
	}

	/**
	 * compute the TARS using unfolding technology
	 * 
	 * @param pn
	 * @return
	 */
	public static HashSet<TransitionLabelPair> getTARSFromPetriNetByCFP(
			PetriNet pn) {
		HashSet<TransitionLabelPair> ret = new HashSet<TransitionLabelPair>();
		CompleteFinitePrefix cfp = buildCompleteFinitePrefix(pn);
		OrderingRelation[][] orderingRelations = cfp.getOrderingRelations();

		// deal with parallel relations first;
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = i + 1; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.CONCURRENCY) {
					ret.add(new TransitionLabelPair(cfp.getTransitions().get(i)
							.getIdentifier(), cfp.getTransitions().get(j)
							.getIdentifier()));
					ret.add(new TransitionLabelPair(cfp.getTransitions().get(j)
							.getIdentifier(), cfp.getTransitions().get(i)
							.getIdentifier()));
				}
			}
		}

		// traverse cfp
		for (Place p : cfp.getPlaces()) {
			Iterator<Transition> ittpre = p.getPredecessors().iterator();
			if (ittpre.hasNext()) {
				while (ittpre.hasNext()) {
					Transition tPre = ittpre.next();
					Iterator<Transition> ittsuc = p.getSuccessors().iterator();
					while (ittsuc.hasNext()) {
						Transition tSuc = ittsuc.next();

						// consider the situation with one place between two
						// transitions which are and-split and and-join
						// transition
						boolean flag = true;
						if (tPre.getSuccessors().size() > 1
								&& tSuc.getPredecessors().size() > 1) {
							Iterator<Place> itp = tPre.getSuccessors()
									.iterator();
							while (itp.hasNext()) {
								Place pp = itp.next();
								if (pp != p) {
									if (maxLength(pp, tSuc) > 1) {
										flag = false;
										break;
									}
								}
							}
						}

						if (flag) {
							ret.add(new TransitionLabelPair(tPre
									.getIdentifier(), tSuc.getIdentifier()));
						}

					}
				}
			} else {
				Iterator<Transition> ittsuc = p.getSuccessors().iterator();
				while (ittsuc.hasNext()) {
					Transition tSuc = ittsuc.next();
					ret.add(new TransitionLabelPair("null", tSuc
							.getIdentifier()));
				}
			}
		}

		// deal with cut off events
		for (Event e : cfp.getCutOffEvents()) {
			int indexe = cfp.getTransitions().indexOf(e);
			// corresponding event
			Event ce = (Event) e.object;
			Iterator<Condition> itcsuc = ce.getSuccessors().iterator();
			while (itcsuc.hasNext()) {
				Condition csuc = itcsuc.next();

				boolean flag = false;
				Iterator<Condition> itp = e.getSuccessors().iterator();
				while (itp.hasNext()) {
					if (itp.next().getOriginalPlace() == csuc
							.getOriginalPlace()) {
						flag = true;
						break;
					}
				}
				if (flag) {
					Iterator<Event> ittsuc = csuc.getSuccessors().iterator();
					while (ittsuc.hasNext()) {
						Event esuc = ittsuc.next();

						// deal with situation with useless place between
						// and-split and and-join transitions
						boolean fflag = true;

						if (orderingRelations[indexe][indexe] != OrderingRelation.PRECEDENCE) {
							if (ce.getSuccessors().size() > 1
									&& esuc.getPredecessors().size() > 1) {
								Iterator<Place> itpsuc = ce.getSuccessors()
										.iterator();
								while (itpsuc.hasNext()) {
									Place pp = itpsuc.next();
									if (pp != csuc) {
										if (maxLength(pp, esuc) > 1) {
											fflag = false;
											break;
										}
									}
								}
							}
						}

						if (fflag) {
							ret.add(new TransitionLabelPair(e.getIdentifier(),
									esuc.getIdentifier()));
						}

					}
				}
			}
		}

		// deal with the local parallel
		int mark = 0;
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = i + 1; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.PRECEDENCE
						&& orderingRelations[j][i] == OrderingRelation.PRECEDENCE) {
					// check whether the nearest common parent is a transition,
					// if so, they are local parallel
					Event event1 = (Event) cfp.getTransitions().get(i);
					Event event2 = (Event) cfp.getTransitions().get(j);
					if (canBeExecutedParallelly(event1, event2, mark, mark + 1)) {
						ret.add(new TransitionLabelPair(event1.getIdentifier(),
								event2.getIdentifier()));
						ret.add(new TransitionLabelPair(event2.getIdentifier(),
								event1.getIdentifier()));
					}
					mark += 2;
				}
			}
		}

		cfp.destroyCFP();

		return ret;
	}

	/**
	 * 
	 * use the coverability graph
	 * 
	 * @param pn
	 * @return
	 */
	public static HashSet<TransitionLabelPair> getTARSFromPetriNetByCG(
			PetriNet pn) {
		HashSet<TransitionLabelPair> ret = new HashSet<TransitionLabelPair>();

		StateSpace coverabilityGraph = buildCoverabilityGraph(pn);

		// get all tars from the coverability graph
		ArrayList states = coverabilityGraph.getVerticeList();

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

			HashSet<String> preConditions = new HashSet<String>();
			for (int in = 0; in < inEdges.size(); in++) {
				String preCondition = ((ModelGraphEdge) inEdges.get(in))
						.getDotAttribute("label");
				if (preCondition == null) {
					preConditions.add("null");
				} else {
					preConditions.add(preCondition);
				}
			}

			HashSet<String> postConditions = new HashSet<String>();
			for (int out = 0; out < outEdges.size(); out++) {
				String postCondition = ((ModelGraphEdge) outEdges.get(out))
						.getDotAttribute("label");
				if (postCondition == null) {
					postConditions.add("null");
				} else {
					postConditions.add(postCondition);
				}
			}

			// // can deal with silent tasks
			// // calculate the pre-conditions
			// HashSet<String> preConditions = new HashSet<String>();
			// for (int in = 0; in < inEdges.size(); in++) {
			// String preCondition = ((ModelGraphEdge) inEdges.get(in))
			// .getDotAttribute("label");
			// if (preCondition == null) {
			// preConditions.add("null");
			// } else if (!preCondition.equals("")) {
			// preConditions.add(preCondition);
			// }
			// }
			//
			// // calculate the post-conditions
			// HashSet<ModelGraphEdge> visited = new HashSet<ModelGraphEdge>();
			// HashSet<ModelGraphEdge> toBeExtended = new
			// HashSet<ModelGraphEdge>();
			// ConcurrentLinkedQueue<ModelGraphEdge> queue = new
			// ConcurrentLinkedQueue<ModelGraphEdge>();
			// HashSet<String> postConditions = new HashSet<String>();
			//
			// for (int out = 0; out < outEdges.size(); out++) {
			// ModelGraphEdge outEdge = (ModelGraphEdge) outEdges.get(out);
			// if (visited.add(outEdge)) {
			// String sucCondition = outEdge.getDotAttribute("label");
			// if (!sucCondition.equals("")) {
			// postConditions.add(sucCondition);
			// } else {
			// if (toBeExtended.add(outEdge)) {
			// queue.add(outEdge);
			// }
			// }
			// }
			// }
			//
			// // extend the edges attached with silent task
			// while (!queue.isEmpty()) {
			// ModelGraphEdge silentEdge = queue.poll();
			// Iterator itExEdge = silentEdge.getDest().getOutEdges()
			// .iterator();
			// while (itExEdge.hasNext()) {
			// ModelGraphEdge outEdge = (ModelGraphEdge) itExEdge.next();
			// if (visited.add(outEdge)) {
			// String sucCondition = outEdge.getDotAttribute("label");
			// if (sucCondition.equals("")) {
			// if (toBeExtended.add(outEdge)) {
			// queue.add(outEdge);
			// }
			// } else {
			// postConditions.add(sucCondition);
			// }
			// }
			// }
			// }

			Iterator<String> itPre = preConditions.iterator();
			while (itPre.hasNext()) {
				String preCondition = itPre.next();
				Iterator<String> itPost = postConditions.iterator();
				while (itPost.hasNext()) {
					String postCondition = itPost.next();
					ret
							.add(new TransitionLabelPair(preCondition,
									postCondition));
				}
			}
		}
		// states = null;
		coverabilityGraph.destroyStateSpace();
		// coverabilityGraph.removeEmptySubgraphs();
		coverabilityGraph = null;

		// source = null;
		// System.gc();
		return ret;
	}

	public static PetriNet getPetriNetFromPnmlFile(File file) {
		PetriNet pn = null;
		try {
			FileInputStream in = new FileInputStream(file);
			pn = getPetriNetFromPnml(in);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pn;
	}

	public static PetriNet getPetriNetFromPnmlFile(String fileName) {
		PetriNet pn = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			pn = getPetriNetFromPnml(in);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pn;
	}

	public static PetriNet getPetriNetFromPnml(InputStream pnml) {
		PetriNet pn = null;
		try {
			PnmlImport pnmlImport = new PnmlImport();
			PetriNetResult result;
			result = (PetriNetResult) pnmlImport.importFile(pnml);
			pn = result.getPetriNet();
			result.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pn;
	}

	public static boolean isInLengthOneLoop(Transition t) {
		HashSet inPlaces = t.getPredecessors();
		HashSet outPlaces = t.getSuccessors();
		if (inPlaces.size() == 0 || outPlaces.size() == 0) {
			return false;
		}

		inPlaces.retainAll(outPlaces);
		if (inPlaces.size() == 0) {
			return false;
		} else {
			return true;
		}

	}

	public static boolean isInLengthTwoLoop(Transition t1, Transition t2) {
		HashSet inPlaces1 = t1.getPredecessors();
		HashSet outPlaces1 = t1.getSuccessors();
		HashSet inPlaces2 = t2.getPredecessors();
		HashSet outPlaces2 = t2.getSuccessors();

		if (inPlaces1.size() == 0 || outPlaces1.size() == 0
				|| inPlaces2.size() == 0 || outPlaces2.size() == 0) {
			return false;
		}

		outPlaces1.retainAll(inPlaces2);
		outPlaces2.retainAll(inPlaces1);

		if (outPlaces1.size() == 0 || outPlaces2.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean existCommunication(Transition startTransition,
			Transition endTransition) {
		if (debug) {
			System.out.println("existCommunication begin");
		}
		Iterator it1 = startTransition.getSuccessors().iterator();
		HashSet set2 = endTransition.getPredecessors();
		while (it1.hasNext()) {
			Place pSuc1 = (Place) it1.next();
			if (set2.contains(pSuc1)) {
				return true;
			}
		}
		return false;
	}

	public static boolean existSynchronization(Place p1, Place p2) {
		if (debug) {
			System.out.println("existSynchronization begin");
		}
		Iterator it1 = p1.getSuccessors().iterator();
		HashSet set2 = p2.getSuccessors();
		while (it1.hasNext()) {
			Transition tSuc1 = (Transition) it1.next();
			if (set2.contains(tSuc1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * how many edges exist in the path from startNode to endNode, breadth first
	 * traverse
	 * 
	 * @param startNode
	 * @param endNode
	 * @return
	 */
	public static int maxLength(PNNode startNode, PNNode endNode) {
		int len = 0;
		int level = 0;
		boolean found = false;
		HashSet<PNNode> visited = new HashSet<PNNode>();
		ArrayList<PNNode> queue = new ArrayList<PNNode>();
		queue.add(startNode);
		while (!queue.isEmpty()) {
			boolean record = false;
			ArrayList<PNNode> newQueue = new ArrayList<PNNode>();
			for (PNNode node : queue) {
				visited.add(node);
				if (node == endNode) {
					record = true;
					found = true;
				}
				Iterator<PNNode> itnsuc = node.getSuccessors().iterator();
				while (itnsuc.hasNext()) {
					PNNode newNode = itnsuc.next();
					if (!visited.contains(newNode)) {
						newQueue.add(newNode);
					}
				}
			}
			if (record) {
				len = level;
			}
			level++;
			queue = newQueue;
		}

		if (found) {
			return len;
		} else {
			return Integer.MIN_VALUE;
		}
	}

	public static boolean isParallel(Transition t1, Transition t2, PetriNet pn) {
		PNNode n = nearestCommonAncestor(t1, t2, pn);
		if (n != t1 && n != t2 && n instanceof Transition) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * the input Petri net must be a workflow net, node1 and node2 must belong
	 * to the same workflow net
	 */
	public static PNNode nearestCommonAncestor(PNNode node1, PNNode node2,
			PetriNet pn) {
		HashMap<PNNode, Integer> levels = numberLevelOfNodes(pn);

		// the nodes in the queue are descending ordered by level
		// the queue with bigger level
		ArrayList<PNNode> queue1 = new ArrayList<PNNode>();

		// the queue with smaller level
		ArrayList<PNNode> queue2 = new ArrayList<PNNode>();

		queue1.add(node1);
		queue2.add(node2);

		while (true) {

			int level1 = levels.get(queue1.get(0));
			int level2 = levels.get(queue2.get(0));

			while (level1 != level2) {
				// the smaller level
				int level = level1 > level2 ? level2 : level1;

				if (level == level1) {
					ArrayList<PNNode> q = queue1;
					queue1 = queue2;
					queue2 = q;
				}

				// deal with the queue with bigger level
				while (queue1.size() > 0) {
					PNNode n = queue1.get(0);
					int l = levels.get(n);
					if (l > level) {
						queue1.remove(0);
						Iterator<PNNode> itPre = n.getPredecessors().iterator();
						while (itPre.hasNext()) {
							PNNode pre = itPre.next();
							int preLevel = levels.get(pre);
							if (preLevel >= l) {
								continue;
							}
							int k = 0;
							for (k = 0; k < queue1.size(); k++) {
								PNNode temp = queue1.get(k);
								int tempLevel = levels.get(temp);
								if (preLevel > tempLevel) {
									break;
								}
							}
							queue1.add(k, pre);
						}
					} else {
						break;
					}
				}

				level1 = levels.get(queue1.get(0));
				level2 = levels.get(queue2.get(0));
			}

			// level1 == level2
			for (int i = 0; i < queue1.size(); i++) {
				PNNode n1 = queue1.get(i);
				if (levels.get(n1) < level1) {
					break;
				}
				for (int j = 0; j < queue2.size(); j++) {
					PNNode n2 = queue2.get(j);
					if (levels.get(n2) < level2) {
						break;
					}
					if (n1 == n2) {
						return n1;
					}
				}
			}

			// delete the nodes with equal level and add the predecessor nodes
			int l1 = levels.get(queue1.get(0));
			while (l1 == level1) {
				PNNode n1 = queue1.get(0);
				queue1.remove(0);
				Iterator<PNNode> itPre = n1.getPredecessors().iterator();
				while (itPre.hasNext()) {
					PNNode pre = itPre.next();
					int preLevel = levels.get(pre);
					if (preLevel >= l1) {
						continue;
					}
					int k = 0;
					for (k = 0; k < queue1.size(); k++) {
						PNNode temp = queue1.get(k);
						int tempLevel = levels.get(temp);
						if (preLevel > tempLevel) {
							break;
						}
					}
					queue1.add(k, pre);
				}
				l1 = levels.get(queue1.get(0));
			}

			int l2 = levels.get(queue2.get(0));
			while (l2 == level2) {
				PNNode n2 = queue2.get(0);
				queue2.remove(0);
				Iterator<PNNode> itPre = n2.getPredecessors().iterator();
				while (itPre.hasNext()) {
					PNNode pre = itPre.next();
					int preLevel = levels.get(pre);
					if (preLevel >= l2) {
						continue;
					}
					int k = 0;
					for (k = 0; k < queue2.size(); k++) {
						PNNode temp = queue2.get(k);
						int tempLevel = levels.get(temp);
						if (preLevel > tempLevel) {
							break;
						}
					}
					queue2.add(k, pre);
				}
				l2 = levels.get(queue2.get(0));
			}

		}
	}

	private static HashMap<PNNode, Integer> numberLevelOfNodes(PetriNet pn) {
		HashMap<PNNode, Integer> result = new HashMap<PNNode, Integer>();

		// find the start place
		Place startPlace = null;
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() == 0) {
				startPlace = p;
				break;
			}
		}

		if (startPlace != null) {
			LinkedList<PNNode> queue = new LinkedList<PNNode>();
			queue.add(startPlace);
			result.put(startPlace, 0);
			while (queue.size() > 0) {
				PNNode node = queue.poll();
				int level = result.get(node);
				Iterator<PNNode> itSuc = node.getSuccessors().iterator();
				while (itSuc.hasNext()) {
					PNNode suc = itSuc.next();
					if (!result.containsKey(suc)) {
						result.put(suc, level + 1);
						queue.add(suc);
					}
				}
			}
		}

		return result;
	}

	/**
	 * check whether two events can be executed parallelly. if their nearest
	 * common ancestor is an event, then return true
	 * 
	 * @param mark1
	 *            , mark2 used to mark the visited node backwards from different
	 *            event
	 */
	public static boolean canBeExecutedParallelly(Event event1, Event event2,
			int mark1, int mark2) {
		// mark all the ancestors of the events, if some node is marked before,
		// that means the nearest common ancestor is found.
		// upward level by level
		if (event1 == event2) {
			return false;
		}

		ArrayList<Event> queue1 = new ArrayList<Event>();
		ArrayList<Event> queue2 = new ArrayList<Event>();
		event1.object2 = mark1;
		event2.object2 = mark2;
		queue1.add(event1);
		queue2.add(event2);
		int level1 = event1.getFoataLevel();
		int level2 = event2.getFoataLevel();
		int level = level1 > level2 ? level1 : level2;

		while (level > 0) {
			ArrayList<Event> temp1 = (ArrayList<Event>) queue1.clone();
			for (Event e : queue1) {
				if (e.getFoataLevel() == level) {
					temp1.remove(e);
					Iterator<Condition> itcpre = e.getPredecessors().iterator();
					while (itcpre.hasNext()) {
						Condition c = itcpre.next();
						boolean visited = false;
						if (c.object2 != null) {
							if (c.object2.equals(mark2)) {
								return false;
							} else if (c.object2.equals(mark1)) {
								visited = true;
							}
						}
						if (!visited) {
							c.object2 = mark1;
							Iterator<Event> itepre = c.getPredecessors()
									.iterator();
							while (itepre.hasNext()) {
								Event pree = itepre.next();
								if (pree == event2) {
									return false;
								}
								visited = false;
								if (pree.object2 != null) {
									if (pree.object2.equals(mark2)) {
										return true;
									} else if (pree.object2.equals(mark1)) {
										visited = true;
									}
								}
								if (!visited) {
									pree.object2 = mark1;
									temp1.add(pree);
								}
							}
						}
					}
				}
			}
			queue1 = temp1;

			ArrayList<Event> temp2 = (ArrayList<Event>) queue2.clone();
			for (Event e : queue2) {
				if (e.getFoataLevel() == level) {
					temp2.remove(e);
					Iterator<Condition> itcpre = e.getPredecessors().iterator();
					while (itcpre.hasNext()) {
						Condition c = itcpre.next();
						boolean visited = false;
						if (c.object2 != null) {
							if (c.object2.equals(mark1)) {
								return false;
							} else if (c.object2.equals(mark2)) {
								visited = true;
							}
						}
						if (!visited) {
							c.object2 = mark2;
							Iterator<Event> itepre = c.getPredecessors()
									.iterator();
							while (itepre.hasNext()) {
								Event pree = itepre.next();
								if (pree == event1) {
									return false;
								}
								visited = false;
								if (pree.object2 != null) {
									if (pree.object2.equals(mark1)) {
										return true;
									} else if (pree.object2.equals(mark2)) {
										visited = true;
									}
								}
								if (!visited) {
									pree.object2 = mark2;
									temp2.add(pree);
								}
							}
						}
					}
				}
			}
			queue2 = temp2;

			level--;
		}
		return true;
	}

	/**
	 * used to check whether the start node can reach the end node in the Petri
	 * net
	 */
	public static boolean canReach(PNNode startNode, PNNode endNode) {
		if (debug) {
			System.out.println("canReach begin");
		}
		// search by breadth-first
		// use a queue
		// if the queue is empty, the check is end

		// initialize the queue
		if (startNode == endNode) {
			if (debug) {
				System.out.println("canReach end");
			}
			return true;
		}

		LinkedList<PNNode> queue = new LinkedList<PNNode>();
		Iterator it = startNode.getSuccessors().iterator();
		while (it.hasNext()) {
			PNNode node = (PNNode) it.next();
			queue.offer(node);
		}

		// check
		PNNode node = queue.poll();
		Vector<PNNode> visited = new Vector<PNNode>();
		while (node != null) {
			visited.add(node);
			if (node == endNode) {
				if (debug) {
					System.out.println("canReach end");
				}
				return true;
			} else {
				Iterator it1 = node.getSuccessors().iterator();
				while (it1.hasNext()) {
					PNNode suc = (PNNode) it1.next();
					if (!visited.contains(suc) && !queue.contains(suc)) {
						queue.offer(suc);
					}
				}
				node = queue.poll();
			}
		}

		if (debug) {
			System.out.println("canReach end");
		}
		return false;
	}

	/**
	 * @param pn
	 *            workflow net ignoring all the tokens it has
	 * @return
	 */
	public static StateSpace buildCoverabilityGraph(PetriNet pn, int depth) {
		for (Place place : pn.getPlaces()) {
			place.removeAllTokens();
			if (place.inDegree() == 0) {
				place.addToken(new Token());
			}
		}
		return CoverabilityGraphBuilder.build(pn, depth);
	}

	public static StateSpace buildCoverabilityGraph(PetriNet pn) {
		for (Place place : pn.getPlaces()) {
			place.removeAllTokens();
			if (place.inDegree() == 0) {
				place.addToken(new Token());
			}
		}
		return CoverabilityGraphBuilder.build(pn);
	}

	public static CompleteFinitePrefix buildCompleteFinitePrefix(PetriNet pn) {
		for (Place place : pn.getPlaces()) {
			place.removeAllTokens();
			if (place.inDegree() == 0) {
				place.addToken(new Token());
			}
		}
		return CompleteFinitePrefixBuilder.build(pn, Integer.MAX_VALUE);
	}

	public static float mcesSimilarity(PetriNet pn1, PetriNet pn2) {
		return MCESSimilarity4PetriNet.similarity(pn1, pn2);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File("e:/test");
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				String pnFileName = f.getAbsolutePath();
				PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(pnFileName);

				HashSet<TransitionLabelPair> cgtars = getTARSFromPetriNetByCG(pn);
				HashSet<TransitionLabelPair> cfptars = getTARSFromPetriNetByCFP(pn);

				Iterator<TransitionLabelPair> ittlp = cgtars.iterator();
				HashSet<TransitionLabelPair> ret = new HashSet<TransitionLabelPair>();
				while (ittlp.hasNext()) {
					TransitionLabelPair tlp = ittlp.next();
					if (!cfptars.contains(tlp)) {
						ret.add(tlp);
					}
				}
				if (!ret.isEmpty()) {
					System.out.println(pnFileName);
					System.out.println("tar in cgtars not in cfptars: ");
					System.out.println(ret.toString());
				}

				ittlp = cfptars.iterator();
				ret = new HashSet<TransitionLabelPair>();
				while (ittlp.hasNext()) {
					TransitionLabelPair tlp = ittlp.next();
					if (!cgtars.contains(tlp)) {
						ret.add(tlp);
					}
				}
				if (!ret.isEmpty()) {
					System.out.println(pnFileName);
					System.out.println("tar in cfptars not in cgtars");
					System.out.println(ret.toString());
				}

				// if (cgtars.containsAll(cfptars) &&
				// cfptars.containsAll(cgtars)) {
				// System.out.println("equal");
				// } else {
				// System.out.println("cfptars:");
				// System.out.println(cfptars.toString());
				// System.out.println("cgtars:");
				// System.out.println(cgtars.toString());
				// }
			}
		}
		// for (int i = 0; i < 50000; i++) {
		// PetriNet pn = getPetriNetFromPnmlFile("e:/test7.pnml");
		// System.out.println(getTARSFromPetriNetByCG(pn).toString());
		// // ((Place)pn.getSource()).addToken(new Token());
		//
		// System.out.println(i);
		// // CoverabilityGraphBuilder.build(pn);
		// // System.out.println(getTARSFromPetriNet(pn));
		// System.out.println("the similarity is: " + tarSimilarity(pn, pn));
		// }
		//
		// PetriNet[] pns = new PetriNet[10];
		//
		// for (int k = 0; k < 500; k++) {
		// System.out.println("the runs k: " + k);
		// for (int i = 0; i < 10; i++) {
		// System.out.println("the runs i: " + i);
		// pns[i] = getPetriNetFromPnmlFile("e:/test" + i + ".pnml");
		// // ((Place)pns[i].getSource()).addToken(new Token());
		// // CoverabilityGraphBuilder.build(pns[i]);
		// for (int j = 0; j < 10; j++) {
		// pns[j] = getPetriNetFromPnmlFile("e:/test" + j + ".pnml");
		// // ((Place)pns[j].getSource()).addToken(new Token());
		// // CoverabilityGraphBuilder.build(pns[j]);
		// System.out.println("the similarity between " + i + " and "
		// + j + " is: " + tarSimilarity(pns[i], pns[j]));
		// // System.out.println("tars of pn" + j);
		// // System.out.println(getTARSFromPetriNet(pns[j]).toString());
		// // pns[j].delete();
		// // pns[j].clearGraph();
		// }
		// // pns[i].delete();
		// // pns[i].clearGraph();
		// }
		// }
	}

	/**
	 * Get the Number of Non-Free Choice of Petri Net.
	 * 
	 * @return the number
	 */
	public static int getNumberofNonFreeChoice(PetriNet pn) {
		int nonfreechoice = 0;
		for (Place p : pn.getPlaces())
			if (p.outDegree() > 1) {
				HashSet<ModelGraphVertex> sucNodes = p.getSuccessors();
				for (ModelGraphVertex tnode : sucNodes) {
					HashSet<ModelGraphVertex> preNodes = tnode
							.getPredecessors();
					if (preNodes.size() > 1)
						nonfreechoice++;

				}
			}
		return nonfreechoice;
	}

	/**
	 * Get the Number of Arbituary Cycle of the Petri Net
	 * 
	 * @return the number
	 */
	public static int getNumberofArbitaryCycle(PetriNet pn) {
		int arbitaryCycle = 0;
		List<List<Node>> cycleList = getCycleList(pn);
		// cycleList = trimCycle(cycleList);
		for (List<Node> cycle : cycleList) {
			int outdegree = 0;
			int indegree = 0;
			for (Node n : cycle) {
				outdegree += n.outDegree() - 1;
				indegree += n.inDegree() - 1;
			}
			if (outdegree + indegree > 2) // 因为出口入口此刻必须至少为2
				arbitaryCycle++;
		}
		return arbitaryCycle;
	}

	/**
	 * Get the Number of Or-Join of the Petri Net
	 * 
	 * @return the number
	 */
	public static int getNumberofOrJoin(PetriNet pn) {
		int result = 0;
		for (Place p : pn.getPlaces()) {
			boolean isOrJoin = false;
			if (p.getPredecessors().size() < 3)
				continue;
			for (Transition t : (HashSet<Transition>) p.getPredecessors())
				if (getdupFuntionTransition(pn, t, p) >= 2) {
					isOrJoin = true;
					break;
				}
			if (isOrJoin == true)
				result++;
		}
		return result;
	}

	/**
	 * Get the Number of Simple Loop of the Petri Net
	 * 
	 * @return the number
	 */
	public static int getNumberofSimpleLoop(PetriNet pn) {
		List<List<Node>> cycleList = getCycleList(pn);
		// cycleList = trimCycle(cycleList);
		return cycleList.size();
	}

	/**
	 * Get the Number of Nested Loop of the Petri Net
	 * 
	 * @return the number
	 */
	public static int getNumberofNestedLoop(PetriNet pn) {
		int nestedSloopCount = 0;
		if (pn.getNumOfCycle() > 1) {

			List<List<Node>> cycleList = getCycleList(pn);
			// cycleList = trimCycle(cycleList);
			for (int i = 0; i < cycleList.size() - 1; i++) {
				int joinNum = 0; // 两个cycleList相交部分的个数
				for (int j = i + 1; j < cycleList.size(); j++) {
					List<Node> singleCycleList1 = cycleList.get(i);
					List<Node> singleCycleList2 = cycleList.get(j);
					if (singleCycleList1.size() == singleCycleList2.size()) // 如果两个的循环的数量是一样多的，那么就当做包含了
						break;
					int contilTag = 0; // 是否连续的标志
					int joinTag = 0; // 是否相交
					int firstITag = 0; // 第一个是否是项链的标志
					for (Node iNode : singleCycleList1) {
						joinTag = 0;
						for (Node jNode : singleCycleList2) {
							if (jNode.equals(iNode)) {
								joinTag = 1;
								break;
							}
						}
						if (singleCycleList1.indexOf(iNode) == 0
								&& joinTag == 1)
							firstITag = 1;
						if (joinTag == 0 && contilTag == 1) {
							contilTag = 0;
							joinNum++;
						}
						if (joinTag == 1 && contilTag == 0) {
							contilTag = 1;
						}
						if (joinTag == 1
								&& contilTag == 1
								&& singleCycleList1.indexOf(iNode) == (singleCycleList1
										.size() - 1) && firstITag == 0) // i是最后一个数字的情况，这样如果第一个元素不是相交的，那么需要将最后一个元素相交
						{
							joinNum++;
						}

					}

				}
				if (joinNum == 1) {
					nestedSloopCount++;
				}
			}
		}
		return nestedSloopCount;
	}

	/**
	 * Get the list of cycles of the PetriNet, of which a cycle is represented
	 * by a list of nodes.
	 * 
	 * @return the list of the cycles
	 */
	public static List<List<Node>> getCycleList(PetriNet pn) {
		List<List<Node>> result = new ArrayList<List<Node>>();

		ArrayList<Node> queue = new ArrayList<Node>();
		List<Node> visitedNodes = new ArrayList<Node>();
		List<Integer> preList = new ArrayList<Integer>();
		for (Node node : pn.getNodes()) {
			Node ini = node;
			visitedNodes = new ArrayList<Node>();
			preList = new ArrayList<Integer>();
			queue = new ArrayList<Node>();
			queue.add(node);
			preList.add(-1);
			int head = 0;
			while (queue.size() > head) {
				Node x = queue.get(head);
				visitedNodes.add(x);
				List<Edge> outgoingArcs = x.getOutEdges();
				if (outgoingArcs == null)
					outgoingArcs = new LinkedList<Edge>();
				for (Edge edge : outgoingArcs) {

					Node opposite = edge.getHead();
					if (opposite.equals(ini)) {
						// to add a new cycle list
						ArrayList<Node> singleCycleList = new ArrayList<Node>();
						int prePointer = head;
						while (prePointer != -1) {
							singleCycleList.add(queue.get(prePointer));
							prePointer = preList.get(prePointer);
						}
						// cycleNodes.add(opposite);
						result.add(singleCycleList);
					}
					if (!visitedNodes.contains(opposite)) {
						queue.add(opposite);
						preList.add(head);
					}
				}
				head++;

			}
		}
		result = trimCycle(result);
		return result;
	}

	/**
	 * Delete the duplicate cycles in a list of cycles
	 * 
	 * @param originalCycleList
	 *            to duplicate cycles
	 * @return the list of the cycles without duplication
	 */
	private static List<List<Node>> trimCycle(List<List<Node>> originalCycleList) {
		List<List<Node>> result = new ArrayList<List<Node>>();
		for (List<Node> toAddCycle : originalCycleList) {
			boolean isOKTag = true;
			for (List<Node> addedCycle : result) {
				if (addedCycle.size() != toAddCycle.size())
					continue;
				boolean notContainTag = false;
				for (Node n : toAddCycle)
					if (!addedCycle.contains(n)) {
						notContainTag = true;
						break;
					}
				if (!notContainTag) {
					isOKTag = false;
					break;
				}
			}
			if (isOKTag)
				result.add(toAddCycle);
		}
		return result;
	}

	/**
	 * Get the number of the places, which has at least two transitions as
	 * successors, and the successors transitions comply with the following
	 * rules: 1. There should be the input transition in the successors. 2.
	 * There should be at least another transition which has the input place as
	 * the successors.
	 * 
	 * @param t
	 *            input transition
	 * @param p
	 *            input place
	 * @return number
	 */
	private static int getdupFuntionTransition(PetriNet pn, Transition t,
			Place p) {
		int result = 0;
		for (Transition tpeer : pn.getTransitions()) {
			if (tpeer.equals(t))
				continue;
			if (!((HashSet<Place>) tpeer.getSuccessors()).contains(p))
				continue;
			for (Place phead : (HashSet<Place>) tpeer.getPredecessors())
				if (((HashSet<Transition>) phead.getSuccessors()).contains(t)) {
					result++;
					break;
				}
		}
		return result;
	}

	public static ResourcePetriNet getResourcePetriNetFromPnmlFile(
			String petriFilePath) {
		ResourcePetriNet result = null;
		PetriNet pn =  getPetriNetFromPnmlFile(petriFilePath);
		result = new ResourcePetriNet(pn);	
		getResourceFromFile(petriFilePath,result);
		return result;
	}
	
	private static void getResourceFromFile(String petriFilePath, ResourcePetriNet rPetriNet)
	{
		SAXBuilder builder=new SAXBuilder(false);
		try {
			FileInputStream fis = new FileInputStream(petriFilePath);
			//InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			//BufferedReader br = new BufferedReader(isr);
			Document doc=builder.build(fis, "UTF-8");
			Element books=doc.getRootElement();
			List netlist=books.getChildren("net");
			Element net = (Element) netlist.get(0);
			List transitionlist = net.getChildren("transition");
			for (Iterator iter = transitionlist.iterator(); iter.hasNext();) {
				Element transition = (Element) iter.next();
				Element name = transition.getChild("name");
				String nameValue = name.getValue().trim();
				Element resource = transition.getChild("resource");
				String resourceValue = resource.getValue().trim();
				rPetriNet.addResource(nameValue, resourceValue);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
