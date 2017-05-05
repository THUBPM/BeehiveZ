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
 * MyPetriTransition.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

import java.util.Vector;

/**
 * Petri���Ǩ��
 * 
 * MyPetriNet Transition class
 * 
 * @author zhp
 *
 */
public class MyPetriTransition extends MyPetriObject implements Cloneable {
	  private String participant; //����ִ����

	  private Vector<String> inputplace;  //����place
	  private Vector<String> outputplace; //���place

	public MyPetriTransition(String id,String name,String participant){
	  this.setid(id);
	  this.setname(name);
	  this.setParticipant(participant);
	  this.settype(MyPetriObject.TRANSITION);
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
}
