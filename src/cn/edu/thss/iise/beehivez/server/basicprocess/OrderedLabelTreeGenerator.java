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
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
/**
 * 通过CTree构造有序的变迁序列树，需要注意的是在构造CTree的过程中，
 * 兄弟节点之间要确保有序，将标签字符串的大小作为节点间顺序前后的标准
 * 要做到这一点只需要保证MyPetriNet中变迁向量是按照标签大小排序即可
 * @author He Tengfei
 *
 */
public class OrderedLabelTreeGenerator {
	private CTree ctree;
	private CTreeGenerator ctreeGenerator;
	public OrderedLabelTreeGenerator(MyPetriNet net){
		net.sort();
		ctreeGenerator = new CTreeGenerator(net);
		ctree = ctreeGenerator.generateCTree();
	}
	
	public OrderedLabelTreeGenerator() {
		// TODO Auto-generated constructor stub
	}

	public OrderedLabelTree generateOrderedLabelTree(){
		OrderedLabelTree result = new OrderedLabelTree();
		LinkedList<OrderedLabelTreeNode> olTreeNodeList = new LinkedList<OrderedLabelTreeNode>();
		LinkedList<CTreeNode> ctreeNodeList = new LinkedList<CTreeNode>();
		olTreeNodeList.add(result.getRoot());
		ctreeNodeList.add(ctree.getRoot());
		while(!olTreeNodeList.isEmpty()){
			OrderedLabelTreeNode currentOlTreeNode = olTreeNodeList.remove();
			CTreeNode currentCTreeNode = ctreeNodeList.remove();
			ArrayList<CTreeNode> childCTreeNodes = currentCTreeNode.getChild();
			if(childCTreeNodes != null){
				for(int i = 0;i<childCTreeNodes.size();i++){
					CTreeNode child = childCTreeNodes.get(i);
					MyPetriTransition transition = ctree.getEdge(currentCTreeNode, child);
					OrderedLabelTreeNode oltNOde = new OrderedLabelTreeNode(transition.getName());
					currentOlTreeNode.addChild(oltNOde);
					olTreeNodeList.add(oltNOde);
					ctreeNodeList.add(child);
				}
			}
		}
		return result ;
	}

	public OrderedLabelTree generateOrderedLabelTree(MyPetriNet petrinet) {
		// TODO Auto-generated method stub
		petrinet.sort();
		ctreeGenerator = new CTreeGenerator(petrinet);
		ctree = ctreeGenerator.generateCTree();
		return generateOrderedLabelTree();
	}
}
