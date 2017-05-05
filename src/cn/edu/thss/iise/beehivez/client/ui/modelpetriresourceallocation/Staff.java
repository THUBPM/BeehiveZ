package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

import java.util.Vector;

public class Staff
{
	String name;
	Vector<String> posts;
	Vector<Integer> capabilities;	
	
	/**
	 * 构造一个人员
	 * @param n 人员名字
	 * @param p 角色
	 * @param c 角色能力
	 */
	public Staff(String n, String p, int c)
	{
		this.name = n;
		posts = new Vector<String>();
		capabilities = new Vector<Integer>();
		posts.add(p);
		capabilities.add(c);
	}
	
	/**
	 * 添加角色
	 * @param p 角色
	 * @param c 角色能力
	 */
	public void addPost(String p, int c)
	{
		int count = posts.size();		
		if(posts.contains(p))
		{
			int index = posts.indexOf(p);
			int initialC = capabilities.elementAt(index);
			capabilities.setElementAt(initialC+c, index);
		}		
		else
		{
			posts.add(p);
			capabilities.add(c);
		}
	}
	
	/**
	 * 返回该人员的第index个角色
	 * @param index 下标
	 * @return 如果下标小于0或大于等于角色类型个数，则返回下标越界；否则返回对应角色名称
	 */
	public String getPost(int index)
	{		
		String result;
		if(index < 0 || index >= posts.size())
		{
			result = "下标越界";
		}
		else
		{
			result = posts.elementAt(index);
		}
		return result;
	}
	
	/**
	 * 返回人员第index个角色的能力
	 * @param index 下标
	 * @return 如果下标越界，则返回0；否则返回对应的能力
	 */
	public int getCapability(int index)
	{
		int result = 0;
		if(index < 0 || index >= posts.size())
		{
			result = 0;
		}
		else
		{
			result = capabilities.elementAt(index);
		}
		return result;
	}
	
	/**
	 * 
	 * @return 该员工所有能力之和
	 */
	public int getCabilitySum()
	{
		int result = 0;
		int size = capabilities.size();
		for(int i = 0; i< size; i++)
		{
			result += capabilities.get(i);
		}
		return result;
	}
	
	/**
	 * 
	 * @return 该员工的职位数量
	 */
	public int getPostSum()
	{
		int result = 0;
		if(posts != null)
		{
			result = posts.size();
		}
		return result;
	}
	
}