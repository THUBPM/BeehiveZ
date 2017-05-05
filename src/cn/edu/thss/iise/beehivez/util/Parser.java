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

public class Parser {
	Tokenizer tokenizer;
	int token;

	public Parser(String query) {
		tokenizer = new Tokenizer(query);
	}

	private Expression parseOr() throws Exception {
		Expression r = parseAnd();
		while (token == Token.OR) {
			Expression a = r;
			readToken();
			r = new Expression(Token.OR, a, parseAnd());
		}
		return r;
	}

	private Expression parseAnd() throws Exception {
		Expression r = parsePhrase();
		while (token == Token.AND) {
			Expression a = r;
			readToken();
			r = new Expression(Token.AND, a, parsePhrase());
		}
		return r;
	}

	private Expression parsePhrase() throws Exception {
		Expression r = null;
		switch (token) {
		case Token.NOT:
			readToken();
			return new Expression(parsePhrase());
		case Token.OPENBRACKET:
			readToken();
			r = parseOr();
			if (token != Token.CLOSEBRACKET)
				throw new Exception("miss ')'");
			readToken();
			break;
		case Token.QUOTE:
			r = new Expression(getPhrase());
			readToken();
			break;
		}
		return r;
	}

	private void readToken() throws Exception {
		token = tokenizer.getToken();
	}

	private String getPhrase() throws Exception {
		return tokenizer.getString();
	}

	public Expression parseQuery() throws Exception {
		readToken();
		return parseOr();
	}

}
