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
import java.util.LinkedList;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * 
 * @author He tengfei
 * 
 */
public class CRTreeGenerator {
	private MyPetriNet petriNet;
	private CTree ctree;

	public CRTreeGenerator(MyPetriNet petriNet) {
		this.petriNet = petriNet;
		CTreeGenerator ctg = new CTreeGenerator(petriNet);
		this.ctree = ctg.generateCTree();
	}

	public CTree getCtree() {
		return ctree;
	}

	public void setCtree(CTree ctree) {
		this.ctree = ctree;
	}

	/**
	 * ��ȡ�޽�Ŀ������result[i]Ϊ1��ʾ��i���������޽��
	 * 
	 * @return
	 */
	public int[] getUnboundedPlaces() {
		int[] flag = null;
		boolean temp = false;
		ArrayList leafNodes = ctree.getLeafNodes();
		for (int i = 0; i < leafNodes.size(); i++) {
			CTreeNode leaf = (CTreeNode) leafNodes.get(i);
			int[] marking = leaf.getMarking().getMarking();
			if (!temp) {
				flag = new int[marking.length];
				for (int j = 0; j < flag.length; j++) {
					flag[j] = 0;
				}
				temp = true;
			}
			for (int j = 0; j < marking.length; j++) {
				if (marking[j] == Integer.MAX_VALUE) {
					flag[j] = 1;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param k
	 *            ����ȷ����ʼ��k��ֵ,k���ڴ���ԭ��������w��ֵ
	 */
	public CTree generateCRTree(int k) {
		int[] flag = getUnboundedPlaces();
		CTreeNode root = ctree.getRoot();
		int[] marking = root.getMarking().getMarking();
		for (int i = 0; i < marking.length; i++) {
			if (flag[i] == 1) {
				marking[i] = k;
			}
		}
		LinkedList list = new LinkedList();
		list.add(root);
		while (list.size() > 0) {
			CTreeNode node = (CTreeNode) list.remove();
			ArrayList children = node.getChild();
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					CTreeNode child = (CTreeNode) children.get(i);
					MyPetriTransition edge = (MyPetriTransition) ctree.getEdge(
							node, child);
					petriNet.setCurrentPlaceMarking(node.getMarking()
							.getMarking());
					petriNet.executeTransition(edge.getId());
					child.setMarking(new Marking(petriNet
							.getCurrentMarkingVector()));
					list.add(child);
				}
			}
		}
		return ctree;
	}

	public CTree generateCRTree() {
		return generateCRTree(-10000);
	}
}
