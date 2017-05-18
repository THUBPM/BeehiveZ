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

public class DocumentItem extends DictionaryItem {
	public String value;
	public boolean isDelete = false;
	private long[] phrases;
	private int size = 0;

	/**
	 * �����ʱ ʹ�õĹ��캯��
	 * 
	 * @param pos
	 * @param v
	 */
	public DocumentItem(long pos, String v, int phraseAccount) {
		super();
		value = v;
		position = pos;
		isDirty = true;
		phrases = new long[phraseAccount];
	}

	/**
	 * ����ʱ�Ĺ��캯��
	 */
	public DocumentItem() {
		super();
	}

	public void addPhrase(long v) {
		phrases[size] = v;
		size++;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 9 + value.length() + (phrases.length << 3);
	}

	@Override
	public void readIn(RAFile file, long pos) throws IOException {
		// TODO Auto-generated method stub
		file.seek(pos);
		position = pos;
		isDirty = false;
		isDelete = file.readBoolean();
		int len = file.readInt();
		byte[] key = new byte[len];
		file.read(key);
		value = new String(key);
		size = file.readInt();
		phrases = new long[size];
		for (int i = 0; i < size; i++)
			phrases[i] = file.readLong();
	}

	@Override
	public void writeOut(RAFile file) throws Exception {
		// TODO Auto-generated method stub
		if (phrases.length != size)
			throw new Exception("some phrases haven't been added!");
		file.seek(position);
		file.writeBoolean(isDelete);
		file.writeInt(value.length());
		file.write(value.getBytes());
		file.writeInt(phrases.length);
		isDirty = false;
		for (long l : phrases)
			file.writeLong(l);
	}

	public long[] getPhrases() {
		return phrases;
	}

	public void delete() {
		isDirty = true;
		isDelete = true;
	}

}
