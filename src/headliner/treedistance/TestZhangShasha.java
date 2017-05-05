package headliner.treedistance;

import java.util.Hashtable;
import java.util.ArrayList;

/* 
 * INSERT-LICENCE-INFO
 */
public class TestZhangShasha {

    public static void main 
	(String argv []) throws java.io.IOException  {
	
	if (argv.length != 2) {
	    System.out.println("Usage TestZhangShasha <tree1> <tree2>");
	    return;
	}

	TreeDefinition aTree = CreateTreeHelper.makeTree(argv[0]);
	System.out.println("The tree is: \n"+aTree);
	TreeDefinition bTree = CreateTreeHelper.makeTree(argv[1]);
	System.out.println("The tree is: \n"+bTree);

	ComparisonZhangShasha treeCorrector = new ComparisonZhangShasha();
	OpsZhangShasha costs = new OpsZhangShasha();
	Transformation transform = treeCorrector.findDistance(aTree, bTree, costs);
	System.out.println("Distance: "+transform.getCost());

    }	
}