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
package cn.edu.thss.iise.beehivez.server.metric;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import antlr.collections.impl.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.bp.BehavioralRelation;
import cn.edu.thss.iise.beehivez.server.bp.BehavioralRelationBuilder;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class BehavioralProfileSimilarity {
	public String getName() {
		return "BehavioralProfileSimilarity";
	}

	public String getDesription() {
		return "similarity match based on basic process segments whitch is designed by wwx";
	}
	public double similarity(PetriNet pn1, PetriNet pn2){
		MyPetriNet mpn1 = MyPetriNet.PromPN2MyPN(pn1);
		ONCompleteFinitePrefixBuilder builder1 = new ONCompleteFinitePrefixBuilder(mpn1);
		ONCompleteFinitePrefix cfp1 = builder1.Build();
		BehavioralRelationBuilder bp1 = new BehavioralRelationBuilder(cfp1);
		bp1.buildBehavioralRelaton();
		
		MyPetriNet mpn2 = MyPetriNet.PromPN2MyPN(pn2);
		ONCompleteFinitePrefixBuilder builder2 = new ONCompleteFinitePrefixBuilder(mpn2);
		ONCompleteFinitePrefix cfp2 = builder2.Build();
		BehavioralRelationBuilder bp2 = new BehavioralRelationBuilder(cfp2);
		bp2.buildBehavioralRelaton();
		
		HashMap<HashMap<String, String>, BehavioralRelation> hm1 = 
			new HashMap<HashMap<String, String>, BehavioralRelation>();
		HashMap<HashMap<String, String>, BehavioralRelation> hm2 = 
			new HashMap<HashMap<String, String>, BehavioralRelation>();
		
		transform(hm1, cfp1, bp1.get_relation());
		transform(hm2, cfp2, bp2.get_relation());
		
		int total = 0;
		int count = 0;
		for(HashMap<String, String> pair1 : hm1.keySet()){
			for(HashMap<String, String> pair2 : hm2.keySet()){
				if(pair2.equals(pair1)){
					++total;
					if(hm1.get(pair1).equals(hm2.get(pair2))){
						++count;
					}
				}
				
			}
		}
		
		double result = (double)count / total;
		
		return result;
	}
	
	public void transform(HashMap<HashMap<String, String>, BehavioralRelation> hm, 
			ONCompleteFinitePrefix cfp, BehavioralRelation[][] relation){
		for(int i = 0; i < cfp.getOn().getEveSet().size(); ++i){
			for(int j = 0; j < cfp.getOn().getEveSet().size(); ++j){
				HashMap<String , String> pair = new HashMap<String , String>();
				pair.put(cfp.getOn().getEveSet().get(i).getLabel(), 
						cfp.getOn().getEveSet().get(j).getLabel());
				hm.put(pair, relation[i][j]);
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		MyPetriNet input2 = null;
		FileInputStream fin = null;
		FileInputStream fin2 = null;
		try {
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\experiment\\实验一F.pnml");
			fin2 = new FileInputStream("C:\\Users\\lenovo\\Documents\\experiment\\实验一E.pnml");
//			fin = new FileInputStream("C:\\Users\\winever\\Documents\\QueryModel\\Non-free Choice\\Nonfree8.pnml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PnmlImport pImport = new PnmlImport();
		PnmlImport pImport2 = new PnmlImport();
		PetriNetResult pnr = null;
		PetriNetResult pnr2 = null;
		try {
			pnr = (PetriNetResult) pImport.importFile(fin);
			pnr2 = (PetriNetResult) pImport2.importFile(fin2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fin.close();
			fin2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PetriNet pn = pnr.getPetriNet();
		PetriNet pn2 = pnr2.getPetriNet();
		
		long start = System.currentTimeMillis();
		System.out.println(start);
//		BehavioralProfileSimilarity bps = new BehavioralProfileSimilarity();
//		double similarity = bps.similarity(pn, pn2);
		CausalFootprintSimilarity cfp = new CausalFootprintSimilarity();
		double similarity = cfp.similarity(pn, pn2);
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = end - start;
		System.out.println(duration);
//		System.out.println("BehaviorProfile:" + similarity);
		System.out.println("CausalFootprintSimilarity:" + similarity);
	}

}
