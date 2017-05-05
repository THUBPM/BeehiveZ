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

package cn.edu.thss.iise.beehivez.server.bp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import bsh.This;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.metric.tar.temp.ONOrderingRelation;
import cn.edu.thss.iise.beehivez.server.metric.tar.temp.OrderingRelation;

public class BehavioralRelationBuilder {

	private BehavioralRelation[][] _relation = null;
	public BehavioralRelation[][] get_relation() {
		return _relation;
	}
	private ONCompleteFinitePrefix _cfp = null;
	private int length = 0;
	
	public BehavioralRelationBuilder(ONCompleteFinitePrefix cfp){
		this._cfp = cfp;
		this.length = cfp.getOn().getEveSet().size();
		this._relation = new BehavioralRelation[length][length];
		for(int i = 0; i < length; ++i){
			for(int j = 0; j < length; ++j){
				this._relation[i][j] = BehavioralRelation.ReverseOrder;
			}
		}
	}
	
	public void buildBehavioralRelaton(){
		ONOrderingRelation or = new ONOrderingRelation(this._cfp);
		for(int i = 0; i < length; ++i){
			for(int j = 0; j < length; ++j){
				if(or.getOrderRelations()[i][j] == OrderingRelation.CON){
					this._relation[i][j] = BehavioralRelation.Interleaving;
				}
				else if(or.getOrderRelations()[i][j] == OrderingRelation.CONFLICT){
					this._relation[i][j] = BehavioralRelation.Exclussive;
				}
				else if(or.getOrderRelations()[i][j] == OrderingRelation.PRE){
					this._relation[i][j] = BehavioralRelation.StrictOrder;
				}
			}
		}
		updateBehavioralRelation();
	}
	
	public void updateBehavioralRelation(){
		for(int i = 0; i < length; ++i){
			for(int j = 0; j < length; ++j){
				if(this._relation[i][j] == BehavioralRelation.StrictOrder){
					updateStrictOrderRelation(i, j);
				}
			}
		}
	}
	
	public void updateStrictOrderRelation(int former, int latter){
		int current = former;
		for(int k = 0; k < length; ++k){
			if(this._relation[k][former] == BehavioralRelation.StrictOrder &&
					this._relation[k][latter] != BehavioralRelation.StrictOrder){
				this._relation[k][latter] = BehavioralRelation.StrictOrder;
				updateStrictOrderRelation(k, latter);
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\experiment\\实验一B.pnml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PnmlImport pImport = new PnmlImport();
		PetriNetResult pnr = null;
		try {
			pnr = (PetriNetResult) pImport.importFile(fin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PetriNet pn = pnr.getPetriNet();
		//pn.setIdentifier("1258790072312.pnml");
		input=MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
		
		long start = System.currentTimeMillis();
		System.out.println(start);
		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
		
		long middle = System.currentTimeMillis();
		BehavioralRelationBuilder br = new BehavioralRelationBuilder(cfp);
		br.buildBehavioralRelaton();
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = middle - start;
		System.out.println("CFP:" + duration);
		duration = end - middle;
		System.out.println("OR:" + duration);
		duration = end - start;
		System.out.println("TOTAL:" + duration);
		
		for(int i = 0; i < br.get_relation().length; ++i){
			System.out.print('\t' + cfp.getOn().getEveSet().get(i).getLabel());
		}
		
		System.out.println();
		for(int i = 0; i < br.get_relation().length; ++i){
			System.out.print(cfp.getOn().getEveSet().get(i).getLabel() + ":");
			for(int j = 0; j < br.get_relation().length; ++j){
				System.out.print(br.get_relation()[i][j]);
				System.out.print('\t');
			}
			System.out.println();
		}
	
//		cfpBuilder.cfp.getOn().ONToMPN().export_pnml("C:\\Users\\winever.winever-PC\\Documents\\QueryModel\\Non-free Choice\\Nonfree7_unfolding.pnml");
	
	}

}
