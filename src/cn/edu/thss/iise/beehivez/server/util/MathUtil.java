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
 * provide some functions used for math calculation
 */
package cn.edu.thss.iise.beehivez.server.util;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author Tao Jin
 * 
 */
public class MathUtil {

	public static String UNIFORMDISTRIBUTION = "uniform";
	public static String BINOMIALDISTRIBUTION = "binomial";

	// clone two dimensional array
	public static boolean[][] twoDimensionalArrayClone(boolean[][] a) {
		boolean[][] b = new boolean[a.length][];
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i].clone();
		}
		return b;
	}

	public static long[][] getDistribution(String distribution, int min,
			int max, long total) {
		if (distribution.equals(UNIFORMDISTRIBUTION)) {
			return getDiscreteUniformDistribution(min, max, total);
		} else if (distribution.equals(BINOMIALDISTRIBUTION)) {
			return getBinomialDistribution(min, max, total);
		} else {
			return null;
		}
	}

	// calculate the binomial distribution
	// ret[0] stores the number user given
	// ret[1] stores the count of the corresponding number in ret[0] with the
	// same index
	// for some number, the count maybe 0.
	// the parameter validation must be finished in advance
	public static long[][] getBinomialDistribution(int min, int max, long total) {
		Random rand = new Random(System.currentTimeMillis());
		int n = max - min;
		long[][] ret = new long[2][n + 1];
		int mean = (n + 1) / 2;
		float p = 1;
		if (n > 0) {
			p = (float) mean / (float) n;
		}

		long count = 0;
		for (int i = 0; i <= n; i++) {
			double p_i = MathUtil.combination(n, i) * Math.pow(p, i)
					* Math.pow((1 - p), (n - i));
			long count_i = (long) (total * p_i);
			ret[0][i] = i + min;
			ret[1][i] = count_i;
			count += count_i;
		}

		while (count < total) {
			int i = rand.nextInt(n + 1);
			ret[1][i]++;
			count++;
		}

		return ret;
	}

	public static boolean satisfyTriIneq(double x, double y, double z) {
		// convert sim to distance
		x = 1 - x;
		y = 1 - y;
		z = 1 - z;

		if (x == 0 || y == 0 || z == 0)
			return true;

		if (x + y > z && x + z > y && y + z > x)
			return true;

		return false;
	}

	// calculate the discrete uniform distribution
	// ret[0] stores the number user given
	// ret[1] stores the count of the corresponding number in ret[0] with the
	// same index
	// for some number, the count maybe 0.
	// the parameter validation must be finished in advance.
	public static long[][] getDiscreteUniformDistribution(int min, int max,
			long total) {
		Random rand = new Random(System.currentTimeMillis());
		int span = max - min + 1;
		long avg = total / span;
		long[][] ret = new long[2][span];

		if (avg >= 1) {
			long count = 0;
			for (int i = 0; i < span; i++) {
				ret[0][i] = min + i;
				ret[1][i] = avg;
				count += avg;
			}

			if (count < total) {
				avg = span / (total - count);
				int a = (int) avg;
				int k = 0;
				while (count < total) {
					int i = rand.nextInt(span);
					ret[1][a * k]++;
					k++;
					count++;
				}
			}
		} else {
			for (int i = 0; i < span; i++) {
				ret[0][i] = min + i;
			}
			long count = 0;
			avg = span / total;
			while (count < total) {
				long k = count * avg;
				int kk = (int) k;
				ret[1][kk] = 1;
				count++;
				// int i = rand.nextInt(span);
				// if (ret[1][i] == 0) {
				// ret[1][i] = 1;
				// count++;
				// }
			}
		}
		return ret;
	}

	// calculate the permutation
	public static BigInteger permutation(int n, int k) {
		BigInteger ret = new BigInteger(String.valueOf(1));
		while (k > 0) {
			ret = ret.multiply(new BigInteger(String.valueOf(n)));
			n--;
			k--;
		}
		return ret;
	}

	// calculate the combination
	// the value would be very large, so store it in the type of double
	public static double combination(int n, int k) {
		double ret = 1;
		while (k > 0) {
			ret = ret * ((double) n / (double) k);
			k--;
			n--;
		}
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// boolean[][] a = {{true, false},{true, false}};
		// boolean[][] b = MathUtil.twoDimensionalArrayClone(a);
		// b[0][0] = false;
		// b[1][1] = true;
		//		
		// System.out.println("a");
		// for (int i = 0; i < 2; i++) {
		// for (int j = 0; j < 2; j++) {
		// System.out.print(a[i][j]);
		// System.out.print(" ");
		// }
		// }
		// System.out.println();
		// System.out.println("b");
		// for (int i = 0; i < 2; i++) {
		// for (int j = 0; j < 2; j++) {
		// System.out.print(b[i][j]);
		// System.out.print(" ");
		// }
		// }

		int min = 1;
		int max = 100;
		int total = 10;
		long[][] dis = getDiscreteUniformDistribution(min, max, total);
		long count = 0;
		for (int i = 0; i < dis[1].length; i++) {
			count += dis[1][i];
			if (dis[1][i] > 0) {
				System.out.println(dis[0][i] + " ---- " + dis[1][i]);
			}
		}
		System.out.println("the count is: " + count);

	}

}
