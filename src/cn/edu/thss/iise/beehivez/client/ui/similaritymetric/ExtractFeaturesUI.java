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
 *@Author Wang Wenxing 
 *
 */
package cn.edu.thss.iise.beehivez.client.ui.similaritymetric;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.client.ui.modelio.*;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.metric.tar.temp.ONOrderingRelation;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

import pipe.gui.CreateGui;

/**
 * Institute of Information System and Engineering
 * TsingHua University
 * Last edited on 2011-1-13
 */
public class ExtractFeaturesUI extends JSplitPane implements Observer {
	
	private static final long serialVersionUID = 1L;
	private JSplitPane ExamplePane = null;
	private JPanel exampleLeft = null;
	private JPanel exampleRight = null;
	private JScrollPane queryResult = null;
	private JLabel jLabel = null;
	private JButton draw = null;
	private JButton choose = null;
	private JLabel jLabel1 = null;
	private Vector<String> algorithm = null;
	private int currentAlgorithm = 0;
	private JComboBox algorithmList = null;
	private JButton start = null;
	public String filepath = null;
	public JPanel visualPanel = new JPanel();
	private JTable resultTable = null;
	private JLabel jLabelSimilarity = null;
	private JTextField jTextFieldSimilarity = null;
	private JTextArea featureResult = null;
	ResourcesManager resourcesManager = new ResourcesManager();
	
	public ExtractFeaturesUI(){
		super();
		initialize();
	}
	
	private void initialize() {
		this.setSize(733, 644);

		this.loadAlgorithms();
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
			jLabelSimilarity.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.similarity"));
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(16, 129, 85, 18));
			jLabel1.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.algorithm"));
			jLabel = new JLabel();
			jLabel.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.chooseexample"));
			jLabel.setBounds(new Rectangle(16, 18, 106, 18));
			exampleLeft = new JPanel();
			exampleLeft.setLayout(null);
			exampleLeft.add(jLabel, null);
			exampleLeft.add(getDraw(), null);
			exampleLeft.add(getChoose(), null);
			exampleLeft.add(jLabel1, null);
			exampleLeft.add(getAlgorithmList(), null);
			exampleLeft.add(getStart(), null);
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
			featureResult = new JTextArea();
			queryResult = new JScrollPane(featureResult);
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
			draw.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.drawmodel"));
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
			choose.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.chooseexample2"));
			choose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(GlobalParameter
							.getQueryObjectPath());
					ExtensionFilter filter1 = new ExtensionFilter(".pnml",
							"PNML files (*.pnml)");
					ExtensionFilter filter2 = new ExtensionFilter(".yawl",
							"YAWL files (*.yawl)");
					fileChooser.setDialogTitle(resourcesManager.getString("ProcessSimilarityMetric.ef.omf"));
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
		algorithmList = new JComboBox();
		algorithmList.setBounds(new Rectangle(130, 122, 150, 30));
		for(int i = 0; i < algorithm.size(); ++i){
			try{
				Class algorithmClass;
				algorithmClass = Class.forName(algorithm.get(i));
				Object algorithmObj = null;
				algorithmObj = algorithmClass.newInstance();
				
				Method method = algorithmClass.getMethod("getName", null);
				Object result = method.invoke(algorithmObj, null);
				algorithmList.addItem((String) result);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		algorithmList.setEditable(false);
		algorithmList.setSelectedItem(currentAlgorithm);
		algorithmList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				currentAlgorithm = ((JComboBox)(e.getSource())).getSelectedIndex();
			}
		});
		return algorithmList;
	}
	
	void loadAlgorithms() {
		algorithm = new Vector<String>();
		// �����Զ����㷨�����������
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.JaccardTARSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.JaccardStructureSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.LabelFreeTARSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.ContextBasedSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.CausalFootprintSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.BPSSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity_Wang");
		algorithm
				.add("cn.edu.thss.iise.beehivez.server.metric.ExtensiveTARSimilarity");
	}
	
	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStart() {
		start = new JButton();
		start.setBounds(new Rectangle(99, 227, 70, 30));
		start.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.start"));
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(filepath == null || filepath.equals("")){
					featureResult.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.pc"));
					return;
				}
				PetriNet petri = null;
				try{
					FileInputStream fin = new FileInputStream(filepath);
					if (filepath.endsWith(".pnml")) {
						petri = PetriNetUtil
								.getPetriNetFromPnmlFile(filepath);
					} else if (filepath.endsWith(".yawl")) {
						featureResult.setText(resourcesManager.getString("ProcessSimilarityMetric.ef.pc"));
						return;
					}
				} catch (Exception e){
					e.printStackTrace();
				}
				MyPetriNet mpn = MyPetriNet.PromPN2MyPN(petri);
				ONCompleteFinitePrefix cfp = null;
				ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(mpn);
				cfp = cfpBuilder.Build();
				ONOrderingRelation or = new ONOrderingRelation(cfp);
//				featureResult.setText(or.getOrderRelations().toString());
				String line = null;
				for(int i = 0; i < or.getOrderRelations().length; ++i){
					line += "    " + cfp.getOn().getEveSet().get(i).getLabel();
				}
				featureResult.setText(line);
				for(int i = 0; i < or.getOrderRelations().length; ++i){
					featureResult.setText(cfp.getOn().getEveSet().get(i).getLabel() + ":");
					for(int j = 0; j < or.getOrderRelations().length; ++j){
						featureResult.setText("    ");
						featureResult.setText("" + or.getOrderRelations()[i][j]);
					}
					System.out.println();
				}
			}
		});
		
		return start;
	}
	
	/**
	 * This method initializes resultTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getResultTable() {
		if (resultTable == null) {

			String[] columnNames = { 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.similarity"), 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.modelid"), 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.mn"),
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.it"), 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.md"), 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.mt"),
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.mc"), 
						resourcesManager.getString("ProcessSimilarityMetric.ef.table.view") 
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
			resultTable.getColumn(resourcesManager.getString("ProcessSimilarityMetric.ef.table.view")).setCellRenderer(new ButtonRenderer());
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

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (o instanceof DataManager) {
			// update algorithlist
//			this.algorithmList.removeAllItems();
//			DataManager dm = DataManager.getInstance();
//			Vector<String> vIndex = dm.getAllUsedIndexNameSupportGraphQuery();
//			if (vIndex.size() == 0) {
//				this.algorithmList.setEnabled(false);
//				this.jTextFieldSimilarity.setText(null);
//				this.jTextFieldSimilarity.setEnabled(false);
//				this.query.setEnabled(false);
//				this.resultTable.setEnabled(false);
//			} else {
//				this.algorithmList.setEnabled(true);
//				this.jTextFieldSimilarity.setEnabled(true);
//				this.query.setEnabled(true);
//				this.resultTable.setEnabled(true);
//				for (String indexName : vIndex) {
//					this.algorithmList.addItem(indexName);
//				}
//				this.algorithmList.setSelectedIndex(0);
//				String indexSelected = (String) this.algorithmList
//						.getSelectedItem();
//				BPMIndex index = dm.getIndex(indexSelected);
//
//				if (index.supportSimilarQuery()) {
//					jTextFieldSimilarity.setEnabled(true);
//					jTextFieldSimilarity.setText("0.8");
//				} else {
//					jTextFieldSimilarity.setText("1");
//					jTextFieldSimilarity.setEnabled(false);
//				}
//				this.algorithmList.updateUI();
//			}
		}
	}

}
