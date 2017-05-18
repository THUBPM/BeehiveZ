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
package cn.edu.thss.iise.beehivez.server.parameter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;

/**
 * @author JinTao
 * 
 */
public class GlobalParameter {
	private static final String GLOBALSETTINGFILENAME = "system.conf";
	private static final String strEnableSimilarLabel = "enableSimilarLabel";
	private static final String strLabelSimilarityThreshold = "labelSimilarityThreshold";
	private static final String strEnableQueryLog = "enableQueryLog";
	private static final String strAllModels2PetriNets = "allModels2PetriNets";
	// private static final String strHomeDirectory = "homeDirectory";

	// whether every model added will be transformed to Petri nets
	private static boolean ISALLMODELS2PETRINETS = true;

	// control the unfolding depth
	public static int unfoldingDepth = Integer.MAX_VALUE;

	// computing time (ms) threshold,used for coverability graph computing
	public static long computingTimeThreshold = 8000;

	// enable the label similarity search or not
	private static boolean enableSimilarLabel = false;

	// the threshold for label string semantic similarity
	private static float labelSimilarityThreshold = 0.8f;

	private static String homeDirectory = "processrepository";

	// the directory for syn_index
	private static String synIndexDir = null;

	// the path for wordnet prolog file
	public static String wordnetPrologFilename = "wn_s.pl";

	// the maximum coverabiligy graph depth
	private static int coverabilityGraphDepth = -1;

	// query object directory
	private static String queryObjectPath = null;

	// record the log point information
	// the start sequential id for log
	private static long logPointStart = 10000;
	// the end sequential id for log
	private static long logPointEnd = 100000;
	// the number of points for log
	private static int nLogPoints = 10;

	// the span of log points calculated from the above parameter automatically
	private static long logPointSpan = (logPointEnd - logPointStart)
			/ (nLogPoints - 1);

	// the number of models in the repository
	private static long nModels = 0;

	// whether log data in log files or not
	private static boolean enableQueryLog = false;

	// whether log storage data in log files or not
	private static boolean storageDataLogged = false;

	public static void initialize() {
		loadGlobalSetting();
		queryObjectPath = homeDirectory + "/QueryModel";
		synIndexDir = homeDirectory + "/index/syn_index";
	}

