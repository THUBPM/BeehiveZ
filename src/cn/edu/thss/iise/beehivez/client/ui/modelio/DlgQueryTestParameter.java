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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * @author JinTao
 * 
 */
public class DlgQueryTestParameter extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelQueryModelsPath = null;
	private JTextField jTextFieldQueryModelsDirectory = null;
	private JTextField jTextFieldLogPointStart = null;
	private JLabel jLabelLogPointStart = null;
	private JLabel jLabelLogPointEnd = null;
	private JLabel jLabelNumberOfLogPoints = null;
	private JTextField jTextFieldNumberOfLogPoints = null;
	private JTextField jTextFieldLogPointEnd = null;
	private JButton jButtonChangePath = null;
	private JButton jButtonOK = null;

	private String queryModelsDirectory = GlobalParameter.getQueryObjectPath(); // @jve:decl-index=0:
	private long logPointStart = GlobalParameter.getLogPointStart();
	private long logPointEnd = GlobalParameter.getLogPointEnd();
	private int numberOfLogPoints = GlobalParameter.getNLogPoints();
	
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This method initializes jTextFieldQueryModelsDirectory
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldQueryModelsDirectory() {
		if (jTextFieldQueryModelsDirectory == null) {
			jTextFieldQueryModelsDirectory = new JTextField();
			jTextFieldQueryModelsDirectory.setBounds(new Rectangle(8, 42, 261,
					22));
			jTextFieldQueryModelsDirectory.setEditable(false);
			jTextFieldQueryModelsDirectory.setText(queryModelsDirectory);
		}
		return jTextFieldQueryModelsDirectory;
	}

	/**
	 * This method initializes jTextFieldLogPointStart
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLogPointStart() {
		if (jTextFieldLogPointStart == null) {
			jTextFieldLogPointStart = new JTextField();
			jTextFieldLogPointStart.setBounds(new Rectangle(164, 101, 106, 22));
			jTextFieldLogPointStart.setText(String.valueOf(logPointStart));
		}
		return jTextFieldLogPointStart;
	}

	/**
	 * This method initializes jTextFieldNumberOfLogPoints
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldNumberOfLogPoints() {
		if (jTextFieldNumberOfLogPoints == null) {
			jTextFieldNumberOfLogPoints = new JTextField();
			jTextFieldNumberOfLogPoints.setBounds(new Rectangle(164, 71, 106,
					22));
			jTextFieldNumberOfLogPoints.setText(String
					.valueOf(numberOfLogPoints));
		}
		return jTextFieldNumberOfLogPoints;
	}

	/**
	 * This method initializes jTextFieldLogPointEnd
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLogPointEnd() {
		if (jTextFieldLogPointEnd == null) {
			jTextFieldLogPointEnd = new JTextField();
			jTextFieldLogPointEnd.setBounds(new Rectangle(165, 131, 105, 22));
			jTextFieldLogPointEnd.setText(String.valueOf(logPointEnd));
		}
		return jTextFieldLogPointEnd;
	}

	/**
	 * This method initializes jButtonChangePath
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChangePath() {
		if (jButtonChangePath == null) {
			jButtonChangePath = new JButton();
			jButtonChangePath.setBounds(new Rectangle(157, 15, 110, 18));
			jButtonChangePath.setText(resourcesManager.getString("DlgQueryTestParameter.changepath"));
			jButtonChangePath
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser chooser = new JFileChooser(
									jTextFieldQueryModelsDirectory.getText());
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								String path = chooser.getSelectedFile()
										.getPath();
								queryModelsDirectory = path;
								jTextFieldQueryModelsDirectory.setText(path);
							}
						}
					});
		}
		return jButtonChangePath;
	}

	private boolean areParemetersLegal() {
		numberOfLogPoints = Integer.parseInt(jTextFieldNumberOfLogPoints
				.getText());
		if (numberOfLogPoints < 1) {
			return false;
		}
		logPointStart = Long.parseLong(jTextFieldLogPointStart.getText());
		if (logPointStart < 1) {
			return false;
		}
		logPointEnd = Long.parseLong(jTextFieldLogPointEnd.getText());
		if (logPointEnd < 1) {
			return false;
		}

		if (logPointEnd < logPointStart) {
			return false;
		}
		if (logPointEnd - logPointStart + 1 < numberOfLogPoints) {
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

			jButtonOK.setBounds(new Rectangle(88, 178, 84, 28));
			jButtonOK.setText(resourcesManager.getString("DlgQueryTestParameter.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (areParemetersLegal()) {
						GlobalParameter.setEnableQueryLog(true);
						GlobalParameter.setLogPointEnd(logPointEnd);
						GlobalParameter.setLogPointStart(logPointStart);
						GlobalParameter.setNLogPoints(numberOfLogPoints);
						GlobalParameter
								.setQueryObjectPath(queryModelsDirectory);
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
	public DlgQueryTestParameter(Frame owner) {
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
		this.setSize(284, 255);
		this.setTitle(resourcesManager.getString("DlgQueryTestParameter.title"));
		this.setName("dlgQueryTestParameter");
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelNumberOfLogPoints = new JLabel();
			jLabelNumberOfLogPoints.setBounds(new Rectangle(8, 73, 133, 18));
			jLabelNumberOfLogPoints.setText(resourcesManager.getString("DlgQueryTestParameter.numlp"));
			jLabelLogPointEnd = new JLabel();
			jLabelLogPointEnd.setBounds(new Rectangle(9, 133, 103, 18));
			jLabelLogPointEnd.setText(resourcesManager.getString("DlgQueryTestParameter.logpointend"));
			jLabelLogPointStart = new JLabel();
			jLabelLogPointStart.setBounds(new Rectangle(9, 103, 110, 18));
			jLabelLogPointStart.setText(resourcesManager.getString("DlgQueryTestParameter.logpoint"));
			jLabelQueryModelsPath = new JLabel();
			jLabelQueryModelsPath.setBounds(new Rectangle(8, 14, 143, 18));
			jLabelQueryModelsPath.setText(resourcesManager.getString("DlgQueryTestParameter.querymodeldirc"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelQueryModelsPath, null);
			jContentPane.add(getJTextFieldQueryModelsDirectory(), null);
			jContentPane.add(getJTextFieldLogPointStart(), null);
			jContentPane.add(jLabelLogPointStart, null);
			jContentPane.add(jLabelLogPointEnd, null);
			jContentPane.add(jLabelNumberOfLogPoints, null);
			jContentPane.add(getJTextFieldNumberOfLogPoints(), null);
			jContentPane.add(getJTextFieldLogPointEnd(), null);
			jContentPane.add(getJButtonChangePath(), null);
			jContentPane.add(getJButtonOK(), null);
		}
		return jContentPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
