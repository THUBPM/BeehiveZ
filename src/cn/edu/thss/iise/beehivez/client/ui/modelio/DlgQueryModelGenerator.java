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
import java.awt.Cursor;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.generator.ModelGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.GWFNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.MurataGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PetriNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PiotrGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.MoeYAWLGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.YAWLGenerator;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.MathUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.awt.Dimension;
import javax.swing.JComboBox;

/**
 * @author JinTao
 * 
 */
public class DlgQueryModelGenerator extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelQueryObjectPath = null;
	private JTextField jTextFieldQueryObjectPath = null;
	private JButton jButtonChooseQueryObjectPath = null;
	private JLabel jLabelNumberOfQueryObjects = null;
	private JTextField jTextFieldNumberOfQueryObjects = null;
	private JLabel jLabelMinimumNumberOfTransitions = null;
	private JTextField jTextFieldMinimumNumberOfTransitions = null;
	private JLabel jLabelMaximumNumberOfTransitions = null;
	private JTextField jTextFieldMaximumNumberOfTransitions = null;
	private JLabel jLabelMaximumDegree = null;
	private JTextField jTextFieldMaximumDegree = null;
	private JCheckBox jCheckBoxClearExistingModels = null;
	private JButton jButtonOK = null;
	private JLabel jLabelMaximumLengthOfTransitionName = null;
	private JTextField jTextFieldMaximumLengthOfTransitionName = null;

	public static final boolean OK = true;
	public static final boolean Cancel = false;

	private boolean ret = Cancel;

	private String queryObjectPath = GlobalParameter.getQueryObjectPath();
	private int numberOfQueryObjects = 10;
	private int minimumNumberOfTransitions = 1;
	private int maximumNumberOfTransitions = 200;
	private int maxDegree = 10;
	private boolean clearExistingModels = true;
	private boolean write2db = true;
	private int maximumLengthOfTransitionName = 3;
	private JLabel jLabelGenerator = null;
	private JComboBox jComboBoxGenerator = null;
	private JCheckBox jCheckBoxWriteToDB = null;
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This method initializes jTextFieldQueryObjectPath
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldQueryObjectPath() {
		if (jTextFieldQueryObjectPath == null) {
			jTextFieldQueryObjectPath = new JTextField();
			jTextFieldQueryObjectPath.setBounds(new Rectangle(14, 33, 272, 22));
			jTextFieldQueryObjectPath.setEditable(false);
			jTextFieldQueryObjectPath.setText(queryObjectPath);
		}
		return jTextFieldQueryObjectPath;
	}

	/**
	 * This method initializes jButtonChooseQueryObjectPath
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChooseQueryObjectPath() {
		if (jButtonChooseQueryObjectPath == null) {
			jButtonChooseQueryObjectPath = new JButton();
			jButtonChooseQueryObjectPath.setBounds(new Rectangle(176, 12, 111,
					19));
			jButtonChooseQueryObjectPath.setText(resourcesManager.getString("DlgQueryModelGenerator.change"));
			jButtonChooseQueryObjectPath
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldQueryObjectPath.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldQueryObjectPath.setText(path);
							}

						}
					});
		}
		return jButtonChooseQueryObjectPath;
	}

	/**
	 * This method initializes jTextFieldNumberOfQueryObjects
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldNumberOfQueryObjects() {
		if (jTextFieldNumberOfQueryObjects == null) {
			jTextFieldNumberOfQueryObjects = new JTextField();
			jTextFieldNumberOfQueryObjects.setBounds(new Rectangle(213, 101,
					71, 20));
			jTextFieldNumberOfQueryObjects.setText(String
					.valueOf(numberOfQueryObjects));
		}
		return jTextFieldNumberOfQueryObjects;
	}

	/**
	 * This method initializes jTextFieldMinimumNumberOfTransitions
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMinimumNumberOfTransitions() {
		if (jTextFieldMinimumNumberOfTransitions == null) {
			jTextFieldMinimumNumberOfTransitions = new JTextField();
			jTextFieldMinimumNumberOfTransitions.setBounds(new Rectangle(221,
					137, 63, 19));
			jTextFieldMinimumNumberOfTransitions.setText(String
					.valueOf(minimumNumberOfTransitions));
		}
		return jTextFieldMinimumNumberOfTransitions;
	}

	/**
	 * This method initializes jTextFieldMaximumNumberOfTransitions
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaximumNumberOfTransitions() {
		if (jTextFieldMaximumNumberOfTransitions == null) {
			jTextFieldMaximumNumberOfTransitions = new JTextField();
			jTextFieldMaximumNumberOfTransitions.setBounds(new Rectangle(222,
					169, 62, 19));
			jTextFieldMaximumNumberOfTransitions.setText(String
					.valueOf(maximumNumberOfTransitions));
		}
		return jTextFieldMaximumNumberOfTransitions;
	}

	/**
	 * This method initializes jTextFieldMaximumDegree
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaximumDegree() {
		if (jTextFieldMaximumDegree == null) {
			jTextFieldMaximumDegree = new JTextField();
			jTextFieldMaximumDegree.setBounds(new Rectangle(220, 203, 64, 19));
			jTextFieldMaximumDegree.setText(String.valueOf(maxDegree));
		}
		return jTextFieldMaximumDegree;
	}

	/**
	 * This method initializes jCheckBoxClearExistingModels
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxClearExistingModels() {
		if (jCheckBoxClearExistingModels == null) {
			jCheckBoxClearExistingModels = new JCheckBox();
			jCheckBoxClearExistingModels.setBounds(new Rectangle(14, 59, 270,
					21));
			jCheckBoxClearExistingModels
					.setText(resourcesManager.getString("DlgQueryModelGenerator.clear"));
			jCheckBoxClearExistingModels.setSelected(clearExistingModels);
		}
		return jCheckBoxClearExistingModels;
	}

	private boolean areParametersLegal() {
		numberOfQueryObjects = Integer.parseInt(jTextFieldNumberOfQueryObjects
				.getText());
		if (numberOfQueryObjects < 1) {
			return false;
		}
		minimumNumberOfTransitions = Integer
				.parseInt(jTextFieldMinimumNumberOfTransitions.getText());
		if (minimumNumberOfTransitions < 1) {
			return false;
		}
		maximumNumberOfTransitions = Integer
				.parseInt(jTextFieldMaximumNumberOfTransitions.getText());
		if (maximumNumberOfTransitions < 1) {
			return false;
		}
		maxDegree = Integer.parseInt(jTextFieldMaximumDegree.getText());
		if (maxDegree < 1) {
			return false;
		}
		maximumLengthOfTransitionName = Integer
				.parseInt(jTextFieldMaximumLengthOfTransitionName.getText());
		if (maximumLengthOfTransitionName < 1) {
			return false;
		}

		if (maximumNumberOfTransitions < minimumNumberOfTransitions) {
			return false;
		}
		// if (numberOfQueryObjects > maximumNumberOfTransitions
		// - minimumNumberOfTransitions + 1) {
		// return false;
		// }
		return true;
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(102, 307, 79, 23));
			jButtonOK.setText(resourcesManager.getString("DlgQueryModelGenerator.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (areParametersLegal()) {
						ret = OK;
						queryObjectPath = jTextFieldQueryObjectPath.getText();
						clearExistingModels = jCheckBoxClearExistingModels
								.isSelected();
						write2db = jCheckBoxWriteToDB.isSelected();
						GlobalParameter.setQueryObjectPath(queryObjectPath);
						// generate the query models here
						File dir = new File(queryObjectPath);
						if (!dir.exists()) {
							dir.mkdirs();
						} else if (dir.isFile()) {
							dir.delete();
							dir.mkdirs();
						}
						if (clearExistingModels) {
							for (File f : dir.listFiles()) {
								if (f.isFile()) {
									f.delete();
								}
							}
						}

						// generate query models
						// get the distributions of models with certain number
						// of transitions
						long dis[][] = MathUtil.getDiscreteUniformDistribution(
								minimumNumberOfTransitions,
								maximumNumberOfTransitions,
								numberOfQueryObjects);
						Vector<Integer> vI = new Vector<Integer>();

						for (int i = 0; i < dis[1].length; i++) {
							if (dis[1][i] > 0) {
								vI.add(i);
								System.out.println(dis[0][i] + " ---- "
										+ dis[1][i]);
							}
						}

						int count = 0;
						Random rand = new Random(System.currentTimeMillis());

						while (count < numberOfQueryObjects) {
							try {
								int nT = rand
										.nextInt(maximumNumberOfTransitions
												- minimumNumberOfTransitions
												+ 1)
										+ minimumNumberOfTransitions;

								int nVI = vI.size();
								if (nVI > 0) {
									int ii = rand.nextInt(nVI);
									int iii = vI.get(ii).intValue();
									nT = (int) dis[0][iii];
									dis[1][iii]--;
									if (dis[1][iii] == 0) {
										vI.remove(ii);
									}
								}

								System.out
										.println("the number of transitions is: "
												+ nT);
								String generator = jComboBoxGenerator
										.getSelectedItem().toString().trim();
								PetriNet pn = null;
								YNet net = null;
								String type = ProcessObject.TYPEPNML;
								DataManager dm = DataManager.getInstance();
								ModelGenerator mg = dm.getGenerator(generator);
								if (mg instanceof PetriNetGenerator) {
									pn = (PetriNet) mg.generateModel(nT, nT,
											maxDegree,
											maximumLengthOfTransitionName);
									type = ProcessObject.TYPEPNML;
								} else if (mg instanceof YAWLGenerator) {
									net = (YNet) mg.generateModel(nT, nT,
											maxDegree,
											maximumLengthOfTransitionName);
									type = ProcessObject.TYPEYAWL;
								}

								count++;
								if (type.equals(ProcessObject.TYPEPNML)
										&& null == pn) {
									System.out
											.println("An empty Petri net is generated");
									count--;
									continue;
								} else if (type.equals(ProcessObject.TYPEYAWL)
										&& null == net) {
									System.out
											.println("An empty Yawl model is generated");
									count--;
									continue;
								}

								if (type.equals(ProcessObject.TYPEPNML)) {
									String fileName = GlobalParameter
											.getQueryObjectPath()
											+ "/"
											+ pn.getIdentifier()
											+ ".pnml";
									PetriNetUtil.export2pnml(pn, fileName);
									if (write2db) {
										// System.out.println("write to database");
										long testCatalogId = dm
												.getProcessCatalogIdByName(ProcesscatalogObject.PETRINETS);
										long processid = dm.addProces(String
												.valueOf(pn.getIdentifier()),
												null, ProcessObject.TYPEPNML,
												testCatalogId, PetriNetUtil
														.getPnmlBytes(pn));
									}
									pn.destroyPetriNet();
									pn = null;
								} else if (type.equals(ProcessObject.TYPEYAWL)) {
									String fileName = GlobalParameter
											.getQueryObjectPath()
											+ "/" + net.getID() + ".yawl";
									YAWLUtil.exportEngineSpecificationToFile(
											fileName, net.getSpecification());

									if (write2db) {
										// System.out.println("write to database");
										long testCatalogId = dm
												.getProcessCatalogIdByName(ProcesscatalogObject.YAWLMODELS);
										long processid = dm
												.addProces(
														String.valueOf(net
																.getID()),
														null,
														ProcessObject.TYPEYAWL,
														testCatalogId,
														YAWLUtil
																.getYNetDefinitionBytes(net));
									}
								}

							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						ClientFrame.getInstance().refreshStatus();
						dispose();
					} else {
						JOptionPane.showMessageDialog(null,
								"illegal parameters");
					}
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jTextFieldMaximumLengthOfTransitionName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaximumLengthOfTransitionName() {
		if (jTextFieldMaximumLengthOfTransitionName == null) {
			jTextFieldMaximumLengthOfTransitionName = new JTextField();
			jTextFieldMaximumLengthOfTransitionName.setBounds(new Rectangle(
					241, 235, 43, 19));
			jTextFieldMaximumLengthOfTransitionName.setText(String
					.valueOf(maximumLengthOfTransitionName));
		}
		return jTextFieldMaximumLengthOfTransitionName;
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
			jComboBoxGenerator.setBounds(new Rectangle(90, 265, 194, 27));
			jComboBoxGenerator.setSelectedIndex(0);
			jComboBoxGenerator
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DataManager dm = DataManager.getInstance();
							jTextFieldMaximumDegree.setEnabled(dm.getGenerator(
									(String) jComboBoxGenerator
											.getSelectedItem())
									.supportDegreeConfiguration());
						}
					});
		}
		return jComboBoxGenerator;
	}

	/**
	 * This method initializes jCheckBoxWriteToDB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxWriteToDB() {
		if (jCheckBoxWriteToDB == null) {
			jCheckBoxWriteToDB = new JCheckBox();
			jCheckBoxWriteToDB.setBounds(new Rectangle(14, 79, 243, 21));
			jCheckBoxWriteToDB.setText(resourcesManager.getString("DlgQueryModelGenerator.write"));
			jCheckBoxWriteToDB.setSelected(write2db);
		}
		return jCheckBoxWriteToDB;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DlgQueryModelGenerator dlg = new DlgQueryModelGenerator(null);
		if (dlg.showDialog() == DlgQueryModelGenerator.OK) {
			String message = "query object path: " + dlg.getQueryObjectPath()
					+ "\n\r" + "clear existing models: "
					+ dlg.isClearExistingModels() + "\n\r"
					+ "minimum number of transitions: "
					+ dlg.getMinimumNumberOfTransitions() + "\n\r"
					+ "maximum number of transitions: "
					+ dlg.getMaximumNumberOfTransitions() + "\n\r"
					+ "maximum degree: " + dlg.getMaxDegree() + "\n\r"
					+ "maximum length of transition name: "
					+ dlg.getMaximumLengthOfTransitionName();
			JOptionPane.showMessageDialog(null, message);
		}
	}

	/**
	 * @param owner
	 */
	public DlgQueryModelGenerator(Frame owner) {
		super(owner);
		initialize();
		ScreenUtil.centerOnMainUI(this);
	}

	public boolean showDialog() {
		this.setVisible(true);
		return ret;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(306, 365);
		this.setTitle(resourcesManager.getString("DlgQueryModelGenerator.title"));
		this.setName("dlgQueryGWFNetGenerator");
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.setVisible(false);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelGenerator = new JLabel();
			jLabelGenerator.setBounds(new Rectangle(11, 269, 65, 18));
			jLabelGenerator.setText(resourcesManager.getString("DlgQueryModelGenerator.generator"));
			jLabelMaximumLengthOfTransitionName = new JLabel();
			jLabelMaximumLengthOfTransitionName.setBounds(new Rectangle(11,
					235, 215, 18));
			jLabelMaximumLengthOfTransitionName
					.setText(resourcesManager.getString("DlgQueryModelGenerator.maxlen"));
			jLabelMaximumDegree = new JLabel();
			jLabelMaximumDegree.setBounds(new Rectangle(11, 203, 191, 18));
			jLabelMaximumDegree.setText(resourcesManager.getString("DlgQueryModelGenerator.maxdeg"));
			jLabelMaximumNumberOfTransitions = new JLabel();
			jLabelMaximumNumberOfTransitions.setBounds(new Rectangle(11, 170,
					188, 18));
			jLabelMaximumNumberOfTransitions.setText(resourcesManager.getString("DlgQueryModelGenerator.maxnum"));
			jLabelMinimumNumberOfTransitions = new JLabel();
			jLabelMinimumNumberOfTransitions.setBounds(new Rectangle(11, 136,
					187, 18));
			jLabelMinimumNumberOfTransitions.setText(resourcesManager.getString("DlgQueryModelGenerator.minnum"));
			jLabelNumberOfQueryObjects = new JLabel();
			jLabelNumberOfQueryObjects
					.setBounds(new Rectangle(11, 102, 164, 18));
			jLabelNumberOfQueryObjects.setText(resourcesManager.getString("DlgQueryModelGenerator.numque"));
			jLabelQueryObjectPath = new JLabel();
			jLabelQueryObjectPath.setBounds(new Rectangle(14, 13, 151, 18));
			jLabelQueryObjectPath.setText(resourcesManager.getString("DlgQueryModelGenerator.querymodelsdirectory"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelQueryObjectPath, null);
			jContentPane.add(getJTextFieldQueryObjectPath(), null);
			jContentPane.add(getJButtonChooseQueryObjectPath(), null);
			jContentPane.add(jLabelNumberOfQueryObjects, null);
			jContentPane.add(getJTextFieldNumberOfQueryObjects(), null);
			jContentPane.add(jLabelMinimumNumberOfTransitions, null);
			jContentPane.add(getJTextFieldMinimumNumberOfTransitions(), null);
			jContentPane.add(jLabelMaximumNumberOfTransitions, null);
			jContentPane.add(getJTextFieldMaximumNumberOfTransitions(), null);
			jContentPane.add(jLabelMaximumDegree, null);
			jContentPane.add(getJTextFieldMaximumDegree(), null);
			jContentPane.add(getJCheckBoxClearExistingModels(), null);
			jContentPane.add(getJButtonOK(), null);
			jContentPane.add(jLabelMaximumLengthOfTransitionName, null);
			jContentPane
					.add(getJTextFieldMaximumLengthOfTransitionName(), null);
			jContentPane.add(jLabelGenerator, null);
			jContentPane.add(getJComboBoxGenerator(), null);
			jContentPane.add(getJCheckBoxWriteToDB(), null);
		}
		return jContentPane;
	}

	/**
	 * @return the queryObjectPath
	 */
	public String getQueryObjectPath() {
		return queryObjectPath;
	}

	/**
	 * @return the numberOfQueryObjects
	 */
	public int getNumberOfQueryObjects() {
		return numberOfQueryObjects;
	}

	/**
	 * @return the minimumNumberOfTransitions
	 */
	public int getMinimumNumberOfTransitions() {
		return minimumNumberOfTransitions;
	}

	/**
	 * @return the maximumNumberOfTransitions
	 */
	public int getMaximumNumberOfTransitions() {
		return maximumNumberOfTransitions;
	}

	/**
	 * @return the maxDegree
	 */
	public int getMaxDegree() {
		return maxDegree;
	}

	/**
	 * @return the clearExistingModels
	 */
	public boolean isClearExistingModels() {
		return clearExistingModels;
	}

	/**
	 * @return the maximumLengthOfTransitionName
	 */
	public int getMaximumLengthOfTransitionName() {
		return maximumLengthOfTransitionName;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
