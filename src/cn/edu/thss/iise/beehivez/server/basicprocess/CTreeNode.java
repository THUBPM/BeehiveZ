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

/**
 * 
 * @author He tengfei
 * 
 */
public class CTreeNode {
	private int id;
	private Marking marking;
	private int type; // 1 ��ʾ�ýڵ���һ���½ڵ� 2��ʾ�ýڵ���һ���ɽڵ� 3��ʾ�ýڵ���һ��ĩ�˽ڵ�
	private ArrayList child = null;
	private CTreeNode parent = null;

	public CTreeNode(int id, int[] initialMarking, int type) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.marking = new Marking(Arrays.copyOf(initialMarking,
				initialMarking.length));
		this.type = type;
	}

	public CTreeNode(int id, Marking marking, int type) {
		// TODO Auto-generated constructor stub
		this(id, marking.getMarking(), type);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Marking getMarking() {
		return marking;
	}

	public void setMarking(Marking marking) {
		this.marking = marking;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<CTreeNode> getChild() {
		return child;
	}

	public void setChild(ArrayList child) {
		this.child = child;
	}

	public CTreeNode getParent() {
		return parent;
	}

	public void setParent(CTreeNode parent) {
		this.parent = parent;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(" + id + ":[");
		int[] marks = marking.getMarking();
		for (int i = 0; i < marks.length - 1; i++) {
			if (marks[i] == Integer.MAX_VALUE) {
				buffer.append("w,");
			} else {
				buffer.append(marks[i] + ",");
			}
		}
		if (marks[marks.length - 1] == Integer.MAX_VALUE) {
			buffer.append("w])");
		} else {
			buffer.append(marks[marks.length - 1] + "])");
		}
		return buffer.toString();
	}

	public void addChild(CTreeNode newCreatedNode) {
		// TODO Auto-generated method stub
		if (child == null) {
			child = new ArrayList();
		}
		child.add(newCreatedNode);
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		CTreeNode node = (CTreeNode) arg0;
		if (id == node.getId()) {
			return true;
		} else
			return false;
	}
}
