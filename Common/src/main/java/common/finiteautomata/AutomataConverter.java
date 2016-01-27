package common.finiteautomata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.bellmanford.EdgeWeightedDigraph;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.VerificationUltility;


public class AutomataConverter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Automata toDFA(Automata automata){
		List<State> allStatesDFA = new ArrayList<State>();
		Map<Set<Integer>, State> mapStates = new HashMap<Set<Integer>, State>();
		
		Stack<Set<Integer>> workingStates = new Stack<Set<Integer>>();
		Set<Integer> initSet = automata.getEpsilonClosure(automata.getInitState());
		final boolean hasEpsilon = automata.hasEpsilonTransitions();

		workingStates.push(initSet);
		
		//state 0 will be the init state in new DFA
		State initInDFA = new State(0);
		mapStates.put(initSet, initInDFA);
		allStatesDFA.add(initInDFA);
		
		while(!workingStates.isEmpty()){
			Set<Integer> statesInNFA = workingStates.pop();
			State stateInDFA = mapStates.get(statesInNFA);
			
			for(int i = 0; i < automata.getNumLabels(); i++){
				Set<Integer> destsInNFA =
				    automata.getDests(statesInNFA, i);
				if (hasEpsilon)
				    destsInNFA =
					automata.getEpsilonClosure(destsInNFA);
				
				if(!destsInNFA.isEmpty()){
					State destInDFA = mapStates.get(destsInNFA);
					
					if(destInDFA == null){
						destInDFA = new State(mapStates.size());
						mapStates.put(destsInNFA, destInDFA);
						allStatesDFA.add(destInDFA);
						
						//new
						workingStates.push(destsInNFA);
					}
					
					stateInDFA.addTrans(i, destInDFA.getId());
				}
			}
		}
		
		Automata dfa = new Automata(initInDFA.getId(), allStatesDFA, automata.getNumLabels());
		
		//compute accepting states
		Set<Integer> acceptingDFA = new HashSet<Integer>();
		for(Set<Integer> statesNFA: mapStates.keySet()){
			for(Integer stateNFA: statesNFA){
				if(automata.getAcceptingStates().contains(stateNFA)){
					acceptingDFA.add(mapStates.get(statesNFA).getId());
					break;
				}
			}
		}
		dfa.setAcceptingStates(acceptingDFA);
		
