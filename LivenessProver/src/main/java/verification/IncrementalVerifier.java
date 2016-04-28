
package verification;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import verification.OldCounterExamples;
import verification.ReachabilityChecking;
import verification.FiniteStateSets;
import encoding.MinisatSolver;
import encoding.ISatSolverFactory;
import encoding.SatSolver;
import encoding.LingelingSolver;
import visitor.AllVisitorImpl;
import visitor.SymmetryProb;
import common.VerificationUltility;
import common.bellmanford.EdgeWeightedDigraph;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.Ultility;

public class IncrementalVerifier {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ISatSolverFactory SOLVER_FACTORY;
    private final boolean useRankingFunctions;

    private static final int     initialFiniteExplorationBound = 3;
    // try to find a progress relation that covers as many
    // configurations as possible
    private static final boolean maximiseProgressRelations = true;
    // try to find a progress relation with as many transitions (and
    // accepting states) as possible
    private static final boolean maximiseTransducer = false;
    private static final boolean eliminateMultipleConfigurations = true;
    private static final int     maxStoredRelationNum = 5;
    private static final int     finiteVerificationBound = 6;

    private final boolean parallelise;
    private final boolean exploreTransducersParallel;

    private final boolean verifySolutions;
    private final boolean closeUnderRotation;
    private final boolean preComputeReachable;

    private final SymmetryProb problem;

    private Automata player1Configs;
    private Automata winningStates;
    private int sosBound;
    private FiniteStateSets finiteStates;
    private Automata systemInvariant;
    private int explorationBound;

    private int exploredBoundSofar;
    private List<Configuration> configurationsUpToBound;

    private List<Automata> chosenBs;
    private List<EdgeWeightedDigraph> chosenTs;
    private List<EdgeWeightedDigraph> distinctRelations;

    private static class Configuration implements Comparable<Configuration> {
	public final List<Integer> word;
	public final int rank;
	public Configuration(List<Integer> word, int rank) {
	    this.word = word;
	    this.rank = rank;
	}
	public int compareTo(Configuration that) {
	    return this.rank - that.rank;
	}
	public String toString() {
	    return "(" + word + ", " + rank + ")";
	}
    }

    public IncrementalVerifier(SymmetryProb problem,
			       ISatSolverFactory SOLVER_FACTORY,
			       boolean useRankingFunctions,
                               boolean preComputeReachable,
			       boolean verifySolutions) {
	this.problem = problem;
	this.SOLVER_FACTORY = SOLVER_FACTORY;
	this.useRankingFunctions = useRankingFunctions;
        this.preComputeReachable = preComputeReachable;
        this.closeUnderRotation = problem.getSymmetries().contains("rotation");
	this.verifySolutions = verifySolutions;

        if (problem.getParLevel() <= 0) {
            parallelise = false;
            exploreTransducersParallel = false;
        } else if (problem.getParLevel() == 1) {
            parallelise = true;
            exploreTransducersParallel = false;
        } else {
            parallelise = true;
            exploreTransducersParallel = true;
        }
    }

