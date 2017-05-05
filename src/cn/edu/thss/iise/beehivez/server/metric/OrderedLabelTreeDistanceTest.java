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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.OrderedLabelTree;
import cn.edu.thss.iise.beehivez.server.basicprocess.OrderedLabelTreeGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

public class OrderedLabelTreeDistanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
       FileInputStream pnml1;
       FileInputStream pnml2;
		try {
			pnml1 = new FileInputStream("temp/New Petri net 1_1.pnml");
			pnml2 = new FileInputStream("temp/New Petri net 1_2.pnml");
	        PnmlImport pnmlimport = new PnmlImport();
	        PetriNet petrinet1 = pnmlimport.read(pnml1);
	        PetriNet petrinet2 = pnmlimport.read(pnml2);
	        OrderedLabelTreeGenerator treeGenerator = new OrderedLabelTreeGenerator();
	        OrderedLabelTree tree1 = treeGenerator.generateOrderedLabelTree(MyPetriNet.PromPN2MyPN(petrinet1));
	        OrderedLabelTree tree2 = treeGenerator.generateOrderedLabelTree(MyPetriNet.PromPN2MyPN(petrinet2));
	        OrderedLabelTreeDistance treeDistance = new OrderedLabelTreeDistance();
	        double distance = treeDistance.getDistance(tree1, tree2);
	        System.out.println("distance:"+distance);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

}
