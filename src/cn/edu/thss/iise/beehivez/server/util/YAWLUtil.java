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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import org.jdom.Element;
import org.jdom.Namespace;
import org.processmining.converting.WFNetToYAWL;
import org.processmining.converting.yawl2pn.YawlToPetriNet;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.yawl.YAWLModel;
import org.processmining.framework.models.yawl.algorithms.YAWLReader;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.unmarshal.YMarshal;

import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author Tao Jin
 * 
 */
public class YAWLUtil {

	// if cannot map every element in sub to sup, return false;
	public static boolean initializeMap(boolean[][] elementMapMatrix,
			ArrayList<YExternalNetElement> subElements,
			ArrayList<YExternalNetElement> supElements) {

		for (int i = 0; i < subElements.size(); i++) {
			boolean flagMap = false;
			for (int j = 0; j < supElements.size(); j++) {
				if (canElementMap(subElements.get(i), supElements.get(j))) {
					elementMapMatrix[i][j] = true;
					flagMap = true;
				}
			}
			if (!flagMap) {
				return false;
			}
		}
		return true;
	}

	// can conditions map according to their input and output tasks,
	// there maybe some tasks mapped into the same task
	private static boolean canConditionMap(YCondition cSub, YCondition cSup) {

		YExternalNetElement[] subPreElements = cSub.getPresetElements()
				.toArray(new YExternalNetElement[0]);
		YExternalNetElement[] supPreElements = cSup.getPresetElements()
				.toArray(new YExternalNetElement[0]);
		YExternalNetElement[] subPostElements = cSub.getPostsetElements()
				.toArray(new YExternalNetElement[0]);
		YExternalNetElement[] supPostElements = cSup.getPostsetElements()
				.toArray(new YExternalNetElement[0]);

		if (subPreElements.length > supPreElements.length
				|| subPostElements.length > supPostElements.length) {
			return false;
		}

		// deal with the input elements
		if (subPreElements.length > 0) {
			boolean[][] preMap = new boolean[subPreElements.length][supPreElements.length];
			for (int i = 0; i < subPreElements.length; i++) {
				for (int j = 0; j < supPreElements.length; j++) {
					if (canElementMap(subPreElements[i], supPreElements[j])) {
						preMap[i][j] = true;
					} else {
						preMap[i][j] = false;
					}
				}
			}
			if (!ToolKit.existOneOneMap(preMap)) {
				return false;
			}
		}

		// deal with the output elements
		if (subPostElements.length > 0) {
			boolean[][] postMap = new boolean[subPostElements.length][supPostElements.length];
			for (int i = 0; i < subPostElements.length; i++) {
				for (int j = 0; j < supPostElements.length; j++) {
					if (canElementMap(subPostElements[i], supPostElements[j])) {
						postMap[i][j] = true;
					} else {
						postMap[i][j] = false;
					}
				}
			}
			if (!ToolKit.existOneOneMap(postMap)) {
				return false;
			}
		}

		return true;
	}

	// can tasks map according to their name and their in degrees and out
	// degrees and also the type of join and split,
	// the data perspective and resource perspective are also considered.
	private static boolean canTaskMap(YTask tSub, YTask tSup) {

		// control-flow perspective
		String tSubLabel = tSub.getName();
		String tSupLabel = tSup.getName();

		boolean flag = false;

		if (tSubLabel.equals(tSupLabel)) {
			flag = true;
		}

		if (!flag && GlobalParameter.isEnableSimilarLabel()) {
			if (StringSimilarityUtil.semanticSimilarity(tSubLabel, tSupLabel) >= GlobalParameter
					.getLabelSemanticSimilarity()) {
				flag = true;
			}
		}

		if (!flag) {
			return false;
		}
		if (tSub.getPresetFlows().size() > tSup.getPresetFlows().size()
				|| tSub.getPostsetFlows().size() > tSup.getPostsetFlows()
						.size()) {
			return false;
		}

		if (tSub.getJoinType() != tSup.getJoinType()
				|| tSub.getSplitType() != tSup.getSplitType()) {
			return false;
		}

		// data perspective
		// data read
		String[] subReadData = tSub.getParamNamesForTaskStarting().toArray(
				new String[0]);
		String[] supReadData = tSup.getParamNamesForTaskStarting().toArray(
				new String[0]);
		if (!ToolKit.strSubSet(subReadData, supReadData)) {
			return false;
		}
		// data write
		String[] subWriteData = tSub.getParamNamesForTaskCompletion().toArray(
				new String[0]);
		String[] supWriteData = tSup.getParamNamesForTaskCompletion().toArray(
				new String[0]);
		if (!ToolKit.strSubSet(subWriteData, supWriteData)) {
			return false;
		}

		// resource perspective
		String[] subRoles = YAWLUtil.getOfferRoleNamesForYTask(tSub);
		String[] supRoles = YAWLUtil.getOfferRoleNamesForYTask(tSup);
		if (!ToolKit.strSubSet(subRoles, supRoles)) {
			return false;
		}
		return true;
	}

	// can elements map
	public static boolean canElementMap(YExternalNetElement eSub,
			YExternalNetElement eSup) {
		if (eSub instanceof YTask && eSup instanceof YTask) {
			return canTaskMap((YTask) eSub, (YTask) eSup);
		} else if (eSub instanceof YCondition && eSup instanceof YCondition) {
			return canConditionMap((YCondition) eSub, (YCondition) eSup);
		} else {
			return false;
		}
	}

