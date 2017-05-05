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
package cn.edu.thss.iise.beehivez.server.metric.tar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCondition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class AdjacentRelation {
	private ONCompleteFinitePrefix _cfp = null;
	public boolean[][] _relation = null;
	public boolean[][] _implicit = null;
	private int size = 0;
	
	public AdjacentRelation(ONCompleteFinitePrefix cfp){
		_cfp = cfp;
		size = _cfp.getOn().getEveSet().size();
		_relation = new boolean[size][size];
		_implicit = new boolean[size][size];
		
		build();
	}
	
	public void build(){
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				_relation[i][j] = false;
				_implicit[i][j] = false;
			}
		}
		
		HashSet<ONEvent> visited = new HashSet<ONEvent>();
		LinkedList<ONEvent> queue = new LinkedList<ONEvent>();
		int indexOfEvent = 0;
		int indexOfPostEvent = 0;
		
		//calculate initial visible events
		Iterator<ONCondition> itCondition = _cfp.getIntialConditions().iterator();
		while(itCondition.hasNext()){
			ONCondition condition = itCondition.next();
			
			Iterator<ONEvent> itPostEvent = 
				_cfp.getOn().getEvesOUTOFCon(condition.getId()).iterator();
			Vector<ONEvent> visibleEvents = new Vector<ONEvent>();
			
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				
				if(postEvent.getLabel().isEmpty()){
					visibleEvents.addAll(postEvent.getVisibleSuccessiveEvents());
				}
				else{
					visibleEvents.add(postEvent);
				}
			}
			for(ONEvent postEvent : visibleEvents){
				if(visited.add(postEvent)){
					queue.add(postEvent);
				}
			}
		}
		
		//calculate the ordering relations recursively
		while(!queue.isEmpty()){
			ONEvent event = queue.removeFirst();
			Iterator<ONEvent> itPostEvent = event.getVisibleSuccessiveEvents().iterator();
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				indexOfEvent = _cfp.getOn().indexOfEvent(event);
				indexOfPostEvent = _cfp.getOn().indexOfEvent(postEvent);
				
				//detect the implicit dependencies between the event and its successors.
				//if true, there is no adjacent relation.
				if(!event.detectImplicitDependency(postEvent)){
					_relation[indexOfEvent][indexOfPostEvent] = true;
					if(visited.add(postEvent)){
						queue.add(postEvent);
					}
				}
				else{
					_implicit[indexOfEvent][indexOfPostEvent] = true;
				}
			}
		}
	}
}
