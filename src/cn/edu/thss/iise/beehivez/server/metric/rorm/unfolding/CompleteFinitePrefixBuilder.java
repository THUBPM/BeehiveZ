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
package cn.edu.thss.iise.beehivez.server.metric.rorm.unfolding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * 
 * @author Tao  Jin
 * 
 */
public class CompleteFinitePrefixBuilder {

	/**
	 * 
	 * @param pn
	 *            the given petri net must be configured with the initial
	 *            marking, the number of tokens in every initial place should
	 *            not be greater than 1, the weight on every arc must be 1
	 * @param maxDepth
	 *            the maximum foata level, if some event's foata level is
	 *            greater than this threshold, this event will not be extended
	 *            any more
	 * @return
	 */
	public static CompleteFinitePrefix build(PetriNet pn) {
		return build(pn, Integer.MAX_VALUE);
	}

	public static CompleteFinitePrefix build(PetriNet pn, int maxDepth) {
		CompleteFinitePrefix cfp = new CompleteFinitePrefix(pn, maxDepth);

		// store the marking of Mark([e])
		// used to determine the cut-off events
		// because we add new event according to the total order, if the same
		// marking appears, the corresponding event must be cut-off event
		HashMap<SimpleMarking, Event> markings = new HashMap<SimpleMarking, Event>();

		// store the cut-off events
		// at the last, create the post-conditions for these events and link
		// them to the corresponding event, finish the construction of complete
		// finite prefix.
		ArrayList<Event> cutOffEvents = new ArrayList<Event>();

		// the initial marking for the given petri net
		SimpleMarking initialMarking = new SimpleMarking();
		// store the new conditions
		ArrayList<Condition> initialConditions = new ArrayList<Condition>();

		Iterator<Place> itp = pn.getPlaces().iterator();
		while (itp.hasNext()) {
			Place p = itp.next();
			int nTokens = p.getNumberOfTokens();
			if (nTokens > 0) {
				initialMarking.addPlace(p);
				Condition c = new Condition(p, cfp);

				// set the concurrent conditions for every new conditions,
				// all the new conditions from the initial marking must be
				// concurrent to each other
				for (Condition cc : initialConditions) {
					c.addPrivateConcurrentCondition(cc);
					cc.addPrivateConcurrentCondition(c);
				}
				cfp.addCondition(c);
				initialConditions.add(c);
			}
		}
		cfp.setInitialConditions(initialConditions);
		cfp.setInitialMarking(initialMarking);
		markings.put(initialMarking, null);

		// store all the unfolding extensions in a priority queue,
		// the order is calculated according to the total order of [e], namely,
		// <F
		PriorityQueue<UnfoldingExtension> queue = new PriorityQueue<UnfoldingExtension>();

		// calculate the unfolding extension
		// the impossible extension will be excluded directly here.
		// every extended event has been added to the complete finite prefix
		// together with all the links to its pre-conditions,
		// when we choose one event to extend the cfp, we must create the
		// post-conditions and link them to the event.
		Iterator<UnfoldingExtension> itpe = getUnfoldingExtensions(
				initialConditions, cfp).iterator();
		while (itpe.hasNext()) {
			queue.add(itpe.next());
		}

		// ectend cfp according the total order of [e]
		while (!queue.isEmpty()) {
			UnfoldingExtension pe = queue.remove();

			// calculate the marking,
			// if the marking already exists, the new event is a cut-off event,
			// for cut-off event, the post-conditions will be created at last,
			// for non-cut-off event, the post-conditions will be created here,
			// for event with large foata level, no post-conditions will be
			// created.

			Event newEvent = pe.getLastEvent();
			if (newEvent.getFoataLevel() >= maxDepth) {
				continue;
			}

			SimpleMarking newMarking = newEvent.getLocalConfigurationMarking();
			Event oldEvent = markings.get(newMarking);
			if (oldEvent != null) {
				// cut-off event
				newEvent.setCutOff(true);
				// link to the lower event
				newEvent.object = oldEvent;
				cutOffEvents.add(newEvent);
				
				// create the post conditions for cut-off event
				Iterator<Place> itpPost = newEvent.getOriginalTransition()
						.getSuccessors().iterator();
				Set<ModelGraphVertex> oldEventPost = oldEvent.getSuccessors();
				while (itpPost.hasNext()) {
					Place pPost = itpPost.next();
					Condition newCondition = new Condition(pPost, cfp);
					// cut-off post place
					newCondition.setCutoffPost(true);
					for(ModelGraphVertex v : oldEventPost) {
//						if(v.getIdentifier().equals(newCondition.getIdentifier())) {
						if(((Condition) v).getOriginalPlace() == newCondition.getOriginalPlace()) {
							Condition c = (Condition) v;
							if(c.getMappingConditions() == null) {
								c.setMappingConditions(new HashSet<Condition>());
								c.getMappingConditions().add(c);
							}
							c.getMappingConditions().add(newCondition);
							newCondition.setMappingConditions(c.getMappingConditions());
							newCondition.setCorrespondingCondition(c);
						}
					}
					cfp.addCondition(newCondition);
					cfp.addEdgeE2C(newEvent, newCondition);
				}
			} else {
				// update the concurrent conditions of the event first
				// the intersection from all the pre-conditions
				newEvent.updateConcurrentConditions();

				// add the marking
				// create the post conditions
				// update the concurrent conditions
				// calculate new extensions and add to the queue

				markings.put(newMarking, newEvent);

				// store the new conditions
				ArrayList<Condition> newConditions = new ArrayList<Condition>();
				Iterator<Place> itpPost = newEvent.getOriginalTransition()
						.getSuccessors().iterator();
				while (itpPost.hasNext()) {
					Place pPost = itpPost.next();
					Condition newCondition = new Condition(pPost, cfp);
					cfp.addCondition(newCondition);
					cfp.addEdgeE2C(newEvent, newCondition);
					newCondition.setCommonConcurrentConditions(newEvent
							.getConcurrentConditions());
					Iterator<Condition> itccc = newEvent
							.getConcurrentConditions().iterator();
					while (itccc.hasNext()) {
						Condition ccc = itccc.next();
						ccc.addPrivateConcurrentCondition(newCondition);
					}
					newConditions.add(newCondition);
				}

				// update the private concurrent conditions for new conditions
				for (int i = 0; i < newConditions.size(); i++) {
					Condition c1 = newConditions.get(i);
					for (int j = i + 1; j < newConditions.size(); j++) {
						Condition c2 = newConditions.get(j);
						c1.addPrivateConcurrentCondition(c2);
						c2.addPrivateConcurrentCondition(c1);
					}
				}

				// calculate new extensions based on new conditions and add them
				// to the queue
				itpe = getUnfoldingExtensions(newConditions, cfp).iterator();
				while (itpe.hasNext()) {
					queue.add(itpe.next());
				}
			}

		}

		cfp.setCutOffEvents(cutOffEvents);

		// create the post conditions for every cut-off event
//		Iterator<Event> itCutOffEvent = cutOffEvents.iterator();
//		while (itCutOffEvent.hasNext()) {
//			Event cutOffEvent = itCutOffEvent.next();
//			Iterator<Place> itpPost = cutOffEvent.getOriginalTransition()
//					.getSuccessors().iterator();
//			while (itpPost.hasNext()) {
//				Place pPost = itpPost.next();
//				Condition newCondition = new Condition(pPost, cfp);
//				// cut-off post place
//				newCondition.setCutoffPost(true);
//				cfp.addCondition(newCondition);
//				cfp.addEdgeE2C(cutOffEvent, newCondition);
//			}
//		}

		return cfp;
	}

