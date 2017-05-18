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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.text.DefaultEditorKit.CutAction;

import bsh.This;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONEvent extends ONObject implements Comparable<ONEvent>, Cloneable {
	private MyPetriTransition trans = null;
	private ONConfiguration localConfiguration = null;
	private ONCompleteFinitePrefix cfp = null;
	private String arcInId;
	private String arcOutId;
	private boolean isCutOffEvent = false;
	private int foataLevel = -1;
	
	private HashSet<ONCondition> coConditions = new HashSet<ONCondition>();
	
	public ONEvent(String id, MyPetriTransition trans, ONCompleteFinitePrefix cfp){
		this.setId(id);
		this.setLabel(trans.getName());
		this.setTrans(trans);
		this.setType(ONObject.EVENT);
		this.cfp = cfp;
	}

	public MyPetriTransition getTrans() {
		return trans;
	}

	public void setTrans(MyPetriTransition trans) {
		this.trans = trans;
	}

	public String getArcInId() {
		return arcInId;
	}

	public void setArcInId(String arcInId) {
		this.arcInId = arcInId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ONEvent o) {
		// TODO Auto-generated method stub
		ONEvent that = (ONEvent) o;
		return Integer.parseInt(this.trans.getId().substring(2)) - Integer.parseInt(that.trans.getId().substring(2));
	}

	public String getArcOutId() {
		return arcOutId;
	}

	public void setArcOutId(String arcOutId) {
		this.arcOutId = arcOutId;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		ONEvent obj = null;
		obj = (ONEvent) super.clone();
		return obj;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return label;
	}

	/**
	 * @return the coConditions
	 */
	public HashSet<ONCondition> getCoConditions() {
		return coConditions;
	}

	/**
	 * @param coConditions the coConditions to set
	 */
	public void setCoConditions(HashSet<ONCondition> coConditions) {
		this.coConditions = coConditions;
	}
	
	public ONCompleteFinitePrefix getSubCfp(){
		ONCompleteFinitePrefix subCfp = new ONCompleteFinitePrefix();
		Vector<ONObject> vo = new Vector<ONObject>();
		vo.add(this);
		Predecessors(vo);
		
		for(int i = 0; i < vo.size(); ++i){
			subCfp.getOn().addObject(vo.get(i));
			if(vo.get(i) instanceof ONEvent){
				for(ONCondition con : cfp.getOn().getConsOUTOFEve(vo.get(i).getId())){
					subCfp.getOn().addObject(con);
				}
			}
		}
		
		subCfp.getOn().getArcSet().addAll(cfp.getOn().getArcSet());
		
		return subCfp;
	}

	/**
	 * @return the localConfiguration
	 */
	public ONConfiguration getLocalConfiguration() {
		Vector<ONObject> vo = new Vector<ONObject>();
		
		localConfiguration = new ONConfiguration(cfp);
		vo.add(this);
		Predecessors(vo);
		
		for(int i = 0; i < vo.size(); ++i){
			if(vo.get(i) instanceof ONEvent){
				ONEvent oe = (ONEvent)vo.get(i);
				localConfiguration.addEvent(oe);
			}
		}
		
		Iterator<ONCondition> conIt = this.cfp.getOn().getConsINTOEve(this.getId()).iterator();
		while(conIt.hasNext()){
			ONCondition preCon = conIt.next();
			Iterator<ONEvent> eveIt = this.cfp.getOn().getEvesINTOCon(preCon.getId()).iterator();
			if(!eveIt.hasNext()){
				if(this.foataLevel == -1){
					this.foataLevel = 1;
				}
			}
			else{
				ONEvent preEve = eveIt.next();
				if(preEve.foataLevel + 1 > this.foataLevel){
					this.foataLevel = preEve.foataLevel + 1;
				}
			}
		}
		
		return localConfiguration;
	}
	
	/**
	 * get all the predecessors of the vector, regardless of conditions or events
	 */
	public void Predecessors(Vector<ONObject> vo){
		ONObject o;
		int index = 0;
		do{
			o = vo.get(index);
			if(o instanceof ONEvent){
				ONEvent one = (ONEvent)o;
				Vector<ONCondition> vonc = cfp.getOn().getConsINTOEve(one.getId());
				vo.addAll(vonc);
			}
			else if(o instanceof ONCondition){
				ONCondition onc = (ONCondition)o;
				Vector<ONEvent> vone = cfp.getOn().getEvesINTOCon(onc.getId());
				vo.addAll(vone);
			}
			++index;
		}while(index < vo.size());
		HashSet<ONObject> ho = new HashSet<ONObject>(vo);
		vo = new Vector<ONObject>(ho);
	}
	
	public ONMarking getLocalConfigurationMarking(){
		return this.getLocalConfiguration().getMarking();
	}
	
	public Vector<ONCondition> getLocalConfigurationConditions(){
		return this.getLocalConfiguration().getConditions();
	}
	public Vector<ONCondition> getCompleteLocalConfigurationConditions(){
		return this.getLocalConfiguration().getCompleteConditions();
	}
	
	public void updateCoConditions(){
		Iterator<ONCondition> conIt = this.cfp.getOn().getConsINTOEve(this.getId()).iterator();
		ONCondition condition = conIt.next();
		this.coConditions.addAll(condition.getPrivateCondition());
		this.coConditions.addAll(condition.getCommonCondition());
		
		while(conIt.hasNext()){
			HashSet<ONCondition> tmp = new HashSet<ONCondition>();
			condition = conIt.next();
			tmp.addAll(condition.getCommonCondition());
			tmp.addAll(condition.getPrivateCondition());
			this.coConditions.retainAll(tmp);
		}
	}
	
	/**
	 * get all the successive conditions of the event, if the event is a cutoff event
	 * return the successive conditions of its corresponding event.
	 */
	public HashSet<ONCondition> getCorrespondingSuccessiveConditions(){
		HashSet<ONCondition> conditions = new HashSet<ONCondition>();
		if(this.isCutOffEvent()){
			ONEvent correspondingEvent = (ONEvent)this.object;
			for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
				for(ONCondition con2 : this.cfp.getOn().getConsOUTOFEve(this.getId())){
					if(con1.getPlace() == con2.getPlace()){
						conditions.add(con1);
					}
				}
			}
		}
		else{
			conditions.addAll(this.cfp.getOn().getConsOUTOFEve(this.getId()));
		}
		
		return conditions;
	}
	
	/**
	 * get the preceding events of current event, without computing
	 * the preceding cutoff events.
	 */
	public Vector<ONEvent> getPrecedingEvents(){
		Vector<ONEvent> prevEvents = new Vector<ONEvent>();
		Iterator<ONCondition> itPrevCondition = cfp.getOn().getConsINTOEve(this.getId()).iterator();
		while(itPrevCondition.hasNext()){
			ONCondition prevCondition = itPrevCondition.next();
			Iterator<ONEvent> itPrevEvent = cfp.getOn().getEvesINTOCon(prevCondition.getId()).iterator();
			while(itPrevEvent.hasNext()){
				ONEvent prevEvent = itPrevEvent.next();
				prevEvents.add(prevEvent);
			}
		}
		
		return prevEvents;
	}
	
	/**
	 * get the preceding events of current event, with the preceding cutoff events.
	 */
	public Vector<ONEvent> getCompletePrecedingEvents(){
		Vector<ONEvent> prevEvents = new Vector<ONEvent>();
		Iterator<ONCondition> itPrevCondition = cfp.getOn().getConsINTOEve(this.getId()).iterator();
		while(itPrevCondition.hasNext()){
			ONCondition prevCondition = itPrevCondition.next();
			Iterator<ONEvent> itPrevEvent = cfp.getOn().getEvesINTOCon(prevCondition.getId()).iterator();
			while(itPrevEvent.hasNext()){
				ONEvent prevEvent = itPrevEvent.next();
				prevEvents.add(prevEvent);
			}
			for(ONEvent event : cfp.getCutOffEvents()){
				for(ONCondition condition : cfp.getOn().getConsOUTOFEve(event.getId())){
					if(condition.getPlace() == prevCondition.getPlace()){
						prevEvents.add(event);
					}
				}
			}
		}
		
		return prevEvents;
	}
	
	/**
	 * get the visible preceding events of current event, 
	 * without the preceding cutoff events.
	 */
	public Vector<ONEvent> getVisiblePrecedingEvents(){
		Vector<ONEvent> prevEvents = getPrecedingEvents();
		
		Vector<ONEvent> prevVisibleEvents = new Vector<ONEvent>();
		Vector<ONEvent> prevCompleteEvents = new Vector<ONEvent>();
		prevCompleteEvents.addAll(prevEvents);
		
		for(ONEvent event : prevCompleteEvents){
			if(event.getLabel().isEmpty()){
				prevVisibleEvents.addAll(event.getVisiblePrecedingEvents());
			}
			else{
				prevVisibleEvents.add(event);
			}
		}
		
		return prevVisibleEvents;
	}
	
	//compute the successors of current event
	//if it's a cutoff event, return the successors of its corresponding event
	public Vector<ONEvent> getSuccessiveEvents(){
		Vector<ONEvent> postEvents = new Vector<ONEvent>();
		Iterator<ONCondition> itPostCondition = null;
		if(!this.isCutOffEvent()){
			itPostCondition = cfp.getOn().getConsOUTOFEve(this.getId()).iterator();
			while(itPostCondition.hasNext()){
				ONCondition postCondition = itPostCondition.next();
				Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(postCondition.getId()).iterator();
				while(itPostEvent.hasNext()){
					ONEvent postEvent = itPostEvent.next();
					postEvents.add(postEvent);
				}
			}
		}
		else{
			ONEvent correspondingEvent = (ONEvent)this.object;
			
			//find the enable event under the conditions of corresponding event
			//if it's visible, establish the ordering relation between the cutoff and current event.
			//else calculate its visible successors. But make sure there is no implicit dependency.
			for(ONCondition postConditionOfCorrespondingEvent : correspondingEvent.getLocalConfigurationConditions()){
				boolean BeCommon = false;
				for(ONCondition postConditionOfCutoff : cfp.getOn().getConsOUTOFEve(this.getId())){
					if(postConditionOfCutoff.getPlace() == postConditionOfCorrespondingEvent.getPlace()){
						BeCommon = true;
						break;
					}
				}
				if(BeCommon){
					Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(postConditionOfCorrespondingEvent.getId()).iterator();
					while(itPostEvent.hasNext()){
						ONEvent postEvent = itPostEvent.next();
							postEvents.add(postEvent);
					}
				}
			}
		}
		
		return postEvents;
	}
	
	/**
	 * get the visible successive events of current event, 
	 * with the successive events of cutoff events.
	 */
	public Vector<ONEvent> getVisibleSuccessiveEvents(){
		Vector<ONEvent> postVisibleEvents = new Vector<ONEvent>();
		Vector<ONEvent> postEvents = getSuccessiveEvents();
		
		for(ONEvent event : postEvents){
			if(event.getLabel().isEmpty()){
				postVisibleEvents.addAll(event.getVisibleSuccessiveEvents());
			}
			else{
				postVisibleEvents.add(event);
			}
		}
		
		return postVisibleEvents;
	}
	
	public boolean hasSuccessiveEvents(){
		Iterator<ONCondition> itPostCondition = cfp.getOn().getConsOUTOFEve(this.getId()).iterator();
		while(itPostCondition.hasNext()){
			ONCondition postCondition = itPostCondition.next();
			Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(postCondition.getId()).iterator();
			while(itPostEvent.hasNext()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * get all the successive events of current event, 
	 * with the successive events of cutoff events.
	 */
	public HashSet<ONEvent> getAllSuccessiveEvents(){
		HashSet<ONEvent> all = new HashSet<ONEvent>();
		HashSet<ONEvent> visited = new HashSet<ONEvent>();
		Vector<ONEvent> postEvents = null;
		LinkedList<ONEvent> queue = new LinkedList<ONEvent>();
		
		queue.add(this);
		while(!queue.isEmpty()){
			ONEvent first = queue.removeFirst();
			for(ONEvent e : first.getSuccessiveEvents()){
				if(visited.add(e)){
					queue.add(e);
					all.add(e);
				}
			}
//			if(visited.add(first)){
//				postEvents = first.getSuccessiveEvents();
//				queue.addAll(postEvents);
//				all.addAll(postEvents);
//			}
		}
		
		return all;
	}
	
	/**
	 * get all the successive cutoff events of current event, 
	 */
	public HashSet<ONEvent> getSuccessiveCutoffs(){
		HashSet<ONEvent> cutoffs = new HashSet<ONEvent>();
		LinkedList<ONEvent> queue = new LinkedList<ONEvent>();
		
		queue.addAll(this.getSuccessiveEvents());
		while(!queue.isEmpty()){
			ONEvent first = queue.removeFirst();
			if(first.isCutOffEvent()){
				if(!first.equals(this)){
					cutoffs.add(first);
				}
			}
			else{
				queue.addAll(first.getSuccessiveEvents());
			}
		}
		
		return cutoffs;
	}
	
	/**
	 * detect the implicit dependency
	 */
	public boolean detectImplicitDependency(ONEvent postEvent){
		//connected
		if(!this.getSuccessiveEvents().contains(postEvent)){
			return false;
		}
		
		//disjunctive
		//add concurrent conditions of current event into a set.
		//@possibleConditions stores all the possible marking after current event
		//these markings should not enable the postEvent
		HashSet<ONCondition> possibleConditions = new HashSet<ONCondition>();
		possibleConditions.addAll(this.coConditions);
		
		//add the following conditions into the set
		ONEvent ce = this;
		if(this.isCutOffEvent){
			ce = (ONEvent)this.object;
		}
		possibleConditions.addAll(ce.getLocalConfigurationConditions());
		
		//under current conditions 
		//put successive conditions of any invisible event enabled into the set
		for(ONEvent e:cfp.getInvisibleEvents()){
			if(possibleConditions.containsAll(cfp.getOn().getConsINTOEve(e.getId()))){
				possibleConditions.addAll(cfp.getOn().getConsINTOEve(e.getId()));
			}
		}
		
		//store the preceding conditions of the postEvent
		//then remove the conditions contained in possibleConditions
		//if leftConditions is empty, it means postEvent is enabled after event.
		HashSet<ONCondition> leftConditions = new HashSet<ONCondition>();
		leftConditions.addAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
		leftConditions.removeAll(possibleConditions);
		
		if(leftConditions.isEmpty()){
			return false;
		}
		
		//reachable
		for(ONCondition c : leftConditions){
			for(ONEvent e:cfp.getOn().getEvesINTOCon(c.getId())){
				if (this.getAllSuccessiveEvents().contains(e)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	

	/**
	 * @return the foatoLevel
	 */
	public int getFoataLevel() {
		return foataLevel;
	}

	/**
	 * @return the isCutOffEvent
	 */
	public boolean isCutOffEvent() {
		return isCutOffEvent;
	}

	/**
	 * @param isCutOffEvent the isCutOffEvent to set
	 */
	public void setCutOffEvent(boolean isCutOffEvent) {
		this.isCutOffEvent = isCutOffEvent;
	}
	
	
}
