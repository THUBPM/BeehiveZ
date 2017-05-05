package cn.edu.thss.iise.beehivez.server.metric.rorm.jbpt.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.processmining.framework.log.LogEvent;

public class PetriNetConversion {

	@SuppressWarnings("unchecked")
	public static org.jbpt.petri.NetSystem convert(
			org.processmining.framework.models.petrinet.PetriNet _pn) {
		org.jbpt.petri.NetSystem ns = new org.jbpt.petri.NetSystem();
		Map<org.processmining.framework.models.ModelGraphVertex, org.jbpt.petri.Node> nodeMap = new HashMap<org.processmining.framework.models.ModelGraphVertex, org.jbpt.petri.Node>();
		for (org.processmining.framework.models.petrinet.Transition _t : _pn
				.getTransitions()) {
			org.jbpt.petri.Transition t = new org.jbpt.petri.Transition(
					_t.getIdentifier());
			ns.addTransition(t);
			nodeMap.put(_t, t);
		}
		for (org.processmining.framework.models.petrinet.Place _p : _pn
				.getPlaces()) {
			org.jbpt.petri.Place p = new org.jbpt.petri.Place(
					_p.getIdentifier());
			ns.addPlace(p);
			if (_p.getInEdges() == null || _p.getInEdges().isEmpty()) {
				ns.getMarking().put(p, 1);
			}
			nodeMap.put(_p, p);
		}
		ArrayList<org.processmining.framework.models.ModelGraphEdge> _edges = _pn
				.getEdges();
		for (org.processmining.framework.models.ModelGraphEdge _e : _edges) {
			org.processmining.framework.models.ModelGraphVertex _tail = _e
					.getSource();
			org.processmining.framework.models.ModelGraphVertex _head = _e
					.getDest();
			ns.addFlow(nodeMap.get(_tail), nodeMap.get(_head));
		}
		return ns;
	}

	public static org.processmining.framework.models.petrinet.PetriNet convert(
			org.jbpt.petri.unfolding.CompletePrefixUnfolding _cpu) {
		org.processmining.framework.models.petrinet.PetriNet pn = new org.processmining.framework.models.petrinet.PetriNet();
		Map<org.jbpt.hypergraph.abs.Vertex, org.processmining.framework.models.ModelGraphVertex> nodeMap = new HashMap<org.jbpt.hypergraph.abs.Vertex, org.processmining.framework.models.ModelGraphVertex>();
		for (org.jbpt.petri.unfolding.Event _e : _cpu.getEvents()) {
			org.processmining.framework.models.petrinet.Transition t = new org.processmining.framework.models.petrinet.Transition(
					_e.getLabel(), pn);
			t.setLogEvent(new LogEvent(t.getIdentifier(), "auto"));
			pn.addTransition(t);
			nodeMap.put(_e, t);
		}
		for (org.jbpt.petri.unfolding.Condition _c : _cpu.getConditions()) {
			org.processmining.framework.models.petrinet.Place p = new org.processmining.framework.models.petrinet.Place(
					_c.getLabel(), pn);
			pn.addPlace(p);
			nodeMap.put(_c, p);
		}
		for (org.jbpt.petri.unfolding.Event _e : _cpu.getEvents()) {
			for (org.jbpt.petri.unfolding.Condition _preC : _e
					.getPreConditions()) {
				org.processmining.framework.models.petrinet.PNEdge e = new org.processmining.framework.models.petrinet.PNEdge(
						(org.processmining.framework.models.petrinet.Place) nodeMap
								.get(_preC),
						(org.processmining.framework.models.petrinet.Transition) nodeMap
								.get(_e));
				pn.addEdge(e);
			}
		}
		for (org.jbpt.petri.unfolding.Condition _c : _cpu.getConditions()) {
			org.jbpt.petri.unfolding.Event _preE = _c.getPreEvent();
			if (_preE != null) {
				org.processmining.framework.models.petrinet.PNEdge e = new org.processmining.framework.models.petrinet.PNEdge(
						(org.processmining.framework.models.petrinet.Transition) nodeMap
								.get(_preE),
						(org.processmining.framework.models.petrinet.Place) nodeMap
								.get(_c));
				pn.addEdge(e);
			}
		}
		return pn;
	}

}
