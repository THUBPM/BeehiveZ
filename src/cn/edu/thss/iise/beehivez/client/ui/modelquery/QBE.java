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
package cn.edu.thss.iise.beehivez.client.ui.modelquery;

import javax.swing.JSplitPane;
import java.awt.Dimension;
import javax.swing.JPanel;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Observable;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import pipe.gui.CreateGui;
import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.DatabaseAccessor;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import javax.swing.JTable;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import javax.swing.JTextField;
/**
 * 
 * @author Nianhua Wu
 * 
 *
 */
public class QBE extends JSplitPane implements java.util.Observer {

	private static final long serialVersionUID = 1L;
	private JSplitPane ExamplePane = null;
	private JPanel exampleLeft = null;
	private JPanel exampleRight = null;
	private JScrollPane queryResult = null;
	private JLabel jLabel = null;
	private JButton draw = null;
	private JButton choose = null;
	private JLabel jLabel1 = null;
	private JComboBox algorithmList = null;
	private JButton query = null;
	public String filepath = null;
	public JPanel visualPanel = new JPanel();
	private JTable resultTable = null;
	private JLabel jLabelSimilarity = null;
	private JTextField jTextFieldSimilarity = null;
	ResourcesManager resourcesManager;

	/**
	 * This is the default constructor
	 */
	public QBE() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		resourcesManager = new ResourcesManager();
		this.setSize(733, 644);

