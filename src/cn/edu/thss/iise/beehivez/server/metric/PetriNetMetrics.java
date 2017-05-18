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
package cn.edu.thss.iise.beehivez.server.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PNNode;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Transition;

import att.grappa.Edge;
import att.grappa.Node;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.util.IntDataAnalyzer;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

/**
 * obtain the metrics of given Petri net
 * 
 * @author Tao Jin ,TSJ
 * 
 */
public class PetriNetMetrics {
	private static int maxlength = 0;
	private PetriNet pn = null;

	public PetriNetMetrics(PetriNet pn) {
		this.pn = pn;
	}

	public int getNumberOfPlaces() {
		return pn.numberOfPlaces();
	}

	public int getNumberOfTransitions() {
		return pn.numberOfTransitions();
	}

	public int getNumberOfArcs() {
		return pn.getNumberOfEdges();
	}

	public int getNumberOfANDJoin() {
		int result = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1) {
				result++;
			}
		}

		return result;
	}

	public int getNumberOfANDSplit() {
		int result = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfXORJoin() {
		int result = 0;
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1) {
				result++;
			}
		}

		return result;
	}

	public int getNumberOfXORSplit() {
		int result = 0;
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				result++;
			}
		}

		return result;
	}
	
	public float getStructuredness(){
		float result = 0.0f;
		MyPetriNet fromedPetriNet = MyPetriNet.PromPN2MyPN(pn);
		int pNumOfFromedPN = fromedPetriNet.getPlaceSet().size();
		int tNumOfFromedPN = fromedPetriNet.getTransitionSet().size();
		PetriNetReduction PNR = new PetriNetReduction();
		MyPetriNet reducedPetriNet = PNR.reduce(fromedPetriNet, 0);
		int pNumOfRedecedPN = reducedPetriNet.getPlaceSet().size();
		int tNumOfRedecedPN = reducedPetriNet.getTransitionSet().size();
		if((pNumOfRedecedPN+tNumOfRedecedPN)==1){
			result = 1;
		}else
		result = 1 - (float)(pNumOfRedecedPN+tNumOfRedecedPN)/(pNumOfFromedPN+tNumOfFromedPN);		
		return result;
	}
	
	public int getMismatch(){
		int result = 0;
		int andjoin = 0;
		int andsplit = 0;
		int xorjoin = 0;
		int xorsplit = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1) {
				andjoin++;
			}
		}
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				andsplit++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1) {
				xorjoin++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				xorsplit++;
			}
		}
		result = Math.abs(andjoin-andsplit)+Math.abs(xorjoin-xorsplit);
		
		return result;
	}
	
	public float getSequentiality(){
		float result = 0.0f;
		int nA = pn.getNumberOfEdges();
		int andjoin = 0;
		int andsplit = 0;
		int xorjoin = 0;
		int xorsplit = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1) {
				andjoin++;
			}
		}
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				andsplit++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1) {
				xorjoin++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				xorsplit++;
			}
		}
		result = (float)(nA - andjoin - andsplit - xorjoin - xorsplit)/nA;
		return result;
	}
	public float getAverDegree(){
		float result = 0.0f;
		int nA = pn.getNumberOfEdges();
		int andjoinnum = 0;
		int andsplitnum = 0;
		int xorjoinnum = 0;
		int xorsplitnum = 0;
		int andjoindegree = 0;
		int andsplitdegree = 0;
		int xorjoindegree = 0;
		int xorsplitdegree = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1) {
				andjoinnum++;
				andjoindegree+=t.inDegree();
			}
		}
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				andsplitnum++;
				andsplitdegree+=t.outDegree();
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1) {
				xorjoinnum++;
				xorjoindegree+=p.inDegree();
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				xorsplitnum++;
				xorsplitdegree+=p.outDegree();
			}
		}
		int totalDegree = andjoindegree + andsplitdegree + xorjoindegree + xorsplitdegree;
		int numOfConnetor = andjoinnum+andsplitnum+xorjoinnum+xorsplitnum;
		if(numOfConnetor == 0){
			result = 0;
		}else{
			result = (float)(totalDegree)/numOfConnetor;
		}		
		return result;
	}
	public int getMaxDegree(){
		int maxDegree = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1&&t.inDegree()>maxDegree) {
				maxDegree = t.inDegree();
			}
		}
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1&&t.outDegree()>maxDegree) {
				maxDegree = t.outDegree();
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1&&p.inDegree()>maxDegree) {
				maxDegree = p.inDegree();
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1&&p.outDegree()>maxDegree) {
				maxDegree = p.outDegree();
			}
		}
		return maxDegree;
	}
			
	
	public int getTS(){
		int result = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				result += t.outDegree()-1;
			}
		}
		return result;
	}
	
	public int getCFC(){
		int result = 0;
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				result++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				result+=p.outDegree();
			}
		}
		return result;
	}
	
	public double getCH(){
		double result = 0.0;
		int nXor = 0;
		int nAnd = 0;
		double nXorP = 0.0;
		double nAndP = 0.0;
		for (Transition t : pn.getTransitions()) {
			if (t.inDegree() > 1) {
				nAnd++;
			}
		}
		for (Transition t : pn.getTransitions()) {
			if (t.outDegree() > 1) {
				nAnd++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.inDegree() > 1) {
				nXor++;
			}
		}
		for (Place p : pn.getPlaces()) {
			if (p.outDegree() > 1) {
				nXor++;
			}
		}
		if((nXor+nAnd)==0){
			nXorP = 0;
			nAndP = 0;
		}else{
			nXorP = (double)nXor/(nXor+nAnd);
			nAndP = (double)nAnd/(nXor+nAnd);
		}	
		if(!(nXorP==0.0)&&!(nAndP==0.0))
		result = -(Math.log(nXorP)/Math.log(2))*nXorP - (Math.log(nAndP)/Math.log(2))*nAndP;
		if((nXorP==0.0)&&!(nAndP==0.0))
		result =  - (Math.log(nAndP)/Math.log(2))*nAndP;
		if(!(nXorP==0.0)&&(nAndP==0.0))
		result = -(Math.log(nXorP)/Math.log(2))*nXorP ;
		if((nXorP==0.0)&&(nAndP==0.0))
		result = 0.0;
		return result;
	}
	
	public float getCYC(){
		float result =0.0f;
		int nP = pn.numberOfPlaces();
		int nT = pn.numberOfTransitions();
		int t = pn.getNumOfCycle();
		result = (float)t/(nP+nT);
		return result;
	}
	//depth
	private boolean isSplit(PNNode node){
		boolean isSplit = false;
		if(node.outDegree()>1) isSplit = true;
		return isSplit;
	}
	private boolean isJoin(PNNode node){
		boolean isJoin = false;
		if(node.inDegree()>1) isJoin = true;
		return isJoin;
	}
	private boolean outisSplit(PNNode node){
		boolean isSplit = false;
		if(node.inDegree()>1) isSplit = true;
		return isSplit;
	}
	private boolean outisJoin(PNNode node){
		boolean isJoin = false;
		if(node.outDegree()>1) isJoin = true;
		return isJoin;
	}
	public int getDepth(){
		int depth = 0;
		ArrayList<Integer> depthVec = new ArrayList<Integer>();
		HashMap<Long,Integer> indepth = new HashMap<Long,Integer>();
		HashMap<Long,Integer> outdepth = new HashMap<Long,Integer>();
		for(PNNode node : pn.getNodes()){
			indepth.put(node.getIdKey(), 0);
			outdepth.put(node.getIdKey(), 0);
		}			
		PNNode pstart = (PNNode)pn.getSource();
	    PNNode pend = (PNNode)pn.getSink();
		ArrayList<PNNode> invisitedNodes = new ArrayList<PNNode>();
		ArrayList<PNNode> outvisitedNodes = new ArrayList<PNNode>();
		
		indepthFun(null,pstart,pend,invisitedNodes,indepth);
		outdepthFun(null,pend,pstart,outvisitedNodes,outdepth);

		for(PNNode node : pn.getNodes()){
			int depthOfNode = 0;
			System.out.print(node.getIdentifier()+" in:");
			int indepthOfNode = indepth.get(node.getIdKey());
			System.out.print(indepthOfNode+",");
			int outdepthOfNode = outdepth.get(node.getIdKey());
			System.out.print(" out:");
			System.out.println(outdepthOfNode);
			if(indepthOfNode == outdepthOfNode){
				depthOfNode = indepthOfNode;
			} else{
				depthOfNode = indepthOfNode>outdepthOfNode?outdepthOfNode:indepthOfNode;
			}
			depthVec.add(depthOfNode);
		}
		System.out.println("depth over!!!");
		Collections.sort(depthVec);
		depth = depthVec.get(depthVec.size()-1);
		return depth;
	}
	public void indepthFun(PNNode preNode,PNNode curNode,PNNode pend,ArrayList<PNNode> visited, HashMap<Long,Integer> indepth){
		visited.add(curNode);
		if(preNode != null)
		{
			int pre = indepth.get(preNode.getIdKey());
			int cur = indepth.get(curNode.getIdKey());

			if(isSplit(preNode)&&!isJoin(curNode))
			{					
				if(pre+1 > cur)
					indepth.put(curNode.getIdKey(), pre+1);
			}
			if(isSplit(preNode)&&isJoin(curNode) || !isSplit(preNode)&&!isJoin(curNode))
			{
				if(pre > cur)
					indepth.put(curNode.getIdKey(), pre);
			}
			if(!isSplit(preNode)&&isJoin(curNode))
			{					
				if(pre - 1 > cur)
					indepth.put(curNode.getIdKey(), pre - 1);
			}
		}
		if(curNode == pend)
		{
			visited.remove(curNode);
			return;
		}
		HashSet<ModelGraphVertex> sucNodes = curNode.getSuccessors();
		for(ModelGraphVertex node : sucNodes)
		{
			if(!visited.contains(node))
				indepthFun(curNode,(PNNode)node,pend,visited,indepth);
		}
		
		visited.remove(curNode);
	}

	public void outdepthFun(PNNode preNode,PNNode curNode,PNNode pstart,ArrayList<PNNode> visited, HashMap<Long,Integer> outdepth){
		visited.add(curNode);
		if(preNode != null)
		{
			int pre = outdepth.get(preNode.getIdKey());
			int cur = outdepth.get(curNode.getIdKey());

			if(outisSplit(preNode)&&!outisJoin(curNode))
			{					
				if(pre+1 > cur)
					outdepth.put(curNode.getIdKey(), pre+1);
			}
			if(outisSplit(preNode)&&outisJoin(curNode) || !outisSplit(preNode)&&!outisJoin(curNode))
			{
				if(pre > cur)
					outdepth.put(curNode.getIdKey(), pre);
			}
			if(!outisSplit(preNode)&&outisJoin(curNode))
			{					
				if(pre - 1 > cur)
					outdepth.put(curNode.getIdKey(), pre - 1);
			}
		}
		if(curNode == pstart)
		{
			visited.remove(curNode);
			return;
		}
		HashSet<ModelGraphVertex> preNodes = curNode.getPredecessors();
		for(ModelGraphVertex node : preNodes)
		{
			if(!visited.contains(node))
				outdepthFun(curNode,(PNNode)node,pstart,visited,outdepth);
		}
		
		visited.remove(curNode);
	}
	
	//Diam
	public int getDiam(){	    
	    PNNode pstart = (PNNode)pn.getSource();
	    PNNode pend = (PNNode)pn.getSink();
	    ArrayList<PNNode> visited = new ArrayList<PNNode>();
	    maxlength = 0;
	    length(pstart,pend,visited);
		return maxlength;
	}
	public void length(PNNode pstart,PNNode pend,ArrayList<PNNode> visited){
		visited.add(pstart);
		if(pstart == pend)
		{
			if((visited.size()-1)>maxlength) 
				maxlength = visited.size()-1;

			visited.remove(pstart);
			return;
		}
		HashSet<ModelGraphVertex> sucNodes = pstart.getSuccessors();
		for(ModelGraphVertex node : sucNodes)
		{
			if(!visited.contains(node))
				length((PNNode)node,pend,visited);
		}
		
		visited.remove(pstart);
	}

	
	public float getSeparability(){
		float result = 0.0f;
		int nP = pn.numberOfPlaces();
		int nT = pn.numberOfTransitions();
		ArrayList<PNNode> cutNodes = new ArrayList<PNNode>();
		Queue<PNNode> queue = new LinkedList<PNNode>();
		List<PNNode> visitedNodes = new ArrayList<PNNode>();
		for(PNNode node : pn.getNodes()){
			visitedNodes= new ArrayList<PNNode>();
			if(!visitedNodes.contains(node)) visitedNodes.add(node);
			HashSet<ModelGraphVertex> sucNodes = node.getSuccessors();
			Iterator<ModelGraphVertex> sucit = sucNodes.iterator();			
			HashSet<ModelGraphVertex> preNodes = node.getPredecessors();
			Iterator<ModelGraphVertex> preit = preNodes.iterator();
			if(preNodes.isEmpty()){
				if(!sucNodes.isEmpty())
				queue.add((PNNode)sucit.next());
			}else 
			queue.add((PNNode)preit.next());			
			
			while(!queue.isEmpty()){
				PNNode x = queue.remove();
				if(!visitedNodes.contains(x)) visitedNodes.add(x);
				List<Edge> outgoingArcs = x.getOutEdges();
				if (outgoingArcs == null)
					outgoingArcs = new LinkedList<Edge>();
				for (Edge edge : outgoingArcs){
					PNNode opposite = (PNNode) edge.getHead();
					if(!visitedNodes.contains(opposite)&&!queue.contains(opposite)){
						queue.add(opposite);
					}
				}
				List<Edge> incomingArcs = x.getInEdges();
				if (incomingArcs == null)
					incomingArcs = new LinkedList<Edge>();
				for (Edge edge : incomingArcs){
					PNNode opposite = (PNNode) edge.getTail();
					if(!visitedNodes.contains(opposite)&&!queue.contains(opposite)){
						queue.add(opposite);
					}
				}
			}
			if(visitedNodes.size()<pn.getNodes().size()) cutNodes.add(node);
		}
		result = (float)cutNodes.size()/(nP+nT-2);
		return result;
	}

	
	public float getDensity() {
		int nP = pn.numberOfPlaces();
		int nT = pn.numberOfTransitions();
		int nA = pn.getNumberOfEdges();
		float numerator = nA;
		float denominator = nP * nT + nT * nP;
		float result = numerator / denominator;
		return result;
	}
	public float getCNC() {
		int nP = pn.numberOfPlaces();
		int nT = pn.numberOfTransitions();
		int nA = pn.getNumberOfEdges();
		float numerator = nA;
		float denominator = nP + nT ;
		float result = numerator / denominator;
		return result;
	}

	public int getMaxInDegree() {
		int result = 0;
		for (PNNode node : pn.getNodes()) {
			int temp = node.inDegree();
			if (temp > result) {
				result = temp;
			}
		}
		return result;
	}

	public int getMaxOutDegree() {
		int result = 0;
		for (PNNode node : pn.getNodes()) {
			int temp = node.outDegree();
			if (temp > result) {
				result = temp;
			}
		}
		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeANDSplitDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (Transition t : pn.getTransitions()) {
			int degree = t.outDegree();
			if (degree > 1) {
				vDegree.add(degree);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeANDJoinDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (Transition t : pn.getTransitions()) {
			int degree = t.inDegree();
			if (degree > 1) {
				vDegree.add(degree);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeXORSplitDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (Place p : pn.getPlaces()) {
			int degree = p.outDegree();
			if (degree > 1) {
				vDegree.add(degree);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return cout, min degree, max degree, average degree, stdev degree
	 */
	public float[] analyzeXORJoinDegree() {
		// count, min degree, max degree, average degree, stdev degree
		float[] result = new float[5];

		Vector<Integer> vDegree = new Vector<Integer>();
		for (Place p : pn.getPlaces()) {
			int degree = p.inDegree();
			if (degree > 1) {
				vDegree.add(degree);
			}
		}

		if (vDegree.size() > 0) {
			int[] data = new int[vDegree.size()];
			int i = 0;
			Iterator<Integer> it = vDegree.iterator();
			while (it.hasNext()) {
				data[i] = it.next().intValue();
				i++;
			}

			try {
				result[0] = IntDataAnalyzer.getCount(data);
				result[1] = IntDataAnalyzer.getMin(data);
				result[2] = IntDataAnalyzer.getMax(data);
				result[3] = IntDataAnalyzer.getAverage(data);
				result[4] = IntDataAnalyzer.getStdev(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return number of state, number of arc
	 */
	public int[] analyzeStateSpace() {
		// number of state, number of arc
		int[] result = new int[2];
		StateSpace ss = PetriNetUtil.buildCoverabilityGraph(pn);
		result[0] = ss.getVerticeList().size();
		result[1] = ss.getNumberOfEdges();
		ss.destroyStateSpace();
		return result;
	}

	public int getNumberOfTARs() {
		return PetriNetUtil.getTARSFromPetriNetByCFP(pn).size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
