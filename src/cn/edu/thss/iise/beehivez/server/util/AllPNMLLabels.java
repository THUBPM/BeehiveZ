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
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

/**
 * @author Tao Jin
 * 
 *         given a set of pnml files, get the number of labels.
 * 
 */
public class AllPNMLLabels {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// whether the label similarity will be considered and the threshold
			boolean enableSimilarity = false;
			float similarityThreshold = 1.0f;

			// choose the source directory
			String sourcePath = "models";
			JFileChooser chooser = new JFileChooser(sourcePath);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				sourcePath = chooser.getSelectedFile().getPath();
			}

			// use to check the duplicate labels
			HashSet<String> labelSet = new HashSet<String>();
			// store the unique labels
			// the similarity can be considered, the first label in every vector
			// is the seed.
			Vector<Vector<String>> similarLabelSets = new Vector<Vector<String>>();

			int nModels = 0;
			int nTransitions = 0;

			File dir = new File(sourcePath);
			for (File f : dir.listFiles()) {
				if (f.getName().endsWith(".pnml")) {
					System.out.println("parsing " + f.getName());
					PetriNet pn = PetriNetUtil
							.getPetriNetFromPnml(new FileInputStream(f));
					nModels++;
					for (Transition t : pn.getTransitions()) {
						nTransitions++;
						String label = t.getIdentifier();
						if (labelSet.add(label)) {
							if (enableSimilarity) {
								// label similarity is considered
								boolean handled = false;
								for (Vector<String> v : similarLabelSets) {
									String seed = v.get(0);
									if (StringSimilarityUtil
											.semanticSimilarity(seed, label) >= similarityThreshold) {
										v.add(label);
										handled = true;
									}
								}

								if (!handled) {
									Vector<String> v = new Vector<String>();
									v.add(label);
									similarLabelSets.add(v);
								}
							} else {
								// label similarity is not considered
								Vector<String> v = new Vector<String>();
								v.add(label);
								similarLabelSets.add(v);
							}
						}
					}
				}
			}

			String reportFileName = sourcePath + "/labelInfo.txt";
			FileWriter fw = new FileWriter(reportFileName, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("the number of models: " + nModels);
			bw.newLine();
			bw.write("the number of transitions in total: " + nTransitions);
			bw.newLine();
			bw
					.write("the number of unique transitions without similarity considered: "
							+ labelSet.size());
			bw.newLine();
			bw
					.write("the number of unique transitions with similarity considered: "
							+ similarLabelSets.size());
			bw.newLine();
			bw.newLine();
			int index = 0;
			for (Vector<String> v : similarLabelSets) {
				bw.write(String.valueOf(index));
				bw.newLine();
				bw.write(v.toString());
				bw.newLine();
				index++;
			}
			bw.write(similarLabelSets.toString());
			bw.close();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
