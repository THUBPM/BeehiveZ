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

package cn.edu.thss.iise.beehivez.server.index.mcmillanindex;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.semanticweb.kaon2.server.rmi.t;

import com.sun.istack.internal.FinalArrayList;

import att.grappa.Element;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriObject;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;


public class Searching {

	boolean node1isPlace=false;
	boolean node2isPlace=false;
	boolean S1ProcedeS2=false;
	boolean S2ProcedeS1=false;
	boolean ifPath=false;
	Vector<MyPetriPlace>placeNode1=null;
	Vector<MyPetriPlace>placeNode2=null;
	Vector<MyPetriTransition>transitionNode1=null;
	Vector<MyPetriTransition>transitionNode2=null;
	int placeNode1length=0;
	int placeNode2length=0;
	int transitionNode1length=0;
	int transitionNode2length=0;
	int CutLength=0;
	int sourceCutLength=0;
	Collection<HashMap<ONEvent, String>> transitionCutArray;
	Vector<ONEvent>Cut=null;
	Vector<ONEvent>sourceCut=null;
	Set<MyPetriTransition>keyset;
	MyPetriNet prefixNew;
	private Set<ONEvent> transitionCutArray1;
	HashMap<ONEvent, String> temp;
	public boolean search(String s1,String s2,MyPetriNet prefix,ONCompleteFinitePrefix PrefixTpcfp){
		temp=new HashMap<ONEvent,String>();
		placeNode1=new Vector<MyPetriPlace>();
		placeNode2=new Vector<MyPetriPlace>();
		transitionNode1=new Vector<MyPetriTransition>();
		transitionNode2=new Vector<MyPetriTransition>();
		Cut=new Vector<ONEvent>();
		sourceCut=new Vector<ONEvent>();
		Set<ONEvent>keyset=new HashSet<ONEvent>();
		MyPetriPlace p;
		MyPetriTransition t;
		prefixNew=new MyPetriNet();
		prefixNew=prefix;
		//�ҵ��ڵ�1����������vector��
		for(int i=0;i<prefix.getPlaceSet().size();i++){
			if(prefix.getPlaceSet().get(i).getName().equalsIgnoreCase(s1)){
				p=prefix.getPlaceSet().get(i);
				placeNode1.addElement(p);
				node1isPlace=true;
			}
		}
		for(int i=0;i<prefix.getTransitionSet().size();i++){
			if(prefix.getTransitionSet().get(i).getName().equalsIgnoreCase(s1)){
				t=prefix.getTransitionSet().get(i);
				transitionNode1.addElement(t);
				node1isPlace=false;
			}
		}
		//�ҵ���2��ڵ�
		for(int i=0;i<prefix.getPlaceSet().size();i++){
			if(prefix.getPlaceSet().get(i).getName().equalsIgnoreCase(s2)){
				placeNode2.addElement( prefix.getPlaceSet().get(i));
				node2isPlace=true;
			}
		}
		for(int i=0;i<prefix.getTransitionSet().size();i++){
			if(prefix.getTransitionSet().get(i).getName().equalsIgnoreCase(s2)){
				transitionNode2.addElement(prefix.getTransitionSet().get(i));
				node2isPlace=false;
			}
		}
		//����vector�е�elementCount
		for(int i=0;i<placeNode1.size();i++){
			if(placeNode1.get(i)!=null){
				placeNode1length++;
			}
			else break;
		}
		for(int i=0;i<placeNode2.size();i++){
			if(placeNode2.get(i)!=null){
				placeNode2length++;
			}
			else break;
		}
		for(int i=0;i<transitionNode1.size();i++){
			if(transitionNode1.get(i)!=null){
				transitionNode1length++;
			}
			else break;
		}
		for(int i=0;i<transitionNode2.size();i++){
			if(transitionNode2.get(i)!=null){
				transitionNode2length++;
			}
			else break;
		}
		//�õ�prefix���У�����Cut��;�õ��е�ԭ��Ǩ������sourceCut��
		transitionCutArray=PrefixTpcfp.getTemporalOrder().values();
		Iterator it=transitionCutArray.iterator();
		while(it.hasNext()){
			temp=(HashMap<ONEvent,String>)it.next();
			transitionCutArray1=temp.keySet();
			Iterator its=transitionCutArray1.iterator();
			while(its.hasNext()){
			    ONEvent one = (ONEvent) its.next();
			    //MyPetriTransition mpt= one.getTrans();
				Cut.add(one);
			}

		}		
		for(int i=0;i<Cut.size();i++){
			if(Cut.get(i)!=null){
				CutLength++;
			}
			else break;
		}
		keyset=PrefixTpcfp.getTemporalOrder().keySet();
		Iterator itss=keyset.iterator();
		while(itss.hasNext()){
			ONEvent onee = (ONEvent) itss.next();
		    //MyPetriTransition mptt= onee.getTrans();
			sourceCut.addElement(onee);
		}

		for(int i=0;i<sourceCut.size();i++){
			if(sourceCut.get(i)!=null){
				
				sourceCutLength++;
			}
			else break;
		}		
		//�ҵ��е㱻˭�еģ�����µĽṹ
//		for(int p1=0;p1<CutLength;p1++){
//			for(int q1=0;q1<sourceCutLength;q1++){
//				if(PrefixTpcfp.getTransCut(sourceCut.get(q1)).containsKey(Cut.get(p1))){
//					//MyPetriArc e = new MyPetriArc(null,Cut.get(p1).getId(),sourceCut.get(q1).getId());
//					MyPetriTransition cut = Cut.get(p1).getTrans();
//					MyPetriTransition sourcecut = sourceCut.get(q1).getTrans();
//					MyPetriArc e = new MyPetriArc(null,cut.getId(),sourcecut.getId());
//					e.setsourceid(Cut.get(p1).getId());
//					e.settargetid(sourceCut.get(q1).getId());
//					prefixNew.getArcSet().add(e);
//				}
//			}
//		}

		//s1,s2����Place������vector�е�ÿ��s1,s2,s1����ң�s2��ǰ�ң��Ƿ����غ�
		if(node1isPlace==true && node2isPlace==true){
			//����placeNode1
			for(int j=0;j<placeNode1length;j++){
				String IDnode1=placeNode1.get(j).getId();
				//����placeNode2
				for(int k=0;k<placeNode2length;k++){
					String IDnode2=placeNode2.get(k).getId();
					if(Path(IDnode1,IDnode2,prefixNew)==true){
						ifPath=true;
						break;
					}
					else continue;
				}
				if(ifPath==true){
					S1ProcedeS2=true;
					break;
				}
				else continue;
			}
			//����s2��s1�Ƿ���·��
	/*		if(ifPath==false){
				for(int j=0;j<placeNode2length;j++){
					String IDnode1=placeNode2.get(j).getId();
					//����placeNode2
					for(int k=0;k<placeNode1length;k++){
						String IDnode2=placeNode1.get(k).getId();
						if(Path(IDnode1,IDnode2,prefixNew,IDnode1)==true){
							ifPath=true;
							//s2������s1����s2����ң�s1��ǰ�ң����ҵ���ѭ�����}��ڵ�
							for(int i=0;i<CutLength;i++){
								//���s2��һ������һ��·
								if(Path(IDnode1,Cut.get(i).getId(),prefixNew,IDnode1)==true){
									//�ҵ��ж�Ӧ��ԭ��Ǩ
									for(int q=0;q<sourceCutLength;q++){
										String cutID=PrefixTpcfp.getTemporalOrder().get(sourceCut.get(q)).getId();
										if(cutID==Cut.get(i).getId()){
											if(Path(IDnode2,sourceCut.get(q).getId(),prefixNew,IDnode2)==true){
												S1ProcedeS2=true;
												break;
											}
											else continue;
										}
									}
								}
								//���s2��һ����û��·��
								else continue;
							}
						}
						else continue;
					}
					if(ifPath==true && S1ProcedeS2==false){
						S2ProcedeS1=true;
						//break;
					}
					else continue;
				}
			}	
	*/	}
		
		
		//s1��transition��s2��place
		else if(node1isPlace==false && node2isPlace==true){
			//�ж�s1��s2�Ƿ���·��������transitionNode1
			for(int j=0;j<transitionNode1length;j++){
				String IDnode1=transitionNode1.get(j).getId();
				//����placeNode2
				for(int k=0;k<placeNode2length;k++){
					String IDnode2=placeNode2.get(k).getId();
					if(Path(IDnode1,IDnode2,prefixNew)==true){
						ifPath=true;
						break;
					}
					else continue;
				}
				if(ifPath==true){
					S1ProcedeS2=true;
					break;
				}
				else continue;
			}
			//����s2��s1�Ƿ���·��
			/*/if(ifPath==false){
				for(int j=0;j<placeNode2length;j++){
					String IDnode1=placeNode2.get(j).getId();
					//����transitionNode1
					for(int k=0;k<transitionNode1length;k++){
						String IDnode2=transitionNode1.get(k).getId();
						if(Path(IDnode1,IDnode2,prefixNew,IDnode1)==true){
							ifPath=true;
							break;
						}
						else continue;
					}
					if(ifPath==true){
						S2ProcedeS1=true;
						break;
					}
					else continue;
				}
			}	
		*/}
		//s1��place��s2��transition
		else if(node1isPlace==true && node2isPlace==false){
			//�ж�s1��s2�Ƿ���·��������placeNode1
			for(int j=0;j<placeNode1length;j++){
				String IDnode1=placeNode1.get(j).getId();
				//����transitionNode2
				for(int k=0;k<transitionNode2length;k++){
					String IDnode2=transitionNode2.get(k).getId();
					if(Path(IDnode1,IDnode2,prefixNew)==true){
						ifPath=true;
						break;
					}
					else continue;
				}
				if(ifPath==true){
					S1ProcedeS2=true;
					break;
				}
				else continue;
			}
			//����s2��s1�Ƿ���·��
			/*/if(ifPath==false){
				for(int j=0;j<transitionNode2length;j++){
					String IDnode1=transitionNode2.get(j).getId();
					//����placeNode1
					for(int k=0;k<placeNode1length;k++){
						String IDnode2=placeNode1.get(k).getId();
						if(Path(IDnode1,IDnode2,prefixNew,IDnode1)==true){
							ifPath=true;
							break;
						}
						else continue;
					}
					if(ifPath==true){
						S2ProcedeS1=true;
						break;
					}
					else continue;
				}
			}	
		/*/}
		else if(node1isPlace==false && node2isPlace==false){
			//����transitionNode1
			for(int j=0;j<transitionNode1length;j++){
				String IDnode1=transitionNode1.get(j).getId();
				//����transitionNode2
				for(int k=0;k<transitionNode2length;k++){
					String IDnode2=transitionNode2.get(k).getId();
					if(Path(IDnode1,IDnode2,prefixNew)==true){
						ifPath=true;
						break;
					}
					else continue;
				}
				if(ifPath==true){
					S1ProcedeS2=true;
					break;
				}
				else continue;
			}
		}
		if(ifPath==true && S1ProcedeS2==true){
			return true;
		}
		else return false;
		
	}
	//�㷨���ж϶��㵽��һ�����Ƿ���һ��·��;
	private HashSet<String> visited = null;
	private Boolean pathHelper(String node1ID, String node2ID,MyPetriNet net) {
		visited.add(node1ID);
		if (node1ID.equals(node2ID)) {
			return true;
		}
		Vector<MyPetriArc> arcs = net.getArcSet();
		Vector<String> targets = new Vector<String>();
		for (MyPetriArc arc : arcs) {
			if (arc.getsourceid().equalsIgnoreCase(node1ID)) {
				targets.add(arc.gettargetid());
			}
		}
		for (String targetId : targets) {
			if (visited.contains(targetId)) {
				continue;
			}
			if (pathHelper(targetId, node2ID, net)) {
				return true;
			}
		}
		return false;
	}
	public Boolean Path(String node1ID,String node2ID,MyPetriNet net){
		visited = new HashSet<String>();
		Boolean result = pathHelper(node1ID, node2ID, net);
		visited = null;
		return result;
	}
	/*public Boolean Path(String node1ID,String node2ID,MyPetriNet net,String keepNode1){
		Vector<MyPetriArc>arc=null;
		arc=new Vector<MyPetriArc>();
		int ArcSetLength=0;
		int ArcLength=0;
		for(int j=0;j<net.getArcSet().size();j++){
			if(net.getArcSet().get(j)!=null){
				ArcSetLength++;
			}
			else break;
		}
		if(node1ID==node2ID){
			return true;
		}
		else{
			for(int i=0;i<ArcSetLength;i++){
				if(net.getArcSet().get(i).getsourceid().equalsIgnoreCase(node1ID)){
					arc.addElement(net.getArcSet().get(i));
				}
				else continue;
			}
			for(int j=0;j<arc.size();j++){
				if(arc.get(j)!=null){
					ArcLength++;
				}
				else break;
			}
			for(int k=0;k<ArcLength;k++){
				node1ID=arc.get(k).gettargetid();
				if(node2ID!=keepNode1){
					return(Path(node1ID,node1ID,net,keepNode1));
				}
				else break;
			}
					
		}
		return false;	
	}*/

	/**
	 * @param args
	 */
//	public static void main(String[] args) throws IOException{
//		// TODO Auto-generated method stub
//		MyPetriNet input = null;
//		McMillanIndex mcmillan = new McMillanIndex();
//		Searching sss=new Searching();
//		FileInputStream fin = null;
//		try {
//			fin = new FileInputStream("7.pnml");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		PnmlImport pImport = new PnmlImport();
//		PetriNetResult pnr = null;
//		try {
//			pnr = (PetriNetResult) pImport.importFile(fin);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			fin.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		PetriNet pn = pnr.getPetriNet();
//		//pn.setIdentifier("1258790072312.pnml");
//		input=MyPetriNet.PromPN2MyPN(pn);
//		MyPetriNet newPetriNet=new MyPetriNet();
//		newPetriNet=mcmillan.McMillanExe(input).getMpn();
//		newPetriNet.export_pnml("Output6.pnml");
//		boolean result=sss.search("T0","P5",newPetriNet,mcmillan.McMillanExe(input));
//		if(result==true)
//		System.out.println("true");
//		else System.out.println("false");
//		
//	}
}
