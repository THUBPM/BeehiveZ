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

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.processmining.converting.*;
import org.processmining.exporting.petrinet.*;
import org.processmining.framework.log.*;
import org.processmining.framework.models.*;
import org.processmining.framework.models.epcpack.*;
import org.processmining.framework.models.epcpack.algorithms.*;
import org.processmining.framework.models.petrinet.*;
import org.processmining.framework.plugin.*;
import org.processmining.framework.ui.*;
import org.processmining.mining.*;
import org.processmining.mining.epcmining.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.processmining.importing.epml.EpmlImport;

/**
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 *
 */
public class AMLtoPNML {

	private static Hashtable<String, String> htDict = new Hashtable<String, String>();

	public AMLtoPNML() {
	}

	public static void loadDict(String strDict) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(strDict));
			String line = null;
			while ((line = br.readLine()) != null) {
				int idx = line.indexOf(',');
				if (idx > 0) {
					htDict.put(line.substring(0, idx), line.substring(idx + 1));
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace(System.out);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}

	}

	public static MiningResult importFile(InputStream input) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc;
			// NodeList netNodes;
			dbf.setValidating(false);
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			// dbf.setExpandEntityReferences(false);
			// dbf.setNamespaceAware(false);

			DocumentBuilder db = dbf.newDocumentBuilder();

			db.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) {
					if (systemId.indexOf("ARIS-Export") != -1) {
						return new InputSource("file:" + About.EXTLIBLOCATION()
								+ "ARIS-Export101.dtd");
					} else {
						return null;
					}
				}
			});

			InputSource inpStream = new InputSource(input);
			inpStream.setSystemId("file:" + System.getProperty("user.dir", ""));
			doc = db.parse(inpStream);

			// check if root element is a aml tag
			Message.add("parsing done" + doc, Message.DEBUG);
			if (!(doc.getDocumentElement().getNodeName().equals("AML"))) {
				Message.add("aml tag not found", Message.ERROR);
				throw new Exception("aml tag not found");
			} else {
				Message.add("aml root element found");
			}

			EPCResult result = new EPCResult(null, (EPC) null);
			HashMap ObjDef_LinkId = new HashMap();
			HashMap modelid_net = new HashMap();
			HashMap ObjDef_Name = new HashMap();
			HashMap function_LinkId = new HashMap();
			HashMap ModelId_ModelType = new HashMap();
			traverseAMLforObjectNames(ObjDef_Name, doc.getDocumentElement(),
					ObjDef_LinkId, ModelId_ModelType);
			Iterator findLinkToEpc = ObjDef_LinkId.keySet().iterator();
			while (findLinkToEpc.hasNext()) {
				String currentObjDef = (String) findLinkToEpc.next();
				String Links = (String) ObjDef_LinkId.get(currentObjDef);
				StringTokenizer linkSet = new StringTokenizer(Links);
				String realEpcLink = "";
				while (linkSet.hasMoreTokens()) {
					String currentLink = linkSet.nextToken();
					if (ModelId_ModelType.get(currentLink).equals("MT_EEPC")) {
						realEpcLink = currentLink;
						break;
					}
				}
				if (realEpcLink.equals(" ")) {
					ObjDef_LinkId.remove(currentObjDef);
				} else {
					ObjDef_LinkId.put(currentObjDef, realEpcLink);
				}
			}
			result = traverseAML(result, doc.getDocumentElement(), null,
					ObjDef_Name, ObjDef_LinkId, modelid_net, function_LinkId);
			Iterator hierarchicalFunctions = function_LinkId.keySet()
					.iterator();
			while (hierarchicalFunctions.hasNext()) {
				EPCSubstFunction f = (EPCSubstFunction) hierarchicalFunctions
						.next();
				f.setSubstitutedEPC((EPC) modelid_net.get(function_LinkId
						.get(f)));
				// Message.add(f.getSubstitutedEPC().getName());
			}

			return result;

		} catch (Throwable x) {
			Message.add(x.toString());
			throw new IOException(x.getMessage());
		}
	}

	public static EPCResult traverseAML(EPCResult partialResult,
			Node currentNode, Object parent, HashMap ObjDef_Name,
			HashMap ObjDef_LinkId, HashMap modelid_net, HashMap function_LinkId)
			throws Exception {
		if (currentNode.hasChildNodes()) {
			for (int i = 0; i < currentNode.getChildNodes().getLength(); i++) {
				Node currentChild = currentNode.getChildNodes().item(i);
				if (currentChild.getNodeName().equals("Group")) {
					String id = currentChild.getAttributes().getNamedItem(
							"Group.ID").getNodeValue();
					String GroupName = "";
					if (currentChild.hasChildNodes()) {
						NodeList currentChildren = currentChild.getChildNodes();
						for (int j = 0; j < currentChildren.getLength(); j++) {
							Node Child = currentChildren.item(j);
							if (!(Child.getNodeName().equals("AttrDef"))) {
								continue;
							}
							if (Child.getAttributes().getNamedItem(
									"AttrDef.Type").getNodeValue().equals(
									"AT_NAME")) {
								if (Child.hasChildNodes()) {
									for (int l = 0; l < Child.getChildNodes()
											.getLength(); l++) {
										if (!(Child.getChildNodes().item(l)
												.getNodeName()
												.equals("AttrValue"))) {
											continue;
										} else {
											GroupName = getTextContent(Child
													.getChildNodes().item(l));
										}
									}
									break;
								}
							}
						}
						if (GroupName.equals("")) {
							GroupName = id;
						}
					}
					ModelHierarchyDirectory dir = new ModelHierarchyDirectory(
							id, GroupName);
					partialResult.addInHierarchy(dir, parent, GroupName);
					partialResult = traverseAML(partialResult, currentChild,
							dir, ObjDef_Name, ObjDef_LinkId, modelid_net,
							function_LinkId);
				}
				if (currentChild.getNodeName().equals("Model")
						&& currentChild.getAttributes().getNamedItem(
								"Model.Type").getNodeValue().equals("MT_EEPC")) {
					String ModelName = "gaga";
					if (currentChild.hasChildNodes()) {
						NodeList currentChildren = currentChild.getChildNodes();
						for (int j = 0; j < currentChildren.getLength(); j++) {
							Node Child = currentChildren.item(j);
							if (!(Child.getNodeName().equals("AttrDef"))) {
								continue;
							}
							if (Child.getAttributes().getNamedItem(
									"AttrDef.Type").getNodeValue().equals(
									"AT_NAME")) {
								if (Child.hasChildNodes()) {
									for (int l = 0; l < Child.getChildNodes()
											.getLength(); l++) {
										if (!(Child.getChildNodes().item(l)
												.getNodeName()
												.equals("AttrValue"))) {
											continue;
										} else {
											ModelName = getTextContent(Child
													.getChildNodes().item(l));
										}
									}
								}
								break;
							}
						}
					}
					try {
						ModelName = ModelName.replaceAll("\n", " ");
						EPC net = read(currentChild, ObjDef_Name,
								ObjDef_LinkId, function_LinkId, ModelName);
						partialResult.addInHierarchy(net, parent, ModelName);
						modelid_net.put(currentChild.getAttributes()
								.getNamedItem("Model.ID").getNodeValue(), net);
					} catch (Throwable x) {
						Message.add(x.getClass().getName());
						// throw new IOException(x.getMessage());
					}
				}
			}
		}
		return partialResult;
	}

	private static void parseNet(Node node, EPC net, HashMap ObjDef_Name,
			HashMap ObjDef_LinkId, HashMap function_LinkId, String ModelName)
			throws Exception {
		HashMap mapping = new HashMap();

		// read all nodes
		NodeList nodes = node.getChildNodes();
		net.setIdentifier(ModelName);
		// Message.add("here I am still happy");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (!n.getNodeName().equals("ObjOcc")) {
				continue;
			}
			if (n.getAttributes().getNamedItem("SymbolNum") == null) {
				continue;
			}
			String symbolnum = n.getAttributes().getNamedItem("SymbolNum")
					.getNodeValue();
			if (!(symbolnum.equals("ST_FUNC") || symbolnum.equals("ST_EV")
					|| symbolnum.equals("ST_OPR_AND_1")
					|| symbolnum.equals("ST_OPR_OR_1") || symbolnum
					.equals("ST_OPR_XOR_1"))) {
				continue;
			}

			String ObjDef = n.getAttributes().getNamedItem("ObjDef.IdRef")
					.getNodeValue();
			String ownName = (String) ObjDef_Name.get(ObjDef);
			// Message.add("YES " +
			// n.getAttributes().getNamedItem("ObjOcc.ID").getNodeValue());
			String id = n.getAttributes().getNamedItem("ObjOcc.ID")
					.getNodeValue();
			// Message.add(id + " " + ownName);
			if (ownName == null || ownName == "") {
				ownName = id.substring(7, 11);
			}
			if (symbolnum.equals("ST_FUNC")) {
				if (ObjDef_LinkId.containsKey(ObjDef)) {
					EPCSubstFunction sf = (EPCSubstFunction) net
							.addFunction(new EPCSubstFunction(new LogEvent(
									ownName, "unknown:normal"), net, null));
					sf.setIdentifier(ownName);
					mapping.put(id, sf);
					function_LinkId.put(sf, ObjDef_LinkId.get(ObjDef));
				} else {
					EPCFunction f = net.addFunction(new EPCFunction(
							new LogEvent(ownName, "unknown:normal"), net));
					f.setIdentifier(ownName);
					mapping.put(id, f);
				}
			} else if (symbolnum.equals("ST_EV")) {
				EPCEvent e = net.addEvent(new EPCEvent(ownName, net));
				e.setIdentifier(ownName);
				mapping.put(id, e);
			} else if (symbolnum.equals("ST_OPR_AND_1")) {
				EPCConnector c = net.addConnector(new EPCConnector(
						EPCConnector.AND, net));
				mapping.put(id, c);
			} else if (symbolnum.equals("ST_OPR_OR_1")) {
				// EPCConnector c = net.addConnector(new
				// EPCConnector(EPCConnector.OR, net));
				EPCConnector c = net.addConnector(new EPCConnector(
						EPCConnector.AND, net));
				mapping.put(id, c);
			} else if (symbolnum.equals("ST_OPR_XOR_1")) {
				EPCConnector c = net.addConnector(new EPCConnector(
						EPCConnector.XOR, net));
				mapping.put(id, c);
			}
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (!n.getNodeName().equals("ObjOcc")) {
				continue;
			}
			if (n.getAttributes().getNamedItem("SymbolNum") == null) {
				continue;
			}
			String symbolnum = n.getAttributes().getNamedItem("SymbolNum")
					.getNodeValue();
			if (!(symbolnum.equals("ST_FUNC") || symbolnum.equals("ST_EV")
					|| symbolnum.equals("ST_OPR_AND_1")
					|| symbolnum.equals("ST_OPR_OR_1") || symbolnum
					.equals("ST_OPR_XOR_1"))) {
				continue;
			}
			String source = n.getAttributes().getNamedItem("ObjOcc.ID")
					.getNodeValue();
			if (n.hasChildNodes()) {
				for (int j = 0; j < n.getChildNodes().getLength(); j++) {
					if (n.getChildNodes().item(j).getNodeName()
							.equals("CxnOcc")) {
						Node CxnOcc = n.getChildNodes().item(j);
						String dest = CxnOcc.getAttributes().getNamedItem(
								"ToObjOcc.IdRef").getNodeValue();
						if (mapping.get(dest) == null) {
							continue;
						}
						if (net.addEdge((EPCObject) mapping.get(source),
								(EPCObject) mapping.get(dest)) == null) {
							throw (new Exception(
									"<html>Structural properties of EPCs are violated in input file.<br>"
											+ "The following edge could not be added:<br><br>"
											+ mapping.get(source).toString()
											+ " ==> "
											+ mapping.get(dest).toString()
											+ "<br><br>Import aborted.</html>"));
						}
					}
				}
			}
		}
	}

	public static EPC read(Node node, HashMap ObjDef_Name,
			HashMap ObjDef_LinkId, HashMap function_LinkId, String ModelName)
			throws Exception {
		EPC result = new EPC(true);
		// Message.add("1.read " +
		// node.getAttributes().getNamedItem("Model.ID").getNodeValue());
		parseNet(node, result, ObjDef_Name, ObjDef_LinkId, function_LinkId,
				ModelName);
		// Message.add("2.read " + node.getNodeName());
		return result;
	}

	public static void traverseAMLforObjectNames(HashMap partialMap,
			Node currentNode, HashMap ObjDef_LinkId, HashMap ModelId_ModelType) {
		if (currentNode.hasChildNodes()) {
			for (int i = 0; i < currentNode.getChildNodes().getLength(); i++) {
				Node currentChild = currentNode.getChildNodes().item(i);
				if (currentChild.getNodeName().equals("Group")) {
					traverseAMLforObjectNames(partialMap, currentChild,
							ObjDef_LinkId, ModelId_ModelType);
				}
				if (currentChild.getNodeName().equals("Model")) {
					if (currentChild.hasAttributes()) {
						String mid = currentChild.getAttributes().getNamedItem(
								"Model.ID").getNodeValue();
						String type = currentChild.getAttributes()
								.getNamedItem("Model.Type").getNodeValue();
						ModelId_ModelType.put(mid, type);
					}
					// traverseAMLforObjectNames(partialMap, currentChild,
					// ObjDef_LinkId);
				}
				if (currentChild.getNodeName().equals("ObjDef")) {
					String id = currentChild.getAttributes().getNamedItem(
							"ObjDef.ID").getNodeValue();
					NodeList currentChildren = currentChild.getChildNodes();
					String ObjName = "";
					for (int k = 0; k < currentChildren.getLength(); k++) {
						Node Child = currentChildren.item(k);
						if (!Child.getNodeName().equals("AttrDef")) {
							continue;
						} else if (!Child.getAttributes().getNamedItem(
								"AttrDef.Type").getNodeValue()
								.equals("AT_NAME")) {
							continue;
						} else if (Child.hasChildNodes()) {
							for (int l = 0; l < Child.getChildNodes()
									.getLength(); l++) {
								if (!(Child.getChildNodes().item(l)
										.getNodeName().equals("AttrValue"))) {
									continue;
								} else {
									ObjName = getTextContent(Child
											.getChildNodes().item(l));
									ObjName = ObjName.replaceAll("\n", "\\\\n");
									break;
								}
							}
						}
					}
					partialMap.put(id, ObjName);
					for (int j = 0; j < currentChild.getAttributes()
							.getLength(); j++) {
						if (currentChild.getAttributes().item(j).getNodeName()
								.equals("LinkedModels.IdRefs")) {
							String links = currentChild.getAttributes()
									.getNamedItem("LinkedModels.IdRefs")
									.getNodeValue();
							/*
							 * if (links.indexOf(" ") > -1) {
							 * Message.add("yes, yes, yes"); links =
							 * links.substring(0, links.indexOf(" ")); }
							 */
							ObjDef_LinkId.put(id, links);
						}
					}
				}
			}
		}
	}

	private static String getTextContent(Node n) {
		NodeList nodeList = n.getChildNodes();
		String textContent = "";
		for (int j = 0; j < nodeList.getLength(); j++) {
			Node k = nodeList.item(j);
			if (k.getNodeType() == Node.TEXT_NODE) {
				textContent = k.getNodeValue();
				break;
			}
		}

		return textContent;
	}

	public static PetriNet convert(ConfigurableEPC baseEPC) {
		HashMap<EPCFunction, Transition> functionActivityMapping;
		HashMap<EPCConnector, Place> xorconnectorChoiceMapping;

		// HV: Initialize the mappings.
		functionActivityMapping = new HashMap<EPCFunction, Transition>();
		xorconnectorChoiceMapping = new HashMap<EPCConnector, Place>();

		// Check to use the weights if necessary
		// HV: Add both mappings. On completion, these will be filledd.
		PetriNet petrinet = EPCToPetriNetConverter.convert(baseEPC,
				new HashMap(), functionActivityMapping,
				xorconnectorChoiceMapping);

		HashSet visible = new HashSet();

		// HV: The next block is taken care of by the functionActivityMapping
		// below.
		/*
		 * Iterator it = petrinet.getTransitions().iterator(); while
		 * (it.hasNext()) { Transition t = (Transition) it.next(); if (t.object
		 * instanceof EPCFunction) { // if (t.getLogEvent() != null) { // Add
		 * transitions with LogEvent (i.e. referring to functions)
		 * visible.add(t); } }
		 */

		// HV: Prevent the places mapped onto from being reduced.
		visible.addAll(functionActivityMapping.values());
		visible.addAll(xorconnectorChoiceMapping.values());
		Message.add(visible.toString(), Message.DEBUG);

		Iterator it = petrinet.getPlaces().iterator();
		while (it.hasNext()) {
			Place p = (Place) it.next();
			if (p.inDegree() * p.outDegree() == 0) {
				// Add Initial and final places to visible, i.e. places that
				// refer to in and output events
				visible.add(p);
			}
		}

		// Reduce the PetriNet with Murata rules, while keeping the visible ones
		PetriNetReduction pnred = new PetriNetReduction();
		pnred.setNonReducableNodes(visible);

		HashMap pnMap = new HashMap(); // Used to map pre-reduction nodes to
		// post-reduction nodes.
		PetriNet reduced = pnred.reduce(petrinet, pnMap);

		if (reduced != petrinet) {
			// Update both mappings from pre-reduction nodes to post-reduction
			// nodes.
			HashMap<EPCFunction, Transition> newFunctionActivityMapping = new HashMap<EPCFunction, Transition>();
			for (EPCFunction function : functionActivityMapping.keySet()) {
				Transition transition = (Transition) functionActivityMapping
						.get(function);
				if (pnMap.keySet().contains(transition)) {
					newFunctionActivityMapping.put(function, (Transition) pnMap
							.get(transition));
				}
			}
			functionActivityMapping = newFunctionActivityMapping;
			HashMap<EPCConnector, Place> newXorconnectorChoiceMapping = new HashMap<EPCConnector, Place>();
			for (EPCConnector connector : xorconnectorChoiceMapping.keySet()) {
				Place place = (Place) xorconnectorChoiceMapping.get(connector);
				if (pnMap.keySet().contains(place)) {
					newXorconnectorChoiceMapping.put(connector, (Place) pnMap
							.get(place));
				}
			}
			xorconnectorChoiceMapping = newXorconnectorChoiceMapping;
		}
		reduced.makeClusters();

		// filter the \nunknown:normal
		ArrayList<Transition> alTrans = reduced.getVisibleTasks();
		for (int i = 0; i < alTrans.size(); i++) {
			Transition t = alTrans.get(i);
			String id = t.getIdentifier();
			int idx = id.indexOf("\\nunknown:normal");
			if (idx > 0) {
				id = id.substring(0, idx);
			}
			// �˴������ֵ��ѯ�滻���е�label
			String mappedId = htDict.get(id);
			if (mappedId != null) {
				t.setIdentifier(mappedId);
			} else {
				t.setIdentifier(id);
			}
		}

		return reduced;
	}

	public static void main(String[] args) {
		AML2PNML(args);
		// EPML2PNML(args);

		System.exit(0);
	}

	/**
	 * EPML2PNML
	 * 
	 * @param args
	 *            String[]
	 */
	private static void EPML2PNML(String[] args) {
		if (args.length != 2) {
			System.out.println("���ṩEPML�ļ�·����PNML���Ŀ¼!");
			System.exit(-1);
		}
		EpmlImport epml = new EpmlImport();
		// load the single epml file
		String filename = args[0];
		try {
			EPCResult epcRes = (EPCResult) epml.importFile(new FileInputStream(
					filename));
			// convert all epc models to pnml files
			ArrayList<ConfigurableEPC> alEPCs = epcRes.getAllEPCs();
			PnmlExport export = new PnmlExport();
			for (ConfigurableEPC epc : alEPCs) {
				String id = epc.getIdentifier();
				if (id.equals("1An_klol") || id.equals("1An_l1y8")
						|| id.equals("1Ex_dzq9") || id.equals("1Ex_e6dx")
						|| id.equals("1Ku_9soy") || id.equals("1Ku_a4cg")
						|| id.equals("1Or_lojl") || id.equals("1Pr_d1ur")
						|| id.equals("1Pr_djki") || id.equals("1Pr_dkfa")
						|| id.equals("1Pr_dl73") || id.equals("1Ve_musj")
						|| id.equals("1Ve_mvwz"))
					continue;
				// save pnml files to the same folder
				File outFile = new File(args[1] + "/" + id + ".pnml");
				if (outFile.exists())
					continue;
				FileOutputStream fos = new FileOutputStream(outFile);
				PetriNet petri = AMLtoPNML.convert(epc);
				try {
					export.export(new ProvidedObject("PetriNet",
							new Object[] { petri }), fos);
				} catch (Exception ex1) {
					ex1.printStackTrace(System.out);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
		}

		System.out.println("EPML Conversion Done");
	}

	private static void AML2PNML(String[] args) {
		if (args.length != 3) {
			System.out.println("���ṩAML����Ŀ¼��PNML���Ŀ¼�Լ��ֵ��ļ�!");
			System.exit(-1);
		}
		System.out.println("����Ŀ¼��" + args[0]);
		System.out.println("���Ŀ¼��" + args[1]);
		System.out.println("�ֵ��ļ���" + args[2]);
		// load the dict
		AMLtoPNML.loadDict(args[2]);
		File srcDir = new File(args[0]);
		File[] lstAML = srcDir.listFiles();
		PnmlExport export = new PnmlExport();
		for (int i = 0; i < lstAML.length; i++) {
			if (lstAML[i].isDirectory()) {
				continue;
			}
			System.out.print(lstAML[i].getName() + "==>");
			try {
				FileInputStream fis = new FileInputStream(lstAML[i]);
				int idx = lstAML[i].getName().indexOf(".xml");
				File outFile = new File(args[1] + "/"
						+ lstAML[i].getName().substring(0, idx) + ".pnml");
				FileOutputStream fos = new FileOutputStream(outFile);
				EPCResult epcRes = (EPCResult) AMLtoPNML.importFile(fis);
				ConfigurableEPC epc = epcRes.getMainEPC();
				PetriNet petri = AMLtoPNML.convert(epc);
				export.export(new ProvidedObject("PetriNet",
						new Object[] { petri }), fos);
				System.out.println(outFile.getName());
			} catch (FileNotFoundException ex) {
				ex.printStackTrace(System.out);
			} catch (IOException ioe) {
				ioe.printStackTrace(System.out);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		System.out.println("Conversion Done");
	}
}
