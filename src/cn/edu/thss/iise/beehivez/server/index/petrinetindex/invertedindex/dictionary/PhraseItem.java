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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary;

import java.io.IOException;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.AVLTreeNode;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.fileAccess.RAFile;

/**
 * �����ʽ String����[int]+String byte[bytes]+relationPos[long]+balance[int]
 * +leftChild[long]+rightChild[long]+parent[long]
 * 
 * @author ���
 * 
 */

public class PhraseItem extends DictionaryItem implements AVLTreeNode {

	private String value;
	private long relationPos;
	// private long next;

	private int balance = 0;
	private long leftChild = -1;
	private long rightChild = -1;
	private long parent = -1;

	public PhraseItem(String v, long pos, long relPos, boolean isD) {
		super();
		value = v;
		position = pos;
		relationPos = relPos;
		isDirty = isD;
	}

	// public long getNext()
	// {
	// return next;
	// }

	public String getValue() {
		return value;
	}

	public long getRelationPos() {
		return relationPos;
	}

	public PhraseItem() {
		super();

	}

	public void setRelationPos(long v) {
		relationPos = v;
		isDirty = true;
	}

	/**
	 * length of the string[int] + string bytes + relation position[long] +
	 * balance [int] + left child[long] + right child [long] + parent [long]
	 * 
	 * @return
	 */
	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return value.length() + 40;
	}

	@Override
	public void writeOut(RAFile file) throws IOException {
		// TODO Auto-generated method stub
		file.seek(this.position);
		file.writeString(value);
		file.writeLong(relationPos);
		file.writeInt(balance);
		file.writeLong(leftChild);
		file.writeLong(rightChild);
		file.writeLong(parent);

		isDirty = false;
		// file.writeInt(value.length());
		// file.write(value.getBytes());
		// file.writeLong(relationPos);
		// file.writeLong(next);

	}

	@Override
	public void readIn(RAFile file, long pos) throws IOException {
		// TODO Auto-generated method stub
		file.seek(pos);
		position = pos;
		value = file.readString();
		relationPos = file.readLong();
		balance = file.readInt();
		leftChild = file.readLong();
		rightChild = file.readLong();
		parent = file.readLong();

		isDirty = false;
		// super.visit();
		// int len=file.readInt();
		// byte[] key=new byte[len];
		// file.read(key);
		// value=new String(key);
		// relationPos=file.readLong();
		// next=file.readLong();

	}

	public int compareTo(AVLTreeNode n) {
		// TODO Auto-generated method stub
		return value.compareTo(n.getData());
	}

	public int compareTo(String v) {
		// TODO Auto-generated method stub
		return this.value.compareTo(v);
	}

	public int getBalance() {
		// TODO Auto-generated method stub
		return balance;
	}

	public long getLeftChild() {
		// TODO Auto-generated method stub
		return leftChild;
	}

	public long getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	public long getRightChild() {
		// TODO Auto-generated method stub
		return rightChild;
	}

	public void setBalance(int v) {
		// TODO Auto-generated method stub
		isDirty = true;
		balance = v;
	}

	public void setLeftChild(AVLTreeNode p) {
		// TODO Auto-generated method stub
		if (p == null)
			leftChild = -1;
		else {
			leftChild = p.getPosition();
			p.setParent(this.position);
		}
		isDirty = true;
	}

	public void setParent(AVLTreeNode p) {
		// TODO Auto-generated method stub
		if (p == null)
			parent = -1;
		else
			this.parent = p.getPosition();
		isDirty = true;
	}

	public void setRightChild(AVLTreeNode p) {
		// TODO Auto-generated method stub
		if (p == null)
			rightChild = -1;
		else {
			rightChild = p.getPosition();
			p.setParent(this.position);
		}
		isDirty = true;
	}

	public String getData() {
		// TODO Auto-generated method stub
		return value;
	}

	public void setParent(long p) {
		// TODO Auto-generated method stub
		parent = p;
		isDirty = true;
	}

}
