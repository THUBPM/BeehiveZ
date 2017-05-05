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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;
import org.w3c.dom.NodeList;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.DocumentDictionary;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.DocumentItem;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseDictionary;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.PhraseItem;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.RelationDictionary;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.dictionary.RelationItem;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.queryParser.QueryExecution;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.queryParser.QueryResultItem;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.queryParser.Result;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.util.Expression;
import cn.edu.thss.iise.beehivez.util.Parser;

public class InvertedListIndex extends PetriNetIndex {
	private DocumentDictionary docDic;
	private PhraseDictionary phraseDic;
	private RelationDictionary relationDic;

	// the location for index data
	private String folder = "processrepository/index/invertedlistindex";

	public InvertedListIndex() {
		File dir = new File(folder);
		if (!dir.exists()) {
			dir.mkdir();
		}
		try {
			docDic = new DocumentDictionary(folder, 4096);
			phraseDic = new PhraseDictionary(folder, 4096);
			relationDic = new RelationDictionary(folder, 4096);
		} catch (Exception e) {
			e.printStackTrace();
			phraseDic = null;
			docDic = null;
			relationDic = null;
		}
	}

	public boolean create() {
		docDic.create();
		phraseDic.create();
		relationDic.create();
		try {
			DataManager dm = DataManager.getInstance();
			ResultSet rs = dm.executeSelectSQL(
					"select process_id,pnml from petrinet", 0,
					Integer.MAX_VALUE, dm.getFetchSize());
			while (rs.next()) {
				long process_id = rs.getLong("process_id");
				InputStream petrinet_inputstream = (InputStream) rs
						.getAsciiStream("pnml");
				PetrinetObject pno = new PetrinetObject();
				pno.setProcess_id(process_id);
				pno.setPnml(ToolKit
						.getBytesFromInputStream(petrinet_inputstream));
				this.addProcessModel(pno);
				petrinet_inputstream.close();
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 * @return ����true��ʾ��ʼ���ɹ�
	 */
	public void init() {
		String str = this.getClass().getCanonicalName();
		this.javaClassName = str;
		this.name = str.substring(str.lastIndexOf(".") + 1);
		// if (docDic.init() && phraseDic.init() && relationDic.init()) {
		// return true;
		// } else
		// return false;
	}

	/**
	 * for Searcher
	 * 
	 * @param pos
	 * @return
	 * @throws Exception
	 */
	public String getDoc(long pos) throws Exception {
		return docDic.getDoc(pos).value;
	}

	public void putDoc(DocumentInfo d) throws Exception {
		synchronized (this) {
			long docPosition = docDic.getNextDocumentPosition();// putDoc(d.getName());
			for (Field s : d.content) {
				PhraseItem p = phraseDic.exist(s.value);
				if (p == null) {

					long relationPos = relationDic.addRelation(docPosition);
					s.setPos(phraseDic.add(s.value, relationPos));
				} else if (p.getRelationPos() == -1) {
					long relationPos = relationDic.addRelation(docPosition);
					p.setRelationPos(relationPos);
					s.setPos(p.getPosition());

				} else {
					s.setPos(p.getPosition());
					relationDic.appendRelation(p.getRelationPos(), docPosition);
				}
			}
			docDic.putDoc(d);
		}
	}

	public ArrayList<String> getDocuments(long relationPos) {

		ArrayList<String> result = new ArrayList<String>();
		if (relationPos == -1)
			return result;
		try {
			RelationItem r = relationDic.getRelationAt(relationPos);

			while (true) {
				DocumentItem doc = docDic.getDoc(r.getDocumentPos());
				if (!doc.isDelete)
					result.add(doc.value);
				if (r.getNext() == -1)
					break;
				r = relationDic.getRelationAt(r.getNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	/**
	 * for test
	 * 
	 * @param v
	 */
	public void get(String v) {
		PhraseItem p = phraseDic.exist(v);
		if (p == null) {
			System.out.println(v + " never appear!");
		} else {
			ArrayList<String> r = getDocuments(p.getRelationPos());
			System.out.println(p.getValue() + " appear in below documents");
			for (String s : r)
				System.out.println(s);
			System.out.println();
		}
	}

	public ArrayList<String> get(long pos) {
		PhraseItem p = phraseDic.getPhrase(pos);
		if (p != null) {
			return getDocuments(p.getRelationPos());
		}
		return null;
	}

	public ArrayList<String> getDocumentNames(QueryResultItem qri) {
		PhraseItem p = phraseDic.getPhrase(qri.getPosition());
		if (p != null) {
			return getDocuments(p.getRelationPos());
		}
		return null;
	}

	/**
	 * 
	 */
	public void test_get(long pos) {
		PhraseItem p = phraseDic.getPhrase(pos);
		if (p == null) {
			System.out.println(p.getValue() + " never appear!");
		} else {
			ArrayList<String> r = getDocuments(p.getRelationPos());
			System.out.println(p.getValue() + " appear in below documents");
			for (String s : r)
				System.out.println(s);
			System.out.println();
		}
	}

	/***************************************************************************
	 * public void scan_test() { if(phraseDic.index.scanStart()) { PhraseItem
	 * p=phraseDic.index.scanNext(); while(p!=null) {
	 * if(p.getValue().equals("swf")) { int a=1; a++; } ArrayList<String>
	 * r=getDocuments(p.getRelationPos()); System.out.println(p.getValue()+"
	 * appear in below documents"); for(String s:r) System.out.println(s);
	 * System.out.println(); p=phraseDic.index.scanNext(); } } }
	 **************************************************************************/

	public ArrayList<String> search(String v) {
		PhraseItem p = phraseDic.exist(v);
		if (p == null) {
			return null;
		} else {
			ArrayList<String> r = getDocuments(p.getRelationPos());
			return r;
		}
	}

	/***************************************************************************
	 * public ArrayList<QueryResultItem> semanticSearch(String s) { return
	 * phraseDic.semanticSearch(s); }
	 * 
	 * @throws Exception
	 **************************************************************************/

	/**
	 * ��ѯ,��ѯ�����'test' or 'java' or ( 'a' and not 'b')
	 */
	public Result semanticSearch(String query) throws Exception {
		QueryExecution qe = new QueryExecution(query, this);
		return qe.excute();
	}

	public void delete(String doc) {
		synchronized (this) {
			try {
				LinkedList<Long> delete = docDic.delete(doc);
				for (long l : delete) {
					DocumentItem docItem = docDic.getDoc(l);
					long[] phrases = docItem.getPhrases();
					for (long p : phrases) {
						PhraseItem phrase = phraseDic.getPhrase(p);
						long rePos = phrase.getRelationPos();
						if (rePos != -1) {
							RelationItem reItem = relationDic
									.getRelationAt(rePos);
							if (reItem.getDocumentPos() == l)
								phrase.setRelationPos(reItem.getNext());
							else {
								RelationItem r = null;
								rePos = reItem.getNext();
								while (rePos != -1) {
									r = reItem;
									reItem = relationDic.getRelationAt(rePos);
									if (reItem.getDocumentPos() == l) {
										r.setNext(reItem.getNext());
										relationDic.remove(reItem);
										break;
									}
								}
							}
						}
					}
					docDic.remove(docItem);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Scanner getPhraseSanner() {
		return new Scanner(phraseDic);
	}

	/**
	 * �ر�ʱ��û�е���close����ʱ����������ڻ����ж�û��д�����
	 * 
	 * @throws Exception
	 */
	public void close() {
		try {
			phraseDic.close();
			relationDic.close();
			docDic.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������в���Petri���ļ�
	 * 
	 * @param url
	 *            �ļ�·��
	 * @param key
	 *            ��Ҫ����(����Ҫ��������)�Ĺؼ���,���ļ�·�����ļ���
	 */
	public void insert(String url, String key) {
		InputStream in = null;
		try {
			in = new FileInputStream(url);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentInfo d = new DocumentInfo();
		d.setName(key);

		try {
			org.w3c.dom.Document doc = dbf.newDocumentBuilder().parse(in);
			NodeList nl = doc.getDocumentElement().getElementsByTagName("text");
			for (int i = 0; i < nl.getLength(); i++) {
				d.addPhrase(nl.item(i).getTextContent());
			}
			this.putDoc(d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void addProcessModel(Object o) {
		PetrinetObject pno = (PetrinetObject) o;
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setName(String.valueOf(pno.getProcess_id()));
		try {
			PetriNet pn = PetriNetUtil.getPetriNetFromPnmlBytes(pno.getPnml());
			for (Transition t : pn.getTransitions()) {
				docInfo.addPhrase(t.getIdentifier());
			}
			this.putDoc(docInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void delProcessModel(Object pno) {
		System.out.println("delete from index need to be implemented");
	}

	public TreeSet<ProcessQueryResult> getPetriNet(Object o) {
		TreeSet<ProcessQueryResult> ret = new TreeSet<ProcessQueryResult>();

		if (o instanceof String) {
			String str = (String) o;
			Parser parser = new Parser(str);
			try {
				Expression expression = parser.parseQuery();
				HashSet s = query(expression);
				Iterator it = s.iterator();
				while (it.hasNext()) {
					long process_id = ((Long) it.next()).longValue();
					ret.add(new ProcessQueryResult(process_id, 1));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	private HashSet query(Expression e) {
		HashSet ret = new HashSet();

		switch (e.getType()) {
		case Expression.AND:
			ret = query(e.getLeft());
			ret.retainAll(query(e.getRight()));
			break;
		case Expression.OR:
			ret = query(e.getLeft());
			ret.addAll(query(e.getRight()));
			break;
		case Expression.NOT:
			ret = query(e.getLeft());
			ret.removeAll(query(e.getRight()));
			break;
		case Expression.VALUE:
			ret = primitiveQuery(e.getStringValue());
			break;
		}
		return ret;
	}

	private HashSet primitiveQuery(String str) {
		HashSet ret = new HashSet();
		for (String s : this.search(str)) {
			ret.add(Long.valueOf(s));
		}
		return ret;
	}

	@Override
	public boolean open() {
		System.out.println("InvertedListIndex open need to be implemented");
		return true;
	}

	@Override
	public boolean supportGraphQuery() {
		return false;
	}

	@Override
	public boolean supportTextQuery() {
		return true;
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getStorageSizeInMB() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		return getPetriNet(o);
	}

	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return false;
	}

}
