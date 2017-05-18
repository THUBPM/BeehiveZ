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
package cn.edu.thss.iise.beehivez.client.ui.modelio;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Font;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.server.generator.ModelGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.GWFNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.MurataGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PetriNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PiotrGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.MoeYAWLGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.YAWLGenerator;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.MathUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.yawlfoundation.yawl.elements.YNet;

import javax.swing.JComboBox;
import javax.swing.WindowConstants;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import javax.swing.JCheckBox;

public class DlgModelBatchGenerator2DB extends JDialog {
	private boolean debug = false;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel NumberInfo = null;
	private JLabel maxTransitionInfo = null;
	private JLabel maxDegreeInfo = null;
	private JTextField jTextFieldNumber = null;
	private JTextField jTextFieldMaxTransition = null;
	private JTextField jTextFieldMaxDegree = null;
	private JButton submit = null;
	private JButton cancel = null;
	private JLabel maxLengthInfo = null;
	private JTextField jTextFieldMaxTransitionNameLength = null;
	private JLabel jLabel1 = null;
	private JComboBox jComboBoxQueryTest = null;

	private long number = 100000;
	private int minTransitions = 1;
	private int maxTransitions = 200;
	private int maxDegree = 10;
	private int maxLengthOfTransitionName = 3;
	private JCheckBox jCheckBoxClearLog = null;
	private JLabel jLabelMinTransitionsPerNet = null;
	private JTextField jTextFieldMinTransitionsPerNet = null;
	private JLabel jLabelGenerator = null;
	private JComboBox jComboBoxGenerator = null;

	private JLabel jLabelQueryTime = null;

