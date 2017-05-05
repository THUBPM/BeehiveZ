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
package cn.edu.thss.iise.beehivez.client.ui.similarityoriginal;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.processmining.exporting.petrinet.PnmlExport;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
import cn.edu.thss.iise.beehivez.util.loggenerator.AverageWeightLPM;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogManager;
import cn.edu.thss.iise.beehivez.util.loggenerator.LogProduceMethod;

public class SimilarityOriginalUI extends JSplitPane {
	
	private JPanel logPanel = null;
	private JPanel MiningPanel= null;
	private JPanel SimilarityPanel= null;
	private JSplitPane bottomComponent;
	
	private JButton logInputButton;
	private JButton logOutputButton;
	private JButton logBegin;	
	
	private JTextField logInputFilePath;
	private JTextField logOutputFilePath;
	
	private JButton miningInputButton;
	private JButton miningOutputButton;
	private JButton miningBegin;	
	
	private JTextField miningInputFilePath;
	private JTextField miningOutputFilePath;
	
	private JButton similarityInputButton;
	private JButton similarityInputButton2;
	private JButton similarityOutputButton;
	private JButton similarityBegin;	
	
	private JTextField similarityInputFilePath;
	private JTextField similarityInputFilePath2;
	private JTextField similarityOutputFilePath;
	
	private JLabel logLabel;
	private JLabel miningLabel;
	private JLabel similarityLabel;
	
	private JLabel logFileNum;
	private JTextField logFileContent;
	
	private JLabel miningFileNum;
	private JTextField miningFileContent;
	
	private JLabel miningAlgorithm;
	
	private JCheckBox[] similarityTypeButton;
	
	private String[] algorithmNameContent = {	"AlphaMiner", "AlphaPlusPlusMiner", 
			"AlphaSharpMinier", "GeneticMiner", 
			"DTGeneticMiner", "HeuristicsMiner", 
			"RegionMiner"};
	
	private JComboBox miningAlgorithmBox;
	
	private ArrayList<String> miningAlgorithmList;
	
	private ArrayList<File> toGenerateLog;
	private ArrayList<File> toMineModel;
	
	private ArrayList<File> toCompareMode1, toCompareMode2;
	
	private JTable similarityTable;
	private DefaultTableModel similarityTableModel;
	private JScrollPane tablePane;
	ResourcesManager resourcesManager = new ResourcesManager();
	private String[] tableTitle = {
			resourcesManager.getString("SimilarityOriginal.table.modelname"),
			resourcesManager.getString("SimilarityOriginal.table.model2name"),
			resourcesManager.getString("SimilarityOriginal.table.strsimilarity"),
			resourcesManager.getString("SimilarityOriginal.table.behsimilarity"),
				};
	private Object[][] tableContent;
		
	private JProgressBar similarityBar;
	private JProgressBar logBar;
	private JProgressBar miningBar;
	
	
	
	
		
	/**
	 * The default constructor
	 */
	public SimilarityOriginalUI()
	{
		super();
		initialize();
	}

