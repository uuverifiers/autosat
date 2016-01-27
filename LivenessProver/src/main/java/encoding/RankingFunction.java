package encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class RankingFunction implements ITransducerEncoding {
    
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ISatSolver solver;
	private Map<Integer, Map<Integer, List<Integer>>> mapStates2Transitions = new HashMap<Integer, Map<Integer,List<Integer>>>();
	private List<int[]> groupTransitionsBySourceInputOutput = new ArrayList<int[]>();
    private Map<List<Integer>, List<List<Integer>>> wordMappings = new HashMap<List<Integer>, List<List<Integer>>>();
	private int startIndexOfTransVars = 1;
	private int startIndexOfZVars = 1;
	private int startIndexOfDisjointInputs = 1;
	private int startIndexOfAcceptedInputs = 1;
	private int startIndexOfNonFunPairs = 1;
	private int numStates;
	private int numLetters;
	
	
	public RankingFunction(ISatSolver solver, int statesCount, int numLetters)
	{
		this.solver = solver;
		this.numStates = statesCount;
		this.numLetters = numLetters;
		allocateBoolVars(solver);
	}


	private void allocateBoolVars(ISatSolver solver) {
		//vars for transition and accepting states
		startIndexOfTransVars = solver.getNextSATVar();
		startIndexOfZVars =
		    startIndexOfTransVars + numStates * numStates * numLetters * numLetters;
                startIndexOfDisjointInputs =
		    startIndexOfZVars + numStates;
                startIndexOfAcceptedInputs =
		    startIndexOfDisjointInputs + numStates * numStates * numStates * numStates;
		startIndexOfNonFunPairs =
		    startIndexOfAcceptedInputs + numStates * numStates * numLetters;
		solver.setNextSATVar(startIndexOfNonFunPairs +
				     numStates * numStates);
	}

	public void encode() throws ContradictionException,
			TimeoutException {
		collectTransitionIndex();
		deterministicEncoding();
		
		InitAcceptingReachableEncoding c3c4Encoding = new InitAcceptingReachableEncoding(this);
		c3c4Encoding.encode();

		SymmetryBreakingEncoding symEncoding = new SymmetryBreakingEncoding(this);
		symEncoding.encode();
	
		addCopyCatConstraints();

		encodeFunConsistency();

                encodeDisjointInputs();

                /*
                  Restrict range of ranking function

	    for (int s1 = 1; s1 <= numStates; ++s1)
		for (int s2 = 1; s2 <= numStates; ++s2)
		    for (int input = 0; input < numLetters; ++input)
			for (int output1 = 3; output1 < numLetters; ++output1)
				solver.addClause(new int[] {
					-getTransBoolVar(s1, input, output1, s2) });
                */
	}

	private void collectTransitionIndex() throws ContradictionException {
		//states start from 1
		int indexRunner = startIndexOfTransVars;
		for(int source = 1; source <= numStates; source++){
			Map<Integer, List<Integer>> mapDestination2Transitions = new HashMap<Integer, List<Integer>>();
			
			//given input, output, index = input * numLetters + output
			//transByInputOutput[index] contains all transition with current source state, input and output
			//transByInputOutput[index][destination-1] is the transition with source, input, output, destination
			int[][]transByInputOutput = new int[numLetters*numLetters][numStates];
			
			for(int destination = 1; destination <= numStates; destination++){
				//
				int startTransition = indexRunner;
				
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						int index = input *numLetters + output;
						transByInputOutput[index][destination-1] = indexRunner;
						
						indexRunner++;
					}
				}
				
				//build mapping souce, destination to transitions, there are numLetters^2 transitions
				List<Integer> transitionsBySourceDestination = new ArrayList<Integer>();
				for(int i = 0; i < numLetters * numLetters; i++){
					transitionsBySourceDestination.add(i + startTransition);
				}
				mapDestination2Transitions.put(destination, transitionsBySourceDestination);
				
			}
			
			//done a source state
			this.mapStates2Transitions.put(source, mapDestination2Transitions);
			
			//separate transitions by source, input, output
			for(int i = 0; i < transByInputOutput.length; i++){
				this.groupTransitionsBySourceInputOutput.add(transByInputOutput[i]);
			}
		}
	}

	/*
	 * Determinisitc: No pair of transitions same source, input, output
	 */
	private void deterministicEncoding() throws ContradictionException {
		//c5 condition
		for(int[] transSameSourceInputOutput: this.groupTransitionsBySourceInputOutput){
			
			for(int i = 0; i < transSameSourceInputOutput.length; i++){
				for(int j = i + 1; j < transSameSourceInputOutput.length; j++){
					int[] notBothTransition = new int[]{-transSameSourceInputOutput[i], - transSameSourceInputOutput[j]};
					solver.addClause(notBothTransition);
				}
			}
		}
	}

	private void encodeFunConsistency() throws ContradictionException {
	    for (int s1 = 1; s1 <= numStates; ++s1)
		for (int s2 = 1; s2 <= numStates; ++s2)
		    for (int input = 0; input < numLetters; ++input)
			for (int output1 = 0; output1 < numLetters; ++output1)
			    for (int output2 = output1 + 1; output2 < numLetters; ++output2)
				solver.addClause(new int[] {
					-getTransBoolVar(s1, input, output1, s2),
					-getTransBoolVar(s1, input, output2, s2) });
	}

	// If an accepting state is copy-cat (that is,
	// it has self-edges labelled with a/a, for every letter a),
	// then the state must not have outgoing edges. Otherwise
	// a violation of functionality and injectivity can be
	// constructed
	private void addCopyCatConstraints() throws ContradictionException {
		int[] clause;

		for (int state1 = 1; state1 <= numStates; ++state1) {
		    for (int state2 = 1; state2 <= numStates; ++state2) {
			if (state1 != state2) {
			    for (int letter1 = 0; letter1 < numLetters; ++letter1) {
				for (int letter2 = 0; letter2 < numLetters; ++letter2) {
				    clause = new int[numLetters + 2];
				    
				    int letter = 0;
				    for (; letter < numLetters; ++letter)
					clause[letter] = -getTransBoolVar(state1,
									  letter, letter, state1);
				    
				    clause[letter] = -getIndexZVar(state1);
				    ++letter;
				    clause[letter] = -getTransBoolVar(state1, letter1,
								      letter2, state2);

				    solver.addClause(clause);
				}
			    }
			}
		    }
		}
	}

    private void encodeDisjointInputs() throws ContradictionException {

	// for each pair of states s, t, getAcceptedInputVar(s, input, t)
	// describes accepted inputs (for some arbitrary output)
        for (int s = 1; s <= numStates; ++s)
            for (int t = 1; t <= numStates; ++t)
		for (int input = 0; input < numLetters; ++input)
		    for (int output = 0; output < numLetters; ++output)
			solver.addClause(new int[] {
				-getTransBoolVar(s, input, output, t),
				getAcceptedInputVar(s, input, t)
			    });

	// for states s1, s2, t1, t2, getDisjointInputsVar(s1, s2, t1, t2)
	// expresses whether the sets of accepted inputs are disjoint
        for (int s1 = 1; s1 <= numStates; ++s1)
            for (int s2 = 1; s2 <= numStates; ++s2)
                for (int t1 = 1; t1 <= s1; ++t1)
                    for (int t2 = 1; (t1 == s1) ? (t2 < s2) : (t2 <= numStates); ++t2)
                        for (int input = 0; input < numLetters; ++input)
			    solver.addClause(new int[] {
				    -getDisjointInputsVar(s1, s2, t1, t2),
				    -getAcceptedInputVar(s1, input, s2),
				    -getAcceptedInputVar(t1, input, t2)
				});

	//////////////////////////////////
	/*	
        for (int s1 = 1; s1 <= numStates; ++s1)
            for (int s2 = 1; s2 <= numStates; ++s2)
                for (int t1 = 1; t1 <= s1; ++t1)
                    for (int t2 = 1; (t1 == s1) ? (t2 < s2) : (t2 <= numStates); ++t2) {
                        int disjointVar = getDisjointInputsVar(s1, s2, t1, t2);
                        int flags = solver.getNextSATVar();
                        solver.setNextSATVar(flags + numLetters);
                        for (int input = 0; input < numLetters; ++input)
                            for (int output = 0; output < numLetters; ++output) {
                                solver.addClause(new int[] {
                                        -getTransBoolVar(s1, input, output, s2),
                                        -disjointVar,
                                        flags + input
                                    });
                                solver.addClause(new int[] {
                                        -getTransBoolVar(t1, input, output, t2),
                                        -(flags + input)
                                    });
                            }
                    }
	*/
        //////////////////////////////////

        for (int t1 = 1; t1 <= numStates; ++t1)
            for (int t2 = 1; t2 < t1; ++t2)
		for (int s = 1; s <= numStates; ++s)
		    solver.addClause(new int[] {
			    getDisjointInputsVar(s, t1, s, t2),
			    getNonFunPairVar(t1, t2)
			});
        
        for (int s1 = 1; s1 <= numStates; ++s1)
            for (int t1 = 1; t1 < s1; ++t1)
		for (int s2 = 1; s2 <= numStates; ++s2)
		    for (int t2 = 1; t2 < s2; ++t2) {
			solver.addClause(new int[] {
				-getNonFunPairVar(s1, t1),
				getDisjointInputsVar(s1, s2, t1, t2),
				getNonFunPairVar(s2, t2)
			    });
			solver.addClause(new int[] {
				-getNonFunPairVar(s1, t1),
				getDisjointInputsVar(s1, t2, t1, s2),
				getNonFunPairVar(s2, t2)
			    });
		    }

        for (int t1 = 1; t1 <= numStates; ++t1)
            for (int t2 = 1; t2 < t1; ++t2)
		for (int s = 1; s <= numStates; ++s)
		    solver.addClause(new int[] {
			    getDisjointInputsVar(t1, s, t2, s),
			    -getNonFunPairVar(t1, t2)
			});

        for (int t1 = 1; t1 <= numStates; ++t1)
            for (int t2 = 1; t2 < t1; ++t2)
		solver.addClause(new int[] {
			-getIndexZVar(t1),
			-getIndexZVar(t2),
			-getNonFunPairVar(t1, t2)
		    });

    }
	
    public List<List<Integer>> mapWord(List<Integer> w)
        throws ContradictionException {

        List<List<Integer>> res = wordMappings.get(w);

        if (res != null)
            return res;
        int wordLen = w.size();

	int imgStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(imgStartIndex + wordLen * numLetters);

        res = new ArrayList<List<Integer>>();
        int varInd = imgStartIndex;
        for (int v : w) {
            List<Integer> vars = new ArrayList<Integer>();
            for (int i = 0; i < numLetters; ++i)
                vars.add(varInd++);
            res.add(vars);
        }

        wordMappings.put(w, res);

        // the image is a well-formed word
        for (int index = 0; index < wordLen; ++index) {
            int[] clause = new int[numLetters];
            for (int j = 0; j < numLetters; ++j)
                clause[j] = res.get(index).get(j);
            solver.addClause(clause);

            for (int j = 0; j < numLetters; ++j)
                for (int j2 = j + 1; j2 < numLetters; ++j2)
                    solver.addClause(new int[] { -res.get(index).get(j),
                                                 -res.get(index).get(j2) });
        }

        // automaton accepts the mapping
        int autRunStartIndex = solver.getNextSATVar();
	solver.setNextSATVar(autRunStartIndex + (wordLen + 1) * numStates);

        // at least one xRFx variable is set per state
        for (int index = 0; index <= wordLen; ++index) {
            int[] clause = new int[numStates];
            for (int state = 1; state <= numStates; ++state)
                clause[state - 1] =
                    autRunStartIndex + index * numStates + state - 1;
            solver.addClause(clause);
        }
        // initial xRFx state
        solver.addClause(new int[] { autRunStartIndex });

        // final xRFx states
        for (int state = 1; state <= numStates; ++state)
            solver.addClause(new int[] {
                    -(autRunStartIndex + wordLen * numStates + state - 1),
                    getIndexZVar(state) });

        // xRFx transitions
        for (int index = 0; index < wordLen; ++index)
            for (int state1 = 1; state1 <= numStates; ++state1)
                for (int state2 = 1; state2 <= numStates; ++state2)
                    for (int j = 0; j < numLetters; ++j)
                        solver.addClause(new int[] {
                                -(autRunStartIndex +
                                  index * numStates + state1 - 1),
                                -(autRunStartIndex +
                                  (index + 1) * numStates + state2 - 1),
                                -res.get(index).get(j),
                                getTransBoolVar(state1, w.get(index), j,
                                                state2)
                            });

        return res;
    }

	/*
	 * q start from 1
	 */
	public int getIndexZVar(int q){
		return this.startIndexOfZVars + q - 1;
	}
	
	public int getTransBoolVar(int source, int input, int output, int dest){
		source--;
		dest--;
		
		return this.startIndexOfTransVars + output + numLetters * (input + numLetters * (dest + numStates * source));
	}

	/*
	 * inputs start from 1
	 */
    private int getDisjointInputsVar(int s1, int s2, int t1, int t2) {
        if (s1 > t1 || (s1 == t1 && s2 > t2)) {
            int x = s1;
            int y = s2;
            s1 = t1;
            s2 = t2;
            t1 = x;
            t2 = y;
        }

        return startIndexOfDisjointInputs +
            (((s1-1) * numStates + (s2-1)) * numStates + (t1-1)) * numStates + (t2-1);
    }

    /*
     * s, t start from 1, a starts from 0
     */
    private int getAcceptedInputVar(int s, int a, int t) {
	return startIndexOfAcceptedInputs + ((s-1) * numLetters + a) * numStates + (t-1);
    }
	
    /*
     * s, t start from 1
     */
    private int getNonFunPairVar(int s, int t) {
	if (s > t) {
	    int x = s;
	    s = t;
	    t = x;
	}
	return startIndexOfNonFunPairs + (s-1) * numStates + (t-1);
    }

	public List<Integer> getTransitions(int source, int destination){
		return mapStates2Transitions.get(source).get(destination);
	}

	public int getNumStates() {
		return numStates;
	}

	public int getNumLetters() {
		return numLetters;
	}
	
	public int getStartIndexOfTransVars() {
		return startIndexOfTransVars;
	}

	public int getStartIndexOfZVars() {
		return startIndexOfZVars;
	}

	public ISatSolver getSolver() {
		return solver;
	}
}
