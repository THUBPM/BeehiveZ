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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * write the execute info
 * 
 * @author He tengfei
 * 
 */
public class ExecuteLog {
	// where the execute info store
	private String fileDir = null;
	private BufferedWriter bw = null;

	public ExecuteLog(String fileDir) {
		this.fileDir = fileDir;
	}

	public void open() {
		try {
			bw = new BufferedWriter(new FileWriter(fileDir, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * clear the log info
	 */
	public void clear() {
		File f = new File(fileDir);
		if (f.exists()) {
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void writeClusterVertexSet(HashSet<CTreeNode> clusterVertexSet) {
		// TODO Auto-generated method stub
		try {
			bw.write("the clusterVertexSet info:");
			outputNodes(clusterVertexSet);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void outputNodes(HashSet<CTreeNode> nodes) {
		// TODO Auto-generated method stub
		try {
			if (nodes == null || nodes.size() <= 0) {
				return;
			}
			Iterator<CTreeNode> it = nodes.iterator();
			while (it.hasNext()) {
				CTreeNode node = (CTreeNode) it.next();
				bw.write(node.getId() + ", ");
			}
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeCutOffVertex(HashSet<CTreeNode> cutOffVertexs) {
		// TODO Auto-generated method stub
		try {
			bw.write("the cutOffVertexs info:");
			outputNodes(cutOffVertexs);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeCTree(CTree ctree) {
		// TODO Auto-generated method stub
		try {
			bw.write("the coverability tree info:");
			outputCTree(ctree);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void outputCTree(CTree ctree) {
		// TODO Auto-generated method stub
		try {
			bw.newLine();
			ArrayList<CTreeNode> allNodes = ctree.getAllNodes();
			bw.write("node info��");
			bw.newLine();
			for (int i = 1; i <= allNodes.size(); i++) {
				bw.write(allNodes.get(i - 1).toString() + " ");
				if (i % 4 == 0) {
					bw.newLine();
				}
			}
			bw.newLine();
			bw.write("edge info��");
			HashMap<String, MyPetriTransition> edges = ctree.getAllEdges();
			Set set = edges.entrySet();
			Iterator it = set.iterator();
			int temp = 0;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				MyPetriTransition value = (MyPetriTransition) entry.getValue();
				StringBuffer buffer = new StringBuffer();
				buffer.append("(");
				buffer.append(key + " " + value.getName() + ")  ");
				if (temp % 4 == 0) {
					bw.newLine();
				}
				bw.write(buffer.toString());
				temp++;
			}
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeCRTree(CTree crtree) {
		// TODO Auto-generated method stub
		try {
			bw.write("the characteristic reachability tree info:");
			outputCTree(crtree);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeBPS(LinkedList<BasicProcessSet> bps) {
		// TODO Auto-generated method stub
		try {
			bw.write("the basic process segments:");
			bw.newLine();
			for (int i = 0; i < bps.size(); i++) {
				// LinkedList list = (LinkedList) basicProcessSet.get(i);
				BasicProcessSet basicProcessSet = (BasicProcessSet) bps.get(i);
				bw.write(basicProcessSet.getName() + ":");
				bw.newLine();
				HashSet set = basicProcessSet.getPSet();
				if (set == null) {
					continue;
				}
				Iterator it = set.iterator();
				while (it.hasNext()) {
					BasicProcess basicProcess = (BasicProcess) it.next();
					Vector process = basicProcess.getProcess();
					StringBuffer sb = new StringBuffer();
					sb.append("(");
					for (int h = 0; h < process.size(); h++) {
						MyPetriTransition pt = (MyPetriTransition) process
								.get(h);
						if (h == process.size() - 1) {
							sb.append(pt.getName() + ") ");
						} else {
							sb.append(pt.getName() + ",");
						}
					}
					bw.write(sb.toString() + " ");
				}
				bw.newLine();
			}
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeCutOffDotPairSet(HashSet<GVBElement> cutOffDotPairSet) {
		// TODO Auto-generated method stub
		try {
			bw.write("the cutOffDotPairSet info:");
			bw.newLine();
			if (cutOffDotPairSet == null || cutOffDotPairSet.size() <= 0) {
				return;
			}
			Iterator<GVBElement> it = cutOffDotPairSet.iterator();
			while (it.hasNext()) {
				GVBElement element = (GVBElement) it.next();
				CTreeNode from = element.getFrom();
				CTreeNode to = element.getTo();
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				sb.append(from.getId() + ",");
				sb.append(to.getId() + ") ");
				bw.write(sb.toString());
			}
			bw.newLine();
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeString(String info) {
		try {
			bw.write(info);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeLines(int newLineCount) {
		// TODO Auto-generated method stub
		for (int i = 0; i < newLineCount; i++) {
			try {
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
