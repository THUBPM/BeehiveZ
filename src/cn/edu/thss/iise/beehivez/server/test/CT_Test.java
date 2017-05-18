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

package cn.edu.thss.iise.beehivez.server.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTree;
import cn.edu.thss.iise.beehivez.server.basicprocess.CTreeGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;

public class CT_Test {
	String filepath;

	public CT_Test(String path) throws Exception {
		FileLogger.deleteLogFile("CT.csv");
		filepath = path;
		File folder = new File(filepath);
		File[] ProcessList = folder.listFiles();

		for (int i = 0; i < ProcessList.length; i++) {
			File Process = ProcessList[i];
			FileInputStream pnml = null;
			PetriNet petrinet = null;
			MyPetriNet mypetrinet = null;
			CTreeGenerator generator = null;
			CTree ctree = null;
			pnml = new FileInputStream(Process.getAbsolutePath());
			PnmlImport pnmlimport = new PnmlImport();
			petrinet = pnmlimport.read(pnml);
			mypetrinet = MyPetriNet.PromPN2MyPN(petrinet);
			generator = new CTreeGenerator(mypetrinet);
			System.out.print(Process.getName() + "    ");
			long startTime = System.nanoTime(); // ��ȡ��ʼʱ��
			ctree = generator.generateCTree();
			long endTime = System.nanoTime(); // ��ȡ����ʱ��
			long usedTime = endTime - startTime;
			int size = ctree.getAllNodes().size();
			String log = Process.getName() + "," + usedTime + "," + size;
			FileLogger.writeLog("CT.csv", log);
			pnml.close();
		}

	}

	public static void main(String[] args) throws Exception {
		CT_Test app = new CT_Test("a/pnml");

	}

}