		//
		return dfa;
	}
	
	public static Automata toCompleteDFA(Automata dfa){
		int init = dfa.getInitState();
		int numberOfLabels = dfa.getNumLabels();
		List<State> states = new ArrayList<State>(Arrays.asList(dfa.getStates()));
		Set<Integer> accepting = dfa.getAcceptingStates();
		
		Automata result = new Automata(init, states.size() + 1, numberOfLabels);
		result.setAcceptingStates(new HashSet<Integer>(accepting));
		
		int dummyState = states.size();

		//copy transition
		for(State state: states){
			for(int i = 0; i < numberOfLabels; i++){
				Set<Integer> nexts = state.getDest(i);
				if(nexts.isEmpty()){
					//add transition to dummy
					result.addTrans(state.getId(), i, dummyState);
				}
				else{
					for(int next: nexts){
						result.addTrans(state.getId(), i, next);
					}
				}
			}
		}
		
		//loop at dummy
		for(int i = 0; i < numberOfLabels; i++){
			result.addTrans(dummyState, i, dummyState);
		}
				
		return result;
	}

    public static Automata pruneUnreachableStates(Automata dfa) {
	List<State> states = new ArrayList<State>(Arrays.asList(dfa.getStates()));
	final int numStates = states.size();
	final int numLabels = dfa.getNumLabels();
	
	boolean reachable[] = new boolean[numStates];

	// forward

	reachable[dfa.getInitState()] = true;

	boolean changed = true;
	while (changed) {
	    changed = false;
	    for (int i = 0; i < numStates; ++i)
		if (reachable[i])
		    for (int l = 0; l < numLabels; ++l)
			for (int j : states.get(i).getDest(l))
			    if (!reachable[j]) {
				changed = true;
				reachable[j] = true;
			    }
	}

	// backward

	boolean backwardReachable[] = new boolean[numStates];

	for (int a : dfa.getAcceptingStates())
	    if (reachable[a])
		backwardReachable[a] = true;

	changed = true;
	while (changed) {
	    changed = false;
	    for (int i = 0; i < numStates; ++i)
		if (reachable[i] && !backwardReachable[i])
		    reachLoop: for (int l = 0; l < numLabels; ++l)
			for (int j : states.get(i).getDest(l))
			    if (backwardReachable[j]) {
				changed = true;
				backwardReachable[i] = true;
				break reachLoop;
			    }
	}

	int mapping[] = new int[numStates];
	int num = 0;
	for (int i = 0; i < numStates; ++i)
	    if (backwardReachable[i]) {
		mapping[i] = num;
		++num;
	    } else {
		mapping[i] = -1;
	    }

	if (num == 0)
	    return new Automata(0, 1, numLabels);

	Automata result = new Automata(mapping[dfa.getInitState()],
				       num, numLabels);
	    
	Set<Integer> newAccepting = new HashSet<Integer>();
	for (int a : dfa.getAcceptingStates())
	    if (mapping[a] >= 0)
		newAccepting.add(mapping[a]);
	result.setAcceptingStates(newAccepting);
	
	for (int i = 0; i < numStates; ++i)
	    if (backwardReachable[i]) {
		State state = states.get(i);
		for (int l = 0; l < numLabels; ++l)
		    for (int dest : state.getDest(l))
			if (backwardReachable[dest])
			    result.addTrans(mapping[i], l, mapping[dest]);
	    }
	
	return result;
    }

	public static Automata toMinimalDFA(Automata dfa){
	    final State[] states = dfa.getStates();
	    final int numStates = states.length;
	    final int numLabels = dfa.getNumLabels();
	    final Set<Integer> accepting = dfa.getAcceptingStates();

	    final Set<Integer>[][] invTransitionsSet =
		new Set[numStates][numLabels];

	    for (int s = 0; s < numStates; ++s)
		for (int l = 0; l < numLabels; ++l)
		    invTransitionsSet[s][l] = new HashSet<Integer>();

	    for (int s = 0; s < numStates; ++s) {
		final State state = states[s];
		for (int l = 0; l < numLabels; ++l)
		    for (int dest : state.getDest(l))
			invTransitionsSet[dest][l].add(s);
	    }

	    final int[][][] invTransitions =
		new int[numStates][numLabels][];

	    for (int s = 0; s < numStates; ++s)
		for (int l = 0; l < numLabels; ++l) {
		    Set<Integer> set = invTransitionsSet[s][l];
		    int[] ar = new int[set.size()];
		    int i = 0;
		    for (int x : set)
			ar[i++] = x;
		    invTransitions[s][l] = ar;
		}

	    final boolean[] nonEqStates = new boolean[numStates * numStates];
	    final Stack<Integer> todo = new Stack<Integer>();

	    // initialise
	    for (int i = 0; i < numStates; ++i)
		for (int j = 0; j < i; ++j)
		    if (accepting.contains(i) != accepting.contains(j)) {
			nonEqStates[i * numStates + j] = true;
			todo.push(i * numStates + j);
		    }

	    // fixed-point iteration
	    while (!todo.empty()) {
		final int nextPair = todo.pop();
		final int state1Id = nextPair / numStates;
		final int state2Id = nextPair % numStates;

		for (int l = 0; l < numLabels; ++l) {
		    final int[] pre1 = invTransitions[state1Id][l];
		    if (pre1.length == 0)
			continue;

		    final int[] pre2 = invTransitions[state2Id][l];
		    if (pre2.length == 0)
			continue;

		    for (int s1 : pre1)
			for (int s2 : pre2)
			    if (s1 != s2) {
				final int ind;
				if (s1 > s2) {
				    ind = s1 * numStates + s2;
				} else {
				    ind = s2 * numStates + s1;
				}
				
				if (!nonEqStates[ind]) {
				    nonEqStates[ind] = true;
				    todo.push(ind);
				}
			    }
		}
	    }

	    int[] mapping = new int[numStates];
	    Arrays.fill(mapping, -1);
	    int num = 0;
	    for (int i = 0; i < numStates; ++i)
		if (mapping[i] < 0) {
		    for (int j = i; j < numStates; ++j)
			if (!nonEqStates[i * numStates + j] &&
			    !nonEqStates[j * numStates + i])
			    mapping[j] = num;
		    ++num;
		}

	    Automata result = mapStates(dfa, mapping, num, true);

	    //	    System.out.println("" + numStates + " -> " + result.getStates().length);
	    return result;
	}

    public static Automata mergeStates(Automata dfa) {
	final State[] states = dfa.getStates();
	final int numStates = states.length;
	final int numLabels = dfa.getNumLabels();
	final Set<Integer> accepting = dfa.getAcceptingStates();

	final Map<List<Integer>, Integer> seenStates =
	    new HashMap<List<Integer>, Integer> ();
	
	int[] oldMapping = new int[numStates];
	int[] mapping = new int[numStates];

	for (int i = 0; i < numStates; ++i)
	    oldMapping[i] = i;

	boolean changed = true;
	while (changed) {
	    changed = false;
	    seenStates.clear();

	    for (int s = 0; s < numStates; ++s) {
		final State state = states[s];

		final List<Integer> sig = new ArrayList<Integer> ();
		sig.add(accepting.contains(s) ? 1 : 0);
	    
		for (int l = 0; l < numLabels; ++l) {
		    final Set<Integer> dest = state.getDest(l);
		    if (dest.isEmpty()) {
			sig.add(-1);
		    } else {
			assert(dest.size() == 1);
			sig.add(oldMapping[dest.iterator().next()]);
		    }
		}

		final Integer oldS = seenStates.get(sig);
		if (oldS == null) {
		    final int next = seenStates.size();
		    seenStates.put(sig, next);
		    mapping[s] = next;
		} else {
		    mapping[s] = oldS;
		}

		if (mapping[s] != oldMapping[s])
		    changed = true;
	    }

	    int[] t = oldMapping;
	    oldMapping = mapping;
	    mapping = t;
	}

	if (seenStates.size() == states.length)
	    return dfa;

	return mapStates(dfa, mapping, seenStates.size(), false);
    }
	
    private static Automata mapStates(Automata dfa,
				      int[] mapping,
				      int newStateNum,
				      boolean hideDeadEnd) {
	final State[] states = dfa.getStates();
	final int numLabels = dfa.getNumLabels();
	final Set<Integer> accepting = dfa.getAcceptingStates();

	Automata result = new Automata(mapping[dfa.getInitState()],
				       newStateNum, numLabels);
	    
	Set<Integer> newAccepting = new HashSet<Integer>();
	for (int a : accepting)
	    newAccepting.add(mapping[a]);
	result.setAcceptingStates(newAccepting);

	int deadEnd;
	if (hideDeadEnd) {
	    outer: for (deadEnd = 0; deadEnd < newStateNum; ++deadEnd) {
		if (newAccepting.contains(deadEnd))
		    continue;

		for (State state : states)
		    if (mapping[state.getId()] == deadEnd)
			for (int l = 0; l < numLabels; ++l)
			    for (int dest : state.getDest(l))
				if (mapping[dest] != deadEnd)
				    continue outer;
	    
		break;
	    }
	} else {
	    deadEnd = newStateNum;
	}

	for (State state : states)
	    for (int l = 0; l < numLabels; ++l)
		for (int dest : state.getDest(l))
		    if (mapping[dest] != deadEnd)
			result.addTrans(mapping[state.getId()],
					l,
					mapping[dest]);

	return result;
    }
    
	public static Automata getComplement(Automata automata){
		if(!automata.isDFA()){
			automata = toDFA(automata);
		}
		
		if(!automata.isCompleteDFA()){
			automata = toCompleteDFA(automata);
		}
		Automata result = new Automata(automata.getInitState(), Arrays.asList(automata.getStates()), automata.getNumLabels());
		
		Set<Integer> acceptings = automata.getAcceptingStates();
		Set<Integer> complementAccepting = new HashSet<Integer>();
		for(int state = 0; state < automata.getStates().length; state++){
			if(!acceptings.contains(state)){
				complementAccepting.add(state);
			}
		}
		
		result.setAcceptingStates(complementAccepting);
		
		return result;
	}

    /**
     * Turn an arbitrary automaton into a minimal DFA.
     */
    public static Automata minimise(Automata automata) {
	if(!automata.isDFA()){
	    automata = toDFA(automata);
	}
		
	if(!automata.isCompleteDFA()){
	    automata = toCompleteDFA(automata);
	}
	
	return toMinimalDFA(automata);
    }

    /**
     * Turn an acyclic automaton into a minimal DFA.
     */
    public static Automata minimiseAcyclic(Automata automata) {
	if(!automata.isDFA()){
	    automata = toDFA(automata);
	}
		
	return mergeStates(pruneUnreachableStates(automata));
    }

    public static Automata getImage(List<Integer> word,
				    EdgeWeightedDigraph function,
				    int numLetters) {
	int wordLen = word.size();

	final int hashStride = wordLen + 1;
	Automata aut =
	    new Automata(VerificationUltility.hash(0, function.getInitState(), hashStride),
			 function.V() * (wordLen + 1),
			 numLetters);

	for (int pos = 0; pos < wordLen; ++pos) {
	    int nextChar = word.get(pos);
	    for(DirectedEdge edge: function.edges()) {
		DirectedEdgeWithInputOutput edgeFunction = (DirectedEdgeWithInputOutput) edge;
		if (edgeFunction.getInput() == nextChar)
		    aut.addTrans(VerificationUltility.hash(pos, edgeFunction.from(), hashStride),
				 edgeFunction.getOutput(),
				 VerificationUltility.hash(pos+1, edgeFunction.to(), hashStride));
	    }
	}

	Set<Integer> acceptings = new HashSet<Integer>();
	for (int a : function.getAcceptingStates())
	    acceptings.add(VerificationUltility.hash(wordLen, a, hashStride));
	aut.setAcceptingStates(acceptings);
	
	Automata prunedAut = AutomataConverter.pruneUnreachableStates(aut);
	Automata completeAut = AutomataConverter.toCompleteDFA(prunedAut);
	Automata minimalAut = AutomataConverter.toMinimalDFA(completeAut);

	return minimalAut;
    }

    public static List<List<Integer>> getWords(Automata lang,
					       int wordLength) {
        return getWords(lang, wordLength, Integer.MAX_VALUE);
    }

    public static List<List<Integer>> getWords(Automata lang,
					       int wordLength,
                                               int maxWords) {
	List<List<Integer>> res = new ArrayList<List<Integer>>();
        try {
            exploreWords(lang.getStates()[lang.getInitState()],
                         lang, wordLength,
                         new ArrayList<Integer>(),
                         res,
                         maxWords);
            return res;
        } catch (TooManyWordsException e) {
            return null;
        }
    }

    private static class TooManyWordsException extends RuntimeException {};

    private static void exploreWords(State state,
				     Automata lang,
				     int wordLength,
				     List<Integer> currentWord,
				     List<List<Integer>> result,
                                     int maxWords) {
	if (wordLength == 0) {
	    if (lang.getAcceptingStates().contains(state.getId())) {
		List<Integer> finalWord = new ArrayList<Integer>();
		finalWord.addAll(currentWord);
		result.add(finalWord);
                if (result.size() > maxWords)
                    throw new TooManyWordsException();
	    }
	} else {
	    for (int l : state.getOutgoingLabels()) {
		currentWord.add(l);
		for (int id : state.getDest(l))
		    exploreWords(lang.getStates()[id],
				 lang,
				 wordLength - 1,
				 currentWord,
				 result,
                                 maxWords);
		currentWord.remove(currentWord.size() - 1);
	    }
	}
    }

    public static Automata getWordAutomaton(Automata aut,
					    int wordLength) {
	Automata lengthAut = new Automata(0, wordLength + 1, aut.getNumLabels());

	Set<Integer> acceptings = new HashSet<Integer>();
	acceptings.add(wordLength);
	lengthAut.setAcceptingStates(acceptings);

	for (int l = 0; l < aut.getNumLabels(); ++l)
	    for (int s = 0; s < wordLength; ++s)
		lengthAut.addTrans(s, l, s + 1);

	return minimise(VerificationUltility.getIntersection(lengthAut, aut));
    }

    public static Automata closeUnderRotation(Automata aut) {
	final State[] autStates = aut.getStates();
	final int N = autStates.length;
	final int numLetters = aut.getNumLabels();
	final Automata result =
	    new Automata(0, N * N * 2 + 1, aut.getNumLabels());
	
	// copy the automata transitions
	for (int s = 0; s < N; ++s)
	    for (int l = 0; l < numLetters; ++l)
		for (int t : autStates[s].getDest(l))
		    for (int i = 0; i < N * 2; ++i)
			result.addTrans(1 + i * N + s, l, 1 + i * N + t);

	// accepting states
	Set<Integer> acceptings = new HashSet<Integer>();

	for (int i = 0; i < N; ++i)
	    acceptings.add(1 + i * 2 * N + N + i);

	result.setAcceptingStates(acceptings);

	// add epsilon transitions
	for (int s : aut.getAcceptingStates())
	    for (int i = 0; i < N; ++i)
		result.addTrans(1 + i * 2 * N + s,
				Automata.EPSILON_LABEL,
				1 + i * 2 * N + N + aut.getInitState());

	for (int i = 0; i < N; ++i)
	    result.addTrans(0, Automata.EPSILON_LABEL, 1 + i * 2 * N + i);

	return minimise(result);
    }

}
