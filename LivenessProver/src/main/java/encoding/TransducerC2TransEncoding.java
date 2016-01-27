package encoding;

import org.sat4j.specs.ContradictionException;

public class TransducerC2TransEncoding {
	private TransducerEncoding encoding;
	private int numStates;
	private int numLetters;
	
	private int startIndexOfRVars;
	private int startIndexOfSVars;
	private int startIndexOfTVars;
	
	public TransducerC2TransEncoding(TransducerEncoding encoding) {
		this.encoding = encoding;
		this.numStates = encoding.getNumStates();
		this.numLetters = encoding.getNumLetters();
		
		allocateBoolVars();
	}
	
	private void allocateBoolVars() {
		startIndexOfRVars = encoding.getSolver().getNextSATVar();
		encoding.getSolver().setNextSATVar(startIndexOfRVars + numStates);
		startIndexOfSVars = encoding.getSolver().getNextSATVar();
		encoding.getSolver().setNextSATVar(startIndexOfSVars + numStates);
		startIndexOfTVars = encoding.getSolver().getNextSATVar();
		encoding.getSolver().setNextSATVar(startIndexOfTVars +
                                                   numStates * (numStates - 1) * numLetters * numLetters);
	}
	
	public void encode() throws ContradictionException {
		// r_q for initial state q
		encoding.getSolver().addClause(new int[]{getIndexRVar(1)});
		
		// s_q for final states q
		for(int q = 1; q <= numStates; q++)
		    encoding.getSolver().addClause(new int[]{-encoding.getIndexZVar(q),
							     getIndexSVar(q)});
		
		// r_q and s_q are not set at the same time for any state
		for(int q = 1; q <= numStates; q++)
		    encoding.getSolver().addClause(new int[]{-getIndexRVar(q),
							     -getIndexSVar(q)});

		// r_q propagate forward, s_q propagate backward
		for(int q = 1; q <= numStates; q++){
		    for(int input = 0; input < numLetters; input++){
			for(int qPrime = 1; qPrime <= numStates; qPrime++){
			    int x = encoding.getTransBoolVar(q, input, input, qPrime);
			    encoding.getSolver().addClause(new int[]{-getIndexRVar(q), -x,
								     getIndexRVar(qPrime)});
			    encoding.getSolver().addClause(new int[]{-getIndexSVar(qPrime), -x,
								     getIndexSVar(q)});
			}
		    }
		}

		// for distinct states q, q' with r_q and s_q', if
		// there are transitions from q to q' with labels a/b
		// and b/c (with a != b and b != c), then q also has
		// an outgoing transition with label a/c to a state
		// q'' with s_q''

		for (int q1 = 1; q1 <= numStates; q1++)
		    for (int q2 = 1; q2 <= numStates; q2++)
			if (q1 != q2)
			    for (int l1 = 0; l1 < numLetters; ++l1)
				for (int l2 = 0; l2 < numLetters; ++l2)
				    if (l1 != l2)
					for (int l3 = 0; l3 < numLetters; ++l3)
					    if (l2 != l3) {
						int[] clause = new int [4 + numStates - 1];
						clause[0] = -getIndexRVar(q1);
						clause[1] = -getIndexSVar(q2);
						clause[2] = -encoding.getTransBoolVar(q1, l1, l2, q2);
						clause[3] = -encoding.getTransBoolVar(q1, l2, l3, q2);
						for (int q3 = 1; q3 <= numStates; ++q3)
						    if (q3 != q1) {
							int ind;
							if (q3 > q1)
							    ind = q3 - 1;
							else
							    ind = q3;
							clause[3 + ind] = getIndexTVar(q1, l1, l3, q3);
						    }
						encoding.getSolver().addClause(clause);
					    }

		for (int q1 = 1; q1 <= numStates; q1++)
		    for (int q2 = 1; q2 <= numStates; q2++)
			if (q1 != q2)
			    for (int l1 = 0; l1 < numLetters; ++l1)
				for (int l2 = 0; l2 < numLetters; ++l2) {
				    encoding.getSolver().addClause
					(new int[] { -getIndexTVar(q1, l1, l2, q2),
						     getIndexSVar(q2) });
				    encoding.getSolver().addClause
					(new int[] { -getIndexTVar(q1, l1, l2, q2),
						     encoding.getTransBoolVar(q1, l1, l2, q2) });
				}
	}

	/*
	 * q start from 1
	 */
	private int getIndexRVar(int q){
		return this.startIndexOfRVars + q - 1;
	}

	/*
	 * q start from 1
	 */
	private int getIndexSVar(int q){
		return this.startIndexOfSVars + q - 1;
	}

	/*
	 * q1, q2 start from 1; l1, l2 start from 0
	 */
    private int getIndexTVar(int q1, int l1, int l2, int q2) {
	assert(q1 != q2);
	--q1; --q2;
	if (q2 > q1)
	    --q2;
	return this.startIndexOfTVars +
	    ((q1 * (numStates - 1) + q2) * numLetters + l1) * numLetters + l2;
    }
}
