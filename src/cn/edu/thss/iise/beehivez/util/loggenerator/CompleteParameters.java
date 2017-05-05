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
package cn.edu.thss.iise.beehivez.util.loggenerator;
/**
 * 
 * @author Nianhua Wu
 * 
 * @date 2011-3-10
 *
 */
public class CompleteParameters {
	
	public static int TarCompleteness = 1;
	public static int cauCompleteness = 2;
	public static int freCompleteness = 3;
	private int completeType ;
	private double completeDegree;
	private int parameter[]= {1,0};
	public CompleteParameters()
	{
		 completeType = TarCompleteness ;
		 completeDegree = 1.0;
	}
	
	public void setCompleteType(int x)
	{
		completeType = x;
	}
	public void setCompleteDegree(double x)
	{
		completeDegree = x;
	}
	public void setParameter(int[]x)
	{
		for(int i =0;i<x.length;i++)
		{
			parameter[i]=x[i];
		}
	}
	public int getCompleteType()
	{
		return completeType;
	}
	public double getCompleteDegree()
	{
		return completeDegree;
	}
	public int[] getParameter()
	{
		return parameter;
	}

}
