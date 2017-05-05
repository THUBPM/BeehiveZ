/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.metric;

import org.processmining.analysis.graphmatching.algos.GraphEditDistanceGreedy;
import org.processmining.analysis.graphmatching.graph.SimpleGraph;
import org.processmining.framework.models.petrinet.StateSpace;

/**
 * @author 王子璇
 *
 */
public class CoverabilityGraphSimilarity {

	/**
	 * 
	 */
	public CoverabilityGraphSimilarity() {
		// TODO Auto-generated constructor stub
	}

	public double getSimilarity(SimpleGraph sg1, SimpleGraph sg2) {
        GraphEditDistanceGreedy measure= new GraphEditDistanceGreedy();
        double distance = measure.compute(sg1, sg2);
        double similarity = (sg1.getVertices().size() + sg2.getVertices().size() - 2 - distance)
        						/ (sg1.getVertices().size() + sg2.getVertices().size() - 2);
		// TODO Auto-generated method stub
		return similarity;
	}

}
