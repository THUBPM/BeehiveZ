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
 * 
 */
package cn.edu.thss.iise.beehivez.util.loginfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.processmining.framework.log.AuditTrailEntry;
import org.processmining.framework.log.AuditTrailEntryList;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.log.LogSummary;
import org.processmining.framework.log.rfb.BufferedLogReader;
import org.processmining.framework.log.rfb.LogData;
import org.processmining.framework.log.rfb.ProcessInstanceImpl;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelation;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
import cn.edu.thss.iise.beehivez.util.loggenerator.GetSucceRelation;

/**
 * Calculate the degree of information completeness of an event log of
 *  a specified workflow model described with Petri net.
 * 
 * Although there are other defintions of the information unit of an 
 * event log, here it is defined as a directed succession between two 
 * events.
 * 
 * Thus the information completeness can be measured by the ratio of 
 * number of observed information unit divided by the number of all 
 * possible information unit that can be derived from the specified 
 * workflw flow model.
 * 
 * @author hedong
 *
 */
public class DirectSuccessionInfo implements LogInfo {

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.util.loginfo.LogInfo#info(java.lang.String, java.lang.String)
	 */
	@Override
	public double info(String logfile, PetriNet pn)throws Exception {
		Hashtable<String,Integer> dsInLog=allObservedDSes(logfile);
		ArrayList<String> dsAll=allPossibleDSes(pn);
		long count=0;
		for(String ds:dsAll){
			if(dsInLog.containsKey(ds))count++;
		}

		return (double)1.0 * count/dsAll.size();
	}
	/**
	 * To find out all possible directed successions that can be derived from the specified worklfow model.
	 * 
	 * The function called procedures developed by Miss Nianhua Wu.
	 * 
	 * @param modelfile	the workflow model described with Petri Net.
	 * @return	a list of directed successsions.
	 * @throws Exception	possible exceptions thrown during parsing the petrinet file.
	 */
	private ArrayList<String> allPossibleDSes(PetriNet pn) throws Exception{
//		GetSucceRelation gsr=new GetSucceRelation();
//		gsr.GetAllSucceRelation(pn);
//		return gsr.getAllRelation();
		MyTransitionAdjacentRelationSet tarSet1 = new MyTransitionAdjacentRelationSet(
				MyPetriNet.fromProMPetriToMyPetri(pn));
		Vector<MyTransitionAdjacentRelation> vec = tarSet1.tarSet;
		ArrayList<String> al = new ArrayList<String>();
		for(MyTransitionAdjacentRelation tar:vec)
			al.add(tar.transitionA+tar.transitionB);
		return al;
	}
	/**
	 * To calculate the occurrences of the observed directed successions in the given log.
	 * 
	 * @param logfile	the log file in mxml format.
	 * @return	observed directed successions along with their occurrences.
	 * @throws Exception possible exceptions thrown during the parsing of the log file.
	 */
	private Hashtable<String,Integer> allObservedDSes(String logfile) throws Exception{
			LogFile logFile = LogFile.getInstance(logfile);
			LogReader reader=LogReaderFactory.createInstance(null, logFile);
			LogSummary summary=reader.getLogSummary();
			BufferedLogReader bufferedreader=(BufferedLogReader)( reader);
			LogData data=bufferedreader. getLogData();
			ArrayList<ProcessInstanceImpl> pis= data. instances();
			Hashtable<String,Integer> dses=new Hashtable<String,Integer>();
			for (ProcessInstanceImpl pi:pis){
				 AuditTrailEntryList entrylist=pi. getAuditTrailEntryList() ;
				 String lastEvent=null;
				 for(int i=0;i<entrylist. size();i++){
					 AuditTrailEntry entry= entrylist. get(i);
					 String currentEvent=entry.getElement();
					 if(lastEvent!=null){
						 String eventpair=lastEvent+""+currentEvent;
						 Integer count=1;
						 if(dses.containsKey(eventpair)){
							 count+=dses.get(eventpair);
							 dses.remove(eventpair);
						 }//else{System.out.println(eventpair);}
						 dses.put(eventpair, count);
					 }
					 lastEvent=currentEvent;
				 }
			}
			return dses;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String logfile="F:/workspace/BeehiveZ2_new/log/Invisible2.mxml";
		String pnfile="C:/QueryModel/Invisible Task/Invisible2.pnml";
		FileInputStream is = new FileInputStream(pnfile);
		PnmlImport input = new PnmlImport();
		PetriNet pn = input.read(is);
		LogInfo li=new DirectSuccessionInfo();
		System.out.println("information completeness of log "+logfile);
		System.out.println(" for model "+pnfile+" is  :");
		System.out.println(li.info(logfile, pn));
	}

}
