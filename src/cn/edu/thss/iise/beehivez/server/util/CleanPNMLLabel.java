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
import java.util.Iterator;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 */
public class CleanPNMLLabel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String srcPath = "models/TC/";
			String destPath = "newmodels/TC/";

			File dir = new File(srcPath);
			int i = 0;
			for (File f : dir.listFiles()) {
				String fileName = f.getName();
				if (fileName.endsWith(".pnml")) {
					String destFileName = destPath + fileName;
					System.out.println("cleaning the labels from "
							+ f.getName());
					PetriNet pn = PetriNetUtil
							.getPetriNetFromPnml(new FileInputStream(f));
					for (Transition t : pn.getTransitions()) {
						String label = t.getIdentifier();
						if (label.matches("[a-zA-Z][0-9]*")) {
							t.setIdentifier("t" + i);
							i++;
						}
						// label = label.replace("\\n", " ");
						// label = label.replace("&", " and ");
						// label = label.replaceAll("[a-zA-Z][0-9]*", "t");
						//
						// t.setIdentifier(label);
					}
					PetriNetUtil.export2pnml(pn, destFileName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
