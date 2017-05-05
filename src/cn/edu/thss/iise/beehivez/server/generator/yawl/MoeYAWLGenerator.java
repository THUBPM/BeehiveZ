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
package cn.edu.thss.iise.beehivez.server.generator.yawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jdom.Element;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YInputCondition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YOutputCondition;
import org.yawlfoundation.yawl.elements.YSpecVersion;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.resourcing.ResourceMap;
import org.yawlfoundation.yawl.resourcing.TaskPrivileges;
import org.yawlfoundation.yawl.resourcing.interactions.AbstractInteraction;
import org.yawlfoundation.yawl.unmarshal.YMetaData;

import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * Generate yawl models
 * 
 * the rules for control-flow perspective generation can be found in paper"Moe Thandar Wynn, H. M. W. (Eric) Verbeek, Wil M. P. van der Aalst, Arthur H. M. ter Hofstede, David Edmond: Reduction rules for YAWL workflows with cancellation regions and OR-joins. Information & Software Technology (INFSOF) 51(6):1010-1020 (2009)"
 * 
 * the rules for data-perspective generation
 * 
 * the rules for resource-perspective generation
 * 
 * @author Tao Jin
 * 
 */
public class MoeYAWLGenerator extends YAWLGenerator {

	/**
	 * @param minNumberOfTasks
	 *            the minimum number of tasks in the resulting model
	 * @param maxNumberofTasks
	 *            the maximum number of tasks in the resulting model
	 * @param maxDegree
	 *            the maximum degree of elements in the resulting mdoel
	 * @param maxNameLength
	 *            the maximum length of name for all the elements
	 */
	public YNet generateModel(int minNumberOfTasks, int maxNumberOfTasks,
			int maxDegree, int maxNameLength) {
		int nTasks = minNumberOfTasks
				+ rand.nextInt(maxNumberOfTasks - minNumberOfTasks + 1);
		// generate an atomic model first
		YNet net = generateAtomicModel(maxNameLength);

		// extend the control-flow perspective of the model here
		// the soundness is guranteed here
		while (net.getNetTasks().size() < nTasks) {
			int choice = rand.nextInt(7);
			switch (choice) {
			case 0:
				FLPY(net);
				break;
			case 1:
				FLTY(net, maxNameLength);
				break;
			case 2:
				FPY(net);
				break;
			case 3:
				FSPY(net, maxNameLength);
				break;
			case 4:
				FSTY(net, maxNameLength);
				break;
			case 5:
				FTY(net, maxNameLength);
				break;
			case 6:
				STY(net, maxNameLength);
				break;
			}
		}

		// extend the data perspective of the model here
		// the soundness is not guranteed here
		extendDataPerspective(net, 3 * nTasks, maxNameLength);

		// extend the resource perspective of the model here
		extendResourcePerspective(net, maxNameLength);

		return net;
	}

	/**
	 * data perspective generation, the soundness is not guranteed, the type of
	 * all the data is string.
	 * 
	 * @param net
	 * @param numData
	 *            the number of data exist in the model
	 */
	private void extendDataPerspective(YNet net, int maxNumberofData,
			int maxDataNameLength) {
		// generate the names of data first
		int numData = rand.nextInt(maxNumberofData) + 1;
		String[] dataNames = new String[numData];
		for (int i = 0; i < numData; i++) {
			dataNames[i] = getRandomString(maxDataNameLength);
			// YVariable var = new YVariable(net);
			// var.setDataTypeAndName("string", dataNames[i],
			// "http://www.w3.org/2001/XMLSchema");
			// net.setLocalVariable(var);
		}

		List<YTask> tasks = net.getNetTasks();
		int nTask = tasks.size();
		// data read for tasks
		int nTaskRead = rand.nextInt(nTask);
		for (int i = 0; i < nTaskRead; i++) {
			YTask task = tasks.get(rand.nextInt(nTask));
			int nRead = rand.nextInt(numData);
			for (int j = 0; j < nRead; j++) {
				String dataName = dataNames[rand.nextInt(numData)];
				task.setDataBindingForInputParam(dataName, dataName);
			}
		}

		// data write for tasks
		int nTaskWrite = rand.nextInt(nTask);
		for (int i = 0; i < nTaskWrite; i++) {
			YTask task = tasks.get(rand.nextInt(nTask));
			int nWrite = rand.nextInt(numData);
			for (int j = 0; j < nWrite; j++) {
				String dataName = dataNames[rand.nextInt(numData)];
				task.setDataBindingForOutputExpression(dataName, dataName);
			}
		}
	}

