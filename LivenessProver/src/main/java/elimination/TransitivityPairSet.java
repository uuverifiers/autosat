package elimination;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import encoding.TransducerEncoding;
import encoding.ISatSolver;


public class TransitivityPairSet {
    private final TransducerEncoding encoding;
    private final PairAcceptanceTree acceptanceTree;
    private final ISatSolver solver;

    public TransitivityPairSet(TransducerEncoding encoding)
                        throws ContradictionException {
        this.encoding = encoding;
        this.solver = encoding.getSolver();
        this.acceptanceTree = new PairAcceptanceTree(encoding);
    }

    public void addPairPermutations(List<Integer> v, List<Integer> w)
                        throws ContradictionException {
        final List<Integer> subV = new ArrayList<Integer>();
        final List<Integer> subW = new ArrayList<Integer>();

	subV.addAll(v);
	subW.addAll(w);

	for (int i = 0; i < v.size(); ++i) {
	    addPair(subV, subW);
	    int t = subV.remove(0);
	    subV.add(t);
	    t = subW.remove(0);
	    subW.add(t);
	}
    }

    public int addPair(List<Integer> v, List<Integer> w)
                              throws ContradictionException {
        final List<Integer> subV = new ArrayList<Integer>();
        final List<Integer> subW = new ArrayList<Integer>();

        Iterator<Integer> itV = v.iterator();
        Iterator<Integer> itW = w.iterator();
        
        while (true) {
            if (acceptanceTree.contains(subV, subW) == 0) {
                final int vwAccept = acceptanceTree.insert(subV, subW);

                ////////////////////////////////////////////////////////////////
                // generate transitivity constraints

                acceptanceTree.visit(new PairAcceptanceTreeVisitor() {
                        public void visit(final List<Integer> a,
                                          final List<Integer> b,
                                          final int abAccept)
                                               throws ContradictionException {
                            if (subW.equals(a)) {

				int vbAccept;
				if ((vbAccept = acceptanceTree.contains(subV, b)) != 0)
				    solver.addClause(new int[] {
					    -vwAccept,
					    -abAccept,
					    vbAccept
					});

			    }

                        }
                    });

                acceptanceTree.visit(new PairAcceptanceTreeVisitor() {
                        public void visit(final List<Integer> a,
                                          final List<Integer> b,
                                          final int abAccept)
                                               throws ContradictionException {
                            if (subV.equals(b)) {

				int awAccept;
				if ((awAccept = acceptanceTree.contains(a, subW)) != 0)
				    solver.addClause(new int[] {
					    -abAccept,
					    -vwAccept,
					    awAccept
					});
                                
                            }
                        }
                    });

                acceptanceTree.visit(new PairAcceptanceTreeVisitor() {
                        public void visit(final List<Integer> a,
                                          final List<Integer> b,
                                          final int abAccept)
                                               throws ContradictionException {
                            if (a.equals(subV)) {

				int bwAccept;
				if ((bwAccept = acceptanceTree.contains(b, subW)) != 0)
				    solver.addClause(new int[] {
					    -abAccept,
					    -bwAccept,
					    vwAccept
					});
                                
                            }
                        }
                    });

                ////////////////////////////////////////////////////////////////
                // generate asymmetricity constraints: v < w && w < v is unsat

		{

		    int wvAccept;
		    if ((wvAccept = acceptanceTree.contains(subW, subV)) != 0)
			solver.addClause(new int[] {
				-vwAccept, -wvAccept
			    });
                                
		}

                ////////////////////////////////////////////////////////////////

            }

            if (itV.hasNext()) {
                subV.add(itV.next());
                subW.add(itW.next());
            } else {
                break;
            }
        }

        return acceptanceTree.contains(v, w);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void fixTransitivityCEX(List<List<Integer>> cex)
	                              throws ContradictionException {
	int[] clause;
	if (cex.get(0).equals(cex.get(cex.size() - 1)))
	    // cycle
	    clause = new int [cex.size() - 1];
	else
	    clause = new int [cex.size()];

	for (int i = 0; i < cex.size() - 1; ++i)
	    clause[i] = -acceptanceTree.insert(cex.get(i), cex.get(i+1));
	
	if (!cex.get(0).equals(cex.get(cex.size() - 1)))
	    clause[clause.length-1] =
		acceptanceTree.insert(cex.get(0), cex.get(cex.size() - 1));
	    
	solver.addClause(clause);
    }

    ////////////////////////////////////////////////////////////////////////////

    public Map<List<Integer>, Set<List<Integer>>>
	computeClosure(final Set<Integer> setVars) {

	final LinkedHashSet<List<Integer>> words = acceptanceTree.allWords();
	final int N = words.size();

	final HashMap<List<Integer>, Integer> wordIndex =
	    new HashMap<List<Integer>, Integer>();
	final List<Integer>[] wordsArray = new List [N];
	{
	    int i = 0;
	    for (List<Integer> w : words) {
		wordsArray[i] = w;
		wordIndex.put(w, i++);
	    }
	}

	final Set<Integer>[] backward = new HashSet [N];
	final Set<Integer>[] forward  = new HashSet [N];

	for (int i = 0; i < N; ++i) {
	    backward[i] = new HashSet<Integer> ();
	    forward[i]  = new HashSet<Integer> ();
	}

	try {
	acceptanceTree.visit(new PairAcceptanceTreeVisitor() {
		public void visit(final List<Integer> a,
				  final List<Integer> b,
				  final int abAccept)
		    throws ContradictionException {
		    if (setVars.contains(abAccept)) {
			backward[wordIndex.get(b)].add(wordIndex.get(a));
			forward[wordIndex.get(a)].add(wordIndex.get(b));
		    }
		}});
	} catch (ContradictionException e) {}

	boolean changed = true;
	while (changed) {
	    changed = false;

	    for (int i = 0; i < N; ++i) {
		List<Integer> newPairs = new ArrayList<Integer>();
		for (int j : backward[i]) {
		    Set<Integer> jForward = forward[j];
		    for (int k : forward[i])
			if (!jForward.contains(k)) {
			    newPairs.add(j);
			    newPairs.add(k);
			}
		}
		
		for (int j = 0; j < newPairs.size(); j += 2) {
		    forward[newPairs.get(j)].add(newPairs.get(j+1));
		    backward[newPairs.get(j+1)].add(newPairs.get(j));
		    changed = true;
		}
	    }
	}

	Map<List<Integer>, Set<List<Integer>>> res =
	    new HashMap<List<Integer>, Set<List<Integer>>>();

	{
	    int i = 0;
	    for (List<Integer> w : words) {
		Set<List<Integer>> succs = new HashSet<List<Integer>> ();
		for (int j = 0; j < N; ++j)
		    if (forward[i].contains(j))
			succs.add(wordsArray[j]);
		res.put(w, succs);
		++i;
	    }
	}

	return res;
    }

    public List<List<List<Integer>>>
        transitivityCEXes(final Set<Integer> setVars) {

	final Map<List<Integer>, Set<List<Integer>>> closure =
	    computeClosure(setVars);
        final List<List<List<Integer>>> res =
	    new ArrayList<List<List<Integer>>>();
	
	try {
	    acceptanceTree.visit(new PairAcceptanceTreeVisitor() {
		    public void visit(final List<Integer> a,
				      final List<Integer> b,
				      final int abAccept)
			throws ContradictionException {
			if (!setVars.contains(abAccept) &&
			    closure.get(a).contains(b)) {

			    Map<List<Integer>, List<List<Integer>>> edges =
				acceptanceTree.enabledEdges(setVars);
			    List<List<Integer>> path =
				new ArrayList<List<Integer>>();
			    Set<List<Integer>> seenWords =
				new HashSet<List<Integer>>();
			    path = findPath(PairAcceptanceTree.cloneList(a),
					    PairAcceptanceTree.cloneList(b),
					    path, edges, seenWords);
			    
			    res.add(path);
                        }
		    }});
	} catch (ContradictionException e) {}
	
	final Set<List<Integer>> cycleNodes =
	    new HashSet<List<Integer>>();

	for (List<Integer> w : closure.keySet()) {
	    if (!cycleNodes.contains(w) &&
		closure.get(w).contains(w)) {

		Map<List<Integer>, List<List<Integer>>> edges =
		    acceptanceTree.enabledEdges(setVars);
                List<List<Integer>> path = new ArrayList<List<Integer>>();
                Set<List<Integer>> seenWords = new HashSet<List<Integer>>();
                path = findPath(PairAcceptanceTree.cloneList(w),
				PairAcceptanceTree.cloneList(w),
				path, edges, seenWords);

		res.add(path);

		for (List<Integer> v : path)
		    cycleNodes.add(v);
            }
	}

	return res;
    }

    private List<List<Integer>>
        findPath(List<Integer> source,
                 List<Integer> target,
                 List<List<Integer>> path,
                 Map<List<Integer>, List<List<Integer>>> edges,
                 Set<List<Integer>> seenWords) {
        path.add(source);
        seenWords.add(source);

        List<List<Integer>> succs = edges.get(source);
	if (succs != null) {
	    for (List<Integer> next : succs) {
		if (next.equals(target)) {
		    path.add(next);
		    return path;
		}
		if (!seenWords.contains(next)) {
		    List<List<Integer>> res =
			findPath(next, target, path, edges, seenWords);
		    if (res != null)
			return res;
		}
	    }
	}

        path.remove(path.size() - 1);

	return null;
    }
}
