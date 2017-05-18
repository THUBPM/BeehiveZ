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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * index the length one path and length two path of petri net
 * 
 * @author JinTao
 * 
 */
public class LengthTwoClosurePathIndex extends PetriNetIndex {
	private static final String indexDirectory = GlobalParameter
			.getHomeDirectory()
			+ "/index/LengthTwoClosurePathIndex";
	private static final String l1pIndexDirectory = indexDirectory
			+ "/LengthOnePathIndex";
	private static final String l2pIndexDirectory = indexDirectory
			+ "/LengthTwoPathIndex";
	private static final String l1pIndexName = "LengthOnePathIndex";
	private static final String l2pIndexName = "LengthTwoPathIndex";
	private static GenericInvertedIndex l1pIndex = new GenericInvertedIndex(
			l1pIndexDirectory, l1pIndexName);
	private static GenericInvertedIndex l2pIndex = new GenericInvertedIndex(
			l2pIndexDirectory, l2pIndexName);

	@Override
	public void addProcessModel(Object o) {

		PetrinetObject pno = (PetrinetObject) o;

		PetriNet pn = pno.getPetriNet();
		if (pn == null) {
			pn = PetriNetUtil.getPetriNetFromPnmlBytes(pno.getPnml());
		}
		if (pn == null) {
			System.out.println("null petri net in addPetriNet of l2cp");
			return;
		}

		long process_id = pno.getProcess_id();

		// add item to B+ tree and file vector file
		// first add length one path into index,
		// then add length two path into index
		for (Transition t : pn.getTransitions()) {
			l1pIndex.addIndexNode(t.getIdentifier(), process_id);
		}

		for (Place p : pn.getPlaces()) {
			Iterator it1 = p.getPredecessors().iterator();
			while (it1.hasNext()) {
				Transition t1 = (Transition) it1.next();
				Iterator it2 = p.getSuccessors().iterator();
				while (it2.hasNext()) {
					Transition t2 = (Transition) it2.next();
					l2pIndex.addIndexNode(t1.getIdentifier().trim() + "->"
							+ t2.getIdentifier().trim(), process_id);
				}
			}
		}

	}

	@Override
	public void close() {
		l1pIndex.close();
		l2pIndex.close();
	}

	@Override
	public boolean create() {
		try {
			l1pIndex.createNewIndex();
			l2pIndex.createNewIndex();
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
		close();
		boolean r = FileUtil.deleteFile(indexDirectory);
		return r;
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
		// first query by length two path
		// then query by left length one path
		PathQueryExpression l2pQE = new PathQueryExpression();
		l2pQE.setType(PathQueryExpression.AND);
		PathQueryExpression l1pQE = new PathQueryExpression();
		l1pQE.setType(PathQueryExpression.AND);
		try {
			// first handle length two path
			ArrayList<Transition> vT = (ArrayList<Transition>) pn
					.getTransitions().clone();
			for (Place p : pn.getPlaces()) {
				Iterator it1 = p.getPredecessors().iterator();
				while (it1.hasNext()) {
					Transition t1 = (Transition) it1.next();
					Iterator it2 = p.getSuccessors().iterator();
					while (it2.hasNext()) {
						Transition t2 = (Transition) it2.next();
						l2pQE.addAtom(t1.getIdentifier().trim() + "->"
								+ t2.getIdentifier().trim(),
								PathQueryExpression.Atom.L2P);
						vT.remove(t1);
						vT.remove(t2);
					}
				}
			}

			// handle the left length one path
			for (Transition t : vT) {
				l1pQE.addAtom(t.getIdentifier(), PathQueryExpression.Atom.L1P);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (l2pQE.getItems().size() < 1) {
			res = l1pIndex.query(l1pQE);
		} else {
			res = l2pIndex.query(l2pQE);
			if (l1pQE.getItems().size() > 1) {
				res.retainAll(l1pIndex.query(l1pQE));
			}
		}
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
			l2pIndex.setupFromExistingIndex();
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
		return getPetriNet(o);
	}

	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return false;
	}

}
