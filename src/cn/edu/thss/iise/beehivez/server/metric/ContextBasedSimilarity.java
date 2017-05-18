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
package cn.edu.thss.iise.beehivez.server.metric;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
/**
 * 
 * @author Nianhua Wu
 * 
 * 
 *
 */
public class ContextBasedSimilarity extends PetriNetSimilarity {

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "Calculate two petrinet Similarity based on the context similarity of each node";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Context based Similarity";
	}
	private boolean PlaceSimilarity(Place p1, Place p2) {
		
		if(p1.inDegree() != p2.inDegree()||p1.outDegree() != p2.outDegree())
			return false;
		boolean hasValue = true;
		Iterator p2_Predecessors = p2.getPredecessors().iterator();
		while(p2_Predecessors.hasNext())
		{
			hasValue = false;
			Transition t2 = (Transition) p2_Predecessors.next();
			Iterator p1_Predecessors = p1.getPredecessors().iterator();
			while(p1_Predecessors.hasNext())
			{
				Transition t1 = (Transition) p1_Predecessors.next();
				if(t1.getIdentifier().equals(t2.getIdentifier()) || t1.isInvisibleTask() && t2.isInvisibleTask())
				{
					hasValue = true;
					break;
				}
				
			}
			if(!hasValue)
				return false;
		}
		Iterator p2_Successors = p2.getSuccessors().iterator();
		while(p2_Successors.hasNext())
		{
			hasValue = false;
			Transition t2 = (Transition) p2_Successors.next();
			Iterator p1_Successors = p1.getSuccessors().iterator();
			while(p1_Successors.hasNext())
			{
				Transition t1 = (Transition) p1_Successors.next();
				if(t1.getIdentifier().equals(t2.getIdentifier()) || t1.isInvisibleTask() && t2.isInvisibleTask())
				{
					hasValue = true;
					break;
				}
			}
			if(!hasValue)
				return false;
		}
		return true;		
	}

	private boolean TransitionSimilarity(Transition t1, Transition t2) {
		
		if(!t1.getIdentifier().equals(t2.getIdentifier()) && !(t1.isInvisibleTask() && t2.isInvisibleTask()))
			return false;	
		if(t1.inDegree() != t2.inDegree()||t1.outDegree() != t2.outDegree())
			return false;
		boolean hasValue = true;
		Iterator t2_Predecessors = t2.getPredecessors().iterator();	
		while(t2_Predecessors.hasNext())
		{
			Place p2 = (Place) t2_Predecessors.next();
			hasValue = false;
			Iterator t1_Predecessors = t1.getPredecessors().iterator();
			while(t1_Predecessors.hasNext())
			{
				Place p1 = (Place) t1_Predecessors.next();
				if(PlaceSimilarity(p1,p2))
				{
					hasValue = true;
					break;
				}
			}
			if(!hasValue)
			{
				return false;
			}
		}
		Iterator t2_Successors= t2.getSuccessors().iterator();	
		while(t2_Successors.hasNext())
		{
			Place p2 = (Place) t2_Successors.next();
			hasValue = false;
			Iterator t1_Successors = t1.getSuccessors().iterator();
			while(t1_Successors.hasNext())
			{
				Place p1 = (Place) t1_Successors.next();
				if(PlaceSimilarity(p1,p2))
				{
					hasValue = true;
					break;
				}
			}
			if(!hasValue)
			{
				return false;
			}
		}
		return true;
	}
	

	
	public float similarity(PetriNet pn1, PetriNet pn2) {
		
		float result = 0;
		Iterator place2 = pn2.getPlaces().iterator();
		while(place2.hasNext())
		{
			Place p2 = (Place) place2.next();
			Iterator place1 = pn1.getPlaces().iterator();
			while(place1.hasNext())
			{
				Place p1 = (Place) place1.next();
				if(PlaceSimilarity(p1,p2))
				{
					result++;
					break;
				}				
			}
		}
		Iterator transition2 = pn2.getTransitions().iterator();
		while(transition2.hasNext())
		{
			Transition t2 = (Transition) transition2.next();
			Iterator transition1 = pn1.getTransitions().iterator();
			while(transition1.hasNext())
			{
				Transition t1 = (Transition) transition1.next();
				if(TransitionSimilarity(t1,t2))
				{
					result++;
					break;
				}				
			}
		}
		
		int total = pn2.getTransitions().size()+ pn2.getPlaces().size();
		total += pn1.getTransitions().size()+ pn1.getPlaces().size();
		total -= result;
		result = result/total;
		
		return result;		
	}
	public static void main(String[] args){
		PetriNetSimilarity sim = new ContextBasedSimilarity();
		//String file1="C:/QueryModel/Duplicate Task/Dup4.pnml";
		String file1="C:/QueryModel/invisible task/Invisible2.pnml";
		String file2="F:/workspace/BeehiveZ2_new/miningmodel/Invisible2.pnml";
		FileInputStream is1 = null;
		FileInputStream is2 = null;
		PetriNet pn1 =null;
		PetriNet pn2 =null;
		PnmlImport input = new PnmlImport();
		try {
			is1 = new FileInputStream(file1);
			is2 = new FileInputStream(file2);		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pn1 = input.read(is1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pn2 = input.read(is2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float res = sim.similarity(pn1, pn2);
		System.out.println(res);

	}

//	public static void main(String[] args){
//		PetriNetSimilarity sim = new ContextBasedSimilarity();
//		FileInputStream is1 = null;
//		FileInputStream is2 = null;
//		PetriNet pn1 =null;
//		PetriNet pn2 =null;
//		PnmlImport input = new PnmlImport();
//		
//		String resultFile = System.getProperty("user.dir", "") + "/contextResult.xls";
//		File resultfile = new File(resultFile);
//		if (!resultfile.exists())
//			try {
//				resultfile.createNewFile();
//			} catch (IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
//		FileWriter fw = null;
//		BufferedWriter bw = null;
//		try {
//			fw = new FileWriter(resultFile);
//			bw = new BufferedWriter(fw);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			bw.write("FileName", 0, 8);
//			bw.write("\t");
//			bw.write("Context", 0, 7);
//			bw.newLine();
//			bw.flush();
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}     
//		//Simple Sequence ,Simple Selection,Simple Parallel
//		//Duplicate Task ,Invisible Task ,Non-free Choice ,Short Loop ,
//		File modelsFile = new File("C:/QueryModel/Non-free Choice");
//		File minedFile = new File("C:/minedmodel/nonfreechoice/nonfree-DGA");
//		File[] file1=modelsFile.listFiles();//原模型
//		File[] file2=minedFile.listFiles();//原模型
//		for(int i=0;i<file1.length;i++){
//			for(int j=0;j<file2.length;j++){
//				if(file2[j].getName().equals(file1[i].getName())){//找到两个相同的文件
//					try {
//						is1 = new FileInputStream(file1[i]);
//						is2 = new FileInputStream(file2[j]);		
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					try {
//						pn1 = input.read(is1);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					try {
//						pn2 = input.read(is2);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					float res = sim.similarity(pn1, pn2);
//					//System.out.println(res);
//					Float ress = (float) (((int) (res * 100)) / 100.0);
//					//Float ress=res;
//					try {
//						bw.write(file1[i].getName(), 0, file1[i].getName().length());
//						bw.write("\t");
//						bw.write(ress.toString(), 0, ress.toString().length());
//						bw.newLine();
//						bw.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		}
//		try {
//			bw.close();
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.print("finished!");
//
//	}

	
}
