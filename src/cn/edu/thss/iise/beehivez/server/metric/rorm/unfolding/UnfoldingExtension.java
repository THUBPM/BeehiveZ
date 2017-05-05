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

import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 */
public class UnfoldingExtension implements Comparable<UnfoldingExtension> {
	private Event _lastEvent = null;
	private Condition[] _preConditions = null;
	private CompleteFinitePrefix _cfp = null;

	/**
	 * the new event is created and added to the net together with the new edges
	 * between every pre-condition and this new event
	 * 
	 * @param transition
	 * @param preConditions
	 * @param cfp
	 */
	public UnfoldingExtension(Transition transition, Condition[] preConditions,
			CompleteFinitePrefix cfp) {
		_lastEvent = new Event(transition, cfp);
		this._preConditions = preConditions;
		this._cfp = cfp;
		cfp.addEvent(_lastEvent);

		// link the new event with its every pre-condition
		for (Condition c : preConditions) {
			cfp.addEdgeC2E(c, _lastEvent);
		}
		_lastEvent.getLocalConfiguration();
	}

	public Event getLastEvent() {
		return this._lastEvent;
	}

	@Override
	public int compareTo(UnfoldingExtension o) {
		return this._lastEvent.getLocalConfiguration().compareTo(
				o._lastEvent.getLocalConfiguration());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _lastEvent.toString() + ", " + _preConditions.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
