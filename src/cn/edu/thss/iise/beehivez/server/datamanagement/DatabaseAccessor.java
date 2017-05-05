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
package cn.edu.thss.iise.beehivez.server.datamanagement;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.processmining.converting.yawl2pn.YawlToPetriNetSettings;
import org.processmining.converting.yawl2yawl.YawlToYawl;
import org.processmining.converting.yawl2yawl.YawlToYawlSettings;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.yawl.YAWLModel;
import org.processmining.framework.models.yawl.algorithms.YAWLReader;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.IndexinfoObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.OplogObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.BasicRltMatrix;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.RltMatrix;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
import cn.edu.thss.iise.beehivez.server.util.ToolKit;
import cn.edu.thss.iise.beehivez.server.util.YAWLUtil;

/**
 * used for operation on derby database
 * 
 * @author Tao Jin 2009.9.4
 * 
 */
public class DatabaseAccessor {

	private final String keyDBName = "dbname";
	private final String keyDBDriver = "dbdriver";
	private final String keyDBURL = "dburl";
	private final String keyUser = "username";
	private final String keyPassword = "password";
	private final String keyRowsLimit = "rowslimit";
	private final String keyFetchSize = "fetchsize";
	private final String keyPageSize = "pagesize";

	private String dbName = "derby";
	private String dbUrl = "jdbc:derby:processrepository";
	private String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String userName = "";
	private String password = "";
	private String rowsLimit = "200";
	private String fetchSize = "100";
	private String pageSize = "10000";

	private Connection conn = null;
	private int nRowsLimit = 200;
	private int nFetchSize = 100;
	private int nPageSize = 10000;

	private static DatabaseAccessor daInstance = new DatabaseAccessor();

	public static DatabaseAccessor getInstance() {
		return daInstance;
	}

	private DatabaseAccessor() {
		Properties ini = getDBConfiguration();
		if (ini == null) {
			System.exit(-1);
		}
		try {
			dbName = ini.getProperty(keyDBName, dbName);
			dbDriver = ini.getProperty(keyDBDriver, dbDriver);
			dbUrl = ini.getProperty(keyDBURL, dbUrl);
			userName = ini.getProperty(keyUser, userName);
			password = ini.getProperty(keyPassword, password);
			rowsLimit = ini.getProperty(keyRowsLimit, rowsLimit);
			nRowsLimit = Integer.parseInt(rowsLimit);
			fetchSize = ini.getProperty(keyFetchSize, fetchSize);
			nFetchSize = Integer.parseInt(fetchSize);
			pageSize = ini.getProperty(keyPageSize, pageSize);
			nPageSize = Integer.parseInt(pageSize);

			Class.forName(dbDriver).newInstance();
			conn = DriverManager.getConnection(dbUrl, userName, password);
			conn.setAutoCommit(false);

			// get the user home path
			int colon = dbUrl.lastIndexOf(':');
			int slash = dbUrl.lastIndexOf('/');
			int index = slash > colon ? slash : colon;
			String homePath = conn.getMetaData().getDatabaseProductName() + "-"
					+ dbUrl.substring(index + 1);
			GlobalParameter.setHomeDirectory(homePath);
			GlobalParameter.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something wrong with database configuration");
			System.exit(-1);
		}
	}

