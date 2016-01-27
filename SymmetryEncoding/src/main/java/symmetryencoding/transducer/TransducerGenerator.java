package symmetryencoding.transducer;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import common.VerificationUltility;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class TransducerGenerator {
	/**
	 * Build graph for transducer from SAT solver
	 * states and inputs/outputs are counted from 0
	 * 
	 * @param model
	 * @param numStates
	 * @param numLetters
	 * @return
	 */
	public static EdgeWeightedDigraph fromSATModel(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates) {
		Set<Integer> newAccept = VerificationUltility.convertAccepting(acceptingStates);
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, 0,
				newAccept);

		int run = 0;
		for (int source = 0; source < numStates; source++) {
			for (int destination = 0; destination < numStates; destination++) {
				for (int input = 0; input < numLetters; input++) {
					for (int output = 0; output < numLetters; output++) {
						if (model[run] > 0) {
							graph.addEdge(new DirectedEdgeWithInputOutput(
									source, destination, input, output));
						}
						run++;
					}
				}
			}
		}
		return graph;
	}
	
	public static EdgeWeightedDigraph fromFile(Scanner scan, int numLetters){
		EdgeWeightedDigraph graph = parse(scan, numLetters);
		graph = TransducerConverter.toDFA(graph, numLetters);
		graph = makeFullySpecified(graph, numLetters);
		return graph;
	}
	
	public static EdgeWeightedDigraph makeFullySpecified(EdgeWeightedDigraph graph, int numberLetters){
		int numberStates = graph.V();
		
		//add 1 trap states
		int trapState = numberStates;
		EdgeWeightedDigraph result = new EdgeWeightedDigraph(numberStates + 1, graph.getInitState(), graph.getAcceptingStates());
		for(DirectedEdge edge: graph.edges()){
			result.addEdge(edge);
		}
		
		boolean[] hasTransition = new boolean[numberStates * numberLetters * numberLetters];
		for(DirectedEdge edge: graph.edges()){
			int source = edge.from();
			DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
			int input = tempEdge.getInput();
			int output = tempEdge.getOutput();
			
			int hash = hash(source, input, output, numberLetters);
			hasTransition[hash] = true;
		}
		
		//add edges to trap state
		for (int source = 0; source < numberStates; source++) {
			for (int input = 0; input < numberLetters; input++) {
				for (int output = 0; output < numberLetters; output++) {
					int hash = hash(source, input, output, numberLetters);
					if(!hasTransition[hash]){
						DirectedEdgeWithInputOutput trapEdge = new DirectedEdgeWithInputOutput(source, trapState, input, output);
						result.addEdge(trapEdge);
					}
				}
			}
		}
		
		//add loop in trap state
		for (int input = 0; input < numberLetters; input++) {
			for (int output = 0; output < numberLetters; output++) {
				DirectedEdgeWithInputOutput trapEdge = new DirectedEdgeWithInputOutput(trapState, trapState, input, output);
				result.addEdge(trapEdge);
			}
		}
		
		return result;
	}

	public static int hash(int source, int input, int output, int numberLetters) {
		int hash = output + numberLetters * (input + (numberLetters * source));
		return hash;
	}
	
	/**
	 * Format of the String
	 * numStates init
	 * numAccepting
	 * acceptingStates
	 * numTransition (don't count I*)
	 * transition: v w input output
	 * num I*
	 * states
	 * @param fileName
	 * @return
	 */
	public static EdgeWeightedDigraph parse(Scanner scan, int numLetters){
		int numStates = scan.nextInt();
		int init = scan.nextInt();
		
		int numAcceptingStates = scan.nextInt();
		Set<Integer> accepting = new HashSet<Integer>();
		for(int i = 0; i < numAcceptingStates; i++){
			accepting.add(scan.nextInt());
		}
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, init, accepting);
		
		int numTrans = scan.nextInt();
		for(int i = 0; i < numTrans; i++){
			int v = scan.nextInt();
			int w = scan.nextInt();
			int input = scan.nextInt();
			int output = scan.nextInt();
			
			DirectedEdge newEdge = new DirectedEdgeWithInputOutput(v, w, input, output);
			graph.addEdge(newEdge);
		}
		
		int numLoop = scan.nextInt();
		for(int i = 0; i < numLoop; i++){
			addIStartEdge(graph, scan.nextInt(), numLetters);
		}
				
		return graph;
	}
	
	private static void addIStartEdge(EdgeWeightedDigraph graph, int state, int numLetters){
		for(int i = 0; i < numLetters; i++){
			graph.addEdge(new DirectedEdgeWithInputOutput(state, state, i, i));
		}
	}
	
	private static final int epsilonLabel = -1;
	
	public static boolean isEpsilonEdge(DirectedEdge edge){
		DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
		return tempEdge != null && tempEdge.getInput() == epsilonLabel && tempEdge.getOutput() == epsilonLabel;
	}
}
