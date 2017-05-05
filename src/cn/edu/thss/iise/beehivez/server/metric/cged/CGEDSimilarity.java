package cn.edu.thss.iise.beehivez.server.metric.cged;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.processmining.analysis.graphmatching.algos.GraphEditDistanceGreedy;
import org.processmining.exporting.petrinet.PnmlExport;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.CoverabilityGraphBuilder;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.framework.ui.ComboBoxLogEvent;
import org.processmining.importing.pnml.PnmlImport;
import org.semanticweb.kaon2.ed;
import org.semanticweb.kaon2.l;

import cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity_Wang;
import cn.edu.thss.iise.beehivez.server.metric.BehavioralProfileSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.CausalFootprintSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.JaccardTARSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.LabelFreeTARSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.ssdt.SSDTSimilarity;

public class CGEDSimilarity extends PetriNetSimilarity {

	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		// TODO Auto-generated method stub
		((Place) pn1.getSource()).addToken(new Token());
		((Place) pn2.getSource()).addToken(new Token());
		StateSpace space1 = CoverabilityGraphBuilder.build(pn1);
		StateSpace space2 = CoverabilityGraphBuilder.build(pn2);
		cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder builder = new cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder();
		CoverabilityGraph cGraph1 = builder.convert(space1, pn1.getName());
		CoverabilityGraph cGraph2 = builder.convert(space2, pn2.getName());

