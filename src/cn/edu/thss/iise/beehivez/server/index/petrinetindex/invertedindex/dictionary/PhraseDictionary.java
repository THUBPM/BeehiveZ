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

import java.io.File;
import java.io.IOException;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.AVLTree;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.AVLTreeNode;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.semanticSimilarity.SemanticSimilarity;

public class PhraseDictionary extends AVLTree {
	// public ListIndex index;

	private Cache cache;
	// private long rootPosition;
	private long unusedPosition;

	private static SemanticSimilarity sim = null;

	private String folder; // �����ŵĸ�Ŀ¼
	private int cacheSize;

	public PhraseDictionary(String folder, int cacheSize) throws Exception {
		if (!folder.endsWith("//"))
			folder += "//";
		this.folder = folder;
		this.cacheSize = cacheSize;
	}

	public void create() {
		String path = folder + "phraseDictionary.pdi";
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			cache = new Cache(path, cacheSize, Cache.type_phrase);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rootPosition = -1;
		unusedPosition = 16;
		if (sim == null)
			sim = new SemanticSimilarity();
	}

	public boolean init() {
		// TODO Auto-generated method stub
		String indexFile = folder + "phraseDictionary.pdi";
		File file = new File(indexFile);
		if (!file.exists())
			return false;
		try {
			cache = new Cache(indexFile, cacheSize, Cache.type_phrase);
			readInfo();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void writeInfo() {
		try {
			cache.writeInfo(new long[] { rootPosition, unusedPosition });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readInfo() {
		try {
			long[] value = cache.readInfo(2);
			rootPosition = value[0];
			unusedPosition = value[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PhraseItem exist(String v) {
		// root=getNode(rootPosition);
		return (PhraseItem) search(v);
	}

	/***************************************************************************
	 * public ArrayList<QueryResultItem> semanticSearch(String s) {
	 * 
	 * ArrayList<QueryResultItem> result=new ArrayList<QueryResultItem>();
	 * if(rootPosition==-1) return result;
	 * semanticCompare(result,rootPosition,s); return result; }
	 * 
	 * private void semanticCompare(ArrayList<QueryResultItem> arr,long
	 * pos,String key) { PhraseItem p=getPhrase(pos); String
	 * string=p.getValue(); double value=sim.semanticCompare(key,string );
	 * if(value>=SemanticSimilarity.availableValue) { arr.add(new
	 * QueryResultItem(p.getPosition(),value)); } long left=p.getLeftChild();
	 * if(left!=-1) semanticCompare(arr,left,key); long right=p.getRightChild();
	 * if(right!=-1) semanticCompare(arr,right,key); }
	 **************************************************************************/

	public long add(String v, long relationP) throws Exception {
		PhraseItem p = new PhraseItem(v, unusedPosition, relationP, true);
		cache.put(p);
		this.insert(p);
		unusedPosition += p.getLength();
		return p.getPosition();
	}

	public static double semanticCompare(String w1, String w2) {
		synchronized (sim) {
			return sim.semanticCompare(w1, w2);
		}
	}

	/***************************************************************************
	 * public PhraseItem exist(String v) { if(index.scanStart()) { PhraseItem
	 * p=index.scanNext(); while(p!=null) { if(p.getValue().equals(v)) return p;
	 * p=index.scanNext(); } } return null; }
	 * 
	 * public long add(String v,long rp) throws PhraseCannotAdd { return
	 * index.putPhrase(v, rp); }
	 **************************************************************************/

	public void close() throws Exception {
		writeInfo();
		cache.close();
	}

	public PhraseItem getPhrase(long pos) {
		try {
			return (PhraseItem) cache.get(pos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public long getUnused() {
		return unusedPosition;
	}

	@Override
	protected AVLTreeNode getNode(long pos) {
		// TODO Auto-generated method stub
		if (pos == -1)
			return null;
		else
			try {
				return (PhraseItem) cache.get(pos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}

}
