package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Arrays;
import java.util.BitSet;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.State;

public class VerificationUltility {
    public static EdgeWeightedDigraph simplifyNFA(EdgeWeightedDigraph automata){
	final int V = automata.V();
	final boolean[] reachingAccept = new boolean [V];
	Arrays.fill(reachingAccept, false);

	for (int s : automata.getAcceptingStates())
	    reachingAccept[s] = true;

	boolean changed = true;
	while (changed) {
	    changed = false;

	    for (int i = 0; i < V; ++i)
		if (!reachingAccept[i])
		    for (DirectedEdge edge : automata.adj(i))
			if (reachingAccept[edge.to()]) {
			    reachingAccept[i] = true;
			    changed = true;
			}
	}

	final Map<Integer, Integer> relevantStates = new HashMap<Integer, Integer>();

	for (int i : automata.computeReachableStates(automata.getInitState())) {
	    if (reachingAccept[i])
		relevantStates.put(i, relevantStates.size());
	}

	// we need at least an initial state
	if (relevantStates.isEmpty())
	    relevantStates.put(automata.getInitState(), 0);

	EdgeWeightedDigraph dfa = new EdgeWeightedDigraph(relevantStates.size());
	dfa.setInitState(relevantStates.get(automata.getInitState()));

	for (int i = 0; i < V; ++i)
	    if (relevantStates.containsKey(i)) {
		final int newFrom = relevantStates.get(i);
		for(DirectedEdge edge: automata.adj(i))
		    if (relevantStates.containsKey(edge.to())) {
			final int newTo = relevantStates.get(edge.to());

			DirectedEdgeWithInputOutput ioEdge = (DirectedEdgeWithInputOutput)edge;
			dfa.addEdge(new DirectedEdgeWithInputOutput(newFrom, newTo,
								    edge.weight(),
								    ioEdge.getInput(),
								    ioEdge.getOutput()));
		    }
	    }

	//compute accepting states
	Set<Integer> acceptingDFA = new HashSet<Integer>();
	for (int s : automata.getAcceptingStates())
	    if (relevantStates.containsKey(s))
		acceptingDFA.add(relevantStates.get(s));
	dfa.setAcceptingStates(acceptingDFA);

	return dfa;
    }

    public static EdgeWeightedDigraph toDFA2(EdgeWeightedDigraph automata, int numLabels) {
	Set<Integer> allStatesDFA = new HashSet<Integer>();
	Map<BitSet, Integer> mapStates = new HashMap<BitSet, Integer>();

	Stack<BitSet> workingStates = new Stack<BitSet>();
	BitSet initSet = new BitSet();
	initSet.set(automata.getInitState());
	epsilonClosure(automata, initSet);

	workingStates.push(initSet);

	//state 0 will be the init state in new DFA
	int initInDFA = 0;
	mapStates.put(initSet, initInDFA);
	allStatesDFA.add(initInDFA);

	List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();
	BitSet[] targetStates = new BitSet [numLabels * numLabels];
	for (int i = 0; i < targetStates.length; ++i)
	    targetStates[i] = new BitSet();

	while (!workingStates.isEmpty()) {
	    BitSet statesInNFA = workingStates.pop();
	    int stateInDFA = mapStates.get(statesInNFA);

	    // compute the target states for the various labels
	    for (int s = statesInNFA.nextSetBit(0); s >= 0; s = statesInNFA.nextSetBit(s+1)) {
		for (DirectedEdge edge : automata.adj(s)) {
		    DirectedEdgeWithInputOutput ioEdge = (DirectedEdgeWithInputOutput)edge;
		    targetStates[ioEdge.getInput() * numLabels +
				 ioEdge.getOutput()].set(edge.to());
		}
	    }

	    // check which target states are actually reachable
	    for(int input = 0; input < numLabels; input++)
		for(int output = 0; output < numLabels; output++){
		    final int index = input * numLabels + output;
		    if (!targetStates[index].isEmpty()) {
			BitSet destsInNFA = targetStates[index];
			epsilonClosure(automata, destsInNFA);
			targetStates[index] = new BitSet();

			Integer destInDFA = mapStates.get(destsInNFA);

			if(destInDFA == null){
			    destInDFA = mapStates.size();
			    mapStates.put(destsInNFA, destInDFA);
			    allStatesDFA.add(destInDFA);

			    //new
			    workingStates.push(destsInNFA);
			}

			edges.add(new DirectedEdgeWithInputOutput(stateInDFA, destInDFA,
								  input, output));
		    }
		}
	}

	EdgeWeightedDigraph dfa = new EdgeWeightedDigraph(allStatesDFA.size());
	dfa.setInitState(initInDFA);

	//compute accepting states
	Set<Integer> acceptingDFA = new HashSet<Integer>();
	for(BitSet statesNFA: mapStates.keySet()){
	    for (int stateNFA = statesNFA.nextSetBit(0);
		 stateNFA >= 0;
		 stateNFA = statesNFA.nextSetBit(stateNFA+1)) {
		if(automata.getAcceptingStates().contains(stateNFA)){
		    acceptingDFA.add(mapStates.get(statesNFA));
		    break;
		}
	    }
	}
	dfa.setAcceptingStates(acceptingDFA);

	for(DirectedEdgeWithInputOutput edge: edges){
	    dfa.addEdge(edge);
	}

	//
	return dfa;
    }

