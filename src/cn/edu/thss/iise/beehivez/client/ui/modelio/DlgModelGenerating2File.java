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
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.ComponentOrientation;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import org.processmining.framework.models.petrinet.PetriNet;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.generator.ModelGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PetriNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.YAWLGenerator;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.MathUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * @author Tao Jin
 * 
 * @date 2012-3-18
 * 
 */
public class DlgModelGenerating2File extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelNumberOfModels = null;
	private JTextField jTextFieldNumberOfModels = null;
	private JLabel jLabelMinTasks = null;
	private JTextField jTextFieldMinTasks = null;
	private JLabel jLabelMaxTasks = null;
	private JTextField jTextFieldMaxTasks = null;
	private JLabel jLabelMaxDegree = null;
	private JTextField jTextFieldMaxDegree = null;
	private JLabel jLabelMaxTaskNameLength = null;
	private JTextField jTextFieldMaxTaskNameLength = null;
	private JLabel jLabelGeneratorChoice = null;
	private JComboBox jComboBoxGeneratorChoice = null;
	private JLabel jLabelOutputFilePath = null;
	private JTextField jTextFieldOutputFilePath = null;
	private JButton jButtonChangePath = null;
	private JButton jButtonOK = null;

	private long nModels = 10000;
	private int minTasks = 1;
	private int maxTasks = 50;
	private int maxDegree = 10;
	private int maxTaskNameLength = 3;
	private String filePath = null;
	private String distribution = MathUtil.UNIFORMDISTRIBUTION;
	private String generator = "MoeYAWLGenerator";

	private JLabel jLabelDistribution = null;
	private JComboBox jComboBoxDistribution = null;
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This method initializes jTextFieldNumberOfModels
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldNumberOfModels() {
		if (jTextFieldNumberOfModels == null) {
			jTextFieldNumberOfModels = new JTextField();
			jTextFieldNumberOfModels.setBounds(new Rectangle(245, 23, 116, 20));
			jTextFieldNumberOfModels.setText(String.valueOf(nModels));
		}
		return jTextFieldNumberOfModels;
	}

	/**
	 * This method initializes jTextFieldMinTasks
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMinTasks() {
		if (jTextFieldMinTasks == null) {
			jTextFieldMinTasks = new JTextField();
			jTextFieldMinTasks.setBounds(new Rectangle(245, 55, 117, 20));
			jTextFieldMinTasks.setText(String.valueOf(minTasks));
		}
		return jTextFieldMinTasks;
	}

	/**
	 * This method initializes jTextFieldMaxTasks
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaxTasks() {
		if (jTextFieldMaxTasks == null) {
			jTextFieldMaxTasks = new JTextField();
			jTextFieldMaxTasks.setBounds(new Rectangle(245, 92, 116, 20));
			jTextFieldMaxTasks.setText(String.valueOf(maxTasks));
		}
		return jTextFieldMaxTasks;
	}

	/**
	 * This method initializes jTextFieldMaxDegree
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaxDegree() {
		if (jTextFieldMaxDegree == null) {
			jTextFieldMaxDegree = new JTextField();
			jTextFieldMaxDegree.setBounds(new Rectangle(245, 126, 115, 20));
			jTextFieldMaxDegree.setText(String.valueOf(maxDegree));
		}
		return jTextFieldMaxDegree;
	}

	/**
	 * This method initializes jTextFieldMaxTaskNameLength
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMaxTaskNameLength() {
		if (jTextFieldMaxTaskNameLength == null) {
			jTextFieldMaxTaskNameLength = new JTextField();
			jTextFieldMaxTaskNameLength.setBounds(new Rectangle(245, 161, 113,
					20));
			jTextFieldMaxTaskNameLength.setText(String
					.valueOf(maxTaskNameLength));
		}
		return jTextFieldMaxTaskNameLength;
	}

	/**
	 * This method initializes jComboBoxGeneratorChoice
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxGeneratorChoice() {
		if (jComboBoxGeneratorChoice == null) {
			DataManager dm = DataManager.getInstance();
			jComboBoxGeneratorChoice = new JComboBox(dm.getAllGeneratorsName());
			jComboBoxGeneratorChoice
					.setBounds(new Rectangle(146, 262, 212, 25));
			jComboBoxGeneratorChoice.setSelectedIndex(0);
			jComboBoxGeneratorChoice
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DataManager dm = DataManager.getInstance();
							jTextFieldMaxDegree.setEnabled(dm.getGenerator(
									(String) jComboBoxGeneratorChoice
											.getSelectedItem())
									.supportDegreeConfiguration());
						}
					});
		}
		return jComboBoxGeneratorChoice;
	}

	/**
	 * This method initializes jTextFieldOutputFilePath
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOutputFilePath() {
		if (jTextFieldOutputFilePath == null) {
			jTextFieldOutputFilePath = new JTextField();
			jTextFieldOutputFilePath.setBounds(new Rectangle(12, 336, 258, 20));
			jTextFieldOutputFilePath.setText(filePath);
			jTextFieldOutputFilePath.setEditable(false);
		}
		return jTextFieldOutputFilePath;
	}

	/**
	 * This method initializes jButtonChangePath
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChangePath() {
		if (jButtonChangePath == null) {
			jButtonChangePath = new JButton();
			jButtonChangePath.setBounds(new Rectangle(277, 335, 80, 23));
			jButtonChangePath.setText(resourcesManager.getString("DlgModelGenerating2File.change"));
			jButtonChangePath
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldOutputFilePath.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldOutputFilePath.setText(path);
							}
						}
					});
		}
		return jButtonChangePath;
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(157, 382, 63, 23));
			jButtonOK.setText(resourcesManager.getString("DlgModelGenerating2File.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!areParametersLegal()) {
						JOptionPane.showMessageDialog(null,
								resourcesManager.getString("DlgModelGenerating2File.please_input_valid_parameters"));
					} else {
						System.out.println("auto generate modes using "
								+ generator);

						// calculate the number of models with different tasks
						long[][] dis = MathUtil.getDistribution(distribution,
								minTasks, maxTasks, nModels);

						// used to determine which one in nTs and counts can be
						// generated more
						Vector<Integer> vI = new Vector<Integer>();
						for (int i = 0; i < dis[1].length; i++) {
							if (dis[1][i] > 0) {
								vI.add(i);
								System.out.println(dis[0][i] + " ---- "
										+ dis[1][i]);
							}
						}
						Random rand = new Random(System.currentTimeMillis());

						File dir = new File(filePath);
						if (!dir.exists()) {
							dir.mkdirs();
						} else if (dir.isFile()) {
							dir.delete();
							dir.mkdirs();
						}

						DataManager dm = DataManager.getInstance();

						for (long i = 1; i <= nModels; i++) {
							int minNT = minTasks;
							int maxNT = maxTasks;

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
							} else {
								int nT = rand.nextInt(maxTasks - minTasks + 1)
										+ minTasks;
								minNT = nT;
								maxNT = nT;
							}
							// System.out.print(".");
							// System.out.println("generate gwfnet " + i);
							PetriNet pn = null;
							String type = ProcessObject.TYPEPNML;
							YNet net = null;
							ModelGenerator mg = dm.getGenerator(generator);
							if (mg instanceof PetriNetGenerator) {
								pn = (PetriNet) mg.generateModel(minNT, maxNT,
										maxDegree, maxTaskNameLength);
								type = ProcessObject.TYPEPNML;
							} else if (mg instanceof YAWLGenerator) {
								net = (YNet) mg.generateModel(minNT, maxNT,
										maxDegree, maxTaskNameLength);
								type = ProcessObject.TYPEYAWL;
							}

							if (type.equals(ProcessObject.TYPEPNML)
									&& null == pn) {
								System.out
										.println("An empty Petri net is generated");
								i--;
								continue;
							}

							if (type.equals(ProcessObject.TYPEYAWL)
									&& null == net) {
								System.out
										.println("An empty YAWL model is generated");
								i--;
								continue;
							}

							if (type.equals(ProcessObject.TYPEPNML)) {
								String fileName = filePath + "/"
										+ pn.getIdentifier() + ".pnml";
								PetriNetUtil.export2pnml(pn, fileName);

								pn.destroyPetriNet();
								pn = null;
							} else if (type.equals(ProcessObject.TYPEYAWL)) {
								String fileName = filePath + "/" + net.getID()
										+ ".yawl";
								YAWLUtil.exportEngineSpecificationToFile(
										fileName, net.getSpecification());

							}

						}
						System.out.println(nModels
								+ " models are generated successfully!");
					}
					dispose();
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jComboBoxDistribution
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxDistribution() {
		if (jComboBoxDistribution == null) {
			jComboBoxDistribution = new JComboBox();
			jComboBoxDistribution.setBounds(new Rectangle(146, 213, 212, 25));
			jComboBoxDistribution.addItem(MathUtil.UNIFORMDISTRIBUTION);
			jComboBoxDistribution.addItem(MathUtil.BINOMIALDISTRIBUTION);
			jComboBoxDistribution.setSelectedIndex(0);
		}
		return jComboBoxDistribution;
	}

	private boolean areParametersLegal() {
		nModels = Long.parseLong(jTextFieldNumberOfModels.getText());
		if (nModels < 1) {
			return false;
		}
		minTasks = Integer.parseInt(jTextFieldMinTasks.getText());
		if (minTasks < 1) {
			return false;
		}
		maxTasks = Integer.parseInt(jTextFieldMaxTasks.getText());
		if (maxTasks < 1) {
			return false;
		}
		if (minTasks > maxTasks) {
			return false;
		}
		maxDegree = Integer.parseInt(jTextFieldMaxDegree.getText());
		if (maxDegree < 1) {
			return false;
		}
		maxTaskNameLength = Integer.parseInt(jTextFieldMaxTaskNameLength
				.getText());
		if (maxTaskNameLength < 1) {
			return false;
		}
		filePath = jTextFieldOutputFilePath.getText();
		if (filePath == null || filePath.equals("")) {
			return false;
		}

		distribution = jComboBoxDistribution.getSelectedItem().toString()
				.trim();
		generator = jComboBoxGeneratorChoice.getSelectedItem().toString()
				.trim();

		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param owner
	 */
	public DlgModelGenerating2File(Frame owner) {
		super(owner);
		initialize();
		ScreenUtil.centerOnMainUI(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(384, 455);
		this.setTitle(resourcesManager.getString("DlgModelGenerating2File.title"));
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.UNKNOWN);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelDistribution = new JLabel();
			jLabelDistribution.setBounds(new Rectangle(21, 217, 76, 16));
			jLabelDistribution.setText(resourcesManager.getString("DlgModelGenerating2File.distribution"));
			jLabelOutputFilePath = new JLabel();
			jLabelOutputFilePath.setBounds(new Rectangle(21, 314, 96, 16));
			jLabelOutputFilePath.setText(resourcesManager.getString("DlgModelGenerating2File.Output_file_path"));
			jLabelGeneratorChoice = new JLabel();
			jLabelGeneratorChoice.setBounds(new Rectangle(21, 266, 107, 16));
			jLabelGeneratorChoice.setText(resourcesManager.getString("DlgModelGenerating2File.Generator_choice"));
			jLabelMaxTaskNameLength = new JLabel();
			jLabelMaxTaskNameLength.setBounds(new Rectangle(21, 163, 148, 16));
			jLabelMaxTaskNameLength.setText(resourcesManager.getString("DlgModelGenerating2File.Max_length_of_task_name"));
			jLabelMaxDegree = new JLabel();
			jLabelMaxDegree.setBounds(new Rectangle(21, 128, 111, 16));
			jLabelMaxDegree.setText(resourcesManager.getString("DlgModelGenerating2File.Max_degree"));
			jLabelMaxTasks = new JLabel();
			jLabelMaxTasks.setBounds(new Rectangle(21, 94, 218, 16));
			jLabelMaxTasks.setText(resourcesManager.getString("DlgModelGenerating2File.Maximum_number_of_tasks_per_model"));
			jLabelMinTasks = new JLabel();
			jLabelMinTasks.setBounds(new Rectangle(21, 57, 212, 16));
			jLabelMinTasks.setText(resourcesManager.getString("DlgModelGenerating2File.Mininum_number_of_tasks_per_model"));
			jLabelNumberOfModels = new JLabel();
			jLabelNumberOfModels.setBounds(new Rectangle(21, 25, 111, 16));
			jLabelNumberOfModels.setText(resourcesManager.getString("DlgModelGenerating2File.Number_of_models"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelNumberOfModels, null);
			jContentPane.add(getJTextFieldNumberOfModels(), null);
			jContentPane.add(jLabelMinTasks, null);
			jContentPane.add(getJTextFieldMinTasks(), null);
			jContentPane.add(jLabelMaxTasks, null);
			jContentPane.add(getJTextFieldMaxTasks(), null);
			jContentPane.add(jLabelMaxDegree, null);
			jContentPane.add(getJTextFieldMaxDegree(), null);
			jContentPane.add(jLabelMaxTaskNameLength, null);
			jContentPane.add(getJTextFieldMaxTaskNameLength(), null);
			jContentPane.add(jLabelGeneratorChoice, null);
			jContentPane.add(getJComboBoxGeneratorChoice(), null);
			jContentPane.add(jLabelOutputFilePath, null);
			jContentPane.add(getJTextFieldOutputFilePath(), null);
			jContentPane.add(getJButtonChangePath(), null);
			jContentPane.add(getJButtonOK(), null);
			jContentPane.add(jLabelDistribution, null);
			jContentPane.add(getJComboBoxDistribution(), null);
		}
		return jContentPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
