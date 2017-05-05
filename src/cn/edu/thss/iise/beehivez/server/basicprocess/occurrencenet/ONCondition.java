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

import java.util.Vector;
import java.util.HashSet;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONCondition extends ONObject implements Comparable, Cloneable {
	private MyPetriPlace place;
	private String arcInId;
	private String arcOutId;
	
	private HashSet<ONCondition> commonCoCondition = new HashSet<ONCondition>();
	private HashSet<ONCondition> privateCoCondition = new HashSet<ONCondition>();
	
	public ONCondition(String id, MyPetriPlace place){
		this.setId(id);
		this.setLabel(place.getName());
		this.setPlace(place);
		this.setType(ONObject.CONDITION);
		
		if(place.obj == null){
			Vector<ONCondition> conditions = new Vector<ONCondition>();
			conditions.add(this);
			place.obj = conditions;
		} else{
			Vector<ONCondition> conditions = (Vector<ONCondition>) place.obj;
			conditions.add(this);
		}
	}
	
	public MyPetriPlace getPlace(){
		return place;
	}
	
	public void setPlace(MyPetriPlace place){
		this.place = place;
	}
	
	public String getArcInId() {
		return arcInId;
	}

	public void setArcInId(String arcInId) {
		this.arcInId = arcInId;
	}

	public String getArcOutId() {
		return arcOutId;
	}

	public void setArcOutId(String arcOutId) {
		this.arcOutId = arcOutId;
	}

	public String toString(){
		return label;
	}
	
	public Object clone() {
		ONCondition obj = null;
		obj = (ONCondition) super.clone();
		return obj;
	}

	/**
	 * @return the commonCondition
	 */
	public HashSet<ONCondition> getCommonCondition() {
		return commonCoCondition;
	}

	/**
	 * @param commonCondition the commonCondition to set
	 */
	public void setCommonCondition(HashSet<ONCondition> commonCoCondition) {
		this.commonCoCondition = commonCoCondition;
	}

	/**
	 * @return the privateCondition
	 */
	public HashSet<ONCondition> getPrivateCondition() {
		return privateCoCondition;
	}
	
	/**
	 * @param Condition the ONCondition
	 */
	public void addPrivateCondition(ONCondition condition) {
		this.privateCoCondition.add(condition);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
