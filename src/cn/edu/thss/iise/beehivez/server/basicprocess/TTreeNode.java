
/**
 * by hhw 
*/

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
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

import java.util.ArrayList;

public class TTreeNode {
	private int id;

	// trace[0]: pre trace 
	// trace[1]: post trace
	//private Marking[] trace = null;
	private MyPetriTransition transition = null;
	private ArrayList<TTreeNode> child = null;
	private TTreeNode parent = null;
	private int occurNum = 0;
	private Marking nextMarking = null;
	
	/************ tree-2 ***************/
	private TTreeNode anchorTNode = null;
	private TTreeNode copy = null;
	private TTreeNode special = null;
	//...
	// private ArrayList<TTreeNode> otherChildTransitions = null;
	private int ANum = 0;
	/** mark[0]: not-pre/pre[0/1];
	  * mark[1]: not-marked/dead-f/old-f[0/1/2];
	  * mark[2]: old-end[0/1];
	  * mark[3]: anchor/not-anchor[0/1];
	  * mark[4]: not-dead-end/dead-end-1/dead-end-2/...[0/1->2->3->...];
	  **/
	private int[] mark = new int[6];

	/************ TTreeNode ***************/
	// here transition = ctn.parent -> ctn
	public TTreeNode() {
		id = 0;
	}
	public TTreeNode(CTree ctree, CTreeNode ctn) {
		//trace = setTrace(a, aParent);
		for(int i = 0; i < 5; i++) {
			mark[i] = 0;
		}
		transition = ctree.getEdge(ctn.getParent(), ctn);
		setNextMarking(ctn.getMarking());
	}

	public TTreeNode(int id, CTree ctree, CTreeNode ctn) {
		//trace = setTrace(a, aParent);
		for(int i = 0; i < 5; i++) {
			mark[i] = 0;
		}
		this.id = id;
		transition = ctree.getEdge(ctn.getParent(), ctn);
		setNextMarking(ctn.getMarking());
	}

	public TTreeNode(TTreeNode ttn) {
		this.id = ttn.getId();
		this.transition = ttn.getTransition();
		this.setCopy(ttn);
	}

	/************ occurNum ***************/
	public int getOccurNum() {
		return occurNum;
	}
	
	public void resetOccurNum() {
		this.occurNum = 0;
	}
	
	public void incOccurNum() {
		this.occurNum++;
	}
	
	/************ nextMarking ***************/
	public Marking getNextMarking() {
		return nextMarking;
	}
	
	public void setNextMarking(Marking nextMarking) {
		this.nextMarking = nextMarking;
	}
	
	/************ copy ***************/
	public void setCopy(TTreeNode ttn) {
		this.copy = ttn;
	}

	public TTreeNode getCopy() {
		return copy;
	}

	/************ special ***************/
	public void setSpecial(TTreeNode ttn) {
		this.special = ttn;
	}

	public TTreeNode getSpecial() {
		return special;
	}
	
	public void nullSpecial() {
		this.special = null;
	}
	
	/************ mark ***************/
	// return: if(mark[1] == 0)
	public int isMarked() {
		return mark[1];
	}

	public int isPre() {
		return mark[0];// 1: true 0:false
	}

	public void setPre() {
		mark[0] = 1;
	}

	// return: if(mark[1] == 1)
	public int isDeadFather() {
		return mark[1];
	}

	public void setDeadFather() {
		mark[1] = 1;
	}

	// return: if(mark[1] == 2)
	public int isOldFather() {
		return mark[1];
	}

	public void setOldFather() {
		mark[1] = 2;
	}

	public int isOldEnd() {
		return mark[2];// 1: true 0:false
	}

	public void setOldEnd() {
		mark[2] = 1;
	}

	public int isAnchor() {
		return mark[3];// 1: true 0:false
	}

	public void setAnchor() {
		mark[3] = 1;
	}

	// return: if(mark[4] == 0)
	public int isDeadEnd() {
		return mark[4];
	}

	public void setDeadEnd(int i) {
		mark[4] = i;
	}

	/************ id ***************/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/************ transition ***************/
	public void setTransition(MyPetriTransition transition) {
		this.transition = transition;
	}

	public MyPetriTransition getTransition() {
		return transition;
	}

	/************ child ***************/
	public ArrayList<TTreeNode> getChild() {
		return child;
	}

	public void setChild(ArrayList<TTreeNode> child) {
		this.child = child;
	}
	
	public int childNumber() {
		return child.size();
	}

	/************ parent ***************/
	public TTreeNode getParent() {
		return parent;
	}

	public void setParent(TTreeNode parent) {
		this.parent = parent;
	}

	public void addChild(TTreeNode newChilde) {
		if (child == null) {
			child = new ArrayList<TTreeNode>();
		}
		child.add(newChilde);
	}
	
	public void deleteChild(TTreeNode theChild) {
		int index = -1;
		for(int i = 0; i < child.size(); i++) {
			if(child.get(i).getCopy().getId() == theChild.getCopy().getId()) {
				index = i;
			}
		}
		if(index > -1) {
			child.remove(index);
		}
	}

	/************ anchorTNode ***************/
	public void setAnchorNode(TTreeNode anchor) {
		this.anchorTNode = anchor;
	}

	public TTreeNode getAnchorNode() {
		return anchorTNode;
	}

	/************ Anum ***************/
	public void setANum(int n) {
		this.ANum = n;
	}

	public int getANum() {
		return ANum;
	}
}