	/**
	 * resource perspective generation, the soundness is not guranteed, every
	 * task is assigned to a role
	 * 
	 * @param net
	 */
	private void extendResourcePerspective(YNet net, int maxRoleNameLength) {
		for (YTask task : net.getNetTasks()) {
			String roleID = getRandomString(maxRoleNameLength);
			ResourceMap rm = new ResourceMap(task.getID());
			rm.setSpecID(net.getSpecification().getSpecificationID());
			rm.setTaskPrivileges(new TaskPrivileges(net.getSpecification()
					.getSpecificationID(), task.getID()));
			rm.getOfferInteraction().setInitiator(
					AbstractInteraction.SYSTEM_INITIATED);
			rm.getOfferInteraction().addRoleUnchecked(roleID);
			String resXML = rm.toXML();
			task.setResourcingXML(resXML);
		}
	}

	/**
	 * generate an atomic yawl model, including one start place, one end place
	 * and one task
	 */
	private YNet generateAtomicModel(int maxNameLength) {
		String specID = String.valueOf(System.nanoTime());
		YSpecification spec = new YSpecification(specID + ".yawl");
		spec.setVersion(YSpecification.Version2_1);
		YMetaData metaData = new YMetaData();
		metaData.setTitle(specID);
		metaData.setDescription("generated automatically");
		List<String> authors = new ArrayList<String>();
		authors.add("yawl model generator");
		metaData.setCreators(authors);
		metaData.setVersion(new YSpecVersion(0, 1));
		metaData.setValidFrom(new Date());
		metaData.setValidUntil(new Date());
		metaData.setUniqueID(specID);
		metaData.setPersistent(true);
		spec.setMetaData(metaData);
		try {
			spec
					.setSchema("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n\n</xs:schema>");
		} catch (Exception e) {
			e.printStackTrace();
		}

		YNet net = new YNet("YAWL" + String.valueOf(System.nanoTime()), spec);
		net.setName("root net");

		spec.setRootNet(net);

		// add start place
		YInputCondition inputCondition = new YInputCondition(String
				.valueOf(System.nanoTime()), net);
		inputCondition.setName("Start");
		net.setInputCondition(inputCondition);

		// add end place
		YOutputCondition outputCondition = new YOutputCondition(String
				.valueOf(System.nanoTime()), net);
		outputCondition.setName("End");
		net.setOutputCondition(outputCondition);

		// add a task
		YAtomicTask task = new YAtomicTask(String.valueOf(System.nanoTime()),
				getRandomType(), getRandomType(), net);
		task.setName(getRandomString(maxNameLength));
		net.addNetElement(task);

		// add both the flow between the input condition and the task and the
		// flow between the task and the output condition
		task.addPreset(new YFlow(inputCondition, task));
		task.addPostset(new YFlow(task, outputCondition));

		return net;
	}

	/**
	 * fission of series tasks rule one YAWL level
	 * 
	 * choose one task and then split it into series tasks, the chosn task must
	 * be and-split one task and one condition are added, the chosen task is the
	 * front one, the output flows are redistributed.
	 * 
	 * @param net
	 */
	private void FSTY(YNet net, int maxNameLength) {
		// choose one task randomly first
		List<YTask> tasks = net.getNetTasks();
		int nTask = tasks.size();

		// choose one task with the split type as and
		int index = rand.nextInt(nTask);
		YTask v = tasks.get(index);

		if (v.getSplitType() != YTask._AND) {
			return;
		}

		// used for redistribution later
		YExternalNetElement[] vPostsetElements = v.getPostsetElements()
				.toArray(new YExternalNetElement[0]);

		// split the chosen task, the chosen task work as front one
		YTask t = v;
		YCondition p = new YCondition(String.valueOf(System.nanoTime()), net);
		net.addNetElement(p);

		YTask u = new YAtomicTask(String.valueOf(System.nanoTime()),
				getRandomType(), YTask._AND, net);
		u.setName(getRandomString(maxNameLength));
		net.addNetElement(u);

		p.addPreset(new YFlow(t, p));
		p.addPostset(new YFlow(p, u));

		// distribute the out flows of v
		int n = rand.nextInt(vPostsetElements.length) + 1;
		HashSet<YExternalNetElement> uPostsetElements = new HashSet<YExternalNetElement>();
		while (uPostsetElements.size() < n) {
			uPostsetElements.add(vPostsetElements[rand
					.nextInt(vPostsetElements.length)]);
		}

		Iterator<YExternalNetElement> it = uPostsetElements.iterator();
		while (it.hasNext()) {
			YExternalNetElement ne = it.next();
			u.addPostset(new YFlow(u, ne));
			t.removePostsetFlow(new YFlow(t, ne));
		}
	}