	private JTextField jTextFieldQueryTimes = null;
	private int queryTimes = 1;
	
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * @param owner
	 */
	public DlgModelBatchGenerator2DB(Frame owner) {
		super(owner, true);
		initialize();
		ScreenUtil.centerOnMainUI(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(379, 471);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle(resourcesManager.getString("DlgModelBatchGenerator2DB.title"));
		this.setModal(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelQueryTime = new JLabel();
			jLabelQueryTime.setBounds(new Rectangle(21, 288, 87, 18));
			jLabelQueryTime.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.repeatCount"));
			jLabelGenerator = new JLabel();
			jLabelGenerator.setBounds(new Rectangle(21, 329, 106, 18));
			jLabelGenerator.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.generatorChoice"));
			jLabelMinTransitionsPerNet = new JLabel();
			jLabelMinTransitionsPerNet
					.setBounds(new Rectangle(21, 73, 171, 27));
			jLabelMinTransitionsPerNet
					.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabelMinTransitionsPerNet.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.minTasksPerModel"));
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(21, 252, 77, 18));
			jLabel1.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.queryTest"));
			maxLengthInfo = new JLabel();
			maxLengthInfo.setBounds(new Rectangle(21, 206, 148, 20));
			maxLengthInfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			maxLengthInfo.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.maxNameLength"));
			maxLengthInfo.setFont(new Font("Dialog", Font.BOLD, 14));
			maxDegreeInfo = new JLabel();
			maxDegreeInfo.setBounds(new Rectangle(21, 156, 158, 30));
			maxDegreeInfo.setFont(new Font("Dialog", Font.BOLD, 14));
			maxDegreeInfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			maxDegreeInfo.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.maxDegree"));
			maxTransitionInfo = new JLabel();
			maxTransitionInfo.setBounds(new Rectangle(21, 116, 174, 30));
			maxTransitionInfo.setFont(new Font("Dialog", Font.BOLD, 14));
			maxTransitionInfo.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.maxTasksPerModel"));
			NumberInfo = new JLabel();
			NumberInfo.setBounds(new Rectangle(21, 31, 156, 30));
			NumberInfo.setFont(new Font("Dialog", Font.BOLD, 14));
			NumberInfo.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.numberOfModels"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(NumberInfo, null);
			jContentPane.add(maxTransitionInfo, null);
			jContentPane.add(maxDegreeInfo, null);
			jContentPane.add(getNumber(), null);
			jContentPane.add(getMaxTransition(), null);
			jContentPane.add(getMaxDegree(), null);
			jContentPane.add(getSubmit(), null);
			jContentPane.add(getCancel(), null);
			jContentPane.add(maxLengthInfo, null);
			jContentPane.add(getMaxTransitionNameLength(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJComboBoxQueryTest(), null);
			jContentPane.add(getJCheckBoxClearLog(), null);
			jContentPane.add(jLabelMinTransitionsPerNet, null);
			jContentPane.add(getJTextFieldMinTransitionsPerNet(), null);
			jContentPane.add(jLabelGenerator, null);
			jContentPane.add(getJComboBoxGenerator(), null);
			jContentPane.add(jLabelQueryTime, null);
			jContentPane.add(getJTextFieldQueryTimes(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes Number
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNumber() {
		if (jTextFieldNumber == null) {
			jTextFieldNumber = new JTextField();
			jTextFieldNumber.setBounds(new Rectangle(228, 29, 120, 30));
			jTextFieldNumber.setText(String.valueOf(number));
		}
		return jTextFieldNumber;
	}

	/**
	 * This method initializes maxTransition
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getMaxTransition() {
		if (jTextFieldMaxTransition == null) {
			jTextFieldMaxTransition = new JTextField();
			jTextFieldMaxTransition.setBounds(new Rectangle(229, 116, 120, 30));
			jTextFieldMaxTransition.setText(String.valueOf(maxTransitions));
		}
		return jTextFieldMaxTransition;
	}

	/**
	 * This method initializes maxDegree
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getMaxDegree() {
		if (jTextFieldMaxDegree == null) {
			jTextFieldMaxDegree = new JTextField();
			jTextFieldMaxDegree.setBounds(new Rectangle(229, 156, 120, 30));
			jTextFieldMaxDegree.setText(String.valueOf(maxDegree));
		}
		return jTextFieldMaxDegree;
	}

	private boolean areParametersLegal() {
		number = Long.parseLong(jTextFieldNumber.getText());
		if (number < 1) {
			return false;
		}
		minTransitions = Integer.parseInt(jTextFieldMinTransitionsPerNet
				.getText());
		if (minTransitions < 1) {
			return false;
		}
		maxTransitions = Integer.parseInt(jTextFieldMaxTransition.getText());
		if (maxTransitions < 1) {
			return false;
		}
		if (minTransitions > maxTransitions) {
			return false;
		}
		maxDegree = Integer.parseInt(jTextFieldMaxDegree.getText());
		if (maxDegree < 1) {
			return false;
		}
		maxLengthOfTransitionName = Integer
				.parseInt(jTextFieldMaxTransitionNameLength.getText());
		if (maxLengthOfTransitionName < 1) {
			return false;
		}

		queryTimes = Integer.parseInt(jTextFieldQueryTimes.getText());
		if (queryTimes < 0) {
			return false;
		}
		return true;
	}

	/**
	 * This method initializes submit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSubmit() {
		if (submit == null) {
			submit = new JButton();
			submit.setBounds(new Rectangle(73, 406, 80, 30));
			submit.setFont(new Font("Dialog", Font.BOLD, 14));
			submit.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.submit"));
			submit.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (!areParametersLegal()) {
						JOptionPane.showMessageDialog(null,
								resourcesManager.getString("DlgModelBatchGenerator2DB.please_input_valid_parameters"));
					} else {
						if (number < GlobalParameter.getLogPointEnd()) {
							GlobalParameter.setLogPointEnd(number);
						}
						if (!jComboBoxQueryTest.getSelectedItem().equals("No")) {
							GlobalParameter.setEnableQueryLog(true);
							if (jCheckBoxClearLog.isSelected()) {
								Indexlogger.createNewLogs();
								DataManager dm = DataManager.getInstance();
								dm.delAllOplog();
							}
						}

						batchGeneratePnml(number, minTransitions,
								maxTransitions, maxDegree,
								maxLengthOfTransitionName, jComboBoxQueryTest
										.getSelectedItem().toString().trim(),
								jComboBoxGenerator.getSelectedItem().toString()
										.trim());
						GlobalParameter.setEnableQueryLog(false);
						ClientFrame.getInstance().refreshStatus();
					}
					dispose();

				}

			});
		}
		return submit;
	}

	/**
	 * This method initializes cancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancel() {
		if (cancel == null) {
			cancel = new JButton();
			cancel.setBounds(new Rectangle(223, 406, 98, 30));
			cancel.setFont(new Font("Dialog", Font.BOLD, 14));
			cancel.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.cancel"));
			cancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();

				}

			});
		}
		return cancel;
	}

	private void batchGeneratePnml(long nNet, int minTasksPerNet,
			int maxTasksPerNet, int maxDegree, int maxTaskNameLength,
			String queryTest, String generator) {

		System.out.println("auto generate modes using " + generator);
		queryTest = queryTest.trim();
		System.out.println("Query Test: " + queryTest);

		// calculate the number of Petri nets with different transitions between
		// minTransitionsPerNet and maxTransitionsPerNet

		long[][] dis = MathUtil.getBinomialDistribution(minTasksPerNet,
				maxTasksPerNet, nNet);

		// used to determine which one in nTs amd counts can be generated more
		Vector<Integer> vI = new Vector<Integer>();
		for (int i = 0; i < dis[1].length; i++) {
			if (dis[1][i] > 0) {
				vI.add(i);
				System.out.println(dis[0][i] + " ---- " + dis[1][i]);
			}
		}

		// calculate the query test points
		Vector<Long> vLogPoint = new Vector<Long>();
		for (long logPoint = GlobalParameter.getLogPointStart(); logPoint <= GlobalParameter
				.getLogPointEnd(); logPoint += GlobalParameter
				.getLogPointSpan()) {
			vLogPoint.add(logPoint);
		}
		int nPoints = vLogPoint.size();

		if (debug) {
			System.out.println("log at:");
			for (long l : vLogPoint) {
				System.out.println(l);
			}
		}

		// record information for automatic query test
		DataManager dm = DataManager.getInstance();
		long petriNetsCatalogId = dm
				.getProcessCatalogIdByName(ProcesscatalogObject.PETRINETS);
		long YAWLModelsCatalogId = dm
				.getProcessCatalogIdByName(ProcesscatalogObject.YAWLMODELS);
		Vector<String> vIndexNameSupportGraphQuery = dm
				.getAllUsedIndexNameSupportGraphQuery();
		int nIndexSupportGraph = vIndexNameSupportGraphQuery.size();
		long[] nGraphQuerySuccessfully = new long[nIndexSupportGraph];
		long[] nGraphQuery = new long[nIndexSupportGraph];
		for (int i = 0; i < nIndexSupportGraph; i++) {
			nGraphQuerySuccessfully[i] = 0;
			nGraphQuery[i] = 0;
		}
		int nIndexSupportText = dm.getAllUsedIndexNameSupportTextQuery().size();
		long[] nTextQuerySuccessfully = new long[nIndexSupportText];
		long[] nTextQuery = new long[nIndexSupportText];
		for (int i = 0; i < nIndexSupportText; i++) {
			nTextQuerySuccessfully[i] = 0;
			nTextQuery[i] = 0;
		}

		// calculate the max length of transition name
		int lenTaskName = 0;
		// int nTransitions = maxTransitionsPerNet;
		// while (nTransitions > 0) {
		// lenTransitionName++;
		// nTransitions /= 62;
		// }
		// lenTransitionName = lenTransitionName > maxTransitionNameLength ?
		// lenTransitionName
		// : maxTransitionNameLength;
		lenTaskName = maxTaskNameLength;

		// // if query test using specific model, generate models randomly first
		// Vector vSpecificProcessID = new Vector();
		// int nGenerated = 0;
		// if (queryTest.equals("Query using specific models")) {
		// for (int i = 2; i < (maxTransitionsPerNet < nNet + 2 ?
		// maxTransitionsPerNet
		// : nNet + 2); i++) {
		// try {
		// PetriNet pn = GWFNetGenerator.generateGWFNet(i, maxDegree,
		// lenTransitionName, false);
		// long curTime = System.currentTimeMillis();
		// String fileName = "stfile/autogeneratedgwfnet.pnml";
		// FileWriter fw = new FileWriter(fileName);
		// BufferedWriter bw = new BufferedWriter(fw);
		//
		// // System.out.println("write to file...");
		// PnmlWriter.write(false, true, pn, bw);
		// bw.close();
		// bw = null;
		// fw.close();
		// fw = null;
		// // System.out.println("write to database");
		// long processid = dm.addProces(String.valueOf(curTime),
		// null, ProcessObject.typePnml, 1, fileName);
		// if (processid != -1) {
		// vSpecificProcessID.add(processid);
		// }
		// nGenerated++;
		// pn.delete();
		// pn.clearGraph();
		// pn = null;
		// System.gc();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

		Random rand = new Random(System.currentTimeMillis());

		for (long i = 1; i <= nNet; i++) {
			int minNT = minTasksPerNet;
			int maxNT = maxTasksPerNet;

			// determine the number of transitions in the generated model
			// following the
			// binomial distribution.
			int nVI = vI.size();
			if (nVI > 0) {
				int ii = rand.nextInt(nVI);
				int iii = vI.get(ii).intValue();
				int nT = (int) dis[0][iii];
				minNT = nT;
				maxNT = nT;
				dis[1][iii]--;
				if (dis[1][iii] == 0) {
					vI.remove(ii);
				}
			}
			// System.out.print(".");
			// System.out.println("generate gwfnet " + i);
			PetriNet pn = null;
			String type = ProcessObject.TYPEPNML;
			YNet net = null;
			ModelGenerator mg = dm.getGenerator(generator);
			if (mg instanceof PetriNetGenerator) {
				pn = (PetriNet) mg.generateModel(minNT, maxNT, maxDegree,
						lenTaskName);
				type = ProcessObject.TYPEPNML;
			} else if (mg instanceof YAWLGenerator) {
				net = (YNet) mg.generateModel(minNT, maxNT, maxDegree,
						lenTaskName);
				type = ProcessObject.TYPEYAWL;
			}

			if (type.equals(ProcessObject.TYPEPNML) && null == pn) {
				System.out.println("An empty Petri net is generated");
				i--;
				continue;
			}

			if (type.equals(ProcessObject.TYPEYAWL) && null == net) {
				System.out.println("An empty YAWL model is generated");
				i--;
				continue;
			}

			try {
				// System.out.println("write to database");
				long processid = -1;
				if (type.equals(ProcessObject.TYPEPNML)) {
					processid = dm.addProces(pn.getIdentifier(), null,
							ProcessObject.TYPEPNML, petriNetsCatalogId,
							PetriNetUtil.getPnmlBytes(pn));
				} else if (type.equals(ProcessObject.TYPEYAWL)) {
					processid = dm.addProces(net.getID(), null,
							ProcessObject.TYPEYAWL, YAWLModelsCatalogId,
							YAWLUtil.getYNetDefinitionBytes(net));
				}

				if (processid == -1) {
					continue;
				}
				// query test
				if (queryTest.equals("Query using specific models")) {
					if (vLogPoint.contains(i)) {
						// record the storage size of every index here
						float processTableSize = dm.getProcessTableSizeInMB();
						long nModels = GlobalParameter.getNModels();
						Indexlogger.logIndexStorageSize("raw",
								processTableSize, nModels);
						Iterator<BPMIndex> it = dm.getAllUsedIndexsIterator();
						while (it.hasNext()) {
							BPMIndex index = it.next();
							Indexlogger.logIndexStorageSize(index.getName(),
									index.getStorageSizeInMB(), nModels);
						}

						// query test here
						File modelDirectory = new File(GlobalParameter
								.getQueryObjectPath());
						for (int runs = 0; runs < queryTimes; runs++) {
							// test for graph query
							for (File f : modelDirectory.listFiles()) {
								if (f.isFile() && f.getName().endsWith(".pnml")) {
									String filepath = f.getAbsolutePath();
									PetriNet query = PetriNetUtil
											.getPetriNetFromPnmlFile(filepath);
									query.setIdentifier(f.getName());
									for (String indexName : dm
											.getAllUsedPetriNetIndexNameSupportGraphQuery()) {
										TreeSet<ProcessQueryResult> v = dm
												.retrieveProcess(query,
														indexName, 0);
										nGraphQuery[vIndexNameSupportGraphQuery
												.indexOf(indexName)]++;
										if (v != null && v.size() > 0) {
											nGraphQuerySuccessfully[vIndexNameSupportGraphQuery
													.indexOf(indexName)]++;
										}

									}
									query.destroyPetriNet();
								} else if (f.isFile()
										&& f.getName().endsWith(".yawl")) {
									String filepath = f.getAbsolutePath();
									YNet query = YAWLUtil
											.getYNetFromFile(filepath);
									for (String indexName : dm
											.getAllUsedYAWLIndexNameSupportGraphQuery()) {
										TreeSet<ProcessQueryResult> v = dm
												.retrieveProcess(query,
														indexName, 0);
										nGraphQuery[vIndexNameSupportGraphQuery
												.indexOf(indexName)]++;
										if (v != null && v.size() > 0) {
											nGraphQuerySuccessfully[vIndexNameSupportGraphQuery
													.indexOf(indexName)]++;
										}

									}
								}
							}

							// test for text query
							// read the text query string
							File fTextQuery = new File(modelDirectory,
									"query.str");
							if (fTextQuery.exists()) {
								FileReader fr = new FileReader(fTextQuery);
								BufferedReader br = new BufferedReader(fr);

								String query = br.readLine();
								while (query != null) {
									for (int k = 0; k < nIndexSupportText; k++) {
										String indexName = dm
												.getAllUsedIndexNameSupportTextQuery()
												.get(k);

										TreeSet<ProcessQueryResult> v = dm
												.retrieveProcess(query,
														indexName, 0);
										nTextQuery[k]++;
										if (v != null && v.size() > 0) {
											nTextQuerySuccessfully[k]++;
										}
									}
									query = br.readLine();
								}

								br.close();
								fr.close();
							}
						}
						vLogPoint.remove(i);
					}

					// if (i % maxTransitionsPerNet == 0) {
					// for (int k = 0; k < vSpecificProcessID.size(); k++) {
					// processid = (Long) vSpecificProcessID.get(k);
					//
					// pn = dm.getProcessPetriNet(processid);
					// for (int n = 0; n < nIndex; n++) {
					// String indexName = dm
					// .getAllUsedIndexSupportGraphQuery()
					// .get(n);
					// Vector<ProcessObject> v = dm
					// .getProcessByExample(pn, indexName);
					// nQuery[n]++;
					// boolean flagFind = false;
					// if (v != null && v.size() > 0) {
					// // System.out.println(indexName +
					// // "query successfully");
					// for (int j = 0; j < v.size(); j++) {
					// if (v.get(j).getProcess_id() == processid) {
					// nQuerySuccessfully[n]++;
					// flagFind = true;
					// break;
					// }
					// }
					// }
					// if (!flagFind) {
					// dm = DataManager.getInstance();
					// String modelType = dm
					// .getProcessType(processid);
					// InputStream modelDefinition = dm
					// .getProcessDefinition(processid);
					// VisualFrame visualframe = new VisualFrame(
					// modelType, modelDefinition);
					// visualframe.setLocation(100, 100);
					// visualframe.setVisible(true);
					// modelDefinition.close();
					// }
					// }
					// pn.delete();
					// pn.clearGraph();
					// pn = null;
					// System.gc();
					// }
					// }
				} else if (queryTest.equals("Query using the model added")) {
					if (type.equals(ProcessObject.TYPEPNML)) {
						for (String indexName : dm
								.getAllUsedPetriNetIndexNameSupportGraphQuery()) {
							boolean flagFind = false;
							TreeSet<ProcessQueryResult> v = dm.retrieveProcess(
									pn, indexName, 0);
							nGraphQuery[vIndexNameSupportGraphQuery
									.indexOf(indexName)]++;
							if (v != null && v.size() > 0) {
								// System.out.println(indexName +
								// "query successfully");
								Iterator<ProcessQueryResult> it = v.iterator();
								while (it.hasNext()) {
									ProcessQueryResult r = it.next();
									if (r.getProcess_id() == processid) {
										nGraphQuerySuccessfully[vIndexNameSupportGraphQuery
												.indexOf(indexName)]++;
										flagFind = true;
										break;
									}
								}
								//							
								// for (int j = 0; j < v.size(); j++) {
								// if (v.get(j).getProcess_id() == processid) {
								// nQuerySuccessfully[k]++;
								// flagFind = true;
								// break;
								// }
								// }
							}
							if (!flagFind) {
								dm = DataManager.getInstance();
								String modelType = dm.getProcessType(processid);
								InputStream modelDefinition = dm
										.getProcessDefinitionInputStream(processid);
								VisualFrame visualframe = new VisualFrame(
										modelType, modelDefinition);
								visualframe.setLocation(100, 100);
								visualframe.setVisible(true);
								modelDefinition.close();
							}
						}
						pn.destroyPetriNet();
						pn = null;
					} else if (type.equals(ProcessObject.TYPEYAWL)) {
						for (String indexName : dm
								.getAllUsedYAWLIndexNameSupportGraphQuery()) {
							boolean flagFind = false;
							TreeSet<ProcessQueryResult> v = dm.retrieveProcess(
									net, indexName, 0);
							nGraphQuery[vIndexNameSupportGraphQuery
									.indexOf(indexName)]++;
							if (v != null && v.size() > 0) {
								// System.out.println(indexName +
								// "query successfully");
								Iterator<ProcessQueryResult> it = v.iterator();
								while (it.hasNext()) {
									ProcessQueryResult r = it.next();
									if (r.getProcess_id() == processid) {
										nGraphQuerySuccessfully[vIndexNameSupportGraphQuery
												.indexOf(indexName)]++;
										flagFind = true;
										break;
									}
								}
								//							
								// for (int j = 0; j < v.size(); j++) {
								// if (v.get(j).getProcess_id() == processid) {
								// nQuerySuccessfully[k]++;
								// flagFind = true;
								// break;
								// }
								// }
							}
							if (!flagFind) {
								dm = DataManager.getInstance();
								String modelType = dm.getProcessType(processid);
								InputStream modelDefinition = dm
										.getProcessDefinitionInputStream(processid);
								VisualFrame visualframe = new VisualFrame(
										modelType, modelDefinition);
								visualframe.setLocation(100, 100);
								visualframe.setVisible(true);
								modelDefinition.close();
							}
						}
					}
					// for (int k = 0; k < nIndexSupportGraph; k++) {
					// String indexName = dm
					// .getAllUsedIndexNameSupportGraphQuery().get(k);
					//
					// boolean flagFind = false;
					// TreeSet<ProcessQueryResult> v = dm.retrieveProcess(pn,
					// indexName, 0);
					// nGraphQuery[k]++;
					// if (v != null && v.size() > 0) {
					// // System.out.println(indexName +
					// // "query successfully");
					// Iterator<ProcessQueryResult> it = v.iterator();
					// while (it.hasNext()) {
					// ProcessQueryResult r = it.next();
					// if (r.getProcess_id() == processid) {
					// nGraphQuerySuccessfully[k]++;
					// flagFind = true;
					// break;
					// }
					// }
					// //
					// // for (int j = 0; j < v.size(); j++) {
					// // if (v.get(j).getProcess_id() == processid) {
					// // nQuerySuccessfully[k]++;
					// // flagFind = true;
					// // break;
					// // }
					// // }
					// }
					// if (!flagFind) {
					// dm = DataManager.getInstance();
					// String modelType = dm.getProcessType(processid);
					// InputStream modelDefinition = dm
					// .getProcessDefinitionInputStream(processid);
					// VisualFrame visualframe = new VisualFrame(
					// modelType, modelDefinition);
					// visualframe.setLocation(100, 100);
					// visualframe.setVisible(true);
					// modelDefinition.close();
					// }
					// }
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		float processTableSize = dm.getProcessTableSizeInMB();
		long nModels = GlobalParameter.getNModels();
		Indexlogger.logIndexStorageSize("raw", processTableSize, nModels);
		Iterator<BPMIndex> it = dm.getAllUsedIndexsIterator();
		while (it.hasNext()) {
			BPMIndex index = it.next();
			Indexlogger.logIndexStorageSize(index.getName(), index
					.getStorageSizeInMB(), nModels);
		}

		String queryTestResult = "";
		if (!queryTest.equals("No")) {
			GlobalParameter.setEnableQueryLog(false);
			for (int k = 0; k < nIndexSupportGraph && nGraphQuery[k] > 0; k++) {
				String indexName = dm.getAllUsedIndexNameSupportGraphQuery()
						.get(k);
				queryTestResult += "\n\r****************\n\rgraph query test using "
						+ indexName
						+ "\n\r count of query: "
						+ nGraphQuery[k]
						+ "\n\r count of query successfully: "
						+ nGraphQuerySuccessfully[k]
						+ "\n\r query success retio: "
						+ (float) nGraphQuerySuccessfully[k]
						/ (float) nGraphQuery[k] * 100 + "%";
			}
			for (int k = 0; k < nIndexSupportText && nTextQuery[k] > 0; k++) {
				String indexName = dm.getAllUsedIndexNameSupportTextQuery()
						.get(k);
				queryTestResult += "\n\r****************\n\rtext query test using "
						+ indexName
						+ "\n\r count of query: "
						+ nTextQuery[k]
						+ "\n\r count of query successfully: "
						+ nTextQuerySuccessfully[k]
						+ "\n\r query success retio: "
						+ (float) nTextQuerySuccessfully[k]
						/ (float) nTextQuery[k] * 100 + "%";
			}
		}
		JOptionPane.showMessageDialog(null, generator
				+ " automatic generation completed\n\r number of models: "
				+ nNet + "\n\rpoints to record: " + nPoints
				+ "\n\rrepeat count of query: " + queryTimes + queryTestResult);
	}

	/**
	 * This method initializes maxTransitionNameLength
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getMaxTransitionNameLength() {
		if (jTextFieldMaxTransitionNameLength == null) {
			jTextFieldMaxTransitionNameLength = new JTextField();
			jTextFieldMaxTransitionNameLength.setBounds(new Rectangle(229, 203,
					121, 27));
			jTextFieldMaxTransitionNameLength.setText(String
					.valueOf(maxLengthOfTransitionName));
		}
		return jTextFieldMaxTransitionNameLength;
	}

	/**
	 * This method initializes jComboBoxQueryTest
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxQueryTest() {
		if (jComboBoxQueryTest == null) {
			jComboBoxQueryTest = new JComboBox();
			jComboBoxQueryTest.setBounds(new Rectangle(145, 249, 205, 27));
			jComboBoxQueryTest.addItem("No");
			jComboBoxQueryTest.addItem("Query using specific models");
			jComboBoxQueryTest.addItem("Query using the model added");
			jComboBoxQueryTest.setSelectedIndex(0);
			jComboBoxQueryTest
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							jCheckBoxClearLog.setEnabled(true);
							if (jComboBoxQueryTest.getSelectedItem().equals(
									"Query using specific models")) {
								DlgQueryTestParameter dlg = new DlgQueryTestParameter(
										null);
								dlg.setVisible(true);
								jTextFieldQueryTimes.setEnabled(true);
							} else if (jComboBoxQueryTest.getSelectedItem()
									.equals("No")) {
								GlobalParameter.setEnableQueryLog(false);
								jCheckBoxClearLog.setEnabled(false);
								jTextFieldQueryTimes.setEnabled(false);
								queryTimes = 0;
								jTextFieldQueryTimes.setText(String
										.valueOf(queryTimes));
							} else {
								jTextFieldQueryTimes.setEnabled(true);
							}
						}
					});
		}
		return jComboBoxQueryTest;
	}

	/**
	 * This method initializes jCheckBoxClearLog
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxClearLog() {
		if (jCheckBoxClearLog == null) {
			jCheckBoxClearLog = new JCheckBox();
			jCheckBoxClearLog.setBounds(new Rectangle(28, 364, 93, 21));
			jCheckBoxClearLog.setText(resourcesManager.getString("DlgModelBatchGenerator2DB.clear_log"));
			jCheckBoxClearLog.setEnabled(false);
		}
		return jCheckBoxClearLog;
	}

	/**
	 * This method initializes jTextFieldMinTransitionsPerNet
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMinTransitionsPerNet() {
		if (jTextFieldMinTransitionsPerNet == null) {
			jTextFieldMinTransitionsPerNet = new JTextField();
			jTextFieldMinTransitionsPerNet.setBounds(new Rectangle(228, 72,
					120, 27));
			jTextFieldMinTransitionsPerNet.setText(String
					.valueOf(minTransitions));
		}
		return jTextFieldMinTransitionsPerNet;
	}

	/**
	 * This method initializes jComboBoxGenerator
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxGenerator() {
		if (jComboBoxGenerator == null) {
			DataManager dm = DataManager.getInstance();
			jComboBoxGenerator = new JComboBox(dm.getAllGeneratorsName());
			jComboBoxGenerator.setBounds(new Rectangle(142, 325, 207, 27));
			jComboBoxGenerator.setSelectedIndex(0);
			jComboBoxGenerator
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DataManager dm = DataManager.getInstance();
							jTextFieldMaxDegree.setEnabled(dm.getGenerator(
									(String) jComboBoxGenerator
											.getSelectedItem())
									.supportDegreeConfiguration());
						}
					});
		}
		return jComboBoxGenerator;
	}

	/**
	 * This method initializes jTextFieldQueryTime
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldQueryTimes() {
		if (jTextFieldQueryTimes == null) {
			jTextFieldQueryTimes = new JTextField();
			jTextFieldQueryTimes.setBounds(new Rectangle(144, 289, 206, 22));
			jTextFieldQueryTimes.setText(String.valueOf(queryTimes));
		}
		return jTextFieldQueryTimes;
	}

} // @jve:decl-index=0:visual-constraint="-131,-90"
