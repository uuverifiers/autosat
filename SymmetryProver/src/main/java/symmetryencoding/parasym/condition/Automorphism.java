package symmetryencoding.parasym.condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

import symmetryencoding.cegeneration.UpdateFromCounterExample;
import symmetryencoding.parasym.DEdgeWith4Labels;
import symmetryencoding.transducer.TransducerGenerator;
import callback.Listener;

public class Automorphism implements ConditionChecking {

	private EdgeWeightedDigraph automorphismGraph;
	private Listener listener;
	
	public Automorphism(EdgeWeightedDigraph automorphismGraph, Listener listener){
		this.automorphismGraph = automorphismGraph;
		this.listener = listener;
	}

	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		List<Integer>[] counterExample = check(model, numStates, numLetters, acceptingStates, automorphismGraph);
		if(counterExample != null){
			updateSAT.acceptAtMostOne(counterExample[0], counterExample[1], counterExample[2], counterExample[3]);
		}
		
		return (counterExample == null);
	}
	
	private List<Integer>[] check(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, EdgeWeightedDigraph Q){
		EdgeWeightedDigraph R = TransducerGenerator.fromSATModel(model, numStates, numLetters, acceptingStates);
		EdgeWeightedDigraph production = productConstruction(Q, R);
		
		List<DirectedEdge> edges = production.DFS(production.getInitState(), production.getAcceptingStates());
		
		listener.inform("Automorphism");
		listener.inform(String.valueOf(edges));
		
		return convert(edges);
	}
	
	private EdgeWeightedDigraph productConstruction(EdgeWeightedDigraph Q,
			EdgeWeightedDigraph R) {

		int numStateQ = Q.V();
		int numStateR = R.V();
		
		//compute numStates, init, accepting
		int numState = numStateQ * numStateQ * numStateR * numStateR;
		int init = 0;
		
		Set<Integer> QAccept = Q.getAcceptingStates();
		Set<Integer> RAccept = R.getAcceptingStates();
		
		Set<Integer> acceptingStates = new HashSet<Integer>();
		
		for(Integer q1: QAccept){
			for(Integer r1: RAccept){
				for(Integer r2: RAccept){
					for(int q2 = 0; q2 < Q.V(); q2++){
						if(!QAccept.contains(q2)){
							acceptingStates.add(hash(q1, q2, r1, r2, numStateQ, numStateR));
						}
					}
				}
			}
		}
		
		EdgeWeightedDigraph production = new EdgeWeightedDigraph(numState, init, acceptingStates);
		
		//build transitions
		for(DirectedEdge edgeFromQ1: Q.edges()){
			DirectedEdgeWithInputOutput tempEdgeQ1 = (DirectedEdgeWithInputOutput) edgeFromQ1;

			for(DirectedEdge edgeFromR1: R.edges()){
				DirectedEdgeWithInputOutput tempEdgeR1 = (DirectedEdgeWithInputOutput) edgeFromR1;

                                if (tempEdgeR1.getInput() != tempEdgeQ1.getInput())
                                    continue;

				for(DirectedEdge edgeFromR2: R.edges()){
					DirectedEdgeWithInputOutput tempEdgeR2 = (DirectedEdgeWithInputOutput) edgeFromR2;

                                        if (tempEdgeR2.getInput() != tempEdgeQ1.getOutput())
                                            continue;
                                
					for(DirectedEdge edgeFromQ2: Q.edges()){
						DirectedEdgeWithInputOutput tempEdgeQ2 = (DirectedEdgeWithInputOutput) edgeFromQ2;
						
                                                if (tempEdgeQ2.getInput() != tempEdgeR1.getOutput() ||
                                                    tempEdgeQ2.getOutput() != tempEdgeR2.getOutput())
                                                    continue;

                                                DEdgeWith4Labels newEdge =
                                                    new DEdgeWith4Labels(hash(edgeFromQ1.from(),
                                                                              edgeFromQ2.from(),
                                                                              edgeFromR1.from(),
                                                                              edgeFromR2.from(),
                                                                              numStateQ,
                                                                              numStateR),
                                                                         hash(edgeFromQ1.to(),
                                                                              edgeFromQ2.to(),
                                                                              edgeFromR1.to(),
                                                                              edgeFromR2.to(),
                                                                              numStateQ,
                                                                              numStateR),
                                                                         tempEdgeQ1.getInput(),
                                                                         tempEdgeQ1.getOutput(),
                                                                         tempEdgeQ2.getInput(),
                                                                         tempEdgeQ2.getOutput());
                                                production.addEdge(newEdge);
						
					}
				}
			}
		}
		
		return production;
	}

	private int hash(int q1, int q2, int r1, int r2, int numStateQ,
			int numStateR) {
		return r2 + numStateR * (r1 + numStateR * (q1 + numStateQ * q2));
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
		
		List<Integer>[] result = new List[]{new ArrayList<Integer>(), new ArrayList<Integer>(),new ArrayList<Integer>(),new ArrayList<Integer>()};
		
		for(DirectedEdge edge: edges){
			DEdgeWith4Labels tempEdge = (DEdgeWith4Labels) edge;
			result[0].add(tempEdge.getLabel1());
			result[1].add(tempEdge.getLabel3());
			
			result[2].add(tempEdge.getLabel2());
			result[3].add(tempEdge.getLabel4());
		}
		
		return result;
	}
}
