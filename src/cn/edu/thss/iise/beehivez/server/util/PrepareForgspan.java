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

import java.io.FileOutputStream;
import java.util.Vector;

import de.parsemis.graph.ListGraph;
import de.parsemis.graph.Node;
import de.parsemis.graph.Edge;
import de.parsemis.parsers.GraphmlParser;
import de.parsemis.parsers.StringLabelParser;

/**
 * @author Tao Jin
 * 
 * @date 2011-3-11
 * 
 */
public class PrepareForgspan {

	private static void test() {
		Vector v = new Vector();

		// graph 1
		ListGraph<String, String> graph1 = new ListGraph<String, String>("g1");
		Node<String, String> node1_1 = graph1.addNode("1");
		Node<String, String> node1_2 = graph1.addNode("2");
		Node<String, String> node1_3 = graph1.addNode("3");
		graph1.addEdge(node1_1, node1_2, "", Edge.OUTGOING);
		graph1.addEdge(node1_1, node1_3, "", Edge.OUTGOING);
		v.add(graph1);

		// graph 2
		ListGraph<String, String> graph2 = new ListGraph<String, String>("g2");
		Node<String, String> node2_1 = graph2.addNode("1");
		Node<String, String> node2_2 = graph2.addNode("2");
		Node<String, String> node2_4 = graph2.addNode("4");
		graph2.addEdge(node2_1, node2_2, "", Edge.OUTGOING);
		graph2.addEdge(node2_1, node2_4, "", Edge.OUTGOING);
		v.add(graph2);

		// graph3
		ListGraph<String, String> graph3 = new ListGraph<String, String>("g3");
		Node<String, String> node3_1 = graph3.addNode("1");
		Node<String, String> node3_2 = graph3.addNode("2");
		Node<String, String> node3_3 = graph3.addNode("3");
		graph3.addEdge(node3_1, node3_2, "", Edge.OUTGOING);
		graph3.addEdge(node3_1, node3_3, "", Edge.OUTGOING);
		graph3.addEdge(node3_2, node3_3, "", Edge.OUTGOING);
		v.add(graph3);

		StringLabelParser lp = new StringLabelParser();
		GraphmlParser<String, String> parser = new GraphmlParser<String, String>(
				lp, lp);
		try {
			FileOutputStream fos = new FileOutputStream("e:/test/example.gml");
			parser.serialize(fos, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