	public static EdgeWeightedDigraph toDFA(EdgeWeightedDigraph automata, int numLabels){
		Set<Integer> allStatesDFA = new HashSet<Integer>();
		Map<Set<Integer>, Integer> mapStates = new HashMap<Set<Integer>, Integer>();

		Stack<Set<Integer>> workingStates = new Stack<Set<Integer>>();
		Set<Integer> initSet = new HashSet<Integer>();
		initSet.add(automata.getInitState());
		initSet = getEpsilonClosure(automata, initSet);

		workingStates.push(initSet);

		//state 0 will be the init state in new DFA
		int initInDFA = 0;
		mapStates.put(initSet, initInDFA);
		allStatesDFA.add(initInDFA);

		List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();

		while(!workingStates.isEmpty()){
			Set<Integer> statesInNFA = workingStates.pop();
			int stateInDFA = mapStates.get(statesInNFA);

			for(int input = 0; input < numLabels; input++){
				for(int output = 0; output < numLabels; output++){
					Set<Integer> destsInNFA =
					    getEpsilonClosure(automata,
							      getDests(automata, statesInNFA,
								       input, output));

					if(!destsInNFA.isEmpty()){
						Integer destInDFA = mapStates.get(destsInNFA);

						if(destInDFA == null){
							destInDFA = mapStates.size();
							mapStates.put(destsInNFA, destInDFA);
							allStatesDFA.add(destInDFA);

							//new
							workingStates.push(destsInNFA);
						}
						edges.add(new DirectedEdgeWithInputOutput(stateInDFA, destInDFA, input, output));
					}
				}
			}
		}

		EdgeWeightedDigraph dfa = new EdgeWeightedDigraph(allStatesDFA.size());
		dfa.setInitState(initInDFA);

		//compute accepting states
		Set<Integer> acceptingDFA = new HashSet<Integer>();
		for(Set<Integer> statesNFA: mapStates.keySet()){
			for(Integer stateNFA: statesNFA){
				if(automata.getAcceptingStates().contains(stateNFA)){
					acceptingDFA.add(mapStates.get(statesNFA));
					break;
				}
			}
		}
		dfa.setAcceptingStates(acceptingDFA);

		for(DirectedEdgeWithInputOutput edge: edges){
			dfa.addEdge(edge);
		}

		//
		return dfa;
	}

	/**
	 * Compute epsilon closure from a set of states
	 */
	public static Set<Integer> getEpsilonClosure(EdgeWeightedDigraph graph, Set<Integer> fromStates){
		Set<Integer> result = new HashSet<Integer>();

		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.addAll(fromStates);

		boolean [] isVisited = new boolean[graph.V()];
		for(int fromState: fromStates){
			isVisited[fromState] = true;
		}

		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			result.add(currentState);

			//add new states to workingState
			for(DirectedEdge edge: graph.adj(currentState)){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				if(tempEdge.getInput() == Automata.EPSILON_LABEL && tempEdge.getOutput() == Automata.EPSILON_LABEL){
					if(!isVisited[tempEdge.to()]){
						isVisited[tempEdge.to()] = true;
						workingStates.push(tempEdge.to());
					}
				}
			}
		}

