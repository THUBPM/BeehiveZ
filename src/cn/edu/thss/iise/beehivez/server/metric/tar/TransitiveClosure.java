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

import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class TransitiveClosure {
	public boolean[][] _relation = null;
	private AdjacentRelation _ar = null;
	private int size = 0;
	
	public TransitiveClosure(ONCompleteFinitePrefix cfp){
		_ar = new AdjacentRelation(cfp);
		size = _ar._relation.length;
		_relation = new boolean[size][size];
		build();
	}
	
	public TransitiveClosure(AdjacentRelation ar){
		_ar = ar;
		size = _ar._relation.length;
		_relation = new boolean[size][size];
		build();
	}
	public void build(){
		for (int i = 0; i < _relation.length; i++) {
			for (int j = 0; j < _relation.length; j++) {
				_relation[i][j] = _ar._relation[i][j];
			}
		}
		
		for (int k = 0; k < _relation.length; k++) {
			for (int i = 0; i < _relation.length; i++) {
				for (int j = 0; j < _relation.length; j++) {
					_relation[i][j] = _relation[i][j] || (_relation[i][k] && _relation[k][j]);
				}
			}
		}
	}
}
