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

package cn.edu.thss.iise.beehivez.util;

public class Token {
	public static final String T_OPENBRACKET = "(";
	public static final String T_CLOSEBRACKET = ")";

	public static final String T_AND = "AND";
	public static final String T_OR = "OR";
	public static final String T_NOT = "NOT";
	public static final String T_QUOTE = "'";

	public static final char C_OPENBRACKET = '(';
	public static final char C_CLOSEBRACKET = ')';
	public static final char C_QUOTE = '\'';

	public static final int OPENBRACKET = 0;
	public static final int CLOSEBRACKET = 1;
	public static final int AND = 2;
	public static final int OR = 3;
	public static final int NOT = 4;
	public static final int QUOTE = 5;

	public static final int PHRASE = 6;
	public static final int UNDEFINED = -1;

	public static final int END = -7;
}
