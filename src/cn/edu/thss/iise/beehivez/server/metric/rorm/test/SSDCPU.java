package cn.edu.thss.iise.beehivez.server.metric.rorm.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unfolding.BPNode;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.Condition;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICoSet;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectMatrix2D;

public class SSDCPU {
	public static final double SSD_EXCLUSIVE = -1.0;
	public static final double SSD_UNDEFINED = -2.0;
	public static final double SSD_INFINITY = -3.0;
	
//	@SuppressWarnings("rawtypes")
//	private Hashtable<String, HashSet<IBPNode>> htVertex;
//	public ArrayList<String> alMatrix;
//	
//	public void initSSD() {
//		this.alMatrix = new ArrayList<String>();
//	}
//	
//	@SuppressWarnings("rawtypes")
//	public DoubleMatrix2D getLCA(CompletePrefixUnfolding cpu) {
//		ArrayList<IBPNode> alVertex = new ArrayList<IBPNode>();
//		alVertex.addAll(cpu.getEvents());
//		alVertex.addAll(cpu.getConditions());
////		ArrayList<String> alMatrix = new ArrayList<String>();
////		if (this.alMatrix.size() != 0) {
////			this.alMatrix.clear();
////		}
//		this.alMatrix.clear();
//		//row and col of matrix -> identifier
//		this.htVertex = new Hashtable<String, HashSet<IBPNode>>();
//		for(IBPNode v : alVertex) {
//			if(!this.alMatrix.contains(v.getName())) {
//				this.htVertex.put(v.getName(), new HashSet<IBPNode>());
//				this.alMatrix.add(v.getName());
//				
//			}
//			this.htVertex.get(v.getName()).add(v);
//		}
//		
//		//the row and col of the following matrix are the same with the key of alMatrix, 0...n-1
//		int n = this.htVertex.size();
//		
//		DoubleMatrix2D anceMatrix2dONet = this.getReachMatrixCPU(cpu, n, this.alMatrix, htVertex);
//		
//		DoubleMatrix2D lcaMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
////		ArrayList<Transition> alVisibleTrans = pn.getVisibleTasks();
//		
//		ArrayList<Event> alTrans = new ArrayList<Event>();
//		alTrans.addAll(0, cpu.getEvents());
//		ArrayList<String> visitedTrans = new ArrayList<String>();
//		for(int i = 0; i < alTrans.size(); ++i) {
//			IBPNode tI = alTrans.get(i);
//			if(visitedTrans.contains(tI.getName())) {
//				continue;
//			}
//			int tIIndex = this.alMatrix.indexOf(tI.getName());
//			for(int j = i + 1; j < alTrans.size(); ++j) {
//				IBPNode tJ = alTrans.get(j);
//				if(visitedTrans.contains(tJ.getName())) {
//					continue;
//				}
//				int tJIndex = this.alMatrix.indexOf(tJ.getName());
//				int maxLCAIndex = tIIndex < tJIndex ? tIIndex : tJIndex;
//				for(int pos = maxLCAIndex; pos > 0; --pos) {
//					if(anceMatrix2dONet.get(pos, tIIndex) == 1.0 && anceMatrix2dONet.get(pos, tJIndex) == 1.0) {
//						lcaMatrix2d.set(tIIndex, tJIndex, pos);
//						lcaMatrix2d.set(tJIndex, tIIndex, pos);
//						break;
//					}
//				}
//			}
//			visitedTrans.add(tI.getName());
//		}
//		return lcaMatrix2d;
//	}
//	
//
//	@SuppressWarnings("rawtypes")
//	public DoubleMatrix2D computeSSD(CompletePrefixUnfolding cpu, ArrayList<String> alOrder) {
//		ArrayList<IBPNode> alVertex = new ArrayList<IBPNode>();
//		alVertex.addAll(cpu.getEvents());
//		alVertex.addAll(cpu.getConditions());
//		this.alMatrix.clear();
////		ArrayList<String> alMatrix = new ArrayList<String>();
//		//row and col of matrix -> identifier
//		this.htVertex = new Hashtable<String, HashSet<IBPNode>>();
//		for(IBPNode v : alVertex) {
//			if(!this.alMatrix.contains(v.getName())) {
//				this.htVertex.put(v.getName(), new HashSet<IBPNode>());
//				this.alMatrix.add(v.getName());
//			}
//			this.htVertex.get(v.getName()).add(v);
//		}
////		ArrayList<String> alSkipTasks = this.getSkipInvisibleTasks(pn, this.htVertex);
//		
//		
//		//the row and col of the following matrix are the same with the key of alMatrix, 0...n-1
//		int n = this.htVertex.size();
//		
//		DoubleMatrix2D anceMatrix2dONet = this.getReachMatrix2d(cpu, n, this.alMatrix, htVertex);
//		
//		DoubleMatrix2D lcaMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
////		ArrayList<Event> alVisibleTrans = new ArrayList<Event>();
////		alVisibleTrans.addAll(cpu.)
////		pn.getVisibleTasks();
//		
//		ArrayList<Event> alTrans = new ArrayList<Event>();
//		alTrans.addAll(0, cpu.getEvents());
//		ArrayList<String> visitedTrans = new ArrayList<String>();
//		for(int i = 0; i < alTrans.size(); ++i) {
//			IBPNode tI = alTrans.get(i);
//			if(visitedTrans.contains(tI.getName())) {
//				continue;
//			}
//			int tIIndex = this.alMatrix.indexOf(tI.getName());
//			for(int j = i + 1; j < alTrans.size(); ++j) {
//				IBPNode tJ = alTrans.get(j);
//				if(visitedTrans.contains(tJ.getName())) {
//					continue;
//				}
//				int tJIndex = this.alMatrix.indexOf(tJ.getName());
//				int maxLCAIndex = tIIndex < tJIndex ? tIIndex : tJIndex;
//				for(int pos = maxLCAIndex; pos > 0; --pos) {
//					if(anceMatrix2dONet.get(pos, tIIndex) == 1.0 && anceMatrix2dONet.get(pos, tJIndex) == 1.0) {
//						lcaMatrix2d.set(tIIndex, tJIndex, pos);
//						lcaMatrix2d.set(tJIndex, tIIndex, pos);
//						break;
//					}
//				}
//			}
//			visitedTrans.add(tI.getName());
//		}
//		
//		//initialize shortest synchronization distance matrix
//		DoubleMatrix2D ssdtMatrix2d = DoubleFactory2D.sparse.make(n, n, -3.0);
//		//trace matrix, used in computing parallel structure
//		ObjectMatrix2D traceMatrix2d = ObjectFactory2D.sparse.make(n, n, new ArrayList<String>());
//		//get all transitions
//		//ArrayList<Transitions> alTrans, already defined above
//		//to mark transitions which have been handled
//		visitedTrans.clear();
//		//for sequential relations, set distance to 1 (predecessor to successor)
//		for(Event t : alTrans) {
//			HashSet<String> hsSuccPlaceId = this.getTransitionSuccSet(t, this.htVertex);
//			if(hsSuccPlaceId.size() > 1) {
//				//parallel structure, pass
//				continue;
//			}
//			String tId = t.getName();
//			if(visitedTrans.contains(tId)) {
//				continue;
//			}
//			int tIndex = this.alMatrix.indexOf(tId);
//			Iterator<IBPNode> itT = this.htVertex.get(tId).iterator();
//			while(itT.hasNext()) {
//				Iterator<Condition> itTSucc = ((Event) itT.next()).getPostConditions().iterator();
//				while(itTSucc.hasNext()) {
//					Condition tSuccPlace = itTSucc.next();
//					Iterator<IBPNode> itTSuccPlace = this.htVertex.get(tSuccPlace.getName()).iterator();
//					while(itTSuccPlace.hasNext()) {
//						Iterator<Event> itPSuccEvent = ((Condition) itTSuccPlace.next()).getPostE().iterator();
//						while(itPSuccEvent.hasNext()) {
//							Event tSuccEvent = itPSuccEvent.next();
//							//set ssd from t to tSuccTran with 1.0
//							String tSuccEventId = tSuccEvent.getName();
//							int tSuccEventIndex = this.alMatrix.indexOf(tSuccEventId);
//							ssdtMatrix2d.set(tIndex, tSuccEventIndex, 1.0);
//							//set trace from t to tSuccTran
//							ArrayList<String> trace = new ArrayList<String>();
//							trace.add(tId);
//							trace.add(tSuccEventId);
//							traceMatrix2d.set(tIndex, tSuccEventIndex, trace);
//						}
//					}
//				}
//			}
//			visitedTrans.add(t.getName());
//		}
//
//		//add invisible tasks of skip type into set of visible tasks
////		for(String s : alSkipTasks) {
////			Iterator<ModelGraphVertex> it = this.htVertex.get(s).iterator();
////			while(it.hasNext()) {
////				alVisibleTrans.add((Transition) it.next());
////			}
////		}
//		
//		//compute ssdt between other pairs of transitions recursively
//		ArrayList<String> alVisitedITrans = new ArrayList<String>();
//		for(int i = 0; i < alTrans.size(); ++i) {
//			Event tI = alTrans.get(i);
//			String tIId = tI.getName();
//			if(alVisitedITrans.contains(tIId)) {
//				continue;
//			}
//			int tIIndex = this.alMatrix.indexOf(tIId);
//			ArrayList<String> alVisitedJTrans = new ArrayList<String>();
//			for(int j = 0; j < alTrans.size(); ++j) {
//				Event tJ = alTrans.get(j);
//				String tJId = tJ.getName();
//				if(alVisitedJTrans.contains(tJId)) {
//					continue;
//				}
//				int tJIndex = this.alMatrix.indexOf(tJId);
//				ArrayList<String> visited = new ArrayList<String>();
//				ArrayList<String> trace = new ArrayList<String>();
//				int ssdt = this.computeRecur(tI, tJ, ssdtMatrix2d, trace, visited, this.alMatrix, this.htVertex, traceMatrix2d);
//				if(ssdt > 0) {
//					ssdtMatrix2d.set(tIIndex, tJIndex, ssdt);
//					traceMatrix2d.set(tIIndex, tJIndex, trace);
//				}
//				alVisitedJTrans.add(tJId);
//			}
//			alVisitedITrans.add(tIId);
//		}
//		
//		//for parallel transitions, set distance to 1 (both directions)
//		//for exclusive transitions, set distance to -1 (relation "x")
//		visitedTrans.clear();
//		for(int i = 0; i < alTrans.size(); ++i) {
//			Event tI = alTrans.get(i);
//			String tIId = tI.getName();
//			if(visitedTrans.contains(tIId)) {
//				continue;
//			}
//			int tIIndex = this.alMatrix.indexOf(tIId);
//			for(int j = i + 1; j < alTrans.size(); ++j) {
//				Event tJ = alTrans.get(j);
//				String tJId = tJ.getName();
//				if(visitedTrans.contains(tJId)) {
//					continue;
//				}
//				int tJIndex = this.alMatrix.indexOf(tJId);
//				int lcaIndex = (int)lcaMatrix2d.get(tIIndex, tJIndex);
//				if(lcaIndex != tIIndex && lcaIndex != tJIndex) {
//					Iterator<IBPNode> itVertex = this.htVertex.get(this.alMatrix.get(lcaIndex)).iterator();
//					if(itVertex.hasNext()) {
//						IBPNode vLCA = itVertex.next();
//						if(vLCA instanceof Event) {
//							// parallel
//							if(!(ssdtMatrix2d.get(tIIndex, tJIndex) > 0 || ssdtMatrix2d.get(tJIndex, tIIndex) > 0)) {
//								ssdtMatrix2d.set(tIIndex, tJIndex, 1.0);
//								ArrayList<String> trace = new ArrayList<String>();
//								trace.add(tIId);
//								trace.add(tJId);
//								traceMatrix2d.set(tIIndex, tJIndex, trace);
//								ssdtMatrix2d.set(tJIndex, tIIndex, 1.0);
//								trace = new ArrayList<String>();
//								trace.add(tJId);
//								trace.add(tIId);
//								traceMatrix2d.set(tJIndex, tIIndex, trace);
//							}
//						} else if(vLCA instanceof Condition) {
//							// exclusive
//							if(!(ssdtMatrix2d.get(tIIndex, tJIndex) > 0 || ssdtMatrix2d.get(tJIndex, tIIndex) > 0)) {
//								ssdtMatrix2d.set(tIIndex, tJIndex, -1.0);
//								ssdtMatrix2d.set(tJIndex, tIIndex, -1.0);
//							}
//						}
//					}
//				}
//			}
//			visitedTrans.add(tIId);
//		}
//		
//		ArrayList<Integer> alOrderNum = new ArrayList<Integer>();
//		for(Event tI : alTrans) {
//			String tIId = tI.getName();
//			int tIIndex = this.alMatrix.indexOf(tIId);
//			if(alOrderNum.contains(tIIndex)) {
//				continue;
//			}
//			alOrderNum.add(tIIndex);
//		}
//		DoubleMatrix2D ssdMatrix = DoubleFactory2D.sparse.make(alOrderNum.size(), alOrderNum.size(), 0);
//		for(int i = 0; i < alOrderNum.size(); ++i) {
//			for(int j = 0; j < alOrderNum.size(); ++j) {
//				ssdMatrix.set(i, j, ssdtMatrix2d.get(alOrderNum.get(i), alOrderNum.get(j)));
//			}
//		}
//		alOrder.clear();
//		for(int i = 0; i < alOrderNum.size(); ++i) {
//			alOrder.add(this.alMatrix.get(alOrderNum.get(i)));
//		}
//		
//		return ssdMatrix;
//	}
//	
//
//	/**
//	 * compute ssd between other pairs of transitions recursively
//	 * @param tI - source transition
//	 * @param tJ - target transition
//	 * @param ssdtMatrix2d - ssdt Matrix
//	 * @param trace - shortest trace from tI to tJ
//	 * @param visited - store visited transitions
//	 * @param alMatrix - all the ids of vertices
//	 * @param htVertex - the map of label->list(vertex)
//	 * @param traceMatrix2d - shortest trace Matrix
//	 * @return
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private int computeRecur(Event tI, Event tJ, DoubleMatrix2D ssdtMatrix2d, ArrayList<String> trace, 
//				ArrayList<String> visited, ArrayList<String> alMatrix, 
//				Hashtable<String, HashSet<IBPNode>> htVertex, 
//				ObjectMatrix2D traceMatrix2d) {
//		String tIId = tI.getName();
//		if(visited.contains(tIId)) {
//			//has already been visited, return infinity
//			return -3;
//		}
//		String tJId = tJ.getName();
//		if(tIId.equals(tJId) && !visited.isEmpty()) {
//			//arrive at tJ, return 0
//			trace.add(tJId);
//			return 0;
//		}
//		int tIIndex = alMatrix.indexOf(tIId);
//		int tJIndex = alMatrix.indexOf(tJId);
//		if(ssdtMatrix2d.get(tIIndex, tJIndex) > 0.0) {
//			trace.addAll((Collection<? extends String>) traceMatrix2d.get(tIIndex, tJIndex));
//			return (int) ssdtMatrix2d.get(tIIndex, tJIndex);
//		}
//		//start, add tI to visited
//		visited.add(tIId);
//		//compute ssd recursively (sequential, exclusive, parallel)
//		HashSet<String> hsTISuccPlace = this.getTransitionSuccSet(tI, htVertex);
//		int nTISuccPlace = hsTISuccPlace.size();
//		if(nTISuccPlace == 1) {
//			//sequential or exclusive, tI has only one output place
//			Iterator<IBPNode> itTI = htVertex.get(tIId).iterator();
//			Condition pTISuccPlace = null;
//			while(itTI.hasNext() && pTISuccPlace == null) {
//				Iterator<Condition> itTISuccPlace = ((Event) itTI.next()).getPostConditions().iterator();
//				while(itTISuccPlace.hasNext()) {
//					pTISuccPlace = itTISuccPlace.next();
//					break;
//				}
//			}
//			HashSet<String> hsPSuccTran = this.getPlaceSuccSet(pTISuccPlace, htVertex, true);
//			int nPSuccTran = hsPSuccTran.size();
//			if(nPSuccTran == 0) {
//				//cannot reach tj, return infinity
//				return -3;
//			} else if(nPSuccTran == 1) {
//				//sequential
//				Iterator<IBPNode> itPlace = htVertex.get(pTISuccPlace.getName()).iterator();
//				Event tPSuccTran = null;
//				while(itPlace.hasNext() && tPSuccTran == null) {
//					Iterator<Event> itTSucc = ((Condition) itPlace.next()).getPostE().iterator();
//					while(itTSucc.hasNext()) {
//						tPSuccTran = itTSucc.next();
//						break;
//					}
//				}
//				ArrayList<String> succTrace = new ArrayList<String>();
//				int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, visited, alMatrix, htVertex, traceMatrix2d);
////				if(succSSDT == -3) {
////					return -3;
////				} else if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
////					trace.add(tIId);
////					trace.addAll(succTrace);
////					return 1 + succSSDT;
////				} else {
////					trace.add(tIId);
////					trace.addAll(succTrace);
////					return succSSDT;
////				}
//				if(succSSDT == -3) {
//					return -3;
//				} else {
//					trace.add(tIId);
//					trace.addAll(succTrace);
//					return 1 + succSSDT;
//				}
//			} else {
//				//exclusive
//				int minSuccSSDT = -3;
//				ArrayList<String> tmpTrace = new ArrayList<String>();
//				ArrayList<String> visitedCopy = new ArrayList<String>();
//				visitedCopy.addAll(visited);
//				for(String pSuccTranId : hsPSuccTran) {
//					Iterator<IBPNode> itPSuccTran = htVertex.get(pSuccTranId).iterator();
//					if(itPSuccTran.hasNext()) {
//						Event tPSuccTran = (Event) itPSuccTran.next();
//						ArrayList<String> succTrace = new ArrayList<String>();
//						ArrayList<String> tmpVisited = new ArrayList<String>();
//						tmpVisited.addAll(visitedCopy);
//						int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
//						if(succSSDT != -3) {
////							if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
////								++succSSDT;
////							}
//							++succSSDT;
//							if(minSuccSSDT == -3 || succSSDT < minSuccSSDT) {
//								tmpTrace.clear();
//								tmpTrace.addAll(succTrace);
//								minSuccSSDT = succSSDT;
//							}
//						}
//						for(String s : tmpVisited) {
//							if(!(visited.contains(s))) {
//								visited.add(s);
//							}
//						}
//					}
//				}
//				if(minSuccSSDT != -3) {
//					trace.add(tIId);
//					trace.addAll(tmpTrace);
//				}
//				return minSuccSSDT;
//			}
//		} else if(nTISuccPlace > 1) {
//			//parallel, tI has more than one output place
//			Iterator<String> itTISuccPlaceId = hsTISuccPlace.iterator();
//			int nTotalSSDT = 0;
//			int nParallel = 0;
//			ArrayList<ArrayList<String>> alSuccTrace = new ArrayList<ArrayList<String>>();
//			ArrayList<String> visitedCopy = new ArrayList<String>();
//			visitedCopy.addAll(visited);
//			while(itTISuccPlaceId.hasNext()) {
//				Iterator<IBPNode> itTISuccPlace = htVertex.get(itTISuccPlaceId.next()).iterator();
//				Condition pTISuccPlace = (Condition) itTISuccPlace.next();
//				HashSet<String> hsPSuccTran = this.getPlaceSuccSet(pTISuccPlace, htVertex, true);
//				int nPSuccTran = hsPSuccTran.size();
//				ArrayList<String> tmpVisited = new ArrayList<String>();
//				tmpVisited.addAll(visitedCopy);
//				if(nPSuccTran == 0) {
//					//cannot reach tj, no action
//				} else if(nPSuccTran == 1) {
//					//sequential
//					Iterator<String> itPSuccTranId = hsPSuccTran.iterator();
//					Event tPSuccTran = (Event) htVertex.get(itPSuccTranId.next()).iterator().next();
//					ArrayList<String> succTrace = new ArrayList<String>();
//					int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
//					if(succSSDT != -3) {
//						nTotalSSDT += (1 + succSSDT);
//						++nParallel;
////						if(tPSuccTran.isInvisibleTask()/* && newTi.getAttribute("skip") == null*/) {
////							--nTotalSSDT;
////						}
//						alSuccTrace.add(succTrace);
//					}
//					for(String s : tmpVisited) {
//						if(!visited.contains(s)) {
//							visited.add(s);
//						}
//					}
//				} else {
//					//exclusive
//					Iterator<String> itPSuccTranId = hsPSuccTran.iterator();
//					int minSuccSSDT = -3;
//					ArrayList<String> tmpTrace = new ArrayList<String>();
//					while(itPSuccTranId.hasNext()) {
//						Iterator<IBPNode> itPSuccTran = htVertex.get(itPSuccTranId.next()).iterator();
//						if(itPSuccTran.hasNext()) {
//							Event tPSuccTran = (Event) itPSuccTran.next();
//							ArrayList<String> succTrace = new ArrayList<String>();
//							tmpVisited.clear();
//							tmpVisited.addAll(visited);
//							int succSSDT = this.computeRecur(tPSuccTran, tJ, ssdtMatrix2d, succTrace, tmpVisited, alMatrix, htVertex, traceMatrix2d);
//							if(succSSDT != -3) {
////								if(!tPSuccTran.isInvisibleTask()/* || newTi.getAttribute("skip") != null*/) {
////									++succSSDT;
////								}
//								++succSSDT;
//								if(minSuccSSDT == -3 || succSSDT < minSuccSSDT) {
//									tmpTrace.clear();
//									tmpTrace.addAll(succTrace);
//									minSuccSSDT = succSSDT;
//								}
//							}
//							for(String s : tmpVisited) {
//								if(!visited.contains(s)) {
//									visited.add(s);
//								}
//							}
//						}
//					}
//					if(minSuccSSDT != -3) {
//						alSuccTrace.add(tmpTrace);
//						nTotalSSDT += minSuccSSDT;
//						++nParallel;	
//					}
//				}
//			}
//			if(nParallel == 0) {
//				return nTotalSSDT;
//			} else {
//				//get merge trace
//				ArrayList<String> parallelSuccTrace = new ArrayList<String>();
//				for(ArrayList<String> succTrace : alSuccTrace) {
//					for(String s : succTrace) {
//						if(parallelSuccTrace.isEmpty() || !parallelSuccTrace.contains(s)) {
//							parallelSuccTrace.add(s);
//						}
//					}
//				}
//				trace.add(tIId);
//				trace.addAll(parallelSuccTrace);
//				int tracelength = trace.size() - 1;
//				return tracelength;
//			}
//		} else {
//			//nSucc = 0, cannot reach tj
//			return -3;
//		}
//	}
//	
//	/**
//	 * get all the successor places of a transition
//	 * @param place
//	 * @param htVertex
//	 * @param hasInv - whether the result should contain invisible tasks
//	 * @return the set of successor places
//	 */
//	@SuppressWarnings("rawtypes")
//	private HashSet<String> getPlaceSuccSet(Condition place, Hashtable<String, HashSet<IBPNode>> htVertex, boolean hasInv) {
//		Iterator<IBPNode> itPlace = htVertex.get(place.getName()).iterator();
//		HashSet<String> succId = new HashSet<String>();
//		while(itPlace.hasNext()) {
//			Iterator<Event> itSucc = ((Condition) itPlace.next()).getPostE().iterator();
//			while(itSucc.hasNext()) {
//				Event pSucc = itSucc.next();
//				String pSuccId = pSucc.getName();
////				if(hasInv == false && pSucc.isInvisibleTask()) {
////					continue;
////				}
//				succId.add(pSuccId);
//			}
//		}
//		return succId;
//	}
//	
//	/**
//	 * get all the successor event of a place
//	 * @param event
//	 * @param htVertex
//	 * @return the set of successor transitions
//	 */
//	@SuppressWarnings("rawtypes")
//	private HashSet<String> getTransitionSuccSet(Event event, Hashtable<String, HashSet<IBPNode>> htVertex) {
//		String tId = event.getName();
//		Iterator<IBPNode> itTran = htVertex.get(tId).iterator();
//		HashSet<String> succId = new HashSet<String>();
//		while(itTran.hasNext()) {
//			Iterator<Condition> itSucc = ((Event) itTran.next()).getPostConditions().iterator();
//			while(itSucc.hasNext()) {
//				succId.add(itSucc.next().getName());
//			}
//		}
//		return succId;
//	}
//	
//	/**
//	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
//	 * @param cpu
//	 * @param n - number of vertices
//	 * @param alMatrix - store the visiting order of vertices
//	 * @param htVertex - store the map of label->list(vertex)
//	 * @return the reachable matrix
//	 */
//	@SuppressWarnings({ "rawtypes" })
//	private DoubleMatrix2D getReachMatrixCPU(CompletePrefixUnfolding cpu, int n, ArrayList<String> alMatrix, 
//			Hashtable<String, HashSet<IBPNode>> htVertex) {
//		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
//		alMatrix.clear();
//		HashSet<IBPNode> startVertex = new HashSet<IBPNode>();
//		
//		startVertex.addAll(cpu.getInitialCut());
//		
//		int order = 0;
//		while(startVertex.size() > 0) {
//			Iterator<IBPNode> iStartVertex = startVertex.iterator();
//			while(iStartVertex.hasNext()) {
//				IBPNode v = iStartVertex.next();
//				String vId = v.getName();
//				if(alMatrix.contains(vId)) {
//					continue;
//				}
//				alMatrix.add(vId);
//				reachMatrix2d.set(order, order, 1.0);
//				Iterator<IBPNode> itV = htVertex.get(vId).iterator();
//				HashSet<String> hsVPredId = new HashSet<String>();
//				while(itV.hasNext()) {
//					IBPNode vt = itV.next();
//					if (vt instanceof Condition) {
//						if (((Condition) vt).getPreEvent() != null) {
//							hsVPredId.add(((Condition) vt).getPreEvent().getName());
//						}
//					}
//					else if (vt instanceof Event) {
//						Iterator<Condition> itVPred = ((Event) vt).getPreConditions().iterator();
//						while(itVPred.hasNext()) {
//							Condition vPred = itVPred.next();
//							String vPredId = vPred.getName();
//							hsVPredId.add(vPredId);
//						}
//					}
//				}
//				Iterator<String> itVPredId = hsVPredId.iterator();
//				while(itVPredId.hasNext()) {
//					String VPredId = itVPredId.next();
//					int vPredOrder = alMatrix.indexOf(VPredId);
//					if(vPredOrder == -1) {
//						continue;
//					}
//					reachMatrix2d.set(vPredOrder, order, 1.0);
//					for(int i = 0; i <= vPredOrder; ++i) {
//						if(reachMatrix2d.get(i, vPredOrder) != 0.0) {
//							reachMatrix2d.set(i, order, 1.0);
//						}
//					}
//				}
//				++order;
//			}
//			
//			//move forward one level
//			iStartVertex = startVertex.iterator();
//			HashSet<IBPNode> descVertex = new HashSet<IBPNode>();
//			while(iStartVertex.hasNext()) {
//				IBPNode v = iStartVertex.next();
// 				String vId = v.getName();
// 				if(alMatrix.contains(vId)) {
// 					if (v instanceof Condition) {
// 						descVertex.addAll(((Condition) v).getPostE());
//					}
//					else if (v instanceof Event) {
//						descVertex.addAll(((Event) v).getPostConditions());
//					}
//				}
//			}
//			startVertex.clear();
//			startVertex.addAll(descVertex);
//		}
//		
//		Hashtable<String, HashSet<IBPNode>> reachVertex = new Hashtable<String, HashSet<IBPNode>>();
//		for(int irow = 0; irow < alMatrix.size(); irow++){
//			reachVertex.put(alMatrix.get(irow), new HashSet<IBPNode>());
//			for(int jcol = 0; jcol < alMatrix.size(); jcol++) {
//				if (reachMatrix2d.get(irow, jcol) != 0.0 && irow != jcol) {
//					reachVertex.get(alMatrix.get(irow)).addAll(this.htVertex.get(alMatrix.get(jcol)));
//				}
//			}
//		}
//		
//		Iterator<Event> iCutOffEvents = cpu.getCutoffEvents().iterator();
//		while(iCutOffEvents.hasNext()) {
//			Event e = iCutOffEvents.next();
//			int eIndex = alMatrix.indexOf(e.getName());
//			
////			Event eCor = cpu.getCorrespondingEvent(e);
////			int eCorIndex = alMatrix.indexOf(eCor.getName());
//
////			ICoSet<BPNode, Condition, Event, Flow, Node, Place, Transition, Marking> ePostConditions = e.getPostConditions();
////			for (Condition eSucc : e.getPostConditions()) {
////				Iterator<Condition> iESuccMappedContions = eSucc.getMappingConditions().iterator();
////				while(iESuccMappedContions.hasNext()) {
////					Condition eSuccMappedContion = iESuccMappedContions.next();
////					if (eSuccMappedContion.getName().equalsIgnoreCase(eSucc.getName())) {
////						continue;
////					}
////					Event eCorAccordingToCut = eSuccMappedContion.getPreEvent();
////					Iterator<IBPNode> iEventCorReachList = reachVertex.get(eCorAccordingToCut.getName()).iterator();
////					while(iEventCorReachList.hasNext()) {
////						IBPNode v = iEventCorReachList.next();
////						int reachableIndex = alMatrix.indexOf(v.getName());
////						reachMatrix2d.set(eIndex, reachableIndex, 1.0);
////					}
//////					int reachableIndex = alMatrix.indexOf(eSuccMappedContion.getName());
//////					reachMatrix2d.set(eIndex, reachableIndex, 1.0);
////				}
////			}
//			
////			for (Condition eCorSucc : eCor.getPostConditions()) {
////				Iterator<Condition> iECorSuccMappedContions = eCorSucc.getMappingConditions().iterator();
////				while(iECorSuccMappedContions.hasNext()) {
////					Condition eCorSuccMappedContion = iECorSuccMappedContions.next();
////					if (eCorSuccMappedContion.getName().equalsIgnoreCase(eCorSucc.getName())) {
////						continue;
////					}
////					int reachableIndex = alMatrix.indexOf(eCorSuccMappedContion.getName());
////					reachMatrix2d.set(eCorIndex, reachableIndex, 1.0);
////				}
////			}
//			
//			
////			// for e
////			Iterator<IBPNode> iEventReachList = reachVertex.get(e.getName()).iterator();
////			while(iEventReachList.hasNext()) {
////				IBPNode v = iEventReachList.next();
////				int reachableIndex = alMatrix.indexOf(v.getName());
////				reachMatrix2d.set(eCorIndex, reachableIndex, 1.0);
////			}
////			// for corresponding event
////			Iterator<IBPNode> iEventCorReachList = reachVertex.get(eCor.getName()).iterator();
////			while(iEventCorReachList.hasNext()) {
////				IBPNode v = iEventCorReachList.next();
////				int reachableIndex = alMatrix.indexOf(v.getName());
////				reachMatrix2d.set(eIndex, reachableIndex, 1.0);
////			}
//		}
//		
//		return reachMatrix2d;
//	}
//	
//	
//	/**
//	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
//	 * @param cpu
//	 * @param n - number of vertices
//	 * @param alMatrix - store the visiting order of vertices
//	 * @param htVertex - store the map of label->list(vertex)
//	 * @return the reachable matrix
//	 */
//	@SuppressWarnings({ "rawtypes" })
//	private DoubleMatrix2D getReachMatrix2d(CompletePrefixUnfolding cpu, int n, ArrayList<String> alMatrix, 
//			Hashtable<String, HashSet<IBPNode>> htVertex) {
//		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
//		alMatrix.clear();
//		HashSet<IBPNode> startVertex = new HashSet<IBPNode>();
//		
//		startVertex.addAll(cpu.getInitialCut());
//		
//		int order = 0;
//		while(startVertex.size() > 0) {
//			Iterator<IBPNode> iStartVertex = startVertex.iterator();
//			while(iStartVertex.hasNext()) {
//				IBPNode v = iStartVertex.next();
//				String vId = v.getName();
//				if(alMatrix.contains(vId)) {
//					continue;
//				}
//				alMatrix.add(vId);
//				reachMatrix2d.set(order, order, 1.0);
//				Iterator<IBPNode> itV = htVertex.get(vId).iterator();
//				HashSet<String> hsVPredId = new HashSet<String>();
//				while(itV.hasNext()) {
//					IBPNode vt = itV.next();
//					if (vt instanceof Condition) {
//						if (((Condition) vt).getPreEvent() != null) {
//							hsVPredId.add(((Condition) vt).getPreEvent().getName());
//						}
//					}
//					else if (vt instanceof Event) {
//						Iterator<Condition> itVPred = ((Event) vt).getPreConditions().iterator();
//						while(itVPred.hasNext()) {
//							Condition vPred = itVPred.next();
//							String vPredId = vPred.getName();
//							hsVPredId.add(vPredId);
//						}
//					}
//				}
//				Iterator<String> itVPredId = hsVPredId.iterator();
//				while(itVPredId.hasNext()) {
//					String VPredId = itVPredId.next();
//					int vPredOrder = alMatrix.indexOf(VPredId);
//					if(vPredOrder == -1) {
//						continue;
//					}
//					reachMatrix2d.set(vPredOrder, order, 1.0);
//					for(int i = 0; i <= vPredOrder; ++i) {
//						if(reachMatrix2d.get(i, vPredOrder) != 0.0) {
//							reachMatrix2d.set(i, order, 1.0);
//						}
//					}
//				}
//				++order;
//			}
//			
//			//move forward one level
//			iStartVertex = startVertex.iterator();
//			HashSet<IBPNode> descVertex = new HashSet<IBPNode>();
//			while(iStartVertex.hasNext()) {
//				IBPNode v = iStartVertex.next();
// 				String vId = v.getName();
// 				if(alMatrix.contains(vId)) {
// 					if (v instanceof Condition) {
// 						descVertex.addAll(((Condition) v).getPostE());
//					}
//					else if (v instanceof Event) {
//						descVertex.addAll(((Event) v).getPostConditions());
//					}
//				}
//			}
//			startVertex.clear();
//			startVertex.addAll(descVertex);
//		}
//		return reachMatrix2d;
//	}
}
