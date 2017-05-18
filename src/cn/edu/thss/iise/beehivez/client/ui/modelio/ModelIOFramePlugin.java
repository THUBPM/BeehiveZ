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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.ui.FunctionFramePlugin;
import cn.edu.thss.iise.beehivez.client.ui.modelio.crawler.DlgCrawler;
import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.util.DataNode;
import cn.edu.thss.iise.beehivez.util.DatabaseModelTree;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * the IO of workflow module,provide the functions of import the model file to
 * DB and export the model file to local disk, support the PNML and XPML
 * 
 * @version 1.00 09/02/25
 * @author zhp Nianhua Wu JinTao
 * 
 */

public class ModelIOFramePlugin extends FunctionFramePlugin implements
		TreeModelListener {

	private static final long serialVersionUID = 1L;
	ResourcesManager resourcesManager = new ResourcesManager();

	JSplitPane splitPane = null;

	JScrollPane leftpanel = null;
	JScrollPane rightpanel = null;

	JTree dataTree = null; // the tree of DB resource files
	JTree fileTree = null; // the tree of local resource files

	JPopupMenu rightmenu = null; // the menu of the dataTree
	JMenuItem menuItemExport = null;
	JMenuItem menuItemExportAllYAWL = null;
	JMenuItem menuItemExportAllPNML = null;
	JMenuItem menuItemRightrefresh = null;
	JMenuItem menuItemLeftrefresh = null;
	JMenuItem menuItemAddCatalog = null;
	JMenuItem menuItemInput = null;
	JMenuItem menuItemRename = null;
	JMenuItem menuItemDelete = null;
	JMenuItem menuItemBatchGenerate2DB = null;
	JMenuItem menuItemGenerateModel2File = null;
	JMenuItem menuItemQueryTest = null;
	JMenuItem menuItemBatchQueryModelGenerate = null;
	JMenuItem menuItemDeleteAllProcess = null;
	JMenuItem menuItemCrawl = null;
	JMenuItem menuItemViewModel = null;
	JMenuItem menuItemModelBatchImport = null;
	JPopupMenu leftmenu = null; // the menu of the fileTree

	DataNode rightselectNode = null; // the select node of the DataTree
	TreePath selectPath = null; // the select path of the DataTree
	DefaultTreeModel model = null;

	// the follow are the implements of the methods in frameplugin

	public String getName() {
		return resourcesManager.getString("ProcessIOFramePlugin.name");
	}

	public String getToolTip() {
		return resourcesManager.getString("ProcessIOFramePlugin.tooltip");
	}

	public Icon getIcon() {
		String path = "/resources/images/Icon_ModelIO.gif";
		String description = "Icon_ModelIO";
		return new ImageIcon(getClass().getResource(path), description);
	}

	/**
	 * ProcessIOFramePlugin construct method
	 * 
	 * @throws Exception
	 */
	public ModelIOFramePlugin(ClientFrame mainframe) {

		super(mainframe);

		// create leftpanel
		leftpanel = Create_leftpanel();

		// create rightpanel
		rightpanel = Create_rightpanel();

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(fileTree,
				DnDConstants.ACTION_MOVE, new DragAndDropGestureListener());
		@SuppressWarnings("unused")
		DropTarget dropTarget = new DropTarget(dataTree,
				new DragAndDropTargetListener());

		// set the selections of splitPane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpanel,
				rightpanel);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		// set the split ratio
		splitPane.setDividerLocation(0.7);

		// add the left and right panels
		getModulePanel().add(splitPane, BorderLayout.CENTER);
		getModulePanel().setBackground(Color.black);

		model = (DefaultTreeModel) dataTree.getModel();
		model.addTreeModelListener(this);

	}

	/**
	 * overide the method in MainFrame to update the Tree
	 */
	public void onLoad() {
		freshTree();
	}

	public void freshTree() {
		DatabaseModelTree dbmt = new DatabaseModelTree();
		model = (DefaultTreeModel) dbmt.createDbmTree().getModel();
		dataTree.setModel(model);
		model.addTreeModelListener(this);
	}

	/**
	 * refresh the fileTree to update the
	 */

	/**
	 * create the menu of dataTree
	 */
	protected void Create_leftmenu() {
		leftmenu = new JPopupMenu();
		menuItemLeftrefresh = new JMenuItem(resourcesManager
				.getString("ProcessIOFramePlugin.refresh"));
		menuItemInput = new JMenuItem(resourcesManager
				.getString("ProcessIOFramePlugin.import"));
		leftmenu.add(menuItemLeftrefresh);
		leftmenu.add(menuItemInput);
		// update the show of the tree
		menuItemLeftrefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileTree.setModel(new FileSystemModel(new FileNode()));
			}
		});

		menuItemInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * initialize the leftpanel
	 */
	protected JScrollPane Create_leftpanel() {
		fileTree = new JTree();
		fileTree.setModel(new FileSystemModel(new FileNode()));
		fileTree.setCellRenderer(new FileRender());

		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
			}
		});
		fileTree.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					Create_leftmenu();
					menuItemInput.setVisible(false);
					TreePath path = fileTree.getPathForLocation(e.getX(), e
							.getY());
					if (path != null) {
						fileTree.setSelectionPath(path);
						FileNode leftselectNode = (FileNode) fileTree
								.getLastSelectedPathComponent();
						if (leftselectNode.toString().toLowerCase().endsWith(
								".pnml")
								|| leftselectNode.toString().toLowerCase()
										.endsWith(".yawl")) {
							selectPath = path;
							menuItemInput.setVisible(true);
						}
					}
					leftmenu.show(fileTree, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

		});
		fileTree.setSelectionRow(0);
		return new JScrollPane(fileTree);

	}

	/**
	 * create the rightmenu
	 */
	protected void Create_rightmenu() {
		rightmenu = new JPopupMenu();
		menuItemRightrefresh = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.refresh"));
		menuItemAddCatalog = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.makenewdir"));
		menuItemRename = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.rename"));
		menuItemDelete = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.delete"));
		menuItemCrawl = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.crawl"));
		menuItemBatchGenerate2DB = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.generateModels2DB"));
		menuItemGenerateModel2File = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.generateModels2Files"));
		menuItemBatchQueryModelGenerate = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.generateQueryModels"));
		menuItemExport = new JMenuItem(resourcesManager
				.getString("ProcessIOFramePlugin.export"));
		menuItemExportAllYAWL = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.exportAllYAWLModels"));
		menuItemExportAllPNML = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.exportAllPNMLModels"));
		menuItemDeleteAllProcess = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.deleteAllModels"));
		menuItemViewModel = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.showModel"));
		menuItemQueryTest = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.queryTest"));
		menuItemModelBatchImport = new JMenuItem(resourcesManager.getString("ProcessIOFramePlugin.rightmenu.modelBatchImport"));

		rightmenu.add(menuItemAddCatalog);
		rightmenu.addSeparator();

		rightmenu.add(menuItemBatchGenerate2DB);
		rightmenu.add(menuItemGenerateModel2File);
		rightmenu.add(menuItemBatchQueryModelGenerate);
		rightmenu.add(menuItemCrawl);
		rightmenu.add(menuItemModelBatchImport);
		rightmenu.addSeparator();

		rightmenu.add(menuItemExport);
		rightmenu.add(menuItemExportAllYAWL);
		rightmenu.add(menuItemExportAllPNML);
		rightmenu.addSeparator();

		rightmenu.add(menuItemQueryTest);
		rightmenu.add(menuItemViewModel);
		rightmenu.addSeparator();

		rightmenu.add(menuItemDelete);
		rightmenu.add(menuItemDeleteAllProcess);
		rightmenu.addSeparator();

		rightmenu.add(menuItemRightrefresh);
		rightmenu.addSeparator();

		menuItemModelBatchImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DlgModelBatchImport dlg = new DlgModelBatchImport(null);
				dlg.setVisible(true);
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}

		});

		menuItemQueryTest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DlgQueryTest dlg = new DlgQueryTest(null);
				dlg.setVisible(true);
			}

		});

		menuItemViewModel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				long process_id = rightselectNode.getProcess_id();
				if (process_id < 0)
					return;

				DataManager dm = DataManager.getInstance();
				String modelType = ProcessObject.TYPEPNML;
				InputStream pnml = dm.getProcessPnml(process_id);
				VisualFrame visualframe = new VisualFrame(modelType, pnml);
				visualframe.setVisible(true);
				try {
					pnml.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		menuItemDeleteAllProcess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataManager dm = DataManager.getInstance();
				dm.delAllProcess();
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}
		});

		// crawl business process model from internet
		menuItemCrawl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DlgCrawler crawler = new DlgCrawler(null);
				crawler.show();
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}

		});

		menuItemRightrefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				freshTree();
			}
		});
		// export the DBfile to local disk
		menuItemExport.addActionListener(new ActionListener() {

			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				JValidateFileChooser fileChooser = new JValidateFileChooser(
						GlobalParameter.getQueryObjectPath());
				fileChooser
						.setSelectedFile(new File(rightselectNode.toString()));
				ExtensionFilter filter1 = new ExtensionFilter(".pnml",
						"PNML files (*.pnml)");
				ExtensionFilter filter2 = new ExtensionFilter(".xpdl",
						"XPDL files (*.xpdl)");
				ExtensionFilter filter3 = new ExtensionFilter(".xpdl",
						"XPDL files (*.xml)");
				fileChooser.setDialogTitle(resourcesManager.getString("DlgExportFile.title"));

				if (rightselectNode.getProcess_id() > 0) {
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					fileChooser.rescanCurrentDirectory();
					fileChooser.addChoosableFileFilter(filter1);
					fileChooser.addChoosableFileFilter(filter2);
					fileChooser.addChoosableFileFilter(filter3);
					fileChooser.setFileFilter(filter1);
				} else {
					fileChooser
							.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
					fileChooser.rescanCurrentDirectory();
				}
				int choose = fileChooser.showSaveDialog(splitPane);
				String savePath = null;
				DataManager dm = DataManager.getInstance();
				if (choose == JFileChooser.APPROVE_OPTION) {
					if (rightselectNode.getProcess_id() > 0) {
						savePath = fileChooser.getSelectedFile()
								.getAbsolutePath();
						try {
							dm.exportProcessToFile(rightselectNode
									.getProcess_id(), savePath);
							fileTree.setModel(new FileSystemModel(
									new FileNode()));
							JOptionPane.showMessageDialog(splitPane,
									"export to file successfully!");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else {
						savePath = fileChooser.getSelectedFile()
								.getAbsolutePath();
						File savefolder = new File(savePath);
						try {
							int count = rightselectNode.getChildCount();
							for (int i = 0; i < count; i++) {
								DataNode node = (DataNode) rightselectNode
										.getChildAt(i);
								if (node.getProcess_id() > 0) {
									String filename = savePath + "\\"
											+ node.getLabel() + ".xml";
									dm.exportProcessToFile(
											node.getProcess_id(), filename);
								}
							}
							JOptionPane.showMessageDialog(splitPane,
									"export to file successfully!");

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		menuItemExportAllYAWL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JValidateFileChooser fileChooser = new JValidateFileChooser(
						GlobalParameter.getQueryObjectPath());
				fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
				fileChooser.rescanCurrentDirectory();
				int choose = fileChooser.showSaveDialog(splitPane);
				String savePath = null;
				DataManager dm = DataManager.getInstance();
				if (choose == JFileChooser.APPROVE_OPTION) {
					savePath = fileChooser.getSelectedFile().getAbsolutePath();
					dm.exportAllYAWLModels(savePath);
					JOptionPane.showMessageDialog(splitPane,
							"export to file successfully!");
				}
			}

		});

		menuItemExportAllPNML.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JValidateFileChooser fileChooser = new JValidateFileChooser(
						GlobalParameter.getQueryObjectPath());
				fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
				fileChooser.rescanCurrentDirectory();
				int choose = fileChooser.showSaveDialog(splitPane);
				String savePath = null;
				DataManager dm = DataManager.getInstance();
				if (choose == JFileChooser.APPROVE_OPTION) {
					savePath = fileChooser.getSelectedFile().getAbsolutePath();
					dm.exportAllPNMLModels(savePath);
					JOptionPane.showMessageDialog(splitPane,
							"export to file successfully!");
				}
			}

		});

		/**
		 * make new catalog
		 */
		menuItemAddCatalog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DataNode node = new DataNode("make new catalog",
						rightselectNode.getCatalog_id());
				String name = node.getLabel();
				long parent_id = node.getParent_id();
				DataManager dm = DataManager.getInstance();
				long catalog_id = dm.addProcessCatalog(parent_id, name);
				node.setCatalog_id(catalog_id);
				DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
				model.insertNodeInto(node, rightselectNode, rightselectNode
						.getChildCount());
				dataTree.expandPath(dataTree.getSelectionPath());
				freshTree();
			}
		});

		/**
		 * delete node
		 */
		menuItemDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int selected = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete this node?",
						"Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
				if (selected != JOptionPane.YES_OPTION) {
					return;
				}
				if (rightselectNode != null) {
					long process_id = rightselectNode.getProcess_id();
					DataManager dm = DataManager.getInstance();
					if (process_id > 0) {
						dm.delProcess(process_id);
						DefaultTreeModel model = (DefaultTreeModel) dataTree
								.getModel();
						model.removeNodeFromParent(rightselectNode);
						// dataTree.validate();
					} else {
						Enumeration count = rightselectNode.children();
						while (count.hasMoreElements()) {
							DataNode child = (DataNode) count.nextElement();
							if (child != null && child.getProcess_id() > 0) {
								dm.delProcess(child.getProcess_id());
								DefaultTreeModel model = (DefaultTreeModel) dataTree
										.getModel();
								model.removeNodeFromParent(child);
							}

						}
					}
				}
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}

		});
		menuItemBatchGenerate2DB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DlgModelBatchGenerator2DB batchGen = new DlgModelBatchGenerator2DB(
						null);
				batchGen.setVisible(true);
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}

		});

		menuItemGenerateModel2File.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DlgModelGenerating2File dlg = new DlgModelGenerating2File(null);
				dlg.setVisible(true);
			}

		});

		menuItemBatchQueryModelGenerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DlgQueryModelGenerator dlg = new DlgQueryModelGenerator(null);
				dlg.setVisible(true);
				ClientFrame.getInstance().refreshStatus();
				freshTree();
			}

		});
	}

	/**
	 * initialize the rightpanel
	 */
	protected JScrollPane Create_rightpanel() {
		DatabaseModelTree dbmt = new DatabaseModelTree();
		dataTree = dbmt.createDbmTree();
		dataTree.setEditable(true);

		dataTree.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath path = dataTree.getPathForLocation(e.getX(), e
							.getY());
					if (path == null)
						return;
					dataTree.setSelectionPath(path);
					rightselectNode = (DataNode) path.getLastPathComponent();
					long process_id = rightselectNode.getProcess_id();
					if (process_id < 0)
						return;

					DataManager dm = DataManager.getInstance();
					String modelType = dm.getProcessType(process_id);
					InputStream modelDefinition = dm
							.getProcessDefinitionInputStream(process_id);
					VisualFrame visualframe = new VisualFrame(modelType,
							modelDefinition);
					visualframe.setVisible(true);
					try {
						modelDefinition.close();
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

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					TreePath path = dataTree.getPathForLocation(e.getX(), e
							.getY());
					Create_rightmenu();

					if (path == null) {
						return;
					} else {
						dataTree.setSelectionPath(path);
						rightselectNode = (DataNode) path
								.getLastPathComponent();
						// configure the right click menu
						if (rightselectNode.getProcess_id() < 0) // catalog
						{
						}
						if (rightselectNode.getCatalog_id() < 0)// process model
						{
							menuItemAddCatalog.setVisible(false);
							menuItemBatchGenerate2DB.setVisible(false);
							menuItemBatchQueryModelGenerate.setVisible(false);
							menuItemDeleteAllProcess.setVisible(false);
						}
					}
					rightmenu.show(rightpanel, e.getX(), e.getY());
				}
			}
		});
		return new JScrollPane(dataTree);
	}

	/**
	 * the fileFilter , make the file chooser only show the right files
	 */
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
	 * fileChooser with file Verification
	 */
	@SuppressWarnings("serial")
	class JValidateFileChooser extends JFileChooser {

		public JValidateFileChooser() {
			super();
		}

		public JValidateFileChooser(File currentDirectory) {
			super(currentDirectory);
		}

		public JValidateFileChooser(File currentDirectory, FileSystemView fsv) {
			super(currentDirectory, fsv);
		}

		public JValidateFileChooser(FileSystemView fsv) {
			super(fsv);
		}

		public JValidateFileChooser(String currentDirectoryPath) {
			super(currentDirectoryPath);
		}

		public JValidateFileChooser(String currentDirectoryPath,
				FileSystemView fsv) {
			super(currentDirectoryPath, fsv);
		}

		/**
		 * the override the approveSelection method,support the validate
		 */
		@Override
		public void approveSelection() {
			File file = getSelectedFile();

			if (!validateFileName(file.getName())) {
				JOptionPane.showMessageDialog(getParent(), resourcesManager
						.getString("ProcessIOFramePlugin.invalidanme"),
						"WARNING", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(getParent(), resourcesManager
						.getString("ProcessIOFramePlugin.fileexist"),
						"QUESTION", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return;
				else {
					super.approveSelection();
				}
			}
			super.approveSelection();
		}

		/*
		 * validate the validity of the file name
		 */
		private boolean validateFileName(String name) {
			if (name.indexOf('/') != -1 || name.indexOf('/') != -1
					|| name.indexOf(':') != -1 || name.indexOf('*') != -1
					|| name.indexOf('?') != -1 || name.indexOf('"') != -1
					|| name.indexOf('<') != -1 || name.indexOf('>') != -1
					|| name.indexOf('|') != -1) {

				return false;
			} else {
				return true;
			}
		}
	}

	/*
	 * rename
	 * 
	 * @see
	 * javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event
	 * .TreeModelEvent)
	 */
	public void treeNodesChanged(TreeModelEvent e) {

		DataNode node = (DataNode) e.getTreePath().getLastPathComponent();
		int[] index = e.getChildIndices();
		node = (DataNode) node.getChildAt(index[0]);
		String oldname = node.getLabel();
		String newname = node.getUserObject().toString();
		int index1 = oldname.lastIndexOf(".");
		int index2 = newname.lastIndexOf(".");
		if (index1 != -1 && index2 != -1) {
			String extension1 = oldname.substring(index1 + 1);
			String extension2 = newname.substring(index2 + 1);
			if (!extension1.equals(extension2)) {
				int selected = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to give a new suffix name",
						"confirm", JOptionPane.YES_NO_OPTION);
				if (selected != JOptionPane.YES_OPTION) {
					newname = newname.substring(0, index2 + 1) + extension1;
				}
			}
		} else if (index1 != -1 && index2 == -1) {
			newname = newname + oldname.substring(index1);
		}
		DataManager dm = DataManager.getInstance();

		if (node.getProcess_id() <= 0) {
			long catalog_id = node.getCatalog_id();
			dm.updateProcessCatalogName(catalog_id, newname);
		} else {
			long process_id = node.getProcess_id();
			dm.updateProcessName(process_id, newname);
		}
		node.setLabel(newname);
	}

	public void treeNodesInserted(TreeModelEvent e) {
	}

	public void treeNodesRemoved(TreeModelEvent e) {
	}

	public void treeStructureChanged(TreeModelEvent e) {
	}
}
