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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.nullindex;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * @author JinTao used for search time comparation
 * 
 */
public class NullIndex extends PetriNetIndex {

	@Override
	public void addProcessModel(Object pno) {

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean create() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void delProcessModel(Object pno) {

	}

	public TreeSet<ProcessQueryResult> getPetriNet(Object o) {
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
					long process_id = rs.getLong("process_id");

					if (Ullman4PetriNet.subGraphIsomorphism(q, c)) {
						ret.add(new ProcessQueryResult(process_id, 1));
					}
					c.destroyPetriNet();
				}
				java.sql.Statement stmt = rs.getStatement();
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

	// @Override
	// protected void init() {
	// String str = this.getClass().getCanonicalName();
	// this.javaClassName = str;
	// this.name = str.substring(str.lastIndexOf(".") + 1);
	// }

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportGraphQuery() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportTextQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {

	}

	@Override
	public float getStorageSizeInMB() {
		return 0;
	}

	@Override
	public boolean supportSimilarQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		// TODO Auto-generated method stub
		return getPetriNet(o);
	}

	@Override
	public boolean supportSimilarLabel() {
		// TODO Auto-generated method stub
		return true;
	}

}
