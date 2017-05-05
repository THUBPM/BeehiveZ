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
 * MyPetriPlace.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

import java.util.Vector;

/**
 * Petri�������
 * 
 * MyPetriNet Place class
 * 
 * @author zhp, wwx
 *
 */
public class MyPetriPlace extends MyPetriObject implements Cloneable{
	
	/**
	 * input and output transitions
	 */
	  private Vector<String> inputtransition;  //����transition
	  private Vector<String> outputtransition; //���transition

	  private int initialtokens = 0;      //token��
	  private int currenttokens = 0;      //token��

	  public MyPetriPlace(String id, String name){
	    this.setid(id);
	    this.setname(name);
	    this.initialtokens=0;
	    this.settype(MyPetriObject.PLACE);
	  }
	  
	  public MyPetriPlace(String id, String name, String tokens){
		  this.setid(id);
		  this.setname(name);
		  this.settype(MyPetriObject.PLACE);
		  
		  int i = 0;
		  if(tokens != null){
			  while( i < tokens.length() 
					  && tokens.charAt(i) >= '0' 
						  && tokens.charAt(i) <= '9' ){
				  this.initialtokens += this.initialtokens * 10 + (tokens.charAt(i) - '0');
				  i++;
			  }
			  currenttokens = initialtokens;
			  this.settype(MyPetriObject.PLACE);
		  }
		  else
			  currenttokens = 0;
	  }
	  

	  public boolean isempty()  //token���Ƿ�Ϊ��
	  {
	    if(currenttokens>0)
	      return false;
	    else
	      return true;
	  }

	  public void empty()
	  {
		  currenttokens=0;
	  }

	  public void marking(int n){
		  currenttokens=n;
	  }
	  public int getmarking(){
	    return currenttokens;
	  }
	
	  public void addtoken(int n)
	  {
		  currenttokens+=n;
	  }
	  
		@Override
		public String toString() {
			return name;
		}
		  
		public Object clone(){
			MyPetriPlace obj = null;
		    	obj = (MyPetriPlace)super.clone();
		    return obj;
		}		  
}
