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

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * 
 * @author He tengfei
 * 
 */
public class CTreeGenerator {
	private MyPetriNet petriNet;
	private CTree ctree;
	private NumberGenerator nextInt;

	public CTreeGenerator(MyPetriNet net) {
		this.petriNet = net;
		nextInt = new NumberGenerator();
	}

	public CTree generateCTree() {
		int[] initialMarking = petriNet.getCurrentMarkingVector();
		CTreeNode root = new CTreeNode(nextInt.next(), initialMarking, 1);
		ctree = new CTree(root);
		ArrayList newNodes = ctree.getNewNodes();
		while (newNodes.size() > 0) {
			/*
			 * System.out.println("���б�ʶΪ�µĽڵ��У�"); for(int
			 * i=0;i<newNodes.size();i++){
			 * System.out.print(newNodes.get(i).toString()+" "); }
			 * System.out.println();
			 */
			for (int i = 0; i < newNodes.size(); i++) {
				CTreeNode newNode = (CTreeNode) newNodes.get(i);
				// System.out.println("�����±�ʶ��"+newNode.toString());
				if (ctree.markingRepeat(newNode)) {
					newNode.setType(2);
					continue;
				}
				petriNet.setCurrentPlaceMarking(newNode.getMarking()
						.getMarking());
				/*
				 * Vector<MyPetriPlace> v = petriNet.getPlaceSet(); StringBuffer
				 * sb = new StringBuffer(); for(int h=0;h<v.size();h++){
				 * sb.append(v.get(h).getmarking()+""); }
				 * 
				 * int[]t = petriNet.getCurrentMarkingVector(); sb = new
				 * StringBuffer(); for(int h=0;h<t.length;h++){
				 * sb.append(t[h]+""); }
				 * System.out.println("petri��ǰ��ʶ: "+sb.toString());
				 */
				Vector<MyPetriTransition> ptVector = petriNet
						.getEnabledTransition(newNode.getMarking().getMarking());
				if (ptVector.size() <= 0) {
					newNode.setType(3);
					continue;
				}
				// System.out.println("��״̬"+sb.toString()+"�£����Դ����ı�Ǩ���£�");
				/*
				 * for(int j=0;j<ptVector.size();j++){
				 * System.out.print("enableT:"+ptVector.get(j).getid()+" "); }
				 * System.out.println();
				 */
				for (int j = 0; j < ptVector.size(); j++) {
					petriNet.setCurrentPlaceMarking(newNode.getMarking()
							.getMarking());
					// System.out.println(ptVector.get(j).getid()+"������");
					petriNet.executeTransition(ptVector.get(j).getId());
					int[] currentMarking = Arrays.copyOf(petriNet
							.getCurrentMarkingVector(), petriNet
							.getCurrentMarkingVector().length);
					/*
					 * StringBuffer buffer = new StringBuffer(); for(int
					 * k=0;k<currentMarking.length;k++){
					 * buffer.append(currentMarking[k]+","); }
					 * System.out.println
					 * (ptVector.get(j).getid()+"������,petri��ı�ʶΪ��"
					 * +buffer.toString());
					 */
					Marking newMarking = new Marking(currentMarking);
					CTreeNode temp = newNode;
					while (temp != null) {
						int[] tempMarking = temp.getMarking().getMarking();
						if (temp.getMarking().lessThan(newMarking)) {
							// System.out.println("temp: "+temp.toString());
							for (int k = 0; k < currentMarking.length; k++) {
								if (tempMarking[k] < currentMarking[k]) {
									currentMarking[k] = Integer.MAX_VALUE;
								}
							}
						}
						temp = temp.getParent();
					}
					newMarking.setMarking(currentMarking);
					CTreeNode newCreatedNode = new CTreeNode(nextInt.next(),
							newMarking, 1);
					// System.out.println("�½ڵ��״̬��"+newCreatedNode.toString());
					newCreatedNode.setParent(newNode);
					newNode.addChild(newCreatedNode);
					newNode.setType(2);
					ctree.addNewEdge(newNode, newCreatedNode, ptVector.get(j));
				}
			}
			newNodes = ctree.getNewNodes();
		}
		return ctree;
	}
}
