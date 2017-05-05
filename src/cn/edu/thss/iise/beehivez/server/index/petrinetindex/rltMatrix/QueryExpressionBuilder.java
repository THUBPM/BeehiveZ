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

import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex.PathQueryExpression;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.queryParser.ParserTreeConstants;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.queryParser.SimpleNode;

public class QueryExpressionBuilder implements ParserTreeConstants {
	private static QueryExpressionBuilder instance = new QueryExpressionBuilder();

	public static QueryExpressionBuilder getInstance() {
		return instance;
	}

	public void bulidQueryExpression(SimpleNode tree, PathQueryExpression qe) {
		switch (tree.getType()) {
		case ParserTreeConstants.JJTACTIVITY:
			qe.addAtom(tree.jjtGetValue().toString(),
					PathQueryExpression.Atom.L1P);
			break;
		case ParserTreeConstants.JJTOR:
			PathQueryExpression item1 = new PathQueryExpression();
			item1.setType(PathQueryExpression.OR);
			for (int i = 0; i < tree.jjtGetNumChildren(); i++) {
				SimpleNode cur = (SimpleNode) tree.jjtGetChild(i);
				bulidQueryExpression(cur, item1);
			}
			qe.addExpression(item1);
			break;
		case ParserTreeConstants.JJTAND:
		case ParserTreeConstants.JJTOP:
		case ParserTreeConstants.JJTSTART:
			PathQueryExpression item2 = new PathQueryExpression();
			item2.setType(PathQueryExpression.AND);
			for (int i = 0; i < tree.jjtGetNumChildren(); i++) {
				SimpleNode cur = (SimpleNode) tree.jjtGetChild(i);
				bulidQueryExpression(cur, item2);
			}
			qe.addExpression(item2);
			break;
		}
	}

	public void buildRltQueryExpression(SimpleNode tree, RltQueryExpression qe) {
		int curInx = 0;
		Object curObj = null;
		RltQueryExpression result = new RltQueryExpression();
		RltQueryAtom tom = new RltQueryAtom();

	}

}
