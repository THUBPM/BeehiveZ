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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.TransitionCluster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * change PnmlReader from ProM
 * 
 * data operations are considered in Petri net. In addition to the PNML schema,
 * the following tags are supported as shown in the examples.
 * 
 * <data id="data_1">
 *   <name>x</name>
 *   <writtenby>trans_1</writtenby>
 *   <readby>trans_2</readby> 
 * </data>
 * 
 * @author Tao Jin
 * 
 * @date 2012-4-27
 * 
 */
public class DPnmlReader {

	private HashMap<String, Place> places;
	private HashMap<String, Transition> transitions;

	public DPnmlReader() {

	}

	public PetriNetWithData read(InputStream input) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		PetriNetWithData dpn = new PetriNetWithData();
		NodeList netNodes;

		dbf.setValidating(false);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);

		doc = dbf.newDocumentBuilder().parse(input);

		// check if root element is a <pnml> tag
		if (!doc.getDocumentElement().getTagName().equals("pnml")) {
			throw new Exception("pnml tag not found");
		}

		netNodes = doc.getDocumentElement().getElementsByTagName("net");
		if (netNodes.getLength() > 0) {
			parseNet(netNodes.item(0), dpn);
		}
		return dpn;
	}

	public PetriNetWithData read(Node node) throws Exception {
		PetriNetWithData result = new PetriNetWithData();
		parseNet(node, result);
		return result;
	}

	private void parseNet(Node node, PetriNetWithData dpn) throws Exception {
		Node id = node.getAttributes().getNamedItem("id");
		Node type = node.getAttributes().getNamedItem("type");

		// check id and type
		if (id == null || id.getNodeValue() == null) {
			throw new Exception("net tag is missing the id attribute");
		}
		if (type == null || type.getNodeValue() == null) {
			throw new Exception("net tag is missing the type attribute)");
		}

		places = new HashMap<String, Place>();
		transitions = new HashMap<String, Transition>();
		PetriNet pn = new PetriNet();

		parsePlaces(node, pn);
		parseTransitions(node, pn);
		parseArcs(node, pn);
		dpn.setPetriNet(pn);
		parseVariables(node, dpn);

		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("toolspecific")
					&& n.getAttributes().getNamedItem("tool").getNodeValue()
							.equals("ProM")) {
				foundToolSpecific();
				parseClusters(n, pn);
			}
		}
	}

	/**
	 * parseClusters
	 * 
	 */
	private void parseClusters(Node node, PetriNet net) {

		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("cluster")) {
				String name = n.getAttributes().getNamedItem("name")
						.getNodeValue();
				TransitionCluster tc = new TransitionCluster(name);
				NodeList trans = n.getChildNodes();
				for (int j = 0; j < trans.getLength(); j++) {
					if (trans.item(j).getNodeName().equals("trans")) {
						String transName = trans.item(j).getFirstChild()
								.getNodeValue();
						Transition t = (Transition) transitions.get(transName);
						tc.add(t);
					}
				}
				net.addCluster(tc);
			}
		}

		/*
		 * if ((net.getClusters()!=null) && (net.getClusters().size()>0)) { it =
		 * net.getClusters().iterator();
		 * bw.write("  <toolspecific tool=\"ProM\" version=\"" + About.VERSION +
		 * "\">\n"); while (it.hasNext()) { TransitionCluster tc =
		 * (TransitionCluster)it.next();
		 * bw.write("    <cluster name=\""+tc.getLabel()+"\">\n"); Iterator it2
		 * = tc.iterator(); while (it2.hasNext()) { Transition t = (Transition)
		 * it2.next();
		 * bw.write("      <trans>trans_"+t.getNumber()+"</trans>\n"); }
		 * bw.write("    </cluster>\n"); } bw.write("  </toolspecific>\n"); }
		 * return null; }
		 */
	}

	private void parseVariables(Node node, PetriNetWithData dpn)
			throws Exception {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("data")) {
				String name = "";
				HashSet<Transition> writtenBy = new HashSet<Transition>();
				HashSet<Transition> readBy = new HashSet<Transition>();
				for (int j = 0; j < n.getChildNodes().getLength(); j++) {
					Node n2 = n.getChildNodes().item(j);
					if (n2.getNodeName().equals("name")) {
						name = n2.getFirstChild().getNodeValue();
					} else if (n2.getNodeName().equals("writtenby")) {
						String tid = n2.getFirstChild().getNodeValue();
						Transition t = transitions.get(tid);
						writtenBy.add(t);
					} else if (n2.getNodeName().equals("readby")) {
						String tid = n2.getFirstChild().getNodeValue();
						Transition t = transitions.get(tid);
						readBy.add(t);
					}
				}

				Node nid = n.getAttributes().getNamedItem("id");
				String id = nid.getNodeValue();
				DataItem di = new DataItem();
				di.setId(id);
				di.setName(name);
				dpn.addVariable(di);
				dpn.addDataRead(di, readBy);
				dpn.addDataWritten(di, writtenBy);
			}
		}
	}

	private void parsePlaces(Node node, PetriNet net) throws Exception {
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("place")) {
				String name = "";
				int noTok = 0;

				for (int j = 0; j < n.getChildNodes().getLength(); j++) {
					Node n2 = n.getChildNodes().item(j);

					if (n2.getNodeName().equals("name")) {
						NodeList nameChildren = n2.getChildNodes();

						for (int k = 0; k < nameChildren.getLength(); k++) {
							Node gn = nameChildren.item(k);

							if (gn.getNodeName().equals("text")
									&& gn.hasChildNodes()) {
								name = gn.getFirstChild().getNodeValue();
							}
						}
					}
					if (n2.getNodeName().equals("initialMarking")) {
						NodeList nameChildren = n2.getChildNodes();

						for (int k = 0; k < nameChildren.getLength(); k++) {
							Node gn = nameChildren.item(k);

							if ((gn.getNodeName().equals("text") || gn
									.getNodeName().equals("value"))
									&& gn.hasChildNodes()) {
								noTok = Integer.parseInt(gn.getFirstChild()
										.getNodeValue());
							}
						}
					}
				}

				Node id = n.getAttributes().getNamedItem("id");
				Place p;

				if (id == null || id.getNodeValue() == null) {
					throw new Exception(
							"place tag is missing the id attribute)");
				}
				p = new Place(id.getNodeValue(), net);
				net.addPlace(p);
				places.put(id.getNodeValue(), p);
				if (!name.equals("")) {
					p.setIdentifier(name);
				}
				for (int j = 0; j < noTok; j++) {
					p.addToken(new Token());
				}
			}
		}
	}

	private void parseTransitions(Node node, PetriNet net) throws Exception {
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("transition")) {
				Node id = n.getAttributes().getNamedItem("id");
				Transition t;

				if (id == null || id.getNodeValue() == null) {
					throw new Exception(
							"transition tag is missing the id attribute)");
				}
				t = parseTrans(n, net);
				net.addTransition(t);
				transitions.put(id.getNodeValue(), t);
			}
		}
	}

	private LogEvent parseLogEvent(Node node) throws Exception {
		NodeList children = node.getChildNodes();
		String logeventName = null;
		String logeventType = null;
		String name = null;

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("logevent")) {
				NodeList logeventChildren = n.getChildNodes();

				for (int j = 0; j < logeventChildren.getLength(); j++) {
					Node gn = logeventChildren.item(j);

					if (gn.getNodeName().equals("name") && gn.hasChildNodes()) {
						logeventName = gn.getFirstChild().getNodeValue();
					}
					if (gn.getNodeName().equals("type") && gn.hasChildNodes()) {
						logeventType = gn.getFirstChild().getNodeValue();
					}
				}
			}
		}
		if (logeventName != null && logeventType != null) {
			return new LogEvent(logeventName, logeventType);
		} else {
			return null;
		}
	}

	private Transition parseTrans(Node node, PetriNet net) throws Exception {
		NodeList children = node.getChildNodes();
		LogEvent e = null;
		String name = null;
		String valueName = null;

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("name")) {
				NodeList nameChildren = n.getChildNodes();

				for (int j = 0; j < nameChildren.getLength(); j++) {
					Node gn = nameChildren.item(j);

					if ((gn.getNodeName().equals("text") || gn.getNodeName()
							.equals("value"))
							&& gn.hasChildNodes()) {
						name = gn.getFirstChild().getNodeValue();
					}

					if (gn.getNodeName().equals("value") && gn.hasChildNodes()) {
						valueName = gn.getFirstChild().getNodeValue();
					}
				}
			}

			if (n.getNodeName().equals("toolspecific")
					&& n.getAttributes().getNamedItem("tool").getNodeValue()
							.equals("ProM")) {
				foundToolSpecific();
				e = parseLogEvent(n);
			}
			if ((valueName != null) && (!valueName.equals(""))) {
				e = new LogEvent(name, "auto");
			}
		}

		if (e != null) {
			// we have enough info for a LogEvent
			Transition t = new Transition(e, net);
			if (name != null) {
				t.setIdentifier(name);
			}
			return t;
		} else if (name != null) {
			return new Transition(name, net);
		} else {
			// use id attribute of transition tag
			return new Transition(node.getAttributes().getNamedItem("id")
					.getNodeValue(), net);
		}
	}

	private void parseArcs(Node node, PetriNet net) throws Exception {
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			if (n.getNodeName().equals("arc")) {
				Node id = n.getAttributes().getNamedItem("id");
				Node source = n.getAttributes().getNamedItem("source");
				Node target = n.getAttributes().getNamedItem("target");

				if (id == null || id.getNodeValue() == null || source == null
						|| source.getNodeValue() == null || target == null
						|| target.getNodeValue() == null) {
					throw new Exception(
							"arc tag is missing id, source or target attribute)");
				}

				if (places.get(source.getNodeValue()) != null) {
					// from place to transition
					net
							.addEdge((Place) places.get(source.getNodeValue()),
									(Transition) transitions.get(target
											.getNodeValue()));
				} else {
					// from transition to place
					net.addEdge((Transition) transitions.get(source
							.getNodeValue()), (Place) places.get(target
							.getNodeValue()));
				}
			}
		}
	}

	protected void foundToolSpecific() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
