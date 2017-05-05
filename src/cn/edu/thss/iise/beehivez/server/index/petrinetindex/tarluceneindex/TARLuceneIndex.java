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

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelLuceneIndex;
import cn.edu.thss.iise.beehivez.server.index.labelindex.SimilarLabelQueryResult;
import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonAnalyzer;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;

/**
 * use task adjacent relations to support similar process model query. use
 * lucene to store the inverted index between TARs and models
 * 
 * @author Tao Jin
 * 
 */
public class TARLuceneIndex extends PetriNetIndex {

	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/TARLuceneIndex";
	private static final File INDEX_DIR = new File(indexDirectory);
	private static IndexWriter indexWriter = null;
	private LabelLuceneIndex labelIndex = new LabelLuceneIndex(indexDirectory);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addProcessModel(Object o) {
		PetrinetObject pno = (PetrinetObject) o;
		try {
			// get the object of petri net and process id
			PetriNet pn = pno.getPetriNet();
			if (pn == null) {
				pn = PetriNetUtil.getPetriNetFromPnmlBytes(pno.getPnml());
			}
			if (pn == null) {
				System.out
						.println("null petri net in addPetriNet of TARLuceneIndex");
				return;
			}

			Document doc = PetriNetTARsDocument.Document(pn, pno
					.getProcess_id());
			indexWriter.addDocument(doc);
			indexWriter.commit();

			// update the label index
			for (Transition t : pn.getTransitions()) {
				labelIndex.addLabel(t.getIdentifier().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			if (indexWriter != null) {
				indexWriter.optimize();
				indexWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		labelIndex.close();
	}

	@Override
	public boolean create() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			indexWriter = new IndexWriter(FSDirectory.open(INDEX_DIR),
					new SemicolonAnalyzer(), true,
					IndexWriter.MaxFieldLength.UNLIMITED);

			labelIndex.create();
			labelIndex.addLabel("null");

			DataManager dm = DataManager.getInstance();
			int limit = dm.getPageSize();
			ResultSet rs = dm.executeSelectSQL(
					"select process_id from petrinet order by process_id", 0,
					Integer.MAX_VALUE, dm.getFetchSize());
			while (rs.next()) {
				PetrinetObject pno = new PetrinetObject();

				long process_id = rs.getLong("process_id");
				pno.setProcess_id(process_id);

				PetriNet pn = dm.getProcessPetriNet(process_id);
				pno.setPetriNet(pn);

				this.addProcessModel(pno);
				pn.destroyPetriNet();
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void delProcessModel(Object pno) {
		System.out.println("need to be implemented");
	}

	@Override
	public boolean destroy() {
		close();
		FileUtil.deleteFile(indexDirectory);
		return true;
	}

	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {

		TreeSet<ProcessQueryResult> ret = new TreeSet<ProcessQueryResult>();

		try {
			if (o instanceof PetriNet) {
				PetriNet pn = (PetriNet) o;

				IndexReader reader = IndexReader.open(FSDirectory
						.open(INDEX_DIR), true);
				Searcher searcher = new IndexSearcher(reader);
				BooleanQuery bq = new BooleanQuery();
				bq.setMaxClauseCount(Integer.MAX_VALUE);

				// make it sure that every queryterm is unique
				HashSet<String> expandedTars = new HashSet();

				// expand the query tars with their similar ones
				HashSet<HashSet<String>> exQueryTars = new HashSet<HashSet<String>>();

				// calculate the tars
				Iterator<TransitionLabelPair> itTAR = PetriNetUtil
						.getTARSFromPetriNetByCFP(pn).iterator();

				if (GlobalParameter.isEnableSimilarLabel()) {
					// label similarity is enabled
					while (itTAR.hasNext()) {
						TransitionLabelPair tlp = itTAR.next();
						String tarString = tlp.getFirst().trim()
								+ PetriNetTARsDocument.TARCONNECTOR
								+ tlp.getSecond().trim();

						HashSet<String> similarTars = new HashSet<String>();

						TreeSet<SimilarLabelQueryResult> pres = labelIndex
								.getSimilarLabels(tlp.getFirst().trim(),
										GlobalParameter
												.getLabelSemanticSimilarity());

						TreeSet<SimilarLabelQueryResult> sucs = labelIndex
								.getSimilarLabels(tlp.getSecond().trim(),
										GlobalParameter
												.getLabelSemanticSimilarity());

						Iterator<SimilarLabelQueryResult> itPre = pres
								.iterator();
						while (itPre.hasNext()) {
							String pre = itPre.next().getLabel();
							Iterator<SimilarLabelQueryResult> itSuc = sucs
									.iterator();
							while (itSuc.hasNext()) {
								String suc = itSuc.next().getLabel();
								String tar = pre
										+ PetriNetTARsDocument.TARCONNECTOR
										+ suc;
								if (similarTars.add(tar)) {
									if (expandedTars.add(tar)) {
										Term term = new Term(
												PetriNetTARsDocument.FIELDTARS,
												tar);
										TermQuery termQuery = new TermQuery(
												term);
										bq.add(termQuery, Occur.SHOULD);
									}
								}
							}
						}

						if (similarTars.size() == 0) {
							similarTars.add(tarString);
						}
						exQueryTars.add(similarTars);
					}
				} else {
					// label similarity is not enabled
					while (itTAR.hasNext()) {
						TransitionLabelPair tlp = itTAR.next();
						String tarString = tlp.getFirst().trim()
								+ PetriNetTARsDocument.TARCONNECTOR
								+ tlp.getSecond().trim();

						HashSet<String> similarTars = new HashSet<String>();
						similarTars.add(tarString);

						if (expandedTars.add(tarString)) {
							Term term = new Term(
									PetriNetTARsDocument.FIELDTARS, tarString);
							TermQuery termQuery = new TermQuery(term);
							bq.add(termQuery, Occur.SHOULD);
						}
						exQueryTars.add(similarTars);
					}
				}

				// while (itTAR.hasNext()) {
				// TransitionLabelPair tlp = itTAR.next();
				// String tarString = tlp.getFirst().trim()
				// + PetriNetTARsDocument.TARCONNECTOR
				// + tlp.getSecond().trim();
				//
				// HashSet<String> similarTars = new HashSet<String>();
				//
				// // expand with its similar tars
				// if (GlobalParameter.isEnableSimilarLabel()) {
				// TreeSet<SimilarLabelQueryResult> pres = labelIndex
				// .getSimilarLabels(tlp.getFirst().trim(),
				// GlobalParameter
				// .getLabelSemanticSimilarity());
				//
				// TreeSet<SimilarLabelQueryResult> sucs = labelIndex
				// .getSimilarLabels(tlp.getSecond().trim(),
				// GlobalParameter
				// .getLabelSemanticSimilarity());
				//
				// Iterator<SimilarLabelQueryResult> itPre = pres
				// .iterator();
				// while (itPre.hasNext()) {
				// String pre = itPre.next().getLabel();
				// Iterator<SimilarLabelQueryResult> itSuc = sucs
				// .iterator();
				// while (itSuc.hasNext()) {
				// String suc = itSuc.next().getLabel();
				// String tar = pre
				// + PetriNetTARsDocument.TARCONNECTOR
				// + suc;
				// if (similarTars.add(tar)) {
				// if (expandedTars.add(tar)) {
				// Term term = new Term(
				// PetriNetTARsDocument.FIELDTARS,
				// tar);
				// TermQuery termQuery = new TermQuery(
				// term);
				// bq.add(termQuery, Occur.SHOULD);
				// }
				// }
				// }
				// }
				//
				// if (similarTars.size() == 0) {
				// similarTars.add(tarString);
				// }
				//
				// } else {
				// similarTars.add(tarString);
				//
				// if (expandedTars.add(tarString)) {
				// Term term = new Term(
				// PetriNetTARsDocument.FIELDTARS, tarString);
				// TermQuery termQuery = new TermQuery(term);
				// bq.add(termQuery, Occur.SHOULD);
				// }
				// }
				//
				// exQueryTars.add(similarTars);
				// }

				TARsQueryResultCollector collector = new TARsQueryResultCollector(
						reader, exQueryTars, similarity);
				searcher.search(bq, collector);
				ret = collector.getQueryResult();
				searcher.close();
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public float getStorageSizeInMB() {
		return FileUtil.getFileSizeInMB(indexDirectory);
	}

	// @Override
	// protected void init() {
	// String str = this.getClass().getCanonicalName();
	// this.javaClassName = str;
	// this.name = str.substring(str.lastIndexOf(".") + 1);
	// }

	@Override
	public boolean open() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			indexWriter = new IndexWriter(FSDirectory.open(INDEX_DIR),
					new SemicolonAnalyzer(), false,
					IndexWriter.MaxFieldLength.UNLIMITED);
			labelIndex.open();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean supportGraphQuery() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportTextQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return true;
	}

}
