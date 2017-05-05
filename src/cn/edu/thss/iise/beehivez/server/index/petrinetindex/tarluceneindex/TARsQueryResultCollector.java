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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import java.util.TreeSet;

import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author Tao Jin
 * 
 */
public class TARsQueryResultCollector extends Collector {
	private int docBase;
	private Scorer scorer;
	private IndexReader reader;

	// the query term set has already been expanded with its similar ones
	private HashSet<HashSet<String>> exQueryTermSet;
	private float similarity;
	private TreeSet<ProcessQueryResult> queryResult = new TreeSet<ProcessQueryResult>();

	public TARsQueryResultCollector(IndexReader reader,
			HashSet<HashSet<String>> exQueryTermSet, float similarity) {
		this.reader = reader;
		this.exQueryTermSet = exQueryTermSet;
		this.similarity = similarity;
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	@Override
	public void collect(int doc) throws IOException {
		int docNum = doc + docBase;

		// get the process id
		String strProcessID = reader.document(docNum).get(
				PetriNetTARsDocument.FIELDPROCESSID);
		if (strProcessID == null) {
			return;
		}
		long process_id = Long.parseLong(strProcessID);

		// get the term set from current doc
		TermFreqVector termFreqVector = reader.getTermFreqVector(docNum,
				PetriNetTARsDocument.FIELDTARS);
		HashSet<String> docTermSet = new HashSet<String>();
		for (String str : termFreqVector.getTerms()) {
			docTermSet.add(str);
		}

		// calculate the tar similarity
		float numerator = 0;
		Iterator<HashSet<String>> itQuery = exQueryTermSet.iterator();
		while (itQuery.hasNext()) {
			HashSet<String> simTars = itQuery.next();
			Iterator<String> itTar = simTars.iterator();
			while (itTar.hasNext()) {
				String tar = itTar.next();
				if (docTermSet.contains(tar)) {
					numerator++;
					break;
				}
			}
		}

		float denominator = docTermSet.size() + exQueryTermSet.size()
				- numerator;

		float score = numerator / denominator;

		if (score >= this.similarity) {
			queryResult.add(new ProcessQueryResult(process_id, score));
		}
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
		this.docBase = docBase;
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}

	/**
	 * @return the queryResult
	 */
	public TreeSet<ProcessQueryResult> getQueryResult() {
		return queryResult;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
