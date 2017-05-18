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
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
import de.parsemis.MainFrame;
import de.parsemis.graph.Edge;
import de.parsemis.graph.ListGraph;
import de.parsemis.graph.Node;
import de.parsemis.parsers.GraphmlParser;
import de.parsemis.parsers.StringLabelParser;

/**
 * @author Tao Jin
 * 
 * @date 2011-3-12
 * 
 */
public class DlgFrequentGraph extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabelType = null;
	private JComboBox jComboBoxType = null;
	private JLabel jLabelSrcDir = null;
	private JButton jButtonChangeDirectory = null;
	private JTextField jTextFieldSourceDirectory = null;
	private JButton jButtonOK = null;
	ResourcesManager resourcesManager;

	/**
	 * This method initializes jComboBoxType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxType() {
		if (jComboBoxType == null) {
			jComboBoxType = new JComboBox();
			jComboBoxType.setBounds(new Rectangle(66, 10, 145, 22));
			jComboBoxType.addItem(ProcessObject.TYPEPNML);
		}
		return jComboBoxType;
	}

	/**
	 * This method initializes jButtonChangeDirectory
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonChangeDirectory() {
		if (jButtonChangeDirectory == null) {
			jButtonChangeDirectory = new JButton();
			jButtonChangeDirectory.setBounds(new Rectangle(131, 60, 142, 19));
			jButtonChangeDirectory.setText(resourcesManager.getString("FrequentGraph.changedirectory"));
			jButtonChangeDirectory
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
		return jButtonChangeDirectory;
	}

	/**
	 * This method initializes jTextFieldSourceDirectory
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSourceDirectory() {
		if (jTextFieldSourceDirectory == null) {
			jTextFieldSourceDirectory = new JTextField();
			jTextFieldSourceDirectory.setBounds(new Rectangle(11, 93, 265, 20));
			jTextFieldSourceDirectory.setEditable(false);
		}
		return jTextFieldSourceDirectory;
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setBounds(new Rectangle(94, 135, 81, 25));
			jButtonOK.setText(resourcesManager.getString("FrequentGraph.ok"));
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String sourcePath = jTextFieldSourceDirectory.getText();
						String labelSim = GlobalParameter
								.isEnableSimilarLabel() ? String
								.valueOf(GlobalParameter
										.getLabelSemanticSimilarity()) : "#";
						String reportFileName = sourcePath + "/labelInfo_"
								+ labelSim + ".txt";
						String gmlFileName = sourcePath + "/graphs_" + labelSim
								+ ".gml";

						// ///////////////////////////
						// deal with label
						// ///////////////////////////

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
						// bw.write(similarLabelSets.toString());
						bw.close();
						fw.close();

						// ////////////////////////////////////
						// transform pnml to gml
						// ////////////////////////////////////
						Vector vgml = new Vector();
						for (File f : dir.listFiles()) {
							if (f.getName().endsWith(".pnml")) {
								System.out.println("transforming "
										+ f.getName());
								PetriNet pn = PetriNetUtil
										.getPetriNetFromPnml(new FileInputStream(
												f));
								// transform
								// first transform places, then transitions, at
								// last edges
								ListGraph<String, String> graph = new ListGraph<String, String>(
										f.getName());
								HashMap<PNNode, Node<String, String>> pnnode2gmlnode = new HashMap<PNNode, Node<String, String>>();
								for (Place p : pn.getPlaces()) {
									Node<String, String> node = graph
											.addNode("p");
									pnnode2gmlnode.put(p, node);
								}
								for (Transition t : pn.getTransitions()) {
									// determine the label index and use it as
									// the node id of gml
									String tlabel = t.getIdentifier();
									int index = 0;
									labelfound: for (index = 0; index < similarLabelSets
											.size(); index++) {
										Vector<String> v = similarLabelSets
												.get(index);
										for (String label : v) {
											if (tlabel.equals(label)) {
												break labelfound;
											}
										}
									}
									Node<String, String> node = graph
											.addNode(String.valueOf(index));
									pnnode2gmlnode.put(t, node);

								}
								for (PNEdge edge : (ArrayList<PNEdge>) pn
										.getEdges()) {
									PNNode source = (PNNode) edge.getSource();
									PNNode dist = (PNNode) edge.getDest();
									Node<String, String> snode = pnnode2gmlnode
											.get(source);
									Node<String, String> dnode = pnnode2gmlnode
											.get(dist);
									graph.addEdge(snode, dnode, "",
											Edge.OUTGOING);
								}
								vgml.add(graph);
							}
						}
						StringLabelParser lp = new StringLabelParser();
						GraphmlParser<String, String> parser = new GraphmlParser<String, String>(
								lp, lp);

						FileOutputStream fos = new FileOutputStream(gmlFileName);
						parser.serialize(fos, vgml);

						javax.swing.SwingUtilities.invokeLater(new MainFrame(
								null));

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
	public DlgFrequentGraph(Frame owner) {
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
		this.setSize(300, 200);
		this.setTitle(resourcesManager.getString("FrequentGraph.title"));
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelSrcDir = new JLabel();
			jLabelSrcDir.setBounds(new Rectangle(13, 62, 100, 16));
			jLabelSrcDir.setText(resourcesManager.getString("FrequentGraph.sourcedirctory"));
			jLabelType = new JLabel();
			jLabelType.setBounds(new Rectangle(11, 13, 38, 16));
			jLabelType.setText(resourcesManager.getString("FrequentGraph.type"));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelType, null);
			jContentPane.add(getJComboBoxType(), null);
			jContentPane.add(jLabelSrcDir, null);
			jContentPane.add(getJButtonChangeDirectory(), null);
			jContentPane.add(getJTextFieldSourceDirectory(), null);
			jContentPane.add(getJButtonOK(), null);
		}
		return jContentPane;
	}

}