	private Properties getDBConfiguration() {
		Properties ini = null;
		try {
			ini = new Properties();
			String filename = System.getProperty("user.dir", "")
					+ System.getProperty("file.separator") + "db.conf";
			FileInputStream is = new FileInputStream(filename);
			ini.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ini;
	}

	public void close() {
		try {
			if (null != conn) {
				conn.commit();
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////
	// used for advanced user
	// //////////////////////////////////////////

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @return the nPageSize
	 */
	public int getNPageSize() {
		return nPageSize;
	}

	/**
	 * @return the nFetchSize
	 */
	public int getNFetchSize() {
		return nFetchSize;
	}

	/**
	 * need to close the resultset and the statement explicitly.
	 * 
	 * @param selectSQL
	 *            It's better to contain the order by clasuses
	 * 
	 */
	public ResultSet executeSelectSQL(String selectSQL, int offset, int limit,
			int fetchSize) {
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			if (dbName.equalsIgnoreCase("derby")) {
				selectSQL = selectSQL + " offset ? rows fetch next ? rows only";
				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setInt(1, offset);
				pstmt.setInt(2, limit);
				pstmt.setFetchSize(fetchSize);
				rs = pstmt.executeQuery();
				return rs;
			} else if (dbName.equalsIgnoreCase("postgresql")
					|| dbName.equalsIgnoreCase("mysql")) {
				selectSQL = selectSQL + " limit ? offset ?";
				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setInt(1, limit);
				pstmt.setInt(2, offset);
				pstmt.setFetchSize(fetchSize);
				rs = pstmt.executeQuery();
				return rs;
			} else {
				System.out.println("offset and page size for " + dbName
						+ " unsupported");
				System.exit(-1);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet executeSelectSQL(String selectSQL) {
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.execute();
			conn.commit();
			rs = pstmt.getResultSet();
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// wwx used for creating index table
	public boolean executeCreatOrDropSql(String sql) {
		PreparedStatement pstmt = null;
		try {
			if (pstmt != null) {
				pstmt.close();
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// wwx
	public void addMcmillanIndex(long process_id, String mpnFilePath,
			String tpoFilePath) {
		PreparedStatement pstmt = null;
		try {
			if (pstmt != null) {
				pstmt.close();
			}
			pstmt = conn
					.prepareStatement(
							"insert into mcmillanIndex (process_id,definitionMPN,definitionTPO,addtime) values (?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, process_id);
			FileInputStream fin = new FileInputStream(mpnFilePath);
			pstmt.setAsciiStream(2, fin);
			fin = new FileInputStream(tpoFilePath);
			pstmt.setAsciiStream(3, fin);
			pstmt.setTimestamp(4, new java.sql.Timestamp(System
					.currentTimeMillis()));
			pstmt.execute();
			conn.commit();
			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// the table size in bytes
	public long getTableSizeInBytes(String tableName) {
		if (dbName.equalsIgnoreCase("derby")) {
			return getDerbyTableSizeInBytes(tableName);
		} else if (dbName.equalsIgnoreCase("postgreSQL")) {
			return getPostgresqlTableSizeInBytes(tableName);
		} else if (dbName.equalsIgnoreCase("mysql")) {
			return getMysqlTableSizeInBytes(tableName);
		} else {
			System.out.println("table size for " + dbName + " unsupported");
			return -1;
		}
	}

	private long getMysqlTableSizeInBytes(String tableName) {
		long ret = -1;
		try {
			String selectSQL = "SELECT sum(data_length) FROM information_schema.tables WHERE table_name = ?";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ret = rs.getLong(1);
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private long getPostgresqlTableSizeInBytes(String tableName) {
		long ret = -1;
		try {
			String selectSQL = "SELECT pg_total_relation_size(?)";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ret = rs.getLong(1);
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private long getDerbyTableSizeInBytes(String tableName) {
		tableName = tableName.toUpperCase();
		long ret = -1;
		try {
			String selectSQL = "select * from new org.apache.derby.diag.SpaceTable('"
					+ tableName
					+ "') t where CONGLOMERATENAME='"
					+ tableName
					+ "'";
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(selectSQL);
			if (rs.next()) {
				long pages = rs.getLong("NUMALLOCATEDPAGES");
				int pagesize = rs.getInt("PAGESIZE");
				ret = pages * pagesize;
			}
			rs.close();
			stmt.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
	}

	// /////////////////////////////////////////
	// operations on process_catalog table
	// /////////////////////////////////////////

	/**
	 * @param pco
	 *            the new process catalog object
	 * @return the new process catalog id which is generated automatically
	 */
	public long addProcessCatalog(ProcesscatalogObject pco) {
		long ret = -1;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement(
							"insert into process_catalog (name,parent_id) values (?,?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, pco.getName());
			pstmt.setLong(2, pco.getParent_id());
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @param id
	 *            the process catalog's id which will be deleted
	 * @return if successful return true else return false
	 */
	public boolean delProcessCatalog(long catalog_id) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("delete from process_catalog where catalog_id=?");
			pstmt.setLong(1, catalog_id);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean updateProcessCatalogName(long catalog_id, String name) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("update process_catalog set name=? where catalog_id=?");
			pstmt.setString(1, name);
			pstmt.setLong(2, catalog_id);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean updateProcessCatalog(ProcesscatalogObject pco) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("update process_catalog set name=?,parent_id=? where catalog_id=?");
			pstmt.setString(1, pco.getName());
			pstmt.setLong(2, pco.getParent_id());
			pstmt.setLong(3, pco.getCatalog_id());
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<ProcesscatalogObject> getAllProcessCatalog() {
		Vector<ProcesscatalogObject> ret = new Vector<ProcesscatalogObject>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(nFetchSize);
			stmt.setMaxRows(nRowsLimit);
			ResultSet rs = stmt.executeQuery("select * from process_catalog");
			while (rs.next()) {
				ProcesscatalogObject pco = new ProcesscatalogObject();
				pco.setCatalog_id(rs.getLong("catalog_id"));
				pco.setName(rs.getString("name").trim());
				pco.setParent_id(rs.getLong("parent_id"));
				ret.add(pco);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getProcessCatalogNameById(long catalog_id) {
		String ret = null;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select name from process_catalog where catalog_id=?");
			pstmt.setLong(1, catalog_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				ret = rs.getString("name").trim();
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public long getProcessCatalogIdByName(String catalog) {
		long ret = -1;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select catalog_id from process_catalog where name=?");
			pstmt.setString(1, catalog);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				ret = rs.getLong("catalog_id");
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// ////////////////////////////////////////////////////
	// operations on process and petrinet table
	// ////////////////////////////////////////////////////

	/**
	 * @return the new process id generated automatically
	 */
	public PetrinetObject addProcess(ProcessObject po) {
		PetrinetObject ret = new PetrinetObject();
		try {
			// insert into process table
			String type = po.getType();
			PreparedStatement pstmt = conn
					.prepareStatement(
							"insert into process (name,description,type,definition,catalog_id,addtime) values (?,?,?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, po.getName());
			pstmt.setString(2, po.getDescription());
			pstmt.setString(3, type);

			ByteArrayInputStream bis = null;
			if (dbName.equalsIgnoreCase("postgresql")
					|| dbName.equalsIgnoreCase("mysql")) {
				pstmt.setString(4, new String(po.getDefinition()));
			} else if (dbName.equalsIgnoreCase("derby")) {
				bis = new ByteArrayInputStream(po.getDefinition());
				pstmt.setAsciiStream(4, bis);
			} else {
				System.out.println(dbName + " not supported");
				System.exit(-1);
			}

			pstmt.setLong(5, po.getCatalog_id());
			pstmt.setTimestamp(6, new java.sql.Timestamp(System
					.currentTimeMillis()));
			pstmt.execute();
			// conn.commit();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			long process_id = rs.getLong(1);
			po.setProcess_id(process_id);
			ret.setProcess_id(process_id);
			if (bis != null) {
				bis.close();
			}

			// transform from other model type to petrinet model
			// TODO: transform from other formats to petrinet
			if (GlobalParameter.isALLMODELS2PETRINETS()) {
				byte[] pnml = null;
				if (type.equals(ProcessObject.TYPEPNML)) {
					pnml = po.getDefinition();
				} else if (type.equals(ProcessObject.TYPEYAWL)) {
					// transform from yawl to petri net here
					PetriNet pn = YAWLUtil.convert2PetriNet(po.getDefinition());
					if (pn == null) {
						return ret;
					}
					pnml = PetriNetUtil.getPnmlBytes(pn);
				} else {
					System.out
							.println("need to transform to petri net in DatabaseAccessor.addProcess");
					return ret;
				}

				// insert into petrinet table

				// calculate the properties of petri net
				PetriNet pn = PetriNetUtil.getPetriNetFromPnmlBytes(pnml);
				ret.setPetriNet(pn);
				ret.setNplace(pn.getPlaces().size());
				ret.setNtransition(pn.getTransitions().size());
				ret.setNarc(pn.getNumberOfEdges());
				ret.setNdegree(-1);

				rs.close();
				pstmt.close();
				pstmt = conn
						.prepareStatement(
								"insert into petrinet (process_id,pnml,nplace,ntransition,narc,ndegree) values (?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				pstmt.setLong(1, ret.getProcess_id());

				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					pstmt.setString(2, new String(pnml));
				} else {
					bis = new ByteArrayInputStream(pnml);
					pstmt.setAsciiStream(2, bis);
				}

				pstmt.setInt(3, ret.getNplace());
				pstmt.setInt(4, ret.getNtransition());
				pstmt.setInt(5, ret.getNarc());
				pstmt.setInt(6, ret.getNdegree());
				pstmt.execute();
				// conn.commit();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long petrinet_id = rs.getLong(1);
				ret.setPetrinet_id(petrinet_id);
				if (bis != null) {
					bis.close();
				}

				// update process table
				rs.close();
				pstmt.close();
				pstmt = conn
						.prepareStatement("update process set petrinet_id=? where process_id=?");
				pstmt.setLong(1, petrinet_id);
				pstmt.setLong(2, process_id);
				pstmt.execute();
			}
			conn.commit();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean delAllProcess() {
		boolean ret = false;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("delete from process");
			stmt.executeUpdate("delete from petrinet");
			conn.commit();
			ret = true;
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;

	}

	public boolean delProcess(long process_id) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("delete from process where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean updateProcessName(long process_id, String name) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("update process set name=? where process_id=?");
			pstmt.setString(1, name);
			pstmt.setLong(2, process_id);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean updateProcess(ProcessObject po) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("update process set name=?,description=?,type=?,catalog_id=? where process_id=?");
			pstmt.setString(1, po.getName());
			pstmt.setString(2, po.getDescription());
			pstmt.setString(3, po.getType());
			pstmt.setLong(4, po.getCatalog_id());
			pstmt.setLong(5, po.getProcess_id());
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<ProcessObject> getAllProcess() {
		Vector<ProcessObject> ret = new Vector<ProcessObject>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setMaxRows(nRowsLimit);
			stmt.setFetchSize(nFetchSize);
			ResultSet rs = stmt
					.executeQuery("select process_id,name,description,type,catalog_id,addtime from process");
			while (rs.next()) {
				ProcessObject po = new ProcessObject();
				po.setProcess_id(rs.getLong("process_id"));
				po.setName(rs.getString("name").trim());
				po.setDescription(rs.getString("description"));
				po.setType(rs.getString("type").trim());
				po.setCatalog_id(rs.getLong("catalog_id"));
				po.setAddTime(rs.getTimestamp("addtime").getTime());
				ret.add(po);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getProcessType(long process_id) {
		String ret = null;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select type from process where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				ret = rs.getString("type").trim();
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public byte[] getProcessDefinitionBytes(long process_id) {
		byte[] ret = null;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select definition from process where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {

				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					String str = rs.getString("definition");
					ret = str.getBytes();
				} else if (dbName.equalsIgnoreCase("derby")) {
					InputStream is = rs.getAsciiStream("definition");
					ret = ToolKit.getBytesFromInputStream(is);
					is.close();
				} else {
					System.out.println(dbName + " not supported");
					System.exit(-1);
				}
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void exportAllYAWLModels(String filePath) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select name, definition from process where type='"
							+ ProcessObject.TYPEYAWL + "'");
			while (rs.next()) {
				byte[] ret = null;
				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					String str = rs.getString("definition");
					ret = str.getBytes();
				} else if (dbName.equalsIgnoreCase("derby")) {
					InputStream is = rs.getAsciiStream("definition");
					ret = ToolKit.getBytesFromInputStream(is);
					is.close();
				} else {
					System.out.println(dbName + " not supported");
					System.exit(-1);
				}
				String name = rs.getString("name");
				String filename = filePath + "/" + name + ".yawl";
				// InputStream fin = new ByteArrayInputStream(ret);
				OutputStream out = new FileOutputStream(filename);
				out.write(ret);
				// byte buf[] = new byte[1024];
				// int len;
				// while ((len = fin.read(buf)) > 0) {
				// out.write(buf, 0, len);
				// }
				out.close();
				// fin.close();
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportAllPNMLModels(String filePath) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select name, definition from process where type='"
							+ ProcessObject.TYPEPNML + "'");
			while (rs.next()) {
				byte[] ret = null;
				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					String str = rs.getString("definition");
					ret = str.getBytes();
				} else if (dbName.equalsIgnoreCase("derby")) {
					InputStream is = rs.getAsciiStream("definition");
					ret = ToolKit.getBytesFromInputStream(is);
					is.close();
				} else {
					System.out.println(dbName + " not supported");
					System.exit(-1);
				}
				String name = rs.getString("name");
				String filename = filePath + "/" + name + ".pnml";
				// InputStream fin = new ByteArrayInputStream(ret);
				OutputStream out = new FileOutputStream(filename);
				out.write(ret);
				// byte buf[] = new byte[1024];
				// int len;
				// while ((len = fin.read(buf)) > 0) {
				// out.write(buf, 0, len);
				// }
				out.close();
				// fin.close();
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InputStream getProcessDefinitionInputStream(long process_id) {
		return new ByteArrayInputStream(getProcessDefinitionBytes(process_id));
	}

	public InputStream getProcessPnml(long process_id) {
		InputStream ret = null;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select pnml from petrinet where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				byte[] temp = null;
				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					String str = rs.getString("pnml");
					temp = str.getBytes();
				} else if (dbName.equalsIgnoreCase("derby")) {
					InputStream is = rs.getAsciiStream("pnml");
					temp = ToolKit.getBytesFromInputStream(is);
					is.close();
				} else {
					System.out.println(dbName + " not supported");
					System.exit(-1);
				}
				if (temp != null) {
					if (temp.length > 0) {
						ret = new ByteArrayInputStream(temp);
					}
					temp = null;
				} else {
					System.out
							.println("something wrong with the pnml fetch from database");
				}
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public PetrinetObject getProcessPetrinetObject(long process_id) {
		PetrinetObject po = new PetrinetObject();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select * from petrinet where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				byte[] temp = null;
				if (dbName.equalsIgnoreCase("postgresql")
						|| dbName.equalsIgnoreCase("mysql")) {
					String str = rs.getString("pnml");
					temp = str.getBytes();
				} else if (dbName.equalsIgnoreCase("derby")) {
					InputStream is = rs.getAsciiStream("pnml");
					temp = ToolKit.getBytesFromInputStream(is);
					is.close();
				} else {
					System.out.println(dbName + " not supported");
					System.exit(-1);
				}

				po.setPnml(temp);
				po.setPetrinet_id(rs.getLong("petrinet_id"));
				po.setProcess_id(rs.getLong("process_id"));
				po.setNarc(rs.getInt("narc"));
				po.setNdegree(rs.getInt("ndegree"));
				po.setNplace(rs.getInt("nplace"));
				po.setNtransition(rs.getInt("ntransition"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return po;
	}

	public ProcessObject getProcess(long process_id) {
		ProcessObject po = new ProcessObject();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select process_id,name,description,type,catalog_id,petrinet_id,addtime from process where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				po.setProcess_id(rs.getLong("process_id"));
				po.setName(rs.getString("name").trim());
				po.setDescription(rs.getString("description"));
				po.setType(rs.getString("type").trim());
				po.setCatalog_id(rs.getLong("catalog_id"));
				po.setPetrinet_id(rs.getLong("petrinet_id"));
				po.setAddTime(rs.getTimestamp("addtime").getTime());
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return po;
	}

	public long getNumberOfPetriNet() {
		long ret = -1;
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt
					.executeQuery("select count(*) as n from petrinet");

			if (rs.next()) {
				ret = rs.getLong("n");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public long getNumberOfModels() {
		long ret = -1;
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt
					.executeQuery("select count(*) as n from process");

			if (rs.next()) {
				ret = rs.getLong("n");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// //////////////////////////////////
	// operations on indexinfo table
	// //////////////////////////////////

	public long addIndexInfo(IndexinfoObject iio) {
		long ret = -1;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement(
							"insert into indexinfo (javaclassname,description,state) values (?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, iio.getJavaclassName());
			pstmt.setString(2, iio.getDescription());
			pstmt.setString(3, iio.getState());
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean delIndexInfo(String indexJavaClassName) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("delete from indexinfo where javaclassname=?");
			pstmt.setString(1, indexJavaClassName);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean delIndexInfo(long index_id) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("delete from indexinfo where index_id=?");
			pstmt.setLong(1, index_id);
			pstmt.execute();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean updateIndexState(String javaClassName, String state) {
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("update indexinfo set state=? where javaclassname=?");
			pstmt.setString(1, state);
			pstmt.setString(2, javaClassName);
			pstmt.executeUpdate();
			conn.commit();
			ret = true;
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<IndexinfoObject> getAllIndexInfo() {
		Vector<IndexinfoObject> ret = new Vector<IndexinfoObject>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(nFetchSize);
			stmt.setMaxRows(nRowsLimit);
			ResultSet rs = stmt.executeQuery("select * from indexinfo");
			while (rs.next()) {
				IndexinfoObject iio = new IndexinfoObject();
				iio.setIndex_id(rs.getLong("index_id"));
				iio.setDescription(rs.getString("description"));
				iio.setJavaclassName(rs.getString("javaclassname").trim());
				iio.setState(rs.getString("state").trim());
				ret.add(iio);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<String> getAllUsedIndexClass() {
		Vector<String> ret = new Vector<String>();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select javaclassname from indexinfo where state=?");
			pstmt.setString(1, IndexinfoObject.USED);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			while (rs.next()) {
				ret.add(rs.getString("javaclassname").trim());
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// //////////////////////////////////////
	// operations on oplog table
	// //////////////////////////////////////

	public long addOplog(OplogObject olo) {
		long ret = -1;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement(
							"insert into oplog (indexname,optype,operand,timecost,optime,nplace,ntransition,narc,ndegree,npetri,resultsize) values (?,?,?,?,?,?,?,?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, olo.getIndexname());
			pstmt.setString(2, olo.getOptype());
			pstmt.setString(3, olo.getOperand());
			pstmt.setLong(4, olo.getTimecost());
			Timestamp ts = new Timestamp(olo.getTimestamp());
			pstmt.setTimestamp(5, ts);
			pstmt.setInt(6, olo.getNplace());
			pstmt.setInt(7, olo.getNtransition());
			pstmt.setInt(8, olo.getNarc());
			pstmt.setInt(9, olo.getNdegree());
			pstmt.setLong(10, olo.getNpetri());
			pstmt.setInt(11, olo.getResultsize());
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);

			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean delAllOplog() {
		boolean ret = false;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("delete from oplog");
			conn.commit();
			ret = true;
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<OplogObject> getAllOplog() {
		Vector<OplogObject> ret = new Vector<OplogObject>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setMaxRows(nRowsLimit);
			stmt.setFetchSize(nFetchSize);
			ResultSet rs = stmt.executeQuery("select * from oplog");
			while (rs.next()) {
				OplogObject olo = new OplogObject();
				olo.setEvent_id(rs.getLong("event_id"));
				olo.setIndexname(rs.getString("indexname").trim());
				olo.setOptype(rs.getString("optype").trim());
				olo.setOperand(rs.getString("operand"));
				olo.setTimecost(rs.getLong("timecost"));
				olo.setTimestamp(rs.getTimestamp("optime").getTime());
				olo.setNarc(rs.getInt("narc"));
				olo.setNdegree(rs.getInt("ndegree"));
				olo.setNplace(rs.getInt("nplace"));
				olo.setNtransition(rs.getInt("ntransition"));
				olo.setNpetri(rs.getLong("npetri"));
				olo.setResultsize(rs.getInt("resultsize"));
				ret.add(olo);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Vector<OplogObject> getAverageTimeCostGroupByNameAndType() {
		Vector<OplogObject> ret = new Vector<OplogObject>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt
					.executeQuery("select indexname,optype,AVG(timecost) as avgtime from oplog group by indexname,optype");
			while (rs.next()) {
				OplogObject olo = new OplogObject();
				olo.setIndexname(rs.getString("indexname").trim());
				olo.setOptype(rs.getString("optype").trim());
				olo.setTimecost(rs.getLong("avgtime"));
				ret.add(olo);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean addRltMatrix(RltMatrix matrix) {
		try {
			// insert into rltmatrix table
			PreparedStatement pstmt = conn
					.prepareStatement("insert into rltmatrix (process_id,transitionnum,tran2idxmap,matrix) values (?,?,?,?)");
			pstmt.setLong(1, matrix.getProcessId());
			pstmt.setInt(2, matrix.brm.transitionNum);
			pstmt.setBytes(3, matrix.serializeLabel2IntMap());
			pstmt.setBytes(4, matrix.serializeMatrixByLabel());
			pstmt.execute();
			conn.commit();
			pstmt.close();
			// System.out.println();
			// matrix.print();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public BasicRltMatrix getRltMatrixByProcessId(long process_id) {
		BasicRltMatrix matrix = new BasicRltMatrix();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("select process_id,transitionnum,tran2idxmap,matrix from rltmatrix where process_id=?");
			pstmt.setLong(1, process_id);
			pstmt.execute();
			conn.commit();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				matrix.process_id = rs.getLong("process_id");
				matrix.transitionNum = rs.getInt("transitionnum");
				matrix.deserializeLabel2IntMap(rs.getBytes("tran2idxmap"));
				matrix.deserializeMatrixByLabel(rs.getBytes("matrix"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrix;
	}

	// test
	public static void main(String[] args) {
		DatabaseAccessor da = DatabaseAccessor.getInstance();
		long size = da.getTableSizeInBytes("petrinet");
		System.out.println("petrinet table size: " + size + " bytes");
		System.out.println();
	}

}