/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: wangwenxingbuaa@gmail.com 
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
package cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet;

import java.util.*;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONConfiguration implements Comparable<ONConfiguration>, Cloneable {
	
	private Vector<ONEvent> events = new Vector<ONEvent>();
	private ONCompleteFinitePrefix onCFP = null;
	private ONMarking marking = null;
	
	private static Comparator<ONEvent> lexiComparator = new Comparator<ONEvent>(){

		@Override
		public int compare(ONEvent one1, ONEvent one2) {
			// TODO Auto-generated method stub
			return Integer.parseInt(one1.getTrans().getId().substring(2)) - Integer.parseInt(one2.getTrans().getId().substring(2));
		}
	};
	
	private static Comparator<ONEvent> foataComparator = new Comparator<ONEvent>(){

		@Override
		public int compare(ONEvent e1, ONEvent e2) {
			// TODO Auto-generated method stub
			return e1.getFoataLevel() - e2.getFoataLevel();
		}
		
	};
	
	/**
	 * 
	 */
	public ONConfiguration() {
		super();
		this.onCFP = new ONCompleteFinitePrefix();
	}

	/**
	 * @param onCFP
	 */
	public ONConfiguration(ONCompleteFinitePrefix onCFP) {
		super();
		this.onCFP = onCFP;
	}
	
	public void addEvent(ONEvent event){
		this.events.add(event);
	}
	
	public void sub(ONConfiguration configuration){
		Iterator<ONEvent> eventIt = configuration.events.iterator();
		while(eventIt.hasNext()){
			ONEvent event = eventIt.next();
			this.events.remove(event);
		}
	}
	
	/**
	 * @return the events
	 */
	public Vector<ONEvent> getEvents() {
		return events;
	}

	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(Vector<ONEvent> events) {
		this.events = events;
	}
	
	public ONConfiguration getConfiguration(Vector<ONEvent> couple){
		HashSet<ONEvent> tempEvents = new HashSet<ONEvent>();
		Iterator<ONEvent> itEvent = couple.iterator();
		while(itEvent.hasNext()){
			ONEvent event = itEvent.next();
			tempEvents.addAll(event.getLocalConfiguration().getEvents());
		}
		events.addAll(tempEvents);
		
		return this;
	}

	/**
	 * @return the marking
	 */
	public ONMarking getMarking() {
		if(marking == null){
			marking = new ONMarking();
			Vector<ONCondition> conditionSet = new Vector<ONCondition>();
			Vector<ONCondition> subConditionSet = new Vector<ONCondition>();
			conditionSet.addAll(onCFP.getIntialConditions());
//			marking.addMarking(onCFP.getIntialMarking());
			Iterator<ONEvent> eventIt = events.iterator();
			while(eventIt.hasNext()){
				ONEvent event = eventIt.next();
				Iterator<ONCondition> preIt = this.onCFP.getOn().getConsINTOEve(event.getId()).iterator();
				Iterator<ONCondition> succIt = this.onCFP.getOn().getConsOUTOFEve(event.getId()).iterator();
				while(succIt.hasNext()){
					conditionSet.add(succIt.next());
				}
				while(preIt.hasNext()){
					subConditionSet.add(preIt.next());
				}
			}
			conditionSet.removeAll(subConditionSet);
			for(int i = 0; i < conditionSet.size(); ++i){
				marking.addPlace(conditionSet.get(i).getPlace());
			}
//			marking.delMarking(subMarking);
			return marking;
		}
		return marking;
	}
	
	public Vector<ONCondition> getConditions() {
			Vector<ONCondition> conditionSet = new Vector<ONCondition>();
			Vector<ONCondition> subConditionSet = new Vector<ONCondition>();
			conditionSet.addAll(onCFP.getIntialConditions());
//			marking.addMarking(onCFP.getIntialMarking());
			Iterator<ONEvent> eventIt = events.iterator();
			while(eventIt.hasNext()){
				ONEvent event = eventIt.next();
				Iterator<ONCondition> preIt = this.onCFP.getOn().getConsINTOEve(event.getId()).iterator();
				Iterator<ONCondition> succIt = this.onCFP.getOn().getConsOUTOFEve(event.getId()).iterator();
				while(succIt.hasNext()){
					conditionSet.add(succIt.next());
				}
				while(preIt.hasNext()){
					subConditionSet.add(preIt.next());
				}
			}
			conditionSet.removeAll(subConditionSet);
			HashSet<ONCondition> temp = new HashSet<ONCondition>(conditionSet);
			
			
		return new Vector<ONCondition>(temp);
	}
	
	public Vector<ONCondition> getCompleteConditions() {
		Vector<ONCondition> conditionSet = new Vector<ONCondition>();
		Vector<ONCondition> subConditionSet = new Vector<ONCondition>();
		conditionSet.addAll(onCFP.getIntialConditions());
//		marking.addMarking(onCFP.getIntialMarking());
		Iterator<ONEvent> eventIt = events.iterator();
		while(eventIt.hasNext()){
			ONEvent event = eventIt.next();
			Iterator<ONCondition> preIt = this.onCFP.getOn().getConsINTOEve(event.getId()).iterator();
			Iterator<ONCondition> succIt = event.getCorrespondingSuccessiveConditions().iterator();
			while(succIt.hasNext()){
				conditionSet.add(succIt.next());
			}
			while(preIt.hasNext()){
				subConditionSet.add(preIt.next());
			}
		}
		conditionSet.removeAll(subConditionSet);
		HashSet<ONCondition> temp = new HashSet<ONCondition>(conditionSet);
		
		
	return new Vector<ONCondition>(temp);
}
	
	public Vector<ONCondition> getConditionsOfTrace() {
		Vector<ONCondition> conditionSet = new Vector<ONCondition>();
		Vector<ONCondition> subConditionSet = new Vector<ONCondition>();
		conditionSet.addAll(onCFP.getIntialConditions());
//		marking.addMarking(onCFP.getIntialMarking());
		Iterator<ONEvent> eventIt = events.iterator();
		while(eventIt.hasNext()){
			ONEvent event = eventIt.next();
			Iterator<ONCondition> preIt = this.onCFP.getOn().getConsINTOEve(event.getId()).iterator();
			Iterator<ONCondition> succIt = this.onCFP.getOn().getConsOUTOFEve(event.getId()).iterator();
			while(succIt.hasNext()){
				conditionSet.add(succIt.next());
			}
			while(preIt.hasNext()){
				subConditionSet.add(preIt.next());
			}
		}
		conditionSet.removeAll(subConditionSet);
		HashSet<ONCondition> temp = new HashSet<ONCondition>(conditionSet);
		
		
	return new Vector<ONCondition>(temp);
}

	/**
	 * @param marking
	 *            the marking to set
	 */
	public void setMarking(ONMarking marking) {
		this.marking = marking;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ONConfiguration configuration) {
		// TODO Auto-generated method stub
		int ret = 0; 
		ret = this.events.size() - configuration.events.size();
		if(ret != 0){
			return ret;
		}
		else{
			ret = lexicalOrderComparison(this, configuration);
			if(ret != 0){
				return ret;
			}
			else{
				try {
					return foataNormalFormComparison((ONConfiguration)this.clone(), (ONConfiguration)configuration.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	private int lexicalOrderComparison(ONConfiguration configuration1, ONConfiguration configuration2){
		int ret = 0;
		
		Collections.sort(configuration1.events, lexiComparator);
		Collections.sort(configuration2.events, lexiComparator);
		Iterator<ONEvent> it1 = configuration1.events.iterator();
		Iterator<ONEvent> it2 = configuration2.events.iterator();
		while(it1.hasNext()){
			ONEvent event1 = it1.next();
			ONEvent event2 = it2.next();
			ret = event1.compareTo(event2);
			if(ret != 0){
				return ret;
			}
		}
		
		return ret;
	}
	
	private int foataNormalFormComparison(ONConfiguration configuration1, ONConfiguration configuration2){
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
		ONConfiguration fnf1 = new ONConfiguration(null);
		Iterator<ONEvent> ite1 = configuration1.events.iterator();
		ONEvent e1 = ite1.next();
		int currentFoataLevel1 = e1.getFoataLevel();
		fnf1.addEvent(e1);
		while (ite1.hasNext()) {
			e1 = ite1.next();
			if (e1.getFoataLevel() == currentFoataLevel1) {
				fnf1.addEvent(e1);
			} else {
				break;
			}
		}
		configuration1.sub(fnf1);

		ONConfiguration fnf2 = new ONConfiguration(null);
		Iterator<ONEvent> ite2 = configuration2.events.iterator();
		ONEvent e2 = ite2.next();
		int currentFoataLevel2 = e2.getFoataLevel();
		fnf2.addEvent(e2);
		while (ite2.hasNext()) {
			e2 = ite2.next();
			if (e2.getFoataLevel() == currentFoataLevel2) {
				fnf2.addEvent(e2);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		ONConfiguration configuration = new ONConfiguration(this.onCFP);
		configuration.events = (Vector<ONEvent>)this.events.clone();
		
		return configuration;
	}

}
