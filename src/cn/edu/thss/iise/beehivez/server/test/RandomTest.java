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

/**
 * used to test the random function
 */
package cn.edu.thss.iise.beehivez.server.test;

import java.util.Random;

/**
 * @author Tao Jin
 * 
 */
public class RandomTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rand = new Random(System.currentTimeMillis());
		int n = 10;
		int[] t = new int[n];
		int count = 20;
		for (int i = 0; i < count; i++) {
			t[rand.nextInt(n)]++;
		}

		for (int i = 0; i < n; i++) {
			System.out.println(i + " --- " + t[i]);
		}

	}

}
