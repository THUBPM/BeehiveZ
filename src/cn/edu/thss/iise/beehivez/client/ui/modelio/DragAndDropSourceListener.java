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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
/**
 * @author Nianhua Wu
 * 
 */
public class DragAndDropSourceListener implements DragSourceListener {

	public void dragDropEnd(DragSourceDropEvent e) {
		// TODO Auto-generated method stub
		if (!e.getDropSuccess()
				|| e.getDropAction() != DnDConstants.ACTION_MOVE) {
			return;
		}
		DragSourceContext context = e.getDragSourceContext();
		Object comp = context.getComponent();
		if (comp == null || !(comp instanceof JTree))
			return;

	}

	public void dragEnter(DragSourceDragEvent e) {
		DragSourceContext context = e.getDragSourceContext();
		int dropAction = e.getDropAction();
		if ((dropAction & DnDConstants.ACTION_COPY) != 0) {
			context.setCursor(DragSource.DefaultCopyDrop);
		} else if ((dropAction & DnDConstants.ACTION_MOVE) != 0) {
			context.setCursor(DragSource.DefaultMoveDrop);
		} else {
			context.setCursor(DragSource.DefaultMoveNoDrop);
		}
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}
}