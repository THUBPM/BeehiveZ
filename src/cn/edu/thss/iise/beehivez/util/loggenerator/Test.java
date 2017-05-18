package cn.edu.thss.iise.beehivez.util.loggenerator;

import java.io.File;
import java.io.FileInputStream;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;

public class Test {
	public Test(String folderName) throws Exception {
		FileLogger.deleteLogFile("log.csv");
		File folder = new File(folderName);
		File[] fileList = folder.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i].getAbsolutePath();
			int index = fileList[i].getName().indexOf(".");
			String logPath = "log" + "/"
					+ fileList[i].getName().substring(0, index) + ".mxml";
			FileInputStream is = new FileInputStream(fileName);
			PnmlImport input = new PnmlImport();
			PetriNet pn = input.read(is);
			// LogProduceMethod lpm = new AverageWeightLPM();
			LogProduceMethod lpm = new StratifiedWeightLPM();
			//LogManager.generateLog(logPath, 1, lpm, pn);
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Test t = new Test("model");
	}

}
