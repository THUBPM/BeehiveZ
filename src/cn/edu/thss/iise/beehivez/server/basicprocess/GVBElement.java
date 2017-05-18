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

/**
 * we use GVBElement class to express a segment of a branch of a tree (CTree or
 * CRTree)
 * 
 * @author He tengfei
 * 
 */
public class GVBElement {
	private CTreeNode from;
	private CTreeNode to;

	public GVBElement(CTreeNode from, CTreeNode to) {
		this.from = from;
		this.to = to;
	}

	public CTreeNode getFrom() {
		return from;
	}

	public void setFrom(CTreeNode from) {
		this.from = from;
	}

	public CTreeNode getTo() {
		return to;
	}

	public void setTo(CTreeNode to) {
		this.to = to;
	}

	public int hashCode() {
		return (from.getId() + "" + to.getId()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GVBElement temp = (GVBElement) obj;
		CTreeNode tempF = temp.getFrom();
		CTreeNode tempT = temp.getTo();
		if (from.equals(tempF) && to.equals(tempT)) {
			return true;
		} else {
			return false;
		}
	}
}
