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
package cn.edu.thss.iise.beehivez.server.metric.tar.temp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;
import cn.edu.thss.iise.beehivez.server.metric.tar.temp.ONOrderingRelation;
import cn.edu.thss.iise.beehivez.util.loggenerator.CompleteParameters;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod;
import cn.edu.thss.iise.beehivez.util.loggenerator.NoiseParameters;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 28, 2011
 * 
 */
public class HeuristicETLPM extends LogProduceMethod {

	ONOrderingRelation or = null;
	ONTransitionConcurrentRelation tcr = null;
	ONCompleteFinitePrefix cfp = null;
	
	int size = 0;
	int[][] visited = null;
	int[][] expected = null;
	int[][] executed = null;
	
	Vector<ONEvent> couple = new Vector<ONEvent>();
	Vector<ONEvent> prevTrace = new Vector<ONEvent>();
	Vector<ONEvent> postTrace = new Vector<ONEvent>();
	Vector<ONEvent> trace = new Vector<ONEvent>();
	Vector<Vector<ONEvent>> traces = new Vector<Vector<ONEvent>>();
	HashSet<Vector<ONEvent>> result = new HashSet<Vector<ONEvent>>();
	
	HashSet<ONEvent> finished = new HashSet<ONEvent>();
	HashMap<ONEvent, HashSet<ONEvent>> inTheAir = new HashMap<ONEvent, HashSet<ONEvent>>();

	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness) {
		// TODO Auto-generated method stub

	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness, int multiple) {
		
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn) {
		// TODO Auto-generated method stub
		logIO.clear();
		logIO.open();
		
		traces.clear();
		result.clear();
		finished.clear();
		inTheAir.clear();
		
		MyPetriNet mpn = MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder cfpBuild = new ONCompleteFinitePrefixBuilder(mpn);
		cfp = cfpBuild.Build();
		
		or = new ONOrderingRelation(cfp);
		tcr = or.getTcr();
		size = cfp.getOn().getEveSet().size();
		visited = new int[size][size];
		expected = new int[size][size];
		executed = new int[size][size];

		//calculate the initial matrices
		for(int j = 0; j < size; ++j){
			for(int k = 0; k < size; ++k){
				visited[j][k] = 0;
				expected[j][k] = 0;
				if(or.getOrderRelations()[j][k] == OrderingRelation.PRE 
						&& or.getOrderRelations()[k][j] == OrderingRelation.NONE){
					expected[j][k] = 1;
				}
				else if(or.getOrderRelations()[j][k] == OrderingRelation.CON){
					expected[j][k] = 1;
				}
				else if(or.getOrderRelations()[j][k] == OrderingRelation.PRE 
						&& or.getOrderRelations()[k][j] == OrderingRelation.PRE){
					expected[j][k] = 2;
				}
			}
		}
		
		Vector<ONEvent> stopEvents = new Vector<ONEvent>();
		for(int j = 0; j < size; ++j){
			traces.clear();
			stopEvents.clear();
			ONEvent event = cfp.getOn().getEveSet().get(j);
			int indexOfEvent = cfp.getOn().indexOfEvent(event);
			if(event.isCutOffEvent() || cfp.getTemporalOrder().keySet().contains(event)){
				inTheAir.put(event, event.getSuccessiveCutoffs());
				for(ONEvent e : event.getSuccessiveCutoffs()){
					inTheAir.get(event).add((ONEvent)e.object);
				}
			}
			
			for(int i = 0; i < size; ++i){
				
				for(int k = 0; k < size; ++k){
					for(int t = 0; t < size; ++t){
						executed[k][t] = 0;
					}
				}
				
				couple.clear();
				prevTrace.clear();
				postTrace.clear();
				
				ONEvent postEvent = cfp.getOn().getEveSet().get(i);
				int indexOfpostEvent = cfp.getOn().indexOfEvent(postEvent);
				couple.add(event);
				couple.add(postEvent);
				completeCouple(couple);
				
				if(visited[indexOfEvent][indexOfpostEvent] > 0){
					continue;
				}
				
				if(or.getOrderRelations()[j][i] == OrderingRelation.PRE){
					trace = new Vector<ONEvent>();
					stopEvents.clear();
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					stopEvents.add(event);
					stopEvents.add(postEvent);
					if(event.isCutOffEvent()){
						couple.remove(postEvent);
//						stopEvents.remove(postEvent);
					}
					combinedCfp.getCombinedCfp(couple);
					HashSet<ONCondition> currentConditions = combinedCfp.setInitialConditions();
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					trace.add(postEvent);
					++executed[indexOfEvent][indexOfpostEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
					if(postEvent.isCutOffEvent()){
						ONEvent correspondingEvent = (ONEvent)postEvent.object;
						for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
							for(ONCondition con2 : cfp.getOn().getConsOUTOFEve(postEvent.getId())){
								if(con1.getPlace() == con2.getPlace()){
									currentConditions.add(con1);
								}
							}
						}
					}
					else{
						currentConditions.addAll(cfp.getOn().getConsOUTOFEve(postEvent.getId()));
					}
					
//					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					if(expected[j][i] == 2){
						trace.add(event);
						++executed[indexOfpostEvent][indexOfEvent];
						trace.add(postEvent);
						++executed[indexOfEvent][indexOfpostEvent];
					}
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
					setVisited(cfp);
//					trace.addAll(prevTrace);
//					ONConfiguration configuration = new ONConfiguration(cfp);
//					
//					Vector<ONEvent> newCouple = new Vector<ONEvent>();
//					couple.remove(event);
//					couple.remove(postEvent);
//					for(ONEvent e : couple){
//						if(e.isCutOffEvent()){
//							newCouple.add((ONEvent)e.object);
//						}
//						else{
//							newCouple.add(e);
//						}
//					}
//					couple = newCouple;
					
//					if(event.isCutOffEvent()){
//						//get configuration of event and postEvent
//						
////						Vector<ONEvent> newCouple = new Vector<ONEvent>();
////						newCouple.add((ONEvent)event.object);
//						
//						if(prevTrace.contains(postEvent)){
////							couple.remove(postEvent);
//							trace.add(event);
//							++executed[indexOfpostEvent][indexOfEvent];
//							if(expected[j][i] == 2){
//								trace.add(postEvent);
//								trace.add(event);
//								++executed[indexOfpostEvent][indexOfEvent];
//								++executed[indexOfEvent][indexOfpostEvent];
//							}
//						}
//						else{
////							newCouple.add(postEvent);
//							trace.add(event);
//							trace.add(postEvent);
//							++executed[indexOfEvent][indexOfpostEvent];
//							if(expected[j][i] == 2){
//								trace.add(event);
//								trace.add(postEvent);
//								++executed[indexOfEvent][indexOfpostEvent];
//								++executed[indexOfpostEvent][indexOfEvent];
//							}
//						}
////						configuration.getConfiguration(newCouple);
//					}
//					else{
//						trace.add(postEvent);
//						++executed[indexOfEvent][indexOfpostEvent];
//					}
//					
////					configuration.getConfiguration(couple);
////					currentConditions = new HashSet<ONCondition>(configuration.getConditions());
//					if(postEvent.isCutOffEvent()){
//						currentConditions.removeAll(cfp.getOn().getConsOUTOFEve(postEvent.getId()));
//						currentConditions.addAll(((ONEvent)postEvent.object).getLocalConfigurationConditions());
//					}
					
//					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
//					trace.addAll(postTrace);
					
				}
				if(or.getOrderRelations()[j][i] == OrderingRelation.CON){
					trace = new Vector<ONEvent>();
					stopEvents.clear();
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					HashSet<ONCondition> currentConditions = combinedCfp.setInitialConditions();
					stopEvents.add(event);
					stopEvents.add(postEvent);
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					trace.add(postEvent);
					++executed[indexOfEvent][indexOfpostEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
					if(postEvent.isCutOffEvent()){
						ONEvent correspondingEvent = (ONEvent)postEvent.object;
						for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
							for(ONCondition con2 : cfp.getOn().getConsOUTOFEve(postEvent.getId())){
								if(con1.getPlace() == con2.getPlace()){
									currentConditions.add(con1);
								}
							}
						}
					}
					else{
						currentConditions.addAll(cfp.getOn().getConsOUTOFEve(postEvent.getId()));
					}
//					stopEvents.add(postEvent);
//					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
					setVisited(cfp);
					
					trace = new Vector<ONEvent>();
					stopEvents.clear();
					currentConditions = combinedCfp.setInitialConditions();
					stopEvents.add(postEvent);
					stopEvents.add(event);
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					
					trace.add(event);
					++executed[indexOfpostEvent][indexOfEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(event.getId()));
					if(event.isCutOffEvent()){
						ONEvent correspondingEvent = (ONEvent)event.object;
						for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
							for(ONCondition con2 : cfp.getOn().getConsOUTOFEve(event.getId())){
								if(con1.getPlace() == con2.getPlace()){
									currentConditions.add(con1);
								}
							}
						}
					}
					else{
						currentConditions.addAll(cfp.getOn().getConsOUTOFEve(event.getId()));
					}
//					stopEvents.add(event);
//					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
					setVisited(cfp);
//					prevTrace.removeAll(couple);
//					prevTrace.remove(event);
//					prevTrace.remove(postEvent);
					
					
//					ONConfiguration configuration = new ONConfiguration(cfp);
//					if(!event.isCutOffEvent() && !postEvent.isCutOffEvent()){
//						configuration.getConfiguration(couple);
//						currentConditions = configuration.getConditions();
//					}
//					if(!event.isCutOffEvent() && postEvent.isCutOffEvent()){
//						couple.remove(postEvent);
//						couple.add((ONEvent)postEvent.object);
//						configuration.getConfiguration(couple);
//						currentConditions = configuration.getConditions();
//					}
//					if(event.isCutOffEvent() && !postEvent.isCutOffEvent()){
//						couple.remove(event);
//						couple.add((ONEvent)event.object);
//						configuration.getConfiguration(couple);
//						currentConditions = configuration.getConditions();
//					}
//					if(event.isCutOffEvent() && postEvent.isCutOffEvent()){
//						couple.remove(event);
//						couple.remove(postEvent);
//						couple.add((ONEvent)event.object);
//						couple.add((ONEvent)postEvent.object);
//						configuration.getConfiguration(couple);
//						currentConditions = configuration.getConditions();
//					}
//					Vector<ONEvent> newCouple = new Vector<ONEvent>();
//					couple.removeAll(stopEvents);
//					for(ONEvent e : couple){
//						if(e.isCutOffEvent()){
//							newCouple.add((ONEvent)e.object);
//						}
//						else{
//							newCouple.add(e);
//						}
//					}
//					couple = newCouple;
//					
//					configuration.getConfiguration(couple);
//					currentConditions = configuration.getConditions();
//					
//					HashSet<ONCondition> copy = new HashSet<ONCondition>(currentConditions);
//					trace = new Vector<ONEvent>();
//					trace.addAll(prevTrace);
//					trace.add(event);
//					currentConditions.removeAll(cfp.getOn().getConsINTOEve(event.getId()));
//					if(event.isCutOffEvent()){
//						ONEvent correspondingEvent = (ONEvent)event.object;
//						for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
//							for(ONCondition con2 : cfp.getOn().getConsOUTOFEve(event.getId())){
//								if(con1.getPlace() == con2.getPlace()){
//									currentConditions.add(con1);
//								}
//							}
//						}
//					}
//					else{
//						currentConditions.addAll(cfp.getOn().getConsOUTOFEve(event.getId()));
//					}
//					trace.add(postEvent);
//					currentConditions.removeAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
//					if(postEvent.isCutOffEvent()){
//						ONEvent correspondingEvent = (ONEvent)postEvent.object;
//						for(ONCondition con1 : correspondingEvent.getLocalConfigurationConditions()){
//							for(ONCondition con2 : cfp.getOn().getConsOUTOFEve(postEvent.getId())){
//								if(con1.getPlace() == con2.getPlace()){
//									currentConditions.add(con1);
//								}
//							}
//						}
//					}
//					else{
//						currentConditions.addAll(cfp.getOn().getConsOUTOFEve(postEvent.getId()));
//					}
//					++executed[indexOfEvent][indexOfpostEvent];
//					cfp.implement(trace, stopEvents, copy, visited, executed, expected, cfp);
//					trace.addAll(postTrace);
//					traces.add(trace);
//					setVisited(cfp);
					
//					copy = new HashSet<ONCondition>(currentConditions);
//					trace = new Vector<ONEvent>();
//					trace.addAll(prevTrace);
//					trace.add(postEvent);
//					trace.add(event);
//					++executed[indexOfpostEvent][indexOfEvent];
//					cfp.implement(trace, stopEvents, copy, visited, executed, expected, cfp);
////					trace.addAll(postTrace);
//					traces.add(trace);
//					++visited[i][j];
				}
			}
			result.addAll(traces);
		}
		
