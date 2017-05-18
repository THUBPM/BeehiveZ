package cn.edu.thss.iise.beehivez.server.metric.rorm.unfolding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.processmining.framework.models.ModelGraphVertex;

public class CompleteFinitePrefixPostProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void postProcess(CompleteFinitePrefix _cfp) {
		Queue<ModelGraphVertex> openConditions = new LinkedList<ModelGraphVertex>();
		for(Event cutoffEvent : _cfp.getCutOffEvents()) {
			Set<ModelGraphVertex> cutoffConditions = cutoffEvent.getSuccessors();
			openConditions.addAll(cutoffConditions);
		}
		while(!openConditions.isEmpty()) {
			Condition cutoffCondition = (Condition) openConditions.remove();
			if(cutoffCondition.getPredecessors().isEmpty()) {
				continue;
			}
			Condition correspondingCondition = cutoffCondition.getCorrespondingCondition();
			Event cutoffEvent = (Event) cutoffCondition.getPredecessors().iterator().next();
			Event correspondingEvent = (Event) correspondingCondition.getPredecessors().iterator().next();
			if(cutoffEvent.getOriginalTransition() == correspondingEvent.getOriginalTransition()) {
				Set<ModelGraphVertex> cutoffPredConditions = cutoffEvent.getPredecessors();
				Set<ModelGraphVertex> correspondingPreConditions = correspondingEvent.getPredecessors();
				for(ModelGraphVertex v : cutoffPredConditions) {
					Condition cutoffPredCondition = (Condition) v;
					cutoffPredCondition.setCutoffPost(true);
					for(ModelGraphVertex o : correspondingPreConditions) {
						if(((Condition) o).getOriginalPlace() == cutoffPredCondition.getOriginalPlace()) {
							Condition correspondingPredCondition = (Condition) o;
							if(correspondingPredCondition.getMappingConditions() == null) {
								correspondingPredCondition.setMappingConditions(new HashSet<Condition>());
								correspondingPredCondition.getMappingConditions().add(correspondingPredCondition);
							}
							correspondingPredCondition.getMappingConditions().add(cutoffPredCondition);
							cutoffPredCondition.setMappingConditions(correspondingPredCondition.getMappingConditions());
							cutoffPredCondition.setCorrespondingCondition(correspondingPredCondition);
						}
					}
					openConditions.offer(v);
				}
				Set<ModelGraphVertex> cutoffEventSucc = cutoffEvent.getSuccessors();
				for(ModelGraphVertex r : cutoffEventSucc) {
					Condition rCondition = (Condition) r;
					if(rCondition.getMappingConditions() != null) {
						rCondition.getMappingConditions().remove(rCondition);
					}
					ArrayList<Condition> oConditions = (ArrayList<Condition>) rCondition.getOriginalPlace().object;
					oConditions.remove(rCondition);
					_cfp.removeVertex(rCondition);
				}
				ArrayList<Event> oEvents = (ArrayList<Event>) cutoffEvent.getOriginalTransition().object;
				oEvents.remove(cutoffEvent);
				_cfp.removeVertex(cutoffEvent);
			}
		}
	}

}
