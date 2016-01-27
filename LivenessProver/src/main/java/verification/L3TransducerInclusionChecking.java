package verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;

import common.VerificationUltility;
import common.IntPair;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class L3TransducerInclusionChecking {

    /*
     * return null or sequence of (x, y)
     */
    public static List<int[]> findShortestCounterExample(EdgeWeightedDigraph dfa1,
							 EdgeWeightedDigraph dfa2){
	// get accepting states
	Set<Integer> acceptingStates1 = dfa1.getAcceptingStates();
	Set<Integer> acceptingStates2 = dfa2.getAcceptingStates();
	
	// dfa2 might be incomplete, therefore add a
	// special non-accepting looping state
	final int dfa2NA = dfa2.V();
	final List<DirectedEdge> emptyList = new ArrayList<DirectedEdge>();

	int numStatesComposition = dfa1.V();

	// store nodes waiting to visit
	List<Integer> working1 = new Stack<Integer>();
	List<Integer> working2 = new Stack<Integer>();

	// for each state, store the path from root to it
	List<List<int[]>> paths = new ArrayList<List<int[]>>();

	working1.add(dfa1.getInitState());
	working2.add(dfa2.getInitState());
	// add path to init
	paths.add(new ArrayList<int[]>());

	// check whether a node is visited or not
	final Set<IntPair> isVisited = new HashSet<IntPair>();
	final IntPair pairInit = new IntPair(dfa1.getInitState(), dfa2.getInitState());
	isVisited.add(pairInit);

	//	boolean[] isVisited = new boolean[numStatesComposition * (dfa2.V() + 1)];
	//	int hashInit = VerificationUltility.hash(dfa1.getInitState(),
	//						 dfa2.getInitState(), numStatesComposition);
	//	isVisited[hashInit] = true;

	while (!working2.isEmpty()) {
	    int currentState1 = working1.remove(0);
	    int currentState2 = working2.remove(0);
	    List<int[]> currentPath = paths.remove(0);

	    // check acceptance condition, reach a state whether word is
	    // accepted by automata1, but not automata2
	    if (acceptingStates1.contains(currentState1) &&
		!acceptingStates2.contains(currentState2)) {
		return currentPath;
	    }

	    Iterable<DirectedEdge> edges1 = dfa1.adj(currentState1);
	    Iterable<DirectedEdge> edges2;

	    if (currentState2 == dfa2NA)
		edges2 = emptyList;
	    else
		edges2 = dfa2.adj(currentState2);

	    for (DirectedEdge edge1 : edges1) {
		DirectedEdgeWithInputOutput tempEdge1 = (DirectedEdgeWithInputOutput) edge1;
		int input1 = tempEdge1.getInput();
		int output1 = tempEdge1.getOutput();
				
		int dest1 = tempEdge1.to();
		int dest2 = -1;
		
		for (DirectedEdge edge2 : edges2) {
		    DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
		    if (input1 == tempEdge2.getInput()
			&& output1 == tempEdge2.getOutput()) {
			dest2 = tempEdge2.to();
		    }
		}

		if (dest2 == -1)
		    // assume that we ended up in the looping state
		    dest2 = dfa2NA;

		//		int hashValue = VerificationUltility.hash(dest1, dest2, numStatesComposition);
		if (isVisited.add(new IntPair(dest1, dest2))) {
		    working1.add(dest1);
		    working2.add(dest2);
		    List<int[]> pathToChild = new ArrayList<int[]>(currentPath);
			    
		    pathToChild.add(new int[] { input1, output1 });//sequence of x, y
		    paths.add(pathToChild);
		}
	    }
	}
	
	return null;
    }
	

}
