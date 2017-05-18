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

/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex;

/**
 * @author ���
 * 
 */
public interface AVLTreeNode {

	public abstract int getBalance();

	public abstract void setBalance(int v);

	public abstract long getPosition();

	public abstract long getLeftChild();

	public abstract long getRightChild();

	public abstract long getParent();

	public abstract void setLeftChild(AVLTreeNode p);

	public abstract void setRightChild(AVLTreeNode p);

	public abstract void setParent(AVLTreeNode p);

	// public abstract void setLeftChild(long p);
	// public abstract void setRightChild(long p);
	public abstract void setParent(long p);

	public abstract int compareTo(AVLTreeNode n);

	public abstract int compareTo(String value);

	// public abstract void addChild(AVLTreeNode n,boolean isLeft);

	public abstract String getData();
}
