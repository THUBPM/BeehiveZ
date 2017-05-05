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
 * PetriNet.java 09-2-28
 */
package cn.edu.thss.iise.beehivez.server.basicprocess.mymodel;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PNEdge;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;

;

/**
 * 
 * @author He tengfei
 * 
 */
public class MyPetriNet implements Cloneable {

	private int source, sink;

	// PetriNet�����PetriNet=(P,T,F)
	private Vector<MyPetriObject> petri;
	private Vector<MyPetriPlace> placeSet;;
	private Vector<MyPetriArc> arcSet;
	private Vector<MyPetriTransition> transitionSet;
	private Vector<MyPetriTransition> enabledTransitionSet;

	private boolean[] enabledTransitions;

	private int[] currentMarkingVector = null;

	public void setPlaceSet(Vector<MyPetriPlace> placeSet) {
		this.placeSet = placeSet;
	}

	public void setArcSet(Vector<MyPetriArc> arcSet) {
		this.arcSet = arcSet;
	}

	public void setTransitionSet(Vector<MyPetriTransition> transitionSet) {
		this.transitionSet = transitionSet;
	}
	private IncidenceMatrix matrix = null;

	public MyPetriNet() {
		placeSet = new Vector<MyPetriPlace>();
		arcSet = new Vector<MyPetriArc>();
		transitionSet = new Vector<MyPetriTransition>();
		enabledTransitionSet = new Vector<MyPetriTransition>();
		source = -1;
		sink = -1;
	}

