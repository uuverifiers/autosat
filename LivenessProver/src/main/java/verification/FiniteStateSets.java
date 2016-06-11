package verification;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.Ultility;
import common.bellmanford.EdgeWeightedDigraph;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.language.InclusionCheckingImpl;
import common.VerificationUltility;

public class FiniteStateSets {

    private static final Logger LOGGER = LogManager.getLogger();
	
    private Map<String, Integer> labelToIndex;
    private Map<Integer, String> indexToLabel;

    private Automata I0;
    private Automata F;
    private EdgeWeightedDigraph player1;
    private EdgeWeightedDigraph player2;
    private int numLetters;

    private final Map<Integer, Set<List<Integer>>> reachableStates =
	new HashMap<Integer, Set<List<Integer>>>();

    private final Map<Integer, Automata> reachableStateAutomata =
	new HashMap<Integer, Automata>();

    private List<String> makeReadable(List<Integer> w) {
        List<String> res = new ArrayList<String> ();
        for (int n : w)
            res.add(indexToLabel.get(n));
        return res;
    }

    public FiniteStateSets(int numLetters,
			   Automata I0, Automata F,
			   EdgeWeightedDigraph player1,
			   EdgeWeightedDigraph player2,
			   Map<String, Integer> labelToIndex) {
	this.numLetters = numLetters;
	this.I0 = I0;
	this.F = F;
	this.player1 = player1;
	this.player2 = player2;
	this.labelToIndex = labelToIndex;
	indexToLabel = new HashMap<Integer, String>();
	for (String l : labelToIndex.keySet())
	    indexToLabel.put(labelToIndex.get(l), l);
    }

    public synchronized Set<List<Integer>> getReachableStates(int wordLen) {
	Set<List<Integer>> reachable = reachableStates.get(wordLen);
	if (reachable == null) {
	    reachable = new HashSet<List<Integer>>();

	    // Compute initial states for the given word length
	    List<List<Integer>> initialStates =
		AutomataConverter.getWords(I0, wordLen);

	    LOGGER.debug("" + initialStates.size() + " initial words");
//	    LOGGER.debug("Initial: " + initialStates);
	    
	    Set<List<Integer>> finalStates =
		new HashSet<List<Integer>>();
	    finalStates.addAll(AutomataConverter.getWords(F, wordLen));

	    LOGGER.debug("" + finalStates.size() + " final words");
	
	    Queue<List<Integer>> todo =
		new ArrayDeque<List<Integer>>();

	    reachable.addAll(initialStates);
	    for (List<Integer> w : initialStates)
		if (!finalStates.contains(w))
		    todo.add(w);

	    while (!todo.isEmpty()) {
		List<Integer> next = todo.poll();

		List<List<Integer>> player1Dest =
		    AutomataConverter.getWords
		    (AutomataConverter.getImage(next, player1, numLetters),
		     wordLen);
		List<List<Integer>> player2Dest =
		    AutomataConverter.getWords
		    (AutomataConverter.getImage(next, player2, numLetters),
		     wordLen);

		for (List<Integer> w : player1Dest)
		    if (!reachable.contains(w)) {
			reachable.add(w);
			if (!finalStates.contains(w))
			    todo.add(w);
		    }
		for (List<Integer> w : player2Dest)
		    if (!reachable.contains(w)) {
			reachable.add(w);
			if (!finalStates.contains(w))
			    todo.add(w);
		    }
	    }

	    LOGGER.debug("" + reachable.size() + " reachable words");

	    reachableStates.put(wordLen, reachable);
	}

	return reachable;
    }

    public synchronized Automata getReachableStateAutomaton(int wordLen) {
	Automata reachable = reachableStateAutomata.get(wordLen);
	if (reachable == null) {
	    LOGGER.debug("computing automaton describing reachable " +
			 "configurations of length " + wordLen);

	    final Automata complementF = AutomataConverter.getComplement(F);
	    reachable = AutomataConverter.getWordAutomaton(I0, wordLen);

	    // do one initial P2 transition
	    reachable =
		AutomataConverter.minimiseAcyclic
		(VerificationUltility.getUnion
		 (reachable,
		  VerificationUltility.getImage
		  (VerificationUltility.getIntersectionLazily(reachable, F, true),
		   player2)));
	    Automata newConfigurations = reachable;

	    while (true) {
		// check whether any new configurations exist
		List<List<Integer>> words =
		    AutomataConverter.getWords(newConfigurations, wordLen, 1);
		if (words.isEmpty())
		    break;

		for (int i = 0; i < 2; ++i) {
		    LOGGER.debug("reachable " + reachable.getStates().length +
				 ", new " + newConfigurations.getStates().length);

		    final Automata post =
			AutomataConverter.minimiseAcyclic
			(VerificationUltility.getImage
			 (VerificationUltility.getIntersectionLazily(newConfigurations,
								     F, true),
			  (i == 0) ? player1 : player2));
		    newConfigurations =
			AutomataConverter.minimiseAcyclic
			(VerificationUltility.getIntersectionLazily(post, reachable,
								    true));
		    reachable =
			AutomataConverter.minimiseAcyclic
			(VerificationUltility.getUnion(reachable, post));
		}
	    }

	    reachableStateAutomata.put(wordLen, reachable);
	}

	return reachable;
    }

