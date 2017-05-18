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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 * @date 2011-3-13
 * 
 */
public class SpecialLabelStatistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String srcPath = "models/TC/";
			String reportFileName = srcPath + "specialLabelInfo.txt";

			int nSpecialTransitions = 0;
			HashSet<String> specialLabels = new HashSet<String>();

			File dir = new File(srcPath);
			for (File f : dir.listFiles()) {
				if (f.getName().endsWith(".pnml")) {
					System.out.println("analyzing the labels from "
							+ f.getName());
					PetriNet pn = PetriNetUtil
							.getPetriNetFromPnml(new FileInputStream(f));
					for (Transition t : pn.getTransitions()) {
						String label = t.getIdentifier();
						if (label.matches("[a-zA-Z][0-9]*")) {
							nSpecialTransitions++;
							specialLabels.add(label);
						}
					}
				}
			}

			FileWriter fw = new FileWriter(reportFileName, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("the number of special transitions: "
					+ nSpecialTransitions);
			bw.newLine();
			bw.write("the number of special labels: " + specialLabels.size());
			bw.newLine();
			bw.write(specialLabels.toString());
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
