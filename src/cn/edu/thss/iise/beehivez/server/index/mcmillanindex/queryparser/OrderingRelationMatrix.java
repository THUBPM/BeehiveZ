package cn.edu.thss.iise.beehivez.server.index.mcmillanindex.queryparser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCompleteFinitePrefix;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONCondition;
import cn.edu.thss.iise.beehivez.server.basicprocess.occurrencenet.ONEvent;

public class OrderingRelationMatrix {
	public OrderingRelation[][] orderingRelations = null;
	//public ONCompleteFinitePrefix tpcfp;
	public Vector<TaskIDTable> taskIDTable = new Vector<TaskIDTable>();
	
	public OrderingRelationMatrix(ONCompleteFinitePrefix tpcfp){
		int nEvents = tpcfp.getOn().getEveSet().size();
		this.orderingRelations = new OrderingRelation[nEvents][nEvents];
		this.createIDTable(tpcfp);
		this.computePrefixRelations(tpcfp);
		this.completePrefixRelations(tpcfp);
//		printPrefixRelations();
	}
	
	public void createIDTable(ONCompleteFinitePrefix tpcfp){
		String temp;
		for(int i = 0; i < this.orderingRelations.length; i++){
			temp = tpcfp.getOn().getEveSet().get(i).getLabel();
			temp = temp.replace("\\n", " ");
			taskIDTable.add(new TaskIDTable(temp, i));
		}
	}
	
	public void computePrefixRelations(ONCompleteFinitePrefix tpcfp){
		for (int i = 0; i < this.orderingRelations.length; i++) {
			for (int j = 0; j < this.orderingRelations.length; j++) {
				if (i == j) {
					this.orderingRelations[i][j] = OrderingRelation.NONE;
				} else {
					this.orderingRelations[i][j] = OrderingRelation.CONCURRENCY;
				}
			}
		}

		// only after all pre-conditions have been visited, the relations to
		// some event can be updated;
		// record which condition has been visited
		HashSet<ONCondition> visited = new HashSet<ONCondition>();

		// works as a queue
		LinkedList<ONEvent> queue = new LinkedList<ONEvent>();
		
		Iterator<ONCondition> itEntry = tpcfp.getIntialConditions().iterator();
		while (itEntry.hasNext()) {
			ONCondition entry = itEntry.next();
			visited.add(entry);
			//queue.addAll(entry.getSuccessors());
			/*Iterator<MyPetriTransition> events = 
				entry.getPlace().getSuccessors().iterator();
			while(events.hasNext()){
				MyPetriTransition trans = events.next();
				ONEvent event = tpcfp.getOn(). trans.getId()
			}*/
			queue.addAll(tpcfp.getOn().getEvesOUTOFCon(entry.getId()));
		}

		while (!queue.isEmpty()) {
			ONEvent e = queue.removeFirst();
			if (visited.containsAll(tpcfp.getOn().getConsINTOEve(e.getId()))) {
				updateEventRelations(e, tpcfp);

				// deal with the sucessor events
				Iterator<ONCondition> itcpost = 
					tpcfp.getOn().getConsOUTOFEve(e.getId()).iterator();
				while (itcpost.hasNext()) {
					ONCondition postc = itcpost.next();
					visited.add(postc);
					Iterator<ONEvent> itepost = 
						tpcfp.getOn().getEvesOUTOFCon(postc.getId()).iterator();
					while (itepost.hasNext()) {
						ONEvent poste = itepost.next();
						if (!queue.contains(poste)) {
							queue.add(poste);
						}
					}
				}

			} else {
				queue.addLast(e);
			}
		}
	}
	
