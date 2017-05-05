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

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * the implement of DragGestureListener
 * 
 * @author nianhua Wu
 * 
 */

public class DragAndDropGestureListener implements DragGestureListener {

	public void dragGestureRecognized(DragGestureEvent dge) {
		// TODO Auto-generated method stub
		JTree tree = (JTree) dge.getComponent();
		TreePath path = tree.getSelectionPath();
		FileNode dragNode = null;
		if (path != null) {
			dragNode = (FileNode) path.getLastPathComponent();
		}
		if (dragNode != null) {
			DragAndDropTransferable transfer = new DragAndDropTransferable(
					dragNode);

			dge.startDrag(DragSource.DefaultMoveDrop, transfer,
					new DragAndDropSourceListener());
		}
	}
}
