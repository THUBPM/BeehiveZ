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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.index.bplustree.BplusTreeLong2Long.BplusNode.Delete;
import cn.edu.thss.iise.beehivez.server.metric.tar.ComputeSemanticSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.tar.ComputeSimilarity;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-11-16
 */
public class ComputeExtensiveTARSimilarity {

	private ONTransitionAdjacentRelation tar1 = null;
	private ONTransitionAdjacentRelation tar2 = null;
	private ONCompleteFinitePrefix cfp1 = null;
	private ONCompleteFinitePrefix cfp2 = null;
	
//	private Boolean available[][] = null;
//	private double labelSimilarity[][] = null;
//	private Vector<String> labelSet1;
//	private Vector<String> labelSet2;
//	private HashMap<String, String> relativelyEqualLabel;
//	private HashMap<HashMap<String, String>, HashMap<String, String>> equalTAR;
	
	private double finalSimilarity = 0;
	private double tarSimilarity = 0;
	private double tar0Similarity = 0;
	private double tarReSimilarity = 0; 
	private double tarImSimilarity = 0;

	
//	public Vector<String> getLabelSet(ONCompleteFinitePrefix cfp){
//		Vector<String> labelSet = new Vector<String>();
//		
//		return labelSet;
//	}
	
//	public double[][] computLabelSetSimilarity(){
//		
//		return labelSimilarity;
//	}
	
//	public HashMap<String, String> getRelativelyEqualLabel(){
//		
//		return relativelyEqualLabel;
//	}
	public ComputeExtensiveTARSimilarity(PetriNet pn1, PetriNet pn2){
		MyPetriNet input1 = null;
		MyPetriNet input2 = null;
		input1=MyPetriNet.PromPN2MyPN(pn1);
		input2=MyPetriNet.PromPN2MyPN(pn2);
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input1);
		cfp1 = cfpBuilder.Build();
		
		ONCompleteFinitePrefixBuilder cfpBuilder2 = new ONCompleteFinitePrefixBuilder(input2);
		cfp2 = cfpBuilder2.Build();
		
