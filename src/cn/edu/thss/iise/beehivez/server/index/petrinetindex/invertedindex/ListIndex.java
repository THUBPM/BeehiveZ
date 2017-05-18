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
 * ��������ṹ,�ѷ���
 */

public class ListIndex {
	/**
	 * private Cache cache;
	 * 
	 * private long head; private long tail;
	 * 
	 * private long scanIndex;
	 * 
	 * 
	 * 
	 * public ListIndex(String path) throws Exception { boolean create=!(new
	 * File(path).exists()); cache=new Cache(path,512,Cache.type_phrase);
	 * if(create) { head=16; tail=16; writeInfo(); } else { readInfo(); } }
	 * 
	 * public long putPhrase(String value,long relationP) { PhraseItem p=null;
	 * 
	 * 
	 * if(head==tail) p=new PhraseItem(value,tail,relationP,-1,true); else p=new
	 * PhraseItem(value,tail,relationP,head,true); head=p.getPosition();
	 * tail+=p.getLength(); try { cache.put(p); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); //throw new
	 * PhraseCannotAdd(); } return p.getPosition(); }
	 * 
	 * public boolean delete(String word,String document) { return false; }
	 * 
	 * public PhraseItem getHead() { if(head==tail) return null; PhraseItem
	 * p=null; try { p = (PhraseItem)cache.get(head); } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } return p; }
	 * 
	 * public boolean scanStart() { if(head==tail) return false; scanIndex=head;
	 * return true; }
	 * 
	 * public PhraseItem scanNext() { if(scanIndex==-1) return null; PhraseItem
	 * p=null; try { p = (PhraseItem)cache.get(scanIndex); } catch (Exception e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); return null; }
	 * scanIndex=p.getNext(); return p; }
	 * 
	 * public void testScan() { PhraseItem p; try { p = getHead();
	 * while(p!=null) { System.out.println(p.getValue()); if(p.getNext()==-1)
	 * break; p=(PhraseItem)cache.get(p.getNext()); } } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 * private void readInfo() { try { long[] value=cache.readInfo(2);
	 * head=value[0]; tail=value[1]; } catch(IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * 
	 * 
	 * private void writeInfo() { try { cache.writeInfo(new long[]{head,tail});
	 * } catch(IOException e) { e.printStackTrace(); } }
	 * 
	 * public void close() throws Exception { writeInfo(); cache.close(); }
	 **/
}
