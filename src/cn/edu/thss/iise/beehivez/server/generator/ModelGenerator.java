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
package cn.edu.thss.iise.beehivez.server.generator;

import java.util.Random;

/**
 * Business process model generator
 * 
 * @author Tao Jin
 * 
 */
public abstract class ModelGenerator {
	protected Random rand = new Random(System.currentTimeMillis());

	/**
	 * 
	 * @param minNumberOfTasks
	 * @param maxNumberOfTasks
	 * @param maxDegree
	 * @param maxNameLength
	 * @return
	 */
	public abstract Object generateModel(int minNumberOfTasks,
			int maxNumberOfTasks, int maxDegree, int maxNameLength);

	public abstract boolean supportDegreeConfiguration();

	/**
	 * generate a string randomly, the length of the result string is random.
	 * 
	 * @param maxLength
	 * @return
	 */
	protected String getRandomString(int maxLength) {
		if (maxLength < 1) {
			maxLength = 1;
		}
		StringBuffer buffer = new StringBuffer(
				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		int range = buffer.length();
		int len = rand.nextInt(maxLength) + 1;
		for (int i = 0; i < len; i++) {
			sb.append(buffer.charAt(rand.nextInt(range)));
		}
		return sb.toString().trim();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
