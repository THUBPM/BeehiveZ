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

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Observable;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.queryparser.QueryParser;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import javax.swing.JComboBox;

public class QBT extends JSplitPane implements java.util.Observer {

	private static final long serialVersionUID = 1L;
	private JPanel topPanel = null;
	private JScrollPane queryResult = null;
	private JTable resultTable = null;
	private JLabel queryInfo = null;
	private JTextArea queryCondition = null;
	private JButton query = null;
	private JLabel jLabelChooseIndex = null;
	private JComboBox jComboBoxIndex = null;
	ResourcesManager resourcesManager;
	/**
	 * This is the default constructor
	 */
	public QBT() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		resourcesManager =new ResourcesManager();
		this.setSize(650, 400);

		this.setBottomComponent(getQueryResult());
		this.setTopComponent(getTopPanel());
		this.setDividerLocation(200);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			jLabelChooseIndex = new JLabel();
			jLabelChooseIndex.setBounds(new Rectangle(13, 161, 91, 18));
			jLabelChooseIndex.setText(resourcesManager.getString("ModelQuery.Plugin.qbk.chooseIndex"));
			queryInfo = new JLabel();
			queryInfo.setBounds(new Rectangle(13, 11, 108, 22));
			queryInfo.setText(resourcesManager.getString("ModelQuery.Plugin.qbk.querystatement"));
			topPanel = new JPanel();
			topPanel.setLayout(null);
			topPanel.add(queryInfo, null);
			topPanel.add(getQueryCondition(), null);
			topPanel.add(getQuery(), null);
			topPanel.add(jLabelChooseIndex, null);
			topPanel.add(getJComboBoxIndex(), null);
		}
		return topPanel;
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
	 * This method initializes resultTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getResultTable() {
		if (resultTable == null) {

			String[] columnNames = { resourcesManager.getString("ModelQuery.Plugin.qbk.similarity"), 
									 resourcesManager.getString("ModelQuery.Plugin.qbk.modelid"), 
									 resourcesManager.getString("ModelQuery.Plugin.qbk.modelname"),
									 resourcesManager.getString("ModelQuery.Plugin.qbk.importtime"), 
									 resourcesManager.getString("ModelQuery.Plugin.qbk.modeldes"), 
									 resourcesManager.getString("ModelQuery.Plugin.qbk.modeltype"),
									 resourcesManager.getString("ModelQuery.Plugin.qbk.modelcat"),
									 resourcesManager.getString("ModelQuery.Plugin.qbk.view") 
									};
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
			resultTable.getColumn(resourcesManager.getString("ModelQuery.Plugin.qbk.view")).setCellRenderer(new ButtonRenderer());
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
							DataManager dm = DataManager.getInstance();
							in = (InputStream) dm.getProcessPnml(process_id);
							VisualFrame visualframe = new VisualFrame(
									ProcessObject.TYPEPNML, in);
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

	/**
	 * This method initializes queryCondition
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextArea getQueryCondition() {
		if (queryCondition == null) {
			queryCondition = new JTextArea();
			queryCondition.setBounds(new Rectangle(13, 41, 622, 102));
			queryCondition.setLineWrap(true);
			queryCondition
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		return queryCondition;
	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setBounds(new Rectangle(390, 155, 73, 30));
			query.setText(resourcesManager.getString("ModelQuery.Plugin.qbk.query"));
			// query ��ť��Ӧ�¼���������ֱ���������ѯ
			query.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					String query = queryCondition.getText();
					if (!query.endsWith(";")) {
						query = query + ";";
					}
					String queryMethod = jComboBoxIndex.getSelectedItem()
							.toString();
					if (queryMethod.equals("TaskRelationIndex")) {
						StringReader sr = new StringReader(query);
						QueryParser p = new QueryParser(sr);
						try {
							p.parse();
							sr.close();
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null,
									resourcesManager.getString("ModelQuery.Plugin.qbk.validate"));
							return;

						}
					}
					DataManager dm = DataManager.getInstance();
					TreeSet<ProcessQueryResult> resultVector = dm
							.retrieveProcess(query, queryMethod, 1);
					Iterator<ProcessQueryResult> itt = resultVector.iterator();
					while (itt.hasNext()) {
						ProcessQueryResult r = itt.next();
						r.setPo(dm.getProcess(r.getProcess_id()));
					}
					String[] newIdentifiers = { "Similarity", "Model ID",
							"Model Name", "Import Time", "Model Description",
							"Model Type", "Model Catalog", "View" };
					JTable table = resultTable;
					DefaultTableModel model = new DefaultTableModel();
					model.setColumnIdentifiers(newIdentifiers);
					if (resultVector != null) {
						Iterator<ProcessQueryResult> it = resultVector
								.descendingIterator();
						while (it.hasNext()) {
							ProcessQueryResult r = it.next();
							ProcessObject po = r.getPo();
							model.addRow(new Object[] { r.getSimilarity(),
									po.getProcess_id(), po.getName(),
									new java.sql.Timestamp(po.getAddTime()),
									po.getDescription(), po.getType(),
									po.getCatalog_id(), "view" });
						}
					}
					table.setModel(model);
					table.validate();

				}
			});
		}
		return query;
	}

	/**
	 * This method initializes jComboBoxIndex
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxIndex() {
		if (jComboBoxIndex == null) {
			DataManager dm = DataManager.getInstance();
			Vector<String> indexListSupportTextQuery = dm
					.getAllUsedIndexNameSupportTextQuery();
			dm.addObserver(this);
			jComboBoxIndex = new JComboBox(indexListSupportTextQuery);
			jComboBoxIndex.setBounds(new Rectangle(117, 157, 178, 27));
			String indexSelected = (String) jComboBoxIndex.getSelectedItem();
			if (indexSelected == null) {
				JButton button = this.getQuery();
				button.setEnabled(false);
			}
		}
		return jComboBoxIndex;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof DataManager) {
			// update algorithlist
			this.jComboBoxIndex.removeAllItems();
			DataManager dm = DataManager.getInstance();
			Vector<String> vIndex = dm.getAllUsedIndexNameSupportTextQuery();
			if (vIndex.size() == 0) {
				this.jComboBoxIndex.setEnabled(false);
				this.query.setEnabled(false);
				this.resultTable.setEnabled(false);
			} else {
				this.jComboBoxIndex.setEnabled(true);
				this.query.setEnabled(true);
				this.resultTable.setEnabled(true);
				for (String indexName : vIndex) {
					this.jComboBoxIndex.addItem(indexName);
				}
				this.jComboBoxIndex.setSelectedIndex(0);

				this.jComboBoxIndex.updateUI();
			}
		}
	}

} // @jve:decl-index=0:visual-constraint="15,-62"
