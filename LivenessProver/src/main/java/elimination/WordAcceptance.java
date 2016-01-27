package elimination;

import java.util.List;

import org.sat4j.specs.ContradictionException;

import encoding.AutomataEncoding;

public class WordAcceptance {
	private AutomataEncoding encoding;

	public WordAcceptance(AutomataEncoding encoding) {
		this.encoding = encoding;
	}

	/**
	* to not accept v, use negation of returned value
	 */
	public int encodeNeg(List<Integer> v)
			throws ContradictionException {
            if (v.isEmpty()) {
		return encode(v);
	    }

		int m = v.size();
		int[] clause;

		int numStates = encoding.getNumStates();

		this.startIndexEVar = encoding.getSolver().getNextSATVar();
		encoding.getSolver().setNextSATVar(this.startIndexEVar + (m - 1) * numStates + 1);

		// rule 2
		for (int i = 1; i <= m - 1; i++) {
			for (int q = 1; q <= numStates; q++) {
				for (int qPrime = 1; qPrime <= numStates; qPrime++) {
					clause = new int[3];
					clause[0] = -getEShort(i, q, v);
					clause[1] = -encoding.getTransBoolVar(q, v.get(i), qPrime);
					clause[2] = getEShort(i + 1, qPrime, v);
					encoding.getSolver().addClause(clause);
				}
			}
		}

		// rule 4, Philipp approach
		for (int q = 1; q <= numStates; q++) {
			clause = new int[3];
			clause[0] = -getEShort(m, q, v);
			clause[1] = -encoding.getIndexZVar(q);
			clause[2] = getAcceptVW();
			encoding.getSolver().addClause(clause);
		}

		return getAcceptVW();
	}

	/**
	 * i, q start from 1
	 */
	private int getEShort(int i, int q, List<Integer> v) {
		if (i == 1)
			return encoding.getTransBoolVar(1, v.get(0), q);
		i--;
		i--;
		q--;
		return (q + encoding.getNumStates() * i) + startIndexEVar + 1;
	}

	/**
	 * @return the index of variable for accept v, w
	 */
	public int encode(List<Integer> v)
			throws ContradictionException {
            if (v.isEmpty()) {
		this.startIndexEVar = encoding.getSolver().getNextSATVar();
		encoding.getSolver().setNextSATVar(this.startIndexEVar + 1);
        	encoding.getSolver().addClause(new int[] {
                        -startIndexEVar, encoding.getIndexZVar(1)
                    });
        	encoding.getSolver().addClause(new int[] {
                        startIndexEVar, -encoding.getIndexZVar(1)
                    });
                return getAcceptVW();
            }

		int m = v.size();
		int[] clause;

		int numStates = encoding.getNumStates();

		this.startIndexEVar = encoding.getSolver().getNextSATVar();
		this.startIndexFVar = this.startIndexEVar + m * numStates + 1;
		// for boolean variables of e, f
		encoding.getSolver().setNextSATVar(startIndexFVar + (m - 1) * numStates * numStates);

		// rule 1
		for (int q = 1; q <= numStates; q++) {
			clause = new int[2];
			clause[0] = -encoding.getTransBoolVar(1, v.get(0), q);
			clause[1] = getE(1, q);
			encoding.getSolver().addClause(clause);

			//
			clause[0] = -clause[0];
			clause[1] = -clause[1];
			encoding.getSolver().addClause(clause);
		}

		// rule 2
		for (int i = 1; i <= m - 1; i++) {
			for (int q = 1; q <= numStates; q++) {
				for (int qPrime = 1; qPrime <= numStates; qPrime++) {
					clause = new int[3];
					clause[0] = -getE(i, q);
					clause[1] = -encoding.getTransBoolVar(q, v.get(i), qPrime);
					clause[2] = getE(i + 1, qPrime);
					encoding.getSolver().addClause(clause);
				}
			}
		}

		// rule 3

		addFVarCondition(m, numStates, v);

		for (int i = 2; i <= m; i++) {
			for (int q = 1; q <= numStates; q++) {
				clause = new int[numStates + 1];
				clause[0] = -getE(i, q);
				for (int qPrime = 1; qPrime <= numStates; qPrime++) {
					clause[qPrime] = getF(i, q, qPrime);
				}

				encoding.getSolver().addClause(clause);

			}
		}

		// rule 4, Philipp approach
		for (int q = 1; q <= numStates; q++) {
			clause = new int[3];
			clause[0] = -getE(m, q);
			clause[1] = -encoding.getIndexZVar(q);
			clause[2] = getAcceptVW();
			encoding.getSolver().addClause(clause);
		}

		for (int q = 1; q <= numStates; q++) {
			clause = new int[3];
			clause[0] = -getE(m, q);
			clause[1] = encoding.getIndexZVar(q);
			clause[2] = -getAcceptVW();
			encoding.getSolver().addClause(clause);
		}

		clause = new int[numStates + 1];
		clause[0] = -getAcceptVW();
		for (int q = 1; q <= numStates; q++) {
			clause[q] = getE(m, q);
		}
		encoding.getSolver().addClause(clause);

		return getAcceptVW();
	}

	/**
	 * f(i, q, q') <-> x(q', a_i, b_i, q) \land e(i-1, q')
	 */
	private void addFVarCondition(int m, int numStates, List<Integer> v) throws ContradictionException {
		int[] clause;

		for (int i = 2; i <= m; i++) {
			for (int q = 1; q <= numStates; q++) {
				for (int qPrime = 1; qPrime <= numStates; qPrime++) {
					int f = getF(i, q, qPrime);
					int x = encoding.getTransBoolVar(qPrime, v.get(i - 1), q);
					int e = getE(i - 1, qPrime);

					clause = new int[2];
					clause[0] = -f;
					clause[1] = x;
					encoding.getSolver().addClause(clause);

					clause = new int[2];
					clause[0] = -f;
					clause[1] = e;
					encoding.getSolver().addClause(clause);

					clause = new int[3];
					clause[0] = -x;
					clause[1] = -e;
					clause[2] = f;
					encoding.getSolver().addClause(clause);
				}
			}
		}
	}

	private int startIndexEVar;

	/**
	 * i, q start from 1
	 */
	private int getE(int i, int q) {
		i--;
		q--;
		return (q + encoding.getNumStates() * i) + startIndexEVar + 1;
	}

	private int startIndexFVar;

	/**
	 * i count from 2, q & qPrime count from 1
	 */
	private int getF(int i, int q, int qPrime) {
		i -= 2;
		q--;
		qPrime--;
		return (qPrime + encoding.getNumStates()
				* (q + encoding.getNumStates() * i))
				+ startIndexFVar;
	}

	/**
	 * Preserve variable at startIndex for accept v, w
	 */
	private int getAcceptVW() {
		return startIndexEVar;
	}
}
