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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.mcmillanindex.Searching;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

public class QBTL extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private JPanel topPanel = null;
	private JScrollPane queryResult = null;
	private JTable resultTable = null;
	private JLabel queryInfo = null;
	private JLabel queryInfo1 = null;
	private JTextField queryCondition = null;
	private JTextField queryCondition1 = null;
	private JLabel totalTimeJLabel = null;
	private JTextField totalTimeJTextField = null;
	private JButton query = null;
	String s1 = null;
	String s2 = null;
	String s3 = null;
	ResourcesManager resourcesManager;
	  

	/**
	 * This is the default constructor
	 */
	public QBTL() {
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
			queryInfo = new JLabel();
			queryInfo.setBounds(new Rectangle(47, 28, 108, 30));
			queryInfo.setText(resourcesManager.getString("ModelQuery.Plugin.qbt.start"));
			// s1=queryInfo.getText();

			queryInfo1 = new JLabel();
			queryInfo1.setBounds(new Rectangle(370, 28, 108, 30));
			queryInfo1.setText(resourcesManager.getString("ModelQuery.Plugin.qbt.target"));
			// s2=queryInfo1.getText();
			
			totalTimeJLabel=new JLabel();
			totalTimeJLabel.setBounds(new Rectangle(395, 139, 73, 40));
			totalTimeJLabel.setText(resourcesManager.getString("ModelQuery.Plugin.qbt.time"));
			
			totalTimeJTextField=new JTextField();
			totalTimeJTextField.setBounds(new Rectangle(485, 139, 200, 40));
			

			topPanel = new JPanel();
			topPanel.setLayout(null);

			topPanel.add(queryInfo, null);
			topPanel.add(getQueryCondition(), null);

			topPanel.add(queryInfo1, null);
			topPanel.add(getQueryCondition1(), null);

			topPanel.add(getQuery(), null);
			
			topPanel.add(totalTimeJLabel, null);
			topPanel.add(totalTimeJTextField, null);
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

			String[] columnNames = { 
					resourcesManager.getString("ModelQuery.Plugin.qbt.modelid"), 
					resourcesManager.getString("ModelQuery.Plugin.qbt.modelname"), 
					resourcesManager.getString("ModelQuery.Plugin.qbt.view"),
					resourcesManager.getString("ModelQuery.Plugin.qbt.runtime") };
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
			resultTable.getColumn(resourcesManager.getString("ModelQuery.Plugin.qbt.view")).setCellRenderer(new ButtonRenderer());
			resultTable.getTableHeader().setFont(
					new Font("Dialog", Font.BOLD, 14));

			final int visualModel = 2;

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
	private JTextField getQueryCondition() {
		if (queryCondition == null) {
			queryCondition = new JTextField();
			queryCondition.setBounds(new Rectangle(160, 26, 200, 50));
			// queryCondition.setLineWrap(true);
			queryCondition
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		}
		return queryCondition;
	}

	private JTextField getQueryCondition1() {
		if (queryCondition1 == null) {
			queryCondition1 = new JTextField();
			queryCondition1.setBounds(new Rectangle(480, 26, 200, 50));
			// queryCondition1.setLineWrap(true);
			queryCondition1
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		return queryCondition1;

	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setBounds(new Rectangle(215, 139, 73, 40));
			query.setText(resourcesManager.getString("ModelQuery.Plugin.qbt.query"));

			// query 按钮响应事件，在这里直接用语义查询
			query.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					// TODO get three parameters as follows
					// DataManager dm = DataManager.getInstance();
					long l=0l;
					s1 = queryCondition.getText();
					s2 = queryCondition1.getText();
					if (s1 == null || s2 == null) {
						return;
					}
					//Vector<ProcessObject> resultVector = new Vector<ProcessObject>();
					String[] newIdentifiers = { "Model ID", "Model Name", 
							  "View",
							"Run Time"};
					JTable table = resultTable;
					DefaultTableModel model = new DefaultTableModel();
					model.setColumnIdentifiers(newIdentifiers);

					try {
						DataManager dm = DataManager.getInstance();
						String strSelectPetriNet = "select PROCESS_ID, DEFINITIONMPN, DEFINITIONTPO from mcmillanIndex";						
						ResultSet rs = dm.executeSelectSQL(strSelectPetriNet);

						while (rs.next()) {
							long process_id = rs.getLong("PROCESS_ID");
							
							
							InputStream in = rs.getAsciiStream("DEFINITIONMPN");
							MyPetriNet myPetriNet = new MyPetriNet();
							Vector<MyPetriPlace> pp=new Vector<MyPetriPlace>();
							Vector<MyPetriTransition> tt=new Vector<MyPetriTransition>();
							Vector<MyPetriArc> aa=new Vector<MyPetriArc>();
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				            Document doc;
				            dbf.setValidating(false);
				            dbf.setIgnoringComments(true);
				            dbf.setIgnoringElementContentWhitespace(true);
                            doc = dbf.newDocumentBuilder().parse(in);
                            Element root = doc.getDocumentElement();
                            Element net=(Element) root.getFirstChild();
                            NodeList nodes1 = net.getChildNodes();
                    		ArrayList<Element> elements1 = new ArrayList<Element>();
                    		for (int i = 0; i < nodes1.getLength(); ++i) {
                    			Node child = nodes1.item(i);
                    			if (child instanceof Element)
                    				elements1.add((Element) nodes1.item(i));
                    		}            
                    		for(int i=0;i<elements1.size();i++){
                    			Element e=elements1.get(i);
                    			if(e.getTagName().equals("place")){
                    				String id=e.getAttribute("id");
                    				Element val=(Element) e.getFirstChild().getFirstChild();
                    				String value=val.getFirstChild().getTextContent();
                    				MyPetriPlace place=new MyPetriPlace(id,value);
                    				pp.add(place);
                    			}
                                if(e.getTagName().equals("transition")){
                                	String id=e.getAttribute("id");
                    				Element val=(Element) e.getFirstChild().getFirstChild();
                    				String value=val.getFirstChild().getTextContent();
                    				MyPetriTransition transition=new MyPetriTransition(id,value,null);
                    				tt.add(transition);
                    			}
                                if(e.getTagName().equals("arc")){
                                	String id=e.getAttribute("id");
                                	String source=e.getAttribute("source");
                                	String target=e.getAttribute("target");
                                	MyPetriArc arc=new MyPetriArc(id,source,target);
                                	aa.add(arc);
                    			}
                    		}
                    		myPetriNet.setPlaceSet(pp);
                    		myPetriNet.setTransitionSet(tt);
                    		myPetriNet.setArcSet(aa);
                    		ONCompleteFinitePrefix tpcfp=new ONCompleteFinitePrefix(myPetriNet);
							
							
                    		HashMap<ONEvent, HashMap<ONEvent, String>> tpo=
								new HashMap<ONEvent, HashMap<ONEvent, String>>();
							InputStream in2 = rs.getAsciiStream("DEFINITIONTPO");
							DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();
				            Document doc1;
				            dbf1.setValidating(false);
				            dbf1.setIgnoringComments(true);
				            dbf1.setIgnoringElementContentWhitespace(true);
                            doc1 = dbf1.newDocumentBuilder().parse(in2);
                            Element root1 = doc1.getDocumentElement();
                            Element temporalOrder=(Element) root1.getFirstChild();
                            NodeList nodes = temporalOrder.getChildNodes();
                    		ArrayList<Element> elements = new ArrayList<Element>();
                    		for (int i = 0; i < nodes.getLength(); ++i) {
                    			Node child = nodes.item(i);
                    			if (child instanceof Element)
                    				elements.add((Element) nodes.item(i));
                    		}                   		
                    		for(int i=0;i<elements.size();++i){       
                    			HashMap<ONEvent, String> map=new HashMap<ONEvent, String>();
                    			Element e=elements.get(i);
                    			String id=e.getAttribute("id");
                    			Element val=(Element) e.getFirstChild().getFirstChild();
                    			String value= val.getFirstChild().getTextContent();
                    			
                    			//MyPetriTransition p=new MyPetriTransition(id,value,null);
                    			MyPetriTransition p=new MyPetriTransition(id,value,null);
                    			ONEvent on = new ONEvent(id, p, tpcfp);
                    			
                    			NodeList cutTranLis=e.getChildNodes();
                    			ArrayList<Element> cutTranList = new ArrayList<Element>();
                    			for (int j = 1; j < cutTranLis.getLength(); ++j) {
                        			Node child = cutTranLis.item(j);
                        			if (child instanceof Element)
                        				cutTranList.add((Element) cutTranLis.item(j));
                        		}
                    			for(int j = 0; j < cutTranList.size(); ++j){
                    				Element ee=cutTranList.get(j);
                    				String cutTranId=ee.getAttribute("id");
                    				Element va=(Element) ee.getFirstChild().getFirstChild();
                    				String cutTranValue= va.getFirstChild().getTextContent();
                    				//String cutTranValue=v.getData();
                    				MyPetriTransition q=new MyPetriTransition(cutTranId,cutTranValue,null);
                    				ONEvent onn = new ONEvent(cutTranId, q, tpcfp);
                    				map.put(onn, "true");
                    			}   
                    			
                    			tpo.put(on, map);                    			
                    		}
                    		
							tpcfp.setMpn(myPetriNet);
							tpcfp.setTemporalOrder(tpo);
							
							Searching searching = new Searching();
							boolean b;
							Date start = Calendar.getInstance().getTime();
							b=searching.search(s1, s2, myPetriNet, tpcfp);
							Date stop = Calendar.getInstance().getTime(); 
							long time = stop.getTime() - start.getTime();
							
							
							if(b==true){
								model
								.addRow(new Object[] {
										process_id,
										"", "view",
										time + "ms" });
							}
							
							//String stime=Long.toString(time);
							//s3+=stime;
							l+=time;
							
							
						}
						rs.close();

						table.setModel(model);
						table.validate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					s3=Long.toString(l);
					totalTimeJTextField.setText(s3+"ms");
					
				}
			});
		}
		return query;
	}

} // @jve:decl-index=0:visual-constraint="10,10"

