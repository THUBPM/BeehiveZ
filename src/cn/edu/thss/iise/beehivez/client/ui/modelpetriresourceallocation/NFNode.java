package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

import java.util.Vector;

/**
 * 表示网络流中的一个节点
 * 
 * @author 唐旺
 *
 */
public class NFNode 
{
	private String label; //抽象意义上的”名字“
	private int type;     //节点原来的类型：0表示人员，1表示角色、2表示任务
	static int id_global = 0; //用于区分不同的节点
	private int id_this;
	//private Vector<NFEdge> edgesIn;  //入边
	//private Vector<NFEdge> edgesOut; //出边
	/**
	 * 构造一个网络流节点
	 * @param l 标签(名字)
	 * @param t 原来类型(人员：0；角色：1；任务：2)
	 */
	public NFNode(String l, int t)
	{
		label = l;
		type = t;
		id_this = id_global;
		id_global++;
		//edgesIn = new Vector<NFEdge>();
		//edgesOut = new Vector<NFEdge>();
	}
	
	
	
	/**
	 * 
	 * @return 该节点的标签
	 */
	public String getLabel()
	{
		return label;
	}
	
	/**
	 * @return 该节点原来的类型:
	 * 0：人员
	 * 1：角色
	 * 2: 任务
	 * 
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * 
	 * @return 该节点的id
	 */
	public int getID()
	{
		return id_this;
	}
	
}
