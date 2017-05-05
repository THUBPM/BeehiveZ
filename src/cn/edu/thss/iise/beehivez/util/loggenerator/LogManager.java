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
package cn.edu.thss.iise.beehivez.util.loggenerator;

import java.io.File;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;

/**
 * used to manage the log files
 * 
 * @author htf
 * 
 */
public class LogManager {

	/**
	 * 
	 * @param fileDir
	 *            where the log store
	 * @param caseCount
	 *            how many cases will be run
	 * @param lpm
	 *            the log produce algorithm
	 * @param pn
	 *            if pn has no mark,we should add a mark at the source place
	 */
	public static void generateLog(String fileDir, int caseCount,
			LogProduceMethod lpm, PetriNet pn, double completeness,int multiple) {
		Place source = (Place) pn.getSource();
		if (source.getNumberOfTokens() <= 0) {
			source.addToken(new Token());
		}
		lpm.generateLog(fileDir, caseCount, pn, completeness,multiple);
	}
	public static void generateLog(String fileDir, int caseCount,
			LogProduceMethod lpm, PetriNet pn, double completeness) {
		Place source = (Place) pn.getSource();
		if (source.getNumberOfTokens() <= 0) {
			source.addToken(new Token());
		}
		lpm.generateLog(fileDir, caseCount, pn, completeness);
	}
	
	public static void generateLog(String fileDir,
			LogProduceMethod lpm, PetriNet pn) {
		Place source = (Place) pn.getSource();
		if (source.getNumberOfTokens() <= 0) {
			source.addToken(new Token());
		}
		lpm.generateLog(fileDir, pn);
	}
	
	public static void generateLog(String fileDir,LogProduceMethod lpm,
			PetriNet pn, CompleteParameters comPara, NoiseParameters noiPara ){
		Place source = (Place) pn.getSource();
		if (source.getNumberOfTokens() <= 0) {
			source.addToken(new Token());
		}
		lpm.generateLog(fileDir, pn, comPara, noiPara);
	}
	

	/**
	 * the files in the path fileDir will all be deleted.
	 * 
	 * @param fileDir
	 */
	public static void clear(String fileDir) {
		File file = new File(fileDir);
		delete(file);
	}

	private static void delete(File f) {
		if (f == null || !f.exists())
			return;
		if (f.isFile())
			f.delete();
		else {
			for (File file : f.listFiles()) {
				if (file.isFile())
					file.delete();
				else if (file.isDirectory())
					delete(file);
			}
			f.delete();
		}
	}
}
