package cn.edu.thss.iise.beehivez.server.metric.rorm.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.processmining.exporting.DotPngExport;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.rorm.jbpt.conversion.PetriNetConversion;

public class UnfoldingGeneratorTest {
	public static void jbptTest() throws Exception {
		String filePrefix = "/Users/little/Downloads/Models/NFC-01";
		String filePath1 = filePrefix + ".pnml";
		String filePath2 = filePrefix + ".png";
		String filePath3 = filePrefix + "-cfp.png";
				
		PnmlImport pnmlImport = new PnmlImport();
		PetriNet p1 = pnmlImport.read(new FileInputStream(new File(filePath1)));
		
		// ori
		
		ProvidedObject po1 = new ProvidedObject("petrinet", p1);
		DotPngExport dpe1 = new DotPngExport();
		OutputStream image1 = new FileOutputStream(filePath2);
		dpe1.export(po1, image1);
		

		NetSystem ns = PetriNetConversion.convert(p1);
		CompletePrefixUnfolding cpu = new CompletePrefixUnfolding(ns);
		
		// cfp
		
		PetriNet p2 = PetriNetConversion.convert(cpu);
		ProvidedObject po2 = new ProvidedObject("petrinet", p2);
		DotPngExport dpe2 = new DotPngExport();
		OutputStream image2 = new FileOutputStream(filePath3);
		dpe2.export(po2, image2);
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int n = 1;
		for(int i=0; i<n; i++)
		{
			long lStart = System.nanoTime();
			jbptTest();
			long lStop = System.nanoTime();
			System.out.println("Duration " + (i+1) + ":" + (lStop - lStart)/1000000 + "\n");
		}
	}

}
