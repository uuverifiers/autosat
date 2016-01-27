package encoding;

import org.sat4j.specs.ContradictionException;

public class SymmetryBreakingEncoding {

    private ITransducerEncoding encoding;

    private int numStates;
    private int numLetters;
	
    public SymmetryBreakingEncoding(ITransducerEncoding encoding) {
	this.encoding = encoding;
	this.numStates = encoding.getNumStates();
	this.numLetters = encoding.getNumLetters();
    }
    

    public void encode() throws ContradictionException {
	falseVarIndex = encoding.getSolver().getNextSATVar();
	encoding.getSolver().setNextSATVar(falseVarIndex + 1);
	encoding.getSolver().addClause(new int[] { -falseVarIndex });

	addIOSymmetryBreakingConstraints();
    }

    private int falseVarIndex;
    private int symmBreakingStartIndex;
    
    
    /**
     * state \in \{ 2, ..., numStates \}
     * input, output \in \{ 0, ..., numLetters - 1 \}
     * kind \in \{ 0, 1, 2, 3, 4 \}
     */
    private int getIOSymmBreakingIndex(int state, int input, int output, int kind) {
        return symmBreakingStartIndex
            + (((state - 2) * numLetters + input) * numLetters + output) * 5 + kind;
    }

    private void addIOSymmetryBreakingConstraints() throws ContradictionException {
        symmBreakingStartIndex = encoding.getSolver().getNextSATVar();
        encoding.getSolver().setNextSATVar(symmBreakingStartIndex +
                               (numStates - 1) * numLetters * numLetters * 5);

        // break symmetries by asserting that states are sorted according
        // to existing of outgoing transitions (compared lexicographically)

        // kind = 4: outgoing transitions with specific input/output letter
        for (int state = 2; state <= numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    for (int target = 1; target <= numStates; ++target)
                        encoding.getSolver().addClause(new int[] {
                                -encoding.getTransBoolVar(state, input, output, target),
                                getIOSymmBreakingIndex(state, input, output, 4)
                            });
                    
                    int[] clause = new int [numStates + 1];
                    clause[0] = -getIOSymmBreakingIndex(state, input, output, 4);
                    for (int target = 1; target <= numStates; ++target)
                        clause[target] = encoding.getTransBoolVar(state, input, output, target);
                    encoding.getSolver().addClause(clause);
                }
            }
        }
        
        // kind = 0: equality of outgoing transition flag
        for (int state = 2; state < numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 0),
                            -getIOSymmBreakingIndex(state, input, output, 4),
                            getIOSymmBreakingIndex(state + 1, input, output, 4)
                        });
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 0),
                            getIOSymmBreakingIndex(state, input, output, 4),
                            -getIOSymmBreakingIndex(state + 1, input, output, 4)
                        });
                }
            }
        }

        // kind = 1: > relation of outgoing transition flag
        for (int state = 2; state < numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 1),
                            -getIOSymmBreakingIndex(state, input, output, 4),
                        });
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 1),
                            getIOSymmBreakingIndex(state + 1, input, output, 4)
                        });
                }
            }
        }

        // kind = 2: prefix equality of Parikh differences
        for (int state = 2; state < numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 2),
                            getIOSymmBreakingIndex(state, input, output, 0)
                        });
                }
                for (int output = (input == 0 ? 1 : 0); output < numLetters; ++output) {
                    if (output > 0)
                        encoding.getSolver().addClause(new int[] {
                                -getIOSymmBreakingIndex(state, input, output, 2),
                                getIOSymmBreakingIndex(state, input, output - 1, 2)
                            });
                    else
                        encoding.getSolver().addClause(new int[] {
                                -getIOSymmBreakingIndex(state, input, output, 2),
                                getIOSymmBreakingIndex(state, input - 1, numLetters - 1, 2)
                            });
                }
            }
        }

        // kind = 3: lexicographic > relation of Parikh differences
        for (int state = 2; state < numStates; ++state) {
            encoding.getSolver().addClause(new int[] { -getIOSymmBreakingIndex(state, 0, 0, 3),
                                           getIOSymmBreakingIndex(state, 0, 0, 1) });
            for (int input = 0; input < numLetters; ++input) {
                for (int output = (input == 0 ? 1 : 0); output < numLetters; ++output) {
                    int prevInput;
                    int prevOutput;
                    if (output > 0) {
                        prevInput = input;
                        prevOutput = output - 1;
                    } else {
                        prevInput = input - 1;
                        prevOutput = numLetters - 1;
                    }
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 2)
                        });
                    encoding.getSolver().addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 3),
                            getIOSymmBreakingIndex(state, input, output, 1)
                        });
                }
            }
        }

        // assert lexicographic <= sortedness of the states
        for (int state = 2; state < numStates; ++state) {
            encoding.getSolver().addClause(new int[] {
                    getIOSymmBreakingIndex(state, numLetters - 1, numLetters - 1, 2),
                    getIOSymmBreakingIndex(state, numLetters - 1, numLetters - 1, 3)
                });
        }
    }
    
}