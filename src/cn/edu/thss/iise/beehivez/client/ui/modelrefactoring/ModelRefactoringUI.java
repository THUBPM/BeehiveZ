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
package cn.edu.thss.iise.beehivez.client.ui.modelrefactoring;

import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.petrinetwithdata.DataItem;
import cn.edu.thss.iise.beehivez.server.petrinetwithdata.PetriNetWithData;
import cn.edu.thss.iise.beehivez.server.petrinetwithdata.parallelize.PetriNetParallelize;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.WoflanAnalysisResult;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JScrollPane;

/**
 * @author Tao Jin
 * 
 * @date 2012-5-13
 * 
 */
public class ModelRefactoringUI extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private JSplitPane jSplitPane1 = null;
	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	private JPanel visualPanel = null;
	private JButton jButtonOpen = null;
	private JButton jButtonParallelize = null;

	private String filepath = null;
	private JScrollPane jScrollPane = null;
	private JTextArea dataOperations = null;
	private JScrollPane jScrollPane1 = null;
	private JTextArea message = null;
	ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This method initializes jSplitPane1
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane1.setTopComponent(getJSplitPane());
			jSplitPane1.setBottomComponent(getJScrollPane1());
			jSplitPane1.setDividerLocation(200);
		}
		return jSplitPane1;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(100);
			jSplitPane.setLeftComponent(getJScrollPane());
			jSplitPane.setRightComponent(getJPanelModel());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getJButtonOpen(), null);
			jPanel.add(getJButtonParallelize(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanelModel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelModel() {
		if (visualPanel == null) {
			visualPanel = new JPanel();
			visualPanel.setLayout(new BorderLayout());
		}
		return visualPanel;
	}

	/**
	 * This method initializes jButtonOpen
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOpen() {
		if (jButtonOpen == null) {
			jButtonOpen = new JButton();
			jButtonOpen.setText(resourcesManager.getString("ModelRefactoring.ui.open"));
			jButtonOpen.setBounds(new Rectangle(10, 10, 64, 28));
			jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(GlobalParameter
							.getQueryObjectPath());
					ExtensionFilter filter1 = new ExtensionFilter(".dpnml",
							"PNML files with data (*.dpnml)");
					fileChooser.setDialogTitle("Open Model File");
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					fileChooser.rescanCurrentDirectory();
					fileChooser.addChoosableFileFilter(filter1);
					fileChooser.setFileFilter(filter1);
					int choose = fileChooser.showOpenDialog(null);
					if (choose == JFileChooser.APPROVE_OPTION) {
						filepath = fileChooser.getSelectedFile()
								.getAbsolutePath();
					} else {
						return;
					}
					FileInputStream in;
					try {
						in = new FileInputStream(filepath);
						VisualFrame visualframe = null;
						if (filepath.endsWith(".dpnml")) {
							visualframe = new VisualFrame(
									ProcessObject.TYPEPNML, in);
							visualPanel.removeAll();
							visualPanel.add(visualframe.getJContentPane(),
									BorderLayout.CENTER);
							visualPanel.setVisible(true);
							visualPanel.updateUI();

							// print data operations
							PetriNetWithData dpn = PetriNetWithData
									.readFromFile(filepath);

							String str = "";
							for (DataItem di : dpn.getVariables()) {
								str += "data: " + di.toString() + "\n";
								str += "written by: ";
								for (Transition t : dpn.getDataWritten()
										.get(di)) {
									str += t.getIdentifier() + " ";
								}
								str += "\nread by: ";
								for (Transition t : dpn.getDataRead().get(di)) {
									str += t.getIdentifier() + " ";
								}
								str += "\n\n";
							}

							dataOperations.setText(str);
						}
						in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return jButtonOpen;
	}

	/**
	 * This method initializes jButtonParallelize
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonParallelize() {
		if (jButtonParallelize == null) {
			jButtonParallelize = new JButton();
			jButtonParallelize.setBounds(new Rectangle(95, 10, 93, 28));
			jButtonParallelize.setText(resourcesManager.getString("ModelRefactoring.ui.para"));
			jButtonParallelize
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								PetriNetWithData dpn = null;
								if (filepath.endsWith(".dpnml")) {
									dpn = PetriNetWithData
											.readFromFile(filepath);
								}

								if (dpn == null
										|| dpn.getDataRead().size() == 0
										&& dpn.getDataWritten().size() == 0) {

									JOptionPane.showMessageDialog(null,
											resourcesManager.getString("ModelRefactoring.ui.illegal"));
									return;
								}

								PetriNet pn = dpn.getPetriNet();

								if (pn == null) {
									JOptionPane.showMessageDialog(null,
											resourcesManager.getString("ModelRefactoring.ui.null"));
									return;
								}

								WoflanAnalysisResult war = new WoflanAnalysisResult(
										pn);
								if (!war.isSoundWorkflowNet()) {
									JOptionPane.showMessageDialog(null,
											resourcesManager.getString("ModelRefactoring.ui.nsound"));
									return;
								}

								PetriNetWithData newdpn = new PetriNetWithData();
								String msg = PetriNetParallelize.parallelize(
										dpn, newdpn);
								message.setText(msg);

								InputStream in;
								in = new ByteArrayInputStream(PetriNetUtil
										.getPnmlBytes(newdpn.getPetriNet()));
								VisualFrame visualframe = new VisualFrame(
										resourcesManager.getString("ModelRefactoring.ui.paralleModel"), "pnml", in);
								visualframe.setVisible(true);
								in.close();

								// print parallelize information

							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
		}
		return jButtonParallelize;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getDataOperations());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes dataOperations
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getDataOperations() {
		if (dataOperations == null) {
			dataOperations = new JTextArea();
			dataOperations.setEditable(false);
			dataOperations.setWrapStyleWord(true);
			dataOperations.setLineWrap(true);
		}
		return dataOperations;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getMessage());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes message
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getMessage() {
		if (message == null) {
			message = new JTextArea();
			message.setEditable(false);
			message.setLineWrap(true);
			message.setWrapStyleWord(true);
		}
		return message;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * This is the default constructor
	 */
	public ModelRefactoringUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(508, 399);

		this.setTopComponent(getJPanel());
		this.setBottomComponent(getJSplitPane1());
		this.setContinuousLayout(false);
		this.setDividerLocation(50);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setComponentOrientation(ComponentOrientation.UNKNOWN);
	}

	class ExtensionFilter extends FileFilter {
		String extension, description;

		public ExtensionFilter(String ext, String desp) {
			extension = ext;
			description = desp;
		}

		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase()
					.endsWith(extension));
		}

		public String getDescription() {
			return description;
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
