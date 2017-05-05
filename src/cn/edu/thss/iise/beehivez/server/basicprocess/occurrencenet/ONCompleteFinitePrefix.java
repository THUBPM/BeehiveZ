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

import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONCompleteFinitePrefix extends ONObject {
	private Vector<ONCondition> initialConditions = null;
	private ONMarking initialMarking = null;
	private ON on = null;
	private MyPetriNet mpn = null;
	
	private int maxDepth = Integer.MAX_VALUE;
	private Vector<ONEvent> cutOffEvents = new Vector<ONEvent>();
	private HashSet<ONEvent> invisibleEvents = new HashSet<ONEvent>();
	
	private HashMap<ONEvent, HashMap<ONEvent, String>> temporalOrder;
	
	public void setTemporalOrder(
			HashMap<ONEvent, HashMap<ONEvent, String>> temporalOrder) {
		this.temporalOrder = temporalOrder;
	}

	public ONEvent start = new ONEvent("-1", new MyPetriTransition("-1", "start", ""), null);
	public ONEvent end = new ONEvent("0", new MyPetriTransition("0", "end", ""), null);
	
	public ONCompleteFinitePrefix(){
		super();
		this.on = new ON();
		this.mpn = new MyPetriNet();
		this.temporalOrder = new HashMap<ONEvent, HashMap<ONEvent,String>>();
	}
	
	/**
	 * @param on
	 * @param mpn
	 * @param maxDepth
	 */
	public ONCompleteFinitePrefix(MyPetriNet mpn) {
		super();
		this.on =new ON();
		this.mpn = mpn;
		this.temporalOrder = new HashMap<ONEvent, HashMap<ONEvent,String>>();
	}
	public ONCompleteFinitePrefix(MyPetriNet mpn, int maxDepth) {
		super();
		this.on =new ON();
		this.mpn = mpn;
		this.maxDepth = maxDepth;
		this.temporalOrder = new HashMap<ONEvent, HashMap<ONEvent,String>>();
	}

	public ONCompleteFinitePrefix(ON on, int maxDepth){
		this.on = on;
		this.maxDepth = maxDepth;
		this.temporalOrder = new HashMap<ONEvent, HashMap<ONEvent,String>>();
	}
	
	public HashMap<ONEvent, String> getTransCut(ONEvent transSource){
		if(temporalOrder.containsKey(transSource)){
			return temporalOrder.get(transSource);
		}
		else{
			return null;
		}
	}
	
	public Vector<ONEvent> getLastEvents(){
		Vector<ONEvent> lastEvents = new Vector<ONEvent>();
		for(ONEvent event : on.getEveSet()){
			if(event.getSuccessiveEvents().isEmpty()){
				lastEvents.add(event);
			}
		}
		
		return lastEvents;
	}
	
	public void getCombinedCfp(Vector<ONEvent> couple){
		HashSet<ONCondition> conditions = new HashSet<ONCondition>();
		HashSet<ONEvent> events = new HashSet<ONEvent>();
		HashSet<ONArc> arcs = new HashSet<ONArc>();
		for(ONEvent event : couple){
			conditions.addAll(event.getSubCfp().getOn().getConSet());
			events.addAll(event.getSubCfp().getOn().getEveSet());
			arcs.addAll(event.getSubCfp().getOn().getArcSet());
		}
		on.getArcSet().addAll(arcs);
		on.getConSet().addAll(conditions);
		on.getEveSet().addAll(events);
	}
	
	public void addTemporalOrder(ONEvent correspond, ONEvent cut, String loop){
		HashMap<ONEvent, String> temporalOrderInfo = new HashMap<ONEvent, String>();
		temporalOrderInfo.put(cut, loop);
		
		if(temporalOrder.containsKey(correspond)){
			temporalOrder.get(correspond).putAll(temporalOrderInfo);
		}
		else{
			temporalOrder.put(correspond, temporalOrderInfo);
		}
	}
	
	public void implement(Vector<ONEvent> trace, HashSet<ONCondition> conditions, 
			int[][] executed, int[][] expected, ONCompleteFinitePrefix cfp){
		HashSet<ONCondition> temp = new HashSet<ONCondition>(conditions);
		int indexOfPrevEvent = -1;
		int indexOfEvent = -1;
		
		for(ONCondition condition : conditions){
			if(on.getEvesOUTOFCon(condition.getId()).isEmpty()){
				temp.remove(condition);
			}
		}
		conditions = temp;
		
		Vector<ONEvent> enabled = new Vector<ONEvent>();
		if(!conditions.isEmpty()){
			for(ONEvent event : on.getEveSet()){
				if(conditions.containsAll(on.getConsINTOEve(event.getId()))){
					enabled.add(event);
				}
			}
			ONEvent selectedEvent = null;
			int selectedIndex = 0;
			int minimumExecuted = 10000;
			
			if(trace.isEmpty()){
				if(!enabled.isEmpty()){
					selectedEvent = enabled.get(0);
					trace.add(selectedEvent);
				}
			}
			else{
				for(int i = 0; i < enabled.size(); ++i){
					indexOfEvent = cfp.getOn().indexOfEvent(enabled.get(i));
					
					
					ONEvent prevEvent = trace.get(trace.size() - 1);
					indexOfPrevEvent = cfp.getOn().indexOfEvent(prevEvent);
					
					if(executed[indexOfPrevEvent][indexOfEvent] < minimumExecuted && 
							executed[indexOfPrevEvent][indexOfEvent] <= expected[indexOfPrevEvent][indexOfEvent]){
						selectedEvent = enabled.get(i);
						selectedIndex = indexOfEvent;
						minimumExecuted = executed[indexOfPrevEvent][indexOfEvent];
					}
				}
				
				if(selectedEvent != null){
					trace.add(selectedEvent);
					++executed[indexOfPrevEvent][selectedIndex];
				}
			}
			
			
			if(selectedEvent != null){
				conditions.removeAll(on.getConsINTOEve(selectedEvent.getId()));
				if(selectedEvent.isCutOffEvent()){
					ONEvent correspondingEvent = (ONEvent)selectedEvent.object;
					for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
						for(ONCondition con2 : on.getConsOUTOFEve(selectedEvent.getId())){
							if(con1.getPlace() == con2.getPlace()){
								conditions.add(con1);
							}
						}
					}
				}
				else{
					conditions.addAll(on.getConsOUTOFEve(selectedEvent.getId()));
				}
				implement(trace, conditions, executed, expected, cfp);
			}
		}
	}
	
	public void implement(Vector<ONEvent> trace, Vector<ONEvent> stopEvents, HashSet<ONCondition> conditions, int[][] visited,
			int[][] executed, int[][] expected, ONCompleteFinitePrefix cfp){
		
		boolean isEmpty = true;
		ONEvent selectedEvent = null;
		int indexOfPrevEvent = -1;
		int indexOfEvent = -1;
		int selectedIndex = 0;
		int minimumExecuted = 10000;
		int minimumVisited = 10000;
		int minimumExpected = 2;
		
		for(ONCondition condition : conditions){
			if(!on.getEvesOUTOFCon(condition.getId()).isEmpty()){
				isEmpty = false;
			}
		}
		
		if(!isEmpty){
			//calculate all the enabled events
			Vector<ONEvent> enabled = new Vector<ONEvent>();
			for(ONEvent event : on.getEveSet()){
				if(conditions.containsAll(on.getConsINTOEve(event.getId()))){
					enabled.add(event);
				}
			}
			//if the other stop event cannot be enabled after the se, add se into selectedEvents
			//and exclude it from enabled. because the other stop event must be fired after one stop event.
			if(stopEvents.size() == 2 && enabled.contains(stopEvents.firstElement())){
				ONEvent other = stopEvents.lastElement();
				HashSet<ONCondition> temp = new HashSet<ONCondition>(conditions);
				temp.removeAll(on.getConsINTOEve(stopEvents.firstElement().getId()));
				temp.addAll(stopEvents.firstElement().getCorrespondingSuccessiveConditions());
				if(!temp.containsAll(on.getConsINTOEve(other.getId()))){
					enabled.remove(stopEvents.firstElement());
				}
				enabled.remove(stopEvents.lastElement());
			}
			//select one of the enabled events
			if(trace.isEmpty()){
				if(!enabled.isEmpty()){
					selectedEvent = enabled.get(0);
					trace.add(selectedEvent);
				}
			}
			else{
				indexOfPrevEvent = cfp.getOn().indexOfEvent(trace.lastElement());
				
//				for(int i = 0; i < enabled.size(); ++i){
//					ONEvent se = enabled.get(i);
//					if(stopEvents.size() == 2 && stopEvents.contains(se)){
//						ONEvent other = null;
//						for(ONEvent stop : stopEvents){
//							if(!stop.equals(se)){
//								other = stop;
//							}
//						}
//						if(other != null){
//							HashSet<ONCondition> temp = new HashSet<ONCondition>(conditions);
//							temp.removeAll(on.getConsINTOEve(se.getId()));
//							temp.addAll(se.getCorrespondingSuccessiveConditions());
//							if(!temp.containsAll(on.getConsINTOEve(other.getId()))){
//								enabled.remove(se);
//								continue;
//							}
//						}
//					}
//				}
				
				//the first heuristic rule: select those whose "executed" is the least.
				for(ONEvent e : enabled){
					indexOfEvent = cfp.getOn().indexOfEvent(e);
					if(executed[indexOfPrevEvent][indexOfEvent] <= minimumExecuted ){
							minimumExecuted = executed[indexOfPrevEvent][indexOfEvent];
					}
				}
				//the second heuristic rule: select those whose "visited" is the least.
				for(ONEvent e : enabled){
					indexOfEvent = cfp.getOn().indexOfEvent(e);
					if(executed[indexOfPrevEvent][indexOfEvent] == minimumExecuted){
						if(visited[indexOfPrevEvent][indexOfEvent] <= minimumVisited){
							minimumVisited = visited[indexOfPrevEvent][indexOfEvent];
						}
					}
				}
				//the third heuristic rule: select those whose "expected" is the least.
				for(ONEvent e : enabled){
					indexOfEvent = cfp.getOn().indexOfEvent(e);
					if(executed[indexOfPrevEvent][indexOfEvent] == minimumExecuted
						&& visited[indexOfPrevEvent][indexOfEvent] == minimumVisited){
						if(expected[indexOfPrevEvent][indexOfEvent] <= minimumExpected){
							minimumExpected = expected[indexOfPrevEvent][indexOfEvent];
							selectedEvent = e;
						}
					}
				}
				if(selectedEvent != null){
					trace.add(selectedEvent);
					selectedIndex = cfp.getOn().indexOfEvent(selectedEvent);
					++executed[indexOfPrevEvent][selectedIndex];
					++visited[indexOfPrevEvent][selectedIndex];
				}
			}
			
			if(selectedEvent != null){
				conditions.removeAll(on.getConsINTOEve(selectedEvent.getId()));
				conditions.addAll(selectedEvent.getCorrespondingSuccessiveConditions());
				if(stopEvents.isEmpty() || !selectedEvent.equals(stopEvents.firstElement())){
					implement(trace, stopEvents, conditions, visited, executed, expected, cfp);
				}
				else{
					stopEvents.remove(selectedEvent);
				}
			}
		}
	}
	
	/**
	 * @return the intialConditions
	 */
	public Vector<ONCondition> getIntialConditions() {
		return initialConditions;
	}

	/**
	 * @param intialConditions the intialConditions to set
	 */
	public void setInitialConditions(Vector<ONCondition> initialConditions) {
		this.initialConditions = initialConditions;
	}
	
	public HashSet<ONCondition> setInitialConditions(){
		HashSet<ONCondition> initial = new HashSet<ONCondition>();
		for(ONCondition condition : this.on.getConSet()){
			if(on.getEvesINTOCon(condition.getId()).isEmpty()){
				initial.add(condition);
			}
		}
		
		return initial;
	}

	/**
	 * @return the intialMarking
	 */
	public ONMarking getIntialMarking() {
		return initialMarking;
	}

	/**
	 * @param intialMarking the intialMarking to set
	 */
	public void setIntialMarking(ONMarking initialMarking) {
		this.initialMarking = initialMarking;
	}

	/**
	 * @return the cutOffEvents
	 */
	public Vector<ONEvent> getCutOffEvents() {
		return cutOffEvents;
	}

	/**
	 * @param cutOffEvents the cutOffEvents to set
	 */
	public void setCutOffEvents(Vector<ONEvent> cutOffEvents) {
		this.cutOffEvents = cutOffEvents;
	}
	
	public void destroyCFP(){
		this.cutOffEvents = null;
		this.initialConditions = null;
		this.initialMarking = null;
		this.mpn = null;
	}

	/**
	 * @return the onobj
	 */
	public ON getOn() {
		return on;
	}

	/**
	 * @param onobj the onobj to set
	 */
	public void setOn(ON on) {
		this.on = on;
	}

	/**
	 * @return the mpn
	 */
	public MyPetriNet getMpn() {
		return mpn;
	}

	/**
	 * @param mpn the mpn to set
	 */
	public void setMpn(MyPetriNet mpn) {
		this.mpn = mpn;
	}

	/**
	 * @return the nonFreeChoiceEvents
	 */
	public HashSet<ONEvent> getInvisibleEvents() {
		return invisibleEvents;
	}

	/**
	 * @return the temporalOrder
	 */
	public HashMap<ONEvent, HashMap<ONEvent, String>> getTemporalOrder() {
		return temporalOrder;
	}
}
