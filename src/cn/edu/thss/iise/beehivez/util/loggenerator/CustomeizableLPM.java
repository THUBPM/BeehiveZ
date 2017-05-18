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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.ui.Message;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;
/**
 * 
 * @author Nianhua Wu
 * 
 * @date 2011-3-10
 *
 */
public class CustomeizableLPM extends LogProduceMethod  {
	public static final int MAXSTEP = 1000;
	
	
	public CustomeizableLPM() {
		super();
	}
	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {	
		
		//tarCompleteness
		if(comPara.getCompleteType()==CompleteParameters.TarCompleteness){
			TarCompleteMethod(fileDir,pn,comPara,noiPara);
		}
		else if(comPara.getCompleteType()==CompleteParameters.cauCompleteness){
			 CauCompleteMethod(fileDir, pn,comPara, noiPara);
		}
		else{	
			FreCompleteMethod(fileDir, pn,comPara, noiPara);
		}
	}
		
	//Tar completeness Method
	public void TarCompleteMethod(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara)
	{
		logIO.clear();
		logIO.open();
		int relationCount = 0;
		
		ArrayList<Place> pList = pn.getPlaces();
		ArrayList<Transition> tList = pn.getTransitions();
		Vector<Transition> iniv = new Vector<Transition>();// initial enabled transition
		Vector<Transition> actv = new Vector<Transition>();// at present enabled transition
		Vector<Transition> exev = new Vector<Transition>();// executed transition
		int[] initialMarking = new int[pList.size()];
		short weight[][] = new short[MAXSTEP][tList.size()+ pList.size()];
		Place source = (Place) pn.getSource();
		int index = pList.indexOf(source);
		initialMarking[index] = 1;
		pn.initialMarking(initialMarking);
		
		//get intial enabled transition
		for (int i = 0; i < tList.size(); i++) {
			boolean bool = pn.isTransitionEnable(tList.get(i));
			if (bool) {
				iniv.add(tList.get(i));
			}
		}
		
		HashSet<TransitionLabelPair> relations = new HashSet<TransitionLabelPair>();//to save transition relations
		ArrayList<ArrayList<Transition>> trace = new ArrayList<ArrayList<Transition>>();
		relations = PetriNetUtil.getTARSFromPetriNetByCFP(pn);
		Message.add("The model contains "+ relations.size()+" TAR relations");
				
		Iterator re_it = relations.iterator();
		int a = 0;
		String temp_str = "";
		while(re_it.hasNext()){
			TransitionLabelPair tran_pair = (TransitionLabelPair) re_it.next();
			temp_str+=tran_pair.toString()+"      ";
			a++;
			if(a==5)
			{
				Message.add(temp_str);
				temp_str="";
				a=0;
			}
		}
		if(temp_str!="")
			Message.add(temp_str);
		Message.add("the log covers relations as follow: ");
			
		double completeDegree = comPara.getCompleteDegree();
		int[] parameter = comPara.getParameter();
		int size = relations.size();
		int needCount =(int) ((completeDegree*size) + 1);
		if(needCount>size)
			needCount=size;
		pn.setCaseid(0);
			
		while (relationCount<needCount) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			trace.add(new ArrayList<Transition>());
			pn.setCaseid(temp + 1);
			for (int i = 0; i < iniv.size(); i++) {
				actv.add(iniv.get(i));
			}
			Random rand = new Random();
			Transition lastExec = null;
			for (int step = 0; step < MAXSTEP; step++) {
				for (int i = 0; i < actv.size(); i++) {
					boolean bool = pn.isTransitionEnable(actv.get(i));
					if (bool) {
						exev.add(actv.get(i));
					} else {
						actv.remove(i);
						i--;
					}
				}
				if (exev.size() > 0) {
					boolean single = true;
					short minWeight = 10000;
					if (exev.size() > 1) {
						single = false;
						for (int m = 0; m < exev.size(); m++) {
							if (weight[step][exev.get(m).getId()] < minWeight)
								minWeight = weight[step][exev.get(m).getId()];
						}
						for (int n = 0; n < exev.size(); n++) {
							if (weight[step][exev.get(n).getId()] > minWeight) {
								exev.remove(n);
								n--;
							}
						}
					}
					int i = rand.nextInt((exev.size()));
					Transition t = exev.get(i);
					pn.executeTransition(t);
					if (step != 0) {
						if(relations.remove(new TransitionLabelPair(lastExec.getIdentifier(),t.getIdentifier()))){
							relationCount++;
							Message.add("("+lastExec.getIdentifier()+","+ t.getIdentifier()+")");
							
						}	
					}else{
						if(relations.remove(new TransitionLabelPair("null",t.getIdentifier()))){
							relationCount++;
							Message.add("(null, "+ t.getIdentifier()+")");
						}
					}
					lastExec = t;
					if (single == false)
						weight[step][exev.get(i).getId()]++;

					HashSet set = t.getSuccessors();
					Iterator it = set.iterator();
					while (it.hasNext()) {
						Place p = (Place) it.next();
						HashSet setp = p.getSuccessors();
						Iterator itp = setp.iterator();
						while (itp.hasNext()) {
							Transition tnext = (Transition) itp.next();
							if (!actv.contains(tnext))
								actv.add(tnext);
						}
					}
					logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");
					(trace.get(pn.getCaseid()-1)).add(t);	
				} else {
					break;
					}
				exev.removeAllElements();
			}
				actv.removeAllElements();

			}
			
