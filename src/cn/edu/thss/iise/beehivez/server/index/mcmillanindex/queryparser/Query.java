package cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCondition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;
import cn.edu.thss.iise.beehivez.server.index.mcmillanindex.Searching;

public class Query {
	public Assignments assignments;
	public Predict predict;
	
	//ONCompleteFinitePrefix tpcfp;
    Searching searching = new Searching();
    
    OrderingRelationMatrix orm;
    //public OrderingRelation[][] orderingRelations = null;
    //public 
    
    Vector<String> universe;
    
    public Query(){
    	
    }
	
	public Query(Assignments assignments, Predict predict){
		this.assignments = assignments;
		this.predict = predict;
	}
	
	/*public void setCFP(ONCompleteFinitePrefix cfp){
    	this.tpcfp = cfp;
    }*/
	
	public void setOrderingRelation(OrderingRelationMatrix orm){
		this.orm = orm;
	}
   
    
   /* public void calOrderingRelation(){
    	OrderingRelationMatrix orm = new OrderingRelationMatrix(tpcfp);
    	orm.computePrefixRelations();
    	orm.completePrefixRelations();
    	this.orderingRelations = orm.orderingRelations;
    }*/
    
	public void calTable(){
		Vector<String> keys = this.assignments.variables;
		for(int i = 0; i < keys.size(); i++){
			reachTaskSet(this.assignments.table.get(keys.get(i)));
		}
	}
	
	
	
	/*public void printPrefixRelations(){
		int length = tpcfp.getOn().getEveSet().size();
		ONEvent e1, e2;
		int k1, k2;
		
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length; j++){
				e1 = tpcfp.getOn().getEveSet().get(i);
				e2 = tpcfp.getOn().getEveSet().get(j);
				k1 = tpcfp.getOn().getEveSet().indexOf(e1);
				k2 = tpcfp.getOn().getEveSet().indexOf(e2);
				System.out.println(
				e1.getLabel() + " " + e2.getLabel() + " " + 
				this.orderingRelations[k1][k2]);
			}
		}
		System.out.println("\n\n");
	}*/
	
	public void calFinalPredictValue(){
		reachPredict(this.predict);
	}
	
	public void reachPredict(Predict p){
		//if(p.valid) return;
		
		switch(p.type){
		case PredictTypeTag.BINLOGOP:
			reachPredict(p.predict1);
			reachPredict(p.predict2);
			break;
		case PredictTypeTag.UNLOGOP:
			reachPredict(p.predict1);
			break;
		case PredictTypeTag.EXIST:
			if(p.taskset1.type == TaskSetTypeTag.VARNAME)
				p.taskset1 = this.getVarnameValue(p.taskset1.varname);
			reachTaskSet(p.taskset1);
			break;
		case PredictTypeTag.SETCOMOP:		
		case PredictTypeTag.TASKCOMPOP:
			if(p.taskset1.type == TaskSetTypeTag.VARNAME)
				p.taskset1 = this.getVarnameValue(p.taskset1.varname);
			else reachTaskSet(p.taskset1);
			if(p.taskset2.type == TaskSetTypeTag.VARNAME)
				p.taskset2 = this.getVarnameValue(p.taskset2.varname);
			else reachTaskSet(p.taskset2);
			break;
		}
		
		calPredictValue(p);
	}
	
	public void reachTaskSet(TaskSet ts){
		//if(ts.specific) return;
		
		switch(ts.type){
		case TaskSetTypeTag.VARNAME:
			ts = this.getVarnameValue(ts.varname);
			return;
		case TaskSetTypeTag.CONSTRUCTION:
			if(ts.taskset1.type == TaskSetTypeTag.VARNAME)
				ts.taskset1 = this.getVarnameValue(ts.taskset1.varname);
			else reachTaskSet(ts.taskset1);
			if(ts.taskset2.type == TaskSetTypeTag.VARNAME)
				ts.taskset2 = this.getVarnameValue(ts.taskset2.varname);
			else reachTaskSet(ts.taskset2);
			break;
		case TaskSetTypeTag.APPICATION:
			if(ts.taskset1.type == TaskSetTypeTag.VARNAME)
				ts.taskset1 = this.getVarnameValue(ts.taskset1.varname);
			else reachTaskSet(ts.taskset1);
			break;
		case TaskSetTypeTag.SPECIFIC:
			/*if(!TaskSet.subsetof(ts.tasks, universe)){
				this.predict.valid = true;
				this.predict.value = false;
				return;
			}*/
		default:
		}
		
		calTaskSetValue(ts);
	}
	
	public TaskSet getVarnameValue(String varname){
		return this.assignments.table.get(varname);
	}
	
