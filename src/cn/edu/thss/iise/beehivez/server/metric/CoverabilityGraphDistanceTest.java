/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.metric;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

import org.processmining.analysis.graphmatching.algos.GraphEditDistanceGreedy;
import org.processmining.analysis.graphmatching.graph.SimpleGraph;
import org.processmining.framework.models.fsm.FSMTransition;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.StateSpace;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.algorithms.CoverabilityGraphBuilder;
import org.processmining.importing.pnml.PnmlImport;

import cn.edu.thss.iise.beehivez.server.basicprocess.OrderedLabelTree;
import cn.edu.thss.iise.beehivez.server.basicprocess.OrderedLabelTreeGenerator;
import cn.edu.thss.iise.beehivez.server.basicprocess.mymodel.MyPetriNet;

/**
 * @author 王子璇
 *
 */
public class CoverabilityGraphDistanceTest {

	/**
	 * 
	 */
	public CoverabilityGraphDistanceTest() {
		// TODO Auto-generated constructor stub
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
       FileInputStream pnml1;
       FileInputStream pnml2;
		try {
			pnml1 = new FileInputStream("F:\\Demo\\Prom\\M31.pnml");
			pnml2 = new FileInputStream("F:\\Demo\\Prom\\M18.pnml");
	        PnmlImport pnmlimport = new PnmlImport();
	        PetriNet petrinet1 = pnmlimport.read(pnml1);
	        PetriNet petrinet2 = pnmlimport.read(pnml2);
	        
	        ((Place) petrinet1.getSource()).addToken(new Token());
	        ((Place) petrinet2.getSource()).addToken(new Token());
	        StateSpace graph1 =CoverabilityGraphBuilder.build(petrinet1);
	        StateSpace graph2 =CoverabilityGraphBuilder.build(petrinet2);
	        MyGraph p1 = new MyGraph();
	        SimpleGraph sg1= p1.convertCoverabilityGraphToSimpleGraph(graph1);
	        MyGraph p2 = new MyGraph();
	        SimpleGraph sg2= p2.convertCoverabilityGraphToSimpleGraph(graph2);
	        
	        CoverabilityGraphSimilarity similarity = new CoverabilityGraphSimilarity();
	        BigDecimal sim = new BigDecimal(similarity.getSimilarity(sg1, sg2));
	        sim = sim.setScale(3, BigDecimal.ROUND_HALF_UP);
	        System.out.println(sim.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}


}
