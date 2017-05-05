/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.metric;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.analysis.graphmatching.graph.SimpleGraph;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.epcpack.EPCConnector;
import org.processmining.framework.models.epcpack.EPCEvent;
import org.processmining.framework.models.epcpack.EPCFunction;
import org.processmining.framework.models.fsm.FSMState;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.StateSpace;
import org.semanticweb.kaon2.vi;

/**
 * @author WangZixuan
 * 
 * The structure of the CoverabilityGraph can not be directly used cause the CGED algorithm is focus on transition not the places,
 * MyGraph : direct graph 
 * vertex is the transition or the set of transitions.(eg:T1+T2+T3)
 *
 */
public class MyGraph {
	
	public Set<Integer> vertices;
	public Map<Integer, Set<Integer>> outgoingEdges;
	public Map<Integer, Set<Integer>> incomingEdges;
	public Map<Integer, String> labels;

	public Set<Integer> functionVertices;
	public Set<Integer> eventVertices;
	public Set<Integer> connectorVertices;


	public MyGraph() {
		
		// TODO Auto-generated constructor stub
	}
	
	public SimpleGraph convertCoverabilityGraphToSimpleGraph(StateSpace cgraph) {
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer, Set<Integer>> outgoingEdges = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> incomingEdges = new HashMap<Integer, Set<Integer>>();
		Map<Integer, String> labels = new HashMap<Integer, String>();
		
		Set<Integer> functionVertices = new HashSet<Integer>();
		Set<Integer> eventVertices = new HashSet<Integer>();
		Set<Integer> connectorVertices = new HashSet<Integer>();
		
		Map<String, Integer> corresponding = new HashMap<String, Integer>();
		Map<Integer, String> revCorresponding = new HashMap<Integer, String>();
		int vId = 0;
		for(ModelGraphVertex v : cgraph.getVerticeList()) {
			FSMState _state = (FSMState) v;
			corresponding.put(_state.getLabel(), ++vId);
			revCorresponding.put(vId, _state.getLabel());
			vertices.add(vId);
			outgoingEdges.put(vId, new HashSet<Integer>());
			incomingEdges.put(vId, new HashSet<Integer>());
			labels.put(vId, "");
		}
		
		for(Object e : cgraph.getEdges()) {
			FSMTransition _transition = (FSMTransition) e;
			FSMState _tail = (FSMState) _transition.getTail();
			FSMState _head = (FSMState) _transition.getHead();
			
			outgoingEdges.get(corresponding.get(_tail.getLabel()))
					.add(corresponding.get(_head.getLabel()));
			incomingEdges.get(corresponding.get(_head.getLabel()))
					.add(corresponding.get(_tail.getLabel()));
			labels.put(corresponding.get(_head.getLabel()), 
					labels.get(corresponding.get(_head.getLabel())) + _transition.getCondition());
		}
		
		// init start state
		labels.put(corresponding.get(cgraph.getStartState().getLabel()), "ROOT");
		// add new end state
		int oriEndState = 0;
		for(Map.Entry<Integer, Set<Integer>> entry : outgoingEdges.entrySet()) {
			if(entry.getValue().isEmpty()) {
				oriEndState = entry.getKey();
				break;
			}
		}
		vertices.add(++vId);
		outgoingEdges.put(vId, new HashSet<Integer>());
		incomingEdges.put(vId, new HashSet<Integer>());
		outgoingEdges.get(oriEndState).add(vId);
		incomingEdges.get(vId).add(oriEndState);
		labels.put(vId, "END");
		
		SimpleGraph sgraph = new SimpleGraph(vertices, outgoingEdges, incomingEdges, labels, 
				functionVertices, eventVertices, connectorVertices);
		return sgraph;
	}
	/**
	 * 
	 */
	public SimpleGraph coveabilityGraphConverttoMyGraph(StateSpace cgraph ) {
		
		Map<ModelGraphVertex, Integer> nodeId2vertex = new HashMap<ModelGraphVertex, Integer>();
		Map<Integer, ModelGraphVertex> vertex2nodeId = new HashMap<Integer, ModelGraphVertex>();
		
		vertices = new HashSet<Integer>();
		outgoingEdges = new HashMap<Integer, Set<Integer>>();
		incomingEdges = new HashMap<Integer, Set<Integer>>();
		labels = new HashMap<Integer, String>();

//		functionVertices = new HashSet<Integer>();
//		eventVertices = new HashSet<Integer>();
//		connectorVertices = new HashSet<Integer>();
		
		functionVertices = null;
		eventVertices = null;
		connectorVertices = null;
//		1.initial the vertex and Mapping between node and vertex
		int vertexId = 0;
		for (ModelGraphVertex n : cgraph.getVerticeList()) {
			vertices.add(vertexId);
			nodeId2vertex.put(n, vertexId);
			vertex2nodeId.put(vertexId, n);
			vertexId++;
		}
//		2. initial the edges
		for (Integer v = 0; v < vertexId; v++) {
			ModelGraphVertex n = vertex2nodeId.get(v);

			Set<Integer> incomingCurrent = new HashSet<Integer>();
			for (Object s : n.getPredecessors()) {
				incomingCurrent.add(nodeId2vertex.get((ModelGraphVertex) s));
				
//				3.labels
				int i = cgraph.getEdges().indexOf((ModelGraphVertex) s); 
				FSMTransition t1 = (FSMTransition) cgraph.getEdges().get(i);
				t1.getCondition();
				
			}
			incomingEdges.put(v, incomingCurrent);

			labels.put(vertexId, incomingEdges.toString());

			Set<Integer> outgoingCurrent = new HashSet<Integer>();
			for (Object s : n.getSuccessors()) {
				outgoingCurrent.add(nodeId2vertex.get((ModelGraphVertex) s));
			}
			outgoingEdges.put(v, outgoingCurrent);
		}
		
		
		SimpleGraph mygraph = new SimpleGraph(vertices, outgoingEdges,
				incomingEdges, labels, functionVertices, eventVertices,
				connectorVertices);
		return mygraph;
		
	}

}
