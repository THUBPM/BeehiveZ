package cn.edu.thss.iise.beehivez.server.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import cn.edu.thss.iise.beehivez.server.util.PetriNetUtil;

class ProcessMatrix
{
	int[][] matrixContent;
	
	
	public ProcessMatrix(int matrixLength)
	{
		matrixContent = new int[matrixLength][];
		for(int i=0; i<matrixLength; i++)
		{
			matrixContent[i] = new int[matrixLength];
			Arrays.fill(matrixContent[i],0);
		}
	}	
	
	public ProcessMatrix(int[][] _matrixContent)
	{
		matrixContent = _matrixContent.clone();
	}
	
	public static ProcessMatrix[] getNormalizedMatrice(DependencyGraph dg1, DependencyGraph dg2)
	{
		int matrixLength;
		HashMap<String,Integer> combinedNodeList;
		ProcessMatrix[] result;
		
		result = new ProcessMatrix[2];
		
		//Combine the two nodeList
		combinedNodeList = dg1.nodeList;
		matrixLength = dg1.nodeList.size();
		for(String transitionName	:	dg2.nodeList.keySet())
		{
			if (combinedNodeList.containsKey(transitionName))
				continue;
			combinedNodeList.put(transitionName, matrixLength);
			matrixLength ++;
		}
		
		for (int i=0; i<2; i++)
			result[i] = new ProcessMatrix(matrixLength);
				
		result[0].getMatrixContent(combinedNodeList, dg1.originalNet);
		result[1].getMatrixContent(combinedNodeList, dg2.originalNet);		
		return result;
	}
	
	public void getMatrixContent(HashMap<String,Integer> nodeList, PetriNet pn)
	{
		ArrayList<Place> placeList = pn.getPlaces();
		for (Place p	:	placeList)
		{
			if (p.getSuccessors().size() == 0 || p.getPredecessors().size() == 0)
				continue;
			Iterator pIt = p.getPredecessors().iterator();
			while (pIt.hasNext())
			{
				Transition pTransition, sTransition;
				String pIdentifier, sIdentifier;
				int pNode, sNode;
				pTransition = (Transition) pIt.next();
				pIdentifier = pTransition.getIdentifier();
				pNode = nodeList.get(pIdentifier);
				
				Iterator sIt = p.getSuccessors().iterator();
				while (sIt.hasNext())
				{
					sTransition = (Transition) sIt.next();
					sIdentifier = sTransition.getIdentifier();
					sNode = nodeList.get(sIdentifier);
					matrixContent[pNode][sNode] = 1;
				}
			}
		}
	}
	
	public void transpose()
	{
		int[][] tempContent;
		int matrixLength = matrixContent.length;
		
		tempContent = new int[matrixLength][];
		for (int i=0; i<matrixLength; i++)
		{
			tempContent[i] = new int [matrixLength];
			Arrays.fill(tempContent[i], 0);
		}
		
		for (int i=0; i<matrixLength; i++)
			for (int j=0; j<matrixLength; j++)
				tempContent[i][j] = matrixContent[j][i];
		matrixContent = tempContent.clone();
	}
	
	static ProcessMatrix minusMatrix(ProcessMatrix pm1, ProcessMatrix pm2)
	{
		int[][] minusedContent;
		int[][] content1 = pm1.matrixContent;
		int[][] content2 = pm2.matrixContent;
		int matrixLength;
		
		ProcessMatrix result;
		if (content1.length != content2.length)
			return null;
		matrixLength = content1.length;
		
		minusedContent = new int[matrixLength][];
		for (int i=0; i<matrixLength; i++)
		{
			minusedContent[i] = new int [matrixLength];
			Arrays.fill(minusedContent[i], 0);
		}
		
		for (int i=0; i<matrixLength; i++)
			for (int j=0; j<matrixLength; j++)
				minusedContent[i][j] = content1[i][j]-content2[i][j];
		
		result = new ProcessMatrix(minusedContent);
		return result;
		
	}
	
	static ProcessMatrix timesMatrix(ProcessMatrix pm1, ProcessMatrix pm2)
	{
		int[][] timedContent;
		int[][] content1 = pm1.matrixContent;
		int[][] content2 = pm2.matrixContent;
		int matrixLength;
		
		ProcessMatrix result;
		if (content1.length != content2.length)
			return null;
		matrixLength = content1.length;
		
		timedContent = new int[matrixLength][];
		for (int i=0; i<matrixLength; i++)
		{
			timedContent[i] = new int [matrixLength];
			Arrays.fill(timedContent[i], 0);
		}
		
		for (int i=0; i<matrixLength; i++)
			for (int j=0; j<matrixLength; j++)
			{				
				for (int k=0; k<matrixLength; k++)
					timedContent[i][j] += content1[i][k] * content2[k][j];
			}
		result = new ProcessMatrix(timedContent);
		return result;
	}
	
	int getTrace()
	{
		int result;		
		result = 0;
		for (int i=0; i<matrixContent.length; i++)
			result +=matrixContent[i][i];
		return result;
	}
}

/**
 * The Dependency Graph of the certain Petri Net
 * @author Guo Qinlong
 *
 */
class DependencyGraph
{
	HashMap<String,Integer> nodeList;	
	PetriNet originalNet;
	
	DependencyGraph(PetriNet pn)
	{
		adaptPetriNet(pn);
		originalNet = pn;
	}
	
	private void adaptPetriNet(PetriNet pn)
	{
		ArrayList<Transition> transitionList;		
		transitionList = pn.getTransitions();						
		
		//Initialize the node List
		nodeList = new HashMap<String,Integer>();
		int head = 0;
		for(Transition t	:	transitionList)
		{
			String identifier = t.getIdentifier();
			nodeList.put(identifier, head);
			head++;
		}
										
	}
	
}

public class DependencyGraphSimilarity extends PetriNetSimilarity{	

	@Override
	public float similarity(PetriNet pn1, PetriNet pn2) {
		DependencyGraph dg1, dg2;
		ProcessMatrix[] minusedMatrix,originalMatrix;
		ProcessMatrix timedMatrix;
		int distance;
		dg1 = new DependencyGraph(pn1);
		dg2 = new DependencyGraph(pn2);
		originalMatrix= ProcessMatrix.getNormalizedMatrice(dg1,dg2);		
		minusedMatrix = new ProcessMatrix[2];
		minusedMatrix[0] = ProcessMatrix.minusMatrix(originalMatrix[0], originalMatrix[1]);
		minusedMatrix[1] = ProcessMatrix.minusMatrix(originalMatrix[0], originalMatrix[1]);
		minusedMatrix[1].transpose();
		timedMatrix = ProcessMatrix.timesMatrix(minusedMatrix[0], minusedMatrix[1]);
		distance = timedMatrix.getTrace();		
		return (float) (1 / (float)(distance + 1));
	}

	@Override
	public String getName() {
		return "DependencyGraphSimilarity";
	}

	@Override
	public String getDesription() {
		return "structure based process similarity metric using Dependency Graph";
	}
	
	public static void main(String args[])
	{
		String fileName1="C:\\test\\testForDependency\\test1.xml";
		String fileName2="C:\\test\\testForDependency\\test2.xml";
		PetriNet pn1 = PetriNetUtil.getPetriNetFromPnmlFile(fileName1);
		PetriNet pn2 = PetriNetUtil.getPetriNetFromPnmlFile(fileName2);
//		for(Transition transition	:		pn2.getTransitions())
//			System.out.println(transition.getIdentifier());
		float similarity = new DependencyGraphSimilarity().similarity(pn1, pn2);
		System.out.println(similarity);
				
	}

}
