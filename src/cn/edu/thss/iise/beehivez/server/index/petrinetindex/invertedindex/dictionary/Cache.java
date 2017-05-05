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
 * ʵ�ֻ���,�������Ӧ�ü̳� DictionaryItem
 * ��������ʵ���������ƣ�ʹ��������ٱ�ʹ�õĲ���������Ļ������
 * ʹ��һ�������¼����˳������ɾ�����Ӳ���Ҫ�����ٶȿ�
 * cache�����ʻ�����ݵ��κ��򣬳����ж����Ƿ�Ϊ����ݣ���Ҫд��
 */

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.fileAccess.BufferedRandomAccessFile;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.fileAccess.RAFile;

public class Cache {
	private Hashtable<Long, DictionaryItem> cache = new Hashtable<Long, DictionaryItem>();
	private int cacheSize = 1024;
	private int amount = 0;

	private MyLinkedList accessList = new MyLinkedList();

	RAFile file;
	String fileName;

	private final int cacheType;

	public static final int type_phrase = 0;
	public static final int type_relation = 1;
	public static final int type_document = 2;

	public Cache(String path, int size, int type) throws Exception {
		file = new BufferedRandomAccessFile(path, 1024, false);
		fileName = path;
		cacheSize = size;
		cacheType = type;
	}

	public String getFilePath() {
		return fileName;
	}

	public long getFileLength() throws IOException {
		return file.length();
	}

	public void put(DictionaryItem item) throws Exception {
		addItem(item);
	}

	public DictionaryItem get(long pos) throws IOException, Exception {
		if (cache.containsKey(pos)) {
			DictionaryItem it = cache.get(pos);

			accessList.remove(it.node);
			accessList.addFirst(it.node);

			// it.visit();
			return it;
		}
		DictionaryItem item = null;
		switch (cacheType) {
		case type_phrase:
			item = new PhraseItem();
			break;
		case type_relation:
			item = new RelationItem();
			break;
		case type_document:
			item = new DocumentItem();
			break;
		}
		item.readIn(file, pos);
		addItem(item);
		return item;

	}

	private void addItem(DictionaryItem item) throws Exception {
		if (this.amount >= this.cacheSize)
			this.cleanUp();

		item.node.key = item.getPosition();
		accessList.addFirst(item.node);
		cache.put(item.getPosition(), item);
		amount++;
	}

	private void releaseItem(Object key) throws Exception {
		DictionaryItem p = cache.get(key);
		if (p.isDirty) {
			p.writeOut(file);
		}

		cache.remove(key);
		amount--;
	}

	public void remove(DictionaryItem item) throws Exception {
		accessList.remove(item.node);
		releaseItem(item.getPosition());
	}

	private void cleanUp() throws Exception {
		MyLinkedListNode node = accessList.removeLast();
		releaseItem(node.key);

	}

	public void flush() throws Exception {
		Iterator it = cache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, DictionaryItem> entry = (Map.Entry) it.next();
			DictionaryItem p = entry.getValue();
			if (p.isDirty) {
				p.writeOut(file);
				p.isDirty = false;
			}
		}
	}

	public void writeInfo(long[] info) throws IOException {
		file.seek(0);
		for (long value : info) {
			file.writeLong(value);
		}
	}

	public long[] readInfo(int length) throws IOException {
		file.seek(0);
		long[] value = new long[length];
		for (int i = 0; i < length; i++)
			value[i] = file.readLong();
		return value;
	}

	public void close() throws Exception {
		this.flush();
		file.close();
		cache.clear();
	}

	/**
	 * private void cleanUp(int cleanSize) throws Exception { //Object key=null;
	 * long time=-1; long[] clean=new long[cleanSize]; int size=0; int index =
	 * 0;
	 * 
	 * 
	 * Iterator it = cache.entrySet().iterator(); while(it.hasNext()) {
	 * Map.Entry <Long,DictionaryItem> entry = (Map.Entry) it.next(); long
	 * v=entry.getValue().getTimestamp();
	 * 
	 * if(size<cleanSize) { clean[size]=entry.getKey(); if(v>time) { time=v;
	 * index=size; } size++; } else if(v<time) { clean[index]=entry.getKey();
	 * time=-1; for(int i=0;i<size;i++) { long
	 * t=cache.get(clean[i]).getTimestamp(); if(t>time) { time=t; index=i; } } }
	 * } for(long l:clean) releaseItem(l); System.gc(); }
	 * 
	 * 
	 * private void cleanUp() throws Exception { Object key=null; long
	 * time=System.currentTimeMillis();
	 * 
	 * 
	 * 
	 * Iterator it = cache.entrySet().iterator(); while(it.hasNext()) {
	 * Map.Entry <Long,DictionaryItem> entry = (Map.Entry) it.next(); long
	 * v=entry.getValue().getTimestamp();
	 * 
	 * if(v<time) { key=entry.getKey(); time=v; } } releaseItem(key);
	 * System.gc(); }
	 **/

}
