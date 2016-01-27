package elimination;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import encoding.SatSolver;

public class SymWordAcceptanceTransducer {
    private SatSolver solver;
    private EdgeWeightedDigraph transducer;
    private int wordLen;

    private int numLetters;
    private int autNumStates;

    public SymWordAcceptanceTransducer(SatSolver solver,
                                       EdgeWeightedDigraph transducer,
                                       int wordLen,
                                       int numLetters) {
        this.solver = solver;
        // possible optimisation: minimise automaton before encoding it
        this.transducer = transducer;
        this.wordLen = wordLen;
        this.numLetters = numLetters;
        this.autNumStates = transducer.V();

        this.startIndex = solver.getNextSATVar();
        solver.setNextSATVar(this.startIndex + (wordLen + 1) * autNumStates + 1);
    }

    public int encode(List<Integer> concInput,
                      int[][] symOutput) throws ContradictionException {
        assert(concInput.size() == wordLen && symOutput.length == wordLen);

        // at least one state is chosen per index
        for (int index = 0; index <= wordLen; ++index) {
            int[] clause = new int[autNumStates];
            for (int state = 0; state < autNumStates; ++state)
                clause[state] = getStateVar(index, state);
            solver.addClause(clause);
        }
        
        // initial states
        solver.addClause(new int[] { getStateVar(0, transducer.getInitState()) });

        // final states
        for (int state = 0; state < autNumStates; ++state)
            if (!transducer.getAcceptingStates().contains(state))
                solver.addClause(new int[] { -getStateVar(wordLen, state) });

        // transitions
        for (int index = 0; index < wordLen; ++index)
            for (int state1 = 0; state1 < autNumStates; ++state1) {
                Iterable<DirectedEdge> outgoing = transducer.adj(state1);
                for (int letter = 0; letter < numLetters; ++letter)
                    for (int state2 = 0; state2 < autNumStates; ++state2) {
                        boolean found = false;
                        for (DirectedEdge edge : outgoing) {
                            DirectedEdgeWithInputOutput ioEdge =
                                (DirectedEdgeWithInputOutput)edge;
                            if (ioEdge.getInput() == concInput.get(index) &&
                                ioEdge.getOutput() == letter &&
                                ioEdge.to() == state2)
                                found = true;
                        }

                        if (!found)
                            solver.addClause(new int[] {
                                    -getAcceptance(),
                                    -getStateVar(index, state1),
                                    -getStateVar(index + 1, state2),
                                    -symOutput[index][letter]
                                });
                    }
            }

        return getAcceptance();
    }

    private int startIndex;

    /**
     * index starts from 0, state starts from 0.
     */
    private int getStateVar(int index, int state) {
        return startIndex + index * autNumStates + state + 1;
    }

    private int getAcceptance() {
        return startIndex;
    }
}