		this.setBottomComponent(getQueryResult());
		this.setDividerLocation(300);
		this.setTopComponent(getExamplePane());
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
	}

	/**
	 * This method initializes ExamplePane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getExamplePane() {
		if (ExamplePane == null) {
			ExamplePane = new JSplitPane();
			ExamplePane.setDividerLocation(300);
			ExamplePane.setLeftComponent(getExampleLeft());
			ExamplePane.setRightComponent(getExampleRight());
			ExamplePane.setDividerSize(10);
		}
		return ExamplePane;
	}

	/**
	 * This method initializes exampleLeft
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExampleLeft() {
		if (exampleLeft == null) {
			jLabelSimilarity = new JLabel();
			jLabelSimilarity.setBounds(new Rectangle(47, 178, 66, 18));
			jLabelSimilarity.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.similarity"));
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(16, 129, 85, 18));
			jLabel1.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.chooseindex"));
			jLabel = new JLabel();
			jLabel.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.chooseexample"));
			jLabel.setBounds(new Rectangle(16, 18, 106, 18));
			exampleLeft = new JPanel();
			exampleLeft.setLayout(null);
			exampleLeft.add(jLabel, null);
			exampleLeft.add(getDraw(), null);
			
			exampleLeft.add(getChoose(), null);
			exampleLeft.add(jLabel1, null);
			exampleLeft.add(getAlgorithmList(), null);
			exampleLeft.add(getQuery(), null);
			exampleLeft.add(jLabelSimilarity, null);
			exampleLeft.add(getJTextFieldSimilarity(), null);
		}
		return exampleLeft;
	}

	/**
	 * This method initializes exampleRight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExampleRight() {
		if (exampleRight == null) {
			exampleRight = new JPanel();
			exampleRight.setLayout(new BorderLayout());
		}
		return exampleRight;
	}

	/**
	 * This method initializes queryResult
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getQueryResult() {
		if (queryResult == null) {
			queryResult = new JScrollPane(getResultTable());
		}
		return queryResult;
	}

	/**
	 * This method initializes draw
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDraw() {
		if (draw == null) {
			draw = new JButton();
			draw.setBounds(new Rectangle(129, 18, 133, 30));
			draw.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.drawmodel"));
			draw.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					CreateGui.init();
				}

			});
		}
		return draw;
	}

	/**
	 * This method initializes choose
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getChoose() {
		if (choose == null) {
			choose = new JButton();
			choose.setBounds(new Rectangle(129, 66, 133, 30));
			choose.setFont(new Font("Dialog", Font.BOLD, 12));
			choose.setActionCommand("example model");
			choose.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.chooseexample2"));
			choose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(GlobalParameter
							.getQueryObjectPath());
					ExtensionFilter filter1 = new ExtensionFilter(".pnml",
							"PNML files (*.pnml)");
					ExtensionFilter filter2 = new ExtensionFilter(".yawl",
							"YAWL files (*.yawl)");
					fileChooser.setDialogTitle(resourcesManager.getString("ModelQuery.Plugin.qbe.open"));
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					fileChooser.rescanCurrentDirectory();
					fileChooser.addChoosableFileFilter(filter1);
					fileChooser.addChoosableFileFilter(filter2);
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
						DataManager dm = DataManager.getInstance();
						in = new FileInputStream(filepath);
						VisualFrame visualframe = null;
						if (filepath.endsWith(".pnml")) {
							visualframe = new VisualFrame(
									ProcessObject.TYPEPNML, in);
							algorithmList.removeAllItems();
							for (String generator : dm
									.getAllUsedPetriNetIndexNameSupportGraphQuery()) {
								algorithmList.addItem(generator);
							}
						} else if (filepath.endsWith(".yawl")) {
							visualframe = new VisualFrame(
									ProcessObject.TYPEYAWL, in);
							algorithmList.removeAllItems();
							for (String generator : dm
									.getAllUsedYAWLIndexNameSupportGraphQuery()) {
								algorithmList.addItem(generator);
							}
						}
						visualPanel = visualframe.getJContentPane();
						exampleRight.removeAll();
						exampleRight.add(visualPanel, BorderLayout.CENTER);
						exampleRight.setVisible(true);
						exampleRight.updateUI();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return choose;
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

	/**
	 * This method initializes algorithmList
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getAlgorithmList() {
		if (algorithmList == null) {
			DataManager dm = DataManager.getInstance();
			Vector<String> indexListSupportGraphQuery = dm
					.getAllUsedIndexNameSupportGraphQuery();
			dm.addObserver(this);
			algorithmList = new JComboBox(indexListSupportGraphQuery);
			algorithmList.setBounds(new Rectangle(130, 122, 150, 30));
			String indexSelected = (String) algorithmList.getSelectedItem();
			if (indexSelected != null) {
				BPMIndex index = dm.getIndex(indexSelected);
				if (index != null) {
					JTextField temp = this.getJTextFieldSimilarity();
					if (index.supportSimilarQuery()) {
						temp.setEnabled(true);
						temp.setText("0.8");
					} else {
						temp.setText("1");
						temp.setEnabled(false);
					}
				}
			} else {
				JTextField temp = this.getJTextFieldSimilarity();
				temp.setEnabled(false);
				JButton button = this.getQuery();
				button.setEnabled(false);
			}
			algorithmList
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DataManager dm = DataManager.getInstance();
							String indexSelected = (String) algorithmList
									.getSelectedItem();
							if (indexSelected == null) {
								return;
							}
							BPMIndex index = dm.getIndex(indexSelected);
							if (index != null) {
								if (index.supportSimilarQuery()) {
									jTextFieldSimilarity.setEnabled(true);
									jTextFieldSimilarity.setText("0.8");
								} else {
									jTextFieldSimilarity.setText("1");
									jTextFieldSimilarity.setEnabled(false);
								}
							} else {
								System.out
										.println("there must be something wrong with the seleced index");
							}
						}
					});
		}
		return algorithmList;
	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setBounds(new Rectangle(99, 227, 70, 30));
			query.setText(resourcesManager.getString("ModelQuery.Plugin.qbe.query"));
			query.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					float similarity = Float.parseFloat(jTextFieldSimilarity
							.getText());
					if (similarity < 0 || similarity > 1) {
						System.out
								.println("the parameter of similarity is invalid");
						return;
					}

					DataManager dm = DataManager.getInstance();
					if (filepath == null || filepath.equals("")) {
						return;
					}
					String method = algorithmList.getSelectedItem().toString();
					// System.out.println("filePath"+filePath+"
					// indexName:"+indexName);
					try {
						FileInputStream fin = new FileInputStream(filepath);
						Object query = null;
						if (filepath.endsWith(".pnml")) {
							query = PetriNetUtil
									.getPetriNetFromPnmlFile(filepath);
						} else if (filepath.endsWith(".yawl")) {
							query = YAWLUtil.getYNetFromFile(filepath);
						}

						TreeSet<ProcessQueryResult> resultVector = dm
								.retrieveProcess(query, method, similarity);

						Iterator<ProcessQueryResult> itt = resultVector
								.iterator();
						while (itt.hasNext()) {
							ProcessQueryResult r = itt.next();
							r.setPo(dm.getProcess(r.getProcess_id()));
						}
						String[] newIdentifiers = { "Similarity", "Model ID", "Model Name",
								"Import Time", "Model Description", "Model Type",
								"Model Catalog", "View"
									 };
						JTable table = resultTable;
						DefaultTableModel model = new DefaultTableModel();
						model.setColumnIdentifiers(newIdentifiers);
						if (resultVector != null) {
							Iterator<ProcessQueryResult> it = resultVector
									.descendingIterator();
							while (it.hasNext()) {
								ProcessQueryResult r = it.next();
								ProcessObject po = r.getPo();
								model
										.addRow(new Object[] {
												r.getSimilarity(),
												po.getProcess_id(),
												po.getName(),
												new java.sql.Timestamp(po
														.getAddTime()),
												po.getDescription(),
												po.getType(),
												po.getCatalog_id(), "view" });
							}
						}
						table.setModel(model);
						table.validate();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		}
		return query;
	}

	/**
	 * This method initializes resultTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getResultTable() {
		if (resultTable == null) {
	
			String[] columnNames = {		resourcesManager.getString("ModelQuery.Plugin.qbe.similarity"), 
					resourcesManager.getString("ModelQuery.Plugin.qbe.modelid"),
					resourcesManager.getString("ModelQuery.Plugin.qbe.modelname"), 
					resourcesManager.getString("ModelQuery.Plugin.qbe.importtime"),									
					resourcesManager.getString("ModelQuery.Plugin.qbe.modeldes"),
					resourcesManager.getString("ModelQuery.Plugin.qbe.modeltype"),
					resourcesManager.getString("ModelQuery.Plugin.qbe.modelcat"),
					resourcesManager.getString("ModelQuery.Plugin.qbe.view")  };
			DefaultTableModel model = new DefaultTableModel(null, columnNames) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			resultTable = new JTable(model);
			resultTable.setRowHeight(30);
			resultTable.setFont(new Font("Dialog", Font.BOLD, 14));
			resultTable.setBounds(new Rectangle(50, 70, 300, 180));
			DefaultTableCellRenderer render = (DefaultTableCellRenderer) resultTable
					.getTableHeader().getDefaultRenderer();
			Dimension d = render.getSize();
			d.height = 30;
			render.setPreferredSize(d);
			resultTable.getColumn(resourcesManager.getString("ModelQuery.Plugin.qbe.view")).setCellRenderer(new ButtonRenderer());
			resultTable.getTableHeader().setFont(
					new Font("Dialog", Font.BOLD, 14));

			final int visualModel = 7;

			resultTable.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {

					Point point = e.getPoint();
					int i = resultTable.rowAtPoint(point);
					int j = resultTable.columnAtPoint(point);
					if (j == visualModel) {
						InputStream in;
						try {
							long process_id = (Long) resultTable.getValueAt(i,
									1);
							String type = (String) resultTable.getValueAt(i, 5);
							type = type.trim();
							DataManager dm = DataManager.getInstance();
							in = (InputStream) dm
									.getProcessDefinitionInputStream(process_id);
							VisualFrame visualframe = new VisualFrame(type, in);
							visualframe.setVisible(true);
							in.close();

						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

			});
		}

		return resultTable;
	}

	public class ButtonRenderer extends JButton implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof DataManager) {
			// update algorithlist
			this.algorithmList.removeAllItems();
			DataManager dm = DataManager.getInstance();
			Vector<String> vIndex = dm.getAllUsedIndexNameSupportGraphQuery();
			if (vIndex.size() == 0) {
				this.algorithmList.setEnabled(false);
				this.jTextFieldSimilarity.setText(null);
				this.jTextFieldSimilarity.setEnabled(false);
				this.query.setEnabled(false);
				this.resultTable.setEnabled(false);
			} else {
				this.algorithmList.setEnabled(true);
				this.jTextFieldSimilarity.setEnabled(true);
				this.query.setEnabled(true);
				this.resultTable.setEnabled(true);
				for (String indexName : vIndex) {
					this.algorithmList.addItem(indexName);
				}
				this.algorithmList.setSelectedIndex(0);
				String indexSelected = (String) this.algorithmList
						.getSelectedItem();
				BPMIndex index = dm.getIndex(indexSelected);

				if (index.supportSimilarQuery()) {
					jTextFieldSimilarity.setEnabled(true);
					jTextFieldSimilarity.setText("0.8");
				} else {
					jTextFieldSimilarity.setText("1");
					jTextFieldSimilarity.setEnabled(false);
				}
				this.algorithmList.updateUI();
			}
		}
	}

	/**
	 * This method initializes jTextFieldSimilarity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSimilarity() {
		if (jTextFieldSimilarity == null) {
			jTextFieldSimilarity = new JTextField();
			jTextFieldSimilarity.setBounds(new Rectangle(130, 175, 129, 22));
		}
		return jTextFieldSimilarity;
	}

}
