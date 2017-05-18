package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.processmining.analysis.clustering.ResultGrappaAdapter;
import org.processmining.framework.util.Dot;

import att.grappa.Graph;
import att.grappa.GrappaPanel;

import cn.edu.thss.iise.beehivez.client.ui.miningevaluate.MyCurve;
import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;



public class ModelPetriResourceAllocationUI extends JSplitPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1816118805815703750L;
	
	//useful component 
	//choose the petri and resource file
	private JSplitPane toppanel = null;
	private JPanel topleftpanel = null;
	private JPanel topleftPanel1 = null;
	private JButton choosePetriFileButton = null;
	private JButton chooseResourceFileButton = null;
	private JButton findSolutionButton = null;
	private String petriFilePath = null;
	private String resourceFilePath = null;
	private JLabel JOpenPetriLabel = null;
	private JLabel JPetriPathLabel = null;
	private JLabel JOpenResourceLabel = null;	
	private JLabel JResourcePathLabel = null;
	private JLabel JSolutionLabel = null;
	
	private JPanel toprightpanel = null;
	public JPanel visualPanel = new JPanel();
	private DefaultTableModel m_data = null;
	private JTable jTable = null;
	Resource resource = null;
	NetworkFlowModel nfModel = null;  //网络流图
	
	private JSplitPane bottompanel = null;
	//资源列表
	private JPanel bottomleftpanel = null;
	//解决方案图
	private GrappaPanel bottomrightpanel=null;
	private Graph graph = null;
	//private JComboBox algorithmList = null;
	//the components before
	
	
	
	
	
	// public MessagePanel messagePanel = new MessagePanel();
	public Vector<String> logAlgorithmList = null;
	public Vector<String> miningAlgorithmList = null;
	public Vector<String> similarityAlgorithmList = null;
	public Vector<String> similarityStrAlgorithmList = null;
	public int selectedLogAlgorithm = 0;
	public int selectedMiningAlgorithm = 0;
	public int selectedSimilarityAlgorithm = 0;
	public int selectedStrSimilarityAlgorithm = 0;
	public Hashtable<String, ResourcePetriNet> map = new Hashtable<String, ResourcePetriNet>();
	public HashMap<String, String> logminemap = null;
	public HashMap<String, String> modellogmap = null;
	public HashMap<String, String> modelminemap = null;
	public HashMap<Float, String> simmodelmap = null;
	public HashMap<Float, LinkedList<String>> map1 = null;
	public HashMap<String, Integer> filenummap = null;
	private JScrollPane jScrollPane1 = null;
	private ResourcePetriNet processModel;
	ResourcesManager resourcesManager = new ResourcesManager();
	
	
	public ModelPetriResourceAllocationUI()
	{
		super();
		initialize();
		// Message.add("Initialized successfully!");
		System.out.println("Initialized successfully!");
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {		
		this.setSize(800, 500);
		this.setTopComponent(getToppanel());
		this.setBottomComponent(getBottompanel());

		this.setDividerLocation(300);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		nfModel = new NetworkFlowModel();

	}
	
	
	private JSplitPane getToppanel() {
		// TODO Auto-generated method stub
		if (toppanel == null) {
			toppanel = new JSplitPane();
			toppanel.setDividerLocation(300);
			toppanel.setLeftComponent(getLeftToppanel());
			toppanel.setRightComponent(getRightToppanel());
			toppanel.setDividerSize(10);
		}
		return toppanel;
	}

	private JSplitPane getBottompanel() {
		// TODO Auto-generated method stub
		if (bottompanel == null) {
			bottompanel = new JSplitPane();
			bottompanel.setDividerLocation(300);
			bottompanel.setLeftComponent(getLeftBottompanel());
			bottompanel.setRightComponent(getRightBottompanel());
			bottompanel.setDividerSize(10);
		}
		return bottompanel;
	}

	private JPanel getLeftBottompanel() {
		// TODO Auto-generated method stub
		if (bottomleftpanel == null) {		
			bottomleftpanel = new JPanel();
			String[] columnNames = { 
					new String("Name"), 
					new String("Post"), 
					new String("Capability") 
					};
			if(m_data == null)
			{
				m_data = new DefaultTableModel();
				for(int i = 0; i < 3; i++)
				{
					m_data.addColumn(columnNames[i]);
				}
			}
			if(jTable == null)
			{
				jTable = new JTable(m_data);
			}
			jTable.setPreferredScrollableViewportSize(new Dimension(250,150));
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane sPane = new JScrollPane(jTable);			
			bottomleftpanel.add(sPane, null);			
		}
		return bottomleftpanel;
	}
	
	public JPanel getRightBottompanel() {
		// TODO Auto-generated method stub
		if(bottomrightpanel==null){
			graph = new Graph("empty graph");
			bottomrightpanel = new GrappaPanel(graph);		
		}
		return bottomrightpanel;
	}
	
	//左上角部分用于打开文件和显示文件路径
	private JPanel getLeftToppanel() {
		// TODO Auto-generated method stub
		if (topleftpanel == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new Dimension(270, 250));

			JOpenPetriLabel = new JLabel();
			JOpenPetriLabel.setBounds(10, 10, 240, 30);
			JOpenPetriLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			JOpenPetriLabel.setText("Open the petri file");
			
			JPetriPathLabel = new JLabel();
			JPetriPathLabel.setBounds(new Rectangle(10, 50, 240, 30));
			JPetriPathLabel.setFont(new Font("Dialog", Font.ITALIC, 12));
			JPetriPathLabel.setText("the petri file path");

			
			JOpenResourceLabel = new JLabel();
			JOpenResourceLabel.setBounds(new Rectangle(10, 90, 240, 30));
			JOpenResourceLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			JOpenResourceLabel.setText("Open the resource file");
				
						
			JResourcePathLabel = new JLabel();
			JResourcePathLabel.setBounds(new Rectangle(10, 130, 240, 30));
			JResourcePathLabel.setFont(new Font("Dialog", Font.ITALIC, 12));
			JResourcePathLabel.setText("the resource file path");
			
			JSolutionLabel = new JLabel();
			JSolutionLabel.setBounds(new Rectangle(10, 170, 240, 30));
			JSolutionLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			JSolutionLabel.setText("show the solution");

			
			topleftpanel = new JPanel();
			topleftpanel.setLayout(null);
			/*
			 *open the petri file 
			 */			
			topleftpanel.add(JOpenPetriLabel);			
			topleftpanel.add(getChoosePetriButton());
			topleftpanel.add(JPetriPathLabel);
			
			/*
			 * open the source file 
			 */			
			topleftpanel.add(JOpenResourceLabel,null);
			topleftpanel.add(getChooseResourceButton(), null);
			topleftpanel.add(JResourcePathLabel,null);
			
			/*
			 * show the solution 
			 */
			topleftpanel.add(JSolutionLabel,null);
			topleftpanel.add(getfindSolutionButton(), null);
			
			JScrollPane sPane = new JScrollPane(topleftpanel);
			topleftPanel1 = new JPanel();
			topleftPanel1.add(sPane, null);	
			
		}
		return topleftpanel;
	}

	/**
	 * This method initializes toppanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRightToppanel() {
		if (toprightpanel == null) {		
			toprightpanel = new JPanel();
			toprightpanel.setLayout(new BorderLayout());			
		}
		return toprightpanel;
	}
	
	
	/**
	 * This method initializes petri File
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getChoosePetriButton() {
		if(nfModel != null)
		{
			nfModel.clearData();
		}
		if (choosePetriFileButton == null) {
			choosePetriFileButton = new JButton("pDirectory");
			choosePetriFileButton.setBounds(new Rectangle(170, 10, 120, 30));
			choosePetriFileButton.addActionListener(new ActionListener() {				

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser("c://");
					
					ExtensionFilter filter1 = new ExtensionFilter(".pnml",
							"PNML files (*.pnml)");
					/*
					ExtensionFilter filter2 = new ExtensionFilter(".yawl",
							"YAWL files (*.yawl)");
					*/
					fileChooser.setDialogTitle("Choose Petri File");
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					fileChooser.rescanCurrentDirectory();
					fileChooser.addChoosableFileFilter(filter1);
					//fileChooser.addChoosableFileFilter(filter2);
					fileChooser.setFileFilter(filter1);
					int choose = fileChooser.showOpenDialog(null);
					if (choose == JFileChooser.APPROVE_OPTION) {
						petriFilePath = fileChooser.getSelectedFile()
								.getAbsolutePath();
					} else {
						return;
					}
					FileInputStream in;
					try {
						DataManager dm = DataManager.getInstance();
						in = new FileInputStream(petriFilePath);
						VisualFrame visualframe = null;
						processModel = (ResourcePetriNet) PetriNetUtil.getResourcePetriNetFromPnmlFile(petriFilePath);
						if (petriFilePath.endsWith(".pnml")) {
							visualframe = new VisualFrame(
									ProcessObject.TYPEPNML, in, processModel);							
						} 
						visualPanel = visualframe.getJContentPane();
						toprightpanel.removeAll();
						toprightpanel.add(visualPanel, BorderLayout.CENTER);
						toprightpanel.setVisible(true);
						toprightpanel.updateUI();
						JPetriPathLabel.setText(petriFilePath);
																	
						//addResourcePetriNetToNetWorkFlowModel(processModel,nfModel);
						JOptionPane.showMessageDialog(null,"read the petri file now!");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return choosePetriFileButton;
	}
	
	/**
	 * This method initializes resource File
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getChooseResourceButton() {
		if(nfModel != null)
		{
			nfModel.clearData();
		}
		if (chooseResourceFileButton == null) {
			chooseResourceFileButton = new JButton("rDirectory");
			chooseResourceFileButton.setBounds(new Rectangle(170, 90, 120, 30));
			chooseResourceFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser("c://");
					ExtensionFilter filter1 = new ExtensionFilter(".txt",
							"TXT files (*.txt)");
					/*
					ExtensionFilter filter2 = new ExtensionFilter(".yawl",
							"YAWL files (*.yawl)");
					*/
					fileChooser.setDialogTitle("Choose Resource File");
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					fileChooser.rescanCurrentDirectory();
					fileChooser.addChoosableFileFilter(filter1);
					//fileChooser.addChoosableFileFilter(filter2);
					fileChooser.setFileFilter(filter1);
					int choose = fileChooser.showOpenDialog(null);
					if (choose == JFileChooser.APPROVE_OPTION) {
						resourceFilePath = fileChooser.getSelectedFile()
								.getAbsolutePath();
					} else {
						return;
					}

					JResourcePathLabel.setText(resourceFilePath);
					resource = new Resource();
					//读入资源文件
					resource.findResouce(resourceFilePath);
					//更新左下角的资源列表模块
					updateResourceTable();					
					//更新网络流图
					//nfModel.updateDataByResource(resource);
					//read the resource file here
					JOptionPane.showMessageDialog(null,"read the resource file now!");
				}

			});
		}
		return chooseResourceFileButton;
	}
	
	/**
	 * 
	 * @return 查找解决方案按钮
	 */
	private JButton getfindSolutionButton() {
		if (findSolutionButton == null) {
			findSolutionButton = new JButton("Solution");
			findSolutionButton.setBounds(new Rectangle(170, 170, 120, 30));
			findSolutionButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//nfModel.clearData();
					nfModel = new NetworkFlowModel();
					addResourcePetriNetToNetWorkFlowModel(processModel,nfModel);
					nfModel.updateDataByResource(resource);
					nfModel.addSrcTgt();
					nfModel.getMaxFlow();					
					
					//JOptionPane.showMessageDialog(null,"find the solution now!");
					updateSolutionPanel(nfModel);
				}

			});
		}
		return findSolutionButton;
	}
	
	
	/**
	 * 更新资源列表
	 */
	private void updateResourceTable()
	{
		DefaultTableModel tableModel = new DefaultTableModel();// (DefaultTableModel)scoreTable.createDefaultDataModel();
		String[] columns = {"Name", "Post", "Capability"};
		int postSize = resource.getPostCount();		
		Object[][] data = new Object[postSize][3];
		
		int staffSize = resource.getstaffCount();
		int i = 0, j = 0, k = 0;
		Staff tempStaff;
		for(i = 0; i < staffSize; i++)
		{
			tempStaff = resource.getStaffAt(i);
			int tempP = tempStaff.getPostSum();
			for(j = 0; j < tempP; j++)
			{
				data[k][0] = tempStaff.name;
				data[k][1] = tempStaff.posts.get(j);
				data[k][2] = tempStaff.capabilities.get(j);
				k++;
			}
		}
		tableModel.setDataVector(data, columns);
		jTable.setModel(tableModel);
	}
	
	/**
	 * 更新解决方案模块
	 */
	private void updateSolutionPanel(NetworkFlowModel nf)
	{
		//更具nf，更新solution.dot
		String dotFileName = new String("D:\\temp\\solution.dot");
		updateDotFile(nf,dotFileName);
		try {
			graph = Dot.execute(dotFileName);
			graph.setEditable(true);
			graph.setMenuable(true);
			graph.setErrorWriter(new PrintWriter(System.err, true));

			// create the visual component and return it
			bottomrightpanel = new GrappaPanel(graph);
			/*
			ResultGrappaAdapter ga = new ResultGrappaAdapter(this);
			gp.addGrappaListener(ga);
			*/
			bottomrightpanel.setScaleToFit(true);
			bottomrightpanel.repaint();
			bottompanel.setRightComponent(bottomrightpanel);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新dot文件
	 * @param nf 网络流模型
	 */
	private void updateDotFile(NetworkFlowModel nf,String fileName)
	{
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(fileName));			
			bw.write("digraph G { rankdir=LR fontname=\"FangSong\"");
			bw.newLine();
			//人员
			bw.write("subgraph cluster_0 {");
			bw.newLine();
			bw.write("style=filled;color=lightgrey;node [style=filled,color=white];");
			bw.newLine();
			bw.write("label = \"Name\"");
			bw.newLine();
			int size = nf.edges.size();
			NFEdge tempEdge;
			NFNode src, tgt;
			for(int i = 0; i < size; i++)
			{
				tempEdge = nf.getEdgeAt(i);
				src = tempEdge.getSource();
				if(src.getType() == 0)
				{
					bw.write(src.getLabel()+";");
				}
			}
			bw.write("}");
			bw.newLine();
			//角色
			bw.write("subgraph cluster_1 {");
			bw.newLine();
			bw.write("style=filled;color= lightblue;node [style=filled,color=white];");
			bw.newLine();
			bw.write("label = \"Post\"");
			bw.newLine();
			for(int i = 0; i < size; i++)
			{
				tempEdge = nf.getEdgeAt(i);
				src = tempEdge.getSource();
				tgt = tempEdge.getTarget();
				if(src.getType() == 1)
				{
					bw.write(src.getLabel()+";");
				}
				if(tgt.getType() == 1)
				{
					bw.write(tgt.getLabel()+";");
				}
			}
			bw.write("}");
			bw.newLine();
			//任务			
			bw.write("subgraph cluster_2 {");
			bw.newLine();
			bw.write("color= black;node [color=white];");
			bw.newLine();
			bw.write("label = \"Task\"");
			bw.newLine();
			for(int i = 0; i < size; i++)
			{
				tempEdge = nf.getEdgeAt(i);
				src = tempEdge.getSource();
				if(src.getType() == 2)
				{
					bw.write(src.getLabel()+"[label = \""+src.getLabel()+"\", color = "+(tempEdge.flow == 1?"green":"red")+"]"+";");
				}
//				tgt = tempEdge.getTarget();
//				if(tgt.getType() == 2)
//				{
//					bw.write(tgt.getLabel()+";");
//				}
			}
			bw.write("}");
			bw.newLine();
			//边
			for(int i = 0; i < size; i++)
			{
				tempEdge = nf.getEdgeAt(i);
				src = tempEdge.getSource();
				tgt = tempEdge.getTarget();
				bw.write(src.getLabel()+" -> " + tgt.getLabel()
						+"[ label = \""+tempEdge.getCapacity()+":"+ tempEdge.getFlow()
						+"\"];");
				bw.newLine();
			}
			bw.write("}");
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addResourcePetriNetToNetWorkFlowModel(ResourcePetriNet processModel2,
			NetworkFlowModel nfModel2) {
		// TODO Auto-generated method stub
		ArrayList<ResourceTransition> ResourceTransitionList = processModel2.getResourceTransitions();
		for (ResourceTransition ResourceTransition	:	ResourceTransitionList)
		{
			NFNode node;
			node = new NFNode(ResourceTransition.getIdentifier(), 2);
			nfModel2.addNode(node);
			ArrayList<String> roles = ResourceTransition.getRoles();
			for (String role	:	roles)
			{
				NFNode roleNode = nfModel2.isInNodes(role, 1);
				if (roleNode == null)
				{
					roleNode = new NFNode(role,1);
					nfModel2.addNode(roleNode);
				}
				NFEdge edge = new NFEdge(roleNode,node,1);
				nfModel2.addEdge(edge);
			}
		}
	}
	
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