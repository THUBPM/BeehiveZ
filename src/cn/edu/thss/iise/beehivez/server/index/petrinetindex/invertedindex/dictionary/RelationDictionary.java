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

import java.io.File;
import java.io.IOException;

public class RelationDictionary {
	private long unused;
	private long deletedHeader;

	private Cache cache;

	private String folder; // �����ŵĸ�Ŀ¼
	private int cacheSize;

	public RelationDictionary(String folder, int cacheSize) throws Exception {
		if (!folder.endsWith("//"))
			folder += "//";
		this.folder = folder;
		this.cacheSize = cacheSize;
	}

	public void create() {
		String indexFile = folder + "relationDictionary.rdi";
		File file = new File(indexFile);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			cache = new Cache(indexFile, cacheSize, Cache.type_relation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unused = 8;
		deletedHeader = -1;
	}

	public boolean init() {
		// TODO Auto-generated method stub
		String indexFile = folder + "relationDictionary.rdi";
		File file = new File(indexFile);
		if (!file.exists())
			return false;
		try {
			cache = new Cache(indexFile, cacheSize, Cache.type_relation);
			readInfo();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void writeInfo() {
		try {
			cache.writeInfo(new long[] { unused, deletedHeader });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readInfo() {
		try {
			long[] info = cache.readInfo(2);
			unused = info[0];
			deletedHeader = info[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long addRelation(long documentPos) throws Exception {
		RelationItem r = null;
		if (deletedHeader == -1) {
			r = new RelationItem(unused, documentPos, -1);
			unused += r.getLength();
		} else {
			r = getRelationAt(deletedHeader);
			deletedHeader = r.getNext();
			r.setDocumentPos(documentPos);
			r.setNext(-1);
		}

		cache.put(r);
		return r.getPosition();
	}

	public void appendRelation(long lastPos, long documentPos) throws Exception {
		RelationItem r = getRelationAt(lastPos);
		RelationItem rnext = null;
		if (deletedHeader == -1) {
			rnext = new RelationItem(unused, documentPos);
			unused += rnext.getLength();
		} else {
			rnext = getRelationAt(deletedHeader);
			deletedHeader = rnext.getNext();
			rnext.setDocumentPos(documentPos);
		}
		rnext.setNext(r.getNext());
		r.setNext(rnext.getPosition());

		cache.put(rnext);
	}

	public RelationItem getRelationAt(long pos) throws Exception {
		return (RelationItem) cache.get(pos);
	}

	public void remove(RelationItem r) throws Exception {
		r.setNext(deletedHeader);
		deletedHeader = r.getPosition();
		cache.remove(r);
	}

	public void close() throws Exception {
		writeInfo();
		cache.close();
	}

}