    /**
     * Compute the regular language of configurations from which
     * player 1 can make a move
     */
    private Automata computeP1Configurations() {
 	EdgeWeightedDigraph p1 = problem.getPlayer1();
	Automata result = new Automata(p1.getInitState(),
				       p1.V(),
				       problem.getNumberOfLetters());

	for (int s = 0; s < p1.V(); ++s)
	    for (DirectedEdge edge : p1.adj(s)) {
		DirectedEdgeWithInputOutput ioEdge =
		    (DirectedEdgeWithInputOutput) edge;
		result.addTrans(ioEdge.from(), ioEdge.getInput(), ioEdge.to());
	    }

	result.setAcceptingStates(p1.getAcceptingStates());

	return AutomataConverter.minimise(result);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setup() {
        player1Configs = computeP1Configurations();
	winningStates = problem.getF();

	sosBound =
	    problem.getMaxNumOfStatesTransducer() * problem.getMaxNumOfStatesTransducer() +
	    problem.getMaxNumOfStatesAutomaton() * problem.getMaxNumOfStatesAutomaton();

	finiteStates =
	    new FiniteStateSets(problem.getNumberOfLetters(),
				problem.getI0(), problem.getF(),
				problem.getPlayer1(),
				problem.getPlayer2(),
				problem.getLabelToIndex());

	if (preComputeReachable) {
	    final LStarInvariantSynth lstarInvSynth =
		new LStarInvariantSynth(problem.getNumberOfLetters(),
					problem.getI0(), problem.getF(),
					problem.getPlayer1(),
					problem.getPlayer2(),
					finiteStates, 5);
	    systemInvariant = lstarInvSynth.infer();
	} else {
	    systemInvariant =
		VerificationUltility.getUniversalAutomaton
		(problem.getNumberOfLetters());
	}

	explorationBound = initialFiniteExplorationBound;

        chosenBs = new ArrayList<Automata> ();
        chosenTs = new ArrayList<EdgeWeightedDigraph> ();
        distinctRelations = new ArrayList<EdgeWeightedDigraph> ();

	exploredBoundSofar = 0;
	configurationsUpToBound = new ArrayList<Configuration>();
    }

    ////////////////////////////////////////////////////////////////////////////

    private void setupExploredConfigurations() {
	for (; exploredBoundSofar <= explorationBound; ++exploredBoundSofar) {
	    final List<List<List<Integer>>> levels =
                finiteStates.getLevelSets(exploredBoundSofar);
	    for (int i = 2; i < levels.size(); i += 2) {
		for (List<Integer> word : levels.get(i))
		    configurationsUpToBound.add(new Configuration(word, exploredBoundSofar + i));
	    }
	}

	Collections.sort(configurationsUpToBound);
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean verify() {
        LOGGER.info("Constructing disjunctive advice bits");

	mainLoop : while (true) {

        setupExploredConfigurations();

	for (int configNum = 0; configNum < configurationsUpToBound.size();) {
	    final Configuration config = configurationsUpToBound.get(configNum);
	    final int rank = config.rank;
	    LOGGER.debug("checking configuration " + config.word + ", rank " +
                         rank + " ...");

	    final boolean coveredConfig = winningStates.accepts(config.word);
	    if (coveredConfig) {
		LOGGER.debug("already covered");
		++configNum;
	    } else {
		LOGGER.debug("not covered, extending progress relation");

                //                if (!distinctRelations.isEmpty() && reuseProgressRelations())
                //                    continue;

		final List<List<Integer>> elimWords =
		    new ArrayList<List<Integer>>();
		elimWords.add(config.word);

		if (eliminateMultipleConfigurations) {
		    for (int i = configNum + 1;
			 i < configurationsUpToBound.size() &&
			     configurationsUpToBound.get(i).rank == rank;
			 ++i)
			if (!winningStates.accepts(configurationsUpToBound.get(i).word))
			    elimWords.add(configurationsUpToBound.get(i).word);
		}

                LOGGER.debug("trying to rank one of " + elimWords);

                final CountDownLatch finishLatch = new CountDownLatch(1);

                final List<ProgressBuilder> builders = new ArrayList<ProgressBuilder> ();
                final List<Thread> builderThreads = new ArrayList<Thread> ();

                // builders for reusing old relations
                int num = 0;
                for (EdgeWeightedDigraph relation : distinctRelations) {
                    List<List<Integer>> extraWords = new ArrayList<List<Integer>> ();
                    for (int len = 0; len <= explorationBound; ++len) {
                        Set<List<Integer>> rankable = null;
                        for (List<Integer> w : elimWords)
                            if (w.size() == len) {
                                if (rankable == null)
                                    rankable =
                                        finiteStates.getRankableConfigs(len, winningStates, relation);
                                if (rankable.contains(w))
                                    extraWords.add(w);
                            }
                    }

                    if (!extraWords.isEmpty()) {
                        LOGGER.debug("relation #" + num + " can rank " + extraWords);
                        final ProgressBuilder builder =
                            new ReusingRelationBuilder(finishLatch, relation, num, extraWords,
                                                       problem.getMaxNumOfStatesAutomaton());
                        builders.add(builder);
                        builderThreads.add(new Thread(builder));
                    }

                    ++num;
                }

                // builders for constructing new relations
                for (int n = exploreTransducersParallel ?
                               1 : problem.getMaxNumOfStatesTransducer();
                     n <= problem.getMaxNumOfStatesTransducer();
                     ++n) {
                    final ProgressBuilder newRelationBuilder =
                        new ProgressRelationBuilder(finishLatch, elimWords, n);
                    builders.add(newRelationBuilder);
                    builderThreads.add(new Thread(newRelationBuilder));
                }

                //                computeProgressRelation(elimWords);

                try {
                    if (parallelise) {
                        for (Thread t : builderThreads)
                            t.start();

                        finishLatch.await();

                        // stop all threads
                        for (ProgressBuilder builder : builders)
                            builder.stopBuilding();
                        for (Thread t : builderThreads)
                            t.join();
                    } else {
                        // run the threads one by one
                        for (int i = 0; i < builders.size(); ++i) {
                            builderThreads.get(i).start();
                            builderThreads.get(i).join();
                            if (builders.get(i).finished)
                                break;
                        }
                    }

                    boolean oneDone = false;
                    for (ProgressBuilder builder : builders) {
                        if (builder.finished) {
                            builder.copyBackResults();
                            oneDone = true;
                            break;
                        }
                    }

                    if (!oneDone)
                        throw new RuntimeException
                            ("Could not extend advice bit further");
                } catch(InterruptedException e) {
                    LOGGER.error("interrupted");
                }
	    }
	}

	LOGGER.info("all reachable configurations up to length " + explorationBound +
		    " are covered");

	// check whether we have found a solution that covers the
	// complete game graph
        if (checkConvergence())
            break mainLoop;
	} // mainLoop

        printResult();

	return true;
    }

    ////////////////////////////////////////////////////////////////////////////

    private abstract class ProgressBuilder implements Runnable {
        private final CountDownLatch finishLatch;
        public boolean finished = false;

        public ProgressBuilder(CountDownLatch finishLatch) {
            this.finishLatch = finishLatch;
        }

        protected void callFinished() {
            finished = true;
            finishLatch.countDown();
        }

        protected boolean stopped = false;

        public abstract void copyBackResults();

        public void stopBuilding() {
            stopped = true;
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Compute a progress relation and set that eliminate at least one
     * of the given words.
     */
    private class ProgressRelationBuilder extends ProgressBuilder {
        private final List<List<Integer>> elimWords;
        private final int maxNumStatesTransducer;

        private Automata localInvariant;

        private ReachabilityChecking checking = null;
        private Automata B = null;
        private EdgeWeightedDigraph transducer = null;

        public ProgressRelationBuilder(CountDownLatch finishLatch,
                                       List<List<Integer>> elimWords,
                                       int maxNumStatesTransducer) {
            super(finishLatch);
            this.elimWords = elimWords;
            this.localInvariant = systemInvariant;
            this.maxNumStatesTransducer = maxNumStatesTransducer;
        }

        public void run() {
            LOGGER.debug("computing new progress relation for one of " + elimWords);

            OldCounterExamples oldCEs = new OldCounterExamples();

            sosLoop: for (int fixedSOS = 1;
                          fixedSOS <= sosBound;
                          ++fixedSOS) {
                for(int numStateTransducer = problem.getMinNumOfStatesTransducer();
                    numStateTransducer <= maxNumStatesTransducer;
                    numStateTransducer++) {
                    for(int numStateAutomata = problem.getMinNumOfStatesAutomaton();
                        numStateAutomata <= problem.getMaxNumOfStatesAutomaton();
                        numStateAutomata++){

                        if (stopped) {
                            LOGGER.debug("stopped");
                            return;
                        }

                        final int sos =
                            numStateTransducer * numStateTransducer +
                            numStateAutomata * numStateAutomata;

                        if (sos != fixedSOS)
                            continue;

                        checking =
                            createReachabilityChecking(useRankingFunctions,
                                                       numStateAutomata,
                                                       numStateTransducer,
                                                       oldCEs,
                                                       localInvariant);

                        checking.setup();
                        checking.addDisjBMembershipConstraint(elimWords);

                        if (checking.findNextSolution(false)) {
                            B = checking.getAutomatonB();
                            transducer = checking.getTransducer();
                        
                            // can the solution be made more general?
                            if (maximiseProgressRelations)
                                while (true) {
                                    final List<List<Integer>> remElimWords =
                                        new ArrayList<List<Integer>>();
                                    for (List<Integer> w : elimWords) {
                                        if (B.accepts(w))
                                            checking.addBMembershipConstraint(w);
                                        else
                                            remElimWords.add(w);
                                    }

                                    if (remElimWords.isEmpty())
                                        break;

                                    LOGGER.debug("trying to cover also one of " + remElimWords);
                                
                                    checking.addDisjBMembershipConstraint(remElimWords);
                                    if (checking.findNextSolution(false)) {
                                        B = checking.getAutomatonB();
                                        transducer = checking.getTransducer();
                                    } else
                                        break;
                                }

                            if (stopped) {
                                LOGGER.debug("stopped");
                                return;
                            }
                            
                            localInvariant = checking.getSystemInvariant();
                            LOGGER.debug("found new progress relation!");
                            callFinished();
                            return;
                        }
                    
                        localInvariant = checking.getSystemInvariant();
                    }
                }
            }

            LOGGER.debug("giving up");
        }
        
        public void copyBackResults() {
            // augment the set of winning states and continue
            // with the next configuration
            augmentWinningStates(checking, B, transducer);
            distinctRelations.add(0, transducer);

            while (distinctRelations.size() > maxStoredRelationNum)
                distinctRelations.remove(distinctRelations.size() - 1);

            LOGGER.debug("new progress relation: " + transducer);
            
            LOGGER.info("storing " + distinctRelations.size() +
                        " progress relations for reuse");

            systemInvariant = localInvariant;
        }

        public void stopBuilding() {
            super.stopBuilding();
            if (checking != null)
                checking.stopChecking();
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Compute a regular set B for which the given progress relation
     * eliminates at least one of the given words.
     */
    private class ReusingRelationBuilder extends ProgressBuilder {
        private final EdgeWeightedDigraph relation;
        private final int relationNum;
        private final List<List<Integer>> elimWords;
        private final int maxNumStatesAutomaton;

        private Automata localInvariant;

        private ReachabilityChecking checking = null;
        private Automata B = null;

        public ReusingRelationBuilder(CountDownLatch finishLatch,
                                      EdgeWeightedDigraph relation,
                                      int relationNum,
                                      List<List<Integer>> elimWords,
                                      int maxNumStatesAutomaton) {
            super(finishLatch);
            this.relation = relation;
            this.relationNum = relationNum;
            this.elimWords = elimWords;
            this.localInvariant = systemInvariant;
            this.maxNumStatesAutomaton = maxNumStatesAutomaton;
        }

        public void run() {
            LOGGER.debug("reusing relation for one of " + elimWords);

            OldCounterExamples oldCEs = new OldCounterExamples();

            for(int numStateAutomata = 1;
                numStateAutomata <= maxNumStatesAutomaton;
                numStateAutomata++) {

                if (stopped) {
                    LOGGER.debug("stopped");
                    return;
                }

                checking =
                    createReachabilityChecking(false, numStateAutomata,
                                               relation.V(), oldCEs,
                                               systemInvariant);

                checking.setup();
                checking.addDisjBMembershipConstraint(elimWords);
                checking.fixTransducer(relation);

                // exclude words that we know cannot be ranked
                for (int len = 0; len <= explorationBound; ++len) {
                    final Set<List<Integer>> rankable =
                        finiteStates.getRankableConfigs(len, winningStates, relation);
                    for (List<Integer> w : AutomataConverter.getWords(player1Configs, len))
                        if (!winningStates.accepts(w) &&
                            !rankable.contains(w) &&
                            finiteStates.isReachable(w)) {
                            //                                LOGGER.info("excluding " + w);
                            checking.addBNonMembershipConstraint(w);
                        }
                }

                if (checking.findNextSolution(false)) {
                    localInvariant = checking.getSystemInvariant();
                    LOGGER.debug("could reuse progress relation!");
                    callFinished();
                    return;
                }

                localInvariant = checking.getSystemInvariant();
            }

            LOGGER.debug("giving up");
        }

        public void copyBackResults() {
            // augment the set of winning states and continue
            // with the next configuration
            augmentWinningStates(checking, checking.getAutomatonB(), relation);

            // move successful relation to the beginning
            distinctRelations.remove(relationNum);
            distinctRelations.add(0, relation);

            systemInvariant = localInvariant;
        }

        public void stopBuilding() {
            super.stopBuilding();
            if (checking != null)
                checking.stopChecking();
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private ReachabilityChecking createReachabilityChecking
        (boolean useRF, int numStateAutomata, int numStateTransducer,
         OldCounterExamples oldCEs, Automata systemInvariant) {
        LOGGER.debug("Transducer states: " + numStateTransducer +
                     ", automaton states: " + numStateAutomata);
        ReachabilityChecking checking =
            new ReachabilityChecking(useRF, false, false, SOLVER_FACTORY);
        checking.setAutomataNumStates(numStateAutomata);
        checking.setF(problem.getF());
        checking.setWinningStates(winningStates);
        checking.setI0(problem.getI0());
        checking.setNumLetters(problem.getNumberOfLetters());
        checking.setPlayer1(problem.getPlayer1());
        checking.setPlayer2(problem.getPlayer2());
        checking.setLabelToIndex(problem.getLabelToIndex());
        checking.setOldCounterExamples(oldCEs);
        checking.setFiniteStateSets(finiteStates);
        checking.setSystemInvariant(systemInvariant);
        checking.setTransducerNumStates(numStateTransducer);

        return checking;
    }

    private void augmentWinningStates(ReachabilityChecking checking,
				      Automata B,
				      EdgeWeightedDigraph transducer) {
        // augment the set of winning states and continue
        // with the next configuration
        winningStates =
            AutomataConverter.minimise
            (VerificationUltility.getUnion
             (winningStates,
              closeUnderRotation ?
              AutomataConverter.closeUnderRotation(B) : B));

        chosenBs.add(B);
        chosenTs.add(transducer);

        LOGGER.info("extending winning set, now have " +
                    chosenBs.size() + " (Bi, Ti) pairs");

        systemInvariant = checking.getSystemInvariant();
    }

    ////////////////////////////////////////////////////////////////////////////

    private boolean checkConvergence() {
	checkConvergence : while (true) {
	    SubsetChecking checking =
		new SubsetChecking
		(VerificationUltility.getIntersection(systemInvariant,
						      player1Configs),
		 winningStates);
	    List<Integer> cex = checking.check();
	    if (cex == null)
                return true;

	    if (finiteStates.isReachable(cex)) {
		assert(cex.size() > explorationBound);
		explorationBound = cex.size();
		LOGGER.info("now checking configurations up to length " + explorationBound);
		break checkConvergence;
	    } else {
		LOGGER.debug("" + cex + " is not reachable, strengthening invariant");

		OldCounterExamples oldCEs = new OldCounterExamples();
		Automata newInv = null;
		Automata knownInv =
		    VerificationUltility.getIntersection
		    (systemInvariant,
		     AutomataConverter.getComplement(problem.getF()));
		for (int num = 1; num < 20 && newInv == null; ++num) {
		    RelativeInvariantSynth invSynth =
			new RelativeInvariantSynth(SOLVER_FACTORY,
						   problem.getNumberOfLetters(),
						   problem.getI0(), knownInv,
						   problem.getPlayer1(),
						   problem.getPlayer2(),
						   cex, oldCEs, num);
		    newInv = invSynth.infer();
		}

		systemInvariant =
		    VerificationUltility.getIntersection(systemInvariant, newInv);

		assert(systemInvariant.isDFA());

		if(!systemInvariant.isCompleteDFA()){
		    systemInvariant =
			AutomataConverter.toCompleteDFA(systemInvariant);
		}

		systemInvariant =
		    AutomataConverter.toMinimalDFA(systemInvariant);

		LOGGER.debug("new system invariant is " + systemInvariant);
	    }
	} // checkConvergence

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////

    // verify that the computed progress relations actually solve the
    // game, for configurations of length len
    private void verifyResults(int len) {
	final EdgeWeightedDigraph player1 = problem.getPlayer1();
	final EdgeWeightedDigraph player2 = problem.getPlayer2();
	final int numLetters = problem.getNumberOfLetters();

	final Set<List<Integer>> p2winning =
	    new HashSet<List<Integer>>();
	p2winning.addAll(AutomataConverter.getWords(problem.getF(), len));

	for (int i = 0; i < chosenBs.size(); ++i) {
	    final Automata B = chosenBs.get(i);
	    final EdgeWeightedDigraph T = chosenTs.get(i);

	    boolean changed = true;
	    while (changed) {
		changed = false;

		addLoop: for (List<Integer> w :
				  AutomataConverter.getWords(B, len))
		    if (!p2winning.contains(w)) {
			final List<List<Integer>> wImage =
			    AutomataConverter.getWords
			    (AutomataConverter.getImage
			     (w, player1, numLetters),
			     len);

			if (wImage.isEmpty())
			    continue;

			for (List<Integer> v : wImage) {
			    boolean isRankable = false;
			    for (List<Integer> u :
				     AutomataConverter.getWords
				     (AutomataConverter.getImage
				      (v, player2, numLetters),
				      len))
				if (p2winning.contains(u) &&
				    B.accepts(u) &&
				    AutomataConverter.getImage(u, T,
							       numLetters)
				    .accepts(w)) {
				    isRankable = true;
				    break;
				}
			    if (!isRankable)
				continue addLoop;
			}

			p2winning.add(w);
			if (closeUnderRotation) {
			    // also add rotated versions
			    List<Integer> w2 = new ArrayList<Integer> ();
			    w2.addAll(w);
			    for (int j = 0; j < len; ++j) {
				w2.add(w2.get(0));
				w2.remove(0);
				p2winning.add(new ArrayList<Integer> (w2));
			    }
			}
			changed = true;
		    }
	    }

	    for (List<Integer> w : AutomataConverter.getWords(B, len))
		if (player1Configs.accepts(w) && finiteStates.isReachable(w))
		    if (!p2winning.contains(w))
			throw new RuntimeException("(B" + i + ", T" + i +
						   ") is incorrect, not winning: " + w);
	}

	for (List<Integer> w : finiteStates.getReachableStates(len))
	    if (player1Configs.accepts(w))
		if (!p2winning.contains(w))
		    throw new RuntimeException
			("Solution is incorrect: don't know how to win from " +
			 w);
    }

    ////////////////////////////////////////////////////////////////////////////

    private void printResult() {
	LOGGER.info("FINISHED");

        Map<Integer, String> indexToLabel = problem.getIndexToLabel();

	System.out.println("VERDICT: Player 2 can win from every reachable configuration");
	System.out.println();

	System.out.println("// Approximation of reachable states");
	System.out.println(systemInvariant.prettyPrint("A", indexToLabel));

	System.out.println("// States from which player 2 can move and win");
	System.out.println(winningStates.prettyPrint("W", indexToLabel));

	System.out.println("// Progress relations" +
                           (closeUnderRotation ? " (all to be closed under rotation)" : ""));

        for (int i = 0; i < chosenBs.size(); ++i) {
            System.out.println(chosenBs.get(i).prettyPrint("B" + i, indexToLabel));
            System.out.println(chosenTs.get(i).prettyPrint("T" + i, indexToLabel, indexToLabel));
        }
        System.out.println();

	System.out.println("// Assumptions made (but not checked):");
	System.out.println("// * players move in alternation");
	System.out.println("// * from every reachable non-terminal configuration, exactly one");
	System.out.println("//   of the players can make a move");
	if (closeUnderRotation)
	    System.out.println("// * the game is symmetric under rotation");

	System.out.println();

	if (verifySolutions)
	    for (int len = 0; len <= finiteVerificationBound; ++len) {
		System.out.print("// Verifying solution for configurations of " +
				 "length " + len + " ... ");
		verifyResults(len);
		System.out.println("done");
	    }
    }

}

// vim: tabstop=4
