/**
 * by hhw 
 */

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

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.ArrayList;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

public class TTreeGenerator {

	public TTree tTree;

	public String getName() {
		return "TraceTree";
	}

	public String getDesription() {
		return "transforming a coverbility tree to a tace tree";
	}

	/************ generatTTree ***************/
	/**
	 * Input: a coverability tree cTree, loop times K Ouput: a trace tree tTree
	 */
	public NewPtsSet generatTTree(CTree cTree, int k,
			String fileName) {

		/************ part-1 ***************/
		// ctree to trace tree-1
		CTreeNode cRoot = cTree.getRoot();
		TTree tTree = new TTree();
		ArrayList<TTreeNode> pathSet = new ArrayList<TTreeNode>();
		generateTTreeV1(cTree, tTree, tTree.getRoot(), cRoot, pathSet);

		/************ part-2 ***************/
		// mark the nodes(find pre-node)
		int kk = 2;
		while (pathSet.size() != 0) {
			int i = 0;
			for (; i < pathSet.size(); i++) {
				ArrayList<TTreeNode> forkNodeSet = new ArrayList<TTreeNode>();
				getAllForkingTransitionsReverse(pathSet.get(i), forkNodeSet);
				for (int j = 0; j < forkNodeSet.size(); j++) {
					boolean isPre = false;
					for (int m = 0; m < forkNodeSet.get(j).getChild().size(); m++) {
						if (forkNodeSet.get(j).getChild().get(m).getId() != forkNodeSet
								.get(j).getANum()) {
							isPre = isThereAPre(kk, false, forkNodeSet.get(j)
									.getChild().get(m));
							if (isPre == true) {
								break;
							}
						}
					}
					if (isPre == true) {
						forkNodeSet.get(j).setPre();
						for (int l = 0; l < forkNodeSet.get(j).getChild()
								.size(); l++) {
							if (forkNodeSet.get(j).getChild().get(l).getId() == forkNodeSet
									.get(j).getANum()) {
								forkNodeSet.get(j).getChild().get(l)
										.setOldFather();
							} else if (forkNodeSet.get(j).getChild().get(l)
									.isMarked() == 0) {
								forkNodeSet.get(j).getChild().get(l)
										.setDeadFather();
							}
						}
						pathSet.get(i).setDeadEnd(kk);
						pathSet.remove(i);
						i--;
						break;
					}
				}
			}
			kk++;
		}

		/************ part-3 ***************/
		// get trace tree-2
		TTreeNode newRoot = new TTreeNode(tTree.getRoot());
		TTree newTTree = new TTree(newRoot);

//		generateTTreeV3(newTTree, newRoot, k);
		generateTTreeV2_1(newTTree, newRoot, k);

		/************ part-4 ***************/
		// get trace sequence
		NewPtsSet NPSet = null;
		ArrayList<TTreeNode> leaf = newTTree.getLeafNodes();

		for (int i = 0; i < leaf.size(); i++) {
			NewPtsSequence seq = new NewPtsSequence(getSequence(leaf.get(i)));
			seq.setIndex(i);
			if (NPSet == null) {
				NPSet = new NewPtsSet(seq, fileName);
			} else {
				NPSet.addSequence(seq);
			}
		}

		return NPSet;
	}

	/************ utilize ***************/
	public void showSequence(TTreeNode ttn) {
		ArrayList<TTreeNode> se = getSequence(ttn);

		for (int i = se.size(); i > 0; i--) {
			if (i == 1) {
				System.out.println(se.get(i - 1).getTransition().getName()
						+ " .");
			} else {
				System.out.print(se.get(i - 1).getTransition().getName()
						+ "-->");
			}
		}
	}
	
	public void generateTTreeV3(TTree newTTree, TTreeNode newRoot, int k) {
		TQueue queue = new TQueue();
		ArrayList<TTreeNode> tSet = null;
		TTreeNode newCopy = null;
		TTreeNode tt = null;
		TTreeNode t = null;

		queue.initTQueue();
		queue.enTQueue(newRoot);

		while (queue.isTQueueEmpty() == false) {
			t = queue.getHead();
			tSet = t.getCopy().getChild();

			if ((tSet == null) && (t.getCopy().isOldEnd() == 1)) {
				Marking mark = t.getCopy().getNextMarking();
				int OccurTime = calOccurTime(t, mark);
				if (OccurTime == k - 1) {
					tt = t.getCopy().getAnchorNode().getParent();
					newCopy = new TTreeNode(tt);
					queue.enTQueue(newCopy);
					newCopy.setSpecial(t);
				} else if (OccurTime >= k){
					cuttingTree(t);
				}
			} else if (tSet != null) {
				for (int j = 0; j < tSet.size(); j++) {
					tt = tSet.get(j);
					newCopy = new TTreeNode(tt);
					queue.enTQueue(newCopy);
					if (t.getSpecial() != null) {
						newTTree.insertChild(t.getSpecial(), newCopy);
					} else {
						newTTree.insertChild(t, newCopy);
					}
				}
			}
			t.nullSpecial();
		}
	}

