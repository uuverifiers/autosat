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

import encoding.TransducerEncoding;
import encoding.ISatSolver;


public class PairAcceptanceTree {
    private final TransducerEncoding encoding;
    private final ISatSolver solver;
    private final int numStates;
    private final int numLetters;

    private final Node root;

    public PairAcceptanceTree(TransducerEncoding encoding)
                        throws ContradictionException {
        this.encoding = encoding;
        this.solver = encoding.getSolver();
        this.numStates = encoding.getNumStates();
        this.numLetters = encoding.getNumLetters();

        root = new Node();

	solver.addClause(new int[] { root.getStateVar(1) });
    }
    
    /**
     * Return the acceptance variable for the given word, or 0
     * otherwise
     */
    public int contains(List<Integer> v, List<Integer> w) {
        Node n = root.lookup(v.iterator(), w.iterator());
        if (n == null)
            return 0;
        else
            return n.getAcceptVar();
    }

    public int insert(List<Integer> v, List<Integer> w)
                     throws ContradictionException {
        return root.insert(v.iterator(), w.iterator()).getAcceptVar();
    }

    public void visit(PairAcceptanceTreeVisitor visitor)
                          throws ContradictionException {
        root.visit(new ArrayList<Integer>(), new ArrayList<Integer>(),
                   visitor);
    }

    public LinkedHashSet<List<Integer>> allWords() {
	final LinkedHashSet<List<Integer>> res =
	    new LinkedHashSet<List<Integer>>();

	try {
	    visit(new PairAcceptanceTreeVisitor() {
		    public void visit(final List<Integer> a,
				      final List<Integer> b,
				      final int acc)
			throws ContradictionException {
			res.add(cloneList(a));
			res.add(cloneList(b));
		    }});
	} catch (ContradictionException e) {}

	return res;
    }

    public Map<List<Integer>, List<List<Integer>>>
        enabledEdges(final Set<Integer> setVars) {

	final Map<List<Integer>, List<List<Integer>>> res =
            new HashMap<List<Integer>, List<List<Integer>>> ();

	try {
	    visit(new PairAcceptanceTreeVisitor() {
		    public void visit(final List<Integer> a,
				      final List<Integer> b,
				      final int abAccept)
			throws ContradictionException {
			if (setVars.contains(abAccept)) {
                            List<List<Integer>> succs = res.get(a);
                            if (succs == null) {
                                succs = new ArrayList<List<Integer>>();
                                res.put(cloneList(a), succs);
                            }
                            succs.add(cloneList(b));
                        }
		    }});
	} catch (ContradictionException e) {}

        return res;
    }

    public static List<Integer> cloneList(List<Integer> w) {
	List<Integer> x = new ArrayList<Integer>();
	x.addAll(w);
	return x;
    }

    ////////////////////////////////////////////////////////////////////////////

    private class Node {
        private final int stateVar;
        private final int acceptVar;

        private final Node[][] children;

        public Node() throws ContradictionException {
            stateVar = solver.getNextSATVar();
            acceptVar = stateVar + numStates;
            solver.setNextSATVar(acceptVar + 1);
            children = new Node[numLetters][numLetters];

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

        public Node lookup(Iterator<Integer> v, Iterator<Integer> w) {
            if (!v.hasNext())
                return this;
            final int nv = v.next();
            final int nw = w.next();
            if (children[nv][nw] == null)
                return null;
            return children[nv][nw].lookup(v, w);
        }

        public Node insert(Iterator<Integer> v, Iterator<Integer> w)
                                     throws ContradictionException {
            if (!v.hasNext())
                return this;
            final int nv = v.next();
            final int nw = w.next();

            if (children[nv][nw] == null) {
                final Node n = new Node();
                children[nv][nw] = n;

                for (int q1 = 1; q1 <= numStates; ++q1)
                    for (int q2 = 1; q2 <= numStates; ++q2) {
                        // enabled transition implies that the
                        // successor state is active
                        solver.addClause
                            (new int[] { -getStateVar(q1),
                                         -encoding.getTransBoolVar(q1,
                                                                   nv, nw,
                                                                   q2),
                                         n.getStateVar(q2) });

                        // if no transition is defined, no successor
                        // state is active
                        solver.addClause
                            (new int[] { -getStateVar(q1),
                                         encoding.getTransBoolVar(q1,
                                                                  nv, nw,
                                                                  q2),
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
            
            return children[nv][nw].insert(v, w);
        }

        public void visit(List<Integer> v, List<Integer> w,
                          PairAcceptanceTreeVisitor visitor)
                                  throws ContradictionException {
            visitor.visit(v, w, getAcceptVar());
            for (int i = 0; i < numLetters; ++i) {
                v.add(i);
                for (int j = 0; j < numLetters; ++j) {
                    w.add(j);
                    if (children[i][j] != null)
                        children[i][j].visit(v, w, visitor);
                    w.remove(v.size() - 1);
                }
                v.remove(v.size() - 1);
            }
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
