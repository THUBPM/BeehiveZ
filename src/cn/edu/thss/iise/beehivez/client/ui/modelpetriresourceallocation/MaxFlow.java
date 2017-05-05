package cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;  
import java.util.List;
import java.util.Queue;  
import java.util.Scanner;  

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.mining.petrinetmining.PetriNetResult;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;
  
public class MaxFlow  
{  
  
    private int capacity[][];  
    private int flow[][];  
    private boolean visited[];  
    private int pre[];  
    private int nodes;  
  
    public MaxFlow( int[][] capacity, int nodes )  
    {  
        this.capacity = capacity;  
        this.nodes = nodes;  
        this.flow = new int[nodes][nodes];  
        this.pre = new int[nodes];  
        this.visited = new boolean[nodes];  
    }  
  
    public int maxFlow( int src, int des )  
    {  
        int maxFlow = 0;  
          
        for( int i = 0; i < nodes; i++ )  
            for( int j = 0; j < nodes; j++ )  
                flow[i][j] = 0;  
  
        while( true )//find a augment path  
        {  
            for( int i = 0; i < nodes; i++ )  
            {  
                visited[i] = false;  
            }  
            pre[src] = -1;  
              
            if(!BFS( src, des )){// the BFS   
                break;  
            }  
              
            /*DFS(src,des);//DFS 
            if(!visited[des]) 
                break;*/  
              
            int increment = Integer.MAX_VALUE;  
            for( int i = des; pre[i] >= src; i = pre[i] )  
            {  
                //find the min flow of the path  
                increment = Math.min( increment, capacity[pre[i]][i]  
                        - flow[pre[i]][i] );  
            }  
              
            //update the flow  
            for( int i = des; pre[i] >= src; i = pre[i] )  
            {  
                flow[pre[i]][i] += increment;  
                flow[i][pre[i]] -= increment;  
            }  
            //increase the maxFow with the increment   
            maxFlow += increment;  
        }  
        return maxFlow;  
    }  
  
    private void DFS(int src, int des){  
        visited[src] = true;  
        for(int i = 0; i < nodes; i++){  
            if(!visited[i] && ( capacity[src][i] - flow[src][i] > 0) ){  
                pre[i] = src;//record the augment path  
                visited[i] = true;  
                DFS(i,des);  
            }  
        }  
    }  
              
    private boolean BFS( int src, int des )  
    {  
        Queue<Integer> queue = new LinkedList<Integer>();  
        queue.add( src );  
        visited[src] = true;  
        while( !queue.isEmpty() )  
        {  
            int node = queue.poll();  
            for( int i = 0; i < nodes; i++ )  
            {  
                if( !visited[i] && (capacity[node][i] - flow[node][i] > 0) )  
                {  
                    queue.add( i );  
                    visited[i] = true;  
                    pre[i] = node;//record the augment path  
                }  
            }  
        }  
  
        return visited[des];  
    }  
    
    public List<Flow> getFlowList()
    {
    	ArrayList<Flow> result;
    	result = new ArrayList<Flow>();
    	for (int i=0; i<nodes; i++)
    		for (int j=0; j<nodes; j++)
    			if (flow[i][j] > 0)
    			{
    				Flow flow = new Flow();
    				flow.src = i;
    				flow.tgt = j;
    				flow.Flow = this.flow[i][j];
    				result.add(flow);
    			}
    	return result;
    }
  
    public static void main( String[] args ) throws IOException  
    {  
    	
        int nodes, edges;  
        FileInputStream fis = new FileInputStream("C:\\a.txt");
        Scanner scanner = new Scanner( fis );  
          
        nodes = scanner.nextInt();  
        edges = scanner.nextInt();  
  
        int[][] capacity = new int[nodes][nodes];  
  
        int src, des, c;  
        for( int i = 0; i < edges; i++ )  
        {  
            src = scanner.nextInt();  
            des = scanner.nextInt();  
            c = scanner.nextInt();  
            capacity[src][des] = c;  
        }  
  
        MaxFlow maxFlow = new MaxFlow( capacity, nodes );  
        System.out.println( maxFlow.maxFlow( 0, nodes - 1 ) );
        ArrayList<Flow> flowList = (ArrayList<Flow>) maxFlow.getFlowList();
        for (Flow flow	:	flowList)
        {
        	System.out.println(flow.src+" -> "+flow.tgt+" : "+flow.Flow);
        }
        fis.close();
        
    	
        PetriNet pn = new PetriNet();
        pn  = PetriNetUtil.getPetriNetFromPnmlFile("C:\\Users\\Guo-68\\Desktop\\a.pnml");
        PetriNetResult pnr;
        //pnr.getVisualization();
    }  
    
    class Flow
    {
    	int src, tgt, Flow;
    }
}  