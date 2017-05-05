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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.processmining.framework.models.petrinet.Transition;

public class BasicRltMatrix implements RltConstants {
	public long process_id = -1L;
	// temp matrix that index by Transition
	public int transitionNum = 0;
	public Map<Transition, Integer> transition2int = null;
	public byte[][] rltMatrix = null;
	// Final matrix that index by label, transitions may share same label
	private int labelNum = 0;
	private Map<String, Integer> label2int = null;
	private byte[][] rltMatrixByLabel = null;

	public int getTransitionIndex(Transition t) {
		return transition2int.get(t);
	}

	public byte getRlt(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		return rltMatrix[i1][i2];
	}

	public void setRlt(Transition t1, Transition t2, byte value) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = value;
	}

	public byte getRlt(int i1, int i2) {
		return rltMatrix[i1][i2];
	}

	public void setRlt(int i1, int i2, byte value) {
		rltMatrix[i1][i2] = value;
	}

	public void setRltParallel(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_PARALLEL);
		rltMatrix[i2][i1] = (byte) (rltMatrix[i2][i1] | BIT_PARALLEL);
	}

	public void setRltParallel(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_PARALLEL);
		rltMatrix[i2][i1] = (byte) (rltMatrix[i2][i1] | BIT_PARALLEL);
	}

	public void setRltDirSucc(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_DIR_SUCC);
	}

	public void setRltDirSucc(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_DIR_SUCC);
	}

	public void setRltIndirSucc(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_INDIR_SUCC);
	}

	public void setRltIndirSucc(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_INDIR_SUCC);
	}

	public void setRltDirCasual(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_DIR_CASUAL);
	}

	public void setRltDirCasual(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_DIR_CASUAL);
	}

	public void setRltIndirCasual(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_INDIR_CASUAL);
	}

	public void setRltIndirCasual(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_INDIR_CASUAL);
	}

	public void setRltCycle(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_CYCLE);
		rltMatrix[i2][i1] = (byte) (rltMatrix[i2][i1] | BIT_CYCLE);
	}

	public void setRltCycle(Transition t1, Transition t2) {
		int i1 = getTransitionIndex(t1);
		int i2 = getTransitionIndex(t2);
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_CYCLE);
		rltMatrix[i2][i1] = (byte) (rltMatrix[i2][i1] | BIT_CYCLE);
	}

	public void setRltMutex(int i1, int i2) {
		rltMatrix[i1][i2] = (byte) (rltMatrix[i1][i2] | BIT_MUTEX);
		rltMatrix[i2][i1] = (byte) (rltMatrix[i2][i1] | BIT_MUTEX);
	}

	public boolean isRltParallel(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_PARALLEL) == BIT_PARALLEL;
	}

	public boolean isRltDirSucc(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_DIR_SUCC) == BIT_DIR_SUCC;
	}

	public boolean isRltIndirSucc(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_INDIR_SUCC) == BIT_INDIR_SUCC;
	}

	public boolean isRltDirCasual(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_DIR_CASUAL) == BIT_DIR_CASUAL;
	}

	public boolean isRltIndirCasual(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_INDIR_CASUAL) == BIT_INDIR_CASUAL;
	}

	public boolean isRltMutex(int i1, int i2) {
		return (byte) (rltMatrix[i1][i2] & BIT_MUTEX) == BIT_MUTEX;
	}

	public Iterator<Map.Entry<Transition, Integer>> getTrnEntrysIterator() {
		return transition2int.entrySet().iterator();
	}

	public boolean isRltEqual(String leftT, String rightT, byte rs) {
		if (!label2int.containsKey(leftT) || !label2int.containsKey(rightT)) {
			return false;
		}
		int i1 = label2int.get(leftT);
		int i2 = label2int.get(rightT);
		return rltMatrixByLabel[i1][i2] == rs;
	}

	public boolean isRltContain(String leftT, String rightT, byte rs) {
		if (!label2int.containsKey(leftT) || !label2int.containsKey(rightT)) {
			return false;
		}
		int i1 = label2int.get(leftT);
		int i2 = label2int.get(rightT);
		return (rltMatrixByLabel[i1][i2] & rs) == rs;
	}

	public void mergeBySameLabel() {
		label2int = new HashMap<String, Integer>();
		Map<String, ArrayList<Integer>> label2TranIdxs = new HashMap<String, ArrayList<Integer>>();
		ArrayList<Integer> temp = null;
		String curLabel = null;
		for (Map.Entry<Transition, Integer> e : transition2int.entrySet()) {
			curLabel = e.getKey().getIdentifier().trim();
			temp = label2TranIdxs.get(curLabel);
			if (temp == null) {
				temp = new ArrayList<Integer>();
			}
			temp.add(e.getValue());
			if (temp.size() > 1)
				Collections.sort(temp);
			if (label2TranIdxs.containsKey(curLabel)) {
				label2TranIdxs.remove(curLabel);
			}
			label2TranIdxs.put(curLabel, temp);

			if (temp.size() == 1) {
				label2int.put(curLabel, e.getValue());
			} else if (temp.size() > 1) {
				label2int.remove(curLabel);
				label2int.put(curLabel, temp.get(0));
			}
		}

		labelNum = label2TranIdxs.size();
		// if need to merge?
		if (labelNum == transitionNum) {
			rltMatrixByLabel = rltMatrix;
		}
		// merge
		else {
			// copy matrix
			rltMatrixByLabel = new byte[labelNum][labelNum];
			for (Map.Entry<String, ArrayList<Integer>> e : label2TranIdxs
					.entrySet()) {
				ArrayList<Integer> lstIndexs = e.getValue();
				Collections.sort(lstIndexs);
				if (lstIndexs.size() > 1) {
					int firstIndex = lstIndexs.get(0);
					for (int i = 1; i < lstIndexs.size(); i++) {
						int indexToMerge = lstIndexs.get(i);
						// merge row
						for (int col = 0; col < transitionNum; col++) {
							rltMatrix[firstIndex][col] |= rltMatrix[indexToMerge][col];
						}
						// merge col
						for (int row = 0; row < transitionNum; row++) {
							rltMatrix[row][firstIndex] |= rltMatrix[row][indexToMerge];
						}
					}
				}
			}

			ArrayList<Integer> remainIndexList = new ArrayList<Integer>();
			for (int inx : label2int.values()) {
				remainIndexList.add(inx);
			}
			Collections.sort(remainIndexList);
			for (int i = 0; i < labelNum; i++)
				for (int j = 0; j < labelNum; j++)
					rltMatrixByLabel[i][j] = rltMatrix[remainIndexList.get(i)][remainIndexList
							.get(j)];
			// reorder lable index
			label2int = reorderMap(label2int);
		}

	}

	private Map<String, Integer> reorderMap(Map<String, Integer> map) {
		Map<Integer, String> tempMap = new TreeMap<Integer, String>();
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			tempMap.put(e.getValue(), e.getKey());
		}
		int i = 0;
		map.clear();
		for (Map.Entry<Integer, String> e : tempMap.entrySet()) {
			map.put(e.getValue(), i);
			i++;
		}
		tempMap = null;
		return map;
	}

	public byte[] serializeMatrix() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo = new ObjectOutputStream(output);
			oo.writeObject(rltMatrix);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	public byte[] serializeMatrixByLabel() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo = new ObjectOutputStream(output);
			oo.writeObject(rltMatrixByLabel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	public boolean deserializeMatrix(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream oi = new ObjectInputStream(in);
			rltMatrix = (byte[][]) oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deserializeMatrixByLabel(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream oi = new ObjectInputStream(in);
			rltMatrixByLabel = (byte[][]) oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public byte[] serializeTran2IntMap() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo = new ObjectOutputStream(output);
			oo.writeObject(transition2int);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	public byte[] serializeLabel2IntMap() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo = new ObjectOutputStream(output);
			oo.writeObject(label2int);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	public boolean deserializeTran2IntMap(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream oi = new ObjectInputStream(in);
			transition2int = (Map<Transition, Integer>) oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deserializeLabel2IntMap(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream oi = new ObjectInputStream(in);
			label2int = (Map<String, Integer>) oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void print() {
		System.out.println();
		for (int t = 0; t < transitionNum; t++) {
			for (Map.Entry<Transition, Integer> e : transition2int.entrySet()) {
				if (e.getValue() == t) {
					System.out.print(e.getKey().getIdentifier() + " ");
				}
			}
		}
		for (int i = 0; i < transitionNum; i++) {
			System.out.println();
			for (int j = 0; j < transitionNum; j++) {
				System.out.print(rltMatrix[i][j] + "  ");
			}

		}
	}

	public void printByLabel() {
		try {
			// PrintStream myout = new PrintStream(new FileOutputStream(new
			// File("C:/matrix.csv")));
			// System.setOut(myout);
			System.out.println();

			for (int t = 0; t < labelNum; t++) {
				for (Map.Entry<String, Integer> e : label2int.entrySet()) {
					if (e.getValue() == t) {
						System.out.print(e.getKey() + ", ");
					}
				}
			}
			for (int i = 0; i < labelNum; i++) {
				System.out.println();
				for (int j = 0; j < labelNum; j++) {
					byte rlt = rltMatrixByLabel[i][j];
					if (rlt != 0)
						for (int m = 0; m < RltConstants.ARR_BIT_RELATIONS.length; m++) {
							if ((rlt & RltConstants.ARR_BIT_RELATIONS[m]) == RltConstants.ARR_BIT_RELATIONS[m]) {
								System.out.print(RltConstants.ARR_RELATIONS[m]
										+ " ");
							}
						}
					else
						System.out.print(" ");
					System.out.print(", ");

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printBySortedLabel() {
		try {
			// PrintStream myout = new PrintStream(new FileOutputStream(new
			// File("C:/matrix.csv")));
			// System.setOut(myout);
			System.out.println();
			if (label2int == null || label2int.size() < 1) {
				label2int = new HashMap<String, Integer>();
				for (Map.Entry<Transition, Integer> e : transition2int
						.entrySet()) {
					label2int.put(e.getKey().getIdentifier().trim(), e
							.getValue());
				}
				labelNum = label2int.size();
				rltMatrixByLabel = rltMatrix;
			}

			SortedMap<String, Integer> sortedLabel2Int = new TreeMap<String, Integer>();
			for (Map.Entry<String, Integer> e : label2int.entrySet()) {
				sortedLabel2Int.put(e.getKey(), e.getValue());
			}

			for (Map.Entry<String, Integer> e : sortedLabel2Int.entrySet()) {
				System.out.print(e.getKey() + ", ");
			}

			Vector<String> lables = new Vector<String>();
			lables.addAll(sortedLabel2Int.keySet());

			for (int i = 0; i < labelNum; i++) {
				System.out.println();
				for (int j = 0; j < labelNum; j++) {
					byte rlt = rltMatrixByLabel[label2int.get(lables.get(i))][label2int
							.get(lables.get(j))];
					if (rlt != 0)
						for (int m = 0; m < RltConstants.ARR_BIT_RELATIONS.length; m++) {
							if ((rlt & RltConstants.ARR_BIT_RELATIONS[m]) == RltConstants.ARR_BIT_RELATIONS[m]) {
								System.out.print(RltConstants.ARR_RELATIONS[m]
										+ " ");
							}
						}
					else
						System.out.print(" ");
					System.out.print(", ");

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isContainActivity(String activity) {

		return label2int.containsKey(activity);
	}
}
