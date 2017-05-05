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
package cn.edu.thss.iise.beehivez.client.ui.metric;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.WindowConstants;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.metric.PetriNetMetrics;
import cn.edu.thss.iise.beehivez.server.metric.YAWLMetrics;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import javax.swing.JComboBox;

/**
 * @author Tao Jin
 * 
 */
public class DlgModelMetrics extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelDataSource = null;
	private JRadioButton jRadioButtonDatabase = null;
	private JRadioButton jRadioButtonFileSystem = null;
	private JButton jButtonChooseDirectory = null;
	private JTextField jTextFieldSourceDirectory = null;
	private JLabel jLabelExport = null;
	private JLabel jLabelMetrics = null;
	private JButton jButtonSaveReportFile = null;
	private JTextField jTextFieldReportFileName = null;
	private JCheckBox jCheckBoxNumberOfTransitions = null;
	private JCheckBox jCheckBoxNumberOfPlaces = null;
	private JCheckBox jCheckBoxNumberOfArcs = null;
	private JCheckBox jCheckBoxNumberOfAndJoin = null;
	private JCheckBox jCheckBoxNumberOfAndSplit = null;
	private JCheckBox jCheckBoxNumberOfXORSplit = null;
	private JCheckBox jCheckBoxNumberOfXORJoin = null;
	private JCheckBox jCheckBoxNumberOfTARs = null;
	private JCheckBox jCheckBoxDensity = null;
	private JCheckBox jCheckBoxAnalyzeStateSpace = null;
	private JCheckBox jCheckBoxAnalyzeAndSplitDegree = null;
	private JCheckBox jCheckBoxAnalyzeAndJoinDegree = null;
	private JCheckBox jCheckBoxAnalyzeXORSplitDegree = null;
	private JCheckBox jCheckBoxAnalyzeXORJoinDegree = null;
	private JButton jButtonOK = null;
	private JCheckBox jCheckBoxMismatch = null;
	private JCheckBox jCheckBoxSequentiality = null;
	private JCheckBox jCheckBoxTS = null;
	private JCheckBox jCheckBoxCFC = null;
	private JCheckBox jCheckBoxCH = null;
	private JCheckBox jCheckBoxCYC = null;
	private JCheckBox jCheckBoxDiam = null;
	private JCheckBox jCheckBoxSeparability = null;
	private JCheckBox jCheckBoxStructuredness = null;
	private JCheckBox jCheckBoxCNC = null;
	private JCheckBox jCheckBoxAverageDegree = null;
	private JCheckBox jCheckBoxMaxDegree = null;
	private JCheckBox jCheckBoxDepth = null;

	private String srcDirectory = "cleanedEpcPnml";
	private String reportFileName = "metrics.csv";

	private boolean isFromDatabase = true;
	private boolean isFromFileSystem = false;
	private boolean recordNumberOfTransitions = true;
	private boolean recordNumberOfPlaces = true;
	private boolean recordNumberOfArcs = true;
	private boolean recordMaxInDegree = true;
	private boolean recordMaxOutDegree = true;
	private boolean recordNumberOfAndJoin = true;
	private boolean recordNumberOfAndSplit = true;
	private boolean recordNumberOfXORSplit = true;
	private boolean recordNumberOfXORJoin = true;
	private boolean recordNumberOfTARs = true;
	private boolean recordDensity = true;
	private boolean recordStateSpaceAnalysis = false;
	private boolean recordAndSplitDegreeAnalysis = true;
	private boolean recordAndJoinDegreeAnalysis = true;
	private boolean recordXORSplitDegreeAnalysis = true;
	private boolean recordXORJoinDegreeAnalysis = true;
	private boolean recordNumberOfORSplit = false;
	private boolean recordNumberOfORJoin = false;
	private boolean recordORSplitDegreeAnalysis = false;
	private boolean recordORJoinDegreeAnalysis = false;
	private boolean recordMismatch = true;
	private boolean recordSequentiality = true;
	private boolean recordTS = true;
	private boolean recordCFC = true;
	private boolean recordCH = true;
	private boolean recordCYC = true;
	private boolean recordDiam = true;
	private boolean recordSeparability = true;
	private boolean recordStructuredness = true;
	private boolean recordCNC = true;
	private boolean recordAverageDegree = true;
	private boolean recordMaxDegree = true;
	private boolean recordDepth = true;
	private JCheckBox jCheckBoxMaxInDegree = null;
	private JCheckBox jCheckBoxMaxOutDegree = null;
	private JLabel jLabelOffset = null;
	private JTextField jTextFieldOffset = null;
	private JLabel jLabelLimit = null;
	private JTextField jTextFieldLimit = null;

	private int offset = 0;
	private int limit = Integer.MAX_VALUE;
	private JCheckBox jCheckBoxNumberOfORSplit = null;
	private JCheckBox jCheckBoxNumberOfORJoin = null;
	private JCheckBox jCheckBoxAnalyzeORSplitDegree = null;
	private JCheckBox jCheckBoxAnalyzeORJoinDegree = null;
	private JComboBox jComboBoxModelType = null;
	private JLabel jLabelModelType = null;
	ResourcesManager resourcesManager;

	/**
	 * This method initializes jRadioButtonDatabase
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDatabase() {
		if (jRadioButtonDatabase == null) {
			jRadioButtonDatabase = new JRadioButton();
			jRadioButtonDatabase.setBounds(new Rectangle(12, 92, 83, 21));
			jRadioButtonDatabase.setText(resourcesManager.getString("ModelMetrics.database"));
			jRadioButtonDatabase.setSelected(isFromDatabase);
			jRadioButtonDatabase
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							if (jRadioButtonDatabase.isSelected()) {
								jRadioButtonFileSystem.setSelected(false);
								jButtonChooseDirectory.setEnabled(false);
								jTextFieldOffset.setEnabled(true);
								jTextFieldLimit.setEnabled(true);
							} else {
								jRadioButtonFileSystem.setSelected(true);
								jButtonChooseDirectory.setEnabled(true);
								jTextFieldOffset.setEnabled(false);
								jTextFieldLimit.setEnabled(false);
							}
						}
					});
		}
		return jRadioButtonDatabase;
	}

	/**
	 * This method initializes jRadioButtonFileSystem
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFileSystem() {
		if (jRadioButtonFileSystem == null) {
			jRadioButtonFileSystem = new JRadioButton();
			jRadioButtonFileSystem.setBounds(new Rectangle(12, 113, 91, 21));
			jRadioButtonFileSystem.setText(resourcesManager.getString("ModelMetrics.filesystem"));
			jRadioButtonFileSystem.setSelected(isFromFileSystem);
			jRadioButtonFileSystem
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							if (jRadioButtonFileSystem.isSelected()) {
								jRadioButtonDatabase.setSelected(false);
								jButtonChooseDirectory.setEnabled(true);
								jTextFieldOffset.setEnabled(false);
								jTextFieldLimit.setEnabled(false);
							} else {
								jRadioButtonDatabase.setSelected(true);
								jButtonChooseDirectory.setEnabled(false);
								jTextFieldOffset.setEnabled(true);
								jTextFieldLimit.setEnabled(true);
							}
						}
					});
		}
		return jRadioButtonFileSystem;
	}

	/**
	 * This method initializes jButtonChooseDirectory
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChooseDirectory() {
		if (jButtonChooseDirectory == null) {
			jButtonChooseDirectory = new JButton();
			jButtonChooseDirectory.setBounds(new Rectangle(105, 115, 140, 18));
			jButtonChooseDirectory.setText(resourcesManager.getString("ModelMetrics.choosedirectory"));
			jButtonChooseDirectory.setEnabled(isFromFileSystem);
			jButtonChooseDirectory
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldSourceDirectory.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldSourceDirectory.setText(path);
							}
						}
					});
		}
		return jButtonChooseDirectory;
	}

	/**
	 * This method initializes jTextFieldDirectory
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSourceDirectory() {
		if (jTextFieldSourceDirectory == null) {
			jTextFieldSourceDirectory = new JTextField();
			jTextFieldSourceDirectory
					.setBounds(new Rectangle(12, 135, 342, 22));
			jTextFieldSourceDirectory.setText(srcDirectory);
			jTextFieldSourceDirectory.setEditable(false);
		}
		return jTextFieldSourceDirectory;
	}

	/**
	 * This method initializes jButtonSaveReportFile
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSaveReportFile() {
		if (jButtonSaveReportFile == null) {
			jButtonSaveReportFile = new JButton();
			jButtonSaveReportFile.setBounds(new Rectangle(101, 191, 142, 17));
			jButtonSaveReportFile.setText(resourcesManager.getString("ModelMetrics.report"));
			jButtonSaveReportFile
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser();
							chooser
									.setFileSelectionMode(JFileChooser.FILES_ONLY);
							if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldReportFileName.setText(path);
							}
						}
					});
		}
		return jButtonSaveReportFile;
	}

	/**
	 * This method initializes jTextFieldReportFileName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldReportFileName() {
		if (jTextFieldReportFileName == null) {
			jTextFieldReportFileName = new JTextField();
			jTextFieldReportFileName.setBounds(new Rectangle(12, 210, 341, 22));
			jTextFieldReportFileName.setText(reportFileName);
			jTextFieldReportFileName.setEditable(false);
		}
		return jTextFieldReportFileName;
	}

	/**
	 * This method initializes jCheckBoxNumberOfTransitions
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfTransitions() {
		if (jCheckBoxNumberOfTransitions == null) {
			jCheckBoxNumberOfTransitions = new JCheckBox();
			jCheckBoxNumberOfTransitions.setBounds(new Rectangle(12, 283, 152,
					21));
			jCheckBoxNumberOfTransitions.setText(resourcesManager.getString("ModelMetrics.numberoftasks"));
			jCheckBoxNumberOfTransitions.setSelected(recordNumberOfTransitions);
		}
		return jCheckBoxNumberOfTransitions;
	}

	/**
	 * This method initializes jCheckBoxNumberOfPlaces
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfPlaces() {
		if (jCheckBoxNumberOfPlaces == null) {
			jCheckBoxNumberOfPlaces = new JCheckBox();
			jCheckBoxNumberOfPlaces.setBounds(new Rectangle(187, 283, 128, 21));
			jCheckBoxNumberOfPlaces.setText(resourcesManager.getString("ModelMetrics.numofpla"));
			jCheckBoxNumberOfPlaces.setSelected(recordNumberOfPlaces);
		}
		return jCheckBoxNumberOfPlaces;
	}

	/**
	 * This method initializes jCheckBoxNumberOfArcs
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfArcs() {
		if (jCheckBoxNumberOfArcs == null) {
			jCheckBoxNumberOfArcs = new JCheckBox();
			jCheckBoxNumberOfArcs.setBounds(new Rectangle(12, 304, 118, 21));
			jCheckBoxNumberOfArcs.setText(resourcesManager.getString("ModelMetrics.numofarc"));
			jCheckBoxNumberOfArcs.setSelected(recordNumberOfArcs);
		}
		return jCheckBoxNumberOfArcs;
	}

	/**
	 * This method initializes jCheckBoxNumberOfAndJoin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfAndJoin() {
		if (jCheckBoxNumberOfAndJoin == null) {
			jCheckBoxNumberOfAndJoin = new JCheckBox();
			jCheckBoxNumberOfAndJoin
					.setBounds(new Rectangle(187, 346, 149, 21));
			jCheckBoxNumberOfAndJoin.setText(resourcesManager.getString("ModelMetrics.numofaj"));
			jCheckBoxNumberOfAndJoin.setSelected(recordNumberOfAndJoin);
		}
		return jCheckBoxNumberOfAndJoin;
	}

	/**
	 * This method initializes jCheckBoxNumberOfAndSplit
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfAndSplit() {
		if (jCheckBoxNumberOfAndSplit == null) {
			jCheckBoxNumberOfAndSplit = new JCheckBox();
			jCheckBoxNumberOfAndSplit
					.setBounds(new Rectangle(12, 346, 142, 21));
			jCheckBoxNumberOfAndSplit.setText(resourcesManager.getString("ModelMetrics.numofas"));
			jCheckBoxNumberOfAndSplit.setSelected(recordNumberOfAndSplit);
		}
		return jCheckBoxNumberOfAndSplit;
	}

	/**
	 * This method initializes jCheckBoxNumberOfXORSplit
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfXORSplit() {
		if (jCheckBoxNumberOfXORSplit == null) {
			jCheckBoxNumberOfXORSplit = new JCheckBox();
			jCheckBoxNumberOfXORSplit
					.setBounds(new Rectangle(12, 367, 146, 21));
			jCheckBoxNumberOfXORSplit.setText(resourcesManager.getString("ModelMetrics.numofxs"));
			jCheckBoxNumberOfXORSplit.setSelected(recordNumberOfXORSplit);
		}
		return jCheckBoxNumberOfXORSplit;
	}

	/**
	 * This method initializes jCheckBoxNumberOfXORJoin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfXORJoin() {
		if (jCheckBoxNumberOfXORJoin == null) {
			jCheckBoxNumberOfXORJoin = new JCheckBox();
			jCheckBoxNumberOfXORJoin
					.setBounds(new Rectangle(187, 367, 142, 21));
			jCheckBoxNumberOfXORJoin.setText(resourcesManager.getString("ModelMetrics.numofxj"));
			jCheckBoxNumberOfXORJoin.setSelected(recordNumberOfXORJoin);
		}
		return jCheckBoxNumberOfXORJoin;
	}

	/**
	 * This method initializes jCheckBoxNumberOfTARs
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfTARs() {
		if (jCheckBoxNumberOfTARs == null) {
			jCheckBoxNumberOfTARs = new JCheckBox();
			jCheckBoxNumberOfTARs.setBounds(new Rectangle(186, 408, 119, 21));
			jCheckBoxNumberOfTARs.setText(resourcesManager.getString("ModelMetrics.numoftar"));
			jCheckBoxNumberOfTARs.setSelected(recordNumberOfTARs);
		}
		return jCheckBoxNumberOfTARs;
	}

	/**
	 * This method initializes jCheckBoxDensity
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDensity() {
		if (jCheckBoxDensity == null) {
			jCheckBoxDensity = new JCheckBox();
			jCheckBoxDensity.setBounds(new Rectangle(187, 304, 69, 21));
			jCheckBoxDensity.setText(resourcesManager.getString("ModelMetrics.density"));
			jCheckBoxDensity.setSelected(recordDensity);
		}
		return jCheckBoxDensity;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeStateSpace
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeStateSpace() {
		if (jCheckBoxAnalyzeStateSpace == null) {
			jCheckBoxAnalyzeStateSpace = new JCheckBox();
			jCheckBoxAnalyzeStateSpace
					.setBounds(new Rectangle(11, 408, 144, 21));
			jCheckBoxAnalyzeStateSpace.setText(resourcesManager.getString("ModelMetrics.ass"));
			jCheckBoxAnalyzeStateSpace.setSelected(recordStateSpaceAnalysis);
		}
		return jCheckBoxAnalyzeStateSpace;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeAndSplitDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeAndSplitDegree() {
		if (jCheckBoxAnalyzeAndSplitDegree == null) {
			jCheckBoxAnalyzeAndSplitDegree = new JCheckBox();
			jCheckBoxAnalyzeAndSplitDegree.setBounds(new Rectangle(11, 429,
					169, 21));
			jCheckBoxAnalyzeAndSplitDegree.setText(resourcesManager.getString("ModelMetrics.andSplitDegree"));
			jCheckBoxAnalyzeAndSplitDegree
					.setSelected(recordAndSplitDegreeAnalysis);
		}
		return jCheckBoxAnalyzeAndSplitDegree;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeAndJoinSplit
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeAndJoinDegree() {
		if (jCheckBoxAnalyzeAndJoinDegree == null) {
			jCheckBoxAnalyzeAndJoinDegree = new JCheckBox();
			jCheckBoxAnalyzeAndJoinDegree.setBounds(new Rectangle(186, 429,
					167, 21));
			jCheckBoxAnalyzeAndJoinDegree.setText(resourcesManager.getString("ModelMetrics.andJoinDegree"));
			jCheckBoxAnalyzeAndJoinDegree
					.setSelected(recordAndJoinDegreeAnalysis);
		}
		return jCheckBoxAnalyzeAndJoinDegree;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeXORSplitDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeXORSplitDegree() {
		if (jCheckBoxAnalyzeXORSplitDegree == null) {
			jCheckBoxAnalyzeXORSplitDegree = new JCheckBox();
			jCheckBoxAnalyzeXORSplitDegree.setBounds(new Rectangle(11, 450,
					172, 21));
			jCheckBoxAnalyzeXORSplitDegree.setText(resourcesManager.getString("ModelMetrics.XORSplitDegree"));
			jCheckBoxAnalyzeXORSplitDegree
					.setSelected(recordXORSplitDegreeAnalysis);
		}
		return jCheckBoxAnalyzeXORSplitDegree;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeXORJoinDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeXORJoinDegree() {
		if (jCheckBoxAnalyzeXORJoinDegree == null) {
			jCheckBoxAnalyzeXORJoinDegree = new JCheckBox();
			jCheckBoxAnalyzeXORJoinDegree.setBounds(new Rectangle(186, 450,
					169, 21));
			jCheckBoxAnalyzeXORJoinDegree.setText(resourcesManager.getString("ModelMetrics.XORJoinDegree"));
			jCheckBoxAnalyzeXORJoinDegree
					.setSelected(recordXORJoinDegreeAnalysis);
		}
		return jCheckBoxAnalyzeXORJoinDegree;
	}

	private void analyzeYAWLInDatabase() {
		try {
			// write report header
			FileWriter fw = new FileWriter(jTextFieldReportFileName.getText(),
					false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("process name,");
			if (jCheckBoxNumberOfTransitions.isSelected()) {
				bw.write("number of tasks,");
			}
			if (jCheckBoxNumberOfPlaces.isSelected()) {
				bw.write("number of conditions,");
			}
			if (jCheckBoxNumberOfArcs.isSelected()) {
				bw.write("number of arcs,");
			}
			if (jCheckBoxDensity.isSelected()) {
				bw.write("density,");
			}
			if (jCheckBoxMaxInDegree.isSelected()) {
				bw.write("max inDegree,");
			}
			if (jCheckBoxMaxOutDegree.isSelected()) {
				bw.write("max outDegree,");
			}
			if (jCheckBoxNumberOfAndSplit.isSelected()) {
				bw.write("number of and-split,");
			}
			if (jCheckBoxNumberOfAndJoin.isSelected()) {
				bw.write("number of and-join,");
			}
			if (jCheckBoxNumberOfXORSplit.isSelected()) {
				bw.write("number of xor-split,");
			}
			if (jCheckBoxNumberOfXORJoin.isSelected()) {
				bw.write("number of xor-join,");
			}
			if (jCheckBoxNumberOfTARs.isSelected()) {
				bw.write("number of tars,");
			}
			if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
				bw.write("number of and-split,");
				bw.write("min degree of and-split,");
				bw.write("max degree of and-split,");
				bw.write("average degree of and-split,");
				bw.write("stdev degree of and-split,");
			}
			if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
				bw.write("number of and-join,");
				bw.write("min degree of and-join,");
				bw.write("max degree of and-join,");
				bw.write("average degree of and-join,");
				bw.write("stdev degree of and-join,");
			}
			if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
				bw.write("number of xor-split,");
				bw.write("min degree of xor-split,");
				bw.write("max degree of xor-split,");
				bw.write("average degree of xor-split,");
				bw.write("stdev degree of xor-split,");
			}
			if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
				bw.write("number of xor-join,");
				bw.write("min degree of xor-join,");
				bw.write("max degree of xor-join,");
				bw.write("average degree of xor-join,");
				bw.write("stdev degree of xor-join,");
			}
			if (jCheckBoxAnalyzeStateSpace.isSelected()) {
				bw.write("number of state in state-space,");
				bw.write("number of arcs in state-space,");
			}
			if (jCheckBoxNumberOfORSplit.isSelected()) {
				bw.write("number of or-split,");
			}
			if (jCheckBoxNumberOfORJoin.isSelected()) {
				bw.write("number of or-join,");
			}
			if (jCheckBoxAnalyzeORSplitDegree.isSelected()) {
				bw.write("number of or-split,");
				bw.write("min degree of or-split,");
				bw.write("max degree of or-split,");
				bw.write("average degree of or-split,");
				bw.write("stdev degree of or-split,");
			}
			if (jCheckBoxAnalyzeORJoinDegree.isSelected()) {
				bw.write("number of or-join,");
				bw.write("min degree of or-join,");
				bw.write("max degree of or-join,");
				bw.write("average degree of or-join,");
				bw.write("stdev degree of or-join,");
			}
			bw.newLine();
			// write report
			DataManager dm = DataManager.getInstance();
			String dbName = dm.getDBName();
			int fetchSize = dm.getFetchSize();
			// ResultSet rs = dm
			// .executeSelectSQL("select name, process_id from process");

			// ResultSet rs = dm
			// .executeSelectSQL(
			// "select name, process.process_id as id from process, petrinet where process.process_id = petrinet.process_id order by name",
			// page * limit, limit, 1);
			ResultSet rs = dm.executeSelectSQL(
					"select name, process_id from process where type='"
							+ ProcessObject.TYPEYAWL + "' order by process_id",
					offset, limit, fetchSize);
			while (rs.next()) {
				try {
					String processName = rs.getString("name");
					bw.write(processName + ",");
					long process_id = rs.getLong("process_id");
					YNet net = YAWLUtil.getYNetFromDefinition(dm
							.getProcessDefinitionBytes(process_id));
					// PetriNet pn = null;

					// long process_id = rs.getLong("id");
					//
					// pn = dm.getProcessPetriNet(process_id);

					// if (dbName.equalsIgnoreCase("postgresql")
					// || dbName.equalsIgnoreCase("mysql")) {
					// String str = rs.getString("pnml");
					// byte[] temp = str.getBytes();
					// pn = PetriNetUtil.getPetriNetFromPnmlBytes(temp);
					// } else if (dbName.equalsIgnoreCase("derby")) {
					// InputStream is = rs.getAsciiStream("pnml");
					// pn = PetriNetUtil.getPetriNetFromPnml(is);
					// is.close();
					// } else {
					// System.out.println(dbName + " unsupported");
					// System.exit(-1);
					// }

					YAWLMetrics ym = new YAWLMetrics(net);
					if (jCheckBoxNumberOfTransitions.isSelected()) {
						bw.write(ym.getNumberOfTasks() + ",");
					}
					if (jCheckBoxNumberOfPlaces.isSelected()) {
						bw.write(ym.getNumberOfConditions() + ",");
					}
					if (jCheckBoxNumberOfArcs.isSelected()) {
						bw.write(ym.getNumberOfArcs() + ",");
					}
					if (jCheckBoxDensity.isSelected()) {
						bw.write(ym.getDensity() + ",");
					}
					if (jCheckBoxMaxInDegree.isSelected()) {
						bw.write(ym.getMaxInDegree() + ",");
					}
					if (jCheckBoxMaxOutDegree.isSelected()) {
						bw.write(ym.getMaxOutDegree() + ",");
					}
					if (jCheckBoxNumberOfAndSplit.isSelected()) {
						bw.write(ym.getNumberOfANDSplit() + ",");
					}
					if (jCheckBoxNumberOfAndJoin.isSelected()) {
						bw.write(ym.getNumberOfANDJoin() + ",");
					}
					if (jCheckBoxNumberOfXORSplit.isSelected()) {
						bw.write(ym.getNumberOfXORSplit() + ",");
					}
					if (jCheckBoxNumberOfXORJoin.isSelected()) {
						bw.write(ym.getNumberOfXORJoin() + ",");
					}
					if (jCheckBoxNumberOfTARs.isSelected()) {
						bw.write(ym.getNumberOfTARs() + ",");
					}
					if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
						float[] ret = ym.analyzeANDSplitDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
						float[] ret = ym.analyzeANDJoinDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
						float[] ret = ym.analyzeXORSplitDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
						float[] ret = ym.analyzeXORJoinDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeStateSpace.isSelected()) {
						int[] ret = ym.analyzeStateSpace();
						bw.write(ret[0] + ",");
						bw.write(ret[1] + ",");
					}
					if (jCheckBoxNumberOfORSplit.isSelected()) {
						bw.write(ym.getNumberOfORSplit() + ",");
					}
					if (jCheckBoxNumberOfORJoin.isSelected()) {
						bw.write(ym.getNumberOfORJoin() + ",");
					}
					if (jCheckBoxAnalyzeORSplitDegree.isSelected()) {
						float[] ret = ym.analyzeORSplitDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeORJoinDegree.isSelected()) {
						float[] ret = ym.analyzeORJoinDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					bw.newLine();
				} catch (Exception e1) {
					e1.printStackTrace();
					bw.newLine();
				}
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();

			// close file
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyzePetriNetsInDatabase() {
		try {
			// write report header
			FileWriter fw = new FileWriter(jTextFieldReportFileName.getText(),
					false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("process name,");
			if (jCheckBoxNumberOfTransitions.isSelected()) {
				bw.write("number of transitions,");
			}
			if (jCheckBoxNumberOfPlaces.isSelected()) {
				bw.write("number of places,");
			}
			if (jCheckBoxNumberOfArcs.isSelected()) {
				bw.write("number of arcs,");
			}
			if (jCheckBoxDensity.isSelected()) {
				bw.write("density,");
			}
			if (jCheckBoxMaxInDegree.isSelected()) {
				bw.write("max inDegree,");
			}
			if (jCheckBoxMaxOutDegree.isSelected()) {
				bw.write("max outDegree,");
			}
			if (jCheckBoxNumberOfAndSplit.isSelected()) {
				bw.write("number of and-split,");
			}
			if (jCheckBoxNumberOfAndJoin.isSelected()) {
				bw.write("number of and-join,");
			}
			if (jCheckBoxNumberOfXORSplit.isSelected()) {
				bw.write("number of xor-split,");
			}
			if (jCheckBoxNumberOfXORJoin.isSelected()) {
				bw.write("number of xor-join,");
			}
			if (jCheckBoxNumberOfTARs.isSelected()) {
				bw.write("number of tars,");
			}
			if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
				bw.write("number of and-split,");
				bw.write("min degree of and-split,");
				bw.write("max degree of and-split,");
				bw.write("average degree of and-split,");
				bw.write("stdev degree of and-split,");
			}
			if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
				bw.write("number of and-join,");
				bw.write("min degree of and-join,");
				bw.write("max degree of and-join,");
				bw.write("average degree of and-join,");
				bw.write("stdev degree of and-join,");
			}
			if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
				bw.write("number of xor-split,");
				bw.write("min degree of xor-split,");
				bw.write("max degree of xor-split,");
				bw.write("average degree of xor-split,");
				bw.write("stdev degree of xor-split,");
			}
			if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
				bw.write("number of xor-join,");
				bw.write("min degree of xor-join,");
				bw.write("max degree of xor-join,");
				bw.write("average degree of xor-join,");
				bw.write("stdev degree of xor-join,");
			}
			if (jCheckBoxAnalyzeStateSpace.isSelected()) {
				bw.write("number of state in state-space,");
				bw.write("number of arcs in state-space,");
			}
			bw.newLine();
			// write report
			DataManager dm = DataManager.getInstance();
			String dbName = dm.getDBName();
			int fetchSize = dm.getFetchSize();
			// ResultSet rs = dm
			// .executeSelectSQL("select name, process_id from process");

			// ResultSet rs = dm
			// .executeSelectSQL(
			// "select name, process.process_id as id from process, petrinet where process.process_id = petrinet.process_id order by name",
			// page * limit, limit, 1);
			ResultSet rs = dm.executeSelectSQL(
					"select name, process_id from process where type='"
							+ ProcessObject.TYPEPNML + "' order by process_id",
					offset, limit, fetchSize);
			while (rs.next()) {
				try {
					String processName = rs.getString("name");
					bw.write(processName + ",");
					long process_id = rs.getLong("process_id");
					PetriNet pn = PetriNetUtil.getPetriNetFromPnmlBytes(dm
							.getProcessDefinitionBytes(process_id));
					// PetriNet pn = null;

					// long process_id = rs.getLong("id");
					//
					// pn = dm.getProcessPetriNet(process_id);

					// if (dbName.equalsIgnoreCase("postgresql")
					// || dbName.equalsIgnoreCase("mysql")) {
					// String str = rs.getString("pnml");
					// byte[] temp = str.getBytes();
					// pn = PetriNetUtil.getPetriNetFromPnmlBytes(temp);
					// } else if (dbName.equalsIgnoreCase("derby")) {
					// InputStream is = rs.getAsciiStream("pnml");
					// pn = PetriNetUtil.getPetriNetFromPnml(is);
					// is.close();
					// } else {
					// System.out.println(dbName + " unsupported");
					// System.exit(-1);
					// }

					PetriNetMetrics pnm = new PetriNetMetrics(pn);
					if (jCheckBoxNumberOfTransitions.isSelected()) {
						bw.write(pnm.getNumberOfTransitions() + ",");
					}
					if (jCheckBoxNumberOfPlaces.isSelected()) {
						bw.write(pnm.getNumberOfPlaces() + ",");
					}
					if (jCheckBoxNumberOfArcs.isSelected()) {
						bw.write(pnm.getNumberOfArcs() + ",");
					}
					if (jCheckBoxDensity.isSelected()) {
						bw.write(pnm.getDensity() + ",");
					}
					if (jCheckBoxMaxInDegree.isSelected()) {
						bw.write(pnm.getMaxInDegree() + ",");
					}
					if (jCheckBoxMaxOutDegree.isSelected()) {
						bw.write(pnm.getMaxOutDegree() + ",");
					}
					if (jCheckBoxNumberOfAndSplit.isSelected()) {
						bw.write(pnm.getNumberOfANDSplit() + ",");
					}
					if (jCheckBoxNumberOfAndJoin.isSelected()) {
						bw.write(pnm.getNumberOfANDJoin() + ",");
					}
					if (jCheckBoxNumberOfXORSplit.isSelected()) {
						bw.write(pnm.getNumberOfXORSplit() + ",");
					}
					if (jCheckBoxNumberOfXORJoin.isSelected()) {
						bw.write(pnm.getNumberOfXORJoin() + ",");
					}
					if (jCheckBoxNumberOfTARs.isSelected()) {
						bw.write(pnm.getNumberOfTARs() + ",");
					}
					if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
						float[] ret = pnm.analyzeANDSplitDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
						float[] ret = pnm.analyzeANDJoinDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
						float[] ret = pnm.analyzeXORSplitDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
						float[] ret = pnm.analyzeXORJoinDegree();
						bw.write((int) ret[0] + ",");
						bw.write((int) ret[1] + ",");
						bw.write((int) ret[2] + ",");
						bw.write(ret[3] + ",");
						bw.write(ret[4] + ",");
					}
					if (jCheckBoxAnalyzeStateSpace.isSelected()) {
						int[] ret = pnm.analyzeStateSpace();
						bw.write(ret[0] + ",");
						bw.write(ret[1] + ",");
					}
					pn.destroyPetriNet();
					bw.newLine();
				} catch (Exception e1) {
					e1.printStackTrace();
					bw.newLine();
				}
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();

			// close file
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyzeYAWLInFileSystem() {

		// write report header
		try {
			FileWriter fw = new FileWriter(jTextFieldReportFileName.getText(),
					false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("process name,");
			if (jCheckBoxNumberOfTransitions.isSelected()) {
				bw.write("number of transitions,");
			}
			if (jCheckBoxNumberOfPlaces.isSelected()) {
				bw.write("number of places,");
			}
			if (jCheckBoxNumberOfArcs.isSelected()) {
				bw.write("number of arcs,");
			}
			if (jCheckBoxDensity.isSelected()) {
				bw.write("density,");
			}
			if (jCheckBoxMaxInDegree.isSelected()) {
				bw.write("max inDegree,");
			}
			if (jCheckBoxMaxOutDegree.isSelected()) {
				bw.write("max outDegree,");
			}
			if (jCheckBoxNumberOfAndSplit.isSelected()) {
				bw.write("number of and-split,");
			}
			if (jCheckBoxNumberOfAndJoin.isSelected()) {
				bw.write("number of and-join,");
			}
			if (jCheckBoxNumberOfXORSplit.isSelected()) {
				bw.write("number of xor-split,");
			}
			if (jCheckBoxNumberOfXORJoin.isSelected()) {
				bw.write("number of xor-join,");
			}
			if (jCheckBoxNumberOfTARs.isSelected()) {
				bw.write("number of tars,");
			}
			if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
				bw.write("number of and-split,");
				bw.write("min degree of and-split,");
				bw.write("max degree of and-split,");
				bw.write("average degree of and-split,");
				bw.write("stdev degree of and-split,");
			}
			if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
				bw.write("number of and-join,");
				bw.write("min degree of and-join,");
				bw.write("max degree of and-join,");
				bw.write("average degree of and-join,");
				bw.write("stdev degree of and-join,");
			}
			if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
				bw.write("number of xor-split,");
				bw.write("min degree of xor-split,");
				bw.write("max degree of xor-split,");
				bw.write("average degree of xor-split,");
				bw.write("stdev degree of xor-split,");
			}
			if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
				bw.write("number of xor-join,");
				bw.write("min degree of xor-join,");
				bw.write("max degree of xor-join,");
				bw.write("average degree of xor-join,");
				bw.write("stdev degree of xor-join,");
			}
			if (jCheckBoxAnalyzeStateSpace.isSelected()) {
				bw.write("number of state in state-space,");
				bw.write("number of arcs in state-space,");
			}
			if (jCheckBoxNumberOfORSplit.isSelected()) {
				bw.write("number of or-split,");
			}
			if (jCheckBoxNumberOfORJoin.isSelected()) {
				bw.write("number of or-join,");
			}
			if (jCheckBoxAnalyzeORSplitDegree.isSelected()) {
				bw.write("number of or-split,");
				bw.write("min degree of or-split,");
				bw.write("max degree of or-split,");
				bw.write("average degree of or-split,");
				bw.write("stdev degree of or-split,");
			}
			if (jCheckBoxAnalyzeORJoinDegree.isSelected()) {
				bw.write("number of or-join,");
				bw.write("min degree of or-join,");
				bw.write("max degree of or-join,");
				bw.write("average degree of or-join,");
				bw.write("stdev degree of or-join,");
			}
			bw.newLine();

			// write report
			File dir = new File(jTextFieldSourceDirectory.getText());
			for (File f : dir.listFiles()) {
				String fileName = f.getName();
				if (fileName.endsWith(".yawl")) {
					String processName = fileName;

					try {
						YNet net = YAWLUtil.getYNetFromFile(f.getPath());
						bw.write(processName + ",");
						YAWLMetrics ym = new YAWLMetrics(net);
						if (jCheckBoxNumberOfTransitions.isSelected()) {
							bw.write(ym.getNumberOfTasks() + ",");
						}
						if (jCheckBoxNumberOfPlaces.isSelected()) {
							bw.write(ym.getNumberOfConditions() + ",");
						}
						if (jCheckBoxNumberOfArcs.isSelected()) {
							bw.write(ym.getNumberOfArcs() + ",");
						}
						if (jCheckBoxDensity.isSelected()) {
							bw.write(ym.getDensity() + ",");
						}
						if (jCheckBoxMaxInDegree.isSelected()) {
							bw.write(ym.getMaxInDegree() + ",");
						}
						if (jCheckBoxMaxOutDegree.isSelected()) {
							bw.write(ym.getMaxOutDegree() + ",");
						}
						if (jCheckBoxNumberOfAndSplit.isSelected()) {
							bw.write(ym.getNumberOfANDSplit() + ",");
						}
						if (jCheckBoxNumberOfAndJoin.isSelected()) {
							bw.write(ym.getNumberOfANDJoin() + ",");
						}
						if (jCheckBoxNumberOfXORSplit.isSelected()) {
							bw.write(ym.getNumberOfXORSplit() + ",");
						}
						if (jCheckBoxNumberOfXORJoin.isSelected()) {
							bw.write(ym.getNumberOfXORJoin() + ",");
						}
						if (jCheckBoxNumberOfTARs.isSelected()) {
							bw.write(ym.getNumberOfTARs() + ",");
						}
						if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
							float[] ret = ym.analyzeANDSplitDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
							float[] ret = ym.analyzeANDJoinDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
							float[] ret = ym.analyzeXORSplitDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
							float[] ret = ym.analyzeXORJoinDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						if (jCheckBoxAnalyzeStateSpace.isSelected()) {
							int[] ret = ym.analyzeStateSpace();
							bw.write(ret[0] + ",");
							bw.write(ret[1] + ",");
						}
						if (jCheckBoxNumberOfORSplit.isSelected()) {
							bw.write(ym.getNumberOfORSplit() + ",");
						}
						if (jCheckBoxNumberOfORJoin.isSelected()) {
							bw.write(ym.getNumberOfORJoin() + ",");
						}
						if (jCheckBoxAnalyzeORSplitDegree.isSelected()) {
							float[] ret = ym.analyzeORSplitDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						if (jCheckBoxAnalyzeORJoinDegree.isSelected()) {
							float[] ret = ym.analyzeORJoinDegree();
							bw.write((int) ret[0] + ",");
							bw.write((int) ret[1] + ",");
							bw.write((int) ret[2] + ",");
							bw.write(ret[3] + ",");
							bw.write(ret[4] + ",");
						}
						bw.newLine();
					} catch (Exception e1) {
						e1.printStackTrace();
						bw.newLine();
					}

				}
			}

			// close file
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyzePetriNetsInFileSystem() {

		// write report header
		try {
			FileWriter fw = new FileWriter(jTextFieldReportFileName.getText(),
					false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("process name,");
			if (jCheckBoxNumberOfTransitions.isSelected()) {
				bw.write("number of transitions,");
			}
			if (jCheckBoxNumberOfPlaces.isSelected()) {
				bw.write("number of places,");
			}
			if (jCheckBoxNumberOfArcs.isSelected()) {
				bw.write("number of arcs,");
			}
			if (jCheckBoxDensity.isSelected()) {
				bw.write("density,");
			}
			if (jCheckBoxMaxInDegree.isSelected()) {
				bw.write("max inDegree,");
			}
			if (jCheckBoxMaxOutDegree.isSelected()) {
				bw.write("max outDegree,");
			}
			if (jCheckBoxNumberOfAndSplit.isSelected()) {
				bw.write("number of and-split,");
			}
			if (jCheckBoxNumberOfAndJoin.isSelected()) {
				bw.write("number of and-join,");
			}
			if (jCheckBoxNumberOfXORSplit.isSelected()) {
				bw.write("number of xor-split,");
			}
			if (jCheckBoxNumberOfXORJoin.isSelected()) {
				bw.write("number of xor-join,");
			}
			if (jCheckBoxNumberOfTARs.isSelected()) {
				bw.write("number of tars,");
			}
			if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
				bw.write("number of and-split,");
				bw.write("min degree of and-split,");
				bw.write("max degree of and-split,");
				bw.write("average degree of and-split,");
				bw.write("stdev degree of and-split,");
			}
			if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
				bw.write("number of and-join,");
				bw.write("min degree of and-join,");
				bw.write("max degree of and-join,");
				bw.write("average degree of and-join,");
				bw.write("stdev degree of and-join,");
			}
			if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
				bw.write("number of xor-split,");
				bw.write("min degree of xor-split,");
				bw.write("max degree of xor-split,");
				bw.write("average degree of xor-split,");
				bw.write("stdev degree of xor-split,");
			}
			if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
				bw.write("number of xor-join,");
				bw.write("min degree of xor-join,");
				bw.write("max degree of xor-join,");
				bw.write("average degree of xor-join,");
				bw.write("stdev degree of xor-join,");
			}
			if (jCheckBoxAnalyzeStateSpace.isSelected()) {
				bw.write("number of state in state-space,");
				bw.write("number of arcs in state-space,");
			}
			if (jCheckBoxMismatch.isSelected()) {
				bw.write("mismatch,");
			if (jCheckBoxSequentiality.isSelected()) {
				bw.write("Sequentiality,");
			}
			if (jCheckBoxTS.isSelected()) {
				bw.write("TS,");
			}
			if (jCheckBoxCH.isSelected()) {
				bw.write("CH,");
			}
			if (jCheckBoxCFC.isSelected()) {
				bw.write("CFC,");
			}
			if (jCheckBoxCYC.isSelected()) {
				bw.write("CYC,");
			}
			if (jCheckBoxDiam.isSelected()) {
				bw.write("Diam,");
			}
			if (jCheckBoxSeparability.isSelected()) {
				bw.write("Separability,");
			}
			if (jCheckBoxStructuredness.isSelected()) {
				bw.write("Structuredness,");
			}
			if (jCheckBoxCNC.isSelected()) {
				bw.write("CNC,");
			}
			if (jCheckBoxMaxDegree.isSelected()) {
				bw.write("MaxDegree of conecetor,");
			}
			if (jCheckBoxAverageDegree.isSelected()) {
				bw.write("AverDegree of conector,");
			}
			if (jCheckBoxDepth.isSelected()) {
				bw.write("Depth");
			}
			bw.newLine();

				// write report
				File dir = new File(jTextFieldSourceDirectory.getText());
				for (File f : dir.listFiles()) {
					String fileName = f.getName();
					if (fileName.endsWith(".pnml")) {
						String processName = fileName;

						try {
							PetriNet pn = PetriNetUtil
									.getPetriNetFromPnmlFile(f.getPath());
							bw.write(processName + ",");
							PetriNetMetrics pnm = new PetriNetMetrics(pn);
							if (jCheckBoxNumberOfTransitions.isSelected()) {
								bw.write(pnm.getNumberOfTransitions() + ",");
							}
							if (jCheckBoxNumberOfPlaces.isSelected()) {
								bw.write(pnm.getNumberOfPlaces() + ",");
							}
							if (jCheckBoxNumberOfArcs.isSelected()) {
								bw.write(pnm.getNumberOfArcs() + ",");
							}
							if (jCheckBoxDensity.isSelected()) {
								bw.write(pnm.getDensity() + ",");
							}
							if (jCheckBoxMaxInDegree.isSelected()) {
								bw.write(pnm.getMaxInDegree() + ",");
							}
							if (jCheckBoxMaxOutDegree.isSelected()) {
								bw.write(pnm.getMaxOutDegree() + ",");
							}
							if (jCheckBoxNumberOfAndSplit.isSelected()) {
								bw.write(pnm.getNumberOfANDSplit() + ",");
							}
							if (jCheckBoxNumberOfAndJoin.isSelected()) {
								bw.write(pnm.getNumberOfANDJoin() + ",");
							}
							if (jCheckBoxNumberOfXORSplit.isSelected()) {
								bw.write(pnm.getNumberOfXORSplit() + ",");
							}
							if (jCheckBoxNumberOfXORJoin.isSelected()) {
								bw.write(pnm.getNumberOfXORJoin() + ",");
							}
							if (jCheckBoxNumberOfTARs.isSelected()) {
								bw.write(pnm.getNumberOfTARs() + ",");
							}
							if (jCheckBoxAnalyzeAndSplitDegree.isSelected()) {
								float[] ret = pnm.analyzeANDSplitDegree();
								bw.write((int) ret[0] + ",");
								bw.write((int) ret[1] + ",");
								bw.write((int) ret[2] + ",");
								bw.write(ret[3] + ",");
								bw.write(ret[4] + ",");
							}
							if (jCheckBoxAnalyzeAndJoinDegree.isSelected()) {
								float[] ret = pnm.analyzeANDJoinDegree();
								bw.write((int) ret[0] + ",");
								bw.write((int) ret[1] + ",");
								bw.write((int) ret[2] + ",");
								bw.write(ret[3] + ",");
								bw.write(ret[4] + ",");
							}
							if (jCheckBoxAnalyzeXORSplitDegree.isSelected()) {
								float[] ret = pnm.analyzeXORSplitDegree();
								bw.write((int) ret[0] + ",");
								bw.write((int) ret[1] + ",");
								bw.write((int) ret[2] + ",");
								bw.write(ret[3] + ",");
								bw.write(ret[4] + ",");
							}
							if (jCheckBoxAnalyzeXORJoinDegree.isSelected()) {
								float[] ret = pnm.analyzeXORJoinDegree();
								bw.write((int) ret[0] + ",");
								bw.write((int) ret[1] + ",");
								bw.write((int) ret[2] + ",");
								bw.write(ret[3] + ",");
								bw.write(ret[4] + ",");
							}
							if (jCheckBoxAnalyzeStateSpace.isSelected()) {
								int[] ret = pnm.analyzeStateSpace();
								bw.write(ret[0] + ",");
								bw.write(ret[1] + ",");
							}
							if (jCheckBoxMismatch.isSelected()) {
								bw.write(pnm.getMismatch() + ",");
							}
							if (jCheckBoxSequentiality.isSelected()) {
								bw.write(pnm.getSequentiality() + ",");
							}
							if (jCheckBoxTS.isSelected()) {
								bw.write(pnm.getTS() + ",");
							}
							if (jCheckBoxCH.isSelected()) {
								bw.write(pnm.getCH() + ",");
							}
							if (jCheckBoxCFC.isSelected()) {
								bw.write(pnm.getCFC() + ",");
							}		
							if (jCheckBoxCYC.isSelected()) {
								bw.write(pnm.getCYC() + ",");
							}	
							if (jCheckBoxDiam.isSelected()) {
								bw.write(pnm.getDiam() + ",");
							}
							if (jCheckBoxSeparability.isSelected()) {
								bw.write(pnm.getSeparability() + ",");
							}
							if (jCheckBoxStructuredness.isSelected()) {
								bw.write(pnm.getStructuredness() + ",");
							}
							if (jCheckBoxCNC.isSelected()) {
								bw.write(pnm.getCNC() + ",");
							}
							if (jCheckBoxMaxDegree.isSelected()) {
								bw.write(pnm.getMaxDegree() + ",");
							}
							if (jCheckBoxAverageDegree.isSelected()) {
								bw.write(pnm.getAverDegree() + ",");
							}
							if (jCheckBoxDepth.isSelected()) {
								bw.write(pnm.getDepth() + ",");
							}
							pn.destroyPetriNet();
							bw.newLine();
						} catch (Exception e1) {
							e1.printStackTrace();
							bw.newLine();
						}

					}
				}
			}

			// close file
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isParameterValid() {
		try {
			offset = Integer.parseInt(jTextFieldOffset.getText());
			limit = Integer.parseInt(jTextFieldLimit.getText());
			if (offset < 0) {
				return false;
			}
			if (limit < 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(123, 640, 102, 21));
			jButtonOK.setText(resourcesManager.getString("ModelMetrics.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!isParameterValid()) {
						JOptionPane.showMessageDialog(null,
								resourcesManager.getString("ModelMetrics.message"));
						return;
					}

					if (jComboBoxModelType.getSelectedItem().equals(
							ProcessObject.TYPEPNML)) {
						if (jRadioButtonDatabase.isSelected()) {
							analyzePetriNetsInDatabase();
						} else {
							analyzePetriNetsInFileSystem();
						}
					} else if (jComboBoxModelType.getSelectedItem().equals(
							ProcessObject.TYPEYAWL)) {
						if (jRadioButtonDatabase.isSelected()) {
							analyzeYAWLInDatabase();
						} else {
							analyzeYAWLInFileSystem();
						}
					}
					dispose();
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jCheckBoxMaxInDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMaxInDegree() {
		if (jCheckBoxMaxInDegree == null) {
			jCheckBoxMaxInDegree = new JCheckBox();
			jCheckBoxMaxInDegree.setBounds(new Rectangle(12, 325, 108, 21));
			jCheckBoxMaxInDegree.setText(resourcesManager.getString("ModelMetrics.maxInDegree"));
			jCheckBoxMaxInDegree.setSelected(recordMaxInDegree);
		}
		return jCheckBoxMaxInDegree;
	}

	/**
	 * This method initializes jCheckBoxMaxOutDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMaxOutDegree() {
		if (jCheckBoxMaxOutDegree == null) {
			jCheckBoxMaxOutDegree = new JCheckBox();
			jCheckBoxMaxOutDegree.setBounds(new Rectangle(187, 325, 117, 21));
			jCheckBoxMaxOutDegree.setText(resourcesManager.getString("ModelMetrics.maxOutDegree"));
			jCheckBoxMaxOutDegree.setSelected(recordMaxOutDegree);
		}
		return jCheckBoxMaxOutDegree;
	}

	/**
	 * @param owner
	 */
	public DlgModelMetrics(Frame owner) {
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
		// this.setSize(372, 562);
		resourcesManager = new ResourcesManager();
		this.setSize(392, 700);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(resourcesManager.getString("ModelMetrics.title"));
		this.setContentPane(getJContentPane());
	}

	private JCheckBox getJComboBoxMM() {
		// TODO Auto-generated method stub
		if (jCheckBoxMismatch == null) {
			jCheckBoxMismatch = new JCheckBox();
			jCheckBoxMismatch.setBounds(new Rectangle(11, 498, 167, 21));
			jCheckBoxMismatch.setText(resourcesManager.getString("ModelMetrics.mismatch"));
			jCheckBoxMismatch.setSelected(recordMismatch);
		}
		return jCheckBoxMismatch;
	}

	private JCheckBox getJComboBoxSequentiality() {
		// TODO Auto-generated method stub
		if (jCheckBoxSequentiality == null) {
			jCheckBoxSequentiality = new JCheckBox();
			jCheckBoxSequentiality.setBounds(new Rectangle(186, 498, 167, 21));
			jCheckBoxSequentiality.setText(resourcesManager.getString("ModelMetrics.sequentiality"));
			jCheckBoxSequentiality.setSelected(recordSequentiality);
		}
		return jCheckBoxSequentiality;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelModelType = new JLabel();
			jLabelModelType.setBounds(new Rectangle(3, 11, 72, 18));
			jLabelModelType.setText(resourcesManager.getString("ModelMetrics.modeltype"));
			jLabelLimit = new JLabel();
			jLabelLimit.setBounds(new Rectangle(228, 93, 29, 19));
			jLabelLimit.setText(resourcesManager.getString("ModelMetrics.limit"));
			jLabelOffset = new JLabel();
			jLabelOffset.setBounds(new Rectangle(99, 93, 37, 18));
			jLabelOffset.setText(resourcesManager.getString("ModelMetrics.offset"));
			jLabelMetrics = new JLabel();
			jLabelMetrics.setBounds(new Rectangle(3, 263, 49, 18));
			jLabelMetrics.setText(resourcesManager.getString("ModelMetrics.metrics"));
			jLabelExport = new JLabel();
			jLabelExport.setBounds(new Rectangle(3, 190, 82, 18));
			jLabelExport.setText(resourcesManager.getString("ModelMetrics.reportexport"));
			jLabelDataSource = new JLabel();
			jLabelDataSource.setBounds(new Rectangle(3, 70, 79, 18));
			jLabelDataSource.setText(resourcesManager.getString("ModelMetrics.datasource"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelDataSource, null);
			jContentPane.add(getJRadioButtonDatabase(), null);
			jContentPane.add(getJRadioButtonFileSystem(), null);
			jContentPane.add(getJButtonChooseDirectory(), null);
			jContentPane.add(getJTextFieldSourceDirectory(), null);
			jContentPane.add(jLabelExport, null);
			jContentPane.add(jLabelMetrics, null);
			jContentPane.add(getJButtonSaveReportFile(), null);
			jContentPane.add(getJTextFieldReportFileName(), null);
			jContentPane.add(getJCheckBoxNumberOfTransitions(), null);
			jContentPane.add(getJCheckBoxNumberOfPlaces(), null);
			jContentPane.add(getJCheckBoxNumberOfArcs(), null);
			jContentPane.add(getJCheckBoxNumberOfAndJoin(), null);
			jContentPane.add(getJCheckBoxNumberOfAndSplit(), null);
			jContentPane.add(getJCheckBoxNumberOfXORSplit(), null);
			jContentPane.add(getJCheckBoxNumberOfXORJoin(), null);
			jContentPane.add(getJCheckBoxNumberOfTARs(), null);
			jContentPane.add(getJCheckBoxDensity(), null);
			jContentPane.add(getJCheckBoxAnalyzeStateSpace(), null);
			jContentPane.add(getJCheckBoxAnalyzeAndSplitDegree(), null);
			jContentPane.add(getJCheckBoxAnalyzeAndJoinDegree(), null);
			jContentPane.add(getJCheckBoxAnalyzeXORSplitDegree(), null);
			jContentPane.add(getJCheckBoxAnalyzeXORJoinDegree(), null);
			jContentPane.add(getJButtonOK(), null);
			jContentPane.add(getJCheckBoxMaxInDegree(), null);
			jContentPane.add(getJCheckBoxMaxOutDegree(), null);
			jContentPane.add(jLabelOffset, null);
			jContentPane.add(getJTextFieldOffset(), null);
			jContentPane.add(jLabelLimit, null);
			jContentPane.add(getJTextFieldLimit(), null);
			jContentPane.add(getJCheckBoxNumberOfORSplit(), null);
			jContentPane.add(getJCheckBoxNumberOfORJoin(), null);
			jContentPane.add(getJCheckBoxAnalyzeORSplitDegree(), null);
			jContentPane.add(getJCheckBoxAnalyzeORJoinDegree(), null);
			jContentPane.add(getJComboBoxModelType(), null);
			jContentPane.add(jLabelModelType, null);
			jContentPane.add(getJComboBoxSequentiality(), null);
			jContentPane.add(getJComboBoxMM(), null);
			jContentPane.add(getJComboBoxTS(), null);
			jContentPane.add(getJComboBoxCFC(), null);
			jContentPane.add(getJComboBoxCH(), null);
			jContentPane.add(getJComboBoxCYC(), null);
			jContentPane.add(getJComboBoxDiam(), null);
			jContentPane.add(getJComboBoxSeparability(), null);
			jContentPane.add(getJComboBoxStructuredness(), null);
			jContentPane.add(getJComboBoxCNC(), null);
			jContentPane.add(getJComboBoxAverageDegree(), null);
			jContentPane.add(getJComboBoxMaxDegree(), null);
			jContentPane.add(getJComboBoxDepth(), null);
		}
		return jContentPane;
	}

	private JCheckBox getJComboBoxDepth() {
		// TODO Auto-generated method stub
		if (jCheckBoxDepth == null) {
			jCheckBoxDepth = new JCheckBox();
			jCheckBoxDepth.setBounds(new Rectangle(11,618,167,21));
			jCheckBoxDepth.setText(resourcesManager.getString("ModelMetrics.depth"));
			jCheckBoxDepth.setSelected(recordDepth);
		}
		return jCheckBoxDepth;
	}

	private JCheckBox getJComboBoxMaxDegree() {
		// TODO Auto-generated method stub
		if (jCheckBoxMaxDegree == null) {
			jCheckBoxMaxDegree = new JCheckBox();
			jCheckBoxMaxDegree.setBounds(new Rectangle(11,598,167,21));
			jCheckBoxMaxDegree.setText(resourcesManager.getString("ModelMetrics.maxdegree"));
			jCheckBoxMaxDegree.setSelected(recordMaxDegree);
		}
		return jCheckBoxMaxDegree;
	}

	private JCheckBox getJComboBoxAverageDegree() {
		// TODO Auto-generated method stub
		if (jCheckBoxAverageDegree == null) {
			jCheckBoxAverageDegree = new JCheckBox();
			jCheckBoxAverageDegree.setBounds(new Rectangle(186,598,167,21));
			jCheckBoxAverageDegree.setText(resourcesManager.getString("ModelMetrics.avedegree"));
			jCheckBoxAverageDegree.setSelected(recordAverageDegree);
		}
		return jCheckBoxAverageDegree;
	}

	private JCheckBox getJComboBoxCNC() {
		// TODO Auto-generated method stub
		if (jCheckBoxCNC == null) {
			jCheckBoxCNC = new JCheckBox();
			jCheckBoxCNC.setBounds(new Rectangle(186,578,167,21));
			jCheckBoxCNC.setText("CNC");
			jCheckBoxCNC.setSelected(recordCNC);
		}
		return jCheckBoxCNC;
	}

	private JCheckBox getJComboBoxStructuredness() {
		// TODO Auto-generated method stub
		if (jCheckBoxStructuredness == null) {
			jCheckBoxStructuredness = new JCheckBox();
			jCheckBoxStructuredness.setBounds(new Rectangle(11,578,167,21));
			jCheckBoxStructuredness.setText(resourcesManager.getString("ModelMetrics.structureness"));
			jCheckBoxStructuredness.setSelected(recordStructuredness);
		}
		return jCheckBoxStructuredness;
	}

	private JCheckBox getJComboBoxSeparability() {
		// TODO Auto-generated method stub
		if (jCheckBoxSeparability == null) {
			jCheckBoxSeparability = new JCheckBox();
			jCheckBoxSeparability.setBounds(new Rectangle(186,558,167,21));
			jCheckBoxSeparability.setText(resourcesManager.getString("ModelMetrics.separability"));
			jCheckBoxSeparability.setSelected(recordSeparability);
		}
		return jCheckBoxSeparability;
	}

	private JCheckBox getJComboBoxDiam() {
		// TODO Auto-generated method stub
		if (jCheckBoxDiam == null) {
			jCheckBoxDiam = new JCheckBox();
			jCheckBoxDiam.setBounds(new Rectangle(11,558,167,21));
			jCheckBoxDiam.setText(resourcesManager.getString("ModelMetrics.diam"));
			jCheckBoxDiam.setSelected(recordDiam);
		}
		return jCheckBoxDiam;
	}

	private JCheckBox getJComboBoxCYC() {
		// TODO Auto-generated method stub
		if (jCheckBoxCYC == null) {
			jCheckBoxCYC = new JCheckBox();
			jCheckBoxCYC.setBounds(new Rectangle(186,538,167,21));
			jCheckBoxCYC.setText("CYC");
			jCheckBoxCYC.setSelected(recordCYC);
		}
		return jCheckBoxCYC;
	}

	private JCheckBox getJComboBoxCH() {
		// TODO Auto-generated method stub
		if (jCheckBoxCH == null) {
			jCheckBoxCH = new JCheckBox();
			jCheckBoxCH.setBounds(new Rectangle(11, 518, 167, 21));
			jCheckBoxCH.setText("CH");
			jCheckBoxCH.setSelected(recordCH);
		}
		return jCheckBoxCH;
	}

	private JCheckBox getJComboBoxCFC() {
		// TODO Auto-generated method stub
		if (jCheckBoxCFC == null) {
			jCheckBoxCFC = new JCheckBox();
			jCheckBoxCFC.setBounds(new Rectangle(186, 518, 167, 21));
			jCheckBoxCFC.setText("CFC");
			jCheckBoxCFC.setSelected(recordCFC);
		}
		return jCheckBoxCFC;
	}

	private JCheckBox getJComboBoxTS() {
		// TODO Auto-generated method stub
		if (jCheckBoxTS == null) {
			jCheckBoxTS = new JCheckBox();
			jCheckBoxTS.setBounds(new Rectangle(11, 538, 167, 21));
			jCheckBoxTS.setText("TS");
			jCheckBoxTS.setSelected(recordTS);
		}
		return jCheckBoxTS;
	}

	/**
	 * This method initializes jTextFieldOffset
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOffset() {
		if (jTextFieldOffset == null) {
			jTextFieldOffset = new JTextField();
			jTextFieldOffset.setBounds(new Rectangle(136, 92, 82, 21));
			jTextFieldOffset.setText(String.valueOf(offset));
		}
		return jTextFieldOffset;
	}

	/**
	 * This method initializes jTextFieldLimit
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLimit() {
		if (jTextFieldLimit == null) {
			jTextFieldLimit = new JTextField();
			jTextFieldLimit.setBounds(new Rectangle(255, 92, 98, 21));
			jTextFieldLimit.setText(String.valueOf(limit));
		}
		return jTextFieldLimit;
	}

	/**
	 * This method initializes jCheckBoxNumberOfORSplit
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfORSplit() {
		if (jCheckBoxNumberOfORSplit == null) {
			jCheckBoxNumberOfORSplit = new JCheckBox();
			jCheckBoxNumberOfORSplit.setBounds(new Rectangle(12, 387, 136, 21));
			jCheckBoxNumberOfORSplit.setText(resourcesManager.getString("ModelMetrics.nOrSplit"));
			jCheckBoxNumberOfORSplit.setSelected(recordNumberOfORSplit);
			jCheckBoxNumberOfORSplit.setEnabled(false);
		}
		return jCheckBoxNumberOfORSplit;
	}

	/**
	 * This method initializes jCheckBoxNumberOfORJoin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumberOfORJoin() {
		if (jCheckBoxNumberOfORJoin == null) {
			jCheckBoxNumberOfORJoin = new JCheckBox();
			jCheckBoxNumberOfORJoin.setBounds(new Rectangle(187, 387, 138, 21));
			jCheckBoxNumberOfORJoin.setText(resourcesManager.getString("ModelMetrics.nOrJoin"));
			jCheckBoxNumberOfORJoin.setSelected(recordNumberOfORJoin);
			jCheckBoxNumberOfORJoin.setEnabled(false);
		}
		return jCheckBoxNumberOfORJoin;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeORSplitDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeORSplitDegree() {
		if (jCheckBoxAnalyzeORSplitDegree == null) {
			jCheckBoxAnalyzeORSplitDegree = new JCheckBox();
			jCheckBoxAnalyzeORSplitDegree.setBounds(new Rectangle(11, 474, 168,
					21));
			jCheckBoxAnalyzeORSplitDegree.setText(resourcesManager.getString("ModelMetrics.analyzeOrSplitDegree"));
			jCheckBoxAnalyzeORSplitDegree
					.setSelected(recordORSplitDegreeAnalysis);
			jCheckBoxAnalyzeORSplitDegree.setEnabled(false);
		}
		return jCheckBoxAnalyzeORSplitDegree;
	}

	/**
	 * This method initializes jCheckBoxAnalyzeORJoinDegree
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAnalyzeORJoinDegree() {
		if (jCheckBoxAnalyzeORJoinDegree == null) {
			jCheckBoxAnalyzeORJoinDegree = new JCheckBox();
			jCheckBoxAnalyzeORJoinDegree.setBounds(new Rectangle(186, 474, 167,
					21));
			jCheckBoxAnalyzeORJoinDegree.setText(resourcesManager.getString("ModelMetrics.analyzeOrJoinDegree"));
			jCheckBoxAnalyzeORJoinDegree
					.setSelected(recordORJoinDegreeAnalysis);
			jCheckBoxAnalyzeORJoinDegree.setEnabled(false);
		}
		return jCheckBoxAnalyzeORJoinDegree;
	}

	/**
	 * This method initializes jComboBoxModelType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxModelType() {
		if (jComboBoxModelType == null) {
			jComboBoxModelType = new JComboBox();
			jComboBoxModelType.addItem(ProcessObject.TYPEPNML);
			jComboBoxModelType.addItem(ProcessObject.TYPEYAWL);
			jComboBoxModelType.setBounds(new Rectangle(11, 39, 339, 18));
			jComboBoxModelType
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (jComboBoxModelType.getSelectedItem().equals(
									ProcessObject.TYPEPNML)) {
								jCheckBoxAnalyzeORSplitDegree
										.setSelected(false);
								jCheckBoxAnalyzeORSplitDegree.setEnabled(false);
								jCheckBoxAnalyzeORJoinDegree.setSelected(false);
								jCheckBoxAnalyzeORJoinDegree.setEnabled(false);
								jCheckBoxNumberOfORSplit.setSelected(false);
								jCheckBoxNumberOfORSplit.setEnabled(false);
								jCheckBoxNumberOfORJoin.setSelected(false);
								jCheckBoxNumberOfORJoin.setEnabled(false);
							} else if (jComboBoxModelType.getSelectedItem()
									.equals(ProcessObject.TYPEYAWL)) {
								jCheckBoxAnalyzeORSplitDegree.setSelected(true);
								jCheckBoxAnalyzeORSplitDegree.setEnabled(true);
								jCheckBoxAnalyzeORJoinDegree.setSelected(true);
								jCheckBoxAnalyzeORJoinDegree.setEnabled(true);
								jCheckBoxNumberOfORSplit.setSelected(true);
								jCheckBoxNumberOfORSplit.setEnabled(true);
								jCheckBoxNumberOfORJoin.setSelected(true);
								jCheckBoxNumberOfORJoin.setEnabled(true);
							}
						}
					});
		}
		return jComboBoxModelType;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DlgModelMetrics dlg = new DlgModelMetrics(null);
		dlg.setVisible(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
