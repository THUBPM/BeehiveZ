package cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser;

import java.util.*;

public class TaskSet {
	
	public boolean specific = false;
	public Vector<String> tasks;
	public Vector<Double> similarity;
	public int type = 0;
	public TaskSet taskset1;
	public TaskSet taskset2;
	public boolean isAny = false;
	public String varname;
	public int optag = 0;
	
	public TaskSet(){
		tasks = new Vector<String>();
		similarity = new Vector<Double>();
		specific = true;
		this.type = TaskSetTypeTag.SPECIFIC;
	}
	
	public TaskSet(int type){
		this.type = type;
	}
	
	public void setOp(String op){
		//op.toLowerCase();
		if(op.equals("succof")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(op.equals("alwsuccdany") || op.equals("alwsuccevery") || op.equals("possuccany") || op.equals("possuccevery")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(op.equals("predof")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(op.equals("alwpredany") || op.equals("alwpredevery") || op.equals("pospredany") || op.equals("pospredevery")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(op.equals("isuccof")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(op.equals("alwisuccany") || op.equals("alwisuccevery") || op.equals("posisuccany") || op.equals("posisuccevery")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(op.equals("ipredof")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(op.equals("alwipredany") || op.equals("alwipredevery") || op.equals("posipredany") || op.equals("posipredevery")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(op.equals("concur")){
			this.optag = OperatorTag.CONCUROF;
		}
		else if(op.equals("exclusive")){
			this.optag = OperatorTag.NONCONCUROF;
		}
		else if(op.equals("union")){
			this.optag = OperatorTag.UNION;
		}
		else if(op.equals("different")){
			this.optag = OperatorTag.DIFFERENCE;
		}
		else if(op.equals("intersect")){
			this.optag = OperatorTag.INTERSECTION;
		}
	}
	
	public TaskSet(Vector<String> task){
		this.type = TaskSetTypeTag.SPECIFIC;
		this.tasks = task;
		this.specific = true;
		
	}
	

	
	public TaskSet(String setop, TaskSet op1, TaskSet op2){
		//setop.toLowerCase();
		this.type = TaskSetTypeTag.CONSTRUCTION;
		this.taskset1 = op1;
		this.taskset2 = op2;
		if(setop.equals("union")){
			this.optag = OperatorTag.UNION;
		}
		else if(setop.equals("different")){
			this.optag = OperatorTag.DIFFERENCE;
		}
		else if(setop.equals("intersect")){
			this.optag = OperatorTag.INTERSECTION;
		}
	}
	
	public TaskSet(String taskcomop, TaskSet ts, String isany){
		//taskcomop.toLowerCase();
		this.type = TaskSetTypeTag.APPICATION;
		this.taskset1 = ts;
		if(isany.equals("any")){
			this.isAny = true;
		}
		if(taskcomop.equals("succof")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(taskcomop.equals("alwsuccdany") || taskcomop.equals("alwsuccevery") || taskcomop.equals("possuccany") || taskcomop.equals("possuccevery")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(taskcomop.equals("predof")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(taskcomop.equals("alwpredany") || taskcomop.equals("alwpredevery") || taskcomop.equals("pospredany") || taskcomop.equals("pospredevery")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(taskcomop.equals("isuccof")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(taskcomop.equals("alwisuccany") || taskcomop.equals("alwisuccevery") || taskcomop.equals("posisuccany") || taskcomop.equals("posisuccevery")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(taskcomop.equals("ipredof")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(taskcomop.equals("alwipredany") || taskcomop.equals("alwipredevery") || taskcomop.equals("posipredany") || taskcomop.equals("posipredevery")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(taskcomop.equals("concur")){
			this.optag = OperatorTag.CONCUROF;
		}
		else if(taskcomop.equals("exclusive")){
			this.optag = OperatorTag.NONCONCUROF;
		}
	}
	
/*	public TaskSet(String taskcomop, TaskSet ts){
		this.type = TaskSetTypeTag.APPICATION;
		this.taskset1 = ts;
		if(taskcomop.equals("succof")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(taskcomop.equals("predof")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(taskcomop.equals("isuccof")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(taskcomop.equals("ipredof")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(taskcomop.equals("concurof")){
			this.optag = OperatorTag.CONCUROF;
		}
		else if(taskcomop.equals("nonconcurof")){
			this.optag = OperatorTag.NONCONCUROF;
		}
	}*/
	
	public TaskSet(String var){
		this.type = TaskSetTypeTag.VARNAME;
		this.varname = var;
	}
	
	public void addTask(String ts, Double sim){
		this.tasks.add(ts);
		this.similarity.add(sim);
	}
	
	public void calValue(){
		switch(this.type){
		
		case TaskSetTypeTag.SPECIFIC:
			return;
			
		case TaskSetTypeTag.CONSTRUCTION:
			switch(this.optag){
			case OperatorTag.UNION:
				this.union();
				return;
			case OperatorTag.DIFFERENCE:
				this.difference();
				return;
			case OperatorTag.INTERSECTION:
				this.intersection();
				return;
			default: 
				return;				
			}
			
		case TaskSetTypeTag.APPICATION:
			switch(this.optag){
			case OperatorTag.PREDOF:
				this.predof();
				return;
			case OperatorTag.IPREDOF:
				this.ipredof();
				return;
			case OperatorTag.SUCCOF:
				this.succof();
				return;
			case OperatorTag.ISUCCOF:
				this.isuccof();
				return;
			case OperatorTag.CONCUROF:
				this.concurof();
				return;
			case OperatorTag.NONCONCUROF:
				this.nonconcurof();
				return;
			default:
				return;
			}
			
		case TaskSetTypeTag.VARNAME:
			
			
		default:
		}
	}

	
	
	
	public void union(){
		this.tasks = TaskSet.getUnion(taskset1.tasks, taskset2.tasks);
	}
	
	public void difference(){
		this.tasks = TaskSet.getDifference(taskset1.tasks, taskset2.tasks);
	}
	
	public void intersection(){
		this.tasks = TaskSet.getIntersection(taskset1.tasks, taskset2.tasks);
	}
	
	public static Vector<String> getUnion(Vector<String> s1, Vector<String> s2){
		Vector<String> result = s1;
		for(int i = 0; i < s2.size(); i++){
			if(!result.contains(s2.get(i)))
				result.add(s2.get(i));
		}
		
		return result;
	}
	
	public static Vector<String> getDifference(Vector<String> s1, Vector<String> s2){
		Vector<String> result = s1;
		for(int i = 0; i < s1.size(); i++){
			for(int j = 0; j < s2.size(); j++){
				if(s1.get(i).compareTo(s2.get(j)) == 0){
					result.remove(s1.get(i));
					continue;
				}
			}
		}
		return result;
	}
	
	public static Vector<String> getIntersection(Vector<String> s1, Vector<String> s2){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < s1.size(); i++){
			for(int j = 0; j < s2.size(); j++){
				if(s1.get(i).compareTo(s2.get(j)) == 0){
					result.add(s1.get(i));
					continue;
				}
			}
		}
		return result;
	}
	
	
	public void succof(){
		
	}
	
	public void isuccof(){
		
	}
	
	public void predof(){
		
	}
	
	public void ipredof(){
		
	}
	
	public void concurof(){
		
	}
	
	public void nonconcurof(){
		
	}
	
	public static boolean identical(Vector<String> s1, Vector<String> s2){
		if(s1.size() != s2.size()){
			return false;
		}
		Vector<String> result = getIntersection(s1, s2);
		if(result.size() == s1.size()){
			return true;
		}
		return false;
	}
	
	public static boolean subsetof(Vector<String> s1, Vector<String> s2){
		Vector<String> result = getIntersection(s1, s2);
		if(result.size() == s1.size()){
			return true;
		}
		return false;
	}
	
	public static boolean overlap(Vector<String> s1, Vector<String> s2){
		Vector<String> result = getIntersection(s1, s2);
		if(result.size() != 0){
			return true;
		}
		return false;
	}
	
}
