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
import cn.edu.thss.iise.beehivez.util.loginfo.DirectSuccessionInfo;

/**
 * 
 * @author zhp,htf
 * 
 */
public class AverageWeightLPM extends LogProduceMethod {
	public static final int MAXSTEP = 10000;

	public AverageWeightLPM() {
		super();
	}

	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn,
			double completeness, int multiple) {
		// TODO Auto-generated method stub
		logIO.clear();
		logIO.open();
		long starttime = System.nanoTime();
		ArrayList<Place> pList = pn.getPlaces();
		int[] initialMarking = new int[pList.size()];
		for (int i = 0; i < initialMarking.length; i++) {
			initialMarking[i] = pList.get(i).getNumberOfTokens();
		}
		for (int j = 0; j < caseCount; j++) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			pn.setCaseid(temp + multiple);
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
					int i = rand.nextInt((exev.size()));
					Transition t = exev.get(i);
					pn.executeTransition(t);
					for (int k = 0; k < multiple; k++){
						logIO.addEventLog("case_" + (temp+k), t,
								logIO.EVENT_COMPLETE, "", "");
					}						
				} else {
					break;
				}
				exev.removeAllElements();
			}
			logIO.store(fileDir);
			double res = 0;
			DirectSuccessionInfo dsi = new DirectSuccessionInfo();
			try {
				res = dsi.info(fileDir, pn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (res < completeness)
				continue;
			if (res >= completeness)
				break;
		}
		long endtime = System.nanoTime();
		long usedtime = endtime - starttime;
		String result = "," + usedtime;
		FileLogger.writeLog("test1.csv", result);
	}
	@Override
	public void generateLog(String fileDir, int caseCount, PetriNet pn, double completeness) {
		// TODO Auto-generated method stub
		logIO.clear();
		logIO.open();
		long starttime = System.nanoTime();
		ArrayList<Place> pList = pn.getPlaces();
		int[] initialMarking = new int[pList.size()];
		for (int i = 0; i < initialMarking.length; i++) {
			initialMarking[i] = pList.get(i).getNumberOfTokens();
		}
		//int count = 0;
		for (int j = 0; j < caseCount; j++) {
			pn.initialMarking(initialMarking);
			int temp = pn.getCaseid();
			pn.setCaseid(temp + 1);
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
					int i = rand.nextInt((exev.size()));
					Transition t = exev.get(i);
					pn.executeTransition(t);
                    logIO.addEventLog("case_" + pn.getCaseid(), t, logIO.EVENT_COMPLETE, "", "");
				
				} else {
					break;
				}
				exev.removeAllElements();
			}
			//count++;
			//if(count%5==0){
				logIO.store(fileDir);
				double res = 0;
				DirectSuccessionInfo dsi = new DirectSuccessionInfo();
				try {
					res = dsi.info(fileDir, pn);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(res<completeness) continue;
				if(res>=completeness) break;
			//}
		}
		long endtime = System.nanoTime();
		long usedtime = endtime - starttime;
		String result = "," + usedtime;
		FileLogger.writeLog("test1.csv", result);
		//logIO.store(fileDir);
	}
	public void generateLog(String fileDir, PetriNet pn){
		
	}
	
	public String getLogType(){
		return "TAR";
	}

	@Override
	public void generateLog(String fileDir, PetriNet pn,
			CompleteParameters comPara, NoiseParameters noiPara) {
		// TODO Auto-generated method stub
		
	}
}
