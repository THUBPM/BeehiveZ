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
 * PetriTransition.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.basicprocess.mymodel;

import java.util.Vector;

/**
 * Petri���Ǩ��
 * 
 * PetriNet Transition class
 * 
 * @author zhp
 *
 */
public class MyPetriTransition extends MyPetriObject implements Cloneable,Comparable {
	private String participant; //����ִ����
	private Vector<String> inputplace; // 输入place
	private Vector<String> outputplace; // 输出place

	Vector<MyPetriPlace> successors; // 输出transition集合
	Vector<MyPetriPlace> predecessors; // 输入transition集合

	public MyPetriTransition(String id,String name,String participant){
	  this.setId(id);
	  this.setName(name);
	  this.setParticipant(participant);
	  this.setType(MyPetriObject.TRANSITION);
	  successors = new Vector<MyPetriPlace> (); 
	  predecessors = new Vector<MyPetriPlace> (); 
	}
	
	public Vector<MyPetriPlace> getSuccessors() {
		return successors;
	}

	public void setSuccessors(Vector<MyPetriPlace> successors) {
		this.successors = successors;
	}

	public Vector<MyPetriPlace> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(Vector<MyPetriPlace> predecessors) {
		this.predecessors = predecessors;
	}

	public void addSuccessor(MyPetriPlace successor) {
		if (!successors.contains(successor)) {
			successors.add(successor);
		}
	}

	public MyPetriPlace deleteSuccessor(MyPetriPlace successor) {	
		if (successors.contains(successor)) {
			successors.remove(successor);
		}
		return successor;
	}

	public void addPredecessor(MyPetriPlace predecessor) {
		if (!predecessors.contains(predecessor)) {
			predecessors.add(predecessor);
		}
	}

	public MyPetriPlace deletePredecessor(MyPetriPlace predecessor) {
		if (predecessors.contains(predecessor)) {
			predecessors.remove(predecessor);
		}
		return predecessor;
	}

	//ִ�к���
	  public void excution(){};   
	  
	//�Ƿ�ʹ��
	  public boolean isenabled(){
		  return false;
	  }; 

	  public void setParticipant(String name){
	    participant=name;
	  }

	  public String getParticipant(){
	    return participant;
	  }

	@Override
	public String toString() {
		return name;
	}
	  
    public Object clone(){
    	MyPetriTransition obj = null;
        	obj = (MyPetriTransition)super.clone();
        return obj;
    }		
    
    public boolean equals(Object o){
    	MyPetriTransition t = (MyPetriTransition) o;
    	if(t.getId().equals(id)){
    		return true;
    	}
    	return false;
    }

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		MyPetriTransition transition = (MyPetriTransition) o;
		return this.getName().compareTo(transition.getName());
	}
}
