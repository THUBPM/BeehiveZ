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
 * MyPetriObject.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

/**
 * PetriNetԪ�أ���Ǩ����������ӻ����������࣬
 * 
 * MyPetriNet Object superclass 
 * MyPetriNet Object contains transitions, places and arcs
 * 
 * @author zhp
 * 
 */
public class MyPetriObject implements Cloneable {
	// Ԫ�����ͱ�ʶ
	public static final int PLACE = 1;
	public static final int TRANSITION = 2;
	public static final int ARC = 3;
	//
	private String id;

	protected String name;
	// �ڵ����� 1:transition 2: place 3: arc
	private int type;

	public void setid(String id) {
		this.id = id;
	}

	public String getid() {
		return id;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getname() {
		return name;
	}

	public void settype(int type) {
		this.type = type;
	}

	// �ж�Ԫ�ص����ͣ��ǿ���\��Ǩ���ǻ�
	public int isA() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}
	
    public Object clone(){
    	MyPetriObject obj = null;
        try{
        	obj = (MyPetriObject)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
    }	
}
