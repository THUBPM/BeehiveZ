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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.semanticweb.kaon2.qu;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONCompleteFinitePrefixBuilder {

	public int ONID = 0;
	public int beforeCut = 0;
	
	public static ONCompleteFinitePrefix cfp;
	private static MyPetriNet mpn = new MyPetriNet();
	
	public ONCompleteFinitePrefixBuilder(MyPetriNet mpn){
		this.mpn = mpn;
		cfp = new ONCompleteFinitePrefix(mpn, Integer.MAX_VALUE);
	}
	/**
	 * 
	 */
	public ONCompleteFinitePrefix Build() {
		// TODO Auto-generated constructor stub
		return build(mpn, Integer.MAX_VALUE);
	}
	
	public ONCompleteFinitePrefix build(MyPetriNet mpn, int maxDepth){
		HashMap<ONMarking, ONEvent> markings = new HashMap<ONMarking, ONEvent>();
		ONMarking initialMarking = new ONMarking();
		Vector<ONCondition> initialConditions = new Vector<ONCondition>();
		
		Iterator<MyPetriPlace> placeIt = mpn.getPlaceSet().iterator();
		while(placeIt.hasNext()){
			MyPetriPlace place = placeIt.next();
			int tokens = place.getCurrenttokens();
			if(tokens > 0){
				initialMarking.addPlace(place);
				ONCondition con = new ONCondition("" + (++ONID), place);
				
				for(ONCondition initialCon : initialConditions){
					con.addPrivateCondition(initialCon);
					initialCon.addPrivateCondition(con);
				}
				initialConditions.add(con);
				cfp.getOn().addObject(con);
			}
		}
		cfp.setInitialConditions(initialConditions);
		cfp.setIntialMarking(initialMarking);
		markings.put(initialMarking, null);
		
		PriorityQueue<ONPossibleExtension> queue = new PriorityQueue<ONPossibleExtension>();
		
		Iterator<ONPossibleExtension> peIt = getPossibleExtension(initialConditions, cfp).iterator();
		while(peIt.hasNext()){
			ONPossibleExtension currPE = peIt.next();
			queue.add(currPE);
		}
		
		while(!queue.isEmpty()){
			ONPossibleExtension pe = queue.remove();
			
			ONEvent newEvent = pe.getEvent();
			if(isInvisible(newEvent)){
				cfp.getInvisibleEvents().add(newEvent);
			}
			if(newEvent.getFoataLevel() >= maxDepth){
				continue;
			}
			
			beforeCut = ONID;
			Iterator<MyPetriPlace> succIt = mpn.getAllTranstionPlaceOut(newEvent.getTrans().getId()).iterator();
			while(succIt.hasNext()){
				MyPetriPlace place = succIt.next();
				ONCondition newCondition = new ONCondition("" + (++ONID), place);
				cfp.getOn().addObject(newCondition);
				ONArc newArc = new ONArc("" + (++ONID), newEvent.getId(), newCondition.getId());
				cfp.getOn().addObject(newArc);
			}
			ONMarking marking = newEvent.getLocalConfigurationMarking();
			ONEvent oldEvent = markings.get(marking);

			
			if(oldEvent != null || marking.equals(initialMarking)){
				newEvent.setCutOffEvent(true);
				cfp.getCutOffEvents().add(newEvent);
				if(oldEvent != null){
					newEvent.object = oldEvent;
					Vector<ONObject> vo = new Vector<ONObject>();
					vo.add(newEvent);
					newEvent.Predecessors(vo);
					if(vo.contains(oldEvent)){
						cfp.addTemporalOrder(oldEvent, newEvent, "loop");
					}
					else{
						cfp.addTemporalOrder(oldEvent, newEvent, "noloop");
					}
				}
				else{
					oldEvent = cfp.start;
					newEvent.object = oldEvent;
					cfp.addTemporalOrder(oldEvent, newEvent, "loop");
				}
			}
			else{
				while(ONID > beforeCut){
					cfp.getOn().removeObject("" + ONID);
					--ONID;
				}
				
				newEvent.updateCoConditions();
				
				markings.put(marking, newEvent);
				
				Vector<ONCondition> newConditions = new Vector<ONCondition>();
				succIt = mpn.getAllTranstionPlaceOut(newEvent.getTrans().getId()).iterator();
				while(succIt.hasNext()){
					MyPetriPlace place = succIt.next();
					ONCondition newCondition = new ONCondition("" + (++ONID), place);
					cfp.getOn().addObject(newCondition);
					ONArc newArc = new ONArc("" + (++ONID), newEvent.getId(), newCondition.getId());
					cfp.getOn().addObject(newArc);
					newCondition.setCommonCondition(newEvent.getCoConditions());
					Iterator<ONCondition> coIt = newEvent.getCoConditions().iterator();
					while(coIt.hasNext()){
						ONCondition condition = coIt.next();
						condition.addPrivateCondition(newCondition);
					}
					newConditions.add(newCondition);
				}
				
				for(int i = 0; i < newConditions.size(); ++i){
					ONCondition c1 = newConditions.get(i);
					for (int j = i + 1; j < newConditions.size(); j++) {
						ONCondition c2 = newConditions.get(j);
						c1.addPrivateCondition(c2);
						c2.addPrivateCondition(c1);
					}
				}
				
				peIt = getPossibleExtension(newConditions, cfp).iterator();
				while(peIt.hasNext()){
					ONPossibleExtension currPE = peIt.next();
					queue.add(currPE);
				}
			}
		}
		
		//update each event
		for(ONEvent e:cfp.getOn().getEveSet()){
			e.updateCoConditions();
		}
		
		return cfp;
	}
	
	public Vector<ONPossibleExtension> getPossibleExtension(Vector<ONCondition> newConditions, ONCompleteFinitePrefix cfp){
		Vector<ONPossibleExtension> pe = new Vector<ONPossibleExtension>();
		
		for(int newConditionIndex = 0; newConditionIndex < newConditions.size(); ++newConditionIndex){
			ONCondition newCondition = newConditions.get(newConditionIndex);
			
			Iterator<MyPetriTransition> tranIt = cfp.getMpn().getPlaceTransitionOut(newCondition.getPlace().getId()).iterator();
			
			Transition : while(tranIt.hasNext()){
				MyPetriTransition transition = tranIt.next();
				
				int sizeOfPre = cfp.getMpn().getAllTranstionPlaceIn(transition.getId()).size();
				
				Vector<Vector<ONCondition>> mappedPreConditions = new Vector<Vector<ONCondition>>();
				Iterator<MyPetriPlace> preIt = cfp.getMpn().getAllTranstionPlaceIn(transition.getId()).iterator();
				while(preIt.hasNext()){
					MyPetriPlace place = preIt.next();
					Vector<ONCondition> mappedConditions;
					if(newCondition.getPlace() == place){
						mappedConditions = new Vector<ONCondition>();
						mappedConditions.add(newCondition);
					}
					else{
						mappedConditions = (Vector<ONCondition>) place.obj;
						if(mappedConditions == null){
							continue Transition;
						}
					}
					mappedPreConditions.add(mappedConditions);
				}
				
				int[] preConditionIndex = new int[sizeOfPre];
				for(int i = 0; i < sizeOfPre; ++i){
					preConditionIndex[i] = 0;
				}
				
				ONCondition[] preConditions = new ONCondition[sizeOfPre];
				int indexOfPrePlace = 0;
				
				while(true){
					while(indexOfPrePlace < sizeOfPre){
						
						ONCondition testThisCondition = 
							mappedPreConditions.get(indexOfPrePlace)
							.get(preConditionIndex[indexOfPrePlace]);
						
						while(newConditions.contains(testThisCondition)
								&& Integer.parseInt(testThisCondition.getId()) < Integer.parseInt(newCondition.getId())){
							++preConditionIndex[indexOfPrePlace];
							
							while(preConditionIndex[indexOfPrePlace] >= mappedPreConditions.get(indexOfPrePlace).size()){
								preConditionIndex[indexOfPrePlace] = 0;
								--indexOfPrePlace;
								if(indexOfPrePlace < 0){
									continue Transition;
								}
								++preConditionIndex[indexOfPrePlace];
							}
							
							testThisCondition = mappedPreConditions.get(indexOfPrePlace).get(preConditionIndex[indexOfPrePlace]);
						}
						
						for(int j = 0; j < indexOfPrePlace; ++j){
							ONCondition condition = preConditions[j];
							if(!condition.getPrivateCondition().contains(testThisCondition)
									&& !condition.getCommonCondition().contains(testThisCondition)){
								++preConditionIndex[indexOfPrePlace];
								
								while(preConditionIndex[indexOfPrePlace] >= mappedPreConditions.get(indexOfPrePlace).size()){
									preConditionIndex[indexOfPrePlace] = 0;
									--indexOfPrePlace;
									if(indexOfPrePlace < 0){
										continue Transition;
									}
									++preConditionIndex[indexOfPrePlace];
								}
								
								testThisCondition = mappedPreConditions.get(indexOfPrePlace).get(preConditionIndex[indexOfPrePlace]);
								
								while(newConditions.contains(testThisCondition)
										&& Integer.parseInt(testThisCondition.getId()) < Integer.parseInt(newCondition.getId())){
									++preConditionIndex[indexOfPrePlace];
									
									while(preConditionIndex[indexOfPrePlace] >= mappedPreConditions.get(indexOfPrePlace).size()){
										preConditionIndex[indexOfPrePlace] = 0;
										--indexOfPrePlace;
										if(indexOfPrePlace < 0){
											continue Transition;
										}
										++preConditionIndex[indexOfPrePlace];
									}
									
									testThisCondition = mappedPreConditions
										.get(indexOfPrePlace).get(preConditionIndex[indexOfPrePlace]);
								}
								j = -1;
							}
						}
						
						preConditions[indexOfPrePlace] = testThisCondition;
						++indexOfPrePlace;
					}
					
					ONPossibleExtension possibleExtension = 
						new ONPossibleExtension("" + (++ONID), transition, preConditions, cfp);
					pe.add(possibleExtension);
					cfp.getOn().addObject(possibleExtension.getEvent());
					for(int k = 0; k < indexOfPrePlace; ++k){
						ONArc arc = new ONArc("" + (++ONID), 
								preConditions[k].getId(), possibleExtension.getEvent().getId());
						cfp.getOn().addObject(arc);
					}
					
					indexOfPrePlace = sizeOfPre - 1;
					++preConditionIndex[indexOfPrePlace];
					
					while(preConditionIndex[indexOfPrePlace] >= mappedPreConditions.get(indexOfPrePlace).size()){
						preConditionIndex[indexOfPrePlace] = 0;
						--indexOfPrePlace;
						if(indexOfPrePlace < 0){
							continue Transition;
						}
						++preConditionIndex[indexOfPrePlace];
					}
				}
			}
		}
		
		return pe;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("preliminaries_4 (2).pnml");
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
		cfpBuilder.Build();
//		TPCFP tpcfp = mcmillan.McMillanExe(input);
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = end - start;
		System.out.println(duration);
//		newPetriNet=tpcfp.getMpn();
		cfp.getOn().ONToMPN().export_pnml("preliminaries_4 (2)_unfolding.pnml");
	}

	/**
	 * @return the cfp
	 */
	public static ONCompleteFinitePrefix getCfp() {
		return cfp;
	}
	
	public static boolean isInvisible(ONEvent event){
		if(event.getLabel().isEmpty()){
			return true;
		}
		return false;
	}
	
//	public static boolean isNonFreeChoice(ONEvent event){
//		boolean isNFC = false;
////		boolean prevPlaceOfB = true;
////		boolean postPlaceOfA = false;
//		MyPetriTransition currentTransition = event.getTrans();
//		Vector<MyPetriPlace> vPrevPlace = mpn.getAllTranstionPlaceIn(currentTransition.getId());
//		if(vPrevPlace.size() < 2){
//			return isNFC;
//		}
//		Iterator<MyPetriPlace> itPrevPlace = mpn.getAllTranstionPlaceIn(currentTransition.getId()).iterator();
//		while(itPrevPlace.hasNext()){
//			MyPetriPlace prevPlace = itPrevPlace.next();
//			Iterator<MyPetriTransition> itSliblingTransition = mpn.getPlaceTransitionOut(prevPlace.getId()).iterator();
////			if(mpn.getPlaceTransitionOut(prevPlace.getId()).size() < 2){
////				prevPlaceOfB = false;
////				continue;
////			}
//			while(itSliblingTransition.hasNext()){
//				MyPetriTransition sliblingTransition = itSliblingTransition.next();
//				if(!sliblingTransition.equals(currentTransition)){
//					Vector<MyPetriPlace> vPrevPlaceOfSlb = mpn.getAllTranstionPlaceIn(sliblingTransition.getId());
//					if(!vPrevPlaceOfSlb.equals(vPrevPlace)){
//						isNFC = true;
//					}
////					Iterator<MyPetriPlace> itPrevPlaceOfSlibling = mpn.getAllTranstionPlaceIn(sliblingTransition.getId()).iterator();
////					while(itPrevPlaceOfSlibling.hasNext()){
////						MyPetriPlace prevPlaceOfSlibing = itPrevPlaceOfSlibling.next();
////						if(!prevPlaceOfSlibing.equals(prevPlace)){
////							isNFC = true;
////						}
////					}
//				}
//			}
////			if(prevPlaceOfB){
////				Iterator<MyPetriTransition> itPrevTransition = mpn.getPlaceTransitionIn(prevPlace.getId()).iterator();
////				while(itPrevTransition.hasNext()){
////					MyPetriTransition prevTransition = itPrevTransition.next();
////					Iterator<MyPetriPlace> itSiblingPlace = mpn.getAllTranstionPlaceOut(prevTransition.getId()).iterator();
////					if(mpn.getAllTranstionPlaceOut(prevTransition.getId()).size() < 2){
////						continue;
////					}
////					while(itSiblingPlace.hasNext()){
////						MyPetriPlace siblingPlace = itSiblingPlace.next();
////						if(mpn.getPlaceTransitionIn(siblingPlace.getId()).size() > 1){
////							postPlaceOfA = true;
////						}
////					}
////				}
////			}
//		}
//		
////		if(!postPlaceOfA && isNFC){
////			return true;
////		}
//		
//		return isNFC;
//	}
}