			// deal with noise
		    int noiseCount = 0;
			if(noiPara.getNoiseType()==NoiseParameters.haveNoise)
			{
				int caseCount = pn.getCaseid();
				double noiseDegree = noiPara.getNoiseDegree();
				noiseCount = (int) ((caseCount/(1-noiseDegree))*noiseDegree);
				if(noiseCount<1)
					noiseCount=1;
				Message.add("Produce "+noiseCount +"noise traces");
				int i = 0;
				int[]para = noiPara.getParameter();
				Random rand = new Random();
				int x = rand.nextInt(caseCount);
				ArrayList<Transition> trace_x = trace.get(x);
				while(i<noiseCount){
					for(int j = 0;j<para.length;j++)
					{
						pn.setCaseid(pn.getCaseid()+1);
						i++;
						if(para[j]==1)
						{
							switch(j){
							case 0: //no head
							{
								for(int k = 1;k<trace_x.size();k++)
								{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 1: //no Tail
							{
								for(int k = 0;k<(trace_x.size()-1);k++)
								{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
							}
							case 2: // partbody
							{
								int m = rand.nextInt(trace_x.size()-1)+1;
								int n = rand.nextInt(m);
								if(n==0)n++;
								
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k<n||k>=m)
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 3: // lack one event 
							{
								int m = rand.nextInt(trace_x.size());
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k!=m);
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 4: //interchange 
							{
								int m = rand.nextInt(trace_x.size());
								int n;
								if(m!=0){
									n=m-1;
								}
								else
									n=m+1;
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k==m){
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(n), logIO.EVENT_COMPLETE, "", "");
									}else if(k==n){
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(m), logIO.EVENT_COMPLETE, "", "");
									}else{
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
									}
								}
								break;
							}
							default:
								break;
						}

					  }
					if(i>=noiseCount) //noiseCase enough
						break;
						
				}
			}			
		}
		File logfile;
		try {
			logfile = new File(fileDir);
			logfile.createNewFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
		logIO.store(fileDir);
		JOptionPane.showMessageDialog(null,
				"Model all TAR relations : " +size+"\n"+
				"Log covers TAR relations: " + needCount + "\n"+
				"Log contains Traces: "+pn.getCaseid()+"\n"+
				"Trace/Transitions = "+ pn.getCaseid()+"/"+pn.getTransitions().size());

	}
	
	//causal Completeness method
	public void CauCompleteMethod(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara)
	{
		logIO.clear();
		logIO.open();
		int relationCount = 0;
		
		ArrayList<Place> pList = pn.getPlaces();
		ArrayList<Transition> tList = pn.getTransitions();
		Vector<Transition> iniv = new Vector<Transition>();// initial enabled transition
		Vector<Transition> actv = new Vector<Transition>();// at present enabled transition
		Vector<Transition> exev = new Vector<Transition>();// executed transition
		int[] initialMarking = new int[pList.size()];
		short weight[][] = new short[MAXSTEP][tList.size()+ pList.size()];
		Place source = (Place) pn.getSource();
		int index = pList.indexOf(source);
		initialMarking[index] = 1;
		pn.initialMarking(initialMarking);
		
		//get intial enabled transition
		for (int i = 0; i < tList.size(); i++) {
			boolean bool = pn.isTransitionEnable(tList.get(i));
			if (bool) {
				iniv.add(tList.get(i));
			}
		}
		
		HashSet<TransitionLabelPair> relations = new HashSet<TransitionLabelPair>();//to save transition relations
		ArrayList<ArrayList<Transition>> trace = new ArrayList<ArrayList<Transition>>();
		relations = PetriNetUtil.getCauRelationsFromPetriNet(pn);
		Message.add("The model contains "+ relations.size()+" Causal relations");
				
		Iterator re_it = relations.iterator();
		int a = 0;
		String temp_str = "";
		while(re_it.hasNext()){
			TransitionLabelPair tran_pair = (TransitionLabelPair) re_it.next();
			temp_str+=tran_pair.toString()+"       ";
			a++;
			if(a==5)
			{
				Message.add(temp_str);
				temp_str="";
				a=0;
			}
		}
		if(temp_str!="")
			Message.add(temp_str);
		Message.add("the log covers relations as follow: ");
			
		double completeDegree = comPara.getCompleteDegree();
		int[] parameter = comPara.getParameter();
		int size = relations.size();
		int needCount =(int) ((completeDegree*size) + 1);
		if(needCount>size)
			needCount=size;
		pn.setCaseid(0);
		while (relationCount<needCount) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			trace.add(new ArrayList<Transition>());
			pn.setCaseid(temp + 1);
			for (int i = 0; i < iniv.size(); i++) {
				actv.add(iniv.get(i));
			}
			Random rand = new Random();
			Transition lastExec = null;
			for (int step = 0; step < MAXSTEP; step++) {
				for (int i = 0; i < actv.size(); i++) {
					boolean bool = pn.isTransitionEnable(actv.get(i));
					if (bool) {
						exev.add(actv.get(i));
					} else {
						actv.remove(i);
						i--;
					}
				}
				if (exev.size() > 0) {
					boolean single = true;
					short minWeight = 10000;
					if (exev.size() > 1) {
						single = false;
						for (int m = 0; m < exev.size(); m++) {
							if (weight[step][exev.get(m).getId()] < minWeight)
								minWeight = weight[step][exev.get(m).getId()];
						}
						for (int n = 0; n < exev.size(); n++) {
							if (weight[step][exev.get(n).getId()] > minWeight) {
								exev.remove(n);
								n--;
							}
						}
					}
					int i = rand.nextInt((exev.size()));
					Transition t = exev.get(i);
					pn.executeTransition(t);
					if (step != 0) {
						if(relations.remove(new TransitionLabelPair(lastExec.getIdentifier(),t.getIdentifier()))){
							relationCount++;
							Message.add("("+lastExec.getIdentifier()+","+ t.getIdentifier()+")");
						}	
					}else{
						if(relations.remove(new TransitionLabelPair("null",t.getIdentifier()))){
							Message.add("(null, "+ t.getIdentifier()+")");
							relationCount++;
						}
						
						}
					lastExec = t;
					if (single == false)
						weight[step][exev.get(i).getId()]++;

					HashSet set = t.getSuccessors();
					Iterator it = set.iterator();
					while (it.hasNext()) {
						Place p = (Place) it.next();
						HashSet setp = p.getSuccessors();
						Iterator itp = setp.iterator();
						while (itp.hasNext()) {
							Transition tnext = (Transition) itp.next();
							if (!actv.contains(tnext))
								actv.add(tnext);
						}
					}
					logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");
					(trace.get(pn.getCaseid()-1)).add(t);	
				} else {
					break;
					}
				exev.removeAllElements();
			}
				actv.removeAllElements();

			}
			
			// deal with noise
			int noiseCount = 0;
			if(noiPara.getNoiseType()==NoiseParameters.haveNoise)
			{
				int caseCount = pn.getCaseid();
				double noiseDegree = noiPara.getNoiseDegree();
				noiseCount = (int) ((caseCount/(1-noiseDegree))*noiseDegree);
				if(noiseCount<1)
					noiseCount=1;
				Message.add("Produce "+noiseCount +"noise traces");
				int i = 0;
				int[]para = noiPara.getParameter();
				Random rand = new Random();
				int x = rand.nextInt(caseCount);
				ArrayList<Transition> trace_x = trace.get(x);
				while(i<noiseCount){
					for(int j = 0;j<para.length;j++)
					{
						pn.setCaseid(pn.getCaseid()+1);
						i++;
						if(para[j]==1)
						{
							switch(j){
							case 0: //no head
							{
								for(int k = 1;k<trace_x.size();k++)
								{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 1: //no Tail
							{
								for(int k = 0;k<(trace_x.size()-1);k++)
								{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
							}
							case 2: // partbody
							{
								int m = rand.nextInt(trace_x.size()-1)+1;
								int n = rand.nextInt(m);
								if(n==0)n++;
								
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k<n||k>=m)
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 3: // lack one event 
							{
								int m = rand.nextInt(trace_x.size());
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k!=m);
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
								break;
								
							}
							case 4: //interchange 
							{
								int m = rand.nextInt(trace_x.size());
								int n;
								if(m!=0){
									n=m-1;
								}
								else
									n=m+1;
								for(int k = 0;k<trace_x.size();k++)
								{
									if(k==m){
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(n), logIO.EVENT_COMPLETE, "", "");
									}else if(k==n){
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(m), logIO.EVENT_COMPLETE, "", "");
									}else{
										logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
									}
								}
								break;
							}
							default:
								break;
						}

					  }
					if(i>=noiseCount) //noiseCase enough
						break;
						
				}
			}			
		}
		File logfile;
		try {
			logfile = new File(fileDir);
			logfile.createNewFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
		logIO.store(fileDir);
		JOptionPane.showMessageDialog(null,
				"Model all Causal relations : " +size+"\n"+
				"Log covers Causal relations: " + needCount + "\n"+
				"Log contains Traces: "+pn.getCaseid()+noiseCount+"\n"+
				"Trace/Transitions = "+ pn.getCaseid()+"/"+pn.getTransitions().size());
		
	}
	//Frequent completeness method
	public void FreCompleteMethod(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara)
	{
		logIO.clear();
		logIO.open();
		int relationCount = 0;
		
		ArrayList<Place> pList = pn.getPlaces();
		ArrayList<Transition> tList = pn.getTransitions();
		Vector<Transition> iniv = new Vector<Transition>();// initial enabled transition
		Vector<Transition> actv = new Vector<Transition>();// at present enabled transition
		Vector<Transition> exev = new Vector<Transition>();// executed transition
		int[] initialMarking = new int[pList.size()];
		short weight[][] = new short[MAXSTEP][tList.size()+ pList.size()];
		Place source = (Place) pn.getSource();
		int index = pList.indexOf(source);
		initialMarking[index] = 1;
		pn.initialMarking(initialMarking);
		
		//get intial enabled transition
		for (int i = 0; i < tList.size(); i++) {
			boolean bool = pn.isTransitionEnable(tList.get(i));
			if (bool) {
				iniv.add(tList.get(i));
			}
		}
		
		HashSet<TransitionLabelPair> relations = new HashSet<TransitionLabelPair>();//to save transition relations
		ArrayList<ArrayList<Transition>> trace = new ArrayList<ArrayList<Transition>>();
		relations = PetriNetUtil.getCauRelationsFromPetriNet(pn);
		Message.add("The model contains "+ relations.size()+" Frequent Causal relations");
				
		Iterator re_it = relations.iterator();
		int a = 0;
		String temp_str = "";
		while(re_it.hasNext()){
			TransitionLabelPair tran_pair = (TransitionLabelPair) re_it.next();
			temp_str+=tran_pair.toString()+"       ";
			a++;
			if(a==5)
			{
				Message.add(temp_str);
				temp_str="";
				a=0;
			}
		}
		if(temp_str!="")
			Message.add(temp_str);
		Message.add("The log covers relations as follow: ");
		pn.setCaseid(0);
		int minRelations = relations.size();
		relationCount = 0;
		while (relationCount<minRelations) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			trace.add(new ArrayList<Transition>());
			pn.setCaseid(temp + 1);

			for (int i = 0; i < iniv.size(); i++) {
				actv.add(iniv.get(i));
			}
			Random rand = new Random();
			Transition lastExec = null;
			for (int step = 0; step < MAXSTEP; step++) {
				for (int i = 0; i < actv.size(); i++) {
					boolean bool = pn.isTransitionEnable(actv.get(i));
					if (bool) {
						exev.add(actv.get(i));
					} else {
						actv.remove(i);
						i--;
					}
				}
				if (exev.size() > 0) {
					boolean single = true;
					short minWeight = 10000;
					if (exev.size() > 1) {
						single = false;
						for (int m = 0; m < exev.size(); m++) {
							if (weight[step][exev.get(m).getId()] < minWeight)
								minWeight = weight[step][exev.get(m).getId()];
						}
						for (int n = 0; n < exev.size(); n++) {
							if (weight[step][exev.get(n).getId()] > minWeight) {
								exev.remove(n);
								n--;
							}
						}
					}
					int i = rand.nextInt((exev.size()));
					Transition t = exev.get(i);
					pn.executeTransition(t);
					if (step != 0) {
						if(relations.remove(new TransitionLabelPair(lastExec.getIdentifier(),t.getIdentifier()))){
							relationCount++;
							Message.add("("+ lastExec.getIdentifier()+", " + t.getIdentifier()+")");
						}	
					}else{
						if(relations.remove(new TransitionLabelPair("null",t.getIdentifier()))){
							Message.add("(null, "+ t.getIdentifier() +")");
							relationCount++;
						}
						
					}
					lastExec = t;
					if (single == false)
						weight[step][exev.get(i).getId()]++;

					HashSet set = t.getSuccessors();
					Iterator it_1 = set.iterator();
					while (it_1.hasNext()) {
						Place p = (Place) it_1.next();
						HashSet setp = p.getSuccessors();
						Iterator itp = setp.iterator();
						while (itp.hasNext()) {
							Transition tnext = (Transition) itp.next();
							if (!actv.contains(tnext))
								actv.add(tnext);
						}
					}
					logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");
					(trace.get(pn.getCaseid()-1)).add(t);
				}
			}
		}
		double freDegree = comPara.getCompleteDegree(); // freConfig = (|A>B|-|B>A|)/(|A>B|+|B>A|+1), |B>A| is noise
		double NoiDegree = noiPara.getNoiseDegree();
		int minFre = (int)((freDegree)/(1-freDegree));
		for(int i = 0;i< minFre;i++){
			for(int j = 0;j<(trace.size()-1);j++){
				pn.setCaseid(pn.getCaseid()+1);
				for(int k = 0;k<trace.get(j).size();k++){
					Transition t = trace.get(j).get(k);
					logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");
				}
			}
		}
		//have noise, then generate more 
		int noiseCase = 0;
		if(noiPara.getNoiseType()==NoiseParameters.haveNoise)
		{
			noiseCase = (int) (pn.getCaseid()* NoiDegree);
			if(noiseCase<1)
				noiseCase = 1;
			Message.add("Produce "+noiseCase +"noise traces");
			int i = 0;
			int[]para = noiPara.getParameter();
			Random rand = new Random();
			int x = rand.nextInt(trace.size());
			ArrayList<Transition> trace_x = trace.get(x);
			while(i<noiseCase){
				for(int j = 0;j<para.length;j++)
				{
					pn.setCaseid(pn.getCaseid()+1);
					i++;
					if(para[j]==1)
					{
						switch(j){
						case 0: //no head
						{
							for(int k = 1;k<trace_x.size();k++)
							{
								logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
							}
							break;
							
						}
						case 1: //no Tail
						{
							for(int k = 0;k<(trace_x.size()-1);k++)
							{
								logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
							}
							break;
						}
						case 2: // partbody
						{
							int m = rand.nextInt(trace_x.size()-1)+1;
							int n = rand.nextInt(m);
							if(n==0)n++;
							
							for(int k = 0;k<trace_x.size();k++)
							{
								if(k<n||k>=m)
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
							}
							break;
							
						}
						case 3: // lack one event 
						{
							int m = rand.nextInt(trace_x.size());
							for(int k = 0;k<trace_x.size();k++)
							{
								if(k!=m);
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
							}
							break;
							
						}
						case 4: //interchange 
						{
							int m = rand.nextInt(trace_x.size());
							int n;
							if(m!=0){
								n=m-1;
							}
							else
								n=m+1;
							for(int k = 0;k<trace_x.size();k++)
							{
								if(k==m){
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(n), logIO.EVENT_COMPLETE, "", "");
								}else if(k==n){
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(m), logIO.EVENT_COMPLETE, "", "");
								}else{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
							}
							// 在该情况下为满频率完备性，产生(1+a)/(1-a)个正常的trace
							int moreCase = (int) ((1+freDegree)/(1-freDegree));
							m = 0;
							for(;m<moreCase;m++){
								for(int k = 0;k<trace_x.size();k++)
								{
									logIO.addEventLog("case_" + pn.getCaseid(), trace_x.get(k), logIO.EVENT_COMPLETE, "", "");
								}
							}
								
							break;
						}
						default:
							break;
					}

				   }
					if(i>=noiseCase) //noiseCase enough
						break;
				}
			}	
		}
		File logfile;
		try {
			logfile = new File(fileDir);
			logfile.createNewFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
		logIO.store(fileDir);	
		JOptionPane.showMessageDialog(null,
				"Model all Causal relations : " +minRelations+"\n"+
				"Log covers Causal relations: " + minRelations + "\n"+
				"Log contains Traces: "+pn.getCaseid()+"\n"+
				"Trace/Transitions = "+ pn.getCaseid()+"/"+pn.getTransitions().size());
	}
	
	@Override
	public String getLogType() {
		// TODO Auto-generated method stub
		return "CustomeizableLog";
	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness, int multiple) {
		
	}
	public static void main(String[]arsgs){
		
		CustomeizableLPM lpm = new CustomeizableLPM();
		CompleteParameters comPara = new CompleteParameters();
		NoiseParameters noiPara = new NoiseParameters();
		String filepath = "test.pnml";
		String logpath = "test.mxml";
		PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(filepath);
		lpm.generateLog(logpath, pn, comPara, noiPara);
		
		
	}

}
