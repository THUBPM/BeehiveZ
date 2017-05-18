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
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Text;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.client.ui.modelquery.QBTL.ButtonRenderer;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.mcmillanindex.Searching;
import cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser.*;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
/**
 * @author Liang Song 2011.9.26
 * 
 */
public class QueryByTLL extends JSplitPane{
	private static final long serialVersionUID = 1L;
	private JPanel topPanel = null;
	private JScrollPane queryResult = null;
	private JTable resultTable = null;
	private JLabel queryInfo = null;
/*	private JTextField queryCondition = null;
	private JTextField queryCondition1 = null;*/
	private JLabel totalTimeJLabel = null;
	private JTextField totalTimeJTextField = null;
	private JLabel totalUnfoldingTimeJLabel = null;
	private JTextField totalUnfoldingTimeJTextField = null;
	private JLabel currentDirectoryJLable = null;
	private JTextField currentDirectoryJTextField = null;
	private JButton query = null;
	private JButton calCFP = null;
	private JButton directory = null;
	private JButton help = null;
	private File fileDirectory = new File("C:\\model\\");
	
	private HashMap<String, OrderingRelationMatrix> file_orm
	 = new HashMap<String, OrderingRelationMatrix>();
	
	String queryString = null;
	String s2 = null;
	String s3 = null;
	
	
	private JTextField querySentence = null;
	
	private Run run;
	
	ResourcesManager resourcesManager;
	  

