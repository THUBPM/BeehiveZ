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
package cn.edu.thss.iise.beehivez.client.ui.modelio;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.processmining.importing.pnml.PnmlImport;
import org.processmining.importing.yawl.YAWLImport;
import org.processmining.mining.MiningResult;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.processmining.mining.yawlmining.YAWLResult;

import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.ResourcePetriNet;
import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.show.ResourcePetriNetResult;
import cn.edu.thss.iise.beehivez.client.util.ScreenUtil;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Nianhua Wu
 * 
 */
public class VisualFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private String modelType = null;
	private InputStream modelDefinition = null;
	private MiningResult modelResult = null;

	/**
	 * This is the default constructor
	 */
	public VisualFrame(String title, String type, InputStream definition) {
		super();
		this.modelType = type;
		this.modelDefinition = definition;
		this.setTitle(title);
		initialize();
		ScreenUtil.centerOnMainUI(this);
	}

	public VisualFrame(String type, InputStream definition) {
		this("Model View", type, definition);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 500);
		this.setContentPane(getJContentPane());
		// this.setTitle("Model View");

		this.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				dispose();
			}

		});

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			try {
				if (this.modelType.equalsIgnoreCase(ProcessObject.TYPEPNML)) {
					PnmlImport pnml = new PnmlImport();
					modelResult = (PetriNetResult) pnml
							.importFile(this.modelDefinition);
				} else if (this.modelType
						.equalsIgnoreCase(ProcessObject.TYPEYAWL)) {
					YAWLImport yi = new YAWLImport();
					modelResult = (YAWLResult) yi
							.importFile(this.modelDefinition);
				}
				jContentPane = (JPanel) modelResult.getVisualization();
				this.modelDefinition.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jContentPane;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		// System.out.println("VisualFrame dispose");
		// jContentPane.removeAll();
		if (modelResult instanceof PetriNetResult) {
			PetriNetResult pnResult = (PetriNetResult) modelResult;
			pnResult.getPetriNet().destroyPetriNet();
			pnResult.destroy();
		}

		if (modelResult instanceof YAWLResult) {
			YAWLResult yawlResult = (YAWLResult) modelResult;
			// TODO: maybe need to do in the future
		}
	}
	
	public VisualFrame(String typepnml, FileInputStream in,
			ResourcePetriNet processModel) {
		super();
		this.modelType = typepnml;
		this.modelDefinition = in;
		this.setTitle("Model View");
		initialize(processModel);
		ScreenUtil.centerOnMainUI(this);
	}

	private void initialize(ResourcePetriNet processModel) {		
		this.setSize(600, 500);
		this.setContentPane(getJContentPane(processModel));
		// this.setTitle("Model View");

		this.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				dispose();
			}

		});
	}

	private Container getJContentPane(ResourcePetriNet processModel) {
		if (jContentPane == null) {
			jContentPane = new JPanel();			
			jContentPane.setLayout(new BorderLayout());
			ResourcePetriNetResult rResult = null;
			try {
				if (this.modelType.equalsIgnoreCase(ProcessObject.TYPEPNML)) {
					rResult = new ResourcePetriNetResult(processModel);
				} 
				jContentPane = (JPanel) rResult.getVisualization();
				this.modelDefinition.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jContentPane;
	}
}
