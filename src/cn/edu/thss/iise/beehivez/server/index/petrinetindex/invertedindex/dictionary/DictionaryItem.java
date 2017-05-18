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

import java.io.IOException;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.fileAccess.RAFile;

public abstract class DictionaryItem {
	protected boolean isDirty;
	// private long timestamp;
	protected long position;

	public MyLinkedListNode node = new MyLinkedListNode();

	public DictionaryItem() {
		// timestamp=System.currentTimeMillis();
	}

	public abstract void readIn(RAFile file, long pos) throws IOException;

	public abstract void writeOut(RAFile file) throws Exception;

	public abstract int getLength();

	/**
	 * public void visit() { timestamp=System.currentTimeMillis(); }
	 * 
	 * public long getTimestamp() { return timestamp; }
	 **/

	public long getPosition() {
		return position;
	}

	public boolean getIsDirty() {
		return isDirty;
	}

}
