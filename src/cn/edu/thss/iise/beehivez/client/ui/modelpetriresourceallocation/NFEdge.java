package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

/**
 * 表示网络流图中的一条边
 * @author 唐旺
 *
 */
public class NFEdge {
	/**
	 * 边的起点
	 */
	NFNode srcNode;
	/**
	 * 变得终点
	 */
	NFNode tgtNode;
	/**
	 * 边的容量
	 */
	int capacity;
	/**
	 * 边上的实际流量
	 */
	int flow;
	
	/**
	 * 构造一条边
	 * @param src 源节点
	 * @param tgt 目标节点
	 * @param c   边的容量
	 */
	public NFEdge(NFNode src, NFNode tgt, int c)
	{
		srcNode = src;
		tgtNode = tgt;
		capacity = c;
		flow = 0;
	}
	
	/**
	 * @return 当前边的实际流量
	 */
	public int getFlow()
	{
		return flow;
	}
	
	/**
	 * 
	 * @param f 流量
	 */
	public void setFlow(int f)
	{
		flow = f;
	}
	
	/**
	 * 
	 * @return 边的容量
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * 
	 * @return 边的起点
	 */
	public NFNode getSource()
	{
		return srcNode;
	}
	
	/**
	 * 
	 * @return 边的终点
	 */
	public NFNode getTarget()
	{
		return tgtNode;
	}
	
}

