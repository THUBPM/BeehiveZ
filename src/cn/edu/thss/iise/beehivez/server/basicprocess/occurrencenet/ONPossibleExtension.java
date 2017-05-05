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

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONPossibleExtension implements Comparable<ONPossibleExtension> {
	private ONEvent event = null;
	private ONCondition[] preConditions = null;
	private ONCompleteFinitePrefix cfp = null;
	
	/**
	 * @param event
	 * @param preConditions
	 * @param cfp
	 */
	public ONPossibleExtension(String id, MyPetriTransition trans, ONCondition[] preConditions,
			ONCompleteFinitePrefix cfp) {
		super();
		this.event = new ONEvent(id, trans, cfp);
		this.preConditions = preConditions;
		this.cfp = cfp;
		
		event.getLocalConfiguration();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ONPossibleExtension pe) {
		// TODO Auto-generated method stub
		return this.event.getLocalConfiguration().compareTo(pe.event.getLocalConfiguration());
	}

	/**
	 * @return the event
	 */
	public ONEvent getEvent() {
		return event;
	}

}
