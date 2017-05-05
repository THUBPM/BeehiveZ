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

package cn.edu.thss.iise.beehivez.server.basicprocess.mymodel;
/**
 * incidence matrix of petri net
 * the rows of matrix express the places and the columns of matrix
 * express the transitions
 * @author He tengfei
 *
 */
public class IncidenceMatrix {
	private int rows;
	private int columns;
	private int matrix[][];
	public IncidenceMatrix(int[][] matrix) {
		rows = matrix.length;
		columns = matrix[0].length;
		this.matrix = new int[rows][columns];
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				this.matrix[i][j] = matrix[i][j];
			}
		}
	}
	public int[] multiplyX(int[] x) {
		// TODO Auto-generated method stub
		int cx[] = new int[matrix.length];
		for(int i=0;i<matrix.length;i++){
			int sum = 0;
			for(int j=0;j<matrix[0].length;j++){
				sum += matrix[i][j]*x[j];
			}
			cx[i] = sum;
		}
		return cx;
	}
}
