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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.semanticSimilarity;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import net.didion.jwnl.JWNL;
import shef.nlp.wordnet.similarity.SimilarityInfo;
import shef.nlp.wordnet.similarity.SimilarityMeasure;

/**
 * @author ���
 * 
 */
public class SemanticSimilarity {
	SimilarityMeasure sim = null;
	StopWordSet stopWord;

	public static final double availableValue = 0.3;
	public static final double wordSimilarityValue = 0.89;

	public SemanticSimilarity() {
		try {
			JWNL.initialize(new FileInputStream(
					"src/cn/edu/thss/iise/beehivez/server/source/wordnet.xml"));
			Map<String, String> params = new HashMap<String, String>();

			params.put("simType", "shef.nlp.wordnet.similarity.JCn");

			// this param should be the URL to an infocontent file (if required
			// by the similarity measure being loaded)
			params
					.put("infocontent",
							"file:src/cn/edu/thss/iise/beehivez/server/source/ic-bnc-resnik-add1.dat");

			// this param should be the URL to a mapping file if the
			// user needs to make synset mappings
			params
					.put("mapping",
							"file:src/cn/edu/thss/iise/beehivez/server/source/domain_independent.txt");

			// create the similarity measure

			sim = SimilarityMeasure.newInstance(params);
			stopWord = new StopWordSet(
					"src/cn/edu/thss/iise/beehivez/server/source/stopWords.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double semanticCompare(String w1, String w2) {
		StringTokenizer st1 = new StringTokenizer(w1);
		StringTokenizer st2 = new StringTokenizer(w2);
		HashSet<WordInfo> phrase1 = new HashSet<WordInfo>();
		HashSet<WordInfo> phrase2 = new HashSet<WordInfo>();
		// ArrayList<String> key1=new ArrayList<String>();
		// ArrayList<String> key2=new ArrayList<String>();

		int sameCount = 0;
		int maxCount = 0;
		int simCount = 0;

		while (st1.hasMoreTokens()) {
			String word = st1.nextToken().toLowerCase();

			if (!stopWord.contains(word)) {
				phrase1.add(new WordInfo(word));
			}
		}
		while (st2.hasMoreTokens()) {
			String word = st2.nextToken().toLowerCase();
			if (!stopWord.contains(word)) {
				phrase2.add(new WordInfo(word));
			}
		}

		maxCount = Math.max(phrase1.size(), phrase2.size());
		for (WordInfo s1 : phrase1) {
			// if(s1.isSame)
			// continue;
			for (WordInfo s2 : phrase2) {
				if (s2.isSame)
					continue;
				if (s1.valueEqual(s2)) {
					sameCount++;
					s1.isSame = s2.isSame = true;
					break;
				}
			}
		}
		for (WordInfo s1 : phrase1) {
			if (s1.isSame)
				continue;
			for (WordInfo s2 : phrase2) {
				if (s2.isSame)
					continue;
				try {
					SimilarityInfo simiInfo = sim.getSimilarity(s1.getValue(),
							s2.getValue());
					double temp = simiInfo.getSimilarity();
					System.out.println("simi " + s1.getValue() + " "
							+ s2.getValue() + " is " + temp);
					if (temp >= SemanticSimilarity.wordSimilarityValue) {
						simCount++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ((double) sameCount + 1.0 * (double) simCount)
				/ (double) maxCount;

	}

	public static void main(String[] args) {
		SemanticSimilarity ss = new SemanticSimilarity();
		double d = ss.semanticCompare("myperson", "dog");
		System.out.println(d);
	}
}
