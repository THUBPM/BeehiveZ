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

package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.HashSet;
import java.util.Iterator;

/**
 * ���̶εļ���
 * 
 * @author He tengfei
 * 
 */
public class BasicProcessSet {
	private HashSet<BasicProcess> pSet = null;
	private String name; // ���̶μ��ϵ����

	public BasicProcessSet(String name) {
		this.name = name;
	}

	public HashSet<BasicProcess> getPSet() {
		return pSet;
	}

	public void setPSet(HashSet<BasicProcess> set) {
		pSet = set;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void add(BasicProcess process) {
		if (pSet == null) {
			pSet = new HashSet<BasicProcess>();
		}
		pSet.add(process);
	}

	/**
	 * �������̶μ���֮��ıȽϣ�get similarity between process Set
	 * 
	 * @return
	 */
	public double getSimilarityBS(BasicProcessSet pSet2) {
		HashSet<BasicProcess> temp = pSet2.getPSet();
		if (pSet == null && temp != null) {
			return 0.0;
		} else if (pSet != null && temp == null) {
			return 0.0;
		} else if (pSet == null && temp == null) {
			return 1.0;
		} else {
			int rowCount = pSet.size();
			int colCount = temp.size();
			double[][] distanceMatrix = new double[rowCount][colCount];
			BasicProcess[] rowElements = new BasicProcess[rowCount];
			Iterator<BasicProcess> it = pSet.iterator();
			int index = 0;
			while (it.hasNext()) {
				rowElements[index] = (BasicProcess) it.next();
				index++;
			}
			BasicProcess[] colElements = new BasicProcess[colCount];
			it = temp.iterator();
			index = 0;
			while (it.hasNext()) {
				colElements[index] = (BasicProcess) it.next();
				index++;
			}
			for (int i = 0; i < rowElements.length; i++) {
				for (int j = 0; j < colElements.length; j++) {
					double simi = rowElements[i].getSimilarity(colElements[j]);
					distanceMatrix[i][j] = simi;
				}
			}
			double result = 0;
			for (int i = 0; i < rowElements.length; i++) {
				double maxValue = distanceMatrix[i][0];
				for (int j = 1; j < colElements.length; j++) {
					if (distanceMatrix[i][j] > maxValue) {
						maxValue = distanceMatrix[i][j];
					}
				}
				result += maxValue;
			}
			for (int i = 0; i < colElements.length; i++) {
				double maxValue = distanceMatrix[0][i];
				for (int j = 1; j < rowElements.length; j++) {
					if (distanceMatrix[j][i] > maxValue) {
						maxValue = distanceMatrix[j][i];
					}
				}
				result += maxValue;
			}
			return result / (rowCount + colCount);
		}
	}
}
