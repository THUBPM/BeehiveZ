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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.RltConstants;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.queryParser.Parser;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.queryParser.SimpleNode;

/**
 * Console for query processes by using relationship matrix
 * 
 * @author zhougz 2010.04.02
 * 
 */
public class QueryByRltMatrix extends JSplitPane implements RltConstants {

	private static final long serialVersionUID = 1L;
	private static final int btnWidth = 25;
	private static final int btnHeight = 29;
	private JPanel topPanel = null;
	private JScrollPane queryResult = null;
	private JTable resultTable = null;
	private JLabel queryInfo = null;
	private JTextArea queryCondition = null;
	private JButton query = null;
	private JToolBar tbRltSymble = null;
	private JLabel labelRltSymbles = null;
	private JLabel lblExample = null;

	/**
	 * This is the default constructor
	 */
	public QueryByRltMatrix() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(650, 400);
		this.setBottomComponent(getQueryResult());
		this.setTopComponent(getTopPanel());
		this.setDividerLocation(200);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		AddSymble listener = new AddSymble();
		for (int i = 0; i < ARR_RELATIONS.length; i++) {
			JButton curBtn = new JButton(ARR_RELATIONS[i]);
			curBtn.setSize(new Dimension(btnWidth, btnHeight));
			curBtn.setToolTipText(ARR_STR_RELATIONS[i]);
			curBtn.addActionListener(listener);
			tbRltSymble.add(curBtn);

		}

		JButton btnAnd = new JButton(" and ");
		btnAnd.setSize(new Dimension(btnWidth, btnHeight));
		btnAnd.setToolTipText("and");
		btnAnd.addActionListener(listener);
		tbRltSymble.add(btnAnd);

		JButton btnOr = new JButton(" or ");
		btnOr.setSize(new Dimension(btnWidth, btnHeight));
		btnOr.setToolTipText("or");
		btnOr.addActionListener(listener);
		tbRltSymble.add(btnOr);

	}

	class AddSymble implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			queryCondition.insert(source.getText(), queryCondition
					.getCaretPosition());
			queryCondition.grabFocus();
			// System.out.println(source.getText() + ", " + source.getSize());
			// queryCondition.insert(arg0, arg1)
		}
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			lblExample = new JLabel();
			lblExample.setBounds(new Rectangle(138, 137, 467, 18));
			lblExample.setText("Example: \"a\"↠\"f\" and (\"c\"  or \"d\")");
			lblExample.setToolTipText("Click me to input query example.");
			lblExample.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// System.out.println("mouseClicked()"); // TODO
					// Auto-generated Event stub mouseClicked()
					if (queryCondition != null) {
						queryCondition
								.setText("\"a\"↠\"f\" and (\"c\"  or \"d\")");
					}
				}
			});
			labelRltSymbles = new JLabel();
			labelRltSymbles.setBounds(new Rectangle(50, 22, 76, 18));
			labelRltSymbles.setText("relationship:");
			queryInfo = new JLabel();
			queryInfo.setBounds(new Rectangle(22, 50, 108, 30));
			queryInfo.setText("query relationship:");
			queryInfo.setToolTipText("Click to show query expression BNF.");
			queryInfo.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					StringBuffer tip = new StringBuffer(
							"BNF:\nExpression "
									+ "::= AndExpression {\"or\" AndExpression}\nAndExpression "
									+ "::= UnaryExpression {\"and\" UnaryExpression}\n"
									+ "UnaryExpression :: = \"(\" Expression \")\" | "
									+ "Activity [Op Activity]\n Activity ::= "
									+ "\"\"\"\" (~[\"\"\"\"])+ \"\"\"\"\"\nOP ::=");
					for (int i = 0; i < ARR_RELATIONS.length; i++) {
						tip.append(ARR_RELATIONS[i]);
						tip.append(" | ");
					}
					JOptionPane.showMessageDialog(getParent(), tip
							.deleteCharAt(tip.length() - 2).toString());
				}
			});
			topPanel = new JPanel();
			topPanel.setLayout(null);
			topPanel.add(queryInfo, null);
			topPanel.add(getQueryCondition(), null);
			topPanel.add(getQuery(), null);
			topPanel.add(getTbRltSymble(), null);
			topPanel.add(labelRltSymbles, null);
			topPanel.add(lblExample, null);
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

			String[] columnNames = { "Model ID", "Model Name", "Import Time",
					"Model Description", "Model Type", "Model Catalog", "View" };
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
			resultTable.getColumn("View").setCellRenderer(new ButtonRenderer());
			resultTable.getTableHeader().setFont(
					new Font("Dialog", Font.BOLD, 14));

			final int visualModel = 6;

			resultTable.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {

					Point point = e.getPoint();
					int i = resultTable.rowAtPoint(point);
					int j = resultTable.columnAtPoint(point);
					if (j == visualModel) {
						InputStream in;
						try {
							long process_id = (Long) resultTable.getValueAt(i,
									0);
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
			queryCondition.setBounds(new Rectangle(138, 52, 468, 79));
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
			query.setBounds(new Rectangle(316, 166, 73, 30));
			query.setText("query");
			query.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String keywords = queryCondition.getText();
					SimpleNode syntaxTree = null;

					// String keywords = "\"first\"↠\"second\" and \"third\"";
					try {
						if (!keywords.endsWith(";")) {
							keywords = keywords + ";";
						}
						InputStreamReader is = new InputStreamReader(
								new ByteArrayInputStream(keywords
										.getBytes("UTF-8")));
						Parser p = new Parser(is);
						syntaxTree = p.Start();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(getParent(), e1
								.getMessage());
						e1.printStackTrace();
						return;
					}

					DataManager dm = DataManager.getInstance();
					TreeSet<ProcessQueryResult> resultVector = dm
							.retrieveProcess(syntaxTree, "RltMatrixIndexNo1", 1);

					String[] newIdentifiers = { "Model ID", "Model Name",
							"Import Time", "Model Description", "Model Type",
							"Model Catalog", "View" };
					JTable table = resultTable;
					DefaultTableModel model = new DefaultTableModel();
					model.setColumnIdentifiers(newIdentifiers);
					if (resultVector != null) {
						Iterator<ProcessQueryResult> it = resultVector
								.iterator();
						while (it.hasNext()) {
							ProcessObject po = it.next().getPo();
							model.addRow(new Object[] { po.getProcess_id(),
									po.getName(),
									new java.sql.Timestamp(po.getAddTime()),
									po.getDescription(), po.getType(),
									po.getCatalog_id(), "view" });
						}
						//						
						// for (int i = 0; i < resultVector.size(); i++) {
						// ProcessObject po = resultVector.;
						// model.addRow(new Object[] { po.getProcess_id(),
						// po.getName(),
						// new java.sql.Timestamp(po.getAddTime()),
						// po.getDescription(), po.getType(),
						// po.getCatalog_id(), "view" });
						// }
					}
					table.setModel(model);
					table.validate();
				}
			});
		}
		return query;
	}

	/**
	 * This method initializes tbRltSymble
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getTbRltSymble() {
		if (tbRltSymble == null) {
			tbRltSymble = new JToolBar();

			tbRltSymble.setBounds(new Rectangle(136, 12, 136 + btnWidth
					* (this.ARR_RELATIONS.length + 2) + 20, 10 + btnHeight));
			tbRltSymble.setLayout(new GridLayout(1,
					this.ARR_RELATIONS.length + 2));
		}
		return tbRltSymble;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
