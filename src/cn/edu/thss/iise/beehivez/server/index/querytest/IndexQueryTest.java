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
package cn.edu.thss.iise.beehivez.server.index.querytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.processmining.framework.models.petrinet.PetriNet;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.filelogger.Indexlogger;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author Tao Jin
 * 
 */
public class IndexQueryTest {

	public static void queryTest(String queryModelDirectory, int repeatCount) {

		DataManager dm = DataManager.getInstance();
		Vector<String> indexesSupportGraph = dm
				.getAllUsedIndexNameSupportGraphQuery();
		int nIndexSupportGraph = indexesSupportGraph.size();
		long[] nGraphQuerySuccessfully = new long[nIndexSupportGraph];
		long[] nGraphQuery = new long[nIndexSupportGraph];
		for (int i = 0; i < nIndexSupportGraph; i++) {
			nGraphQuerySuccessfully[i] = 0;
			nGraphQuery[i] = 0;
		}

		Vector<String> indexesSupportText = dm
				.getAllUsedIndexNameSupportTextQuery();
		int nIndexSupportText = indexesSupportText.size();
		long[] nTextQuerySuccessfully = new long[nIndexSupportText];
		long[] nTextQuery = new long[nIndexSupportText];
		for (int i = 0; i < nIndexSupportText; i++) {
			nTextQuerySuccessfully[i] = 0;
			nTextQuery[i] = 0;
		}

		// record the failed query file name
		HashSet<String> failedQueries = new HashSet<String>();

		File fQueryModelDir = new File(queryModelDirectory);

		// record the storage size of every index here
		float processTableSize = dm.getProcessTableSizeInMB();
		long nModels = GlobalParameter.getNModels();
		Indexlogger.logIndexStorageSize("raw", processTableSize, nModels);
		Iterator<BPMIndex> it = dm.getAllUsedIndexsIterator();
		while (it.hasNext()) {
			BPMIndex index = it.next();
			Indexlogger.logIndexStorageSize(index.getName(), index
					.getStorageSizeInMB(), nModels);
		}

		for (int i = 0; i < repeatCount; i++) {
			// test for Petri nets
			for (File f : fQueryModelDir.listFiles()) {
				if (f.isFile() && f.getName().endsWith(".pnml")) {
					try {
						String filepath = f.getAbsolutePath();
						System.out.println("query test for " + filepath);
						PetriNet query = PetriNetUtil
								.getPetriNetFromPnmlFile(filepath);
						query.setIdentifier(f.getName());
						for (String indexName : dm
								.getAllUsedPetriNetIndexNameSupportGraphQuery()) {
							TreeSet<ProcessQueryResult> v = dm.retrieveProcess(
									query, indexName, 0);
							nGraphQuery[indexesSupportGraph.indexOf(indexName)]++;
							if (v != null) {
								if (v.size() > 0) {
									nGraphQuerySuccessfully[indexesSupportGraph
											.indexOf(indexName)]++;
								} else {
									failedQueries.add(f.getName()
											+ " [failed on] " + indexName);
								}
							} else {
								failedQueries.add(f.getName() + " [failed on] "
										+ indexName);
							}
						}
						query.destroyPetriNet();
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}

				// test for yawl models
				if (f.isFile() && f.getName().endsWith(".yawl")) {
					try {
						String filepath = f.getAbsolutePath();
						System.out.println("query test for " + filepath);
						YNet net = YAWLUtil.getYNetFromFile(filepath);
						for (String indexName : dm
								.getAllUsedYAWLIndexNameSupportGraphQuery()) {
							TreeSet<ProcessQueryResult> v = dm.retrieveProcess(
									net, indexName, 0);
							nGraphQuery[indexesSupportGraph.indexOf(indexName)]++;
							if (v != null) {
								if (v.size() > 0) {
									nGraphQuerySuccessfully[indexesSupportGraph
											.indexOf(indexName)]++;
								} else {
									failedQueries.add(f.getName()
											+ " [failed on] " + indexName);
								}
							} else {
								failedQueries.add(f.getName() + " [failed on] "
										+ indexName);
							}
						}
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			}

			// test for text query
			// read the text query string
			try {
				File fTextQuery = new File(queryModelDirectory, "query.str");
				if (fTextQuery.exists()) {
					FileReader fr = new FileReader(fTextQuery);
					BufferedReader br = new BufferedReader(fr);

					String query = br.readLine();
					while (query != null) {
						System.out.println("query test for " + query);
						for (int k = 0; k < nIndexSupportText; k++) {
							String indexName = dm
									.getAllUsedIndexNameSupportTextQuery().get(
											k);

							TreeSet<ProcessQueryResult> v = dm.retrieveProcess(
									query, indexName, 0);
							nTextQuery[k]++;
							if (v != null && v.size() > 0) {
								nTextQuerySuccessfully[k]++;
							} else {
								failedQueries.add(query + " [failed on] "
										+ indexName);
							}
						}
						query = br.readLine();
					}

					br.close();
					fr.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		String queryTestResult = "";
		for (int k = 0; k < nIndexSupportGraph && nGraphQuery[k] > 0; k++) {
			String indexName = dm.getAllUsedIndexNameSupportGraphQuery().get(k);
			queryTestResult += "\n\r****************\n\rgraph query test using "
					+ indexName
					+ "\n\r count of query: "
					+ nGraphQuery[k]
					+ "\n\r count of query successfully: "
					+ nGraphQuerySuccessfully[k]
					+ "\n\r query success retio: "
					+ (float) nGraphQuerySuccessfully[k]
					/ (float) nGraphQuery[k] * 100 + "%";
		}
		for (int k = 0; k < nIndexSupportText && nTextQuery[k] > 0; k++) {
			String indexName = dm.getAllUsedIndexNameSupportTextQuery().get(k);
			queryTestResult += "\n\r****************\n\rtext query test using "
					+ indexName + "\n\r count of query: " + nTextQuery[k]
					+ "\n\r count of query successfully: "
					+ nTextQuerySuccessfully[k] + "\n\r query success retio: "
					+ (float) nTextQuerySuccessfully[k] / (float) nTextQuery[k]
					* 100 + "%";
		}
		for (String failedQuery : failedQueries) {
			queryTestResult += "\n\r" + failedQuery;
		}
		JOptionPane.showMessageDialog(null, queryTestResult);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
