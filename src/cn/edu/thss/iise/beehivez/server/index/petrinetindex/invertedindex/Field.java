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

/**
 * @author acer
 * 
 */
public class Field {
	public String value;
	public long positionInFile = -1;
	public int frequency = 0;

	public int type;

	public final static int INDEX = 1;
	public final static int SEARCHABLE = 2;

	public Field(String v) {
		value = v;
	}

	public Field(String v, int t) {
		value = v;
		type = t;
	}

	public long getPos() {
		return positionInFile;
	}

	public void setPos(long v) {
		positionInFile = v;
	}

	public boolean equal(Field f) {
		if (f.type != this.type)
			return false;
		if (!f.value.equals(this.value))
			return false;
		return true;
	}
}
