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
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

/**
 * 
 * @author He tengfei
 * 
 */
public class BPSGenerator {
	// the model file
	private static String fileName;
	// used to record the execute info
	private ExecuteLog log = null;

	/**
	 * 
	 * @param bpsFile
	 *            where execute info store
	 */
	public BPSGenerator(ExecuteLog log) {
		this.log = log;
	}

	public LinkedList generateProcessSets(String fName) {
		int index = fName.lastIndexOf('/');
		fileName = fName.substring(index + 1);
		MyPetriNet petriNet = getPetriNet(fName);
		return getProcesses(petriNet);
	}

	public LinkedList<BasicProcessSet> getProcesses(MyPetriNet petriNet) {
		// TODO Auto-generated method stub
		CRTreeGenerator crtg = new CRTreeGenerator(petriNet);
		CTree ctree = crtg.getCtree();
		log.writeCTree(ctree);
		HashSet<CTreeNode> clusterVertexSet = ctree.getClusterVertexs();
		log.writeClusterVertexSet(clusterVertexSet);
		HashSet<CTreeNode> imageNodes = ctree
				.getDirectImageVertexsOfleafNodes();
		HashSet<CTreeNode> cutOffVertexs = ctree.getCutOffVertexs(imageNodes);
		log.writeCutOffVertex(cutOffVertexs);
		HashSet<GVBElement> cutOffDotPairSet = ctree.getCutoffDotPairSet();
		log.writeCutOffDotPairSet(cutOffDotPairSet);
		CTree crtree = crtg.generateCRTree();
		log.writeCRTree(crtree);
		LinkedList<BasicProcessSet> list = crtree.getBasicProcessOfPetrinet(
				cutOffDotPairSet, clusterVertexSet);
		log.writeBPS(list);
		return list;
	}

	private MyPetriNet getPetriNet(String fName) {
		// TODO Auto-generated method stub
		File file = new File(fName);
		try {
			FileInputStream input = new FileInputStream(file);
			PnmlImport pi = new PnmlImport();
			PetriNet pn = pi.read(input);
			return MyPetriNet.PromPN2MyPN(pn);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
