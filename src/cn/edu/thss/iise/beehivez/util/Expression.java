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
package cn.edu.thss.iise.beehivez.util;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseDictionary;

/**
 * @author ���
 * 
 */
public class Expression {
	public static final int AND = Token.AND;
	public static final int OR = Token.OR;
	public static final int NOT = Token.NOT;
	public static final int VALUE = -2;

	public static final int UNDEFINE = -1;

	private int type;
	private String value = null;
	private Expression left = null;
	private Expression right = null;

	public Expression(Expression sub) {
		left = sub;
		type = Expression.NOT;
	}

	public Expression(String v) {
		value = v;
		type = Expression.VALUE;
	}

	public Expression(int t, Expression l, Expression r) {
		type = t;
		left = l;
		right = r;
	}

	public void setType(int t) {
		type = t;
	}

	public int getType() {
		return type;
	}

	public String getStringValue() {
		return value;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public double getSenmaticValue(String key) {
		switch (type) {
		case Expression.AND:
			return left.getSenmaticValue(key) * right.getSenmaticValue(key);
		case Expression.NOT:
			return 1 - left.getSenmaticValue(key);
		case Expression.OR:
			return Math.max(left.getSenmaticValue(key), right
					.getSenmaticValue(key));
		default:
			return PhraseDictionary.semanticCompare(key, value);
		}
	}

}
