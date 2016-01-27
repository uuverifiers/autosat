package verification;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.ArrayDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.Ultility;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

public class FiniteChecker {

    private static final Logger LOGGER = LogManager.getLogger();
	
    private Map<String, Integer> labelToIndex;
    private Map<Integer, String> indexToLabel;

    private int wordLen;
    
	private Automata I0;
	private Automata F;
	private EdgeWeightedDigraph player1;
	private EdgeWeightedDigraph player2;
    private int numLetters;

    public FiniteChecker(int wordLen, int numLetters) {
	this.wordLen = wordLen;
	this.numLetters = numLetters;
    }

    public void solve() {
	System.out.println("Solving instance with " + wordLen + " letters ...");

	// Compute initial states for the given word length
	List<List<Integer>> initialStates =
	    AutomataConverter.getWords(I0, wordLen);

	System.out.println("Initial: " + initialStates);

	List<List<Integer>> finalStates =
	    AutomataConverter.getWords(F, wordLen);

	System.out.println("" + finalStates.size() + " final words");

	// Compute all reachable states
	Map<List<Integer>, List<List<Integer>>> player1Moves =
	    new HashMap<List<Integer>, List<List<Integer>>>();
	Map<List<Integer>, List<List<Integer>>> player2Moves =
	    new HashMap<List<Integer>, List<List<Integer>>>();
	    
	Set<List<Integer>> reachable =
	    new HashSet<List<Integer>>();

	Queue<List<Integer>> todo =
	    new ArrayDeque<List<Integer>>();

	todo.addAll(initialStates);
	reachable.addAll(initialStates);

	while (!todo.isEmpty()) {
	    List<Integer> next = todo.poll();
	    //	    System.out.println(getRankSym(next));

	    List<List<Integer>> player1Dest =
		AutomataConverter.getWords
		(AutomataConverter.getImage(next, player1, numLetters),
		 wordLen);
	    List<List<Integer>> player2Dest =
		AutomataConverter.getWords
		(AutomataConverter.getImage(next, player2, numLetters),
		 wordLen);

	    if (!player1Dest.isEmpty())
		player1Moves.put(next, player1Dest);
	    if (!player2Dest.isEmpty())
		player2Moves.put(next, player2Dest);

	    for (List<Integer> w : player1Dest)
		if (!reachable.contains(w)) {
		    reachable.add(w);
		    todo.add(w);
		}
	    for (List<Integer> w : player2Dest)
		if (!reachable.contains(w)) {
		    reachable.add(w);
		    todo.add(w);
		}
	}

	System.out.println("" + reachable.size() + " reachable words");

	// Declare symbols
	for (List<Integer> w : reachable)
	    System.out.println("(declare-const " + getRankSym(w) + " Int)");

	// Compute required inequalities
	for (List<Integer> player1State : player1Moves.keySet())
	    if (!finalStates.contains(player1State))
		for (List<Integer> player2State :
			 player1Moves.get(player1State))
		    if (!finalStates.contains(player2State)) {
			List<List<Integer>> player2Dest =
			    player2Moves.get(player2State);
			boolean canGotoF = false;
			for (List<Integer> w : player2Dest)
			    if (finalStates.contains(w))
				canGotoF = true;
			if (!canGotoF) {
			    // then player 2 needs to be able to
			    // go to somewhere smaller
			    System.out.print("(assert (or false");
			    for (List<Integer> w : player2Dest)
				System.out.print(" (> " +
						 getRankSym(player1State) +
						 " " +
						 getRankSym(w) +
						 ")");
			    System.out.println("))");
			}
		    }
    }

    private String getRankSym(List<Integer> word) {
	String res = "rank";
	for (int n : word)
	    res = res + "_" + indexToLabel.get(n);
	return res;
    }

	public Automata getI0() {
		return I0;
	}

	public void setI0(Automata i0) {
		I0 = i0;
	}

	public Automata getF() {
		return F;
	}

	public void setF(Automata f) {
		F = f;
	}

	public EdgeWeightedDigraph getPlayer1() {
		return player1;
	}

	public void setPlayer1(EdgeWeightedDigraph player1) {
		this.player1 = player1;
	}

	public EdgeWeightedDigraph getPlayer2() {
		return player2;
	}

	public void setPlayer2(EdgeWeightedDigraph player2) {
		this.player2 = player2;
	}

	public void setLabelToIndex(Map<String, Integer> labelToIndex) {
		this.labelToIndex = labelToIndex;
		indexToLabel = new HashMap<Integer, String>();
		for (String l : labelToIndex.keySet())
		    indexToLabel.put(labelToIndex.get(l), l);
	}
    
}