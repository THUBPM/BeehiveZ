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
/**
 *@Author Wang Wenxing 
 *
 */
package cn.edu.thss.iise.beehivez.server.metric.tar.temp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-11-10
 */
public class ONTransitionAdjacentRelation {

	/**
	 * 2010-11-10
	 * @param args
	 * 
	 */
	
	private ONCompleteFinitePrefix cfp = null;
	private ONTransitionConcurrentRelation tcr = null;
	
	public HashMap<String, HashSet<String>> tar = null;
	public HashMap<String, HashSet<String>> tar0 = null;
	public HashMap<String, HashSet<String>> tarRe = null;
	public HashMap<String, HashSet<String>> tarIm = null;
	
	private HashSet<ONEvent> inloop = null;
	private LinkedList<ONEvent> queue = null;
	private HashSet<ONEvent> visited = null;
	
	/**
	 * @param cfp
	 */
	public ONTransitionAdjacentRelation(ONCompleteFinitePrefix cfp) {
		super();
		this.cfp = cfp;
		this.tcr = new ONTransitionConcurrentRelation(this.cfp);
		
		this.tar = new HashMap<String, HashSet<String>>();
		this.tar0 = new HashMap<String, HashSet<String>>();
		this.tarRe = new HashMap<String, HashSet<String>>();
		this.tarIm = new HashMap<String, HashSet<String>>();
		
		this.inloop = new HashSet<ONEvent>();
		this.queue = new LinkedList<ONEvent>();
		this.visited = new HashSet<ONEvent>();
		buildTAR();
	}

	public void buildTAR(){
		
		//Find causal transition adjacent relation from the complete finite prefix
		buildArtificialTAR();
		
		while(!queue.isEmpty()){
			ONEvent event = queue.removeFirst();
			Iterator<ONEvent> itPostEvent = event.getVisibleSuccessiveEvents().iterator();
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				if(event.detectImplicitDependency(postEvent)){
					addTARIM(event,postEvent);
				}
				else{
					addTAR(event, postEvent);
				}
				if(visited.add(postEvent)){
					queue.add(postEvent);
				}
			}
		}
		
		//complete the transition adjacent relation of cutoff events
		handleCutoff();
		
		//add the concurrent relation into the tar
		for(int i = 0; i < tcr.size; ++i){
			if(cfp.getOn().getEveSet().get(i).getLabel().isEmpty()){
				continue;
			}
			for(int j = 0; j < tcr.size; ++j){
				if(cfp.getOn().getEveSet().get(j).getLabel().isEmpty()){
					continue;
				}
				if(tcr.transitionConcurrentRelation[i][j]){
					ONEvent event1 = cfp.getOn().getEveSet().get(i);
					ONEvent event2 = cfp.getOn().getEveSet().get(j);
					if(event1.getLabel().isEmpty() || event2.getLabel().isEmpty()){
						continue;
					}
					if(inloop.contains(event1) && inloop.contains(event2)){
						addTARRE(event1, event2);
						addTARRE(event2, event1);
					}
					else{
						addTAR(cfp.getOn().getEveSet().get(i), cfp.getOn().getEveSet().get(j));
						addTAR(cfp.getOn().getEveSet().get(j), cfp.getOn().getEveSet().get(i));
					}
				}
			}
		}
		
