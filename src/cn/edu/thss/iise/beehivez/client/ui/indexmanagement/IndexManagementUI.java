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
package cn.edu.thss.iise.beehivez.client.ui.indexmanagement;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.IndexinfoObject;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * @author JinTao 2009.9.8
 * 
 */
public class IndexManagementUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jIndexManagementTabbedPane = null;
	private JScrollPane jIndexInfoScrollPane = null;
	private JTable jIndexInfoTable = null;
	private JPanel jIndexRegisterPanel = null;
	private JLabel jIndexNameLabel = null;
	private JTextField jIndexNameTextField = null;
	private JLabel jIndexDescriptionLabel = null;
	private JTextArea jIndexDescriptionTextArea = null;
	private JButton jRegisterButton = null;
	private DefaultTableModel indexInfoTableModel = null;
	ResourcesManager resourcesManager;

	private void loadIndexInfoTable() {
		indexInfoTableModel.setRowCount(0);
		DataManager dm = DataManager.getInstance();
		Vector<IndexinfoObject> viio = dm.getAllIndexInfo();
		for (int i = 0; i < viio.size(); i++) {
			IndexinfoObject iio = viio.get(i);
			indexInfoTableModel.addRow(new Object[] { iio.getIndex_id(),
					iio.getJavaclassName(), iio.getDescription(),
					iio.getState(), "delete" });
		}
	}

	/**
	 * This is the default constructor
	 */
	public IndexManagementUI() {
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
		if (indexInfoTableModel == null) {
			indexInfoTableModel = new DefaultTableModel();
			String[] columnHeader = new String[] { 
						resourcesManager.getString("IndexManagement.info.indexid"),
						resourcesManager.getString("IndexManagement.info.icn"), 
						resourcesManager.getString("IndexManagement.info.description"), 
						resourcesManager.getString("IndexManagement.info.state"), 
						resourcesManager.getString("IndexManagement.info.operation") };
			indexInfoTableModel.setColumnIdentifiers(columnHeader);
		}
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		this.setSize(444, 311);
		this.setLayout(new GridBagLayout());
		this.add(getJIndexManagementTabbedPane(), gridBagConstraints);
	}

	/**
	 * This method initializes jIndexManagementTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJIndexManagementTabbedPane() {
		if (jIndexManagementTabbedPane == null) {
			jIndexManagementTabbedPane = new JTabbedPane();
			jIndexManagementTabbedPane.addTab(resourcesManager.getString("IndexManagement.info.tabtitle"),
					getJIndexInfoScrollPane());
			jIndexManagementTabbedPane.addTab(resourcesManager.getString("IndexManagement.register.tabtitle"),
					getJIndexRegisterPanel());
		}
		return jIndexManagementTabbedPane;
	}

	/**
	 * This method initializes jIndexInfoScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJIndexInfoScrollPane() {
		if (jIndexInfoScrollPane == null) {
			jIndexInfoScrollPane = new JScrollPane();
			jIndexInfoScrollPane.setViewportView(getJIndexInfoTable());
		}
		return jIndexInfoScrollPane;
	}

	/**
	 * This method initializes jIndexInfoTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJIndexInfoTable() {
		if (jIndexInfoTable == null) {
			jIndexInfoTable = new JTable();
			this.loadIndexInfoTable();
			jIndexInfoTable.setModel(indexInfoTableModel);

			jIndexInfoTable
					.addMouseListener(new java.awt.event.MouseListener() {
						public void mouseClicked(java.awt.event.MouseEvent e) {
							Point p = e.getPoint();
							int row = jIndexInfoTable.rowAtPoint(p);
							int col = jIndexInfoTable.columnAtPoint(p);
							if (col == 3) {
								// change the state
								String javaClassName = (String) jIndexInfoTable
										.getValueAt(row, 1);
								String state = (String) jIndexInfoTable
										.getValueAt(row, 3);
								state = state.trim();
								if (state
										.equalsIgnoreCase(IndexinfoObject.USED)) {
									state = IndexinfoObject.UNUSED;
								} else if (state
										.equalsIgnoreCase(IndexinfoObject.UNUSED)) {
									state = IndexinfoObject.USED;
								}
								DataManager dm = DataManager.getInstance();
								dm.updateIndexState(javaClassName, state);
								loadIndexInfoTable();
								indexInfoTableModel.fireTableDataChanged();
							} else if (col == 4) {
								// delete this index
								long index_id = (Long) jIndexInfoTable
										.getValueAt(row, 0);
								String javaClassName = (String) jIndexInfoTable
										.getValueAt(row, 1);
								DataManager dm = DataManager.getInstance();
								dm.delIndexInfo(javaClassName);
								loadIndexInfoTable();
								indexInfoTableModel.fireTableDataChanged();
							}
						}

						public void mousePressed(java.awt.event.MouseEvent e) {
						}

						public void mouseReleased(java.awt.event.MouseEvent e) {
						}

						public void mouseEntered(java.awt.event.MouseEvent e) {
						}

						public void mouseExited(java.awt.event.MouseEvent e) {
						}
					});
		}
		return jIndexInfoTable;
	}

	/**
	 * This method initializes jIndexRegisterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJIndexRegisterPanel() {
		if (jIndexRegisterPanel == null) {
			jIndexDescriptionLabel = new JLabel();
			jIndexDescriptionLabel.setBounds(new Rectangle(19, 65, 113, 18));
			jIndexDescriptionLabel.setText(resourcesManager.getString("IndexManagement.register.indexdes"));
			jIndexNameLabel = new JLabel();
			jIndexNameLabel.setBounds(new Rectangle(16, 16, 73, 18));
			jIndexNameLabel.setText(resourcesManager.getString("IndexManagement.register.indexname"));
			jIndexRegisterPanel = new JPanel();
			jIndexRegisterPanel.setLayout(null);
			jIndexRegisterPanel.add(jIndexNameLabel, null);
			jIndexRegisterPanel.add(getJIndexNameTextField(), null);
			jIndexRegisterPanel.add(jIndexDescriptionLabel, null);
			jIndexRegisterPanel.add(getJIndexDescriptionTextArea(), null);
			jIndexRegisterPanel.add(getJRegisterButton(), null);
		}
		return jIndexRegisterPanel;
	}

	/**
	 * This method initializes jIndexNameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJIndexNameTextField() {
		if (jIndexNameTextField == null) {
			jIndexNameTextField = new JTextField();
			jIndexNameTextField.setBounds(new Rectangle(107, 16, 321, 22));
		}
		return jIndexNameTextField;
	}

	/**
	 * This method initializes jIndexDescriptionTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJIndexDescriptionTextArea() {
		if (jIndexDescriptionTextArea == null) {
			jIndexDescriptionTextArea = new JTextArea();
			jIndexDescriptionTextArea
					.setBounds(new Rectangle(20, 87, 407, 130));
		}
		return jIndexDescriptionTextArea;
	}

	/**
	 * This method initializes jRegisterButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJRegisterButton() {
		if (jRegisterButton == null) {
			jRegisterButton = new JButton();
			jRegisterButton.setBounds(new Rectangle(158, 232, 110, 23));
			jRegisterButton.setText(resourcesManager.getString("IndexManagement.register.register"));
			jRegisterButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							String javaClassName = jIndexNameTextField
									.getText();
							String description = jIndexDescriptionTextArea
									.getText();
							if (javaClassName != null) {
								javaClassName = javaClassName.trim();
								if (javaClassName.length() > 0) {
									DataManager dm = DataManager.getInstance();
									dm.addIndexInfo(javaClassName, description);
									loadIndexInfoTable();
									indexInfoTableModel.fireTableDataChanged();
									jIndexNameTextField.setText(null);
									jIndexDescriptionTextArea.setText(null);
									JOptionPane.showMessageDialog(null,
											resourcesManager.getString("IndexManagement.register.suc"));
								}
							}
						}
					});
		}
		return jRegisterButton;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
