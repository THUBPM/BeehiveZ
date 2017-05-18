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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.IndexinfoObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.OplogObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.PetrinetObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;
import cn.edu.thss.iise.beehivez.server.generator.ModelGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.GWFNetGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.MurataGenerator;
import cn.edu.thss.iise.beehivez.server.generator.petrinet.PiotrGenerator;
import cn.edu.thss.iise.beehivez.server.generator.yawl.MoeYAWLGenerator;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;
import cn.edu.thss.iise.beehivez.server.index.ProcessQueryResult;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.PetriNetIndex;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.BasicRltMatrix;
import cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix.RltMatrix;
import cn.edu.thss.iise.beehivez.server.index.yawlindex.YAWLIndex;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;

/**
 * @author JinTao 2009.9.5
 * 
 */
public class DataManager extends java.util.Observable {
	private DatabaseAccessor databaseAccessor = DatabaseAccessor.getInstance();

	// all used generator
	// name->generator
	private HashMap<String, ModelGenerator> generators = new HashMap<String, ModelGenerator>();

	public String[] getAllGeneratorsName() {
		return generators.keySet().toArray(new String[0]);
	}

	public ModelGenerator getGenerator(String name) {
		return generators.get(name);
	}

	// all used index
	private HashMap<String, BPMIndex> usedIndexes = new HashMap<String, BPMIndex>();

	public BPMIndex getIndex(String indexName) {
		return usedIndexes.get(indexName);
	}

	private static DataManager dmInstance = new DataManager();

	public static DataManager getInstance() {
		return dmInstance;
	}

	private DataManager() {
		loadIndexs();
		loadGenerators();
	}

	public void close() {
		Iterator it = usedIndexes.values().iterator();
		while (it.hasNext()) {
			BPMIndex index = (BPMIndex) it.next();
			index.close();
		}
		databaseAccessor.close();

	}

	private void loadGenerators() {
		generators.put("GWFNetGenerator", new GWFNetGenerator());
		generators.put("MurataGenerator", new MurataGenerator());
		generators.put("PiotrGenerator", new PiotrGenerator());
		generators.put("MoeYAWLGenerator", new MoeYAWLGenerator());
	}