	public int[] getCurrentMarkingVector() {
		int placeSize = placeSet.size();
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			currentMarkingVector[placeNo] = ((MyPetriPlace) placeSet
					.get(placeNo)).getCurrenttokens();
		}
		return currentMarkingVector;
	}

	public void initialMarking() {
		int placeSize = placeSet.size();
		currentMarkingVector = new int[placeSize];
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			currentMarkingVector[placeNo] = ((MyPetriPlace) placeSet
					.get(placeNo)).getCurrenttokens();
		}
	}

	public void setCurrentPlaceMarking(int[] marking) {
		int placeSize = placeSet.size();
		for (int i = 0; i < placeSize; i++) {
			placeSet.get(i).setCurrenttokens(marking[i]);
		}
		currentMarkingVector = Arrays.copyOf(marking, marking.length);
	}

	/**
	 * Method to clone a PetriNet Object
	 */
	public Object clone() {
		MyPetriNet obj = null;
		try {
			obj = (MyPetriNet) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Add any PetriNet Object All observers are notified of this change.
	 * 
	 * @param e
	 *            The PetriNet Object to be added.
	 */
	// ��ӳ�Ա
	public void addObject(MyPetriObject e) {
		if (e instanceof MyPetriPlace) {
			placeSet.add((MyPetriPlace) e);
		} else if (e instanceof MyPetriTransition) {
			transitionSet.add((MyPetriTransition) e);
		} else if (e instanceof MyPetriArc) {
			arcSet.add((MyPetriArc) e);
		}
	}

	/**
	 * @return The location of the Source Place in the PetriNet Object Vector
	 */
	// ����Դ����id
	public int getSourcePlace() {
		if (source != -1) {
			return source;
		}

		MyPetriObject p, q;

		int temp;
		String s;
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);
			temp = -1;
			for (int j = 0; j < arcSet.size(); j++) {
				q = arcSet.get(j);
				s = ((MyPetriArc) q).gettargetid();
				if (s.equals(p.getId())) {
					temp = 0;
					break;
				}
			}
			if (temp == -1) {
				return i;
			}
		}
		return -1;
	}

	public int getIndex(MyPetriObject petriobject) {
		if (petriobject instanceof MyPetriPlace) {
			for (int i = 0; i < getPlaceSet().size(); i++) {
				if (petriobject.equals(getPlaceSet().get(i))) {
					return i;
				}
			}
		} else if (petriobject instanceof MyPetriTransition) {
			for (int i = 0; i < getTransitionSet().size(); i++) {
				if (petriobject.equals(getTransitionSet().get(i))) {
					return i;
				}
			}
		} else if (petriobject instanceof MyPetriArc) {
			for (int i = 0; i < getArcSet().size(); i++) {
				if (petriobject.equals(getArcSet().get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return The vector contains all Transitions in the PetriNet Object Vector
	 */
	// ��ȡ��Ǩ����
	public Vector<MyPetriTransition> getTransitionSet() {
		return transitionSet;
	}

	public Vector<MyPetriTransition> getEnabledTransition(int[] markup) {
		enabledTransitionSet.clear();
		enabledTransitions = new boolean[transitionSet.size()];
		this.setCurrentPlaceMarking(markup);
		/*
		 * System.out.println("petri marking:"); for (int i = 0; i <
		 * markup.length; i++) { System.out.print(markup[i]); }
		 * System.out.println();
		 */
		for (int i = 0; i < transitionSet.size(); i++) {
			if (beTransitionEnabled(transitionSet.get(i).getId())) {
				enabledTransitionSet.add(transitionSet.get(i));
				enabledTransitions[i] = true;
			}
		}
		return enabledTransitionSet;
	}

	/**
	 * 
	 * @return The vector contains all Places in the PetriNet Object Vector
	 */
	// ��ȡ�����
	public Vector<MyPetriPlace> getPlaceSet() {
		return placeSet;
	}

	public Vector<MyPetriArc> getArcSet() {
		return arcSet;
	}

	/**
	 * 
	 * @return Sink Place
	 */
	// �����ս����
	public int getSinkPlace() {
		if (sink != -1) {
			return sink;
		}

		MyPetriObject p, q;

		int temp;
		String s;
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);
			temp = -1;
			for (int j = 0; j < arcSet.size(); j++) {
				q = arcSet.get(j);
				s = ((MyPetriArc) q).getsourceid();
				if (s.equals(p.getId())) {
					temp = 0;
					break;
				}
			}
			if (temp == -1) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Add a Source Place to Transitions that don't have a Place input Keep the
	 * PetriNet Structure perfect
	 */
	// ���Դ����֤petrinet�ṹ����
	// �������е�û���������transition,���������һ��Դ����
	public void addSourcePlace() {
		MyPetriPlace sourceplace = new MyPetriPlace("_sourcePlace",
				"Source Place");

		MyPetriObject p, q;
		int count = 0;
		int temp;
		String s;
		for (int i = 0; i < transitionSet.size(); i++) {
			p = transitionSet.get(i);

			temp = -1;
			for (int j = 0; j < arcSet.size(); j++) {
				q = arcSet.get(j);
				if (q.getType() != MyPetriObject.ARC) {
					continue;
				}
				s = ((MyPetriArc) q).gettargetid();
				if (s.equals(p.getId())) {
					temp = 0;
					break;
				}
			}
			if (temp == -1) {
				// ˵��transitionû���������
				if (count == 0) {
					addObject(sourceplace);
				}
				MyPetriArc arc = new MyPetriArc("source_outarc" + count,
						sourceplace.getId(), p.getId());
				addObject(arc);
				count++;
			}

		}
	}

	/**
	 * Add a Sink Place to Transitions that don't have a Place output Keep the
	 * PetriNet Structure perfect
	 */
	// ����ܽ����֤petrinet�ṹ����
	// �������е�û���������transition,���������һ���ܽ����
	public void addSinkPlace() {
		MyPetriPlace sinkplace = new MyPetriPlace("_sinkPlace", "Sink Place");

		MyPetriObject p, q;
		int count = 0;
		int temp;
		String s;
		for (int i = 0; i < transitionSet.size(); i++) {
			p = transitionSet.get(i);

			temp = -1;
			for (int j = 0; j < arcSet.size(); j++) {
				q = arcSet.get(j);

				s = ((MyPetriArc) q).getsourceid();
				if (s.equals(p.getId())) {
					temp = 0;
					break;
				}
			}
			if (temp == -1) {
				// ˵��transitionû��������
				if (count == 0) {
					addObject(sinkplace);
				}
				MyPetriArc arc = new MyPetriArc("sink_inarc" + count,
						p.getId(), sinkplace.getId());
				addObject(arc);
				count++;
			}

		}

	}

	/**
	 * Add a token into a Place
	 * 
	 * @param id
	 *            The Place id
	 */
	// ����������1��token
	public void produceToken(String id) {
		MyPetriObject p;
		String s;
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);

			s = p.getId();
			if (s.equals(id)) {
				((MyPetriPlace) p).addtoken(1);
				break;
			}
		}
	}

	/**
	 * Consume a token into a Place
	 * 
	 * @param id
	 *            The Place id
	 */
	// ������м���1��token
	public void consumeToken(String id) {
		MyPetriObject p;
		String s;
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);
			s = p.getId();
			if (s.equals(id)) {
				((MyPetriPlace) p).addtoken(-1);
				break;
			}
		}
	}

	/**
	 * Clear all tokens, then add a token into the Source Place
	 */
	// ���
	public void marking() {
		// �������token,����source���������һ��token
		MyPetriObject p;
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);
			if (!((MyPetriPlace) p).isempty()) {
				((MyPetriPlace) p).empty();
			}
		}
		//
		((MyPetriPlace) placeSet.get(getSourcePlace())).setCurrenttokens(1);
	}

	/**
	 * Used for transferring a XPDL Object to PetriNet Object
	 * 
	 * @param transtionid
	 *            Id of specified Transition
	 * @return One Input Place of the specified Transition, if none, return null
	 */
	// ���ر�Ǩid��һ������������û�з���null
	// ����XPDL�����petrinet
	public MyPetriPlace getTranstionPlaceIn(String transtionid) {
		MyPetriObject p;
		MyPetriArc r;
		String s, placeid;

		placeid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.gettargetid();
			if (!s.equals(transtionid)) {
				continue;
			}
			placeid = r.getsourceid();
		}

		if (placeid != null) {
			for (int i = 0; i < placeSet.size(); i++) {
				p = placeSet.get(i);
				s = p.getId();
				if (s.equals(placeid)) {
					return (MyPetriPlace) p;
				}
			}

		}
		return null;
	}

	/**
	 * Used for transferring a XPDL Object to a PetriNet Object
	 * 
	 * @param transtionid
	 *            id of specified transition
	 * @return One Output Place of the specified Transition, if none, return
	 *         null
	 */
	// ���ر�Ǩid��һ�����������û�з���null
	// ����XPDL�����petrinet
	public MyPetriPlace getTranstionPlaceOut(String transtionid) {
		MyPetriObject p;
		MyPetriArc r;
		String s, placeid;

		placeid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);

			r = (MyPetriArc) p;
			s = r.getsourceid();
			if (!s.equals(transtionid)) {
				continue;
			}
			placeid = r.gettargetid();
		}

		if (placeid != null) {
			for (int i = 0; i < placeSet.size(); i++) {
				p = placeSet.get(i);

				s = p.getId();
				if (s.equals(placeid)) {
					return (MyPetriPlace) p;
				}
			}

		}
		return null;
	}

	/**
	 * Used for transferring a XPDL Object to PetriNet Object
	 * 
	 * @param placeid
	 *            Id of specified Transition
	 * @return All Input Transitions of the specified Place, if none, return
	 *         null
	 */
	// ���ؿ���id�����е������Ǩ�����û�з���null
	// ����XPDL�����petrinet
	public Vector<MyPetriTransition> getPlaceTransitionIn(String placeid) {
		Vector<MyPetriTransition> petritransition = new Vector<MyPetriTransition>();
		MyPetriObject p;
		MyPetriArc r;
		String s, transitionid;

		transitionid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			if (p.getType() != MyPetriObject.ARC)
				continue;

			r = (MyPetriArc) p;
			s = r.gettargetid();
			if (!s.equals(placeid)) {
				continue;
			}
			transitionid = r.getsourceid();

			if (transitionid != null) {
				for (int j = 0; j < transitionSet.size(); j++) {
					p = transitionSet.get(j);
					s = p.getId();
					if (s.equals(transitionid)) {
						petritransition.addElement((MyPetriTransition) p);
					}
				}
			}
		}
		return petritransition;
	}

	/**
	 * Used for transferring a XPDL Object to PetriNet Object
	 * 
	 * @param placeid
	 *            Id of specified Transition
	 * @return All Output Transitions of the specified Place, if none, return
	 *         null
	 */
	// ���ؿ���id����������Ǩ�����û�з���null
	// ����XPDL�����petrinet
	public Vector<MyPetriTransition> getPlaceTransitionOut(String placeid) {
		Vector<MyPetriTransition> petritransition = new Vector<MyPetriTransition>();
		MyPetriObject p;
		MyPetriArc r;
		String s, transitionid;

		transitionid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.getsourceid();
			if (!s.equals(placeid)) {
				continue;
			}
			transitionid = r.gettargetid();

			if (transitionid != null) {
				for (int j = 0; j < transitionSet.size(); j++) {
					p = transitionSet.get(j);
					s = p.getId();
					if (s.equals(transitionid)) {
						petritransition.addElement((MyPetriTransition) p);
					}
				}
			}
		}
		return petritransition;
	}

	/**
	 * 
	 * @param id
	 *            The id of a Place or a Transition
	 * @return The number of Input Arcs for the specified Place or Transition
	 */
	// ���ؿ���/��Ǩ�����뻡��
	public int getArcIn(String id) {
		int n = 0;
		MyPetriObject p;
		MyPetriArc r;
		String s;

		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.gettargetid();
			if (s.equals(id)) {
				n++;
			}
		}

		return n;
	}

	/**
	 * 
	 * @param id
	 *            The id of a Place or a Transition
	 * @return The number of Output Arcs for the specified Place or Transition
	 */
	// ���ر�Ǩ�����������
	public int getArcOut(String id) {
		int n = 0;
		MyPetriObject p;
		MyPetriArc r;
		String s;

		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.getsourceid();
			if (s.equals(id)) {
				n++;
			}
		}

		return n;
	}

	public MyPetriObject getArcTarget(MyPetriArc arc) {
		String targetid = arc.gettargetid();
		for (int i = 0; i < placeSet.size(); i++) {
			if (targetid.equals(placeSet.get(i).getId())) {
				return placeSet.get(i);
			}
		}
		for (int i = 0; i < transitionSet.size(); i++) {
			if (targetid.equals(transitionSet.get(i).getId())) {
				return transitionSet.get(i);
			}
		}
		return null;
	}

	public MyPetriObject getArcSource(MyPetriArc arc) {
		String souceid = arc.getsourceid();
		for (int i = 0; i < placeSet.size(); i++) {
			if (souceid.equals(placeSet.get(i).getId())) {
				return placeSet.get(i);
			}
		}
		for (int i = 0; i < transitionSet.size(); i++) {
			if (souceid.equals(transitionSet.get(i).getId())) {
				return transitionSet.get(i);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 *            Id of one Transition
	 * @return A boolean value, true means the Transition has been enabled
	 */
	// id�ı�Ǩ�Ƿ��Ѿ�ʹ��
	public boolean beTransitionEnabled(String id) {
		// �ҳ��������뻡,�ж��Ƿ�ÿ�����������token
		// �ҳ����л�
		MyPetriObject p, q;
		MyPetriArc r;
		String s;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.gettargetid();
			if (!s.equals(id)) {
				continue;
			}

			// �ҳ��������

			for (int j = 0; j < placeSet.size(); j++) {
				q = placeSet.get(j);

				s = r.getsourceid();
				if (!s.equals(q.getId())) {
					continue;
				}

				if (((MyPetriPlace) q).isempty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Execute the specified Transition without log
	 * 
	 * @param id
	 *            Id of the Transition
	 */
	// ִ��id�ı�Ǩ,��д��־
	public void executeTransition(String id) {
		// �ҳ��������뻡,token-1
		// �ҳ��������, token+1
		// �����Լ���ִ�к���
		MyPetriPlace place;
		MyPetriArc arc;
		String sourceid;
		String targetid;
		for (int i = 0; i < arcSet.size(); i++) {
			arc = arcSet.get(i);
			targetid = arc.gettargetid();
			sourceid = arc.getsourceid();
			if (targetid.equals(id)) {
				// �ҳ��������
				for (int j = 0; j < placeSet.size(); j++) {
					place = placeSet.get(j);
					if (place.getId().equals(sourceid)) {
						place.addtoken(-1);
						break;
					}
				}
			}
			if (sourceid.equals(id)) {
				// �ҳ�������
				for (int j = 0; j < placeSet.size(); j++) {
					place = placeSet.get(j);
					if (place.getId().equals(targetid)) {
						place.addtoken(1);
						break;
					}
				}
			}
		}
	}

	public void run() {
		int caseid = 0;

		// ��Դ�����ǿ�ʼ�������}���
		/*
		 * if ( ( (PetriPlace) petri.get(getSourcePlace())).isempty()) { return; }
		 */
		// log case id
		caseid++;
		Vector<MyPetriTransition> exev = new Vector<MyPetriTransition>();
		Random rand = new Random();

		// ��ת10000��ǿ�ƽ���Ԥ������ѭ���ͻ������
		for (int step = 0; step < 10000; step++) {
			/*
			 * //������� if (!((PetriPlace) petri.get(getsinkplace())).isempty()) {
			 * return; }
			 */
			// �ҳ�����ʹ�ܱ�Ǩ
			MyPetriObject p;

			for (int i = 0; i < transitionSet.size(); i++) {
				p = transitionSet.get(i);

				if (beTransitionEnabled(p.getId())) {
					// executeTransition(p.getid());
					// break;
					exev.add((MyPetriTransition) p);
				}
			}
			// �ȸ���ִ��ʹ�ܱ�Ǩ
			if (exev.size() > 0) {
				// int j=exev.size();
				int i = rand.nextInt((exev.size()));
				MyPetriTransition ep = (MyPetriTransition) exev.get(i);
				executeTransition(ep.getId());
			} else {
				// û��ʹ�����񣬿��ܽ����������
				break;
			}
			// ��տ�ִ�б�Ǩ����
			exev.removeAllElements();
		}
	}

	/**
	 * Export the PetriNet Object to a PNML File
	 * 
	 * @param exportFileName
	 *            the Filename exported
	 */
	// ��petri��������Ϊpnml��ʽ���ļ�
	//
	public void export_pnml(String exportFileName) {

		// �����ĵ�
		Element root;
		root = new Element("pnml");
		Document docJDOMexp = new Document(root);

		Element net = new Element("net");
		net.setAttribute("id", "workflownet");
		net.setAttribute("type",
				"http://www.informatik.hu-berlin.de/top/pnml/basicPNML.rng");
		root.addContent(net);

		Element place, transition, arc;
		Element name, value;
		MyPetriObject p;

		// ֱ�Ӷ�petri��������Ԫ�ؽ��в���
		for (int i = 0; i < placeSet.size(); i++) {
			p = placeSet.get(i);
			place = new Element("place");
			place.setAttribute("id", p.getId());
			name = new Element("name");
			value = new Element("value");
			value.setText(p.getName());
			name.addContent(value);

			place.addContent(name);

			net.addContent(place);
		}
		for (int i = 0; i < transitionSet.size(); i++) {
			p = transitionSet.get(i);

			transition = new Element("transition");
			transition.setAttribute("id", p.getId());
			name = new Element("name");
			value = new Element("value");
			value.setText(p.getName());
			name.addContent(value);
			transition.addContent(name);
			net.addContent(transition);
		}
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			arc = new Element("arc");
			arc.setAttribute("id", p.getId());
			arc.setAttribute("source", ((MyPetriArc) p).getsourceid());
			arc.setAttribute("target", ((MyPetriArc) p).gettargetid());
			net.addContent(arc);
		}
		try {
			XMLOutputter XMLOut = new XMLOutputter();
			// XMLOut.setEncoding("gb2312");
			XMLOut.output(docJDOMexp, new FileOutputStream(exportFileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//wwx
	public void removeObject(String getid) {
		// TODO Auto-generated method stub
		for (int i = 0; i < placeSet.size(); i++) {
			if (placeSet.get(i).getId().equals(getid)) {
				placeSet.remove(i);
			}
		}
		for (int i = 0; i < transitionSet.size(); i++) {
			if (transitionSet.get(i).getId().equals(getid)) {
				transitionSet.remove(i);
			}
		}
		for (int i = 0; i < arcSet.size(); i++) {
			if (arcSet.get(i).getId().equals(getid)) {
				arcSet.remove(i);
			}
		}
	}

	public static MyPetriNet PromPN2MyPN(PetriNet promPetri) {
		// TODO Auto-generated method stub
		MyPetriNet petri = new MyPetriNet();
		ArrayList<Transition> transitions = null;
		ArrayList<Place> places = null;
		ArrayList<PNEdge> edges = null;
		transitions = promPetri.getTransitions();
		places = promPetri.getPlaces();
		edges = promPetri.getEdges();
		Transition t = null;
		String name = null;
		Long idkey = -1L;
		for (int i = 0; i < transitions.size(); i++) {
			t = transitions.get(i);
			name = t.getIdentifier();
			if(t.isInvisibleTask())
				  name = "";
			idkey = t.getIdKey();
			MyPetriTransition tempMyPT = new MyPetriTransition("t_"
					+ idkey.toString(), name, null);
			petri.addObject(tempMyPT);
		}
		if (hasNoMarking(places)) {
			setInitialMarking(places);
		}
		Place p;
		for (int i = 0; i < places.size(); i++) {
			p = places.get(i);
			name = p.getIdentifier();
			idkey = p.getIdKey();
			int initialMarking = p.getNumberOfTokens();
			MyPetriPlace tempMyPP = new MyPetriPlace("s_" + idkey.toString(),
					name);
			tempMyPP.setInitialtokens(initialMarking);
			tempMyPP.setCurrenttokens(initialMarking);
			petri.addObject(tempMyPP);
		}

		PNEdge e;
		for (int i = 0; i < edges.size(); i++) {
			e = edges.get(i);
			idkey = e.getIdKey();
			ModelGraphVertex source = e.getSource();
			ModelGraphVertex target = e.getDest();
			String sourceid = null, targetid = null;
			if (source instanceof Place) {
				sourceid = "s_" + source.getIdKey().toString();
				targetid = "t_" + target.getIdKey().toString();
			} else {
				sourceid = "t_" + source.getIdKey().toString();
				targetid = "s_" + target.getIdKey().toString();
			}
			// System.out.println("sourceid: "+sourceid+" targetid: "+targetid);
			MyPetriArc tempMyPA = new MyPetriArc(idkey.toString(), sourceid,
					targetid);
			petri.addObject(tempMyPA);
		}
		petri.initialMarking();
		petri.setIncidenceMatrix(promPetri.getPTMatrix());
		return petri;
	}

	public void setIncidenceMatrix(int matrix[][]) {
		this.matrix = new IncidenceMatrix(matrix);
	}

	private static void setInitialMarking(ArrayList<Place> places) {
		// TODO Auto-generated method stub
		Place p;
		HashSet set;
		for (int i = 0; i < places.size(); i++) {
			p = places.get(i);
			set = p.getPredecessors();
			if (set.size() <= 0) {
				p.addToken(new Token());
			}
		}
	}

	private static boolean hasNoMarking(ArrayList<Place> places) {
		// TODO Auto-generated method stub
		Place p;
		int initialMarking = 0;
		for (int i = 0; i < places.size(); i++) {
			p = places.get(i);
			initialMarking = p.getNumberOfTokens();
			if (initialMarking > 0) {
				return false;
			}
		}
		return true;
	}

	public int[] getTVector(Vector<MyPetriTransition> process) {
		// TODO Auto-generated method stub
		int tvector[] = new int[transitionSet.size()];
		for(int i=0;i<process.size();i++){
			for(int j=0;j<transitionSet.size();j++){
				if(transitionSet.get(j).equals(process.get(i))){
					tvector[j] +=1;
					break;
				}
			}
		}
		return tvector;
	}

	public int[] getCX(int[] x) {
		// TODO Auto-generated method stub
		return matrix.multiplyX(x);
	}
	public Vector<MyPetriPlace> getAllTranstionPlaceIn(String transtionid) {
		MyPetriObject p;
		MyPetriArc r;
		String s, placeid;
		Vector<MyPetriPlace> petriplace = new Vector<MyPetriPlace>();

		placeid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);
			r = (MyPetriArc) p;
			s = r.gettargetid();
			if (!s.equals(transtionid)) {
				continue;
			}
			placeid = r.getsourceid();
			if (placeid != null) {
				for (int j = 0; j < placeSet.size(); j++) {
					p = placeSet.get(j);
					s = p.getId();
					if (s.equals(placeid)) {
						petriplace.addElement((MyPetriPlace) p);
					}
				}

			}
		}

		
		return petriplace;
	}
	public Vector<MyPetriPlace> getAllTranstionPlaceOut(String transtionid) {
		MyPetriObject p; 
		MyPetriArc r;
		String s, placeid;
		Vector<MyPetriPlace> petriplace = new Vector<MyPetriPlace>();

		placeid = null;
		for (int i = 0; i < arcSet.size(); i++) {
			p = arcSet.get(i);

			r = (MyPetriArc) p;
			s = r.getsourceid();
			if (!s.equals(transtionid)) {
				continue;
			}
			placeid = r.gettargetid();
			if (placeid != null) {
				for (int j = 0; j < placeSet.size(); j++) {
					p = placeSet.get(j);

					s = p.getId();
					if (s.equals(placeid)) {
						petriplace.addElement((MyPetriPlace) p);
					}
				}

			}
		}

		
		return petriplace;
	}
	/**
	 * 对变迁向量按照变迁的标签值进行排序
	 */
	public void sort() {
		// TODO Auto-generated method stub
		Arrays.sort(transitionSet.toArray());
	}
	
	public void addPlace(MyPetriPlace p){
		placeSet.add(p);
	}
	
	public void addTransition(MyPetriTransition pt){
		transitionSet.add(pt);
	}
	
	public void addArc(MyPetriArc arc){
		arcSet.add(arc);
	}
	
	// 删除与id相连的所有边
	public void deleteArc(String cellid) {
		/*for (int i = 0; i < arcVector.size(); i++) {
			PetriArc arc = arcVector.get(i);
			if (arc.getsourceid().trim().equals(cellid)) {
				arcVector.remove(i);
				System.out.println("删除了边: "+arc.getsourceid()+arc.gettargetid());
			}
		}
		for (int i = 0; i < arcVector.size(); i++) {
			PetriArc arc = arcVector.get(i);
			if (arc.gettargetid().trim().equals(cellid)) {
				arcVector.remove(i);
				System.out.println("删除了边: "+arc.getsourceid()+arc.gettargetid());
			}
		}*/
		Vector<MyPetriArc> temp = new Vector<MyPetriArc>();
		for (int i = 0; i < arcSet.size(); i++) {
			MyPetriArc arc = arcSet.get(i);
			if (!arc.getsourceid().trim().equals(cellid) && !arc.gettargetid().trim().equals(cellid)) {
				temp.add(arc);
			}
		}
		arcSet = temp;
	}
	
	
	public void addArc(MyPetriObject source, MyPetriObject target) {
		if(source instanceof MyPetriTransition)
		{
			((MyPetriTransition)source).addSuccessor((MyPetriPlace)target);
			((MyPetriPlace)target).addPredecessor((MyPetriTransition)source);
		}
		else
		{
			((MyPetriPlace)source).addSuccessor((MyPetriTransition)target);
			((MyPetriTransition)target).addPredecessor((MyPetriPlace)source);			
		}
		arcSet.add(new MyPetriArc(source.getId(), target.getId()));
	}
	
	// 增加一条边
	public void addArc(String sourceid, String targetid) {
		arcSet.add(new MyPetriArc(sourceid, targetid));
	}

	// 删除一个库所，删除库所以及和库所相连的所有边
	public void deletePlace(String cellid) {
		for (int i = 0; i < placeSet.size(); i++) {
			if (placeSet.get(i).getId().equals(cellid)) {
				placeSet.remove(i);
			}
		}
		deleteArc(cellid);
	}

	public void deletePlace(MyPetriPlace place) {
		for(MyPetriTransition trans : transitionSet)
		{
			trans.deletePredecessor(place);
			trans.deleteSuccessor(place);
		}
		deleteArc(place.getId());
		placeSet.remove(place);
	}

	// 删除库所
	public void deleteTransition(String cellid) {
		for (int i = 0; i < transitionSet.size(); i++) {
			if (transitionSet.get(i).getId().equals(cellid)) {
				transitionSet.remove(i);
			}
		}
		deleteArc(cellid);
	}

	public void deleteTransition(MyPetriTransition pt) {
		//处理所有place的前驱和后续
		for(MyPetriPlace place : placeSet)
		{
			place.deletePredecessor(pt);
			place.deleteSuccessor(pt);
		}
		deleteArc(pt.getId());
		System.out.println("删除了变迁"+pt.toString()+" "+pt.getId());
		transitionSet.remove(pt);	
	}
	public Vector<MyPetriObject> getPetri() {
		return petri;
	}

	public void setPetri(Vector<MyPetriObject> petri) {
		this.petri = petri;
	}

}