	/**
	 * This is the default constructor
	 */
	public QueryByTLL() {
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
		this.setSize(750, 400);

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
			queryInfo = new JLabel();
			queryInfo.setBounds(new Rectangle(47, 28, 108, 30));
			queryInfo.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.querystring"));
			// s1=queryInfo.getText();

			/*queryInfo1 = new JLabel();
			queryInfo1.setBounds(new Rectangle(370, 28, 108, 30));
			queryInfo1.setText("LabelOfTargetN");*/
			// s2=queryInfo1.getText();
			
			totalTimeJLabel=new JLabel();
			totalTimeJLabel.setBounds(new Rectangle(295, 150, 113, 40));
			totalTimeJLabel.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.tqt"));
			
			totalTimeJTextField=new JTextField();
			totalTimeJTextField.setBounds(new Rectangle(435, 150, 70, 40));
			
			totalUnfoldingTimeJLabel=new JLabel();
			totalUnfoldingTimeJLabel.setBounds(new Rectangle(295, 100, 133, 40));
			totalUnfoldingTimeJLabel.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.tut"));
			
			totalUnfoldingTimeJTextField=new JTextField();
			totalUnfoldingTimeJTextField.setBounds(new Rectangle(435, 100, 70, 40));

			currentDirectoryJLable=new JLabel();
			currentDirectoryJLable.setBounds(new Rectangle(565, 60, 133, 40));
			currentDirectoryJLable.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.cd"));
			
			currentDirectoryJTextField=new JTextField();
			currentDirectoryJTextField.setBounds(new Rectangle(535, 100, 200, 40));
			currentDirectoryJTextField.setText(this.fileDirectory.getAbsolutePath());
			

			topPanel = new JPanel();
			topPanel.setLayout(null);

			topPanel.add(queryInfo, null);
			topPanel.add(getQuerySentence(), null);

			/*topPanel.add(queryInfo1, null);
			topPanel.add(getQueryCondition1(), null);*/
			
		//	topPanel.add(getQuerySentence(), null);

			topPanel.add(getQuery(), null);
			topPanel.add(getCalCFP(), null);
			
			topPanel.add(getDirectory(), null);
			topPanel.add(getHelp(), null);
			
			topPanel.add(totalTimeJLabel, null);
			topPanel.add(totalTimeJTextField, null);
			topPanel.add(totalUnfoldingTimeJLabel, null);
			topPanel.add(totalUnfoldingTimeJTextField, null);
			topPanel.add(this.currentDirectoryJLable, null);
			topPanel.add(this.currentDirectoryJTextField, null);
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
	
	private JButton getDirectory(){
		directory = new JButton();
		directory.setBounds(new Rectangle(600, 20, 135, 40));
		directory.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.modelcollection"));
		directory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				// Note: source for ExampleFileFilter can be found in FileChooserDemo,
				    // under the demo/jfc directory in the JDK.
				    
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    int returnVal = chooser.showOpenDialog(topPanel);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				       //System.out.println("You chose directory: " +
				            //chooser.getSelectedFile().getAbsolutePath());
				       fileDirectory = new File(chooser.getSelectedFile().getAbsolutePath());
				       currentDirectoryJTextField.setText(fileDirectory.getAbsolutePath());
				    }
			}
		});
		
		return directory;
	}
	
	private JButton getHelp(){
		
        
        help = new JButton();
        help.setBounds(new Rectangle(665, 150, 70, 40));
        help.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.help"));
        help.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File   file=new   File( ". ");
				//System.out.println(file.getAbsolutePath());
					
				int len = file.getAbsolutePath().length();
				String   f= file.getAbsolutePath().substring(0, len - 2) + "QueryHelp.pdf";
				//System.out.println(f);
		        Runtime   r   =   Runtime.getRuntime();
		        try {
					r.exec( "cmd   /c   start   "   +   f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		});
		
		return help;
	}

	/**
	 * This method initializes resultTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getResultTable() {
		if (resultTable == null) {

			String[] columnNames = { 
					resourcesManager.getString("ModelQuery.Plugin.qbtl.modelid"), 
					resourcesManager.getString("ModelQuery.Plugin.qbtl.modelname"), 
					resourcesManager.getString("ModelQuery.Plugin.qbtl.view"),
					resourcesManager.getString("ModelQuery.Plugin.qbtl.runtime")
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
			resultTable.getColumn(resourcesManager.getString("ModelQuery.Plugin.qbtl.view")).setCellRenderer(new ButtonRenderer());
			resultTable.getTableHeader().setFont(
					new Font("Dialog", Font.BOLD, 14));

			final int visualModel = 1;

			resultTable.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {

					Point point = e.getPoint();
					int i = resultTable.rowAtPoint(point);
					int j = resultTable.columnAtPoint(point);
					if (j == visualModel) {
						//InputStream in;
						try {
							String file =  (String) resultTable.getValueAt(i, 0);
							
							
							FileInputStream fin = null;
							try {
								fin = new FileInputStream(fileDirectory.getAbsolutePath() + "\\" + file);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			            	
							
							VisualFrame visualframe = new VisualFrame(
									ProcessObject.TYPEPNML, fin);
							visualframe.setVisible(true);

						}
						catch(Exception e2){
							e2.printStackTrace();
						}
					

					};
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


	
	private JTextField getQuerySentence() {
		if (querySentence == null) {
			querySentence = new JTextField();
			querySentence.setBounds(new Rectangle(160, 26, 400, 40));
			// queryCondition1.setLineWrap(true);
			querySentence
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		return querySentence;

	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setBounds(new Rectangle(200, 110, 73, 40));
			query.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.query"));

			
			query.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					// TODO get three parameters as follows
					// DataManager dm = DataManager.getInstance();
					long tut = 01;
					
					queryString = querySentence.getText();
					if (queryString == null) {
						return;
					}
					run = new Run(queryString);
					run.start();
					//Vector<ProcessObject> resultVector = new Vector<ProcessObject>();
					String[] newIdentifiers = {"Model Name", 
							  "View",
							"Query Time"};
					JTable table = resultTable;
					DefaultTableModel model = new DefaultTableModel();
					model.setColumnIdentifiers(newIdentifiers);

					try {
						/*DataManager dm = DataManager.getInstance();
						String strSelectPetriNet = "select PROCESS_ID, DEFINITIONMPN, DEFINITIONTPO from mcmillanIndex";						
						ResultSet rs = dm.executeSelectSQL(strSelectPetriNet);

						while (rs.next()) {
							long process_id = (int)rs.getLong("PROCESS_ID");
							
							process_id -= 55;
							if(process_id == 8){
								process_id+= 1;

								process_id = 8;
							}
							String pid = Integer.toString((int)(process_id));
							InputStream in = rs.getAsciiStream("DEFINITIONMPN");
							//InputStream in = rs.getAsciiStream("DEFINITIONMPN");
							FileInputStream fin = null;
							try {
								fin = new FileInputStream("C:\\New Petri net "+pid+".pnml");
								} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							MyPetriNet myPetriNet = new MyPetriNet();
							ONCompleteFinitePrefix tpcfp = new ONCompleteFinitePrefix();
							
							PnmlImport pImport = new PnmlImport();
							PetriNetResult pnr = null;
							try {
								pnr = (PetriNetResult) pImport.importFile(fin);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							PetriNet pn = pnr.getPetriNet();
							//pn.setIdentifier("1258790072312.pnml");
							myPetriNet = MyPetriNet.PromPN2MyPN(pn);
							ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(myPetriNet);
							*/
				/*			tpcfp = cfpBuilder.Build();*/
                    		

							/*run.setCFP(tpcfp);
							run.setPetriNet(myPetriNet);*/
						
				        File[] fileList = fileDirectory.listFiles();  
				         
				        for (int i = 0 ; i< fileList.length ; i++)   
				        {   
				            File file = fileList[i];
				            if(!file.isFile())
				            	continue;
				            if(file.getName().contains(".pnml")){
				            	
				            	OrderingRelationMatrix orm = null;
				            	orm = file_orm.get(file.getName());
				            	if(orm == null)
				            		continue;
								
				            	run.setORM(orm);
				            	
								Date startq = Calendar.getInstance().getTime();
								boolean result = run.getPredictResult();
								//b=searching.search(s1, s2, myPetriNet, tpcfp);
								Date stopq = Calendar.getInstance().getTime(); 
								long querytime = stopq.getTime() - startq.getTime();
								
								
								if(result==true){
									model
									.addRow(new Object[] {
											file.getName(), "view",
											querytime + "ms" });
								}


								tut+= querytime;
								
				            }  
				        }

						table.setModel(model);
						table.validate();
					} catch (Exception e) {
						e.printStackTrace();
					}

					totalTimeJTextField.setText(Long.toString(tut)+"ms");
					
				}
			});
		}
		return query;
	}

	private JButton getCalCFP() {
		if (calCFP == null) {
			calCFP = new JButton();
			calCFP.setBounds(new Rectangle(85, 110, 73, 40));
			calCFP.setText(resourcesManager.getString("ModelQuery.Plugin.qbtl.calcfp"));

			
			calCFP.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					// TODO get three parameters as follows
					// DataManager dm = DataManager.getInstance();
					long l=0l;

					try {
						
						
				        File[] fileList = fileDirectory.listFiles();  
				         
				        for (int i = 0 ; i< fileList.length ; i++)   
				        {   
				            File file = fileList[i];
				            if(!file.isFile())
				            	continue;
				            if(file.getName().contains(".pnml")){
				            	
				            	FileInputStream fin = null;
								try {
									fin = new FileInputStream(fileDirectory.getAbsolutePath() + "\\" +  file.getName());
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
				            	MyPetriNet myPetriNet = new MyPetriNet();
								ONCompleteFinitePrefix tpcfp = new ONCompleteFinitePrefix();
								
								PnmlImport pImport = new PnmlImport();
								PetriNetResult pnr = null;
								try {
									pnr = (PetriNetResult) pImport.importFile(fin);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								PetriNet pn = pnr.getPetriNet();
								myPetriNet = MyPetriNet.PromPN2MyPN(pn);
								
								Date start = Calendar.getInstance().getTime();
								
								ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(myPetriNet);

								tpcfp = cfpBuilder.Build();
								
								Date stop = Calendar.getInstance().getTime(); 
								
								OrderingRelationMatrix orm = new OrderingRelationMatrix(tpcfp);
								file_orm.put(file.getName(), orm);
								
								long time = stop.getTime() - start.getTime();
								l+=time;
								
				            }
				        }
							
							
						//rs.close();


					} catch (Exception e) {
						e.printStackTrace();
					}
					s3=Long.toString(l);
					totalUnfoldingTimeJTextField.setText(s3+"ms");
					
				}
			});
		}
		return calCFP;
	}

}
