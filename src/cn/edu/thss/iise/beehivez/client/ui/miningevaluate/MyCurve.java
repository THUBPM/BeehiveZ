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
package cn.edu.thss.iise.beehivez.client.ui.miningevaluate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MyCurve extends JPanel {
	private final int WIDTH = 500;
	private final int HEIGHT = 170;
	private final int SIZE = 40;
	private String xTitle;
	private String yTitle;
	private  ArrayList<Float> f = new ArrayList<Float>();
	private  ArrayList<Float> aB = new ArrayList<Float>();
	private  ArrayList<Float> aS = new ArrayList<Float>();
	private  ArrayList<Float> sim = new ArrayList<Float>();
	private  ArrayList<Float> strsim = new ArrayList<Float>();
	
	public ArrayList<Float> getStrsim() {
		return strsim;
	}

	public void setStrsim(ArrayList<Float> strsim) {
		this.strsim = strsim;
	}

	public ArrayList<Float> getSim() {
		return sim;
	}

	public void setSim(ArrayList<Float> sim) {
		this.sim = sim;
	}

	public ArrayList<Float> getF() {
		return f;
	}

	public void setF(ArrayList<Float> f) {
		this.f = f;
	}

	public ArrayList<Float> getaB() {
		return aB;
	}

	public void setaB(ArrayList<Float> aB) {
		this.aB = aB;
	}

	public ArrayList<Float> getaS() {
		return aS;
	}

	public void setaS(ArrayList<Float> aS) {
		this.aS = aS;
	}

	public MyCurve() {
		this.xTitle = "";
		this.yTitle = "";
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, WIDTH, HEIGHT);
		drawHistogram(g);
	}

	// ����״ͼ
	public void setHistogramTitle(String y, String x) {
		xTitle = x;
		yTitle = y;
	}

	public void drawHistogram(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("e��", Font.PLAIN, 15));
		// g.drawString("��״ͼ", SIZE + 50, 30);
		g.setColor(Color.BLACK);
		g.setFont(new Font("e��", Font.PLAIN, 10));
		// ����
		g.drawLine(SIZE, HEIGHT - SIZE, SIZE, SIZE);
		// ����
		g.drawLine(SIZE, HEIGHT - SIZE, WIDTH - SIZE, HEIGHT - SIZE);
		// �����
		g.drawLine(SIZE, HEIGHT - SIZE, SIZE + 1, HEIGHT - SIZE);
		g.drawString("0.0", SIZE - 30, HEIGHT - SIZE);
		g.drawLine(SIZE, HEIGHT - SIZE - 15, SIZE + 1, HEIGHT - SIZE - 15);
		g.drawString("0.2", SIZE - 30, HEIGHT - SIZE - 15);
		g.drawLine(SIZE, HEIGHT - SIZE - 30, SIZE + 1, HEIGHT - SIZE - 30);
		g.drawString("0.4", SIZE - 30, HEIGHT - SIZE - 30);
		g.drawLine(SIZE, HEIGHT - SIZE - 45, SIZE + 1, HEIGHT - SIZE - 45);
		g.drawString("0.6", SIZE - 30, HEIGHT - SIZE - 45);
		g.drawLine(SIZE, HEIGHT - SIZE - 60, SIZE + 1, HEIGHT - SIZE - 60);
		g.drawString("0.8", SIZE - 30, HEIGHT - SIZE - 60);
		g.drawLine(SIZE, HEIGHT - SIZE - 75, SIZE + 1, HEIGHT - SIZE - 75);
		g.drawString("1.0", SIZE - 30, HEIGHT - SIZE - 75);

		// ��ͷ
		g.setColor(Color.BLACK);
		int[] x1 = { SIZE - 6, SIZE, SIZE + 6 };
		int[] y1 = { SIZE + 8, SIZE, SIZE + 8 };
		g.drawPolyline(x1, y1, 3);
		int[] x2 = { WIDTH - SIZE - 8, WIDTH - SIZE, WIDTH - SIZE - 8 };
		int[] y2 = { HEIGHT - SIZE - 6, HEIGHT - SIZE, HEIGHT - SIZE + 6 };
		g.drawPolyline(x2, y2, 3);
		// title
		g.setFont(new Font("e��", Font.PLAIN, 15));
		g.drawString(this.yTitle, SIZE - 30, SIZE - 20);
		g.drawString(this.xTitle, WIDTH - SIZE - 30, HEIGHT - SIZE + 20);
		
		int width=0;
		if(sim.size()>0){
			width = (int) ((WIDTH - 3 * SIZE) / (sim.size() * 2 + 1));
		}else{
			width = (int) ((WIDTH - 3 * SIZE) / (f.size() * 2 + 1));
		}
				
		int simlength = sim.size();
		int[] y = new int[simlength];
		int[] x = new int[simlength];
		g.setColor(Color.GREEN);
		for(int i=0;i<simlength;i++){
			y[i]=(int)(130-sim.get(i)/0.2*15);
			x[i]=width * (i * 2 + 1) + SIZE;	
			g.fillRect(x[i], y[i]-2, 4, 4);
		}
		g.drawPolyline(x,y,simlength);
		
		int[] sy = new int[simlength];
		int[] sx = new int[simlength];
		g.setColor(Color.DARK_GRAY);
		for(int i=0;i<simlength;i++){
			sy[i]=(int)(130-strsim.get(i)/0.2*15);
			sx[i]=width * (i * 2 + 1) + SIZE;	
			g.fillRect(sx[i], sy[i]-2, 4, 4);
		}
		g.drawPolyline(sx,sy,simlength);
		
		int flength = f.size();
		int[] y3 = new int[flength];
		int[] x3 = new int[flength];
		g.setColor(Color.RED);
		for(int i=0;i<flength;i++){
			y3[i]=(int)(130-f.get(i)/0.2*15);
			x3[i]=width * (i * 2 + 1) + SIZE;
			//g.fillRect(x3[i], y3[i]-2, 4, 4);
			//绘制三角形
			int[] xx = new int[3];
			int[] yy = new int[3];
			xx[0]=x3[i]+2;
			yy[0]= y3[i]-2-1;
			xx[1]=x3[i]-1;
			yy[1]= y3[i]-2+4+1;
			xx[2]=x3[i]+4+1;
			yy[2]= y3[i]-2+4+1;
			g.fillPolygon(xx, yy, 3);
		}
		g.drawPolyline(x3,y3,flength);
		
		int ablength = aB.size();
		int[] y4 = new int[ablength];
		int[] x4 = new int[ablength];
		g.setColor(Color.BLUE);
		for(int i=0;i<ablength;i++){
			y4[i]=(int)(130-aB.get(i)/0.2*15);
			x4[i]=width * (i * 2 + 1) + SIZE;	
			//g.fillRect(x4[i], y4[i]-2, 4, 4);
			g.fillRoundRect(x4[i], y4[i]-2, 5, 5, 2, 2);
		}
		g.drawPolyline(x4,y4,ablength);
		
		int aslength = aS.size();
		int[] y5 = new int[aslength];
		int[] x5 = new int[aslength];
		g.setColor(Color.ORANGE);
		for(int i=0;i<aslength;i++){
			y5[i]=(int)(130-aS.get(i)/0.2*15);
			x5[i]=width * (i * 2 + 1) + SIZE;	
			//g.fillRect(x5[i], y5[i]-2, 4, 4);
			g.drawLine(x5[i], y5[i], x5[i], y5[i]+2);
			g.drawLine(x5[i], y5[i], x5[i]-2, y5[i]+2);
			g.drawLine(x5[i], y5[i], x5[i]+2, y5[i]+2);
		}
		g.drawPolyline(x5,y5,aslength);
		
		//标注
		g.setColor(Color.GREEN);
		g.drawLine(420, 15, 435, 15);
		g.fillRect(427, 13, 4, 4);
		
		g.setColor(Color.RED);
		g.drawLine(420, 30, 435, 30);
		int[] a = new int[3];
		int[] b = new int[3];
		a[0]=429;
		b[0]= 27;
		a[1]=427;
		b[1]= 33;
		a[2]=432;
		b[2]= 33;
		g.fillPolygon(a, b, 3);
		
		g.setColor(Color.BLUE);
		g.drawLine(420, 45, 435, 45);
		g.fillRoundRect(427, 43, 4, 4,2,2);
		
		g.setColor(Color.ORANGE);
		g.drawLine(420, 60, 435, 60);
		g.drawLine(427, 60, 427, 58);
		g.drawLine(427, 60, 425, 62);
		g.drawLine(427, 60, 429, 62);
		
		g.setColor(Color.DARK_GRAY);
		g.drawLine(420, 75, 435, 75);
		g.fillRect(427, 73, 4, 4);
		
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("e��", Font.PLAIN, 11));
		g.drawString("behaSim", 437, 15);
		g.drawString("f", 437, 30);
		g.drawString("aB", 437, 45);
		g.drawString("aS", 437, 60);
		g.drawString("struSim", 437, 75);
		
		

	}
}

