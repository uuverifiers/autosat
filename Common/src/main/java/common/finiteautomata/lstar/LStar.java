package common.finiteautomata.lstar;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;

import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.State;
import common.finiteautomata.language.InclusionCheckingImpl;

public class LStar {

    // simple test for the algorithm
    public static void main(String[] args) {
	final Automata sol = new Automata(0, 4, 2);
	final Set<Integer> accept = new HashSet<Integer>();
	accept.add(3);
	sol.setAcceptingStates(accept);

	sol.addTrans(0, 0, 0);
	sol.addTrans(1, 0, 1);
	sol.addTrans(2, 0, 2);
	sol.addTrans(3, 0, 3);

	sol.addTrans(0, 1, 1);
	sol.addTrans(1, 1, 2);
	sol.addTrans(2, 1, 3);
	sol.addTrans(3, 1, 0);

	final Teacher teacher = new Teacher() {
		public boolean isAccepted(List<Integer> word) {
		    System.out.println(word + " -> " + sol.accepts(word));
		    return sol.accepts(word);
		}

		public boolean isCorrectLanguage(Automata hyp,
						 List<List<Integer>> posCEX,
						 List<List<Integer>> negCEX) {
		    System.out.println();
		    System.out.println("Hypothesis:");
		    System.out.println(hyp);

		    InclusionCheckingImpl ic = new InclusionCheckingImpl();

		    List<Integer> cex =
			ic.findCounterExample(hyp,
					      AutomataConverter
					      .toCompleteDFA(sol));
		    if (cex != null) {
			System.out.println("negative cex: " + cex);
			negCEX.add(cex);
			return false;
		    }

		    cex =
			ic.findCounterExample(sol,
					      AutomataConverter
					      .toCompleteDFA(hyp));
		    if (cex != null) {
			System.out.println("positive cex: " + cex);
			posCEX.add(cex);
			return false;
		    }

		    return true;
		}
	    };

	final LStar lstar = new LStar(2, teacher);
	lstar.setup();
	lstar.solve();
    }

    ////////////////////////////////////////////////////////////////////////////

    private final int numLetters;
    private final Teacher teacher;
    private Automata solution = null;

    private Node classTree = null;

    private List<List<Integer>> distWords =
	new ArrayList<List<Integer>> ();

    private static final List<Integer> emptyWord =
	new ArrayList<Integer> ();

    public LStar(int numLetters,
		 Teacher teacher) {
	this.numLetters = numLetters;
	this.teacher = teacher;
    }

    public void setup() {
	solution = null;

	final boolean initAccepting = teacher.isAccepted(emptyWord);

	final Automata hypAut = new Automata(0, 1, numLetters);
	final Set<Integer> accept = new HashSet<Integer>();
	if (initAccepting)
	    accept.add(0);
	hypAut.setAcceptingStates(accept);
	
	for (int l = 0; l < numLetters; ++l)
	    hypAut.addTrans(0, l, 0);

	final List<List<Integer>> posCEX = new ArrayList<List<Integer>> ();
	final List<List<Integer>> negCEX = new ArrayList<List<Integer>> ();

	if (teacher.isCorrectLanguage(hypAut, posCEX, negCEX)) {
	    // finished already
	    solution = hypAut;
	} else {
	    classTree = new Node(emptyWord,
				 new Node (initAccepting ?
					   negCEX.get(0) :
					   emptyWord,
					   null, null),
				 new Node (initAccepting ?
					   emptyWord :
					   posCEX.get(0),
					   null, null));
	}
    }

    ////////////////////////////////////////////////////////////////////////////

    public void solve() {
	if (solution != null)
	    return;

	final List<List<Integer>> posCEX = new ArrayList<List<Integer>> ();
	final List<List<Integer>> negCEX = new ArrayList<List<Integer>> ();

	final List<List<Integer>> accessWords =
	    new ArrayList<List<Integer>> ();
	classTree.collectLeafWords(accessWords);

	Automata hypAut = extractAutomaton(accessWords);
        boolean cont = !teacher.isCorrectLanguage(hypAut, posCEX, negCEX);

	while (cont) {
	    final List<Integer> cex;
	    if (!posCEX.isEmpty())
		cex = posCEX.get(0);
	    else
		cex = negCEX.get(0);

	    // analyse the counterexample
	    
	    int currentState = hypAut.getInitState();
	    final List<Integer> prefix = new ArrayList<Integer> ();
	    Node lastSifted = null;

	    int j = 0;
	    while (true) {
		final Node sifted = classTree.sift(prefix);
		
		if (!sifted.word.equals(accessWords.get(currentState))) {
		    // have found the point where the automaton goes wrong;
		    // add a new state

		    prefix.remove(prefix.size() - 1);
		    
		    final Node[] distNode = new Node [1];
		    final boolean[] swapped = new boolean [1];

		    classTree.findDistinguishingPoint
			(sifted.word,
			 accessWords.get(currentState),
			 distNode, swapped);

		    final Node nodeA = new Node (lastSifted.word, null, null);
		    final Node nodeB = new Node (prefix, null, null);

		    List<Integer> bestDistWord = new ArrayList<Integer> ();
		    bestDistWord.add(cex.get(j - 1));
		    bestDistWord.addAll(distNode[0].word);

//		    System.out.println("new distinguishing word: " +
//				       bestDistWord);

		    // check whether we can find a shorter distinguishing word
		    for (List<Integer> oldDist : distWords) {
			final List<Integer> oldDistPrefix =
			    new ArrayList<Integer> ();

			for (int i = 0;
			     i < oldDist.size() && i < bestDistWord.size() - 1;
			     ++i) {
			    oldDistPrefix.add(oldDist.get(i));
			    if (oldDistPrefix.equals(bestDistWord))
				continue;

			    final List<Integer> a =
				new ArrayList<Integer>(nodeA.word);
			    final List<Integer> b =
				new ArrayList<Integer>(nodeB.word);
			    
			    a.addAll(oldDistPrefix);
			    b.addAll(oldDistPrefix);

			    if (teacher.isAccepted(a) != teacher.isAccepted(b)){
				bestDistWord = oldDistPrefix;

//				System.out.println
//				    ("better distinguishing word: " +
//				     bestDistWord);
			    }
			}
		    }

		    if (!distWords.contains(bestDistWord))
			distWords.add(bestDistWord);

		    lastSifted.word = bestDistWord;

		    final List<Integer> a = new ArrayList<Integer>(nodeA.word);
		    a.addAll(bestDistWord);
		    if (teacher.isAccepted(a)) {
			lastSifted.right = nodeA;
			lastSifted.left = nodeB;
		    } else {
			lastSifted.right = nodeB;
			lastSifted.left = nodeA;
		    }

		    break;
		}

		lastSifted = sifted;

		final int nextChar = cex.get(j++);

		final State s = hypAut.getStates()[currentState];
		final Set<Integer> nextStates = s.getDest(nextChar);
		assert(nextStates.size() == 1);

		currentState = nextStates.iterator().next();

		prefix.add(nextChar);
	    }

	    accessWords.clear();
	    classTree.collectLeafWords(accessWords);

	    hypAut = extractAutomaton(accessWords);

            if (posCEX.isEmpty() ? hypAut.accepts(cex) : !hypAut.accepts(cex)) {
                // the counterexample has not been eliminated yet, try again
            } else {
                posCEX.clear();
                negCEX.clear();
                cont = !teacher.isCorrectLanguage(hypAut, posCEX, negCEX);
            }
	}

	solution = hypAut;
    }

