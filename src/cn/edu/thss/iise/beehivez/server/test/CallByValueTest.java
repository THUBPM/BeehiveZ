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
 * @date 2012-3-6
 * 
 */
public class CallByValueTest {

	private static void change(int x) {
		x = 2;
	}

	private static void change(Integer x) {
		x = 2;
	}

	private static void change(int[] x) {
		x[0] = 2;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int x = 1;
		System.out.println("before call change int, x = " + x);
		change(x);
		System.out.println("after call change int, x = " + x);

		Integer y = 1;
		System.out.println("before call change Integer, y = " + y);
		change(y);
		System.out.println("after call change Integer, y = " + y);

		int[] z = new int[1];
		z[0] = 1;
		System.out.println("before call change int[], z[0] = " + z[0]);
		change(z);
		System.out.println("after call change int[], z[0] = " + z[0]);

	}

}
