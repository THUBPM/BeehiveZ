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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

/**
 * @author Tao Jin
 * 
 *         A SemicolonTokenizer is a tokenizer that divides text at semicolon.
 *         Adjacent sequences of non-semicolon characters form tokens.
 */

public class SemicolonTokenizer extends CharTokenizer {

	public static final char delimiter = ';';

	public SemicolonTokenizer(AttributeSource source, Reader input) {
		super(source, input);
	}

	public SemicolonTokenizer(Reader input) {
		super(input);
	}

	public SemicolonTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.analysis.CharTokenizer#isTokenChar(char)
	 * 
	 * collect characters which are not comma
	 */
	@Override
	protected boolean isTokenChar(char c) {

		if (c == delimiter) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
