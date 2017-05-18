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

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.algorithms.TPNWriter;
import org.processmining.framework.models.petrinet.algorithms.Woflan;

/**
 * 
 * @author Tao Jin
 * 
 * @date 2012-5-13
 * 
 */
public class WoflanAnalysisResult {

	private Woflan woflan = new Woflan();
	private int woflanNet;
	private boolean isWorkflowNet;
	private boolean isSCoverable;
	private boolean isPICoverable;
	private boolean isBounded;
	private boolean isNonDead;
	private boolean isLive;

	public WoflanAnalysisResult(PetriNet net) {
		try {
			// Write the net to a temporary tpn file.
			File tpnFile = File.createTempFile("woflan", ".tpn");
			tpnFile.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tpnFile,
					false));
			String export = TPNWriter.write(net);
			bw.write(export);
			bw.close();

			woflanNet = woflan.Open(tpnFile.getAbsolutePath());
			analyze();
			woflan.Close(woflanNet);

		} catch (Exception ex) {
			System.err
					.println("Error while running Woflan: " + ex.getMessage());
		}
	}

	private void analyze() {
		int nofSrcP, nofSnkP, nofSrcT, nofSnkT, nofUncN, nofSncN;
		nofSrcP = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofSrcP,
				0, 0));
		nofSnkP = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofSnkP,
				0, 0));
		nofSrcT = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofSrcT,
				0, 0));
		nofSnkT = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofSnkT,
				0, 0));
		woflan.Info(woflanNet, woflan.SetSUnc, 0, 0);
		nofUncN = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofUncN,
				0, 0));
		nofSncN = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofSncN,
				0, 0));
		if (nofSrcP == 1 && nofSnkP == 1 && nofSrcT == 0 && nofSnkT == 0
				&& nofUncN == 0 && nofSncN == 0) {
			this.isWorkflowNet = true;
		} else {
			this.isWorkflowNet = false;
		}

		woflan.Info(woflanNet, woflan.SetSCom, 0, 0);
		int nofNot = Integer.parseInt(woflan.Info(woflanNet,
				woflan.InfoNofNotSCom, 0, 0));
		if (nofNot == 0) {
			this.isSCoverable = true;
		} else {
			this.isSCoverable = false;
		}

		woflan.Info(woflanNet, woflan.SetSPIn, 0, 0);
		nofNot = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofNotSPIn,
				0, 0));
		if (nofNot == 0) {
			this.isPICoverable = true;
		} else {
			this.isPICoverable = false;
		}

		woflan.Info(woflanNet, woflan.SetUnb, 0, 0);
		nofNot = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofUnbP, 0,
				0));
		if (nofNot == 0) {
			this.isBounded = true;
		} else {
			this.isBounded = false;
		}

		woflan.Info(woflanNet, woflan.SetNLive, 0, 0);
		nofNot = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofDeadT,
				0, 0));
		if (nofNot == 0) {
			this.isNonDead = true;
		} else {
			this.isNonDead = false;
		}

		// Pre: The next line has been invoked before.
		// woflan.Info(woflanNet, woflan.SetNLive, 0, 0);
		nofNot = Integer.parseInt(woflan.Info(woflanNet, woflan.InfoNofNLiveT,
				0, 0));
		if (nofNot == 0) {
			this.isLive = true;
		} else {
			this.isLive = false;
		}
	}

	public boolean isWorkflowNet() {
		return this.isWorkflowNet;
	}

	public boolean isSoundWorkflowNet() {
		return this.isWorkflowNet && this.isNonDead && this.isBounded
				&& this.isLive;
	}

	public boolean isSCoverable() {
		return this.isSCoverable;
	}

	public boolean isPICoverable() {
		return this.isPICoverable;
	}

	public boolean isNonDead() {
		return this.isNonDead;
	}

	public boolean isLive() {
		return this.isLive;
	}

	public boolean isBounded() {
		return this.isBounded;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// input Petri net
		System.out.println("\ninput Petri net");
		PetriNet pn1 = PetriNetUtil
				.getPetriNetFromPnmlFile("E:/test/prexample.pnml");
		WoflanAnalysisResult war1 = new WoflanAnalysisResult(pn1);
		System.out.println("workflow net: " + war1.isWorkflowNet());
		System.out.println("live: " + war1.isLive());
		System.out.println("bound: " + war1.isBounded());
		System.out.println("non dead: " + war1.isNonDead());
		System.out.println("sound wfnet: " + war1.isSoundWorkflowNet());

		// input Petri net with initial token in the source place
		System.out
				.println("\ninput Petri net with initial token in the source place");
		PetriNet pn2 = PetriNetUtil
				.getPetriNetFromPnmlFile("E:/test/prexample.pnml");
		for (Place p : pn2.getPlaces()) {
			if (p.inDegree() == 0) {
				p.addToken(new Token());
			}
		}
		WoflanAnalysisResult war2 = new WoflanAnalysisResult(pn2);
		System.out.println("workflow net: " + war2.isWorkflowNet());
		System.out.println("live: " + war2.isLive());
		System.out.println("bound: " + war2.isBounded());
		System.out.println("non dead: " + war2.isNonDead());
		System.out.println("sound wfnet: " + war2.isSoundWorkflowNet());
	}

}
