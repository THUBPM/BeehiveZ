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
package cn.edu.thss.iise.beehivez.server.metric.tar.loggenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

//import org.apache.derby.tools.sysinfo;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;
import cn.edu.thss.iise.beehivez.server.metric.tar.AdjacentRelation;
import cn.edu.thss.iise.beehivez.server.metric.tar.ConcurrentRelation;
import cn.edu.thss.iise.beehivez.server.metric.tar.TransitiveClosure;
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

	AdjacentRelation ar = null;
	ConcurrentRelation cr = null;
	TransitiveClosure tc = null;
	ONCompleteFinitePrefix cfp = null;
	
	int size = 0;
	//the matrix ''visited'' stores the times visited in all traces
	int[][] visited = null;
	//executed stores the times visited in one trace
	int[][] executed = null;
	//expected stores the times expected to visit. 
	int[][] expected = null;
	
	
	Vector<ONEvent> couple = new Vector<ONEvent>();
	Vector<ONEvent> stopEvents = new Vector<ONEvent>();
	Vector<ONEvent> trace = new Vector<ONEvent>();
	Vector<ONEvent> traceplus = new Vector<ONEvent>();
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
		
		ar = new AdjacentRelation(cfp);
		cr = new ConcurrentRelation(cfp);
//		tc = new TransitiveClosure(ar);
		size = cfp.getOn().getEveSet().size();
		visited = new int[size][size];
		expected = new int[size][size];
		executed = new int[size][size];

		//calculate the initial matrices
		for(int j = 0; j < size; ++j){
			for(int k = 0; k < size; ++k){
				visited[j][k] = 0;
				expected[j][k] = 0;
				if(ar._relation[j][k] && !ar._relation[k][j]){
					expected[j][k] = 1;
				}
				else if(cr._relation[j][k]){
					expected[j][k] = 1;
				}
				else if(ar._relation[j][k] && ar._relation[k][j]){
					expected[j][k] = 2;
				}
			}
		}
		
		
		for(int j = 0; j < size; ++j){
			traces.clear();
			
			ONEvent event = cfp.getOn().getEveSet().get(j);
			int indexOfEvent = j;
			
			if((event.isCutOffEvent() && !cfp.getTemporalOrder().get(event.object).get(event).equals("loop"))){
				if(!inTheAir.containsKey(event)){
					inTheAir.put(event, new HashSet<ONEvent>());
				}
				for(ONEvent e : event.getSuccessiveCutoffs()){
					if(!cfp.getTemporalOrder().get(e.object).get(e).equals("loop")){
						inTheAir.get(event).add(e);
					}
				}
				if(event.getSuccessiveCutoffs().isEmpty()){
					finished.add(event);
				}
			}
//			if(event.isCutOffEvent() && !cfp.getTemporalOrder().get(event.object).get(event).equals("loop")){
//				if(event.getSuccessiveCutoffs().isEmpty()){
//					finished.add(event);
//				}
//				inTheAir.put(event, event.getSuccessiveCutoffs());
//				else{
//					inTheAir.put(event, event.getSuccessiveCutoffs());
//					for(ONEvent e : event.getSuccessiveCutoffs()){
//						if(e.getSuccessiveCutoffs().contains(event)){
//							inTheAir.get(event).remove(e);
//						}
//					}
//					if(inTheAir.get(event).isEmpty()){
//						finished.add(event);
//					}
//				}
//			}
			
			for(int i = 0; i < size; ++i){
				for(int k = 0; k < size; ++k){
					for(int t = 0; t < size; ++t){
						executed[k][t] = 0;
					}
				}
				
				ONEvent postEvent = cfp.getOn().getEveSet().get(i);
				int indexOfpostEvent = i;
				
				stopEvents.clear();
				couple.clear();
				couple.add(event);
				couple.add(postEvent);
				completeCouple(couple);
				 
				if(visited[indexOfEvent][indexOfpostEvent] > 0){
					continue;
				}
				
				if(ar._relation[j][i]){
					trace = new Vector<ONEvent>();
					stopEvents.add(event);
					stopEvents.add(postEvent);
					if(event.isCutOffEvent()){
						couple.remove(postEvent);
					}
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					HashSet<ONCondition> currentConditions = combinedCfp.setInitialConditions();
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					trace.add(postEvent);
					++executed[indexOfEvent][indexOfpostEvent];
					++visited[indexOfEvent][indexOfpostEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
					currentConditions.addAll(postEvent.getCorrespondingSuccessiveConditions());
					
					int index1 = 0;
					int index2 = 0;
					if(expected[j][i] == 2){
						trace.add(event);
						index1 = trace.size() - 1;
						++executed[indexOfpostEvent][indexOfEvent];
						++visited[indexOfpostEvent][indexOfEvent];
						
						trace.add(postEvent);
						index2 = trace.size() - 1;
						++executed[indexOfEvent][indexOfpostEvent];
						++visited[indexOfEvent][indexOfpostEvent];
					}
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
					
					if(expected[j][i] == 2){
						traceplus = new Vector<ONEvent>();
						for(int k = 0; k < trace.size(); ++k){
							if(k != index1 && k != index2){
								traceplus.add(trace.get(k));
							}
						}
						traces.add(traceplus);
					}
//					setVisited(cfp);
				}
				if(cr._relation[j][i]){
					trace = new Vector<ONEvent>();
					stopEvents.add(event);
					stopEvents.add(postEvent);
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					HashSet<ONCondition> currentConditions = combinedCfp.setInitialConditions();
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					trace.add(postEvent);
					++executed[indexOfEvent][indexOfpostEvent];
					++visited[indexOfEvent][indexOfpostEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(postEvent.getId()));
					currentConditions.addAll(postEvent.getCorrespondingSuccessiveConditions());
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
//					setVisited(cfp);
					
					trace = new Vector<ONEvent>();
					stopEvents.clear();
					stopEvents.add(postEvent);
					stopEvents.add(event);
					currentConditions = combinedCfp.setInitialConditions();
					combinedCfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					trace.add(event);
					++executed[indexOfpostEvent][indexOfEvent];
					++visited[indexOfpostEvent][indexOfEvent];
					currentConditions.removeAll(cfp.getOn().getConsINTOEve(event.getId()));
					currentConditions.addAll(event.getCorrespondingSuccessiveConditions());
					stopEvents.clear();
					cfp.implement(trace, stopEvents, currentConditions, visited, executed, expected, cfp);
					traces.add(trace);
//					setVisited(cfp);
				}
			}
			result.addAll(traces);
		}
		
		HashMap<ONEvent, HashSet<ONEvent>> tempAir = new HashMap<ONEvent, HashSet<ONEvent>>(inTheAir);
		for(ONEvent cutoff : inTheAir.keySet()){
			ONEvent corresponding = (ONEvent)cutoff.object;
			if(!tempAir.containsKey(corresponding)){
				tempAir.put(corresponding, new HashSet<ONEvent>());
			}
			tempAir.get(corresponding).add(cutoff);
//			|| cfp.getTemporalOrder().keySet().contains(event)
		}
		inTheAir = tempAir;
		
		while(!inTheAir.isEmpty()){
			stopEvents.clear();
//			
//			for(ONEvent cutoff : finished){
//				inTheAir.remove(cutoff);
//			}
//			for(ONEvent cutoff : finished){
//				for(ONEvent cutoff2 : inTheAir.keySet()){
//					inTheAir.get(cutoff2).remove(cutoff);
//				}
//			}
			
			for(ONEvent cutoff : inTheAir.keySet()){
				if(inTheAir.get(cutoff) != null){
					//
					for(int k = 0; k < size; ++k){
						for(int t = 0; t < size; ++t){
							executed[k][t] = 0;
						}
					}
					
					couple.clear();
					couple.add(cutoff);
					completeCouple(cutoff);
					stopEvents.clear();
					stopEvents.add(cutoff);
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					HashSet<ONCondition> currentConditions = combinedCfp.setInitialConditions();
					Vector<ONEvent> subTrace = new Vector<ONEvent>();
					combinedCfp.implement(subTrace, stopEvents, currentConditions, visited, executed, expected, cfp);
					
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
									temp.add(trace);
									continue;
								}
								Vector<ONEvent> piece = new Vector<ONEvent>();
								for(int k = 0; k <= index; ++k){
									piece.add(t.get(k));
								}
								ONConfiguration configuration = new ONConfiguration(cfp);
								configuration.getConfiguration(piece);
								
								if(e.getCompleteLocalConfigurationConditions().containsAll(configuration.getCompleteConditions()) &&
										configuration.getCompleteConditions().containsAll(e.getCompleteLocalConfigurationConditions())){
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
								temp.add(trace);
								continue;
							}
							Vector<ONEvent> piece = new Vector<ONEvent>();
							for(int k = 0; k <= index; ++k){
								piece.add(t.get(k));
							}
							ONConfiguration configuration = new ONConfiguration(cfp);
							configuration.getConfiguration(piece);
							
							if(correspondingEvent.getCompleteLocalConfigurationConditions().containsAll(configuration.getCompleteConditions()) &&
									configuration.getCompleteConditions().containsAll(correspondingEvent.getCompleteLocalConfigurationConditions())){
								for(int k = index + 1; k < t.size(); ++k){
									trace.add(t.get(k));
								}
								temp.add(trace);
							}
						}
						
					}
					result.addAll(temp);
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
			if(cr._relation[index][index1] && cr._relation[index][index2]){
				couple.add(event);
			}
		}
	}
	
	public void completeCouple(ONEvent e){
		int index1 = cfp.getOn().indexOfEvent(e);
		for(ONEvent event : cfp.getOn().getEveSet()){
			int index = cfp.getOn().indexOfEvent(event);
			if(cr._relation[index][index1]){
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
		long start = System.nanoTime();
		HeuristicETLPM eTar = new HeuristicETLPM();
		long end = System.nanoTime();
		System.out.println(end - start);
		eTar.generateLog("C:\\Users\\lenovo\\Documents\\completeLog\\log\\3branches.log", pn);
	
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {
		// TODO Auto-generated method stub
		
	}

}
