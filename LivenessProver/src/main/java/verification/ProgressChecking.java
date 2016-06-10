package verification;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.VerificationUltility;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

public class ProgressChecking {
	private static final Logger LOGGER = LogManager.getLogger();

	private Automata B;
	private Automata F;
	private Automata winningStates;
	private Automata systemInvariant;
	private EdgeWeightedDigraph player1;
	private EdgeWeightedDigraph player2;
	private EdgeWeightedDigraph guessingTransducer;
	private int numLetters;
	
	
	public ProgressChecking(Automata B,
				Automata F,
				Automata winningStates,
				Automata systemInvariant,
				EdgeWeightedDigraph player1,
				EdgeWeightedDigraph player2,
				EdgeWeightedDigraph guessingTransducer,
				int numLetters){
		this.B = B;
		this.F = F;
                this.winningStates = winningStates;
		this.systemInvariant = systemInvariant;
		this.player1 = player1;
		this.player2 = player2;
		this.guessingTransducer = guessingTransducer;
		this.numLetters = numLetters;
	}
	
    /*
     * Return 2 words x, y
     * result[0], result[1]
     */
    public List<List<Integer>> check(){
	//		EdgeWeightedDigraph left = composeLeftPart();
	Automata complementF = AutomataConverter.getComplement(F);
	Automata winningStatesF = AutomataConverter.getComplement(winningStates);
	Automata invariant =
	    VerificationUltility.getIntersection(B, systemInvariant);
	EdgeWeightedDigraph right = composeRightPart();

	/*		if(!VerificationUltility.isDFA(left, numLetters)){
			left = VerificationUltility.toDFA(left, numLetters);
                        } */
	
	right = VerificationUltility.simplifyNFA(right);

	if(!VerificationUltility.isDFA(right, numLetters)){
	    right = VerificationUltility.toDFA2(right, numLetters);
	}
		
	//		if(!VerificationUltility.isComplete(right, numLetters)){
	//			right = VerificationUltility.makeComplete(right, numLetters);
	//}
	
	//		LOGGER.debug("size left: " + left.V());
	
	LOGGER.debug("Bx   size: " + invariant.getStates().length);
	LOGGER.debug("!Fx  size: " + complementF.getStates().length);
	LOGGER.debug("rhs  size: " + right.V());
	
	List<int[]> counterExample =
	    findShortestCounterExample(invariant,
                                       winningStatesF,
				       complementF,
//				       VerificationUltility
//				       .getUniversalAutomaton(numLetters),
				       player1,
				       right);
	List<List<Integer>> result =
	    VerificationUltility.convertToWords(counterExample, 2);
	
	return result;
    }
	
	private EdgeWeightedDigraph composeLeftPart(){
	    // B(x) & !F(x) & !F(y) & (x ->1 y)
	    Automata complementF = AutomataConverter.getComplement(F);
	    Automata invariant =
		VerificationUltility.getIntersection(B, systemInvariant);
		
	    int numStatesB = invariant.getStates().length;
	    int numStatesComplementF = complementF.getStates().length;
	    int numStatesPlayer1 = player1.V();
		
	    EdgeWeightedDigraph result =
		new EdgeWeightedDigraph(numStatesB *
					numStatesComplementF * numStatesComplementF *
					numStatesPlayer1);
	    result.setInitState(VerificationUltility.hash(invariant.getInitState(),
							  complementF.getInitState(),
							  complementF.getInitState(),
							  player1.getInitState(),
							  numStatesB,
							  numStatesComplementF,
							  numStatesComplementF));
		
	    //set accepting
	    Set<Integer> acceptings = new HashSet<Integer>();
	    for(int acceptB: invariant.getAcceptingStates()){
		for(int acceptComplementFx: complementF.getAcceptingStates()){
		    for(int acceptComplementFy: complementF.getAcceptingStates()){
			for(int acceptPlayer1: player1.getAcceptingStates()){
			    acceptings.add(VerificationUltility.hash(acceptB,
								     acceptComplementFx,
								     acceptComplementFy,
								     acceptPlayer1,
								     numStatesB,
								     numStatesComplementF,
								     numStatesComplementF));
			}
		    }
		}
	    }
	    result.setAcceptingStates(acceptings);
		
	    
	    List<DirectedEdgeWithInputOutput> edgesB = VerificationUltility.getEdges(invariant);
	    List<DirectedEdgeWithInputOutput> edgesComplementF = VerificationUltility.getEdges(complementF);
		
	    for(DirectedEdge edge: player1.edges()){
		DirectedEdgeWithInputOutput edgePlayer1 = (DirectedEdgeWithInputOutput) edge;
		for(DirectedEdgeWithInputOutput edgeB: edgesB){
		    if (edgePlayer1.getInput() == edgeB.getInput())
			for(DirectedEdgeWithInputOutput edgeComplementFx: edgesComplementF){
			    if (edgePlayer1.getInput() == edgeComplementFx.getInput())
				for(DirectedEdgeWithInputOutput edgeComplementFy: edgesComplementF){
				    if (edgePlayer1.getOutput() == edgeComplementFy.getInput()) {
					int source = VerificationUltility.hash(edgeB.from(),
									       edgeComplementFx.from(),
									       edgeComplementFy.from(),
									       edgePlayer1.from(),
									       numStatesB,
									       numStatesComplementF,
									       numStatesComplementF);
					int dest = VerificationUltility.hash(edgeB.to(),
									     edgeComplementFx.to(),
									     edgeComplementFy.to(),
									     edgePlayer1.to(),
									     numStatesB,
									     numStatesComplementF,
									     numStatesComplementF);
			    
					DirectedEdgeWithInputOutput newEdge =
					    new DirectedEdgeWithInputOutput(source, dest,
									    edgePlayer1.getInput(),
									    edgePlayer1.getOutput());
					result.addEdge(newEdge);
				    }
				}
			}
		}
	    }
		
	    return result;
	}
	