	/**
	 * fission of series conditions rule on YAWL level
	 * 
	 * chose one condition and split it into series, one condition and one task
	 * are added, the chosen condition is the rear one, the output flows of the
	 * chosen condition are redistributed.
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void FSPY(YNet net, int maxNameLength) {
		// choose one condition
		ArrayList<YCondition> conditions = getNetConditions(net);
		int nCondition = conditions.size();
		if (nCondition < 1) {
			return;
		}

		int index = rand.nextInt(nCondition);
		YCondition r = conditions.get(index);

		// used for redistribution later
		YExternalNetElement[] rPresetElements = r.getPresetElements().toArray(
				new YExternalNetElement[0]);

		// split the chosen condition, the original one as the rear one
		YCondition q = r;
		YCondition p = new YCondition(String.valueOf(System.nanoTime()), net);
		net.addNetElement(p);

		YAtomicTask t = new YAtomicTask(String.valueOf(System.nanoTime()),
				getRandomType(), getRandomType(), net);
		t.setName(getRandomString(maxNameLength));
		net.addNetElement(t);

		t.addPreset(new YFlow(p, t));
		t.addPostset(new YFlow(t, q));

		// redistribute the input flow of r
		int n = rand.nextInt(rPresetElements.length) + 1;
		HashSet<YExternalNetElement> pPresetElements = new HashSet<YExternalNetElement>();
		while (pPresetElements.size() < n) {
			pPresetElements.add(rPresetElements[rand
					.nextInt(rPresetElements.length)]);
		}
		Iterator<YExternalNetElement> it = pPresetElements.iterator();
		while (it.hasNext()) {
			YExternalNetElement pe = it.next();
			p.addPreset(new YFlow(pe, p));
			q.removePresetFlow(new YFlow(pe, q));
		}
	}

	/**
	 * fission of conditions rule on yawl level
	 * 
	 * chose one condition and split it, one condition is added
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void FPY(YNet net) {
		// choose one condition
		ArrayList<YCondition> conditions = getNetConditions(net);
		int nCondition = conditions.size();
		if (nCondition < 1) {
			return;
		}

		int index = rand.nextInt(nCondition);
		YCondition c = conditions.get(index);

		// check whether the chosen condition can be split
		Set<YExternalNetElement> cPresetElements = c.getPresetElements();
		Set<YExternalNetElement> cPostsetElements = c.getPostsetElements();
		Iterator<YExternalNetElement> itpost = cPostsetElements.iterator();
		int joinType = ((YTask) itpost.next()).getJoinType();
		while (itpost.hasNext()) {
			YExternalNetElement e = itpost.next();
			if (((YTask) e).getJoinType() != joinType) {
				return;
			}
		}

		Iterator<YExternalNetElement> itpre = cPresetElements.iterator();
		while (itpre.hasNext()) {
			YExternalNetElement e = itpre.next();
			if (joinType != YTask._OR && ((YTask) e).getSplitType() != joinType) {
				return;
			}
		}

		// split the chosen condition
		YCondition p1 = c;
		YCondition p2 = new YCondition(String.valueOf(System.nanoTime()), net);
		net.addNetElement(p2);
		itpre = cPresetElements.iterator();
		while (itpre.hasNext()) {
			p2.addPreset(new YFlow(itpre.next(), p2));
		}
		itpost = cPostsetElements.iterator();
		while (itpost.hasNext()) {
			p2.addPostset(new YFlow(p2, itpost.next()));
		}
	}

	/**
	 * fission of tasks rule on yawl level
	 * 
	 * choose on task and split it, one task is added
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void FTY(YNet net, int maxNameLength) {
		// choose one task
		List<YTask> tasks = net.getNetTasks();

		int index = rand.nextInt(tasks.size());
		YTask v = tasks.get(index);
		int joinType = v.getJoinType();
		if ((joinType != v.getSplitType()) || joinType == YTask._OR) {
			return;
		}

		Set<YExternalNetElement> vPresetElements = v.getPresetElements();
		Set<YExternalNetElement> vPostsetElements = v.getPostsetElements();

		// split the chosen task
		YTask t1 = v;
		YTask t2 = new YAtomicTask(String.valueOf(System.nanoTime()), joinType,
				joinType, net);
		t2.setName(getRandomString(maxNameLength));
		net.addNetElement(t2);

		Iterator<YExternalNetElement> itpre = vPresetElements.iterator();
		while (itpre.hasNext()) {
			YExternalNetElement pre = itpre.next();
			t2.addPreset(new YFlow(pre, t2));
		}

		Iterator<YExternalNetElement> itpost = vPostsetElements.iterator();
		while (itpost.hasNext()) {
			YExternalNetElement post = itpost.next();
			t2.addPostset(new YFlow(t2, post));
		}
	}

	/**
	 * fission of self-loop tasks rule on yawl level
	 * 
	 * choose one condition and add self-loop task
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void FLTY(YNet net, int maxNameLength) {
		// choose one condition
		ArrayList<YCondition> conditions = getNetConditions(net);
		int nCondition = conditions.size();
		if (nCondition < 1) {
			return;
		}
		int index = rand.nextInt(nCondition);
		YCondition p = conditions.get(index);

		// add self-loop task
		YTask t = new YAtomicTask(String.valueOf(System.nanoTime()),
				getRandomType(), getRandomType(), net);
		t.setName(getRandomString(maxNameLength));
		net.addNetElement(t);
		t.addPreset(new YFlow(p, t));
		t.addPostset(new YFlow(t, p));
	}

	/**
	 * fission of self-loop conditions rule on yawl level
	 * 
	 * choose one task and add self-loop conditions
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void FLPY(YNet net) {
		// choose one task
		List<YTask> tasks = net.getNetTasks();
		int nTask = tasks.size();

		int index = rand.nextInt(nTask);
		YTask t = tasks.get(index);
		if (t.getJoinType() == YTask._AND || t.getSplitType() != YTask._XOR) {
			return;
		}

		// add self-loop conditions
		YCondition x = new YCondition(String.valueOf(System.nanoTime()), net);
		net.addNetElement(x);
		x.addPreset(new YFlow(t, x));
		x.addPostset(new YFlow(x, t));
	}

	/**
	 * split of task rule on yawl level
	 * 
	 * choose one task and split it
	 * 
	 * @param net
	 * @param maxNameLength
	 */
	private void STY(YNet net, int maxNameLength) {
		// choose one task
		List<YTask> tasks = net.getNetTasks();
		int nTask = tasks.size();

		int index = rand.nextInt(nTask);
		YTask v = tasks.get(index);

		// split the chosen task, the chosen task is the front one
		YTask t = v;

		int joinType = getRandomType();
		int splitType = joinType;
		if (joinType == YTask._OR) {
			splitType = getRandomType();
		}
		YTask u = new YAtomicTask(String.valueOf(System.nanoTime()), joinType,
				v.getSplitType(), net);
		u.setName(getRandomString(maxNameLength));
		net.addNetElement(u);
		t.setSplitType(splitType);

		// move the output flows from t to u
		for (YExternalNetElement post : t.getPostsetElements()) {
			u.addPostset(new YFlow(u, post));
			t.removePostsetFlow(new YFlow(t, post));
		}

		YCondition p = new YCondition(String.valueOf(System.nanoTime()), net);
		net.addNetElement(p);
		p.addPreset(new YFlow(t, p));
		p.addPostset(new YFlow(p, u));
	}

