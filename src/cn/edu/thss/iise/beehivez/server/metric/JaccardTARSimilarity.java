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

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.*;
/*
 * ����Jaccardϵ��ģ�TAR����Petri��ģ����Ϊ�����Զ�
 * @author zhp
 */
public class JaccardTARSimilarity extends PetriNetSimilarity {

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "Jaccard similarity based on the TAR set";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "JaccardTARSimilarity";
	}
	
	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		// TODO Auto-generated method stub
		return getSimilarityValue(MyPetriNet.fromProMPetriToMyPetri(pn1),MyPetriNet.fromProMPetriToMyPetri(pn2));
	}
	
	// �����㷨��ֵ�����
	public float getSimilarityValue(MyPetriNet petri1, MyPetriNet petri2) {
		if(petri1==null|| petri2==null){
			return -1;
		}
		
		// ���TAR����
		MyTransitionAdjacentRelationSet tarSet1 = new MyTransitionAdjacentRelationSet(petri1);
		MyTransitionAdjacentRelationSet tarSet2 = new MyTransitionAdjacentRelationSet(petri2);
		
		return getSimilarityValue(tarSet1,tarSet2);
	}
	
	// �����㷨��ֵ�����
	//�������Ϊ}��Tar��
	public float getSimilarityValue(MyTransitionAdjacentRelationSet tarSet1, MyTransitionAdjacentRelationSet tarSet2) {
		if(tarSet1==null|| tarSet2==null){
			return -1;
		}
		
		// ����TAR���϶�
		// �ҽ���
		MyTransitionAdjacentRelationSet commonSet = new MyTransitionAdjacentRelationSet();
		MyTransitionAdjacentRelation r1, r2;
		for (int i = 0; i < tarSet1.tarSet.size(); i++) {
			r1 = tarSet1.tarSet.get(i);
			for (int j = 0; j < tarSet2.tarSet.size(); j++) {
				r2 = tarSet2.tarSet.get(j);
				if (r1.transitionA.equals(r2.transitionA)
						&& r1.transitionB.equals(r2.transitionB)) {
					commonSet.addTAR(r1);
					break;
				}
			}
		}

		float f = commonSet.tarSet.size() * commonSet.tarSet.size();
		if(tarSet1.tarSet.size()==0||tarSet2.tarSet.size()==0){
			f = f / (1+(tarSet1.tarSet.size() * tarSet2.tarSet.size()));
		}else
		{f = f / (tarSet1.tarSet.size() * tarSet2.tarSet.size());}
		
		return f;
	}
	public static void main(String[] args){
		PetriNetSimilarity sim = new JaccardTARSimilarity();
		//String file1="C:/QueryModel/东锅PNML-114完全版-20100520/FI.404.pnml";
		String file1="C:/Documents and Settings/Administrator/桌面/alpha#miningmodel/FI.404.pnml";
		//String file2=System.getProperty("user.dir","")+"/Invisible2m.pnml";
		FileInputStream is1 = null;
		FileInputStream is2 = null;
		PetriNet pn1 =null;
		PetriNet pn2 =null;
		PnmlImport input = new PnmlImport();
		try {
			is1 = new FileInputStream(file1);
			//is2 = new FileInputStream(file2);		
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
			//pn2 = input.read(is2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		float res = sim.similarity(pn1, pn2);
//		System.out.println(res);
		
		MyTransitionAdjacentRelationSet tarSet1 = new MyTransitionAdjacentRelationSet(MyPetriNet.fromProMPetriToMyPetri(pn1));
		System.out.println(tarSet1);
	}


}
