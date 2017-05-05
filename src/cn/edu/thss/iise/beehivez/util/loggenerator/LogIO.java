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
package cn.edu.thss.iise.beehivez.util.loggenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriTransition;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class LogIO {
	public static final String EVENT_START = "start";
	public static final String EVENT_COMPLETE = "complete";
	public static final String EVENT_SCHEDULE = "schedule";
	public static final String EVENT_SUSPEND = "suspend";
	public static final String EVENT_RESUME = "resume";
	public static final String EVENT_PI_ABORT = "pi_abort";
	public static final String EVENT_ATE_ABORT = "ate_abort";
	public static final String EVENT_WITHDRAW = "withdraw";
	public static final String EVENT_ASSIGN = "assign";
	public static final String EVENT_REASSIGN = "reassign";
	public static final String EVENT_AUTOSKIP = "autoskip";
	public static final String EVENT_MANUALSKIP = "manualskip";

	private String fileName;
	private Document docJDOM;

	public LogIO() {
		fileName = "logs\\test.xml";
	}

	public LogIO(String fileName) {
		if (fileName != null)
			this.fileName = fileName;
		else
			this.fileName = "logs\\test.xml";
		docJDOM = null;
		createdoc();
	}

	public void createdoc() {

		Element root, e1, e2, e3;
		root = new Element("WorkflowLog");
		// root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		// root.setAttribute("xsi:noNamespaceSchemaLocation","WorkflowLog.xsd");
		root.setAttribute("description", "Test log for decision miner");
		docJDOM = new Document(root);

		// Source
		e1 = new Element("Source");
		e1.setAttribute("program", "LogGenerator, Tsinghua, ISE");
		e2 = new Element("Attribute");
		e2.setAttribute("name", "info");
		e2.setText("LogGenerator, Tsinghua, ISE");
		e3 = new Element("Data");
		e3.addContent(e2);
		e1.addContent(e3);
		root.addContent(e1);

		// Process
		e1 = new Element("Process");
		e1.setAttribute("id", "0");
		e1.setAttribute("description", "");
		root.addContent(e1);
	}

	public void addEventLog(String caseid, Transition t, String eventtype,
			String timestamp, String originator) {

		
		// ����ר��Ϊpetri��ģ������·�ɽڵ�
		if (t.isInvisibleTask())
			return;
		//
		Element e = new Element("AuditTrailEntry");
		Element e1 = new Element("Data");
		e.addContent(e1);

		e1 = new Element("WorkflowModelElement").setText(t.getIdentifier());
		e.addContent(e1);
		e1 = new Element("EventType").setText(eventtype);
		e.addContent(e1);
		e1 = new Element("Timestamp").setText(timestamp);
		e.addContent(e1);
		e1 = new Element("Originator").setText(originator);
		e.addContent(e1);

		// ����case��¼
		e1 = docJDOM.getRootElement().getChild("Process");

		List list = e1.getChildren("ProcessInstance");
		Element e2 = null;
		String id;
		for (int i = 0; i < list.size(); i++) {
			e1 = (Element) list.get(i);
			id = e1.getAttributeValue("id");
			if (id.equals(caseid)) {
				e2 = e1;
				break;
			}
		}

		if (e2 == null) { // ��һ����¼
			e2 = new Element("ProcessInstance");
			e2.setAttribute("id", caseid);
			e2.setAttribute("description", "");
			e1 = docJDOM.getRootElement().getChild("Process");
			e1.addContent(e2);
		}
		e2.addContent(e);
	}
	
	//wwx
	public void addEventLog(String caseid, MyPetriTransition t, String eventtype,
			String timestamp, String originator) {

		
		// ����ר��Ϊpetri��ģ������·�ɽڵ�
		if (t.getName().isEmpty())
			return;
		//
		Element e = new Element("AuditTrailEntry");
		Element e1 = new Element("Data");
		e.addContent(e1);

		e1 = new Element("WorkflowModelElement").setText(t.getName());
		e.addContent(e1);
		e1 = new Element("EventType").setText(eventtype);
		e.addContent(e1);
		e1 = new Element("Timestamp").setText(timestamp);
		e.addContent(e1);
		e1 = new Element("Originator").setText(originator);
		e.addContent(e1);

		// ����case��¼
		e1 = docJDOM.getRootElement().getChild("Process");

		List list = e1.getChildren("ProcessInstance");
		Element e2 = null;
		String id;
		for (int i = 0; i < list.size(); i++) {
			e1 = (Element) list.get(i);
			id = e1.getAttributeValue("id");
			if (id.equals(caseid)) {
				e2 = e1;
				break;
			}
		}

		if (e2 == null) { // ��һ����¼
			e2 = new Element("ProcessInstance");
			e2.setAttribute("id", caseid);
			e2.setAttribute("description", "");
			e1 = docJDOM.getRootElement().getChild("Process");
			e1.addContent(e2);
		}
		e2.addContent(e);
	}

	public void addEventLog(Element e) {

	}

	public void addCaseLog(Element e) {
	}

	public void store(String fileName) {
		if (fileName == null) {
			fileName = this.fileName;
		}
		
		File logFile = new File(fileName);
		if(!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 

		try {
			XMLOutputter XMLOut = new XMLOutputter();
			// XMLOut.setEncoding("gb2312");
			XMLOut.output(docJDOM, new FileOutputStream(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LogIO logio = new LogIO();
		logio.createdoc();
	}

	public void clear() {
		// TODO Auto-generated method stub
		if (docJDOM != null) {
			docJDOM = null;
		}
	}

	public void open() {
		// TODO Auto-generated method stub
		createdoc();
	}
}
