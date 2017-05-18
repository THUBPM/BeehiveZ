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
package cn.edu.thss.iise.beehivez.client.ui.customizableloggenerator;

import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import cn.edu.thss.iise.beehivez.client.ui.miningevaluate.MyTableModel;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
import cn.edu.thss.iise.beehivez.util.loggenerator.AverageWeightLPM;
import cn.edu.thss.iise.beehivez.util.loggenerator.CompleteParameters;
import cn.edu.thss.iise.beehivez.util.loggenerator.CustomeizableLPM;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogManager;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod;
import cn.edu.thss.iise.beehivez.util.loggenerator.NoiseParameters;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.awt.Point;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.ui.Message;
import org.processmining.framework.ui.MessagePanel;
import org.processmining.importing.pnml.PnmlImport;
/**
 * @author Nianhua  Wu 2011.3.10
 * 
 */
public class CustomizableLogGeneratorUI extends JSplitPane {

	private JPasswordField jPasswordField = null;
	private JSplitPane topPanel = null;
	private JTabbedPane topRightPanel = null;
	private JPanel topLeftPanel = null;  
	private JPanel completenessConfig = null;
	private JPanel noiseConfig = null;
	private JScrollPane jScrollPane = null;
	private MyTableModel TableModel = new MyTableModel();
	private JTable Table = null;
	private JButton chooseModel = null;
	private JLabel jLabel = null;
	private String modelpath = "";  
	public HashMap<String, Integer> filenummap = null;
	private Object[][] data ;
	private JLabel completeConfig = null;
	private JLabel noiseTypeConfig = null;
	private JRadioButton noNoise = null;
	private JRadioButton haveNoise = null;
	private JCheckBox noHead = null;
	private JCheckBox noTail = null;
	private JCheckBox partBody = null;
	private JCheckBox removeEvent = null;
	private JCheckBox interChange = null;
	private JTextField noisePercent = null;
	private JSlider percentControl = null;
	private JRadioButton tarCompleteness = null;
	private JRadioButton causalCompleteness = null;
	private JCheckBox DS = null;
	private JCheckBox DS1 = null;
	private JRadioButton jRadioButton = null;
	private JCheckBox ES = null;
	private JCheckBox TS = null;
	private JTextField complete1 = null;
	private JTextField complete2 = null;
	private JSlider Slider1 = null;
	private JSlider Slider2 = null;
	private ButtonGroup group= null;  //  @jve:decl-index=0:
	private JTextField esValue = null;
	private MessagePanel messagePanel = new MessagePanel();
	private JPanel buttonPanel = null;
	private JPanel panel = null;
	private JButton submit = null;
	private JLabel jLabel1 = null;
	private int  noiseFlag = 1;
	private int noiseType[]={0,0,0,0,0};
	private int tarType[]={1,0};
	private int freType[]={1,0};
	private int completeType = 1;
	private CompleteParameters comPara = new CompleteParameters();  //  @jve:decl-index=0:
	private NoiseParameters noiPara = new NoiseParameters();  //  @jve:decl-index=0:
	private JSlider Slider3 = null;
	ResourcesManager resourcesManager = new ResourcesManager();

	
	/**
	 * This method initializes 
	 * 
	 */
	public CustomizableLogGeneratorUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        messagePanel.setBounds(new Rectangle(0, 54, 798, 175));
        this.setSize(new Dimension(800, 600));
        this.setBottomComponent(getButtonPanel());
        this.setDividerLocation(360);
        this.setTopComponent(getTopPanel());
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		Message.add(resourcesManager.getString("CustomizableLog.ui.message8"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message9"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message10"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message11"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message12"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message13"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message14"));
		Message.add(resourcesManager.getString("CustomizableLog.ui.message15"));
			
	}

	/**
	 * This method initializes topPanel	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getTopPanel() {
		if (topPanel == null) {
			topPanel = new JSplitPane();
			topPanel.setDividerLocation(330);
			topPanel.setRightComponent(getTopRightPanel());
			topPanel.setLeftComponent(getTopLeftPanel());

		}
		return topPanel;
	}

	/**
	 * This method initializes topRightPanel	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getTopRightPanel() {
		if (topRightPanel == null) {
			topRightPanel = new JTabbedPane();
			topRightPanel.addTab(resourcesManager.getString("CustomizableLog.ui.cconf"), null, getCompletenessConfig(), null);
			topRightPanel.addTab(resourcesManager.getString("CustomizableLog.ui.nconf"), null, getNoiseConfig(), null);
		}
		return topRightPanel;
	}

	/**
	 * This method initializes topLeftPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTopLeftPanel() {
		if (topLeftPanel == null) {
			jLabel = new JLabel();
			jLabel.setText(resourcesManager.getString("CustomizableLog.ui.processmodels"));
			jLabel.setBounds(new Rectangle(26, 10, 116, 30));
			topLeftPanel = new JPanel();
			topLeftPanel.setLayout(null);
			topLeftPanel.setSize(new Dimension(350, 350));
			topLeftPanel.add(jLabel, null);
			topLeftPanel.add(getJScrollPane(), null);
			topLeftPanel.add(getJButton(), null);
			
		}
		return topLeftPanel;
	}

	/**
	 * This method initializes completenessConfig	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCompletenessConfig() {
		if (completenessConfig == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(113, 215, 97, 25));
			jLabel1.setText(resourcesManager.getString("CustomizableLog.ui.threshold"));
			completeConfig = new JLabel();
			completeConfig.setText(resourcesManager.getString("CustomizableLog.ui.Completeness"));
			completeConfig.setBounds(new Rectangle(118, 20, 200, 27));
			completeConfig.setFont(new Font("Dialog", Font.BOLD, 14));
			completenessConfig = new JPanel();
			completenessConfig.setLayout(null);
			completenessConfig.add(getJRadioButton(), null);
			completenessConfig.add(completeConfig, null);
			completenessConfig.add(getJRadioButton1(), null);
			completenessConfig.add(getDS(), null);
			completenessConfig.add(getDS1(), null);
			completenessConfig.add(getJRadioButton2(), null);
			completenessConfig.add(getES(), null);
			completenessConfig.add(getTS(), null);
			group = new ButtonGroup();
			group.add(getJRadioButton());
			group.add(getJRadioButton1());
			group.add(getJRadioButton2());
			completenessConfig.add(getComplete1(), null);
			completenessConfig.add(getComplete2(), null);
			completenessConfig.add(getSlider1(), null);
			completenessConfig.add(getSlider2(), null);
			completenessConfig.add(getEsValue(), null);
			completenessConfig.add(getSubmit(), null);
			completenessConfig.add(jLabel1, null);
			completenessConfig.add(getSlider3(), null);
		}
		return completenessConfig;
	}

	/**
	 * This method initializes noiseConfig	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNoiseConfig() {
		if (noiseConfig == null) {
			noiseTypeConfig = new JLabel();
			noiseTypeConfig.setText(resourcesManager.getString("CustomizableLog.ui.ntc"));
			noiseTypeConfig.setFont(new Font("Dialog", Font.BOLD, 14));
			noiseConfig = new JPanel();
			noiseConfig.setLayout(null);
			noiseConfig.add(noiseTypeConfig, null);
			noiseTypeConfig.setBounds(118, 20, 134, 30);
			noiseConfig.add(getNoNoise(), null);
			noiseConfig.add(getHaveNoise(), null);
			noiseConfig.add(getNoHead(), null);
			noiseConfig.add(getNoTail(), null);
			noiseConfig.add(getPartBody(), null);
			noiseConfig.add(getRemoveEvent(), null);
			noiseConfig.add(getInterChange(), null);
			noiseConfig.add(getNoisePercent(), null);
			noiseConfig.add(getPercentControl(), null);
		}
		return noiseConfig;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setLocation(new Point(10, 50));
			jScrollPane.setSize(new Dimension(315, 276));
			String[] columnNames = { 
					resourcesManager.getString("CustomizableLog.ui.table.select"), 
					resourcesManager.getString("CustomizableLog.ui.table.foldername"), 
					resourcesManager.getString("CustomizableLog.ui.table.fileNum")
					};
			Object nullData[][] = new Object[0][3];
			TableModel.setColumnNames(columnNames);
			TableModel.setDatas(nullData);
			Table = new JTable(TableModel);
			Table.getColumnModel().getColumn(0).setPreferredWidth(60);
			Table.getColumnModel().getColumn(1).setPreferredWidth(90);
			Table.getColumnModel().getColumn(2).setPreferredWidth(90);
			Table.getTableHeader().setReorderingAllowed(false);
			Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//jTable.getTableHeader().setResizingAllowed(false);
			jScrollPane.setViewportView(Table);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (chooseModel == null) {
			chooseModel = new JButton();
			chooseModel.setText(resourcesManager.getString("CustomizableLog.ui.chooseModel"));
			chooseModel.setBounds(new Rectangle(174, 8, 124, 30));
			chooseModel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser("c://");
					fileChooser.setDialogTitle(resourcesManager.getString("CustomizableLog.ui.omf"));
					fileChooser
							.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
					fileChooser.rescanCurrentDirectory();
					int choose = fileChooser.showOpenDialog(null);
					if (choose == JFileChooser.APPROVE_OPTION) {
						// modelSetPath.setText(fileChooser.getSelectedFile()
						// .getAbsolutePath().trim());
						modelpath = fileChooser.getSelectedFile()
								.getAbsolutePath().trim();
					} else
						return;

					filenummap = new HashMap<String, Integer>();
					File modelsFile = new File(modelpath);
					File[] files = modelsFile.listFiles();
					for (int i = 0; i < files.length; i++) {
						if(files[i].isDirectory()){
							File[] temp = files[i].listFiles();
							int num = temp.length;
							filenummap.put(files[i].getName(), num);
						}
					}
					if(filenummap.size()!=0){
						data = new Object[filenummap.size()][3];
						Set<String> en = filenummap.keySet();
						Iterator<String> it = en.iterator();
						int i = 0;
						while (it.hasNext()) {
							String filefoldername = it.next();
							int filenumber = filenummap.get(filefoldername);
							data[i][0] = new Boolean(false);
							data[i][1] = filefoldername;
							data[i][2] = new Integer(filenumber);
							i++;
						}
						TableModel.setDatas(data);
						TableModel.fireTableDataChanged();
					}
				}

			});
		}
		return chooseModel;
	}

	/**
	 * This method initializes noNoise	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNoNoise() {
		if (noNoise == null) {
			noNoise = new JRadioButton();
			noNoise.setText(resourcesManager.getString("CustomizableLog.ui.dncn"));
			noNoise.setFont(new Font("Dialog", Font.BOLD, 12));
			noNoise.setBounds(new Rectangle(45, 65, 200, 21));
			noNoise.setSelected(true);
			noNoise.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					noNoise.setSelected(true);
					 if(haveNoise.isSelected()){
						 haveNoise.setSelected(false);
						 noHead.setSelected(false);
						 noTail.setSelected(false);
						 partBody.setSelected(false);
						 removeEvent.setSelected(false);
						 interChange.setSelected(false);
						 
					 }
					 noiseFlag = 1;
				}

			});
				
			};
		return noNoise;
	}

	/**
	 * This method initializes haveNoise	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getHaveNoise() {
		if (haveNoise == null) {
			haveNoise = new JRadioButton();
			haveNoise.setFont(new Font("Dialog", Font.BOLD, 12));
			haveNoise.setText(resourcesManager.getString("CustomizableLog.ui.cn"));
			haveNoise.setBounds(new Rectangle(45, 100, 107, 21));
			haveNoise.setSelected(false);
			haveNoise.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					haveNoise.setSelected(true);
					 if(noNoise.isSelected()){
						 noNoise.setSelected(false);
					 }
					 noiseFlag = 2;
				}

			});
		}
		return haveNoise;
	}

	/**
	 * This method initializes noHead	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getNoHead() {
		if (noHead == null) {
			noHead = new JCheckBox();
			noHead.setText(resourcesManager.getString("CustomizableLog.ui.nohead"));
			noHead.setBounds(new Rectangle(75, 130, 80, 21));
		}
		return noHead;
	}

	/**
	 * This method initializes noTail	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getNoTail() {
		if (noTail == null) {
			noTail = new JCheckBox();
			noTail.setText(resourcesManager.getString("CustomizableLog.ui.notail"));
			noTail.setBounds(new Rectangle(75, 160, 70, 21));
			noTail.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return noTail;
	}

	/**
	 * This method initializes partBody	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getPartBody() {
		if (partBody == null) {
			partBody = new JCheckBox();
			partBody.setText(resourcesManager.getString("CustomizableLog.ui.incompletebody"));
			partBody.setBounds(new Rectangle(75, 190, 131, 21));
		}
		return partBody;
	}

	/**
	 * This method initializes removeEvent	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getRemoveEvent() {
		if (removeEvent == null) {
			removeEvent = new JCheckBox();
			removeEvent.setText(resourcesManager.getString("CustomizableLog.ui.loe"));
			removeEvent.setBounds(new Rectangle(75, 220, 130, 21));
		}
		return removeEvent;
	}

	/**
	 * This method initializes interChange	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getInterChange() {
		if (interChange == null) {
			interChange = new JCheckBox();
			interChange.setText(resourcesManager.getString("CustomizableLog.ui.pte"));
			interChange.setBounds(new Rectangle(75, 250, 152, 21));
		}
		return interChange;
	}

	/**
	 * This method initializes noisePercent	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNoisePercent() {
		if (noisePercent == null) {
			noisePercent = new JTextField();
			noisePercent.setBounds(new Rectangle(151, 100, 50, 22));
			noisePercent.setText("5%");
		}
		return noisePercent;
	}

	/**
	 * This method initializes percentControl	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getPercentControl() {
		if (percentControl == null) {
			percentControl = new JSlider(0,10,0);
			percentControl.setBounds(new Rectangle(201, 83, 228, 66));
			percentControl.setPaintTicks(true); 
			percentControl.setMajorTickSpacing(1); 
			percentControl.setMinorTickSpacing(1); 
			//setPaintLabels()方法为设置是否数字标记，若设为true，则JSlider刻度上就会有数值出现。 
			percentControl.setPaintLabels(true); 
	        //setPaintTrack()方法表示是否出现滑动杆的横杆。默认值为true. 
			percentControl.setPaintTrack(true); 
	        //setSnapToTicks()方法表示一次移动一个小刻度，而不再是一次移动一个单位刻度。 
			percentControl.setSnapToTicks(true); 
			percentControl.setValue(5);
			percentControl.addChangeListener(new ChangeListener() {				
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					Integer hValue=((JSlider)e.getSource()).getValue(); 
					noisePercent.setText(hValue.toString()+"%");
				}
			});			
		}
		return percentControl;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton() {
		if (tarCompleteness == null) {
			tarCompleteness = new JRadioButton();
			tarCompleteness.setText(resourcesManager.getString("CustomizableLog.ui.tc"));
			tarCompleteness.setLocation(new Point(45, 70));
			tarCompleteness.setSize(new Dimension(136, 21));
			tarCompleteness.setSelected(true);
		}
		return tarCompleteness;
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton1() {
		if (causalCompleteness == null) {
			causalCompleteness = new JRadioButton();
			causalCompleteness.setText(resourcesManager.getString("CustomizableLog.ui.cc"));
			causalCompleteness.setLocation(new Point(45, 150));
			causalCompleteness.setMnemonic(KeyEvent.VK_UNDEFINED);
			causalCompleteness.setSize(new Dimension(150, 21));
		}
		return causalCompleteness;
	}

	/**
	 * This method initializes DS	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getDS() {
		if (DS == null) {
			DS = new JCheckBox();
			DS.setText("DS");
			DS.setLocation(new Point(65, 102));
			DS.setSize(new Dimension(77, 21));
			DS.setSelected(true);
			DS.setEnabled(false);
		}
		return DS;
	}

	/**
	 * This method initializes DS1	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getDS1() {
		if (DS1 == null) {
			DS1 = new JCheckBox();
			DS1.setText("DS+");
			DS1.setLocation(new Point(65, 120));
			DS1.setSize(new Dimension(55, 21));
		}
		return DS1;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton2() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setText(resourcesManager.getString("CustomizableLog.ui.fc"));
			jRadioButton.setBounds(new Rectangle(45, 190, 162, 21));
		}
		return jRadioButton;
	}

	/**
	 * This method initializes ES	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getES() {
		if (ES == null) {
			ES = new JCheckBox();
			ES.setText("ES");
			ES.setBounds(new Rectangle(65, 215, 41, 25));
			ES.setSelected(true);
			ES.setEnabled(false);
		}
		return ES;
	}

	/**
	 * This method initializes TS	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getTS() {
		if (TS == null) {
			TS = new JCheckBox();
			TS.setText("TS");
			TS.setBounds(new Rectangle(65, 240, 41, 25));
		}
		return TS;
	}

	/**
	 * This method initializes complete1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getComplete1() {
		if (complete1 == null) {
			complete1 = new JTextField();
			complete1.setBounds(new Rectangle(208, 68, 35, 25));
			complete1.setText("100%");
		}
		return complete1;
	}

	/**
	 * This method initializes complete2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getComplete2() {
		if (complete2 == null) {
			complete2 = new JTextField();
			complete2.setBounds(new Rectangle(208, 146, 35, 25));
			complete2.setText("100%");
		}
		return complete2;
	}

	/**
	 * This method initializes Slider1	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider1() {
		if (Slider1 == null) {
			Slider1 = new JSlider(0,100,0);
			Slider1.setBounds(new Rectangle(246, 64, 200, 52));
			Slider1.setPaintTicks(true); 
			Slider1.setMajorTickSpacing(10); 
			Slider1.setMinorTickSpacing(1); 
			//setPaintLabels()方法为设置是否数字标记，若设为true，则JSlider刻度上就会有数值出现。 
			Slider1.setPaintLabels(true); 
	        //setPaintTrack()方法表示是否出现滑动杆的横杆。默认值为true. 
			Slider1.setPaintTrack(true); 
	        //setSnapToTicks()方法表示一次移动一个小刻度，而不再是一次移动一个单位刻度。 
			Slider1.setSnapToTicks(true); 
			Slider1.setValue(100);
			Slider1.addChangeListener(new ChangeListener() {				
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					Integer hValue=((JSlider)e.getSource()).getValue(); 
					complete1.setText(hValue.toString()+"%");
				}
			});			
		}
		return Slider1;
	}

	/**
	 * This method initializes Slider2	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider2() {
		if (Slider2 == null) {
			Slider2 = new JSlider(0,100,0);
			Slider2.setBounds(new Rectangle(248, 137, 200, 50));
			Slider2.setPaintTicks(true); 
			Slider2.setMajorTickSpacing(10); 
			Slider2.setMinorTickSpacing(1); 
			//setPaintLabels()方法为设置是否数字标记，若设为true，则JSlider刻度上就会有数值出现。 
			Slider2.setPaintLabels(true); 
	        //setPaintTrack()方法表示是否出现滑动杆的横杆。默认值为true. 
			Slider2.setPaintTrack(true); 
	        //setSnapToTicks()方法表示一次移动一个小刻度，而不再是一次移动一个单位刻度。 
			Slider2.setSnapToTicks(true); 
			Slider2.setValue(100);
			Slider2.addChangeListener(new ChangeListener() {				
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					Integer hValue=((JSlider)e.getSource()).getValue(); 
					complete2.setText(hValue.toString()+"%");
				}
			});			
		}
		return Slider2;
	}

	/**
	 * This method initializes esValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getEsValue() {
		if (esValue == null) {
			esValue = new JTextField();
			esValue.setBounds(new Rectangle(208, 215, 35, 25));
			esValue.setText("95%");
		}
		return esValue;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());
			buttonPanel.add(messagePanel,BorderLayout.CENTER);

		}
		return buttonPanel;
	}

	/**
	 * This method initializes submit	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSubmit() {
		if (submit == null) {
			submit = new JButton();
			submit.setText(resourcesManager.getString("CustomizableLog.ui.start"));
			submit.setBounds(new Rectangle(167, 283, 73, 30));
			submit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// haven't choose model folder		
					if (modelpath.equals("")) {
						JOptionPane.showMessageDialog(null,
								resourcesManager.getString("CustomizableLog.ui.message"));
						return;
					}

					Message.add(resourcesManager.getString("CustomizableLog.ui.message2"));
					Vector<File> filesvec=new Vector<File>();//model files
					for(int i=0;i<data.length;i++){
						if((Boolean)data[i][0]){
							String str = (String) data[i][1];
							File modelsFile = new File(modelpath+"/"+str);
							File[] file1=modelsFile.listFiles();
							for(int j=0;j<file1.length;j++){
								filesvec.add(file1[j]);
							}
						}
					}
					
					
					//log directory
					Message.add(resourcesManager.getString("CustomizableLog.ui.message3"));
					String foldername = System.getProperty(
							"user.dir", "")+ "\\customizablelog";
					File logfolder = new File(foldername);					
					if(!logfolder.exists()) {
						logfolder.mkdirs();
					} else if (logfolder.isFile()) {
						logfolder.delete();
						logfolder.mkdirs();
					}
					Message.add(resourcesManager.getString("CustomizableLog.ui.message4"));
					//get the completeness configure parameters
					double comDegree;
					if(tarCompleteness.isSelected()){
						completeType = 1;
						if(DS1.isSelected())
							tarType[1]=1;
						comPara.setParameter(tarType);
						comDegree = ((double)Slider1.getValue())/100;
						}else if(causalCompleteness.isSelected()){
						completeType = 2;
						comDegree = ((double)Slider2.getValue())/100;
					}else{
						completeType = 3;
						if(TS.isSelected())
							freType[1]=1;
						comPara.setParameter(freType);
						comDegree = ((double)Slider3.getValue())/100;
						
					}
					comPara.setCompleteType(completeType);
					comPara.setCompleteDegree(comDegree);
					
					Message.add(resourcesManager.getString("CustomizableLog.ui.message5"));
			
					//get the noise configure parameters
					if(noHead.isSelected())
						noiseType[0]=1;
					if(noTail.isSelected())
						noiseType[1]=1;
					if(partBody.isSelected())
						noiseType[2]=1;
					if(removeEvent.isSelected())
						noiseType[3]=1;
					if(interChange.isSelected())
						noiseType[4]=1;
					noiPara.setNoiseType(noiseFlag);
					noiPara.setParameter(noiseType);
					double noiseDegree = ((double)percentControl.getValue())/100.0;
					noiPara.setNoiseDegree(noiseDegree);
					if(completeType ==3 && noiseFlag==2)
					{
						if((comDegree+1)<((1-noiseDegree)*2))
						{
							JOptionPane.showMessageDialog(null,
									resourcesManager.getString("CustomizableLog.ui.message6"));
							return;
							
						}
					}
					
					FileInputStream in = null;
					PnmlImport input = new PnmlImport();

					CustomeizableLPM lpm = new CustomeizableLPM();
					for (int i = 0; i < filesvec.size(); i++) {
						File model=filesvec.get(i);
						if (model.getAbsolutePath().endsWith(".pnml")
								|| model.getAbsolutePath().endsWith(".xml")) {
							try {
									in = new FileInputStream(model.getAbsolutePath());
									PetriNet pn = input.read(in);
									in.close();
									int index = model.getName().lastIndexOf(".");
									String logPath = logfolder.getPath() +"\\"+ model.getName().substring(0, index)+ ".mxml";
									File logfile = new File(logPath);
									if (logfile.exists())
									{
										logfile.delete();
									}
									logfile.createNewFile();
									Message.add(resourcesManager.getString("CustomizableLog.ui.message7"));	
									LogManager.generateLog(logPath, lpm, pn, comPara, noiPara);
									System.out.println("log generate");
									
								}
							
							catch(Exception e1){
								e1.printStackTrace();
							}
						}
					}
					Message.add("Log Generate finished");
								
					//Customizable Log Generation
					
				}
			});
		}
		return submit;
	}

	/**
	 * This method initializes Slider3	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider3() {
		if (Slider3 == null) {
			Slider3 = new JSlider(0,100, 0);
			Slider3.setBounds(new Rectangle(248, 214, 200, 45));
			Slider3.setMinorTickSpacing(1);
			Slider3.setPaintLabels(true);
			Slider3.setPaintTicks(true);
			Slider3.setPaintTrack(true);
			Slider3.setSnapToTicks(true);
			Slider3.setValue(95);
			Slider3.setMajorTickSpacing(10);
			Slider3.addChangeListener(new ChangeListener() {				
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					Integer hValue=((JSlider)e.getSource()).getValue(); 
					esValue.setText(hValue.toString()+"%");
				}
			});			
		}
		return Slider3;
	}
	



	

}  
