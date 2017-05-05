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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.index.labelindex.LabelLuceneIndex;
import cn.edu.thss.iise.beehivez.server.index.labelindex.SimilarLabelQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * @author JinTao
 * 
 *         only index the length one path of petri net
 * 
 */
public class LengthOnePathIndex extends PetriNetIndex {

	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/LengthOnePathIndex";
	private static final String indexName = "LengthOnePathIndex";
	private static GenericInvertedIndex l1pIndex = new GenericInvertedIndex(
			indexDirectory, indexName);
	private static LabelLuceneIndex labelIndex = new LabelLuceneIndex(
			indexDirectory);

	@Override
	public void addProcessModel(Object o) {
		PetrinetObject pno = (PetrinetObject) o;
		PetriNet pn = pno.getPetriNet();
		if (pn == null) {
			pn = PetriNetUtil.getPetriNetFromPnmlBytes(pno.getPnml());
		}
		if (pn == null) {
			System.out.println("null petri net in addPetriNet of l1p");
			return;
		}

		long process_id = pno.getProcess_id();

		// add item to B+ tree and file vector file
		for (Transition t : pn.getTransitions()) {
			String label = t.getIdentifier();
			l1pIndex.addIndexNode(label, process_id);
			labelIndex.addLabel(label);
		}

	}

	@Override
	public void close() {
		l1pIndex.close();
		labelIndex.close();
	}

	@Override
	public boolean create() {
		try {
			l1pIndex.createNewIndex();
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
		System.out.println("not implemented");
	}

	@Override
	public boolean destroy() {
		labelIndex.destroy();
		return l1pIndex.destroy();
	}

	public TreeSet<ProcessQueryResult> getPetriNet(Object o) {
		TreeSet<ProcessQueryResult> fret = new TreeSet<ProcessQueryResult>();

		HashSet ret = null;
		if (o instanceof PetriNet) {
			PetriNet query = (PetriNet) o;
			long start = System.currentTimeMillis();
			ret = this.query(query);
			long end = System.currentTimeMillis();
			long indexSearchTime = end - start;
			int candidateSetSize = ret.size();

			// accurately match
			Iterator it = ret.iterator();
			while (it.hasNext()) {
				long process_id = (Long) it.next();
				DataManager dm = DataManager.getInstance();
				InputStream in = dm.getProcessPnml(process_id);
				PnmlImport pi = new PnmlImport();
				try {
					PetriNetResult result = (PetriNetResult) pi.importFile(in);
					PetriNet pn = result.getPetriNet();
					if (!Ullman4PetriNet.subGraphIsomorphism(query, pn)) {
						it.remove();
					}
					result.destroy();
					pn.destroyPetriNet();
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (GlobalParameter.isEnableQueryLog()) {
				int resultSetSize = ret.size();
				float hitRatio = (float) resultSetSize
						/ (float) candidateSetSize;
				long nModels = GlobalParameter.getNModels();
				Indexlogger.logIndexSearchTime(this.getName(), query
						.getIdentifier(), indexSearchTime, query
						.getTransitions().size(), query.getPlaces().size(),
						query.getNumberOfEdges(), nModels);
				Indexlogger.logIndexCandidateSetSize(this.getName(), query
						.getIdentifier(), candidateSetSize, nModels);
				Indexlogger.logIndexCandidateSetHitRatio(this.getName(), query
						.getIdentifier(), hitRatio, nModels);
			}

			Iterator itt = ret.iterator();
			while (itt.hasNext()) {
				long process_id = ((Long) itt.next()).longValue();
				fret.add(new ProcessQueryResult(process_id, 1));
			}
		}
		return fret;
	}

	private HashSet query(PetriNet pn) {
		HashSet res = new HashSet();
		PathQueryExpression qe = new PathQueryExpression();
		qe.setType(PathQueryExpression.AND);
		try {
			if (!GlobalParameter.isEnableSimilarLabel()) {
				HashSet<String> unique = new HashSet<String>();
				for (Transition t : pn.getTransitions()) {
					String path = t.getIdentifier();
					if (unique.add(path)) {
						qe.addAtom(path, PathQueryExpression.Atom.L1P);
					}
				}
			} else {
				// enable similar label
				HashSet<String> unique = new HashSet<String>();
				for (Transition t : pn.getTransitions()) {
					String label = t.getIdentifier();
					if (unique.add(label)) {
						HashSet<String> labels = new HashSet<String>();
						// labels.add(label);
						TreeSet<SimilarLabelQueryResult> simLabels = labelIndex
								.getSimilarLabels(label, GlobalParameter
										.getLabelSemanticSimilarity());
						Iterator<SimilarLabelQueryResult> it = simLabels
								.iterator();
						while (it.hasNext()) {
							label = it.next().getLabel();
							labels.add(label);
						}
						if (labels.size() == 0) {
							qe.addAtom(label, PathQueryExpression.Atom.L1P);
						} else if (labels.size() == 1) {
							qe.addAtom(label, PathQueryExpression.Atom.L1P);
						} else {
							PathQueryExpression subQuery = new PathQueryExpression();
							subQuery.setType(PathQueryExpression.OR);
							Iterator<String> itLabel = labels.iterator();
							while (itLabel.hasNext()) {
								subQuery.addAtom(itLabel.next(),
										PathQueryExpression.Atom.L1P);
							}
							qe.addExpression(subQuery);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res = l1pIndex.query(qe);
		return res;
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
			l1pIndex.setupFromExistingIndex();
			labelIndex.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean supportGraphQuery() {
		return true;
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

	@Override
	public float getStorageSizeInMB() {
		return FileUtil.getFileSizeInMB(indexDirectory);
	}

	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		// TODO Auto-generated method stub
		return getPetriNet(o);
	}

	@Override
	public boolean supportSimilarLabel() {
		return true;
	}

}
