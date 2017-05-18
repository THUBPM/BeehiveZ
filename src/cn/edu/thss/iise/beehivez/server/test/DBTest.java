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
package cn.edu.thss.iise.beehivez.server.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author JinTao
 * 
 *         test for db
 * 
 */
public class DBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String DBURL = "jdbc:derby:processrepository";
		String DBDRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(DBDRIVER).newInstance();
			Connection conn = DriverManager.getConnection(DBURL);
			conn.setAutoCommit(false);
			String sqlCreateTable = "create table test (a int, b int, c int, d int)";
			String sqlDropTable = "drop table test";
			Statement stmt = conn.createStatement();
			stmt.execute(sqlCreateTable);
			conn.commit();

			long start, end;
			PreparedStatement pstmt;

			// test for statement
			stmt.execute(sqlDropTable);
			stmt.execute(sqlCreateTable);
			conn.commit();
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				stmt.execute("insert into test values(" + i + "," + i + "," + i
						+ "," + i + ")");
				conn.commit();
			}
			end = System.currentTimeMillis();
			System.out.println("the time cost of statement is: "
					+ (end - start));

			// test for prepared statement
			stmt.execute(sqlDropTable);
			stmt.execute(sqlCreateTable);
			conn.commit();
			start = System.currentTimeMillis();
			pstmt = conn.prepareStatement("insert into test values (?,?,?,?)");
			for (int i = 0; i < 10000; i++) {
				pstmt.setInt(1, i);
				pstmt.setInt(2, i);
				pstmt.setInt(3, i);
				pstmt.setInt(4, i);
				pstmt.executeUpdate();
				conn.commit();
			}
			end = System.currentTimeMillis();
			System.out.println("the time cost of preparedstatement is: "
					+ (end - start));

			// test for prepared statement used like statement
			stmt.execute(sqlDropTable);
			stmt.execute(sqlCreateTable);
			conn.commit();
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				pstmt = conn
						.prepareStatement("insert into test values (?,?,?,?)");
				pstmt.setInt(1, i);
				pstmt.setInt(2, i);
				pstmt.setInt(3, i);
				pstmt.setInt(4, i);
				pstmt.executeUpdate();
				conn.commit();
				pstmt.close();
			}
			end = System.currentTimeMillis();
			System.out
					.println("the time cost of preparedstatement used like statement is: "
							+ (end - start));

			stmt.execute(sqlDropTable);
			conn.commit();
			conn.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
