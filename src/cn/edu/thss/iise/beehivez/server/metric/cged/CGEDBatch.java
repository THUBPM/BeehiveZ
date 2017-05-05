package cn.edu.thss.iise.beehivez.server.metric.cged;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.processmining.analysis.graphmatching.algos.GraphEditDistanceGreedy;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.algorithms.CoverabilityGraphBuilder;
import org.processmining.importing.pnml.PnmlImport;
import org.semanticweb.kaon2.bu;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

public class CGEDBatch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String path = new String("F:\\Demo\\SAP");

		CGEDBatch cged = new CGEDBatch();     
		try {
			cged.computeSimilarityBatch(path);
//			cged.analyzeModelBatch(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void analyzeModelBatch(String filePath) throws Exception {
		File folder = new File(filePath);
		File[] ffolder = folder.listFiles();
		
		int maxTransition = Integer.MIN_VALUE;
		int minTransition = Integer.MAX_VALUE;
		int maxPlace = Integer.MIN_VALUE;
		int minPlace = Integer.MAX_VALUE;
		int maxArcs = Integer.MIN_VALUE;
		int minArcs = Integer.MAX_VALUE;
		int totalTransitions = 0;
		int totalPlaces = 0;
		int totalArcs = 0;
		int totalModels = 0;

		for (int i = 0; i < ffolder.length; i++) {
			// read Petri-Net
			PnmlImport pnmlImport = new PnmlImport();
			FileInputStream pnml = new FileInputStream(
					ffolder[i].getAbsolutePath());
			PetriNet pn = pnmlImport.read(pnml);			
			// statistics of transitions, places and arcs
			int numOfTransitions = pn.getTransitions().size();
			int numOfPlaces = pn.getPlaces().size();
			int numOfArcs = pn.getEdges().size();
			if(maxTransition < numOfTransitions) {
				maxTransition = numOfTransitions;
			}
			if(minTransition > numOfTransitions) {
				minTransition = numOfTransitions;
			}
			if(maxPlace < numOfPlaces) {
				maxPlace = numOfPlaces;
			}
			if(minPlace > numOfPlaces) {
				minPlace = numOfPlaces;
			}
			if(maxArcs < numOfArcs) {
				maxArcs = numOfArcs;
			}
			if(minArcs > numOfArcs) {
				minArcs = numOfArcs;
			}
			totalTransitions += numOfTransitions;
			totalPlaces += numOfPlaces;
			totalArcs += numOfArcs;
			++totalModels;
		}
		BigDecimal aveTransition = new BigDecimal(totalTransitions / (double)totalModels);
		aveTransition = aveTransition.setScale(3, BigDecimal.ROUND_HALF_UP);
		BigDecimal avePlace = new BigDecimal(totalPlaces / (double)totalModels);
		avePlace = avePlace.setScale(3, BigDecimal.ROUND_HALF_UP);
		BigDecimal aveArc = new BigDecimal(totalArcs / (double)totalModels);
		aveArc = aveArc.setScale(3, BigDecimal.ROUND_HALF_UP);
		System.out.println("总模型数：" + totalModels);
		System.out.print("平均变迁数：" + aveTransition.toString());
		System.out.print("  平均库所数：" + avePlace.toString());
		System.out.println("  平均边数：" + aveArc.toString());
		System.out.print("最大变迁数：" + maxTransition);
		System.out.print("  最大库所数：" + maxPlace);
		System.out.println("  最大边数：" + maxArcs);
		System.out.print("最小变迁数：" + minTransition);
		System.out.print("  最小库所数：" + minPlace);
		System.out.println("  最小边数：" + minArcs);
	}

	/**
	 * 计算文件夹内所有模型的两两相似度
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public void computeSimilarityBatch(String filePath) throws Exception {
		//
		long avgnum = 0;
		File folder = new File(filePath);
		File[] ffolder = folder.listFiles();
		ArrayList<CoverabilityGraph> setArray = new ArrayList<CoverabilityGraph>();
		List<String> allFilenames = new ArrayList<String>();
		long avgtime1 = 0;
		long a1 = 0;
		long a2 = 0;
		
		int maxTransition = Integer.MIN_VALUE;
		int minTransition = Integer.MAX_VALUE;
		int maxPlace = Integer.MIN_VALUE;
		int minPlace = Integer.MAX_VALUE;
		int maxArcs = Integer.MIN_VALUE;
		int minArcs = Integer.MAX_VALUE;
		int totalTransitions = 0;
		int totalPlaces = 0;
		int totalArcs = 0;

		for (int i = 0; i < ffolder.length; i++) {
			a1 = System.currentTimeMillis();
			// read Petri-Net
			PnmlImport pnmlImport = new PnmlImport();
			FileInputStream pnml = new FileInputStream(
					ffolder[i].getAbsolutePath());
			PetriNet pn = pnmlImport.read(pnml);
			
			// statistics of transitions, places and arcs
			int numOfTransitions = pn.getTransitions().size();
			int numOfPlaces = pn.getPlaces().size();
			int numOfArcs = pn.getEdges().size();
			if(maxTransition < numOfTransitions) {
				maxTransition = numOfTransitions;
			}
			if(minTransition > numOfTransitions) {
				minTransition = numOfTransitions;
			}
			if(maxPlace < numOfPlaces) {
				maxPlace = numOfPlaces;
			}
			if(minPlace > numOfPlaces) {
				minPlace = numOfPlaces;
			}
			if(maxArcs < numOfArcs) {
				maxArcs = numOfArcs;
			}
			if(minArcs > numOfArcs) {
				minArcs = numOfArcs;
			}
			totalTransitions += numOfTransitions;
			totalPlaces += numOfPlaces;
			totalArcs += numOfArcs;
			
			// initial the source place
			((Place) pn.getSource()).addToken(new Token());
			// build the coverabilityGraph
			StateSpace space = CoverabilityGraphBuilder.build(pn);
			cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder builder = new cn.edu.thss.iise.beehivez.server.metric.cged.CoverabilityGraphBuilder();
			// convert to the T-labelGraph
			CoverabilityGraph cGraph = builder.convert(space,
					ffolder[i].getName());

			a2 = System.currentTimeMillis();
			avgtime1 = avgtime1 + (a2 - a1);
			avgnum = avgnum + cGraph.getVertices().size();
			allFilenames.add(ffolder[i].getName());
			setArray.add(cGraph);
		}
		avgnum = avgnum / ffolder.length;
		avgtime1 = avgtime1 / ffolder.length;
		System.out.println("变迁覆盖图生成完毕，平均基数为：" + avgnum);
		BigDecimal aveTransition = new BigDecimal(totalTransitions / (double)ffolder.length);
		aveTransition = aveTransition.setScale(3, BigDecimal.ROUND_HALF_UP);
		BigDecimal avePlace = new BigDecimal(totalPlaces / (double)ffolder.length);
		avePlace = avePlace.setScale(3, BigDecimal.ROUND_HALF_UP);
		BigDecimal aveArc = new BigDecimal(totalArcs / (double)ffolder.length);
		aveArc = aveArc.setScale(3, BigDecimal.ROUND_HALF_UP);
		System.out.print("平均变迁数：" + aveTransition.toString());
		System.out.print("  平均库所数：" + avePlace.toString());
		System.out.println("  平均边数：" + aveArc.toString());
		System.out.print("最大变迁数：" + maxTransition);
		System.out.print("  最大库所数：" + maxPlace);
		System.out.println("  最大边数：" + maxArcs);
		System.out.print("最小变迁数：" + minTransition);
		System.out.print("  最小库所数：" + minPlace);
		System.out.println("  最小边数：" + minArcs);

		// 生成两两模型相似度矩阵
		double[][] simMatrix_greedy = new double[ffolder.length][ffolder.length];
		long avgtime2 = 0;
		long b1 = 0;
		long b2 = 0;
		long n = 0;
		long num100 = 0;
		for (int i = 0; i < ffolder.length; i++) {
			for (int j = i; j < ffolder.length; j++) {
				n++;
				// System.out.print(n);
				if (i == j) {
					simMatrix_greedy[i][j] = 1.0;
				} else {
					b1 = System.currentTimeMillis();
					// double[][] seqM =
					// this.computeCGEDMatrix(setArray.get(i),setArray.get(j));
					// System.out.println("序列间的相似度矩阵计算完毕");
					// System.out.println("开始："+setArray.get(i).getFileName()+"和"+setArray.get(j).getFileName());

					simMatrix_greedy[i][j] = simMatrix_greedy[j][i] = computeSimilarityForTwoNet_Greedy(
							setArray.get(i), setArray.get(j));
					if (simMatrix_greedy[i][j] == 100.0) {
						num100++;
					}
					b2 = System.currentTimeMillis();
					avgtime2 = avgtime2 + (b2 - b1);
					// System.out.println("完成：" + setArray.get(i).getFilename()
					// + "和"
					// + setArray.get(j).getFilename() + ":"
					// + simMatrix_greedy[i][j]);
				}
			}
		}
		avgtime2 = avgtime2 / n;
		System.out.println("相似度矩阵生成完毕");
		System.out.println("超时未计算出结果率：" + num100 * 1.0 / n);
		// System.out.println("生成CoverabilityGraph的平均时间："+avgtime1+",计算相似性公式的平均时间："+avgtime2+"。求两模型相似度的平均时间："+(avgtime1+avgtime2));
		System.out.println("计算相似性公式的平均时间：" + avgtime2 + "。求两模型相似度的平均时间："
				+ (avgtime1 + avgtime2));

		// //打印相似度矩阵
		// for(int i = 0;i<simMatrix_astar.length;i++){
		// for(int j = 0;j<simMatrix_astar.length;j++){
		// System.out.print(simMatrix_astar[i][j]+" ");
		// }
		// System.out.println();
		// }
		//
		// //考查三角不等式满足情况
		StringBuilder builder4_2 = new StringBuilder();
		builder4_2.append("不满足4_2的模型为：\n");
		StringBuilder builder4_3 = new StringBuilder();
		builder4_3.append("不满足4_3的模型为：\n");
		int sum = 0;
		int meet4_2 = 0;
		int meet4_3 = 0;
		for (int i = 0; i < setArray.size(); i++) {
			for (int j = i + 1; j < setArray.size(); j++) {
				for (int k = j + 1; k < setArray.size(); k++) {
					if ((simMatrix_greedy[i][j] != 100.0)
							&& (simMatrix_greedy[i][k] != 100.0)
							&& (simMatrix_greedy[j][k] != 100.0)) {
						sum++;
						if (this.meet4_2(simMatrix_greedy[i][j],
								simMatrix_greedy[i][k], simMatrix_greedy[j][k])) {
							meet4_2++;
						} else {
							builder4_2.append(allFilenames.get(i));
							builder4_2.append(" : ");
							builder4_2.append(allFilenames.get(j));
							builder4_2.append(" : ");
							builder4_2.append(allFilenames.get(k));
							builder4_2.append("\n");
						}
						if (this.meet4_3(simMatrix_greedy[i][j],
								simMatrix_greedy[i][k], simMatrix_greedy[j][k])) {
							meet4_3++;
						} else {
							builder4_3.append(allFilenames.get(i));
							builder4_3.append(" : ");
							builder4_3.append(allFilenames.get(j));
							builder4_3.append(" : ");
							builder4_3.append(allFilenames.get(k));
							builder4_3.append("\n");
						}

					}
				}
			}
		}
		if (sum == 0) {
			System.out.println("不到三个");
		} else {
			double meetRate4_2 = meet4_2 * 1.0 / sum;
			System.out.println("公式4_2三角不等式的满足情况：" + meetRate4_2);
			double meetRate4_3 = meet4_3 * 1.0 / sum;
			System.out.println("公式4_3三角不等式的满足情况：" + meetRate4_3);
//			System.out.println(builder4_2.toString());
//			System.out.println(builder4_3.toString());
			BufferedWriter writer = new BufferedWriter(new FileWriter("F:\\Demo\\no_meet4.txt"));
			writer.write(builder4_2.toString());
			writer.newLine();
			writer.write(builder4_3.toString());
			writer.close();
		}
		//
		//
	}

	/**
	 * 计算公式4_2
	 */
	public boolean meet4_2(double sim1, double sim2, double sim3) {
		double dis1 = 1 - sim1;
		double dis2 = 1 - sim2;
		double dis3 = 1 - sim3;
		if ((dis1 + dis2 >= dis3) && (dis1 + dis3 >= dis2)
				&& (dis2 + dis3 >= dis1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 计算公式4_3 用-1表示无穷大
	 */
	public boolean meet4_3(double sim1, double sim2, double sim3) {
		double dis1;
		if (sim1 == 0.0) {
			dis1 = -1.0;
		} else {
			dis1 = 1 / sim1 - 1.0;
		}
		double dis2;
		if (sim2 == 0.0) {
			dis2 = -1.0;
		} else {
			dis2 = 1 / sim2 - 1.0;
		}
		double dis3;
		if (sim3 == 0.0) {
			dis3 = -1.0;
		} else {
			dis3 = 1 / sim3 - 1.0;
		}
		if (((dis1 == -1.0) && (dis2 != -1.0) && (dis3 != -1.0))
				|| ((dis1 != -1.0) && (dis2 == -1.0) && (dis3 != -1.0))
				|| ((dis1 != -1.0) && (dis2 != -1.0) && (dis3 == -1.0))) {
			// System.out.println("dis[i][j]:" + dis1);
			// System.out.println("dis[i][k]:" + dis2);
			// System.out.println("dis[j][k]:" + dis3);
			return false;
		} else if (((dis1 == -1.0) && (dis2 == -1.0) && (dis3 != -1.0))
				|| ((dis1 == -1.0) && (dis2 != -1.0) && (dis3 == -1.0))
				|| ((dis1 != -1.0) && (dis2 == -1.0) && (dis3 == -1.0))) {
			return true;
		} else if ((dis1 == -1.0) && (dis2 == -1.0) && (dis3 == -1.0)) {
			return true;
		} else if ((dis1 + dis2 >= dis3) && (dis1 + dis3 >= dis2)
				&& (dis2 + dis3 >= dis1)) {
			return true;
		} else {
			// System.out.println("dis[i][j]:" + dis1);
			// System.out.println("dis[i][k]:" + dis2);
			// System.out.println("dis[j][k]:" + dis3);
			return false;
		}
	}

	/**
	 * 计算两触发序列集合的矩阵，矩阵的值为触发序列的相似度
	 * 
	 * @param seqSet1
	 * @param seqSet2
	 * @return
	 */
	// public double[][] computeCGEDMatrix(CoverabilityGraph
	// seqSet1,CoverabilityGraph seqSet2){
	// if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
	// double[][] seqM = new
	// double[seqSet1.getNPSet().size()][seqSet2.getNPSet().size()];
	// for(int i = 0;i<seqSet1.getNPSet().size();i++){
	// for(int j = 0;j<seqSet2.getNPSet().size();j++){
	// seqM[i][j] =
	// seqSet1.getNPSet().get(i).SequenceSimilarity(seqSet2.getNPSet().get(j));
	// }
	// // System.out.println(i);
	// }
	// return seqM;
	// }
	// else{
	// double[][] seqM = new
	// double[seqSet2.getNPSet().size()][seqSet1.getNPSet().size()];
	// for(int i = 0;i<seqSet2.getNPSet().size();i++){
	// for(int j = 0;j<seqSet1.getNPSet().size();j++){
	// seqM[i][j] =
	// seqSet2.getNPSet().get(i).SequenceSimilarity(seqSet1.getNPSet().get(j));
	// }
	// }
	// return seqM;
	// }
	// }

	/**
	 * 给定两个模型序列集合，返回相似度的值，A*算法
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	// public double computeSimilarityForTwoNet_Astar(double[][] seqM,NewPtsSet
	// seqSet1,NewPtsSet seqSet2){
	// if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
	// return seqSet1.setSimilarity_Astar(seqM,seqSet2);
	// }
	// else{
	// return seqSet2.setSimilarity_Astar(seqM,seqSet1);
	// }
	//
	// }
	//
	/**
	 * 给定两个模型序列集合，返回相似度的值，遍历算法
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	// public double computeSimilarityForTwoNet_ergodic(double[][]
	// seqM,NewPtsSet seqSet1,NewPtsSet seqSet2){
	// if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
	// return seqSet1.setSimilarity_ergodic(seqSet2,seqM);
	// }
	// else{
	// return seqSet2.setSimilarity_ergodic(seqSet1,seqM);
	// }
	//
	// }

	/**
	 * 给定两个模型序列集合，返回相似度的值，贪心算法
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	public double computeSimilarityForTwoNet_Greedy(CoverabilityGraph cGraph1,
			CoverabilityGraph cGraph2) {

		GraphEditDistanceGreedy measure = new GraphEditDistanceGreedy();
		double distance = measure.compute(cGraph1, cGraph2);
		int edgeSpan1 = 0, edgeSpan2 = 0;
		for (String edge : cGraph1.getConflictSpans().keySet()) {
			int span = Math.max(cGraph1.getConflictSpans().get(edge), cGraph1
					.getLoopSpans().get(edge));
			edgeSpan1 += ((span > 0) ? span : 1.0);
		}
		for (String edge : cGraph2.getConflictSpans().keySet()) {
			int span = Math.max(cGraph2.getConflictSpans().get(edge), cGraph2
					.getLoopSpans().get(edge));
			edgeSpan2 += ((span > 0) ? span : 1.0);
		}

		double edgeWeight = (edgeSpan1 + edgeSpan2 - 2) * 0.5;
		double similarity = (cGraph1.getVertices().size()
				+ cGraph2.getVertices().size() - 2 - distance + edgeWeight)
				/ (cGraph1.getVertices().size() + cGraph2.getVertices().size()
						- 2 + edgeWeight);
		BigDecimal sim = new BigDecimal(similarity);
		sim = sim.setScale(3, BigDecimal.ROUND_HALF_UP);
		return sim.doubleValue();
		// return distance;

	}
	/**
	 * 给定模型，返回对应的触发序列集合
	 * 
	 * @param process
	 * @return
	 */
	// public NewPtsSet computeSequenceSet(File process){
	// if (process.isHidden()) {
	//
	// }
	//
	// PnmlImport pnmlimport = new PnmlImport();
	// CTree ctree = null;
	// PetriNet petrinet = null;
	// FileInputStream pnml = null;
	// try {
	// pnml = new FileInputStream(process.getAbsolutePath());
	// petrinet = pnmlimport.read(pnml);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// CTreeGenerator generator = new CTreeGenerator(
	// MyPetriNet.PromPN2MyPN(petrinet));
	// ctree = generator.generateCTree();
	// TTreeGenerator ttg = new TTreeGenerator();
	// /**
	// * Input: a coverability tree cTree, loop times K Ouput: a trace tree
	// tTree
	// */
	// NewPtsSet nps = ttg.generatTTree(ctree, 2, process.getName());//循环次数
	// nps.showSet();
	// return nps;//循环次数
	//
	// }

}
