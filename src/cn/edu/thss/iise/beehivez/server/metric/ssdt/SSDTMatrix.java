package cn.edu.thss.iise.beehivez.server.metric.ssdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectMatrix2D;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * @author Picry Mashu
 *
 */
public class SSDTMatrix
{
	
	public static final double SSD_EXCLUSIVE = -1.0;
	public static final double SSD_UNDEFINED = -2.0;
	public static final double SSD_INFINITY = -3.0;
	
	public static float build(PetriNet pn1, PetriNet pn2) {		
		SSDTMatrix ssdMatrix = new SSDTMatrix();
		//get complete finite prefix of two petrinets
		CompleteFinitePrefix cfp1 = PetriNetUtil.buildCompleteFinitePrefix(pn1);
		CompleteFinitePrefix cfp2 = PetriNetUtil.buildCompleteFinitePrefix(pn2);
		//Compute the shortest synchronization distance matrix from both cfps one by one
		ArrayList<String> alOrder_cfp1 = new ArrayList<String>();
		ArrayList<String> alOrder_cfp2 = new ArrayList<String>();
		DoubleMatrix2D ssdMatrix_cfp1 = ssdMatrix.computeSSD(cfp1, alOrder_cfp1);
		DoubleMatrix2D ssdMatrix_cfp2 = ssdMatrix.computeSSD(cfp2, alOrder_cfp2);
//		System.out.println(alOrder_cfp1);
//		System.out.println(ssdMatrix_cfp1);
		//get intersection of two cfps
		Hashtable<String, String> htCommon = ssdMatrix.getIntersection(cfp1, cfp2, alOrder_cfp1, alOrder_cfp2);
		//Compute the shortest synchronization distance matrix from both reduced cfps
		ArrayList<String> alOrder_cfp1_common = new ArrayList<String>();
		ArrayList<String> alOrder_cfp2_common = new ArrayList<String>();
		DoubleMatrix2D ssdMatrix_cfp1_common = ssdMatrix.computeSSD(cfp1, alOrder_cfp1_common);
		DoubleMatrix2D ssdMatrix_cfp2_common = ssdMatrix.computeSSD(cfp2, alOrder_cfp2_common);
		//get difference sets of two cfp
		ArrayList<String> alDiff_cfp1 = new ArrayList<String>();
		ArrayList<String> alDiff_cfp2 = new ArrayList<String>();
		for(String s : alOrder_cfp1) {
			if(!htCommon.containsKey(s)) {
				alDiff_cfp1.add(s);
			}
		}
		for(String s : alOrder_cfp2) {
			if(!htCommon.containsValue(s)) {
				alDiff_cfp2.add(s);
			}
		}
		//mark the same identifier of alDiff_cfp1 and alDiff_cfp2
		for(int i = 0; i < alDiff_cfp1.size(); ++i) {
			String sId = alDiff_cfp1.get(i);
			if(alDiff_cfp2.contains(sId)) {
				int j = alDiff_cfp2.indexOf(sId);
				alDiff_cfp1.set(i, sId + "_MODEL1");
				alDiff_cfp2.set(j, sId + "_MODEL2");
				int m = alOrder_cfp1.indexOf(sId);
				int n = alOrder_cfp2.indexOf(sId);
				if(m != -1) {
					alOrder_cfp1.set(m, sId + "_MODEL1");
				}
				if(n != -1) {
					alOrder_cfp2.set(n, sId + "_MODEL2");
				}
			}
		}
		ArrayList<String> alMatrixOrder1 = new ArrayList<String>();
		alMatrixOrder1.addAll(alOrder_cfp1_common);
		alMatrixOrder1.addAll(alDiff_cfp1);
		alMatrixOrder1.addAll(alDiff_cfp2);
		ArrayList<String> alMatrixOrder2 = new ArrayList<String>();
		alMatrixOrder2.addAll(alOrder_cfp2_common);
		alMatrixOrder2.addAll(alDiff_cfp1);
		alMatrixOrder2.addAll(alDiff_cfp2);
		int n = htCommon.size() + alDiff_cfp1.size() + alDiff_cfp2.size();
		DoubleMatrix2D ssdMatrixFinal_cfp1 = DoubleFactory2D.sparse.make(n, n, 0);
		DoubleMatrix2D ssdMatrixFinal_cfp2 = DoubleFactory2D.sparse.make(n, n, 0);
		//build final ssd for cfp1
		for(int x = 0; x < n; ++x) {
			String sx = alMatrixOrder1.get(x);
			for(int y = 0; y < n; ++y) {
				String sy = alMatrixOrder1.get(y);
				if(alOrder_cfp1_common.contains(sx)) {
					if(alOrder_cfp1_common.contains(sy)) {
						int i = alOrder_cfp1_common.indexOf(sx);
						int j = alOrder_cfp1_common.indexOf(sy);
						ssdMatrixFinal_cfp1.set(x, y, ssdMatrix_cfp1_common.get(i, j));
					} else {
						if(alOrder_cfp1.contains(sy)) {
							int i = alOrder_cfp1.indexOf(sx);
							int j = alOrder_cfp1.indexOf(sy);
							ssdMatrixFinal_cfp1.set(x, y, ssdMatrix_cfp1.get(i, j));
						} else {
							ssdMatrixFinal_cfp1.set(x, y, -2.0);
						}
					}
				} else {
					if(alOrder_cfp1.contains(sx)) {
						if(alOrder_cfp1.contains(sy)) {
							int i = alOrder_cfp1.indexOf(sx);
							int j = alOrder_cfp1.indexOf(sy);
							ssdMatrixFinal_cfp1.set(x, y, ssdMatrix_cfp1.get(i, j));
						} else {
							ssdMatrixFinal_cfp1.set(x, y, -2.0);
						}
					} else {
						ssdMatrixFinal_cfp1.set(x, y, -2.0);
					}
				}
			}
		}
		//build final ssd for cfp2
		for(int x = 0; x < n; ++x) {
			String sx = alMatrixOrder2.get(x);
			for(int y = 0; y < n; ++y) {
				String sy = alMatrixOrder2.get(y);
				if(alOrder_cfp2_common.contains(sx)) {
					if(alOrder_cfp2_common.contains(sy)) {
						int i = alOrder_cfp2_common.indexOf(sx);
						int j = alOrder_cfp2_common.indexOf(sy);
						ssdMatrixFinal_cfp2.set(x, y, ssdMatrix_cfp2_common.get(i, j));
					} else {
						if(alOrder_cfp2.contains(sy)) {
							int i = alOrder_cfp2.indexOf(sx);
							int j = alOrder_cfp2.indexOf(sy);
							ssdMatrixFinal_cfp2.set(x, y, ssdMatrix_cfp2.get(i, j));
						} else {
							ssdMatrixFinal_cfp2.set(x, y, -2.0);
						}
					}
				} else {
					if(alOrder_cfp2.contains(sx)) {
						if(alOrder_cfp2.contains(sy)) {
							int i = alOrder_cfp2.indexOf(sx);
							int j = alOrder_cfp2.indexOf(sy);
							ssdMatrixFinal_cfp2.set(x, y, ssdMatrix_cfp2.get(i, j));
						} else {
							ssdMatrixFinal_cfp2.set(x, y, -2.0);
						}
					} else {
						ssdMatrixFinal_cfp2.set(x, y, -2.0);
					}
				}
			}
		}
		//turn -2 to -3, regard #undefined as infinity
		for(int i = 0; i < n; ++i) {
			for(int j = 0; j < n; ++j) {
				double value1 = ssdMatrixFinal_cfp1.get(i, j);
				double value2 = ssdMatrixFinal_cfp2.get(i, j);
				if(Double.compare(value1, -2.0) == 0) {
					ssdMatrixFinal_cfp1.set(i, j, -3.0);
				}
				if(Double.compare(value2, -2.0) == 0) {
					ssdMatrixFinal_cfp2.set(i, j, -3.0);
				}
			}
		}
		System.out.println(alMatrixOrder1);
		System.out.println(ssdMatrixFinal_cfp1.toString());
		System.out.println(alMatrixOrder2);
		System.out.println(ssdMatrixFinal_cfp2.toString());
		//get similarity of two matrixs
		int sameCount = 0;
		for(int i = 0; i < n; ++i) {
			for(int j = 0; j < n; ++j) {
				double value1 = ssdMatrixFinal_cfp1.get(i, j);
				double value2 = ssdMatrixFinal_cfp2.get(i, j);
				if(Double.compare(value1, value2) == 0) {
					++sameCount;
				}
			}
		}
		return (float) ((double)sameCount / ((double)n * (double)n));
	}
	
