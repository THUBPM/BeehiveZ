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
package cn.edu.thss.iise.beehivez.util.loggenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
/**
 * 
 * @author Nianhua Wu
 * 
 * @date 2010-3-10
 *
 */
public class GetSucceRelation {

	private ArrayList<String> AllRelation = null;
	private Vector<String> states = null;
	private HashMap<Transition,LinkedList<Transition>> map = null;

	public GetSucceRelation() {
		AllRelation = new ArrayList<String>();
		states = new Vector<String>();
		map = new HashMap<Transition,LinkedList<Transition>>();
	}

	public ArrayList<String> getAllRelation() {
		return AllRelation;
	}
	

	public HashMap<Transition, LinkedList<Transition>> getMap() {
		return map;
	}

	public void display() {
		Iterator it = AllRelation.iterator();
		while (it.hasNext()) {
			String element = (String) it.next();
			System.out.println(element);
		}
	}
	

	public void GetAllSucceRelation(PetriNet pn) {
		ArrayList<Place> pList = pn.getPlaces();
		ArrayList<Transition> tList = pn.getTransitions();
		ArrayList<Transition> EnableTrans = new ArrayList<Transition>();
		Place source = (Place) pn.getSource();
		int[] initialMarking = new int[pList.size()];
		// 初始化marking
		int index = pList.indexOf(source);
		initialMarking[index] = 1;
		pn.initialMarking(initialMarking);
		// 计算出当前使能的变迁
		for (int i = 0; i < tList.size(); i++) {
			boolean bool = pn.isTransitionEnable(tList.get(i));
			if (bool) {
				EnableTrans.add(tList.get(i));
			}
		}

		SucceRelations(EnableTrans, pn, initialMarking);
		//计算出所有可能关系的的map，下面进行连接
		Set<Transition> set = map.keySet();
		Iterator<Transition> it = set.iterator();
		while (it.hasNext()) {
			Transition t = it.next();
			if (!t.isInvisibleTask()) {
				LinkedList<Transition> link = (LinkedList<Transition>) map.get(t);
				for (int i = 0; i < link.size(); i++) {
					if (link.get(i).isInvisibleTask()
							&& set.contains(link.get(i))) {
						LinkedList<Transition> temp = (LinkedList<Transition>) map.get(link.get(i));
						for (int j = 0; j < temp.size(); j++) {
							String name = temp.get(j).getIdentifier();
							if (!temp.get(j).isInvisibleTask()
									&& !t.getIdentifier().equals(name)
									&& !link.contains(temp.get(j))) {
								LinkedList<Transition> tb = (LinkedList<Transition>) map.get(t);
								tb.add(temp.get(j));
							}
							if (temp.get(j).isInvisibleTask()
									&& set.contains(temp.get(j))) {
								LinkedList<Transition> tempp = (LinkedList<Transition>) map.get(temp
										.get(j));
								for (int k = 0; k < tempp.size(); k++) {
									if (!tempp.get(k).getIdentifier().equals(
											t.getIdentifier())
											&& !link.contains(tempp.get(k))) {
										LinkedList<Transition> ta = (LinkedList<Transition>) map.get(t);
										ta.add(tempp.get(k));
									}
								}
							}
						}

					}
				}
			}
		}//只考虑连续两个Invisible，下面去掉Invisible作为中间的链接
		HashMap<Transition,LinkedList<Transition>> mapmap =new HashMap<Transition,LinkedList<Transition>>();
		//System.out.println(map);
		Set<Transition> sett = map.keySet();
		Iterator<Transition> itt = sett.iterator();
		while(itt.hasNext()){
			Transition tt=itt.next();
			if(!tt.isInvisibleTask()){	
				mapmap.put(tt, map.get(tt));
//				LinkedList<Transition> ll= map.get(tt);
//				for(int i=0;i<ll.size();i++){
//					if(ll.get(i).getIdentifier().equals("Invisible")){
//						ll.remove(ll.get(i));
//					}
//				}
			}	
//			if(tt.getIdentifier().equals("Invisible")){	
//                itt.next();
//				map.remove(tt);
//			}	
		}
		Set<Transition> settt = mapmap.keySet();
		Iterator<Transition> ittt = settt.iterator();
		while(ittt.hasNext()){
			Transition tt=ittt.next();
			LinkedList<Transition> ll= map.get(tt);
			LinkedList<Transition> temp = new LinkedList<Transition>();
			for(int i=0;i<ll.size();i++){
				if(!ll.get(i).isInvisibleTask()){
					temp.add(ll.get(i));
					
				}
			}
			ll.clear();
			ll.addAll(temp);
		}
		map.clear();
		map.putAll(mapmap);
		
	    //
		Set<Transition> s = mapmap.keySet();
		Iterator<Transition> i = settt.iterator();
		while(i.hasNext()){
			Transition t = i.next();
			String a = t.getIdentifier();
			LinkedList<Transition> link = map.get(t);
			for(int j=0;j<link.size();j++){
				String b = link.get(j).getIdentifier();
				AllRelation.add(a+b);
			}
		}
			
		



	}

	public void SucceRelations(ArrayList<Transition> EnableTrans, PetriNet pn,
			int[] initialMarking) {
		String state = "";
		for (int i = 0; i < initialMarking.length; i++) {
			state += initialMarking[i];

		}
		if (states.contains(state))
			return;
		states.add(state);
		for (int i = 0; i < EnableTrans.size(); i++) {
			pn.initialMarking(initialMarking);
			ArrayList<Place> pList = pn.getPlaces();
			ArrayList<Transition> nextTrans = new ArrayList<Transition>();
			Transition A = EnableTrans.get(i);
			pn.executeTransition(A);
			ArrayList<Transition> tList = pn.getTransitions();

			for (int j = 0; j < tList.size(); j++) {
				boolean bool = pn.isTransitionEnable(tList.get(j));
				if (bool) {
					nextTrans.add(tList.get(j));
				}
			}
			for (int k = 0; k < nextTrans.size(); k++) {
//				AllRelation.add(A.getIdentifier()
//						+ nextTrans.get(k).getIdentifier());
				if (!map.keySet().contains(A)) {
					LinkedList<Transition> link = new LinkedList<Transition>();
					link.add(nextTrans.get(k));
					map.put(A, link);
				} else {
					LinkedList<Transition> linklist = map.get(A);
					//if(!linklist.contains(nextTrans.get(k)))
					linklist.add(nextTrans.get(k));
				}
			}
						
			int[] Marking = new int[pList.size()];
			for (int m = 0; m < Marking.length; m++) {
				Marking[m] = pList.get(m).getNumberOfTokens();
			}
			SucceRelations(nextTrans, pn, Marking);
		}
		//已求出HashMap<Transition,LinkedList<Transition>>，去Invisibl		

	}

	public static void main(String[] args) {
		GetSucceRelation test = new GetSucceRelation();
//		PetriNet pn = PetriNetUtil
//				.getPetriNetFromPnmlFile("C:/QueryModel/Invisible Task/Dup3.pnml");
		PetriNet pn = PetriNetUtil
		.getPetriNetFromPnmlFile("C:/QueryModel/temp/Invisible4.pnml");
		test.GetAllSucceRelation(pn);
		test.display();
//		HashMap<Transition,LinkedList<Transition>> mapp = test.getMap();
//		System.out.println(mapp);

	}

}
