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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 *
 */
public class OrderingRelationBuilder {
	private CompleteFinitePrefix _cfp = null;
	private OrderingRelation[][] orderingRelations = null;

	public OrderingRelationBuilder(CompleteFinitePrefix cfp) {
		this._cfp = cfp;
		int nEvents = cfp.getTransitions().size();
		this.orderingRelations = new OrderingRelation[nEvents][nEvents];
		computePrefixRelations();
		completePrefixRelations();
	}

	public OrderingRelation[][] getOrderingRelations() {
		return this.orderingRelations;
	}

	/**
	 * do not consider the cut-off events
	 */
	private void computePrefixRelations() {
		// initialize to cocurrency relation
		for (int i = 0; i < this.orderingRelations.length; i++) {
			for (int j = 0; j < this.orderingRelations.length; j++) {
				if (i == j) {
					this.orderingRelations[i][j] = OrderingRelation.NONE;
				} else {
					this.orderingRelations[i][j] = OrderingRelation.CONCURRENCY;
				}
			}
		}

		// only after all pre-conditions have been visited, the relations to
		// some event can be updated;
		// record which condition has been visited
		HashSet<Condition> visited = new HashSet<Condition>();

		// works as a queue
		LinkedList<Event> queue = new LinkedList<Event>();

		Iterator<Condition> itEntry = this._cfp.getInitialConditions()
				.iterator();
		while (itEntry.hasNext()) {
			Condition entry = itEntry.next();
			visited.add(entry);
			queue.addAll(entry.getSuccessors());
		}

		while (!queue.isEmpty()) {
			Event e = queue.removeFirst();
			if (visited.containsAll(e.getPredecessors())) {
				updateEventRelations(e);

				// deal with the sucessor events
				Iterator<Condition> itcpost = e.getSuccessors().iterator();
				while (itcpost.hasNext()) {
					Condition postc = itcpost.next();
					visited.add(postc);
					Iterator<Event> itepost = postc.getSuccessors().iterator();
					while (itepost.hasNext()) {
						Event poste = itepost.next();
						if (!queue.contains(poste)) {
							queue.add(poste);
						}
					}
				}

			} else {
				queue.addLast(e);
			}
		}
	}

