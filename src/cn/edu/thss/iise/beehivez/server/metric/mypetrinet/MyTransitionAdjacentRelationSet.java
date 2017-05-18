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
/*
 * MyTransitionAdjacentRelationSet.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

import java.io.*;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;

/**
 * Transition Adjacent Relation Set Object
 * 
 * Petri���Ǩ�ڽӹ�ϵ��������
 * 
 * @author zhp
 * 
 */
public class MyTransitionAdjacentRelationSet implements Cloneable {

	public Vector<MyTransitionAdjacentRelation> tarSet;

	private MyPetriNet petri;
	private MyPetriPlace markedplace;
	private MyReachMarkingGraph rmg;

	// /**
	// *ͨ�����л��������ƶ���
	// *
	// * @return ���Ƶ�Tar����
	// *
	// */
	// public MyTransitionAdjacentRelationSet
	// cloneTAR(MyTransitionAdjacentRelationSet tarSet){
	//    	
	// MyTransitionAdjacentRelationSet newtar=null;
	//    	
	// try{
	// ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	// ObjectOutputStream out = new ObjectOutputStream(byteOut);
	// out.writeObject(tarSet);
	//        
	// ByteArrayInputStream byteIn = new
	// ByteArrayInputStream(byteOut.toByteArray());
	// ObjectInputStream in =new ObjectInputStream(byteIn);
	//    	
	// newtar= (MyTransitionAdjacentRelationSet)in.readObject();
	// }
	// catch(Exception e){
	// System.out.println("����TAR��������쳣");
	// System.out.print(e.getStackTrace());
	// }
	// return newtar;
	// }