	public static void storeGlobalSetting() {
		try {
			Properties ini = new Properties();
			ini.setProperty(strEnableQueryLog, String.valueOf(enableQueryLog));
			ini.setProperty(strEnableSimilarLabel, String
					.valueOf(enableSimilarLabel));
			ini.setProperty(strLabelSimilarityThreshold, String
					.valueOf(labelSimilarityThreshold));
			ini.setProperty(strAllModels2PetriNets, String
					.valueOf(ISALLMODELS2PETRINETS));
			// ini.setProperty(strHomeDirectory, homeDirectory);
			String filename = System.getProperty("user.dir", "")
					+ System.getProperty("file.separator")
					+ GLOBALSETTINGFILENAME;
			FileWriter fw = new FileWriter(filename, false);
			ini.store(fw, null);
			fw.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	private static void loadGlobalSetting() {
		try {
			Properties ini = new Properties();

			String filename = System.getProperty("user.dir", "")
					+ System.getProperty("file.separator")
					+ GlobalParameter.GLOBALSETTINGFILENAME;

			FileInputStream is = new FileInputStream(filename);
			ini.load(is);
			is.close();

			if (ini.getProperty(GlobalParameter.strEnableSimilarLabel, "false")
					.equalsIgnoreCase("true")) {
				GlobalParameter.setEnableSimilarLabel(true);
			} else {
				GlobalParameter.setEnableSimilarLabel(false);
			}

			if (ini.getProperty(GlobalParameter.strEnableQueryLog, "false")
					.equalsIgnoreCase("true")) {
				GlobalParameter.setEnableQueryLog(true);
			} else {
				GlobalParameter.setEnableQueryLog(false);
			}

			if (ini
					.getProperty(GlobalParameter.strAllModels2PetriNets,
							"false").equalsIgnoreCase("true")) {
				GlobalParameter.setALLMODELS2PETRINETS(true);
			} else {
				GlobalParameter.setALLMODELS2PETRINETS(false);
			}

			GlobalParameter.setLabelSemanticSimilarity(Float.parseFloat(ini
					.getProperty(GlobalParameter.strLabelSimilarityThreshold,
							"0.8")));

			// homeDirectory = ini.getProperty(strHomeDirectory, homeDirectory);

		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	/**
	 * @return the enableSimilarLabel
	 */
	public static boolean isEnableSimilarLabel() {
		return enableSimilarLabel;
	}

	/**
	 * @param enableSimilarLabel
	 *            the enableSimilarLabel to set
	 */
	public static void setEnableSimilarLabel(boolean enableSimilarLabel) {
		GlobalParameter.enableSimilarLabel = enableSimilarLabel;
	}

	/**
	 * @return the labelSemanticSimilarity
	 */
	public static float getLabelSemanticSimilarity() {
		return labelSimilarityThreshold;
	}

	/**
	 * @param labelSemanticSimilarity
	 *            the labelSemanticSimilarity to set
	 */
	public static void setLabelSemanticSimilarity(float labelSemanticSimilarity) {
		GlobalParameter.labelSimilarityThreshold = labelSemanticSimilarity;
	}

	/**
	 * @return the coverabilityGraphDepth
	 */
	public static int getCoverabilityGraphDepth() {
		return coverabilityGraphDepth;
	}

	/**
	 * @param coverabilityGraphDepth
	 *            the coverabilityGraphDepth to set
	 */
	public static void setCoverabilityGraphDepth(int coverabilityGraphDepth) {
		GlobalParameter.coverabilityGraphDepth = coverabilityGraphDepth;
	}

	/**
	 * @return the queryPath
	 */
	public static String getQueryObjectPath() {
		return queryObjectPath;
	}

	/**
	 * @param queryPath
	 *            the queryPath to set
	 */
	public static void setQueryObjectPath(String queryObjectPath) {
		GlobalParameter.queryObjectPath = queryObjectPath;
	}

	/**
	 * @return the logPointStart
	 */
	public static long getLogPointStart() {
		return logPointStart;
	}

	/**
	 * @param logPointStart
	 *            the logPointStart to set
	 */
	public static void setLogPointStart(long logPointStart) {
		GlobalParameter.logPointStart = logPointStart;
		if (nLogPoints == 1) {
			logPointSpan = logPointEnd;
		} else {
			logPointSpan = (logPointEnd - logPointStart) / (nLogPoints - 1);
		}
		if (logPointSpan == 0) {
			logPointSpan = 1;
		}
	}

	/**
	 * @return the logPointEnd
	 */
	public static long getLogPointEnd() {
		return logPointEnd;
	}

	/**
	 * @param logPointEnd
	 *            the logPointEnd to set
	 */
	public static void setLogPointEnd(long logPointEnd) {
		GlobalParameter.logPointEnd = logPointEnd;
		if (nLogPoints == 1) {
			logPointSpan = logPointEnd;
		} else {
			logPointSpan = (logPointEnd - logPointStart) / (nLogPoints - 1);
		}
		if (logPointSpan == 0) {
			logPointSpan = 1;
		}
	}

	/**
	 * @return the nLogPoints
	 */
	public static int getNLogPoints() {
		return nLogPoints;
	}

	/**
	 * @param logPoints
	 *            the nLogPoints to set
	 */
	public static void setNLogPoints(int logPoints) {
		nLogPoints = logPoints;
		if (nLogPoints == 1) {
			logPointSpan = logPointEnd;
		} else {
			logPointSpan = (logPointEnd - logPointStart) / (nLogPoints - 1);
		}
		if (logPointSpan == 0) {
			logPointSpan = 1;
		}
	}

	/**
	 * @return the nModels
	 */
	public static long getNModels() {
		return nModels;
	}

	/**
	 * @return the homeDirectory
	 */
	public static String getHomeDirectory() {
		return homeDirectory;
	}

	/**
	 * @param homeDirectory
	 *            the homeDirectory to set
	 */
	public static void setHomeDirectory(String homeDirectory) {
		GlobalParameter.homeDirectory = homeDirectory;
	}

	/**
	 * @param models
	 *            the nModels to set
	 */
	public static void setNModels(long models) {
		nModels = models;
	}

	/**
	 * @return the logPointSpan
	 */
	public static long getLogPointSpan() {
		return logPointSpan;
	}

	// increase the number of models by one
	public static void addOneModel() {
		nModels++;
	}

	// decrease the number of models by one
	public static void removeOneModel() {
		nModels--;
	}

	/**
	 * @return the dataLogged
	 */
	public static boolean isEnableQueryLog() {
		return enableQueryLog;
	}

	/**
	 * @param dataLogged
	 *            the dataLogged to set
	 */
	public static void setEnableQueryLog(boolean dataLogged) {
		GlobalParameter.enableQueryLog = dataLogged;
	}

	/**
	 * @return the storageDataLogged
	 */
	public static boolean isStorageDataLogged() {
		return storageDataLogged;
	}

	/**
	 * @param storageDataLogged
	 *            the storageDataLogged to set
	 */
	public static void setStorageDataLogged(boolean storageDataLogged) {
		GlobalParameter.storageDataLogged = storageDataLogged;
	}

	/**
	 * @return the synIndexDir
	 */
	public static String getSynIndexDir() {
		return synIndexDir;
	}

	/**
	 * @return the unfoldingDepth
	 */
	public static int getUnfoldingDepth() {
		return unfoldingDepth;
	}

	/**
	 * @param unfoldingDepth
	 *            the unfoldingDepth to set
	 */
	public static void setUnfoldingDepth(int unfoldingDepth) {
		GlobalParameter.unfoldingDepth = unfoldingDepth;
	}

	/**
	 * @return the iS2PETRINETS
	 */
	public static boolean isALLMODELS2PETRINETS() {
		return ISALLMODELS2PETRINETS;
	}

	/**
	 * @param is2petrinets
	 *            the iS2PETRINETS to set
	 */
	public static void setALLMODELS2PETRINETS(boolean is2petrinets) {
		ISALLMODELS2PETRINETS = is2petrinets;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
