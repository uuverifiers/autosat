package elimination;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import encoding.AutomataEncoding;
import encoding.ISatSolver;


public class AcceptanceTree {
    private final AutomataEncoding encoding;
    private final ISatSolver solver;
    private final int numStates;
    private final int numLetters;
    private final int trueVar;

    private final Node root;

    public AcceptanceTree(AutomataEncoding encoding)
                        throws ContradictionException {
        this.encoding = encoding;
        this.solver = encoding.getSolver();
        this.numStates = encoding.getNumStates();
        this.numLetters = encoding.getNumLabels();

        root = new Node();

	if (numStates > 0)
	    solver.addClause(new int[] { root.getStateVar(1) });

	trueVar = solver.getNextSATVar();
	solver.setNextSATVar(trueVar + 1);
	solver.addClause(new int[] { trueVar });
    }
    
    /**
     * Return the acceptance variable for the given word, or 0
     * otherwise
     */
    public int contains(List<Integer> v) {
	if (numStates == 0)
	    return 0;
	
        Node n = root.lookup(v.iterator());
        if (n == null)
            return 0;
        else
            return n.getAcceptVar();
    }

    public int insert(List<Integer> v)
                     throws ContradictionException {
	if (numStates == 0)
	    return trueVar;
	else
	    return root.insert(v.iterator()).getAcceptVar();
    }

    ////////////////////////////////////////////////////////////////////////////

    private class Node {
        private final int stateVar;
        private final int acceptVar;

        private final Node[] children;

        public Node() throws ContradictionException {
            stateVar = solver.getNextSATVar();
            acceptVar = stateVar + numStates;
            solver.setNextSATVar(acceptVar + 1);
            children = new Node[numLetters];

            // at most one of the state variables is set
            for (int q1 = 1; q1 <= numStates; ++q1)
                for (int q2 = q1 + 1; q2 <= numStates; ++q2)
                    solver.addClause
                        (new int[] { -getStateVar(q1), -getStateVar(q2) });

            // if any state is selected, the word is accepted if the
            // state is accepting
            for (int q = 1; q <= numStates; ++q) {
                solver.addClause
                    (new int[] { -getStateVar(q),
                                 -encoding.getIndexZVar(q),
                                 getAcceptVar() });
                solver.addClause
                    (new int[] { -getStateVar(q),
                                 encoding.getIndexZVar(q),
                                 -getAcceptVar() });
            }

            // if no final state was reached, the word was not accepted
            int[] clause = new int [numStates + 1];
            clause[0] = -getAcceptVar();
            for (int q = 1; q <= numStates; ++q)
                clause[q] = getStateVar(q);
            solver.addClause(clause);
        }

        public Node lookup(Iterator<Integer> v) {
            if (!v.hasNext())
                return this;
            final int nv = v.next();
            if (children[nv] == null)
                return null;
            return children[nv].lookup(v);
        }

        public Node insert(Iterator<Integer> v)
                                     throws ContradictionException {
            if (!v.hasNext())
                return this;
            final int nv = v.next();

            if (children[nv] == null) {
                final Node n = new Node();
                children[nv] = n;

                for (int q1 = 1; q1 <= numStates; ++q1)
                    for (int q2 = 1; q2 <= numStates; ++q2) {
                        // enabled transition implies that the
                        // successor state is active
                        solver.addClause
                            (new int[] { -getStateVar(q1),
                                         -encoding.getTransBoolVar(q1, nv, q2),
                                         n.getStateVar(q2) });

                        // if no transition is defined, no successor
                        // state is active
                        solver.addClause
                            (new int[] { -getStateVar(q1),
                                         encoding.getTransBoolVar(q1, nv, q2),
                                         -n.getStateVar(q2) });
                    }
                
                // if any successor state is active, also some current
                // state is active
                for (int q1 = 1; q1 <= numStates; ++q1) {
                    int[] clause = new int [numStates + 1];
                    clause[0] = -n.getStateVar(q1);
                    for (int q2 = 1; q2 <= numStates; ++q2)
                        clause[q2] = getStateVar(q2);
                    solver.addClause(clause);
                }
            }
            
            return children[nv].insert(v);
        }

        // q starts from 1
        public int getStateVar(int q) {
            return stateVar + q - 1;
        }

        public int getAcceptVar() {
            return acceptVar;
        }
    }
}
