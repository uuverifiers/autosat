package symmetryencoding.encoding;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.Configuration;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;

public class ParikhSymmetryEncoding {

	private TransducerEncoding encoding;

	private int numStates;
	private int numLetters;
	
	public ParikhSymmetryEncoding(TransducerEncoding encoding) {
		this.encoding = encoding;
		this.numStates = encoding.getNumStates();
		this.numLetters = encoding.getNumLetters();
	}

	public void encode() throws ContradictionException {
		falseVarIndex = encoding.getNextSATVar();
		encoding.setNextSATVar(falseVarIndex + 1);
		encoding.addClause(new int[] { -falseVarIndex });

		if(!Configuration.offParikh){
			addParikhCondition();
			addSymmetryBreakingConstraints();
		} else {
//                     addIOSymmetryBreakingConstraints();
			addFiniteImageConstraints(encoding.getFiniteOutputs());
        }
		if(!Configuration.offConditions.contains(Condition.Copycat)){
			addCopyCatConstraints();
		}
	}

	private int falseVarIndex;
	private int parikhStartIndex;

	/**
	 * state \in \{ 1, ..., numStates \} letter \in \{ 0, ..., numLetters - 2 \}
	 * diff \in \{ -numStates, ..., numStates \}
	 */
	private int getParikhDiffIndex(int state, int letter, int diff) {
		if (diff <= -numStates || diff >= numStates)
			return falseVarIndex;
		else
			return parikhStartIndex + ((state - 1) * (numLetters - 1) + letter)
					* (2 * numStates - 1) + diff + numStates - 1;
	}

	private int symmBreakingStartIndex;

	/**
	 * state \in \{ 2, ..., numStates - 1 \} letter \in \{ 0, ..., numLetters -
	 * 2 \} kind \in \{ 0, 1, 2, 3 \}
	 */
	private int getSymmBreakingIndex(int state, int letter, int kind) {
		return symmBreakingStartIndex
				+ ((state - 2) * (numLetters - 1) + letter) * 4 + kind;
	}
    
    /**
     * state \in \{ 2, ..., numStates \}
     * input, output \in \{ 0, ..., numLetters - 1 \}
     * kind \in \{ 0, 1, 2, 3, 4 \}
     */
    private int getIOSymmBreakingIndex(int state, int input, int output, int kind) {
        return symmBreakingStartIndex
            + (((state - 2) * numLetters + input) * numLetters + output) * 5 + kind;
    }

	private void addParikhCondition() throws ContradictionException {
		parikhStartIndex = encoding.getNextSATVar();
		encoding.setNextSATVar(parikhStartIndex + numStates * (numLetters - 1)
				* (2 * numStates - 1));

		int[] clause;

		// for each state and each letter, at least one index bit is set
		for (int state = 1; state <= numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				clause = new int[2 * numStates - 1];
				for (int d = -numStates + 1; d < numStates; ++d) {
					clause[d + numStates - 1] = getParikhDiffIndex(state,
							letter, d);
				}
				encoding.addClause(clause);
			}
		}

