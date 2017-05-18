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

/**
 * 有序树的节点
 * 
 * @author he tengfei
 * 
 */
public class OrderedLabelTreeNode {
	private String label = null ;
	private ArrayList<OrderedLabelTreeNode> children = null ;
	public OrderedLabelTreeNode() {
	}

	public OrderedLabelTreeNode(String label) {
		this.label = label;
	}

	public void addChild(OrderedLabelTreeNode oltNode) {
		// TODO Auto-generated method stub
		if(children == null){
			children = new ArrayList<OrderedLabelTreeNode>();
		}
		children.add(oltNode);
	}

	public ArrayList getChilds() {
		// TODO Auto-generated method stub
		return children;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return label;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		label = name;
	}

}