	public static YAWLModel PetriNet2YAWL(PetriNet pn) {
		WFNetToYAWL conventor = new WFNetToYAWL();
		return conventor.convert(pn);
	}

	public static YAWLModel readYAWL(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			YAWLModel model = YAWLReader.read(fis);
			fis.close();
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeYAWL(YAWLModel ymodel, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			String export = ymodel.writeToYAWL(bw);
			bw.write(export);
			bw.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PetriNet convert2PetriNet(byte[] definition) {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(definition);
			YAWLModel model = YAWLReader.read(input);
			return YawlToPetriNet.convert(model).getPetriNet();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PetriNet convert2PetriNet(YNet net) {
		return convert2PetriNet(getYNetDefinitionBytes(net));
	}

	public static HashSet<YFlow> getNetFlows(YNet net) {
		HashSet<YFlow> ret = new HashSet<YFlow>();
		for (YExternalNetElement element : net.getNetElements().values()) {
			ret.addAll(element.getPresetFlows());
			ret.addAll(element.getPostsetFlows());
		}
		return ret;
	}

	public static void exportEngineSpecificationToFile(String fullFileName,
			YSpecification specification) {
		try {
			PrintStream outputStream = new PrintStream(
					new BufferedOutputStream(new FileOutputStream(fullFileName)),
					false, "UTF-8");
			outputStream.println(getEngineSpecificationXML(specification));

			outputStream.close();
			// System.out.println("file successfully exported to:" +
			// fullFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getEngineSpecificationXML(YSpecification specification) {
		try {
			return YMarshal.marshal(specification);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] getYNetDefinitionBytes(YNet net) {
		return getEngineSpecificationXML(net.getSpecification()).getBytes();
	}

	public static YSpecification importEngineSpecificationFromFile(
			String fullFileName) {
		String specXml = getEngineSpecificationXMLFromFile(fullFileName);
		try {
			List<YSpecification> specifications = YMarshal
					.unmarshalSpecifications(specXml, false);
			// Engine currently only supplies a single specification per file.
			return specifications.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String[] getOfferRoleNamesForYTask(YTask task) {
		ArrayList<String> ret = new ArrayList<String>();
		Element resourceElement = task.getResourcingSpecs();
		if (resourceElement != null) {
			Namespace nsYawl = resourceElement.getNamespace();
			Element offer = resourceElement.getChild("offer", nsYawl);
			if (offer != null) {
				String initiator = offer.getAttributeValue("initiator");
				if (initiator != null) {
					if (initiator.equals("system")) {
						Element eDistSet = offer.getChild("distributionSet",
								nsYawl);
						if (eDistSet != null) {
							Element eInitialSet = eDistSet.getChild(
									"initialSet", nsYawl);
							if (eInitialSet != null) {
								List roles = eInitialSet.getChildren("role",
										nsYawl);
								Iterator itr = roles.iterator();
								while (itr.hasNext()) {
									Element eRole = (Element) itr.next();
									String role = eRole.getText();
									if (role.indexOf(',') > -1) {
										String[] rs = role.split(",");
										for (String r : rs) {
											ret.add(r.trim());
										}
									} else {
										ret.add(role);
									}
								}
							}
						}
					}
				}
			}
		}

		return ret.toArray(new String[0]);
	}

	public static YNet getYNetFromFile(String filePath) {
		return importEngineSpecificationFromFile(filePath).getRootNet();
	}

	public static YNet getYNetFromDefinition(byte[] definition) {
		String specXml = new String(definition);
		try {
			List<YSpecification> specifications = YMarshal
					.unmarshalSpecifications(specXml, false);
			// Engine currently only supplies a single specification per file.
			YSpecification spec = specifications.get(0);
			return spec.getRootNet();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getEngineSpecificationXMLFromFile(String fullFileName) {
		StringBuilder sb = new StringBuilder();
		try {
			FileReader fr = new FileReader(fullFileName);
			BufferedReader br = new BufferedReader(fr);
			String temp = br.readLine();
			while (temp != null) {
				sb.append(temp);
				temp = br.readLine();
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * get all the conditions of a net, including input condition and output
	 * condition
	 * 
	 * @param net
	 * @return
	 */
	public static ArrayList<YCondition> getNetConditions(YNet net) {
		ArrayList<YCondition> result = new ArrayList<YCondition>();
		for (YExternalNetElement element : net.getNetElements().values()) {
			if (element instanceof YCondition) {
				result.add((YCondition) element);
			}
		}
		return result;
	}

	/**
	 * get all the elements of a yawl model, including all the conditions and
	 * tasks.
	 * 
	 * @param net
	 * @return
	 */
	public static ArrayList<YExternalNetElement> getNetElements(YNet net) {
		ArrayList<YExternalNetElement> ret = new ArrayList<YExternalNetElement>();
		Iterator<YExternalNetElement> it = net.getNetElements().values()
				.iterator();
		while (it.hasNext()) {
			ret.add(it.next());
		}
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		YNet net = YAWLUtil.getYNetFromFile("e:/test/a.yawl");
		for (YTask task : net.getNetTasks()) {
			System.out.println("task name:");
			System.out.println(task.getName());
			System.out.println("read data:");
			System.out.println(task.getParamNamesForTaskStarting());
			System.out.println("write data:");
			System.out.println(task.getParamNamesForTaskCompletion());
			System.out.println("role:");
			String[] roles = YAWLUtil.getOfferRoleNamesForYTask(task);
			for (String str : roles) {
				System.out.print(str + ", ");
			}
			System.out.println();
		}
	}

}
