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
public class UnstratifiedWeightLPM extends LogProduceMethod {
	public static final int MAXSTEP = 10000;

	public UnstratifiedWeightLPM() {
		super();
	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness, int multiple) {
		
	}

	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn ,double completeness) {
		// TODO Auto-generated method stub
		logIO.clear();
		logIO.open();
		long starttime = System.currentTimeMillis();
		ArrayList<Place> pList = pn.getPlaces();
		int[] initialMarking = new int[pList.size()];
		ArrayList<Transition> tempList = pn.getTransitions();
		int[] weight = new int[tempList.size() + pList.size()];
		for (int i = 0; i < initialMarking.length; i++) {
			initialMarking[i] = pList.get(i).getNumberOfTokens();
		}
		for (int j = 0; j < caseCount; j++) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			// System.out.println("temp: "+temp);
			pn.setCaseid(temp + 1);
			// System.out.println("after add 1: "+pn.getCaseid());

			Vector<Transition> exev = new Vector<Transition>();
			Random rand = new Random();
			for (int step = 0; step < MAXSTEP; step++) {
				ArrayList<Transition> tList = pn.getTransitions();
				for (int i = 0; i < tList.size(); i++) {
					boolean bool = pn.isTransitionEnable(tList.get(i));
					if (bool) {
						exev.add(tList.get(i));
					}
				}
				if (exev.size() > 0) {
					boolean single = false;
					int minWeight = weight[exev.get(0).getId()];
					if (exev.size() == 1)
						single = true;
					for (int m = 0; m < exev.size(); m++) {
						if (weight[exev.get(m).getId()] <= minWeight)
							minWeight = weight[exev.get(m).getId()];
					}
					int index = -1;
					for (int n = 0; n < exev.size(); n++) {
						if (weight[exev.get(n).getId()] == minWeight) {
							index = n;
							break;
						}
					}
					Transition t = exev.get(index);
					pn.executeTransition(t);
					if (single == false)
						weight[exev.get(index).getId()]++;
					logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");

				} else {
					break;
				}
				exev.removeAllElements();
			}
		}
		long endtime = System.currentTimeMillis();
		long usedtime = endtime - starttime;
		String result = "," + usedtime;
		FileLogger.writeLog("test.csv", result);
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