	public MyReachMarkingGraph getRmg() {
		return rmg;
	}
	public Object clone() {
		MyTransitionAdjacentRelationSet obj = null;
		try {
			obj = (MyTransitionAdjacentRelationSet) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Constructor
	 * 
	 * @param petri
	 *            The MyPetriNet to be dealt with
	 * @param markedplace
	 *            The marked place.
	 */
	public MyTransitionAdjacentRelationSet(MyPetriNet petri,
			MyPetriPlace markedplace) {
		tarSet = new Vector<MyTransitionAdjacentRelation>();
		this.petri = petri;
		this.markedplace = markedplace;
		// ����TAR����
		bulidTARSet();
	}

	/**
	 * Default Constructor
	 */
	// Ĭ�Ϲ��캯����һ��յ�TAR������
	public MyTransitionAdjacentRelationSet() {
		tarSet = new Vector<MyTransitionAdjacentRelation>();
		this.petri = null;
		this.markedplace = null;
	}

	/**
	 * Constructor, the default marked place is the Source Place
	 * 
	 * @param petri
	 *            The MyPetriNet to be dealt with.
	 */

	// Ĭ��Դ����Ϊ��ǿ���
	public MyTransitionAdjacentRelationSet(MyPetriNet petri) {
		// Ĭ��Դ����Ϊ��ǿ���
		int mp = petri.getSourcePlace();
		this.markedplace = (MyPetriPlace) (petri.getPetriObject(mp));

		this.petri = petri;

		tarSet = new Vector<MyTransitionAdjacentRelation>();
		// ����TAR����
		bulidTARSet();

	}
	
	//for experiment_2
	public MyTransitionAdjacentRelationSet(PetriNet pn) {
		// 默认源库所为标记库所
		MyPetriNet petri = MyPetriNet.fromProMPetriToMyPetri(pn);
		int mp = petri.getSourcePlace();
		this.markedplace = (MyPetriPlace) (petri.getPetriObject(mp));

		this.petri = petri;

		tarSet = new Vector<MyTransitionAdjacentRelation>();
		// 构造TAR集合
		bulidMyTARSet();

	}
	
	//for experiment_2
	public void bulidMyTARSet() {
		// 清空当前所有TAR集合
		tarSet.removeAllElements();
		// 构造可达图
		rmg = new MyReachMarkingGraph(petri, markedplace);

		// 扫描可达图得到二元关系矩阵
		// 状态节点的任一入弧和任意出弧构成关系对
		MyReachMarkingGraph.RMGObject g;

		for (int i = 0; i < rmg.reachmarkinggraph.size(); i++) {
			g = rmg.reachmarkinggraph.get(i);
			if (g.isA() == MyReachMarkingGraph.RMGObject.MARKINGSTATE) {
				// 找出每个标识状态的所有入度，出度
				Vector<String> inputarc = new Vector<String>();
				Vector<String> outputarc = new Vector<String>();
				MyReachMarkingGraph.RMGObject r;
				for (int j = 0; j < rmg.reachmarkinggraph.size(); j++) {
					r = rmg.reachmarkinggraph.get(j);
					if (r.isA() == MyReachMarkingGraph.RMGObject.ARC) {
						if (((MyReachMarkingGraph.RMGArc) r).getTo().equals(
								((MyReachMarkingGraph.RMGMarkingState) g)
										.getstate())) {
							inputarc.add(((MyReachMarkingGraph.RMGArc) r)
									.getName());
						}
						if (((MyReachMarkingGraph.RMGArc) r).getFrom().equals(
								((MyReachMarkingGraph.RMGMarkingState) g)
										.getstate())) {
							outputarc.add(((MyReachMarkingGraph.RMGArc) r)
									.getName());
						}
					}
				}
				// 每个入度和出度构成一个TAR
				for (int k = 0; k < inputarc.size(); k++) {
					for (int l = 0; l < outputarc.size(); l++) {
						boolean bfound = false;
						MyTransitionAdjacentRelation tar;
						for (int m = 0; m < tarSet.size(); m++) {
							tar = tarSet.get(m);
							if (tar.transitionA.equals(inputarc.get(k))
									&& tar.transitionB.equals(outputarc.get(l))) {
								bfound = true;
								break;
							}
						}
						// 防止重复加入
						if (!bfound) {
							addTAR(inputarc.get(k), outputarc.get(l));
						}
					}
				}
			}
		}

	}

	/**
	 * Add a TAR
	 * 
	 * @param a
	 *            Transition A
	 * @param b
	 *            Transition B
	 */
	public void addTAR(String a, String b) {
		tarSet.add(new MyTransitionAdjacentRelation(a, b));
	}

	public void addTAR(MyTransitionAdjacentRelation r) {
		tarSet.add(r);
	}

	/**
	 * Build TARSet
	 */
	// ���petri��ͳ�ʼ��ʶ����ɴ�ͼ������TAR��
	public void bulidTARSet() {
		// ��յ�ǰ����TAR����
		tarSet.removeAllElements();
		// ����ɴ�ͼ
		rmg = new MyReachMarkingGraph(petri, markedplace);

		// ɨ��ɴ�ͼ�õ���Ԫ��ϵ����
		// ״̬�ڵ����һ�뻡���������ɹ�ϵ��
		MyReachMarkingGraph.RMGObject g;

		for (int i = 0; i < rmg.reachmarkinggraph.size(); i++) {
			g = rmg.reachmarkinggraph.get(i);
			if (g.isA() == MyReachMarkingGraph.RMGObject.MARKINGSTATE) {
				// �ҳ�ÿ���ʶ״̬��������ȣ����
				Vector<String> inputarc = new Vector<String>();
				Vector<String> outputarc = new Vector<String>();
				MyReachMarkingGraph.RMGObject r;
				for (int j = 0; j < rmg.reachmarkinggraph.size(); j++) {
					r = rmg.reachmarkinggraph.get(j);
					if (r.isA() == MyReachMarkingGraph.RMGObject.ARC) {
						if (((MyReachMarkingGraph.RMGArc) r).getTo().equals(
								((MyReachMarkingGraph.RMGMarkingState) g)
										.getstate())) {
							String from = ((MyReachMarkingGraph.RMGArc) r).getName();
							if(!from.equals(""))
								inputarc.add(from);
						}
						if (((MyReachMarkingGraph.RMGArc) r).getFrom().equals(
								((MyReachMarkingGraph.RMGMarkingState) g)
										.getstate())) {
							String to = ((MyReachMarkingGraph.RMGArc) r).getName();
							if(!to.equals(""))
							{
								outputarc.add(to);
							}
							else
							{
								//traverse all arcs reachable from r's output state, find all first non-empty arcs
								Vector<String> alTos = findNonemptyArcs(((MyReachMarkingGraph.RMGArc) r).getTo());
								outputarc.addAll(alTos);
							}
						}
					}
				}
				// ÿ����Ⱥͳ�ȹ���һ��TAR
				for (int k = 0; k < inputarc.size(); k++) {
					for (int l = 0; l < outputarc.size(); l++) {
						boolean bfound = false;
						MyTransitionAdjacentRelation tar;
						for (int m = 0; m < tarSet.size(); m++) {
							tar = tarSet.get(m);
							if (tar.transitionA.equals(inputarc.get(k))
									&& tar.transitionB.equals(outputarc.get(l))) {
								bfound = true;
								break;
							}
						}
						// ��ֹ�ظ�����
						if (!bfound) {
							addTAR(inputarc.get(k), outputarc.get(l));
						}
					}
				}
			}
		}

	}

	private Vector<String> findNonemptyArcs(String currState) {
		// TODO Auto-generated method stub
		Vector<String> vecReturn = new Vector<String>();
		
		MyReachMarkingGraph.RMGObject r;
		for (int i = 0; i < rmg.reachmarkinggraph.size(); i++)
		{
			r = rmg.reachmarkinggraph.get(i);
			if (r.isA() == MyReachMarkingGraph.RMGObject.ARC)
			{
				//traverse currState's output arcs
				if(((MyReachMarkingGraph.RMGArc) r).getFrom().equals(currState))
				{
					String name = ((MyReachMarkingGraph.RMGArc) r).getName();
					if(!name.equals(""))
					{
						vecReturn.add(name);
					}
					else
					{
						Vector<String> vecRetChild = findNonemptyArcs(((MyReachMarkingGraph.RMGArc) r).getTo());
						vecReturn.addAll(vecRetChild);
					}
				}
			}
		}
		return vecReturn;
	}

	/**
	 * Output TARSet by text
	 */
	// ���ı���ʽ���TAR����
	public String toString() {
		String strTARSet = "";

		MyTransitionAdjacentRelation temp;
		for (int i = 0; i < tarSet.size(); i++) {
			temp = tarSet.get(i);

			strTARSet += '(' + temp.transitionA + ',' + temp.transitionB + ") ";
		}

		return strTARSet;
	}

}