	private void updateEventRelations(ONEvent event, ONCompleteFinitePrefix tpcfp) {
		// get the index of the event
		//int indexEvent = Integer.parseInt(event.getId());
		int indexEvent = tpcfp.getOn().getEveSet().indexOf(event);
		// deal with the pre-conditions one by one
		Iterator<ONCondition> itc = 
			tpcfp.getOn().getConsINTOEve(event.getId()).iterator();
		while (itc.hasNext()) {
			ONCondition prec = itc.next();

			// deal with the relation between the event and its pre-events
			Iterator<ONEvent> itepre = 
				tpcfp.getOn().getEvesINTOCon(prec.getId()).iterator();
			if (itepre.hasNext()) {
				ONEvent pree = itepre.next();
				int indexPree = tpcfp.getOn().getEveSet().indexOf(pree);
				this.orderingRelations[indexPree][indexEvent] = OrderingRelation.IPRECEDENCE;
				this.orderingRelations[indexEvent][indexPree] = OrderingRelation.NONE;

				for (int k = 0; k < this.orderingRelations.length; k++) {
					if (this.orderingRelations[k][indexPree] == OrderingRelation.PRECEDENCE
						||	this.orderingRelations[k][indexPree] == OrderingRelation.IPRECEDENCE) {
						this.orderingRelations[k][indexEvent] = OrderingRelation.PRECEDENCE;
						this.orderingRelations[indexEvent][k] = OrderingRelation.NONE;
					}
					if (this.orderingRelations[indexPree][k] == OrderingRelation.CONFLICT) {
						this.orderingRelations[indexEvent][k] = OrderingRelation.CONFLICT;
						this.orderingRelations[k][indexEvent] = OrderingRelation.CONFLICT;
					}
				}
			}

			// deal with the relation between the event and its sibling events
			Iterator<ONEvent> itesibling = 
				tpcfp.getOn().getEvesOUTOFCon(prec.getId()).iterator();
			while (itesibling.hasNext()) {
				ONEvent esibling = itesibling.next();
				if (esibling != event) {
					int indexEsibling = tpcfp.getOn().getEveSet().indexOf(esibling);
					this.orderingRelations[indexEvent][indexEsibling] = OrderingRelation.CONFLICT;
					this.orderingRelations[indexEsibling][indexEvent] = OrderingRelation.CONFLICT;
					for (int k = 0; k < this.orderingRelations.length; k++) {
						if (k != indexEvent) {
							if (this.orderingRelations[indexEsibling][k] == OrderingRelation.PRECEDENCE
									|| this.orderingRelations[indexEsibling][k] == OrderingRelation.IPRECEDENCE) {
								this.orderingRelations[k][indexEvent] = OrderingRelation.CONFLICT;
								this.orderingRelations[indexEvent][k] = OrderingRelation.CONFLICT;
							}
						}
					}
				}
			}
		}
	}
	
	public void completePrefixRelations(ONCompleteFinitePrefix tpcfp){
		for (ONEvent cutOffEvent : tpcfp.getCutOffEvents()) {
			ONEvent correspondingEvent = (ONEvent)cutOffEvent.object;
			Vector<ONEvent> cutOffLocalConfigurationEvents = cutOffEvent
					.getLocalConfiguration().getEvents();
			
			//TODO: need to be changed, use the marking of the local configuration instead.
			Iterator<ONCondition> itccpost = 
				tpcfp.getOn().getConsOUTOFEve(correspondingEvent.getId()).iterator();
			while (itccpost.hasNext()) {
				ONCondition ccpost = itccpost.next();

				// determine whether the followed place are same
				boolean flag = false;
				Iterator<ONCondition> itcpost = 
					tpcfp.getOn().getConsOUTOFEve(cutOffEvent.getId()).iterator();
				while (itcpost.hasNext()) {
					if (itcpost.next().getPlace() == ccpost.getPlace()){
						flag = true;
						break;
					}
				}
				if (flag) {
					Iterator<ONEvent> itcepost = 
						tpcfp.getOn().getEvesOUTOFCon(ccpost.getId()).iterator();
					while (itcepost.hasNext()) {
						ONEvent cepost = itcepost.next();
						int indexcepost = tpcfp.getOn().getEveSet().indexOf(cepost);
						for (ONEvent pree : cutOffLocalConfigurationEvents) {
							int indexpree = tpcfp.getOn().getEveSet().indexOf(pree);

							this.orderingRelations[indexpree][indexcepost] = OrderingRelation.IPRECEDENCE;
							if (this.orderingRelations[indexcepost][indexpree] != OrderingRelation.PRECEDENCE
									&& this.orderingRelations[indexcepost][indexpree] != OrderingRelation.IPRECEDENCE) {
								this.orderingRelations[indexcepost][indexpree] = OrderingRelation.NONE;
							}
							for (int k = 0; k < this.orderingRelations.length; k++) {
								if (this.orderingRelations[indexcepost][k] == OrderingRelation.PRECEDENCE
										|| this.orderingRelations[indexcepost][k] == OrderingRelation.IPRECEDENCE) {
									this.orderingRelations[indexpree][k] = OrderingRelation.PRECEDENCE;
									if (this.orderingRelations[k][indexpree] != OrderingRelation.PRECEDENCE
										&&	this.orderingRelations[k][indexpree] != OrderingRelation.IPRECEDENCE) {
										this.orderingRelations[k][indexpree] = OrderingRelation.NONE;
									}
								}
							}
						}

					}

				}
			}
		}
	}
}
