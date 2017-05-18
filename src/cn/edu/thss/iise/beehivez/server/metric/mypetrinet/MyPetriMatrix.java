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

package cn.edu.thss.iise.beehivez.server.metric.mypetrinet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class MyPetriMatrix {

	private int[][] A;

	int m, n;
	
	public boolean matrixChanged;
	
	public MyPetriMatrix(){
		matrixChanged = true;
	}
	
	public MyPetriMatrix(int m, int n){
		this();
		this.m = m;
		this.n = n;
		A = new int[m][n];
	}
	
	public MyPetriMatrix(MyPetriMatrix B){
	      this();
	      this.m = B.m;
	      this.n = B.n;
	      A = B.A.clone();
	}
	
	public MyPetriMatrix(int[][] A){
	      this();
	      if (A != null) {
	         m = A.length;
	         if (A.length >= 1) {
	            n = A[0].length;
	            for (int i = 0; i < m; i++) {
	               if (A[i].length != n) {
	                  throw new IllegalArgumentException(
	                           "All rows must have the same length.");
	               }
	            }
	            this.A = A;
	         }
	      }
	}
	
	public MyPetriMatrix(int[] vals, int m){
	      this();
	      this.m = m;
	      n = (m != 0 ? vals.length/m : 0);
	      if (m*n != vals.length) {
	         throw new IllegalArgumentException("Array length must be a multiple of m.");
	      }
	      A = new int[m][n];
	      for (int i = 0; i < m; i++) {
	         for (int j = 0; j < n; j++) {
	            A[i][j] = vals[i+j*m];
	         }
	      }
	}
	
	public void set(int i, int j, int s){
		A[i][j] = s;
	}
	
   public int get(int i, int j) {
	      return A[i][j];
	   }
	
   public int[][] getArrayCopy() {
	      int[][] C = new int[m][n];
	      for (int i = 0; i < m; i++) {
	         for (int j = 0; j < n; j++) {
	            C[i][j] = A[i][j];
	         }
	      }
	      return C;
	   }
   
   /**
    * Get row dimension.
    * @return The number of rows.
    */
   public int getRowDimension() {
      return m;
   }

   
   /**
    * Get column dimension.
    * @return The number of columns.
    */
   public int getColumnDimension() {
      return n;
   }
}