		return result;
	}

    /**
     * Compute epsilon closure from a set of states
     */
    public static void epsilonClosure(EdgeWeightedDigraph graph, BitSet fromStates){
	Stack<Integer> workingStates = new Stack<Integer>();

	for (int i = fromStates.nextSetBit(0); i >= 0; i = fromStates.nextSetBit(i+1))
	    workingStates.add(i);

	while(!workingStates.isEmpty()){
	    int currentState = workingStates.pop();

	    //add new states to workingState
	    for(DirectedEdge edge: graph.adj(currentState)){
		DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
		if (tempEdge.getInput() == Automata.EPSILON_LABEL &&
		    tempEdge.getOutput() == Automata.EPSILON_LABEL) {
		    if (!fromStates.get(tempEdge.to())) {
			fromStates.set(tempEdge.to());
			workingStates.push(tempEdge.to());
		    }
		}
	    }
	}
    }

	private static Set<Integer> getDests(EdgeWeightedDigraph graph, Set<Integer> states, int input, int output){
		Set<Integer> result = new HashSet<Integer>();

		for(int stateIndex: states){
			Iterable<DirectedEdge> edges = graph.adj(stateIndex);
			for(DirectedEdge edge: edges){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				if(tempEdge.getInput() == input && tempEdge.getOutput() == output){
					result.add(tempEdge.to());
				}
			}
		}

		return result;
	}

	public static boolean isDFA(EdgeWeightedDigraph graph, int numLetters){
		int numStates = graph.V();
		for(int i = 0; i < numStates; i++){
			Iterable<DirectedEdge> edges = graph.adj(i);
			boolean [][] hasTrans = new boolean[numLetters][numLetters];
			for(DirectedEdge edge: edges){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				int input = tempEdge.getInput();
				int output = tempEdge.getOutput();

				if(input == Automata.EPSILON_LABEL || output == Automata.EPSILON_LABEL){
					return false;
				}
				else if(hasTrans[input][output]){
					return false;
				}
				else{
					hasTrans[input][output] = true;
				}
			}
		}

		return true;
	}

	public static EdgeWeightedDigraph makeComplete(EdgeWeightedDigraph transducer, int numLetters){
		EdgeWeightedDigraph completeTransducer = new EdgeWeightedDigraph(transducer.V() + 1, transducer.getInitState(), new HashSet<Integer>(transducer.getAcceptingStates()));
		int dummyState = transducer.V();

		for(int i = 0; i < transducer.V(); i++){
			boolean [][] hasTrans = new boolean[numLetters][numLetters];
			Iterable<DirectedEdge> edges = transducer.adj(i);

			//copy transition
			for(DirectedEdge edge: edges){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				hasTrans[tempEdge.getInput()][tempEdge.getOutput()] = true;

				completeTransducer.addEdge(new DirectedEdgeWithInputOutput(tempEdge));
			}

			//add dummy transition
			for(int input = 0; input < numLetters; input++){
				for(int output = 0; output < numLetters; output++){
					if(!hasTrans[input][output]){
						completeTransducer.addEdge(new DirectedEdgeWithInputOutput(i, dummyState, input, output));
					}
				}
			}
		}

		//loop at dummy
		for(int input = 0; input < numLetters; input++){
			for(int output = 0; output < numLetters; output++){
				completeTransducer.addEdge(new DirectedEdgeWithInputOutput(dummyState, dummyState, input, output));
			}
		}

		return completeTransducer;
	}

	public static boolean isComplete(EdgeWeightedDigraph transducer, int numLetters){
		int numStates = transducer.V();

		boolean [][][] hasTrans = new boolean[numStates] [numLetters] [numLetters];
		for(DirectedEdge edge: transducer.edges()){
			DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
			int source = tempEdge.from();
			int input = tempEdge.getInput();
			int output = tempEdge.getOutput();
			if(hasTrans[source][input][output]){
				return false;
			}
			else{
				hasTrans[source][input][output] = true;
			}
		}

		return false;
	}

	/*
	 * States are counted from 0
	 */
	public static int hash(int state1, int state2, int numStates1) {
		return state1 + numStates1 * state2;
	}

	/*
	 * States are counted from 0
	 */
	public static int hash(int state1, int state2, int state3, int numStates1, int numStates2){
		return state1 + numStates1 * (state2 + numStates2 * state3);
	}

	/*
	 * States are counted from 0
	 */
    public static int hash(int state1, int state2, int state3, int state4,
			   int numStates1, int numStates2, int numStates3){
	return state1 + numStates1 * (state2 + numStates2 * (state3 + numStates3 * state4));
    }


	/**
	 * Convert accepting states to based 0.
	 * @param acceptingStates
	 * @return
	 */
	public static Set<Integer> convertAccepting(Set<Integer> acceptingStates) {
		// compute accepting state
		Set<Integer> newAccept = new HashSet<Integer>();
		for (Integer acc : acceptingStates) {
			newAccept.add(acc - 1);
		}
		return newAccept;
	}

	public static Automata getUnion(Automata B, Automata F){
		int numStatesB = B.getStates().length;
		int numStatesF = F.getStates().length;

		int numStatesBF = 1 + numStatesB + numStatesF;
		Automata result = new Automata(0, numStatesBF, B.getNumLabels());

		int offsetStateB = 1;
		int offsetStateF = offsetStateB + numStatesB;

		Set<Integer> acceptings = new HashSet<Integer>();
		for(int acceptB: B.getAcceptingStates()){
			acceptings.add(acceptB + offsetStateB);
		}

		for(int acceptF: F.getAcceptingStates()){
			acceptings.add(acceptF + offsetStateF);
		}

		if (B.getAcceptingStates().contains(B.getInitState()) ||
		    F.getAcceptingStates().contains(F.getInitState()))
		    acceptings.add(0);

		result.setAcceptingStates(acceptings);

		//add empty transition from new init to 2 inits of B, F
                //		result.addTrans(0, Automata.EPSILON_LABEL, B.getInitState() + offsetStateB);
                //		result.addTrans(0, Automata.EPSILON_LABEL, F.getInitState() + offsetStateF);

		List<DirectedEdgeWithInputOutput> edgesB = getEdges(B);
		for(DirectedEdgeWithInputOutput edgeB: edgesB){
			result.addTrans(edgeB.from() + offsetStateB, edgeB.getInput(), edgeB.to() + offsetStateB);
                        if (edgeB.from() == B.getInitState())
                            result.addTrans(0, edgeB.getInput(), edgeB.to() + offsetStateB);
		}

		List<DirectedEdgeWithInputOutput> edgesF = getEdges(F);
		for(DirectedEdgeWithInputOutput edgeF: edgesF){
			result.addTrans(edgeF.from() + offsetStateF, edgeF.getInput(), edgeF.to() + offsetStateF);
                        if (edgeF.from() == F.getInitState())
                            result.addTrans(0, edgeF.getInput(), edgeF.to() + offsetStateF);
		}

		return result;

	}

	public static Automata getUniversalAutomaton(int numLetters) {
	    Automata result = new Automata(0, 1, numLetters);

	    Set<Integer> acceptings = new HashSet<Integer>();
	    acceptings.add(0);
	    result.setAcceptingStates(acceptings);

	    for (int i = 0; i < numLetters; ++i)
		result.addTrans(0, i, 0);

	    return result;
	}

	public static Automata getIntersection(Automata B, Automata F){
	    int numStatesB = B.getStates().length;
	    int numStatesF = F.getStates().length;

	    int numStatesBF = numStatesB * numStatesF;
	    Automata result = new Automata(hash(B.getInitState(), F.getInitState(), numStatesB),
					   numStatesBF, B.getNumLabels());

	    Set<Integer> acceptings = new HashSet<Integer>();
	    for(int acceptB: B.getAcceptingStates())
		for(int acceptF: F.getAcceptingStates())
		    acceptings.add(hash(acceptB, acceptF, numStatesB));

	    result.setAcceptingStates(acceptings);

	    List<DirectedEdgeWithInputOutput> edgesB = getEdges(B);
	    List<DirectedEdgeWithInputOutput> edgesF = getEdges(F);

	    for (DirectedEdgeWithInputOutput edgeB : edgesB)
		for (DirectedEdgeWithInputOutput edgeF : edgesF)
		    if (edgeB.getInput() == edgeF.getInput())
			result.addTrans(hash(edgeB.from(), edgeF.from(), numStatesB),
					edgeB.getInput(),
					hash(edgeB.to(), edgeF.to(), numStatesB));

	    return AutomataConverter.pruneUnreachableStates(result);
	}

	public static Automata getIntersectionLazily(Automata A,
						     Automata B,
						     boolean complementB) {
	    //	    System.out.println("A: " + A);
	    //	    System.out.println("B: " + B);
	    final int numLetters = A.getNumLabels();
	    final State[] statesA = A.getStates();
	    final State[] statesB = B.getStates();
	    final int numStatesA = statesA.length;
	    final int numStatesB = statesB.length;
	    final Set<Integer> acceptingA = A.getAcceptingStates();
	    final Set<Integer> acceptingB = B.getAcceptingStates();

	    assert(!complementB || B.isDFA());

	    final List<IntPair> newStates = new ArrayList<IntPair>();
	    final Map<IntPair, Integer> stateId = new HashMap<IntPair, Integer>();

	    newStates.add(new IntPair (A.getInitState(), B.getInitState()));
	    stateId.put(newStates.get(0), 0);

	    final List<Integer> transFrom  = new ArrayList<Integer>();
	    final List<Integer> transLabel = new ArrayList<Integer>();
	    final List<Integer> transTo    = new ArrayList<Integer>();

	    final Set<Integer> dummyBStates = new HashSet<Integer>();
	    dummyBStates.add(numStatesB);

	    for (int nextToProcess = 0;
		 nextToProcess < newStates.size();
		 ++nextToProcess) {
		final IntPair state = newStates.get(nextToProcess);
		final State stateA = statesA[state.a];

		for (int l : stateA.getOutgoingLabels()) {
		    Set<Integer> destsB;
		    if (complementB) {
			if (state.b == numStatesB) {
			    destsB = dummyBStates;
			} else {
			    destsB = statesB[state.b].getDest(l);
			    if (destsB.isEmpty())
				destsB = dummyBStates;
			}
		    } else {
			destsB = statesB[state.b].getDest(l);
		    }

		    for (int destA : stateA.getDest(l))
			for (int destB : destsB) {
			    final IntPair dest = new IntPair (destA, destB);

			    Integer destId = stateId.get(dest);
			    if (destId == null) {
				destId = newStates.size();
				stateId.put(dest, destId);
				newStates.add(dest);
			    }

			    transFrom.add(nextToProcess);
			    transLabel.add(l);
			    transTo.add(destId);
			}
		}
	    }

	    final Automata result = new Automata(0, newStates.size(), numLetters);

	    for (int i = 0; i < transFrom.size(); ++i)
		result.addTrans(transFrom.get(i), transLabel.get(i), transTo.get(i));

	    Set<Integer> acceptings = new HashSet<Integer>();
	    for (int i = 0; i < newStates.size(); ++i) {
		final IntPair state = newStates.get(i);
		if (acceptingA.contains(state.a) &&
		    (complementB ?
		     !acceptingB.contains(state.b) :
		     acceptingB.contains(state.b)))
		    acceptings.add(i);
	    }

	    result.setAcceptingStates(acceptings);

	    return result;
	}

    public static Automata getImage(Automata from,
				    EdgeWeightedDigraph function) {
	final int numFrom = from.getStates().length;
	final int numFunction = function.V();
	final int numLetters = from.getNumLabels();

	Automata result =
	    new Automata(VerificationUltility.hash(from.getInitState(),
						   function.getInitState(),
						   numFrom),
			 numFrom * numFunction,
			 numLetters);

	Set<Integer> acceptings = new HashSet<Integer>();
	for (int acc1 : from.getAcceptingStates())
	    for (int acc3 : function.getAcceptingStates())
		acceptings.add(VerificationUltility.hash(acc1, acc3, numFrom));
	result.setAcceptingStates(acceptings);

	for (DirectedEdge edge : function.edges()) {
	    DirectedEdgeWithInputOutput ioEdge = (DirectedEdgeWithInputOutput) edge;
	    for (int from1 = 0; from1 < numFrom; ++from1)
		for (int to1 : from.getStates()[from1].getDest(ioEdge.getInput()))
		    result.addTrans(VerificationUltility.hash(from1, ioEdge.from(),
							      numFrom),
				    ioEdge.getOutput(),
				    VerificationUltility.hash(to1, ioEdge.to(), numFrom));
	}

	return result;
    }

    public static Automata getPreImage(EdgeWeightedDigraph function,
                                       Automata to) {
	final int numTo = to.getStates().length;
	final int numFunction = function.V();
	final int numLetters = to.getNumLabels();

	Automata result =
	    new Automata(VerificationUltility.hash(to.getInitState(),
						   function.getInitState(),
						   numTo),
			 numTo * numFunction,
			 numLetters);

	Set<Integer> acceptings = new HashSet<Integer>();
	for (int acc1 : to.getAcceptingStates())
	    for (int acc3 : function.getAcceptingStates())
		acceptings.add(VerificationUltility.hash(acc1, acc3, numTo));
	result.setAcceptingStates(acceptings);

	for (DirectedEdge edge : function.edges()) {
	    DirectedEdgeWithInputOutput ioEdge = (DirectedEdgeWithInputOutput) edge;
	    for (int from1 = 0; from1 < numTo; ++from1)
		for (int to1 : to.getStates()[from1].getDest(ioEdge.getOutput()))
		    result.addTrans(VerificationUltility.hash(from1, ioEdge.from(),
							      numTo),
				    ioEdge.getInput(),
				    VerificationUltility.hash(to1, ioEdge.to(), numTo));
	}

	return result;
    }


	public static List<DirectedEdgeWithInputOutput> getEdges(Automata automata){
		List<DirectedEdgeWithInputOutput> result = new ArrayList<DirectedEdgeWithInputOutput>();

		int dummyOutput = -1;
		for(State state: automata.getStates()){
			for(int label = 0; label < automata.getNumLabels(); label++){
				Set<Integer> dests = state.getDest(label);
				for(int dest: dests){
					DirectedEdgeWithInputOutput edge = new DirectedEdgeWithInputOutput(state.getId(), dest, label, dummyOutput);
					result.add(edge);
				}
			}
		}

		return result;
	}

	/*
	 * counterExample[i] contains labels i.th of words
	 * return list of words
	 */
	public static List<List<Integer>> convertToWords(List<int[]> counterExample, int NUM_WORDS) {
		if(counterExample == null){
			return null;
		}

		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for(int i = 0; i < NUM_WORDS; i++){
			result.add(new ArrayList<Integer>());
		}

		for(int[] tripple: counterExample){
			for(int i = 0; i < NUM_WORDS; i++){
				result.get(i).add(tripple[i]);
			}
		}

		return result;
	}

    /**
     * Compute the set of all words x such that (x, y) \in fun for some y
     */
    public static Automata computeDomain(EdgeWeightedDigraph fun,
                                         int numLabels) {
	Automata result = new Automata(fun.getInitState(),
				       fun.V(),
				       numLabels);

	for (int s = 0; s < fun.V(); ++s)
	    for (DirectedEdge edge : fun.adj(s)) {
		DirectedEdgeWithInputOutput ioEdge =
		    (DirectedEdgeWithInputOutput) edge;
		result.addTrans(ioEdge.from(), ioEdge.getInput(), ioEdge.to());
	    }

	result.setAcceptingStates(fun.getAcceptingStates());

	return AutomataConverter.minimise(result);
    }

    /**
     * Compute the set of all words y such that (x, y) \in fun for some x
     */
    public static Automata computeRange(EdgeWeightedDigraph fun,
                                        int numLabels) {
	Automata result = new Automata(fun.getInitState(),
				       fun.V(),
				       numLabels);

	for (int s = 0; s < fun.V(); ++s)
	    for (DirectedEdge edge : fun.adj(s)) {
		DirectedEdgeWithInputOutput ioEdge =
		    (DirectedEdgeWithInputOutput) edge;
		result.addTrans(ioEdge.from(), ioEdge.getOutput(), ioEdge.to());
	    }

	result.setAcceptingStates(fun.getAcceptingStates());

	return AutomataConverter.minimise(result);
    }

}

// vim: ts=4
