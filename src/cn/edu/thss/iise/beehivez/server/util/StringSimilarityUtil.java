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
package cn.edu.thss.iise.beehivez.server.util;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.apache.lucene.wordnet.SynonymMap;
import org.tartarus.snowball.ext.EnglishStemmer;

import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelDocument;
import cn.edu.thss.iise.beehivez.server.index.labelindex.SynonymIndex;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author Tao Jin
 * 
 */
public class StringSimilarityUtil {

	private static EnglishStemmer stemer = new EnglishStemmer();

	public static float semanticSimilarity(String label1, String label2) {
		return diceSemanticSimilarity(snowballTokenize(label1),
				snowballTokenize(label2));
	}

	/**
	 * Dice coefficient is a term based similarity measure (0-1) whereby the
	 * similarity measure is defined as twice the number of terms common to
	 * compared entitys divided by the total number of terms in both tested
	 * entities. The Coefficient result of 1 indicates identical vectors as
	 * where a 0 equals orthogonal vectors.
	 * 
	 * Dices coefficient = (2*Common Terms) / (Number of terms in String1 +
	 * Number of terms in String2)
	 * 
	 * semantic means we use synonyms from WordNet
	 * 
	 * the label must be tokenized before this function called
	 * 
	 * @param termSet1
	 * @param termSet2
	 * @return
	 */
	public static float diceSemanticSimilarity(HashSet<String> termSet1,
			HashSet<String> termSet2) {
		// calculate the similarity
		float numerator = 0;
		float denominator = 0;
		float similarity = 0;

		SynonymMap synMap = SynonymIndex.getSynonymMap();
		Iterator<String> it = termSet1.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (termSet2.contains(s)) {
				numerator += 1;
				continue;
			}
			for (String syn : synMap.getSynonyms(s)) {
				stemer.setCurrent(syn);
				stemer.stem();
				syn = stemer.getCurrent();
				if (termSet2.contains(syn)) {
					numerator += 1;
					break;
				}
			}
		}

		denominator = termSet1.size() + termSet2.size();
		similarity = (2 * numerator) / denominator;

		return similarity;
	}

	/**
	 * tokenize the given string, all the words are extracted, lowercased, all
	 * the stop words are removed, and all the words are replaced with their
	 * stem
	 * 
	 * @param label
	 * @return
	 */
	public static HashSet<String> snowballTokenize(String label) {
		HashSet<String> ret = new HashSet<String>();
		try {
			Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_CURRENT,
					"English", StandardAnalyzer.STOP_WORDS_SET);

			TokenStream stream = analyzer.tokenStream(
					LabelDocument.FIELD_LABEL, new StringReader(label));
			TermAttribute termAtt = stream.addAttribute(TermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				ret.add(termAtt.term());
			}
			stream.end();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