	private EdgeWeightedDigraph composeRightPart(){
	    // exists z. (B(z) & (y ->2 z) & x > z)
	    List<DirectedEdgeWithInputOutput> edgesB = VerificationUltility.getEdges(B);
		
	    int numStatesB = B.getStates().length;
	    int numStatesPlayer2 = player2.V();
	    int numStatesTransducer = guessingTransducer.V();
		
	    EdgeWeightedDigraph result =
		new EdgeWeightedDigraph(numStatesB * numStatesPlayer2 * numStatesTransducer);
	    result.setInitState(VerificationUltility.hash(B.getInitState(),
							  player2.getInitState(),
							  guessingTransducer.getInitState(),
							  numStatesB,
							  numStatesPlayer2));
		
	    // set accepting
	    Set<Integer> acceptings = new HashSet<Integer>();
	    for (int acceptB : B.getAcceptingStates()) {
		for (int acceptPlayer2 : player2.getAcceptingStates()) {
		    for (int acceptTransducer : guessingTransducer.getAcceptingStates()) {
			acceptings.add(VerificationUltility.hash(acceptB,
								 acceptPlayer2,
								 acceptTransducer,
								 numStatesB,
								 numStatesPlayer2));
		    }
		}
	    }
	    result.setAcceptingStates(acceptings);
				
	    for(DirectedEdgeWithInputOutput edgeB: edgesB){
		for(DirectedEdge edge2: player2.edges()){
		    DirectedEdgeWithInputOutput edgePlayer2 = (DirectedEdgeWithInputOutput) edge2;
		    if(edgeB.getInput() == edgePlayer2.getOutput()){
			for(DirectedEdge edge: guessingTransducer.edges()){
			    DirectedEdgeWithInputOutput edgeTransducer = (DirectedEdgeWithInputOutput) edge;
			    if(edgeTransducer.getInput() == edgeB.getInput()){
				int source = VerificationUltility.hash(edgeB.from(),
								       edgePlayer2.from(),
								       edgeTransducer.from(),
								       numStatesB,
								       numStatesPlayer2);
				int dest = VerificationUltility.hash(edgeB.to(),
								     edgePlayer2.to(),
								     edgeTransducer.to(),
								     numStatesB,
								     numStatesPlayer2);
							
				DirectedEdgeWithInputOutput newEdge =
				    new DirectedEdgeWithInputOutput(source,
								    dest,
								    edgeTransducer.getOutput(),
								    edgePlayer2.getInput());
				result.addEdge(newEdge);
			    }
			}
		    }
		}
	    }
		
	    return result;
	}

    //////////////////////////////////////////////////////////////////////////

