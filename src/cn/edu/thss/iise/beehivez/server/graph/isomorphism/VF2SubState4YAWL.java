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
package cn.edu.thss.iise.beehivez.server.graph.isomorphism;

import java.util.ArrayList;
import java.util.Iterator;

import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;

import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * @author Tao Jin
 * 
 * @date 2012-3-5
 * 
 */
public class VF2SubState4YAWL {
	private int core_len, orig_core_len;
	private int added_node_sub;
	private int tsubboth_len, tsupboth_len, tsubin_len, tsubout_len,
			tsupin_len, tsupout_len;
	private int[] core_sub;
	private int[] core_sup;
	private int[] in_sub;
	private int[] in_sup;
	private int[] out_sub;
	private int[] out_sup;
	private int[] order;
	private YNet subnet, supnet;
	private int nsub, nsup;
	private long[] share_count;

	private ArrayList<YExternalNetElement> subElements, supElements;
	private boolean[][] elementMapMatrix;

	public VF2SubState4YAWL(YNet sub, YNet sup, boolean[][] eMapMatrix) {
		this(sub, sup, eMapMatrix, false);
	}

	public VF2SubState4YAWL(YNet sub, YNet sup, boolean[][] eMapMatrix,
			boolean sortNodes) {
		subnet = sub;
		supnet = sup;
		subElements = YAWLUtil.getNetElements(subnet);
		supElements = YAWLUtil.getNetElements(supnet);

		nsub = subElements.size();
		nsup = supElements.size();

		elementMapMatrix = eMapMatrix;

		if (sortNodes) {
			// can be changed in the future with some other sequence order
			order = null;
		} else {
			order = null;
		}

		core_len = orig_core_len = 0;
		tsubboth_len = tsubin_len = tsubout_len = 0;
		tsupboth_len = tsupin_len = tsupout_len = 0;

		added_node_sub = -1;

		core_sub = new int[nsub];
		core_sup = new int[nsup];
		in_sub = new int[nsub];
		in_sup = new int[nsup];
		out_sub = new int[nsub];
		out_sup = new int[nsup];
		share_count = new long[1];

		int i;
		for (i = 0; i < nsub; i++) {
			core_sub[i] = -1;
			in_sub[i] = 0;
			out_sub[i] = 0;
		}
		for (i = 0; i < nsup; i++) {
			core_sup[i] = -1;
			in_sup[i] = 0;
			out_sup[i] = 0;
		}

		share_count[0] = 1;
	}

	public VF2SubState4YAWL(VF2SubState4YAWL state) {
		subnet = state.subnet;
		supnet = state.supnet;
		nsub = state.nsub;
		nsup = state.nsup;

		subElements = state.subElements;
		supElements = state.supElements;
		elementMapMatrix = state.elementMapMatrix;

		order = state.order;

		core_len = orig_core_len = state.core_len;
		tsubin_len = state.tsubin_len;
		tsubout_len = state.tsubout_len;
		tsubboth_len = state.tsubboth_len;
		tsupin_len = state.tsupin_len;
		tsupout_len = state.tsupout_len;
		tsupboth_len = state.tsupboth_len;

		added_node_sub = -1;

		core_sub = state.core_sub;
		core_sup = state.core_sup;
		in_sub = state.in_sub;
		in_sup = state.in_sup;
		out_sub = state.out_sub;
		out_sup = state.out_sup;
		share_count = state.share_count;

		++share_count[0];
	}

	public YNet getSubNet() {
		return subnet;
	}

	public YNet getSupNet() {
		return supnet;
	}

	public boolean nextPair(int[] pnsub, int[] pnsup) {
		return this.nextPair(pnsub, pnsup, -1, -1);
	}

	public boolean nextPair(int[] pnsub, int[] pnsup, int prev_nsub,
			int prev_nsup) {
		if (prev_nsub == -1) {
			prev_nsub = 0;
		}
		if (prev_nsup == -1) {
			prev_nsup = 0;
		} else {
			prev_nsup++;
		}

		if (tsubboth_len > core_len && tsupboth_len > core_len) {
			while (prev_nsub < nsub
					&& (core_sub[prev_nsub] != -1 || out_sub[prev_nsub] == 0 || in_sub[prev_nsub] == 0)) {
				prev_nsub++;
				prev_nsup = 0;
			}
		} else if (tsubout_len > core_len && tsupout_len > core_len) {
			while (prev_nsub < nsub
					&& (core_sub[prev_nsub] != -1 || out_sub[prev_nsub] == 0)) {
				prev_nsub++;
				prev_nsup = 0;
			}
		} else if (tsubin_len > core_len && tsupin_len > core_len) {
			while (prev_nsub < nsub
					&& (core_sub[prev_nsub] != -1 || in_sub[prev_nsub] == 0)) {
				prev_nsub++;
				prev_nsup = 0;
			}
		} else if (prev_nsub == 0 && order != null) {
			int i = 0;
			while (i < nsub && core_sub[prev_nsub = order[i]] != -1)
				i++;
			if (i == nsub)
				prev_nsub = nsub;
		} else {
			while (prev_nsub < nsub && core_sub[prev_nsub] != -1) {
				prev_nsub++;
				prev_nsup = 0;
			}
		}

		if (tsubboth_len > core_len && tsupboth_len > core_len) {
			while (prev_nsup < nsup
					&& (core_sup[prev_nsup] != -1 || out_sup[prev_nsup] == 0 || in_sup[prev_nsup] == 0)) {
				prev_nsup++;
			}
		} else if (tsubout_len > core_len && tsupout_len > core_len) {
			while (prev_nsup < nsup
					&& (core_sup[prev_nsup] != -1 || out_sup[prev_nsup] == 0)) {
				prev_nsup++;
			}
		} else if (tsubin_len > core_len && tsupin_len > core_len) {
			while (prev_nsup < nsup
					&& (core_sup[prev_nsup] != -1 || in_sup[prev_nsup] == 0)) {
				prev_nsup++;
			}
		} else {
			while (prev_nsup < nsup && core_sup[prev_nsup] != -1) {
				prev_nsup++;
			}
		}

		if (prev_nsub < nsub && prev_nsup < nsup) {
			pnsub[0] = prev_nsub;
			pnsup[0] = prev_nsup;
			return true;
		}

		return false;
	}

