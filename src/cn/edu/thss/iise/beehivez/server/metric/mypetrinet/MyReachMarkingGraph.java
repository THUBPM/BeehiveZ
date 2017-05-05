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
/*
 * MyReachMarkingGraph.java 09-2-28
 */

package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

import java.util.Vector;

/**
 * Define MyPetriNet Reach Marking Graph
 * 
 * Petri��ɴ��ʶͼ
 * 
 * @author zhp
 * 
 */
public class MyReachMarkingGraph implements Cloneable {

	// Petri���������б�
	public Vector<MyPetriPlace> placearray;
	// Petri�����
	private MyPetriNet petri;
	private RMGMarkingState m0;

	public Vector<RMGObject> reachmarkinggraph;

	public Object clone() {
		MyReachMarkingGraph obj = null;
		try {
			obj = (MyReachMarkingGraph) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Constructor
	 * 
	 * @param petri
	 *            The specified MyPetriNet
	 * @param markedplace
	 *            The initial marked place
	 */
	public MyReachMarkingGraph(MyPetriNet petri, MyPetriPlace markedplace) {
		placearray = new Vector<MyPetriPlace>();
		reachmarkinggraph = new Vector<RMGObject>();

		this.petri = petri;

		// ���п���
		buildPlaceArray();
		// ��ݳ�ʼ��ʶ����õ�M0
		buildM0(markedplace);
		// ���M0����ɴ�ͼ
		bulidRMG();
	}

	/**
	 * Add all the Place to the Place vector
	 */
	// �õ�Petri���е����п����б�
	private void buildPlaceArray() {
		if (petri == null) {
			return;
		}

		MyPetriObject p;
		String s;
		for (int i = 0; i < petri.petri.size(); i++) {
			p = petri.petri.get(i);
			if (p.isA() != MyPetriObject.PLACE) {
				continue;
			}
			placearray.add((MyPetriPlace) p);
		}
	}

	/**
	 * Create the original marking m0 by the initial marked Place
	 * 
	 * @param markedplace
	 *            The initial marked Place
	 */
	// ��ݳ�ʼ��ʶ�Ŀ���id�������ʼmarking m0,��ʽ��00100000.
	private void buildM0(MyPetriPlace markedplace) {
		String m = "";
		MyPetriPlace p;
		for (int i = 0; i < placearray.size(); i++) {
			p = placearray.get(i);
			if (p.equals(markedplace)) {
				m += "1";
			} else {
				m += "0";
			}
		}

		m0 = new RMGMarkingState(m, RMGObject.MARKINGSTATE);
	}

	/**
	 * Judge whether the marking state is new
	 * 
	 * @param m
	 *            The marking state to be judged.
	 * @return The result judged
	 */
	private boolean isNewMarkingState(RMGMarkingState m) {
		// ��ʶ�Ƿ����±�ʶ
		boolean benew = true;
		for (int i = 0; i < reachmarkinggraph.size(); i++) {
			if (reachmarkinggraph.get(i).isA() == RMGObject.MARKINGSTATE) {
				if (((RMGMarkingState) reachmarkinggraph.get(i)).getstate()
						.equals(m.getstate())) {
					benew = false;
					break;
				}
			}
		}
		return benew;

	}

	/**
	 * Build the reach marking graph by m0
	 */
	// ��Ĭ��m0����ɴ�ͼ
	public void bulidRMG() {
		this.reachmarkinggraph.clear();
		bulidRMG(this.m0);
	}

	/**
	 * Build the reach marking graph by m0
	 * 
	 * @param m0
	 *            The marking state
	 */
	// ���petri��ͳ�ʼ��ʶ������ɴ�ͼ
	// �ݹ��㷨
	private void bulidRMG(RMGMarkingState m0) {

		// ��ʶ�Ƿ����±�ʶ
		if (isNewMarkingState(m0)) {
			this.reachmarkinggraph.add(m0);
		}

		// �ҳ�����ʹ�ܱ�Ǩ
		Vector<MyPetriTransition> enabledtransition = new Vector<MyPetriTransition>();

		MyPetriObject t;

		setMarking(m0);
		for (int i = 0; i < petri.petri.size(); i++) {
			t = petri.petri.get(i);
			if (t.isA() != MyPetriObject.TRANSITION) {
				continue;
			}
			if (petri.beTransitionEnabled(t.getid())) {
				enabledtransition.add((MyPetriTransition) t);
			}
		}

		// ����ÿһ��ʹ�����񣬵õ�ÿһ���±�ʶ����ӻ��������еݹ�
		for (int i = 0; i < enabledtransition.size(); i++) {
			setMarking(m0);
			// ʹ��һ���Ǩ
			petri.executetransition2(enabledtransition.get(i).getid());
			// �õ�һ���±�ʶ
			RMGMarkingState m1 = getMarkingState();
			// ��ӻ�
			String name = enabledtransition.get(i).getname();
			this.reachmarkinggraph.add(new RMGArc(m0.getstate(), m1.getstate(), name, RMGObject.ARC));
			// �ݹ�
			if (isNewMarkingState(m1)) {
				bulidRMG(m1);
			}
		}

	}

	/**
	 * Output the reach marking graph by text
	 */
	// ���ı���ʽ���ɴ�ͼ
	public String toString() {
		String strRMG = "";
		strRMG = "�����б�(" + placearray.size() + "):\r\n";
		MyPetriPlace p;
		for (int i = 0; i < placearray.size(); i++) {
			p = placearray.get(i);
			strRMG += p.getname() + "\r\n";
		}

		strRMG += "��ʶ״̬�б?\r\n";
		RMGObject r;
		for (int i = 0; i < reachmarkinggraph.size(); i++) {
			r = reachmarkinggraph.get(i);
			if (r.isA() == RMGObject.MARKINGSTATE) {
				strRMG += ((RMGMarkingState) r).getstate() + "\r\n";
			}
		}

		strRMG += "ת�ƻ��б?\r\n";

		for (int i = 0; i < reachmarkinggraph.size(); i++) {
			r = reachmarkinggraph.get(i);
			if (r.isA() == RMGObject.ARC) {
				strRMG += ((RMGArc) r).getName() + ":" + ((RMGArc) r).getFrom()
						+ "->" + ((RMGArc) r).getTo() + "\r\n";
			}
		}
		return strRMG;

	}

	/**
	 * Clear all the object
	 */
	// ������б�ʶ
	private void setUnmarking() {
		MyPetriPlace p;
		for (int i = 0; i < placearray.size(); i++) {
			p = placearray.get(i);
			p.empty();
		}
	}

	/**
	 * Mark the MyPetriNet by the marking state m
	 * 
	 * @param m
	 *            The marking state
	 */
	// ��ݱ�ʶm,��ʶPetri��
	private void setMarking(RMGMarkingState m) {
		// ������б�ʶ
		setUnmarking();

		String strmarking;
		strmarking = m.getstate();
		char c;
		for (int i = 0; i < strmarking.length(); i++) {
			c = strmarking.charAt(i);
			if (c == '1') {
				placearray.get(i).addtoken(1);
			}
		}
	}

	/**
	 * Get the current MyPetriNet marking state.
	 * 
	 * @return A RMBMarkingState Object
	 */
	// ���petri��ǰ�ı�ʶ״̬
	private RMGMarkingState getMarkingState() {
		String m = "";
		MyPetriPlace p;
		for (int i = 0; i < placearray.size(); i++) {
			p = placearray.get(i);
			if (p.getmarking() > 0) {
				m += "1";
			} else {
				m += "0";
			}
		}
		return new RMGMarkingState(m, RMGObject.MARKINGSTATE);
	}

	/**
	 * The Arc in the reach marking graph
	 * 
	 * �ɴ�ͼת�ƻ���
	 * 
	 * @author zhp
	 * 
	 */
	public class RMGArc extends RMGObject implements Cloneable {
		private String statefrom;
		private String stateto;
		// private String transitionid;
		private String transitionname;

		public RMGArc(String from, String to, String transitionname, int type) {
			this.statefrom = from;
			this.stateto = to;
			this.transitionname = transitionname;
			this.settype(type);
		}

		public String getFrom() {
			return statefrom;
		}

		public String getTo() {
			return stateto;
		}

		public String getName() {
			return transitionname;
		}

		public Object clone() {
			RMGArc obj = null;

			obj = (RMGArc) super.clone();

			return obj;
		}
	}

	/**
	 * The vertex in the reach marking graph
	 * 
	 * �ɴ�ͼ���㣬����ʶ״̬��
	 * 
	 * @author zhp
	 * 
	 */
	public class RMGMarkingState extends RMGObject implements Cloneable {
		private String markingstate; // ״̬��ʶ

		public RMGMarkingState(String state, int type) {
			this.markingstate = state;
			this.settype(type);
		}

		public void setstate(String state) {
			markingstate = state;
		}

		public String getstate() {
			return markingstate;
		}

		public Object clone() {
			RMGMarkingState obj = null;

			obj = (RMGMarkingState) super.clone();

			return obj;
		}

	}

	/**
	 * The super Class of reach marking graph.
	 * 
	 * �ɴ��ʶͼԪ�ض������
	 * 
	 * @author zhp
	 * 
	 */
	public class RMGObject implements Cloneable {
		public static final int MARKINGSTATE = 1;
		public static final int ARC = 2;

		int type; // �ڵ����� 1:state 2: RMGArc

		public void settype(int type) {
			this.type = type;
		}

		public int isA() {
			return type;
		}

		public Object clone() {
			RMGObject obj = null;
			try {
				obj = (RMGObject) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return obj;
		}

	}

}
