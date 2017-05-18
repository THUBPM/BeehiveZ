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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * the extends class of DefaultMutableTreeNode
 * 
 * @author zhp,���ڷ�
 * 
 */
public class DataNode extends DefaultMutableTreeNode {

	private long parent_id;
	private long catalog_id; // if the node is catalog , the value means
	// catalog_id,else,its value is -1
	private long process_id; // if the node is process,the value means
	// process_id,else its value is -1
	private String process_type; // node type: folder,PMNL,XPDL
	private String label; // node name

	// process node construct method
	public DataNode(String label, long parent_id, long process_id, String type) {
		this.label = label;
		this.parent_id = parent_id;
		this.process_id = process_id;
		this.process_type = type;
		// default
		this.catalog_id = -1;
	}

	// process node construct method without process_id
	public DataNode(String label, long parent_id, String type) {
		this.label = label;
		this.parent_id = parent_id;
		this.process_type = type;
		// default
		this.catalog_id = -1;
	}

	// catalog node construct method
	public DataNode(String label, long parent_id, long catalog_id) {
		this.label = label;
		this.parent_id = parent_id;
		this.catalog_id = catalog_id;
		// default
		this.process_id = -1;
		this.process_type = "FOLDER";
	}

	// catalog node construct method without catalog_id
	public DataNode(String label, long parent_id) {
		this.label = label;
		this.parent_id = parent_id;
		// default
		this.process_id = -1;
		this.process_type = "FOLDER";
	}

	public void setUserObject(DataNode useobject) {
		this.userObject = useobject;
	}

	public void setCatalog_id(long id) {
		this.catalog_id = id;
	}

	public long getCatalog_id() {
		return catalog_id;
	}

	public long getProcess_id() {
		return process_id;
	}

	public String toString() {
		return label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public String getType() {
		return this.process_type;
	}
}