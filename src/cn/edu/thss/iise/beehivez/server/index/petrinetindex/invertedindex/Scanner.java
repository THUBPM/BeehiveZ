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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex;

import java.util.Iterator;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseDictionary;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseItem;

public class Scanner implements Iterator {
	private long pos = 16;
	private long len;
	private PhraseDictionary dic;

	public Scanner(PhraseDictionary pd) {
		dic = pd;
		len = dic.getUnused();
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		return pos < len;
	}

	public Object next() {
		// TODO Auto-generated method stub
		PhraseItem phrase = dic.getPhrase(pos);
		pos += phrase.getLength();
		return phrase;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

}