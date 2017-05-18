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
package cn.edu.thss.iise.beehivez.server.metric.mcessimilarity;

/**
 * This class is used to represent the nodes in modular product graph of line
 * graphs of Petri nets. It only contains two elements representing the index of
 * task line from respective Petri net.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 * 
 */
public class MNode {

	// the index of task line from the first Petri net
	private int id1;

	// the index of task line from the second Petri net
	private int id2;

	public MNode(int tlid1, int tlid2) {
		id1 = tlid1;
		id2 = tlid2;
	}

	public int getId1() {
		return id1;
	}

	public int getId2() {
		return id2;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
