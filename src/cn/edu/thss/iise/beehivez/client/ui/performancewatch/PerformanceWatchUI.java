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
package cn.edu.thss.iise.beehivez.client.ui.performancewatch;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.DatabaseAccessor;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.OplogObject;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.framework.ui.filters.GenericFileFilter;

/**
 * @author JinTao 2009.9.8
 * 
 */
public class PerformanceWatchUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;
	private JScrollPane jAllLogScrollPane = null;
	private JTable jAllLogTable = null;
	private JScrollPane jAggregatedLogScrollPane = null;
	private JTable jAggregatedLogTable = null;
	private JPopupMenu jLogViewPopupMenu = null; // @jve:decl-index=0:visual-constraint="522,103"
	private JMenuItem jMenuItemRefresh = null;
	private JMenuItem jMenuItemDeleteAll = null;

	private DefaultTableModel allLogTableModel = null;
	private DefaultTableModel aggregatedLogTableModel = null;
	private JMenuItem jMenuItemExportIndexLogAdd = null;
	private JMenu jMenuExportIndexlog = null;
	private JMenuItem jMenuItemExportIndexLogQuery = null;
	ResourcesManager resourcesManager;
	
	private void loadAllLogDataFromDB() {
		allLogTableModel.setRowCount(0);
		DataManager dm = DataManager.getInstance();
		Vector<OplogObject> vOplog = dm.getAllOplog();
		for (int i = 0; i < vOplog.size(); i++) {
			OplogObject logObject = vOplog.get(i);
			allLogTableModel.addRow(new Object[] {
					new java.sql.Timestamp(logObject.getTimestamp()),
					logObject.getIndexname(), logObject.getOptype(),
					logObject.getOperand(), logObject.getTimecost(),
					logObject.getNplace(), logObject.getNtransition(),
					logObject.getNarc(), logObject.getNpetri(),
					logObject.getResultsize() });
		}
	}

	private void exportIndexLog(String type) {

		DataManager dm = DataManager.getInstance();
		String select = "select * from oplog where optype='" + type + "'";
		ResultSet rs = dm.executeSelectSQL(select, 0, Integer.MAX_VALUE, dm
				.getFetchSize());

		// choose the destination file to store the data
		JFileChooser chooser = new JFileChooser();
		GenericFileFilter filter = new GenericFileFilter("csv");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filename = filter.addExtension(chooser.getSelectedFile()
					.getPath());
			if (filename != null && !filename.equals("")) {
				try {
					FileWriter fw = new FileWriter(filename, false);
					BufferedWriter bw = new BufferedWriter(fw);
					// write the header
					String header = "id, indexname, optype, operand, timecost, nplace, ntransition, narc, npetri, resultsize";
					bw.write(header);
					bw.newLine();

					// write the log data
					long event_id;
					String indexname;
					String optype;
					String operand;
					long timecost;
					int nplace, ntransition, narc;
					long npetri;
					int resultsize;

					String exportData;
					try {
						while (rs.next()) {
							event_id = rs.getLong("event_id");
							indexname = rs.getString("indexname");
							optype = rs.getString("optype");
							operand = rs.getString("operand");
							timecost = rs.getLong("timecost");
							nplace = rs.getInt("nplace");
							ntransition = rs.getInt("ntransition");
							narc = rs.getInt("narc");
							npetri = rs.getLong("npetri");
							resultsize = rs.getInt("resultsize");
							exportData = String.valueOf(event_id) + ", "
									+ indexname + ", " + optype + ", "
									+ operand + ", " + String.valueOf(timecost)
									+ ", " + String.valueOf(nplace) + ", "
									+ String.valueOf(ntransition) + ", "
									+ String.valueOf(narc) + ", "
									+ String.valueOf(npetri) + ", "
									+ String.valueOf(resultsize);
							;
							bw.write(exportData);
							bw.newLine();
						}
						Statement stmt = rs.getStatement();
						rs.close();
						stmt.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"database access error");
					}

					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "file access error");
				}
			}
		}
	}

	private void loadAggregatedLogDataFromDB() {
		aggregatedLogTableModel.setRowCount(0);
		DataManager dm = DataManager.getInstance();
		Vector<OplogObject> vo = dm.getAverageTimeCostGroupByNameAndType();
		for (int i = 0; i < vo.size(); i++) {
			OplogObject o = vo.get(i);
			aggregatedLogTableModel.addRow(new Object[] { o.getIndexname(),
					o.getOptype(), o.getTimecost() });
		}
	}

	/**
	 * This is the default constructor
	 */
	public PerformanceWatchUI() {
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
		if (allLogTableModel == null) {
			String[] columnHeader = { 
						resourcesManager.getString("PerformanceWatch.alllog.ot"), 
						resourcesManager.getString("PerformanceWatch.alllog.in"),
						resourcesManager.getString("PerformanceWatch.alllog.operationtype"), 
						resourcesManager.getString("PerformanceWatch.alllog.operand"), 
						resourcesManager.getString("PerformanceWatch.alllog.timecost"),
						resourcesManager.getString("PerformanceWatch.alllog.numpla"), 
						resourcesManager.getString("PerformanceWatch.alllog.numtra"), 
						resourcesManager.getString("PerformanceWatch.alllog.numarc"),
						resourcesManager.getString("PerformanceWatch.alllog.numpn"), 
						resourcesManager.getString("PerformanceWatch.alllog.rsz") };
			allLogTableModel = new DefaultTableModel();
			allLogTableModel.setColumnIdentifiers(columnHeader);
		}

		if (aggregatedLogTableModel == null) {
			String[] columnHeader = { 
					resourcesManager.getString("PerformanceWatch.dal.indexname"), 
					resourcesManager.getString("PerformanceWatch.dal.operationtype"),
					resourcesManager.getString("PerformanceWatch.dal.atc") };
			aggregatedLogTableModel = new DefaultTableModel();
			aggregatedLogTableModel.setColumnIdentifiers(columnHeader);
		}

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		this.setSize(485, 321);
		this.setLayout(new GridBagLayout());
		this.add(getJTabbedPane(), gridBagConstraints);
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setName("");
			jTabbedPane.addTab(resourcesManager.getString("PerformanceWatch.alllog.tabtitle"), getJAllLogScrollPane());
			jTabbedPane.addTab(resourcesManager.getString("PerformanceWatch.dal.tabtitle"),
					getJAggregatedLogScrollPane());
			jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						getJLogViewPopupMenu();
						jLogViewPopupMenu.show(jTabbedPane, e.getX(), e.getY());
					}
				}
			});
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jAllLogScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJAllLogScrollPane() {
		if (jAllLogScrollPane == null) {
			jAllLogScrollPane = new JScrollPane();
			jAllLogScrollPane.setViewportView(getJAllLogTable());
		}
		return jAllLogScrollPane;
	}

	/**
	 * This method initializes jAllLogTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJAllLogTable() {
		if (jAllLogTable == null) {
			jAllLogTable = new JTable();
			this.loadAllLogDataFromDB();
			jAllLogTable.setModel(allLogTableModel);
		}
		return jAllLogTable;
	}

	/**
	 * This method initializes jAggregatedLogScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJAggregatedLogScrollPane() {
		if (jAggregatedLogScrollPane == null) {
			jAggregatedLogScrollPane = new JScrollPane();
			jAggregatedLogScrollPane.setViewportView(getJAggregatedLogTable());
		}
		return jAggregatedLogScrollPane;
	}

	/**
	 * This method initializes jAggregatedLogTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJAggregatedLogTable() {
		if (jAggregatedLogTable == null) {
			jAggregatedLogTable = new JTable();
			this.loadAggregatedLogDataFromDB();
			jAggregatedLogTable.setModel(aggregatedLogTableModel);
		}
		return jAggregatedLogTable;
	}

	/**
	 * This method initializes jLogViewPopupMenu
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJLogViewPopupMenu() {
		if (jLogViewPopupMenu == null) {
			jLogViewPopupMenu = new JPopupMenu();
			jLogViewPopupMenu.add(getJMenuExportIndexlog());
			jLogViewPopupMenu.add(getJRefreshMenuItem());
			jLogViewPopupMenu.add(getJMenuItemDeleteAll());
		}
		return jLogViewPopupMenu;
	}

	/**
	 * This method initializes jRefreshMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJRefreshMenuItem() {
		if (jMenuItemRefresh == null) {
			jMenuItemRefresh = new JMenuItem();
			jMenuItemRefresh.setText(resourcesManager.getString("PerformanceWatch.misc.refresh"));
			jMenuItemRefresh
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							loadAllLogDataFromDB();
							loadAggregatedLogDataFromDB();
							allLogTableModel.fireTableDataChanged();
							aggregatedLogTableModel.fireTableDataChanged();
						}
					});
		}
		return jMenuItemRefresh;
	}

	public void refreshTable() {
		loadAllLogDataFromDB();
		loadAggregatedLogDataFromDB();
		allLogTableModel.fireTableDataChanged();
		aggregatedLogTableModel.fireTableDataChanged();
	}

	/**
	 * This method initializes jMenuItemDeleteAll
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemDeleteAll() {
		if (jMenuItemDeleteAll == null) {
			jMenuItemDeleteAll = new JMenuItem();
			jMenuItemDeleteAll.setText(resourcesManager.getString("PerformanceWatch.misc.dal"));
			jMenuItemDeleteAll
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Indexlogger.createNewLogs();
							DataManager dm = DataManager.getInstance();
							dm.delAllOplog();
							allLogTableModel.setRowCount(0);
							aggregatedLogTableModel.setRowCount(0);
							allLogTableModel.fireTableDataChanged();
							aggregatedLogTableModel.fireTableDataChanged();
						}
					});
		}
		return jMenuItemDeleteAll;
	}

	/**
	 * This method initializes jMenuItemExportIndexLogAdd
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemExportIndexLogAdd() {
		if (jMenuItemExportIndexLogAdd == null) {
			jMenuItemExportIndexLogAdd = new JMenuItem();
			jMenuItemExportIndexLogAdd.setText(resourcesManager.getString("PerformanceWatch.misc.lod"));
			jMenuItemExportIndexLogAdd
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							exportIndexLog(OplogObject.ADDDATATOINDEX);
						}
					});
		}
		return jMenuItemExportIndexLogAdd;
	}

	/**
	 * This method initializes jMenuExportIndexlog
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getJMenuExportIndexlog() {
		if (jMenuExportIndexlog == null) {
			jMenuExportIndexlog = new JMenu();
			jMenuExportIndexlog.setText(resourcesManager.getString("PerformanceWatch.misc.eil"));
			jMenuExportIndexlog.add(getJMenuItemExportIndexLogQuery());
			jMenuExportIndexlog.add(getJMenuItemExportIndexLogAdd());
		}
		return jMenuExportIndexlog;
	}

	/**
	 * This method initializes jMenuItemExportIndexLogQuery
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemExportIndexLogQuery() {
		if (jMenuItemExportIndexLogQuery == null) {
			jMenuItemExportIndexLogQuery = new JMenuItem();
			jMenuItemExportIndexLogQuery.setText(resourcesManager.getString("PerformanceWatch.misc.loq"));
			jMenuItemExportIndexLogQuery
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							exportIndexLog(OplogObject.QUERYDATAUSEINDEX);
						}
					});
		}
		return jMenuItemExportIndexLogQuery;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
