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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.taskedgeindex;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelLuceneIndex;
import cn.edu.thss.iise.beehivez.server.index.labelindex.SimilarLabelQueryResult;
import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonAnalyzer;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.metric.mcessimilarity.TaskLine4PetriNet;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * use MCES-based similarity to measure the similarity between Petri nets based
 * on structure. use lucene to store the relations between task edges and Petri
 * nets.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 * 
 */
public class TaskEdgeLuceneIndex extends PetriNetIndex {

	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/TaskEdgeLuceneIndex";
	private static final File INDEX_DIR = new File(indexDirectory);
	private static IndexWriter indexWriter = null;
	private LabelLuceneIndex labelIndex = new LabelLuceneIndex(indexDirectory);

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

			Document doc = PetriNetTaskEdgesDocument.Document(pn, pno
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
			// labelIndex.addLabel("null");

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
				HashSet<String> expandedTaskEdges = new HashSet();

				// expand the query task edges with their similar ones
				HashSet<HashSet<String>> exQueryTaskEdges = new HashSet<HashSet<String>>();

				// calculate the task edges of query Petri net
				ArrayList<TaskLine4PetriNet> tls = TaskLine4PetriNet
						.getAllTaskLinesOfPetriNet(pn);

				if (GlobalParameter.isEnableSimilarLabel()) {
					// label similarity is enabled
					for (TaskLine4PetriNet tl : tls) {
						String taskEdgeString = tl.getSrcTransition()
								.getIdentifier().trim()
								+ PetriNetTaskEdgesDocument.TASKEDGECONNECTOR
								+ tl.getDestTransition().getIdentifier().trim();

						HashSet<String> similarTaskEdges = new HashSet<String>();

						TreeSet<SimilarLabelQueryResult> pres = labelIndex
								.getSimilarLabels(tl.getSrcTransition()
										.getIdentifier().trim(),
										GlobalParameter
												.getLabelSemanticSimilarity());

						TreeSet<SimilarLabelQueryResult> sucs = labelIndex
								.getSimilarLabels(tl.getDestTransition()
										.getIdentifier().trim(),
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
								String taskEdge = pre
										+ PetriNetTaskEdgesDocument.TASKEDGECONNECTOR
										+ suc;
								if (similarTaskEdges.add(taskEdge)) {
									if (expandedTaskEdges.add(taskEdge)) {
										Term term = new Term(
												PetriNetTaskEdgesDocument.FIELDTASKEDGES,
												taskEdge);
										TermQuery termQuery = new TermQuery(
												term);
										bq.add(termQuery, Occur.SHOULD);
									}
								}
							}
						}

						if (similarTaskEdges.size() == 0) {
							similarTaskEdges.add(taskEdgeString);
						}
						exQueryTaskEdges.add(similarTaskEdges);
					}
				} else {
					// label similarity is not enabled
					for (TaskLine4PetriNet tl : tls) {
						String taskEdgeString = tl.getSrcTransition()
								.getIdentifier().trim()
								+ PetriNetTaskEdgesDocument.TASKEDGECONNECTOR
								+ tl.getDestTransition().getIdentifier().trim();

						HashSet<String> similarTaskEdges = new HashSet<String>();
						similarTaskEdges.add(taskEdgeString);

						if (expandedTaskEdges.add(taskEdgeString)) {
							Term term = new Term(
									PetriNetTaskEdgesDocument.FIELDTASKEDGES,
									taskEdgeString);
							TermQuery termQuery = new TermQuery(term);
							bq.add(termQuery, Occur.SHOULD);
						}
						exQueryTaskEdges.add(similarTaskEdges);
					}
				}

				TaskEdgesQueryResultCollector collector = new TaskEdgesQueryResultCollector(
						reader, exQueryTaskEdges, similarity);
				searcher.search(bq, collector);
				TreeSet<ProcessQueryResult> temp = collector.getQueryResult();
				searcher.close();
				reader.close();

				// verify the candidate model
				Iterator<ProcessQueryResult> it = temp.iterator();
				while (it.hasNext()) {
					ProcessQueryResult pqr = it.next();
					long id = pqr.getProcess_id();
					DataManager dm = DataManager.getInstance();
					PetriNet c = dm.getProcessPetriNet(id);
					float mcesSimilarity = PetriNetUtil.mcesSimilarity(c, pn);
					if (mcesSimilarity >= similarity) {
						ret.add(new ProcessQueryResult(id, mcesSimilarity));
					}
				}
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
		return true;
	}

	@Override
	public boolean supportSimilarQuery() {
		return true;
	}

	@Override
	public boolean supportTextQuery() {
		return false;
	}

	@Override
	public boolean supportSimilarLabel() {
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
