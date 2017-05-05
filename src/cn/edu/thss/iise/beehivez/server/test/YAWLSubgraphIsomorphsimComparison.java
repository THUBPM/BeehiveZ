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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.processmining.framework.models.petrinet.PetriNet;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4YAWL;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.VF24YAWL;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.TransitionRelationBuilder;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author Tao Jin
 * 
 * @date 2012-3-7
 * 
 */
public class YAWLSubgraphIsomorphsimComparison {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// read the yawl model from this directory
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String path = chooser.getSelectedFile().getPath();

				File dir = new File(path);

				// the file used for log
				File fLog = new File(dir, "log.csv");
				FileWriter fw = new FileWriter(fLog);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("yawl1, yawl2, method, result, timecost(ns)");
				bw.newLine();

				// used for time cost measure
				long start, timecost;

				ArrayList<File> yawlfiles = new ArrayList<File>();
				for (File f : dir.listFiles()) {
					if (f.getName().endsWith(".yawl")) {
						yawlfiles.add(f);
					}
				}

				// use the ullman algorithm
				for (int i = 0; i < yawlfiles.size(); i++) {
					YNet ynet1 = YAWLUtil.getYNetFromFile(yawlfiles.get(i)
							.getAbsolutePath());
					for (int j = 0; j < yawlfiles.size(); j++) {
						YNet ynet2 = YAWLUtil.getYNetFromFile(yawlfiles.get(j)
								.getAbsolutePath());

						// to be exact, repeat test and then get the average
						// time
						// cost
						timecost = 0;
						boolean res = false;
						for (int n = 0; n < 5; n++) {
							start = System.nanoTime();
							try {
								res = Ullman4YAWL.subGraphIsomorphism(ynet1,
										ynet2);
								timecost += System.nanoTime() - start;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						double avgtimecost = (double) timecost / 5.0;
						bw.write(yawlfiles.get(i).getName() + ","
								+ yawlfiles.get(j).getName() + ",ullman," + res
								+ "," + avgtimecost);
						bw.newLine();
						bw.flush();

					}
				}

				// use the vf2 algorithm
				for (int i = 0; i < yawlfiles.size(); i++) {
					YNet ynet1 = YAWLUtil.getYNetFromFile(yawlfiles.get(i)
							.getAbsolutePath());
					for (int j = 0; j < yawlfiles.size(); j++) {
						YNet ynet2 = YAWLUtil.getYNetFromFile(yawlfiles.get(j)
								.getAbsolutePath());

						// to be exact, repeat test and then get the average
						// time
						// cost
						timecost = 0;
						boolean res = false;
						for (int n = 0; n < 5; n++) {
							start = System.nanoTime();
							try {
								res = VF24YAWL
										.subGraphIsomorphism(ynet1, ynet2);
								timecost += System.nanoTime() - start;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						double avgtimecost = (double) timecost / 5.0;
						bw.write(yawlfiles.get(i).getName() + ","
								+ yawlfiles.get(j).getName() + ",vf2," + res
								+ "," + avgtimecost);
						bw.newLine();
						bw.flush();
					}
				}

				bw.close();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
