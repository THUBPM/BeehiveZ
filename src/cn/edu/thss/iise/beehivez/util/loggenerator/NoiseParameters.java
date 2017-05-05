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
public class NoiseParameters {
	public static int noNoise = 1;
	public static int haveNoise = 2;
	private int noiseType;
	private double noiseDegree ;
	private int parameter[] = {1,1,1,1,1};
	public NoiseParameters()
	{
		noiseType = haveNoise;
		noiseDegree = 0.0;
	}
	
	public void setNoiseType(int type)
	{
		noiseType = type;
	}
	public void setNoiseDegree(double percent)
	{
		noiseDegree = percent;
	}
	public void setParameter(int[]para)
	{
		for(int i = 0;i<para.length;i++)
		{
			parameter[i] = para[i];
		}
	}
	public int getNoiseType()
	{
		return noiseType;
	}
	public double getNoiseDegree()
	{
		return noiseDegree;
	}
	public int[] getParameter()
	{
		return parameter;
	}

}
