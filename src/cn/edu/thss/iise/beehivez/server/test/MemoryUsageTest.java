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
package cn.edu.thss.iise.beehivez.server.test;

/**
 * @author Tao Jin
 * 
 *         used for memory usage test
 * 
 */
public class MemoryUsageTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long mem0 = 0, mem1 = 0;
		mem0 = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("memory used: " + mem0 + " bytes");

		mem1 = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("memory used: " + mem1 + " bytes");

		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		mem0 = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("memory used: " + mem0 + " bytes");

		long[] a = new long[1000];
		long[] b = new long[1000];

		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		mem1 = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("memory used: " + mem1 + " bytes");

		System.out.println("memory used: " + (mem1 - mem0) + " bytes");
	}

}
