package elimination;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sat4j.specs.ContradictionException;

import common.VerificationUltility;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.State;
import common.bellmanford.EdgeWeightedDigraph;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import encoding.AutomataEncoding;
import encoding.TransducerEncoding;
import encoding.RankingFunction;
import encoding.ISatSolver;

public class CE4Elimination {
    private static final Logger LOGGER = LogManager.getLogger();

    private AutomataEncoding automataEncoding;
    private TransducerEncoding transducerEncoding;
    private RankingFunction rankingFunctionEncoding;
    private TransitivityPairSet transitivitySet;
    private ISatSolver solver;
    private Automata F;
    private EdgeWeightedDigraph player2;

    private List<Integer> ceX;
    private List<Integer> ceY;
    private int wordLen;
    private int numLetters;
 
    private int automatonNumStates;
    private int transducerNumStates;
    private int rankingFunctionNumStates;

    private Automata zLanguage;

    private int trueVar;
    
    public CE4Elimination(AutomataEncoding automataEncoding,
                          TransducerEncoding transducerEncoding,
			  TransitivityPairSet transitivitySet,
                          RankingFunction rankingFunctionEncoding,
                          Automata F, EdgeWeightedDigraph player2,
                          List<Integer> ceX, List<Integer> ceY) {
        this.automataEncoding = automataEncoding;
        this.transducerEncoding = transducerEncoding;
	this.rankingFunctionEncoding = rankingFunctionEncoding;
	this.transitivitySet = transitivitySet;
        this.F = F;
        this.player2 = player2;
        this.solver = automataEncoding.getSolver();
        this.ceX = ceX;
        this.ceY = ceY;
        this.wordLen = ceX.size();
        this.numLetters = automataEncoding.getNumLabels();
        this.automatonNumStates = automataEncoding.getNumStates();
	if (transducerEncoding != null)
	    this.transducerNumStates = transducerEncoding.getNumStates();
	if (rankingFunctionEncoding != null)
	    this.rankingFunctionNumStates = rankingFunctionEncoding.getNumStates();

	this.zLanguage = computeZLanguage();
    }

    public void encode() throws ContradictionException {
	trueVar = solver.getNextSATVar();
	solver.setNextSATVar(trueVar + 1);
	solver.addClause(new int[] { trueVar });

	// Bx non-acceptance
	int Bx;
	if (automatonNumStates >= 1) {
	    Bx = automataEncoding.acceptWord(ceX);
	    //	    WordAcceptance wordAcceptance = new WordAcceptance(automataEncoding);
	    //	    Bx = wordAcceptance.encodeNeg(ceX);
	} else {
	    Bx = trueVar;
	}

	// check how big the z language is

	if (zLanguage.getAcceptingStates().isEmpty()) {
	    solver.addClause(new int[] { -Bx });
	} else {
	    // is the z language small?

            List<List<Integer>> zWords =
                AutomataConverter.getWords(zLanguage, wordLen, 3);
            if (zWords == null) {
//		LOGGER.debug("big language");
		encodeFull(Bx);
            } else {
//		LOGGER.debug("possible z configurations: " + zWords);
		encodeFixedZ(zWords, Bx);
            }
	}
    }

    ////////////////////////////////////////////////////////////////////////////

