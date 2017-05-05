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

import java.util.TreeSet;

import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefixBuilder;
import cn.edu.thss.iise.beehivez.server.datamanagement.*;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.metric.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.util.FileUtil;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class McMillanIndex extends PetriNetIndex {

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#addProcessModel(java.lang.Object)
	 */
	@Override
	public void addProcessModel(Object o) {
		// TODO Auto-generated method stub
		PetrinetObject pno = (PetrinetObject)o;
		PetriNet pn = pno.getPetriNet();
		if(pn == null){
			pn = PetriNetUtil.getPetriNetFromPnml(pno.getPnmlIn());
		}
		if(pn == null){
			System.out.println("null petri net in addPetriNet");
		}
//		
		long process_id = pno.getProcess_id();
		MyPetriNet input = MyPetriNet.PromPN2MyPN(pn);
		ONCompleteFinitePrefixBuilder cfpBuilder = new ONCompleteFinitePrefixBuilder(input);
		ONCompleteFinitePrefix cfp = cfpBuilder.Build();
		TPCFPWriter tw = new TPCFPWriter();
		String fileName = (pn.getIdentifier() == null)?pn.getIdentifier():"currentTPCFP";
		try {
			tw.exportTPCFP(cfp, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataManager dm = DataManager.getInstance();
		dm.addMcmillanIndex(process_id, fileName + ".pnml", fileName + ".tpml");
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#create()
	 */
	@Override
	public boolean create() {
		// TODO Auto-generated method stub
		DataManager dm = DataManager.getInstance();
		
		return dm.executeCreatSQL("mcmillanIndex");
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#delProcessModel(java.lang.Object)
	 */
	@Override
	public void delProcessModel(Object o) {
		// TODO Auto-generated method stub
		System.out.println("not implemented");
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#destroy()
	 */
	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		DataManager dm = DataManager.getInstance();
		
		return dm.executeDropSQL("mcmillanIndex");
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#getProcessModels(java.lang.Object, float)
	 */
	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		// TODO Auto-generated method stub
		TreeSet<ProcessQueryResult> fret = new TreeSet<ProcessQueryResult>();
		HashSet ret = null;
		if (o instanceof PetriNet) {
			PetriNet q = (PetriNet) o;
			DataManager dm = DataManager.getInstance();
			String strSelectPetriNet = "select process_id, definitionMPN from mcmillanIndex";
			ResultSet rs = dm.executeSelectSQL(strSelectPetriNet);
			try {
				while (rs.next()) {
					long process_id = rs.getLong("process_id");
					InputStream in = rs.getAsciiStream("definitionMPN");
					PnmlImport pnml = new PnmlImport();
					PetriNetResult result = (PetriNetResult) pnml
							.importFile(in);
					PetriNet c = result.getPetriNet();
					if (Ullman4PetriNet.subGraphIsomorphism(q, c)) {
						ret.add(process_id);
					}
					c.delete();
					c.clearGraph();
					in.close();
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Iterator itt = ret.iterator();
		while (itt.hasNext()) {
			long process_id = ((Long) itt.next()).longValue();
			fret.add(new ProcessQueryResult(process_id, 1));
		}
		
		return fret;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#getStorageSizeInMB()
	 */
	@Override
	public float getStorageSizeInMB() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#open()
	 */
	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportGraphQuery()
	 */
	@Override
	public boolean supportGraphQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportSimilarLabel()
	 */
	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportSimilarQuery()
	 */
	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportTextQuery()
	 */
	@Override
	public boolean supportTextQuery() {
		// TODO Auto-generated method stub
		return false;
	}

}