	/**
	 * calculate the unfolding extension upon the new conditions, the new
	 * conditions must be already in the complete finite prefix, and already
	 * connected to the pre-event with the concurrent conditions information
	 * updated.
	 * 
	 * @param newConditions
	 * @return
	 */
	private static ArrayList<UnfoldingExtension> getUnfoldingExtensions(
			ArrayList<Condition> newConditions, CompleteFinitePrefix cfp) {
		// store the results
		ArrayList<UnfoldingExtension> ret = new ArrayList<UnfoldingExtension>();

		// deal with the new condition one by one.
		// to avoid duplicate, we combine the new conditions according to their
		// id's order
		for (int newConditionIndex = 0; newConditionIndex < newConditions
				.size(); newConditionIndex++) {
			Condition newCondition = newConditions.get(newConditionIndex);
			// get the post transitions for the original place
			Iterator<Transition> itt = newCondition.getOriginalPlace()
					.getSuccessors().iterator();
			transitionTest: while (itt.hasNext()) {
				Transition tPost = itt.next();

				// test whether this transition can work as a extension
				// check whether all its pre conditions are existing
				int sizePrePlaces = tPost.getPredecessors().size();

				// store all the pre-conditions.
				// for every place, there is a set of mapped conditions.
				// the combination will be computed later
				ArrayList<ArrayList<Condition>> mappedPreConditions = new ArrayList<ArrayList<Condition>>();
				Iterator<Place> itp = tPost.getPredecessors().iterator();
				while (itp.hasNext()) {
					Place p = itp.next();
					ArrayList<Condition> mappedConditions;
					if (newCondition.getOriginalPlace() == p) {
						mappedConditions = new ArrayList<Condition>();
						mappedConditions.add(newCondition);
					} else {
						mappedConditions = (ArrayList<Condition>) p.object;
						if (mappedConditions == null) {
							continue transitionTest;
						}
					}
					mappedPreConditions.add(mappedConditions);
				}

				// record the index of condition of the corresponding place will
				// be checked
				int[] preConditionIndex = new int[sizePrePlaces];
				for (int k = 0; k < sizePrePlaces; k++) {
					preConditionIndex[k] = 0;
				}

				// store the pre-conditions for one combination
				Condition[] preConditions = new Condition[sizePrePlaces];

				// deal with pre-places one by one
				int indexPrePlace = 0;

				// in every run, find an possible combination, create a new
				// extension.
				while (true) {

					while (indexPrePlace < sizePrePlaces) {
						// get the conditions

						Condition testCondition = mappedPreConditions.get(
								indexPrePlace).get(
								preConditionIndex[indexPrePlace]);

						// test whether the condition satisfies the rule.
						// if it is ok, add it to preConditions

						// first, test for the conditions exists in
						// newConditions, to avoid duplicate, we combine these
						// conditions according to their id order.
						// second, test whether this condition is concurrent to
						// the conditions already in preConditions

						// the first test
						while (newConditions.contains(testCondition)
								&& testCondition.getId() < newCondition.getId()) {
							// ignored this one, to avoid duplicate
							preConditionIndex[indexPrePlace]++;

							while (preConditionIndex[indexPrePlace] >= mappedPreConditions
									.get(indexPrePlace).size()) {
								preConditionIndex[indexPrePlace] = 0;
								indexPrePlace--;
								if (indexPrePlace < 0) {
									continue transitionTest;
								}
								preConditionIndex[indexPrePlace]++;
							}

							testCondition = mappedPreConditions.get(
									indexPrePlace).get(
									preConditionIndex[indexPrePlace]);

						}

						// the second test, test whether the new condition is
						// concurrent to the existing conditions in
						// pre-conditions
						for (int k = 0; k < indexPrePlace; k++) {
							Condition c = preConditions[k];
							if (!c.getPrivateConcurrentConditions().contains(
									testCondition)
									&& !c.getCommonConcurrentConditions()
											.contains(testCondition)) {
								preConditionIndex[indexPrePlace]++;

								while (preConditionIndex[indexPrePlace] >= mappedPreConditions
										.get(indexPrePlace).size()) {
									preConditionIndex[indexPrePlace] = 0;
									indexPrePlace--;
									if (indexPrePlace < 0) {
										continue transitionTest;
									}
									preConditionIndex[indexPrePlace]++;
								}

								testCondition = mappedPreConditions.get(
										indexPrePlace).get(
										preConditionIndex[indexPrePlace]);

								while (newConditions.contains(testCondition)
										&& testCondition.getId() < newCondition
												.getId()) {
									// ignored this one, to avoid duplicate
									preConditionIndex[indexPrePlace]++;

									while (preConditionIndex[indexPrePlace] >= mappedPreConditions
											.get(indexPrePlace).size()) {
										preConditionIndex[indexPrePlace] = 0;
										indexPrePlace--;
										if (indexPrePlace < 0) {
											continue transitionTest;
										}
										preConditionIndex[indexPrePlace]++;
									}

									testCondition = mappedPreConditions.get(
											indexPrePlace).get(
											preConditionIndex[indexPrePlace]);

								}
								k = -1;

							}
						}

						preConditions[indexPrePlace] = testCondition;

						indexPrePlace++;
					}

					// add new extension and adjust the index for next test
					UnfoldingExtension extension = new UnfoldingExtension(
							tPost, preConditions, cfp);
					ret.add(extension);

					indexPrePlace = sizePrePlaces - 1;
					preConditionIndex[indexPrePlace]++;

					while (preConditionIndex[indexPrePlace] >= mappedPreConditions
							.get(indexPrePlace).size()) {
						preConditionIndex[indexPrePlace] = 0;
						indexPrePlace--;
						if (indexPrePlace < 0) {
							continue transitionTest;
						}
						preConditionIndex[indexPrePlace]++;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File("e:/test");
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				String pnFileName = f.getAbsolutePath();
				int i = pnFileName.lastIndexOf(".pnml");
				if (i > 0) {
					String cfpFileName = pnFileName.substring(0, i);
					cfpFileName += "cfp.pnml";
					PetriNet pn = PetriNetUtil
							.getPetriNetFromPnmlFile(pnFileName);
					for (Place place : pn.getPlaces()) {
						place.removeAllTokens();
						if (place.inDegree() == 0) {
							place.addToken(new Token());
						}
					}

					System.out.println("construct the cfp of " + pnFileName);
					long start = System.currentTimeMillis();
					CompleteFinitePrefix cfp = CompleteFinitePrefixBuilder
							.build(pn, Integer.MAX_VALUE);
					long timeCost = System.currentTimeMillis() - start;
					System.out.println("the constrution for cfp of "
							+ pnFileName + " cost: " + timeCost + " ms");
					PetriNetUtil.export2pnml(cfp, cfpFileName);
				}
			}
		}
	}

}
