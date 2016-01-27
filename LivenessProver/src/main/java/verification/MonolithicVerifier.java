
package verification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import verification.OldCounterExamples;
import verification.ReachabilityChecking;
import verification.FiniteStateSets;
import encoding.ISatSolverFactory;
import visitor.AllVisitorImpl;
import visitor.SymmetryProb;
import common.VerificationUltility;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

public class MonolithicVerifier {
    private static final Logger LOGGER = LogManager.getLogger();
	
    private final ISatSolverFactory SOLVER_FACTORY;
    private final boolean useRankingFunctions;

    private static final boolean useGlobalSystemInvariant = false;

    private final SymmetryProb problem;

    public MonolithicVerifier(SymmetryProb problem,
			      ISatSolverFactory SOLVER_FACTORY,
			      boolean useRankingFunctions) {
	this.problem = problem;
	this.SOLVER_FACTORY = SOLVER_FACTORY;
	this.useRankingFunctions = useRankingFunctions;
    }

    public boolean verify() {
	int bound =
	    problem.getMaxNumOfStatesTransducer() * problem.getMaxNumOfStatesTransducer() +
	    problem.getMaxNumOfStatesAutomaton() * problem.getMaxNumOfStatesAutomaton();
		
	if (problem.getCloseInitStates())
	    bound +=
		problem.getMaxNumOfInitStatesAutomaton() *
		problem.getMaxNumOfInitStatesAutomaton();

	OldCounterExamples oldCEs = new OldCounterExamples();
	FiniteStateSets finiteStates =
	    new FiniteStateSets(problem.getNumberOfLetters(),
				problem.getI0(), problem.getF(),
				problem.getPlayer1(),
				problem.getPlayer2(),
				problem.getLabelToIndex());
		
	Automata systemInvariant = null;
	if (useGlobalSystemInvariant)
	    systemInvariant =
		VerificationUltility.getUniversalAutomaton(problem.getNumberOfLetters());

	for (int fixedSOS = 1; fixedSOS <= bound; ++fixedSOS) {
	    for(int numStateTransducer = problem.getMinNumOfStatesTransducer();
		numStateTransducer <= problem.getMaxNumOfStatesTransducer();
		numStateTransducer++){
		for(int numStateAutomata = problem.getMinNumOfStatesAutomaton();
		    numStateAutomata <= problem.getMaxNumOfStatesAutomaton();
		    numStateAutomata++){
		    for(int numInitStateAutomata = problem.getMinNumOfInitStatesAutomaton();
			numInitStateAutomata <= problem.getMaxNumOfInitStatesAutomaton();
			numInitStateAutomata++){

			int sos =
			    numStateTransducer * numStateTransducer +
			    numStateAutomata * numStateAutomata;
			if (problem.getCloseInitStates())
			    sos += numInitStateAutomata * numInitStateAutomata;

			if (sos != fixedSOS)
			    continue;

			LOGGER.info("Transducer states: " + numStateTransducer +
				    ", automaton states: " + numStateAutomata);
			ReachabilityChecking checking =
			    new ReachabilityChecking(useRankingFunctions,
						     problem.getCloseInitStates(),
						     true,
						     SOLVER_FACTORY);
			checking.setAutomataNumStates(numStateAutomata);
			checking.setF(problem.getF());
			checking.setWinningStates(problem.getF());
			checking.setI0(problem.getI0());
			checking.setNumLetters(problem.getNumberOfLetters());
			checking.setPlayer1(problem.getPlayer1());
			checking.setPlayer2(problem.getPlayer2());
			checking.setTransducerNumStates(numStateTransducer);
			checking.setLabelToIndex(problem.getLabelToIndex());
			checking.setOldCounterExamples(oldCEs);
			checking.setFiniteStateSets(finiteStates);
			checking.setSystemInvariant(systemInvariant);

			checking.setup();

			boolean result = checking.findNextSolution(true);
			if(result){
			    return true;
			}

			systemInvariant = checking.getSystemInvariant();
		    }
		}
	    }
	}

	return false;
    }
}
