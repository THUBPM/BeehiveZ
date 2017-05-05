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

import headliner.treedistance.ComparisonZhangShasha;
import headliner.treedistance.CreateTreeHelper;
import headliner.treedistance.OpsZhangShasha;
import headliner.treedistance.Transformation;
import headliner.treedistance.TreeDefinition;
import cn.edu.thss.iise.beehivez.server.basicprocess.OrderedLabelTree;

/**
 * 计算两个标签树之间的相似性
 * @author He Tengfei
 *
 */
public class OrderedLabelTreeDistance {
	public OrderedLabelTreeDistance(){
	}
	
	public double getDistance(OrderedLabelTree tree1,OrderedLabelTree tree2 ){
		String edges1 = tree1.getEdgesValue();
		String edges2 = tree2.getEdgesValue();
		System.out.println("edges1:"+edges1);
		System.out.println("edges2:"+edges2);
		TreeDefinition aTree = CreateTreeHelper.makeTree(edges1);
		TreeDefinition bTree = CreateTreeHelper.makeTree(edges2);
		ComparisonZhangShasha treeCorrector = new ComparisonZhangShasha();
		OpsZhangShasha costs = new OpsZhangShasha();
		Transformation transform = treeCorrector.findDistance(aTree, bTree, costs);
		return transform.getCost();
	}
}