	public boolean isFeasiblePair(int indexSub, int indexSup) {

		if (indexSub > nsub || indexSup > nsup || core_sub[indexSub] != -1
				|| core_sup[indexSup] != -1) {
			return false;
		}

		YExternalNetElement nodeSub = subElements.get(indexSub);
		YExternalNetElement nodeSup = supElements.get(indexSup);

		if (!elementMapMatrix[indexSub][indexSup]) {
			return false;
		}

		int othersub = 0, othersup = 0;
		int posttermoutsub = 0, pretermoutsub = 0, posttermoutsup = 0, pretermoutsup = 0, postterminsub = 0, preterminsub = 0, postterminsup = 0, preterminsup = 0, postnewsub = 0, prenewsub = 0, postnewsup = 0, prenewsup = 0;

		// Check the 'out' edges of nodesub
		Iterator<YExternalNetElement> it = nodeSub.getPostsetElements()
				.iterator();
		while (it.hasNext()) {
			othersub = subElements.indexOf(it.next());
			if (core_sub[othersub] != -1) {
				othersup = core_sub[othersub];
				if (!nodeSup.getPostsetElements().contains(
						supElements.get(othersup))) {
					return false;
				}
			} else {
				if (in_sub[othersub] > 0) {
					postterminsub++;
				}
				if (out_sub[othersub] > 0) {
					posttermoutsub++;
				}
				if (in_sub[othersub] == 0 && out_sub[othersub] == 0) {
					postnewsub++;
				}
			}
		}

		// Check the 'in' edges of nodesub
		it = nodeSub.getPresetElements().iterator();
		while (it.hasNext()) {
			othersub = subElements.indexOf(it.next());
			if (core_sub[othersub] != -1) {
				othersup = core_sub[othersub];
				if (!nodeSup.getPresetElements().contains(
						supElements.get(othersup))) {
					return false;
				}
			} else {
				if (in_sub[othersub] > 0) {
					preterminsub++;
				}
				if (out_sub[othersub] > 0) {
					pretermoutsub++;
				}
				if (in_sub[othersub] == 0 && out_sub[othersub] == 0) {
					prenewsub++;
				}
			}
		}

		// Check the 'out' edges of nodesup
		it = nodeSup.getPostsetElements().iterator();
		while (it.hasNext()) {
			othersup = supElements.indexOf(it.next());
			if (core_sup[othersup] != -1) {
				othersub = core_sup[othersup];
				if (!nodeSub.getPostsetElements().contains(
						subElements.get(othersub))) {
					return false;
				}
			} else {
				if (in_sup[othersup] > 0) {
					postterminsup++;
				}
				if (out_sup[othersup] > 0) {
					posttermoutsup++;
				}
				if (in_sup[othersup] == 0 && out_sup[othersup] == 0) {
					postnewsup++;
				}
			}
		}

		// Check the 'in' edges of nodesup
		it = nodeSup.getPresetElements().iterator();
		while (it.hasNext()) {
			othersup = supElements.indexOf(it.next());
			if (core_sup[othersup] != -1) {
				othersub = core_sup[othersup];
				if (!nodeSub.getPresetElements().contains(
						subElements.get(othersub))) {
					return false;
				}
			} else {
				if (in_sup[othersup] > 0) {
					preterminsup++;
				}
				if (out_sup[othersup] > 0) {
					pretermoutsup++;
				}
				if (in_sup[othersup] == 0 && out_sup[othersup] == 0) {
					prenewsup++;
				}
			}
		}

		return postterminsub <= postterminsup && preterminsub <= preterminsup
				&& posttermoutsub <= posttermoutsup
				&& pretermoutsub <= pretermoutsup && postnewsub <= postnewsup
				&& prenewsub <= prenewsup;
	}