		adjustTAR();
		adjustTARIM();
	}
	
	public void handleCutoff(){
		for(ONEvent cutoff : cfp.getCutOffEvents()){
//			if(cutoff.getLabel().isEmpty()){
//				continue;
//			}
			HashSet<String> postEventsOfCor = new HashSet<String>();
			ONEvent correnspondingEvent = (ONEvent)cutoff.object;
			String loop = cfp.getTemporalOrder().get(correnspondingEvent).get(cutoff);
			Iterator<ONCondition> itPostConditionOfCor;
			
			if(correnspondingEvent.equals(cfp.start)){
				itPostConditionOfCor = cfp.getIntialConditions().iterator();
			}
			else if(!correnspondingEvent.getVisibleSuccessiveEvents().isEmpty()){
				itPostConditionOfCor = correnspondingEvent.getLocalConfigurationConditions().iterator();
			}
			else{
				continue;
			}
			
			while(itPostConditionOfCor.hasNext()){
				boolean flag = false;
				ONCondition postConditionOfCor = itPostConditionOfCor.next();
				Iterator<ONCondition> itPostConditionOfCuts = cfp.getOn().getConsOUTOFEve(cutoff.getId()).iterator();
				while(itPostConditionOfCuts.hasNext()){
					ONCondition postConditionOfCuts = itPostConditionOfCuts.next();
					if(postConditionOfCor.getPlace() == postConditionOfCuts.getPlace()){
						flag = true;
						break;
					}
				}
				if(flag){
					Iterator<ONEvent> itPostEventOfCor = cfp.getOn().getEvesOUTOFCon(postConditionOfCor.getId()).iterator();
					Vector<ONEvent> visiblePostEvent = new Vector<ONEvent>();
					while(itPostEventOfCor.hasNext()){
						ONEvent postEventOfCor = itPostEventOfCor.next();
						if(postEventOfCor.getLabel().isEmpty()){
							visiblePostEvent.addAll(postEventOfCor.getVisibleSuccessiveEvents());
						}
						else{
							visiblePostEvent.add(postEventOfCor);
						}
					}
					for(ONEvent postEventOfCor : visiblePostEvent){
						Vector<ONEvent> prevEvents = new Vector<ONEvent>();
						
						if(loop.equals("loop") && cutoff.getLocalConfiguration().getEvents().contains(postEventOfCor)){
							if(!cutoff.getLabel().isEmpty()){
								addTARRE(cutoff, postEventOfCor);
							}
							buildTARRE(postEventOfCor, cutoff);
						}
						else{
							if(!cutoff.getLabel().isEmpty()){
								addTAR(cutoff, postEventOfCor);
							}
						}
						if(cutoff.getLabel().isEmpty()){
							prevEvents = cutoff.getVisiblePrecedingEvents();
						}
						for(ONEvent e : prevEvents){
							addTAR(e, postEventOfCor);
						}
					}
				}
			}
		}
	}
	
	public void buildArtificialTAR(){
		HashSet<String> single = new HashSet<String>();
		HashSet<String> foreward = new HashSet<String>();
		HashSet<String> backward = new HashSet<String>();
		
		Iterator<ONCondition> itCondition = cfp.getIntialConditions().iterator();
		while(itCondition.hasNext()){
			ONCondition condition = itCondition.next();
			
			Iterator<ONEvent> itPostEvent = 
				cfp.getOn().getEvesOUTOFCon(condition.getId()).iterator();
			Vector<ONEvent> visibleEvents = new Vector<ONEvent>();
			
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				
				if(postEvent.getLabel().isEmpty()){
					visibleEvents.addAll(postEvent.getVisibleSuccessiveEvents());
					for(ONEvent e : postEvent.getVisibleSuccessiveEvents()){
						foreward.add(e.getLabel());
					}
				}
				else{
					visibleEvents.add(postEvent);
				}
			}
			for(ONEvent postEvent : visibleEvents){
				if(visited.add(postEvent)){
					queue.add(postEvent);
				}
			}
		}
		
		for(ONEvent e : cfp.getOn().getEveSet()){
			if(!e.getLabel().isEmpty() && e.getPrecedingEvents().isEmpty()){
				ONEvent ce = e;
				if(e.isCutOffEvent()){
					ce = (ONEvent)e.object;
				}
				if(ce.getSuccessiveEvents().isEmpty()){
					single.add(ce.getLabel());
				}
			}
			if(e.getLabel().isEmpty()){
				if(e.getVisibleSuccessiveEvents().isEmpty()){
					for(ONEvent ee : e.getVisiblePrecedingEvents()){
						backward.add(ee.getLabel());
					}
				}
			}
		}
		
		addTAR0(single, "d");
		addTAR0(foreward, "s");
		addTAR0(backward, "e");
	}
	
	public boolean isSingle(ONEvent event){
		//if current event is not a cutoff node, check whether there are events following it. 
		//If none, it's a single event and return true.
		if(!event.isCutOffEvent()){
			if(!event.getVisibleSuccessiveEvents().isEmpty()){
				return false;
			}
			
			return true;
		}
		//If the event is a cutoff node, check the corresponding event.
		else{
			ONEvent correspondingEvent = (ONEvent)event.object;
			if(cfp.getTemporalOrder().get(correspondingEvent).get(event).equals("loop")){
				return false;
			}
			if(!correspondingEvent.getVisibleSuccessiveEvents().isEmpty()){
				return false;
			}
			
			return true;
		}
	}
	
	public void addTAR0(HashSet<String> e, String s){
		if(e.isEmpty()){
			return;
		}
		
		if(s == "d" || s == "s"){
			if(tar0.get("a_start").isEmpty()){
				tar0.put("a_start", e);
			}
			else{
				tar0.get("a_start").addAll(e);
			}
		}
		else if(s == "d" || s == "e"){
			if(tar0.get("a_end").isEmpty()){
				tar0.put("a_end", e);
			}
			else{
				tar0.get("a_end").addAll(e);
			}
		}
	}
	
	public void buildTAR0(ONEvent singleEvent){
		HashSet<String> single = new HashSet<String>();
		single.add(singleEvent.getLabel());
		tar0.put("start", single);
		
		HashSet<String> fake = new HashSet<String>();
		fake.add("end");
		tar0.put(singleEvent.getLabel(), fake);
	}
	
	public void buildTARRE(ONEvent correspondingEvent, ONEvent cutoff){
		Vector<ONEvent> events = cutoff.getLocalConfiguration().getEvents();
		LinkedList<ONEvent> subQueue = new LinkedList<ONEvent>();
		HashSet<ONEvent> subVisited = new HashSet<ONEvent>();
		
		events.removeAll(correspondingEvent.getLocalConfiguration().getEvents());
		if(subVisited.add(correspondingEvent)){
			subQueue.add(correspondingEvent);
		}
		
		while(!subQueue.isEmpty()){
			ONEvent event = subQueue.removeFirst();
			for(ONEvent e : event.getVisibleSuccessiveEvents()){
				if(events.contains(e)){
					addTARRE(event, e);
					
					if(subVisited.add(e)){
						subQueue.add(e);
					}
				}
			}
//			Iterator<ONCondition> itPostCondition = cfp.getOn().getConsOUTOFEve(event.getId()).iterator();
//			while(itPostCondition.hasNext()){
//				ONCondition postCondition = itPostCondition.next();
//				
//				Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(postCondition.getId()).iterator();
//				while(itPostEvent.hasNext()){
//					ONEvent postEvent = itPostEvent.next();
//					
//					if(events.contains(postEvent)){
//						addTARRE(event, postEvent);
//						
//						if(subVisited.add(postEvent)){
//							subQueue.add(postEvent);
//						}
//					}
//				}
//			}
		}
	}
	
	public boolean addTARIM(ONEvent event, ONEvent postEvent){
		if(tarIm.containsKey(event.getLabel())){
			if(!tarIm.get(event.getLabel()).add(postEvent.getLabel())){
				return false;
			}
		}
		else{
			HashSet<String> post = new HashSet<String>();
			post.add(postEvent.getLabel());
			tarIm.put(event.getLabel(), post);
		}
		
		return true;
	}
	
	public void adjustTAR(){
		HashMap<String, HashSet<String>> temp = new HashMap<String, HashSet<String>>();
		for(String key : tar.keySet()){
			if(tarRe.containsKey(key)){
				tar.get(key).removeAll(tarRe.get(key));
			}
			if(!tar.get(key).isEmpty()){
				temp.put(key, tar.get(key));
			}
		}
		tar = temp;
	}
	
	//remove all tars appear in tar or tarRe
	public void adjustTARIM(){
		HashMap<String, HashSet<String>> temp = new HashMap<String, HashSet<String>>();
		for(String key : tarIm.keySet()){
			if(tar.containsKey(key)){
				tarIm.get(key).removeAll(tar.get(key));
			}
			if(tarRe.containsKey(key)){
				tarIm.get(key).removeAll(tarRe.get(key));
			}
			if(!tarIm.get(key).isEmpty()){
				temp.put(key, tarIm.get(key));
			}
		}
		tarIm = temp;
	}
	
	public boolean addTARRE(ONEvent event, ONEvent postEvent){
		if(tarRe.containsKey(event.getLabel())){
			if(!tarRe.get(event.getLabel()).add(postEvent.getLabel())){
				return false;
			}
		}
		else{
			HashSet<String> post = new HashSet<String>();
			post.add(postEvent.getLabel());
			tarRe.put(event.getLabel(), post);
		}
		inloop.add(event);
		
		return true;
	}
	
	public boolean addTAR(ONEvent event, ONEvent postEvent){
//		MyPetriTransition transition = event.getTrans();
//		MyPetriTransition postTransition = postEvent.getTrans();
		if(tar.containsKey(event.getLabel())){
			if(!tar.get(event.getLabel()).add(postEvent.getLabel())){
				return false;
			}
		}
		else{
			HashSet<String> post = new HashSet<String>();
			post.add(postEvent.getLabel());
			tar.put(event.getLabel(), post);
		}
		
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("experiment_2\\p5.pnml");
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\experiment\\隐式依赖_1.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PnmlImport pImport = new PnmlImport();
		PetriNetResult pnr = null;
		try {
			pnr = (PetriNetResult) pImport.importFile(fin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PetriNet pn = pnr.getPetriNet();
		//pn.setIdentifier("1258790072312.pnml");
		input=MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
		
		long start = System.currentTimeMillis();
		System.out.println(start);
		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
		long middle = System.currentTimeMillis();
		System.out.println(middle - start);
		ONTransitionAdjacentRelation tar = new ONTransitionAdjacentRelation(cfp);
		long end = System.currentTimeMillis();
		System.out.println(end - middle);
	}

	/**
	 * @return the tar
	 */
	public HashMap<String, HashSet<String>> getTar() {
		return tar;
	}

	/**
	 * @return the tar0
	 */
	public HashMap<String, HashSet<String>> getTar0() {
		return tar0;
	}

	/**
	 * @return the tarRe
	 */
	public HashMap<String, HashSet<String>> getTarRe() {
		return tarRe;
	}

	/**
	 * @return the tarIm
	 */
	public HashMap<String, HashSet<String>> getTarIm() {
		return tarIm;
	}

}
