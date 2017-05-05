package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

import java.util.List;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.MaxFlow.Flow;

/**
 * 网络流图
 * @author 唐旺
 *
 */
public class NetworkFlowModel 
{
	boolean isAddedSrcTgt = false;
	/**
	 * 网络流图中的节点
	 */
	Vector<NFNode> nodes;	
	/**
	 * 网络流途中的边
	 */
	Vector<NFEdge> edges;
	
	public NetworkFlowModel()
	{
		//resource = r;
		nodes = new Vector<NFNode>();
		edges = new Vector<NFEdge>();
	}
	
	/**
	 * 添加一条新的边
	 * @param ne 网络流图中的边
	 */	
	public void addEdge(NFEdge ne)
	{
		edges.add(ne);
	}
	
	/**
	 * 添加一个新节点
	 * @param nn 网络流图中的节点
	 */
	public void addNode(NFNode nn)
	{
		nodes.add(nn);
	}
	
	/**
	 * 返回第idx条边
	 * @param idx 下标
	 * @return 如果下标小于0 或者大于等于size，则返回null；否则返回对应的边
	 */
	public NFEdge getEdgeAt(int idx)
	{
		NFEdge result = null;
		if(idx >= 0 && idx < edges.size())
		{
			result = edges.get(idx);
		}
		return result;
	}
	
	/**
	 * 返回地idx个节点
	 * @param idx 下标
	 * @return 如果下标小于0 或者大于等于size，则返回null；否则返回对应的节点
	 */
	public NFNode getNodeAt(int idx)
	{
		NFNode result = null;
		if(idx >= 0 && idx < nodes.size())
		{
			result = nodes.get(idx);
		}
		return result;
	}
	
	/**
	 * 处理由于资源更新而导致的网络流图的更新，当前只有添加没有删除
	 * @param resource 资源
	 */
	public void updateDataByResource(Resource resource)
	{		
		int staffNum = resource.getstaffCount();
		Staff staff;
		for(int i = 0; i < staffNum; i++)
		{
			Staff2NF(resource.getStaffAt(i));			
		}
	}
	
	/**
	 * 输入一个人员对象，添加响应的人员节点、角色节点、边
	 * @param staff
	 */
	private void Staff2NF(Staff staff)
	{
		//人员节点
		NFNode sNode = isInNodes(staff.name,0);
		if(sNode == null)
		{
			NFNode node = new NFNode(staff.name,0);
			nodes.add(node);
			sNode = node;
		}
		//角色节点
		int postSize = staff.getPostSum();
		String postName;
		NFNode pNode;
		for(int i = 0; i < postSize; i++)
		{
			postName = staff.getPost(i);
			pNode = isInNodes(postName,1);
			if(pNode == null)
			{
				NFNode node = new NFNode(postName,1);
				nodes.add(node);
				pNode = node;
			}
			NFEdge edge = new NFEdge(sNode,pNode,staff.getCapability(i));
			edges.add(edge);
		}	
	}
	
	/**
	 * 检查是否已经存在了这个节点
	 * @param label 名称
	 * @param type 类型
	 * @return 如果已经存在，则返回该节点；否则返回null
	 */
	NFNode isInNodes(String label,int type)
	{
		NFNode result = null;
		int size = nodes.size();
		NFNode temp;
		for(int i = 0; i < size; i++)
		{
			temp = nodes.get(i);
			if(temp.getLabel().equalsIgnoreCase(label) && temp.getType() == type)
			{
				result = temp;
				break;
			}
		}
		return result;
	}
	
	/**
	 * TODO
	 * 计算最大流
	 */
	public void getMaxFlow()
	{
		MaxFlow maxFlow;
		
		int nodeNum = this.nodes.size();
		int[][] capacity = new int[nodeNum][];
		for (int i=0; i<nodeNum; i++)
			capacity[i] = new int[nodeNum];		
		for (int i=0; i<edges.size(); i++)
		{
			NFEdge edge = edges.get(i);
			NFNode src; 
			NFNode tgt;
			int cap;
			src = edge.getSource();
			tgt = edge.getTarget();
			cap = edge.getCapacity();
			int srcPos, tgtPos;
			srcPos = nodes.indexOf(src);
			tgtPos = nodes.indexOf(tgt);
			capacity[srcPos][ tgtPos] = cap;			
		}
		maxFlow = new MaxFlow(capacity,nodeNum);
		int src1 = 0;
		int tgt1 = 0;
		for (int i=0; i<nodes.size(); i++)
		{
			if (nodes.get(i).getType() == -1)
				src1 = i;
			if (nodes.get(i).getType() == 3)
				tgt1 = i;
		}
		maxFlow.maxFlow(src1, tgt1);
		List<Flow> flows = maxFlow.getFlowList();
		for (int i=0; i<flows.size(); i++)
		{
			NFEdge edge;
			int src, tgt, flow;
			src = flows.get(i).src;
			tgt = flows.get(i).tgt;
			flow = flows.get(i).Flow;
			edge = getEdgeByVerticeIndex(src, tgt);
			edge.setFlow(flow);
		}
	}

	private NFEdge getEdgeByVerticeIndex(int src1, int tgt1) {
		for (NFEdge edge	:	edges)
		{
			NFNode src = edge.getSource();
			NFNode tgt = edge.getTarget();
			int srcPos = nodes.indexOf(src);
			int tgtPos = nodes.indexOf(tgt);
			if (srcPos == src1 && tgtPos == tgt1)
				return edge;			
		}
		return null;
	}

	public void addSrcTgt() {
		// TODO Auto-generated method stub
		if (!isAddedSrcTgt)
		{
			isAddedSrcTgt = true;
			NFNode src = new NFNode("SRC", -1);
			NFNode tgt = new NFNode("TGT", 3);
			//this.addNode(src);
			this.nodes.add(0, src);
			this.addNode(tgt);
			
			//src:
			for (int i=0; i<nodes.size(); i++)
			{
				if (nodes.get(i).getType() == 0)
				{
					int capSum;
					capSum = getCapacitySum(nodes.get(i));
					NFEdge edge = new NFEdge(src, nodes.get(i), capSum);
					this.addEdge(edge);
				}								
			}
			//tgt
			for (int i=0; i<nodes.size(); i++)
			{
				if (nodes.get(i).getType() == 2)
				{
					NFEdge edge = new NFEdge(nodes.get(i), tgt, 1);
					this.addEdge(edge);
				}
			}
		}
	}

	private int getCapacitySum(NFNode nfNode) {
		int sum = 0;
		for (int i=0; i<edges.size(); i++)
			if (edges.get(i).srcNode.equals(nfNode))
				sum += edges.get(i).capacity;
		return sum;
	}
	
	/**
	 * 请空所有数据
	 */
	public void clearData()
	{
		this.nodes.clear();
		this.edges.clear();
	}
	
	
}
