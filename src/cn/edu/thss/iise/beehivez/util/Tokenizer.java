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

public class Tokenizer {
	String value = null;
	int index;
	int length;
	// String token;
	int tokenType;

	public Tokenizer(String v) {
		value = v;
		index = 0;
		length = v.length();
	}

	public int getToken() throws Exception {
		while (index < length && Character.isWhitespace(value.charAt(index))) {
			index++;
		}
		if (index >= length)
			return Token.END;
		char c = value.charAt(index);
		switch (c) {
		case Token.C_OPENBRACKET:
			tokenType = Token.OPENBRACKET;
			index++;
			// token=Token.T_OPENBRACKET;
			break;
		case Token.C_CLOSEBRACKET:
			tokenType = Token.CLOSEBRACKET;
			index++;
			// token=Token.T_CLOSEBRACKET;
			break;
		case Token.C_QUOTE:
			tokenType = Token.QUOTE;
			index++;
			// token=Token.T_QUOTE;
			break;
		case 'A':
		case 'a':
			if (!this.match(Token.T_AND)) {
				throw new Exception("wrong token,is it 'and'?");
			}
			// token=Token.T_AND;
			tokenType = Token.AND;
			break;
		case 'o':
		case 'O':
			if (!this.match(Token.T_OR)) {
				throw new Exception("wrong token,is it 'or'?");
			}
			// token=Token.T_OR;
			tokenType = Token.OR;
			break;
		case 'N':
		case 'n':
			if (!this.match(Token.T_NOT))
				throw new Exception("wrong token,is it 'not'?");
			// token=Token.T_NOT;
			tokenType = Token.NOT;
			break;
		default:
			throw new Exception("unknown token!");
		}
		return tokenType;
	}

	private boolean match(String match) {
		int len = match.length();
		for (int i = 0; i < len; i++, index++) {
			if (!(match.charAt(i) == value.charAt(index) || Math.abs(match
					.charAt(i)
					- value.charAt(index)) == 32)) {
				return false;
			}
		}
		return true;
	}

	public String getString() throws Exception {
		int start = index;
		while (index < length && value.charAt(index) != Token.C_QUOTE)
			index++;
		if (index == length)
			throw new Exception("wrong phrase,need ''' at the end!");
		String result = value.substring(start, index);
		index++;
		return result;
	}

}
