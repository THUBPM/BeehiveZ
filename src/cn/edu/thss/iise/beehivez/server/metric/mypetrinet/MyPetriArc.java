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
/*
 * MyPetriArc.java 
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

/**
 * Petri�����ӻ���
 * 
 * MyPetriNet Arc class
 * 
 * @author zhp
 * 
 */
public class MyPetriArc extends MyPetriObject implements Cloneable {
	private String sourceid;
	private String targetid;
	private int weight;

	public MyPetriArc(String id, String sourceid, String targetid) {
		this.setid(id);
		this.sourceid = sourceid;
		this.targetid = targetid;
		this.weight = 1;
		this.settype(MyPetriObject.ARC);
	}

	public void setsourceid(String id) {
		sourceid = id;
	}

	public void settargetid(String id) {
		targetid = id;
	}

	public String getsourceid() {
		return sourceid;
	}

	public String gettargetid() {
		return targetid;
	}

	public int getweight() {
		return weight;
	}

	/**
	 * ���ڷ�
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object clone() {
		MyPetriArc obj = null;
		obj = (MyPetriArc) super.clone();
		return obj;
	}
}
