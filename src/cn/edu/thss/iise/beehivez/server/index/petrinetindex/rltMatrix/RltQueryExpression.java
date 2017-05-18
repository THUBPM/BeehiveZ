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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;

import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex.PathQueryExpression;

public class RltQueryExpression {
	private Vector items = new Vector();

	public Vector getItems() {
		return items;
	}

	public void addAtom(RltQueryAtom a) {
		items.add(a);
	}

	public void addExpression(PathQueryExpression e) {
		items.add(e);
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (Object o : items) {
			result.append(o.toString());
		}
		return result.toString();
	}

}
