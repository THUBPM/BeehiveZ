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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;

public class RltQueryAtom implements RltConstants {
	public static final int SINGLE = 1;
	public static final int DOUBLE = 2;
	public static final int AND = 1;
	public static final int OR = 2;
	private String leftT = null;
	private String rightT = null;
	private byte relationship = 0;
	private String strRS = null;
	private int type = DOUBLE;
	private int boolConn = AND;

	public RltQueryAtom(String lt, String rt, byte rs) {
		boolean flag = false;
		leftT = lt;
		rightT = rt;

		for (int i = 0; i < RltConstants.ARR_BIT_RELATIONS.length
				&& flag == false; i++) {
			if (rs == RltConstants.ARR_BIT_RELATIONS[i]) {
				flag = true;
				relationship = rs;
				strRS = ARR_RELATIONS[i];
			}
		}
	}

	public boolean isOr() {
		return boolConn == OR;
	}

	public void setType(int t) {
		type = t;
	}

	public int getType() {
		return type;
	}

	public boolean isDouble() {
		return type == DOUBLE;
	}

	public void setBoolConn(int b) {
		boolConn = b;
	}

	public RltQueryAtom(String t) {
		type = SINGLE;
		leftT = t;
	}

	public RltQueryAtom(String lt, String rt, String rs) {
		boolean flag = false;
		leftT = lt;
		rightT = rt;
		relationship = 0;
		strRS = rs;
		for (int i = 0; i < RltConstants.ARR_RELATIONS.length && flag == false; i++) {
			if (rs.equals(RltConstants.ARR_RELATIONS[i])) {
				flag = true;
				relationship = RltConstants.ARR_BIT_RELATIONS[i];
			}
		}
	}

	public RltQueryAtom() {
		// TODO Auto-generated constructor stub
	}

	public String getQueryString() {
		if (type == DOUBLE)
			return leftT + relationship + rightT;
		else
			return leftT;
	}

	public void setQueryL(String t) {
		leftT = t;
	}

	public String getQueryL() {
		return leftT;
	}

	public void setQueryR(String t) {
		rightT = t;
	}

	public String getQueryR() {
		return rightT;
	}

	public String getQueryLR() {
		return leftT + rightT;
	}

	public void setRelationship(byte rs) {
		boolean flag = false;

		for (int i = 0; i < RltConstants.ARR_BIT_RELATIONS.length
				&& flag == false; i++) {
			if (rs == RltConstants.ARR_BIT_RELATIONS[i]) {
				flag = true;
				relationship = rs;
				strRS = ARR_RELATIONS[i];
			}
		}
	}

	public byte getRelationship() {
		return relationship;
	}

	@Override
	public String toString() {
		String b = "OR";
		if (boolConn == AND) {
			b = "AND";
		}
		if (type == DOUBLE)
			return b + "," + leftT + "," + strRS + "," + rightT;
		else
			return b + "," + leftT;
	}

}