    public boolean isReachable(List<Integer> word) {
	//	assert(getReachableStateAutomaton(word.size()).accepts(word) ==
	//	       getReachableStates(word.size()).contains(word));
	return getReachableStateAutomaton(word.size()).accepts(word);
    }

/*
    private Automata getImage(Automata from,
			      Automata complementF,
			      EdgeWeightedDigraph function) {
	final int numFrom = from.getStates().length;
	final int numComplementF = complementF.getStates().length;
	final int numFunction = function.V();

	Automata result = new Automata(VerificationUltility.hash(from.getInitState(),
								 complementF.getInitState(),
								 function.getInitState(),
								 numFrom, numComplementF),
				       numFrom * numComplementF * numFunction,
				       numLetters);

	Set<Integer> acceptings = new HashSet<Integer>();
	for (int acc1 : from.getAcceptingStates())
	    for (int acc2 : complementF.getAcceptingStates())
		for (int acc3 : function.getAcceptingStates())
		    acceptings.add(VerificationUltility.hash(acc1, acc2, acc3,
							     numFrom, numComplementF));
	result.setAcceptingStates(acceptings);

	for (DirectedEdge edge : function.edges()) {
	    DirectedEdgeWithInputOutput ioEdge = (DirectedEdgeWithInputOutput) edge;
	    for (int from1 = 0; from1 < numFrom; ++from1)
		for (int to1 : from.getStates()[from1].getDest(ioEdge.getInput()))
		    for (int from2 = 0; from2 < numComplementF; ++from2)
			for (int to2 :
				 complementF.getStates()[from2].getDest
				 (ioEdge.getInput()))
			    result.addTrans(VerificationUltility.hash(from1, from2,
								      ioEdge.from(),
								      numFrom, numComplementF),
					    ioEdge.getOutput(),
					    VerificationUltility.hash(to1, to2,
								      ioEdge.to(),
								      numFrom, numComplementF));
	}
	
	return result;
    }
*/

    public List<List<List<Integer>>> getLevelSets(int wordLen) {
        return getLevelSets(wordLen, AutomataConverter.getWords(F, wordLen));
    }

    public List<List<List<Integer>>> getLevelSets(int wordLen,
                                                  List<List<Integer>> knownWinningStates) {
	final Set<List<Integer>> reachable = getReachableStates(wordLen);
	final List<List<List<Integer>>> res = new ArrayList<List<List<Integer>>>();

	Map<List<Integer>, List<List<Integer>>> player1Moves =
	    new HashMap<List<Integer>, List<List<Integer>>>();
	Map<List<Integer>, List<List<Integer>>> player2Moves =
	    new HashMap<List<Integer>, List<List<Integer>>>();

	for (List<Integer> w : reachable) {
	    List<List<Integer>> player1Dest =
		AutomataConverter.getWords
		(AutomataConverter.getImage(w, player1, numLetters),
		 wordLen);
	    List<List<Integer>> player2Dest =
		AutomataConverter.getWords
		(AutomataConverter.getImage(w, player2, numLetters),
		 wordLen);

	    if (!player1Dest.isEmpty())
		player1Moves.put(w, player1Dest);
	    if (!player2Dest.isEmpty())
		player2Moves.put(w, player2Dest);

	    if (player1Dest.isEmpty() && player2Dest.isEmpty() &&
		!knownWinningStates.contains(w))
		throw new RuntimeException(
                  "There is a non-final reachable configuration from " +
		  "which neither player can make a move: " +
                  makeReadable(w));
	    if (!player1Dest.isEmpty() && !player2Dest.isEmpty())
		throw new RuntimeException(
                  "There is a reachable configuration from " +
		  "which both players can make a move: " +
                  makeReadable(w));
	}

	Set<List<Integer>> winningStates = new HashSet<List<Integer>>();
	winningStates.addAll(knownWinningStates);

	res.add(knownWinningStates);

	boolean changed = true;
	while (changed) {
	    changed = false;

	    List<List<Integer>> nextLevel = new ArrayList<List<Integer>>();

	    for (List<Integer> w : reachable) {
		if (!winningStates.contains(w)) {
		    // check whether player 2 can reach a winning position
		    List<List<Integer>> player2Dest = player2Moves.get(w);
		    if (player2Dest != null)
			for (List<Integer> v : player2Dest)
			    if (winningStates.contains(v)) {
				nextLevel.add(w);
				changed = true;
				break;
			    }
		}
	    }

	    res.add(nextLevel);
	    winningStates.addAll(nextLevel);

	    nextLevel = new ArrayList<List<Integer>>();

	    for (List<Integer> w : reachable) {
		if (!winningStates.contains(w)) {
		    // check whether player 1 must move to a winning position
		    // for player 2
		    List<List<Integer>> player1Dest = player1Moves.get(w);
		    if (player1Dest != null &&
			winningStates.containsAll(player1Dest)) {
			nextLevel.add(w);
			changed = true;
		    }
		}
	    }

	    res.add(nextLevel);
	    winningStates.addAll(nextLevel);
	}

	return res;
    }

