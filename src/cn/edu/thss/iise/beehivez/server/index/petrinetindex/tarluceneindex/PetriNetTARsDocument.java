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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonTokenizer;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.TransitionLabelPair;

/**
 * get lucene document object from task adjacent relations
 * 
 * @author Tao Jin
 */
public class PetriNetTARsDocument {
	public static final String FIELDPROCESSID = "process_id";
	public static final String FIELDTARS = "TARS";
	public static final String TARCONNECTOR = " > ";

	private PetriNetTARsDocument() {

	}

	// make a lucene document from a petri net object
	public static Document Document(PetriNet pn, long process_id) {
		Document doc = new Document();

		Field fProcessID = new Field(FIELDPROCESSID,
				String.valueOf(process_id), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);
		fProcessID.setOmitTermFreqAndPositions(true);
		doc.add(fProcessID);

		// get TARS from petri net and form a string with semicolon divided
		StringBuilder sb = new StringBuilder();

		HashSet<TransitionLabelPair> tars = PetriNetUtil
				.getTARSFromPetriNetByCFP(pn);
		Iterator<TransitionLabelPair> it = tars.iterator();
		while (it.hasNext()) {
			TransitionLabelPair tlp = it.next();
			sb.append(tlp.getFirst().trim() + TARCONNECTOR
					+ tlp.getSecond().trim() + SemicolonTokenizer.delimiter);
		}

		Field fTARs = new Field(FIELDTARS, new StringReader(sb.toString()),
				Field.TermVector.YES);
		fTARs.setOmitNorms(true);
		fTARs.setOmitTermFreqAndPositions(true);
		doc.add(fTARs);

		return doc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PetriNet pn = PetriNetUtil.getPetriNetFromPnmlFile("e:/test.pnml");
		Document doc = PetriNetTARsDocument.Document(pn, 0);
		System.out.println("petri net");
		System.out.println("the number of transitions: "
				+ pn.getTransitions().size());
		Iterator it = pn.getTransitions().iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("the number of places: " + pn.getPlaces().size());
		System.out.println("the source place is: " + pn.getSource().toString());
		System.out.println("the sink place is: " + pn.getSink().toString());
	}

}
