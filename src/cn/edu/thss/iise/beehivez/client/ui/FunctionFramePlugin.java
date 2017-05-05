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
package cn.edu.thss.iise.beehivez.client.ui;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * The super class of all function plugins
 * 
 * @version 1.0 09/02/25
 * @author Zha Haiping, He Tengfei
 * 
 *         edited by JinTao 2009.9.6
 */
public abstract class FunctionFramePlugin extends JFrame {

	private ClientFrame mainframe = null;
	private JPanel panel = null;

	public FunctionFramePlugin(ClientFrame mainframe) {
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		this.mainframe = mainframe;
	}

	// get the main frame
	protected ClientFrame getMainFrame() {
		return mainframe;
	}

	// auto perform after the function plugin loaded.
	// you can do some initialization work of the plugin here
	public abstract void onLoad();

	// get the pane of the plugin
	public JPanel getModulePanel() {
		return panel;
	}

	// abstract method must be implemented in function plugins

	// reutrn the descriptive name of the function plugin
	public abstract String getName();

	// return the descriptive icon of the function plugin
	public abstract Icon getIcon();

	// return the string as tooltip of the function plugin
	public abstract String getToolTip();
}
