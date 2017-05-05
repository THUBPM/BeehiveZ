/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: wangwenxingbuaa@gmail.com 
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
package cn.edu.thss.iise.beehivez.server.metric.tar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.semanticweb.kaon2.ar;
import org.semanticweb.kaon2.tc;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCondition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ExtensiveTAR {
	private ONCompleteFinitePrefix _cfp = null;
	private int size = 0;
	private AdjacentRelation _ar = null;
	private ConcurrentRelation _cr = null;
	private TransitiveClosure _tc = null;
	
	public HashMap<String, HashSet<String>> _tar = null;
	public HashMap<String, HashSet<String>> _tar0 = null;
	public HashMap<String, HashSet<String>> _tarRe = null;
	public HashMap<String, HashSet<String>> _tarIm = null;
	
	public ExtensiveTAR(PetriNet pn){
		MyPetriNet mpn = MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder builder = new ONCompleteFinitePrefixBuilder(mpn);
		_cfp = builder.Build();
		
		build();
	}
	
	public ExtensiveTAR(MyPetriNet mpn){
		ONCompleteFinitePrefixBuilder builder = new ONCompleteFinitePrefixBuilder(mpn);
		_cfp = builder.Build();
		
		build();
	}
	public ExtensiveTAR(ONCompleteFinitePrefix cfp){
		_cfp = cfp;
		
		build();
	}
	
	public void build(){
		size = _cfp.getOn().getEveSet().size();
		_ar = new AdjacentRelation(_cfp);
		_cr = new ConcurrentRelation(_cfp);
		_tc = new TransitiveClosure(_ar);
		
		_tar = new HashMap<String, HashSet<String>>();
		_tar0 = new HashMap<String, HashSet<String>>();
		_tarRe = new HashMap<String, HashSet<String>>();
		_tarIm = new HashMap<String, HashSet<String>>();
		
		ONEvent leftEvent = null;
		ONEvent rightEvent = null;
		for (int i = 0; i < size; i++) {
			leftEvent = _cfp.getOn().getEveSet().get(i);
			for (int j = 0; j < size; j++) {
				rightEvent = _cfp.getOn().getEveSet().get(j);
				if (_ar._implicit[i][j]) {
					addTAR(_tarIm, leftEvent, rightEvent);
				}
				else if (_ar._relation[i][j]) {
					if (_tc._relation[i][i] && _tc._relation[j][j]) {
						addTAR(_tarRe, leftEvent, rightEvent);
					}
					else {
						addTAR(_tar, leftEvent, rightEvent);
					}
				}
				else if (_cr._relation[i][j] && _cr._relation[j][i]) {
					if (_tc._relation[i][i] && _tc._relation[j][j]) {
						addTAR(_tarRe, leftEvent, rightEvent);
					}
					else {
						addTAR(_tar, leftEvent, rightEvent);
					}
				}
			}
		}
		
		buildArtificialTAR();
	}
	
	public void buildArtificialTAR(){
		HashSet<String> single = new HashSet<String>();
		HashSet<String> foreward = new HashSet<String>();
		HashSet<String> backward = new HashSet<String>();
		
		Iterator<ONCondition> itCondition = _cfp.getIntialConditions().iterator();
		while(itCondition.hasNext()){
			ONCondition condition = itCondition.next();
			
			Iterator<ONEvent> itPostEvent = 
				_cfp.getOn().getEvesOUTOFCon(condition.getId()).iterator();
			
			while(itPostEvent.hasNext()){
				ONEvent postEvent = itPostEvent.next();
				
				if(postEvent.getLabel().isEmpty()){
					for(ONEvent e : postEvent.getVisibleSuccessiveEvents()){
						foreward.add(e.getLabel());
					}
				}
			}
		}
		
		for(ONEvent e : _cfp.getOn().getEveSet()){
			if(!e.getLabel().isEmpty() && e.getPrecedingEvents().isEmpty()){
				ONEvent ce = e;
				if(e.isCutOffEvent()){
					ce = (ONEvent)e.object;
				}
				if(ce.getSuccessiveEvents().isEmpty()){
					single.add(ce.getLabel());
				}
			}
			if(e.getLabel().isEmpty()){
				if(e.getVisibleSuccessiveEvents().isEmpty()){
					for(ONEvent ee : e.getVisiblePrecedingEvents()){
						backward.add(ee.getLabel());
					}
				}
			}
		}
		
		addTAR0(single, "d");
		addTAR0(foreward, "s");
		addTAR0(backward, "e");
	}
	
	public void addTAR0(HashSet<String> e, String s){
		if(e.isEmpty()){
			return;
		}
		
		if(s == "d" || s == "s"){
			if(!_tar0.containsKey("start point")){
				_tar0.put("start point", e);
			}
			else{
				_tar0.get("start point").addAll(e);
			}
		}
		if(s == "d" || s == "e"){
			if(!_tar0.containsKey("end point")){
				_tar0.put("end point", e);
			}
			else{
				_tar0.get("end point").addAll(e);
			}
		}
	}
	
	public void addTAR(HashMap<String, HashSet<String>> tar, ONEvent leftEvent, ONEvent righteEvent){
		if(tar.containsKey(leftEvent.getLabel())){
			tar.get(leftEvent.getLabel()).add(righteEvent.getLabel());
		}
		else{
			HashSet<String> right = new HashSet<String>();
			right.add(righteEvent.getLabel());
			tar.put(leftEvent.getLabel(), right);
		}
	}
}
