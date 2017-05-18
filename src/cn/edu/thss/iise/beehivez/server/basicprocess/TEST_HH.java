package cn.edu.thss.iise.beehivez.server.basicprocess;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Comparator;

public class TEST_HH {

	public static void main(String[] args) {
		double k1 = (0.9528*115+0.7216*124+0.9808*592+0.6911*200)/(115+124+592+200);
		double k2 = (0.955*115+0.7269*124+0.9802*592+0.6914*200)/(115+124+592+200);
		double k3 = (0.9549*115+0.7416*124+0.9799*592+0.6912*200)/(115+124+592+200);
		double k4 = (0.9557*115+0.7593*124+0.9799*592+0.6914*200)/(115+124+592+200);

		System.out.println("k1:"+k1);
		System.out.println("k2:"+k2);
		System.out.println("k3:"+k3);
		System.out.println("k4:"+k4);
		//		// TODO Auto-generated method stub
//		PriorityQueue<Double> heap = new PriorityQueue<Double>(10, 
//			new Comparator<Double>(){
//				@Override
//				public int compare(Double d1, Double d2){
//					return 0;
//				}
//		
//			}
//		);
//
//		
//		heap.add(3.);
//		heap.add(1.);
//		heap.add(3.);
//		heap.add(2.);
//		
//		System.out.println(heap.size() + " ");
//		
//		heap.add(3.);
//		heap.add(5.);
//		heap.add(7.);
//		
//		while(!heap.isEmpty())
//			System.out.print(heap.poll() + " ");

	}

}
