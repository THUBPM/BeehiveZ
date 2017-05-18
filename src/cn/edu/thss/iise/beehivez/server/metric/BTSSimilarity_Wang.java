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

package cn.edu.thss.iise.beehivez.server.metric;

import java.util.LinkedList;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.basicprocess.BasicProcessSet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.bts.BTSGenerator_Wang;
import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;

public class BTSSimilarity_Wang {
	public String getName() {
		return "BPSSimilarity_Wang";
	}

	public String getDesription() {
		return "similarity match based on basic process segments whitch is designed by prof wang";
	}
	
	public double similarity(PetriNet pn1, PetriNet pn2) {
		
		BTSGenerator_Wang btsG = new BTSGenerator_Wang();
		LinkedList<BasicProcessSet> list1 = btsG.getBTS(MyPetriNet
				.PromPN2MyPN(pn1));
		LinkedList<BasicProcessSet> list2 = btsG.getBTS(MyPetriNet
				.PromPN2MyPN(pn2));
		// test
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list1.size();i++){
			BasicProcessSet bps = list1.get(i);
			sb.append(bps.toString());
		}
		//System.out.println(sb.toString());
		sb = new StringBuffer();
		for(int i=0;i<list2.size();i++){
			BasicProcessSet bps = list2.get(i);
			sb.append(bps.toString());
		}
		//System.out.println(sb.toString());
		// end test
		double[] similarity = new double[3]; 
		int number[] = new int [3];
		int total = 0;
		for(int i=0;i<similarity.length;i++){
			BasicProcessSet bps1 = list1.get(i);
			BasicProcessSet bps2 = list2.get(i);
			similarity[i] = bps1.getSimilarityBS(bps2);
			number[i] = 0;
			if(bps1.getPSet() != null){
				number[i] += bps1.getPSet().size();
			}
			if(bps2.getPSet()!=null){
				number[i] += bps2.getPSet().size();
			}
			total += number[i];
		}
		double[] landa = new double[3];
		double result = 0.0;
		for(int i=0;i<landa.length;i++){
			landa[i] = (1.0 * number[i]) / total;
			result += landa[i] * similarity[i];
		}
		return result;
	}
}
