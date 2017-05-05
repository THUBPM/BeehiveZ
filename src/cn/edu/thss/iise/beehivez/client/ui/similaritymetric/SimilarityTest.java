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

package cn.edu.thss.iise.beehivez.client.ui.similaritymetric;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.filelogger.FileLogger;
import cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity_Wang;
import cn.edu.thss.iise.beehivez.server.metric.CausalFootprintSimilarity;
import cn.edu.thss.iise.beehivez.server.metric.tar.temp.ComputeExtensiveTARSimilarity;

import java.util.Hashtable;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Set;
import cn.edu.thss.iise.beehivez.server.util.MathUtil;
import java.lang.String;

public class SimilarityTest
{
    public String filepath;
    public SimilarityTest(String path)
    {
        filepath = path;
    }

    public void BPS_WangTest()
    {
        FileLogger.deleteLogFile("Similarity-BTS.csv");
        FileLogger.deleteLogFile("Time-BTS.csv");
        File folder = new File(filepath);
        File[] ProcessList = folder.listFiles();
        for (int i = 0; i < ProcessList.length; i++)
        {
            for (int j = i; j < ProcessList.length; j++)
            {
                File Process1 = ProcessList[i];
                File Process2 = ProcessList[j];
                //File Process2 = ProcessList[i];
                try
                {
                    FileInputStream pnml1 = new FileInputStream(Process1.getAbsolutePath());
                    FileInputStream pnml2 = new FileInputStream(Process2.getAbsolutePath());
                    PnmlImport pnmlimport = new PnmlImport();
                    PetriNet petrinet1 = pnmlimport.read(pnml1);
                    PetriNet petrinet2 = pnmlimport.read(pnml2);
                    System.out.println(Process1.getName() + "-->" + Process2.getName());
                    BTSSimilarity_Wang bts = new BTSSimilarity_Wang();
                    double similarity = bts.similarity(petrinet1, petrinet2);
                    String result = Process1.getName() + "," + Process2.getName()
                        + "," + similarity;
                    FileLogger.writeLog("Similarity-BTS.csv", result);
                    pnml1.close();
                    pnml2.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public void FootprintTest()
    {
        FileLogger.deleteLogFile("Similarity-CFP.csv");
        File folder = new File(filepath);
        File[] ProcessList = folder.listFiles();
        for (int i = 0; i < ProcessList.length; i++)
        {
            for (int j = i; j < ProcessList.length; j++)
            //int j = i;
            {
                File Process1 = ProcessList[i];
                File Process2 = ProcessList[j];
                try
                {
                    long startTime = System.nanoTime();
                    FileInputStream pnml1 = new FileInputStream(Process1.getAbsolutePath());
                    FileInputStream pnml2 = new FileInputStream(Process2.getAbsolutePath());
                    PnmlImport pnmlimport = new PnmlImport();
                    PetriNet petrinet1 = pnmlimport.read(pnml1);
                    PetriNet petrinet2 = pnmlimport.read(pnml2);
                    System.out.println(Process1.getName() + "-->" + Process2.getName());
                    CausalFootprintSimilarity footprint = new
                        CausalFootprintSimilarity();
                    double similarity = footprint.similarity(petrinet1,
                        petrinet2);
                    long usedTime = System.nanoTime()-startTime;
                    String result = Process1.getName() + "," + Process2.getName() +
                        "," + similarity + "," + usedTime;
                    FileLogger.writeLog("Similarity-CFP.csv", result);
                    pnml1.close();
                    pnml2.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
	public void ExtensiveTARTest(){
		File folder = new File(filepath);
		File[] ProcessList = folder.listFiles();
		FileLogger.deleteLogFile("similarity_exTAR.csv");
		for(int i = 0;i<ProcessList.length;i++)
		{
			for(int j = i+1;j<ProcessList.length;j++)
			{
					File Process1 = ProcessList[i];
					File Process2 = ProcessList[j];
					FileInputStream pnml1 = null;
					FileInputStream pnml2 = null;
					PetriNet petrinet1 = null;
					PetriNet petrinet2 = null;
					try
					{
						pnml1 = new FileInputStream(Process1.getAbsolutePath());
						pnml2 = new FileInputStream(Process2.getAbsolutePath());
						PnmlImport pnmlimport = new PnmlImport();
						petrinet1 = pnmlimport.read(pnml1);
						petrinet2 = pnmlimport.read(pnml2);
						MyPetriNet mpn1 = MyPetriNet.PromPN2MyPN(petrinet1);
						MyPetriNet mpn2 = MyPetriNet.PromPN2MyPN(petrinet2);
						System.out.println(Process1.getName() + "-->" + Process2.getName());
						long startTime=System.nanoTime();  
						ComputeExtensiveTARSimilarity exTAR = new ComputeExtensiveTARSimilarity(mpn1, mpn2);
						double similarity = exTAR.computeExtenSiveTARSimilarity();
						long endTime = System.nanoTime();
						long usedTime = endTime-startTime;
						String result = Process1.getName()+","+Process2.getName()+","+similarity + ","+usedTime;
						FileLogger.writeLog("similarity_exTAR.csv", result);
						pnml1.close();
						pnml2.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					} 
			}
		}
	}
	
	public void triInequalityExtensiveTAR(){
		File folder = new File(filepath);
		File[] ProcessList = folder.listFiles();
		FileLogger.deleteLogFile("triInequality_exTAR.csv");
		for(int i = 0; i < ProcessList.length; ++i){
			File Process1 = ProcessList[i];
			FileInputStream pnml1 = null;
			PetriNet pn1 = null;
			PnmlImport pnmlimport = new PnmlImport();
			try {
				pnml1 = new FileInputStream(Process1.getAbsolutePath());
				pn1 = pnmlimport.read(pnml1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MyPetriNet mpn1 = MyPetriNet.PromPN2MyPN(pn1);
			
			for(int j = i + 1; j < ProcessList.length; ++j){
				File Process2 = ProcessList[j];
				FileInputStream pnml2 = null;
				PetriNet pn2 = null;
				try {
					pnml2 = new FileInputStream(Process2.getAbsolutePath());
					pn2 = pnmlimport.read(pnml2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyPetriNet mpn2 = MyPetriNet.PromPN2MyPN(pn2);
				
				ComputeExtensiveTARSimilarity exTAR = new ComputeExtensiveTARSimilarity(mpn1, mpn2);
				double x = exTAR.computeExtenSiveTARSimilarity();
				
				for(int k = j + 1; k < ProcessList.length; ++k){
					File Process3 = ProcessList[k];
					FileInputStream pnml3 = null;
					PetriNet pn3 = null;
					try {
						pnml3 = new FileInputStream(Process3.getAbsolutePath());
						pn3 = pnmlimport.read(pnml3);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MyPetriNet mpn3 = MyPetriNet.PromPN2MyPN(pn3);
					
					exTAR = new ComputeExtensiveTARSimilarity(mpn1, mpn3);
					double y = exTAR.computeExtenSiveTARSimilarity();
					
					exTAR = new ComputeExtensiveTARSimilarity(mpn2, mpn3);
					double z = exTAR.computeExtenSiveTARSimilarity();
					
					boolean sat = MathUtil.satisfyTriIneq(x, y, z);
					String result = Process1.getName()+","+Process2.getName()+","+ Process3.getName() + "," + sat;
					System.out.println(result);
					FileLogger.writeLog("triInequality_exTAR.csv", result);
					try {
						pnml3.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					pnml2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				pnml1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    public static void TestTriIneq(String simFileName, String outFile)
    {
        //there are three column in the file: 1. src model; 2. dst model; 3. sim value
        try
        {
            FileLogger.deleteLogFile(outFile);
            BufferedReader br = new BufferedReader(new FileReader(simFileName));
            String line = null;
            Hashtable<String, Hashtable> htSrcModels = new Hashtable();
            Vector<String> srcModels = new Vector();
            while((line = br.readLine()) != null)
            {
                String[] sarParams = line.split(",");
                if(!srcModels.contains(sarParams[0]))
                    srcModels.add(sarParams[0]);
                if(!htSrcModels.containsKey(sarParams[0]))
                    htSrcModels.put(sarParams[0], new Hashtable());
                Hashtable<String, Double> htDstModels = htSrcModels.get(sarParams[0]);
                htDstModels.put(sarParams[1], Double.valueOf(sarParams[2]));
            }
            br.close();

            int totalSat = 0;
            int totalPair = 0;
            for(int i=0; i<srcModels.size(); i++)
            {
                Hashtable<String, Double> htX = htSrcModels.get(srcModels.get(i));
                for(int j=i+1; j<srcModels.size(); j++)
                {
                    Hashtable<String, Double> htY = htSrcModels.get(srcModels.get(j));
                    Double dXY = htX.get(srcModels.get(j));
                    if(dXY == null)
                        continue;
                    for(int k=j+1; k<srcModels.size(); k++)
                    {
                        Double dXZ = htX.get(srcModels.get(k));
                        Double dYZ = htY.get(srcModels.get(k));

                        if(dXZ == null || dYZ == null)
                            continue;

                        totalPair++;

                        if(MathUtil.satisfyTriIneq(dXY.doubleValue(), dXZ.doubleValue(), dYZ.doubleValue()))
                            totalSat++;
                        else
                        {
                            String result = "";
                            result += srcModels.get(i) + "," + srcModels.get(j) + "," + srcModels.get(k);
                            result += ",";
                            result += dXY.doubleValue() + "," + dXZ.doubleValue() + "," + dYZ.doubleValue();
                            //���i,j,k���ߵ���ƣ�����֮��Ķ�ֵ
                            FileLogger.writeLog(outFile, result);
                        }
                    }
                }
            }

            System.out.println(simFileName + "'s total rate:" + totalSat*1.0/totalPair);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace(System.out);
        }
        catch (IOException ex2)
        {
            ex2.printStackTrace(System.out);
        }
    }

    public static void TestAvgTime(String simFileName)
    {
        //there are one column in the file: time
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(simFileName));
            String line = null;
            long total = 0;
            int n = 0;
            while((line = br.readLine()) != null)
            {
                total += Long.valueOf(line.trim()).longValue();
                n++;
            }
            br.close();

            System.out.println(simFileName + "'s avg time:" + total*1.0/n);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace(System.out);
        }
        catch (IOException ex2)
        {
            ex2.printStackTrace(System.out);
        }
    }

    public static void main(String[] args)
    {
        SimilarityTest test = new SimilarityTest("experiment_3");

//        test.BPS_WangTest();
//        SimilarityTest.TestTriIneq("Similarity-BTS.csv", "TriIneq-BTS.csv");
//        
//        test.FootprintTest();
//        SimilarityTest.TestTriIneq("Similarity-CFP.csv", "TriIneq-CFP.csv");
//        
        test.ExtensiveTARTest();
        test.triInequalityExtensiveTAR();
        
        /*
        SimilarityTest.TestTriIneq("Similarity-SAP-CFP.csv");
        SimilarityTest.TestTriIneq("Similarity-SAP-Bae.csv");
        SimilarityTest.TestTriIneq("Similarity-SAP-TAR.csv");

        SimilarityTest.TestAvgTime("Time-SAP-BTS.csv");
        SimilarityTest.TestAvgTime("Time-SAP-CFP.csv");
        SimilarityTest.TestAvgTime("Time-SAP-Bae.csv");
        SimilarityTest.TestAvgTime("Time-SAP-TAR.csv");
        */

        System.exit(0);
    }
}
