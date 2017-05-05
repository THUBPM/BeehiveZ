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

import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * class BasicProcess is used to definition the transition sequence of a process
 * segments.
 * 
 * @author He tengfei
 * 
 */
public class BasicProcess {
	private Vector<MyPetriTransition> process;

	public BasicProcess() {
		process = new Vector<MyPetriTransition>();
	}

	public void addTransition(MyPetriTransition pt) {
		process.add(pt);
	}

	public Vector<MyPetriTransition> getProcess() {
		return process;
	}

	public void setProcess(Vector<MyPetriTransition> process) {
		this.process = process;
	}

	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < process.size(); i++) {
			sb.append(process.get(i).getName());
		}
		return sb.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		BasicProcess temp = (BasicProcess) obj;
		Vector<MyPetriTransition> tempProcess = temp.getProcess();
		if (tempProcess.size() != process.size()) {
			return false;
		} else {
			for (int i = 0; i < process.size(); i++) {

				if (!((MyPetriTransition) process.get(i)).getName().equals(
						((MyPetriTransition) tempProcess.get(i)).getName())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ��ȡ���̶�֮���������ֵ
	 * 
	 * @param other
	 * @return ���double��ֵ���ڵ���0.99������Ϊ���������̶���һ��ġ�
	 */
	public double getSimilarity(BasicProcess other) {
		int largeSize = process.size();
		int otherSize = other.getProcess().size();
		if (otherSize > largeSize) {
			largeSize = otherSize;
		}
		int longestSubSeqLen = getLcsLength(other);
		return longestSubSeqLen * 1.0 / largeSize;
	}

	/**
	 * ��ȡ����̶���other��̶ε�����������еĳ���
	 * 
	 * @param other
	 * @return
	 */
	public int getLcsLength(BasicProcess other) {
		int m = process.size();
		Vector<MyPetriTransition> otherProcess = other.getProcess();
		int n = otherProcess.size();
		int[][] c = new int[m + 1][n + 1];
		for (int i = 1; i <= m; i++) {
			c[i][0] = 0;
		}
		for (int j = 1; j <= n; j++) {
			c[0][j] = 0;
		}
		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (process.get(i - 1).getName().toLowerCase().equals(
						otherProcess.get(j - 1).getName().toLowerCase())) {
					c[i][j] = c[i - 1][j - 1] + 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
				} else {
					c[i][j] = c[i][j - 1];
				}
			}
		}
		return c[m][n];
	}
}
