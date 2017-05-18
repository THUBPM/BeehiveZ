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
package cn.edu.thss.iise.beehivez.client.ui.imagemaker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.util.Dot;
import org.processmining.framework.util.RuntimeUtils;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * 
 * given a model file, output the png file
 * 
 * @author Tao Jin
 * 
 * @date 2011-9-24
 * 
 */
public class ImageMaker {

	public static void Pnml2Png(String fileName) {
		PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile(fileName);
		BufferedWriter bw;
		File dotFile;
		String fileNamePrefix = fileName.substring(0, fileName
				.lastIndexOf(".pnml"));
		String pngFileName = fileNamePrefix + ".png";
		// String dotFileName = fileNamePrefix + ".dot";
		// System.out.println("png file name: " + pngFileName);
		try {
			// create temporary DOT file
			// dotFile = new File(dotFileName);
			dotFile = File.createTempFile("pmt", ".dot");
			// dotFile.deleteOnExit();
			bw = new BufferedWriter(new FileWriter(dotFile, false));
			pn.writeToDot(bw);
			bw.close();

			Process dot;

			if (RuntimeUtils.isRunningWindows()) {
				// On Windows systems, use the dot.exe
				// System.out.println("running dot on windows");
				dot = Runtime.getRuntime().exec(
						"dot" + System.getProperty("file.separator")
								+ "dot.exe -Tpng -o\"" + pngFileName + "\" \""
								+ dotFile.getAbsolutePath() + "\"");
			} else if (RuntimeUtils.isRunningMacOsX()) {
				// On Mac OS X, use the dot executable distributed within
				// Graphviz.app (from Pixelglow)
				// by default (assumes standard installation into system-wide
				// /Applications folder)
				dot = Runtime.getRuntime()
						.exec(
								"/Applications/Graphviz.app/Contents/MacOS/dot -Tpng -o"
										+ pngFileName + " "
										+ dotFile.getAbsolutePath());
			} else {
				// assume UNIX-like OS with dot executable in $PATH
				dot = Runtime.getRuntime().exec(
						"dot -Tpng -o" + pngFileName + " "
								+ dotFile.getAbsolutePath());
			}

			dot.waitFor();
			dot.destroy();
			dot = null;

			// System.out.println("dot finished");

			dotFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pnml2Png("E:/test/wedding.pnml");
	}

}
