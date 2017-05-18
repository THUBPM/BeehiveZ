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

package cn.edu.thss.iise.beehivez.server.metric;

import java.util.Iterator;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriObject;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

public class PetriNetReduction {
	
	public PetriNetReduction() {
	}

	/**
	 * Apply the liveness and boundedness preserving reduction rules on the given Petri net as long as reductions can be applied.
	 * @param providedPN The given Petri net.
	 * @return If any reduction could be applied, a fully reduced Petri net.
	 *         Otherwise, the given Petri net.
	 */
	public MyPetriNet reduce(MyPetriNet providedPN,int rule) {
		MyPetriNet reducedPN = reduceOnce(providedPN,rule);
		/*while (reducedPN!=providedPN) { // Some reductions could be applied, try again.
			providedPN = reducedPN;
			reducedPN = reduceOnce(providedPN);
		}*/
		// No reductions could be applied any more. Return result.
		// Note that if no reductions could be applied at all, then reducedPN equals providedPN.
		return reducedPN;
	}

	/**
	 * Apply the liveness and boundedness preserving reduction rules on the given Petri net in one pass.
	 * @param providedPN The given Petri net.
	 * @return If any reduction could be applied, a reduced Petri net.
	 *         Otherwise, the given Petri net.
	 */
	public MyPetriNet reduceOnce(MyPetriNet providedPN,int rule) {
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// Check Murata rules one by one.
		boolean nothingReduced = true;
		switch(rule){
		case 0:
			boolean somethingReduced = true;
			while (somethingReduced)
			{
				somethingReduced  = false;
				int nOld = providedPN.getTransitionSet().size()+providedPN.getPlaceSet().size();
				providedPN = excuteRule1(providedPN);
				providedPN = excuteRule2(providedPN);
				providedPN = excuteRule3(providedPN);
				providedPN = excuteRule4(providedPN);
				providedPN = excuteRule5(providedPN);
				providedPN = excuteRule6(providedPN);
				int nNew = providedPN.getTransitionSet().size()+providedPN.getPlaceSet().size();
				if(nNew != nOld)
					somethingReduced  = true;
			}
			break;
		case 1:
			providedPN = excuteRule1(providedPN);
			break;
		case 2:
			providedPN = excuteRule2(providedPN);
			break;
		case 3:
			providedPN = excuteRule3(providedPN);
			break;
		case 4:
			providedPN = excuteRule4(providedPN);
			break;
		case 5:
			providedPN = excuteRule5(providedPN);
			break;
		case 6:
			providedPN = excuteRule6(providedPN);
			break;
		}
		return providedPN;
	}
	
	private MyPetriNet excuteRule6(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
//		System.out.println("-----");
		// Rule 6: Elimination of Self-loop Transitions
		Vector<MyPetriTransition> transitions = providedPN.getTransitionSet();
		for(int i=0;i<transitions.size();i++){
			MyPetriTransition transition = transitions.get(i);
			if (notReducedTransitions.contains(transition) && // transition not reduced
					transition.getSuccessors().size() == 1 && // transition has only one output place
					transition.getPredecessors().size() == 1) { // transition has only one input place
				if (transition.getSuccessors().iterator().next() ==
						transition.getPredecessors().iterator().next()) { // input place equals output place
					// Reduce transition onto itself
					System.out.println("运用规则6********");
					notReducedTransitions.remove(transition);
					providedPN.deleteTransition(transition);
				}
			}
		}
		providedPN = getNewPN(providedPN);
		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}

	private MyPetriNet excuteRule5(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		// Rule 5: Elimination of Self-loop Places
		Vector<MyPetriPlace> places = providedPN.getPlaceSet();
		for(int i=0;i<places.size();i++){
			MyPetriPlace place = places.get(i);
			if (notReducedPlaces.contains(place) && // place not reduced
					place.getSuccessors().size() == 1 && // place has only one output transition
					place.getPredecessors().size() == 1 
					) { // place contains tokens
				if (place.getSuccessors().iterator().next() ==
						place.getPredecessors().iterator().next()) { // input transition equals output transition
					// Reduce place onto itself
					System.out.println("运用规则5**********");
					notReducedPlaces.remove(place);
					providedPN.deletePlace(place);
				}
			}
		}
		providedPN = getNewPN(providedPN);
		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}

	private MyPetriNet excuteRule4(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		// Rule 4: Fusion of Parallel Transitions
		Vector<MyPetriTransition> transitions = providedPN.getTransitionSet();
		for(int i=0;i<transitions.size();i++){
			MyPetriTransition transition = transitions.get(i);
			if (notReducedTransitions.contains(transition) && // transition not reduced
					transition.getSuccessors().size() >= 1 && // transition has at least one output place
					transition.getPredecessors().size() >= 1) { // transition has at least one input place
				
				MyPetriPlace place = (MyPetriPlace) transition.getPredecessors().iterator().next(); // Get input place
//				MyPetriPlace otherPlace = (MyPetriPlace) transition.getSuccessors().iterator().next(); // Get output place
				
				Vector<MyPetriTransition> successors = new Vector<MyPetriTransition>(place.getSuccessors());
				for(MyPetriTransition otherTransition:successors) 
				{
					//MyPetriTransition otherTransition = (MyPetriTransition) it2.next(); // For every output transition of the input place
					if (transition != otherTransition && // other transition is not transition
							otherTransition.getSuccessors().equals(transition.getSuccessors()) && // other transition also has only one output place
							otherTransition.getPredecessors().equals(transition.getPredecessors())) { // other transition also has only one input place
						System.out.println("运用规则4***********");
						// Reduce other transition to itself
						notReducedTransitions.remove(otherTransition);
						providedPN.deleteTransition(otherTransition);
					}
				}	
			}
		}
		providedPN = getNewPN(providedPN);
		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}

