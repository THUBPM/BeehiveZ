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
 *@Author Wang Wenxing 
 *
 */
package cn.edu.thss.iise.beehivez.server.metric.tar.temp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.*;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-11-14
 */
public class ONTransitionConcurrentRelation {

	public boolean transitionConcurrentRelation[][] = null;
	public ONCompleteFinitePrefix cfp = null;
	public int size = 0;
	
	/**
	 * @param cfp
	 */
	public ONTransitionConcurrentRelation(ONCompleteFinitePrefix cfp) {
		super();
		this.cfp = cfp;
		size = this.cfp.getOn().getEveSet().size();
		transitionConcurrentRelation = new boolean[size][size];
		buildConcurrentRelation();
	}

	public void buildConcurrentRelation(){
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				transitionConcurrentRelation[i][j] = false;
			}
		}
		
		for(int i = 0; i < size; ++i){
			boolean existCo = true;
			ONEvent left = cfp.getOn().getEveSet().get(i);
			HashSet<ONCondition> leftCoConditions = new HashSet<ONCondition>();
			HashSet<ONCondition> leftCoConditionsCOPY;
			Iterator<ONCondition> itPrevCondition1 = cfp.getOn().getConsINTOEve(left.getId()).iterator();
			if(itPrevCondition1.hasNext()){
				ONCondition prevCondition1 = itPrevCondition1.next();
				for(ONCondition coCondition : prevCondition1.getCommonCondition()){
					leftCoConditions.add(coCondition);
				}
				for(ONCondition coCondition : prevCondition1.getPrivateCondition()){
					leftCoConditions.add(coCondition);
				}
			}
			while(itPrevCondition1.hasNext()){
				ONCondition prevCondition1 = itPrevCondition1.next();
				HashSet<ONCondition> currentCoConditions = new HashSet<ONCondition>();
				
				for(ONCondition coCondition : prevCondition1.getCommonCondition()){
					currentCoConditions.add(coCondition);
				}
				for(ONCondition coCondition : prevCondition1.getPrivateCondition()){
					currentCoConditions.add(coCondition);
				}
				
				//may exist exception
				leftCoConditionsCOPY = new HashSet<ONCondition>(leftCoConditions);
				for(ONCondition condition : leftCoConditions){
					if(!currentCoConditions.contains(condition)){
						leftCoConditionsCOPY.remove(condition);
					}
				}
				leftCoConditions = leftCoConditionsCOPY;
				if(leftCoConditions.isEmpty()){
					existCo = false;
					break;
				}
			}
			
			if(!existCo){
				continue;
			}
			
			for(int j = i + 1; j < size; ++j){
				boolean isConcurrent = true;
				existCo = true;
				ONEvent right = cfp.getOn().getEveSet().get(j);
				HashSet<ONCondition> rightCoConditions = new HashSet<ONCondition>();
				HashSet<ONCondition> rightCoConditionsCOPY;
				Iterator<ONCondition> itPrevCondition2 = cfp.getOn().getConsINTOEve(right.getId()).iterator();
				if(itPrevCondition2.hasNext()){
					ONCondition preCondition2 = itPrevCondition2.next();
					for(ONCondition coCondition : preCondition2.getCommonCondition()){
						rightCoConditions.add(coCondition);
					}
					for(ONCondition coCondition : preCondition2.getPrivateCondition()){
						rightCoConditions.add(coCondition);
					}
				}
				while(itPrevCondition2.hasNext()){
					ONCondition preCondition2 = itPrevCondition2.next();
					HashSet<ONCondition> currentCoConditions = new HashSet<ONCondition>();
					for(ONCondition coCondition : preCondition2.getCommonCondition()){
						currentCoConditions.add(coCondition);
					}
					for(ONCondition coCondition : preCondition2.getPrivateCondition()){
						currentCoConditions.add(coCondition);
					}
					
					//may exist exception
					rightCoConditionsCOPY = new HashSet<ONCondition>(rightCoConditions);
					for(ONCondition condition : rightCoConditions){
						if(!currentCoConditions.contains(condition)){
							rightCoConditionsCOPY.remove(condition);
						}
					}
					rightCoConditions = rightCoConditionsCOPY;
					if(rightCoConditions.isEmpty()){
						existCo = false;
						break;
					}
				}
				if(!existCo){
					continue;
				}
				
				itPrevCondition1 = cfp.getOn().getConsINTOEve(left.getId()).iterator();
				while(itPrevCondition1.hasNext()){
					ONCondition prevCondition1 = itPrevCondition1.next();
					if(!rightCoConditions.contains(prevCondition1)){
						isConcurrent = false;
						break;
					}
				}
				
				itPrevCondition2 = cfp.getOn().getConsINTOEve(right.getId()).iterator();
				while(itPrevCondition2.hasNext()){
					ONCondition prevCondition2 = itPrevCondition2.next();
					if(!leftCoConditions.contains(prevCondition2)){
						isConcurrent = false;
					}
				}
				
				if(isConcurrent){
					transitionConcurrentRelation[i][j] = true;
					transitionConcurrentRelation[j][i] = true;
				}
				
			}
		}
	}


	/**
	 * 2010-11-14
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("C:\\Users\\winever\\Documents\\QueryModel\\Non-free Choice\\Nonfree8.pnml");
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
		input=MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
		long start = System.currentTimeMillis();
		System.out.println(start);
		
		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
		ONTransitionConcurrentRelation tcr = new ONTransitionConcurrentRelation(cfp);
		
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = end - start;
		System.out.println(duration);
		cfpBuilder.cfp.getOn().ONToMPN().export_pnml("C:\\Users\\winever\\Documents\\QueryModel\\Non-free Choice\\Unfolding_Nonfree8.pnml");
		
		for(int i = 0; i < tcr.size; ++i){
			System.out.print('\t' + cfp.getOn().getEveSet().get(i).getLabel());
		}
		
		System.out.println();
		for(int i = 0; i < tcr.size; ++i){
			System.out.print(cfp.getOn().getEveSet().get(i).getLabel() + ":");
			for(int j = 0; j < tcr.size; ++j){
				System.out.print('\t');
				System.out.print(tcr.transitionConcurrentRelation[i][j]);
			}
			System.out.println();
		}
		
	}

}
