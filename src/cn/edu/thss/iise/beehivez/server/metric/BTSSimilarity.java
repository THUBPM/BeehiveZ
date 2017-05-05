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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcess;
import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcessSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.ExecuteLog;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.bts.BTSGenerator;

/**
 * similarity based on the basic transition sequences of petri nets,because we
 * use the transition sequences to express the basic process, so we can reuse
 * the BasicProcess class to definition the basic transition. Also we can reuse
 * the BaiscProcessSet class to definition the set of basic transition.
 * 
 * @author He tengfei
 * 
 */
public class BTSSimilarity {
	public static final String EXECUTE_INFO = "temp/btsExecuteInfo.txt";
	public static final String FOLDER = "temp";

	private ExecuteLog log = null;

	public BTSSimilarity() {
		log = new ExecuteLog(EXECUTE_INFO);
	}

	public String getName() {
		return "BTSSimilarity";
	}

	public String getDesription() {
		return "similarity match based on basic transition sequences.";
	}

	public String similarity(PetriNet pn1, PetriNet pn2) {
		File folder = new File(FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
		log.clear();
		log.open();
		log.writeString("the execute info as follows:\n");
		BTSGenerator bp = new BTSGenerator(log);
		log.writeString("the bts info of the first petri net object:");
		LinkedList<BasicProcessSet> list1 = bp.getBTS(MyPetriNet
				.PromPN2MyPN(pn1));
		log.writeLines(2);
		log.writeString("the bts info of the second petri net object:");
		LinkedList<BasicProcessSet> list2 = bp.getBTS(MyPetriNet
				.PromPN2MyPN(pn2));
		log.writeLines(3);
		StringBuffer sb = new StringBuffer(
				"the similar vector of these two petri net is :");
		sb.append("{");

		BasicProcessSet bps1 = null;
		BasicProcessSet bps2 = null;
		// ���ڼ�¼������̶μ��϶�Ϊ�յ����
		int count = 0;
		// ���ڼ�¼�������̶μ���֮���������
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
		return EXECUTE_INFO;
	}

	public void similarity2(String pnml1, String pnml2) {
		PnmlImport pi = new PnmlImport();
		PetriNet pn1 = null;
		PetriNet pn2 = null;
		Result rt = null;
		try {
			pn1 = pi.read(new FileInputStream(new File(pnml1)));
			pn2 = pi.read(new FileInputStream(new File(pnml2)));
			File folder = new File(FOLDER);
			if (!folder.exists()) {
				folder.mkdir();
			}
			/*
			 * log.clear(); log.open(); log.writeString("the execute info as
			 * follows:\n");
			 */
			BTSGenerator bp = new BTSGenerator(log);
			// log.writeString("the bts info of the first petri net object:");
			LinkedList<BasicProcessSet> list1 = bp.getBTS(MyPetriNet
					.PromPN2MyPN(pn1));
			// log.writeLines(2);
			// log.writeString("the bts info of the second petri net object:");
			LinkedList<BasicProcessSet> list2 = bp.getBTS(MyPetriNet
					.PromPN2MyPN(pn2));
			// log.writeLines(3);
			/*
			 * StringBuffer sb = new StringBuffer( "the similar vector of these
			 * two petri net is :"); sb.append("{");
			 */

			BasicProcessSet bps1 = null;
			BasicProcessSet bps2 = null;
			// ���ڼ�¼������̶μ��϶�Ϊ�յ����
			int count = 0;
			// ���ڼ�¼�������̶μ���֮���������
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
				/*
				 * if (i != list1.size() - 1) { sb.append(simiValues[i] + ", "); }
				 * else { sb.append(simiValues[i] + ""); }
				 */
			}
			/*
			 * sb.append("}"); log.writeString(sb.toString());
			 */
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
			list1.clear();
			list2.clear();
			/*
			 * log .writeString("the similarity coefficient of these two petri
			 * nets is: " + average); log.close();
			 */
			System.out.println(pnml1 + "	" + pnml2 + " 	" + average);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class Result implements Comparable {
		private String file1;
		private String file2;
		private double similarity;

		public Result(String pnml1, String pnml2, double average) {
			// TODO Auto-generated constructor stub
			this.file1 = pnml1;
			this.file2 = pnml2;
			this.similarity = average;
		}

		public String getFile1() {
			return file1;
		}

		public void setFile1(String file1) {
			this.file1 = file1;
		}

		public String getFile2() {
			return file2;
		}

		public void setFile2(String file2) {
			this.file2 = file2;
		}

		public double getSimilarity() {
			return similarity;
		}

		public void setSimilarity(double similarity) {
			this.similarity = similarity;
		}

		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			Result rt = (Result) o;
			if ((similarity - rt.getSimilarity()) >= 0) {
				return 1;
			} else
				return -1;
		}
	}

	/**
	 * get the top ten similar models in path
	 * 
	 * @param path
	 */
	public void getTopTen() {
		String path = "E:\\���ⱨ��\\��������\\pnml";
		File f = new File(path);
		String absolutePath = f.getAbsolutePath();
		// PriorityQueue pq = new PriorityQueue();
		if (f.isDirectory()) {
			String files[] = f.list();
			for (int i = 0; i < files.length; i++) {
				for (int j = i + 1; j < files.length; j++) {
					similarity2(absolutePath + "\\" + files[i], absolutePath
							+ "\\" + files[j]);
					// pq.add(rt);
				}
			}
		}
		/*
		 * for(int i=0;i<pq.size()&&i<10;i++){ Result rt = (Result)
		 * pq.remove(); System.out.println(rt.getFile1()+" "+rt.getFile2()+"
		 * "+rt.getSimilarity()); }
		 */
	}

	public void getAllBTSsAndSimilarity() {
		String path = "E:\\���ⱨ��\\��������\\pnml";
		File f = new File(path);
		String absolutePath = f.getAbsolutePath();
		PnmlImport pi = null;
		Vector vector = new Vector();
		if (f.isDirectory()) {
			String files[] = f.list();
			pi = new PnmlImport();
			for (int i = 0; i < files.length; i++) {
				try {
					PetriNet pn = pi.read(new FileInputStream(new File(
							absolutePath + "\\" + files[i])));
					BTSGenerator bg = new BTSGenerator(log);
					LinkedList ls = bg.getBTS(MyPetriNet.PromPN2MyPN(pn));
					// outputBTSs(files[i],ls);
					vector.add(new BTSs(files[i], ls));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		outputSimilarityResult(vector);
	}
	
	public void outputSimilarityResult(Vector vector){
		File f = new File("similarityResult.txt");
		BufferedWriter bw = null;
		StringBuffer sb = new StringBuffer("");
		try {
			bw = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < vector.size(); i++) {
			for (int j = i + 1; j < vector.size(); j++) {
				BTSs bts1 = (BTSs) vector.get(i);
				BTSs bts2 = (BTSs) vector.get(j);
				double similarity = getSimilarity(bts1, bts2);
				sb.append(bts1.getFile() + "		" + bts2.getFile()
						+ "		" + similarity);
				sb.append("\n");
			}
		}
		try {
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void outputBTSs(String fileName,LinkedList ls){
		File file = new File(fileName);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<ls.size();i++){
			BasicProcessSet bps = (BasicProcessSet) ls.get(i);
			sb.append(bps.getName());
			sb.append("\n");
			HashSet<BasicProcess> bpSet = bps.getPSet();
			if (bpSet != null) {
				Iterator it = bpSet.iterator();
				while (it.hasNext()) {
					BasicProcess bp = (BasicProcess) it.next();
					Vector<MyPetriTransition> tranVector = bp
							.getProcess();
					for (int k = 0; k < tranVector.size(); k++) {
						sb.append(tranVector.get(k).getName()+ " ");
					}
					sb.append("\n");
				}
			}
		}
		try {
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getSimilarity(BTSs bts1, BTSs bts2) {
		BasicProcessSet bps1 = null;
		BasicProcessSet bps2 = null;
		LinkedList<BasicProcessSet> list1 = bts1.getBts();
		LinkedList<BasicProcessSet> list2 = bts2.getBts();
		// ���ڼ�¼������̶μ��϶�Ϊ�յ����
		int count = 0;
		// ���ڼ�¼�������̶μ���֮���������
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
			/*
			 * if (i != list1.size() - 1) { sb.append(simiValues[i] + ", "); }
			 * else { sb.append(simiValues[i] + ""); }
			 */
		}
		/*
		 * sb.append("}"); log.writeString(sb.toString());
		 */
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
		return average;
	}

	public class BTSs {
		private String file;
		private LinkedList bts;

		public BTSs(String file, LinkedList bts) {
			super();
			this.file = file;
			this.bts = bts;
		}

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		public LinkedList getBts() {
			return bts;
		}

		public void setBts(LinkedList bts) {
			this.bts = bts;
		}

	}

	public static void main(String[] args) {
		BTSSimilarity test = new BTSSimilarity();
		test.getAllBTSsAndSimilarity();
	}
}
