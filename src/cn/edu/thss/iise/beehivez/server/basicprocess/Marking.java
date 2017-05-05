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

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.Arrays;

/**
 * 
 * @author He tengfei
 * 
 */
public class Marking {
	private int[] marking;

	public Marking(int[] initialMarking) {
		this.marking = Arrays.copyOf(initialMarking, initialMarking.length);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		boolean flag = true;
		Marking m = (Marking) obj;
		int[] mark = m.getMarking();
		for (int i = 0; i < mark.length; i++) {
			if (mark[i] != marking[i]) {
				flag = false;
				break;
			}
		}
		return flag;

	}

	public int[] getMarking() {
		return marking;
	}

	public void setMarking(int[] marking) {
		this.marking = marking;
	}

	public boolean lessThan(Marking newMarking) {
		// TODO Auto-generated method stub
		boolean flag = true;
		int[] tempMarking = newMarking.getMarking();
		for (int i = 0; i < tempMarking.length; i++) {
			if (marking[i] > tempMarking[i]) {
				flag = false;
				break;
			}
		}
		if (this.equals(newMarking)) {
			flag = false;
		}
		return flag;
	}

	public boolean lessOrEqualThan(Marking newMarking) {
		if (equals(newMarking)) {
			return true;
		} else if (lessThan(newMarking)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean containsW() {
		// TODO Auto-generated method stub
		boolean flag = false;
		for (int i = 0; i < marking.length; i++) {
			if (marking[i] == Integer.MAX_VALUE) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean greaterThan(Marking marking2) {
		// TODO Auto-generated method stub
		boolean flag = true;
		int[] tempMarking = marking2.getMarking();
		for (int i = 0; i < tempMarking.length; i++) {
			if (marking[i] < tempMarking[i]) {
				flag = false;
				break;
			}
		}
		if (this.equals(marking2)) {
			flag = false;
		}
		return flag;
	}

}
