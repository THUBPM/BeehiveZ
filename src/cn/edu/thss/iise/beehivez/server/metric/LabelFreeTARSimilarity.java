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

import java.util.*;

import org.processmining.framework.models.petrinet.PetriNet;
import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.*;
/*
 * ����TAR�����Զ����������ʶ�޹صĹ�������Զ���
 * @author zhp
 */
public class LabelFreeTARSimilarity extends PetriNetSimilarity {

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "Label free similarity based on the Jaccard TAR similarity";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "LabelFreeTARSimilarity";
	}

	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		// TODO Auto-generated method stub
		return getSimilarityMeasureResult(MyPetriNet.fromProMPetriToMyPetri(pn1),MyPetriNet.fromProMPetriToMyPetri(pn2));
	}

	// ����Ĺ������鳤�ȣ���Լ�ɴ�������ģ�͹�ģ
	private int MAXSIZEOFTRANSITIONSET = 100;
	//	
	// TAR����
	private MyTransitionAdjacentRelationSet tarSet1 = null;
	private MyTransitionAdjacentRelationSet tarSet2 = null;

	private Vector<MyPetriTransition> transitionSet1 = null;
	private Vector<MyPetriTransition> transitionSet2 = null;

	// ��Ǩ����
	private int sizeofTransitionSet1 = 0;
	private int sizeofTransitionSet2 = 0;
	// sizeofTransitionSet1��sizeofTransitionSet2�����е���С�ߺ������
	private int sizeofTransitionMin = 0;
	private int sizeofTransitionMax = 0;

	// ������ƶ�
	private float maxSimilarity = 0;
	// ���ƶ����ʱ��ӳ�䷽��
	private Vector<MyPetriTransition> maxMappingSet = null;

	// �㷨����е�������Ǩ�ݴ���
	private MyPetriTransition[] sourceSetArray = null;
	private MyPetriTransition[] workingSetArray = null;

	public float getSimilarityMeasureResult(MyPetriNet petri1, MyPetriNet petri2) {

		if (petri1 == null || petri2 == null) {
			return -1;
		}
		// ���TAR����
		tarSet1 = new MyTransitionAdjacentRelationSet(petri1);
		tarSet2 = new MyTransitionAdjacentRelationSet(petri2);

		// ��ȡ��Ǩ�б�
		transitionSet1 = petri1.getTransitionSet();
		transitionSet2 = petri2.getTransitionSet();

		sizeofTransitionSet1 = transitionSet1.size();
		sizeofTransitionSet2 = transitionSet2.size();

		// ��ʼ����������
		maxMappingSet = new Vector<MyPetriTransition>();
		workingSetArray = new MyPetriTransition[MAXSIZEOFTRANSITIONSET];
		sourceSetArray = new MyPetriTransition[MAXSIZEOFTRANSITIONSET];

		Vector<MyPetriTransition> sourceSet = null;
		if (sizeofTransitionSet1 > sizeofTransitionSet2) {
			sizeofTransitionMin = sizeofTransitionSet2;
			sizeofTransitionMax = sizeofTransitionSet1;
			sourceSet = transitionSet1;
		} else {
			sizeofTransitionMin = sizeofTransitionSet1;
			sizeofTransitionMax = sizeofTransitionSet2;
			sourceSet = transitionSet2;
		}
		// ���Դ��������
		for (int i = 0; i < sourceSet.size(); i++) {
			sourceSetArray[i] = sourceSet.get(i);
		}

		maxSimilarity = 0;

		// �������ƶȣ��ݹ��㷨
		calUnlabeledSimilarity(0);
		// ���ؽ��

		return maxSimilarity;

	}

	// �Ǳ�������Զ���TAR�ݹ��㷨
	// ���������ʽ
	private void calUnlabeledSimilarity(int ordinal) {
		// ����Ѿ��ҵ����ŷ�������ֹ����
		if (maxSimilarity == 1.0)
			return;

		// ����ӳ��
		if (ordinal < sizeofTransitionMin) {
			for (int i = 0; i < sizeofTransitionMax; i++) {
				// ӳ�䵽һ��δӳ���ı�Ǩ
				MyPetriTransition t = (MyPetriTransition) sourceSetArray[i];
				if (t == null)
					continue;

				workingSetArray[ordinal] = t;
				sourceSetArray[i] = null;

				// �ݹ�
				calUnlabeledSimilarity(ordinal + 1);

				// �ָ��ݹ�ǰ��ֵ
				sourceSetArray[i] = t;
			}
		} else { // ���ӳ�䣬�������ƶ�

			Vector<MyTransitionAdjacentRelation> newSet1;
			Vector<MyTransitionAdjacentRelation> newSet2;
			Vector<MyPetriTransition> targetTranSet;
			// Set2--->Set1
			if (sizeofTransitionSet1 > sizeofTransitionSet2) {
				targetTranSet=transitionSet2;
				newSet1 = copyTarList(tarSet2.tarSet);
				newSet2 = copyTarList(tarSet1.tarSet);
			} else // Set1--->Set2
			{
				targetTranSet=transitionSet1;
				newSet1 = copyTarList(tarSet1.tarSet);
				newSet2 = copyTarList(tarSet2.tarSet);
			}
			// ��newSet1����ӳ�����
			for (int i = 0; i < targetTranSet.size(); i++) {
				for (int j = 0; j < newSet1.size(); j++) {
					if (((MyTransitionAdjacentRelation) newSet1.get(j)).transitionA
							.equals(((MyPetriTransition) (targetTranSet.get(i)))
									.getname())) {
						((MyTransitionAdjacentRelation) newSet1.get(j)).transitionA = new String(
								workingSetArray[i].getname());
					}

					if (((MyTransitionAdjacentRelation) newSet1.get(j)).transitionB
							.equals(((MyPetriTransition) (targetTranSet.get(i)))
									.getname())) {
						((MyTransitionAdjacentRelation) newSet1.get(j)).transitionB = new String(
								workingSetArray[i].getname());
					}
				}
			}

			// ͨ��newSet1��newSet2����labeled TAR�㷨�������ƶ�
			float sim = getSimilarityValue(newSet1, newSet2);

			// ������С���ƶ�ʱ��ӳ�䷽��
			if (sim > maxSimilarity) {
				maxSimilarity = sim;
				maxMappingSet.clear();
				for (int i = 0; i < sizeofTransitionMin; i++) {
					maxMappingSet.add(i, workingSetArray[i]);
				}
			}
		}

	}

	// ����TAR�б�
	private Vector<MyTransitionAdjacentRelation> copyTarList(
			Vector<MyTransitionAdjacentRelation> tarList) {
		Vector<MyTransitionAdjacentRelation> newTarList = new Vector<MyTransitionAdjacentRelation>();
		MyTransitionAdjacentRelation tar;
		for (int i = 0; i < tarList.size(); i++) {
			tar = new MyTransitionAdjacentRelation();
			tar.transitionA = new String(tarList.get(i).transitionA);
			tar.transitionB = new String(tarList.get(i).transitionB);

			newTarList.add(tar);
		}

		return newTarList;
	}
	
	// �����㷨��ֵ�������
	//�������Ϊ����Tar�б�
	private float getSimilarityValue(Vector<MyTransitionAdjacentRelation> tarList1,Vector<MyTransitionAdjacentRelation> tarList2) {
		if(tarList1==null|| tarList2==null){
			return -1;
		}
		
		// ����TAR���϶���
		// �ҽ���
		Vector<MyTransitionAdjacentRelation> commonList = new Vector<MyTransitionAdjacentRelation>();
		MyTransitionAdjacentRelation r1, r2;
		for (int i = 0; i < tarList1.size(); i++) {
			r1 = tarList1.get(i);
			for (int j = 0; j < tarList2.size(); j++) {
				r2 = tarList2.get(j);
				if (r1.transitionA.equals(r2.transitionA)
						&& r1.transitionB.equals(r2.transitionB)) {
					commonList.add(r1);
					break;
				}
			}
		}

		float f = commonList.size() * commonList.size();
		f = f / (tarList1.size() * tarList2.size());
		
		return f;
	}
}