	public void calTaskSetValue(TaskSet ts){
		switch(ts.type){
		
		case TaskSetTypeTag.SPECIFIC:
			return;
			
		case TaskSetTypeTag.CONSTRUCTION:
			switch(ts.optag){
			case OperatorTag.UNION:
				ts.union();
				break;
			case OperatorTag.DIFFERENCE:
				ts.difference();
				break;
			case OperatorTag.INTERSECTION:
				ts.intersection();
				break;
			default: 
				break;			
			}
			
		case TaskSetTypeTag.APPICATION:
			switch(ts.optag){
			case OperatorTag.SUCCOF:
				ts.tasks = getSuccofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			case OperatorTag.PREDOF:
				ts.tasks = getPredofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			case OperatorTag.ISUCCOF:
				ts.tasks = getISuccofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			case OperatorTag.IPREDOF:
				ts.tasks = getIPredofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			case OperatorTag.CONCUROF:
				ts.tasks = getConcurofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			case OperatorTag.NONCONCUROF:
				ts.tasks = getNonConcurofAnyAll(ts.taskset1.tasks, ts.isAny);
				break;
			/*
			 * waiting for adding
			 * */
			}
		}
		
		ts.specific = true;
	}
	
	public void calPredictValue(Predict p){
		switch(p.type){
		case PredictTypeTag.BINLOGOP:
			switch(p.optag){
			case OperatorTag.AND:
				p.value = p.predict1.value && p.predict2.value;
				break;
			case OperatorTag.OR:
				p.value = p.predict1.value || p.predict2.value;
			}
			break;
		case PredictTypeTag.UNLOGOP:
			p.value = !p.predict1.value;
			break;
		case PredictTypeTag.EXIST:
			p.value = TaskSet.subsetof(p.taskset1.tasks, universe);
			break;
		case PredictTypeTag.SETCOMOP:
			switch(p.optag){
			case OperatorTag.IDENTICAL:
				if(p.taskset1.tasks.size() == 0)
					p.value = false;
				else
					p.value = TaskSet.identical(p.taskset1.tasks, p.taskset2.tasks);
				break;
			case OperatorTag.SUBSETOF:
				if(p.taskset1.tasks.size() == 0)
					p.value = false;
				else 
					p.value = TaskSet.subsetof(p.taskset1.tasks, p.taskset2.tasks);
				break;
			case OperatorTag.OVERLAP:
				p.value = TaskSet.overlap(p.taskset1.tasks, p.taskset2.tasks);
				break;
			}
			break;
		case PredictTypeTag.TASKCOMPOP:
			switch(p.optag){
			case OperatorTag.SUCCOF:
				p.value = isSuccof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
			case OperatorTag.PREDOF:
				p.value = isPredof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
			case OperatorTag.ISUCCOF:
				p.value = isISuccof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
			case OperatorTag.IPREDOF:
				p.value = isIPredof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
			case OperatorTag.CONCUROF:
				p.value = isConcurof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
			case OperatorTag.NONCONCUROF:
				p.value = isNonConcurof(p.taskset1.tasks, p.taskset2.tasks, p.isAny);
				break;
				/* 
				 * waiting to add
				 * */
			}
			break;
		}
		//p.valid = true;
	}
	
	public void getUniverse(){
		universe = new Vector<String>();
		String temp;
		for(int i = 0; i < orm.taskIDTable.size(); i++){
			temp = orm.taskIDTable.get(i).taskname;
			if(!universe.contains(temp))
				universe.add(temp);
		}
		//for(int i = 0)
		/*for(int i=0;i<tpcfp.getOn().getEveSet().size();i++){
			temp = tpcfp.getOn().getEveSet().get(i).getLabel();
			if(!universe.contains(temp))
				universe.add(temp);
		}*/
	}
	
	
	
	public Vector<Integer> findTaskId(String ts){
		//Vector<ONEvent> events = tpcfp.getOn().getEveSet();
		Vector<Integer> result = new Vector<Integer>();
		for(int i = 0; i < orm.taskIDTable.size(); i++ ){
			if(orm.taskIDTable.get(i).taskname.equalsIgnoreCase(ts))
				result.add(i);
		}
		return result;
		
	}
	
