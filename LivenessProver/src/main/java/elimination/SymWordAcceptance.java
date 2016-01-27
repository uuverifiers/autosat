package elimination;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.finiteautomata.Automata;
import encoding.SatSolver;

public class SymWordAcceptance {
    private SatSolver solver;
    private Automata aut;
    private int wordLen;

    private int numLetters;
    private int autNumStates;

    public SymWordAcceptance(SatSolver solver,
                             Automata aut,
                             int wordLen) {
        this.solver = solver;
        // possible optimisation: minimise automaton before encoding it
        this.aut = aut;
        this.wordLen = wordLen;
        this.numLetters = aut.getNumLabels();
        this.autNumStates = aut.getStates().length;

        this.startIndex = solver.getNextSATVar();
        solver.setNextSATVar(this.startIndex + (wordLen + 1) * autNumStates + 1);
    }

    public int encode(int[][] symWord) throws ContradictionException {
        assert(wordLen == symWord.length);

        // at least one state is chosen per index
        for (int index = 0; index <= wordLen; ++index) {
            int[] clause = new int[autNumStates];
            for (int state = 0; state < autNumStates; ++state)
                clause[state] = getStateVar(index, state);
            solver.addClause(clause);
        }
        
        // initial states
        for (int state : aut.getEpsilonClosure(aut.getInitState()))
            solver.addClause(new int[] { getStateVar(0, state) });

        // final states
        for (int state = 0; state < autNumStates; ++state)
            if (!aut.getAcceptingStates().contains(state))
                solver.addClause(new int[] { -getStateVar(wordLen, state) });

        // transitions
        for (int index = 0; index < wordLen; ++index)
            for (int state1 = 0; state1 < autNumStates; ++state1)
                for (int letter = 0; letter < numLetters; ++letter) {
                    Set<Integer> sources = new HashSet<Integer>();
                    sources.add(state1);
                    Set<Integer> dests = aut.getEpsilonClosure(aut.getDests(sources, letter));
                    for (int state2 = 0; state2 < autNumStates; ++state2)
                        if (!dests.contains(state2))
                            solver.addClause(new int[] {
                                    -getAcceptance(),
                                    -getStateVar(index, state1),
                                    -getStateVar(index + 1, state2),
                                    -symWord[index][letter]
                                });
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
