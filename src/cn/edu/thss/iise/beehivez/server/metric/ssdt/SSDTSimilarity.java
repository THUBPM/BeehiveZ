/**
 * Behavioral Petri Net Similarity Algorithm based on Shortest Synchronization Distance
 */
package cn.edu.thss.iise.beehivez.server.metric.ssdt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.hibernate.mapping.Array;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

//import com.iise.shawn.util.PetriNetConversion;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.metric.rorm.jbpt.conversion.PetriNetConversion;

/**
 * @author little
 *
 */
public class SSDTSimilarity extends PetriNetSimilarity {

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity#similarity(org.processmining.framework.models.petrinet.PetriNet, org.processmining.framework.models.petrinet.PetriNet)
	 */
	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		//Compute the shortest synchronization distance matrix from pn1 and pn2
		return SSDTMatrix.build(pn1, pn2);
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Shortest Succession Distance between Tasks Matrix";
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.metric.PetriNetSimilarity#getDesription()
	 */
	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "Behavioral Petri Net Similarity Algorithm based on Shortest Succession Distance between Tasks, which is computed on Complete Finite Prefix.";
	}

	public static void main(String[] args)
	{
		FileInputStream fin1 = null;
		FileInputStream fin2 = null;
		try {
			String filePath1 = "D:\\实验室\\开题\\model\\FF301_1.pnml";
			//filePath1 = "D:\\Learn@Tsinghua\\各年级资料\\04b.四年级春季学期\\综合论文训练\\pnml\\original.pnml";
			String filePath2 = filePath1;
			filePath2 = "D:\\实验室\\开题\\model\\FF301_1.pnml";
			fin1 = new FileInputStream(filePath1);
			fin2 = new FileInputStream(filePath2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PnmlImport pImport = new PnmlImport();
		PetriNetResult pnr1 = null;
		PetriNetResult pnr2 = null;
		try {
			pnr1 = (PetriNetResult) pImport.importFile(fin1);
			pnr2 = (PetriNetResult) pImport.importFile(fin2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fin1.close();
			fin2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PetriNet pn1 = pnr1.getPetriNet();
		PetriNet pn2 = pnr2.getPetriNet();
		
		NetSystem ns = PetriNetConversion.convert(pn1);
		CompletePrefixUnfolding cpu = new CompletePrefixUnfolding(ns);
		
		PetriNet p2 = PetriNetConversion.convert(cpu);
		
//		CompleteFinitePrefix cfp1 = PetriNetUtil.buildCompleteFinitePrefix(pn1);
//		BufferedWriter bw;
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("D:\\Learn@Tsinghua\\过程数据组\\04.SSDT算法研究\\02.模型测试\\non_free_choice_join_cfp.pnml"))));
//			PnmlWriter.write(false, true, cfp1, bw);
//			bw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		SSDTSimilarity ssdSimilarity = new SSDTSimilarity();
		System.out.println(ssdSimilarity.similarity(p2, pn2));
	}
}
