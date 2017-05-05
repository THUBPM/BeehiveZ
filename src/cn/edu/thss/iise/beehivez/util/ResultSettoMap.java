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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * ���ã���ResultSetת����Map�������ڿͻ��������֮��Ĵ��䶼�Զ���ķ�ʽ���У�
 * ResultSet����Ĵ�С������
 * 
 * @author ���ڷ�
 * 
 */
public class ResultSettoMap {
	private HashMap resultMap = null;

	public ResultSettoMap() {
		resultMap = new HashMap();
	}

	public HashMap generateXML(ResultSet rs) {
		try {
			if (!rs.next()) {
				rs.close();
				return resultMap;
			} else {
				ResultSetMetaData rsmd = null;
				rsmd = (ResultSetMetaData) rs.getMetaData();
				int colCount = rsmd.getColumnCount();
				String[] colNames = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					colNames[i] = rsmd.getColumnName(i + 1);
				}
				int rowKey = 1;
				do {
					HashMap colMap = new HashMap();
					for (int i = 1; i <= colCount; i++) {
						int colTypeCode = rsmd.getColumnType(i);
						String value = getValue(rs, i, colTypeCode);
						colMap.put(colNames[i - 1], value);
					}
					resultMap.put(rowKey, colMap);
					rowKey++;
				} while (rs.next());
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
		return resultMap;
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
