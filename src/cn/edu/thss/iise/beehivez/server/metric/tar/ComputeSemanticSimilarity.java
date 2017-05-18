/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: wangwenxingbuaa@gmail.com 
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
package cn.edu.thss.iise.beehivez.server.metric.tar;

import java.util.HashMap;
import java.util.HashSet;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.index.petrinetindex.semanticSimilarity.SemanticSimilarity;
import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;

/**
 * @author Wenxing Wang
 * 
 * @date Mar 27, 2011
 * 
 */
public class ComputeSemanticSimilarity {
	private ExtensiveTAR _tar1 = null;
	private ExtensiveTAR _tar2 = null;
	
	private double _globalSimilarity = 0;
	private double _localSimilarity = 0;
	private double _coefficient = 0;
	private double threshold = 0.8;
	
	public ComputeSemanticSimilarity(ExtensiveTAR tar1, ExtensiveTAR tar2) {
		super();
		_tar1 = tar1;
		_tar2 = tar2;
	}
	
	public ComputeSemanticSimilarity(PetriNet pn1, PetriNet pn2) {
		super();
		_tar1 = new ExtensiveTAR(pn1);
		_tar2 = new ExtensiveTAR(pn2);
	}
	
	public double compute(){
		int same = 0;
		int total = 0;
		HashMap<String, HashSet<String>> sameSet = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> totalSet = new HashMap<String, HashSet<String>>();
		
		for(String label1 : _tar1._tar.keySet()){
			total += _tar1._tar.get(label1).size();
//			SemanticSimilarity ss = new SemanticSimilarity();
			for(String label2 : _tar2._tar.keySet()){
				double similarity = StringSimilarityUtil.semanticSimilarity(label1, label2);
				if(similarity >= threshold){
					for(String label11 : _tar1._tar.get(label1)){
						for(String label22 : _tar2._tar.get(label2)){
							similarity = StringSimilarityUtil.semanticSimilarity(label11, label22);
							if(similarity >= threshold){
								++same;
							}
							else{
								++total;
							}
						}
					}
				}
				else{
					total += _tar2._tar.get(label2).size();
				}
			}
		}
		
		for(String label1 : _tar1._tar0.keySet()){
			total += _tar1._tar0.get(label1).size();
//			SemanticSimilarity ss = new SemanticSimilarity();
			for(String label2 : _tar2._tar0.keySet()){
				double similarity = StringSimilarityUtil.semanticSimilarity(label1, label2);
				if(similarity >= threshold){
					for(String label11 : _tar1._tar0.get(label1)){
						for(String label22 : _tar2._tar0.get(label2)){
							similarity = StringSimilarityUtil.semanticSimilarity(label11, label22);
							if(similarity >= threshold){
								++same;
							}
							else{
								++total;
							}
						}
					}
				}
				else{
					total += _tar2._tar0.get(label2).size();
				}
			}
		}
		
		for(String label1 : _tar1._tarRe.keySet()){
			total += _tar1._tarRe.get(label1).size();
//			SemanticSimilarity ss = new SemanticSimilarity();
			for(String label2 : _tar2._tarRe.keySet()){
				double similarity = StringSimilarityUtil.semanticSimilarity(label1, label2);
				if(similarity >= threshold){
					for(String label11 : _tar1._tarRe.get(label1)){
						for(String label22 : _tar2._tarRe.get(label2)){
							similarity = StringSimilarityUtil.semanticSimilarity(label11, label22);
							if(similarity >= threshold){
								++same;
							}
							else{
								++total;
							}
						}
					}
				}
				else{
					total += _tar2._tarRe.get(label2).size();
				}
			}
		}
		
		for(String label1 : _tar1._tarIm.keySet()){
			total += _tar1._tarIm.get(label1).size();
//			SemanticSimilarity ss = new SemanticSimilarity();
			for(String label2 : _tar2._tarIm.keySet()){
				double similarity = StringSimilarityUtil.semanticSimilarity(label1, label2);
				if(similarity >= threshold){
					for(String label11 : _tar1._tarIm.get(label1)){
						for(String label22 : _tar2._tarIm.get(label2)){
							similarity = StringSimilarityUtil.semanticSimilarity(label11, label22);
							if(similarity >= threshold){
								++same;
							}
							else{
								++total;
							}
						}
					}
				}
				else{
					total += _tar2._tarIm.get(label2).size();
				}
			}
		}
		
		_globalSimilarity = (double)same / total;
		
		return _globalSimilarity;
	}
}
