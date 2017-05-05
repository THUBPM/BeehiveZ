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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;

/**
 * 
 * @author Nianhua Wu
 * 
 */
public class StratifiedWeightLPM extends LogProduceMethod {
	public static final int MAXSTEP = 10000;

	public StratifiedWeightLPM() {
		super();
	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness, int multiple) {
		
	}

	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn, double completeness) {
		// TODO Auto-generated method stub
		GetSucceRelation succeRelation = new GetSucceRelation();
		succeRelation.GetAllSucceRelation(pn);
		ArrayList<String> relations = succeRelation.getAllRelation();
		logIO.clear();
		logIO.open();
		ArrayList<Place> pList = pn.getPlaces();
		ArrayList<Transition> tList = pn.getTransitions();
		Vector<Transition> iniv = new Vector<Transition>();// 鍒濆鍖栫姸涓嬪彲鎵ц鐨勫彉杩�
		Vector<Transition> actv = new Vector<Transition>();// 褰撳墠鍙兘鍙墽琛岀殑鍙樿縼
		Vector<Transition> exev = new Vector<Transition>();// 褰撳墠鍙墽琛岀殑鍙樿縼
		int[] initialMarking = new int[pList.size()];
		short weight[][] = new short[MAXSTEP][tList.size() + pList.size()];
		Place source = (Place) pn.getSource();
		int index = pList.indexOf(source);
		initialMarking[index] = 1;
		pn.initialMarking(initialMarking);
		for (int i = 0; i < tList.size(); i++) {
			boolean bool = pn.isTransitionEnable(tList.get(i));
			if (bool) {
				iniv.add(tList.get(i));
			}
		}
		int j = 0;
		long start = System.currentTimeMillis();
		while (j < caseCount || !(relations.isEmpty())) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
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
						String string = lastExec.getIdentifier()
								+ t.getIdentifier();
						relations.remove(string);
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
					// System.out.print(t.getIdentifier()+" ");

				} else {
					break;
				}
				exev.removeAllElements();
			}
			actv.removeAllElements();
			// System.out.println();
			j++;

		}
		long end = System.currentTimeMillis();
		long usedTime = end - start;
		String result = "";
		result += usedTime + "," + j;
		FileLogger.writeLog("test2.csv", result);
		/*
		 * for(int j = 0; j < caseCount; j++) {
		 * pn.initialMarking(initialMarking); int temp = pn.getCaseid();
		 * pn.setCaseid( temp+ 1);
		 * 
		 * if(pn.getCaseid() < 10) System.out.print("case_" + pn.getCaseid()
		 * +" : "); else System.out.print("case_" + pn.getCaseid() +": ");
		 * 
		 * for (int i = 0; i < iniv.size(); i++) { actv.add(iniv.get(i)); }
		 * Random rand = new Random(); for (int step = 0; step < MAXSTEP;
		 * step++) { for (int i = 0; i < actv.size(); i++) { boolean bool =
		 * pn.isTransitionEnable(actv.get(i)); if (bool) {
		 * exev.add(actv.get(i)); } else { actv.remove(i); i--; } } if
		 * (exev.size() > 0) { boolean single = true; short minWeight = 10000;
		 * if(exev.size() > 1) { single = false; for (int m = 0;m < exev.size();
		 * m++) { if(weight[step][exev.get(m).getId()] < minWeight) minWeight =
		 * weight[step][exev.get(m).getId()]; } for (int n = 0;n < exev.size();
		 * n++) { if(weight[step][exev.get(n).getId()] > minWeight){
		 * exev.remove(n); n--; } } } int i = rand.nextInt((exev.size()));
		 * Transition t = exev.get(i); pn.executeTransition(t); if(single ==
		 * false) weight[step][exev.get(i).getId()]++;
		 * 
		 * HashSet set = t.getSuccessors(); Iterator it = set.iterator();
		 * while(it.hasNext()) { Place p = (Place) it.next(); HashSet setp =
		 * p.getSuccessors(); Iterator itp = setp.iterator();
		 * while(itp.hasNext()) { Transition tnext = (Transition) itp.next();
		 * if(!actv.contains(tnext)) actv.add(tnext); } }
		 * logIO.addEventLog("case_" + pn.getCaseid(), t.getIdentifier(),
		 * logIO.EVENT_COMPLETE, "", "");
		 * System.out.print(t.getIdentifier()+" ");
		 * 
		 * } else{ break; } exev.removeAllElements(); }
		 * actv.removeAllElements(); System.out.println(); }
		 */
		logIO.store(fileDir);
	}
	public void generateLog(String fileDir, PetriNet pn){
		
	}
	
	public String getLogType(){
		return "";
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {
		// TODO Auto-generated method stub
		
	}
}
