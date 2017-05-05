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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.processmining.framework.models.petrinet.*;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
/*
 * ����Jaccardϵ�����ģ�ͽṹ�����Զ�
 * @author zhp
 */
public class JaccardStructureSimilarity extends PetriNetSimilarity {

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "structure based process similarity metric using Jaccard coefficient";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "JaccardStructureSimilarity";
	}

	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		
		if(pn1==null || pn2==null) return -1;   //�쳣���
		// TODO Auto-generated method stub
		int cap=0; //ģ��Ԫ�صĽ���ģ
		int cup=1; //ģ��Ԫ�صĲ���ģ
		
		cap=calCap(pn1, pn2);
		
		cup=calCup(pn1, pn2);
		
		return (cap*(float)1.0)/cup;
	}
	
	private int calCap(PetriNet pn1, PetriNet pn2){
		int cap=0;
		
		 ArrayList<Transition> transitions1, transitions2; /* the list of transition nodes */
		  ArrayList<Place> places1,places2; /* the list of place nodes */
		  ArrayList<PNEdge> edges1, edges2; /* the list of place nodes */
		  
		  transitions1=pn1.getTransitions();
		  places1=pn1.getPlaces();
		  edges1=pn1.getEdges();
		  
		  transitions2=pn2.getTransitions();
		  places2=pn2.getPlaces();
		  edges2=pn2.getEdges();		  
		  
		  //��ǨԪ��		  
		  for(int i=0;i<transitions1.size();i++){
			  
			  Transition t=transitions1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<transitions1.size();j++){
				  if(t.getIdentifier().equals(transitions1.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
	  			  
			  for(int j=0;j<transitions2.size();j++){
				  if(t.getIdentifier().equals(transitions2.get(j).getIdentifier())){
					  cap++;
					  break;
				  }				  
			  }
		  }
		  
		  //����Ԫ��		  
		  for(int i=0;i<places1.size();i++){
			  
			  Place p=places1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<places1.size();j++){
				  if(p.getIdentifier().equals(places1.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
	  			  
			  for(int j=0;j<places2.size();j++){
				  if(p.getIdentifier().equals(places2.get(j).getIdentifier())){
					  cap++;
					  break;
				  }				  
			  }
		  }
		  
		  //l�ỡԪ��		  
		  for(int i=0;i<edges1.size();i++){
			  
			  PNEdge e=edges1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<edges1.size();j++){
				  if(e.getSource().getIdentifier().equals(edges1.get(j).getSource().getIdentifier())&&
						  e.getDest().getIdentifier().equals(edges1.get(j).getDest().getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
	  			  
			  for(int j=0;j<edges2.size();j++){
				  if(e.getSource().getIdentifier().equals(edges2.get(j).getSource().getIdentifier())&&
						  e.getDest().getIdentifier().equals(edges2.get(j).getDest().getIdentifier())){
					  cap++;
					  break;
				  }				  
			  }
		  }	
		
		return cap;
	}
	
	private int calCup(PetriNet pn1, PetriNet pn2){
		int cup=0;
		
		 ArrayList<Transition> transitions1, transitions2; /* the list of transition nodes */
		  ArrayList<Place> places1,places2; /* the list of place nodes */
		  ArrayList<PNEdge> edges1, edges2; /* the list of place nodes */
		  
		  transitions1=pn1.getTransitions();
		  places1=pn1.getPlaces();
		  edges1=pn1.getEdges();
		  
		  transitions2=pn2.getTransitions();
		  places2=pn2.getPlaces();
		  edges2=pn2.getEdges();		  
		  
		  //��ǨԪ��	
		  //����һ
		  for(int i=0;i<transitions1.size();i++){
			  
			  Transition t=transitions1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<transitions1.size();j++){
				  if(t.getIdentifier().equals(transitions1.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  //����Ƿ�Է�����ͬԪ��
			  boolean found=false;
			  for(int j=0;j<transitions2.size();j++){
				  if(t.getIdentifier().equals(transitions2.get(j).getIdentifier())){
					  found=true;
					  break;
				  }				  
			  }
			  if(found==false) cup++;
		  }
		  //�����
		  for(int i=0;i<transitions2.size();i++){
			  
			  Transition t=transitions2.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<transitions2.size();j++){
				  if(t.getIdentifier().equals(transitions2.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  cup++;
		  }
		  
		  
		  //����Ԫ��		  
		  //����һ
		  for(int i=0;i<places1.size();i++){
			  
			  Place p=places1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<places1.size();j++){
				  if(p.getIdentifier().equals(places1.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  //����Ƿ�Է�����ͬԪ��
			  boolean found=false;
			  for(int j=0;j<places2.size();j++){
				  if(p.getIdentifier().equals(places2.get(j).getIdentifier())){
					  found=true;
					  break;
				  }				  
			  }
			  if(found==false) cup++;
		  }
		  //�����
		  for(int i=0;i<places2.size();i++){
			  
			  Place p=places2.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<places2.size();j++){
				  if(p.getIdentifier().equals(places2.get(j).getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  cup++;
		  }
		  
		  //l�ỡԪ��	
		  //����һ
		  for(int i=0;i<edges1.size();i++){
			  
			  PNEdge e=edges1.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<edges1.size();j++){
				  if(e.getSource().getIdentifier().equals(edges1.get(j).getSource().getIdentifier())&&
						  e.getDest().getIdentifier().equals(edges1.get(j).getDest().getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  //����Ƿ�Է�����ͬԪ��
			  boolean found=false;
			  for(int j=0;j<edges2.size();j++){
				  if(e.getSource().getIdentifier().equals(edges2.get(j).getSource().getIdentifier())&&
						  e.getDest().getIdentifier().equals(edges2.get(j).getDest().getIdentifier())){
					  found=true;
					  break;
				  }				  
			  }
			  if(found==false) cup++;
		  }
		  //�����
		  for(int i=0;i<edges2.size();i++){
			  
			  PNEdge e=edges2.get(i);			  
			  //������������ظ�Ԫ��
			  boolean duplicated=false;
			  for(int j=i+1;j<edges2.size();j++){
				  if(e.getSource().getIdentifier().equals(edges2.get(j).getSource().getIdentifier())&&
						  e.getDest().getIdentifier().equals(edges2.get(j).getDest().getIdentifier())){
					  duplicated=true;
					  break;
				  }				  
			  }
			  
			  if(duplicated==true) continue;
			  cup++;
		  }
		
		return cup;
	}
	public static void main(String[] args){
		PetriNetSimilarity sim = new JaccardStructureSimilarity();
		String file1="C:/QueryModel/Simple Sequence/Simsequence1.pnml";
		String file2="E:/workspace/BeehiveZ-new/miningmodel/Simsequence1.pnml";
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
}
