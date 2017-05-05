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

/**
 * 
 * @author nianhualeaf
 * DragAndDropTargetListener class implements the interface of DropTargetListener
 *
 */

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.edu.thss.iise.beehivez.util.DataNode;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

public class DragAndDropTargetListener implements DropTargetListener {

	ResourcesManager resourcesManager = new ResourcesManager();

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent e) {
		DropTarget dropTarget = (DropTarget) e.getSource();
		Component comp = dropTarget.getComponent();
		JTree tree = (JTree) comp;
		TreePath treePath = tree.getPathForLocation(e.getLocation().x, e
				.getLocation().y);
		if (treePath != null) {
			tree.setSelectionPath(treePath);
			DataNode treeNode = (DataNode) treePath.getLastPathComponent();
			((DefaultTreeModel) tree.getModel()).reload(treeNode);
		}
	}

	/*
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent e) {
		Transferable transfer = e.getTransferable();
		FileNode dragSource = null;
		if (transfer
				.isDataFlavorSupported(DragAndDropTransferable.TREENODE_FLAVOR)) {
			try {
				e.acceptDrop(DnDConstants.ACTION_MOVE);
				dragSource = (FileNode) transfer
						.getTransferData(DragAndDropTransferable.TREENODE_FLAVOR);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (dragSource == null) {
			e.dropComplete(false);
			return;
		}
		DropTarget dropTarget = (DropTarget) e.getSource();
		Component comp = dropTarget.getComponent();
		// if the drop container is not tree
		if (!(comp instanceof JTree)) {
			e.dropComplete(false);
			return;
		}
		JTree tree = (JTree) comp;
		TreePath treePath = tree.getPathForLocation(e.getLocation().x, e
				.getLocation().y);
		// if the drop resource is null
		if (treePath == null) {
			JOptionPane.showMessageDialog(tree.getParent().getParent(),
					"error path!");
			e.dropComplete(false);
			return;
		}
		DataNode treeNode = (DataNode) treePath.getLastPathComponent();
		long catalog_id = treeNode.getCatalog_id();
		if (!isCanDrop(dragSource, treeNode, tree)) {

			e.dropComplete(false);
			return;
		}
		if (catalog_id < 0) {
			JOptionPane
					.showMessageDialog(
							tree.getParent().getParent(),
							resourcesManager
									.getString("ProcessExplorerFramePlugin.dragAnddrop.notcatalog"));
			e.dropComplete(false);
			return;
		}

		String path = dragSource.path;
		String name = dragSource.toString();
		if (fileAccept(name)) {
			ModelImportDialog importDialog = new ModelImportDialog(name, path,
					catalog_id, tree, treeNode);
			importDialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(tree.getParent().getParent(),
					"error file type!");

		}

		e.dropComplete(true);

	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	/*
	 * judge can drop or not
	 */
	public boolean isCanDrop(FileNode dragNode, DataNode dropNode, JTree tree) {

		if (dragNode == null)
			return false;
		if (dropNode == null)
			return false;
		return true;
	}

	public boolean fileAccept(String filename) {
		if (filename.endsWith(".xpdl") || filename.endsWith(".xml")
				|| filename.endsWith(".pnml")|| filename.endsWith(".yawl")
				|| (!filename.contains(".")))
			return true;
		else
			return false;

	}

}
