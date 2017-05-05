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
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.sql.ResultSet;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.TreeSet;
//import java.util.Vector;
//
//import org.processmining.framework.models.petrinet.PetriNet;
//import org.processmining.framework.models.petrinet.Transition;
//
//import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
//import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
//import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
//import cn.edu.thss.iise.beehivez.server.index.PetriNetIndex;
//import cn.edu.thss.iise.beehivez.server.index.PetriNetQueryResult;
//import cn.edu.thss.iise.beehivez.server.index.pathindex.GenericInvertedIndex;
//import cn.edu.thss.iise.beehivez.server.index.pathindex.PathQueryExpression;
//import cn.edu.thss.iise.beehivez.server.index.rltMatrix.queryParser.ParserTreeConstants;
//import cn.edu.thss.iise.beehivez.server.index.rltMatrix.queryParser.SimpleNode;
//import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
//import cn.edu.thss.iise.beehivez.server.util.FileUtil;
//import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
//
///**
// * relationship matrix index #3 using TransitionName1(String) +
// * Relationship(byte) + TransitionName2(String) to index ProcessModel (PetriNet)
// * 
// * @author zhougz 2010.04.02
// */
//public class RltMatrixIndexNo3 extends PetriNetIndex implements RltConstants {
//
//	private static final String indexDirectory = "processrepository/index/RltMatrixNo3";
//	private static final String indexName = "RltMatrixNo3";
//	private static GenericInvertedIndex rltMatrixIndex = new GenericInvertedIndex(
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
//
//		if (pn == null) {
//			pn = PetriNetUtil.getPetriNetFromPnml(pno.getPnmlIn());
//		}
//		if (pn == null) {
//			System.out.println("null petri net in addPetriNet of RltMatrixNo1");
//			return;
//		}
//
//		long process_id = pno.getProcess_id();
//
//		long start = System.currentTimeMillis();
//		RltMatrix matrix = RltMatrixBuilder.build(pn, process_id);
//		long end = System.currentTimeMillis();
//		long matrix1time = end - start;
//		if (matrix == null)
//			return;
//		// return ;
//		matrix.setProcessId(process_id);
//		Iterator<Map.Entry<Transition, Integer>> it = matrix
//				.getTrnEntrysIterator();
//		Map.Entry<Transition, Integer> e = null;
//		HashSet<String> labelSet = new HashSet<String>();
//		// add transitions to B+ tree Index
//		start = System.currentTimeMillis();
//		while (it.hasNext()) {
//			e = it.next();
//			labelSet.add(e.getKey().getIdentifier().trim());
//		}
//		Iterator<String> it2 = labelSet.iterator();
//		while (it2.hasNext()) {
//			rltMatrixIndex.addIndexNode(it2.next(), process_id);
//		}
//		end = System.currentTimeMillis();
//		// add relationship matrix to table RLTMATRIX
//		long labelTime = end - start;
//		start = System.currentTimeMillis();
//		DataManager.getInstance().addRltMatrix(matrix);
//		end = System.currentTimeMillis();
//		long matrix2time = end - start;
//		System.out.println(matrix1time + matrix2time + "  " + labelTime);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see cn.edu.thss.iise.beehivez.server.index.PetriNetIndex#close()
//	 */
//	@Override
//	public void close() {
//		rltMatrixIndex.close();
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
//
//			PrintStream myout = new PrintStream(new FileOutputStream(new File(
//					"C:/log.txt")));
//			System.setOut(myout);
//			rltMatrixIndex.createNewIndex();
//			DataManager dm = DataManager.getInstance();
//			ResultSet rs = dm.executeSelectSQL("delete from rltmatrix");
//			if (rs != null) {
//				rs = null;
//			}
//			rs = dm.executeSelectSQL("select process_id from petrinet");
//			// if add matrix during rs is open, it will cause conflict, because
//			// dm has only One connection.
//			Vector<Long> idsToCreate = new Vector<Long>();
//
//			while (rs.next()) {
//				long process_id = rs.getLong("process_id");
//				idsToCreate.add(process_id);
//			}
//			rs.close();
//
//			Iterator<Long> it = idsToCreate.iterator();
//			while (it.hasNext()) {
//				long cur_pid = it.next();
//				rs = dm
//						.executeSelectSQL("select pnml from petrinet where process_id = "
//								+ cur_pid);
//				if (rs.next()) {
//					InputStream petrinet_inputstream = (InputStream) rs
//							.getAsciiStream("pnml");
//					PetrinetObject pno = new PetrinetObject();
//					pno.setPetriNet(PetriNetUtil
//							.getPetriNetFromPnml(petrinet_inputstream));
//					pno.setProcess_id(cur_pid);
//					petrinet_inputstream.close();
//					rs.close();
//					this.addPetriNet(pno);
//				}
//			}
//
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
//		return rltMatrixIndex.destroy();
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
//		if (o instanceof SimpleNode) {
//			SimpleNode syntaxRoot = (SimpleNode) o;
//
//			QueryExpressionBuilder qeb = QueryExpressionBuilder.getInstance();
//			PathQueryExpression qe = new PathQueryExpression();
//			qeb.bulidQueryExpression(syntaxRoot, qe);
//
//			long start = System.currentTimeMillis();
//			tempRet = this.query(qe);
//			long end = System.currentTimeMillis();
//			long indexSearchTime = end - start;
//			int candidateSetSize = tempRet.size();
//			start = System.currentTimeMillis();
//			// accurately match
//			Iterator it = tempRet.iterator();
//			DataManager dm = DataManager.getInstance();
//			while (it.hasNext()) {
//				long process_id = (Long) it.next();
//				// System.out.println(process_id);
//				BasicRltMatrix matrix = dm.getRltMatrixByProcessId(process_id);
//				if (matrix.process_id != process_id) {
//					System.out.println("Waring: process " + process_id
//							+ " has no rltmatirx.");
//					it.remove();
//					continue;
//				}
//
//				if (!isMatch(syntaxRoot, matrix)) {
//					it.remove();
//				}
//			}
//			end = System.currentTimeMillis();
//
//			int resultSetSize = tempRet.size();
//			float hitRatio = (float) resultSetSize / (float) candidateSetSize;
//			System.out.println(candidateSetSize + "/" + resultSetSize + " = "
//					+ hitRatio);
//			System.out.println("indexSearchTime = " + indexSearchTime + "  "
//					+ "matrix map time " + (end - start));
//			//				
//			// if (GlobalParameter.isQueryDataLogged())
//			// {
//			// 
//			// long nModels = GlobalParameter.getNModels();
//			// Indexlogger.logIndexSearchTime(this.getName(), "rltmatrix query",
//			// indexSearchTime,0, 0, 0, nModels);
//			// Indexlogger.logIndexCandidateSetSize(this.getName(),
//			// "rltmatrix query", candidateSetSize, nModels);
//			// Indexlogger.logIndexCandidateSetHitRatio(this.getName(),
//			// "rltmatrix query", hitRatio, nModels);
//			// }
//		}
//		Iterator it1 = tempRet.iterator();
//		while (it1.hasNext()) {
//			ret.add(new PetriNetQueryResult(((Long) it1.next()).longValue(), 1));
//		}
//
//		return ret;
//	}
//
//	private boolean isMatch(SimpleNode syntaxTree, BasicRltMatrix matrix) {
//		switch (syntaxTree.getType()) {
//		case ParserTreeConstants.JJTACTIVITY:
//			return matrix
//					.isContainActivity(syntaxTree.jjtGetValue().toString());
//		case ParserTreeConstants.JJTOR:
//			boolean flag1 = false;
//			for (int i = 0; i < syntaxTree.jjtGetNumChildren(); i++) {
//				flag1 = flag1
//						|| isMatch((SimpleNode) syntaxTree.jjtGetChild(i),
//								matrix);
//				if (flag1 == true) {
//					break;
//				}
//			}
//			return flag1;
//		case ParserTreeConstants.JJTSTART:
//		case ParserTreeConstants.JJTAND:
//			boolean flag2 = true;
//			for (int i = 0; i < syntaxTree.jjtGetNumChildren(); i++) {
//				flag2 = flag2
//						&& isMatch((SimpleNode) syntaxTree.jjtGetChild(i),
//								matrix);
//				if (flag2 == false) {
//					break;
//				}
//			}
//			return flag2;
//		case ParserTreeConstants.JJTOP:
//			SimpleNode leftActivity = (SimpleNode) syntaxTree.jjtGetChild(0);
//			SimpleNode rightActivity = (SimpleNode) syntaxTree.jjtGetChild(1);
//			return matrix.isRltContain(leftActivity.jjtGetValue().toString(),
//					rightActivity.jjtGetValue().toString(), Byte
//							.parseByte(syntaxTree.jjtGetValue().toString()));
//		}
//		return true;
//	}
//
//	private HashSet query(PathQueryExpression qe) {
//		HashSet res = null;
//		res = rltMatrixIndex.query(qe);
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
//			rltMatrixIndex.setupFromExistingIndex();
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
//		return true;
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
