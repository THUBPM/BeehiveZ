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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ��ResultSet���������д�뵽xml�ļ��У����ڴ���,��Ҫע�����
 * ���ResultSet��������һ����ֵ��xml���͵ģ��������ݷ���
 * xml�У���jdom����������?��Ϊ�Ѿ������xml����
 * 
 * @author ���ڷ�
 * 
 */
public class ResultSetoXML {
	public File generateXML(ResultSet rs) {
		StringBuffer buffer = new StringBuffer(1024 * 2);
		try {
			if (!rs.next()) {
				buffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
				buffer.append("<resultset>\n");
				buffer.append("</resultset>");
				rs.close();
			} else {
				buffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
				buffer.append("<resultset>\n");

				ResultSetMetaData rsmd = null;
				rsmd = (ResultSetMetaData) rs.getMetaData();
				int colCount = rsmd.getColumnCount();
				do {
					buffer.append("\t<row>\n");
					for (int i = 1; i <= colCount; i++) {
						int colTypeCode = rsmd.getColumnType(i);
						String colType = Integer.toString(colTypeCode);
						buffer.append("\t\t<col name=\""
								+ rsmd.getColumnName(i) + "\" type=\""
								+ colType + "\">");
						buffer.append(getValue(rs, i, colTypeCode));
						buffer.append("</col>\n");
					}
					buffer.append("\t</row>\n");
				} while (rs.next());
				buffer.append("</resultset>");
				rs.close();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				if (!rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return getFile(buffer.toString());
	}

	private File getFile(String fileInfo) {
		File file = null;
		String filePath = null;
		StringBuffer sb = new StringBuffer("stfile/resultset_");
		Long time = System.currentTimeMillis();
		sb.append(Long.valueOf(time));
		sb.append(".xml");
		filePath = sb.toString();
		file = new File(filePath);
		if (file.exists()) {
			try {
				file.delete();
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DataOutputStream writer;
		try {
			writer = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));
			writer.write(fileInfo.getBytes());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * �ͻ��˲�֧��sql��array��ref,��struct�������
	 * 
	 * @param rs
	 * @param i
	 * @param colType
	 * @return
	 */
	public String getValue(ResultSet rs, int i, int colType) {
		String defaultValue = null;
		switch (colType) {
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
			try {
				return rs.getString(i);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			try {
				byte[] value = rs.getBytes(i);
				return new String(value);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case java.sql.Types.BLOB:
			try {
				Blob value = rs.getBlob(i);
				int length = (int) value.length();
				byte[] bytes = value.getBytes(1, length);
				return new String(bytes);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case java.sql.Types.CLOB:
			try {
				Clob value = rs.getClob(i);
				long length = value.length();
				String strValue = value.getSubString(1, (int) length);
				return strValue;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			try {
				Object value = rs.getObject(i);
				if (value != null) {
					return value.toString();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
}
