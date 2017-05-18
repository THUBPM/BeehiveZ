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
package cn.edu.thss.iise.beehivez.server.index.yawlindex;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4PetriNet;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.Ullman4YAWL;
import cn.edu.thss.iise.beehivez.server.graph.isomorphism.VF24YAWL;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author Tao Jin
 * 
 */
public class NullYAWLIndex extends YAWLIndex {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#addProcessModel(java.
	 * lang.Object)
	 */
	@Override
	public void addProcessModel(Object o) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#close()
	 */
	@Override
	public void close() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#create()
	 */
	@Override
	public boolean create() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#delProcessModel(java.
	 * lang.Object)
	 */
	@Override
	public void delProcessModel(Object o) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#destroy()
	 */
	@Override
	public boolean destroy() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#getProcessModels(java
	 * .lang.Object, float)
	 */
	@Override
	public TreeSet<ProcessQueryResult> getProcessModels(Object o,
			float similarity) {
		TreeSet<ProcessQueryResult> ret = new TreeSet<ProcessQueryResult>();
		if (o instanceof YNet) {
			YNet q = (YNet) o;
			DataManager dm = DataManager.getInstance();
			String strSelectYNet = "select process_id, definition from process where type='yawl'";
			ResultSet rs = dm.executeSelectSQL(strSelectYNet, 0,
					Integer.MAX_VALUE, dm.getFetchSize());
			try {
				while (rs.next()) {
					YNet c = null;
					if (dm.getDBName().equalsIgnoreCase("postgresql")
							|| dm.getDBName().equalsIgnoreCase("mysql")) {
						String str = rs.getString("definition");
						byte[] temp = str.getBytes();
						c = YAWLUtil.getYNetFromDefinition(temp);
					} else if (dm.getDBName().equalsIgnoreCase("derby")) {
						InputStream in = rs.getAsciiStream("definition");
						byte[] temp = ToolKit.getBytesFromInputStream(in);
						c = YAWLUtil.getYNetFromDefinition(temp);
						in.close();
					} else {
						System.out.println(dm.getDBName() + " unsupported");
						System.exit(-1);
					}
					long process_id = rs.getLong("process_id");

					if (Ullman4YAWL.subGraphIsomorphism(q, c)) {
						// if (VF24YAWL.subGraphIsomorphism(q, c)) {
						ret.add(new ProcessQueryResult(process_id, 1));
					}
				}
				java.sql.Statement stmt = rs.getStatement();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#getStorageSizeInMB()
	 */
	@Override
	public float getStorageSizeInMB() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#open()
	 */
	@Override
	public boolean open() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportGraphQuery()
	 */
	@Override
	public boolean supportGraphQuery() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportSimilarLabel()
	 */
	@Override
	public boolean supportSimilarLabel() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportSimilarQuery()
	 */
	@Override
	public boolean supportSimilarQuery() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.thss.iise.beehivez.server.index.BPMIndex#supportTextQuery()
	 */
	@Override
	public boolean supportTextQuery() {
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
