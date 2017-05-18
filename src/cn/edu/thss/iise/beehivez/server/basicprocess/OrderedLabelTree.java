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

/**
 * 定义有序树
 * 
 * @author He tengfei
 * 
 */
public class OrderedLabelTree {
	private OrderedLabelTreeNode root;

	public OrderedLabelTree() {
		root = new OrderedLabelTreeNode("Root");
	}

	public OrderedLabelTreeNode getRoot() {
		// TODO Auto-generated method stub
		return root;
	}

	/**
	 * 需要注意的是zhangshasha算法将相同的字母作为同一个节点处理，因此这里使用节点名+惟一标识
	 * 来区分不同的节点
	 * @return 树中两个节点之间的关系集合
	 */
	public String getEdgesValue() {
		// TODO Auto-generated method stub
		LinkedList<OrderedLabelTreeNode> nodes = new LinkedList<OrderedLabelTreeNode>();
		nodes.add(root);
		StringBuffer sb = new StringBuffer("");
		int identity = 0;
		while (!nodes.isEmpty()) {
			OrderedLabelTreeNode current = nodes.remove();
			ArrayList childs = current.getChilds();
			if (childs != null) {
				for (int i = 0; i < childs.size(); i++) {
					OrderedLabelTreeNode child = (OrderedLabelTreeNode) childs
							.get(i);
					sb.append(current.getName()+ "-");
					String name = child.getName();
					child.setName(name+":"+(identity++));
					sb.append(child.getName()+ ";");
					nodes.add(child);
				}
			}
		}
		return sb.toString();
	}
}
