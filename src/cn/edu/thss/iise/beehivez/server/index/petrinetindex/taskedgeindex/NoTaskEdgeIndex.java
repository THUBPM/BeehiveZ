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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.taskedgeindex;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * used to compare the performance with the TaskEdgeLuceneIndex.
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 * 
 */
public class NoTaskEdgeIndex extends PetriNetIndex {

	@Override
	public void addProcessModel(Object pno) {
	}

	@Override
	public void close() {
	}

	@Override
	public boolean create() {
		return true;
	}

	@Override
	public void delProcessModel(Object pno) {
	}

	@Override
	public boolean destroy() {
		return false;
	}

	/**
	 * the similarity between the query condition model and the result models
	 * should be equel to or greater than the threshold.
	 */
	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		TreeSet<ProcessQueryResult> ret = new TreeSet<ProcessQueryResult>();
		if (o instanceof PetriNet) {
			PetriNet q = (PetriNet) o;
			DataManager dm = DataManager.getInstance();
			String strSelectPetriNet = "select process_id, pnml from petrinet";
			ResultSet rs = dm.executeSelectSQL(strSelectPetriNet, 0,
					Integer.MAX_VALUE, dm.getFetchSize());
			try {
				while (rs.next()) {
					PetriNet c = null;

					if (dm.getDBName().equalsIgnoreCase("postgresql")
							|| dm.getDBName().equalsIgnoreCase("mysql")) {
						String str = rs.getString("pnml");
						byte[] temp = str.getBytes();
						c = PetriNetUtil.getPetriNetFromPnmlBytes(temp);
					} else if (dm.getDBName().equalsIgnoreCase("derby")) {
						InputStream in = rs.getAsciiStream("pnml");
						PnmlImport pnml = new PnmlImport();
						PetriNetResult result = (PetriNetResult) pnml
								.importFile(in);
						c = result.getPetriNet();
						result.destroy();
						in.close();
					} else {
						System.out.println(dm.getDBName() + " unsupported");
						System.exit(-1);
					}

					float rsim = PetriNetUtil.mcesSimilarity(c, q);
					long process_id = rs.getLong("process_id");

					if (rsim >= similarity) {
						ret.add(new ProcessQueryResult(process_id, rsim));
					}
					c.destroyPetriNet();
				}
				Statement stmt = rs.getStatement();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	@Override
	public float getStorageSizeInMB() {
		return 0;
	}

	@Override
	public boolean open() {
		return true;
	}

	@Override
	public boolean supportGraphQuery() {
		return true;
	}

	@Override
	public boolean supportSimilarQuery() {
		return true;
	}

	@Override
	public boolean supportTextQuery() {
		return false;
	}

	@Override
	public boolean supportSimilarLabel() {
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
