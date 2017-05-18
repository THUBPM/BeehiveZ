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
 * PetriPlace.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.basicprocess.mymodel;

import java.util.*;

/**
 * 
 * PetriNet Place class
 * 
 * @author He tengfei
 * 
 */
public class MyPetriPlace extends MyPetriObject implements Comparable, Cloneable {
	private int initialtokens = 0; // token��
	private int currenttokens = 0; // token��
	
	private Vector<String> inputtransition; // 输入transition
	private Vector<String> outputtransition; // 输出transition

	private int tokens; // token数

	Vector<MyPetriTransition> successors; // 输出transition集合
	Vector<MyPetriTransition> predecessors; // 输入transition集合

	public MyPetriPlace(String id, String name) {
		this.setId(id);
		this.setName(name);
		this.initialtokens = 0;
		this.setType(MyPetriObject.PLACE);
	}

	public boolean isempty() // token���Ƿ�Ϊ��
	{
		if (currenttokens > 0)
			return false;
		else
			return true;
	}

	public void empty() {
		currenttokens = 0;
	}

	public int getInitialtokens() {
		return initialtokens;
	}

	public void setInitialtokens(int initialtokens) {
		this.initialtokens = initialtokens;
	}

	public int getCurrenttokens() {
		return currenttokens;
	}

	public void setCurrenttokens(int currenttokens) {
		this.currenttokens = currenttokens;
	}

	public void addtoken(int n) {
		if (currenttokens != Integer.MAX_VALUE)
			currenttokens += n;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public int hashCode(){
		int result = 17;
		result = 37 * result + id.hashCode();
		return result;
	}

	public Object clone() {
		MyPetriPlace obj = null;
		obj = (MyPetriPlace) super.clone();
		return obj;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		MyPetriPlace that = (MyPetriPlace)o;
		int ret = Integer.parseInt(this.getId().substring(2)) - Integer.parseInt(that.getId().substring(2));
		if(ret != 0){
			return ret;
		}
		return 0;
	}
	
	public Vector<MyPetriTransition> getSuccessors() {
		return successors;
	}

	public void setSuccessors(Vector<MyPetriTransition> successors) {
		this.successors = successors;
	}

	public Vector<MyPetriTransition> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(Vector<MyPetriTransition> predecessors) {
		this.predecessors = predecessors;
	}

	public void addSuccessor(MyPetriTransition successor) {
		if (!successors.contains(successor)) {
			successors.add(successor);
		}
	}

	public MyPetriTransition deleteSuccessor(MyPetriTransition successor) {	
		if (successors.contains(successor)) {
			successors.remove(successor);
		}
		return successor;
	}

	public void addPredecessor(MyPetriTransition predecessor) {
		if (!predecessors.contains(predecessor)) {
			predecessors.add(predecessor);
		}
	}

	public MyPetriTransition deletePredecessor(MyPetriTransition predecessor) {
		if (predecessors.contains(predecessor)) {
			predecessors.remove(predecessor);
		}
		return predecessor;
	}
}
