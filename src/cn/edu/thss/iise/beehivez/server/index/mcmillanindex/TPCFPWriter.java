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
package cn.edu.thss.iise.beehivez.server.index.mcmillanindex;

import java.io.*;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

//import att.grappa.Element;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class TPCFPWriter {
	TPCFPWriter(){
		
	}
	
	public void exportTPCFP(ONCompleteFinitePrefix tpcfp, String exportFileName) throws IOException{
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
		
		exportMyPetriNet(tpcfp.getOn().ONToMPN(), exportFileName);
		exportTemporalOrder(tpcfp.getTemporalOrder(), exportFileName);
		//bw.close();
		return;
	}
	
	public void exportMyPetriNet(MyPetriNet mpn, String exportFileName){
		String exportPNFileName = exportFileName + ".pnml";
		mpn.export_pnml(exportPNFileName);
	}
	
	public void exportTemporalOrder(HashMap<ONEvent, HashMap<ONEvent, String>> tpo,
			String exportFileName){
		Element root;
		root = new Element("tpml");
		Document docJOMexp = new Document(root);
		
		Element temporalOrder = new Element("TemporalOrder");
		temporalOrder.setAttribute("id", "Temporal Order Infomation");
		root.addContent(temporalOrder);
		
		Element sourceTransition, cutTransition;
		Element name, value, loop;
		for(ONEvent sourceEvent:tpo.keySet()){
			sourceTransition = new Element("sourceEvent");
			sourceTransition.setAttribute("id", sourceEvent.getId());
			
			name = new Element("name");
			value = new Element("value");
			value.setText(sourceEvent.getLabel());
			name.addContent(value);
			
			sourceTransition.addContent(name);
			
			for(ONEvent cutEvent:tpo.get(sourceEvent).keySet()){
				cutTransition = new Element("cutEvent");
				cutTransition.setAttribute("id", cutEvent.getId());
				
				name = new Element("name");
				value = new Element("value");
				value.setText(cutEvent.getLabel());
				name.addContent(value);
				loop = new Element("loop");
				if(tpo.get(sourceEvent).get(cutEvent).equals("loop")){
					loop.setText("true");
				}
				else{
					loop.setText("false");
				}
				
				cutTransition.addContent(name);
				cutTransition.addContent(loop);
				
				sourceTransition.addContent(cutTransition);
			}
			
			temporalOrder.addContent(sourceTransition);
		}
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.output(docJOMexp, new FileOutputStream(exportFileName + ".tpml"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
