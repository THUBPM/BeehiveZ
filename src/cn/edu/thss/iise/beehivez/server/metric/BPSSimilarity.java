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

import java.io.File;
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.basicprocess.BPSGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcessSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.ExecuteLog;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

/**
 * similarity metric based on basic process segments
 * 
 * @author He tengfei
 * 
 */
public class BPSSimilarity {
	public static final String EXECUTE_INFO = "temp/bpsExecuteInfo.txt";
	public static final String FOLDER = "temp";
	private ExecuteLog log = null;

	public BPSSimilarity() {
		log = new ExecuteLog(EXECUTE_INFO);
	}

	public String getName() {
		return "BPSSimilarity";
	}

	public String getDesription() {
		return "similarity match based on basic process segments.";
	}

	public double/* double */similarity(PetriNet pn1, PetriNet pn2) {
		File folder = new File(FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
		log.clear();
		log.open();
		log.writeString("the execute info as follows:\n");
		BPSGenerator bp = new BPSGenerator(log);
		log.writeString("the bps info of the first petri net object:");
		LinkedList<BasicProcessSet> list1 = bp.getProcesses(MyPetriNet
				.PromPN2MyPN(pn1));
		log.writeLines(2);
		log.writeString("the bps info of the second petri net object:");
		LinkedList<BasicProcessSet> list2 = bp.getProcesses(MyPetriNet
				.PromPN2MyPN(pn2));
		log.writeLines(3);
		StringBuffer sb = new StringBuffer(
				"the similar vector of these two petri net is :");
		sb.append("{");

		BasicProcessSet bps1 = null;
		BasicProcessSet bps2 = null;
		// ���ڼ�¼}���̶μ��϶�Ϊ�յ����
		int count = 0;
		// ���ڼ�¼������̶μ���֮���������
		double simiValues[] = new double[list1.size()];
		boolean flag[] = new boolean[list1.size()];
		for (int i = 0; i < list1.size(); i++) {
			bps1 = list1.get(i);
			bps2 = list2.get(i);

			if (bps1.getPSet() == null && bps2.getPSet() == null) {
				count++;
				simiValues[i] = 0.05;
				flag[i] = true;
			} else {
				simiValues[i] = bps1.getSimilarityBS(list2.get(i));
			}
			if (i != list1.size() - 1) {
				sb.append(simiValues[i] + ", ");
			} else {
				sb.append(simiValues[i] + "");
			}
		}
		sb.append("}");
		log.writeString(sb.toString());
		// ���ڼ�¼ϵ��
		double sigma = 0.0;
		sigma = (1 - count * 0.05) / (list1.size() - count);
		double average = 0;
		for (int i = 0; i < list1.size(); i++) {
			if (flag[i] == true) {
				average += simiValues[i];
			} else {
				average += sigma * simiValues[i];
			}
		}
		log
				.writeString("the similarity coefficient of these two petri nets is: "
						+ average);
		log.close();
		//return EXECUTE_INFO;
		 return average; 
	}
}
