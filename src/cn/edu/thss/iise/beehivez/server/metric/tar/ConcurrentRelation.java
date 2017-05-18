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

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCondition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ConcurrentRelation {
	public boolean[][] _relation = null;
	private ONCompleteFinitePrefix _cfp = null;
	private int size = 0;
	
	public ConcurrentRelation(ONCompleteFinitePrefix cfp) {
		super();
		_cfp = cfp;
		size = _cfp.getOn().getEveSet().size();
		_relation = new boolean[size][size];
		
		build();
	}
	
	public void build(){
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				_relation[i][j] = false;
			}
		}
		
		for(int i = 0; i < size; ++i){
			boolean exist = true;
			ONEvent left = _cfp.getOn().getEveSet().get(i);
			HashSet<ONCondition> leftCoConditions = new HashSet<ONCondition>();
			
			exist = check(left, leftCoConditions);
			if(!exist){
				continue;
			}
			
			for(int j = i + 1; j < size; ++j){
				boolean leftConcurrent = true;
				boolean rightConcurrent = true;
				ONEvent right = _cfp.getOn().getEveSet().get(j);
				HashSet<ONCondition> rightCoConditions = new HashSet<ONCondition>();

				exist = true;
				exist = check(right, rightCoConditions);
				if(!exist){
					continue;
				}
				
				leftConcurrent = match(left, rightCoConditions);
				rightConcurrent = match(right, leftCoConditions);
				
				if(rightConcurrent && leftConcurrent){
					_relation[i][j] = true;
					_relation[j][i] = true;
				}
				
			}
		}
	}
	
	public boolean match(ONEvent e, HashSet<ONCondition> coCondition) {
		Iterator<ONCondition> itPrevCondition = _cfp.getOn().getConsINTOEve(e.getId()).iterator();
		while(itPrevCondition.hasNext()){
			ONCondition prevCondition = itPrevCondition.next();
			if(!coCondition.contains(prevCondition)){
				return false;
			}
		}
		return true;
	}
	
	public boolean check(ONEvent e, HashSet<ONCondition> coCondition){
		Iterator<ONCondition> itPrevCondition1 = _cfp.getOn().getConsINTOEve(e.getId()).iterator();
		if(itPrevCondition1.hasNext()){
			ONCondition prevCondition1 = itPrevCondition1.next();
			coCondition.addAll(prevCondition1.getCommonCondition());
			coCondition.addAll(prevCondition1.getPrivateCondition());
		}
		while(itPrevCondition1.hasNext()){
			ONCondition prevCondition1 = itPrevCondition1.next();
			HashSet<ONCondition> currentCoConditions = new HashSet<ONCondition>();
			currentCoConditions.addAll(prevCondition1.getCommonCondition());
			currentCoConditions.addAll(prevCondition1.getPrivateCondition());
			coCondition.retainAll(currentCoConditions);
			if(coCondition.isEmpty()){
				return false;
			}
		}
		
		return true;
	}
}
