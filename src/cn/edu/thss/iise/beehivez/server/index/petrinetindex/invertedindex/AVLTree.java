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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex;

/**
 * @author ���
 * 
 */

public abstract class AVLTree {
	// AVLTreeNode root=null;
	protected long rootPosition;

	public void insert(AVLTreeNode node) throws Exception {
		synchronized (this) {
			if (rootPosition == -1) {
				// AVLTreeNode root=node;
				rootPosition = node.getPosition();
			} else {
				AVLTreeNode n = getNode(rootPosition);
				AVLTreeNode p = n;
				int compare = 0;
				boolean isLeft;
				while (true) {
					compare = n.compareTo(node);
					if (compare == 0)
						throw new Exception("same value exists in the tree!");

					p = n;
					isLeft = compare < 0;
					n = getNode(isLeft ? n.getLeftChild() : n.getRightChild());

					if (n == null)
						break;
				}
				if (isLeft)
					p.setLeftChild(node);
				else
					p.setRightChild(node);
				balance(p, isLeft);
			}
		}
	}

	/**
	 * ��߸� [-1] �ұ߸� [1] �ȸ�[0]
	 * 
	 * @param node
	 * @param isLeft
	 */
	private void balance(AVLTreeNode node, boolean isLeft) {
		// boolean isRoot=false;

		while (true) {
			switch (isLeft ? node.getBalance() : -node.getBalance()) {
			case 1:
				node.setBalance(0);
				return;
			case 0:
				node.setBalance(isLeft ? -1 : 1);
				break;
			case -1:
				AVLTreeNode n = getNode(isLeft ? node.getLeftChild() : node
						.getRightChild());
				if (n.getBalance() == 0) {
					int a = 0;
					a++;
				}
				if (n.getBalance() == (isLeft ? -1 : 1)) {
					AVLTreeNode parent = getNode(node.getParent());
					if (parent == null) {
						rootPosition = n.getPosition();
						n.setParent(null);
					} else if (parent.getLeftChild() == node.getPosition())
						parent.setLeftChild(n);
					else
						parent.setRightChild(n);
					if (isLeft) {
						node.setLeftChild(getNode(n.getRightChild()));
						n.setRightChild(node);
					} else {
						node.setRightChild(getNode(n.getLeftChild()));
						n.setLeftChild(node);
					}
					n.setBalance(0);
					node.setBalance(0);
				} else {
					AVLTreeNode r = getNode(isLeft ? n.getRightChild() : n
							.getLeftChild());
					AVLTreeNode parent = getNode(node.getParent());
					if (parent == null) {
						rootPosition = r.getPosition();
						r.setParent(null);
					} else if (parent.getLeftChild() == node.getPosition())
						parent.setLeftChild(r);
					else
						parent.setRightChild(r);
					int rb = r.getBalance();
					if (isLeft) {
						n.setRightChild(getNode(r.getLeftChild()));
						node.setLeftChild(getNode(r.getRightChild()));
						r.setLeftChild(n);
						r.setRightChild(node);
						node.setBalance(rb == -1 ? 1 : 0);
						n.setBalance(rb == 1 ? -1 : 0);
					} else {
						n.setLeftChild(getNode(r.getRightChild()));
						node.setRightChild(getNode(r.getLeftChild()));
						r.setLeftChild(node);
						r.setRightChild(n);
						node.setBalance(rb == 1 ? -1 : 0);
						n.setBalance(rb == -1 ? 1 : 0);
					}
					r.setBalance(0);
				}
				return;
			}
			AVLTreeNode parent = getNode(node.getParent());
			if (parent == null) {
				rootPosition = node.getPosition();
				return;
			}

			isLeft = (parent.getLeftChild() == node.getPosition());
			node = parent;
		}
	}

	public AVLTreeNode search(String value) {
		if (rootPosition == -1)
			return null;
		AVLTreeNode n = getNode(rootPosition);
		boolean isLeft;
		while (true) {
			int com = n.compareTo(value);
			if (com == 0)
				return n;
			isLeft = com < 0;
			long pos = isLeft ? n.getLeftChild() : n.getRightChild();
			if (pos == -1)
				return null;
			n = getNode(pos);
		}
	}

	protected abstract AVLTreeNode getNode(long pos);
	// {
	// if(pos==-1)
	// return null;
	// }
}
