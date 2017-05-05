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
 * @author Nianhua Wu
 * the extends TreeModel class,implements the interface of TreeModel
 *
 */

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FileSystemModel implements TreeModel {
	FileNode theRoot;

	char fileType = FileNode.DIRECTORY;

	public FileSystemModel(FileNode fs) {
		theRoot = fs;
	}

	public Object getRoot() {
		return theRoot;
	}

	public Object getChild(Object parent, int index) {
		return ((FileNode) parent).getChild(fileType, index);
	}

	public int getChildCount(Object parent) {
		return ((FileNode) parent).getChildCount(fileType);
	}

	public boolean isLeaf(Object parent) {
		return ((FileNode) parent).isLeaf(fileType);
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((FileNode) parent).getIndexOfChild(fileType, child);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void addTreeModelListener(TreeModelListener l) {
	}

	public void removeTreeModelListener(TreeModelListener l) {
	}

}
