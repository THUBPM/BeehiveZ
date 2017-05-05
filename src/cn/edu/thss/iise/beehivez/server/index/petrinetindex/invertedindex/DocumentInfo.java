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

import java.util.ArrayList;

public class DocumentInfo {
	private String name;
	public ArrayList<Field> content = new ArrayList<Field>();

	public int getSize() {
		return content.size();
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void add(Field f) {
		for (Field field : content) {
			if (field.equal(f)) {
				field.frequency++;
				return;
			}
		}
		content.add(f);
	}

	public void addPhrase(String value) {
		// content.add(new Field(value));
		for (Field field : content) {
			if (field.value.equals(value)) {
				field.frequency++;
				return;
			}
		}
		content.add(new Field(value));
	}

}
