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
package cn.edu.thss.iise.beehivez.server.filelogger;

import java.io.File;

import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author JinTao
 * 
 *         all log is in csv files
 * 
 */
public class Indexlogger {

	private static final String PATH = GlobalParameter.getHomeDirectory()
			+ "/index/indexlog";
	private static final String SEARCHTIMELOGFILE = PATH + "/SearchTimeLog.csv";
	private static final String CANDIDATESETSIZELOGFILE = PATH
			+ "/CandidateSetSizeLog.csv";
	private static final String CANDIDATESETHITRATIOLOGFILE = PATH
			+ "/CandidateSetHitRatioLog.csv";
	private static final String STORAGESIZELOGFILE = PATH
			+ "/StorageSizeLog.csv";

	// initialization
	static {
		File f = new File(PATH);
		if (!f.exists()) {
			f.mkdirs();
			createNewLogs();
		} else if (f.isFile()) {
			f.delete();
			f.mkdirs();
			createNewLogs();
		}
	}

	public static void createNewLogs() {
		logIndexSearchTimeTitle();
		logIndexCandidateSetSizeTitle();
		logIndexCandidateSetHitRatioTitle();
		logIndexStorageSizeTitle();
	}

	/**
	 * @param indexName
	 * @param queryObjectId
	 * @param timeCost
	 *            in ms
	 * @param nTransitions
	 *            the number of transitions in the query object
	 * @param nPlaces
	 *            the number of places in the query object
	 * @param nArcs
	 *            the number of arcs in the query object
	 * @param nModels
	 *            the number of models in the repository
	 */
	public static void logIndexSearchTime(String indexName,
			String queryObjectId, long timeCost, int nTransitions, int nPlaces,
			int nArcs, long nModels) {
		String log = indexName + "," + queryObjectId + "," + timeCost + ","
				+ nTransitions + "," + nPlaces + "," + nArcs + "," + nModels;
		FileLogger.writeLog(SEARCHTIMELOGFILE, log);
	}

	private static void logIndexSearchTimeTitle() {
		// FileLogger.deleteLogFile(SEARCHTIMELOGFILE);
		String log = "indexName,queryObjectId,timeCost,nTransitions,nPlaces,nArcs,nModels";
		FileLogger.writeLog(SEARCHTIMELOGFILE, log);
	}

	/**
	 * @param indexName
	 * @param queryObjectId
	 * @param size
	 *            the size of candidate set
	 * @param nModels
	 *            the number of models in the repository
	 */
	public static void logIndexCandidateSetSize(String indexName,
			String queryObjectId, int size, long nModels) {
		String log = indexName + "," + queryObjectId + "," + size + ","
				+ nModels;
		FileLogger.writeLog(CANDIDATESETSIZELOGFILE, log);
	}

	private static void logIndexCandidateSetSizeTitle() {
		// FileLogger.deleteLogFile(CANDIDATESETSIZELOGFILE);
		String log = "indexName,queryObject,size,nModels";
		FileLogger.writeLog(CANDIDATESETSIZELOGFILE, log);
	}

	/**
	 * @param indexName
	 * @param queryObjectId
	 * @param ratio
	 *            the hit ratio of the candidate set
	 * @param nModels
	 *            the number of models in the repository
	 */
	public static void logIndexCandidateSetHitRatio(String indexName,
			String queryObjectId, float ratio, long nModels) {
		String log = indexName + "," + queryObjectId + "," + ratio + ","
				+ nModels;
		FileLogger.writeLog(CANDIDATESETHITRATIOLOGFILE, log);
	}

	private static void logIndexCandidateSetHitRatioTitle() {
		// FileLogger.deleteLogFile(CANDIDATESETHITRATIOLOGFILE);
		String log = "indexName,queryObjectId,ratio,nModels";
		FileLogger.writeLog(CANDIDATESETHITRATIOLOGFILE, log);
	}

	public static void logIndexStorageSize(String indexName,
			float storageSizeInMB, long nModels) {
		String log = indexName + "," + storageSizeInMB + "," + nModels;
		FileLogger.writeLog(STORAGESIZELOGFILE, log);
	}

	private static void logIndexStorageSizeTitle() {
		// FileLogger.deleteLogFile(STORAGESIZELOGFILE);
		String log = "indexName,storageSizeInMB,nModels";
		FileLogger.writeLog(STORAGESIZELOGFILE, log);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
