/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: wangwenxingbuaa@gmail.com 
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
package cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriObject;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ONObject implements Cloneable {
	public static final int CONDITION = 1;
	public static final int EVENT = 2;
	public static final int ARC = 3;
	
	protected String id;

	protected String label;
	// �ڵ����� 1:condition 2: event 3: arc
	private int type;

	public Object object = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return label;
	}
	
    public Object clone(){
    	ONObject obj = null;
        try{
        	obj = (ONObject)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
    }	
}
