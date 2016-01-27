package encoding;

import java.util.HashSet;
import java.util.Set;

import common.VerificationUltility;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;

public class BoolValToAutomaton {

	/**
	 * Build graph for transducer from SAT solver
	 * states and inputs/outputs are counted from 0
	 */
	public static EdgeWeightedDigraph toTransducer(Set<Integer> modelPosVars, ITransducerEncoding encoding) {
		int numStates = encoding.getNumStates();
		int numLetters = encoding.getNumLetters();
		
		Set<Integer> accepting = extractAcceptingStates(modelPosVars, encoding.getStartIndexOfZVars(), numStates);
		Set<Integer> newAccept = VerificationUltility.convertAccepting(accepting);
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, 0, newAccept);

		int run = encoding.getStartIndexOfTransVars();
		for (int source = 0; source < numStates; source++) {
			for (int destination = 0; destination < numStates; destination++) {
				for (int input = 0; input < numLetters; input++) {
					for (int output = 0; output < numLetters; output++) {
                                                if (modelPosVars.contains(run)) {
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

    public static EdgeWeightedDigraph buildLexOrder(EdgeWeightedDigraph rf) {
	int numRFStates = rf.V();
	Set<Integer> rfAccept = rf.getAcceptingStates();
	Set<Integer> newAccept = new HashSet<Integer> ();

	for (int a : rfAccept)
	    for (int b : rfAccept)
		newAccept.add(VerificationUltility.hash(a, b, 1, numRFStates, numRFStates));
		
	EdgeWeightedDigraph result =
	    new EdgeWeightedDigraph(numRFStates * numRFStates * 3,
				    VerificationUltility.hash(rf.getInitState(),
							      rf.getInitState(), 0,
							      numRFStates, numRFStates),
				    newAccept);

	for(DirectedEdge edge1: rf.edges()) {
	    DirectedEdgeWithInputOutput tempEdge1 = (DirectedEdgeWithInputOutput) edge1;
	    for(DirectedEdge edge2: rf.edges()) {
		DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;

		{
		    // transitions outgoing from the initial state
		    int target = 0;
		    if (tempEdge1.getOutput() < tempEdge2.getOutput())
			target = 1;
		    if (tempEdge1.getOutput() > tempEdge2.getOutput())
			target = 2;
		    
		    DirectedEdge newEdge = new DirectedEdgeWithInputOutput
			(VerificationUltility.hash(tempEdge1.from(), tempEdge2.from(), 0,
						   numRFStates, numRFStates),
			 VerificationUltility.hash(tempEdge1.to(), tempEdge2.to(), target,
						   numRFStates, numRFStates),
			 tempEdge1.getInput(), tempEdge2.getInput());
		    result.addEdge(newEdge);
		}
		
		for (int s = 1; s <= 2; ++s) {
		    DirectedEdge newEdge = new DirectedEdgeWithInputOutput
			(VerificationUltility.hash(tempEdge1.from(), tempEdge2.from(), s,
						   numRFStates, numRFStates),
			 VerificationUltility.hash(tempEdge1.to(), tempEdge2.to(), s,
						   numRFStates, numRFStates),
			 tempEdge1.getInput(), tempEdge2.getInput());
		    result.addEdge(newEdge);
		}
	    }
	}
	
	return result;
    }

	/**
	 * Extract acceptantance states from SAT
	 * States are counted from 1
	 */
	public static Set<Integer> extractAcceptingStates(Set<Integer> modelPosVars, int startOfBoolForAccepting, int numStates) {
		Set<Integer> acceptingStates = new HashSet<Integer>();
		
		int run = startOfBoolForAccepting;
		for(int q = 1; q <= numStates; q++){
                        if(modelPosVars.contains(run)){
				acceptingStates.add(q);
			}
			run++;
		}

		return acceptingStates;
	}
	
	/**
	 * Build automaton from SAT solver
	 * states counted from 0, label counted from 0
	 */
	public static Automata toAutomata(Set<Integer> modelPosVars, AutomataEncoding encoding) {
		int numStates = encoding.getNumStates();
		int numLetters = encoding.getNumLabels();

		if (numStates < 1) {
		    // generate an automaton that accepts everything

		    //state count from 0, label count from 0
		    Automata graph = new Automata(0, 1, numLetters);
		    Set<Integer> newAccept = new HashSet<Integer>();
		    newAccept.add(0);
		    graph.setAcceptingStates(newAccept);

		    for(int label = 0; label < numLetters; label++)
			graph.addTrans(0, label, 0);
		    
		    return graph;
		}
		
		Set<Integer> accepting = extractAcceptingStates(modelPosVars, encoding.getStartIndexOfZVars(), numStates);
		Set<Integer> newAccept = VerificationUltility.convertAccepting(accepting);
		
		//state count from 0, label count from 0
		Automata graph = new Automata(0, numStates, numLetters);
		graph.setAcceptingStates(newAccept);
		
		int run = encoding.getStartIndexOfTransVars();
		for(int source = 0; source < numStates; source++){
			for(int label = 0; label < numLetters; label++){
				for(int dest = 0; dest < numStates; dest++){
                                        if(modelPosVars.contains(run)){
						graph.addTrans(source, label, dest);
					}
					run++;
				}
			}
		}

		return graph;
	}
}