    ////////////////////////////////////////////////////////////////////////////

    public List<Automata> getLevelAutomata(int wordLen) {
	final List<Automata> res = new ArrayList<Automata>();
        
        final Automata complementF = AutomataConverter.getComplement(F);
        final Automata reachable = getReachableStateAutomaton(wordLen);

        final Automata p1Configs =
            VerificationUltility.computeDomain(player1, numLetters);
        final Automata p2Configs =
            VerificationUltility.computeDomain(player2, numLetters);

        {
            // Check that at most one player can move from each
            // reachable configuration

            final Automata p1p2Configs =
                VerificationUltility.getIntersectionLazily
                   (p1Configs, p2Configs, false);

            final List<Integer> cex =
                AutomataConverter.getSomeWord(p1p2Configs);
            if (cex != null)
                throw new RuntimeException(
                  "There is a reachable configuration from " +
                  "which both players can make a move: " +
                  makeReadable(cex));
        }

        {
            // Check that at least one player can move from each
            // reachable configuration

            final Automata p1p2UnionConfigs =
                AutomataConverter.toDFA(
                VerificationUltility.getUnion(
                VerificationUltility.getUnion(p1Configs, p2Configs), F));
            final Automata cexAut =
                VerificationUltility.getIntersectionLazily
                   (reachable, p1p2UnionConfigs, true);

            final List<Integer> cex =
                AutomataConverter.getSomeWord(cexAut);
            if (cex != null)
                throw new RuntimeException(
                  "There is a non-final reachable configuration from " +
		  "which neither player can make a move: " +
                  makeReadable(cex));
        }

        // Check that players move in alternation
        final Automata p1Range =
            VerificationUltility.computeRange(player1, numLetters);
        final Automata p2Range =
            VerificationUltility.computeRange(player2, numLetters);

        {
            final Automata p1RangeReachable =
                VerificationUltility.getIntersectionLazily
                (VerificationUltility.getIntersectionLazily
                 (p1Range, reachable, false), F, true);

            final List<Integer> cex =
                AutomataConverter.getSomeWord
                (VerificationUltility.getIntersectionLazily
                 (p1RangeReachable, p2Configs, true));
            if (cex != null)
                throw new RuntimeException
                    ("Player 1 can move to a configuration that does not " +
                     "belong to player 2: " + makeReadable(cex));
        }

        {
            final Automata p2RangeReachable =
                VerificationUltility.getIntersectionLazily
                (VerificationUltility.getIntersectionLazily
                 (p2Range, reachable, false), F, true);

            final List<Integer> cex =
                AutomataConverter.getSomeWord
                (VerificationUltility.getIntersectionLazily
                 (p2RangeReachable, p1Configs, true));
            if (cex != null)
                throw new RuntimeException
                    ("Player 2 can move to a configuration that does not " +
                     "belong to player 1: " + makeReadable(cex));
        }

        Automata winningStates = AutomataConverter.getWordAutomaton(F, wordLen);
	res.add(winningStates);

	boolean changed = true;
	while (changed) {
	    changed = false;

            Automata nextLevel =
                AutomataConverter.minimiseAcyclic(
                  VerificationUltility.getIntersectionLazily(
                  VerificationUltility.getIntersectionLazily(
                       VerificationUltility.getPreImage
                          (player2, res.get(res.size() - 1)),
                       winningStates, true), reachable, false));
            res.add(nextLevel);
            
	    LOGGER.debug("nextLevel (1): " + nextLevel.getStates().length);

            if (AutomataConverter.getWords(nextLevel, wordLen, 1).isEmpty()) {
                // finished, but add also an automaton for player 2
                res.add(nextLevel);
            } else {
                winningStates =
                    AutomataConverter.minimiseAcyclic(
                       VerificationUltility.getUnion(winningStates, nextLevel));
             
                // compute states from which all player 1 moves lead to
                // winningStates
                final Automata notWinning =
                    AutomataConverter.getComplement(winningStates);
                final Automata pre =
                    VerificationUltility.getPreImage(player1, notWinning);
                final Automata notPre =
                    AutomataConverter.getComplement(pre);

                // restrict to p1-states
                final Automata notPreP1 =
                    VerificationUltility.getIntersectionLazily
                    (notPre, p1Configs, false);

                nextLevel =
                    AutomataConverter.minimiseAcyclic(
                      VerificationUltility.getIntersectionLazily(
                         VerificationUltility.getIntersectionLazily(
                            notPreP1, winningStates, true), reachable, false));
                res.add(nextLevel);

                LOGGER.debug("nextLevel (2): " + nextLevel.getStates().length);

                if (!AutomataConverter.getWords(nextLevel, wordLen, 1)
                                      .isEmpty()) {
                    changed = true;

                    winningStates =
                        AutomataConverter.minimiseAcyclic(
                           VerificationUltility.getUnion(winningStates, nextLevel));
                }
            }
        }
        
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void verifyInstance(int wordLen,
			       boolean closeUnderTransitions) {
	final List<List<List<Integer>>> levels = getLevelSets(wordLen);
	final Set<List<Integer>> winningStates = new HashSet<List<Integer>>();
	
	for (List<List<Integer>> level : levels)
	    winningStates.addAll(level);
	
	List<Integer> cex = null;
	if (closeUnderTransitions) {
	    for (List<Integer> w : getReachableStates(wordLen))
		if (!winningStates.contains(w))
		    cex = w;
	} else {
	    for (List<Integer> w :
		     AutomataConverter.getWords(I0, wordLen))
		if (!winningStates.contains(w))
		    cex = w;
	}
	if (cex != null)
	    throw new RuntimeException
		("There is a reachable configuration from " +
		 "which player 2 cannot win: " +
                 makeReadable(cex));
    }

    ////////////////////////////////////////////////////////////////////////////

    public void verifyInstanceSymbolically(int wordLen,
                                           boolean closeUnderTransitions) {
	final List<Automata> levels = getLevelAutomata(wordLen);

        Automata winningStates = null;
	for (Automata level : levels)
            if (winningStates == null)
                winningStates = level;
            else
                winningStates =
                    AutomataConverter.minimiseAcyclic
                    (VerificationUltility.getUnion(winningStates, level));

	List<Integer> cex = null;
	if (closeUnderTransitions)
            cex = AutomataConverter.getSomeWord
                (VerificationUltility.getIntersectionLazily
                 (getReachableStateAutomaton(wordLen), winningStates, true));
	else
            cex = AutomataConverter.getSomeWord
                (VerificationUltility.getIntersectionLazily
                 (AutomataConverter.getWordAutomaton(I0, wordLen),
                  winningStates, true));

	if (cex != null)
	    throw new RuntimeException
		("There is a reachable configuration from " +
		 "which player 2 cannot win: " +
                 makeReadable(cex));
    }

    public Set<List<Integer>> getRankableConfigs(int wordLen,
						 Automata winningConfigs,
						 EdgeWeightedDigraph relation) {
	Set<List<Integer>> rankable = new HashSet<List<Integer>>();
	for (List<Integer> w : getReachableStates(wordLen))
	    if (winningConfigs.accepts(w))
		rankable.add(w);
	
	boolean changed = true;
	while (changed) {
	    changed = false;
	    
	    for (List<Integer> w : getReachableStates(wordLen))
		if (!rankable.contains(w) && isRankable(w, rankable, relation)) {
		    rankable.add(w);
		    changed = true;
		}
	}

	return rankable;
    }

    public List<Integer> getFirstRankableConfig(int wordLen,
						Automata winningConfigs,
						EdgeWeightedDigraph relation) {
	Set<List<Integer>> rankable = new HashSet<List<Integer>>();
	for (List<Integer> w : getReachableStates(wordLen))
	    if (winningConfigs.accepts(w))
		rankable.add(w);

	for (List<Integer> w : getReachableStates(wordLen))
	    if (!rankable.contains(w) && isRankable(w, rankable, relation))
		return w;
	
	return null;
    }

    private boolean isRankable(List<Integer> w,
			       Set<List<Integer>> rankable,
			       EdgeWeightedDigraph relation) {
	final List<List<Integer>> wImage =
	    AutomataConverter.getWords
	    (AutomataConverter.getImage(w, player1, numLetters), w.size());

	if (wImage.isEmpty())
	    return false;

	for (List<Integer> v : wImage) {
	    boolean isR = false;
	    for (List<Integer> u : 
		     AutomataConverter.getWords
		     (AutomataConverter.getImage(v, player2, numLetters),
		      w.size()))
		if (rankable.contains(u) &&
		    AutomataConverter.getImage(u, relation, numLetters)
		    .accepts(w)) {
		    isR = true;
		    break;
		}
	    if (!isR)
		return false;
	}

	return true;
    }
}