		GraphEditDistanceGreedy measure = new GraphEditDistanceGreedy();
		double distance = measure.compute(cGraph1, cGraph2);
		// System.out.println(distance);
		int edgeSpan1 = 0, edgeSpan2 = 0;
		for (String edge : cGraph1.getConflictSpans().keySet()) {
			int span = Math.max(cGraph1.getConflictSpans().get(edge), cGraph1
					.getLoopSpans().get(edge));
			edgeSpan1 += ((span > 0) ? span : 1.0);
		}
		for (String edge : cGraph2.getConflictSpans().keySet()) {
			int span = Math.max(cGraph2.getConflictSpans().get(edge), cGraph2
					.getLoopSpans().get(edge));
			edgeSpan2 += ((span > 0) ? span : 1.0);
		}
		double edgeWeight = (edgeSpan1 + edgeSpan2 - 2) * 0.5;
		double similarity = (cGraph1.getVertices().size()
				+ cGraph2.getVertices().size() - 2 - distance + edgeWeight)
				/ (cGraph1.getVertices().size() + cGraph2.getVertices().size()
						- 2 + edgeWeight);
		BigDecimal sim = new BigDecimal(similarity);
		sim = sim.setScale(3, BigDecimal.ROUND_HALF_UP);
		return sim.floatValue();
	}

	public String[] newSimilarity(PetriNet pn1, PetriNet pn2) {
		// TODO Auto-generated method stub
		((Place) pn1.getSource()).addToken(new Token());
		((Place) pn2.getSource()).addToken(new Token());
		StateSpace space1 = CoverabilityGraphBuilder.build(pn1);
		StateSpace space2 = CoverabilityGraphBuilder.build(pn2);
		cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder builder = new cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder();
		CoverabilityGraph cGraph1 = builder.convert(space1, pn1.getName());
		CoverabilityGraph cGraph2 = builder.convert(space2, pn2.getName());

		GraphEditDistanceGreedy measure = new GraphEditDistanceGreedy();
		double distance = measure.compute(cGraph1, cGraph2);
		// System.out.println(distance);
		int edgeSpan1 = 0, edgeSpan2 = 0;
		for (String edge : cGraph1.getConflictSpans().keySet()) {
			int span = Math.max(cGraph1.getConflictSpans().get(edge), cGraph1
					.getLoopSpans().get(edge));
			edgeSpan1 += ((span > 0) ? span : 1.0);
		}
		for (String edge : cGraph2.getConflictSpans().keySet()) {
			int span = Math.max(cGraph2.getConflictSpans().get(edge), cGraph2
					.getLoopSpans().get(edge));
			edgeSpan2 += ((span > 0) ? span : 1.0);
		}
		double edgeWeight = (edgeSpan1 + edgeSpan2 - 2) * 0.5;
		double similarity = (cGraph1.getVertices().size()
				+ cGraph2.getVertices().size() - 2 - distance + edgeWeight)
				/ (cGraph1.getVertices().size() + cGraph2.getVertices().size()
						- 2 + edgeWeight);
		BigDecimal sim = new BigDecimal(similarity);
		sim = sim.setScale(3, BigDecimal.ROUND_HALF_UP);
		BigDecimal dis = new BigDecimal(distance);
		dis = dis.setScale(3, BigDecimal.ROUND_HALF_UP);
		String[] result = { dis.toString(), sim.toString() };
		return result;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void batchExperiment() {
		try {
			String prefix = "F:\\Demo\\Prom\\";
			String suffix = ".pnml";
			List<String> modelFiles = new ArrayList<String>();
			modelFiles.add("M0,M1");
			modelFiles.add("M0,M2");
			modelFiles.add("M0,M3");
			modelFiles.add("M0,M4");
			modelFiles.add("M0,M5");
			modelFiles.add("M0,M6");
			modelFiles.add("M0,M7");
			modelFiles.add("M0,M8");
			modelFiles.add("M3,M9");
			modelFiles.add("M3,M10");
			modelFiles.add("M3,M11");
			modelFiles.add("M3,M12");
			modelFiles.add("M3,M13");
			modelFiles.add("M3,M9");
			modelFiles.add("M3,M14");
			modelFiles.add("M3,M15");
			modelFiles.add("M3,M16");

			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"F:\\Demo\\result.csv"));
			CGEDSimilarity cged = new CGEDSimilarity();
			File file1 = null, file2 = null;
			FileInputStream fInput1 = null, fInput2 = null;
			PnmlImport pnmlImport = new PnmlImport();
			PetriNet pn1 = null, pn2 = null;
			StringBuilder disBuilder = new StringBuilder();
			StringBuilder simBuilder = new StringBuilder();
			for (String filePair : modelFiles) {
				String[] models = filePair.split(",");
				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				String[] result = cged.newSimilarity(pn1, pn2);
				disBuilder.append(result[0]);
				disBuilder.append(",");
				simBuilder.append(result[1]);
				simBuilder.append(",");
			}
			writer.write(disBuilder.toString());
			writer.newLine();
			writer.write(simBuilder.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void comparativeExperiment() {
		try {
			String prefix = "F:\\Demo\\Prom\\";
			String suffix = ".pnml";
			List<String> modelFiles = new ArrayList<String>();
			modelFiles.add("M0,M1");
			modelFiles.add("M0,M2");
			modelFiles.add("M0,M3");
			modelFiles.add("M0,M4");
			modelFiles.add("M0,M5");
			modelFiles.add("M0,M6");
			modelFiles.add("M0,M7");
			modelFiles.add("M0,M8");
//			modelFiles.add("M0,M8a");
//			modelFiles.add("M0,M8b");
			modelFiles.add("M3,M9");
			modelFiles.add("M3,M10");
			modelFiles.add("M3,M11");
			modelFiles.add("M3,M12");
			modelFiles.add("M3,M13");
			modelFiles.add("M3,M9");
			modelFiles.add("M3,M14");
			modelFiles.add("M3,M15");
			modelFiles.add("M3,M16");

			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"F:\\Demo\\result.csv"));
			JaccardTARSimilarity tar = new JaccardTARSimilarity();
			BTSSimilarity_Wang pts = new BTSSimilarity_Wang();
			SSDTSimilarity ssdt = new SSDTSimilarity();
			CausalFootprintSimilarity cf = new CausalFootprintSimilarity();
			BehavioralProfileSimilarity bp = new BehavioralProfileSimilarity();
			CGEDSimilarity cged = new CGEDSimilarity();
			File file1 = null, file2 = null;
			FileInputStream fInput1 = null, fInput2 = null;
			PnmlImport pnmlImport = new PnmlImport();
			PetriNet pn1 = null, pn2 = null;
			StringBuilder tarBuilder = new StringBuilder();
			tarBuilder.append("TAR,");
			StringBuilder ptsBuilder = new StringBuilder();
			ptsBuilder.append("PTS,");
			StringBuilder ssdtBuilder = new StringBuilder();
			ssdtBuilder.append("SSDT,");
			StringBuilder cfBuilder = new StringBuilder();
			cfBuilder.append("CF,");
			StringBuilder bpBuilder = new StringBuilder();
			bpBuilder.append("BP,");
			StringBuilder cgedBuilder = new StringBuilder();
			cgedBuilder.append("CGED,");
			int i = 0;
			for (String filePair : modelFiles) {
				String[] models = filePair.split(",");
				
				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal tarV = new BigDecimal(tar.similarity(pn1, pn2));
				tarV = tarV.setScale(3, BigDecimal.ROUND_HALF_UP);
				tarBuilder.append(tarV.toString());
				tarBuilder.append(",");

				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal ptsV = new BigDecimal(pts.similarity(pn1, pn2));
				ptsV = ptsV.setScale(3, BigDecimal.ROUND_HALF_UP);
				ptsBuilder.append(ptsV.toString());
				ptsBuilder.append(",");

				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal ssdtV = new BigDecimal(ssdt.similarity(pn1, pn2));
				ssdtV = ssdtV.setScale(3, BigDecimal.ROUND_HALF_UP);
				ssdtBuilder.append(ssdtV.toString());
				ssdtBuilder.append(",");

				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal cfV = new BigDecimal(cf.similarity(pn1, pn2));
				cfV = cfV.setScale(3, BigDecimal.ROUND_HALF_UP);
				cfBuilder.append(cfV.toString());
				cfBuilder.append(",");

				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal bpV = new BigDecimal(bp.similarity(pn1, pn2));
				bpV = bpV.setScale(3, BigDecimal.ROUND_HALF_UP);
				bpBuilder.append(bpV.toString());
				bpBuilder.append(",");

				file1 = new File(prefix + models[0] + suffix);
				fInput1 = new FileInputStream(file1);
				file2 = new File(prefix + models[1] + suffix);
				fInput2 = new FileInputStream(file2);
				pn1 = pnmlImport.read(fInput1);
				pn2 = pnmlImport.read(fInput2);
				pn1.setName(file1.getName());
				pn2.setName(file2.getName());
				BigDecimal cgedV = new BigDecimal(cged.similarity(pn1, pn2));
				cgedV = cgedV.setScale(3, BigDecimal.ROUND_HALF_UP);
				cgedBuilder.append(cgedV.toString());
				cgedBuilder.append(",");
				
				System.out.println((++i) + "/17");
			}
			writer.write(tarBuilder.toString());
			writer.newLine();
			writer.write(ptsBuilder.toString());
			writer.newLine();
			writer.write(ssdtBuilder.toString());
			writer.newLine();
			writer.write(cfBuilder.toString());
			writer.newLine();
			writer.write(bpBuilder.toString());
			writer.newLine();
			writer.write(cgedBuilder.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void singleExperiment() {
		try {

			File file1 = new File("C:\\Users\\picry\\Desktop\\CGED\\Model\\Model\\WEN_S7.pnml");
			FileInputStream fInput1 = new FileInputStream(file1);
			File file2 = new File("C:\\Users\\picry\\Desktop\\CGED\\Model\\Model\\WEN_S8.pnml");
			FileInputStream fInput2 = new FileInputStream(file2);
			PnmlImport pnmlImport = new PnmlImport();
			PetriNet pn1 = pnmlImport.read(fInput1);
			PetriNet pn2 = pnmlImport.read(fInput2);
			pn1.setName(file1.getName());
			pn2.setName(file2.getName());
			System.out.println(pn1.getTransitions().size() + "  " + pn2.getTransitions().size());
//			CGEDSimilarity similarity = new CGEDSimilarity();
//			SSDTSimilarity similarity = new9 SSDTSimilarity();
//			JaccardTARSimilarity similarity = new JaccardTARSimilarity();
			BTSSimilarity_Wang similarity = new BTSSimilarity_Wang();
			long start = System.currentTimeMillis();
			System.out.println(similarity.similarity(pn1, pn2));
			long end = System.currentTimeMillis();
			System.out.println("Time consuming: " + ((end - start) / 1000));
//			String[] result = similarity.newSimilarity(pn1, pn2);
//			System.out.println(result[0]);
//			System.out.println(file1.getName() + ":" + file2.getName()
//					+ "   Similarity:" + result[1]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void autoLayout() {
		try {
			File file = new File("D:\\Downloads\\2.pnml");
			FileInputStream fInput = new FileInputStream(file);
			PnmlImport pnmlImport = new PnmlImport();
			PetriNet pn = pnmlImport.read(fInput);
			for(Transition t : pn.getTransitions()) {
				if(t.isInvisibleTask()) {
					LogEvent event = new LogEvent(ComboBoxLogEvent.NONE,
							ComboBoxLogEvent.NONE);
					t.setLogEvent(event);
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\Downloads\\22.pnml"));
			PnmlWriter.write(false, true, pn, bw);
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		batchExperiment();
//		comparativeExperiment();
		singleExperiment();
//		autoLayout();
	}

}