	/**
	 * Compute SSDT matrix for a specific CFP
	 * @param cfp - The complete finite prefix of a petri net
	 * @param alOrder - store the visiting order of cfp
	 * @return The reachable matrix of the cfp
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D computeSSD(CompleteFinitePrefix cfp, ArrayList<String> alOrder) {
		//Get all vertex of cfp and store them in a table
		ArrayList<ModelGraphVertex> alVertex = cfp.getVerticeList();
		ArrayList<String> alMatrix = new ArrayList<String>();
		//row and col of matrix -> identifier
		Hashtable<String, HashSet<ModelGraphVertex>> htVertex = new Hashtable<String, HashSet<ModelGraphVertex>>();
		for(ModelGraphVertex v : alVertex) {
			if(!alMatrix.contains(v.getIdentifier())) {
				htVertex.put(v.getIdentifier(), new HashSet<ModelGraphVertex>());
				alMatrix.add(v.getIdentifier());
			}
			htVertex.get(v.getIdentifier()).add(v);
		}
		//get all invisible tasks of skip type
		ArrayList<String> alSkipTasks = this.getSkipInvisibleTasks(cfp, htVertex);
		//System.out.println(alSkipTasks);

		//the row and col of the following matrix are the same with the key of alMatrix, 0...n-1
		int n = htVertex.size();
		//Preprocess descendants table of any vertex
		DoubleMatrix2D anceMatrix2d = this.getReachMatrix(cfp, n, alMatrix, htVertex);
//		System.out.println(alMatrix);
//		System.out.println(anceMatrix2d);
		
		//store any two transitions' least common ancestors (vertex's order)
		//traverse the reachable matrix bottom-up
		DoubleMatrix2D lcaMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		ArrayList<Transition> alVisibleTrans = cfp.getVisibleTasks();
		ArrayList<Transition> alTrans = cfp.getTransitions();
		ArrayList<String> visitedTrans = new ArrayList<String>();
		for(int i = 0; i < alTrans.size(); ++i) {
			Transition tI = alTrans.get(i);
			if(visitedTrans.contains(tI.getIdentifier())) {
				continue;
			}
			int tIIndex = alMatrix.indexOf(tI.getIdentifier());
			for(int j = i + 1; j < alTrans.size(); ++j) {
				Transition tJ = alTrans.get(j);
				if(visitedTrans.contains(tJ.getIdentifier())) {
					continue;
				}
				int tJIndex = alMatrix.indexOf(tJ.getIdentifier());
				int maxLCAIndex = tIIndex < tJIndex ? tIIndex : tJIndex;
				for(int pos = maxLCAIndex; pos > 0; --pos) {
					if(anceMatrix2d.get(pos, tIIndex) == 1.0 && anceMatrix2d.get(pos, tJIndex) == 1.0) {
						lcaMatrix2d.set(tIIndex, tJIndex, pos);
						lcaMatrix2d.set(tJIndex, tIIndex, pos);
						break;
					}
				}
			}
			visitedTrans.add(tI.getIdentifier());
		}
//		System.out.println(alMatrix);
//		System.out.println(lcaMatrix2d.toString());
		
		
		//initialize shortest synchronization distance matrix
		DoubleMatrix2D ssdtMatrix2d = DoubleFactory2D.sparse.make(n, n, -3.0);
		//trace matrix, used in computing parallel structure
		ObjectMatrix2D traceMatrix2d = ObjectFactory2D.sparse.make(n, n, new ArrayList<String>());
		//get all transitions
		//ArrayList<Transitions> alTrans, already defined above
		//to mark transitions which have been handled
		visitedTrans.clear();
		//for sequential relations, set distance to 1 (predecessor to successor)
		for(Transition t : alTrans) {
			HashSet<String> hsSuccPlaceId = this.getTransitionSuccSet(t, htVertex);
			if(hsSuccPlaceId.size() > 1) {
				//parallel structure, pass
				continue;
			}
			String tId = t.getIdentifier();
			if(visitedTrans.contains(tId)) {
				continue;
			}
			int tIndex = alMatrix.indexOf(tId);
			Iterator<ModelGraphVertex> itT = htVertex.get(tId).iterator();
			while(itT.hasNext()) {
				Iterator<Place> itTSucc = itT.next().getSuccessors().iterator();
				while(itTSucc.hasNext()) {
					Place tSuccPlace = itTSucc.next();
					Iterator<ModelGraphVertex> itTSuccPlace = htVertex.get(tSuccPlace.getIdentifier()).iterator();
					while(itTSuccPlace.hasNext()) {
						Iterator<Transition> itPSuccTran = itTSuccPlace.next().getSuccessors().iterator();
						while(itPSuccTran.hasNext()) {
							Transition tSuccTran = itPSuccTran.next();
							//set ssd from t to tSuccTran with 1.0
							String tSuccTranId = tSuccTran.getIdentifier();
							int tSuccTranIndex = alMatrix.indexOf(tSuccTranId);
							ssdtMatrix2d.set(tIndex, tSuccTranIndex, 1.0);
							//set trace from t to tSuccTran
							ArrayList<String> trace = new ArrayList<String>();
							trace.add(tId);
							trace.add(tSuccTranId);
							traceMatrix2d.set(tIndex, tSuccTranIndex, trace);
						}
					}
				}
			}
			visitedTrans.add(t.getIdentifier());
		}

		//add invisible tasks of skip type into set of visible tasks
		for(String s : alSkipTasks) {
			Iterator<ModelGraphVertex> it = htVertex.get(s).iterator();
			while(it.hasNext()) {
				alVisibleTrans.add((Transition) it.next());
			}
		}
		//compute ssdt between other pairs of transitions recursively
		ArrayList<String> alVisitedITrans = new ArrayList<String>();
		for(int i = 0; i < alVisibleTrans.size(); ++i) {
			Transition tI = alVisibleTrans.get(i);
			String tIId = tI.getIdentifier();
			if(alVisitedITrans.contains(tIId)) {
				continue;
			}
			int tIIndex = alMatrix.indexOf(tIId);
			ArrayList<String> alVisitedJTrans = new ArrayList<String>();
			for(int j = 0; j < alVisibleTrans.size(); ++j) {
				Transition tJ = alVisibleTrans.get(j);
				String tJId = tJ.getIdentifier();
				if(alVisitedJTrans.contains(tJId)) {
					continue;
				}
				int tJIndex = alMatrix.indexOf(tJId);
				ArrayList<String> visited = new ArrayList<String>();
				ArrayList<String> trace = new ArrayList<String>();
				int ssdt = this.computeRecur(tI, tJ, ssdtMatrix2d, trace, visited, alMatrix, htVertex, traceMatrix2d);
				if(ssdt > 0) {
					ssdtMatrix2d.set(tIIndex, tJIndex, ssdt);
					//System.out.println(tiId + "->" + tjId + ": " + trace.toString());
					traceMatrix2d.set(tIIndex, tJIndex, trace);
				}
				alVisitedJTrans.add(tJId);
			}
			alVisitedITrans.add(tIId);
		}
		
		//for parallel transitions, set distance to 1 (both directions)
		//for exclusive transitions, set distance to -1 (relation "x")
		visitedTrans.clear();
		for(int i = 0; i < alVisibleTrans.size(); ++i) {
			Transition tI = alVisibleTrans.get(i);
			String tIId = tI.getIdentifier();
			if(visitedTrans.contains(tIId)) {
				continue;
			}
			int tIIndex = alMatrix.indexOf(tIId);
			for(int j = i + 1; j < alVisibleTrans.size(); ++j) {
				Transition tJ = alVisibleTrans.get(j);
				String tJId = tJ.getIdentifier();
				if(visitedTrans.contains(tJId)) {
					continue;
				}
				int tJIndex = alMatrix.indexOf(tJId);
				int lcaIndex = (int)lcaMatrix2d.get(tIIndex, tJIndex);
				if(lcaIndex != tIIndex && lcaIndex != tJIndex) {
					Iterator<ModelGraphVertex> itVertex = htVertex.get(alMatrix.get(lcaIndex)).iterator();
					if(itVertex.hasNext()) {
						ModelGraphVertex vLCA = itVertex.next();
						if(vLCA instanceof Transition) {
							//parallel
							if(!(ssdtMatrix2d.get(tIIndex, tJIndex) > 0 || ssdtMatrix2d.get(tJIndex, tIIndex) > 0)) {
								ssdtMatrix2d.set(tIIndex, tJIndex, 1.0);
								ArrayList<String> trace = new ArrayList<String>();
								trace.add(tIId);
								trace.add(tJId);
								traceMatrix2d.set(tIIndex, tJIndex, trace);
								ssdtMatrix2d.set(tJIndex, tIIndex, 1.0);
								trace = new ArrayList<String>();
								trace.add(tJId);
								trace.add(tIId);
								traceMatrix2d.set(tJIndex, tIIndex, trace);
							}
						} else if(vLCA instanceof Place) {
							//exclusive
							if(!(ssdtMatrix2d.get(tIIndex, tJIndex) > 0 || ssdtMatrix2d.get(tJIndex, tIIndex) > 0)) {
								ssdtMatrix2d.set(tIIndex, tJIndex, -1.0);
								ssdtMatrix2d.set(tJIndex, tIIndex, -1.0);
							}
						}
					}
				}
			}
			visitedTrans.add(tIId);
		}
		
		//System.out.println(this.alMatrix);
		//System.out.println(ssdMatrix2d.toString());
		
		ArrayList<Integer> alOrderNum = new ArrayList<Integer>();
		for(Transition tI : alVisibleTrans) {
			String tIId = tI.getIdentifier();
			int tIIndex = alMatrix.indexOf(tIId);
			if(alOrderNum.contains(tIIndex)) {
				continue;
			}
			alOrderNum.add(tIIndex);
		}
		DoubleMatrix2D ssdMatrix = DoubleFactory2D.sparse.make(alOrderNum.size(), alOrderNum.size(), 0);
		for(int i = 0; i < alOrderNum.size(); ++i) {
			for(int j = 0; j < alOrderNum.size(); ++j) {
				ssdMatrix.set(i, j, ssdtMatrix2d.get(alOrderNum.get(i), alOrderNum.get(j)));
			}
		}
		alOrder.clear();
		for(int i = 0; i < alOrderNum.size(); ++i) {
			alOrder.add(alMatrix.get(alOrderNum.get(i)));
		}
		
//		System.out.println(alOrder);
//		System.out.println(ssdMatrix);
		
		return ssdMatrix;
	}
	
	/**
	 * compute ssd between other pairs of transitions recursively
	 * @param tI - source transition
	 * @param tJ - target transition
	 * @param ssdtMatrix2d - ssdt Matrix
	 * @param trace - shortest trace from tI to tJ
	 * @param visited - store visited transitions
	 * @param alMatrix - all the ids of vertices
	 * @param htVertex - the map of label->list(vertex)
	 * @param traceMatrix2d - shortest trace Matrix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int computeRecur(Transition tI, Transition tJ, DoubleMatrix2D ssdtMatrix2d, ArrayList<String> trace, 
				ArrayList<String> visited, ArrayList<String> alMatrix, 
				Hashtable<String, HashSet<ModelGraphVertex>> htVertex, 
				ObjectMatrix2D traceMatrix2d) {
		String tIId = tI.getIdentifier();
		if(visited.contains(tIId)) {
			//has already been visited, return infinity
			return -3;
		}
		String tJId = tJ.getIdentifier();
		if(tIId.equals(tJId) && !visited.isEmpty()) {
			//arrive at tJ, return 0
			trace.add(tJId);
			return 0;
		}
		int tIIndex = alMatrix.indexOf(tIId);
		int tJIndex = alMatrix.indexOf(tJId);
		if(ssdtMatrix2d.get(tIIndex, tJIndex) > 0.0) {
			trace.addAll((Collection<? extends String>) traceMatrix2d.get(tIIndex, tJIndex));
			return (int) ssdtMatrix2d.get(tIIndex, tJIndex);
		}
		//start, add tI to visited
		visited.add(tIId);
		//compute ssd recursively (sequential, exclusive, parallel)
		HashSet<String> hsTISuccPlace = this.getTransitionSuccSet(tI, htVertex);
		int nTISuccPlace = hsTISuccPlace.size();
		if(nTISuccPlace == 1) {
			//sequential or exclusive, tI has only one output place
			Iterator<ModelGraphVertex> itTI = htVertex.get(tIId).iterator();
			Place pTISuccPlace = null;
			while(itTI.hasNext() && pTISuccPlace == null) {
				Iterator<Place> itTISuccPlace = itTI.next().getSuccessors().iterator();
				while(itTISuccPlace.hasNext()) {
					pTISuccPlace = itTISuccPlace.next();
					break;
				}
			}
			HashSet<String> hsPSuccTran = this.getPlaceSuccSet(pTISuccPlace, htVertex, true);
			int nPSuccTran = hsPSuccTran.size();
			if(nPSuccTran == 0) {
				//cannot reach tj, return infinity
				return -3;
			} else if(nPSuccTran == 1) {
				//sequential
				Iterator<ModelGraphVertex> itPlace = htVertex.get(pTISuccPlace.getIdentifier()).iterator();
				Transition tPSuccTran = null;
				while(itPlace.hasNext() && tPSuccTran == null) {
					Iterator<Transition> itTSucc = itPlace.next().getSuccessors().iterator();
					while(itTSucc.hasNext()) {
						tPSuccTran = itTSucc.next();
						break;
					}
				}
				ArrayList<String> succTrace = new ArrayList<String>();
				int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, visited, alMatrix, htVertex, traceMatrix2d);
				if(succSSDT == -3) {
					return -3;
				} else if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
					trace.add(tIId);
					trace.addAll(succTrace);
					return 1 + succSSDT;
				} else {
					trace.add(tIId);
					trace.addAll(succTrace);
					return succSSDT;
				}
			} else {
				//exclusive
				int minSuccSSDT = -3;
				ArrayList<String> tmpTrace = new ArrayList<String>();
				ArrayList<String> visitedCopy = new ArrayList<String>();
				visitedCopy.addAll(visited);
				for(String pSuccTranId : hsPSuccTran) {
					Iterator<ModelGraphVertex> itPSuccTran = htVertex.get(pSuccTranId).iterator();
					if(itPSuccTran.hasNext()) {
						Transition tPSuccTran = (Transition) itPSuccTran.next();
						ArrayList<String> succTrace = new ArrayList<String>();
						ArrayList<String> tmpVisited = new ArrayList<String>();
						tmpVisited.addAll(visitedCopy);
						int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
						if(succSSDT != -3) {
							if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
								++succSSDT;
							}
							if(minSuccSSDT == -3 || succSSDT < minSuccSSDT) {
								tmpTrace.clear();
								tmpTrace.addAll(succTrace);
								minSuccSSDT = succSSDT;
							}
						}
						for(String s : tmpVisited) {
							if(!(visited.contains(s))) {
								visited.add(s);
							}
						}
					}
				}
				if(minSuccSSDT != -3) {
					trace.add(tIId);
					trace.addAll(tmpTrace);
				}
				return minSuccSSDT;
			}
		} else if(nTISuccPlace > 1) {
			//parallel, tI has more than one output place
			Iterator<String> itTISuccPlaceId = hsTISuccPlace.iterator();
			int nTotalSSDT = 0;
			int nParallel = 0;
			ArrayList<ArrayList<String>> alSuccTrace = new ArrayList<ArrayList<String>>();
			ArrayList<String> visitedCopy = new ArrayList<String>();
			visitedCopy.addAll(visited);
			while(itTISuccPlaceId.hasNext()) {
				Iterator<ModelGraphVertex> itTISuccPlace = htVertex.get(itTISuccPlaceId.next()).iterator();
				Place pTISuccPlace = (Place) itTISuccPlace.next();
				HashSet<String> hsPSuccTran = this.getPlaceSuccSet(pTISuccPlace, htVertex, true);
				int nPSuccTran = hsPSuccTran.size();
				ArrayList<String> tmpVisited = new ArrayList<String>();
				tmpVisited.addAll(visitedCopy);
				if(nPSuccTran == 0) {
					//cannot reach tj, no action
				} else if(nPSuccTran == 1) {
					//sequential
					Iterator<String> itPSuccTranId = hsPSuccTran.iterator();
					Transition tPSuccTran = (Transition) htVertex.get(itPSuccTranId.next()).iterator().next();
					ArrayList<String> succTrace = new ArrayList<String>();
					int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
					if(succSSDT != -3) {
						nTotalSSDT += (1 + succSSDT);
						++nParallel;
						if(tPSuccTran.isInvisibleTask()/* && newTi.getAttribute("skip") == null*/) {
							--nTotalSSDT;
						}
						alSuccTrace.add(succTrace);
					}
					for(String s : tmpVisited) {
						if(!visited.contains(s)) {
							visited.add(s);
						}
					}
				} else {
					//exclusive
					Iterator<String> itPSuccTranId = hsPSuccTran.iterator();
					int minSuccSSDT = -3;
					ArrayList<String> tmpTrace = new ArrayList<String>();
					while(itPSuccTranId.hasNext()) {
						Iterator<ModelGraphVertex> itPSuccTran = htVertex.get(itPSuccTranId.next()).iterator();
						if(itPSuccTran.hasNext()) {
							Transition tPSuccTran = (Transition) itPSuccTran.next();
							ArrayList<String> succTrace = new ArrayList<String>();
							tmpVisited.clear();
							tmpVisited.addAll(visited);
							int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
							if(succSSDT != -3) {
								if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
									++succSSDT;
								}
								if(minSuccSSDT == -3 || succSSDT < minSuccSSDT) {
									tmpTrace.clear();
									tmpTrace.addAll(succTrace);
									minSuccSSDT = succSSDT;
								}
							}
							for(String s : tmpVisited) {
								if(!visited.contains(s)) {
									visited.add(s);
								}
							}
						}
					}
					if(minSuccSSDT != -3) {
						alSuccTrace.add(tmpTrace);
						nTotalSSDT += minSuccSSDT;
						++nParallel;	
					}
				}
			}
			if(nParallel == 0) {
				return nTotalSSDT;
			} else {
				//get merge trace
				ArrayList<String> parallelSuccTrace = new ArrayList<String>();
				for(ArrayList<String> succTrace : alSuccTrace) {
					for(String s : succTrace) {
						if(parallelSuccTrace.isEmpty() || !parallelSuccTrace.contains(s)) {
							parallelSuccTrace.add(s);
						}
					}
				}
				trace.add(tIId);
				trace.addAll(parallelSuccTrace);
				int tracelength = trace.size() - 1;
				return tracelength;
//				//get merge index
//				ArrayList<String> firstTrace = alSuccTrace.get(0);
//				int mergeIndex = 0;
//				boolean hasMerge = true;
//				for(int i = firstTrace.size() - 1; i >= 0 && hasMerge == true; --i) {
//					String strMerge = firstTrace.get(i);
//					hasMerge = true;
//					for(int j = 1; j < nParallel && hasMerge == true; ++j) {
//						ArrayList<String> otherTrace = alSuccTrace.get(j);
//						if(mergeIndex >= otherTrace.size()) {
//							hasMerge = false;
//							break;
//						}
//						String otherMerge = otherTrace.get(otherTrace.size() - mergeIndex - 1);
//						if(!otherMerge.equals(strMerge)) {
//							hasMerge = false;
//						}
//					}
//					if(hasMerge == true) {
//						++mergeIndex;
//					}
//				}
//				ArrayList<String> parallelSuccTrace = new ArrayList<String>();
//				for(int i = 0; i < nParallel; ++i) {
//					parallelSuccTrace.addAll(alSuccTrace.get(i).subList(0, alSuccTrace.get(i).size() - mergeIndex));
//				}
//				parallelSuccTrace.addAll(firstTrace.subList(firstTrace.size() - mergeIndex, firstTrace.size()));
//				trace.add(tiId);
//				trace.addAll(parallelSuccTrace);
//				int tracelength = trace.size() - 1;
//				return tracelength;
//				//return nTotalSSD - (nParallel - 1);
			}
		} else {
			//nSucc = 0, cannot reach tj
			return -3;
		}
	}
	
	/**
	 * get all the successor places of a transition
	 * @param place
	 * @param htVertex
	 * @param hasInv - whether the result should contain invisible tasks
	 * @return the set of successor places
	 */
	private HashSet<String> getPlaceSuccSet(Place place, Hashtable<String, HashSet<ModelGraphVertex>> htVertex, boolean hasInv) {
		Iterator<ModelGraphVertex> itPlace = htVertex.get(place.getIdentifier()).iterator();
		HashSet<String> succId = new HashSet<String>();
		while(itPlace.hasNext()) {
			@SuppressWarnings("unchecked")
			Iterator<Transition> itSucc = itPlace.next().getSuccessors().iterator();
			while(itSucc.hasNext()) {
				Transition pSucc = itSucc.next();
				String pSuccId = pSucc.getIdentifier();
				if(hasInv == false && pSucc.isInvisibleTask()) {
					continue;
				}
				succId.add(pSuccId);
			}
		}
		return succId;
	}
	
	/**
	 * get all the predecessor places of a transition
	 * @param place
	 * @param htVertex
	 * @param hasInv - whether the result should contain invisible tasks
	 * @return the set of predecessor places
	 */
	private HashSet<String> getPlacePredSet(Place place, Hashtable<String, HashSet<ModelGraphVertex>> htVertex, boolean hasInv) {
		Iterator<ModelGraphVertex> itPlace = htVertex.get(place.getIdentifier()).iterator();
		HashSet<String> predId = new HashSet<String>();
		while(itPlace.hasNext()) {
			@SuppressWarnings("unchecked")
			Iterator<Transition> itPred = itPlace.next().getPredecessors().iterator();
			while(itPred.hasNext()) {
				Transition pPred = itPred.next();
				String pPredId = pPred.getIdentifier();
				if(hasInv == false && pPred.isInvisibleTask()) {
					continue;
				}
				predId.add(pPredId);
			}
		}
		return predId;
	}
	
	/**
	 * get all the successor transitions of a place
	 * @param transition
	 * @param htVertex
	 * @return the set of successor transitions
	 */
	private HashSet<String> getTransitionSuccSet(Transition transition, Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		String tId = transition.getIdentifier();
		Iterator<ModelGraphVertex> itTran = htVertex.get(tId).iterator();
		HashSet<String> succId = new HashSet<String>();
		while(itTran.hasNext()) {
			@SuppressWarnings("unchecked")
			Iterator<Place> itSucc = itTran.next().getSuccessors().iterator();
			while(itSucc.hasNext()) {
				succId.add(itSucc.next().getIdentifier());
			}
		}
		return succId;
	}
	
	/**
	 * get all the predecessor transitions of a place
	 * @param transition
	 * @param htVertex
	 * @return the set of predecessor transitions
	 */
	private HashSet<String> getTransitionPredSet(Transition transition, Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		String tId = transition.getIdentifier();
		Iterator<ModelGraphVertex> itTran = htVertex.get(tId).iterator();
		HashSet<String> predId = new HashSet<String>();
		while(itTran.hasNext()) {
			@SuppressWarnings("unchecked")
			Iterator<Place> itPred = itTran.next().getPredecessors().iterator();
			while(itPred.hasNext()) {
				predId.add(itPred.next().getIdentifier());
			}
		}
		return predId;
	}
	
	/**
	 * get intersection of two cfps
	 * @param cfp1 - will be updated and only the common part will be left
	 * @param cfp2 - will be updated and only the common part will be left
	 * @param alOrder1 - visiting order of transitions in cfp1
	 * @param alOrder2 - visiting order of transitions in cfp2
	 * @return table of transition in cfp1 -> transition in cfp2
	 */
	private Hashtable<String, String> getIntersection(CompleteFinitePrefix cfp1, CompleteFinitePrefix cfp2,
			ArrayList<String> alOrder1, ArrayList<String> alOrder2) {
		Hashtable<String, HashSet<ModelGraphVertex>> htVertex1 = new Hashtable<String, HashSet<ModelGraphVertex>>();
		Hashtable<String, HashSet<ModelGraphVertex>> htVertex2 = new Hashtable<String, HashSet<ModelGraphVertex>>();
		Hashtable<String, String> htCommon = new Hashtable<String, String>();
		//build identifier -> vertex map of cfp1i
		for(ModelGraphVertex v : cfp1.getVerticeList()) {
			if(!htVertex1.containsKey(v.getIdentifier())) {
				htVertex1.put(v.getIdentifier(), new HashSet<ModelGraphVertex>());
			}
			htVertex1.get(v.getIdentifier()).add(v);
		}
		//build identifier -> vertex map of cfp2
		for(ModelGraphVertex v : cfp2.getVerticeList()) {
			if(!htVertex2.containsKey(v.getIdentifier())) {
				htVertex2.put(v.getIdentifier(), new HashSet<ModelGraphVertex>());
			}
			htVertex2.get(v.getIdentifier()).add(v);
		}
		//get common transitions
		for(String s : alOrder1) {
			ModelGraphVertex v1 = htVertex1.get(s).iterator().next();
			if(v1 == null || ((Transition)v1).isInvisibleTask()) {
				continue;
			}
			if(alOrder2.contains(s)) {
				ModelGraphVertex v2 = htVertex2.get(s).iterator().next();
				if(v2 == null || ((Transition)v2).isInvisibleTask()) {
					continue;
				}
				htCommon.put(s, s);
			}
		}
		//get invisible tasks of skip type
		ArrayList<String> alSkipInvTask1 = this.getSkipInvisibleTasks(cfp1, htVertex1);
		ArrayList<String> alSkipInvTask2 = this.getSkipInvisibleTasks(cfp2, htVertex2);
		//get the same skip tasks of two cfps
		for(String tSkipInvId1 : alSkipInvTask1) {
			//if the predecessor places and successor places of two skip tasks are the same
			//these two skip tasks are regarded the same
			Transition tSkipInv1 = (Transition) htVertex1.get(tSkipInvId1).iterator().next();
			HashSet<String> hsTSkipInv1PredPlaceId = this.getTransitionPredSet(tSkipInv1, htVertex1);
			HashSet<String> hsTSkipInv1SuccPlaceId = this.getTransitionSuccSet(tSkipInv1, htVertex1);
			for(String tSkipInvId2 : alSkipInvTask2) {
				Transition tSkipInv2 = (Transition) htVertex2.get(tSkipInvId2).iterator().next();
				HashSet<String> hsTSkipInv2PredPlaceId = this.getTransitionPredSet(tSkipInv2, htVertex2);
				HashSet<String> hsTSkipInv2SuccPlaceId = this.getTransitionSuccSet(tSkipInv2, htVertex2);
				if(!(hsTSkipInv1PredPlaceId.size() == hsTSkipInv2PredPlaceId.size() && hsTSkipInv1SuccPlaceId.size() == hsTSkipInv2SuccPlaceId.size())) {
					continue;
				}
				boolean same = true;
				//confirm if two preds are the same
				Iterator<String> itTSkipInv1PredPlaceId = hsTSkipInv1PredPlaceId.iterator();
				while(itTSkipInv1PredPlaceId.hasNext() && same == true) {
					String pTSkipInv1PredPlaceId = itTSkipInv1PredPlaceId.next();
					Place pTSkipInv1PredPlace = (Place) htVertex1.get(pTSkipInv1PredPlaceId).iterator().next();
					HashSet<String> hsP1PredTranId = this.getPlacePredSet(pTSkipInv1PredPlace, htVertex1, false);
					HashSet<String> hsP1SuccTranId = this.getPlaceSuccSet(pTSkipInv1PredPlace, htVertex1, false);
					same = false;
					Place pTSkipInv2PredPlace = (Place) htVertex2.get(pTSkipInv1PredPlaceId).iterator().next();
					HashSet<String> hsP2PredTranId = this.getPlacePredSet(pTSkipInv2PredPlace, htVertex2, false);
					HashSet<String> hsP2SuccTranId = this.getPlaceSuccSet(pTSkipInv2PredPlace, htVertex2, false);
					if(hsP1PredTranId.equals(hsP2PredTranId) && hsP1SuccTranId.equals(hsP2SuccTranId)) {
						same = true;
						break;
					}
				}
				//confirm if two succs are the same
				Iterator<String> itTSkipInv1SuccPlaceId = hsTSkipInv1SuccPlaceId.iterator();
				while(itTSkipInv1SuccPlaceId.hasNext() && same == true) {
					String pTSkipInv1SuccPlaceId = itTSkipInv1SuccPlaceId.next();
					Place pTSkipInv1SuccPlace = (Place) htVertex1.get(pTSkipInv1SuccPlaceId).iterator().next();
					HashSet<String> hsP1PredTranId = this.getPlacePredSet(pTSkipInv1SuccPlace, htVertex1, false);
					HashSet<String> hsP1SuccTranId = this.getPlaceSuccSet(pTSkipInv1SuccPlace, htVertex1, false);
					same = false;
					Place pTSkipInv2SuccPlace = (Place) htVertex2.get(pTSkipInv1SuccPlaceId).iterator().next();
					HashSet<String> hsP2PredTranId = this.getPlacePredSet(pTSkipInv2SuccPlace, htVertex2, false);
					HashSet<String> hsP2SuccTranId = this.getPlaceSuccSet(pTSkipInv2SuccPlace, htVertex2, false);
					if(hsP1PredTranId.equals(hsP2PredTranId) && hsP1SuccTranId.equals(hsP2SuccTranId)) {
						same = true;
						break;
					}
				}
				if(same == true) {
					htCommon.put(tSkipInvId1, tSkipInvId2);
					break;
				}
			}
		}
		
//		hsCommon.clear();
//		String[] commons = {"zHp", "kl", "x", "MLZ"};
//		for(String s : commons) {
//			hsCommon.add(s);
//		}
		HashSet<String> hsCommon1 = new HashSet<String>();
		HashSet<String> hsCommon2 = new HashSet<String>();
		hsCommon1.addAll(htCommon.keySet());
		hsCommon2.addAll(htCommon.values());
		this.getCommonPart(cfp1, htVertex1, hsCommon1);
		this.getCommonPart(cfp2, htVertex2, hsCommon2);
		return htCommon;
	}
	
	/**
	 * delete transitions which are not in hsCommon
	 * @param cfp
	 * @param htVertex
	 * @param hsCommon - transitions which should be kept
	 */
	private void getCommonPart(CompleteFinitePrefix cfp, Hashtable<String, HashSet<ModelGraphVertex>> htVertex,
			HashSet<String> hsCommon) {
		ArrayList<Transition> alTrans = cfp.getTransitions();
		//Get all visible transitions
		HashSet<String> hsTVisId = new HashSet<String>();
		for(Transition t : alTrans) {
			if(!t.isInvisibleTask()) {
				hsTVisId.add(t.getIdentifier());
			}
		}
		//add skip invisible tasks into set
		ArrayList<String> alSkipTasks = this.getSkipInvisibleTasks(cfp, htVertex);
		hsTVisId.addAll(alSkipTasks);
		//start
		Iterator<String> itTVisId = hsTVisId.iterator();
		ArrayList<String> alMatrix = new ArrayList<String>();
		//Get reachability matrix of cfp
		DoubleMatrix2D anceMatrix2d = this.getReachMatrix(cfp, htVertex.size(), alMatrix, htVertex);
		while(itTVisId.hasNext()) {
			String tVisId = itTVisId.next();
			if(hsCommon.contains(tVisId)) {
				continue;
			}
			Transition tVis = (Transition) htVertex.get(tVisId).iterator().next();
			HashSet<String> hsTPredPlaceId = this.getTransitionPredSet(tVis, htVertex);
			HashSet<String> hsTSuccPlaceId = this.getTransitionSuccSet(tVis, htVertex);
			if(hsTPredPlaceId.size() == 1 && hsTSuccPlaceId.size() == 1) {
				String pTPredPlaceId = hsTPredPlaceId.iterator().next();
				String pTSuccPlaceId = hsTSuccPlaceId.iterator().next();
				if(pTPredPlaceId.equals(pTSuccPlaceId)) {
					//self circle, remove visibleTrans
					Iterator<ModelGraphVertex> itTVis = htVertex.get(tVisId).iterator();
					while(itTVis.hasNext()) {
						Transition delT = (Transition) itTVis.next();
						cfp.delTransition(delT);
					}
					continue;
				}
				//find if there's another trace from tPredPlace to tSuccPlace besides t itself
				ArrayList<String> visited = new ArrayList<String>();
				visited.add(tVisId);
				boolean canReach = this.canReach(pTPredPlaceId, pTSuccPlaceId, htVertex, visited);
				if(canReach == true) {
					//if true, del t
					Iterator<ModelGraphVertex> itTVis = htVertex.get(tVisId).iterator();
					while(itTVis.hasNext()) {
						Transition delT = (Transition) itTVis.next();
						cfp.delTransition(delT);
					}
					continue;
				} else {
					//find lca relation of tPredPlace and tSuccPlace
					int pTPredPlaceIndex = alMatrix.indexOf(pTPredPlaceId);
					int pTSuccPlaceIndex = alMatrix.indexOf(pTSuccPlaceId);
					boolean lcaPredSucc = false;
					boolean lcaSuccPred = false;
					if(anceMatrix2d.get(pTPredPlaceIndex, pTSuccPlaceIndex) > 0.0) {
						lcaPredSucc = true;
					}
					if(anceMatrix2d.get(pTSuccPlaceIndex, pTPredPlaceIndex) > 0.0) {
						lcaSuccPred = true;
					}
					if(lcaPredSucc == true) {
						//pred is succ's ancestor, del t and merge pred and succ
						Place pTPredPlace = (Place) htVertex.get(pTPredPlaceId).iterator().next();
						Iterator<ModelGraphVertex> itTVis = htVertex.get(tVisId).iterator();
						while(itTVis.hasNext()) {
							Transition delT = (Transition) itTVis.next();
							Place pTSuccPlace = (Place) delT.getSuccessors().iterator().next();
							cfp.delTransition(delT);
							pTSuccPlace = cfp.mergePlaces(pTPredPlace, pTSuccPlace);
							Iterator<ModelGraphVertex> itPTSuccPlace = htVertex.get(pTSuccPlaceId).iterator();
							while(itPTSuccPlace.hasNext()) {
								Place succPlace = (Place) itPTSuccPlace.next();
								if(succPlace.getId() == pTSuccPlace.getId()) {
									continue;
								}
								succPlace.setIdentifier(pTPredPlaceId);
								htVertex.get(pTPredPlaceId).add(succPlace);
							}
							htVertex.remove(pTSuccPlaceId);
						}
						continue;
					} else if(lcaSuccPred == true){
						//succ is pred's ancestor, del t
						Iterator<ModelGraphVertex> itTVis = htVertex.get(tVisId).iterator();
						while(itTVis.hasNext()) {
							Transition delT = (Transition) itTVis.next();
							cfp.delTransition(delT);
						}
						continue;
					} else {
						//turn t into a invisible task
						tVis.setLogEvent(null);
					}
				}
			} else {
				//turn visibleTrans into a invisble transition
				tVis.setLogEvent(null);
//				if(visibleTrans.isInvisibleTask()) {
//					System.out.println(visibleTrans.getIdentifier() + "  INV");
//				}
			}
		}
	}
	
	/** 
	 * Get a list of invisible tasks of skip type in a cfp
	 * @param cfp
	 * @param htVertex - the map of label->list(vertex)
	 * @return list of invisible tasks of skip type
	 */
	private ArrayList<String> getSkipInvisibleTasks(CompleteFinitePrefix cfp, Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		ArrayList<Transition> alInvTasks = cfp.getInvisibleTasks();
		ArrayList<String> visitedTasks = new ArrayList<String>();
		ArrayList<String> alSkipTasks = new ArrayList<String>();
		for(Transition t : alInvTasks) {
			if(visitedTasks.contains(t.getIdentifier())) {
				continue;
			}
			visitedTasks.add(t.getIdentifier());
			HashSet<String> tPredPlaceId = this.getTransitionPredSet(t, htVertex);
			HashSet<String> tSuccPlaceId = this.getTransitionSuccSet(t, htVertex);
			//if there is a trace from pred place to succ place other than the invisible task itself
			//this invisible task is of skip type
			boolean canReach = false;
			for(String source : tPredPlaceId) {
				for(String target : tSuccPlaceId) {
					ArrayList<String> visited = new ArrayList<String>();
					visited.add(t.getIdentifier());
					if(this.canReach(source, target, htVertex, visited) == true) {
						canReach = true;
						break;
					}
				}
				if(canReach == true) {
					break;
				}
			}
			if(canReach == true) {
				Iterator<ModelGraphVertex> itInvTask = htVertex.get(t.getIdentifier()).iterator();
				while(itInvTask.hasNext()) {
					ModelGraphVertex v = itInvTask.next();
					v.setAttribute("skip", "skip");
				}
				alSkipTasks.add(t.getIdentifier());
			}
		}
		return alSkipTasks;
	}
	
	/**
	 * Judge if there is a trace from a vertex to another
	 * @param source - source vertex
	 * @param target - target vertex
	 * @param htVertex - the map of label->list(vertex)
	 * @param visited - list of vertices which have been visited
	 * @return a boolean type of value
	 */
	@SuppressWarnings("unchecked")
	private boolean canReach(String source, String target, Hashtable<String, HashSet<ModelGraphVertex>> htVertex,
			ArrayList<String> visited) {
		HashSet<ModelGraphVertex> startNodes = htVertex.get(source);
		while(startNodes.size() > 0) {
			Iterator<ModelGraphVertex> iStart = startNodes.iterator();
			ArrayList<String> exclude = new ArrayList<String>();
			while(iStart.hasNext()) {
				ModelGraphVertex v = iStart.next();
				String vId = v.getIdentifier();
				if(vId.equals(target)) {
					return true;
				} else if(visited.contains(vId)) {
					exclude.add(vId);
					continue;
				}
				visited.add(vId);
			}
			//move forward one level
			iStart = startNodes.iterator();
			HashSet<ModelGraphVertex> descVertex = new HashSet<ModelGraphVertex>();
			while(iStart.hasNext()) {
				ModelGraphVertex v = iStart.next();
				String vId = v.getIdentifier();
				if(exclude.contains(vId)) {
					continue;
				}
				if(visited.contains(vId)) {
					descVertex.addAll(v.getSuccessors());
				}
			}
			startNodes = new HashSet<ModelGraphVertex>();
			startNodes.addAll(descVertex);
		}
		return false;
	}
	
	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cfp
	 * @param cfp
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrix(CompleteFinitePrefix cfp, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		alMatrix.clear();
		HashSet<ModelGraphVertex> startVertex = cfp.getStartNodes();
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<ModelGraphVertex> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				if(alMatrix.contains(vId)) {
//					Iterator<ModelGraphVertex> itVPred = v.getPredecessors().iterator();
//					while(itVPred.hasNext()) {
//						ModelGraphVertex vPred = itVPred.next();
//						String vPredId = vPred.getIdentifier();
//						if(this.alMatrix.contains(vPredId)) {
//							int vPredOrder = this.alMatrix.indexOf(vPredId);
//							int vOrder = this.alMatrix.indexOf(vId);
//							anceMatrix2d.set(vPredOrder, vOrder, 1.0);
//						}
//					}
					continue;
				}
				alMatrix.add(vId);
				reachMatrix2d.set(order, order, 1.0);
				Iterator<ModelGraphVertex> itV = htVertex.get(vId).iterator();
				HashSet<String> hsVPredId = new HashSet<String>();
				while(itV.hasNext()) {
					ModelGraphVertex vt = itV.next();
					Iterator<ModelGraphVertex> itVPred = vt.getPredecessors().iterator();
					while(itVPred.hasNext()) {
						ModelGraphVertex vPred = itVPred.next();
						String vPredId = vPred.getIdentifier();
						hsVPredId.add(vPredId);
					}
				}
				Iterator<String> itVPredId = hsVPredId.iterator();
				while(itVPredId.hasNext()) {
					String VPredId = itVPredId.next();
					int vPredOrder = alMatrix.indexOf(VPredId);
					if(vPredOrder == -1) {
						continue;
					}
					reachMatrix2d.set(vPredOrder, order, 1.0);
					for(int i = 0; i <= vPredOrder; ++i) {
						if(reachMatrix2d.get(i, vPredOrder) != 0.0) {
							reachMatrix2d.set(i, order, 1.0);
						}
					}
				}
				++order;
			}
			
			//move forward one level
			iStartVertex = startVertex.iterator();
			HashSet<ModelGraphVertex> descVertex = new HashSet<ModelGraphVertex>();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				if(alMatrix.contains(vId)) {
					descVertex.addAll(v.getSuccessors());
				}
			}
			startVertex.clear();
			startVertex.addAll(descVertex);
		}
		
		//correct matrix
//		boolean change = true;
//		while(change) {
//			change = false;
//			for(int i = 0; i < n; ++i) {
//				for(int j = 0; j < n; ++j) {
//					if(anceMatrix2d.get(i, j) != 0.0) {
//						for(int k = 0; k < n; ++k) {
//							if(anceMatrix2d.get(j, k) != 0.0 && anceMatrix2d.get(i, k) == 0.0) {
//								anceMatrix2d.set(i, k, 1.0);
//								change = true;
//							}
//						}
//					}
//				}
//			}
//		}

		//System.out.println(this.alMatrix);
		//System.out.println(anceMatrix2d);
		
		return reachMatrix2d;
	}
	
}