		// for each state and each letter, at most one index bit is set
		for (int state = 1; state <= numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				for (int d1 = -numStates + 1; d1 < numStates; ++d1) {
					for (int d2 = d1 + 1; d2 < numStates; ++d2) {
						encoding.addClause(new int[] {
								-getParikhDiffIndex(state, letter, d1),
								-getParikhDiffIndex(state, letter, d2) });
					}
				}
			}
		}

		// the initial state has difference 0 for each letter
		for (int letter = 0; letter < numLetters - 1; ++letter) {
			encoding.addClause(new int[] { getParikhDiffIndex(1, letter, 0) });
		}

		// final states have difference 0 for each letter
		for (int state = 1; state <= numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				encoding.addClause(new int[] { -encoding.getIndexZVar(state),
						getParikhDiffIndex(state, letter, 0) });
			}
		}

		// transitions increase or decrease difference
		for (int state1 = 1; state1 <= numStates; ++state1) {
			for (int state2 = 1; state2 <= numStates; ++state2) {
				for (int input = 0; input < numLetters; ++input) {
					for (int output = 0; output < numLetters; ++output) {
						for (int letter = 0; letter < numLetters - 1; ++letter) {
							for (int oldD = -numStates + 1; oldD < numStates; ++oldD) {

								int diffdiff = (letter == output ? 1 : 0)
										- (letter == input ? 1 : 0);

								encoding.addClause(new int[] {
										-encoding.getTransBoolVar(state1, input, output,
												state2),
										-getParikhDiffIndex(state1, letter,
												oldD),
										getParikhDiffIndex(state2, letter, oldD
												+ diffdiff) });
							}
						}
					}
				}
			}
		}
	}

	private void addSymmetryBreakingConstraints() throws ContradictionException {
		symmBreakingStartIndex = encoding.getNextSATVar();
		encoding.setNextSATVar(symmBreakingStartIndex + (numStates - 2)
				* (numLetters - 1) * 4);

		// break symmetries by asserting that states are sorted according
		// to Parikh difference (compared lexicographically)

		// kind = 0: equality of Parikh difference
		for (int state = 2; state < numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				for (int d = -numStates + 1; d < numStates; ++d) {
					encoding.addClause(new int[] {
							-getSymmBreakingIndex(state, letter, 0),
							-getParikhDiffIndex(state, letter, d),
							getParikhDiffIndex(state + 1, letter, d) });
				}
			}
		}

		// kind = 1: > relation of Parikh difference
		for (int state = 2; state < numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				for (int d = -numStates + 1; d < numStates; ++d) {
					encoding.addClause(new int[] {
							-getSymmBreakingIndex(state, letter, 1),
							-getParikhDiffIndex(state, letter, d),
							getParikhDiffIndex(state + 1, letter, d + 1),
							getParikhDiffIndex(state + 1, letter, d + 2) });
				}
			}
		}

		// kind = 2: prefix equality of Parikh differences
		for (int state = 2; state < numStates; ++state) {
			for (int letter = 0; letter < numLetters - 1; ++letter) {
				encoding.addClause(new int[] { -getSymmBreakingIndex(state, letter, 2),
						getSymmBreakingIndex(state, letter, 0) });
			}
			for (int letter = 1; letter < numLetters - 1; ++letter) {
				encoding.addClause(new int[] { -getSymmBreakingIndex(state, letter, 2),
						getSymmBreakingIndex(state, letter - 1, 2) });
			}
		}

		// kind = 3: lexicographic > relation of Parikh differences
		for (int state = 2; state < numStates; ++state) {
			encoding.addClause(new int[] { -getSymmBreakingIndex(state, 0, 3),
					getSymmBreakingIndex(state, 0, 1) });
			for (int letter = 1; letter < numLetters - 1; ++letter) {
				encoding.addClause(new int[] { -getSymmBreakingIndex(state, letter, 3),
						getSymmBreakingIndex(state, letter - 1, 3),
						getSymmBreakingIndex(state, letter - 1, 2) });
				encoding.addClause(new int[] { -getSymmBreakingIndex(state, letter, 3),
						getSymmBreakingIndex(state, letter - 1, 3),
						getSymmBreakingIndex(state, letter, 1) });
			}
		}

		// assert lexicographic <= sortedness of the states
		for (int state = 2; state < numStates; ++state) {
			encoding.addClause(new int[] {
					getSymmBreakingIndex(state, numLetters - 2, 2),
					getSymmBreakingIndex(state, numLetters - 2, 3) });
		}
	}

    private void addIOSymmetryBreakingConstraints() throws ContradictionException {
        symmBreakingStartIndex = encoding.getNextSATVar();
        encoding.setNextSATVar(symmBreakingStartIndex +
                               (numStates - 1) * numLetters * numLetters * 5);

        // break symmetries by asserting that states are sorted according
        // to existing of outgoing transitions (compared lexicographically)

        // kind = 4: outgoing transitions with specific input/output letter
        for (int state = 2; state <= numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    for (int target = 1; target <= numStates; ++target)
                        encoding.addClause(new int[] {
                                -encoding.getTransBoolVar(state, input, output, target),
                                getIOSymmBreakingIndex(state, input, output, 4)
                            });
                    
                    int[] clause = new int [numStates + 1];
                    clause[0] = -getIOSymmBreakingIndex(state, input, output, 4);
                    for (int target = 1; target <= numStates; ++target)
                        clause[target] = encoding.getTransBoolVar(state, input, output, target);
                    encoding.addClause(clause);
                }
            }
        }
        
        // kind = 0: equality of outgoing transition flag
        for (int state = 2; state < numStates; ++state) {
            for (int input = 0; input < numLetters; ++input) {
                for (int output = 0; output < numLetters; ++output) {
                    encoding.addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 0),
                            -getIOSymmBreakingIndex(state, input, output, 4),
                            getIOSymmBreakingIndex(state + 1, input, output, 4)
                        });
                    encoding.addClause(new int[] {
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
                    encoding.addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 1),
                            -getIOSymmBreakingIndex(state, input, output, 4),
                        });
                    encoding.addClause(new int[] {
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
                    encoding.addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 2),
                            getIOSymmBreakingIndex(state, input, output, 0)
                        });
                }
                for (int output = (input == 0 ? 1 : 0); output < numLetters; ++output) {
                    if (output > 0)
                        encoding.addClause(new int[] {
                                -getIOSymmBreakingIndex(state, input, output, 2),
                                getIOSymmBreakingIndex(state, input, output - 1, 2)
                            });
                    else
                        encoding.addClause(new int[] {
                                -getIOSymmBreakingIndex(state, input, output, 2),
                                getIOSymmBreakingIndex(state, input - 1, numLetters - 1, 2)
                            });
                }
            }
        }

        // kind = 3: lexicographic > relation of Parikh differences
        for (int state = 2; state < numStates; ++state) {
            encoding.addClause(new int[] { -getIOSymmBreakingIndex(state, 0, 0, 3),
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
                    encoding.addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 2)
                        });
                    encoding.addClause(new int[] {
                            -getIOSymmBreakingIndex(state, input, output, 3),
                            getIOSymmBreakingIndex(state, prevInput, prevOutput, 3),
                            getIOSymmBreakingIndex(state, input, output, 1)
                        });
                }
            }
        }

        // assert lexicographic <= sortedness of the states
        for (int state = 2; state < numStates; ++state) {
            encoding.addClause(new int[] {
                    getIOSymmBreakingIndex(state, numLetters - 1, numLetters - 1, 2),
                    getIOSymmBreakingIndex(state, numLetters - 1, numLetters - 1, 3)
                });
        }
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
								clause[letter] = -encoding.getTransBoolVar(state1,
										letter, letter, state1);

							clause[letter] = -encoding.getIndexZVar(state1);
							++letter;
							clause[letter] = -encoding.getTransBoolVar(state1, letter1,
									letter2, state2);

							encoding.addClause(clause);
						}
					}
				}
			}
		}
	}

    private int finiteImageStartIndex;

    /**
     * state \in \{ 1, ..., numStates \}
     * num \in \{ 0, ..., numStates - 1 \}
     */
    private int getFiniteImageIndex(int state, int num) {
        if (num < 0 || num >= numStates)
            return falseVarIndex;
        else
            return finiteImageStartIndex + (state - 1) * numStates + num;
    }

    private void addFiniteImageConstraints(boolean[] finiteImageLetters)
        throws ContradictionException {
        finiteImageStartIndex = encoding.getNextSATVar();
        encoding.setNextSATVar(finiteImageStartIndex + numStates * numStates);
        
        int[] clause;

        // for each state, at least one counting bit is set
        for (int state = 1; state <= numStates; ++state) {
            clause = new int[numStates];
            for (int num = 0; num < numStates; ++num)
                clause[num] = getFiniteImageIndex(state, num);
            encoding.addClause(clause);
        }

        // for each state, at most one counting bit is set
        for (int state = 1; state <= numStates; ++state) {
            for (int num = 0; num < numStates - 1; ++num)
                for (int num2 = num + 1; num2 < numStates; ++num2)
                    encoding.addClause(new int[] {
                            -getFiniteImageIndex(state, num),
                            -getFiniteImageIndex(state, num2),
                        });
        }

        // the initial state has count 0
        encoding.addClause(new int[] {
                getFiniteImageIndex(1, 0)
            });

        // transitions either increase or keep the counter
        for (int state1 = 1; state1 <= numStates; ++state1) {
            for (int state2 = 1; state2 <= numStates; ++state2) {
                for (int input = 0; input < numLetters; ++input) {
                    for (int output = 0; output < numLetters; ++output) {
                        for (int num = 0; num < numStates; ++num) {
                            if (finiteImageLetters[output]) {
                                clause = new int [numStates - num + 1];
                                clause[0] = -encoding.getTransBoolVar(state1, input, output, state2);
                                clause[1] = -getFiniteImageIndex(state1, num);
                                for (int i = num + 1; i < numStates; ++i)
                                    clause[i - num + 1] = getFiniteImageIndex(state2, i);
                                encoding.addClause(clause);
                            } else {
                                encoding.addClause(new int[] {
                                        -encoding.getTransBoolVar(state1, input, output, state2),
                                        -getFiniteImageIndex(state1, num),
                                        getFiniteImageIndex(state2, num)
                                    });
                            }
                        }
                    }
                }
            }
        }
    }


	private void addNextAutomatonClause(int[] model)
			throws ContradictionException {
		int[] clause = new int[numStates * numStates * numLetters * numLetters
				+ numStates];
		int i = 0;

		for (int state1 = 1; state1 <= numStates; ++state1) {
			int var = encoding.getIndexZVar(state1);
			clause[i] = -model[var - 1];
			++i;

			for (int state2 = 1; state2 <= numStates; ++state2) {
				for (int input = 0; input < numLetters; ++input) {
					for (int output = 0; output < numLetters; ++output) {
						var = encoding.getTransBoolVar(state1, input, output, state2);
						clause[i] = -model[var - 1];
						++i;
					}
				}
			}
		}

		assert (i == clause.length);
		encoding.addClause(clause);
	}

}
