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
/**
 *@Author Wang Wenxing 
 *
 */
package cn.edu.thss.iise.beehivez.server.metric.tar.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.util.StringSimilarity;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriArc;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriNet;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriPlace;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyPetriTransition;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelation;
//import cn.edu.thss.iise.beehivez.server.metric.mypetrinet.MyTransitionAdjacentRelationSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.*;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2010-12-6
 */
public class main {

	/**
	 * 2010-12-6
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		MyPetriNet petrii, petrij;
//		Vector<MyPetriTransition> transitionSeti;
//		Vector<MyPetriTransition> transitionSetj;
//		Vector<MyPetriTransition> transitionSetmap;

//	        FileLogger.deleteLogFile("Similarity-SAP-TAR.csv");
			// ѡ��ģ��·��
			String strModelPath = "a";

			// ��ȡ·��������ģ���ļ�
			File fModels = new File(strModelPath);
			String[] filenames = fModels.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".pnml")) {
						return true;
					}
					return false;
				}
			});

			try {
				// ����ÿ��ģ���ļ���TAR��
				for (int i = 0; i < filenames.length; i++) {
					// ��ȡģ��i��Petri�����
					MyPetriNet input = null;
					FileInputStream fin = null;
					try {
						fin = new FileInputStream(strModelPath + "\\" + filenames[i]);
//						fin = new FileInputStream("C:\\Users\\winever\\Documents\\QueryModel\\Non-free Choice\\Nonfree8.pnml");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PnmlImport pImport = new PnmlImport();
					PetriNetResult pnr = null;
					try {
						pnr = (PetriNetResult) pImport.importFile(fin);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						fin.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PetriNet pn = pnr.getPetriNet();
					//pn.setIdentifier("1258790072312.pnml");
					input=MyPetriNet.PromPN2MyPN(pn);
					ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
					
					long start = System.currentTimeMillis();
//					System.out.println(start);
					ONCompleteFinitePrefix cfp = cfpBuilder.Build();
					
					long middle = System.currentTimeMillis();
					ONTransitionAdjacentRelation tar = new ONTransitionAdjacentRelation(cfp);
					
					if(tar.getTarIm().size() != 0){
						System.out.println(filenames[i]);
					}
					
//					System.out.println(middle);
//					petrii = exportModeltoPetrinet(strModelPath + "\\"
//							+ filenames[i]);
//					
//					ONCompleteFinitePrefixBuilder cfpbuilder = new ONCompleteFinitePrefixBuilder(petrii);
//					
//					transitionSeti = petrii.getTransitionSet();

//					for (int j = i; j < filenames.length; j++)
//	                {
//	                    System.out.println(filenames[i] + "-->" + filenames[j]);
//						// //��ȡģ��j��Petri�����
//						try {
//							petrij = exportModeltoPetrinet(strModelPath + "\\"
//									+ filenames[j]);
//
//							transitionSetj = petrij.getTransitionSet();
//
//							// ���б��ƥ��
//							mapTransitionSet();
//
//							long start = System.nanoTime(); // ����
//							// ����TAR��
//							MyTransitionAdjacentRelationSet tarSeti = new MyTransitionAdjacentRelationSet(
//									petrii);
//
//							MyTransitionAdjacentRelationSet tarSetj = new MyTransitionAdjacentRelationSet(
//									petrij);
//
//							// �����Զ���ֵ
//							double sim = this.getSimilarityValue(tarSeti, tarSetj);
//							// �����Լ���ʱ��
//							long time = System.nanoTime() - start; // ����
//
//							// �������Ϣ��¼����,������ţ�ģ����ƣ��ڵ����TAR���ʱ��
//							String result = filenames[i] + ",";
//							result = result + filenames[j] + ",";
//							result = result + sim + ",";
//							result = result + time;
//
//	                        FileLogger.writeLog("Similarity-SAP-TAR.csv", result);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
				}
				JOptionPane.showMessageDialog(null, "�ɹ�����");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("End");
	}
}
	
	class Work2 {
		// ���¼�����������Ϊ���ƥ�����
		MyPetriNet petrii, petrij;
//		Vector<MyPetriTransition> transitionSeti;
//		Vector<MyPetriTransition> transitionSetj;
//		Vector<MyPetriTransition> transitionSetmap;

		public void run() {

//	        FileLogger.deleteLogFile("Similarity-SAP-TAR.csv");
			// ѡ��ģ��·��
			String strModelPath = "a";

			// ��ȡ·��������ģ���ļ�
			File fModels = new File(strModelPath);
			String[] filenames = fModels.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".pnml")) {
						return true;
					}
					return false;
				}
			});

			try {
				// ����ÿ��ģ���ļ���TAR��
				for (int i = 0; i < filenames.length; i++) {
					// ��ȡģ��i��Petri�����
					MyPetriNet input = null;
					FileInputStream fin = null;
					try {
						fin = new FileInputStream(strModelPath + "\\" + filenames[i]);
//						fin = new FileInputStream("C:\\Users\\winever\\Documents\\QueryModel\\Non-free Choice\\Nonfree8.pnml");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PnmlImport pImport = new PnmlImport();
					PetriNetResult pnr = null;
					try {
						pnr = (PetriNetResult) pImport.importFile(fin);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						fin.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PetriNet pn = pnr.getPetriNet();
					//pn.setIdentifier("1258790072312.pnml");
					input=MyPetriNet.PromPN2MyPN(pn);
					ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
					
					long start = System.currentTimeMillis();
					System.out.println(start);
					ONCompleteFinitePrefix cfp = cfpBuilder.Build();
					
					long middle = System.currentTimeMillis();
					ONTransitionAdjacentRelation tar = new ONTransitionAdjacentRelation(cfp);
					
					if(tar.getTar0().size() != 0){
						System.out.println(filenames[i]);
					}
					System.out.println("End");
//					System.out.println(middle);
//					petrii = exportModeltoPetrinet(strModelPath + "\\"
//							+ filenames[i]);
//					
//					ONCompleteFinitePrefixBuilder cfpbuilder = new ONCompleteFinitePrefixBuilder(petrii);
//					
//					transitionSeti = petrii.getTransitionSet();

//					for (int j = i; j < filenames.length; j++)
//	                {
//	                    System.out.println(filenames[i] + "-->" + filenames[j]);
//						// //��ȡģ��j��Petri�����
//						try {
//							petrij = exportModeltoPetrinet(strModelPath + "\\"
//									+ filenames[j]);
//
//							transitionSetj = petrij.getTransitionSet();
//
//							// ���б��ƥ��
//							mapTransitionSet();
//
//							long start = System.nanoTime(); // ����
//							// ����TAR��
//							MyTransitionAdjacentRelationSet tarSeti = new MyTransitionAdjacentRelationSet(
//									petrii);
//
//							MyTransitionAdjacentRelationSet tarSetj = new MyTransitionAdjacentRelationSet(
//									petrij);
//
//							// �����Զ���ֵ
//							double sim = this.getSimilarityValue(tarSeti, tarSetj);
//							// �����Լ���ʱ��
//							long time = System.nanoTime() - start; // ����
//
//							// �������Ϣ��¼����,������ţ�ģ����ƣ��ڵ����TAR���ʱ��
//							String result = filenames[i] + ",";
//							result = result + filenames[j] + ",";
//							result = result + sim + ",";
//							result = result + time;
//
//	                        FileLogger.writeLog("Similarity-SAP-TAR.csv", result);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
				}
				JOptionPane.showMessageDialog(null, "�ɹ�����");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ���������ϵı�ǽ���ӳ�䣬����ı�ƥ�䡣
//		void mapTransitionSet() {
//			transitionSetmap = new Vector<MyPetriTransition>();
//			int i, j;
//			for (i = 0; i < transitionSeti.size(); i++) {
//				int mindistance = 10000;
//				int distance;
//				int map = 0;
//				for (j = 0; j < transitionSetj.size(); j++) {
//					distance = StringSimilarity.similarity(transitionSeti.get(i)
//							.toString(), transitionSetj.get(j).toString());
//					if (distance < mindistance) {
//						mindistance = distance;
//						map = j;
//					}
//				}
//
//				transitionSetmap.add(i, transitionSetj.get(map));
//			}
//		}

//		public double getSimilarityValue(MyTransitionAdjacentRelationSet tarSet1,
//				MyTransitionAdjacentRelationSet tarSet2) {
//			if (tarSet1 == null || tarSet2 == null) {
//				return -1;
//			}

			// ����TAR���϶���
			// �ҽ���
//			MyTransitionAdjacentRelationSet commonSet = new MyTransitionAdjacentRelationSet();
//			MyTransitionAdjacentRelation r1, r2;
//			int indexa=0, indexb=0;
//			for (int i = 0; i < tarSet1.tarSet.size(); i++) {
//				r1 = tarSet1.tarSet.get(i);
//				for (int j = 0; j < tarSet2.tarSet.size(); j++) {
//					r2 = tarSet2.tarSet.get(j);
//					// ���ǻ�����С����ƥ�䷽��
//					// �ҳ�tar1��������Ǩ�������е�λ��
//					for (int index = 0; index < transitionSeti.size(); index++) {
//						if (transitionSeti.get(index).toString().equals(
//								r1.transitionA)) {
//							indexa = index;
//						}
//						if (transitionSeti.get(index).toString().equals(
//								r1.transitionB)) {
//							indexb = index;
//						}
//					}
//
//					if (transitionSetmap.get(indexa).toString().equals(r2.transitionA)
//							&& transitionSetmap.get(indexb).toString().equals(r2.transitionB)) {
//						commonSet.addTAR(r1);
//						break;
//					}
//
//					// //����ֱ�ӻ����ı�ƥ��ķ���
//					// if (r1.transitionA.equals(r2.transitionA)
//					// && r1.transitionB.equals(r2.transitionB)) {
//					// commonSet.addTAR(r1);
//					// break;
//					// }
//				}
//			}
//
//			double f = commonSet.tarSet.size() * commonSet.tarSet.size();
//			f = f / (tarSet1.tarSet.size() * tarSet2.tarSet.size());
//
//			return f;
//		}

		// ���ָ����ģ��·����ȡPetri��ģ�Ͷ���
		

}