    private void encodeFull(int Bx) throws ContradictionException {
        final int zLangStateNum = zLanguage.getStates().length;

	this.zStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.zStartIndex + wordLen * numLetters);
	this.BzStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.BzStartIndex + (wordLen + 1) * automatonNumStates + 1);
	this.zLangStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.zLangStartIndex + zLangStateNum + 1);

        // z is a well-formed word
        for (int index = 0; index < wordLen; ++index) {
            int[] clause = new int[numLetters];
            for (int j = 0; j < numLetters; ++j)
                clause[j] = getZWordVar(index, j);
            solver.addClause(clause);

            for (int j = 0; j < numLetters; ++j)
                for (int j2 = j + 1; j2 < numLetters; ++j2)
                    solver.addClause(new int[] { -getZWordVar(index, j),
                                                 -getZWordVar(index, j2) });
        }

	if (automatonNumStates >= 1) {
	    // at least one Bz variable is set per state
	    for (int index = 0; index <= wordLen; ++index) {
		int[] clause = new int[automatonNumStates];
		for (int state = 1; state <= automatonNumStates; ++state)
		    clause[state - 1] = getBzVar(index, state);
		solver.addClause(clause);
	    }

	    // initial Bz state
	    solver.addClause(new int[] { getBzVar(0, 1) });

	    // final Bz states
	    for (int state = 1; state <= automatonNumStates; ++state)
		solver.addClause(new int[] { -getBzAcceptance(),
					     -getBzVar(wordLen, state),
					     automataEncoding.getIndexZVar(state) });

	    // Bz transitions
	    for (int index = 0; index < wordLen; ++index)
		for (int state1 = 1; state1 <= automatonNumStates; ++state1)
		    for (int state2 = 1; state2 <= automatonNumStates; ++state2)
			for (int j = 0; j < numLetters; ++j)
			    solver.addClause(new int[] {
				    -getBzAcceptance(),
				    -getBzVar(index, state1),
				    -getBzVar(index + 1, state2),
				    -getZWordVar(index, j),
				    automataEncoding.getTransBoolVar(state1, j, state2)
				});
	} else {
	    solver.addClause(new int[] { getBzAcceptance() });
	}
        
        // y ->2 z acceptance

        // the zLanguage automaton is acyclic, compute the depth of the
        // individual states

	State[] zStates = zLanguage.getStates();
        int[] zDepth = new int [zLangStateNum];
	Arrays.fill(zDepth, -1);
        zDepth[zLanguage.getInitState()] = 0;
	boolean changed = true;
	while (changed) {
	    changed = false;
	    for (int i = 0; i < zLangStateNum; ++i)
		if (zDepth[i] >= 0) {
		    int depth = zDepth[i];
		    State state = zStates[i];
		    for (int j : state.getDest())
			if (zDepth[j] == -1) {
			    zDepth[j] = depth + 1;
			    changed = true;
			} else {
			    assert(zDepth[j] == depth + 1);
			}
		}
	}

	// for each depth, at least one of the state bits is set
	for (int depth = 0; depth <= ceY.size(); ++depth) {
	    int num = 1;
	    for (int j = 0; j < zLangStateNum; ++j)
		if (zDepth[j] == depth)
		    ++num;
	    int[] clause = new int [num];
	    clause[0] = -getZLangAcceptance();
	    num = 1;
	    for (int j = 0; j < zLangStateNum; ++j)
		if (zDepth[j] == depth)
		    clause[num++] = getZLangVar(j);
	    solver.addClause(clause);
	}

	// only accepting final states are selected
	for (int s1 = 0; s1 < zLangStateNum; ++s1)
	    if (zDepth[s1] == ceY.size() &&
		!zLanguage.getAcceptingStates().contains(s1))
		solver.addClause(new int[] { -getZLangVar(s1) });

        // whenever two consecutive states are selected in the z
        // automaton, a transition between them has to be possible
	for (int s1 = 0; s1 < zLangStateNum; ++s1)
	    for (int s2 = 0; s2 < zLangStateNum; ++s2)
		if (zDepth[s1] >= 0 && zDepth[s1] + 1 == zDepth[s2]) {
		    State state = zStates[s1];
		    int transNum = 0;
		    for (int i = 0; i < numLetters; ++i)
			if (state.getDest(i).contains(s2))
			    ++transNum;
		    int[] clause = new int [transNum + 2];
		    clause[0] = -getZLangVar(s1);
		    clause[1] = -getZLangVar(s2);
		    int j = 2;
		    for (int i = 0; i < numLetters; ++i)
			if (state.getDest(i).contains(s2))
			    clause[j++] = getZWordVar(zDepth[s1], i);
		    solver.addClause(clause);
		}

        int xGTz;
	if (rankingFunctionEncoding == null) {
	    xGTz = encodeFull_xGTz();
	} else {
	    xGTz = encodeLex_xGTz(null);
	}

        // root assertions
        solver.addClause(new int[] { -Bx, getBzAcceptance() /*, Fz */ });
        solver.addClause(new int[] { -Bx, getZLangAcceptance() });
        solver.addClause(new int[] { -Bx, xGTz });
    }

    ////////////////////////////////////////////////////////////////////////////

    private void encodeFixedZ(List<List<Integer>> ceZs,
			      int Bx) throws ContradictionException {
        int disjVarStart = solver.getNextSATVar();
        solver.setNextSATVar(disjVarStart + ceZs.size());

        int[] clause = new int [ceZs.size() + 1];
        clause[0] = -Bx;
        for (int i = 0; i < ceZs.size(); ++i)
            clause[i+1] = disjVarStart + i;

        solver.addClause(clause);

        for (int i = 0; i < ceZs.size(); ++i) {
            List<Integer> ceZ = ceZs.get(i);

            // Bz acceptance
            int Bz;
            if (automatonNumStates >= 1) {
		Bz = automataEncoding.acceptWord(ceZ);
		//                WordAcceptance wordAcceptance = new WordAcceptance(automataEncoding);
		//                Bz = wordAcceptance.encode(ceZ);
            } else {
                Bz = trueVar;
            }

            // xGTz acceptance
            //	PairAcceptance pairAcceptance = new PairAcceptance(transducerEncoding);
            //	int xGTz = pairAcceptance.encodePos(ceZ, ceX);
            int xGTz;

            if (rankingFunctionEncoding != null) {
                xGTz = encodeLex_xGTz(ceZ);
            } else {
                xGTz = transitivitySet.addPair(ceZ, ceX);
            }

            // root assertions
            solver.addClause(new int[] { -(disjVarStart + i), Bz });
            solver.addClause(new int[] { -(disjVarStart + i), xGTz });
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private int encodeFull_xGTz() throws ContradictionException {
	this.xGTzStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.xGTzStartIndex + (wordLen + 1) * transducerNumStates + 1);

        // at least one xGTz variable is set per state
        for (int index = 0; index <= wordLen; ++index) {
            int[] clause = new int[transducerNumStates];
            for (int state = 1; state <= transducerNumStates; ++state)
                clause[state - 1] = getxGTzVar(index, state);
            solver.addClause(clause);
        }
        // initial xGTz state
        solver.addClause(new int[] { getxGTzVar(0, 1) });

        // final xGTz states
        for (int state = 1; state <= transducerNumStates; ++state)
            solver.addClause(new int[] { -getxGTzAcceptance(),
                                         -getxGTzVar(wordLen, state),
                                         transducerEncoding.getIndexZVar(state) });

        // xGTz transitions
        for (int index = 0; index < wordLen; ++index)
            for (int state1 = 1; state1 <= transducerNumStates; ++state1)
                for (int state2 = 1; state2 <= transducerNumStates; ++state2)
                    for (int j = 0; j < numLetters; ++j)
                        solver.addClause(new int[] {
                                -getxGTzAcceptance(),
                                -getxGTzVar(index, state1),
                                -getxGTzVar(index + 1, state2),
                                -getZWordVar(index, j),
                                transducerEncoding.getTransBoolVar(state1, j, ceX.get(index), state2)
                            });
        
        return getxGTzAcceptance();
    }

    ////////////////////////////////////////////////////////////////////////////

    private int encodeLex_xGTz(List<Integer> ceZ) throws ContradictionException {
	this.xGTzStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.xGTzStartIndex + 1);
	this.RFxGTRFzStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(this.RFxGTRFzStartIndex + (wordLen + 1) * 2);

        List<List<Integer>> RFx = rankingFunctionEncoding.mapWord(ceX);

	//////////////////////////////
	// the ranking function maps z to RFz

        List<List<Integer>> RFz;

	if (ceZ != null) {
            RFz = rankingFunctionEncoding.mapWord(ceZ);
	} else {
	    // then there is whole language of possible z words, and
	    // we have a symbol representation available

            this.RFzStartIndex = solver.getNextSATVar();
            solver.setNextSATVar(this.RFzStartIndex + wordLen * numLetters);
            this.zRFzStartIndex = solver.getNextSATVar();
            solver.setNextSATVar(this.zRFzStartIndex + (wordLen + 1) * rankingFunctionNumStates);

            RFz = new ArrayList<List<Integer>>();
            for (int index = 0; index < wordLen; ++index) {
                List<Integer> vars = new ArrayList<Integer>();
                for (int i = 0; i < numLetters; ++i)
                    vars.add(getRFzWordVar(index, i));
                RFz.add(vars);
            }

            //////////////////////////////
            // RFz is a well-formed word
            for (int index = 0; index < wordLen; ++index) {
                int[] clause = new int[numLetters];
                for (int j = 0; j < numLetters; ++j)
                    clause[j] = getRFzWordVar(index, j);
                solver.addClause(clause);
                
                for (int j = 0; j < numLetters; ++j)
                    for (int j2 = j + 1; j2 < numLetters; ++j2)
                        solver.addClause(new int[] { -getRFzWordVar(index, j),
                                                     -getRFzWordVar(index, j2) });
            }

            // at least one zRFz variable is set per state
            for (int index = 0; index <= wordLen; ++index) {
                int[] clause = new int[rankingFunctionNumStates];
                for (int state = 1; state <= rankingFunctionNumStates; ++state)
                    clause[state - 1] = getzRFzVar(index, state);
                solver.addClause(clause);
            }
            // initial zRFz state
            solver.addClause(new int[] { getzRFzVar(0, 1) });

            // final zRFz states
            for (int state = 1; state <= rankingFunctionNumStates; ++state)
                solver.addClause(new int[] { -getzRFzVar(wordLen, state),
                                             rankingFunctionEncoding.getIndexZVar(state) });
            
	    // zRFz transitions
	    for (int index = 0; index < wordLen; ++index)
		for (int state1 = 1; state1 <= rankingFunctionNumStates; ++state1)
		    for (int state2 = 1; state2 <= rankingFunctionNumStates; ++state2)
			for (int k = 0; k < numLetters; ++k)
			    for (int j = 0; j < numLetters; ++j)
			    solver.addClause(new int[] {
				    -getzRFzVar(index, state1),
				    -getzRFzVar(index + 1, state2),
				    -getZWordVar(index, k),
				    -getRFzWordVar(index, j),
				    rankingFunctionEncoding.getTransBoolVar(state1, k, j, state2)
				});
	}

	//////////////////////////////
	// RFx is lexicographically bigger than RFz

	// empty prefixes are equal
        solver.addClause(new int[] { -getRFxGTRFzVar(0, 0) });
        solver.addClause(new int[] { -getRFxGTRFzVar(0, 1) });

	// as soon as RFx and RFz differ in one character,
	// lexicographic order is decided
        for (int index = 0; index < wordLen; ++index) {
	    for (int lx = 0; lx < numLetters; ++lx)
		for (int lz = 0; lz < numLetters; ++lz) {
		    int newBit0 = -1;
		    int newBit1 = -1;
		    if (lx > lz)
			newBit0 = 1;
		    if (lx < lz)
			newBit1 = 1;

		    solver.addClause(new int[] { getRFxGTRFzVar(index, 0),
						 getRFxGTRFzVar(index, 1),
						 -RFx.get(index).get(lx),
						 -RFz.get(index).get(lz),
						 getRFxGTRFzVar(index+1, 0) * newBit0 });
		    solver.addClause(new int[] { getRFxGTRFzVar(index, 0),
						 getRFxGTRFzVar(index, 1),
						 -RFx.get(index).get(lx),
						 -RFz.get(index).get(lz),
						 getRFxGTRFzVar(index+1, 1) * newBit1 });
		}

	    solver.addClause(new int[] { -getRFxGTRFzVar(index, 0),
					 getRFxGTRFzVar(index+1, 0) });
	    solver.addClause(new int[] { -getRFxGTRFzVar(index, 0),
					 -getRFxGTRFzVar(index+1, 1) });

	    solver.addClause(new int[] { -getRFxGTRFzVar(index, 1),
					 -getRFxGTRFzVar(index+1, 0) });
	    solver.addClause(new int[] { -getRFxGTRFzVar(index, 1),
					 getRFxGTRFzVar(index+1, 1) });
	}

	//////////////////////////////
	// overall acceptance

        return getRFxGTRFzAcceptance();
    }

    ////////////////////////////////////////////////////////////////////////////

    private Automata computeZLanguage() {
	return AutomataConverter.getImage(ceY, player2, numLetters);
    }

    private int zStartIndex;
    private int RFzStartIndex;
    
    /**
     * Both arguments start from 0;
     */
    private int getZWordVar(int index, int letter) {
        return zStartIndex + index * numLetters + letter;
    }

    /**
     * Both arguments start from 0;
     */
    private int getRFzWordVar(int index, int letter) {
        return RFzStartIndex + index * numLetters + letter;
    }

    private int[][] getZVars() {
        int[][] res = new int[wordLen][numLetters];
        for (int index = 0; index < wordLen; ++index)
            for (int j = 0; j < numLetters; ++j)
                res[index][j] = getZWordVar(index, j);
        return res;
    }

    private int BzStartIndex;

    /**
     * index starts from 0, state starts from 1.
     */
    private int getBzVar(int index, int state) {
        return BzStartIndex + index * automatonNumStates + state;
    }

    private int getBzAcceptance() {
        return BzStartIndex;
    }

    private int xGTzStartIndex;

    /**
     * index starts from 0, state starts from 1.
     */
    private int getxGTzVar(int index, int state) {
        return xGTzStartIndex + index * transducerNumStates + state;
    }

    private int getxGTzAcceptance() {
        return xGTzStartIndex;
    }

    private int zRFzStartIndex;

    /**
     * index starts from 0, state starts from 1.
     */
    private int getzRFzVar(int index, int state) {
        return zRFzStartIndex + index * rankingFunctionNumStates + state - 1;
    }

    private int RFxGTRFzStartIndex;

    /**
     * index starts from 0, bit is in {0, 1}.
     */
    private int getRFxGTRFzVar(int index, int bit) {
        return RFxGTRFzStartIndex + index * 2 + bit;
    }

    private int getRFxGTRFzAcceptance() {
        return RFxGTRFzStartIndex + wordLen * 2;
    }

    private int zLangStartIndex;

    /** state starts from 0 */
    private int getZLangVar(int state) {
        return zLangStartIndex + state + 1;
    }

    private int getZLangAcceptance() {
        return zLangStartIndex;
    }



}