	/**
	 * 生成trace的新方法，按照marking，最终方法
	 * 
	 * @param newTTree
	 * @param newRoot
	 * @param k
	 */
	public void generateTTreeV2_1(TTree newTTree, TTreeNode newRoot, int k) {
		TQueue queue = new TQueue();
		ArrayList<TTreeNode> tSet = null;
		TTreeNode newCopy = null;
		TTreeNode tt = null;
		TTreeNode t = null;

		queue.initTQueue();
		queue.enTQueue(newRoot);

		while (queue.isTQueueEmpty() == false) {
			t = queue.getHead();
			tSet = t.getCopy().getChild();

			if ((tSet == null) && (t.getCopy().isOldEnd() == 1)) {
				Marking mark = t.getCopy().getNextMarking();
				int OccurTime = calOccurTime(t, mark);
				if (OccurTime < k) {
					tt = t.getCopy().getAnchorNode().getParent();
					newCopy = new TTreeNode(tt);
					queue.enTQueue(newCopy);
					newCopy.setSpecial(t);
				} else {
					cuttingTree(t);
				}
			} else if (tSet != null) {
				for (int j = 0; j < tSet.size(); j++) {
					tt = tSet.get(j);
					newCopy = new TTreeNode(tt);
					queue.enTQueue(newCopy);
					if (t.getSpecial() != null) {
						newTTree.insertChild(t.getSpecial(), newCopy);
					} else {
						newTTree.insertChild(t, newCopy);
					}
				}
			}
			t.nullSpecial();
		}
	}

	/**
	 * 生成trace集合的旧方法，按照oldfather次数计算
	 * 
	 * @param newTTree
	 * @param newRoot
	 * @param k
	 */
//	public void generateTTreeV2_2(TTree newTTree, TTreeNode newRoot, int k) {
//		TQueue queue = new TQueue();
//		ArrayList<TTreeNode> tSet = null;
//		TTreeNode newCopy = null;
//		TTreeNode tt = null;
//		TTreeNode t = null;
//
//		queue.initTQueue();
//		queue.enTQueue(newRoot);
//
//		while (queue.isTQueueEmpty() == false) {
//			t = queue.getHead();
//			tSet = t.getCopy().getChild();
//
//			if (t.getCopy().isPre() == 1) {
//				for (int i = 0; i < tSet.size(); i++) {
//					tt = tSet.get(i);
//					if (tt.isOldFather() == 2) {
//						if (t.getSpecial() != null) {
//							if (calOccurNum(t.getSpecial(), tt.getTransition()
//									.getName()) < k) {
//								newCopy = new TTreeNode(tt);
//								queue.enTQueue(newCopy);
//								newTTree.insertChild(t.getSpecial(), newCopy);
//							}
//						} else {
//							if (calOccurNum(t, tt.getTransition().getName()) < k) {
//								newCopy = new TTreeNode(tt);
//								queue.enTQueue(newCopy);
//								newTTree.insertChild(t, newCopy);
//							}
//						}
//					} else {
//						newCopy = new TTreeNode(tt);
//						queue.enTQueue(newCopy);
//						if (t.getSpecial() != null) {
//							newTTree.insertChild(t.getSpecial(), newCopy);
//						} else {
//							newTTree.insertChild(t, newCopy);
//						}
//					}
//				}
//			} else if ((tSet == null) && (t.getCopy().isOldEnd() == 1)) {
//				tt = t.getCopy().getAnchorNode().getParent();
//				newCopy = new TTreeNode(tt);
//				queue.enTQueue(newCopy);
//				newCopy.setSpecial(t);
//			} else if (tSet != null) {
//				for (int j = 0; j < tSet.size(); j++) {
//					tt = tSet.get(j);
//					newCopy = new TTreeNode(tt);
//					queue.enTQueue(newCopy);
//					if (t.getSpecial() != null) {
//						newTTree.insertChild(t.getSpecial(), newCopy);
//					} else {
//						newTTree.insertChild(t, newCopy);
//					}
//				}
//			}
//			t.nullSpecial();
//		}
//	}

//	public int calOccurNum(TTreeNode t, String name) {
//		int num = 0;
//		TTreeNode ttn = t;
//
//		TTreeNode father = ttn.getParent();
//		while (father.getId() != 0) {
//			ttn = father;
//			father = ttn.getParent();
//			if (ttn.getCopy().getTransition().getName().equals(name)) {
//				num++;
//			}
//		}
//
//		return num;
//	}