	private void loadIndexs() {
		Iterator it = usedIndexes.values().iterator();
		while (it.hasNext()) {
			BPMIndex index = (BPMIndex) it.next();
			index.close();
		}

		usedIndexes.clear();
		Vector<String> vIndexClass = databaseAccessor.getAllUsedIndexClass();
		for (String str : vIndexClass) {
			try {
				BPMIndex index = (BPMIndex) (Class.forName(str).newInstance());
				index.open();
				usedIndexes.put(index.getName(), index);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getDBName() {
		return databaseAccessor.getDbName();
	}

	public int getPageSize() {
		return databaseAccessor.getNPageSize();
	}

	public int getFetchSize() {
		return databaseAccessor.getNFetchSize();
	}

	public long addProcessCatalog(long parent_id, String name) {
		ProcesscatalogObject pco = new ProcesscatalogObject();
		pco.setParent_id(parent_id);
		pco.setName(name);
		return databaseAccessor.addProcessCatalog(pco);
	}

	public boolean delProcessCatalog(long catalog_id) {
		return databaseAccessor.delProcessCatalog(catalog_id);
	}

	public boolean updateProcessCatalogName(long catalog_id, String name) {
		return databaseAccessor.updateProcessCatalogName(catalog_id, name);
	}

	public Vector<ProcesscatalogObject> getAllProcessCatalog() {
		return databaseAccessor.getAllProcessCatalog();
	}

	public long getProcessCatalogIdByName(String catalog) {
		return databaseAccessor.getProcessCatalogIdByName(catalog);
	}

	// //////////////////////////////////////////
	// used for advanced user
	// //////////////////////////////////////////
	public ResultSet executeSelectSQL(String selectSQL) {
		return databaseAccessor.executeSelectSQL(selectSQL);
	}

	public ResultSet executeSelectSQL(String selectSQL, int offset, int limit,
			int fetchSize) {
		return databaseAccessor.executeSelectSQL(selectSQL, offset, limit,
				fetchSize);
	}

	// wwx
	public boolean executeCreatSQL(String tableName) {
		String creatSQL = "CREATE TABLE "
				+ tableName
				+ "("
				+ "mcmillanIndex_id bigint not null generated always as identity  (START WITH 1, INCREMENT BY 1),"
				+ "process_id bigint not null,"
				+ "definitionMPN clob not null,"
				+ "definitionTPO clob not null,"
				+ "addtime timestamp not null,"
				+ "primary key (mcmillanIndex_id),"
				+ "FOREIGN KEY (process_id) REFERENCES process (process_id)  ON DELETE CASCADE)";

		return databaseAccessor.executeCreatOrDropSql(creatSQL);
	}

	// wwx
	public boolean executeDropSQL(String tableName) {
		String dropSQL = "DROP TABLE " + tableName + ";";

		return databaseAccessor.executeCreatOrDropSql(dropSQL);
	}

	// wwx
	public void addMcmillanIndex(long process_id, String mpnFilePath,
			String tpoFilePath) {
		databaseAccessor.addMcmillanIndex(process_id, mpnFilePath, tpoFilePath);
	}

	// ////////////////////////////////////
	// operations on process
	// maintain the index at the same time
	// ////////////////////////////////////

	public long addProces(String name, String desc, String type,
			long catalog_id, byte[] definition) {
		ProcessObject po = new ProcessObject();
		po.setCatalog_id(catalog_id);
		po.setName(name);
		po.setDescription(desc);
		po.setType(type);
		po.setDefinition(definition);

		PetrinetObject pno = databaseAccessor.addProcess(po);

		// update all the yawl indexes here
		OplogWriter logWriter = OplogWriter.getInstance();
		if (po.getType().equals(ProcessObject.TYPEYAWL)) {
			for (YAWLIndex index : getAllUsedYAWLIndex()) {
				long startTime = System.currentTimeMillis();
				index.addProcessModel(po);
				long timeCost = System.currentTimeMillis() - startTime;
				logWriter.writeLog(index.getName(), po,
						OplogObject.ADDDATATOINDEX, 1, timeCost);
			}
		}

		long processid = pno.getProcess_id();
		GlobalParameter.addOneModel();

		// update all the petri net indexes here
		if (-1 != pno.getPetrinet_id()) {
			try {
				for (PetriNetIndex index : getAllUsedPetriNetIndex()) {
					long startTime = System.currentTimeMillis();
					index.addProcessModel(pno);
					long timeCost = System.currentTimeMillis() - startTime;
					logWriter.writeLog(index.getName(), pno,
							OplogObject.ADDDATATOINDEX, 1, timeCost);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PetriNet pn = pno.getPetriNet();
				if (pn != null) {
					pn.destroyPetriNet();
				}
			}
		}
		return processid;
	}

	public boolean delAllProcess() {
		boolean ret = databaseAccessor.delAllProcess();
		GlobalParameter.setNModels(0);
		if (ret == true) {
			Iterator it = usedIndexes.values().iterator();
			while (it.hasNext()) {
				BPMIndex index = (BPMIndex) it.next();
				index.close();
				index.create();
			}
		}
		return ret;
	}

	public void delProcess(long process_id) {
		PetrinetObject pno = databaseAccessor
				.getProcessPetrinetObject(process_id);
		PetriNet pn = getProcessPetriNet(process_id);
		pno.setPetriNet(pn);
		pno.setNarc(pn.getNumberOfEdges());
		pno.setNdegree(-1);
		pno.setNplace(pn.getPlaces().size());
		pno.setNtransition(pn.getTransitions().size());
		Iterator it = usedIndexes.values().iterator();
		OplogWriter logWriter = OplogWriter.getInstance();
		while (it.hasNext()) {
			BPMIndex index = (BPMIndex) it.next();
			long startTime = System.currentTimeMillis();
			index.delProcessModel(pno);
			Long timeCost = System.currentTimeMillis() - startTime;
			logWriter.writeLog(index.getName(), pno,
					OplogObject.DELDATAFROMINDEX, 1, timeCost);
		}
		databaseAccessor.delProcess(process_id);
		GlobalParameter.removeOneModel();
		pn.destroyPetriNet();
	}

	public void updateProcess(ProcessObject po) {
		databaseAccessor.updateProcess(po);
	}

	public boolean updateProcessName(long process_id, String name) {
		return databaseAccessor.updateProcessName(process_id, name);
	}

	public Vector<ProcessObject> getAllProcess() {
		return databaseAccessor.getAllProcess();
	}

	// public Vector<ProcessObject> getProcessByExample(String filename,
	// String indexName) {
	// try {
	// FileInputStream fin = new FileInputStream(filename);
	// PnmlImport pi = new PnmlImport();
	// PetriNetResult pnr = (PetriNetResult) pi.importFile(fin);
	// PetriNet pn = pnr.getPetriNet();
	// return getProcessByExample(pn, indexName);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	// }

	public ProcessObject getProcess(long process_id) {
		return databaseAccessor.getProcess(process_id);
	}

	public String getProcessType(long process_id) {
		return databaseAccessor.getProcessType(process_id);
	}

	public byte[] getProcessDefinitionBytes(long process_id) {
		return databaseAccessor.getProcessDefinitionBytes(process_id);
	}

	public InputStream getProcessDefinitionInputStream(long process_id) {
		return databaseAccessor.getProcessDefinitionInputStream(process_id);
	}

	public InputStream getProcessPnml(long process_id) {
		return databaseAccessor.getProcessPnml(process_id);
	}

	public PetriNet getProcessPetriNet(long process_id) {
		try {
			PnmlImport pnml = new PnmlImport();
			InputStream in = databaseAccessor.getProcessPnml(process_id);
			if (in != null) {
				PetriNetResult result = (PetriNetResult) pnml.importFile(in);
				PetriNet pn = result.getPetriNet();
				result.destroy();
				in.close();
				in = null;
				return pn;
			} else {
				System.out
						.println("something wrong with pnml fetch from database");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public TreeSet<ProcessQueryResult> retrieveProcess(Object o,
			String indexName, float similarity) {
		TreeSet<ProcessQueryResult> ret = new TreeSet<ProcessQueryResult>();
		BPMIndex index = usedIndexes.get(indexName);

		if (index != null) {
			OplogWriter logWriter = OplogWriter.getInstance();
			long startTime = System.currentTimeMillis();
			TreeSet<ProcessQueryResult> pidSet = index.getProcessModels(o,
					similarity);
			long timeCost = System.currentTimeMillis() - startTime;
			logWriter.writeLog(index.getName(), o,
					OplogObject.QUERYDATAUSEINDEX, pidSet.size(), timeCost);
			Iterator it = pidSet.descendingIterator();
			while (it.hasNext()) {
				ProcessQueryResult r = (ProcessQueryResult) it.next();
				r.setPo(databaseAccessor.getProcess(r.getProcess_id()));
			}
			ret = pidSet;
		}
		return ret;
	}

	public void exportAllYAWLModels(String filePath) {
		databaseAccessor.exportAllYAWLModels(filePath);
	}

	public void exportAllPNMLModels(String filePath) {
		databaseAccessor.exportAllPNMLModels(filePath);
	}

	public boolean exportProcessToFile(long process_id, String filename) {
		InputStream fin = databaseAccessor
				.getProcessDefinitionInputStream(process_id);
		try {
			OutputStream out = new FileOutputStream(filename);
			byte buf[] = new byte[1024];
			int len;
			while ((len = fin.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			fin.close();
			System.out.println("\nFile is created");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public long getNumberOfPetriNet() {
		return databaseAccessor.getNumberOfPetriNet();
	}

	public long getNumberOfModels() {
		return databaseAccessor.getNumberOfModels();
	}

	// ////////////////////////////////////
	// operations on index info
	// ////////////////////////////////////

	public long addIndexInfo(String javaClassName, String description) {
		IndexinfoObject iio = new IndexinfoObject();
		iio.setDescription(description);
		iio.setJavaclassName(javaClassName);
		iio.setState(IndexinfoObject.USED);
		long ret = databaseAccessor.addIndexInfo(iio);
		try {
			BPMIndex bi = (BPMIndex) Class.forName(javaClassName).newInstance();
			bi.create();
			usedIndexes.put(bi.getName(), bi);
			this.setChanged();
			this.notifyObservers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public boolean delIndexInfo(String indexJavaClassName) {
		String indexName = indexJavaClassName.substring(indexJavaClassName
				.lastIndexOf(".") + 1);
		BPMIndex index = usedIndexes.get(indexName);
		if (index != null) {
			index.close();
			usedIndexes.remove(indexName);
		}
		boolean ret = databaseAccessor.delIndexInfo(indexJavaClassName);
		this.setChanged();
		this.notifyObservers();
		return ret;
	}

	public boolean updateIndexState(String javaClassName, String state) {
		boolean ret = databaseAccessor.updateIndexState(javaClassName, state);
		String indexName = javaClassName.substring(javaClassName
				.lastIndexOf(".") + 1);
		if (state.equalsIgnoreCase(IndexinfoObject.UNUSED)) {
			BPMIndex index = usedIndexes.get(indexName);
			index.destroy();
			usedIndexes.remove(indexName);
		} else if (state.equalsIgnoreCase(IndexinfoObject.USED)) {
			try {
				BPMIndex index = (BPMIndex) Class.forName(javaClassName)
						.newInstance();
				index.create();
				usedIndexes.put(index.getName(), index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.setChanged();
		this.notifyObservers();
		return ret;
	}

	public Vector<IndexinfoObject> getAllIndexInfo() {
		return databaseAccessor.getAllIndexInfo();
	}

	public Iterator<BPMIndex> getAllUsedIndexsIterator() {
		return usedIndexes.values().iterator();
	}

	public Vector<String> getAllUsedIndexClass() {
		return databaseAccessor.getAllUsedIndexClass();
	}

	private ArrayList<YAWLIndex> getAllUsedYAWLIndex() {
		ArrayList<YAWLIndex> ret = new ArrayList<YAWLIndex>();
		Iterator<BPMIndex> it = usedIndexes.values().iterator();
		while (it.hasNext()) {
			BPMIndex index = it.next();
			if (index instanceof YAWLIndex) {
				ret.add((YAWLIndex) index);
			}
		}
		return ret;
	}

	private ArrayList<PetriNetIndex> getAllUsedPetriNetIndex() {
		ArrayList<PetriNetIndex> ret = new ArrayList<PetriNetIndex>();
		Iterator<BPMIndex> it = usedIndexes.values().iterator();
		while (it.hasNext()) {
			BPMIndex index = it.next();
			if (index instanceof PetriNetIndex) {
				ret.add((PetriNetIndex) index);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedYAWLIndexName() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index instanceof YAWLIndex) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedPetriNetIndexName() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index instanceof PetriNetIndex) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedIndexNameSupportTextQuery() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index.supportTextQuery()) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedIndexNameSupportGraphQuery() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index.supportGraphQuery()) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedPetriNetIndexNameSupportGraphQuery() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index instanceof PetriNetIndex && index.supportGraphQuery()) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<String> getAllUsedYAWLIndexNameSupportGraphQuery() {
		Vector<String> ret = new Vector<String>();
		Iterator it = usedIndexes.keySet().iterator();
		while (it.hasNext()) {
			String indexName = (String) it.next();
			BPMIndex index = usedIndexes.get(indexName);
			if (index instanceof YAWLIndex && index.supportGraphQuery()) {
				ret.add(indexName);
			}
		}
		return ret;
	}

	public Vector<PetriNetIndex> getAllUsedPetriNetIndexSupportGraphQuery() {
		Vector<PetriNetIndex> ret = new Vector<PetriNetIndex>();
		for (BPMIndex index : usedIndexes.values()) {
			if (index instanceof PetriNetIndex && index.supportGraphQuery()) {
				ret.add((PetriNetIndex) index);
			}
		}
		return ret;
	}

	public Vector<YAWLIndex> getAllUsedYAWLIndexSupportGraphQuery() {
		Vector<YAWLIndex> ret = new Vector<YAWLIndex>();
		for (BPMIndex index : usedIndexes.values()) {
			if (index instanceof YAWLIndex && index.supportGraphQuery()) {
				ret.add((YAWLIndex) index);
			}
		}
		return ret;
	}

	// ////////////////////////////////////
	// operations on oplog
	// ////////////////////////////////////
	public long addOplog(OplogObject olo) {
		return databaseAccessor.addOplog(olo);
	}

	public boolean delAllOplog() {
		return databaseAccessor.delAllOplog();
	}

	public Vector<OplogObject> getAllOplog() {
		return databaseAccessor.getAllOplog();
	}

	public Vector<OplogObject> getAverageTimeCostGroupByNameAndType() {
		return databaseAccessor.getAverageTimeCostGroupByNameAndType();
	}

	// ////////////////////////////
	// get the process table size in MB
	public float getProcessTableSizeInMB() {
		float ret = databaseAccessor.getTableSizeInBytes("process");
		ret = ret / (float) (1024 * 1024);
		return ret;
	}

	// get the petrinet table size in MB
	public float getPetriNetTableSizeInMB() {
		float ret = databaseAccessor.getTableSizeInBytes("petrinet");
		ret = ret / (float) (1024 * 1024);
		return ret;
	}

	public void addRltMatrix(RltMatrix m) {
		databaseAccessor.addRltMatrix(m);
	}

	public BasicRltMatrix getRltMatrixByProcessId(long process_id) {
		return databaseAccessor.getRltMatrixByProcessId(process_id);
	}

	// test
	public static void main(String[] args) {
		DataManager dm = DataManager.getInstance();
		int i = 0;
		while (true) {
			for (ProcessObject po : dm.getAllProcess()) {
				dm.getProcessPetriNet(po.getProcess_id());
				i++;
				if (i % 10000 == 0) {
					System.out.println(i);
				}
			}
		}
	}

}
