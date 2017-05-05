/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: wangwenxingbuaa@gmail.com 
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
package cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet;

import java.util.*;

import org.processmining.importing.coloredcontrolflownet.arcinscriptionparser.parser;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.*;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONMarking implements Comparable {
	private TreeSet<MyPetriPlace> places;
	
	public ONMarking(){
		places = new TreeSet<MyPetriPlace>();
	}
	
	public void addPlace(MyPetriPlace place){
		places.add(place);
	}
	
	public void delPlace(MyPetriPlace place){
		places.remove(place);
	}
	
	public Iterator iterator(){
		return places.iterator();
	}
	
	public int getSize(){
		return places.size();
	}
	
	public void addMarking(ONMarking marking){
		MyPetriPlace place;
		Iterator it = marking.iterator();
		while(it.hasNext()){
			place = (MyPetriPlace) it.next();
			addPlace(place);
		}
	}
	
	public void delMarking(ONMarking marking){
		MyPetriPlace place;
		Iterator it = marking.iterator();
		while(it.hasNext()){
			place = (MyPetriPlace) it.next();
			delPlace(place);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object object) {
		// TODO Auto-generated method stub
		ONMarking marking = (ONMarking)object;
		if(this.getSize() != marking.getSize()){
			return this.getSize() - marking.getSize();
		}
		
		if(this.getSize() == 0){
			return 0;
		}
		
		Iterator<MyPetriPlace> thisIt = this.places.iterator();
		Iterator<MyPetriPlace> thatIt = marking.places.iterator();
		while(thisIt.hasNext()){
			MyPetriPlace thisPlace = thisIt.next();
			MyPetriPlace thatPlace = thatIt.next();
			int ret = Integer.parseInt(thisPlace.getId().substring(2)) - Integer.parseInt(thatPlace.getId().substring(2));
			if(ret != 0){
				return ret;
			}
		}
		
		return 0;
	}
	
	public boolean equals(Object marking){
		if (!(marking instanceof ONMarking)) {
			return false;
		}
		
		if(this.compareTo(marking) == 0){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isLessOrEqual(ONMarking marking){
		Iterator it = this.iterator();
		boolean t = true;
		
		while(it.hasNext() && t){
			MyPetriPlace place = (MyPetriPlace)it.next();
			if(!marking.places.contains(place)){
				t = false;
			}
		}
		
		return t;
	}
	
	public int hashCode(){
		int ret = 17;
		for(MyPetriPlace place : this.places){
			ret = 37 * ret + place.hashCode();
		}
		
		return ret;
	}

}
