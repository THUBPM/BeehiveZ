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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.taskedgeindex;

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonTokenizer;
import cn.edu.thss.iise.beehivez.server.metric.mcessimilarity.TaskLine4PetriNet;

/**
 * get lucene document object from task edges of Petri net
 * 
 * @author Tao Jin
 * 
 * @date 2011-3-20
 * 
 */
public class PetriNetTaskEdgesDocument {

	public static final String FIELDPROCESSID = "process_id";
	public static final String FIELDTASKEDGES = "TASKEDGES";
	public static final String TASKEDGECONNECTOR = " > ";

	public PetriNetTaskEdgesDocument() {

	}

	// make a lucene document from a petri net object
	public static Document Document(PetriNet pn, long process_id) {
		Document doc = new Document();

		Field fProcessID = new Field(FIELDPROCESSID,
				String.valueOf(process_id), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);
		fProcessID.setOmitTermFreqAndPositions(true);
		doc.add(fProcessID);

		// get Task edges from petri net and form a string with semicolon
		// divided
		StringBuilder sb = new StringBuilder();

		ArrayList<TaskLine4PetriNet> tls = TaskLine4PetriNet
				.getAllTaskLinesOfPetriNet(pn);
		for (TaskLine4PetriNet tl : tls) {
			sb.append(tl.getSrcTransition().getIdentifier().trim()
					+ TASKEDGECONNECTOR
					+ tl.getDestTransition().getIdentifier().trim()
					+ SemicolonTokenizer.delimiter);
		}
		Field fTaskEdges = new Field(FIELDTASKEDGES, new StringReader(sb
				.toString()), Field.TermVector.YES);
		fTaskEdges.setOmitNorms(true);
		fTaskEdges.setOmitTermFreqAndPositions(true);
		doc.add(fTaskEdges);

		return doc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
