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
package cn.edu.thss.iise.beehivez.server.util;

/**
 * @author Tao Jin
 * 
 */
public class TransitionLabelPair implements Comparable {

	private String first = null;
	private String second = null;

	public TransitionLabelPair(String first, String second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the second
	 */
	public String getSecond() {
		return second;
	}

	/**
	 * @return the first
	 */
	public String getFirst() {
		return first;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(Object o) {
		TransitionLabelPair other = (TransitionLabelPair) o;
		int ret = this.first.compareTo(other.first);
		if (ret == 0) {
			return this.second.compareTo(other.second);
		} else {
			return ret;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TransitionLabelPair) {
			TransitionLabelPair other = (TransitionLabelPair) obj;
			if (this.first == null) {
				if (other.first == null) {
					return this.second.equals(other.second);
				} else {
					return false;
				}
			} else {
				if (other.first == null) {
					return false;
				} else {
					return this.first.equals(other.first)
							&& this.second.equals(other.second);
				}
			}
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}
