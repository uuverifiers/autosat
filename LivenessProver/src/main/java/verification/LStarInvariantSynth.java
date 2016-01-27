package verification;

import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.Ultility;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.lstar.LStar;
import common.finiteautomata.lstar.Teacher;

public class LStarInvariantSynth {
    private static final Logger LOGGER = LogManager.getLogger();

    private int numLetters;

    private Automata I0;
    private Automata F;
    private Automata relevantStates;
    private EdgeWeightedDigraph player1;
    private EdgeWeightedDigraph player2;
    private FiniteStateSets finiteStates;

    private int explicitExplorationDepth;

    public LStarInvariantSynth(int numLetters,
			       Automata I0,
			       Automata F,
			       EdgeWeightedDigraph player1,
			       EdgeWeightedDigraph player2,
			       FiniteStateSets finiteStates,
			       int explicitExplorationDepth) {
	this.I0 = I0;
	this.F = F;
	this.player1 = player1;
	this.player2 = player2;
	this.numLetters = numLetters;
	this.finiteStates = finiteStates;
	this.explicitExplorationDepth = explicitExplorationDepth;
	this.relevantStates = AutomataConverter.getComplement(F);
    }
    
    public Automata infer() {
	LOGGER.debug("Using L* to infer system invariant");

	final Teacher invTeacher = new InvTeacher();
	final LStar lstar = new LStar(numLetters, invTeacher);

	lstar.setup();
	lstar.solve();

	LOGGER.debug("FOUND SOLUTION!");
	LOGGER.debug(lstar.getSolution());

	return lstar.getSolution();
    }   

    private class InvTeacher implements Teacher {
	public boolean isAccepted(List<Integer> word) {
	    return finiteStates.isReachable(word);
	}

	public boolean isCorrectLanguage(Automata hyp,
					 List<List<Integer>> posCEX,
					 List<List<Integer>> negCEX) {
	    LOGGER.debug("found hypothesis, size " + hyp.getStates().length);
	    
	    // first test: are initial states contained?

	    SubsetChecking s1 = new SubsetChecking(I0, hyp);
	    List<Integer> w = s1.check();
	    if (w != null) {
		LOGGER.debug("I0 not contained: " + w);
		posCEX.add(w);
		return false;
	    }

	    // second test: are concrete unreachable configurations excluded?

	    for (int l = 0; l <= explicitExplorationDepth; ++l) {
		SubsetChecking s2 =
		    new SubsetChecking(AutomataConverter.getWordAutomaton(hyp, l),
				       finiteStates.getReachableStateAutomaton(l));
		List<Integer> w2 = s2.check();
		if (w2 != null) {
		    LOGGER.debug("not reachable: " + w2);
		    negCEX.add(w2);
		    return false;
		}
	    }

	    /*
		for (List<Integer> w2 : AutomataConverter.getWords(hyp, l))
		    if (!finiteStates.isReachable(w2)) {
			LOGGER.debug("not reachable: " + w2);
			negCEX.add(w2);
			return false;
		    }
	    */

	    // third test: is the invariant inductive?

	    InductivenessChecking s2 =
		new InductivenessChecking(hyp, relevantStates,
					  player1, numLetters);
	    List<List<Integer>> xy = s2.check();
	    if(xy != null){
		LOGGER.debug("inductiveness failed for P1: " + xy);

		// first check whether we can also find shorter words
		// that should not be accepted
		for (int l = explicitExplorationDepth + 1;
		     l <= xy.get(0).size();
		     ++l) {
		    SubsetChecking s3 =
			new SubsetChecking(AutomataConverter.getWordAutomaton(hyp, l),
					   finiteStates.getReachableStateAutomaton(l));
		    List<Integer> w3 = s3.check();
		    if (w3 != null) {
			LOGGER.debug("not reachable: " + w3);
			negCEX.add(w3);
			return false;
		    }
		}

		if (finiteStates.isReachable(xy.get(0)))
		    posCEX.add(xy.get(1));
		else
		    negCEX.add(xy.get(0));
		return false;
	    }

	    s2 = new InductivenessChecking(hyp, relevantStates,
					   player2, numLetters);
	    xy = s2.check();
	    if(xy != null){
		LOGGER.debug("inductiveness failed for P2: " + xy);
		if (finiteStates.isReachable(xy.get(0)))
		    posCEX.add(xy.get(1));
		else
		    negCEX.add(xy.get(0));
		return false;
	    }

	    return true;
	}
    }
}