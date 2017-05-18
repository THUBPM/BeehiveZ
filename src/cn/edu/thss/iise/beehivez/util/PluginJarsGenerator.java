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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * ����resultset_xxx.xml��pluginsĿ¼�����jar�ļ�
 * ��ɵ�jar�ļ������־���jar�ļ�����������
 * 
 * @author ���ڷ�
 * 
 */
public class PluginJarsGenerator {

	public static void generatorPluginJars(String filePath) {
		File file = new File(filePath);
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = null;
		Element root = null;
		List rowElements = null;
		Element row = null;
		List colElements = null;
		Element col, mainClassCol, jarFileCol = null;
		try {
			doc = builder.build(file);
			root = doc.getRootElement();
			root = doc.getRootElement();
			rowElements = root.getChildren("row");
			if (rowElements.size() <= 0)
				return;
			Iterator outIterator = rowElements.iterator();
			while (outIterator.hasNext()) {
				row = (Element) outIterator.next();
				colElements = row.getChildren("col");
				// ����3��7�ֱ��ʾmainclassname��jarfile �������ֶ���
				// ��ݿ������ڵ�λ��
				String mainClass = null;
				String jarFile = null;
				for (int i = 0; i < colElements.size(); i++) {
					Element tmp = (Element) colElements.get(i);
					String attrValue = tmp.getAttributeValue("name").trim()
							.toLowerCase();
					if (attrValue.equals("mainclassname")) {
						mainClass = tmp.getText();
					} else if (attrValue.equals("jarfile")) {
						jarFile = tmp.getText();
					}
				}
				createJarFile(mainClass, jarFile);
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createJarFile(String mainClass, String jarFile) {
		// TODO Auto-generated method stub
		File file = new File("plugins/" + mainClass + ".jar");
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
				DataOutputStream writer = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(file)));
				writer.write(jarFile.getBytes());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
