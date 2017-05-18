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

public class RelationItem extends DictionaryItem {
	private long documentPos;
	private long next;

	public RelationItem(long p, long d) {
		super();
		position = p;
		documentPos = d;
		isDirty = true;
	}

	public RelationItem(long p, long d, long n) {
		super();
		position = p;
		documentPos = d;
		next = n;
		isDirty = true;
	}

	public void setDocumentPos(long d) {
		documentPos = d;
		isDirty = true;
	}

	public long getNext() {
		return next;
	}

	public void setNext(long n) {
		next = n;
		isDirty = true;
	}

	public RelationItem() {
		super();

	}

	public long getDocumentPos() {
		return documentPos;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 16;
	}

	@Override
	public void readIn(RAFile file, long pos) throws IOException {
		// TODO Auto-generated method stub
		file.seek(pos);
		position = pos;
		documentPos = file.readLong();
		next = file.readLong();
		isDirty = false;
	}

	@Override
	public void writeOut(RAFile file) throws IOException {
		// TODO Auto-generated method stub
		file.seek(position);
		file.writeLong(documentPos);
		file.writeLong(next);
		isDirty = false;
	}

}
