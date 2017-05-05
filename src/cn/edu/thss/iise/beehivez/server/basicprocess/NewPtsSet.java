package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class NewPtsSet {
	private ArrayList<NewPtsSequence> NPSet = null;
	public String fileName;
	private int n = 1;

	public NewPtsSet(NewPtsSequence sequence, String fileName) {
		NPSet = new ArrayList<NewPtsSequence>();
		this.fileName = fileName;
		NPSet.add(sequence);
	}

	/************ fileName ***************/
	public String getFileName() {
		return this.fileName;
	}

	/************ NPSet ***************/
	public ArrayList<NewPtsSequence> getNPSet() {
		return NPSet;
	}

	public void addSequence(NewPtsSequence sequence) {
		NPSet.add(sequence);
	}

	public void showSet() {
		 System.out.println("file: " + fileName+NPSet.size());
		 for (int i = 0; i < NPSet.size(); i++) {
		 NPSet.get(i).showSequence(i + 1);
		 System.out.println();
		 }
	}

	/************ utilize ***************/
	public void resetMapping() {
		for (int i = 0; i < NPSet.size(); i++) {
			NPSet.get(i).resetMatched();
		}
	}

	// to get an average similarity of a sequence(seq) compared to this sequence
	// set
	public double averageSimilaritySeq2Set(double[][] seqM, int k) {
		double averageSimilarity = 0.0;
		for (int i = 0; i < NPSet.size(); i++) {
			averageSimilarity = averageSimilarity + seqM[i][k];
		}
		averageSimilarity = averageSimilarity / NPSet.size();

		return averageSimilarity;
	}

	// to do setSimilarity_ergodic();
//	public void ergodic(ArrayList<NewPtsSequence> set2, AstarTreeNode node,
//			int vol1, int vol2) {
//		int start = node.getIndex();
//
//		if (start == vol1) {
//			for (int i = 0; i < set2.size(); i++) {
//				if (valid(node, i) == true) {
//					node.addSim(set2.get(i).getSimAverage());
//				}
//			}
//			return;
//		}
//		for (int i = 0; i < vol2; i++) {
//			if (valid(node, i) == true) {
//				AstarTreeNode astn = new AstarTreeNode(start, i, vol1, vol2,
//						start + 1, node.getSim());
//				node.addChild(astn);
//				astn.addSim(NPSet.get(start)
//						.SequenceSimilarity_new(set2.get(i)));
//				ergodic(set2, astn, vol1, vol2);
//			}
//		}
//	}

	// public int aStarBAK(ArrayList<AstarTreeNode> leaf,
	// ArrayList<NewPtsSequence> set2, int vol1, int vol2) {
	// int selected = maxLeaf(leaf);
	// AstarTreeNode node = leaf.get(selected);
	// int start = node.getIndex();
	// if(start == vol1) {
	// return 1;
	// }
	// for(int i = 0; i < vol2; i++) {
	// if(valid(node, i) == true) {
	// AstarTreeNode astn = new AstarTreeNode(start, i, vol1, vol2, start+1,
	// node.getSim());
	// node.addChild(astn);
	// astn.addSim(NPSet.get(start).SequenceSimilarity_new(set2.get(i)));
	// astn.setPossibleMax(maxRemains(set2, astn, start+1));
	// leaf.add(astn);
	// }
	// }
	// leaf.remove(selected);
	//
	// return 0;
	// }

	public int aStar(PriorityQueue<AstarTreeNode> leaf,
			ArrayList<NewPtsSequence> set2, int vol1, int vol2, double[][] seqM) {

		if (leaf.peek().getIndex() == vol1)
			return 1;

		AstarTreeNode node = leaf.poll();
		if (node.getPossibleMax() == 0.0) {
			return -1;
		}
		int start = node.getIndex();
		for (int i = 0; i < vol2; i++) {
			if (valid(node, i) == true) {
				AstarTreeNode astn = new AstarTreeNode(start, i, vol1, vol2,
						start + 1, node.getSim(),this.n);
				this.n++;
				node.addChild(astn);
				// astn.addSim(NPSet.get(start)
				// .SequenceSimilarity_new(set2.get(i)));
				astn.addSim(seqM[start][i]);
				astn.setPossibleMax(maxRemains(set2, astn, start + 1, seqM));
				leaf.add(astn);
//				System.out.println(node.number+"->"+astn.number);
//				writeFile(node.number+"->"+astn.number+"\n");
//				writeFile(astn.number+":seqM[start][i]"+seqM[start][i]+":start:"+start+",i:"+i+"\n");
//				writeFile(astn.number+":g"+astn.getSim()+",h:"+(astn.getPossibleMax()-astn.getSim())+",g+h:"+astn.getPossibleMax()+"\n");
			}
		}

		return 0;
	}
	
	public void writeFile(String content){
		  try {
	            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
	            FileWriter writer = new FileWriter("C:\\Users\\dongzihe\\Desktop\\1234.txt", true);
	            writer.write(content);
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

		
	}

	public int maxLeaf(ArrayList<AstarTreeNode> leaf) {
		int id = 0;
		double max = 0.0;
		for (int i = 0; i < leaf.size(); i++) {
			if (max <= leaf.get(i).getPossibleMax()) {
				max = leaf.get(i).getPossibleMax();
				id = i;
			}
		}
		return id;
	}

	/**
	 * h()
	 * 
	 * @param set2
	 * @param node
	 * @param start
	 * @return
	 */
	public double maxRemains(ArrayList<NewPtsSequence> set2,
			AstarTreeNode node, int start, double[][] seqM) {
		double mr = 0.0;
		int num = set2.size() - NPSet.size();

		for (int i = start; i < NPSet.size(); i++) {
			mr += maxSequenceSimilarityRemains(i, set2, node, seqM);

		}
		if (num > 0) {
			double[] l = new double[set2.size()];
			for (int j = 0; j < set2.size(); j++) {
				if (valid(node, j) == false) {
					l[j] = 0.0;
				} else {
					l[j] = set2.get(j).getSimAverage();
				}
			}
			// l[]存放的是B集合每个元素和A的相似度
			// 找前num个最大值

			// 小顶堆
			PriorityQueue<Double> heap = new PriorityQueue<Double>();
			for (int k = 0; k < l.length; k++) {
				if (heap.size() < num)
					heap.add(l[k]);
				else if (heap.peek() < l[k]) {
					heap.poll();
					heap.add(l[k]);
				}
			}

			while (!heap.isEmpty())
				mr += heap.poll();
		}
		// System.out.println("mr"+mr);
		return mr;
	}

	public double maxAverageRmains(int n, double[] l, int length) {
		double max = 0.0;
		int id = n;

		for (int i = n; i < length; i++) {
			if (max <= l[i]) {
				id = i;
				max = l[i];
			}
		}
		l[id] = l[n];

		return max;
	}

	public double maxSequenceSimilarityRemains(int index,
			ArrayList<NewPtsSequence> set2, AstarTreeNode node, double[][] seqM) {
		double mssr = 0.0;

		for (int i = 0; i < set2.size(); i++) {
			if (valid(node, i) == false) {
				continue;
			}
			double s = seqM[index][i];
			if (mssr < s) {
				mssr = s;
			}
		}

		return mssr;
	}

	// to check if the index(n) of set(A) has already got a match
	public boolean valid(AstarTreeNode node, int n) {
		boolean valid = true;
		int[] m = node.getMatch();
		if (m[1] == -1) {
			return valid;
		}
		if (m[1] == n) {
			valid = false;
			return valid;
		}
		AstarTreeNode f = node.getFather();
		valid = valid(f, n);

		return valid;
	}

	// get the sum of similarity of the match which has the maximum similarity
	public double getMax(AstarTreeNode node, double max) {
		ArrayList<AstarTreeNode> child = node.getChild();
		if (child == null) {
			if (max < node.getSim()) {
				max = node.getSim();
			}
			return max;
		}
		for (int i = 0; i < child.size(); i++) {
			max = getMax(child.get(i), max);
		}

		return max;
	}

//	public AstarTreeNode getBestMatchOfAll(ArrayList<NewPtsSequence> set2) {
//		double max = 0.0;
//		int id = 0;
//		int adder = 0;
//		ArrayList<AstarTreeNode> list = new ArrayList<AstarTreeNode>();
//		for (int i = 0; i < NPSet.size(); i++) {
//			if (NPSet.get(i).isMatched() == true) {
//				adder++;
//				continue;
//			}
//			AstarTreeNode nd = NPSet.get(i).getBestMatch(set2, NPSet.size());
//			list.add(nd);
//			if (max <= nd.getSim()) {
//				max = nd.getSim();
//				id = i - adder;
//			}
//		}
//
//		return list.get(id);
//	}

	public double getSum_greedy(ArrayList<AstarTreeNode> list,
			ArrayList<NewPtsSequence> set2) {
		double sum = 0.0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).getSim();
		}
		for (int i = 0; i < set2.size(); i++) {
			if (set2.get(i).isMatched() == false) {
				sum += set2.get(i).getSimAverage();
			}
		}
		return sum;
	}

	/************ similarity ***************/
