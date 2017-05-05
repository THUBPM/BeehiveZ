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

package cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet;

import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriArc;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriPlace;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ON implements Cloneable {
	private Vector<ONCondition> conSet;
	private Vector<ONEvent> eveSet;
	private Vector<ONArc> arcSet;
	
	
	public ON() {
		conSet = new Vector<ONCondition>();
		eveSet = new Vector<ONEvent>();
		arcSet = new Vector<ONArc>();
		// TODO Auto-generated constructor stub
	}


	public ON(Vector<ONCondition> conSet, Vector<ONEvent> eveSet,
			Vector<ONArc> arcSet) {
		super();
		this.conSet = conSet;
		this.eveSet = eveSet;
		this.arcSet = arcSet;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		ON obj = null;
        try{
        	obj = (ON)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
	}


	public Vector<ONCondition> getConSet() {
		return conSet;
	}


	public void setConSet(Vector<ONCondition> conSet) {
		this.conSet = conSet;
	}


	public Vector<ONEvent> getEveSet() {
		return eveSet;
	}


	public void setEveSet(Vector<ONEvent> eveSet) {
		this.eveSet = eveSet;
	}


	public Vector<ONArc> getArcSet() {
		return arcSet;
	}


	public void setArcSet(Vector<ONArc> arcSet) {
		this.arcSet = arcSet;
	}
	
	
	public void addObject(ONObject o){
		if(o instanceof ONCondition){
			conSet.add((ONCondition)o);
		}else if(o instanceof ONEvent){
			eveSet.add((ONEvent)o);
		}else if(o instanceof ONArc){
			arcSet.add((ONArc)o);
		}
	}
	
	//wwx
	public void removeObject(String getid) {
		// TODO Auto-generated method stub
		for (int i = 0; i < conSet.size(); i++) {
			if (conSet.get(i).getId().equals(getid)) {
				conSet.remove(i);
			}
		}
		for (int i = 0; i < eveSet.size(); i++) {
			if (eveSet.get(i).getId().equals(getid)) {
				eveSet.remove(i);
			}
		}
		for (int i = 0; i < arcSet.size(); i++) {
			if (arcSet.get(i).getId().equals(getid)) {
				arcSet.remove(i);
			}
		}
	}
	
	//��ȡĳһevent����������conditions
	public Vector<ONCondition> getConsINTOEve(String eventId){
		Vector<ONCondition> cons = new Vector<ONCondition>();
		ONObject on;
		ONArc arc;
		String targetId, sourceId;
		
		for(int i = 0; i < arcSet.size(); ++i){
			on = arcSet.get(i);
			arc = (ONArc)on;
			targetId = arc.getTargetId();
			if(!targetId.equals(eventId)){
				continue;
			}
			sourceId = arc.getSourceId();
			
			if(sourceId != null){
				for(int j = 0; j < conSet.size(); ++j){
					on = conSet.get(j);
					if(sourceId.equals(on.getId())){
						cons.add((ONCondition)on);
					}
				}
			}
		}
		
		return cons;
	}
	
	//��ȡĳһevent���������conditions
	public Vector<ONCondition> getConsOUTOFEve(String eventId){
		Vector<ONCondition> cons = new Vector<ONCondition>();
		ONObject on;
		ONArc arc;
		String targetId, sourceId;
		
		for(int i = 0; i < arcSet.size(); ++i){
			on = arcSet.get(i);
			arc = (ONArc)on;
			sourceId = arc.getSourceId();
			if(!sourceId.equals(eventId)){
				continue;
			}
			targetId = arc.getTargetId();
			
			if(targetId != null){
				for(int j = 0; j < conSet.size(); ++j){
					on = conSet.get(j);
					if(targetId.equals(on.getId())){
						cons.add((ONCondition)on);
					}
				}
			}
		}
		
		return cons;
	}
	
	//��ȡĳһcondition����������events
	public Vector<ONEvent> getEvesINTOCon(String conditionId){
		Vector<ONEvent> eves = new Vector<ONEvent>();
		ONObject on;
		ONArc arc;
		String targetId, sourceId;
		
		for(int i = 0; i < arcSet.size(); ++i){
			on = arcSet.get(i);
			arc = (ONArc)on;
			targetId = arc.getTargetId();
			if(!targetId.equals(conditionId)){
				continue;
			}
			sourceId = arc.getSourceId();
			
			if(sourceId != null){
				for(int j = 0; j < eveSet.size(); ++j){
					on = eveSet.get(j);
					if(sourceId.equals(on.getId())){
						eves.add((ONEvent)on);
					}
				}
			}
		}
		
		return eves;
	}
	
	//��ȡĳһcondition���������events
	public Vector<ONEvent> getEvesOUTOFCon(String conditionId){
		Vector<ONEvent> cons = new Vector<ONEvent>();
		ONObject on;
		ONArc arc;
		String targetId, sourceId;
		
		for(int i = 0; i < arcSet.size(); ++i){
			on = arcSet.get(i);
			arc = (ONArc)on;
			sourceId = arc.getSourceId();
			if(!sourceId.equals(conditionId)){
				continue;
			}
			targetId = arc.getTargetId();
			
			if(targetId != null){
				for(int j = 0; j < eveSet.size(); ++j){
					on = eveSet.get(j);
					if(targetId.equals(on.getId())){
						cons.add((ONEvent)on);
					}
				}
			}
		}
		
		return cons;
	}
	
	public MyPetriNet ONToMPN(){
		MyPetriNet mpn = new MyPetriNet();
		
		for(ONCondition onc : conSet){
			mpn.getPlaceSet().add(new MyPetriPlace(onc.id, onc.label));
		}
		
		for(ONEvent one : eveSet){
			mpn.getTransitionSet().add(new MyPetriTransition(one.id, one.label, ""));
		}
		
		for(ONArc ona : arcSet){
			mpn.getArcSet().add(new MyPetriArc(ona.id, ona.getSourceId(), ona.getTargetId()));
		}
		
		return mpn;
	}
	
	public int indexOfEvent(ONEvent event){
		for(int i = 0; i < this.eveSet.size(); ++i){
			if(this.eveSet.get(i).equals(event)){
				return i;
			}
		}
		
		return -1;
	}
}
