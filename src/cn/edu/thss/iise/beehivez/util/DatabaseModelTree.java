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

package cn.edu.thss.iise.beehivez.util;

import java.awt.Component;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;

/**
 * create model tree according to the database refer to the processcatalog table
 * and process table
 * 
 * @author He Tengfei
 * 
 *         edited by JinTao 2009.9.7
 * 
 */
public class DatabaseModelTree {
	JTree tree;
	DataNode rootNode;
	ResourcesManager rm;

	DataNode parentNode = null;

	public DatabaseModelTree() {

		rm = new ResourcesManager();
	}

	public JTree createDbmTree() {
		DataManager dm = null;

		long catalog_id;
		String label;
		long parent_id;
		long process_id;
		String name;
		String type;
		DataNode treenode;

		try {
			dm = DataManager.getInstance();
			// construct the DB model tree
			Vector<ProcesscatalogObject> vpco = dm.getAllProcessCatalog();
			for (int i = 0; i < vpco.size(); i++) {
				ProcesscatalogObject pco = vpco.get(i);
				catalog_id = pco.getCatalog_id();
				label = pco.getName();
				parent_id = pco.getParent_id();
				treenode = new DataNode(label, parent_id, catalog_id);
				// catalog_id == 1 means the root,then create the tree
				if (catalog_id == 1) {
					tree = new JTree(treenode);
					tree.setCellRenderer(new DefaultTreeCellRenderer() {

						public Component getTreeCellRendererComponent(
								JTree tree, Object value, boolean sel,
								boolean expanded, boolean leaf, int row,
								boolean hasFocus) {
							// TODO Auto-generated method stub
							super.getTreeCellRendererComponent(tree, value,
									sel, expanded, leaf, row, hasFocus);

							DataNode tempNode = (DataNode) value;

							if (leaf && tempNode.getProcess_id() < 0) {
								setIcon(new ImageIcon(getClass().getResource(
										"/resources/icons/FOLDER_CLOSE.GIF")));
							}

							else if (tempNode.getProcess_id() > 0
									&& (tempNode.getType()
											.equalsIgnoreCase(ProcessObject.TYPEPNML))) {
								setIcon(new ImageIcon(getClass().getResource(
										"/resources/icons/PNML.GIF")));
							} else if (tempNode.getProcess_id() > 0
									&& (tempNode.getType()
											.equalsIgnoreCase(ProcessObject.TYPEYAWL))) {
								setIcon(new ImageIcon(getClass().getResource(
										"/resources/icons/YAWL.GIF")));
							}
							return this;
						}

					});
					DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree
							.getCellRenderer();
					renderer.setOpenIcon(new ImageIcon(getClass().getResource(
							"/resources/icons/FOLDER_OPEN.GIF")));
					renderer.setClosedIcon(new ImageIcon(getClass()
							.getResource("/resources/icons/FOLDER_CLOSE.GIF")));
					rootNode = treenode;
				} else {
					parentNode = null;
					getNodebyId(rootNode, parent_id);
					if (parentNode == null) {
						System.out
								.println(rm
										.getString("ProcessExplorerFramePlugin.exceptcreatetree"));

					} else {
						parentNode.add(treenode);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// add all the model node
			Vector<ProcessObject> vpo = dm.getAllProcess();
			for (int i = 0; i < vpo.size(); i++) {
				ProcessObject po = vpo.get(i);
				parent_id = po.getCatalog_id();
				process_id = po.getProcess_id();
				name = po.getName();
				type = po.getType();
				// create node
				treenode = new DataNode(name, parent_id, process_id, type);

				// find the parent node,then add to the tree
				parentNode = null;
				getNodebyId(rootNode, parent_id);
				if (parentNode == null) {
					System.out
							.println(rm
									.getString("ProcessExplorerFramePlugin.exceptcreatetree"));

				} else {

					parentNode.add(treenode);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tree;
	}

	// get the node by catalog_id
	public void getNodebyId(DataNode rootNode, long catalog_id) {
		if (rootNode != null) {
			if (rootNode.getCatalog_id() == catalog_id
					&& rootNode.getProcess_id() < 0) {
				parentNode = rootNode;
			}
			Enumeration children = rootNode.children();
			while (children.hasMoreElements()) {
				DataNode child = (DataNode) children.nextElement();
				getNodebyId(child, catalog_id);
			}
		}
	}
}
