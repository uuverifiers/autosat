package symmetryencoding.parasym.condition;

import java.util.Set;
import java.util.Stack;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class GraphUltililty {

	/**
     * Check whether a state in goal reachable from root
     * @param root
     * @param goal
     * @param graph
     * @return
     */
	public static boolean isReachableByO1Trans(int root, Set<Integer> goal, EdgeWeightedDigraph graph){
		//store nodes waiting to visit
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.push(root);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[graph.V()];
		isVisited[root] = true;
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			

			//check reachable
			if(goal.contains(currentState)){
				return true;
			}

			//add new states to workingState
			for(DirectedEdge edge: graph.adj(currentState)){
				DirectedEdgeWithInputOutput edgeTemp = (DirectedEdgeWithInputOutput) edge;
				if(isBinaryTrans(edgeTemp) && !isVisited[edge.to()]){
					workingStates.push(edge.to());
					
					isVisited[edge.to()] = true;
				}
			}
		}
		
		return false;

	}
	
	private static boolean isBinaryTrans(DirectedEdgeWithInputOutput edge){
		return edge.getInput() <= 1 && edge.getOutput() <= 1;
	}
}
