package symmetryencoding.parasym.condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.VerificationUltility;
import common.bellmanford.BellmanFordSP;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import symmetryencoding.cegeneration.UpdateFromCounterExample;
import callback.Listener;

public class Parikh implements ConditionChecking{

	private Listener listener;
	public Parikh(Listener listener) {
		this.listener = listener;
	}

	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		List<Integer>[] counterExample = check(model, numStates, numLetters, acceptingStates);

		if(counterExample != null){
			updateSAT.notAccept(counterExample[0], counterExample[1]);
		}
		
		return (counterExample == null);
	}
	
	public List<Integer>[] check(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates){
		for(int a = 0; a < numLetters; a++){
			EdgeWeightedDigraph graph = buildWeightedGraph(model, numStates, numLetters, acceptingStates, a);
			List<DirectedEdge> singleChecking = check(graph);
			
			if(singleChecking != null){
				listener.inform("Parikh");
				listener.inform(String.valueOf(singleChecking));
				return convert(singleChecking);
			}
			
			
		}
		
		listener.inform("Parikh");
		return null;
	}
	
	public List<DirectedEdge> check(EdgeWeightedDigraph graph){
		
		int init = graph.getInitState();
		Set<Integer> acceptingStates = graph.getAcceptingStates();
		
		BellmanFordSP bellmanFord = new BellmanFordSP(graph, init);
		if(bellmanFord.hasNegativeCycle()){
			//get cycle
			List<DirectedEdge> edgesInCycle = new ArrayList<DirectedEdge>();
			for(DirectedEdge edge: bellmanFord.negativeCycle()){
				edgesInCycle.add(edge);
			}
			
			//get first state in cycle
			int stateInCycle = edgesInCycle.get(0).from();
			
			Set<Integer> statesInCycle = new HashSet<Integer>();
			statesInCycle.add(stateInCycle);
			List<DirectedEdge> pathToStateInCycle = graph.DFS(init, statesInCycle);
			int weightToStateInCycle = computeWeight(pathToStateInCycle);
			
			List<DirectedEdge> pathFromStateInCycleToAccepting = graph.DFS(stateInCycle, acceptingStates);
			int weightFromStateInCycleToAccepting = computeWeight(pathFromStateInCycleToAccepting);
			
			int numCyclesRequired = ((weightToStateInCycle + weightFromStateInCycleToAccepting) != 0)? 0: 1;
			
			List<DirectedEdge> result = new ArrayList<DirectedEdge>();
			
			//add path to state in cyle
			result.addAll(pathToStateInCycle);
			
			//add cycle
			for(int i = 0; i < numCyclesRequired; i++){
				result.addAll(edgesInCycle);
			}
			
			//add path to accepting
			result.addAll(pathFromStateInCycleToAccepting);
			
			return result;
			
		}
		else{
			for(int acceptingState: acceptingStates){
				if(bellmanFord.distTo(acceptingState) != 0){
					
					//edges in the direction from goal to init
					List<DirectedEdge> result = new LinkedList<DirectedEdge>();
					for(DirectedEdge edge: bellmanFord.pathTo(acceptingState)){
						result.add(0, edge);
					}
					
					return result;
				}
			}
		}
		
		return null;
	}

	private EdgeWeightedDigraph buildWeightedGraph(int[] model, int numStates,
			int numLetters, Set<Integer> acceptingStates, int a) {
		int init = 0;
		Set<Integer> newAcceptingStates = VerificationUltility.convertAccepting(acceptingStates);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, init, newAcceptingStates);
		
		int run = 0;
		for(int source = 0; source < numStates; source++){
			for(int destination = 0; destination < numStates; destination++){
				int weight = Integer.MAX_VALUE;
				int selectedInput = 0;
				int selectedOutput = 0;
				
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						if(model[run] > 0){
							if(input == a && output != a){
								if(1 < weight){
									weight = 1;
									selectedInput = input;
									selectedOutput = output;
								}
								
							}
							else if(input != a && output == a){
								if(-1 < weight){
									weight = -1;
									selectedInput = input;
									selectedOutput = output;
								}
							}
							else{
								if(0 < weight){
									weight = 0;
									selectedInput = input;
									selectedOutput = output;
								}
							}
						}
						run++;
					}
				}
				
				//
				if(weight != Integer.MAX_VALUE){
					graph.addEdge(new DirectedEdgeWithInputOutput(source, destination, weight, selectedInput, selectedOutput));
				}
				
			}
		}
		return graph;
	}
	
	private int computeWeight(List<DirectedEdge> edges){
		int weight = 0;
		
		for(DirectedEdge edge: edges){
			weight += edge.weight();
		}
		
		return weight;
	}
	
	/**
	 * Convert from edges to labels
	 * @param edges
	 * @return
	 */
	private List<Integer>[] convert(List<DirectedEdge> edges){
		if(edges == null){
			return null;
		}
		
		List<Integer>[] result = new List[]{new ArrayList<Integer>(), new ArrayList<Integer>()};
		
		for(DirectedEdge edge: edges){
			DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
			result[0].add(tempEdge.getInput());
			result[1].add(tempEdge.getOutput());
		}
		
		return result;
	}
}
