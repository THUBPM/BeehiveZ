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

import java.util.HashSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;

import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.CompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.petrinetunfolding.OrderingRelation;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;

/**
 * @author Tao Jin
 * 
 * @date 2011-4-7
 * 
 */
public class PrintOrderingRelations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PetriNet pn = PetriNetUtil
				.getPetriNetFromPnmlFile("e:/test/wedding.pnml");
		CompleteFinitePrefix cfp = PetriNetUtil.buildCompleteFinitePrefix(pn);
		OrderingRelation[][] orderingRelations = cfp.getOrderingRelations();
		HashSet<String> causalRelations = new HashSet<String>();
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = 0; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.PRECEDENCE) {
					causalRelations.add(cfp.getTransitions().get(i)
							.getIdentifier()
							+ " -> "
							+ cfp.getTransitions().get(j).getIdentifier());
				}
			}
		}
		HashSet<String> conflictRelations = new HashSet<String>();
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = 0; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.CONFLICT) {
					conflictRelations.add(cfp.getTransitions().get(i)
							.getIdentifier()
							+ " ## "
							+ cfp.getTransitions().get(j).getIdentifier());
				}
			}
		}
		HashSet<String> concurrencyRelations = new HashSet<String>();
		for (int i = 0; i < orderingRelations.length; i++) {
			for (int j = 0; j < orderingRelations.length; j++) {
				if (orderingRelations[i][j] == OrderingRelation.CONCURRENCY) {
					concurrencyRelations.add(cfp.getTransitions().get(i)
							.getIdentifier()
							+ " || "
							+ cfp.getTransitions().get(j).getIdentifier());
				}
			}
		}

		System.out.println("the relation of causal");
		System.out.println(causalRelations.toString());
		System.out.println("the relation of confilct");
		System.out.println(conflictRelations.toString());
		System.out.println("the relation of concurrency");
		System.out.println(concurrencyRelations.toString());
	}

}
