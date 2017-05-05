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

import java.io.IOException;

import org.processmining.framework.log.AuditTrailEntry;
import org.processmining.framework.log.AuditTrailEntryList;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogFilter;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.log.LogSummary;
import org.processmining.framework.log.classic.LogReaderClassic;
import org.processmining.framework.log.filter.DefaultLogFilter;
import org.processmining.framework.log.filter.DistancePreservingObfuscationFilter;
import org.processmining.framework.log.rfb.fsio.FS2VirtualFileSystem;
import org.processmining.framework.ui.menus.LogReaderMenu;
import org.processmining.framework.util.StopWatch;

/*
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * License to link and use is also granted to open source programs which
 * are not licensed under the terms of the GPL, given that they satisfy one
 * or more of the following conditions:
 * 1) Explicit license is granted to the ProM and ProMimport programs for
 *    usage, linking, and derivative work.
 * 2) Carte blance license is granted to all programs developed at
 *    Eindhoven Technical University, The Netherlands, or under the
 *    umbrella of STW Technology Foundation, The Netherlands.
 * For further exemptions not covered by the above conditions, please
 * contact the author of this code.
 * 
 */

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class LogReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// set up benchmark
		try {
			// LogReaderFactory.setLogReaderClass(BufferedLogReader.class);
			LogReaderFactory.setLogReaderClass(LogReaderClassic.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogReaderMenu.storageProvider = FS2VirtualFileSystem.instance();
		FS2VirtualFileSystem.instance().setUseLazyCopies(true);

		// open log
		System.out.println("Loading log...");
		StopWatch timer = new StopWatch();
		timer.start();
		LogReader log = openLog("/Users/christian/Desktop/logreader_benchmark/logs/PMS.mxml.gz");
		// LogReader log =
		// openLog("/Users/christian/Desktop/logreader_benchmark/logs/WWW.mxml.gz");
		timer.stop();
		System.out.println("Loaded log in " + timer.formatDuration());
		reportMemory();
		System.out.println();

		// read test
		System.out.println("Reading log...");
		readBenchmark(log, 1);
		reportMemory();
		System.out.println();

		// filter log
		System.out.println("Filtering log...");
		timer.start();
		LogReader logFiltered = deriveFilteredLog(log);
		timer.stop();
		System.out.println("Filtered log in " + timer.formatDuration());
		reportMemory();
		System.out.println();

		// filtered read test
		System.out.println("Reading filtered log...");
		readBenchmark(logFiltered, 1);
		reportMemory();
		System.out.println();
	}

	public static LogReader openLog(String addr) {
		StopWatch timer = new StopWatch();
		timer.start();
		LogFile logFile = LogFile.getInstance(addr);
		LogReader log = null;
		try {
			log = LogReaderFactory.createInstance(new DefaultLogFilter(
					DefaultLogFilter.INCLUDE), logFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogSummary summary = log.getLogSummary();
		summary.getNumberOfAuditTrailEntries();
		return log;
	}

	public static LogReader deriveFilteredLog(LogReader log) {
		LogFilter filter = new DistancePreservingObfuscationFilter();
		filter.setLowLevelFilter(log.getLogFilter());
		LogReader filteredLog = null;
		try {
			filteredLog = LogReaderFactory.createInstance(filter, log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// force creation / filtering
		int num = filteredLog.getLogSummary().getNumberOfAuditTrailEntries();
		num++;
		return filteredLog;
	}

	public static void readBenchmark(LogReader log, int iterations) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			System.out.print('.');
			read(log);
		}
		time = System.currentTimeMillis() - time;
		time /= iterations;
		System.out.println();
		System.out.println("Mean read cycle time: "
				+ StopWatch.formatDuration(time) + " (" + iterations
				+ " iterations)");
	}

	public static void read(LogReader log) {
		long counter = 0;
		long memUsageComplete = 0;
		int memUsageCounter = 0;
		for (int i = 0; i < log.numberOfInstances(); i++) {
			AuditTrailEntryList ateList = log.getInstance(i)
					.getAuditTrailEntryList();
			for (int j = 0; j < ateList.size(); j++) {
				try {
					AuditTrailEntry ate = ateList.get(j);
					String wfme = ate.getElement();
					wfme.trim();
				} catch (IndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				counter++;
				if (counter % 100 == 0) {
					memUsageComplete += memoryUsage();
					memUsageCounter++;
				}
			}
		}
		long memUsageMean = memUsageComplete / memUsageCounter;
		System.out.println("Mean memory usage during reading: "
				+ (memUsageMean / 1024) + " kB");
	}

	public static long memoryUsage() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

	public static void reportMemory() {
		System.gc();
		System.out.println("Memory used: " + (memoryUsage() / 1024) + " kB");
	}

}