	/**
	 * get all the conditions of a net except the input condition and output
	 * condition
	 * 
	 * @param net
	 * @return
	 */
	private ArrayList<YCondition> getNetConditions(YNet net) {
		ArrayList<YCondition> result = new ArrayList<YCondition>();
		for (YExternalNetElement element : net.getNetElements().values()) {
			if (element instanceof YCondition) {
				if (element.compareTo(net.getInputCondition()) != 0
						&& element.compareTo(net.getOutputCondition()) != 0) {
					result.add((YCondition) element);
				}
			}
		}
		return result;
	}

	/**
	 * get random type for split or join
	 * 
	 * @return
	 */
	private int getRandomType() {
		int ret = YTask._OR;

		int n = rand.nextInt(3);
		if (n == 0) {
			ret = YTask._AND;
		} else if (n == 1) {
			ret = YTask._OR;
		} else if (n == 2) {
			ret = YTask._XOR;
		}
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// long start = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			System.out.println(i);
			MoeYAWLGenerator generator = new MoeYAWLGenerator();
			YNet net = generator.generateModel(1, 50, -1, 3);
			YAWLUtil.exportEngineSpecificationToFile("e:/yawl/"
					+ net.getSpecification().getID() + ".yawl", net
					.getSpecification());
		}
		// long timecost = System.nanoTime() - start;
		// System.out.println(timecost + "ns");
	}

	@Override
	public boolean supportDegreeConfiguration() {
		return false;
	}

}
