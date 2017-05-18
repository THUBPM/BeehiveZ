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

package cn.edu.thss.iise.beehivez.server.generator.petrinet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.WFNet;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.framework.ui.filters.GenericFileFilter;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 *
 */
public class GWFNetGenerator extends PetriNetGenerator {

	public PetriNet generateModel(int minTransitionsPerNet,
			int maxTransitionsPerNet, int maxDegree, int maxTransitionNameLength) {
		if (minTransitionsPerNet > maxTransitionsPerNet
				|| minTransitionsPerNet < 1 || maxTransitionsPerNet < 1
				|| maxDegree < 1 || maxTransitionNameLength < 1) {
			System.out.println("invalid parameters in generateGWFNet");
			return null;
		}
		// record the result Petri net
		PetriNet pn = new PetriNet();
		pn.setIdentifier(String.valueOf(System.currentTimeMillis()));

		// record the number of place in the result petri net
		int nPlaces = 0;

		// record the number of transition in the result petri net
		int nTransitions = 0;

		// generate the initial Petri net
		Place p0 = new Place("p" + nPlaces, pn);
		nPlaces++;
		pn.addPlace(p0);

		int transitionNumber = rand.nextInt(maxTransitionsPerNet
				- minTransitionsPerNet + 1)
				+ minTransitionsPerNet;

		// used for unused place
		Vector<Place> maybeUnusedPlace = new Vector<Place>();

		// generate the result petri net step by step
		while (nTransitions < transitionNumber) {
			ArrayList<PNNode> nodes = pn.getNodes();
			// random choose a node to expand
			int nNodes = nodes.size();
			int index = rand.nextInt(nNodes);
			PNNode node = nodes.get(index);
			if (node instanceof Place) {
				// expansion on the place
				Place p = (Place) node;

				// if the place choosed for expansion, it is not unused place
				maybeUnusedPlace.remove(p);

				int choice = rand.nextInt(2);
				if (0 == choice && p.outDegree() != 0 && p.inDegree() != 0
						&& p.inDegree() < maxDegree
						&& p.outDegree() < maxDegree) {
					// self-loop expansion
					String tName = getRandomString(maxTransitionNameLength);
					Transition t = new Transition(tName, pn);
					LogEvent le = new LogEvent(tName, "auto");
					t.setLogEvent(le);
					pn.addTransition(t);
					nTransitions++;

					pn.addEdge(p, t);
					pn.addEdge(t, p);

					// too many length one loop
					// adjust it to length two loop
					if (rand.nextInt(5) != 1 && nTransitions < transitionNumber) {
						// generate another place as the end part of the
						// original
						// place
						Place pp = new Place("p" + nPlaces, pn);
						nPlaces++;
						pn.addPlace(pp);
						Iterator it = p.getSuccessors().iterator();
						while (it.hasNext()) {
							Transition tSuc = (Transition) it.next();
							pn.delEdge(p, tSuc);
							pn.addEdge(pp, tSuc);
						}

						// generate a transition between two places
						tName = getRandomString(maxTransitionNameLength);
						t = new Transition(tName, pn);
						le = new LogEvent(tName, "auto");
						t.setLogEvent(le);
						pn.addTransition(t);
						nTransitions++;

						pn.addEdge(p, t);
						pn.addEdge(t, pp);
					}
				} else {
					// choice or sequence structure expansion
					// random the degree
					int nDegree = rand.nextInt(maxDegree) + 1;
					int limit = transitionNumber - nTransitions;
					if (nDegree > limit) {
						nDegree = limit;
					}

					// generate another place as the end part of the original
					// place
					Place pp = new Place("p" + nPlaces, pn);
					nPlaces++;
					pn.addPlace(pp);
					Iterator it = p.getSuccessors().iterator();
					while (it.hasNext()) {
						Transition tSuc = (Transition) it.next();
						pn.delEdge(p, tSuc);
						pn.addEdge(pp, tSuc);
					}

					// generate transitions between these two places
					for (int i = 0; i < nDegree; i++) {
						String tName = getRandomString(maxTransitionNameLength);
						Transition t = new Transition(tName, pn);
						LogEvent le = new LogEvent(tName, "auto");
						t.setLogEvent(le);
						pn.addTransition(t);
						nTransitions++;

						pn.addEdge(p, t);
						pn.addEdge(t, pp);
					}
				}
			} else if (node instanceof Transition) {
				// expansion on the transition
				Transition t = (Transition) node;

				// generate another transition as the end part of the original
				// transtion
				String tName = getRandomString(maxTransitionNameLength);
				Transition tt = new Transition(tName, pn);
				LogEvent le = new LogEvent(tName, "auto");
				tt.setLogEvent(le);
				pn.addTransition(tt);
				nTransitions++;

				Iterator it = t.getSuccessors().iterator();
				while (it.hasNext()) {
					Place p = (Place) it.next();
					pn.delEdge(t, p);
					pn.addEdge(tt, p);
				}

				// generate places between the two transitions
				int nDegree = rand.nextInt(maxDegree) + 1;
				for (int i = 0; i < nDegree; i++) {
					Place p = new Place("p" + nPlaces, pn);
					nPlaces++;
					pn.addPlace(p);

					pn.addEdge(t, p);
					pn.addEdge(p, tt);

					maybeUnusedPlace.add(p);
				}
			}
		}

		// delete unused places
		for (int i = 0; i < maybeUnusedPlace.size(); i++) {
			Place p = maybeUnusedPlace.get(i);
			if (p.outDegree() == 1) {
				Transition t = (Transition) p.getSuccessors().iterator().next();
				if (t.inDegree() > 1) {
					pn.delPlace(p);
				}
			}
		}

		// // add a place and a transition at the beginning of this model
		// Place pPre = new Place("pPre", pn);
		// Transition tPre = new Transition("tPre", pn);
		// pn.addPlace(pPre);
		// pn.addTransition(tPre);
		// pn.addEdge(pPre, tPre);
		// pn.addEdge(tPre, p0);

		return pn;
	}

	public void export2pnml(PetriNet pn, String filename) {
		if (filename != null && !filename.equals("")) {
			try {
				FileWriter fw = new FileWriter(filename, false);
				BufferedWriter bw = new BufferedWriter(fw);
				PnmlWriter.write(false, true, pn, bw);
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void export2pnml(PetriNet pn) {
		String filename;
		JFileChooser chooser = new JFileChooser();
		GenericFileFilter filter = new GenericFileFilter("pnml");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filename = filter.addExtension(chooser.getSelectedFile().getPath());
			export2pnml(pn, filename);
		}
	}

	public static void main(String[] args) {
		int i = 0;
		while (true) {
			long start = System.currentTimeMillis();
			GWFNetGenerator generator = new GWFNetGenerator();
			PetriNet pn = generator.generateModel(1, 50, 10, 3);
			PetriNetUtil.getPnmlBytes(pn);
			long end = System.currentTimeMillis();
			long timeCost = end - start;
			i++;
			if (i % 10000 == 0) {
				System.out.println(i);
				System.out.println(timeCost + "ms");
			}
			pn.destroyPetriNet();
		}
	}

	@Override
	public boolean supportDegreeConfiguration() {
		return true;
	}
}
