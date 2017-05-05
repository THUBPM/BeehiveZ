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
 */
public class GotoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int n = 0;
		int k = 0;
		loop0: while (true) {
			loop1: while (n < 5) {
				System.out.println(n);
				n++;
			}
			k++;
			if (k < 2) {
				n = 0;
				continue loop0;
			} else {
				break loop0;
			}
		}

		// outer: for (int i = 0; i < 10; i++) {
		// System.out.println("outer: " + i);
		// inter: for (int j = 0; j < 10; j++) {
		// if (j > 5) {
		// continue outer;
		// }
		// if (i > 7) {
		// break outer;
		// }
		// System.out.print("inter: " + j);
		// }
		// }
		// int j = 9;
		// int n = 0;
		// validate: {
		// if (j == 8) {
		// System.out.println("j==8");
		// break validate;
		// }
		// if (j == 9) {
		// System.out.println("j==9");
		// break validate;
		// }
		// }
	}

}