//		for(ONEvent corresponding : cfp.getTemporalOrder().keySet()){
//			
//		}
		
		while(!inTheAir.isEmpty()){
			stopEvents.clear();
			
			for(ONEvent cutoff : inTheAir.keySet()){
				if(inTheAir.get(cutoff).isEmpty()){
					//
					for(int k = 0; k < size; ++k){
						for(int t = 0; t < size; ++t){
							executed[k][t] = 0;
						}
					}
					
					Vector<ONEvent> subTrace = new Vector<ONEvent>();
					cutoff.getSubCfp().implement(subTrace, cutoff.getSubCfp().setInitialConditions(), executed, expected, cfp);
					ONEvent correspondingEvent = null;
					if(cutoff.object != null){
						correspondingEvent = (ONEvent)cutoff.object;
					}
					else{
						correspondingEvent = cutoff;
					}
					HashSet<Vector<ONEvent>> temp = new HashSet<Vector<ONEvent>>();
					HashSet<ONEvent> otherCutoffs = new HashSet<ONEvent>();
					otherCutoffs.addAll(cfp.getTemporalOrder().get(correspondingEvent).keySet());
					otherCutoffs.remove(cutoff);
					for(Vector<ONEvent> t : result){
						for(ONEvent e: otherCutoffs){
							if(t.contains(e)){//或者correspondingEvent的其他切点
								trace = new Vector<ONEvent>();
								trace.addAll(subTrace);
								int index = t.indexOf(e);
								//find the suitable trace that can be executed following the cutoff
								if(index == t.size() - 1){
									continue;
								}
								Vector<ONEvent> piece = new Vector<ONEvent>();
								for(int k = 0; k <= index; ++k){
									piece.add(t.get(k));
								}
								ONConfiguration configuration = new ONConfiguration(cfp);
								configuration.getConfiguration(piece);
								
								if(e.getLocalConfigurationConditions().containsAll(configuration.getConditions()) &&
										configuration.getConditions().containsAll(e.getLocalConfigurationConditions())){
									for(int k = index + 1; k < t.size(); ++k){
										trace.add(t.get(k));
									}
									temp.add(trace);
								}
							}
						}
						if(t.contains(correspondingEvent)){//或者correspondingEvent的其他切点
							trace = new Vector<ONEvent>();
							trace.addAll(subTrace);
							int index = t.indexOf(correspondingEvent);
							//find the suitable trace that can be executed following the cutoff
							if(index == t.size() - 1){
								continue;
							}
							Vector<ONEvent> piece = new Vector<ONEvent>();
							for(int k = 0; k <= index; ++k){
								piece.add(t.get(k));
							}
							ONConfiguration configuration = new ONConfiguration(cfp);
							configuration.getConfiguration(piece);
							
							if(correspondingEvent.getLocalConfigurationConditions().containsAll(configuration.getConditions()) &&
									configuration.getConditions().containsAll(correspondingEvent.getLocalConfigurationConditions())){
								for(int k = index + 1; k < t.size(); ++k){
									trace.add(t.get(k));
								}
								temp.add(trace);
							}
						}
						
					}
					result.addAll(temp);
					//
					
					finished.add(cutoff);
				}
			}
			
			for(ONEvent cutoff : finished){
				inTheAir.remove(cutoff);
			}
			for(ONEvent cutoff : finished){
				for(ONEvent cutoff2 : inTheAir.keySet()){
					inTheAir.get(cutoff2).remove(cutoff);
				}
			}
			//finished.clear();
		}
		
		int caseid = 0;
		for(Vector<ONEvent> r : result){
			for(ONEvent e : r){
				logIO.addEventLog("" + caseid, e.getTrans(), logIO.EVENT_COMPLETE, "", "");
			}
			++caseid;
		}
		logIO.store(fileDir);
	}
	
	public void setVisited(ONCompleteFinitePrefix cfp){
		for(int i = 0; i < trace.size() - 1; ++i){
			ONEvent prev = trace.get(i);
			ONEvent post = trace.get(i + 1);
			int index1 = cfp.getOn().indexOfEvent(prev);
			int index2 = cfp.getOn().indexOfEvent(post);
			++visited[index1][index2];
		}
	}
	
	public void completeCouple(Vector<ONEvent> couple){
		int index1 = cfp.getOn().indexOfEvent(couple.get(0));
		int index2 = cfp.getOn().indexOfEvent(couple.get(1));
		for(ONEvent event : cfp.getOn().getEveSet()){
			int index = cfp.getOn().indexOfEvent(event);
			if(tcr.transitionConcurrentRelation[index][index1] && 
					tcr.transitionConcurrentRelation[index][index2]){
				couple.add(event);
			}
		}
	}

	@Override
	public String getLogType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\completeLog\\3branches.xml");
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
		HeuristicETLPM eTar = new HeuristicETLPM();
		eTar.generateLog("C:\\Users\\lenovo\\Documents\\completeLog\\3branches_h.log", pn);
	
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {
		// TODO Auto-generated method stub
		
	}

}
