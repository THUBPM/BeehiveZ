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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Tao Jin
 * 
 */
public class Configuration implements Comparable<Configuration>, Cloneable {

	private ArrayList<Event> events = new ArrayList<Event>();
	private CompleteFinitePrefix _cfp = null;
	private SimpleMarking marking = null;

	// used for partial order comparison (e.g. <E)
	// sorted by event's id
	// sort only necessary
	private static Comparator<Event> lexiComparator = new Comparator<Event>() {

		@Override
		public int compare(Event o1, Event o2) {
			return o1.getOriginalTransition().getId()
					- o2.getOriginalTransition().getId();
		}

	};

	// used for foata normal form comparison (e.g. <F)
	// sorted by foata level
	// sort only necessary
	private static Comparator<Event> foataComparator = new Comparator<Event>() {

		@Override
		public int compare(Event o1, Event o2) {
			return o1.getFoataLevel() - o2.getFoataLevel();
		}

	};

	public Configuration(CompleteFinitePrefix cfp) {
		this._cfp = cfp;
	}

	public void add(Event event) {
		this.events.add(event);
	}

	public ArrayList<Event> getAllCausalEvents() {
		return this.events;
	}

	/**
	 * this function must be called in the end after all events are added into
	 * this configuration
	 * 
	 * the marking is represented using the places in the original Petri net
	 * 
	 * @return
	 */
	public SimpleMarking getMarking() {
		if (marking == null) {
			marking = new SimpleMarking();
			marking.add(_cfp.getInitialMarking());
			Iterator<Event> ite = events.iterator();
			LinkedList<Event> events = new LinkedList<Event>();
			events.addAll(this.events);
			//System.out.println(events);
			int count = 0;
			while(!events.isEmpty()){
				Event e = events.removeFirst();
				//System.out.println(e);
				if(marking.isEnabled(e)){
					count = 0;
					marking.firing(e);
					//System.out.println(marking);
				}else{
					count++;
					events.addLast(e);
				}
				if(count>=events.size()){
					return marking;
				}
			}
			/*
			while (ite.hasNext()) {
				SimpleMarking msub = new SimpleMarking();
				Event e = ite.next();
				System.out.println("event:"+e);
				Iterator<Place> itPrePlace = e.getOriginalTransition()
						.getPredecessors().iterator();
				Iterator<Place> itPostPlace = e.getOriginalTransition()
						.getSuccessors().iterator();
				while (itPostPlace.hasNext()) {
					Place next = itPostPlace.next();
					marking.addPlace(next);
					System.out.println("marking add:"+next);
				}
				while (itPrePlace.hasNext()) {
					Place next = itPrePlace.next();
					msub.addPlace(next);
					System.out.println("msub add:"+next);
				}
				marking.sub(msub);
				System.out.println(marking);
			}
			*/
			//System.out.println("end");
			return marking;
		} else {
			return marking;
		}
	}

	// only used for foata normal form comparison
	private void sub(Configuration sub) {
		Iterator<Event> ite = sub.events.iterator();
		while (ite.hasNext()) {
			Event e = ite.next();
			this.events.remove(e);
		}
	}

	@Override
	public int compareTo(Configuration o) {
		int ret = 0;
		ret = this.events.size() - o.events.size();
		if (ret != 0) {
			return ret;
		} else {
			ret = lexicalOrderComparison(this, o);
			if (ret != 0) {
				return ret;
			} else {
				// because foataNormalFormComparison will damage the
				// configuration, we use the copies of them
				Configuration c1 = (Configuration) this.clone();
				Configuration c2 = (Configuration) o.clone();

				return foataNormalFormComparison(c1, c2);
			}
		}
	}

	/**
	 * partial order <E the prerequsite is that their size is the same
	 * 
	 * @param configuration1
	 * @param configuration2
	 * @return
	 */
	private int lexicalOrderComparison(Configuration configuration1,
			Configuration configuration2) {
		int ret = 0;
		// sort by lexical order first
		Collections.sort(configuration1.events, lexiComparator);
		Collections.sort(configuration2.events, lexiComparator);
		Iterator<Event> ite1 = configuration1.events.iterator();
		Iterator<Event> ite2 = configuration2.events.iterator();
		while (ite1.hasNext()) {
			Event e1 = ite1.next();
			Event e2 = ite2.next();
			ret = e1.compareTo(e2);
			if (ret != 0) {
				return ret;
			}
		}
		return ret;
	}

	/**
	 * the total order comparison, that is, <F the foata level is used here to
	 * speed the comparison the given configuration will be damaged during the
	 * comparison, be careful about this
	 * 
	 * @param configuration1
	 * @param configuration2
	 * @return
	 */
	private int foataNormalFormComparison(Configuration configuration1,
			Configuration configuration2) {
		int ret = 0;

		if (configuration1.events.size() == 0
				&& configuration2.events.size() == 0) {
			return 0;
		} else if (configuration1.events.size() == 0) {
			return -1;
		} else if (configuration2.events.size() == 0) {
			return 1;
		}

		// sort by foata order first
		Collections.sort(configuration1.events, foataComparator);
		Collections.sort(configuration2.events, foataComparator);

		// compute foata normal form
		// the number of events would not be 0, because this situation has been
		// handled before
		Configuration fnf1 = new Configuration(null);
		Iterator<Event> ite1 = configuration1.events.iterator();
		Event e1 = ite1.next();
		int currentFoataLevel1 = e1.getFoataLevel();
		fnf1.add(e1);
		while (ite1.hasNext()) {
			e1 = ite1.next();
			if (e1.getFoataLevel() == currentFoataLevel1) {
				fnf1.add(e1);
			} else {
				break;
			}
		}
		configuration1.sub(fnf1);

		Configuration fnf2 = new Configuration(null);
		Iterator<Event> ite2 = configuration2.events.iterator();
		Event e2 = ite2.next();
		int currentFoataLevel2 = e2.getFoataLevel();
		fnf2.add(e2);
		while (ite2.hasNext()) {
			e2 = ite2.next();
			if (e2.getFoataLevel() == currentFoataLevel2) {
				fnf2.add(e2);
			} else {
				break;
			}
		}
		configuration2.sub(fnf2);

		// compare based on foata normal form
		ret = fnf1.events.size() - fnf2.events.size();
		if (ret != 0) {
			return ret;
		} else {
			ret = lexicalOrderComparison(fnf1, fnf2);
			if (ret != 0) {
				return ret;
			} else {
				return foataNormalFormComparison(configuration1, configuration2);
			}
		}
	}

	public Object clone() {
		Configuration configuration = new Configuration(this._cfp);
		configuration.events = (ArrayList<Event>) this.events.clone();
		return configuration;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
}
