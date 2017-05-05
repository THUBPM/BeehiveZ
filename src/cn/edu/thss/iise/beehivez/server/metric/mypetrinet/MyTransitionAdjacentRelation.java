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
/**
 * MyTransitionAdjacentRelation.java 09-3-10
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

/**
 * Transition Adjacent Relation Object
 * 
 * ��Ǩ�ڽӹ�ϵ������
 * 
 * @author zhp
 * 
 */
public class MyTransitionAdjacentRelation implements Cloneable {
	public String transitionA, transitionB;

	public MyTransitionAdjacentRelation(String a, String b) {
		this.transitionA = a;
		this.transitionB = b;
	}
	public MyTransitionAdjacentRelation() {

	}
	
	public MyTransitionAdjacentRelation(MyTransitionAdjacentRelation r) {
		this.transitionA = r.transitionA;
		this.transitionB = r.transitionB;
	}
	
    public Object clone(){
    	MyTransitionAdjacentRelation obj = null;
        try{
        	obj = (MyTransitionAdjacentRelation)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
    }
}