//	public double setSimilarity_ergodic(NewPtsSet set2, double[][] seqM) {
//		// suppose NPset2.size() >= NPset1.size()
//		double setSimilarity = 0.0;
//		ArrayList<NewPtsSequence> NPSet2 = set2.getNPSet();
//		int vol1 = NPSet.size();
//		int vol2 = NPSet2.size();
//		for (int i = 0; i < vol2; i++) {
//			NPSet2.get(i).setSimAverage(averageSimilaritySeq2Set(seqM, i));
//		}
//
//		/************ part-1 ***************/
//		AstarTree asTree = new AstarTree(vol1, vol2);
//		AstarTreeNode asRoot = asTree.getRoot();
//		ergodic(NPSet2, asRoot, vol1, vol2);
//		double sim = getMax(asRoot, 0.0);
//
//		/************ part-2 ***************/
//		double dis = 0.0;
//		dis = (vol2 - vol1) * 1.0 / vol2;
//
//		/************ part-3 ***************/
//		setSimilarity = sim / vol2 * (1 - dis / 1000);
//
//		return setSimilarity;
//	}

//	public double setSimilarity_greedy(NewPtsSet set2, double[][] seqM) {
//		// suppose NPset2.size() >= NPset1.size()
//		double setSimilarity = 0.0;
//		ArrayList<NewPtsSequence> NPSet2 = set2.getNPSet();
//		int vol1 = NPSet.size();
//		int vol2 = NPSet2.size();
//		for (int i = 0; i < vol2; i++) {
//			NPSet2.get(i).setSimAverage(averageSimilaritySeq2Set(seqM, i));
//		}
//
//		/************ part-1 ***************/
//		double sum = 0.0;
//		ArrayList<AstarTreeNode> ls = new ArrayList<AstarTreeNode>();
//		for (int i = 0; i < vol1; i++) {
//			ls.add(getBestMatchOfAll(NPSet2));
//			int[] m = ls.get(i).getMatch();
//			NPSet.get(m[0]).setMatched();
//			NPSet2.get(m[1]).setMatched();
//		}
//		sum = getSum_greedy(ls, NPSet2);
//		resetMapping();
//		set2.resetMapping();
//
//		/************ part-2 ***************/
//		double dis = 0.0;
//		dis = (vol2 - vol1) * 1.0 / vol2;
//
//		/************ part-3 ***************/
//		setSimilarity = sum / vol2 * (1 - dis / 1000);
//
//		return setSimilarity;
//	}

	public double setSimilarity_Astar(double[][] seqM, NewPtsSet set2) {
		// suppose NPset2.size() >= NPset1.size()
		double setSimilarity = 0.0;
		ArrayList<NewPtsSequence> NPSet2 = set2.getNPSet();
		int vol1 = NPSet.size();
		int vol2 = NPSet2.size();
		for (int i = 0; i < vol2; i++) {
			NPSet2.get(i).setSimAverage(averageSimilaritySeq2Set(seqM, i));
		}
		// System.out.println("finisha");
		/************ part-1 ***************/
		AstarTree asTree = new AstarTree(vol1, vol2,0);
		AstarTreeNode asRoot = asTree.getRoot();
		asRoot.setPossibleMax(maxRemains(NPSet2, asRoot, 0, seqM));
		System.out.println("g+h:"+asRoot.getPossibleMax());
//		System.out.println("possiblemax:" + asRoot.getPossibleMax());
		PriorityQueue<AstarTreeNode> leaf = new PriorityQueue<AstarTreeNode>(
				10, new Comparator<AstarTreeNode>() {
					public int compare(AstarTreeNode n1, AstarTreeNode n2) {
						return n1.getPossibleMax() < n2.getPossibleMax() ? 1
								: -1;
					}
				});

		leaf.add(asRoot);
		// System.out.println("finishb");
		double sim = 0.0;
		int mark = 0;
		long i = 0, start = System.currentTimeMillis();
		;
		while (true) {
			{
				if (i++ % 100 == 0) {
					//限制时间
					long time = (System.currentTimeMillis() - start) / 1000;
					if (time > 300) {// 大于5分钟
						return 100.0;
					}
					//限制节点个数
					if (leaf.size() > 30000000) {
						return 100.0;
					}
//					System.out.println(i + "\t" + leaf.size() + "\t" + time);
				}

			}
			mark = aStar(leaf, NPSet2, vol1, vol2, seqM);
			if (mark == -1) {
				sim = 0.0;
				break;
			}
			if (mark == 1){
				sim = leaf.peek().getPossibleMax();
				break;
			}
				
		}
		// System.out.println(i);
		// System.out.println("finishc");

		// System.out.println("finishd");
		// 输出匹配
		// AstarTreeNode y = leaf.get(maxLeaf(leaf));
		// for(int i = 0; i < vol1; i++) {
		// int[] match = y.getMatch();
		// System.out.println(i + ": [" + match[0] + ", " + match[1] + "].");
		// y = y.getFather();
		// }

		/************ part-2 ***************/
		double dis = 0.0;
		dis = (vol2 - vol1) * 1.0 / vol2;

		/************ part-3 ***************/
		// n取值
		setSimilarity = sim / vol2 * (1 - dis / 1000);

		return setSimilarity;
	}

}
