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
package cn.edu.thss.iise.beehivez.server.index.yawlindex.yawltasksluceneindex;

import java.io.StringReader;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import cn.edu.thss.iise.beehivez.server.index.luceneindex.analyzer.SemicolonTokenizer;

/**
 * get lucene document object for tasks in yawl models
 * 
 * @author Tao Jin
 * 
 */
public class YAWLTasksDocument {
	public static final String FIELDPROCESSID = "process_id";
	public static final String FIELDTASKS = "tasks";

	private YAWLTasksDocument() {
	}

	// make a lucene document from a Yawl model object
	public static Document Document(YNet net, long process_id) {
		Document doc = new Document();

		Field fProcessID = new Field(FIELDPROCESSID,
				String.valueOf(process_id), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);
		fProcessID.setOmitTermFreqAndPositions(true);
		doc.add(fProcessID);

		// get TARS from petri net and form a string with semicolon divided
		StringBuilder sb = new StringBuilder();

		HashSet<String> tasks = new HashSet<String>();
		for (YTask task : net.getNetTasks()) {
			String taskName = task.getName();
			if (tasks.add(taskName)) {
				sb.append(taskName.trim() + SemicolonTokenizer.delimiter);
			}
		}

		Field fTasks = new Field(FIELDTASKS, new StringReader(sb.toString()),
				Field.TermVector.YES);
		fTasks.setOmitNorms(true);
		fTasks.setOmitTermFreqAndPositions(true);
		doc.add(fTasks);

		return doc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
