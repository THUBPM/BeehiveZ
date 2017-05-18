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
 * the node type of local resource Tree
 *
 */

import java.io.File;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

public class FileNode {

	private static FileSystemView fsView;

	@SuppressWarnings("unused")
	private static boolean showHiden = true;
	// current file
	public File theFile;
	// the file's absolutely path
	public String path;
	// file type
	final public static char DIRECTORY = 'D';
	final public static char FILE = 'F';

	// all the filelist
	private Vector<File> folder = new Vector<File>();

	/**
	 * set that whether apply hiden files
	 * 
	 * @param ifshow
	 */
	public void SetShowHiden(boolean ifshow) {
		showHiden = ifshow;
	}

	/**
	 * get system icon
	 * 
	 * @return
	 */
	public Icon getIcon() {
		return fsView.getSystemIcon(theFile);
	}

	/**
	 * overrides Object.toString()method
	 */
	public String toString() {
		return fsView.getSystemDisplayName(theFile);
	}

	/**
	 * construct method with no param
	 */
	public FileNode() {
		fsView = FileSystemView.getFileSystemView();
		theFile = fsView.getHomeDirectory(); // ��ø�Ŀ¼
		path = theFile.getAbsolutePath();
		prepareChildren();
	}

	/**
	 * construct method with param
	 * 
	 * @param file
	 */
	public FileNode(File file) {
		theFile = file;
		path = file.getAbsolutePath();
		prepareChildren();
	}

	/**
	 * get the file's subfiles ,then add to the filelist
	 */
	private void prepareChildren() {
		File[] files = fsView.getFiles(theFile, true);
		for (int i = 0; i < files.length; i++) {
			if (!files[i].toString().toLowerCase().endsWith(".lnk")) {
				folder.add(files[i]);
			}
		}
	}

	/**
	 * get the child node by index
	 * 
	 * @param fileType
	 * @param index
	 * @return
	 */
	public FileNode getChild(char fileType, int index) {
		if (DIRECTORY == fileType) {
			return new FileNode(folder.get(index));
		}

		else if (FILE == fileType) {
			return null;
		} else {
			return null;
		}
	}

	/**
	 * get the number of children node
	 */
	public int getChildCount(char fileType) {
		if (DIRECTORY == fileType) {
			return folder.size();
		}

		else if (FILE == fileType) {
			return -1;
		} else {
			return -1;
		}
	}

	/**
	 * judge the node is leaf or not
	 * 
	 * @param fileType
	 * @return
	 */
	public boolean isLeaf(char fileType) {
		if (DIRECTORY == fileType) {
			return (folder.size() == 0);
		} else if (FILE == fileType) {
			return true;
		} else {
			return true;
		}
	}

	/**
	 * get the index of the child
	 * 
	 * @param fileType
	 * @param child
	 * @return
	 */
	public int getIndexOfChild(char fileType, Object child) {
		if (child instanceof FileNode) {
			if (DIRECTORY == fileType) {
				return (folder.indexOf(((FileNode) child).theFile));
			} else if (FILE == fileType) {
				return -1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

}
