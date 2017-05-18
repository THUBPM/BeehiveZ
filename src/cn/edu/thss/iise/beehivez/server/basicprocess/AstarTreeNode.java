package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.ArrayList;

public class AstarTreeNode {
	private int index;
	private int vol1; // volume of the set1
	private int vol2; // volume of the set2
	private int[] match = new int[2];
	private ArrayList<AstarTreeNode> child = null;
	private double sim = 0.0;// the sum of the similarity of the currently known matches
	private double possibleMax = 0.0;
	private AstarTreeNode father = null;
	public int number = 0;

	/************ AstarTreeNode ***************/
	public AstarTreeNode(int vol1, int vol2, int n) {
		index = 0;
		child = new ArrayList<AstarTreeNode>();
		this.vol1 = vol2;
		this.vol1 = vol2;
		match[0] = -1;
		match[1] = -1;
		this.number = n;
	} 
	public AstarTreeNode(int n1, int n2, int vol1, int vol2, int index, double sim, int n) {
		match[0] = n1;
		match[1] = n2;
		this.vol1 = vol1;
		this.vol1 = vol2;
		this.index = index;
		this.sim = sim;
		this.number = n;
	}

	/************ father ***************/
	public AstarTreeNode getFather() {
		return father;
	}

	public void setFather(AstarTreeNode f) {
		father = f;
	}

	/************ match ***************/
	public int[] getMatch() {
		return match;
	}

	public void setMatch(int a, int b) {
		match[0] = a;
		match[1] = b;
	}

	/************ child ***************/
	public void addChild(AstarTreeNode c) {
		if(child == null) {
			child = new ArrayList<AstarTreeNode>();
		}
		child.add(c);
		c.setFather(this);
	}

	public ArrayList<AstarTreeNode> getChild() {
		return child;
	}

	/************ vol ***************/
	public int getVol1() {
		return vol1;
	}

	public int getVol2() {
		return vol2;
	}

	/************ index ***************/
	public int getIndex() {
		return index;
	}

	public void setIndex(int i) {
		index = i;		
	}

	/************ sim ***************/
	public double getSim() {
		return sim;
	}

	public void setSim(double s) {
		sim = s;
	}

	public void addSim(double a) {
		sim += a;
	}

	/************ possibleMax ***************/
	public void setPossibleMax(double pm) {
		possibleMax = sim + pm;
	}

	public double getPossibleMax() {
		return possibleMax;
	}
}
