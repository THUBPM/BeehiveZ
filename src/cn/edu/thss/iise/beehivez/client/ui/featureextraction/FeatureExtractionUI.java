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
package cn.edu.thss.iise.beehivez.client.ui.featureextraction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.metric.PetriNetMetrics;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

public class FeatureExtractionUI extends JSplitPane{

	ResourcesManager resourcesManager = new ResourcesManager();

	private static final long serialVersionUID = 4798913086384672284L;
	private JPanel topPanel = null;
	private JPanel bottomPanel= null;
	
	private JCheckBox featureButton[];
	
	private String[] featureName={"" +
			resourcesManager.getString("FeatureExtraction.ui.tran"), 
			resourcesManager.getString("FeatureExtraction.ui.pla"), 
			resourcesManager.getString("FeatureExtraction.ui.arcs"), 
			resourcesManager.getString("FeatureExtraction.ui.ed"), 
			resourcesManager.getString("FeatureExtraction.ui.maxinDegree"), 
			resourcesManager.getString("FeatureExtraction.ui.maxoutDegree"),
			resourcesManager.getString("FeatureExtraction.ui.tars"),
			resourcesManager.getString("FeatureExtraction.ui.number"),
			resourcesManager.getString("FeatureExtraction.ui.mind"), 
			resourcesManager.getString("FeatureExtraction.ui.maxd"), 
			resourcesManager.getString("FeatureExtraction.ui.ave"), 
			resourcesManager.getString("FeatureExtraction.ui.std"), 		
			resourcesManager.getString("FeatureExtraction.ui.number"),
			resourcesManager.getString("FeatureExtraction.ui.mind"), 
			resourcesManager.getString("FeatureExtraction.ui.maxd"), 
			resourcesManager.getString("FeatureExtraction.ui.ave"), 
			resourcesManager.getString("FeatureExtraction.ui.std"), 			
			resourcesManager.getString("FeatureExtraction.ui.number"),
			resourcesManager.getString("FeatureExtraction.ui.mind"), 
			resourcesManager.getString("FeatureExtraction.ui.maxd"), 
			resourcesManager.getString("FeatureExtraction.ui.ave"), 
			resourcesManager.getString("FeatureExtraction.ui.std"), 			
			resourcesManager.getString("FeatureExtraction.ui.number"),
			resourcesManager.getString("FeatureExtraction.ui.mind"), 
			resourcesManager.getString("FeatureExtraction.ui.maxd"), 
			resourcesManager.getString("FeatureExtraction.ui.ave"), 
			resourcesManager.getString("FeatureExtraction.ui.std"), 			
			resourcesManager.getString("FeatureExtraction.ui.state"),
			"AND-XOR Mis",
			"Sequentiality",
			"TS",
			"CH",
			"CFC",
			"CYC",
			resourcesManager.getString("FeatureExtraction.ui.diam"),
			"Separability",
			"Structuredness",
			"CNC",
			resourcesManager.getString("FeatureExtraction.ui.maxde"),
			resourcesManager.getString("FeatureExtraction.ui.avede"),
			"Depth",
			resourcesManager.getString("FeatureExtraction.ui.iv"),
			resourcesManager.getString("FeatureExtraction.ui.dt"),
			resourcesManager.getString("FeatureExtraction.ui.nfc"),
			resourcesManager.getString("FeatureExtraction.ui.ac"),
			resourcesManager.getString("FeatureExtraction.ui.oj"),
			resourcesManager.getString("FeatureExtraction.ui.sl"),
			resourcesManager.getString("FeatureExtraction.ui.nl")
			};
	private JPanel[] checkGroups;
	private String[] groupNames={
				resourcesManager.getString("FeatureExtraction.ui.top"),
				resourcesManager.getString("FeatureExtraction.ui.strbeh"),
				"and-split",
				"and-join",
				"xor-split",
				"xor-join",
				resourcesManager.getString("FeatureExtraction.ui.connector"),
				resourcesManager.getString("FeatureExtraction.ui.substructure")
				};
	
	private JButton inputButton;	
	private JButton outputButton;
	private JTextField inputFilePath;
	private JTextField outputFilePath;
	
	private JLabel totalFileNum;
	private JTextField totalFileNumContent;
	
	private JButton Extract;
	
	private File[] toExtractFile;
	
