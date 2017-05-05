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

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;

/**
 * implement complete finite prefix according to the paper 'An Improvement of
 * McMillan's Unfolding Algorithm' published in Formal Methods in System Design
 * 2002
 * 
 * @author Tao Jin
 * 
 */
public class CompleteFinitePrefix extends PetriNet {

	// the initial conditions
	private ArrayList<Condition> _initialConditions = null;

	// represent the marking of the corresponding Petri net
//	private Marking _initialMarking = null;
	private SimpleMarking _initialMarking = null;
	private PetriNet _pn = null;

	// the unfolding will stop at the maximal depth
	// here, the depth means the length of trace
	private int _maxDepth = Integer.MAX_VALUE;

	// store the cut-off events
	private ArrayList<Event> _cutOffEvents = null;

	public CompleteFinitePrefix(PetriNet pn, int maxDepth) {
		this._pn = pn;
		this._maxDepth = maxDepth;
	}

	public int getMaxDepth() {
		return this._maxDepth;
	}

	public void setCutOffEvents(ArrayList<Event> cutOffEvents) {
		this._cutOffEvents = cutOffEvents;
	}

	public ArrayList<Event> getCutOffEvents() {
		return this._cutOffEvents;
	}

	public void setInitialConditions(ArrayList<Condition> initialConditions) {
		this._initialConditions = initialConditions;
	}

	public ArrayList<Condition> getInitialConditions() {
		return this._initialConditions;
	}

	public void setInitialMarking(SimpleMarking m) {
		this._initialMarking = m;
	}

	public SimpleMarking getInitialMarking() {
		return this._initialMarking;
	}

	public void addCondition(Condition condition) {
		this.addPlace(condition);
	}

	public void addEvent(Event event) {
		this.addTransition(event);
	}

	public void addEdgeC2E(Condition condition, Event event) {
		this.addEdge(condition, event);
	}

	public void addEdgeE2C(Event event, Condition condition) {
		this.addEdge(event, condition);
	}

	public OrderingRelation[][] getOrderingRelations() {
		OrderingRelationBuilder orb = new OrderingRelationBuilder(this);
		return orb.getOrderingRelations();
	}

	public void destroyCFP() {
		this._cutOffEvents = null;
		this._initialConditions = null;
		this._initialMarking = null;
		this._pn = null;
		super.destroyPetriNet();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
