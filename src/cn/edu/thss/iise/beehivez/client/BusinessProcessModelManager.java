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
package cn.edu.thss.iise.beehivez.client;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.ui.ImageSplash;

/**
 * @author Tao Jin 2009.9.6
 * 
 */
public class BusinessProcessModelManager {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClientFrame instance = ClientFrame.getInstance();
		ImageSplash splash = new ImageSplash();
		splash.changeText("Loading......");
		for (int i = 0; i <= 100; i += 10) {
			try {
				Thread.sleep(50);
				splash.setProgress(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		splash.close();
		instance.start();
	}

}
