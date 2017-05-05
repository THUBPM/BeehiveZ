package cn.edu.thss.iise.beehivez.server.metric.rorm.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.petri.unfolding.BPNode;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.Condition;
import org.processmining.exporting.DotPngExport;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.importing.pnml.PnmlImport;

import att.grappa.Edge;
import att.grappa.Node;
import cern.colt.matrix.DoubleMatrix2D;
import cn.edu.thss.iise.beehivez.server.metric.rorm.jbpt.conversion.PetriNetConversion;

public class UnfoldingGeneratorTest {
//	public static void lrmsa() throws Exception {
//		// input multi-set of activities
//		ArrayList<String> multisetOfAct = new ArrayList<String>();
//		multisetOfAct.add("T0");
//		multisetOfAct.add("T3");
//		// input model file
//		String modelFile = "/Users/shawn/Documents/LAB/开题/exp/myModels/nfc_simple_prom.pnml";	
//		PnmlImport pnmlImport = new PnmlImport();
//		PetriNet p1 = pnmlImport.read(new FileInputStream(new File(modelFile)));
//		
//		
//		
//		NetSystem ns = PetriNetConversion.convert(p1);
//		CompletePrefixUnfolding cpu = new CompletePrefixUnfolding(ns);
//		
//		SSDCPU ssd = new SSDCPU();
//		ssd.initSSD();
//		DoubleMatrix2D lcaMatrixOriNet = ssd.getLCA(cpu);
//		ArrayList<String> alOrder_cfp = new ArrayList<String>();
//		DoubleMatrix2D ssdMatrix = ssd.computeSSD(cpu, alOrder_cfp);
//		
//		Map<String, Pair<String, Double>> candidates = new HashMap<String, Pair<String, Double>>();
//		for (int i = 0; i < multisetOfAct.size(); i++) {
//			for (int j = 0; j < multisetOfAct.size() && j != i; j++) {
//				ArrayList<Integer> matrixIndexRowList = getIndex(alOrder_cfp, multisetOfAct.get(i));
//				Iterator<Integer> iRowList = matrixIndexRowList.iterator();
//				double dist = Double.MAX_VALUE;
//				ArrayList<Integer> matrixIndexColList = getIndex(alOrder_cfp, multisetOfAct.get(j));
//				Iterator<Integer> iColList = matrixIndexColList.iterator();
//				while(iRowList.hasNext()) {
//					Integer iTemRow = iRowList.next();
//					while(iColList.hasNext()) {
//						Integer iTemCol = iColList.next();
//						double temDist = ssdMatrix.get(iTemRow, iTemCol);
////						if (temDist < dist && dist) {
////							dist = temDist;
////						}
//					}
//				}
//				candidates.put(multisetOfAct.get(i), new Pair<String, Double>(multisetOfAct.get(j), dist));
//			}
//		}
//		System.out.print(candidates);
//	}
//	
//	public static ArrayList<Integer> getIndex(ArrayList<String> alMatrix, String vName) {
//		ArrayList<Integer> rtn = new ArrayList<Integer>();
//		for (int i = 0; i < alMatrix.size(); i++) {
//			if (alMatrix.get(i).split("-")[0].equalsIgnoreCase(vName)) {
//				rtn.add(i);
//			}
//		}
//		return rtn;
//	}
//	
//	public static void jbptTest() throws Exception {
//		// String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/Models/NFC-01";
////		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/simple_xor_split";
////		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/simple_loop_prom";
////		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/XOR_SPLIT_AND_SPLIT";
//		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/2_cutoff_events_prom";
////		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/nfc_simple_prom";
////		String filePrefix = "/Users/shawn/Documents/LAB/开题/exp/myModels/cut1142";
//		String filePath1 = filePrefix + ".pnml";
//		String filePath2 = filePrefix + ".png";
//		String filePath3 = filePrefix + "-cfp.png";
//				
//		
////		PNMLSerializer pnmlSerializer = new PNMLSerializer();
////		String filePath = "/Users/shawn/Documents/LAB/开题/exp/myModels/2_cutoff_events_prom";
////		NetSystem ns = pnmlSerializer.parse(filePath1);
//		
//		PnmlImport pnmlImport = new PnmlImport();
//		PetriNet p1 = pnmlImport.read(new FileInputStream(new File(filePath1)));
//		
//		// ori
//		
////		ProvidedObject po1 = new ProvidedObject("petrinet", p1);
//		
////		DotPngExport dpe1 = new DotPngExport();
////		OutputStream image1 = new FileOutputStream(filePath2);
////		dpe1.export(po1, image1);
//		
//
//		NetSystem ns = PetriNetConversion.convert(p1);
//		CompletePrefixUnfolding cpu = new CompletePrefixUnfolding(ns);
//		
//		// cfp
//		
//		PetriNet p2 = PetriNetConversion.convert(cpu);
//		ProvidedObject po2 = new ProvidedObject("petrinet", p2);
//		DotPngExport dpe2 = new DotPngExport();
//		OutputStream image2 = new FileOutputStream(filePath3);
//		dpe2.export(po2, image2);
//		
//		ArrayList<String> mulitiSetOfAct = new ArrayList<String>();
//		mulitiSetOfAct.add("B");
//		
//////		ArrayList<String> alOrder_cfp1 = new ArrayList<String>();
////		SSD ssd = new SSD();
//////		DoubleMatrix2D ssdMatrix = ssd.computeSSD(cpu, alOrder_cfp1);
////		DoubleMatrix2D lcaMatrixOriNet = ssd.getLCA(cpu);
////		System.out.println(lcaMatrixOriNet);
//		
//		SSDCPU ssd = new SSDCPU();
//		ssd.initSSD();
//		DoubleMatrix2D lcaMatrixOriNet = ssd.getLCA(cpu);
//		ArrayList<String> alOrder_cfp1 = new ArrayList<String>();
//		DoubleMatrix2D ssdMatrix = ssd.computeSSD(cpu, alOrder_cfp1);
//		System.out.println(ssdMatrix);
//		
//	}
//	
	public static void getTraceBackbone(String startNode, ArrayList<String> alMatrix, ArrayList<String> mulitiSetOfAct, DoubleMatrix2D ssdMatrix, DoubleMatrix2D lcaMatrixOriNet) {
		Double[] ssdRow = new Double[alMatrix.size()];
		int indexOfStartNode = alMatrix.indexOf(startNode);
		for (int i = 0; i < ssdMatrix.columns(); i++) {
			ssdRow[i] = ssdMatrix.get(indexOfStartNode, i);
		}
		Arrays.sort(ssdRow);
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int n = 1;
		for(int i=0; i<n; i++)
		{
			long lStart = System.nanoTime();
//			jbptTest();
			long lStop = System.nanoTime();
			System.out.println("Duration " + (i+1) + ":" + (lStop - lStart)/1000000 + "\n");
		}
//		lrmsa();
	}

}
