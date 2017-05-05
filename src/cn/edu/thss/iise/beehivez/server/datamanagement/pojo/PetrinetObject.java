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
package cn.edu.thss.iise.beehivez.server.datamanagement.pojo;

import java.io.InputStream;

import org.processmining.framework.models.petrinet.PetriNet;

/**
 * @author JinTao 2009.9.3
 * 
 */
public class PetrinetObject {
	private long petrinet_id = -1;
	private long process_id = -1;
	private InputStream pnmlIn = null;
	private int nplace = -1;
	private int ntransition = -1;
	private int narc = -1;
	private int ndegree = -1;
	private PetriNet petriNet = null;
	private byte[] pnml = null;

	/**
	 * @return the pnml
	 */
	public byte[] getPnml() {
		return pnml;
	}

	/**
	 * @param pnml
	 *            the pnml to set
	 */
	public void setPnml(byte[] pnml) {
		this.pnml = pnml;
	}

	/**
	 * @return the nplace
	 */
	public int getNplace() {
		return nplace;
	}

	/**
	 * @param nplace
	 *            the nplace to set
	 */
	public void setNplace(int nplace) {
		this.nplace = nplace;
	}

	/**
	 * @return the ntransition
	 */
	public int getNtransition() {
		return ntransition;
	}

	/**
	 * @param ntransition
	 *            the ntransition to set
	 */
	public void setNtransition(int ntransition) {
		this.ntransition = ntransition;
	}

	/**
	 * @return the narc
	 */
	public int getNarc() {
		return narc;
	}

	/**
	 * @param narc
	 *            the narc to set
	 */
	public void setNarc(int narc) {
		this.narc = narc;
	}

	/**
	 * @return the ndegree
	 */
	public int getNdegree() {
		return ndegree;
	}

	/**
	 * @param ndegree
	 *            the ndegree to set
	 */
	public void setNdegree(int ndegree) {
		this.ndegree = ndegree;
	}

	/**
	 * @return the petrinet_id
	 */
	public long getPetrinet_id() {
		return petrinet_id;
	}

	/**
	 * @param petrinetId
	 *            the petrinet_id to set
	 */
	public void setPetrinet_id(long petrinetId) {
		petrinet_id = petrinetId;
	}

	/**
	 * @return the process_id
	 */
	public long getProcess_id() {
		return process_id;
	}

	/**
	 * @param processId
	 *            the process_id to set
	 */
	public void setProcess_id(long processId) {
		process_id = processId;
	}

	/**
	 * @return the petriNet
	 */
	public PetriNet getPetriNet() {
		return petriNet;
	}
	
	/**
	 * @return the pnmlIn
	 */
	public InputStream getPnmlIn() {
		return pnmlIn;
	}
	
	/**
	 * @param pnmlIn
	 *            the pnmlIn to set
	 */
	public void setPnmlIn(InputStream pnmlIn) {
		this.pnmlIn = pnmlIn;
	}

	/**
	 * @param petriNet
	 *            the petriNet to set
	 */
	public void setPetriNet(PetriNet petriNet) {
		this.petriNet = petriNet;
	}

}
