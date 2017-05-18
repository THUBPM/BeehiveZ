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
package cn.edu.thss.iise.beehivez.server.datamanagement;

import java.sql.ResultSet;

import org.processmining.framework.models.petrinet.PetriNet;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.OplogObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author JinTao
 * 
 *         used for performance log, used as follows.
 * 
 *         OplogWriter logWriter = OplogWriter.getIntance()
 *         logWriter.prepareLog()
 * 
 *         user's function needed to log the performance
 * 
 *         logWriter.writeLog(...)
 * 
 */
public class OplogWriter {

	private static OplogWriter instance = new OplogWriter();

	private OplogWriter() {
	}

	public static OplogWriter getInstance() {
		return instance;
	}

	private OplogObject parseOplogObjectFromArg(Object o) {
		OplogObject olo = new OplogObject();
		if (o instanceof PetrinetObject) {
			PetrinetObject po = (PetrinetObject) o;
			olo.setOperand(po.getPetriNet().getIdentifier());
			olo.setNarc(po.getNarc());
			olo.setNdegree(po.getNdegree());
			olo.setNplace(po.getNplace());
			olo.setNtransition(po.getNtransition());
		} else if (o instanceof PetriNet) {
			PetriNet pn = (PetriNet) o;
			olo.setOperand(pn.getIdentifier());
			olo.setNarc(pn.getNumberOfEdges());
			olo.setNdegree(-1);
			olo.setNplace(pn.getPlaces().size());
			olo.setNtransition(pn.getTransitions().size());
		} else if (o instanceof String) {
			String str = (String) o;
			olo.setOperand(str);
		} else if (o instanceof ProcessObject) {
			ProcessObject po = (ProcessObject) o;
			olo.setOperand(po.getName());
			Object oo = po.getObject();
			if (oo != null) {
				if (oo instanceof YNet) {
					YNet net = (YNet) oo;
					olo.setNtransition(net.getNetTasks().size());
					olo.setNplace(YAWLUtil.getNetConditions(net).size());
					olo.setNarc(YAWLUtil.getNetFlows(net).size());
				}
			}
		} else if (o instanceof YNet) {
			YNet net = (YNet) o;
			olo.setOperand(net.getID());
			olo.setNtransition(net.getNetTasks().size());
			olo.setNplace(YAWLUtil.getNetConditions(net).size());
			olo.setNarc(YAWLUtil.getNetFlows(net).size());
		}

		return olo;
	}

	public void writeLog(String className, Object arg, String opType,
			int resultsize, long timeCost) {
		String indexName = className;
		long opTime = System.currentTimeMillis();
		OplogObject olo = parseOplogObjectFromArg(arg);
		olo.setIndexname(indexName);
		olo.setOptype(opType);
		olo.setTimestamp(opTime);
		olo.setTimecost(timeCost);
		olo.setNpetri(GlobalParameter.getNModels());
		olo.setResultsize(resultsize);
		DataManager dm = DataManager.getInstance();
		dm.addOplog(olo);
	}
}