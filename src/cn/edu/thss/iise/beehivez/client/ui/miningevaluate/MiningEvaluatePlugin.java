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
package cn.edu.thss.iise.beehivez.client.ui.miningevaluate;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.ui.FunctionFramePlugin;
import cn.edu.thss.iise.beehivez.client.ui.indexmanagement.IndexManagementUI;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

public class MiningEvaluatePlugin extends FunctionFramePlugin{
	private static final long serialVersionUID = 1L;
	public MiningEvaluateUI ui = null;
	ResourcesManager resourcesManager = new ResourcesManager();
	public MiningEvaluatePlugin(ClientFrame mainframe) {
		super(mainframe);
		ui = new MiningEvaluateUI();
		getModulePanel().add(ui, BorderLayout.CENTER);
		getModulePanel().setBackground(Color.black);
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		String path = "/resources/images/Icon_MiningEvaluate.gif";
		String description = "Icon_IndexManagement";
		return new ImageIcon(getClass().getResource(path), description);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return resourcesManager.getString("MiningEvaluate.plugin");
	}

	@Override
	public String getToolTip() {
		// TODO Auto-generated method stub
		return resourcesManager.getString("MiningEvaluate.plugin");
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

}
