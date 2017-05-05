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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary;

/**
 * 
 * @author ���
 * 
 * @param <K>
 */

public class MyLinkedList {
	public MyLinkedListNode first = null;
	public MyLinkedListNode last = null;

	public void addFirst(MyLinkedListNode n) {
		if (first == null) {
			first = n;
			last = n;
			n.last = null;
			n.next = null;
		} else {
			n.next = first;
			first.last = n;
			first = n;
			n.last = null;
		}
	}

	public void addLast(MyLinkedListNode n) {
		if (first == null) {
			first = n;
			last = n;
			n.last = null;
			n.next = null;
		} else {

			n.next = null;
			last.next = n;
			n.last = this.last;
			last = n;
		}
	}

	public MyLinkedListNode getLast() {
		return last;
	}

	public MyLinkedListNode removeLast() {
		MyLinkedListNode n = last;
		last = n.last;
		last.next = null;
		n.next = n.last = null;
		return n;
	}

	public void remove(MyLinkedListNode n) {
		if (n.last == null && n.next == null) {
			first = last = null;
		} else if (n.last == null) {
			first = n.next;
			n.next.last = null;
			n.last = n.next = null;
		} else if (n.next == null) {
			last = n.last;
			last.next = null;
			n.last = n.next = null;
		} else {
			n.last.next = n.next;
			n.next.last = n.last;
		}
	}
}
