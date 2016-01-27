package verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import common.VerificationUltility;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import verification.L2TransducerComposition.DirectedEdgeWithInputOutputCommon;

public class L2TransducerInclusionChecking {

	/*
	 * return null or sequence of (w1, w2, w3)
	 */
	public static List<int[]> findShortestCounterExample(EdgeWeightedDigraph dfa1, EdgeWeightedDigraph completeDFA2){
		// get accepting states
		Set<Integer> acceptingStates1 = dfa1.getAcceptingStates();
		Set<Integer> acceptingStates2 = completeDFA2.getAcceptingStates();

		int numStatesComposition = dfa1.V();

		// store nodes waiting to visit
		List<Integer> working1 = new Stack<Integer>();
		List<Integer> working2 = new Stack<Integer>();

		// for each state, store the path from root to it
		List<List<int[]>> paths = new ArrayList<List<int[]>>();

		working1.add(dfa1.getInitState());
		working2.add(completeDFA2.getInitState());
		// add path to init
		paths.add(new ArrayList<int[]>());

		// check whether a node is visited or not
		boolean[] isVisited = new boolean[numStatesComposition * completeDFA2.V()];
		int hashInit = VerificationUltility.hash(dfa1.getInitState(),
				completeDFA2.getInitState(), numStatesComposition);
		isVisited[hashInit] = true;
		while (!working2.isEmpty()) {
			int currentState1 = working1.remove(0);
			int currentState2 = working2.remove(0);
			List<int[]> currentPath = paths.remove(0);

			// check acceptance condition, reach a state whether word is
			// accepted by automata1, but not automata2
			if (acceptingStates1.contains(currentState1)
					&& !acceptingStates2.contains(currentState2)) {
				return currentPath;
			}

			Iterable<DirectedEdge> edges1 = dfa1.adj(currentState1);
			Iterable<DirectedEdge> edges2 = completeDFA2.adj(currentState2);

			for (DirectedEdge edge1 : edges1) {
				//build from composition, edge is DirectedEdgeWithInputOutputCommon
				DirectedEdgeWithInputOutputCommon tempEdge1 = (DirectedEdgeWithInputOutputCommon) edge1;
				int input1 = tempEdge1.getInput();
				int output1 = tempEdge1.getOutput();
				int common = tempEdge1.getCommon();
				
				int dest1 = tempEdge1.to();
				for (DirectedEdge edge2 : edges2) {
					DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
					if (input1 == tempEdge2.getInput()
							&& output1 == tempEdge2.getOutput()) {
						int dest2 = tempEdge2.to();
						int hashValue = VerificationUltility.hash(dest1, dest2, numStatesComposition);
						if (!isVisited[hashValue]) {
							isVisited[hashValue] = true;

							//
							working1.add(dest1);
							working2.add(dest2);
							List<int[]> pathToChild = new ArrayList<int[]>(currentPath);

							pathToChild.add(new int[] { input1, common, output1 });//sequence of w1, w2, w3
							paths.add(pathToChild);
						}
					}

				}

			}
		}

		return null;
	}
	

}
