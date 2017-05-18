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
package cn.edu.thss.iise.beehivez.server.metric.mcessimilarity;

import java.util.ArrayList;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

/**
 * The class is used for modular product of line graphs for Petri net. Task line
 * means the line between two tasks, and it is defined based on structure.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-19
 * 
 */
public class TaskLine4PetriNet {

	private Transition srcTransition = null;
	private Transition destTransition = null;

	public TaskLine4PetriNet(Transition tSrc, Transition tDest) {
		srcTransition = tSrc;
		destTransition = tDest;
	}

	public Transition getSrcTransition() {
		return srcTransition;
	}

	public Transition getDestTransition() {
		return destTransition;
	}

	@Override
	public String toString() {
		String str = "(tStrc:" + srcTransition.toString() + ", tDest:"
				+ destTransition.toString() + ")";
		return str;
	}

	public static ArrayList<TaskLine4PetriNet> getAllTaskLinesOfPetriNet(
			PetriNet pn) {
		ArrayList<TaskLine4PetriNet> result = new ArrayList<TaskLine4PetriNet>();
		// travel all the places in the given Petri net to obtain all the task
		// lines
		for (Place p : pn.getPlaces()) {
			Iterator<Transition> itPre = p.getPredecessors().iterator();
			while (itPre.hasNext()) {
				Transition tPre = itPre.next();
				Iterator<Transition> itSuc = p.getSuccessors().iterator();
				while (itSuc.hasNext()) {
					Transition tSuc = itSuc.next();
					TaskLine4PetriNet tl = new TaskLine4PetriNet(tPre, tSuc);
					result.add(tl);
				}
			}
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
