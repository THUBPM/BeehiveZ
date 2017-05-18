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
package cn.edu.thss.iise.beehivez.client.ui.modelio.crawler;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 * @author 
 * 
 */
public class DlgCrawler extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelFileType = null;
	private JComboBox jComboBoxModelType = null;
	private JButton jButtonCrawl = null;
	private JScrollPane jScrollPane = null;
	private JTextArea jTextAreaResult = null;
	ResourcesManager resourcesManager = new ResourcesManager();
	
	/**
	 * @param owner
	 */
	public DlgCrawler(Frame owner) {
		super(owner);
		initialize();
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(355, 271);
		this.setModal(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(resourcesManager.getString("DlgCrawler.title"));
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelFileType = new JLabel();
			jLabelFileType.setBounds(new Rectangle(14, 16, 69, 18));
			jLabelFileType.setText(resourcesManager.getString("DlgCrawler.type"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelFileType, null);
			jContentPane.add(getJComboBoxModelType(), null);
			jContentPane.add(getJButtonCrawl(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jComboBoxModelType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxModelType() {
		if (jComboBoxModelType == null) {
			jComboBoxModelType = new JComboBox();
			jComboBoxModelType.setBounds(new Rectangle(93, 12, 132, 26));
			jComboBoxModelType.addItem(MyGoogleSearch.PNML);
		}
		return jComboBoxModelType;
	}

	/**
	 * This method initializes jButtonCrawl
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonCrawl() {
		if (jButtonCrawl == null) {
			jButtonCrawl = new JButton();
			jButtonCrawl.setBounds(new Rectangle(248, 12, 75, 26));
			jButtonCrawl.setText(resourcesManager.getString("DlgCrawler.crawl"));
			jButtonCrawl.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String modelType = jComboBoxModelType.getSelectedItem()
							.toString().trim();
					MyGoogleSearch gs = new MyGoogleSearch(modelType, 100);
					ArrayList<String> links = null;
					int count = 0;
					int amount = 0;
					String strResult = "begin to search from the internet\n\r";
					jTextAreaResult.setText(strResult);
					jTextAreaResult.paintImmediately(jTextAreaResult
							.getBounds());
					while (true) {
						links = gs.nextPage();
						if (links == null)
							break;
						amount += links.size();
						for (String link : links) {
							HttpFile file = new HttpFile(link);
							strResult = "find a model file at " + link + "\n\r";
							jTextAreaResult.append(strResult);
							jTextAreaResult.paintImmediately(jTextAreaResult
									.getBounds());

							String name = link
									.substring(link.lastIndexOf("/") + 1);
							String content = null;
							try {
								content = file.getContent();
								strResult = "download successfully" + "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
							} catch (IOException e1) {
								strResult = "download failed" + "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
								continue;
							}
							PetriNet pn = null;
							try {
								InputStream in = new ByteArrayInputStream(
										content.getBytes());
								PnmlImport pi = new PnmlImport();
								PetriNetResult pnr = (PetriNetResult) pi
										.importFile(in);
								pn = pnr.getPetriNet();
								pnr.destroy();
								for (Transition t : pn.getTransitions()) {
									LogEvent le = new LogEvent(t
											.getIdentifier(), "auto");
									t.setLogEvent(le);
								}

								strResult = "format check successfully"
										+ "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
							} catch (Exception e1) {
								e1.printStackTrace();
								strResult = "format is error" + "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
								continue;
							}
							try {
								DataManager dm = DataManager.getInstance();
								System.out.println("write to database");
								dm.addProces(name, null,
										ProcessObject.TYPEPNML, 3, PetriNetUtil
												.getPnmlBytes(pn));

								count++;
								strResult = "saved to database successfully"
										+ "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
							} catch (Exception e2) {
								// e2.printStackTrace();
								strResult = "write to database failed" + "\n\r";
								jTextAreaResult.append(strResult);
								jTextAreaResult
										.paintImmediately(jTextAreaResult
												.getBounds());
								break;
							}
						}

					}

					jTextAreaResult
							.append("the total number of searched model file is: "
									+ amount);
					jTextAreaResult
							.append("the number of model files saved to database successfully is: "
									+ count);
					jTextAreaResult.paintImmediately(jTextAreaResult
							.getBounds());

					JOptionPane.showMessageDialog(null, "completed!");
				}
			});
		}
		return jButtonCrawl;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(0, 46, 349, 196));
			jScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane.setViewportView(getJTextAreaResult());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextAreaResult
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaResult() {
		if (jTextAreaResult == null) {
			jTextAreaResult = new JTextArea();
			jTextAreaResult.setEditable(false);
			jTextAreaResult.setLineWrap(true);
			jTextAreaResult.setText("");
		}
		return jTextAreaResult;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
