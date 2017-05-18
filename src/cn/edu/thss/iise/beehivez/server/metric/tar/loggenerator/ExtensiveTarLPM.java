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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.xml.bind.util.ValidationEventCollector;

import org.processmining.analysis.socialsuccess.bigfive.agreeableness.OnGoodTerms;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.semanticweb.kaon2.pi;

import sun.net.www.http.PosterOutputStream;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;
import cn.edu.thss.iise.beehivez.server.metric.tar.AdjacentRelation;
import cn.edu.thss.iise.beehivez.server.metric.tar.ConcurrentRelation;
import cn.edu.thss.iise.beehivez.util.loggenerator.CompleteParameters;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod;
import cn.edu.thss.iise.beehivez.util.loggenerator.NoiseParameters;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 28, 2011
 * 
 */
public class ExtensiveTarLPM extends LogProduceMethod {

	AdjacentRelation ar = null;
	ConcurrentRelation cr = null;
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

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod#generateLog(java.lang.String, int, org.processmining.framework.models.petrinet.PetriNet, double)
	 */
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness) {
		// TODO Auto-generated method stub

	}
  
   @Override
   public void generateLog(String fileDir, int caseCount, PetriNet pn,
		double completeness, int multiple) {
	
   }

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod#generateLog(java.lang.String, int, org.processmining.framework.models.petrinet.PetriNet)
	 */
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
			int indexOfEvent = cfp.getOn().indexOfEvent(event);
			if(event.isCutOffEvent()){
				inTheAir.put(event, event.getSuccessiveCutoffs());
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
				
				if(ar._relation[j][i]){
					HashSet<ONCondition> currentConditions = null;
					trace = new Vector<ONEvent>();
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					combinedCfp.implement(prevTrace, combinedCfp.setInitialConditions(), executed, expected, cfp);
					trace.addAll(prevTrace);
					ONConfiguration configuration = new ONConfiguration(cfp);
					
					if(event.isCutOffEvent()){
						//get configuration of event and postEvent
						
						Vector<ONEvent> newCouple = new Vector<ONEvent>();
						newCouple.add((ONEvent)event.object);
						
						if(prevTrace.contains(postEvent)){
							trace.add(event);
							++executed[indexOfpostEvent][indexOfEvent];
							if(expected[j][i] == 2){
								trace.add(postEvent);
								trace.add(event);
								++executed[indexOfpostEvent][indexOfEvent];
								++executed[indexOfEvent][indexOfpostEvent];
							}
						}
						else{
							newCouple.add(postEvent);
							trace.add(event);
							trace.add(postEvent);
							++executed[indexOfEvent][indexOfpostEvent];
							if(expected[j][i] == 2){
								trace.add(event);
								trace.add(postEvent);
								++executed[indexOfEvent][indexOfpostEvent];
								++executed[indexOfpostEvent][indexOfEvent];
							}
						}
						configuration.getConfiguration(newCouple);
						
					}
					else{
						trace.add(postEvent);
						++executed[indexOfEvent][indexOfpostEvent];
						configuration.getConfiguration(couple);
					}
					
					currentConditions = new HashSet<ONCondition>(configuration.getConditions());
					if(postEvent.isCutOffEvent()){
						currentConditions.removeAll(cfp.getOn().getConsOUTOFEve(postEvent.getId()));
						currentConditions.addAll(((ONEvent)postEvent.object).getLocalConfigurationConditions());
					}
					
					cfp.implement(trace, currentConditions, executed, expected, cfp);
//					trace.addAll(postTrace);
					traces.add(trace);
					setVisited(cfp);
				}
				if(cr._relation[j][i]){
					ONCompleteFinitePrefix combinedCfp = new ONCompleteFinitePrefix(mpn);
					combinedCfp.getCombinedCfp(couple);
					combinedCfp.implement(prevTrace, combinedCfp.setInitialConditions(), executed, expected, cfp);
					prevTrace.removeAll(couple);
					
					Vector<ONCondition> currentConditions = null;
					ONConfiguration configuration = new ONConfiguration(cfp);
					if(!event.isCutOffEvent() && !postEvent.isCutOffEvent()){
						configuration.getConfiguration(couple);
						currentConditions = configuration.getConditions();
					}
					if(!event.isCutOffEvent() && postEvent.isCutOffEvent()){
						couple.remove(postEvent);
						couple.add((ONEvent)postEvent.object);
						configuration.getConfiguration(couple);
						currentConditions = configuration.getConditions();
					}
					if(event.isCutOffEvent() && !postEvent.isCutOffEvent()){
						couple.remove(event);
						couple.add((ONEvent)event.object);
						configuration.getConfiguration(couple);
						currentConditions = configuration.getConditions();
					}
					if(event.isCutOffEvent() && postEvent.isCutOffEvent()){
						couple.remove(event);
						couple.remove(postEvent);
						couple.add((ONEvent)event.object);
						couple.add((ONEvent)postEvent.object);
						configuration.getConfiguration(couple);
						currentConditions = configuration.getConditions();
					}
					
					HashSet<ONCondition> copy = new HashSet<ONCondition>(currentConditions);
					trace = new Vector<ONEvent>();
					trace.addAll(prevTrace);
					trace.add(event);
					trace.add(postEvent);
					++executed[indexOfEvent][indexOfpostEvent];
					cfp.implement(trace, copy, executed, expected, cfp);
//					trace.addAll(postTrace);
					traces.add(trace);
					setVisited(cfp);
					
					copy = new HashSet<ONCondition>(currentConditions);
					trace = new Vector<ONEvent>();
					trace.addAll(prevTrace);
					trace.add(postEvent);
					trace.add(event);
					++executed[indexOfpostEvent][indexOfEvent];
					cfp.implement(trace, copy, executed, expected, cfp);
//					trace.addAll(postTrace);
					traces.add(trace);
					++visited[i][j];
				}
			}
			result.addAll(traces);
		}
		
		while(!inTheAir.isEmpty()){
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
					ONEvent correspondingEvent = (ONEvent)cutoff.object;
					HashSet<Vector<ONEvent>> temp = new HashSet<Vector<ONEvent>>();
					for(Vector<ONEvent> t : result){
						if(t.contains(correspondingEvent)){
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
							
							if(cutoff.getLocalConfigurationConditions().containsAll(configuration.getConditions()) &&
									configuration.getConditions().containsAll(cutoff.getLocalConfigurationConditions())){
								for(int k = index + 1; k < t.size(); ++k){
									trace.add(t.get(k));
								}
								temp.add(trace);
							}
						}
					}
					result.addAll(temp);
					//
					for(ONEvent cutoff2 : inTheAir.keySet()){
						inTheAir.get(cutoff2).remove(cutoff);
					}
					finished.add(cutoff);
				}
			}
			
			for(ONEvent cutoff : finished){
				inTheAir.remove(cutoff);
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

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod#getLogType()
	 */
	@Override
	public String getLogType() {
		// TODO Auto-generated method stub
		return "ETAR";
	}

	/**
	 * 2010-12-28
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\completeLog\\Nonfree3.pnml");
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
		ExtensiveTarLPM eTar = new ExtensiveTarLPM();
		eTar.generateLog("C:\\Users\\lenovo\\Documents\\completeLog\\Nonfree3.log", pn);
	
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {
		// TODO Auto-generated method stub
		
	}

}
