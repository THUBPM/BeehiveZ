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

/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author ���
 * 
 */
public class Searcher {
	private final InvertedListIndex index;

	public Searcher(InvertedListIndex in) {
		index = in;
	}

	public Scanner search(String query) {
		LinkedList<Long> result = new LinkedList<Long>();

		return new Scanner(index, result);
	}

	// public Scanner getScanner();

	protected class Scanner implements Iterator<String> {
		int cached = 0;
		int cachedSize = 100;
		final Iterator<Long> docList;
		final InvertedListIndex index;
		LinkedList<String> cache = new LinkedList<String>();
		Iterator<String> list;

		public Scanner(InvertedListIndex dd, LinkedList<Long> ll) {
			index = dd;
			docList = ll.iterator();
			cache();
		}

		private void cache() {
			cache.clear();
			cached = 0;
			try {
				while (docList.hasNext() && cached < cachedSize) {
					cache.add(index.getDoc(docList.next()));
					cached++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			list = cache.iterator();
		}

		public boolean hasNext() {
			return list.hasNext() || docList.hasNext();
		}

		public String next() {
			if (!list.hasNext() && docList.hasNext()) {
				cache();
			}
			return list.next();
		}

		public void remove() {
			// TODO Auto-generated method stub

		}
	}
}
