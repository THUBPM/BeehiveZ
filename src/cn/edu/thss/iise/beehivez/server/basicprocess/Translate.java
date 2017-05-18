package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.importing.pnml.PnmlImport;

import att.grappa.Edge;

public class Translate {
	public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "D:\\实验室\\开题\\efficiency\\loop\\";
		translateAll(path);
		
//		String logString = "D:\\实验室\\开题\\model_ZXC\\synthetic\\parallel\\";
//		batchLog(logString);
	}
	
	public static void batchLog(String path) throws Exception {
		PnmlImport pnmlImport = new PnmlImport();
        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File file : files) {
            FileInputStream input = new FileInputStream(file);
            System.out.println(file.getAbsolutePath());
            PetriNet pn = pnmlImport.read(input);
            input.close();
            pn.setName(file.getName());
            
            int index = 0;

            for(Transition t : pn.getTransitions()) {
            	t.setIdentifier("task" + index++);
            }
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            PnmlWriter.write(false, true, pn, writer);
            writer.close();
        }
	}
	
	public static void translate(String path) throws Exception {
		
		
		PnmlImport pnmlImport = new PnmlImport();
        File file = new File(path);
//        File[] files = folder.listFiles();
//        for(File file : files) {
            FileInputStream input = new FileInputStream(file);
            System.out.println(file.getAbsolutePath());
            PetriNet pn = pnmlImport.read(input);
            input.close();
            pn.setName(file.getName());
            
            int index = 0;

            for(Transition t : pn.getTransitions()) {
            	if (isContainChinese(t.getIdentifier())) {
            		t.setIdentifier("task" + index++);
            	}
            }
            index = 0;
            for(Place p : pn.getPlaces()) {
            	if (isContainChinese(p.getIdentifier())) {
            		p.setIdentifier("place" + index++);
            	}
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            PnmlWriter.write(false, true, pn, writer);
            writer.close();
//        }
	}
	
	public static void translateAll(String path) throws Exception {
		
		PnmlImport pnmlImport = new PnmlImport();
        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File file : files) {
            FileInputStream input = new FileInputStream(file);
            System.out.println(file.getAbsolutePath());
            PetriNet pn = pnmlImport.read(input);
            input.close();
            pn.setName(file.getName());
            
            int index = 0;

            for(Transition t : pn.getTransitions()) {
            	t.setIdentifier("task" + index++);
            }
            index = 0;
            for(Place p : pn.getPlaces()) {
            	p.setIdentifier("place" + index++);
            }
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            PnmlWriter.write(false, true, pn, writer);
            writer.close();
        }
	}
}
