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
package cn.edu.thss.iise.beehivez.server.petrinetwithdata;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * consider data operation in Petri net. only reading and writing operations are
 * considered.
 * 
 * @author Tao Jin
 * 
 * @date 2012-4-27
 * 
 */
public class PetriNetWithData {

	private PetriNet pn;
	private ArrayList<DataItem> variables = new ArrayList<DataItem>();
	private HashMap<DataItem, HashSet<Transition>> dataWritten = new HashMap<DataItem, HashSet<Transition>>();
	private HashMap<DataItem, HashSet<Transition>> dataRead = new HashMap<DataItem, HashSet<Transition>>();

	public PetriNetWithData() {
	}

	public static PetriNetWithData readFromFile(File file) {
		PetriNetWithData dpn = null;
		try {
			FileInputStream in = new FileInputStream(file);
			DPnmlReader reader = new DPnmlReader();
			dpn = reader.read(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpn;
	}

	public static PetriNetWithData readFromFile(String fileName) {
		PetriNetWithData dpn = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			DPnmlReader reader = new DPnmlReader();
			dpn = reader.read(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpn;
	}

	public static void writeToFile(PetriNetWithData dpn, String filename) {
		if (filename != null && !filename.equals("")) {
			try {
				FileWriter fw = new FileWriter(filename, false);
				BufferedWriter bw = new BufferedWriter(fw);
				DPnmlWriter.write(false, true, dpn, bw);
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("wrong filename");
		}
	}

	public void addDataWritten(DataItem di, HashSet<Transition> ts) {
		dataWritten.put(di, ts);
	}

	public void addDataRead(DataItem di, HashSet<Transition> ts) {
		dataRead.put(di, ts);
	}

	public void addVariable(DataItem di) {
		variables.add(di);
	}

	/**
	 * @return the pn
	 */
	public PetriNet getPetriNet() {
		return pn;
	}

	/**
	 * @param pn
	 *            the pn to set
	 */
	public void setPetriNet(PetriNet pn) {
		this.pn = pn;
	}

	/**
	 * @return the dataWritten
	 */
	public HashMap<DataItem, HashSet<Transition>> getDataWritten() {
		return dataWritten;
	}

	/**
	 * @return the dataRead
	 */
	public HashMap<DataItem, HashSet<Transition>> getDataRead() {
		return dataRead;
	}

	/**
	 * @return the variables
	 */
	public ArrayList<DataItem> getVariables() {
		return variables;
	}

	/**
	 * @param variables the variables to set
	 */
	public void setVariables(ArrayList<DataItem> variables) {
		this.variables = variables;
	}

	/**
	 * @param dataWritten the dataWritten to set
	 */
	public void setDataWritten(HashMap<DataItem, HashSet<Transition>> dataWritten) {
		this.dataWritten = dataWritten;
	}

	/**
	 * @param dataRead the dataRead to set
	 */
	public void setDataRead(HashMap<DataItem, HashSet<Transition>> dataRead) {
		this.dataRead = dataRead;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
