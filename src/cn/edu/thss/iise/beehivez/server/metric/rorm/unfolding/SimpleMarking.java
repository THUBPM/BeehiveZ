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

import java.util.Iterator;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

/**
 * only store the places, do not store the tokens
 * 
 * @author Tao Jin
 * 
 */
public class SimpleMarking implements Comparable {
	protected TreeSet<Place> places;

	public SimpleMarking() {
		places = new TreeSet<Place>();
	}

	public void addPlace(Place place) {
		places.add(place);
	}

	public void delPlace(Place place) {
		places.remove(place);
	}

	public boolean equals(Object marking) {
		if (!(marking instanceof SimpleMarking)) {
			return false;
		}

		if (this.compareTo(marking) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEnabled(Event e){
		for(Object o : e.getOriginalTransition().getPredecessors()){
			Place pre = (Place) o;
			if(!places.contains(pre)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isEnabled(Transition t, Boolean forward){
		if(forward){
			for(Object o : t.getPredecessors()){
				Place pre = (Place) o;
				if(!places.contains(pre)){
					return false;
				}
			}
		}else{
			for(Object o : t.getSuccessors()){
				Place pre = (Place) o;
				if(!places.contains(pre)){
					return false;
				}
			}
		}
		return true;
	}
	
	public void firing(Event e){
		for(Object o : e.getOriginalTransition().getPredecessors()){
			Place pre = (Place) o;
			delPlace(pre);
		}
		for(Object o : e.getOriginalTransition().getSuccessors()){
			Place post = (Place) o;
			addPlace(post);
		}
	}
	
	public void firing(Transition t, Boolean forward){
		if(forward){
			for(Object o : t.getPredecessors()){
				Place pre = (Place) o;
				delPlace(pre);
			}
			for(Object o : t.getSuccessors()){
				Place post = (Place) o;
				addPlace(post);
			}
		}else{
			for(Object o : t.getSuccessors()){
				Place pre = (Place) o;
				delPlace(pre);
			}
			for(Object o : t.getPredecessors()){
				Place post = (Place) o;
				addPlace(post);
			}
		}
	}
	
	public Iterator iterator() {
		return places.iterator();
	}

	public boolean isLessOrEqual(SimpleMarking marking) {
		Iterator it = iterator();
		boolean r = true;
		while (it.hasNext() && r) {
			Place place = (Place) it.next();
			if (!marking.places.contains(place)) {
				r = false;
			}
		}
		return r;
	}

	public int getSize() {
		return places.size();
	}

	public int compareTo(Object object) {
		SimpleMarking marking = (SimpleMarking) object;
		if (getSize() != marking.getSize()) {
			// A marking with less places marked is considered smaller.
			return getSize() - marking.getSize();
		}

		// Same amount of places
		if (getSize() == 0) {
			// both are empty markings
			return 0;
		}

		Iterator<Place> it = places.iterator();
		Iterator<Place> it2 = marking.places.iterator();
		while (it.hasNext()) {
			Place pThis = it.next();
			Place pThat = it2.next();
			int ret = pThis.compareTo(pThat);
			if (ret != 0) {
				return ret;
			}
		}

		return 0;
	}

	public void add(SimpleMarking marking) {
		Place place;
		Iterator it = marking.iterator();
		while (it.hasNext()) {
			place = (Place) it.next();
			addPlace(place);
		}
	}

	public void sub(SimpleMarking marking) {
		Place place;
		Iterator it = marking.iterator();
		while (it.hasNext()) {
			place = (Place) it.next();
			delPlace(place);
		}
	}

	public String toString() {
		String s = "[";
		Iterator it = places.iterator();
		while (it.hasNext()) {
			Place place = (Place) it.next();
			s += String.valueOf(place);
			if (it.hasNext()) {
				s += ", ";
			}
		}
		s += "]";
		return s;
	}

	public int hashCode() {
		int ret = 17;
		for (Place p : places) {
			ret = 37 * ret + p.hashCode();
		}
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public TreeSet<Place> getPlaces() {
		return places;
	}

	public void setPlaces(TreeSet<Place> places) {
		this.places = places;
	}
}
