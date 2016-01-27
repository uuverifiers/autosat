package verification;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.VerificationUltility;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

public class InductivenessChecking {
    private static final Logger LOGGER = LogManager.getLogger();

    private Automata A;
    private Automata knownInv;
    private EdgeWeightedDigraph player;
    private int numLetters;
	
    /**
     * Make sure that I0, label starting from 1
     */
    public InductivenessChecking(Automata A,
				 Automata knownInv,
				 EdgeWeightedDigraph player,
				 int numLetters){
	this.A = A;
	this.knownInv = knownInv;
	this.player = player;
	this.numLetters = numLetters;
    }
	
    /**
     * Return 2 words x, y
     * result[0], result[1]
     */
    public List<List<Integer>> check() {
	Automata complementA = AutomataConverter.getComplement(A);

	int numStatesA = A.getStates().length;
	int numStatesCA = complementA.getStates().length;
	int numStatesKI = knownInv.getStates().length;
	int numStatesPlayer = player.V();

	EdgeWeightedDigraph product =
	    new EdgeWeightedDigraph(numStatesA * numStatesPlayer *
				    numStatesCA * numStatesKI);
	product.setInitState(VerificationUltility.hash(A.getInitState(),
						       player.getInitState(),
						       complementA.getInitState(),
						       knownInv.getInitState(),
						       numStatesA,
						       numStatesPlayer,
						       numStatesCA));

	//set accepting
	Set<Integer> acceptings = new HashSet<Integer>();
	for (int acceptAx: A.getAcceptingStates()) {
	    for (int acceptPlayer: player.getAcceptingStates()) {
		for (int acceptNAy: complementA.getAcceptingStates()) {
		    for (int acceptKI: knownInv.getAcceptingStates()) {
			acceptings.add(VerificationUltility.hash(acceptAx,
								 acceptPlayer,
								 acceptNAy,
								 acceptKI,
								 numStatesA,
								 numStatesPlayer,
								 numStatesCA));
		    }
		}
	    }
	}
	product.setAcceptingStates(acceptings);

	List<DirectedEdgeWithInputOutput> edgesA =
	    VerificationUltility.getEdges(A);
	List<DirectedEdgeWithInputOutput> edgesCA =
	    VerificationUltility.getEdges(complementA);
	List<DirectedEdgeWithInputOutput> edgesKI =
	    VerificationUltility.getEdges(knownInv);
	
	for(DirectedEdge edge: player.edges()) {
	    DirectedEdgeWithInputOutput edgePlayer = (DirectedEdgeWithInputOutput) edge;
	    for(DirectedEdgeWithInputOutput edgeAx: edgesA)
		if (edgePlayer.getInput() == edgeAx.getInput())
		    for(DirectedEdgeWithInputOutput edgeKI: edgesKI)
			if (edgeKI.getInput() == edgeAx.getInput())
			    for(DirectedEdgeWithInputOutput edgeAy: edgesCA)
				if (edgePlayer.getOutput() == edgeAy.getInput()) {
				    int source = VerificationUltility.hash(edgeAx.from(),
									   edgePlayer.from(),
									   edgeAy.from(),
									   edgeKI.from(),
									   numStatesA,
									   numStatesPlayer,
									   numStatesCA);
				    int dest = VerificationUltility.hash(edgeAx.to(),
									 edgePlayer.to(),
									 edgeAy.to(),
									 edgeKI.to(),
									 numStatesA,
									 numStatesPlayer,
									 numStatesCA);
			    
				    DirectedEdgeWithInputOutput newEdge =
					new DirectedEdgeWithInputOutput(source, dest,
									edgePlayer.getInput(),
									edgePlayer.getOutput());
				    product.addEdge(newEdge);
				}
	}
	
	List<DirectedEdge> edges =
	    product.DFS(product.getInitState(), product.getAcceptingStates());

	if (edges == null) {
	    return null;
	} else {
	    List<Integer> x = new ArrayList<Integer>();
	    List<Integer> y = new ArrayList<Integer>();

	    for (DirectedEdge _edge : edges) {
		DirectedEdgeWithInputOutput edge = (DirectedEdgeWithInputOutput)_edge;
		x.add(edge.getInput());
		y.add(edge.getOutput());
	    }

	    List<List<Integer>> result = new ArrayList<List<Integer>>();
	    result.add(x);
	    result.add(y);

	    return result;
	}
    }
}