    /*
     * return null or sequence of (x, y)
     */
    private List<int[]> findShortestCounterExample(Automata Bx,
						   Automata complementFx,
						   Automata complementFy,
						   EdgeWeightedDigraph x2y,
						   EdgeWeightedDigraph dfa2) {
	// Language inclusion check; the left-hand side is
	//     B(x) & !F(x) & !F(y) & (x ->1 y)
	// the right-hand side is
	//     dfa2

	// get accepting states
	final Set<Integer> acceptingStatesBx =
	    Bx.getAcceptingStates();
	final Set<Integer> acceptingStatesNFx =
	    complementFx.getAcceptingStates();
	final Set<Integer> acceptingStatesNFy =
	    complementFy.getAcceptingStates();
	final Set<Integer> acceptingStatesX2Y =
	    x2y.getAcceptingStates();
	final Set<Integer> acceptingStates2 =
	    dfa2.getAcceptingStates();
	
	// dfa2 might be incomplete, therefore add a
	// special non-accepting looping state
	final int dfa2NA = dfa2.V();
	final List<DirectedEdge> emptyList = new ArrayList<DirectedEdge>();

	// store nodes waiting to visit
	final Deque<IntTuple> working = new ArrayDeque<IntTuple>();

	// for each state, store the letters seen on the path from
	// root to it
	final Deque<List<int[]>> paths = new ArrayDeque<List<int[]>>();

	final IntTuple initState =
	    new IntTuple(Bx.getInitState(),
			 complementFx.getInitState(),
			 complementFy.getInitState(),
			 x2y.getInitState(),
			 dfa2.getInitState());
	working.add(initState);

	if (acceptingStatesBx.contains(initState.s1) &&
	    acceptingStatesNFx.contains(initState.s2) &&
	    acceptingStatesNFy.contains(initState.s3) &&
	    acceptingStatesX2Y.contains(initState.s4) &&
	    !acceptingStates2.contains(initState.s5)) {
	    return new ArrayList<int[]>();
	}

	// add path to init
	paths.add(new ArrayList<int[]>());

	// check whether a node is visited or not
	final Set<IntTuple> isVisited = new HashSet<IntTuple>();
	isVisited.add(initState);

	while (!working.isEmpty()) {
	    IntTuple currentState = working.poll();
	    List<int[]> currentPath = paths.poll();

	    Iterable<DirectedEdge> x2yEdges = x2y.adj(currentState.s4);
	    Iterable<DirectedEdge> edges2;

	    if (currentState.s5 == dfa2NA)
		edges2 = emptyList;
	    else
		edges2 = dfa2.adj(currentState.s5);

	    for (DirectedEdge _x2yEdge : x2yEdges) {
		final DirectedEdgeWithInputOutput x2yEdge =
		    (DirectedEdgeWithInputOutput) _x2yEdge;
		final int charX = x2yEdge.getInput();
		final int charY = x2yEdge.getOutput();
				
		final int x2yDest = x2yEdge.to();
		
		final Set<Integer> BxDests =
		    Bx.getStates()[currentState.s1].getDest(charX);
		if (BxDests.isEmpty())
		    continue;
		assert(BxDests.size() == 1);
		final int BxDest = BxDests.iterator().next();

		final Set<Integer> NFxDests =
		    complementFx.getStates()[currentState.s2].getDest(charX);
		if (NFxDests.isEmpty())
		    continue;
		assert(NFxDests.size() == 1);
		final int NFxDest = NFxDests.iterator().next();

		final Set<Integer> NFyDests =
		    complementFy.getStates()[currentState.s3].getDest(charY);
		if (NFyDests.isEmpty())
		    continue;
		assert(NFyDests.size() == 1);
		final int NFyDest = NFyDests.iterator().next();

		int dest2 = -1;

		for (DirectedEdge edge2 : edges2) {
		    DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
		    if (charX == tempEdge2.getInput()
			&& charY == tempEdge2.getOutput())
			dest2 = tempEdge2.to();
		}

		if (dest2 == -1)
		    // assume that we ended up in the looping state
		    dest2 = dfa2NA;

		final IntTuple newState =
		    new IntTuple(BxDest, NFxDest, NFyDest, x2yDest, dest2);

		if (isVisited.add(newState)) {
		    List<int[]> pathToChild = new ArrayList<int[]>(currentPath);
		    pathToChild.add(new int[] { charX, charY });

		    if (acceptingStatesBx.contains(newState.s1) &&
			acceptingStatesNFx.contains(newState.s2) &&
			acceptingStatesNFy.contains(newState.s3) &&
			acceptingStatesX2Y.contains(newState.s4) &&
			!acceptingStates2.contains(newState.s5)) {
			return pathToChild;
		    }

		    working.add(newState);
		    paths.add(pathToChild);
		}
	    }
	}
	
	return null;
    }

    private static class IntTuple {

	final int s1, s2, s3, s4, s5;

	public IntTuple(int s1, int s2, int s3, int s4, int s5) {
	    this.s1 = s1;
	    this.s2 = s2;
	    this.s3 = s3;
	    this.s4 = s4;
	    this.s5 = s5;
	}

	public int hashCode() {
	    return
		((((s1 * 82483721 +
		    s2) * 274837 +
		   s3) * 421431 +
		  s4) * 17 +
		 s5) * 718327;
	}

	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (!(obj instanceof IntTuple))
		return false;
	    IntTuple other = (IntTuple) obj;
	    return
		this.s1 == other.s1 &&
		this.s2 == other.s2 &&
		this.s3 == other.s3 &&
		this.s4 == other.s4 &&
		this.s5 == other.s5;
	}

    }
}
