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
import javax.swing.JComboBox;
import java.awt.Rectangle;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Vector;

/**
 * @author Tao Jin
 * 
 */
public class DlgLabelStatistics extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JComboBox jComboBoxFileType = null;
	private JLabel jLabelFileType = null;
	private JLabel jLabelDirectory = null;
	private JTextField jTextFieldSourceDirectory = null;
	private JButton jButtonSourceDirectory = null;
	private JLabel jLabelTargetFileName = null;
	private JButton jButtonTargetDirectory = null;
	private JTextField jTextFieldTargetFileName = null;
	private JButton jButtonOK = null;
	ResourcesManager resourcesManager;

	/**
	 * This method initializes jComboBoxFileType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxFileType() {
		if (jComboBoxFileType == null) {
			jComboBoxFileType = new JComboBox();
			jComboBoxFileType.setBounds(new Rectangle(14, 30, 169, 25));
			jComboBoxFileType.addItem(ProcessObject.TYPEPNML);
		}
		return jComboBoxFileType;
	}

	/**
	 * This method initializes jTextFieldSourceDirectory
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSourceDirectory() {
		if (jTextFieldSourceDirectory == null) {
			jTextFieldSourceDirectory = new JTextField();
			jTextFieldSourceDirectory.setBounds(new Rectangle(15, 97, 263, 20));
			jTextFieldSourceDirectory.setEditable(false);
			jTextFieldSourceDirectory.setText("models");
		}
		return jTextFieldSourceDirectory;
	}

	/**
	 * This method initializes jButtonSourceDirectory
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSourceDirectory() {
		if (jButtonSourceDirectory == null) {
			jButtonSourceDirectory = new JButton();
			jButtonSourceDirectory.setBounds(new Rectangle(138, 67, 137, 21));
			jButtonSourceDirectory.setText(resourcesManager.getString("LabelStatistics.sourceDirectory"));
			jButtonSourceDirectory
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
		return jButtonSourceDirectory;
	}

	/**
	 * This method initializes jButtonTargetDirectory
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonTargetDirectory() {
		if (jButtonTargetDirectory == null) {
			jButtonTargetDirectory = new JButton();
			jButtonTargetDirectory.setBounds(new Rectangle(137, 135, 136, 20));
			jButtonTargetDirectory.setText(resourcesManager.getString("LabelStatistics.setfilename"));
			jButtonTargetDirectory
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser();
							chooser
									.setFileSelectionMode(JFileChooser.FILES_ONLY);
							if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldTargetFileName.setText(path);
							}
						}
					});
		}
		return jButtonTargetDirectory;
	}

	/**
	 * This method initializes jTextFieldTargetFileName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTargetFileName() {
		if (jTextFieldTargetFileName == null) {
			jTextFieldTargetFileName = new JTextField();
			jTextFieldTargetFileName.setBounds(new Rectangle(14, 167, 261, 20));
			jTextFieldTargetFileName.setEditable(false);
			jTextFieldTargetFileName.setText("labelInfo.txt");
		}
		return jTextFieldTargetFileName;
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(102, 207, 90, 21));
			jButtonOK.setText(resourcesManager.getString("LabelStatistics.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String sourcePath = jTextFieldSourceDirectory.getText();
						String reportFileName = jTextFieldTargetFileName
								.getText();

						// use to check the duplicate labels
						HashSet<String> labelSet = new HashSet<String>();
						// store the unique labels
						// the similarity can be considered, the first label in
						// every vector
						// is the seed.
						Vector<Vector<String>> similarLabelSets = new Vector<Vector<String>>();

						int nModels = 0;
						int nTransitions = 0;

						File dir = new File(sourcePath);
						for (File f : dir.listFiles()) {
							if (f.getName().endsWith(".pnml")) {
								System.out.println("parsing " + f.getName());
								PetriNet pn = PetriNetUtil
										.getPetriNetFromPnml(new FileInputStream(
												f));
								nModels++;
								for (Transition t : pn.getTransitions()) {
									nTransitions++;
									String label = t.getIdentifier();
									if (labelSet.add(label)) {
										if (GlobalParameter
												.isEnableSimilarLabel()) {
											// label similarity is considered
											boolean handled = false;
											for (Vector<String> v : similarLabelSets) {
												String seed = v.get(0);
												if (StringSimilarityUtil
														.semanticSimilarity(
																seed, label) >= GlobalParameter
														.getLabelSemanticSimilarity()) {
													v.add(label);
													handled = true;
												}
											}

											if (!handled) {
												Vector<String> v = new Vector<String>();
												v.add(label);
												similarLabelSets.add(v);
											}
										} else {
											// label similarity is not
											// considered
											Vector<String> v = new Vector<String>();
											v.add(label);
											similarLabelSets.add(v);
										}
									}
								}
							}
						}

						FileWriter fw = new FileWriter(reportFileName, false);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write("the number of models: " + nModels);
						bw.newLine();
						bw.write("the number of transitions in total: "
								+ nTransitions);
						bw.newLine();
						bw
								.write("the number of unique transitions without similarity considered: "
										+ labelSet.size());
						bw.newLine();
						bw
								.write("the number of unique transitions with similarity considered: "
										+ similarLabelSets.size());
						bw.newLine();
						bw.newLine();
						for (int index = 0; index < similarLabelSets.size(); index++) {
							Vector<String> v = similarLabelSets.get(index);
							bw.write(String.valueOf(index));
							bw.newLine();
							bw.write(v.toString());
							bw.newLine();
						}
						// int index = 0;
						// for (Vector<String> v : similarLabelSets) {
						// bw.write(String.valueOf(index));
						// bw.newLine();
						// bw.write(v.toString());
						// bw.newLine();
						// index++;
						// }
						bw.close();
						fw.close();

						dispose();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		}
		return jButtonOK;
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
	public DlgLabelStatistics(Frame owner) {
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
		resourcesManager = new ResourcesManager();
		this.setSize(300, 265);
		this.setTitle(resourcesManager.getString("LabelStatistics.title"));
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelTargetFileName = new JLabel();
			jLabelTargetFileName.setBounds(new Rectangle(13, 134, 112, 16));
			jLabelTargetFileName.setText(resourcesManager.getString("LabelStatistics.targetfilename"));
			jLabelDirectory = new JLabel();
			jLabelDirectory.setBounds(new Rectangle(12, 68, 113, 16));
			jLabelDirectory.setText(resourcesManager.getString("LabelStatistics.source"));
			jLabelFileType = new JLabel();
			jLabelFileType.setBounds(new Rectangle(14, 9, 81, 16));
			jLabelFileType.setText(resourcesManager.getString("LabelStatistics.type"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJComboBoxFileType(), null);
			jContentPane.add(jLabelFileType, null);
			jContentPane.add(jLabelDirectory, null);
			jContentPane.add(getJTextFieldSourceDirectory(), null);
			jContentPane.add(getJButtonSourceDirectory(), null);
			jContentPane.add(jLabelTargetFileName, null);
			jContentPane.add(getJButtonTargetDirectory(), null);
			jContentPane.add(getJTextFieldTargetFileName(), null);
			jContentPane.add(getJButtonOK(), null);
		}
		return jContentPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
