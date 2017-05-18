package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

public class PTSNew {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String path = new String("C:\\Users\\dongzihe\\Dropbox\\毕设相关\\毕设代码\\模型库\\闻老师给的\\sapOK1");
//		String path = new String("/Users/dongzihe/Dropbox/毕设相关/毕设代码/模型库/闻老师给的/11");
		PTSNew ptsn = new PTSNew();
		try {
			ptsn.computeSimilarityBatch(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 计算文件夹内所有模型的两两相似度
	 * @param filePath
	 */
	public void computeSimilarityBatch(String filePath){
		//触发序列集合的平均基数
		long avgnum = 0;
		File folder = new File(filePath);
		File[] ffolder = folder.listFiles();
		ArrayList<NewPtsSet> setArray = new ArrayList<NewPtsSet>();
		long avgtime1 = 0;
		long a1 = 0;
		long a2 = 0;
		for(int i = 0;i<ffolder.length;i++){
			a1 = System.currentTimeMillis();
			NewPtsSet nps= computeSequenceSet(ffolder[i]);
			a2 = System.currentTimeMillis();
			avgtime1 = avgtime1+(a2-a1);					
			System.out.println(nps.getFileName()+":"+nps.getNPSet().size());
			avgnum = avgnum + nps.getNPSet().size();
			setArray.add(nps);
		}
		avgnum = avgnum/ffolder.length;
		avgtime1 = avgtime1/ffolder.length;
		System.out.println("触发序列集合生成完毕,触发序列集合的平均基数为："+avgnum);
//		//生成两两模型相似度矩阵
		double[][] simMatrix_astar = new double[ffolder.length][ffolder.length];
		long avgtime2 = 0;
		long b1 = 0;
		long b2 = 0;
		long n = 0;
		long num100 = 0;
		for(int i = 0;i<ffolder.length;i++){
			for(int j = i;j<ffolder.length;j++){
				n++;
				System.out.print(n);
				if(i==j){
					simMatrix_astar[i][j] = 1.0;
				}
				else{
					b1 = System.currentTimeMillis();
					double[][] seqM = this.computeSeqMatrix(setArray.get(i),setArray.get(j));
					System.out.println("序列间的相似度矩阵计算完毕");
//					System.out.println("开始："+setArray.get(i).getFileName()+"和"+setArray.get(j).getFileName());
					
					simMatrix_astar[i][j] = simMatrix_astar[j][i] = computeSimilarityForTwoNet_Astar(seqM,setArray.get(i),setArray.get(j));
					if(simMatrix_astar[i][j]==100.0){
						num100++;
					}
					b2 = System.currentTimeMillis();
					avgtime2 = avgtime2+(b2-b1);
					System.out.println("完成："+setArray.get(i).getFileName()+"和"+setArray.get(j).getFileName()+":"+simMatrix_astar[i][j]);
				}
			}
		}
		avgtime2 = avgtime2/n;
		System.out.println("相似度矩阵生成完毕");
		System.out.println("超时未计算出结果率："+num100*1.0/n);
		System.out.println("生成触发序列的平均时间："+avgtime1+",计算相似性公式的平均时间："+avgtime2+"。求两模型相似度的平均时间："+(avgtime1+avgtime2));
//		//打印相似度矩阵
//		for(int i = 0;i<simMatrix_astar.length;i++){
//			for(int j = 0;j<simMatrix_astar.length;j++){
//				System.out.print(simMatrix_astar[i][j]+" ");
//			}
//			System.out.println();
//		}
//		
//		//考查三角不等式满足情况
		int sum = 0;
		int meet4_2 = 0;
		int meet4_3 = 0;
		for(int i = 0;i<setArray.size();i++){
			for(int j = i+1; j<setArray.size();j++){
				for(int k = j+1;k<setArray.size();k++){ 
					if((simMatrix_astar[i][j]!=100.0)&&(simMatrix_astar[i][k]!=100.0)&&(simMatrix_astar[j][k]!=100.0)){
						sum++;
//						if(this.meet4_2(simMatrix_astar[i][j], simMatrix_astar[i][k], simMatrix_astar[j][k])){
//							meet4_2++;
//						}
						if(this.meet4_3(simMatrix_astar[i][j], simMatrix_astar[i][k], simMatrix_astar[j][k])){
							meet4_3++;
						}
						else{
							System.out.println("i:"+setArray.get(i).fileName);
							System.out.println("j:"+setArray.get(j).fileName);
							System.out.println("k:"+setArray.get(k).fileName);
							System.out.println("simMatrix_astar[i][j]:"+simMatrix_astar[i][j]);
							System.out.println("simMatrix_astar[i][k]:"+simMatrix_astar[i][k]);
							System.out.println("simMatrix_astar[j][k]:"+simMatrix_astar[j][k]);
							System.out.println(".........................................");
							
						}
					
					}
				}
			}
		}
		if(sum ==0){
			System.out.println("不到三个");
		}
		else{
			double meetRate4_2 = meet4_2*1.0/sum;
			System.out.println("公式4_2三角不等式的满足情况："+meetRate4_2);
			double meetRate4_3 = meet4_3*1.0/sum;
			System.out.println("公式4_3三角不等式的满足情况："+meetRate4_3);
		}
//
//		
	}
	
	/**
	 * 计算公式4_2
	 */
	public boolean meet4_2(double sim1,double sim2,double sim3){
		double dis1 = 1-sim1;
		double dis2 = 1-sim2;
		double dis3 = 1-sim3;
		if((dis1+dis2>=dis3)&&(dis1+dis3>=dis2)&&(dis2+dis3>=dis1)){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 计算公式4_3
	 * 用-1表示无穷大
	 */
	public boolean meet4_3(double sim1,double sim2,double sim3){
		double dis1;
		if(sim1 == 0.0){
			dis1 = -1.0;
		}
		else{
			dis1 = 1/sim1-1.0;
		}
		double dis2;
		if(sim2 == 0.0){
			dis2 = -1.0;
		}
		else{
			dis2 = 1/sim2-1.0;
		}
		double dis3;
		if(sim3 == 0.0){
			dis3 = -1.0;
		}
		else{
			dis3 = 1/sim3-1.0;
		}
		if(((dis1 == -1.0)&&(dis2!=-1.0)&&(dis3!=-1.0))||((dis1 != -1.0)&&(dis2==-1.0)&&(dis3!=-1.0))||((dis1 != -1.0)&&(dis2!=-1.0)&&(dis3==-1.0))){
			System.out.println("dis[i][j]:"+dis1);
			System.out.println("dis[i][k]:"+dis2);
			System.out.println("dis[j][k]:"+dis3);
			return false;
		}
		else if(((dis1 == -1.0)&&(dis2==-1.0)&&(dis3!=-1.0))||((dis1 == -1.0)&&(dis2!=-1.0)&&(dis3==-1.0))||((dis1 != -1.0)&&(dis2==-1.0)&&(dis3==-1.0))){
			return true;
		}
		else if((dis1 == -1.0)&&(dis2==-1.0)&&(dis3==-1.0)){
			return true;
		}
		else if((dis1+dis2>=dis3)&&(dis1+dis3>=dis2)&&(dis2+dis3>=dis1)){
			return true;
		}
		else{
			System.out.println("dis[i][j]:"+dis1);
			System.out.println("dis[i][k]:"+dis2);
			System.out.println("dis[j][k]:"+dis3);
			return false;
		}
	}
	/**
	 * 计算两触发序列集合的矩阵，矩阵的值为触发序列的相似度
	 * @param seqSet1
	 * @param seqSet2
	 * @return
	 */
	public double[][] computeSeqMatrix(NewPtsSet seqSet1,NewPtsSet seqSet2){
		if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
			double[][] seqM = new double[seqSet1.getNPSet().size()][seqSet2.getNPSet().size()];
			for(int i = 0;i<seqSet1.getNPSet().size();i++){
				for(int j = 0;j<seqSet2.getNPSet().size();j++){
					seqM[i][j] = seqSet1.getNPSet().get(i).SequenceSimilarity(seqSet2.getNPSet().get(j));
				}
//				System.out.println(i);
			}
			return seqM;
		}
		else{
			double[][] seqM = new double[seqSet2.getNPSet().size()][seqSet1.getNPSet().size()];
			for(int i = 0;i<seqSet2.getNPSet().size();i++){
				for(int j = 0;j<seqSet1.getNPSet().size();j++){
					seqM[i][j] = seqSet2.getNPSet().get(i).SequenceSimilarity(seqSet1.getNPSet().get(j));
				}
			}
			return seqM;
		}
	}
	

	/**
	 * 给定两个模型序列集合，返回相似度的值，A*算法
	 * @param set1
	 * @param set2
	 * @return
	 */
	public double computeSimilarityForTwoNet_Astar(double[][] seqM,NewPtsSet seqSet1,NewPtsSet seqSet2){
		if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
			return seqSet1.setSimilarity_Astar(seqM,seqSet2);
		}
		else{
			return seqSet2.setSimilarity_Astar(seqM,seqSet1);
		}

	}
	
	/**
	 * 给定两个模型序列集合，返回相似度的值，遍历算法
	 * @param set1
	 * @param set2
	 * @return
	 */
//	public double computeSimilarityForTwoNet_ergodic(double[][] seqM,NewPtsSet seqSet1,NewPtsSet seqSet2){
//		if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
//			return seqSet1.setSimilarity_ergodic(seqSet2,seqM);
//		}
//		else{
//			return seqSet2.setSimilarity_ergodic(seqSet1,seqM);
//		}
//
//	}
	
	/**
	 * 给定两个模型序列集合，返回相似度的值，贪心算法
	 * @param set1
	 * @param set2
	 * @return
	 */
//	public double computeSimilarityForTwoNet_greedy(double[][] seqM,NewPtsSet seqSet1,NewPtsSet seqSet2){
//		if(seqSet1.getNPSet().size()<=seqSet2.getNPSet().size()){
//			return seqSet1.setSimilarity_greedy(seqSet2,seqM);
//		}
//		else{
//			return seqSet2.setSimilarity_greedy(seqSet1,seqM);
//		}
//
//	}
	/**
	 * 给定模型，返回对应的触发序列集合
	 * @param process
	 * @return
	 */
	public NewPtsSet computeSequenceSet(File process){
		if (process.isHidden()) {
			
		}

		PnmlImport pnmlimport = new PnmlImport();
		CTree ctree = null;
		PetriNet petrinet = null;
		FileInputStream pnml = null;
		try {
			pnml = new FileInputStream(process.getAbsolutePath());
			petrinet = pnmlimport.read(pnml);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CTreeGenerator generator = new CTreeGenerator(
				MyPetriNet.PromPN2MyPN(petrinet));
		ctree = generator.generateCTree();
		TTreeGenerator ttg = new TTreeGenerator();
		/**
		 * Input: a coverability tree cTree, loop times K Ouput: a trace tree tTree
		 */
		NewPtsSet nps = ttg.generatTTree(ctree, 2, process.getName());//循环次数
		nps.showSet(); 
		return nps;//循环次数

	}

}
