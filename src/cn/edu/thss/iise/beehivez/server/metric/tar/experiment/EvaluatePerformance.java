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
package cn.edu.thss.iise.beehivez.server.metric.tar.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import org.processmining.framework.models.causality.CausalFootprint;
import org.processmining.framework.models.causality.CausalityFootprintFactory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.semanticweb.kaon2.c;

//import quicktime.app.image.FileFilter;


import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.bts.BTSGenerator_Wang;
import cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity_Wang;
import cn.edu.thss.iise.beehivez.server.metric.CausalFootprintSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.ExtensiveTARSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.JaccardTARSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelation;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
import cn.edu.thss.iise.beehivez.server.metric.tar.ComputeSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.tar.ExtensiveTAR;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 28, 2011
 * 
 */
public class EvaluatePerformance {

	public static void compute(PetriNet pn1, PetriNet pn2){
		JaccardTARSimilarity tarSimilarity = new JaccardTARSimilarity();
		System.out.println("TAR:" + tarSimilarity.similarity(pn1, pn2));
		
		ExtensiveTARSimilarity extarSimilarity = new ExtensiveTARSimilarity();
		System.out.println("TAR*:" + extarSimilarity.similarity(pn1, pn2));
		
		BTSSimilarity_Wang btsSimilarity = new BTSSimilarity_Wang();
		System.out.println("BTS:" + btsSimilarity.similarity(pn1, pn2));
		
		CausalFootprintSimilarity cfpSimilarity = new CausalFootprintSimilarity();
		System.out.println("CFP:" + cfpSimilarity.similarity(pn1, pn2));
	}
	
	public static void run(PetriNet pn, int method){
		long start, end, duration;
		
		if (method == 1) {
			//TAR*
			start = System.currentTimeMillis();
			ExtensiveTAR extar = new ExtensiveTAR(pn);
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("TAR*:" + duration);
		}
		else if (method == 2) {
			//TAR
			start = System.currentTimeMillis();
			MyTransitionAdjacentRelationSet tarSet = new MyTransitionAdjacentRelationSet(pn);
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.printf("states:%d, tar:%d, TAR:%d", 
					 tarSet.getRmg().reachmarkinggraph.size(), tarSet.tarSet.size(), duration);
			System.out.println();
		}
		else if (method == 3) {
			//PTS
			start = System.currentTimeMillis();
			BTSGenerator_Wang bts = new BTSGenerator_Wang();
			MyPetriNet mpn = MyPetriNet.PromPN2MyPN(pn);
			bts.getBTS(mpn);
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.printf("BTS:%d", duration);
			System.out.println();
		}
		else if (method == 4) {
			//causal foot print
			start = System.currentTimeMillis();
			CausalFootprint c = CausalityFootprintFactory.make(pn, null);
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.printf("CFP:%d", duration);
			System.out.println();
		}
		else{
			System.out.println("Incorrect input!");
		}
		
		//Behavioral Profile
//		start = System.currentTimeMillis();
//		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
//		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
//		BehavioralRelationBuilder bp = new BehavioralRelationBuilder(cfp);
//		bp.buildBehavioralRelaton();
//		end = System.currentTimeMillis();
//		duration = end - start;
//		System.out.printf(" BP:%d", duration);
	}
	
	public static PetriNet getModel(String path){
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(path);
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
		
		return pn;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path;
		Scanner in = new Scanner(System.in);
		
		System.out.println("Select 1: similarity, 2:performance");
		int select = Integer.parseInt(in.nextLine());
		if (select == 1) {
			System.out.println("Enter the two models:");
			path = "experiment_1";
			String str1 = in.nextLine();
			PetriNet model1 = getModel(path + "\\" + str1);
			String str2 = in.nextLine();
			PetriNet model2 = getModel(path + "\\" + str2);
			
			compute(model1, model2);
		}
		else if (select == 2) {
			path = "experiment_1_2";
			File models = new File(path);
			String[] modelList = models.list(new FilenameFilter() {
				
				@Override
				public boolean accept(java.io.File dir, String name) {
					// TODO Auto-generated method stub
					if (name.endsWith(".pnml")) {
						return true;
					}
					return false;
				}
			});
			
			System.out.println("Select 1: TAR*, 2:TAR, 3:PTS, 4:CFP");
			int method = Integer.parseInt(in.nextLine());
			for (int i = 0; i < modelList.length; i++) {
				PetriNet model = getModel(path + "\\" + modelList[i]);
				System.out.print(modelList[i] + ":");
				run(model, method);
			}
			
		}
		else{
			System.out.println("Incorrect input!");
		}
	}
	


}
