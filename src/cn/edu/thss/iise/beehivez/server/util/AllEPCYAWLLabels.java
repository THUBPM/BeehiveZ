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
import org.processmining.framework.models.yawl.YAWLDecomposition;
import org.processmining.framework.models.yawl.YAWLModel;
import org.processmining.framework.models.yawl.YAWLNode;
import org.processmining.framework.models.yawl.YAWLTask;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

/**
 * @author Tao Jin
 * 
 */
public class AllEPCYAWLLabels {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String path = "YAWL/";
			TreeSet<String> labels = new TreeSet<String>();

			int nModels = 0;
			int nTasks = 0;
			int nLabels = 0;

			File dir = new File(path);
			for (File f : dir.listFiles()) {
				System.out.println("parsing " + f.getName());
				YNet net = YAWLUtil.getYNetFromFile(f.getPath());
				nModels++;
				for (YTask task : net.getNetTasks()) {
					labels.add(task.getName());
					nTasks++;
				}
				// YAWLModel model = YAWLUtil.readYAWL(f.getPath());
				// YAWLDecomposition yd = model.getDecompositions().iterator()
				// .next();
				// nModels++;
				// for (YAWLNode node : yd.getNodes()) {
				// if (node instanceof YAWLTask) {
				// labels.add(node.getName());
				// nTasks++;
				// }
				// }
			}
			nLabels = labels.size();

			FileWriter fw = new FileWriter("yawllabelInfo.txt", false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("the number of models: " + nModels);
			bw.newLine();
			bw.write("the number of transitions in total: " + nTasks);
			bw.newLine();
			bw.write("the number of different transitions: " + nLabels);
			bw.newLine();
			bw.close();
			fw.close();

			fw = new FileWriter("yawllabels.txt", false);
			bw = new BufferedWriter(fw);
			Iterator<String> it = labels.iterator();
			while (it.hasNext()) {
				String label = it.next();
				bw.write(label);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
