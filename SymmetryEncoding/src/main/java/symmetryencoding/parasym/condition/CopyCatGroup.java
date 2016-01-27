package symmetryencoding.parasym.condition;

import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

import symmetryencoding.cegeneration.UpdateFromCounterExample;
import symmetryencoding.transducer.TransducerGenerator;

public class CopyCatGroup implements ConditionChecking{
	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		
		EdgeWeightedDigraph graph = TransducerGenerator.fromSATModel(model, numStates, numLetters, acceptingStates);

		boolean[] candidates = new boolean[numStates + 1];
		for (int q = 1; q <= numStates; ++q)
			candidates[q] = acceptingStates.contains(q);

		boolean changed = true;
		while (changed) {
			changed = false;

			for (int q = 1; q <= numStates; ++q)
				if (candidates[q]) {
					boolean[] copyEdges = new boolean[numLetters];
					for (int i = 0; i < numLetters; ++i)
						copyEdges[i] = false;

					for (DirectedEdge preEdge : graph.adj(q - 1)) {
						DirectedEdgeWithInputOutput edge = (DirectedEdgeWithInputOutput) preEdge;
						if (edge.getInput() == edge.getOutput()) {
							if (!candidates[edge.to() + 1]) {
								candidates[q] = false;
								changed = true;
								break;
							}
							copyEdges[edge.getInput()] = true;
						}
					}

					for (int i = 0; i < numLetters; ++i)
						if (!copyEdges[i]) {
							candidates[q] = false;
							changed = true;
							break;
						}
				}
		}

		int num = 0;
		for (int q = 1; q <= numStates; ++q) {
			if (candidates[q])
				num += 1;
		}

		if (num > 1) {
			updateSAT.updateCopyCatGroups(numStates, numLetters, candidates, num);
		}

		return (num <= 1);
	}
}