    ////////////////////////////////////////////////////////////////////////////

    private Automata extractAutomaton(List<List<Integer>> accessWords) {
	final Map<List<Integer>, Integer> accessIndex =
	    new HashMap<List<Integer>, Integer> ();

	int i = 0;
	for (List<Integer> w : accessWords)
	    accessIndex.put(w, i++);

	final Automata result =
	    new Automata(accessIndex.get(emptyWord),
			 accessWords.size(), numLetters);
	final Set<Integer> accept = new HashSet<Integer>();

	// add transitions and accepting states
	for (int x = 0; x < 2; ++x) {
	    final Iterator<Node> it =
		((x == 0) ? classTree.left : classTree.right).enumLeaves();
	    while (it.hasNext()) {
		final Node leaf = it.next();
		final int index = accessIndex.get(leaf.word);

		final List<Integer> extWord = new ArrayList<Integer> ();
		extWord.addAll(leaf.word);
	    
		for (int l = 0; l < numLetters; ++l) {
		    extWord.add(l);
		    result.addTrans(index, l,
				    accessIndex.get(classTree.sift(extWord)
						    .word));
		    extWord.remove(extWord.size() - 1);
		}

		if (x == 1)
		    accept.add(index);
	    }
	}

	result.setAcceptingStates(accept);
	return result;
    }

    public Automata getSolution() {
	return solution;
    }

    ////////////////////////////////////////////////////////////////////////////

    private class Node {
	public List<Integer> word;
	public Node left;
	public Node right;

	public Node(List<Integer> word,
		    Node left,
		    Node right) {
	    this.word = word;
	    this.left = left;
	    this.right = right;
	}

	public Node sift(List<Integer> w) {
	    if (left == null && right == null) {
		return this;
	    } else {
		final int oldSize = w.size();
		w.addAll(this.word);

		final boolean f = teacher.isAccepted(w);
		
		while (w.size() > oldSize)
		    w.remove(w.size() - 1);

		return (f ? right : left).sift(w);
	    }
	}

	public int getLeafNum() {
	    if (left == null && right == null)
		return 1;
	    else
		return left.getLeafNum() + right.getLeafNum();
	}

	public void collectLeafWords(List<List<Integer>> words) {
	    if (left == null && right == null) {
		words.add(word);
	    } else {
		left.collectLeafWords(words);
		right.collectLeafWords(words);
	    }
	}

	public int findDistinguishingPoint(List<Integer> word1,
					   List<Integer> word2,
					   Node[] node,
					   boolean[] swapped) {
	    if (left == null && right == null) {
		if (word.equals(word1))
		    return 1;
		else if (word.equals(word2))
		    return 2;
		else
		    return 0;
	    } else {
		final int leftRes =
		    left.findDistinguishingPoint(word1, word2, node, swapped);
		if (leftRes == 3) // found
		    return 3;

		final int rightRes =
		    right.findDistinguishingPoint(word1, word2, node, swapped);

		if (leftRes == 1 && rightRes == 2) {
		    node[0] = this;
		    swapped[0] = false;
		    return 3;
		}

		if (leftRes == 2 && rightRes == 1) {
		    node[0] = this;
		    swapped[0] = true;
		    return 3;
		}

		assert(leftRes == 0 || rightRes == 0);

		return leftRes + rightRes;
	    }
	}

	public Iterator<Node> enumLeaves() {
	    final Stack<Node> rem = new Stack<Node>();
	    rem.push(this);

	    return new Iterator<Node>() {
		private final Stack<Node> remaining = rem;

		public boolean hasNext() {
		    return !remaining.empty();
		}

		public Node next() {
		    Node res = remaining.pop();
		    while (res.left != null) {
			remaining.push(res.right);
			res = res.left;
		    }
		    return res;
		}

		public void remove() {
		    throw new UnsupportedOperationException();
		}
	    };
	}
    }

}
