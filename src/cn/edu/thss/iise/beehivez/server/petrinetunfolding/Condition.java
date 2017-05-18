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

import org.processmining.framework.models.petrinet.Place;

/**
 * @author Tao Jin
 * 
 */
public class Condition extends Place {
	private Place _originalPlace = null;

	// used to store the concurrent conditions here
	// it is divided into two parts
	// the common one is inherited from the pre-event
	// the private one is the other conditions concurrent to this one
	// the private one will be updated during the whole cfp construction
	private HashSet<Condition> _commonConcurrentConditions = new HashSet<Condition>();
	private HashSet<Condition> _privateConcurrentCondition = new HashSet<Condition>();

	public Condition(Place originalPlace, CompleteFinitePrefix net) {
		super(originalPlace.getIdentifier(), net);

		// link from the condition to the original place;
		this._originalPlace = originalPlace;

		// link from original place to this condition
		if (originalPlace.object == null) {
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			conditions.add(this);
			originalPlace.object = conditions;
		} else {
			ArrayList<Condition> conditions = (ArrayList<Condition>) originalPlace.object;
			conditions.add(this);
		}
	}

	public void setCommonConcurrentConditions(
			HashSet<Condition> commonConcurrentConditions) {
		this._commonConcurrentConditions = commonConcurrentConditions;
	}

	public HashSet<Condition> getCommonConcurrentConditions() {
		return this._commonConcurrentConditions;
	}

	public HashSet<Condition> getPrivateConcurrentConditions() {
		return this._privateConcurrentCondition;
	}

	public void addPrivateConcurrentCondition(Condition condition) {
		this._privateConcurrentCondition.add(condition);
	}

	public Place getOriginalPlace() {
		return this._originalPlace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.models.petrinet.Place#equals(java.lang.Object
	 * )
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
