package cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser;
import java.io.*;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.index.mcmillanindex.Searching;

public class Run {
	
	public String queryString;
	public MyParser parser;
    public Query q;
    OrderingRelationMatrix orm;
    ONCompleteFinitePrefix tpcfp;
    //Searching searching = new Searching();
    
    public Run(String query){
    	this.queryString = query;
    }
    
   /* public void setCFP(ONCompleteFinitePrefix cfp){
    	this.tpcfp = cfp;
    }*/
    
    public void setORM(OrderingRelationMatrix orm){
    	this.orm = orm;
    }
	
	/*public static void main(String args[]){
		
	    Run run = new Run();
	    run.start();
	}*/
	
	public void start(){
		  //System.out.println("Reading from file . . ."); 
		    
		    try {
		    	/*FileInputStream stream = new FileInputStream("C:\\test2.txt");
			      parser = new MyParser(stream);*/
		    	parser = new MyParser(new ByteArrayInputStream(queryString.getBytes()));
		      q = parser.Start();
	//	      System.out.println("Parsed successfully.");
		      
		      
		      
		    } catch (Exception e) {
		      System.out.println(e.getMessage());
		      System.out.println("Encountered errors during parse.");
		    }
	}
	
	public boolean getPredictResult(){
		//q.setCFP(tpcfp);
		//q.predict.reSet();
		q.setOrderingRelation(orm);
		q.getUniverse();
	    //q.calOrderingRelation();
		//q.calOrderingRelation();
		if(q.assignments != null){
			q.calTable();	      
		}
	    q.calFinalPredictValue();
        
	    //System.out.println(q.predict.value);	      
	    return q.predict.value;
	}
	
	

}
