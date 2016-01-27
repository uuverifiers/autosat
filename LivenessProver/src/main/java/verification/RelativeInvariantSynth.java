package verification;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import common.Ultility;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

import elimination.CEElimination;
import elimination.WordAcceptance;
import encoding.AutomataEncoding;
import encoding.BoolValToAutomaton;
import encoding.ISatSolver;
import encoding.ISatSolverFactory;
import encoding.TransducerEncoding;
import encoding.RankingFunction;
import elimination.TransitivityPairSet;

public class RelativeInvariantSynth {
    private static final Logger LOGGER = LogManager.getLogger();


    private int automataNumStates;
    private int numLetters;
    
    private ISatSolver solver;
	
    private Automata I0;
    private Automata relevantStates;
    private List<Integer> excludedWord;
    private EdgeWeightedDigraph player1;
    private EdgeWeightedDigraph player2;
    
    private OldCounterExamples oldCounterExamples;

    public RelativeInvariantSynth(ISatSolverFactory solverFactory,
				  int numLetters,
				  Automata I0,
				  Automata relevantStates,
				  EdgeWeightedDigraph player1,
				  EdgeWeightedDigraph player2,
				  List<Integer> excludedWord,
				  OldCounterExamples oldCounterExamples,
				  int automataNumStates) {
	solver = solverFactory.spawnSolver();
	this.I0 = I0;
	this.relevantStates = relevantStates;
	this.excludedWord = excludedWord;
	this.player1 = player1;
	this.player2 = player2;
	this.oldCounterExamples = oldCounterExamples;
	this.numLetters = numLetters;
	this.automataNumStates = automataNumStates;
    }
    
    public Automata infer() {
	AutomataEncoding automataEncoding =
	    new AutomataEncoding(solver,
				 automataNumStates,
				 numLetters);

	try {
	    LOGGER.debug("Encoding automaton with " + automataNumStates + " states");
	    automataEncoding.encode();

	    //	    WordAcceptance acc = new WordAcceptance(automataEncoding);
	    //	    int accept = acc.encodeNeg(excludedWord);
	    int accept = automataEncoding.acceptWord(excludedWord);
	    solver.addClause(new int[] { -accept });

	    boolean unsat = true;
	    boolean success = false;

	    int round = 0;

	    CEElimination ceElimination = new CEElimination(solver);

	    updateWithOldCE(automataEncoding, ceElimination);

	    while (solver.isSatisfiable()) {
		round += 1;
		LOGGER.debug("Satisfiable, round " + round +
			     ", states " + automataNumStates +
			     ", clause num " + solver.getClauseNum());

		unsat = false;
		Set<Integer> modelPosVars = solver.positiveModelVars();

		Automata automaton =
		    BoolValToAutomaton.toAutomata(modelPosVars, automataEncoding);
		assert(automaton.isDFA());
		//		LOGGER.debug("Find Automaton ");
		//		LOGGER.debug(automaton);

		////////////////////////////////////////////////////////
		// S1

		SubsetChecking s1 = new SubsetChecking(I0, automaton);
		List<Integer> w = s1.check();
		if (w != null) {
		    LOGGER.debug("S1 failed!");
		    LOGGER.debug(w);
		    ceElimination.ce0Elimination(automataEncoding, w);
		    oldCounterExamples.addL0B(w);
		    continue;
		}

		////////////////////////////////////////////////////////
		// S2

		InductivenessChecking s2 =
		    new InductivenessChecking(automaton, relevantStates,
					      player1, numLetters);
		List<List<Integer>> xy = s2.check();
		if(xy != null){
		    LOGGER.debug("S2 failed for P1!");
		    LOGGER.debug(xy);
		    ceElimination.ce1Elimination(automataEncoding, xy);
		    oldCounterExamples.addL1(xy);
		    continue;
		}

		s2 = new InductivenessChecking(automaton, relevantStates,
					       player2, numLetters);
		xy = s2.check();
		if(xy != null){
		    LOGGER.debug("S2 failed for P2!");
		    LOGGER.debug(xy);
		    ceElimination.ce1Elimination(automataEncoding, xy);
		    oldCounterExamples.addL1(xy);
		    continue;
		}

		// otherwise we are finished!
		LOGGER.debug("FOUND SOLUTION!");
		return automaton;
	    }
	
	    LOGGER.debug("no more models");
	    return null;
	} catch (ContradictionException e) {
                LOGGER.info("Unsatisfiable!");
                return null;
            } catch (TimeoutException e) {
                LOGGER.info("Time out!");
                return null;
            }
    }

    private void updateWithOldCE(AutomataEncoding automataEncoding,
				 CEElimination ceElimination)
	throws ContradictionException {
	
	LOGGER.debug("Updating encoding with old counter examples...");
		
	for(List<Integer> ce: oldCounterExamples.getL0B()){
	    ceElimination.ce0Elimination(automataEncoding, ce);
	}
		
	for(List<List<Integer>> ce: oldCounterExamples.getL1()){
	    ceElimination.ce1Elimination(automataEncoding, ce);
	}
    }

}