	public void addPair(int indexsub, int indexsup) {
		if (indexsub >= nsub || indexsup >= nsup || core_len >= nsub
				|| core_len >= nsup) {
			System.out.println("vf2 cannot add new pair");
			return;
		}

		core_len++;
		added_node_sub = indexsub;

		if (in_sub[indexsub] == 0) {
			in_sub[indexsub] = core_len;
			tsubin_len++;
			if (out_sub[indexsub] > 0)
				tsubboth_len++;
		}
		if (out_sub[indexsub] == 0) {
			out_sub[indexsub] = core_len;
			tsubout_len++;
			if (in_sub[indexsub] > 0)
				tsubboth_len++;
		}

		if (in_sup[indexsup] == 0) {
			in_sup[indexsup] = core_len;
			tsupin_len++;
			if (out_sup[indexsup] > 0)
				tsupboth_len++;
		}
		if (out_sup[indexsup] == 0) {
			out_sup[indexsup] = core_len;
			tsupout_len++;
			if (in_sup[indexsup] > 0)
				tsupboth_len++;
		}

		core_sub[indexsub] = indexsup;
		core_sup[indexsup] = indexsub;

		int other;
		Iterator<YExternalNetElement> it = subElements.get(indexsub)
				.getPresetElements().iterator();
		while (it.hasNext()) {
			other = subElements.indexOf(it.next());
			if (in_sub[other] == 0) {
				in_sub[other] = core_len;
				tsubin_len++;
				if (out_sub[other] > 0) {
					tsubboth_len++;
				}
			}
		}

		it = subElements.get(indexsub).getPostsetElements().iterator();
		while (it.hasNext()) {
			other = subElements.indexOf(it.next());
			if (out_sub[other] == 0) {
				out_sub[other] = core_len;
				tsubout_len++;
				if (in_sub[other] > 0) {
					tsubboth_len++;
				}
			}
		}

		it = supElements.get(indexsup).getPresetElements().iterator();
		while (it.hasNext()) {
			other = supElements.indexOf(it.next());
			if (in_sup[other] == 0) {
				in_sup[other] = core_len;
				tsupin_len++;
				if (out_sup[other] > 0) {
					tsupboth_len++;
				}
			}
		}

		it = supElements.get(indexsup).getPostsetElements().iterator();
		while (it.hasNext()) {
			other = supElements.indexOf(it.next());
			if (out_sup[other] == 0) {
				out_sup[other] = core_len;
				tsupout_len++;
				if (in_sup[other] > 0) {
					tsupboth_len++;
				}
			}
		}
	}

	public boolean isGoal() {
		return core_len == nsub;
	}

	public boolean isDead() {
		return nsub > nsup || tsubboth_len > tsupboth_len
				|| tsubout_len > tsupout_len || tsubin_len > tsupin_len;
	}

	public int coreLen() {
		return core_len;
	}

	public void getCoreSet(int[] c1, int[] c2) {
		int i, j;
		for (i = 0, j = 0; i < nsub; i++)
			if (core_sub[i] != -1) {
				c1[j] = i;
				c2[j] = core_sub[i];
				j++;
			}
	}

	public void backTrack() {
		if (core_len - orig_core_len > 1 || added_node_sub == -1) {
			System.out.println("something wrong with vf2 state backtrack");
			return;
		}

		if (orig_core_len < core_len) {
			int nodesup;

			if (in_sub[added_node_sub] == core_len) {
				in_sub[added_node_sub] = 0;
			}
			Iterator<YExternalNetElement> it = subElements.get(added_node_sub)
					.getPresetElements().iterator();
			while (it.hasNext()) {
				int other = subElements.indexOf(it.next());
				if (in_sub[other] == core_len) {
					in_sub[other] = 0;
				}
			}

			if (out_sub[added_node_sub] == core_len) {
				out_sub[added_node_sub] = 0;
			}
			it = subElements.get(added_node_sub).getPostsetElements()
					.iterator();
			while (it.hasNext()) {
				int other = subElements.indexOf(it.next());
				if (out_sub[other] == core_len) {
					out_sub[other] = 0;
				}
			}

			nodesup = core_sub[added_node_sub];

			if (in_sup[nodesup] == core_len) {
				in_sup[nodesup] = 0;
			}
			it = supElements.get(nodesup).getPresetElements().iterator();
			while (it.hasNext()) {
				int other = supElements.indexOf(it.next());
				if (in_sup[other] == core_len) {
					in_sup[other] = 0;
				}
			}

			if (out_sup[nodesup] == core_len) {
				out_sup[nodesup] = 0;
			}
			it = supElements.get(nodesup).getPostsetElements().iterator();
			while (it.hasNext()) {
				int other = supElements.indexOf(it.next());
				if (out_sup[other] == core_len) {
					out_sup[other] = 0;
				}
			}

			core_sub[added_node_sub] = -1;
			core_sup[nodesup] = -1;

			core_len = orig_core_len;
			added_node_sub = -1;
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new VF2SubState4YAWL(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
