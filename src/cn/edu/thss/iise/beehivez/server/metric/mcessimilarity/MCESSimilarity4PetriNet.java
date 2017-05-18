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

import org.processmining.framework.models.petrinet.PetriNet;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * given two petri nets, calculate the distance and the similarity based on
 * maximum common edge subgraph.
 * 
 * the similarity is related to the distance.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-15
 * 
 */
public class MCESSimilarity4PetriNet {

	/**
	 * given two petri nets, compute the similarity based on maximum common edge
	 * subgraph
	 * 
	 * @param pn1
	 *            the petri net 1
	 * @param pn2
	 *            the petri net 2
	 * @return the similarity ranges between 0 and 1
	 */
	public static float similarity(PetriNet pn1, PetriNet pn2) {
		float result = 0;

		ModularProductOfLineGraphs4PetriNet mpg = new ModularProductOfLineGraphs4PetriNet(
				pn1, pn2);

		int numerator = mpg.getNumberOfVerticesInvolvedInBiggestClique();
		int ntls1 = mpg.getNumberOfTaskLinesOfPN1();
		int ntls2 = mpg.getNumberOfTaskLinesOfPN2();
		int denominator = ntls1 > ntls2 ? ntls2 : ntls1;

		result = (float) numerator / (float) denominator;

		return result;
	}

	/**
	 * given two petri nets, compute the distance based on maximum common edge
	 * subgraph
	 * 
	 * @param pn1
	 *            the petri net 1
	 * @param pn2
	 *            the petri net 2
	 * @return the distance
	 */
	public static float distance(PetriNet pn1, PetriNet pn2) {
		return 1 - similarity(pn1, pn2);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test
		PetriNet pn1 = PetriNetUtil.getPetriNetFromPnmlFile("e:/test/a.pnml");
		PetriNet pn2 = PetriNetUtil.getPetriNetFromPnmlFile("e:/test/b.pnml");
		PetriNet pn3 = PetriNetUtil.getPetriNetFromPnmlFile("e:/test/c.pnml");

		long s = System.currentTimeMillis();
		float sim = MCESSimilarity4PetriNet.similarity(pn1, pn2);
		long e = System.currentTimeMillis();
		System.out.println("the similarity between pn1 and pn2 is: " + sim);
		System.out.println("time cose: " + (e - s) + " ms");

		s = System.currentTimeMillis();
		sim = MCESSimilarity4PetriNet.similarity(pn1, pn1);
		e = System.currentTimeMillis();
		System.out.println();
		System.out.println("the similarity between pn1 and pn1 is: " + sim);
		System.out.println("time cose: " + (e - s) + " ms");

		s = System.currentTimeMillis();
		sim = MCESSimilarity4PetriNet.similarity(pn1, pn3);
		e = System.currentTimeMillis();
		System.out.println();
		System.out.println("the similarity between pn1 and pn3 is: " + sim);
		System.out.println("time cose: " + (e - s) + " ms");

		s = System.currentTimeMillis();
		sim = MCESSimilarity4PetriNet.similarity(pn2, pn3);
		e = System.currentTimeMillis();
		System.out.println();
		System.out.println("the similarity between pn2 and pn3 is: " + sim);
		System.out.println("time cose: " + (e - s) + " ms");
	}

}