		tar1 = new ONTransitionAdjacentRelation(cfp1);
		tar2 = new ONTransitionAdjacentRelation(cfp2);
	}
	
	public ComputeExtensiveTARSimilarity(MyPetriNet pn1, MyPetriNet pn2){
		super();
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(pn1);
		cfp1 = cfpBuilder.Build();
		
		ONCompleteFinitePrefixBuilder cfpBuilder2 = new ONCompleteFinitePrefixBuilder(pn2);
		cfp2 = cfpBuilder2.Build();
		
		tar1 = new ONTransitionAdjacentRelation(cfp1);
		tar2 = new ONTransitionAdjacentRelation(cfp2);
	}
	
	/**
	 * @param cfp1
	 * @param cfp2
	 */
	public ComputeExtensiveTARSimilarity(ONCompleteFinitePrefix cfp1,
			ONCompleteFinitePrefix cfp2) {
		super();
		this.cfp1 = cfp1;
		this.cfp2 = cfp2;
		tar1 = new ONTransitionAdjacentRelation(cfp1);
		tar2 = new ONTransitionAdjacentRelation(cfp2);
	}

	public double computeExtenSiveTARSimilarity(){
		HashMap<String, HashSet<String>> allTARS = new HashMap<String, HashSet<String>>();
		double same;
		double all;
		
		//for tar
		double tarCoefficient = 0;
		HashMap<String, HashSet<String>> allSameTar = new HashMap<String, HashSet<String>>(tar1.getTar());
		HashMap<String, HashSet<String>> allTar = new HashMap<String, HashSet<String>>(tar2.getTar());
		for(String label : tar1.getTar().keySet()){
			if(tar2.getTar().keySet().contains(label)){
				allSameTar.get(label).retainAll(tar2.getTar().get(label));
				allTar.get(label).addAll(tar1.getTar().get(label));
			}
			else{
				allSameTar.remove(label);
				allTar.put(label, tar1.getTar().get(label));
			}
		}
		
		same = 0;
		all = 0;
		for(String label : allSameTar.keySet()){
			same += allSameTar.get(label).size();
		}
		for(String label : allTar.keySet()){
			all += allTar.get(label).size();
		}
		if(all == 0){
			tarSimilarity = 0;
		}
		else{
			tarSimilarity = same / all;
		}
		tarCoefficient = all;
		
		//for tar0
		double tar0Coefficient = 0;
		HashMap<String, HashSet<String>> allSameTar0 = new HashMap<String, HashSet<String>>(tar1.getTar0());
		HashMap<String, HashSet<String>> allTar0 = new HashMap<String, HashSet<String>>(tar2.getTar0());
		for(String label : tar1.getTar0().keySet()){
			if(tar2.getTar0().keySet().contains(label)){
				allSameTar0.get(label).retainAll(tar2.getTar0().get(label));
				allTar0.get(label).addAll(tar1.getTar0().get(label));
			}
			else{
				allSameTar0.remove(label);
				allTar0.put(label, tar1.getTar0().get(label));
			}
		}
		
		same = 0;
		all = 0;
		for(String label : allSameTar0.keySet()){
			same += allSameTar0.get(label).size();
		}
		for(String label : allTar0.keySet()){
			all += allTar0.get(label).size();
		}
		if(all == 0){
			tar0Similarity = 0;
		}
		else{
			tar0Similarity = same / all;
		}
		tar0Coefficient = all;
		
		//for tar'
		double tarReCoefficient = 0;
		HashMap<String, HashSet<String>> allSameTarRe = new HashMap<String, HashSet<String>>(tar1.getTarRe());
		HashMap<String, HashSet<String>> allTarRe = new HashMap<String, HashSet<String>>(tar2.getTarRe());
		for(String label : tar1.getTarRe().keySet()){
			if(tar2.getTarRe().keySet().contains(label)){
				allSameTarRe.get(label).retainAll(tar2.getTarRe().get(label));
				allTarRe.get(label).addAll(tar1.getTarRe().get(label));
			}
			else{
				allSameTarRe.remove(label);
				allTarRe.put(label, tar1.getTarRe().get(label));
			}
		}
		
		same = 0;
		all = 0;
		for(String label : allSameTarRe.keySet()){
			same += allSameTarRe.get(label).size();
		}
		for(String label : allTarRe.keySet()){
			all += allTarRe.get(label).size();
		}
		if(all == 0){
			tarReSimilarity = 0;
		}
		else{
			tarReSimilarity = same / all;
		}
		tarReCoefficient = all;
		
		//for tar+
		double tarImCoefficient = 0;
		HashMap<String, HashSet<String>> allSameTarIm = new HashMap<String, HashSet<String>>(tar1.getTarIm());
		HashMap<String, HashSet<String>> allTarIm = new HashMap<String, HashSet<String>>(tar2.getTarIm());
		for(String label : tar1.getTarIm().keySet()){
			if(tar2.getTarIm().keySet().contains(label)){
				allSameTarIm.get(label).retainAll(tar2.getTarIm().get(label));
				allTarIm.get(label).addAll(tar1.getTarIm().get(label));
			}
			else{
				allSameTarIm.remove(label);
				allTarIm.put(label, tar1.getTarIm().get(label));
			}
		}
		
		same = 0;
		all = 0;
		for(String label : allSameTarIm.keySet()){
			same += allSameTarIm.get(label).size();
		}
		for(String label : allTarIm.keySet()){
			all += allTarIm.get(label).size();
		}
		if(all == 0){
			tarImSimilarity = 0;
		}
		else{
			tarImSimilarity = same / all;
		}
		tarImCoefficient = all;
		
		all = 0;
		all = tarCoefficient + tar0Coefficient + tarReCoefficient + tarImCoefficient;
		if(all == 0){
			return 0;
		}
		
		tarCoefficient /= all;
		tar0Coefficient /= all;
		tarReCoefficient /= all;
		tarImCoefficient /= all;
		
		finalSimilarity = tarCoefficient * tarSimilarity + tar0Coefficient * tar0Similarity 
						+ tarReCoefficient * tarReSimilarity + tarImCoefficient * tarImSimilarity;
		
		return finalSimilarity; 
	}
	
	/**
	 * 2010-11-16
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		MyPetriNet input2 = null;
		FileInputStream fin = null;
		FileInputStream fin2 = null;
		try {
			fin = new FileInputStream("experiment_1\\N4.xml");
			fin2 = new FileInputStream("experiment_1\\N5.xml");
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
		input=MyPetriNet.PromPN2MyPN(pn);
		input2=MyPetriNet.PromPN2MyPN(pn2);
		
		
		long start = System.currentTimeMillis();
		System.out.println(start);
//		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
//		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
//		ONTransitionAdjacentRelation tar = new ONTransitionAdjacentRelation(cfp);
		
//		ONCompleteFinitePrefixBuilder cfpBuilder2 = new ONCompleteFinitePrefixBuilder(input2);
//		ONCompleteFinitePrefix cfp2 = cfpBuilder2.Build();
//		ONTransitionAdjacentRelation tar2 = new ONTransitionAdjacentRelation(cfp2);
		
//		ComputeSimilarity cs = new ComputeSimilarity(pn, pn2);
		ComputeSemanticSimilarity css = new ComputeSemanticSimilarity(pn, pn2);
//		ComputeExtensiveTARSimilarity compute = new ComputeExtensiveTARSimilarity(input, input2);
		double similarity = css.compute();
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = end - start;
		System.out.println(duration);
//		System.out.println("TAR :" + compute.getTarSimilarity());
		System.out.println("TAR+:" + similarity);
//		cfpBuilder.cfp.getOn().ONToMPN().export_pnml("C:\\Users\\winever\\Documents\\model\\Unfolding_mcm10.pnml");
	}

	/**
	 * @return the tarSimilarity
	 */
	public double getTarSimilarity() {
		return tarSimilarity;
	}

}
