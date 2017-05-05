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
package cn.edu.thss.iise.beehivez.server.util;

import java.io.File;
import java.io.FileInputStream;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.yawl.YAWLModel;

/**
 * transform from epc model to yawl model
 * 
 * @author Tao Jin
 * 
 */
public class PNML2YAWL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String srcPath = "cleanedEpcPnml/";
			String destPath = "cleanedEpcYawl/";

			File dir = new File(srcPath);
			for (File f : dir.listFiles()) {
				String fileName = f.getName();
				String destFileName = destPath
						+ fileName.substring(0, fileName.indexOf(".pnml"))
						+ ".yawl";
				System.out.println("transform from " + f.getName()
						+ " to YAWL model");
				PetriNet pn = PetriNetUtil
						.getPetriNetFromPnml(new FileInputStream(f));
				YAWLModel ym = YAWLUtil.PetriNet2YAWL(pn);
				String id = destFileName.substring(destFileName
						.lastIndexOf('/') + 2, destFileName.lastIndexOf('.'));
				ym.getDecomposition("labeledWFNet").setID(id);
				YAWLUtil.writeYAWL(ym, destFileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
