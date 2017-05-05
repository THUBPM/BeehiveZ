package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;

public class Resource 
{
	Vector<Staff> staffs;
	
	public Resource()
	{
		staffs = new Vector<Staff>();
	}
	
	
	/**
	 * 返回人员数量
	 * @return
	 */
	public int getstaffCount()
	{
		int result;
		if(staffs == null)
			result = 0;
		else
			result =staffs.size();
		return result;
	}
	
	/**
	 * 员工所有的capabilities数之和
	 * @return
	 */
	public int getCapabilityCount()
	{
		int result = 0;
		Staff temp;
		if(staffs != null)
		{
			int size = staffs.size();
			for(int i = 0; i < size; i++)
			{
				temp = staffs.get(i);
				result += temp.getCabilitySum();
			}
		}
		return result;
	}
	
	/**
	 * 所有员工的职位数之和
	 * @return
	 */
	public int getPostCount()
	{
		int result = 0;
		Staff temp;
		if(staffs != null)
		{
			int size = staffs.size();
			for(int i = 0; i < size; i++)
			{
				temp = staffs.get(i);
				result += temp.getPostSum();
			}
		}
		return result;
	}
	
	/**
	 * 读入资源文件
	 * @param path
	 */
	public void findResouce(String path)
	{
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(path),"UTF-8");
			LineNumberReader lbr = new LineNumberReader(
					isr);
							
			String s;
			String[] info;
			for(s = lbr.readLine(); s != null; s = lbr.readLine())
			{
				info = s.split(" ");
				int index = isInside(info[0]);
				if(index >= 0)
				{
					Staff temp = staffs.get(index);
					temp.addPost(info[1],Integer.parseInt(info[2]));
				}
				else
				{
					staffs.add(new Staff(info[0],info[1],Integer.parseInt(info[2])));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * 查看staffs中是否已经存在名字为name的员工（默认所有员工名字不同）
	 * @param name
	 * @return 如果有员工名字与name相同，则返回下标，否则返回-1
	 */
	private int isInside(String name)
	{
		int result = -1;
		if(staffs != null)
		{
			int size = staffs.size();
			for(int i = 0; i < size; i++)
			{
				if(staffs.get(i).name.equalsIgnoreCase(name))
				{
					result = i;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 返回地idx出的人员
	 * @param idx 下标
	 * @return 如果下标越界，则返回null，否则返回对应人员
	 */
	public Staff getStaffAt(int idx)
	{
		Staff result = null;
		if(idx >= 0 && idx < staffs.size())
		{
			result = staffs.elementAt(idx);
		}
		return result;
	}
}



