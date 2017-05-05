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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-11-10
 */
public class ONOrderingRelation {
	
	private ONCompleteFinitePrefix cfp = null;
	private int size = 0;
	private ONTransitionConcurrentRelation tcr = null;
	private OrderingRelation[][] orderRelations = null;
	
	/**
	 * @return the tcr
	 */
	public ONTransitionConcurrentRelation getTcr() {
		return tcr;
	}
	
	/**
	 * @param cfp
	 */
	public ONOrderingRelation(ONCompleteFinitePrefix cfp) {
		super();
		this.cfp = cfp;
		size = this.cfp.getOn().getEveSet().size();
		tcr = new ONTransitionConcurrentRelation(this.cfp);
		
		orderRelations = new OrderingRelation[size][size];
		for(int i = 0; i < orderRelations.length; ++i){
			for(int j = 0; j < orderRelations.length; ++j){
				orderRelations[i][j] = OrderingRelation.NONE;
			}
		}
		
		buildOrderRelation();
	}

	public void buildOrderRelation(){
		HashSet<ONEvent> visited = new HashSet<ONEvent>();
		LinkedList<ONEvent> queue = new LinkedList<ONEvent>();
		int indexOfEvent = 0;
		int indexOfPostEvent = 0;
		
		//calculate initial visible events
		Iterator<ONCondition> itCon = cfp.getIntialConditions().iterator();
		while(itCon.hasNext()){
			ONCondition condition = itCon.next();
			
			Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(condition.getId()).iterator();
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				if(visited.add(postEvent)){
					queue.add(postEvent);
				}
			}
		}
		
		//calculate the ordering relations recursively
		while(!queue.isEmpty()){
			ONEvent event = queue.removeFirst();
			Iterator<ONEvent> itPostEvent = event.getSuccessiveEvents().iterator();
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				
				//detect the implicit dependencies between the event and its successors.
				//if true, there is no adjacent relation.
				if(!event.detectImplicitDependency(postEvent)){
					indexOfEvent = cfp.getOn().indexOfEvent(event);
					indexOfPostEvent = cfp.getOn().indexOfEvent(postEvent);
					orderRelations[indexOfEvent][indexOfPostEvent] = OrderingRelation.PRE;
					
					if(visited.add(postEvent)){
						queue.add(postEvent);
					}
				}
			}
		}
		
		//complete the ordering relation by dealing with the visible cutoffs.
//		int indexOfCutoff = 0;
//		int indexOfPostEventOfCutoff = 0;
//		for(ONEvent cutoff : cfp.getCutOffEvents()){
//			//ignore those invisible cutoffs
//			if(cutoff.getLabel().isEmpty()){
//				continue;
//			}
//			
//			indexOfEvent = cfp.getOn().indexOfEvent(cutoff);
//			
//			//get the local configuration of the corresponding event.
//			ONEvent correspondingEvent = (ONEvent)cutoff.object;
//			Iterator<ONCondition> itPostConditionOfCorrespondingEvent = correspondingEvent.getLocalConfigurationConditions().iterator();
//			Iterator<ONCondition> itPostConditionOfCutoff = cfp.getOn().getConsOUTOFEve(cutoff.getId()).iterator();
//			
//			//find the enable event under the conditions of corresponding event
//			//if it's visible, establish the ordering relation between the cutoff and current event.
//			//else calculate its visible successors. But make sure there is no implicit dependency.
//			while(itPostConditionOfCorrespondingEvent.hasNext()){
//				boolean BeCommon = false;
//				ONCondition postConditionOfCorrespondingEvent = itPostConditionOfCorrespondingEvent.next();
//				while(itPostConditionOfCutoff.hasNext()){
//					ONCondition postConditionOfCutoffCondition = itPostConditionOfCutoff.next();
//					if(postConditionOfCutoffCondition.getPlace() == postConditionOfCorrespondingEvent.getPlace()){
//						BeCommon = true;
//						break;
//					}
//				}
//				if(BeCommon){
//					Iterator<ONEvent> itPostEvent = cfp.getOn().getEvesOUTOFCon(postConditionOfCorrespondingEvent.getId()).iterator();
//					while(itPostEvent.hasNext()){
//						ONEvent postEvent = itPostEvent.next();
//						if(!cutoff.detectImplicitDependency(postEvent, tcr)){
//							indexOfPostEvent = cfp.getOn().indexOfEvent(postEvent);
//							orderRelations[indexOfEvent][indexOfPostEvent] = OrderingRelation.PRE;
//						}
//					}
//				}
//			}
//		}
		
		//complete the ordering relations by calculating the concurrent relations, 
		//this may overwrite some elements in the matrix
		for(int i = 0; i < tcr.size; ++i){
			for(int j = 0; j < tcr.size; ++j){
				if(tcr.transitionConcurrentRelation[i][j]){
					this.orderRelations[i][j] = OrderingRelation.CON;
					this.orderRelations[j][i] = OrderingRelation.CON;
				}
			}
		}
	}
	
	/**
	 * @return the orderRelations
	 */
	public OrderingRelation[][] getOrderRelations() {
		return orderRelations;
	}

	/**
	 * @param orderRelations the orderRelations to set
	 */
	public void setOrderRelations(OrderingRelation[][] orderRelations) {
		this.orderRelations = orderRelations;
	}

	/**
	 * 2010-11-10
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\model\\4.pnml");
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
		ONOrderingRelation or = new ONOrderingRelation(cfp);
		
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = middle - start;
		System.out.println("CFP:" + duration);
		duration = end - middle;
		System.out.println("OR:" + duration);
		duration = end - start;
		System.out.println("TOTAL:" + duration);
		
		for(int i = 0; i < or.getOrderRelations().length; ++i){
			System.out.print('\t' + cfp.getOn().getEveSet().get(i).getLabel());
		}
		
		System.out.println();
		for(int i = 0; i < or.getOrderRelations().length; ++i){
			System.out.print(cfp.getOn().getEveSet().get(i).getLabel() + ":");
			for(int j = 0; j < or.getOrderRelations().length; ++j){
				System.out.print(or.getOrderRelations()[i][j]);
				System.out.print('\t');
			}
			System.out.println();
		}
	
//		cfpBuilder.cfp.getOn().ONToMPN().export_pnml("C:\\Users\\winever.winever-PC\\Documents\\QueryModel\\Non-free Choice\\Nonfree7_unfolding.pnml");
	}
}
