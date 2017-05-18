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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;
//package cn.edu.thss.iise.beehivez.server.index.rltMatrix;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.ResultSet;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.TreeSet;
//
//import org.processmining.framework.models.petrinet.PetriNet;
//import org.processmining.framework.models.petrinet.Transition;
//import org.processmining.importing.pnml.PnmlImport;
//import org.processmining.mining.petrinetmining.PetriNetResult;
//
//import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
//import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
//import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
//import cn.edu.thss.iise.beehivez.server.index.PetriNetIndex;
//import cn.edu.thss.iise.beehivez.server.index.PetriNetQueryResult;
//import cn.edu.thss.iise.beehivez.server.index.pathindex.GenericInvertedIndex;
//import cn.edu.thss.iise.beehivez.server.index.pathindex.PathQueryExpression;
//import cn.edu.thss.iise.beehivez.server.metric.isomorphism.Ullman4PetriNet;
//import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
//import cn.edu.thss.iise.beehivez.server.util.FileUtil;
//import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
//
//public class RltMatrixIndexNo2 extends PetriNetIndex {
//
//	private static final String indexDirectory = "processrepository/index/RltMatrixNo2";
//	private static final String indexName = "RltMatrixNo2";
//	private static GenericInvertedIndex l1pIndex = new GenericInvertedIndex(
//			indexDirectory, indexName);
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#addPetriNet(cn.edu
//	 * .thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject)
//	 */
//	@Override
//	public void addPetriNet(PetrinetObject pno) {
//		PetriNet pn = pno.getPetriNet();
//		if (pn == null) {
//			pn = PetriNetUtil.getPetriNetFromPnml(pno.getPnmlIn());
//		}
//		if (pn == null) {
//			System.out.println("null petri net in addPetriNet of l1p");
//			return;
//		}
//
//		long process_id = pno.getProcess_id();
//
//		// add item to B+ tree and file vector file
//		for (Transition t : pn.getTransitions()) {
//			l1pIndex.addIndexNode(t.getIdentifier(), process_id);
//		}
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#close()
//	 */
//	@Override
//	public void close() {
//		l1pIndex.close();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#create()
//	 */
//	@Override
//	public boolean create() {
//		try {
//			l1pIndex.createNewIndex();
//			DataManager dm = DataManager.getInstance();
//			ResultSet rs = dm
//					.executeSelectSQL("select process_id,pnml from petrinet");
//			while (rs.next()) {
//				long process_id = rs.getLong("process_id");
//				InputStream petrinet_inputstream = (InputStream) rs
//						.getAsciiStream("pnml");
//				PetrinetObject pno = new PetrinetObject();
//				pno.setProcess_id(process_id);
//				pno.setPnmlIn(petrinet_inputstream);
//				this.addPetriNet(pno);
//				petrinet_inputstream.close();
//			}
//			rs.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#delPetriNet(cn.edu
//	 * .thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject)
//	 */
//	@Override
//	public void delPetriNet(PetrinetObject pno) {
//		System.out.println("not implemented");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#destroy()
//	 */
//	@Override
//	public boolean destroy() {
//		return l1pIndex.destroy();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#getPetriNet(java
//	 * .lang.Object)
//	 */
//	@Override
//	public TreeSet<PetriNetQueryResult> getPetriNet(Object o, float similarity) {
//		TreeSet<PetriNetQueryResult> ret = new TreeSet<PetriNetQueryResult>();
//		HashSet tempRet = null;
//		if (o instanceof PetriNet) {
//			PetriNet query = (PetriNet) o;
//			long start = System.currentTimeMillis();
//			tempRet = this.query(query);
//			long end = System.currentTimeMillis();
//			long indexSearchTime = end - start;
//			int candidateSetSize = tempRet.size();
//
//			// accurately match
//			Iterator it = tempRet.iterator();
//			while (it.hasNext()) {
//				long process_id = (Long) it.next();
//				DataManager dm = DataManager.getInstance();
//				InputStream in = dm.getProcessPnml(process_id);
//				PnmlImport pi = new PnmlImport();
//				try {
//					PetriNetResult result = (PetriNetResult) pi.importFile(in);
//					PetriNet pn = result.getPetriNet();
//					if (!Ullman4PetriNet.subGraphIsomorphism(query, pn)) {
//						it.remove();
//					}
//					pn.delete();
//					pn.clearGraph();
//					in.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//			if (GlobalParameter.isEnableQueryLog()) {
//				int resultSetSize = tempRet.size();
//				float hitRatio = (float) resultSetSize
//						/ (float) candidateSetSize;
//				long nModels = GlobalParameter.getNModels();
//				Indexlogger.logIndexSearchTime(this.getName(), query
//						.getIdentifier(), indexSearchTime, query
//						.getTransitions().size(), query.getPlaces().size(),
//						query.getNumberOfEdges(), nModels);
//				Indexlogger.logIndexCandidateSetSize(this.getName(), query
//						.getIdentifier(), candidateSetSize, nModels);
//				Indexlogger.logIndexCandidateSetHitRatio(this.getName(), query
//						.getIdentifier(), hitRatio, nModels);
//			}
//		}
//
//		Iterator it = tempRet.iterator();
//		while (it.hasNext()) {
//			ret.add(new PetriNetQueryResult(((Long) it.next()).longValue(), 1));
//		}
//
//		return ret;
//	}
//
//	private HashSet query(PetriNet pn) {
//		HashSet res = new HashSet();
//		PathQueryExpression qe = new PathQueryExpression();
//		qe.setType(PathQueryExpression.AND);
//		try {
//			for (Transition t : pn.getTransitions()) {
//				qe.addAtom(t.getIdentifier(), PathQueryExpression.Atom.L1P);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		res = l1pIndex.query(qe);
//		return res;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#init()
//	 */
//	@Override
//	protected void init() {
//		String str = this.getClass().getCanonicalName();
//		this.javaClassName = str;
//		this.name = str.substring(str.lastIndexOf(".") + 1);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#open()
//	 */
//	@Override
//	public boolean open() {
//		try {
//			l1pIndex.setupFromExistingIndex();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#supportGraphQuery()
//	 */
//	@Override
//	public boolean supportGraphQuery() {
//		return false;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#supportTextQuery()
//	 */
//	@Override
//	public boolean supportTextQuery() {
//		return false;
//	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public float getStorageSizeInMB() {
//		return FileUtil.getFileSizeInMB(indexDirectory);
//	}
//
//	@Override
//	public boolean supportSimilarQuery() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean supportSimilarLabel() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//}
