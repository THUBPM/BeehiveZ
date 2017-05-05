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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.queryParser;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.InvertedListIndex;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.Scanner;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseItem;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.semanticSimilarity.SemanticSimilarity;
import cn.edu.thss.iise.beehivez.util.Expression;
import cn.edu.thss.iise.beehivez.util.Parser;

public class QueryExecution {
	Parser parser;
	Expression expression;
	InvertedListIndex index;

	public QueryExecution(String query, InvertedListIndex in) {
		parser = new Parser(query);
		index = in;
	}

	public Result excute() throws Exception {
		Result result = new Result();
		Scanner scanner = index.getPhraseSanner();
		expression = parser.parseQuery();

		while (scanner.hasNext()) {
			PhraseItem phrase = (PhraseItem) scanner.next();
			double sim = expression.getSenmaticValue(phrase.getValue());
			if (sim > SemanticSimilarity.availableValue) {
				QueryResultItem r = new QueryResultItem(phrase.getPosition(),
						sim);
				result.add(r);
			}
		}

		result.rank();
		return result;
	}

	/**
	 * private Collection getCollection(Expression exp) throws Exception {
	 * switch(exp.getType()) { case Expression.AND: return
	 * Collection.intersection( getCollection(exp.getLeft()),
	 * getCollection(exp.getRight())); case Expression.OR: return
	 * Collection.union( getCollection(exp.getLeft()),
	 * getCollection(exp.getRight())); case Expression.NOT: Collection
	 * c=getCollection(exp.getLeft()); c.setIsNot(); return c; case
	 * Expression.VALUE: return new Collection(
	 * index.semanticSearch(exp.getStringValue())); default: throw new
	 * Exception("the expression type is unknown!"); } }
	 **/
}
