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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author JinTao
 * 
 */
public class FileLogger {

	public static void deleteLogFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void writeLog(String fileName, String log) {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(log);
			bw.newLine();
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// test
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			writeLog("a.log", String.valueOf(i));
		}
	}

}
