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
package cn.edu.thss.iise.beehivez.server.datamanagement.pojo;

/**
 * @author JinTao 2009.9.3
 * 
 */
public class ProcesscatalogObject {
	public static String PETRINETS = "petri nets";
	public static String YAWLMODELS = "yawl models";

	private long catalog_id = -1;
	private String name = null;
	private long parent_id = -1;

	/**
	 * @return the catalog_id
	 */
	public long getCatalog_id() {
		return catalog_id;
	}

	/**
	 * @param catalogId
	 *            the catalog_id to set
	 */
	public void setCatalog_id(long catalogId) {
		catalog_id = catalogId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent_id
	 */
	public long getParent_id() {
		return parent_id;
	}

	/**
	 * @param parentId
	 *            the parent_id to set
	 */
	public void setParent_id(long parentId) {
		parent_id = parentId;
	}
}
