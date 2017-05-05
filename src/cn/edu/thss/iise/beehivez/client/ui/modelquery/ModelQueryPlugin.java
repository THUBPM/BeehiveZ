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
package cn.edu.thss.iise.beehivez.client.ui.modelquery;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.ui.FunctionFramePlugin;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
/**
 * 
 * @author Nianhua Wu
 * 
 *
 */
public class ModelQueryPlugin extends FunctionFramePlugin {

	private static final long serialVersionUID = 0L;
	public JTabbedPane tabpane = null;
	QBT qbkPanel = null;
	QBE qbePanel = null;
    QBTL qbtlPanel = null;
    QueryByTLL qbtllPanel = null;
	// QueryByRltMatrix qbrmPanel = null;
    ResourcesManager resourcesManager;

	public ModelQueryPlugin(ClientFrame mainframe) {
		super(mainframe);
		tabpane = new JTabbedPane();
		qbkPanel = new QBT();
		qbePanel = new QBE();
		qbtlPanel = new QBTL();
		qbtllPanel = new QueryByTLL();
		// qbrmPanel = new QueryByRltMatrix();
		resourcesManager = new ResourcesManager();
		tabpane.addTab(resourcesManager.getString("ModelQuery.Plugin.qbk.tabTitle"), qbkPanel);
		tabpane.addTab(resourcesManager.getString("ModelQuery.Plugin.qbe.tabTitle"), qbePanel);
		tabpane.addTab(resourcesManager.getString("ModelQuery.Plugin.qbt.tabTitle"), qbtlPanel);
		tabpane.addTab(resourcesManager.getString("ModelQuery.Plugin.qbtl.tabTitle"), qbtllPanel);
		// tabpane.addTab("query by relationship matrix", qbrmPanel);
		getModulePanel().add(tabpane, BorderLayout.CENTER);
		getModulePanel().setBackground(Color.black);
	}

	public void onLoad() {

	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		String path = "/resources/images/Icon_ModelQuery.gif";
		String description = "Icon_ModelQuery";
		return new ImageIcon(getClass().getResource(path), description);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return resourcesManager.getString("ModelQuery.Plugin.title");
	}

	@Override
	public String getToolTip() {
		// TODO Auto-generated method stub
		return resourcesManager.getString("ModelQuery.Plugin.title");
	}

}
