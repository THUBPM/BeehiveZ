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
import java.util.LinkedList;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.DocumentInfo;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.Field;

/**
 * no index structure
 * 
 * @author ���
 * 
 */
public class DocumentDictionary {

	private long unused;
	private Cache cache = null;

	private String folder; // �����ŵĸ�Ŀ¼
	private int cacheSize;

	public DocumentDictionary(String folder, int cacheSize) {
		if (!folder.endsWith("//"))
			folder += "//";
		this.folder = folder;
		this.cacheSize = cacheSize;
	}

	public void create() {
		String indexFile = folder + "docDictionary.ddi";
		File file = new File(indexFile);
		if (file.exists()) {
			file.delete();

		}
		try {
			file.createNewFile();
			cache = new Cache(indexFile, cacheSize, Cache.type_document);
			unused = 8;
			writeInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean init() {
		// TODO Auto-generated method stub
		String indexFile = folder + "docDictionary.ddi";
		File file = new File(indexFile);
		if (!file.exists())
			return false;
		try {
			cache = new Cache(indexFile, cacheSize, Cache.type_document);
			readInfo();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void readInfo() throws IOException {
		unused = cache.readInfo(1)[0];
	}

	private void writeInfo() throws IOException {
		cache.writeInfo(new long[] { unused });
	}

	public long getNextDocumentPosition() {
		return unused;
	}

	public long putDoc(DocumentInfo docInfo) throws Exception {
		DocumentItem d = new DocumentItem(unused, docInfo.getName(), docInfo
				.getSize());

		for (Field f : docInfo.content) {
			d.addPhrase(f.getPos());
		}

		unused += d.getLength();
		cache.put(d);
		return d.getPosition();
	}

	public DocumentItem getDoc(long pos) throws Exception {
		return (DocumentItem) cache.get(pos);
	}

	public void close() throws Exception {
		writeInfo();
		cache.close();
	}

	public void remove(DocumentItem doc) throws Exception {
		cache.remove(doc);
	}

	/**
	 * ����һ��ɾ���־��ͬʱɾ���ϵ ������������涼����ɾ���������ʹ��
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public LinkedList<Long> delete(String value) throws Exception {

		// RandomAccessFile file=new RandomAccessFile(cache.getFilePath(),"rw");
		LinkedList<Long> result = new LinkedList<Long>();
		long pos = 8;
		// cache.flush();
		// file.seek(0);
		while (true) {
			if (pos >= unused)
				break;
			DocumentItem doc = (DocumentItem) cache.get(pos);
			if (!doc.isDelete && doc.value.equals(value)) {
				doc.delete();
				result.add(doc.getPosition());
			}
			pos += doc.getLength();
		}
		return result;
	}

}
