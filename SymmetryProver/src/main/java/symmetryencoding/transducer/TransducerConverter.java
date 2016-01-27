package symmetryencoding.transducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class TransducerConverter {

	public static EdgeWeightedDigraph toDFA(EdgeWeightedDigraph graph, int numLetters){
		Map<Set<Integer>, Integer> mapStates = new HashMap<Set<Integer>, Integer>();
		
		Stack<Set<Integer>> workingStates = new Stack<Set<Integer>>();
		Set<Integer> initSet = new HashSet<Integer>();
		initSet.add(graph.getInitState());
		initSet = getEpsilonClosure(graph, initSet);
		
		workingStates.push(initSet);
		
		//state 0 will be the init state in new DFA
		int initDFA = 0;
		mapStates.put(initSet, initDFA);
		
		List<DirectedEdge> edgesInDFA = new ArrayList<DirectedEdge>();
		while(!workingStates.isEmpty()){
			Set<Integer> statesInNFA = workingStates.pop();
			int stateInDFA = mapStates.get(statesInNFA);
			
			for(int input = 0; input < numLetters; input++ ){
				for(int output = 0; output < numLetters; output++){
					Set<Integer> destsInNFA = getEpsilonClosure(graph, getDests(graph, statesInNFA, input, output));
					
					if(!destsInNFA.isEmpty()){
						int destInDFA = 0;
						if(!mapStates.containsKey(destsInNFA)){
							destInDFA = mapStates.size();
							mapStates.put(destsInNFA, destInDFA);
							
							//add new state
							workingStates.push(destsInNFA);
						}
						else{
							destInDFA = mapStates.get(destsInNFA);
						}
						
						DirectedEdge newEdge = new DirectedEdgeWithInputOutput(stateInDFA, destInDFA, input, output);
						edgesInDFA.add(newEdge);
					}
				}
			}
		}
		
		//compute accepting states
		Set<Integer> acceptingDFA = new HashSet<Integer>();
		for(Set<Integer> statesNFA: mapStates.keySet()){
			for(Integer stateNFA: statesNFA){
				if(graph.getAcceptingStates().contains(stateNFA)){
					acceptingDFA.add(mapStates.get(statesNFA));
					break;
				}
			}
		}
				
				
		EdgeWeightedDigraph dfa = new EdgeWeightedDigraph(mapStates.size(), initDFA, acceptingDFA);
		for(DirectedEdge edge: edgesInDFA){
			dfa.addEdge(edge);
		}
		
		//
		return dfa;
	}
	
	
	/**
	 * Return set of destination such that there exists a transition from state in sources with input and output to destination state
	 * @param graph
	 * @param sources
	 * @param input
	 * @param output
	 * @return
	 */
	private static Set<Integer> getDests(EdgeWeightedDigraph graph, Set<Integer> sources, int input, int output){
		Set<Integer> result = new HashSet<Integer>();
		for(int source: sources){
			result.addAll(getDests(graph, source, input, output));
		}
		
		return result;
		
	}
	
	private static Set<Integer> getDests(EdgeWeightedDigraph graph, int source, int input, int output){
		Set<Integer>  result = new HashSet<Integer>();
		for(DirectedEdge edge: graph.adj(source)){
			if(hasInputOutput(edge, input, output)){
				result.add(edge.to());
			}
		}
		
		return result;
	}
	
	
	private static boolean hasInputOutput(DirectedEdge edge, int input, int output){
		DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
		return tempEdge != null && tempEdge.getInput() == input && tempEdge.getOutput() == output;
	}
	
	private static Set<Integer> getEpsilonClosure(EdgeWeightedDigraph graph, Set<Integer> fromStates){
		Set<Integer> result = new HashSet<Integer>();
		
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.addAll(fromStates);
		
		boolean [] isVisited = new boolean[graph.V()];
		for(int init: fromStates){
			isVisited[init] = true;
		}
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();

			result.add(currentState);
			
			//add new states to workingState
			for(DirectedEdge edge: graph.adj(currentState)){
				if(TransducerGenerator.isEpsilonEdge(edge) && !isVisited[edge.to()]){
					workingStates.push(edge.to());
					
					isVisited[edge.to()] = true;
				}
			}
		}
		
		return result;
	}
}
