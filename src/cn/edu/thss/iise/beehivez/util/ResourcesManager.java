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

package cn.edu.thss.iise.beehivez.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.*;
import java.io.*;

/**
 * provide the operation on resources file
 * 
 * @author He Tengfei, zhp
 * 
 */
public class ResourcesManager {

	private ResourceBundle bundle = null;
	Locale currentlocale = null;
	public final static String LANGUAGE_FRAME_PLUGIN_CONFIG = "user.conf";

	public ResourcesManager() {
		currentlocale = this.getCurrentLocaleByConfig();
		// currentlocale=Locale.ENGLISH.US;
		bundle = getResourceBundle(currentlocale);
	}

	/**
	 * Returns the resource bundle associated with this module. Used to get
	 * accessable and internationalized strings.
	 */
	public ResourceBundle getResourceBundle(Locale currentlocale) {
		return ResourceBundle.getBundle("resources.processmanager",
				currentlocale);
	}

	public void setLocale(Locale locale) {
		this.currentlocale = locale;
		bundle = getResourceBundle(currentlocale);
		setCurrentLocaleToConfig();
	}

	/**
	 * get a string from a bundle resource
	 */

	public String getString(String key) {
		String value = null;
		try {
			value = bundle.getString(key.trim());
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		if (value == null) {
			value = "Could not find resource: " + key + "  ";
		}
		return value;
	}

	public Locale getCurrentLocaleByConfig() {
		try {
			Properties ini = new Properties();

			String filename = System.getProperty("user.dir", "")
					+ System.getProperty("file.separator")
					+ LANGUAGE_FRAME_PLUGIN_CONFIG;

			FileInputStream is = new FileInputStream(filename);
			ini.load(is);
			is.close();

			if (ini.getProperty("language").equals("English"))
				currentlocale = Locale.US;
			else
				currentlocale = Locale.CHINESE;

		} catch (Exception e) {
			e.getStackTrace();
		}
		return currentlocale;
	}
	
	public void setCurrentLocaleToConfig()
	{
		try {
			Properties ini = new Properties();

			String filename = System.getProperty("user.dir", "")
					+ System.getProperty("file.separator")
					+ LANGUAGE_FRAME_PLUGIN_CONFIG;

			FileInputStream is = new FileInputStream(filename);
			ini.load(is);
			is.close();
			if (currentlocale == Locale.US)
				ini.setProperty("language", "English");
			else
				ini.setProperty("language", "Chinese");			
			FileOutputStream os = new FileOutputStream(filename);
			ini.store(os, "ok");
			os.close();
		} catch (Exception e) {
			e.getStackTrace();
		}		
	}

	/**
	 * Returns a mnemonic from the resource bundle. Typically used as keyboard
	 * shortcuts in menu items.
	 */
	public char getMnemonic(String key) {
		return (getString(key.trim())).charAt(0);
	}

}
