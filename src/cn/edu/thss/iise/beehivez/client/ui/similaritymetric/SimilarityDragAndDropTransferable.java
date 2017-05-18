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
package cn.edu.thss.iise.beehivez.client.ui.similaritymetric;

/**
 * 
 * @author nianhualeaf
 * DragAndDropTransferable class  implements the interface Transferable
 *
 */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import cn.edu.thss.iise.beehivez.util.DataNode;

public class SimilarityDragAndDropTransferable implements Transferable {
	private DataNode treeNode;
	public final static DataFlavor TREENODE_FLAVOR = new DataFlavor(
			DataNode.class, "treeNode instances");
	public DataFlavor[] flavors = new DataFlavor[] { TREENODE_FLAVOR };

	public SimilarityDragAndDropTransferable(DataNode treenode) {
		this.treeNode = treenode;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(TREENODE_FLAVOR)) {
			return treeNode;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor df : flavors) {
			if (df.equals(flavor))
				return true;
		}
		return false;
	}

}
