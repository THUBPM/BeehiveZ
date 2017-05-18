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

import java.io.File;
import java.io.FileInputStream;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.metric.BPSSimilarity;

public class Test {
	public Test() throws Exception {
		File[] file2 = new File[2];
		String fName1 = "temp/model1.pnml";
		String fName2 = "temp/model2.pnml";
		FileInputStream inputStream = new FileInputStream(fName1);
		PnmlImport pnmlImport = new PnmlImport();
		PetriNet p1 = pnmlImport.read(inputStream);
		inputStream = new FileInputStream(fName2);
		PetriNet p2 = pnmlImport.read(inputStream);
		BPSSimilarity bpss = new BPSSimilarity();
		bpss.similarity(p1, p2);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Test t = new Test();
	}
}
