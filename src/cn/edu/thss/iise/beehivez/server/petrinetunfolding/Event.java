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
package cn.edu.thss.iise.beehivez.server.petrinetunfolding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 */
public class Event extends Transition {

	private static final long serialVersionUID = -9176946892805044671L;

	private Transition _originalTransition = null;
	private Configuration localConfiguration = null;
	private boolean _isCutOffEvent = false;

	// foata level, used for foata normal form comparation
	private int foataLevel = -1;

	// used to store the concurrent conditions
	// only used to calculate the common concurrent conditions for post
	// conditions
	// it comes from the intersection of all concurrent conditions of
	// pre-conditions
	// it will not be updated during cfp construction
	private HashSet<Condition> _concurrentConditions = new HashSet<Condition>();

	// represent the marking of the corresponding Petri net
	private CompleteFinitePrefix cfp = null;

	public Event(Transition originalTransition, CompleteFinitePrefix net) {
		super(originalTransition.getIdentifier(), net);

		this.setLogEvent(originalTransition.getLogEvent());

		this.cfp = net;
		// link from this event to the original transition
		this._originalTransition = originalTransition;

		// // link from the original transition to this event
		// if (originalTransition.object == null) {
		// ArrayList<Event> events = new ArrayList<Event>();
		// events.add(this);
		// originalTransition.object = events;
		// } else {
		// ArrayList<Event> events = (ArrayList<Event>)
		// originalTransition.object;
		// events.add(this);
		// }
	}

	public void setConcurrentConditions(HashSet<Condition> concurrentConditions) {
		this._concurrentConditions = concurrentConditions;
	}

	public HashSet<Condition> getConcurrentConditions() {
		return this._concurrentConditions;
	}

	public Transition getOriginalTransition() {
		return this._originalTransition;
	}

	/**
	 * It must be used after this event has been added into the net, that means
	 * it has been connected with its pre conditions the foata level is computed
	 * during the local configuration construction
	 * 
	 * @return
	 */
	public int getFoataLevel() {
		return foataLevel;
	}

	/**
	 * It must be used after this event has been aded into the net that means it
	 * has been connected with its pre conditions
	 * 
	 * @return
	 */
	public Configuration getLocalConfiguration() {
		if (this.localConfiguration == null) {
			// determine whether the event already be included
			HashSet<Event> events = new HashSet<Event>();

			// compute the local configuration
			this.localConfiguration = new Configuration(this.cfp);
			LinkedList<Event> queue = new LinkedList<Event>();
			queue.add(this);
			while (!queue.isEmpty()) {
				Event event = queue.remove();
				if (events.add(event)) {
					this.localConfiguration.add(event);

					Iterator<Condition> itc = event.getPredecessors()
							.iterator();
					while (itc.hasNext()) {
						Condition condition = itc.next();
						Iterator<Event> ite = condition.getPredecessors()
								.iterator();
						if (ite.hasNext()) {
							Event e = ite.next();
							if (!events.contains(e)) {
								queue.add(e);
							}
						}
					}
				}
			}

			// calculate foata level here
			Iterator<Condition> itc = this.getPredecessors().iterator();
			while (itc.hasNext()) {
				Condition prec = itc.next();
				Iterator<Event> ite = prec.getPredecessors().iterator();
				if (!ite.hasNext()) {
					if (this.foataLevel == -1) {
						this.foataLevel = 1;
					}
				} else {
					Event pree = ite.next();
					if (pree.foataLevel + 1 > this.foataLevel) {
						this.foataLevel = pree.foataLevel + 1;
					}
				}
			}
			return this.localConfiguration;
		} else {
			return this.localConfiguration;
		}
	}

	public SimpleMarking getLocalConfigurationMarking() {
		return this.getLocalConfiguration().getMarking();
	}

	public boolean isCutOffEvent() {
		return this._isCutOffEvent;
	}

	public void setCutOff(boolean isCutOffEvent) {
		this._isCutOffEvent = isCutOffEvent;
	}

	/**
	 * this function must be called after the event is linked to its
	 * pre-conditions
	 */
	public void updateConcurrentConditions() {
		// calculate the concurrent conditions for the new event, the
		// intersection
		Iterator<Condition> itc = this.getPredecessors().iterator();
		Condition prec = itc.next();
		this._concurrentConditions
				.addAll(prec.getPrivateConcurrentConditions());
		this._concurrentConditions.addAll(prec.getCommonConcurrentConditions());
		while (itc.hasNext()) {
			HashSet<Condition> tempConditions = new HashSet<Condition>();
			prec = itc.next();
			tempConditions.addAll(prec.getCommonConcurrentConditions());
			tempConditions.addAll(prec.getPrivateConcurrentConditions());
			this._concurrentConditions.retainAll(tempConditions);
		}
	}

	@Override
	public int compareTo(Object o) {
		Event other = (Event) o;
		return this._originalTransition.getId()
				- other._originalTransition.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.models.petrinet.Transition#equals(java.lang
	 * .Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
