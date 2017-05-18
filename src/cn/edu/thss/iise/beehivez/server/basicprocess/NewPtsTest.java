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

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

public class NewPtsTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
//		String modelFile = new String("D:\\实验室\\开题\\efficiency\\loop\\");
//		String modelFile = new String("D:\\实验室\\开题\\efficiency\\synthetic\\scalability\\size\\parallel\\");
//		D:\实验室\开题\efficiency\synthetic\scalability\size\sequential
//		String modelFile = new String("D:\\实验室\\开题\\efficiency\\synthetic\\invisible\\");
		String modelFile = new String("D:\\实验室\\开题\\efficiency\\synthetic\\scalability\\size\\complex\\");
		
//		NewPtsTest npt = new NewPtsTest();
		
		PnmlImport pnmlimport = new PnmlImport();
		
		
		
		File folder = new File(modelFile);
        File[] files = folder.listFiles();
        for(File file : files) {
        	File output = new File(modelFile + file.getName() + ".seq");
    		FileWriter fw = new FileWriter(output, true);
    		BufferedWriter bw = new BufferedWriter(fw);
    		
    		
        	CTree ctree = null;
    		PetriNet petrinet = null;
    		FileInputStream pnml = null;
    		pnml = new FileInputStream(file);
    		petrinet = pnmlimport.read(pnml);

    		CTreeGenerator generator = new CTreeGenerator(
    				MyPetriNet.PromPN2MyPN(petrinet));
    		ctree = generator.generateCTree();
    		TTreeGenerator ttg = new TTreeGenerator();

    		NewPtsSet nps2 = ttg.generatTTree(ctree, 5, "simple_loop_prom");
    		
    		Iterator<NewPtsSequence> traceList = nps2.getNPSet().iterator();
    		
//    		while (traceList.hasNext()) {
//    			String logSeq = "";
//    			NewPtsSequence seq = traceList.next();
//    			ArrayList<TTreeNode> list = seq.getNPSequence();
//    			Iterator<TTreeNode> iTTreeNode = list.iterator();
//    			
//    			while (iTTreeNode.hasNext()) {
//    				TTreeNode node = iTTreeNode.next();
//    				System.out.print(node.getTransition().getName() + ", ");
//    				logSeq += node.getTransition().getName() + ", ";
//    			}
//    			System.out.println("");
//    			bw.write(logSeq + "\n");
//    		}
    		String logSeq = "";
    		while (traceList.hasNext()) {
    			String tmpLogSeq = "";
    			int logLength = 0;
    			
    			NewPtsSequence seq = traceList.next();
    			ArrayList<TTreeNode> list = seq.getNPSequence();
    			Collections.reverse(list);
    			Iterator<TTreeNode> iTTreeNode = list.iterator();
    			
    			while(iTTreeNode.hasNext()) {
    				TTreeNode node = iTTreeNode.next();
    				if (node.getTransition().getName().startsWith("INV_") || node.getTransition().getName().equals("")) {
    					continue;
    				}
    				
//    				trace.add(node.getTransition().getName().replace(",", " and"));
    				System.out.print(node.getTransition().getName() + ", ");
    				tmpLogSeq += node.getTransition().getName().replace(",", " and") + ", ";
    			}
    			System.out.println("");
    			
    			
    			if (tmpLogSeq.length() > logLength) {
    				logSeq = String.valueOf(tmpLogSeq.subSequence(0, tmpLogSeq.length() - 2));
    			}
    			
    			
    		}
    		bw.write(logSeq + "\n");
    		bw.close();
    		fw.close();
        }
	}

	public void test(String filepath) throws Exception {
		File folder = new File(filepath);
		File[] ProcessList = folder.listFiles();

		ArrayList<NewPtsSet> testSets = new ArrayList<NewPtsSet>();

		for (int i = 0; i < ProcessList.length; i++) {
			File Process = ProcessList[i];
			if (Process.isHidden()) {
				continue;
			}

			PnmlImport pnmlimport = new PnmlImport();
			CTree ctree = null;
			PetriNet petrinet = null;
			FileInputStream pnml = null;
			pnml = new FileInputStream(Process.getAbsolutePath());
			petrinet = pnmlimport.read(pnml);

			CTreeGenerator generator = new CTreeGenerator(
					MyPetriNet.PromPN2MyPN(petrinet));
			ctree = generator.generateCTree();
			TTreeGenerator ttg = new TTreeGenerator();

			NewPtsSet nps2 = ttg.generatTTree(ctree, 2, Process.getName());

			testSets.add(nps2);

		}
		if (testSets.get(0).getNPSet().size() > testSets.get(1).getNPSet()
				.size()) {
			NewPtsSet temp1 = testSets.get(0);
			NewPtsSet temp2 = testSets.get(1);
			testSets.clear();
			testSets.add(temp2);
			testSets.add(temp1);
		}

//		showResult(testSets, 1);
	}

//	public void showResult(ArrayList<NewPtsSet> testSets, int a) {
//		testSets.get(a - 1).showSet();
//		testSets.get(a).showSet();
////		 System.out.println("The new PTS similarity< Ergodic> of *" +
////		 testSets.get(a-1).getFileName() + " and *" +
////		 testSets.get(a).getFileName() + " is: [" +
////		 testSets.get(a-1).setSimilarity_ergodic(testSets.get(a)) + "].");
//		System.out.println("The new PTS similarity< Greedy > of *"
//				+ testSets.get(a - 1).getFileName() + " and *"
//				+ testSets.get(a).getFileName() + " is: ["
//				+ testSets.get(a - 1).setSimilarity_greedy(testSets.get(a))
//				+ "].");
//		System.out.println("The new PTS similarity<   A*   > of *"
//				+ testSets.get(a - 1).getFileName() + " and *"
//				+ testSets.get(a).getFileName() + " is: ["
//				+ testSets.get(a - 1).setSimilarity_Astar(testSets.get(a))
//				+ "].");
//	}

}