	private MyPetriNet excuteRule3(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		// Rule 3: Fusion of Parallel Places
		Vector<MyPetriPlace> places = notReducedPlaces;
		for(int i=0;i<places.size();i++){
			MyPetriPlace place = places.get(i);
			if (notReducedPlaces.contains(place) && // place not reduced
					place.getSuccessors().size() >= 1 && // place has at least one output transition
					place.getPredecessors().size() >= 1) { // place has at least one input transition
				
				MyPetriTransition transition = (MyPetriTransition) place.getPredecessors().iterator().next(); // Get input transition
				Vector<MyPetriPlace> successors = new Vector<MyPetriPlace>(transition.getSuccessors());
				for(MyPetriPlace otherPlace:successors) 
				{
					if (place != otherPlace && // other place is not place
							otherPlace.getSuccessors().equals(place.getSuccessors()) && // other place has same output transitions
							otherPlace.getPredecessors().equals(place.getPredecessors())) { // other place has same input transitions
						System.out.println("运用规则3*********");
						// Reduce other place to itself
						notReducedPlaces.remove(otherPlace);
						providedPN.deletePlace(otherPlace);
					}
				}
				
			}
		}
		providedPN = getNewPN(providedPN);
		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}

	private MyPetriNet excuteRule2(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
//		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
//		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
//		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
//		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		// Rule 2: Fusion of Series Transitions
		Vector<MyPetriTransition> transitions = providedPN.getTransitionSet();
		for(int i=0;i<transitions.size();i++){
			MyPetriTransition transition = transitions.get(i);
			if (notReducedTransitions.contains(transition) && // transition not reduced
					transition.getPredecessors().size() == 1) { // transition has only one input place
				MyPetriPlace place = (MyPetriPlace) transition.getPredecessors().iterator().next(); // Get the input place
				if (notReducedPlaces.contains(place) && // output place not reduced
						place.getPredecessors().size() == 1 && // input place has only one input transition
						place.getSuccessors().size() == 1 ) { // input place has only one output transition
					
						System.out.println("运用规则2**********");
						MyPetriTransition otherTransition = (MyPetriTransition) place.getPredecessors().iterator().
													 next(); // Get input transition
						// Reduce output transition to input transition
						notReducedTransitions.remove(transition);
						System.out.println(" 变迁： "+transition+"被移除");
						providedPN.deleteTransition(transition);
						notReducedPlaces.remove(place);
						System.out.println(" 库所： "+place+"被移除");
						providedPN.deletePlace(place);
						Iterator iterator = transition.getSuccessors().iterator();
						System.out.println("变迁: "+transition+"有"+transition.getSuccessors().size()+"个后序");
						while(iterator.hasNext()){
							MyPetriPlace temp = (MyPetriPlace) iterator.next();
							if(isExistPlace(notReducedPlaces,temp)&&isExistTransition(notReducedTransitions,otherTransition)){
								providedPN.addArc(otherTransition, temp);
							}		
							// otherTransition.addSuccessor(temp);
							System.out.println("变迁:"+otherTransition+"增加后序"+temp);
						}
				}
			} 
		}
		providedPN = getNewPN(providedPN);
//		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}

	private MyPetriNet excuteRule1(MyPetriNet providedPN) {
		// TODO Auto-generated method stub
		Vector<MyPetriPlace> notReducedPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> notReducedTransitions = new Vector<MyPetriTransition>();
		Vector<MyPetriArc> notReducedArcs = new Vector<MyPetriArc>();
		Vector<MyPetriObject> notReducedObjects = new Vector<MyPetriObject>();
		Iterator it;
		// 获取当前Petri网的信息
		notReducedPlaces = providedPN.getPlaceSet();
		notReducedTransitions = providedPN.getTransitionSet();
		notReducedArcs = providedPN.getArcSet();
		// 算法运行前边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		// Rule 1: Fusion of Series Places
		Vector<MyPetriPlace> places = notReducedPlaces;
		for(int i=0;i<places.size();i++){
			MyPetriPlace place = places.get(i);
			if(place.getSuccessors()==null){
				continue;
			}else{
			if (notReducedPlaces.contains(place) && // place not reduced
					place.getSuccessors().size() == 1) { // place has only one output transition
				MyPetriTransition transition = (MyPetriTransition) place.getSuccessors().iterator().next(); // Get the output transition
				if (notReducedTransitions.contains(transition) && // output transition is not reduced
						transition.getPredecessors().size() == 1 && // output transition has only one input place
						transition.getSuccessors().size() == 1) { // output transition has only one output place
					System.out.println("运用规则1*********");
					MyPetriPlace otherPlace = (MyPetriPlace) transition.getSuccessors().iterator().next(); // Get output place
					// Reduce input place to output place (all arcs connected to input place will be transferred to output place)
					notReducedPlaces.remove(place);	
					System.out.println("删除了库所："+place.toString());
					providedPN.deletePlace(place);
					notReducedTransitions.remove(transition);
					System.out.println(" 删除了变迁： "+transition.toString());
					providedPN.deleteTransition(transition);
					// 删除与库所和变迁相连的边
					
					Iterator iterator = place.getPredecessors().iterator();
					System.out.println("库所："+place+"拥有"+place.getPredecessors().size()+"个前序");
					while(iterator.hasNext()){
						MyPetriTransition temp = (MyPetriTransition) iterator.next();
						System.out.println("测试: "+ temp.getId()+"  "+ otherPlace.getId());
						if(isExistPlace(notReducedPlaces,otherPlace)&&isExistTransition(notReducedTransitions,temp)){
							providedPN.addArc(temp, otherPlace);
						}
						
						// otherPlace.addPredecessor(temp);
						// temp.addSuccessor(otherPlace);
						// tempPt.add(temp);
						System.out.println("库所："+otherPlace.toString()+"增加了前序："+temp.toString());
					}
					// tempPP.add(place);
				}
			}}
		}
		providedPN = getNewPN(providedPN);
		notReducedArcs = providedPN.getArcSet();
		// 算法运行后边的情况
//		for(int i=0;i<notReducedArcs.size();i++){
//			MyPetriArc arc = notReducedArcs.get(i);
//			System.out.println(arc.getsourceid()+" "+arc.gettargetid());
//		}
		return providedPN;
	}
	
	// 检查在notReducedPlaces是否存在place
	public boolean isExistPlace(Vector<MyPetriPlace> notReducedPlaces, MyPetriPlace place){
		if(notReducedPlaces.contains(place)){
			return true;
		}
		else 
			return false;
	}
	
	// 检查在notReducedTransitions是否存在transition
	public boolean isExistTransition(Vector<MyPetriTransition> notReducedTransitions,MyPetriTransition transition){
		if(notReducedTransitions.contains(transition))
			return true;
		else 
			return false;
	}
	
	// 根据传入的Petri网对象的placevector,transitionvector,arcvector，重新构造新的Petri网
	public MyPetriNet getNewPN(MyPetriNet reducedPN){
		Vector<MyPetriPlace> notReducedPlaces = reducedPN.getPlaceSet();
		Vector<MyPetriTransition> notReducedTransitions = reducedPN.getTransitionSet();
		Vector<MyPetriArc> notReducedArcs = reducedPN.getArcSet();
		Vector<MyPetriPlace> newPlaces = new Vector<MyPetriPlace>();
		Vector<MyPetriTransition> newTransitions = new Vector<MyPetriTransition>();
		// Vector<PetriArc> arcs = new Vector<PetriArc>();
		Iterator it;
		it = notReducedPlaces.iterator();
		while(it.hasNext()){
			MyPetriPlace pp = (MyPetriPlace) it.next();
			String placeid = pp.getId();
			// 返回库所的所有输入变迁
			Vector<MyPetriTransition> transitionIn = reducedPN.getPlaceTransitionIn(placeid);
			// 返回库所的所有输出变迁
			Vector<MyPetriTransition> transitionOut = reducedPN.getPlaceTransitionOut(placeid);
			pp.setPredecessors(transitionIn);
			pp.setSuccessors(transitionOut);
			newPlaces.add(pp);
		}
		it = notReducedTransitions.iterator();
		while(it.hasNext()){
			MyPetriTransition pt = (MyPetriTransition) it.next();
			String transitionid = pt.getId();
			// 返回变迁的所有输入库所
			Vector<MyPetriPlace> placeIn = reducedPN.getAllTranstionPlaceIn(transitionid);
			// 返回变迁的所有输出库所
			Vector<MyPetriPlace> placeOut = reducedPN.getAllTranstionPlaceOut(transitionid);
			pt.setPredecessors(placeIn);
			pt.setSuccessors(placeOut);
			// System.out.println(pt.toString()+"所有输入库所："+placeIn.toString()+"所有输出库所："+placeOut.toString());
			newTransitions.add(pt);
		}
		
		// 重新设置Petri网的库所和变迁，以及Petri网的对象
		reducedPN.setPlaceSet(newPlaces);
		reducedPN.setTransitionSet(newTransitions);
		reducedPN.setArcSet(notReducedArcs);
		Vector<MyPetriObject> po = new Vector<MyPetriObject>();
		po.addAll(newPlaces);
		po.addAll(newTransitions);
		po.addAll(notReducedArcs);
		// System.out.println("Petri对象: "+po.toString());
		reducedPN.setPetri(po);
		return reducedPN;
	}
}