	public boolean isSuccof(String ts1,String ts2){
		/*searching = new Searching();
		return searching.search(ts2, ts1, this.myPetriNet, this.tpcfp);*/
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts2id.get(j)][ts1id.get(i)] == OrderingRelation.PRECEDENCE
				 ||this.orm.orderingRelations[ts2id.get(j)][ts1id.get(i)] == OrderingRelation.IPRECEDENCE	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isSuccof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isSuccofAny(ts1, ts2);
		else
			return isSuccofAll(ts1, ts2);
	}
	
	public boolean isSuccofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isSuccof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSuccofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isSuccof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isISuccof(String ts1,String ts2){
		/*searching = new Searching();
		return searching.search(ts2, ts1, this.myPetriNet, this.tpcfp);*/
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts2id.get(j)][ts1id.get(i)] == OrderingRelation.IPRECEDENCE	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isISuccof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isISuccofAny(ts1, ts2);
		else
			return isISuccofAll(ts1, ts2);
	}
	
	public boolean isISuccofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isISuccof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isISuccofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isISuccof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isPredof(String ts1,String ts2){
		/*searching = new Searching();
		return searching.search(ts2, ts1, this.myPetriNet, this.tpcfp);*/
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts1id.get(i)][ts2id.get(j)] == OrderingRelation.PRECEDENCE
				 ||this.orm.orderingRelations[ts1id.get(i)][ts2id.get(j)] == OrderingRelation.IPRECEDENCE	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isPredof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isPredofAny(ts1, ts2);
		else
			return isPredofAll(ts1, ts2);
	}
	
	public boolean isPredofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isPredof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isPredofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isPredof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isIPredof(String ts1,String ts2){
		/*searching = new Searching();
		return searching.search(ts2, ts1, this.myPetriNet, this.tpcfp);*/
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts1id.get(i)][ts2id.get(j)] == OrderingRelation.IPRECEDENCE	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isIPredof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isIPredofAny(ts1, ts2);
		else
			return isIPredofAll(ts1, ts2);
	}
	
	public boolean isIPredofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isIPredof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isIPredofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isIPredof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isConcurof(String ts1, String ts2){
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts1id.get(i)][ts2id.get(j)] == OrderingRelation.CONCURRENCY	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isNonConcurof(String ts1, String ts2){
		Vector<Integer> ts1id = findTaskId(ts1);
		Vector<Integer> ts2id = findTaskId(ts2);
		if(ts1id.size() == 0 || ts2id.size() == 0)
			return false;
		for(int i = 0; i < ts1id.size(); i++){
			for(int j = 0; j < ts2id.size(); j++){
				if(this.orm.orderingRelations[ts1id.get(i)][ts2id.get(j)] == OrderingRelation.CONFLICT	)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isConcurof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isConcurofAny(ts1, ts2);
		else
			return isConcurofAll(ts1, ts2);
	}
	
	public boolean isConcurofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isConcurof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isConcurofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isConcurof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isNonConcurof(Vector<String> ts1, Vector<String> ts2, boolean isAny){
		if(isAny)
			return isNonConcurofAny(ts1, ts2);
		else
			return isNonConcurofAll(ts1, ts2);
	}
	
	public boolean isNonConcurofAny(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(isNonConcurof(ts1.get(i), ts2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isNonConcurofAll(Vector<String> ts1, Vector<String> ts2){
		for(int i = 0; i < ts1.size(); i++){
			for(int j = 0; j < ts2.size(); j++){
				if(!isNonConcurof(ts1.get(i), ts2.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	
	/*
	 * Application -- Succof
	 * */
	
	public Vector<String> getSuccof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isSuccof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	public Vector<String> getSuccofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getSuccofAny(ts);
		}
		else{
			return getSuccofAll(ts);
		}
			
	}
	
	public Vector<String> getSuccofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getSuccof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getSuccofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getSuccof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	
	/*
	 * Application -- predof
	 * */
	public Vector<String> getPredof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isPredof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	
	public Vector<String> getPredofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getPredofAny(ts);
		}
		else{
			return getPredofAll(ts);
		}
			
	}
	
	public Vector<String> getPredofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getPredof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getPredofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getPredof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	
	public Vector<String> getISuccof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isISuccof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	public Vector<String> getISuccofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getISuccofAny(ts);
		}
		else{
			return getISuccofAll(ts);
		}
			
	}
	
	public Vector<String> getISuccofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getISuccof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getISuccofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getISuccof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	
	/*
	 * Application -- predof
	 * */
	public Vector<String> getIPredof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isIPredof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	
	public Vector<String> getIPredofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getIPredofAny(ts);
		}
		else{
			return getIPredofAll(ts);
		}
			
	}
	
	public Vector<String> getIPredofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getIPredof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getIPredofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getIPredof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	/*
	 * Application -- Concurof
	 * */
	
	public Vector<String> getConcurof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isConcurof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	
	public Vector<String> getConcurofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getConcurofAny(ts);
		}
		else{
			return getConcurofAll(ts);
		}
			
	}
	
	public Vector<String> getConcurofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getConcurof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getConcurofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getConcurof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	
	/*
	 * Application -- NonConcurof
	 * */
	
	public Vector<String> getNonConcurof(String ts){
		Vector<String> result = new Vector<String>();
		for(int i = 0; i < universe.size(); i++){
			if(isNonConcurof(universe.get(i), ts)){
				result.add(universe.get(i));
			}
		}
		return result;
	}
	
	
	public Vector<String> getNonConcurofAnyAll(Vector<String> ts, boolean isAny){
		if(isAny){
			return getNonConcurofAny(ts);
		}
		else{
			return getNonConcurofAll(ts);
		}
	}
	
	public Vector<String> getNonConcurofAny(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getNonConcurof(ts.get(i));
			result = TaskSet.getUnion(result, temp);
		}
		return result;
	}
	
	public Vector<String> getNonConcurofAll(Vector<String> ts){
		Vector<String> result = new Vector<String>();
		Vector<String> temp = new Vector<String>();
		for(int i = 0; i < ts.size(); i++){
			temp = getNonConcurof(ts.get(i));
			if(i == 0){
				result = temp;
			}
			else{
				result = TaskSet.getIntersection(result, temp);
			}
		}
		return result;
	}
	
}