	private JTextArea statusArea;
	private JLabel status;
	private JScrollPane statusPane;		
	
	
	/**
	 * This is the default constructor
	 */
	public FeatureExtractionUI()
	{
		super();
		initialize();		
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 500);
		this.setDividerLocation(100);
		this.setTopComponent(getToppanel());
		this.setBottomComponent(getBottompanel());		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);		
	}
	
	
	/**
	 * This method get the top part of the panel
	 * @return the top part of the panel
	 */
	private JPanel getToppanel() {
		if (topPanel == null)
		{
			topPanel = new JPanel();
			topPanel.setLayout(null);
			
			//InputButton, OutputButton
			inputButton = new JButton(resourcesManager.getString("FeatureExtraction.ui.inputfolder"));
			inputButton.setBounds(10, 10, 120, 30);
			outputButton = new JButton(resourcesManager.getString("FeatureExtraction.ui.outputfolder"));
			outputButton.setBounds(10, 50, 120, 30);
			
			inputFilePath = new JTextField("C:\\");
			inputFilePath.setBounds(140, 10, 200, 30);		
			outputFilePath = new JTextField("C:\\feature.csv");
			outputFilePath.setBounds(140, 50, 200, 30);
			
			totalFileNum = new JLabel(resourcesManager.getString("FeatureExtraction.ui.tfn"));
			totalFileNum.setBounds(350, 10, 80, 30);
			totalFileNumContent = new JTextField();
			totalFileNumContent.setBounds(440,10,50,30);
			totalFileNumContent.setEditable(false);
			
			Extract = new JButton(resourcesManager.getString("FeatureExtraction.ui.extraction"));
			Extract.setBounds(350, 50, 140, 30);
			
			status = new JLabel(resourcesManager.getString("FeatureExtraction.ui.status"));
			status.setBounds(500, 5, 50, 20);
			
			statusArea= new JTextArea();
			statusPane = new JScrollPane(statusArea);
			//statusPane.add(statusArea);
			statusPane.setBounds(500,25,270,60);
			
			
			inputButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					JFileChooser chooser = new JFileChooser(inputFilePath.getText());
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						inputFilePath.setText(path);
						statusArea.append("Input FilePath:\t"+path+"\n");
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
						toExtractFile = tempExtractFile.toArray(new File[0]);
						totalFileNumContent.setText(String.valueOf(toExtractFile.length));
					}
				}
				
			});
			
			outputButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					JFileChooser chooser = new JFileChooser(outputFilePath.getText());
					//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	
					chooser.setSelectedFile(new File("C:\\Feature.csv"));
					if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile()
								.getPath();
						File f = new File(path);
						if (f.isDirectory())
							path = path +"\\feature.csv";
						outputFilePath.setText(path);
						
						
						statusArea.append("Output FilePath:\t"+path+"\n");
					}
				}				
			});
			
			Extract.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						extractAndSaveFeature();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
			});
												
			topPanel.add(inputButton);
			topPanel.add(outputButton);
			topPanel.add(inputFilePath);
			topPanel.add(outputFilePath);
			topPanel.add(totalFileNum);
			topPanel.add(totalFileNumContent);
			topPanel.add(Extract);
			topPanel.add(status);
			topPanel.add(statusPane);
						
		}
		return topPanel;
	}	
	
	/**
	 * To extract the feature and save them
	 * @throws IOException
	 */
	protected void extractAndSaveFeature() throws IOException 
	{
		String outputPath;
		outputPath = outputFilePath.getText();//+"\\Feature.csv";
		statusArea.append("Begin Extracting\n");
		File f = new File(outputPath);
		if (f.exists() == false)
			f.createNewFile();
		FileWriter fw = new FileWriter(outputPath);
		BufferedWriter bw = new BufferedWriter(fw);
		writeTitle(bw);				
		for (File file	:	toExtractFile)
		{
			statusArea.append("Extracting : "+file.getName()+"..\n");
			String fileName;
			fileName = file.getName();
			ArrayList<Double> statistics;
			statistics = extractSingleFeature(file);
			wrigeSingleFile(bw,statistics,fileName);
		}
		bw.close();
		statusArea.append("Extaction is finished\n");
		
	}
	
	private void wrigeSingleFile(BufferedWriter bw, ArrayList<Double> statistics, String fileName) throws IOException
	{
		bw.write(fileName+",");
		for (Double d	:	statistics)
		{
			bw.write(String.valueOf(d)+",");			
		}
		bw.newLine();
	}
	
	private void writeTitle(BufferedWriter bw) throws IOException
	{
		bw.write("file name,");
		
		if (featureButton[0].isSelected() == true)	
			bw.write("number of transitions,");
		if (featureButton[1].isSelected() == true)
			bw.write("number of places,");
		if (featureButton[2].isSelected() == true)
			bw.write("number of arcs,");
		if (featureButton[3].isSelected() == true)
			bw.write("Edge density,");
		if (featureButton[4].isSelected() == true)
			bw.write("max inDegree,");
		if (featureButton[5].isSelected() == true)
			bw.write("max outDegree,");
		if (featureButton[6].isSelected() == true)
			bw.write("number of tars,");
		
		if (featureButton[7].isSelected() == true)
			bw.write("number of and-split,");
		if (featureButton[8].isSelected() == true)
			bw.write("min degree of and-split,");
		if (featureButton[9].isSelected() == true)
			bw.write("max degree of and-split,");
		if (featureButton[10].isSelected() == true)
			bw.write("average degree of and-split,");
		if (featureButton[11].isSelected() == true)
			bw.write("stdev degree of and-split,");
		
		if (featureButton[12].isSelected() == true)
			bw.write("number of and-join,");
		if (featureButton[13].isSelected() == true)
			bw.write("min degree of and-join,");
		if (featureButton[14].isSelected() == true)
			bw.write("max degree of and-join,");
		if (featureButton[15].isSelected() == true)
			bw.write("average degree of and-join,");
		if (featureButton[16].isSelected() == true)
			bw.write("stdev degree of and-join,");
		
		if (featureButton[17].isSelected() == true)
			bw.write("number of xor-split,");		
		if (featureButton[18].isSelected() == true)
			bw.write("min degree of xor-split,");
		if (featureButton[19].isSelected() == true)
			bw.write("max degree of xor-split,");
		if (featureButton[20].isSelected() == true)
			bw.write("average degree of xor-split,");
		if (featureButton[21].isSelected() == true)
			bw.write("stdev degree of xor-split,");
		
		if (featureButton[22].isSelected() == true)
			bw.write("number of xor-join,");
		if (featureButton[23].isSelected() == true)
			bw.write("min degree of xor-join,");
		if (featureButton[24].isSelected() == true)
			bw.write("max degree of xor-join,");
		if (featureButton[25].isSelected() == true)
			bw.write("average degree of xor-join,");
		if (featureButton[26].isSelected() == true)
			bw.write("stdev degree of xor-join,");
		
		if (featureButton[27].isSelected() == true)
			bw.write("number of state in state-space,");
		if (featureButton[28].isSelected() == true)
			bw.write("AND-XOR mismatch,");			
		if (featureButton[29].isSelected() == true)
			bw.write("Sequentiality,");
		if (featureButton[30].isSelected() == true)
			bw.write("TS,");
		if (featureButton[31].isSelected() == true)
			bw.write("CH,");
		if (featureButton[32].isSelected() == true)
			bw.write("CFC,");
		if (featureButton[33].isSelected() == true)
			bw.write("CYC,");
		if (featureButton[34].isSelected() == true)
			bw.write("Diam,");
		if (featureButton[35].isSelected() == true)
			bw.write("Separability,");
		if (featureButton[36].isSelected() == true)
			bw.write("Structuredness,");
		if (featureButton[37].isSelected() == true)
			bw.write("CNC,");
		if (featureButton[38].isSelected() == true)
			bw.write("MaxDegree of conecetor,");
		if (featureButton[39].isSelected() == true)
			bw.write("AverDegree of conector,");	
		if (featureButton[40].isSelected() == true)
			bw.write("Depth,");	
		
		if (featureButton[41].isSelected() == true)
			bw.write("Number of Invisible Tasks,");
		if (featureButton[42].isSelected() == true)
			bw.write("Number of Duplicate Tasks,");
		if (featureButton[43].isSelected() == true)
			bw.write("Non-Free Choice,");
		if (featureButton[44].isSelected() == true)
			bw.write("Arbitary Cycle,");
		if (featureButton[45].isSelected() == true)
			bw.write("Or-Join,");
		if (featureButton[46].isSelected() == true)
			bw.write("Short-Loop,");
		if (featureButton[47].isSelected() == true)
			bw.write("Nested-Loop,");		
			bw.newLine();
	}
	
	

	private ArrayList<Double> extractSingleFeature(File file)
	{
		PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(file);
		PetriNetMetrics pnm = new PetriNetMetrics(pn);
		ArrayList<Double> singleResult = new ArrayList<Double>();
		if (featureButton[0].isSelected() == true)
			singleResult.add((double)pnm.getNumberOfTransitions());
		if (featureButton[1].isSelected() == true)
			singleResult.add((double)pnm.getNumberOfPlaces());
		if (featureButton[2].isSelected() == true)
			singleResult.add((double)pnm.getNumberOfArcs());
		if (featureButton[3].isSelected() == true)
			singleResult.add((double)pnm.getDensity());
		if (featureButton[4].isSelected() == true)
			singleResult.add((double)pnm.getMaxInDegree());
		if (featureButton[5].isSelected() == true)
			singleResult.add((double)pnm.getMaxOutDegree());
		if (featureButton[6].isSelected() == true)
			singleResult.add((double)pnm.getNumberOfTARs());
		
				
			float[] ret = pnm.analyzeANDSplitDegree();
		if (featureButton[7].isSelected() == true)
			singleResult.add((double)(Math.round(ret[0])));
		if (featureButton[8].isSelected() == true)
			singleResult.add((double)(Math.round(ret[1])));
		if (featureButton[9].isSelected() == true)
			singleResult.add((double)(Math.round(ret[2])));
		if (featureButton[10].isSelected() == true)
			singleResult.add((double)ret[3]);
		if (featureButton[11].isSelected() == true)
			singleResult.add((double)ret[4]);
			
					
			ret = pnm.analyzeANDJoinDegree();
		if (featureButton[12].isSelected() == true)
			singleResult.add((double)(Math.round(ret[0])));
		if (featureButton[13].isSelected() == true)
			singleResult.add((double)(Math.round(ret[1])));
		if (featureButton[14].isSelected() == true)
			singleResult.add((double)(Math.round(ret[2])));
		if (featureButton[15].isSelected() == true)
			singleResult.add((double)ret[3]);
		if (featureButton[16].isSelected() == true)
			singleResult.add((double)ret[4]);
		
		
			ret = pnm.analyzeXORSplitDegree();
		if (featureButton[17].isSelected() == true)
			singleResult.add((double)(Math.round(ret[0])));
		if (featureButton[18].isSelected() == true)
			singleResult.add((double)(Math.round(ret[1])));
		if (featureButton[19].isSelected() == true)
			singleResult.add((double)(Math.round(ret[2])));
		if (featureButton[20].isSelected() == true)
			singleResult.add((double)ret[3]);
		if (featureButton[21].isSelected() == true)
			singleResult.add((double)ret[4]);							
		
			ret = pnm.analyzeXORJoinDegree();
		if (featureButton[22].isSelected() == true)
			singleResult.add((double)(Math.round(ret[0])));
		if (featureButton[23].isSelected() == true)
			singleResult.add((double)(Math.round(ret[1])));
		if (featureButton[24].isSelected() == true)
			singleResult.add((double)(Math.round(ret[2])));
		if (featureButton[25].isSelected() == true)
			singleResult.add((double)ret[3]);
		if (featureButton[26].isSelected() == true)
			singleResult.add((double)ret[4]);							
		
		
			int[] ret1 = pnm.analyzeStateSpace();
		if (featureButton[27].isSelected() == true)
			singleResult.add((double)ret1[0]);
			//singleResult.add((double)ret1[1]);
			
		if (featureButton[28].isSelected() == true)
			singleResult.add((double)pnm.getMismatch());
					
		if (featureButton[29].isSelected() == true)
			singleResult.add((double)pnm.getSequentiality());
						
		if (featureButton[30].isSelected() == true)
			singleResult.add((double)pnm.getTS());							
					
		if (featureButton[31].isSelected() == true)
			singleResult.add((double)pnm.getCH());
												
		if (featureButton[32].isSelected() == true)
			singleResult.add((double)pnm.getCFC());
				
		if (featureButton[33].isSelected() == true)
			singleResult.add((double)pnm.getCYC());
					
		if (featureButton[34].isSelected() == true)
			singleResult.add((double)pnm.getDiam());
					
		if (featureButton[35].isSelected() == true)
			singleResult.add((double)pnm.getSeparability());
					
		if (featureButton[36].isSelected() == true)
			singleResult.add((double)pnm.getStructuredness());
					
		if (featureButton[37].isSelected() == true)
			singleResult.add((double)pnm.getCNC());
					
		if (featureButton[38].isSelected() == true)
			singleResult.add((double)pnm.getMaxDegree());
					
		if (featureButton[39].isSelected() == true)
			singleResult.add((double)pnm.getAverDegree());
												
		if (featureButton[40].isSelected() == true)
			singleResult.add((double)pnm.getDepth());
		if (featureButton[41].isSelected() == true)
			singleResult.add((double)pn.getInvisibleTasks().size());
		if (featureButton[42].isSelected() == true)
			singleResult.add((double)pn.getNumberOfDuplicateTasks());
		if (featureButton[43].isSelected() == true)
			singleResult.add((double)PetriNetUtil.getNumberofNonFreeChoice(pn));
		if (featureButton[44].isSelected() == true)
			singleResult.add((double)PetriNetUtil.getNumberofArbitaryCycle(pn));
		if (featureButton[45].isSelected() == true)
			singleResult.add((double)PetriNetUtil.getNumberofOrJoin(pn));
		if (featureButton[46].isSelected() == true)
			singleResult.add((double)PetriNetUtil.getNumberofSimpleLoop(pn));
		if (featureButton[47].isSelected() == true)
			singleResult.add((double)PetriNetUtil.getNumberofNestedLoop(pn));
		return singleResult;
	}
	
	/**
	 * Get the bottom part of the panel
	 * @return the bottom panel
	 */
	private JPanel getBottompanel() {
		if (bottomPanel == null)
		{
			bottomPanel = new JPanel();
			bottomPanel.setLayout(null);
			
			//featureButton
			featureButton = new JCheckBox[48];
			for (int i=0; i<48; i++)
			{
				featureButton[i] = new JCheckBox(featureName[i]);
				featureButton[i].setSelected(true);
				//bottomPanel.add(featureButton[i]);
			}
			
			//buttonGroup
			checkGroups = new JPanel[8];
			for (int i=0; i<8; i++)
			{
				checkGroups[i] = new JPanel();
				checkGroups[i].setLayout(null);
				checkGroups[i].setBorder(BorderFactory.createTitledBorder(groupNames[i]));				
			}
			checkGroups[0].setBounds(10,10,150,210);
			checkGroups[1].setBounds(460,280,300,110);
			checkGroups[2].setBounds(160,10,150,180);			
			checkGroups[3].setBounds(160,190,150,180);
			checkGroups[4].setBounds(310,10,150,180);
			checkGroups[5].setBounds(310,190,150,180);
			checkGroups[6].setBounds(460,10,150,270);
			checkGroups[7].setBounds(610,10,150,270);
												
			bottomPanel.add(checkGroups[0]);
			bottomPanel.add(checkGroups[1]);
			bottomPanel.add(checkGroups[2]);
			bottomPanel.add(checkGroups[3]);
			bottomPanel.add(checkGroups[4]);
			bottomPanel.add(checkGroups[5]);
			bottomPanel.add(checkGroups[6]);
			bottomPanel.add(checkGroups[7]);
			
			for (int i=0; i<6; i++)
			{
				featureButton[i].setBounds(5,20+i*30,130,30);
				checkGroups[0].add(featureButton[i]);
			}
			
			//Str & Beh
			for (int i=33; i<36; i++)
			{
				if (i == 34) 
					continue;
				featureButton[i].setBounds(5,15+(i-33)*30,130,30);
				checkGroups[1].add(featureButton[i]);
			}
			for (int i=36; i<38; i++)
			{
				featureButton[i].setBounds(5+150,15+(i-36)*30,130,30);
				checkGroups[1].add(featureButton[i]);
			}
			for (int i=27; i<28; i++)
			{
				featureButton[i].setBounds(5+150,15+30*2+(i-27)*30,130,30);
				checkGroups[1].add(featureButton[i]);
			}
			featureButton[6].setBounds(5,15+1*30,130,30);
			checkGroups[1].add(featureButton[6]);
			
			
			for (int i=7;i<12; i++)
			{
				featureButton[i].setBounds(5,20+(i-7)*30,130,30);
				checkGroups[2].add(featureButton[i]);
			}
			
			for (int i=12;i<17; i++)
			{
				featureButton[i].setBounds(5,20+(i-12)*30,130,30);
				checkGroups[3].add(featureButton[i]);
			}
			
			for (int i=17;i<22; i++)
			{
				featureButton[i].setBounds(5,20+(i-17)*30,130,30);
				checkGroups[4].add(featureButton[i]);
			}
			
			for (int i=22;i<27; i++)
			{
				featureButton[i].setBounds(5,20+(i-22)*30,130,30);
				checkGroups[5].add(featureButton[i]);
			}
			
			
			//Connector Information
			for (int i=28;i<33; i++)
			{
				featureButton[i].setBounds(5,20+(i-28)*30,130,30);
				checkGroups[6].add(featureButton[i]);
			}
			
			for (int i=38;i<41; i++)
			{
				featureButton[i].setBounds(5,20+30*5+(i-38)*30,130,30);
				checkGroups[6].add(featureButton[i]);
			}
			
			//
			
			for (int i=41;i<48; i++)
			{
				featureButton[i].setBounds(5,20+(i-41)*30,130,30);
				checkGroups[7].add(featureButton[i]);
			}
			featureButton[34].setBounds(5,20+(48-41)*30,130,30);
			checkGroups[7].add(featureButton[34]);
			
			checkGroups[0].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[0].isSelected() == true)
						for (int i=0; i<6; i++)
							featureButton[i].setSelected(false);
					else
						for (int i=0; i<6; i++)
							featureButton[i].setSelected(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;					
				}
				
			});
			
			checkGroups[1].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[33].isSelected() == true)
					{
						for (int i=33; i<38; i++)
						{
							if (i==34) 
								continue;
							featureButton[i].setSelected(false);							
						}
						featureButton[6].setSelected(false);
						featureButton[27].setSelected(false);
					}
					else
					{
						for (int i=33; i<38; i++)
						{
							if (i==34) 
								continue;
							featureButton[i].setSelected(true);
						}
						featureButton[6].setSelected(true);
						featureButton[27].setSelected(true);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;					
				}
				
			});
			
			checkGroups[2].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[7].isSelected() == true)					
						for (int i=7; i<12; i++)
							featureButton[i].setSelected(false);
					else
						for (int i=7; i<12; i++)
							featureButton[i].setSelected(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;				
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;	
				}
				
			});
			
			checkGroups[3].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[12].isSelected() == true)
						for (int i=12; i<17; i++)
							featureButton[i].setSelected(false);
					else
						for (int i=12; i<17; i++)
							featureButton[i].setSelected(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;	
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;	
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;	
				}
				
			});
			
			checkGroups[4].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[17].isSelected() == true)
						for (int i=17; i<22; i++)
							featureButton[i].setSelected(false);
					else
						for (int i=17; i<22; i++)
							featureButton[i].setSelected(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;
				}
				
			});
			
			checkGroups[5].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[22].isSelected() == true)
						for (int i=22; i<27; i++)
							featureButton[i].setSelected(false);
					else
						for (int i=22; i<27; i++)
							featureButton[i].setSelected(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;
				}
				
			});
			
			checkGroups[6].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[28].isSelected() == true)
					{
						for (int i=28; i<33; i++)
							featureButton[i].setSelected(false);
						for (int i=38; i<41; i++)
							featureButton[i].setSelected(false);
					}
					else
					{
						for (int i=28; i<33; i++)
							featureButton[i].setSelected(true);
						for (int i=38; i<41; i++)
							featureButton[i].setSelected(true);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;
				}
				
			});
			
			checkGroups[7].addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent arg0) {		
					if (featureButton[41].isSelected() == true)
					{
						for (int i=41; i<48; i++)
							featureButton[i].setSelected(false);
						featureButton[34].setSelected(false);
					}
					else
					{
						for (int i=41; i<48; i++)
							featureButton[i].setSelected(true);
						featureButton[34].setSelected(true);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					return;
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					return;
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					return;
				}
				
			});
						
				
		}
		return bottomPanel;		
	}		
}
