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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.processmining.analysis.causality.Similarity;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.framework.ui.Progress;

import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;
import cn.edu.thss.iise.beehivez.server.metric.causality.CausalFootPrintSimilarityResult;
/**
 *Causal Footprint 
 * @author Nianhua Wu
 *
 */
		
public class CausalFootprintSimilarity extends PetriNetSimilarity{

	@Override
	public String getDesription() {
		// TODO Auto-generated method stub
		return "similarity base on two petrinet's  causal footprint similarity";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Causal Footprint similarity";
	}

	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		// TODO Auto-generated method stub
		CausalFootPrintSimilarityResult result = new CausalFootPrintSimilarityResult(pn1, pn2);
		Similarity d = result.calculateSimilarity(new Progress("Calculating Similarity",0,100), true);
		result.c1.clearGraph();
		result.c2.clearGraph();
		result.c1.delete();
		result.c2.delete();
		result = null;
		return (float) d.similarity;
	}
}
