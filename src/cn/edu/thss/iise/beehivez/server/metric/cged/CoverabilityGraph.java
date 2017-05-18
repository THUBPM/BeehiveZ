package cn.edu.thss.iise.beehivez.server.metric.cged;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.analysis.graphmatching.graph.SimpleGraph;

public class CoverabilityGraph extends SimpleGraph {

	private String filename;
	private Map<String, Integer> loopSpans;
	private Map<String, Integer> conflictSpans;
	private Map<Integer, List<String>> listLabels;

	public CoverabilityGraph(Set<Integer> vertices,
			Map<Integer, Set<Integer>> outgoingEdges,
			Map<Integer, Set<Integer>> incomingEdges,
			Map<Integer, String> labels, Map<Integer, List<String>> listLabels,
			Set<Integer> functionVertices, Set<Integer> eventVertices,
			Set<Integer> connectorVertices, Map<String, Integer> loopSpans,
			Map<String, Integer> conflictSpans, String filename) {
		super(vertices, outgoingEdges, incomingEdges, labels, functionVertices,
				eventVertices, connectorVertices);
		this.loopSpans = loopSpans;
		this.conflictSpans = conflictSpans;
		this.filename = filename;
		this.listLabels = listLabels;
	}

	public Map<String, Integer> getLoopSpans() {
		return loopSpans;
	}

	public Map<String, Integer> getConflictSpans() {
		return conflictSpans;
	}

	public String getFilename() {
		return filename;
	}

	public List<String> getListLabel(int vertex) {
		return listLabels.get(vertex);
	}

}
