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

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.TransitionRelationBuilder;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * compare the performance of TARS computing between the method based on
 * coverability graph and the complete prefix unfolding
 * 
 * @author Tao Jin
 * 
 */
public class TARSComputingPerformanceComparison {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// read the Petri net from this directory
			File dir = new File("paralleltest");

			// the file used for log
			File fLog = new File(dir, "log.csv");
			FileWriter fw = new FileWriter(fLog);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("pn, method, timecost(ms)");
			bw.newLine();

			// used for time cost measure
			long start, timecost;

			// to be exact, repeat test and then get the average
			// time cost

			// use the method based on complete prefix unfolding
			for (File f : dir.listFiles()) {
				if (f.getName().endsWith(".pnml")) {
					for (int i = 0; i < 5; i++) {
						PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(f);
						// use the method based on complete prefix unfolding
						start = System.currentTimeMillis();
						try {
							PetriNetUtil.getTARSFromPetriNetByCFP(pn);
							timecost = System.currentTimeMillis() - start;
							bw.write(f.getName() + ",cfp," + timecost);
							bw.newLine();
							bw.flush();
						} catch (Exception e) {
							e.printStackTrace();
							timecost = System.currentTimeMillis() - start;
							timecost = -timecost;
							bw.write(f.getName() + ",cfp," + timecost);
							bw.newLine();
							bw.flush();
						}

						pn.destroyPetriNet();
					}
				}
			}

			// use the method based on coverability graph
			for (File f : dir.listFiles()) {
				if (f.getName().endsWith(".pnml")) {
					for (int i = 0; i < 5; i++) {
						PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(f);

						// use the method based on coverability graph
						start = System.currentTimeMillis();
						try {
							PetriNetUtil.getTARSFromPetriNetByCG(pn);
							timecost = System.currentTimeMillis() - start;
							bw.write(f.getName() + ",cg," + timecost);
							bw.newLine();
							bw.flush();
						} catch (Exception e) {
							e.printStackTrace();
							timecost = System.currentTimeMillis() - start;
							timecost = -timecost;
							bw.write(f.getName() + ",cg," + timecost);
							bw.newLine();
							bw.flush();
						}

						pn.destroyPetriNet();
					}
				}
			}

			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
