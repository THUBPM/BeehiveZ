package cn.edu.thss.iise.beehivez.server.metric.rorm.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.unfolding.BPNode;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.Condition;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.ICoSet;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectMatrix2D;
import cn.edu.thss.iise.beehivez.server.metric.rorm.jbpt.conversion.PetriNetConversion;

public class SSD {
	public static final double SSD_EXCLUSIVE = -1.0;
	public static final double SSD_UNDEFINED = -2.0;
	public static final double SSD_INFINITY = -3.0;
	
	public Hashtable<String, HashSet<ModelGraphVertex>> htVertex;
	public int n;
	public ArrayList<String> alMatrix;
	
	public DoubleMatrix2D getLCA(CompletePrefixUnfolding cpu) {
		PetriNet pn = PetriNetConversion.convert(cpu);
		ArrayList<ModelGraphVertex> alVertex = pn.getVerticeList();
		ArrayList<String> alMatrix = new ArrayList<String>();
		//row and col of matrix -> identifier
		this.htVertex = new Hashtable<String, HashSet<ModelGraphVertex>>();
		for(ModelGraphVertex v : alVertex) {
			if(!alMatrix.contains(v.getIdentifier())) {
				this.htVertex.put(v.getIdentifier(), new HashSet<ModelGraphVertex>());
				alMatrix.add(v.getIdentifier());
			}
			this.htVertex.get(v.getIdentifier()).add(v);
		}
		
		//the row and col of the following matrix are the same with the key of alMatrix, 0...n-1
		int n = this.htVertex.size();
		
//		DoubleMatrix2D anceMatrix2d = this.getReachMatrix(pn, n, alMatrix, this.htVertex);
		
//		DoubleMatrix2D anceMatrix2dONet = this.getReachMatrixOriginalNet2(pn, n, alMatrix, this.htVertex);
		DoubleMatrix2D anceMatrix2dONet = this.getReachMatrixCPU(cpu, n, alMatrix, htVertex);
		
		DoubleMatrix2D lcaMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		ArrayList<Transition> alVisibleTrans = pn.getVisibleTasks();
		ArrayList<Transition> alTrans = pn.getTransitions();
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
					if(anceMatrix2dONet.get(pos, tIIndex) == 1.0 && anceMatrix2dONet.get(pos, tJIndex) == 1.0) {
						lcaMatrix2d.set(tIIndex, tJIndex, pos);
						lcaMatrix2d.set(tJIndex, tIIndex, pos);
						break;
					}
				}
			}
			visitedTrans.add(tI.getIdentifier());
		}
		return lcaMatrix2d;
	}
	
	public DoubleMatrix2D computeSSD(CompletePrefixUnfolding cpu, ArrayList<String> alOrder) {
		PetriNet pn = PetriNetConversion.convert(cpu);
		ArrayList<ModelGraphVertex> alVertex = pn.getVerticeList();
		ArrayList<String> alMatrix = new ArrayList<String>();
		//row and col of matrix -> identifier
		this.htVertex = new Hashtable<String, HashSet<ModelGraphVertex>>();
		for(ModelGraphVertex v : alVertex) {
			if(!alMatrix.contains(v.getIdentifier())) {
				this.htVertex.put(v.getIdentifier(), new HashSet<ModelGraphVertex>());
				alMatrix.add(v.getIdentifier());
			}
			this.htVertex.get(v.getIdentifier()).add(v);
		}
		//get all invisible tasks of skip type
		ArrayList<String> alSkipTasks = this.getSkipInvisibleTasks(pn, this.htVertex);
		//System.out.println(alSkipTasks);

		//the row and col of the following matrix are the same with the key of alMatrix, 0...n-1
		int n = this.htVertex.size();
		//Preprocess descendants table of any vertex
		
		DoubleMatrix2D anceMatrix2d = this.getReachMatrix(pn, n, alMatrix, this.htVertex);
		
		DoubleMatrix2D lcaMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		ArrayList<Transition> alVisibleTrans = pn.getVisibleTasks();
		ArrayList<Transition> alTrans = pn.getTransitions();
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
			HashSet<String> hsSuccPlaceId = this.getTransitionSuccSet(t, this.htVertex);
			if(hsSuccPlaceId.size() > 1) {
				//parallel structure, pass
				continue;
			}
			String tId = t.getIdentifier();
			if(visitedTrans.contains(tId)) {
				continue;
			}
			int tIndex = alMatrix.indexOf(tId);
			Iterator<ModelGraphVertex> itT = this.htVertex.get(tId).iterator();
			while(itT.hasNext()) {
				Iterator<Place> itTSucc = itT.next().getSuccessors().iterator();
				while(itTSucc.hasNext()) {
					Place tSuccPlace = itTSucc.next();
					Iterator<ModelGraphVertex> itTSuccPlace = this.htVertex.get(tSuccPlace.getIdentifier()).iterator();
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
			Iterator<ModelGraphVertex> it = this.htVertex.get(s).iterator();
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
				int ssdt = this.computeRecur(tI, tJ, ssdtMatrix2d, trace, visited, alMatrix, this.htVertex, traceMatrix2d);
				if(ssdt > 0) {
					ssdtMatrix2d.set(tIIndex, tJIndex, ssdt);
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
					Iterator<ModelGraphVertex> itVertex = this.htVertex.get(alMatrix.get(lcaIndex)).iterator();
					if(itVertex.hasNext()) {
						ModelGraphVertex vLCA = itVertex.next();
						if(vLCA instanceof Transition) {
							// parallel
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
							// exclusive
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
		
		return ssdMatrix;
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
	 * Get a list of invisible tasks of skip type in a cpu
	 * @param pn
	 * @param htVertex - the map of label->list(vertex)
	 * @return list of invisible tasks of skip type
	 */
	private ArrayList<String> getSkipInvisibleTasks(PetriNet pn, Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		ArrayList<Transition> alInvTasks = pn.getInvisibleTasks();
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
			}
		} else {
			//nSucc = 0, cannot reach tj
			return -3;
		}
	}
	
	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
	 * @param pn
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrix(PetriNet pn, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		alMatrix.clear();
		HashSet<ModelGraphVertex> startVertex = new HashSet<ModelGraphVertex>();
		startVertex.add(pn.getSource());
		
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<ModelGraphVertex> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				if(alMatrix.contains(vId)) {
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
		
		return reachMatrix2d;
	}

	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
	 * @param pn
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrixCPU2(CompletePrefixUnfolding cpu, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		PetriNet pn = PetriNetConversion.convert(cpu);
		
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		alMatrix.clear();
		HashSet<Node> startVertex = new HashSet<Node>();
		NetSystem ns = (NetSystem) cpu.getOriginativeNetSystem();
		startVertex.addAll(ns.getSourceNodes());
		
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<Node> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				Node v = iStartVertex.next();
				String vId = v.getName();
				if(alMatrix.contains(vId)) {
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
			HashSet<Node> descVertex = new HashSet<Node>();
			while(iStartVertex.hasNext()) {
				Node v = iStartVertex.next();
 				String vId = v.getName();
 				if(alMatrix.contains(vId)) {
					descVertex.addAll(ns.getPostset(v));
				}
			}
			startVertex.clear();
			startVertex.addAll(descVertex);
		}
		
		Hashtable<String, HashSet<ModelGraphVertex>> reachVertex = new Hashtable<String, HashSet<ModelGraphVertex>>();
		for(int irow = 0; irow < alMatrix.size(); irow++){
			reachVertex.put(alMatrix.get(irow), new HashSet<ModelGraphVertex>());
			for(int jcol = 0; jcol < alMatrix.size(); jcol++) {
				if (reachMatrix2d.get(irow, jcol) != 0.0 && irow != jcol) {
					reachVertex.get(alMatrix.get(irow)).addAll(this.htVertex.get(alMatrix.get(jcol)));
				}
			}
		}
		
		Iterator<Event> iCutOffEvents = cpu.getCutoffEvents().iterator();
		while(iCutOffEvents.hasNext()) {
			Event e = iCutOffEvents.next();
			int eIndex = alMatrix.indexOf(e.getName());
//			String eCasualConditionName = "";
//			ICoSet<BPNode, Condition, Event, Flow, Node, org.jbpt.petri.Place, org.jbpt.petri.Transition, Marking> ePost = e.getPostConditions();
//			Iterator<Condition> iEPost = ePost.iterator();
//			while (iEPost.hasNext()) {
//				eCasualConditionName = iEPost.next().getName();
//			}
//			int ePostIndex = alMatrix.indexOf(eCasualConditionName);
			
			Event eCor = cpu.getCorrespondingEvent(e);
			int eCorIndex = alMatrix.indexOf(eCor.getName());
//			String eCorCasualConditionName = "";
//			ICoSet<BPNode, Condition, Event, Flow, Node, org.jbpt.petri.Place, org.jbpt.petri.Transition, Marking> eCorPost = e.getPostConditions();
//			Iterator<Condition> iECorPost = eCorPost.iterator();
//			while (iECorPost.hasNext()) {
//				eCorCasualConditionName = iECorPost.next().getName();
//			}
//			int eCorPostIndex = alMatrix.indexOf(eCorCasualConditionName);
			
			// for e
			Iterator<ModelGraphVertex> iEventReachList = reachVertex.get(e.getName()).iterator();
			while(iEventReachList.hasNext()) {
				ModelGraphVertex v = iEventReachList.next();
				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
				reachMatrix2d.set(eCorIndex, reachableIndex, 1.0);
			}
			// for corresponding event
			Iterator<ModelGraphVertex> iEventCorReachList = reachVertex.get(eCor.getName()).iterator();
			while(iEventCorReachList.hasNext()) {
				ModelGraphVertex v = iEventCorReachList.next();
				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
				reachMatrix2d.set(eIndex, reachableIndex, 1.0);
			}
//			// for event condition
//			Iterator<ModelGraphVertex> iEventCorPostReachList = reachVertex.get(eCorCasualConditionName).iterator();
//			while(iEventCorPostReachList.hasNext()) {
//				ModelGraphVertex v = iEventCorPostReachList.next();
//				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
//				reachMatrix2d.set(ePostIndex, reachableIndex, 1.0);
//			}
//			// for event corresponding condition
//			Iterator<ModelGraphVertex> iEventPostReachList = reachVertex.get(eCasualConditionName).iterator();
//			while(iEventPostReachList.hasNext()) {
//				ModelGraphVertex v = iEventPostReachList.next();
//				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
//				reachMatrix2d.set(eCorPostIndex, reachableIndex, 1.0);
//			}
		}
		
		return reachMatrix2d;
	}
	
	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
	 * @param pn
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrixCPU(CompletePrefixUnfolding cpu, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		PetriNet pn = PetriNetConversion.convert(cpu);
		
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		alMatrix.clear();
		HashSet<ModelGraphVertex> startVertex = new HashSet<ModelGraphVertex>();
		startVertex.add(pn.getSource());
		
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<ModelGraphVertex> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				if(alMatrix.contains(vId)) {
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
		
		Hashtable<String, HashSet<ModelGraphVertex>> reachVertex = new Hashtable<String, HashSet<ModelGraphVertex>>();
		for(int irow = 0; irow < alMatrix.size(); irow++){
			reachVertex.put(alMatrix.get(irow), new HashSet<ModelGraphVertex>());
			for(int jcol = 0; jcol < alMatrix.size(); jcol++) {
				if (reachMatrix2d.get(irow, jcol) != 0.0 && irow != jcol) {
					reachVertex.get(alMatrix.get(irow)).addAll(this.htVertex.get(alMatrix.get(jcol)));
				}
			}
		}
		
		Iterator<Event> iCutOffEvents = cpu.getCutoffEvents().iterator();
		while(iCutOffEvents.hasNext()) {
			Event e = iCutOffEvents.next();
			int eIndex = alMatrix.indexOf(e.getName());
//			String eCasualConditionName = "";
//			ICoSet<BPNode, Condition, Event, Flow, Node, org.jbpt.petri.Place, org.jbpt.petri.Transition, Marking> ePost = e.getPostConditions();
//			Iterator<Condition> iEPost = ePost.iterator();
//			while (iEPost.hasNext()) {
//				eCasualConditionName = iEPost.next().getName();
//			}
//			int ePostIndex = alMatrix.indexOf(eCasualConditionName);
			
			Event eCor = cpu.getCorrespondingEvent(e);
			int eCorIndex = alMatrix.indexOf(eCor.getName());
//			String eCorCasualConditionName = "";
//			ICoSet<BPNode, Condition, Event, Flow, Node, org.jbpt.petri.Place, org.jbpt.petri.Transition, Marking> eCorPost = e.getPostConditions();
//			Iterator<Condition> iECorPost = eCorPost.iterator();
//			while (iECorPost.hasNext()) {
//				eCorCasualConditionName = iECorPost.next().getName();
//			}
//			int eCorPostIndex = alMatrix.indexOf(eCorCasualConditionName);
			
			// for e
			Iterator<ModelGraphVertex> iEventReachList = reachVertex.get(e.getName()).iterator();
			while(iEventReachList.hasNext()) {
				ModelGraphVertex v = iEventReachList.next();
				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
				reachMatrix2d.set(eCorIndex, reachableIndex, 1.0);
			}
			// for corresponding event
			Iterator<ModelGraphVertex> iEventCorReachList = reachVertex.get(eCor.getName()).iterator();
			while(iEventCorReachList.hasNext()) {
				ModelGraphVertex v = iEventCorReachList.next();
				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
				reachMatrix2d.set(eIndex, reachableIndex, 1.0);
			}
//			// for event condition
//			Iterator<ModelGraphVertex> iEventCorPostReachList = reachVertex.get(eCorCasualConditionName).iterator();
//			while(iEventCorPostReachList.hasNext()) {
//				ModelGraphVertex v = iEventCorPostReachList.next();
//				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
//				reachMatrix2d.set(ePostIndex, reachableIndex, 1.0);
//			}
//			// for event corresponding condition
//			Iterator<ModelGraphVertex> iEventPostReachList = reachVertex.get(eCasualConditionName).iterator();
//			while(iEventPostReachList.hasNext()) {
//				ModelGraphVertex v = iEventPostReachList.next();
//				int reachableIndex = alMatrix.indexOf(v.getIdentifier());
//				reachMatrix2d.set(eCorPostIndex, reachableIndex, 1.0);
//			}
		}
		
		return reachMatrix2d;
	}
	
	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
	 * @param pn
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrixOriginalNet(PetriNet pn, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
//		alMatrix.clear();
		HashSet<ModelGraphVertex> startVertex = new HashSet<ModelGraphVertex>();
		startVertex.add(pn.getSource());
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<ModelGraphVertex> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				order = alMatrix.indexOf(vId);
				// 1215
//				if(alMatrix.contains(vId)) {
//					continue;
//				}
//				alMatrix.add(vId);
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
					int vPredOrder = alMatrix.indexOf(VPredId.split("-")[0]);
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
//				++order;
			}
			
			//move forward one level
			iStartVertex = startVertex.iterator();
			HashSet<ModelGraphVertex> descVertex = new HashSet<ModelGraphVertex>();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
 				String vId = v.getIdentifier();
 				// 1215
//				if(alMatrix.contains(vId)) {
//					descVertex.addAll(v.getSuccessors());
//				}
 				descVertex.addAll(v.getSuccessors());
			}
			startVertex.clear();
			startVertex.addAll(descVertex);
		}
		int irow = 1, jcol = 0;
		while(irow < alMatrix.size()) {
			while(jcol < irow) {
				if (reachMatrix2d.get(irow, jcol) != 0.0) {
					for(int jcolrow = jcol + 1; jcolrow < alMatrix.size(); jcolrow++) {
						if (reachMatrix2d.get(jcol, jcolrow) != 0.0) {
							reachMatrix2d.set(irow, jcolrow, 1.0);
						}
					}
				}
				++jcol;
			}
			jcol = 0;
			++irow;
		}
		
		return reachMatrix2d;
	}
	
	/**
	 * Preprocess descendants table of any vertex, thus, generate the reachable matrix of a cpu
	 * @param pn
	 * @param n - number of vertices
	 * @param alMatrix - store the visiting order of vertices
	 * @param htVertex - store the map of label->list(vertex)
	 * @return the reachable matrix
	 */
	@SuppressWarnings("unchecked")
	private DoubleMatrix2D getReachMatrixOriginalNet2(PetriNet pn, int n, ArrayList<String> alMatrix, 
			Hashtable<String, HashSet<ModelGraphVertex>> htVertex) {
		DoubleMatrix2D reachMatrix2d = DoubleFactory2D.sparse.make(n, n, 0);
		HashSet<ModelGraphVertex> cutOffVertex = new HashSet<ModelGraphVertex>();
		alMatrix.clear();
		HashSet<ModelGraphVertex> startVertex = new HashSet<ModelGraphVertex>();
		startVertex.add(pn.getSource());
		int order = 0;
		while(startVertex.size() > 0) {
			Iterator<ModelGraphVertex> iStartVertex = startVertex.iterator();
			while(iStartVertex.hasNext()) {
				ModelGraphVertex v = iStartVertex.next();
				String vId = v.getIdentifier();
				// 1215
				if(alMatrix.contains(vId)) {
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
					int vPredOrder = alMatrix.indexOf(VPredId.split("-")[0]);
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
 				// 1215
				if(alMatrix.contains(vId)) {
					descVertex.addAll(v.getSuccessors());
				}
			}
			startVertex.clear();
			Iterator<ModelGraphVertex> itVDescId = descVertex.iterator();
			while(itVDescId.hasNext()) {
				ModelGraphVertex v = itVDescId.next();
				String vId = v.getIdentifier();
				if(alMatrix.contains(vId)) {
					cutOffVertex.add(v);
					int tempOrder = alMatrix.indexOf(vId);
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
						int vPredOrder = alMatrix.indexOf(VPredId.split("-")[0]);
						if(vPredOrder == -1) {
							continue;
						}
						reachMatrix2d.set(vPredOrder, tempOrder, 1.0);
						for(int i = 0; i <= vPredOrder; ++i) {
							if(reachMatrix2d.get(i, vPredOrder) != 0.0) {
								reachMatrix2d.set(i, tempOrder, 1.0);
							}
						}
					}
				}
				else {
					startVertex.add(v);
				}
			}
//			startVertex.addAll(descVertex);
		}
		int irow = 1, jcol = 0;
		while(irow < alMatrix.size()) {
			while(jcol < irow) {
				if (reachMatrix2d.get(irow, jcol) != 0.0) {
					for(int jcolrow = jcol + 1; jcolrow < alMatrix.size(); jcolrow++) {
						if (reachMatrix2d.get(jcol, jcolrow) != 0.0) {
							reachMatrix2d.set(irow, jcolrow, 1.0);
						}
					}
				}
				++jcol;
			}
			jcol = 0;
			++irow;
		}
		
		Iterator<ModelGraphVertex> itVCutOff = cutOffVertex.iterator();
		while(itVCutOff.hasNext()) {
			ModelGraphVertex v = itVCutOff.next();
			int indexCutOff = alMatrix.indexOf(v.getIdentifier());
			for(int i = 0; i < alMatrix.size(); i++) {
				if (reachMatrix2d.get(i, indexCutOff) != 0.0) {
					for(int j = 0; j < alMatrix.size(); j++) {
						if (reachMatrix2d.get(indexCutOff, j) != 0.0) {
							reachMatrix2d.set(i, j, 1.0);
						}
					}
				}
			}
		}
		
		return reachMatrix2d;
	}
}
