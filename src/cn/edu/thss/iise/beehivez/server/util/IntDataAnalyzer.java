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
package cn.edu.thss.iise.beehivez.server.util;

/**
 * @author Tao Jin
 * 
 */
public class IntDataAnalyzer {

	public static int getMax(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int result = Integer.MIN_VALUE;
		for (int i : data) {
			if (i > result) {
				result = i;
			}
		}
		return result;
	}

	public static int getMin(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int result = Integer.MAX_VALUE;
		for (int i : data) {
			if (i < result) {
				result = i;
			}
		}
		return result;
	}

	public static int getSum(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int sum = 0;
		for (int i : data) {
			sum += i;
		}
		return sum;
	}

	public static int getCount(int[] data) throws NoIntDataException {
		if (data == null) {
			throw new NoIntDataException();
		}
		return data.length;
	}

	public static float getAverage(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int count = data.length;
		float result = (float) getSum(data) / (float) count;
		return result;
	}

	public static int getSquareSum(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int result = 0;
		for (int i : data) {
			result += i * i;
		}
		return result;
	}

	public static float getVariance(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		int count = data.length;
		float squareSum = getSquareSum(data);
		float average = getAverage(data);
		float result = (squareSum - count * average * average) / count;
		return result;
	}

	public static float getStdev(int[] data) throws NoIntDataException {
		if (data == null || data.length < 1) {
			throw new NoIntDataException();
		}
		return (float) Math.sqrt(Math.abs(getVariance(data)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