	/**
	 * This method initialize the plugin
	 */
	private void initialize() {		
		this.setSize(800, 500);
		this.setTopComponent(getLogPanel());
		this.setBottomComponent(getBottomComponent());
		this.setDividerLocation(140);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		initializeField();
	}
	
		
	private void initializeField()
	{		
		miningAlgorithmList = new ArrayList<String>();
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.AlphaMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.AlphaPlusPlusMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.AlphaSharpMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.GeneticMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.DupTGeneticMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.HeuristicMiner");
		miningAlgorithmList.add("cn.edu.thss.iise.beehivez.server.mining.Region_Miner");
	}
	
	
	/**
	 * This method constructs the log panel
	 * @return log panel
	 */
	private JPanel getLogPanel() {
		if (logPanel == null)
		{			
			logPanel = new JPanel();
			logPanel.setLayout(null);
			logInputButton = new JButton(resourcesManager.getString("SimilarityOriginal.originalm"));
			logInputButton.setBounds(10, 40, 140, 30);
			
			logInputFilePath = new JTextField("C:\\");
			logInputFilePath.setBounds(160,40,220,30);
			
			logOutputButton = new JButton(resourcesManager.getString("SimilarityOriginal.logfolder"));
			logOutputButton.setBounds(390, 40, 140, 30);
			
			logOutputFilePath = new JTextField("C:\\");
			logOutputFilePath.setBounds(540,40,220,30);
			
			logBegin = new JButton(resourcesManager.getString("SimilarityOriginal.generatelog"));
			logBegin.setBounds(390, 80, 140, 30);
			
			logLabel = new JLabel(resourcesManager.getString("SimilarityOriginal.gl"));
			logLabel.setBounds(10, 5, 150, 30);
			logLabel.setFont(new java.awt.Font("Dialog", 1, 15));
			
			logFileNum = new JLabel(resourcesManager.getString("SimilarityOriginal.fn"));
			logFileNum.setBounds(10, 80, 60, 30);
			logFileContent = new JTextField("");
			logFileContent.setBounds(90, 80, 40, 30);
			logFileContent.setEditable(false);
			
			logBar = new JProgressBar(0,100);
			logBar.setBounds(540,90,220,10);
			
			logInputButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser chooser = new JFileChooser(logInputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						logInputFilePath.setText(path);		
						File folder = new File(path);
						File[] fileList = folder.listFiles();
						ArrayList<File> tempExtractFile = new ArrayList<File>();
						for (File file	:	fileList)
						{
							if (file.getAbsolutePath().endsWith(".pnml"))
							{
								tempExtractFile.add(file);
							}
						}						
						toGenerateLog = tempExtractFile;
						logFileContent.setText(String.valueOf(toGenerateLog.size()));
					}					
				}				
			});
			
			logOutputButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser(logOutputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						logOutputFilePath.setText(path);						
					}
					
				}
				
			});
			
			logBegin.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					new logGenerateThread().start();
				}

				
			});
			
			
			logPanel.add(logInputButton);
			logPanel.add(logOutputButton);
			logPanel.add(logInputFilePath);
			logPanel.add(logOutputFilePath);
			logPanel.add(logBegin);
			logPanel.add(logLabel);
			logPanel.add(logFileNum);
			logPanel.add(logFileContent);			
			logPanel.add(logBar);		
		}
		return logPanel;		
	}
	
	
	/**
	 * This methods get the mining panel
	 * @return  mining panel
	 */
	private JPanel getMiningPanel() {
		if (MiningPanel == null)
		{
			MiningPanel = new JPanel();
			MiningPanel = new JPanel();
			MiningPanel.setLayout(null);
			miningInputButton = new JButton(resourcesManager.getString("SimilarityOriginal.logfolder"));
			miningInputButton.setBounds(10, 40, 140, 30);
			
			miningInputFilePath = new JTextField("C:\\");
			miningInputFilePath.setBounds(160,40,220,30);
			
			miningOutputButton = new JButton(resourcesManager.getString("SimilarityOriginal.outputfolder"));
			miningOutputButton.setBounds(390, 40, 140, 30);
			
			miningOutputFilePath = new JTextField("C:\\");
			miningOutputFilePath.setBounds(540,40,220,30);
			
			miningBegin = new JButton(resourcesManager.getString("SimilarityOriginal.processmining"));
			miningBegin.setBounds(390, 80, 140, 30);
			
			miningLabel = new JLabel(resourcesManager.getString("SimilarityOriginal.processmininglabel"));
			miningLabel.setBounds(10, 5, 150, 30);
			miningLabel.setFont(new java.awt.Font("Dialog", 1, 15));
			
			miningFileNum = new JLabel(resourcesManager.getString("SimilarityOriginal.filenum"));
			miningFileNum.setBounds(10, 80, 60, 30);
			miningFileContent = new JTextField("");
			miningFileContent.setBounds(110, 80, 40, 30);
			miningFileContent.setEditable(false);
			
			miningAlgorithm = new JLabel(resourcesManager.getString("SimilarityOriginal.miningalgorithm"));
			miningAlgorithm.setBounds(160,80,100,30);
			
			miningAlgorithmBox = new JComboBox(algorithmNameContent);
			miningAlgorithmBox.setBounds(270,80,110,30);
			
			miningBar = new JProgressBar();
			miningBar.setBounds(540,90,220,10);
			
			miningInputButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser(miningInputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						miningInputFilePath.setText(path);		
						File folder = new File(path);
						File[] fileList = folder.listFiles();
						ArrayList<File> tempExtractFile = new ArrayList<File>();
						for (File file	:	fileList)
						{
							if (file.getAbsolutePath().endsWith(".mxml"))
							{
								tempExtractFile.add(file);
							}
						}						
						toMineModel = tempExtractFile;
						miningFileContent.setText(String.valueOf(toMineModel.size()));
						
					}
				}
				
			});
			
			miningOutputButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser(miningOutputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						miningOutputFilePath.setText(path);						
					}
				}
				
			});
			
			miningBegin.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) {

					new MiningThread().start();
				}
				
			});
			
			MiningPanel.add(miningInputButton);
			MiningPanel.add(miningOutputButton);
			MiningPanel.add(miningInputFilePath);
			MiningPanel.add(miningOutputFilePath);
			MiningPanel.add(miningBegin);
			MiningPanel.add(miningLabel);
			MiningPanel.add(miningFileNum);
			MiningPanel.add(miningFileContent);
			MiningPanel.add(miningAlgorithm);
			MiningPanel.add(miningAlgorithmBox);
			MiningPanel.add(miningBar);
		}
		return MiningPanel;		
	}
	/**
	 * This methods constructs the similarity Panel
	 * @return similarity panel
	 */
	private JPanel getSimilarityPanel() {
		if (SimilarityPanel == null)
		{
			SimilarityPanel = new JPanel();
			SimilarityPanel = new JPanel();
			SimilarityPanel = new JPanel();
			SimilarityPanel.setLayout(null);
			similarityInputButton = new JButton(resourcesManager.getString("SimilarityOriginal.originalModel"));
			similarityInputButton.setBounds(10, 40, 140, 30);
			
			similarityInputFilePath = new JTextField("C:\\");
			similarityInputFilePath.setBounds(160,40,220,30);
			
			similarityOutputButton = new JButton(resourcesManager.getString("SimilarityOriginal.outputfolder"));
			similarityOutputButton.setBounds(10, 120, 140, 30);
			
			similarityOutputFilePath = new JTextField("C:\\");
			similarityOutputFilePath.setBounds(160,120,220,30);
			
			similarityInputButton2 = new JButton(resourcesManager.getString("SimilarityOriginal.minedModel"));
			similarityInputButton2.setBounds(10, 80, 140, 30);
			
			similarityInputFilePath2 = new JTextField("C:\\");
			similarityInputFilePath2.setBounds(160, 80, 220, 30);
			
			similarityBegin = new JButton(resourcesManager.getString("SimilarityOriginal.similaritycalculation"));
			similarityBegin.setBounds(160, 160, 160, 30);
			//similarityBegin.setFont(new Font(null, 1, 10));
			
			similarityLabel = new JLabel(resourcesManager.getString("SimilarityOriginal.similaritycalculation2"));
			similarityLabel.setBounds(10, 5, 150, 30);
			similarityLabel.setFont(new java.awt.Font("Dialog", 1, 15));			
			
//			similarityType = new JLabel("Similarity Type");
//			similarityType.setBounds(160,80,100,30);
			
			
			similarityTypeButton = new JCheckBox[2];
			similarityTypeButton[0] = new JCheckBox(resourcesManager.getString("SimilarityOriginal.strsimilarity"));
			similarityTypeButton[0].setBounds(390,5,100,30);
			similarityTypeButton[1] = new JCheckBox(resourcesManager.getString("SimilarityOriginal.behsimilarity"));
			similarityTypeButton[1].setBounds(500,5,100,30);
			for (int i=0; i<2; i++)
				similarityTypeButton[i].setSelected(true);
			
			tableContent = new Object[10][];
			for (int i=0; i<10; i++)
			{
				tableContent[i] = new Object[4];
				for (int j=0; j<4; j++)
					tableContent[i][j] = "";
			}
			similarityTableModel = new DefaultTableModel(tableContent,tableTitle);
			similarityTable = new JTable(similarityTableModel);			
			tablePane = new JScrollPane(similarityTable);
			tablePane.setBounds(390, 40, 380, 150);
			
			
			similarityInputButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					
					JFileChooser chooser = new JFileChooser(similarityInputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().getPath();
						similarityInputFilePath.setText(path);
						File folder = new File(path);
						File[] fileList = folder.listFiles();
						toCompareMode1 = new ArrayList<File>();
						for (File file	:	fileList)
						{
							if (file.getAbsolutePath().endsWith(".pnml"))
							{
								toCompareMode1.add(file);
							}
						}

						if (similarityTable.getRowCount()<fileList.length)
						{							
							//TableModel tableModel = (TableModel) (similarityTable.getModel());							
							//((DefaultTableModel) tableModel).addRow(new Object[]{"","","",""});
							while (similarityTable.getRowCount()<fileList.length)
								similarityTableModel.addRow(new Object[]{"","","",""});
						}
						for (int i=0; i<similarityTable.getRowCount(); i++)
							similarityTable.setValueAt("", i, 0);
						for (int i=0; i<fileList.length; i++)
							similarityTable.setValueAt(fileList[i].getName(), i, 0);

					}
					
				}				
			});
			
			similarityInputButton2.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					JFileChooser chooser = new JFileChooser(similarityInputFilePath2.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().getPath();
						similarityInputFilePath2.setText(path);
						File folder = new File(path);
						File[] fileList = folder.listFiles();
						toCompareMode2 = new ArrayList<File>();
						for (File file	:	fileList)
						{
							if (file.getAbsolutePath().endsWith(".pnml"))
							{
								toCompareMode2.add(file);
							}
						}
						if (similarityTable.getRowCount()<fileList.length)
						{
							DefaultTableModel tableModel = (DefaultTableModel) similarityTable.getModel();
							tableModel.addRow(new Object[]{"","","",""});
						}
						for (int i=0; i<similarityTable.getRowCount(); i++)
							similarityTable.setValueAt("", i, 1);
						for (int i=0; i<fileList.length; i++)
							similarityTable.setValueAt(fileList[i].getName(), i, 1);
					}
				}
				
			});
			
			similarityOutputButton.addActionListener(new ActionListener()
			{				
				public void actionPerformed(ActionEvent e) 
				{
					JFileChooser chooser = new JFileChooser(similarityOutputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().getPath();
						similarityOutputFilePath.setText(path);										
					}	
					
				}				
			});
			
			similarityBegin.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					try 
					{
						if (toCompareMode1.size() != toCompareMode2.size())
						{
							JOptionPane.showMessageDialog(null, resourcesManager.getString("SimilarityOriginal.message"));
							return;
						}
						getSimilarity(toCompareMode1,toCompareMode2,similarityOutputFilePath.getText());						
					} 
					catch (Exception e1) 
					{						
						e1.printStackTrace();
					} 
				}
				
			});
			
			
			SimilarityPanel.add(similarityInputButton);
			SimilarityPanel.add(similarityInputButton2);
			SimilarityPanel.add(similarityOutputButton);
			SimilarityPanel.add(similarityInputFilePath);
			SimilarityPanel.add(similarityInputFilePath2);
			SimilarityPanel.add(similarityOutputFilePath);
			SimilarityPanel.add(similarityBegin);
			SimilarityPanel.add(similarityLabel);
			SimilarityPanel.add(similarityTypeButton[0]);
			SimilarityPanel.add(similarityTypeButton[1]);
			SimilarityPanel.add(tablePane);			
		}
		return SimilarityPanel;		
	}
		
	/**
	 * This methods calculates the two modellists's similarity
	 * @param modelList1 the first input model list
	 * @param modelList2 the second input model list
	 * @param outputPath
	 * @throws IOException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private void getSimilarity(ArrayList<File> modelList1, ArrayList<File> modelList2, String outputPath) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		System.out.println("Laile");
		System.out.println(modelList1.size());
		System.out.println(modelList2.size());
		ArrayList<Double> str;
		ArrayList<Double> beh;
		if (modelList1.size() != modelList2.size())
			return;
		String output = outputPath +"\\" + "similarity.csv";
		FileWriter fw = new FileWriter(output);
		BufferedWriter bw = new BufferedWriter(fw);
		writeTitle(bw);
		str = new ArrayList<Double>();
		beh = new ArrayList<Double>();
		for (int i=0; i<modelList1.size(); i++)
		{
			System.out.println("Cal..."+i);
			String modelName1 = modelList1.get(i).getName();
			String modelName2 = modelList2.get(i).getName();
			ArrayList<Double> similarity;
			similarity = getSimilaritySingleFile(modelList1.get(i), modelList2.get(i));
			if (similarityTypeButton[0].isSelected())
				str.add(similarity.get(0));
			if (!similarityTypeButton[0].isSelected() && similarityTypeButton[1].isSelected())
				beh.add(similarity.get(0));
			if (similarityTypeButton[0].isSelected() && similarityTypeButton[1].isSelected())
				beh.add(similarity.get(1));
			writeSingleSimilarity(modelName1,modelName2,similarity,bw);
		}
		
		if (str.size() != 0)
		{
			if (similarityTable.getRowCount()<str.size())
			{
				DefaultTableModel tableModel = (DefaultTableModel) similarityTable.getModel();
				tableModel.addRow(new Object[]{"","","",""});
			}
			for (int i=0; i<similarityTable.getRowCount(); i++)
				similarityTable.setValueAt("", i, 2);
			for (int i=0; i<str.size(); i++)
				similarityTable.setValueAt(str.get(i).toString(), i, 2);
		}
		
		if (beh.size() != 0)
		{
			if (similarityTable.getRowCount()<beh.size())
			{
				DefaultTableModel tableModel = (DefaultTableModel) similarityTable.getModel();
				tableModel.addRow(new Object[]{"","","",""});
			}
			for (int i=0; i<similarityTable.getRowCount(); i++)
				similarityTable.setValueAt("", i, 3);
			for (int i=0; i<beh.size(); i++)
				similarityTable.setValueAt(beh.get(i), i, 3);
		}
		
		
		bw.close();		
	}
	
	private void writeSingleSimilarity(String modelName1,String modelName2,ArrayList<Double> similarity, BufferedWriter bw) throws IOException
	{
		bw.write(modelName1+","+modelName2+",");
		for (Double d	:	similarity)
			bw.write(String.valueOf(d)+",");
		bw.newLine();
		
	}
	
	private void writeTitle(BufferedWriter bw) throws IOException
	{
		bw.write("Model1 Name,Model2 Name");
		if (similarityTypeButton[0].isSelected() == true)
			bw.write("StrSimilarity,");
		if (similarityTypeButton[1].isSelected() == true)
			bw.write("BehSimilarity,");
		bw.newLine();
	}
	
	private ArrayList<Double> getSimilaritySingleFile(File model1, File model2) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		ArrayList<Double> result;
		PetriNet pn1 = PetriNetUtil.getPetriNetFromPnmlFile(model1);
		PetriNet pn2 = PetriNetUtil.getPetriNetFromPnmlFile(model2);
		result = new ArrayList<Double>();
		
		if (similarityTypeButton[0].isSelected() == true)
		{
			String similarityStrAlgorithmName = "cn.edu.thss.iise.beehivez.server.metric.ContextBasedSimilarity";
			Class similarityStrAlgorithmClass = Class
					.forName(similarityStrAlgorithmName);
			Object similarityStrAlgorithmObject = similarityStrAlgorithmClass
					.newInstance();
			Class stype1[] = new Class[2];
			stype1[0] = Class
					.forName("org.processmining.framework.models.petrinet.PetriNet");
			stype1[1] = Class
					.forName("org.processmining.framework.models.petrinet.PetriNet");
			Method similarityStrMethod = similarityStrAlgorithmClass.getMethod("similarity", stype1);
			Object s_args1[] = new Object[2];
			s_args1[0] = pn1;
			s_args1[1] = pn2;
			Object r = similarityStrMethod.invoke(
					similarityStrAlgorithmObject, s_args1);		//计算二者相似度，结果为两个object
			double rr = Double.parseDouble(r.toString());
			result.add(rr);
			
		}
		
		if (similarityTypeButton[1].isSelected() == true)
		{
			String similarityAlgorithmName = "cn.edu.thss.iise.beehivez.server.metric.JaccardTARSimilarity";
			Class similarityAlgorithmClass = Class.forName(similarityAlgorithmName);
			Object similarityAlgorithmObject = similarityAlgorithmClass.newInstance();
			Class stype[] = new Class[2];
			stype[0] = Class
					.forName("org.processmining.framework.models.petrinet.PetriNet");
			stype[1] = Class
					.forName("org.processmining.framework.models.petrinet.PetriNet");
			Method similarityMethod = similarityAlgorithmClass.getMethod("similarity", stype);
			Object s_args[] = new Object[2];
			s_args[0] = pn1;
			s_args[1] = pn2;
			Object r = similarityMethod.invoke(similarityAlgorithmObject, s_args);
			double rr = Double.parseDouble(r.toString());
			result.add(rr);
		}
		return result;		
	}
	
	public JSplitPane getBottomComponent() {
		if (bottomComponent == null)
		{
			bottomComponent = new JSplitPane();
			bottomComponent.setDividerLocation(140);
			bottomComponent.setOrientation(JSplitPane.VERTICAL_SPLIT);		
			bottomComponent.setTopComponent(getMiningPanel());
			bottomComponent.setBottomComponent(getSimilarityPanel());
			
		}
		return bottomComponent;
	}	
	
	public class logGenerateThread extends Thread
	{
		public void run()
		{
			LogProduceMethod lpm;
			
		    lpm = new AverageWeightLPM();				    
		    double completeness = 1.0;				    
			int multiple =1;								
			FileInputStream in;
			PnmlImport input = new PnmlImport();
			logBar.setMinimum(0);
			logBar.setMaximum(toGenerateLog.size());
			logBar.setValue(0);						
			for (int i = 0; i < toGenerateLog.size(); i++) {
				File model=toGenerateLog.get(i);
				logBar.setValue(i+1);
				System.out.println(logBar.getValue());				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (model.getAbsolutePath().endsWith(".pnml")
						|| model.getAbsolutePath().endsWith(".xml")) {
					try 
					{
						in = new FileInputStream(model
								.getAbsolutePath());
						PetriNet pn = input.read(in);
						in.close();
						int index = model.getName().lastIndexOf(".");
						String logPath = logOutputFilePath.getText() +"\\"+ model.getName().substring(0, index)+ ".mxml";
						
						String temp=logPath.replaceAll("log", "miningmodel");
						String minepath=temp.replaceAll("mxml", "pnml");
						File logfile = new File(logPath);								
						logfile.createNewFile();
						LogManager.generateLog(logPath, 100, lpm, pn, completeness,multiple);
						System.out.println("log generate");								
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}						
			}										
		}		
	}
	
	public class MiningThread extends Thread 
	{
		
		public void run() 
		{														
			LogReader logReader = null;
			String miningAlgorithmName;			//挖掘算法
			Class miningAlgorithmClass;
			
			String miningmodelfolder = miningOutputFilePath.getText();
			
			File modelFolder = new File(miningmodelfolder);
			if (!modelFolder.exists())
				modelFolder.mkdir();
			
			
			int checkedMiningAlgorithm = miningAlgorithmBox.getSelectedIndex();
			ArrayList<String> modelName = new ArrayList<String>();
			try {
				//挖掘的过程
											
					
				miningAlgorithmName = miningAlgorithmList.get(checkedMiningAlgorithm);
				miningAlgorithmClass = Class.forName(miningAlgorithmName);
			
				miningBar.setMinimum(0);
				miningBar.setMaximum(toMineModel.size());
				int head=0;
				for (File log	:	toMineModel)
				{
					miningBar.setValue(head+1);
					head ++;
					LogFile logFile = LogFile.getInstance(log.getAbsolutePath());
					logReader = LogReaderFactory.createInstance(null, logFile);
	
					Object miningAlgorithmObject = miningAlgorithmClass.newInstance();
					Class ptype[] = new Class[1];
					ptype[0] = Class.forName("org.processmining.framework.log.LogReader");
	                    		
					Method miningMethod = miningAlgorithmClass.getMethod("mine", ptype);
					Object m_args[] = new Object[1];
					m_args[0] = logReader;
							
					PetriNet miningModel = (PetriNet) miningMethod.invoke(miningAlgorithmObject, m_args);						
	
					String outputFile = miningOutputFilePath.getText();					
					outputFile = outputFile+"\\"+log.getName();
					outputFile = outputFile.replaceFirst("mxml", "pnml");						
											
					if (miningModel != null) 
					{							
						PnmlExport exportPlugin = new PnmlExport();
						Object[] objects = new Object[] { miningModel };
						ProvidedObject object = new ProvidedObject("temp",	objects);
						File file = new File(outputFile);
						if (file.exists()) 
						{
							file.delete();
						}
						file.createNewFile();
						FileOutputStream outputStream = new FileOutputStream(outputFile);
						exportPlugin.export(object, outputStream);
						outputStream.close();
						
					}
					else 
					{
						System.err.println("No Petri net could be constructed.");
					}	                    						
				} //每一次的文件
															
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}						
		}
	}
	

}
