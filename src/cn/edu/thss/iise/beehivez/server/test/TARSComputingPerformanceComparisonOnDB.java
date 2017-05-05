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
package cn.edu.thss.iise.beehivez.server.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * compare the performance of TARS computing between the method based on
 * coverability graph and the complete prefix unfolding
 * 
 * @author Tao Jin
 * 
 */
public class TARSComputingPerformanceComparisonOnDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			File fLog = new File("log.csv");
			FileWriter fw = new FileWriter(fLog);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("pn, method, timecost(ms)");
			bw.newLine();

			// used for time cost measure
			long start, timecost;

			DataManager dm = DataManager.getInstance();
			int fetchSize = dm.getFetchSize();

			ResultSet rs = dm.executeSelectSQL(
					"select process_id from process where type='"
							+ ProcessObject.TYPEPNML + "'", 0,
					Integer.MAX_VALUE, fetchSize);
			while (rs.next()) {
				long process_id = rs.getLong("process_id");
				for (int i = 0; i < 5; i++) {
					PetriNet pn = PetriNetUtil.getPetriNetFromPnmlBytes(dm
							.getProcessDefinitionBytes(process_id));
					start = System.currentTimeMillis();
					PetriNetUtil.getTARSFromPetriNetByCFP(pn);
					timecost = System.currentTimeMillis() - start;
					bw.write(process_id + ",cfp," + timecost);
					bw.newLine();
					pn.destroyPetriNet();
				}
				for (int i = 0; i < 5; i++) {
					PetriNet pn = PetriNetUtil.getPetriNetFromPnmlBytes(dm
							.getProcessDefinitionBytes(process_id));
					start = System.currentTimeMillis();
					PetriNetUtil.getTARSFromPetriNetByCG(pn);
					timecost = System.currentTimeMillis() - start;
					bw.write(process_id + ",cg," + timecost);
					bw.newLine();
					pn.destroyPetriNet();
				}
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();

			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
