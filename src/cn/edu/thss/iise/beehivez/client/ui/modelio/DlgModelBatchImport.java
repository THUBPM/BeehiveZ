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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.processmining.framework.models.petrinet.PetriNet;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.querytest.IndexQueryTest;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.TreeSet;

/**
 * @author Tao Jin
 * 
 */
public class DlgModelBatchImport extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelModelDir = null;
	private JTextField jTextFieldModelDir = null;
	private String modelDirectory = "epcPnml";
	private JButton jButtonChooseModelDir = null;
	private JCheckBox jCheckBoxTestAfterImport = null;
	private boolean isTestAfterImport = false;
	private JLabel jLabelQueryModelDir = null;
	private JButton jButtonQueryModelDirChoose = null;
	private JTextField jTextFieldQueryModelDirectory = null;
	private String queryModelDirectory = "epcPnml";
	private JLabel jLabelRuns = null;
	private JTextField jTextFieldNumberOfTestRuns = null;
	private int testRuns = 0;
	private JButton jButtonOK = null;
	
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * @param owner
	 */
	public DlgModelBatchImport(Frame owner) {
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
		this.setSize(297, 280);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(resourcesManager.getString("DlgModelBatchImport.title"));
		this.setContentPane(getJContentPane());
		this.setModal(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelRuns = new JLabel();
			jLabelRuns.setBounds(new Rectangle(12, 174, 128, 18));
			jLabelRuns.setText(resourcesManager.getString("DlgModelBatchImport.repeat"));
			jLabelQueryModelDir = new JLabel();
			jLabelQueryModelDir.setBounds(new Rectangle(12, 110, 138, 18));
			jLabelQueryModelDir.setText(resourcesManager.getString("DlgModelBatchImport.modeldirectory"));
			jLabelModelDir = new JLabel();
			jLabelModelDir.setBounds(new Rectangle(12, 10, 99, 18));
			jLabelModelDir.setText(resourcesManager.getString("DlgModelBatchImport.md"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelModelDir, null);
			jContentPane.add(getJTextFieldModelDir(), null);
			jContentPane.add(getJButtonChooseModelDir(), null);
			jContentPane.add(getJCheckBoxTestAfterImport(), null);
			jContentPane.add(jLabelQueryModelDir, null);
			jContentPane.add(getJButtonQueryModelDirChoose(), null);
			jContentPane.add(getJTextFieldQueryModelDirectory(), null);
			jContentPane.add(jLabelRuns, null);
			jContentPane.add(getJTextFieldNumberOfTestRuns(), null);
			jContentPane.add(getJButtonOK(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextFieldModelDir
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldModelDir() {
		if (jTextFieldModelDir == null) {
			jTextFieldModelDir = new JTextField();
			jTextFieldModelDir.setBounds(new Rectangle(12, 41, 261, 22));
			jTextFieldModelDir.setEditable(false);
			jTextFieldModelDir.setText(this.modelDirectory);

		}
		return jTextFieldModelDir;
	}

	/**
	 * This method initializes jButtonChooseModelDir
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChooseModelDir() {
		if (jButtonChooseModelDir == null) {
			jButtonChooseModelDir = new JButton();
			jButtonChooseModelDir.setBounds(new Rectangle(186, 8, 86, 22));
			jButtonChooseModelDir.setText(resourcesManager.getString("DlgModelBatchImport.choose"));
			jButtonChooseModelDir
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldModelDir.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldModelDir.setText(path);
							}
						}
					});
		}
		return jButtonChooseModelDir;
	}

	/**
	 * This method initializes jCheckBoxTestAfterImport
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxTestAfterImport() {
		if (jCheckBoxTestAfterImport == null) {
			jCheckBoxTestAfterImport = new JCheckBox();
			jCheckBoxTestAfterImport.setBounds(new Rectangle(12, 76, 239, 21));
			jCheckBoxTestAfterImport.setText(resourcesManager.getString("DlgModelBatchImport.test"));
			jCheckBoxTestAfterImport.setSelected(false);
			jCheckBoxTestAfterImport
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							isTestAfterImport = jCheckBoxTestAfterImport
									.isSelected();
							if (isTestAfterImport) {
								jButtonQueryModelDirChoose.setEnabled(true);
								jTextFieldNumberOfTestRuns.setEnabled(true);
								jTextFieldNumberOfTestRuns.setText("5");
							} else {
								jButtonQueryModelDirChoose.setEnabled(false);
								jTextFieldNumberOfTestRuns.setEnabled(false);
								jTextFieldNumberOfTestRuns.setText("0");
							}
						}
					});
		}
		return jCheckBoxTestAfterImport;
	}

	/**
	 * This method initializes jButtonQueryModelDirChoose
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonQueryModelDirChoose() {
		if (jButtonQueryModelDirChoose == null) {
			jButtonQueryModelDirChoose = new JButton();
			jButtonQueryModelDirChoose
					.setBounds(new Rectangle(187, 108, 87, 22));
			jButtonQueryModelDirChoose.setText(resourcesManager.getString("DlgModelBatchImport.choose"));
			jButtonQueryModelDirChoose.setEnabled(false);
			jButtonQueryModelDirChoose
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldQueryModelDirectory.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldQueryModelDirectory.setText(path);
							}
						}
					});
		}
		return jButtonQueryModelDirChoose;
	}

	/**
	 * This method initializes jTextFieldQueryModelDirectory
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldQueryModelDirectory() {
		if (jTextFieldQueryModelDirectory == null) {
			jTextFieldQueryModelDirectory = new JTextField();
			jTextFieldQueryModelDirectory.setBounds(new Rectangle(12, 140, 263,
					22));
			jTextFieldQueryModelDirectory.setEditable(false);
			jTextFieldQueryModelDirectory.setText(this.queryModelDirectory);
		}
		return jTextFieldQueryModelDirectory;
	}

	/**
	 * This method initializes jTextFieldNumberOfTestRuns
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldNumberOfTestRuns() {
		if (jTextFieldNumberOfTestRuns == null) {
			jTextFieldNumberOfTestRuns = new JTextField();
			jTextFieldNumberOfTestRuns.setBounds(new Rectangle(174, 172, 101,
					22));
			jTextFieldNumberOfTestRuns.setEnabled(false);
			jTextFieldNumberOfTestRuns.setText(String.valueOf(this.testRuns));
		}
		return jTextFieldNumberOfTestRuns;
	}

	private boolean validateParameters() {
		this.modelDirectory = jTextFieldModelDir.getText();
		this.queryModelDirectory = jTextFieldQueryModelDirectory.getText();
		this.isTestAfterImport = jCheckBoxTestAfterImport.isSelected();
		String strTestRuns = jTextFieldNumberOfTestRuns.getText();
		if (!strTestRuns.matches("[0-9]+")) {
			return false;
		}

		this.testRuns = Integer.parseInt(strTestRuns);

		if (this.testRuns < 0) {
			return false;
		}
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
			jButtonOK.setBounds(new Rectangle(104, 215, 73, 22));
			jButtonOK.setText(resourcesManager.getString("DlgModelBatchImport.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!validateParameters()) {
						JOptionPane.showMessageDialog(null,
								"illegal parameters");
					} else {
						try {
							// import models, only pnml and yawl at present
							File fModelDir = new File(modelDirectory);
							DataManager dm = DataManager.getInstance();
							long petriNetsCatalogID = dm
									.getProcessCatalogIdByName(ProcesscatalogObject.PETRINETS);
							long yawlCatalogID = dm
									.getProcessCatalogIdByName(ProcesscatalogObject.YAWLMODELS);
							System.out.println("import models ...");
							for (File f : fModelDir.listFiles()) {
								String fileName = f.getName();
								if (fileName.endsWith(".pnml")) {
									// System.out.println("import " + fileName);
									// can be imported
									FileInputStream fis = new FileInputStream(f
											.getAbsolutePath());
									byte[] temp = ToolKit
											.getBytesFromInputStream(fis);
									fis.close();
									dm.addProces(f.getName(), null,
											ProcessObject.TYPEPNML,
											petriNetsCatalogID, temp);
								} else if (fileName.endsWith(".yawl")) {
									// System.out.println("import " + fileName);
									// can be imported
									FileInputStream fis = new FileInputStream(f
											.getAbsolutePath());
									byte[] temp = ToolKit
											.getBytesFromInputStream(fis);
									fis.close();
									dm.addProces(f.getName(), null,
											ProcessObject.TYPEYAWL,
											yawlCatalogID, temp);
								}
							}
							System.out.println("models import finished.");

							// query test if needed
							if (isTestAfterImport) {
								System.out
										.println("query test after import...");
								IndexQueryTest.queryTest(queryModelDirectory,
										testRuns);
								System.out.println("finisehd");
							}
							ClientFrame.getInstance().refreshStatus();
							dispose();
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
				}
			});
		}
		return jButtonOK;
	}

	public static void main(String[] args) {
		DlgModelBatchImport dlg = new DlgModelBatchImport(null);
		dlg.setVisible(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
