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
package cn.edu.thss.iise.beehivez.server.metric;

import java.util.Iterator;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Transition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import cn.edu.thss.iise.beehivez.server.util.IntDataAnalyzer;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * obtain the metrics of given yawl model
 * 
 * @author Tao Jin
 * 
 */
public class YAWLMetrics {

	private YNet net = null;

	public YAWLMetrics(YNet net) {
		this.net = net;
	}

	public int getNumberOfConditions() {
		return YAWLUtil.getNetConditions(net).size();
	}

	public int getNumberOfTasks() {
		return net.getNetTasks().size();
	}

	public int getNumberOfArcs() {
		return YAWLUtil.getNetFlows(net).size();
	}

	public int getNumberOfANDJoin() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._AND) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfANDSplit() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._AND) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfXORJoin() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._XOR) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfXORSplit() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._XOR) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfORSplit() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._OR) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfORJoin() {
		int result = 0;
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._OR) {
				result++;
			}
		}
		return result;
	}

	public float getDensity() {
		int nP = getNumberOfConditions();
		int nT = getNumberOfTasks();
		int nA = getNumberOfArcs();
		float numerator = nA;
		float denominator = nP * nT + nT * nP;
		float result = numerator / denominator;
		return result;
	}

	public int getMaxInDegree() {
		int result = 0;
		for (YExternalNetElement element : net.getNetElements().values()) {
			int temp = element.getPresetFlows().size();
			if (temp > result) {
				result = temp;
			}
		}
		return result;
	}

	public int getMaxOutDegree() {
		int result = 0;
		for (YExternalNetElement element : net.getNetElements().values()) {
			int temp = element.getPostsetFlows().size();
			if (temp > result) {
				result = temp;
			}
		}
		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeANDSplitDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._AND) {
				int temp = task.getPostsetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeANDJoinDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._AND) {
				int temp = task.getPresetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeXORSplitDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._XOR) {
				int temp = task.getPostsetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeXORJoinDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._XOR) {
				int temp = task.getPresetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeORJoinDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getJoinType() == YTask._OR) {
				int temp = task.getPresetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeORSplitDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (YTask task : net.getNetTasks()) {
			if (task.getSplitType() == YTask._OR) {
				int temp = task.getPostsetFlows().size();
				vDegree.add(temp);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return number of state, number of arc
	 */
	public int[] analyzeStateSpace() {
		// number of state, number of arc
		int[] result = new int[2];
		PetriNet pn = YAWLUtil.convert2PetriNet(net);
		StateSpace ss = PetriNetUtil.buildCoverabilityGraph(pn);
		result[0] = ss.getVerticeList().size();
		result[1] = ss.getNumberOfEdges();
		ss.destroyStateSpace();
		return result;
	}

	public int getNumberOfTARs() {
		PetriNet pn = YAWLUtil.convert2PetriNet(net);
		return PetriNetUtil.getTARSFromPetriNetByCFP(pn).size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
