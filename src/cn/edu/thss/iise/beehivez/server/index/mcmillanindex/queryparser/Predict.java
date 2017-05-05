package cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser;



public class Predict {
	
	public boolean value = false;
	public boolean valid = false;
	public int type = 0;
	public int optag = 0;
	public Predict predict1;
	public Predict predict2;
	public TaskSet taskset1;
	public TaskSet taskset2;
	public boolean isAny = false;
	
	
	
	public Predict(String op, Predict pre1, Predict pre2){
		this.type = PredictTypeTag.BINLOGOP;
		if(op.equals("&&")){
			this.optag = OperatorTag.AND;
		}
		else if(op.equals("||")){
			this.optag = OperatorTag.OR;
		}
		
		this.predict1 = pre1;
		this.predict2 = pre2;
	}
	
	public Predict(Predict pre){
		this.type = PredictTypeTag.UNLOGOP;
		this.predict1 = pre;
	}
	
	public Predict(TaskSet taskset){
		this.type = PredictTypeTag.EXIST;
		this.taskset1 = taskset;
	}
	
	public Predict(int type){
		this.type = type;
	}
	
	public Predict(String op, TaskSet ts1, TaskSet ts2){
		this.type = PredictTypeTag.SETCOMOP;
		this.taskset1 = ts1;
		this.taskset2 = ts2;
		
		if(op.equals("identical")){
			this.optag = OperatorTag.IDENTICAL;
		}
		else if(op.equals("subsetof")){
			this.optag = OperatorTag.SUBSETOF;
		}
		else if(op.equals("overlap")){
			this.optag = OperatorTag.OVERLAP;
		}
	}
	
	public Predict(String op, TaskSet ts1, TaskSet ts2, String anyall){
		this.type = PredictTypeTag.TASKCOMPOP;
		this.taskset1 = ts1;
		this.taskset2 = ts2;
		
		if(anyall.equals("any")){
			this.isAny = true;
		}
		
		if(op.equals("succof")){
			this.optag = OperatorTag.SUCCOF;
		}
		else if(op.equals("predof")){
			this.optag = OperatorTag.PREDOF;
		}
		else if(op.equals("isuccof")){
			this.optag = OperatorTag.ISUCCOF;
		}
		else if(op.equals("ipredof")){
			this.optag = OperatorTag.IPREDOF;
		}
		else if(op.equals("concur")){
			this.optag = OperatorTag.CONCUROF;
		}
		else if(op.equals("exclusive")){
			this.optag = OperatorTag.NONCONCUROF;
		}
	}
	
	public void reSet(){
		valid = false;
		value = false;
		isAny = false;
	}
	
	public void setOp(String op){
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
		else if(op.equals("identical")){
			this.optag = OperatorTag.IDENTICAL;
		}
		else if(op.equals("subsetof")){
			this.optag = OperatorTag.SUBSETOF;
		}
		else if(op.equals("overlap")){
			this.optag = OperatorTag.OVERLAP;
		}
		else if(op.equals("&&")){
			this.optag = OperatorTag.AND;
		}
		else if(op.equals("||")){
			this.optag = OperatorTag.OR;
		}
	}
	
	public void setAnyall(String anyall){
		if(anyall.equals("any")){
			this.isAny = true;
		}
		else this.isAny = false;
	}
	
}
