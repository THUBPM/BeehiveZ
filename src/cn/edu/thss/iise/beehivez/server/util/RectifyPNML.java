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
import java.util.ArrayList;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

/**
 * some Petri nets transformed using ProM has more than one source places and
 * more than one sink places, this tool is used to rectify these models. merge
 * all the source places into one, and merge all the sink places into one.
 * 
 * @author Tao Jin
 * 
 */
public class RectifyPNML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String srcPath = "cleanedEpcPnml/";
			String destPath = "rectifiedPnml/";

			File dir = new File(srcPath);
			for (File f : dir.listFiles()) {
				System.out.println("parsing " + f.getPath());
				PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(f);
				Place pSource = new Place("source", pn);
				pn.addPlace(pSource);
				Place pSink = new Place("sink", pn);
				pn.addPlace(pSink);
				ArrayList<Place> removeList = new ArrayList<Place>();
				for (Place p : pn.getPlaces()) {
					if (p != pSource && p != pSink) {
						if (p.inDegree() == 0) {
							Iterator<Transition> it = p.getSuccessors()
									.iterator();
							while (it.hasNext()) {
								Transition t = it.next();
								pn.addEdge(pSource, t);
							}
							removeList.add(p);
						} else if (p.outDegree() == 0) {
							Iterator<Transition> it = p.getPredecessors()
									.iterator();
							while (it.hasNext()) {
								Transition t = it.next();
								pn.addEdge(t, pSink);
							}
							removeList.add(p);
						}
					}
				}

				for (Place p : removeList) {
					pn.delPlace(p);
				}

				PetriNetUtil.export2pnml(pn, destPath + f.getName());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
