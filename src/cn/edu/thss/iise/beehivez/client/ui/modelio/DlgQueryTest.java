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

/**
 * used for query test, collecting time data for query
 */
package cn.edu.thss.iise.beehivez.client.ui.modelio;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import javax.swing.JTextField;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.querytest.IndexQueryTest;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * @author Tao Jin
 * 
 */
public class DlgQueryTest extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel1 = null;
	private JButton jButtonChangePath = null;
	private JTextField jTextFieldPath = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldTimes = null;
	private JButton jButtonOK = null;

	private String queryModelsDirectory = GlobalParameter.getQueryObjectPath();
	int times = 1;
	
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This method initializes jButtonChangePath
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChangePath() {
		if (jButtonChangePath == null) {
			jButtonChangePath = new JButton();
			jButtonChangePath.setBounds(new Rectangle(152, 14, 113, 19));
			jButtonChangePath.setText(resourcesManager.getString("DlgQueryTest.changepath"));
			jButtonChangePath
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldPath.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								jTextFieldPath.setText(path);
							}
						}
					});
		}
		return jButtonChangePath;
	}

	/**
	 * This method initializes jTextFieldPath
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldPath() {
		if (jTextFieldPath == null) {
			jTextFieldPath = new JTextField();
			jTextFieldPath.setBounds(new Rectangle(5, 38, 261, 22));
			jTextFieldPath.setEditable(false);
			jTextFieldPath.setText(queryModelsDirectory);
		}
		return jTextFieldPath;
	}

	/**
	 * This method initializes jTextFieldTimes
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTimes() {
		if (jTextFieldTimes == null) {
			jTextFieldTimes = new JTextField();
			jTextFieldTimes.setBounds(new Rectangle(157, 71, 106, 22));
			jTextFieldTimes.setText("1");
		}
		return jTextFieldTimes;
	}

	private boolean verifyInput() {
		queryModelsDirectory = jTextFieldPath.getText();
		times = Integer.parseInt(jTextFieldTimes.getText());
		if (queryModelsDirectory == null || times < 1) {
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
			jButtonOK.setBounds(new Rectangle(100, 117, 61, 21));
			jButtonOK.setText(resourcesManager.getString("DlgQueryTest.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (verifyInput()) {
						GlobalParameter
								.setQueryObjectPath(queryModelsDirectory);

						IndexQueryTest.queryTest(queryModelsDirectory, times);
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param owner
	 */
	public DlgQueryTest(Frame owner) {
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
		this.setSize(277, 177);
		this.setTitle(resourcesManager.getString("DlgQueryTest.title"));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(6, 73, 138, 18));
			jLabel2.setText(resourcesManager.getString("DlgQueryTest.rc"));
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(5, 14, 140, 18));
			jLabel1.setText(resourcesManager.getString("DlgQueryTest.dirc"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJButtonChangePath(), null);
			jContentPane.add(getJTextFieldPath(), null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getJTextFieldTimes(), null);
			jContentPane.add(getJButtonOK(), null);
		}
		return jContentPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
