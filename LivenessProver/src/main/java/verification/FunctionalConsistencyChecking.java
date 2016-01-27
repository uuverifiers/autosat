package verification;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.sat4j.specs.ContradictionException;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

/**
 * Check whether there exists x, y1, y2 such that (x, y1) and (x, y2) accepted by the transducer
 * @author khanh
 *
 */
public class FunctionalConsistencyChecking {

    public List<List<Integer>> check(EdgeWeightedDigraph function) {
	EdgeWeightedDigraph productGraph = productConstruction(function);
	List<DirectedEdge> edges = checkProduct(productGraph);
	List<Integer>[] words = convert(edges);

	if (words == null)
	    return null;

	List<List<Integer>> res = new ArrayList<List<Integer>>();
	res.add(words[0]);
	res.add(words[1]);
	res.add(words[3]);

	return res;
    }
	
	private List<DirectedEdge> checkProduct(EdgeWeightedDigraph productGraph){
		
		//store the path from root to current Node
		List<DirectedEdge> path = new ArrayList<DirectedEdge>();
		//for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();
		
		//store nodes waiting to visit
		Stack<DirectedEdge> workingStates = new Stack<DirectedEdge>();
		DirectedEdge dummyEdge = createDummyEdge(productGraph.getInitState());

		workingStates.push(dummyEdge);
		
		//for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.push(0);
		
		//check whether an edge is visited or not
		HashSet<DirectedEdge> isVisited = new HashSet<DirectedEdge>();
		while(!workingStates.isEmpty()){
			DirectedEdge currentEdge = workingStates.pop();
			int currentState = currentEdge.to();
			int depthLevel = depthStack.pop();
		
			while(depthList.size() > 0){
					int lastDepth = depthList.get(depthList.size() - 1);
					if(lastDepth >= depthLevel){
						//back track a new node, remove nodes not in the path to this node (having depth level greater than or equal its depth level
						depthList.remove(depthList.size() - 1);
						path.remove(path.size() - 1);
					}
					else{
						break;
					}
			}
			
			//add this node and its depth level
			path.add(currentEdge);
			depthList.add(depthLevel);
		
			//check output1 != output2
			if(edgeToFind(currentEdge)){
				//check reachable from currentEdge.to() to an accepting state
				List<DirectedEdge> pathToAcc = productGraph.DFS(currentEdge.to(), productGraph.getAcceptingStates());
				if(pathToAcc != null){
					path.addAll(pathToAcc);
					
					//remove dummy
					path.remove(0);
					return path;
				}
			}

		
			//add new states to workingState
			for(DirectedEdge edge: productGraph.adj(currentState)){
				if(!isVisited.contains(edge)){
					workingStates.push(edge);
					depthStack.push(depthLevel+1);
					
					isVisited.add(edge);
				}
			}
		}
		
		return null;
	}
	
	protected DirectedEdge createDummyEdge(int init){
		return new DEdgeWithInput2Output(0, init, 0, 0, 0);
	}
	
	protected boolean edgeToFind(DirectedEdge edge){
		DEdgeWithInput2Output tempEdge = (DEdgeWithInput2Output) edge;
		return (tempEdge.output1 != tempEdge.output2);
	}
	
	private EdgeWeightedDigraph productConstruction(EdgeWeightedDigraph singleGraph){
		int numState = singleGraph.V();
		int init = hash(singleGraph.getInitState(), singleGraph.getInitState(), numState);
		Set<Integer> newAcceptedStates = new HashSet<Integer>();
		for(Integer acceptState1: singleGraph.getAcceptingStates()){
			for(Integer acceptState2: singleGraph.getAcceptingStates()){
				newAcceptedStates.add(hash(acceptState1, acceptState2, numState));
			}
		}
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numState * numState, init, newAcceptedStates);
		
		for(DirectedEdge edge1: singleGraph.edges()){
			for(DirectedEdge edge2: singleGraph.edges()){
				addNewEdge(edge1, edge2, numState, graph);
			}
		}
		
		return graph;
	}
	
	/**
	 * If edge1, edge2 have the same input, create new edge with that input and 2 outputs from them
	 * add that edge to graph
	 * @param edge1
	 * @param edge2
	 * @param numState
	 * @return
	 */
	protected void addNewEdge(DirectedEdge edge1, DirectedEdge edge2, int numState, EdgeWeightedDigraph graph){
		DirectedEdgeWithInputOutput tempEdge1 = (DirectedEdgeWithInputOutput) edge1;
		DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
		
		if(tempEdge1.getInput() == tempEdge2.getInput()){
			int q1 = edge1.from();
			int q2 = edge2.from();
			int q3 = edge1.to();
			int q4 = edge2.to();
			
			DEdgeWithInput2Output newEdge = new DEdgeWithInput2Output(hash(q1, q2, numState), hash(q3, q4, numState),
					tempEdge1.getInput(), tempEdge1.getOutput(), tempEdge2.getOutput());
			graph.addEdge(newEdge);
		}
	}

	/**
	 * 
	 * @param q1 run from 0 to numState-1
	 * @param q2 run from 0 to numState-1
	 * @param numState
	 * @return
	 */
	protected int hash(int q1, int q2, int numState){
		return q1 * numState + q2;
	}
	
	/**
	 * Convert from edges to labels
	 * @param edges
	 * @return
	 */
	protected List<Integer>[] convert(List<DirectedEdge> edges){
		if(edges == null){
			return null;
		}
		
		List<Integer>[] result = new List[]{new ArrayList<Integer>(), new ArrayList<Integer>(),new ArrayList<Integer>(),new ArrayList<Integer>()};
		
		for(DirectedEdge edge: edges){
			DEdgeWithInput2Output tempEdge = (DEdgeWithInput2Output) edge;
			result[0].add(tempEdge.getInput());
			result[1].add(tempEdge.getOutput1());
			
			result[2].add(tempEdge.getInput());
			result[3].add(tempEdge.getOutput2());
		}
		
		return result;
	}
	
	public static class DEdgeWithInput2Output extends DirectedEdge{

		private int input;
		private int output1;
		private int output2;
		
		public DEdgeWithInput2Output(int v, int w, int input, int output1, int output2) {
			super(v, w);
			// TODO Auto-generated constructor stub
			this.input = input;
			this.output1 = output1;
			this.output2 = output2;
		}

		public int getInput() {
			return input;
		}

		public int getOutput1() {
			return output1;
		}
		
		public int getOutput2() {
			return output2;
		}
		
	    /**
	     * Returns a string representation of the directed edge.
	     * @return a string representation of the directed edge
	     */
	    public String toString() {
	        return this.from() + " " + input + "/(" + output1 + "," + output2 + ") " + this.to();
	    }
	}
}
