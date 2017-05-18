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
package cn.edu.thss.iise.beehivez.server.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author Tao Jin
 * 
 */
public class ToolKit {

	/**
	 * check whether the given subStrings is the subset of supStrings, string
	 * similarity is considered.
	 * 
	 * @param subs
	 * @param sups
	 * @return
	 */
	public static boolean strSubSet(String[] subStrings, String[] supStrings) {
		int nSubString = subStrings.length;
		int nSupString = supStrings.length;

		if (nSubString == 0) {
			return true;
		} else if (nSubString > nSupString) {
			return false;
		}

		boolean[][] map = new boolean[nSubString][nSupString];
		for (int i = 0; i < nSubString; i++) {
			boolean flag = false;
			for (int j = 0; j < nSupString; j++) {
				map[i][j] = false;
				if (subStrings[i].equalsIgnoreCase(supStrings[j])) {
					map[i][j] = true;
					flag = true;
				} else if (GlobalParameter.isEnableSimilarLabel()) {
					if (StringSimilarityUtil.semanticSimilarity(subStrings[i],
							supStrings[j]) >= GlobalParameter
							.getLabelSemanticSimilarity()) {
						map[i][j] = true;
						flag = true;
					}
				}
			}
			if (!flag) {
				return false;
			}
		}

		// check whether can find an one-one map
		return existOneOneMap(map);

	}

	// check whether can find an one-one map from row index to column index.
	// the number of rows must not be greater than the number of columns.
	public static boolean existOneOneMap(boolean[][] map) {
		int nSub = map.length;
		int nSup = map[0].length;

		if (nSub > nSup) {
			return false;
		}

		// check whether can find an one-one map
		int[] subMapTo = new int[nSub];
		for (int i = 0; i < nSub; i++) {
			subMapTo[i] = -1;
		}
		boolean[] supMapped = new boolean[nSup];
		for (int i = 0; i < nSup; i++) {
			supMapped[i] = false;
		}
		int depth = 0;
		while (true) {
			do {
				subMapTo[depth]++;
			} while (subMapTo[depth] < nSup
					&& (!map[depth][subMapTo[depth]] || supMapped[subMapTo[depth]]));
			if (subMapTo[depth] >= nSup) {
				subMapTo[depth] = -1;
				depth--;
				if (depth < 0) {
					return false;
				}
				if (subMapTo[depth] > 0) {
					supMapped[subMapTo[depth]] = false;
				}
			} else {
				supMapped[subMapTo[depth]] = true;
				depth++;
				if (depth >= nSub) {
					return true;
				}
			}
		}
	}

	public static byte[] getBytesFromInputStream(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
