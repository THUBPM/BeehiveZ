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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex;

/**
 * @author Tao Jin
 * used for query
 */
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.queryParser.SimpleNode;

public class PathQueryExpression {

	public static final int AND = 1;
	public static final int OR = 2;
	public static final int NOT = 3;

	private int type = AND;
	private Vector items = new Vector();

	public Vector getItems() {
		return items;
	}

	public int getType() {
		return type;
	}

	public void setType(int t) {
		this.type = t;
	}

	public void addAtom(String atom, int type) {
		Atom a = new Atom(atom, type);
		items.add(a);
	}

	public void addExpression(PathQueryExpression e) {
		items.add(e);
	}

	public static class Atom {
		public static final int L1P = 1;
		public static final int L2P = 2;

		private String atom;
		private int type;

		public Atom(String atom, int type) {
			this.atom = atom;
			this.type = type;
		}

		public String getAtom() {
			return atom;
		}

		public void setAtom(String atom) {
			this.atom = atom;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return atom;
		}
	}

	public void dump(String prefix) {
		if (items != null && !items.isEmpty()) {
			for (int i = 0; i < items.size(); ++i) {
				Object o = items.get(i);
				if (o instanceof PathQueryExpression) {
					PathQueryExpression q = (PathQueryExpression) o;
					System.out.println(prefix + q.type);
					q.dump(prefix + "  ");
				} else {
					Atom a = (Atom) o;
					System.out.println(prefix + a);
				}

			}
		}
	}

}
