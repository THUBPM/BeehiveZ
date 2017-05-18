package cn.edu.thss.iise.beehivez.server.metric.cged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.fsm.FSMState;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.StateSpace;
import org.semanticweb.kaon2.ti;

public class CoverabilityGraphBuilder {
	
	private int time = 0;
	private int endNodeId = 0;
	
	public CoverabilityGraph convert(StateSpace space, String filename) {
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer, Set<Integer>> outgoingEdges = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> incomingEdges = new HashMap<Integer, Set<Integer>>();
		Map<Integer, String> labels = new HashMap<Integer, String>();
		Map<String, Integer> loopSpans = new HashMap<String, Integer>();
		Map<String, Integer> conflictSpans = new HashMap<String, Integer>();
		Map<Integer, List<String>> listLabels = new HashMap<Integer, List<String>>();
		
		Set<Integer> functionVertices = new HashSet<Integer>();
		Set<Integer> eventVertices = new HashSet<Integer>();
		Set<Integer> connectorVertices = new HashSet<Integer>();
		
		Map<String, Integer> corresponding = new HashMap<String, Integer>();
		// dfs to set id of vertices
		List<ModelGraphVertex> alSpaceVertices = space.getVerticeList();
		Set<String> visited = new HashSet<String>();
		time = 0;
		for(ModelGraphVertex v : alSpaceVertices) {
			FSMState _state = (FSMState) v;
			if(!visited.contains(_state.getLabel())) {
				dfsInitVertexId(_state, visited, corresponding, vertices, 
						outgoingEdges, incomingEdges, labels, listLabels);
			}
		}
		// add ROOT
		labels.put(1, "ROOT");
		listLabels.put(1, new ArrayList<String>());
		listLabels.get(1).add("ROOT");
		// init edges
		for(Object e : space.getEdges()) {
			FSMTransition _transition = (FSMTransition) e;
			FSMState _tail = (FSMState) _transition.getTail();
			FSMState _head = (FSMState) _transition.getHead();
			
			outgoingEdges.get(corresponding.get(_tail.getLabel()))
					.add(corresponding.get(_head.getLabel()));
			incomingEdges.get(corresponding.get(_head.getLabel()))
					.add(corresponding.get(_tail.getLabel()));
			labels.put(corresponding.get(_head.getLabel()), 
					labels.get(corresponding.get(_head.getLabel())) + _transition.getCondition());
			listLabels.get(corresponding.get(_head.getLabel())).add(_transition.getCondition());
		}
		// add END node
		vertices.add(++time);
		outgoingEdges.put(time, new HashSet<Integer>());
		incomingEdges.put(time, new HashSet<Integer>());
		labels.put(time, "END");
		listLabels.put(time, new ArrayList<String>());
		listLabels.get(time).add("END");
		incomingEdges.get(time).add(endNodeId);
		outgoingEdges.get(endNodeId).add(time);
		// init spans
		for(Map.Entry<Integer, Set<Integer>> entry : outgoingEdges.entrySet()) {
			int _source = entry.getKey();
			for(int _target : entry.getValue()) {
				String edge = _source + "->" + _target;
				loopSpans.put(edge, Integer.MIN_VALUE);
				conflictSpans.put(edge, Integer.MIN_VALUE);
			}
		}
		// dfs to compute longest path for span
		Set<Integer> visitedSpan = new HashSet<Integer>();
		for(String edge : loopSpans.keySet()) {
			String[] twoVertices = edge.split("->");
			int _source = Integer.parseInt(twoVertices[0]);
			int _target = Integer.parseInt(twoVertices[1]);
			visited.clear();
			int conflictDis = dfsVisit(_source, _target, visitedSpan, outgoingEdges);
			if(conflictDis > 1) {
				conflictSpans.put(edge, conflictDis);
			}
			visited.clear();
			int loopDis = dfsVisit(_target, _source, visitedSpan, outgoingEdges);
			if(loopDis > 0) {
				loopSpans.put(edge, loopDis);
			}
		}
		CoverabilityGraph cGraph = new CoverabilityGraph(vertices, outgoingEdges, incomingEdges, labels, listLabels,
				functionVertices, eventVertices, connectorVertices, loopSpans, conflictSpans, filename);
		return cGraph;
	}

	private int dfsVisit(int source, int target, Set<Integer> visited,
			Map<Integer, Set<Integer>> outgoingEdges) {
		if(visited.contains(source)) {
			return Integer.MIN_VALUE;
		}
		if(source == target) {
			return 0;
		}
		int max = Integer.MIN_VALUE;
		visited.add(source);
		for(int v : outgoingEdges.get(source)) {
			int dis = dfsVisit(v, target, visited, outgoingEdges);
			max = (dis > max) ? dis : max;
		}
		visited.remove(source);
		return max == Integer.MIN_VALUE ? max : 1 + max;
	}
	
	private void dfsInitVertexId(FSMState u, Set<String> visited,
			Map<String, Integer> corresponding,
			Set<Integer> vertices,
			Map<Integer, Set<Integer>> outgoingEdges,
			Map<Integer, Set<Integer>> incomingEdges,
			Map<Integer, String> labels,
			Map<Integer, List<String>> listLabels) {
		corresponding.put(u.getLabel(), ++time);
		vertices.add(time);
		outgoingEdges.put(time, new HashSet<Integer>());
		incomingEdges.put(time, new HashSet<Integer>());
		labels.put(time, "");
		listLabels.put(time, new ArrayList<String>());
		visited.add(u.getLabel());
		if(u.getSuccessors().isEmpty()) {
			endNodeId = time;
		}
		for(Object v : u.getSuccessors()) {
			FSMState _state = (FSMState) v;
			if(!visited.contains(_state.getLabel())) {
				dfsInitVertexId(_state, visited, corresponding, vertices, 
						outgoingEdges, incomingEdges, labels, listLabels);
			}
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(int endNodeId) {
		this.endNodeId = endNodeId;
	}
	
}
