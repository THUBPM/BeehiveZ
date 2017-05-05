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
package cn.edu.thss.iise.beehivez.client.ui.modelio;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.util.DataNode;
import cn.edu.thss.iise.beehivez.util.DatabaseModelTree;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * ģ���ļ��ĵ���Ի���
 * 
 * @author ���껪 ���ڷ� edited by JinTao 2009.9.6
 * 
 */
public class ModelImportDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel modelInfo = null;
	private JLabel nameinfo = null;
	private JTextField modelName = null;
	private JLabel descriptioninfo = null;
	private JTextArea modelDescription = null;
	private JLabel typeinfo = null;
	private JComboBox modelType = null;
	private JButton Import = null;
	private JButton Cancel = null;
	private JLabel pathinfo = null;
	private JTextField modelPath = null;
	private JPanel panel = null;

	private long catalog_id;

	private JTree tree;
	@SuppressWarnings("unused")
	private DataNode treeNode;
	
	private ResourcesManager resourcesManager = new ResourcesManager();

	/**
	 * This is the default constructor
	 */
	public ModelImportDialog(String fileName, String path, long catalog_id,
			JTree tree, DataNode treeNode) {
		super();
		initialize();
		getModelName().setText(fileName);
		getModelPath().setText(path);
		if (fileName.toLowerCase().endsWith(ProcessObject.TYPEPNML)) {
			getModelType().setSelectedItem(ProcessObject.TYPEPNML);
		} else if (fileName.toLowerCase().endsWith(ProcessObject.TYPEYAWL)) {
			getModelType().setSelectedItem(ProcessObject.TYPEYAWL);
		} else {
			getModelType().setSelectedItem("Folder");

		}
		this.catalog_id = catalog_id;
		this.tree = tree;
		this.treeNode = treeNode;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setBounds(100, 50, 600, 450);
		this.setTitle(resourcesManager.getString("ModelImportDialog.title"));
		pathinfo = new JLabel();
		pathinfo.setBounds(new Rectangle(80, 300, 140, 30));
		pathinfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		pathinfo.setText(resourcesManager.getString("ModelImportDialog.modelpath"));
		pathinfo.setFont(new Font("Dialog", Font.BOLD, 14));
		typeinfo = new JLabel();
		typeinfo.setBounds(new Rectangle(80, 250, 140, 30));
		typeinfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		typeinfo.setText(resourcesManager.getString("ModelImportDialog.modeltype"));
		typeinfo.setFont(new Font("Dialog", Font.BOLD, 14));
		descriptioninfo = new JLabel();
		descriptioninfo.setBounds(new Rectangle(80, 100, 140, 30));
		descriptioninfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		descriptioninfo.setText(resourcesManager.getString("ModelImportDialog.modeldes"));
		descriptioninfo.setFont(new Font("Dialog", Font.BOLD, 14));
		nameinfo = new JLabel();
		nameinfo.setBounds(new Rectangle(80, 50, 140, 30));
		nameinfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		nameinfo.setFont(new Font("Dialog", Font.BOLD, 14));
		nameinfo.setText(resourcesManager.getString("ModelImportDialog.modelname"));
		modelInfo = new JLabel();
		modelInfo.setText(resourcesManager.getString("ModelImportDialog.modelinfo"));
		modelInfo.setFont(new Font("Dialog", Font.BOLD, 14));
		modelInfo.setBounds(new Rectangle(50, 10, 140, 30));
		panel = new JPanel();
		panel.setBounds(0, 0, 600, 400);
		panel.setLayout(null);
		panel.add(modelInfo, null);
		panel.add(nameinfo, null);
		panel.add(getModelName(), null);
		panel.add(descriptioninfo, null);
		panel.add(getModelDescription(), null);
		panel.add(typeinfo, null);
		panel.add(getModelType(), null);
		panel.add(getImport(), null);
		panel.add(getCancel(), null);
		panel.add(pathinfo, null);
		panel.add(getModelPath(), null);
		this.add(panel);
		this.setModal(true);
	}

	/**
	 * This method initializes ModelName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getModelName() {
		if (modelName == null) {
			modelName = new JTextField();
			modelName.setBounds(new Rectangle(220, 50, 250, 30));
			modelName.setFont(new Font("Dialog", Font.BOLD, 14));
		}
		return modelName;
	}

	/**
	 * This method initializes ModelDescription
	 * 
	 * @return javax.swing.JList
	 */
	private JTextArea getModelDescription() {
		if (modelDescription == null) {
			modelDescription = new JTextArea();
			modelDescription = new JTextArea();
			modelDescription.setBounds(new Rectangle(220, 100, 250, 120));
			modelDescription.setFont(new Font("Dialog", Font.PLAIN, 14));
		}
		return modelDescription;
	}

	/**
	 * This method initializes ModelType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getModelType() {
		if (modelType == null) {
			Vector<String> types = new Vector<String>();
			types.add(ProcessObject.TYPEPNML);
			types.add(ProcessObject.TYPEYAWL);
			types.add(resourcesManager.getString("ModelImportDialog.folder"));
			modelType = new JComboBox(types);
			modelType.setBounds(new Rectangle(220, 250, 250, 30));
			modelType.setFont(new Font("Dialog", Font.BOLD, 14));
		}
		return modelType;
	}

	/**
	 * This method initializes Import
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getImport() {
		if (Import == null) {
			Import = new JButton();
			Import.setBounds(new Rectangle(210, 350, 80, 30));
			Import.setFont(new Font("Dialog", Font.BOLD, 14));
			Import.setText(resourcesManager.getString("ModelImportDialog.import"));
			Import.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String name = modelName.getText();
					String desc = modelDescription.getText();
					String type = modelType.getSelectedItem().toString().trim();
					String filePath = modelPath.getText();
					File modelfile = new File(filePath);
					DataManager dm = DataManager.getInstance();
					// type is pnml, check the content
					if (type.toLowerCase().endsWith("pnml")
							&& isPNML(modelfile)) {
						if (isPNML(modelfile)) {
							try {
								FileInputStream fis = new FileInputStream(
										modelfile);
								byte[] temp = ToolKit
										.getBytesFromInputStream(fis);
								fis.close();
								dm.addProces(name, desc, type.toLowerCase(),
										catalog_id, temp);
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						} else {
							JOptionPane.showConfirmDialog(null,
									"File content is not consistent with file type,"
											+ "import failed!");
							dispose();
						}
					}
					// type is folder
					else if (type.toLowerCase().endsWith("folder")) {
						importFolder(modelfile);

					} else {
						System.out.println("unsupported now");
					}

					DatabaseModelTree dbmt = new DatabaseModelTree();
					tree.setModel(dbmt.createDbmTree().getModel());
					tree.validate();
					ClientFrame frame = ClientFrame.getInstance();
					frame.refreshStatus();
					dispose();
				}

			});
		}
		return Import;
	}

	/**
	 * This method initializes Cancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancel() {
		if (Cancel == null) {
			Cancel = new JButton();
			Cancel.setBounds(new Rectangle(380, 350, 80, 30));
			Cancel.setFont(new Font("Dialog", Font.BOLD, 14));
			Cancel.setText(resourcesManager.getString("ModelImportDialog.cancel"));
			Cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
				}
			});
		}
		return Cancel;
	}

	/**
	 * This method initializes ModelPath
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getModelPath() {
		if (modelPath == null) {
			modelPath = new JTextField();
			modelPath.setBounds(new Rectangle(222, 300, 250, 30));
			modelPath.setEnabled(false);
			modelPath.setFont(new Font("Dialog", Font.PLAIN, 14));
		}
		return modelPath;
	}

	public long getCatalog_id() {
		return catalog_id;
	}

	public void setCatalog_id(long catalog_id) {
		this.catalog_id = catalog_id;
	}

	public boolean isPNML(File f) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(f);
			Element root = doc.getRootElement();
			String rootInfo = root.getName().toLowerCase();
			if (rootInfo.equals("pnml")) {
				return true;
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void importFolder(File modelfolder) {
		File[] filelist = modelfolder.listFiles();
		DataManager dm = DataManager.getInstance();
		for (int i = 0; i < filelist.length; i++) {
			File modelfile = filelist[i];
			String filename = modelfile.getName();
			if (filename.toLowerCase().endsWith(ProcessObject.TYPEPNML)) {
				if (isPNML(modelfile)) {
					dm.addProces(modelfile.getName(), "", "pnml", catalog_id,
							PetriNetUtil.getPnmlBytesFromFile(modelfile
									.getAbsolutePath()));
				}
			}
			if (modelfile.getName().toLowerCase().endsWith(
					ProcessObject.TYPEYAWL)) {
				// TODO:
			}
			if (modelfile.isDirectory()) {
				importFolder(modelfile);
			}
		}

	}

} // @jve:decl-index=0:visual-constraint="-150,-39"
