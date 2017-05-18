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
package cn.edu.thss.iise.beehivez.server.index.yawlindex.yawltasksluceneindex;

import java.io.File;
import java.io.IOException;
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
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4YAWL;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.VF24YAWL;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelLuceneIndex;
import cn.edu.thss.iise.beehivez.server.index.labelindex.SimilarLabelQueryResult;
import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonAnalyzer;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex.PetriNetTARsDocument;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex.TARsQueryResultCollector;
import cn.edu.thss.iise.beehivez.server.index.yawlindex.YAWLIndex;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author Tao Jin
 * 
 */
public class YAWLTasksLuceneIndex extends YAWLIndex {
	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/YawlTasksLuceneIndex";
	private static final File INDEX_DIR = new File(indexDirectory);
	private static IndexWriter indexWriter = null;
	private LabelLuceneIndex labelIndex = new LabelLuceneIndex(indexDirectory);

	@Override
	public void addProcessModel(Object o) {
		ProcessObject po = (ProcessObject) o;
		try {
			// get the object of yawl net and process id
			YNet net = (YNet) po.getObject();
			if (net == null) {
				net = YAWLUtil.getYNetFromDefinition(po.getDefinition());
				po.setObject(net);
			}
			if (net == null) {
				System.out
						.println("null yawl model in addProcessModel of TaskLuceneIndex");
				return;
			}

			Document doc = YAWLTasksDocument.Document(net, po.getProcess_id());
			indexWriter.addDocument(doc);
			indexWriter.commit();

			// update the label index
			for (YTask task : net.getNetTasks()) {
				labelIndex.addLabel(task.getName().trim());
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
					"select process_id from process where type='"
							+ ProcessObject.TYPEYAWL + "'", 0,
					Integer.MAX_VALUE, dm.getFetchSize());
			while (rs.next()) {
				ProcessObject po = new ProcessObject();
				long process_id = rs.getLong("process_id");
				po.setProcess_id(process_id);

				byte[] definition = dm.getProcessDefinitionBytes(process_id);
				po.setDefinition(definition);
				po.setObject(YAWLUtil.getYNetFromDefinition(definition));

				this.addProcessModel(po);
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
	public void delProcessModel(Object o) {
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
			if (o instanceof YNet) {
				YNet query = (YNet) o;

				IndexReader reader = IndexReader.open(FSDirectory
						.open(INDEX_DIR), true);
				Searcher searcher = new IndexSearcher(reader);
				BooleanQuery bq = new BooleanQuery();
				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

				// expand the tasks with their similar ones
				HashSet<String> expandedTasks = new HashSet<String>();

				if (GlobalParameter.isEnableSimilarLabel()) {
					// label similarity is enabled
					for (YTask task : query.getNetTasks()) {
						String taskName = task.getName().trim();
						if (expandedTasks.add(taskName)) {
							BooleanQuery subq = new BooleanQuery();
							// Term term = new
							// Term(YAWLTasksDocument.FIELDTASKS,
							// taskName);
							// TermQuery termQuery = new TermQuery(term);
							// subq.add(termQuery, Occur.SHOULD);

							TreeSet<SimilarLabelQueryResult> similarTasks = labelIndex
									.getSimilarLabels(taskName, GlobalParameter
											.getLabelSemanticSimilarity());
							Iterator<SimilarLabelQueryResult> it = similarTasks
									.iterator();
							while (it.hasNext()) {
								SimilarLabelQueryResult sl = it.next();
								String similarTaskName = sl.getLabel();
								Term term = new Term(
										YAWLTasksDocument.FIELDTASKS,
										similarTaskName);
								TermQuery termQuery = new TermQuery(term);
								subq.add(termQuery, Occur.SHOULD);
							}
							if (subq.getClauses().length > 0) {
								bq.add(subq, Occur.MUST);
							} else {
								return ret;
							}
						}
					}
				} else {
					// label similarity is not enabled
					for (YTask task : query.getNetTasks()) {
						String taskName = task.getName().trim();
						if (expandedTasks.add(taskName)) {
							Term term = new Term(YAWLTasksDocument.FIELDTASKS,
									taskName);
							TermQuery termQuery = new TermQuery(term);
							bq.add(termQuery, Occur.MUST);
						}
					}
				}

				// for (YTask task : query.getNetTasks()) {
				// String taskName = task.getName().trim();
				// if (GlobalParameter.isEnableSimilarLabel()) {
				// // label similarity is enabled
				// if (expandedTasks.add(taskName)) {
				// BooleanQuery subq = new BooleanQuery();
				// // Term term = new
				// // Term(YAWLTasksDocument.FIELDTASKS,
				// // taskName);
				// // TermQuery termQuery = new TermQuery(term);
				// // subq.add(termQuery, Occur.SHOULD);
				//
				// TreeSet<SimilarLabelQueryResult> similarTasks = labelIndex
				// .getSimilarLabels(taskName, GlobalParameter
				// .getLabelSemanticSimilarity());
				// Iterator<SimilarLabelQueryResult> it = similarTasks
				// .iterator();
				// while (it.hasNext()) {
				// SimilarLabelQueryResult sl = it.next();
				// String similarTaskName = sl.getLabel();
				// Term term = new Term(
				// YAWLTasksDocument.FIELDTASKS,
				// similarTaskName);
				// TermQuery termQuery = new TermQuery(term);
				// subq.add(termQuery, Occur.SHOULD);
				// }
				// if (subq.getClauses().length > 0) {
				// bq.add(subq, Occur.MUST);
				// } else {
				// return ret;
				// }
				// }
				// } else {
				// // label similarity is not enabled
				// if (expandedTasks.add(taskName)) {
				// Term term = new Term(YAWLTasksDocument.FIELDTASKS,
				// taskName);
				// TermQuery termQuery = new TermQuery(term);
				// bq.add(termQuery, Occur.MUST);
				// }
				// }
				// }

				YAWLTasksQueryResultCollector collector = new YAWLTasksQueryResultCollector(
						reader);
				searcher.search(bq, collector);
				ret = collector.getQueryResult();
				searcher.close();
				reader.close();

				// sub graph isomorphism check using Ullman's algorithm
				// accurately match
				Iterator<ProcessQueryResult> it = ret.iterator();
				while (it.hasNext()) {
					ProcessQueryResult pqr = it.next();
					long process_id = pqr.getProcess_id();
					DataManager dm = DataManager.getInstance();
					YNet model = YAWLUtil.getYNetFromDefinition(dm
							.getProcessDefinitionBytes(process_id));
					if (!Ullman4YAWL.subGraphIsomorphism(query, model)) {
						it.remove();
					}
					// if (!VF24YAWL.subGraphIsomorphism(query, model)) {
					// it.remove();
					// }
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
	public boolean supportSimilarLabel() {
		return true;
	}

	@Override
	public boolean supportSimilarQuery() {
		return false;
	}

	@Override
	public boolean supportTextQuery() {
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
