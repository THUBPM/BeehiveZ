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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.processmining.framework.models.yawl.YAWLCondition;
import org.processmining.framework.models.yawl.YAWLDecomposition;
import org.processmining.framework.models.yawl.YAWLEdge;
import org.processmining.framework.models.yawl.YAWLModel;
import org.processmining.framework.models.yawl.YAWLNode;
import org.processmining.framework.models.yawl.YAWLTask;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

/**
 * some yawl model transformed using ProM has more than one input conditions and
 * more than one output conditions, this tool is used to rectify these models.
 * merge all the input conditions into one, and merge all the output conditions
 * into one.
 * 
 * @author Tao Jin
 * 
 */
public class RectifyYAWL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String srcPath = "cleanedEpcYawl/";
			String destPath = "rectifiedYAWL/";

			File dir = new File(srcPath);
			for (File f : dir.listFiles()) {
				System.out.println("parsing " + f.getPath());
				YAWLModel model = YAWLUtil.readYAWL(f.getPath());
				Iterator<YAWLDecomposition> itd = model.getDecompositions()
						.iterator();
				YAWLDecomposition yd = null;
				do {
					yd = itd.next();
				} while (itd.hasNext() && !yd.isRoot());
				if (yd == null) {
					System.out.println("no root net in " + f.getName());
					continue;
				}
				YAWLCondition inputCondition = yd.addInputCondition("newStart");
				YAWLCondition outputCondition = yd.addOutputCondition("newEnd");

				ArrayList<String> removeList = new ArrayList<String>();
				for (YAWLNode node : yd.getNodes()) {
					if (node instanceof YAWLCondition) {
						YAWLCondition c = (YAWLCondition) node;
						if (c != inputCondition && c != outputCondition) {
							if (c.isInputCondition() || c.inDegree() == 0) {
								Iterator<YAWLNode> it = c.getSuccessors()
										.iterator();
								while (it.hasNext()) {
									YAWLNode n = it.next();
									yd.addEdge(inputCondition.getID(), n
											.getID(), true, null, null);
								}
								removeList.add(c.getID());
							} else if (c.isOutputCondition()
									|| c.outDegree() == 0) {
								Iterator<YAWLNode> it = c.getPredecessors()
										.iterator();
								while (it.hasNext()) {
									YAWLNode n = it.next();
									yd.addEdge(n.getID(), outputCondition
											.getID(), true, null, null);
								}
								removeList.add(c.getID());
							}
						}
					}
				}

				for (String str : removeList) {
					yd.removeYawlNode(str);
				}

				YAWLUtil.writeYAWL(model, destPath + f.getName());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
