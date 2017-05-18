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
package cn.edu.thss.iise.beehivez.server.parameter;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * used for system configuration
 * 
 * @author Tao Jin
 * 
 */
public class DlgSystemConfig extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JCheckBox jCheckBoxEnableSimilarLabel = null;
	private JLabel jLabelLabelSimilarityThreshold = null;
	private JTextField jTextFieldLabelSimilarityThreshold = null;
	private JCheckBox jCheckBoxEnableQueryLog = null;
	private JButton jButtonOK = null;

	private boolean enableSimilarLabel = GlobalParameter.isEnableSimilarLabel();
	private float labelSimilarityThreshold = GlobalParameter
			.getLabelSemanticSimilarity();
	private boolean enableQueryLog = GlobalParameter.isEnableQueryLog();
	private boolean allModels2PetriNets = GlobalParameter
			.isALLMODELS2PETRINETS();

	private boolean isChanged = false;
	private JCheckBox jCheckBoxAllModels2PetriNet = null;
	
	ResourcesManager resourcesManager;

	/**
	 * This method initializes jCheckBoxEnableSimilarLabel
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxEnableSimilarLabel() {
		if (jCheckBoxEnableSimilarLabel == null) {
			jCheckBoxEnableSimilarLabel = new JCheckBox();
			jCheckBoxEnableSimilarLabel
					.setBounds(new Rectangle(7, 10, 151, 21));
			jCheckBoxEnableSimilarLabel.setText(resourcesManager.getString("SystemConfigure.similarlabel"));
			jCheckBoxEnableSimilarLabel.setSelected(this.enableSimilarLabel);
			jCheckBoxEnableSimilarLabel
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							isChanged = true;
							if (jCheckBoxEnableSimilarLabel.isSelected()) {
								jTextFieldLabelSimilarityThreshold
										.setEnabled(true);
							} else {
								jTextFieldLabelSimilarityThreshold
										.setEnabled(false);
							}
						}
					});
		}
		return jCheckBoxEnableSimilarLabel;
	}

	/**
	 * This method initializes jTextFieldLabelSimilarityThreshold
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLabelSimilarityThreshold() {
		if (jTextFieldLabelSimilarityThreshold == null) {
			jTextFieldLabelSimilarityThreshold = new JTextField();
			jTextFieldLabelSimilarityThreshold.setBounds(new Rectangle(160, 37,
					49, 22));
			jTextFieldLabelSimilarityThreshold.setText(String
					.valueOf(this.labelSimilarityThreshold));
			if (this.enableSimilarLabel) {
				jTextFieldLabelSimilarityThreshold.setEnabled(true);
			} else {
				jTextFieldLabelSimilarityThreshold.setEnabled(false);
			}
			jTextFieldLabelSimilarityThreshold
					.addKeyListener(new java.awt.event.KeyAdapter() {
						public void keyTyped(java.awt.event.KeyEvent e) {
							isChanged = true;
						}
					});
		}
		return jTextFieldLabelSimilarityThreshold;
	}

	/**
	 * This method initializes jCheckBoxEnableQueryLog
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxEnableQueryLog() {
		if (jCheckBoxEnableQueryLog == null) {
			jCheckBoxEnableQueryLog = new JCheckBox();
			jCheckBoxEnableQueryLog.setBounds(new Rectangle(7, 69, 130, 21));
			jCheckBoxEnableQueryLog.setText(resourcesManager.getString("SystemConfigure.querylog"));
			jCheckBoxEnableQueryLog.setSelected(this.enableQueryLog);
			jCheckBoxEnableQueryLog
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							isChanged = true;
						}
					});
		}
		return jCheckBoxEnableQueryLog;
	}

	private boolean validateParameters() {
		try {
			String str = jTextFieldLabelSimilarityThreshold.getText();
			this.labelSimilarityThreshold = Float.parseFloat(str);
			if (this.labelSimilarityThreshold < 0
					|| this.labelSimilarityThreshold > 1) {
				return false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}

		this.enableQueryLog = jCheckBoxEnableQueryLog.isSelected();
		this.enableSimilarLabel = jCheckBoxEnableSimilarLabel.isSelected();
		this.allModels2PetriNets = jCheckBoxAllModels2PetriNet.isSelected();
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
			jButtonOK.setBounds(new Rectangle(68, 146, 78, 21));
			jButtonOK.setText(resourcesManager.getString("SystemConfigure.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!isChanged) {
						dispose();
						return;
					}

					if (validateParameters()) {
						GlobalParameter.setEnableQueryLog(enableQueryLog);
						GlobalParameter
								.setEnableSimilarLabel(enableSimilarLabel);
						GlobalParameter
								.setLabelSemanticSimilarity(labelSimilarityThreshold);
						GlobalParameter
								.setALLMODELS2PETRINETS(allModels2PetriNets);
						GlobalParameter.storeGlobalSetting();
						dispose();
					} else {
						JOptionPane.showMessageDialog(null,
								resourcesManager.getString("SystemConfigure.illegalparameters"));
					}
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * @param owner
	 */
	public DlgSystemConfig(Frame owner) {
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
		this.setSize(262, 201);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(resourcesManager.getString("SystemConfigure.title"));
		this.setContentPane(getJContentPane());
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelLabelSimilarityThreshold = new JLabel();
			jLabelLabelSimilarityThreshold.setBounds(new Rectangle(7, 39, 143,
					18));
			jLabelLabelSimilarityThreshold
					.setText(resourcesManager.getString("SystemConfigure.similaritythreshold"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJCheckBoxEnableSimilarLabel(), null);
			jContentPane.add(jLabelLabelSimilarityThreshold, null);
			jContentPane.add(getJTextFieldLabelSimilarityThreshold(), null);
			jContentPane.add(getJCheckBoxEnableQueryLog(), null);
			jContentPane.add(getJButtonOK(), null);
			jContentPane.add(getJCheckBoxAllModels2PetriNet(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jCheckBoxAllModels2PetriNet
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAllModels2PetriNet() {
		if (jCheckBoxAllModels2PetriNet == null) {
			jCheckBoxAllModels2PetriNet = new JCheckBox();
			jCheckBoxAllModels2PetriNet
					.setBounds(new Rectangle(7, 106, 224, 21));
			jCheckBoxAllModels2PetriNet
					.setText(resourcesManager.getString("SystemConfigure.transform"));
			jCheckBoxAllModels2PetriNet.setSelected(allModels2PetriNets);
			jCheckBoxAllModels2PetriNet
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							isChanged = true;
						}
					});
		}
		return jCheckBoxAllModels2PetriNet;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DlgSystemConfig dlg = new DlgSystemConfig(null);
		dlg.setVisible(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
