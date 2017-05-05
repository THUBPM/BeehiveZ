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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.TreeSet;

import org.processmining.framework.models.LogEventProvider;
import org.processmining.framework.models.causality.CausalFootprint;
import org.processmining.framework.models.causality.CausalityFootprintFactory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.bp.BehavioralRelationBuilder;
import cn.edu.thss.iise.beehivez.server.bts.BTSGenerator_Wang;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
import cn.edu.thss.iise.beehivez.server.metric.tar.ExtensiveTAR;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-12-14
 */
public class Experiment_2 {

	/**
	 * 2010-12-14
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String strModelPath = "experiment_1_2";
		File models = new File(strModelPath);
		long start, end, duration, tarDuration;
		String[] filenames = models.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.endsWith(".pnml")){
					return true;
				}
				return false;
			}
		});
		
		try{
			for(int i = 0; i < filenames.length; ++i){
				MyPetriNet input = null;
				FileInputStream fin = null;
				
				fin = new FileInputStream(strModelPath + "\\" + filenames[i]);
				PnmlImport pImport = new PnmlImport();
				PetriNetResult pnr = null;
				pnr = (PetriNetResult) pImport.importFile(fin);
				PetriNet pn = pnr.getPetriNet();
				input = MyPetriNet.PromPN2MyPN(pn);
				
				//TAR*
//				start = System.currentTimeMillis();
//				ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
//				ONCompleteFinitePrefix cfp = cfpBuilder.Build();
//				ExtensiveTAR tar = new ExtensiveTAR(cfp);
//				end = System.currentTimeMillis();
//				duration = end - start;
//				int t = 0;
//				for(String str : tar._tar.keySet()){
//					t += tar._tar.get(str).size();
//				}
//				for(String str : tar._tarRe.keySet()){
//					t += tar._tarRe.get(str).size();
//				}
//				for(String str : tar._tarIm.keySet()){
//					t += tar._tarIm.get(str).size();
//				}
//				for(String str : tar._tar0.keySet()){
//					t += tar._tar0.get(str).size();
//				}
//				System.out.printf("name:%s, tasks:%d, tar*:%d, TAR*:%d", 
//						filenames[i], 
//						cfp.getOn().getEveSet().size(), t, duration);
				
				//TAR
				start = System.currentTimeMillis();
				MyTransitionAdjacentRelationSet tarSet = new MyTransitionAdjacentRelationSet(pn);
				end = System.currentTimeMillis();
				tarDuration = end - start;
				System.out.printf("name:%s, states:%d, tar:%d, TAR:%d", 
						filenames[i], tarSet.getRmg().reachmarkinggraph.size(), tarSet.tarSet.size(), tarDuration);
				
				//Behavioral Profile
//				start = System.currentTimeMillis();
//				ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
//				ONCompleteFinitePrefix cfp = cfpBuilder.Build();
//				BehavioralRelationBuilder bp = new BehavioralRelationBuilder(cfp);
//				bp.buildBehavioralRelaton();
//				end = System.currentTimeMillis();
//				duration = end - start;
//				System.out.printf("name:%s, BP:%d", filenames[i], duration);
				
				//PTS
//				start = System.currentTimeMillis();
//				BTSGenerator_Wang bts = new BTSGenerator_Wang();
//				bts.getBTS(input);
//				end = System.currentTimeMillis();
//				duration = end - start;
//				System.out.printf("name:%s, BTS:%d", filenames[i], duration);
				
				//causal foot print
//				start = System.currentTimeMillis();
//				CausalFootprint c = CausalityFootprintFactory.make(input);
//				end = System.currentTimeMillis();
//				duration = end - start;
//				System.out.printf("name:%s, CFP:%d", filenames[i], duration);
				
//				System.out.println("test");
				System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
