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
package cn.edu.thss.iise.beehivez.server.index;

import java.lang.Comparable;
import java.util.Iterator;
import java.util.TreeSet;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;

/**
 * @author Tao Jin
 * 
 *         use to store the query result, has two fields: (1)process id
 *         (2)similarity between the query and the result process, if the query
 *         is exact, this value is always 1.
 * 
 */
public class ProcessQueryResult implements Comparable<ProcessQueryResult> {
	private long process_id;
	private float similarity = 1;
	private ProcessObject po = null;

	public ProcessQueryResult(long process_id, float similarity) {
		this.process_id = process_id;
		this.similarity = similarity;
	}

	/**
	 * @return the po
	 */
	public ProcessObject getPo() {
		return po;
	}

	/**
	 * @param po
	 *            the po to set
	 */
	public void setPo(ProcessObject po) {
		this.po = po;
	}

	/**
	 * @return the process_id
	 */
	public long getProcess_id() {
		return process_id;
	}

	/**
	 * @param process_id
	 *            the process_id to set
	 */
	public void setProcess_id(long process_id) {
		this.process_id = process_id;
	}

	/**
	 * @return the similarity
	 */
	public float getSimilarity() {
		return similarity;
	}

	/**
	 * @param similarity
	 *            the similarity to set
	 */
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessQueryResult) {
			ProcessQueryResult o = (ProcessQueryResult) obj;
			return this.process_id == o.process_id
					&& this.similarity == o.similarity;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(process_id = " + process_id + " , similarity = " + similarity
				+ ")";
	}

	@Override
	public int compareTo(ProcessQueryResult o) {
		if (this.similarity > o.similarity) {
			return 1;
		} else if (this.similarity < o.similarity) {
			return -1;
		} else {
			// this.similarity == o.similarity;
			if (this.process_id < o.process_id) {
				return -1;
			} else if (this.process_id > o.process_id) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeSet<ProcessQueryResult> s = new TreeSet<ProcessQueryResult>();
		ProcessQueryResult a = new ProcessQueryResult(2, 3);
		ProcessQueryResult b = new ProcessQueryResult(1, 2);
		ProcessQueryResult c = new ProcessQueryResult(3, 4);
		s.add(a);
		s.add(b);
		s.add(c);
		Iterator it = s.descendingIterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

}
