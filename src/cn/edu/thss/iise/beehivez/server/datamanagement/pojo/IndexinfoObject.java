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
public class IndexinfoObject {
	public static final String USED = "used";
	public static final String UNUSED = "unused";
	private long index_id = -1;
	private String javaclassname = null;
	private String description = null;
	private String state = UNUSED;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the index_id
	 */
	public long getIndex_id() {
		return index_id;
	}

	/**
	 * @param indexId
	 *            the index_id to set
	 */
	public void setIndex_id(long indexId) {
		index_id = indexId;
	}

	/**
	 * @return the javaclass
	 */
	public String getJavaclassName() {
		return javaclassname;
	}

	/**
	 * @param javaclass
	 *            the javaclass to set
	 */
	public void setJavaclassName(String javaclassname) {
		this.javaclassname = javaclassname;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

}
