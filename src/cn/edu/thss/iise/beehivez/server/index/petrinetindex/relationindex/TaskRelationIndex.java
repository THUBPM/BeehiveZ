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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelLuceneIndex;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.queryparser.QueryParser;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;

/**
 * @author Tao Jin
 * 
 */
public class TaskRelationIndex extends PetriNetIndex {
	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/TaskRelationIndex";
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
						.println("null petri net in addPetriNet of TransitionRelationIndex");
				return;
			}

			Document doc = TransitionRelationDocument.Document(pn, pno
					.getProcess_id());
			indexWriter.addDocument(doc);
			indexWriter.commit();

			// update the label index
			for (Transition t : pn.getTransitions()) {
				labelIndex.addLabel(t.getIdentifier());
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

			DataManager dm = DataManager.getInstance();
			ResultSet rs = dm.executeSelectSQL(
					"select process_id from petrinet", 0, Integer.MAX_VALUE, dm
							.getFetchSize());
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
			if (o instanceof String) {
				String query = (String) o;

				// analyze the query
				StringReader sr = new StringReader(query);
				BufferedReader br = new BufferedReader(sr);
				QueryParser parser = new QueryParser(br);
				if (GlobalParameter.isEnableSimilarLabel()) {
					parser.setSemanticAide(this.labelIndex, GlobalParameter
							.getLabelSemanticSimilarity());
				}
				Query q = parser.parse();
				// System.out.println("before optimization");
				// System.out.println(q.toString());
				br.close();
				sr.close();

				// optimize the query here
				// bq = parser.optimize(bq);
				// System.out.println("after optimization");
				// System.out.println(bq.toString());

				// query the lucene
				IndexReader reader = IndexReader.open(FSDirectory
						.open(INDEX_DIR), true);
				Searcher searcher = new IndexSearcher(reader);

				RelationQueryResultCollector collector = new RelationQueryResultCollector(
						reader);
				searcher.search(q, collector);
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
	//
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
		return false;
	}

	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportTextQuery() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