	/**
	 * for an event, if all its pre-conditions have been visited, the relations
	 * to it can be updated
	 * 
	 * @param event
	 */
	private void updateEventRelations(Event event) {
		// get the index of the event
		int indexEvent = this._cfp.getTransitions().indexOf(event);

		// deal with the pre-conditions one by one
		Iterator<Condition> itc = event.getPredecessors().iterator();
		while (itc.hasNext()) {
			Condition prec = itc.next();

			// deal with the relation between the event and its pre-events
			Iterator<Event> itepre = prec.getPredecessors().iterator();
			if (itepre.hasNext()) {
				Event pree = itepre.next();
				int indexPree = this._cfp.getTransitions().indexOf(pree);
				this.orderingRelations[indexPree][indexEvent] = OrderingRelation.PRECEDENCE;
				this.orderingRelations[indexEvent][indexPree] = OrderingRelation.NONE;

				for (int k = 0; k < this.orderingRelations.length; k++) {
					if (this.orderingRelations[k][indexPree] == OrderingRelation.PRECEDENCE) {
						this.orderingRelations[k][indexEvent] = OrderingRelation.PRECEDENCE;
						this.orderingRelations[indexEvent][k] = OrderingRelation.NONE;
					}
					if (this.orderingRelations[indexPree][k] == OrderingRelation.CONFLICT) {
						this.orderingRelations[indexEvent][k] = OrderingRelation.CONFLICT;
						this.orderingRelations[k][indexEvent] = OrderingRelation.CONFLICT;
					}
				}
			}

			// deal with the relation between the event and its sibling events
			Iterator<Event> itesibling = prec.getSuccessors().iterator();
			while (itesibling.hasNext()) {
				Event esibling = itesibling.next();
				if (esibling != event) {
					int indexEsibling = this._cfp.getTransitions().indexOf(
							esibling);
					this.orderingRelations[indexEvent][indexEsibling] = OrderingRelation.CONFLICT;
					this.orderingRelations[indexEsibling][indexEvent] = OrderingRelation.CONFLICT;
					for (int k = 0; k < this.orderingRelations.length; k++) {
						if (k != indexEvent) {
							if (this.orderingRelations[indexEsibling][k] == OrderingRelation.PRECEDENCE) {
								this.orderingRelations[k][indexEvent] = OrderingRelation.CONFLICT;
								this.orderingRelations[indexEvent][k] = OrderingRelation.CONFLICT;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * deal with the cut-off events, the cut-off events is linked to the event
	 * (corresponding event) with the same Mark([e]) using the object
	 */
	private void completePrefixRelations() {
		for (Event cutOffEvent : this._cfp.getCutOffEvents()) {
			Event correspondingEvent = (Event) cutOffEvent.object;
			ArrayList<Event> cutOffLocalConfigurationEvents = cutOffEvent
					.getLocalConfiguration().getAllCausalEvents();
			
			//TODO: need to be changed, use the marking of the local configuration instead.
			Iterator<Condition> itccpost = correspondingEvent.getSuccessors()
					.iterator();
			while (itccpost.hasNext()) {
				Condition ccpost = itccpost.next();

				// determine whether the followed place are same
				boolean flag = false;
				Iterator<Condition> itcpost = cutOffEvent.getSuccessors()
						.iterator();
				while (itcpost.hasNext()) {
					if (itcpost.next().getOriginalPlace() == ccpost
							.getOriginalPlace()) {
						flag = true;
						break;
					}
				}
				if (flag) {
					Iterator<Event> itcepost = ccpost.getSuccessors()
							.iterator();
					while (itcepost.hasNext()) {
						Event cepost = itcepost.next();
						int indexcepost = this._cfp.getTransitions().indexOf(
								cepost);
						for (Event pree : cutOffLocalConfigurationEvents) {
							int indexpree = this._cfp.getTransitions().indexOf(
									pree);

							this.orderingRelations[indexpree][indexcepost] = OrderingRelation.PRECEDENCE;
							if (this.orderingRelations[indexcepost][indexpree] != OrderingRelation.PRECEDENCE) {
								this.orderingRelations[indexcepost][indexpree] = OrderingRelation.NONE;
							}
							for (int k = 0; k < this.orderingRelations.length; k++) {
								if (this.orderingRelations[indexcepost][k] == OrderingRelation.PRECEDENCE) {
									this.orderingRelations[indexpree][k] = OrderingRelation.PRECEDENCE;
									if (this.orderingRelations[k][indexpree] != OrderingRelation.PRECEDENCE) {
										this.orderingRelations[k][indexpree] = OrderingRelation.NONE;
									}
								}
							}
						}

					}

				}
			}

			// /////////////
			// for (Event pree : cutOffLocalConfigurationEvents) {
			// int indexpree = this._cfp.getTransitions().indexOf(pree);
			//
			// Iterator<Condition> itccpost = correspondingEvent
			// .getSuccessors().iterator();
			// while (itccpost.hasNext()) {
			// Condition ccpost = itccpost.next();
			//
			// // determine whether the followed place are same
			// boolean flag = false;
			// Iterator<Condition> itcpost = cutOffEvent.getSuccessors()
			// .iterator();
			// while (itcpost.hasNext()) {
			// if (itcpost.next().getOriginalPlace() == ccpost
			// .getOriginalPlace()) {
			// flag = true;
			// break;
			// }
			// }
			// if (flag) {
			// Iterator<Event> itcepost = ccpost.getSuccessors()
			// .iterator();
			// while (itcepost.hasNext()) {
			// Event cepost = itcepost.next();
			// int indexcepost = this._cfp.getTransitions()
			// .indexOf(cepost);
			// this.orderingRelations[indexpree][indexcepost] =
			// OrderingRelation.PRECEDENCE;
			// if (this.orderingRelations[indexcepost][indexpree] ==
			// OrderingRelation.CONCURRENCY) {
			// this.orderingRelations[indexcepost][indexpree] =
			// OrderingRelation.NONE;
			// }
			//
			// for (int k = 0; k < this.orderingRelations.length; k++) {
			// if (this.orderingRelations[indexcepost][k] ==
			// OrderingRelation.PRECEDENCE) {
			// this.orderingRelations[indexpree][k] =
			// OrderingRelation.PRECEDENCE;
			// if (this.orderingRelations[k][indexpree] ==
			// OrderingRelation.CONCURRENCY) {
			// this.orderingRelations[k][indexpree] = OrderingRelation.NONE;
			// }
			// }
			// }
			// }
			//
			// }
			// }
			// }
			// ///////////////
			// // store the successor events from the corresponding event
			// HashSet<Event> sucEvents = new HashSet<Event>();
			//
			// Iterator<Condition> itpostcc = correspondingEvent.getSuccessors()
			// .iterator();
			// while (itpostcc.hasNext()) {
			// Condition succc = itpostcc.next();
			// Iterator<Condition> itpostc = cutOffEvent.getSuccessors()
			// .iterator();
			// while (itpostc.hasNext()) {
			// Condition succ = itpostc.next();
			// if (succc.getOriginalPlace() == succ.getOriginalPlace()) {
			// sucEvents.addAll(succc.getSuccessors());
			// break;
			// }
			// }
			// }
			//
			// ArrayList<Event> cutOffLocalConfigurationEvents = cutOffEvent
			// .getLocalConfiguration().getAllCausalEvents();
			// for (Event esuc : sucEvents) {
			// int indexEsuc = this._cfp.getTransitions().indexOf(esuc);
			// for (Event pree : cutOffLocalConfigurationEvents) {
			// int indexpree = this._cfp.getTransitions().indexOf(pree);
			// this.orderingRelations[indexpree][indexEsuc] =
			// OrderingRelation.PRECEDENCE;
			// this.orderingRelations[indexEsuc][indexpree] =
			// OrderingRelation.NONE;
			// for (int k = 0; k < this.orderingRelations.length; k++) {
			// if (this.orderingRelations[indexEsuc][k] ==
			// OrderingRelation.PRECEDENCE) {
			// this.orderingRelations[indexpree][k] =
			// OrderingRelation.PRECEDENCE;
			// this.orderingRelations[k][indexpree] = OrderingRelation.NONE;
			// }
			// }
			// }
			// }
		}
	}
}