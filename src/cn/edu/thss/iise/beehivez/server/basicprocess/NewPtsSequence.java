package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.ArrayList;
import java.util.HashMap;

public class NewPtsSequence {
	private ArrayList<TTreeNode> NPSequence = null;
	private int index = -1;
	private boolean isMatched = false;
	private double simAverage = 0.0;
	
	//存放被比较的序列和相似度值
	HashMap<NewPtsSequence,Double> simMap = new HashMap<NewPtsSequence,Double>();
	
	
	/************ NewPtsSequence ***************/
	public NewPtsSequence(ArrayList<TTreeNode> seq) {
		setNPSequence(seq);
	}
	
	/************ NPSequence ***************/
	public void setNPSequence(ArrayList<TTreeNode> seq) {
		this.NPSequence = seq;
	}
	
	public ArrayList<TTreeNode> getNPSequence() {
		return NPSequence;
	}
		
	public void showSequence(int n) {
		System.out.print("  *" + n + ": ");
		for(int i = NPSequence.size(); i > 0; i--) {
			if(i == 1) {
				System.out.println(NPSequence.get(i-1).getTransition().getName() + " .");
			} else {
				System.out.print(NPSequence.get(i-1).getTransition().getName() + "-->");
			}
		}
	}

	/************ Index ***************/
	public int getIndex() {
		return index;
	}

	public void setIndex(int n) {
		this.index = n;
	}

	/************ isMatched ***************/
	public boolean isMatched() {
		return isMatched;
	}

	public void setMatched() {
		this.isMatched = true;
	}

	public void resetMatched() {
		this.isMatched = false;
	}

	/************ simAverage ***************/
	public void setSimAverage(double sa) {
		simAverage = sa;
	}

	public double getSimAverage() {
		return simAverage;
	}

	/************ utilize ***************/
//	public AstarTreeNode getBestMatch(ArrayList<NewPtsSequence> set2, int vol1) {
//		double maxSequenceSimilarity = 0.0;
//		int a = 0;
//
//		for(int i = 0; i < set2.size(); i++) {
//			if(set2.get(i).isMatched() == true) {
//				continue;
//			}
//			double s = SequenceSimilarity_new(set2.get(i));
//			if(maxSequenceSimilarity <= s) {
//				maxSequenceSimilarity = s;
//				a = i;
//			}
//		}
//		AstarTreeNode node = new AstarTreeNode(vol1, set2.size());
//		node.setMatch(this.index, a);
//		node.setSim(maxSequenceSimilarity);
//
//		return node;
//	}

	public double maxSequenceSimilarity(NewPtsSet set2) {
		double maxSequenceSimilarity = 0.0;
		
		ArrayList<NewPtsSequence> NPSet2 = set2.getNPSet();
		for(int i = 0; i < NPSet2.size(); i++) {
			double s = SequenceSimilarity_new(NPSet2.get(i));
			if(maxSequenceSimilarity < s) {
				maxSequenceSimilarity = s;
			}
		}
		
		return maxSequenceSimilarity;
	}
	
	public double totalSequenceSimilarity(NewPtsSet set2) {
		double totalSequenceSimilarity = 0.0;
		
		ArrayList<NewPtsSequence> NPSet2 = set2.getNPSet();
		for(int i = 0; i < NPSet2.size(); i++) {
			totalSequenceSimilarity += SequenceSimilarity_new(NPSet2.get(i));
		}
		
		return totalSequenceSimilarity;
	}
	
	/**
	 * dong
	 * 将使用过的相似度存在数组里面，下次就不用重复求了
	 * @param seq2
	 * @return
	 */
	public double SequenceSimilarity_new(NewPtsSequence seq2) {
		if(this.simMap.containsKey(seq2)){
			return this.simMap.get(seq2);
		}
		else{
			double d = SequenceSimilarity(seq2);
			this.simMap.put(seq2, d);
			return d;
		}
		
	}
	
	
	
	public double SequenceSimilarity(NewPtsSequence seq2) {
		double sequenceSimilarity = 0.0;
		
		ArrayList<TTreeNode> NPSequence2 = seq2.getNPSequence();
		int length1 = NPSequence.size();
		int length2 = NPSequence2.size();
		
		/************ part-1 ***************/
		//get max length
		double maxlength = length1 + 0.0;
		if(maxlength < length2) {
			maxlength = length2;
		}
		
		/************ part-2 ***************/
		//get max common length
		int maxcommonlength = 0;
		int c[][] = new int[2][length2];
		int i = 0;
		int j = 0;
		for(i = 0; i < length2; i++) {
			c[1][i] = 0;
		}
		for(i = 0; i < length1; i++) {
			for(j = 0; j < length2; j++) {
				if(NPSequence.get(i).getTransition().getName().equals(NPSequence2.get(j).getTransition().getName())) {
					if((i == 0) || (j == 0)) {
						c[i%2][j] = 1;
					} else {
						c[i%2][j] = c[(i-1)%2][j-1] + 1;
					}
				} else {
					if((i == 0) && (j == 0)) {
						c[i%2][j] = 0;
					} else if((i == 0) && (j > 0)) {
						c[i%2][j] = c[i%2][j-1];
					} else if((i > 0) && (j == 0)) {
						c[i%2][j] = c[(i-1)%2][j];
					} else {
						c[i%2][j] = c[(i-1)%2][j];
						if(c[i%2][j] < c[i%2][j-1]) {
							c[i%2][j] = c[i%2][j-1];
						}
					}
				}
			}
			if(maxcommonlength < c[i%2][j-1]) {
				maxcommonlength = c[i%2][j-1];
			}
		}
		
		/************ part-3 ***************/
		//get sequence similarity
		sequenceSimilarity = (double)maxcommonlength / maxlength;
		
		return sequenceSimilarity;
	}
}