	public int calOccurTime(TTreeNode t, Marking mark) {
		int time = 0;
		TTreeNode ttn = t;
		TTreeNode father = ttn.getParent();

		while (father.getId() != 0) {
			ttn = father;
			father = ttn.getParent();
			if (ttn.getCopy().getNextMarking().equals(mark) == true) {
				if (ttn.getCopy().isOldEnd() == 1) {
					time++;
				}
			}
		}

		return time;
	}

	public void cuttingTree(TTreeNode ttn) {
		TTreeNode father = ttn.getParent();
		TTreeNode node = ttn;
		while (father.childNumber() < 2) {
			node = father;
			father = node.getParent();
		}
		father.deleteChild(node);
	}

	public ArrayList<TTreeNode> getSequence(TTreeNode ttn) {
		ArrayList<TTreeNode> se = new ArrayList<TTreeNode>();

		se.add(ttn);
		TTreeNode father = ttn.getParent();
		while (father.getId() != 0) {
			ttn = father;
			se.add(ttn);
			father = ttn.getParent();
		}

		return se;
	}

	public boolean isThereAPre(int kk, boolean b, TTreeNode ttn) {
		boolean bb = b;
		ArrayList<TTreeNode> child = ttn.getChild();
		if (child == null) {
			if (ttn.isDeadEnd() == (kk - 1)) {
				return true;
			}
			return false;
		} else {
			for (int i = 0; i < child.size(); i++) {
				b = isThereAPre(kk, bb, child.get(i));
				if (b == true) {
					bb = true;
					break;
				}
			}
		}
		return bb;
	}

	public void getAllForkingTransitionsReverse(TTreeNode ttn,
			ArrayList<TTreeNode> fSet) {
		TTreeNode c = ttn;
		TTreeNode f = c.getParent();
		TTreeNode anchor = ttn.getAnchorNode();
		ArrayList<TTreeNode> child;
		if (anchor == null) {
			System.out.println("error1");
		}
		while (f != null) {
			child = f.getChild();
			if (child.size() > 1) {
				f.setANum(c.getId());
				fSet.add(f);
			}
			if (f.getId() == anchor.getId()) {
				break;
			}
			c = f;
			f = c.getParent();
		}
	}

	public void generateTTreeV1(CTree ctree, TTree ttree, TTreeNode parent,
			CTreeNode ctn, ArrayList<TTreeNode> pathSet) {
		ArrayList<CTreeNode> children = ctn.getChild();
		for (int i = 0; i < children.size(); i++) {
			CTreeNode child = children.get(i);
			int id = ttree.getNodeNum();
			TTreeNode tChild = new TTreeNode(id, ctree, child);
			ttree.insertChild(parent, tChild);
			if (child.getChild() == null) {
				// child is old-end
				if (child.getType() != 3) {
					tChild.setOldEnd();
					markAnchor(ctree, ttree, tChild, child);
					pathSet.add(tChild);
				} else {
					tChild.setDeadEnd(1);
				}
			} else {
				generateTTreeV1(ctree, ttree, tChild, child, pathSet);
			}
		}
		return;
	}

	public void markAnchor(CTree ctree, TTree ttree, TTreeNode ttn,
			CTreeNode ctn) {
		CTreeNode cAnchor = getOldEndAnchor(ctree, ctn);
		MyPetriTransition t = null;
		CTreeNode cc = ctn;
		CTreeNode cf = cc.getParent();
		while (cf != null) {
			if (cf.equals(cAnchor)) {
				t = ctree.getEdge(cAnchor, cc);
				break;
			}
			cc = cf;
			cf = cc.getParent();
		}

		TTreeNode f = ttn.getParent();
		while (f != null) {
			if (f.getTransition().equals(t)) {
				ttn.setAnchorNode(f);
				f.setAnchor();
				break;
			}
			f = f.getParent();
		}
	}

	public CTreeNode getOldEndAnchor(CTree ctree, CTreeNode oldEnd) {

		return ctree.getDirectImageVertexOfleafNode(oldEnd);
	}
}
