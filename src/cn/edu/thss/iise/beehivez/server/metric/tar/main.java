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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.semanticweb.kaon2.ar;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPetriNet input = null;
		FileInputStream fin = null;
		try {
//			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\model\\4.pnml");
			fin = new FileInputStream("C:\\Users\\lenovo\\Documents\\completeLog\\Nonfree7.pnml");
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
		AdjacentRelation r = new AdjacentRelation(cfp);
//		TransitiveClosure r = new TransitiveClosure(cfp);
//		ExtensiveTAR extar = new ExtensiveTAR(cfp);
		
		long end = System.currentTimeMillis();
		System.out.println(end);
		long duration = middle - start;
		System.out.println("CFP:" + duration);
		duration = end - middle;
		System.out.println("TAR:" + duration);
		duration = end - start;
		System.out.println("TOTAL:" + duration);
		
		for(int i = 0; i < r._relation.length; ++i){
			System.out.print('\t' + cfp.getOn().getEveSet().get(i).getLabel());
		}
		
		System.out.println();
		for(int i = 0; i < r._relation.length; ++i){
			System.out.print(cfp.getOn().getEveSet().get(i).getLabel() + ":" + '\t');
			for(int j = 0; j < r._relation.length; ++j){
				System.out.print(r._relation[i][j]);
				System.out.print('\t');
			}
			System.out.println();
		}
	}

}
