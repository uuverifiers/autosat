package encoding;

import org.sat4j.specs.ContradictionException;

public class TransducerC2Encoding {
	private TransducerEncoding encoding;
	private int numStates;
	private int numLetters;
	
	private int startIndexOfRVars;
	
	public TransducerC2Encoding(TransducerEncoding encoding) {
		this.encoding = encoding;
		this.numStates = encoding.getNumStates();
		this.numLetters = encoding.getNumLetters();
		
		allocateBoolVars();
	}
	
	private void allocateBoolVars() {
		startIndexOfRVars = encoding.getSolver().getNextSATVar();		
		encoding.getSolver().setNextSATVar(startIndexOfRVars + numStates);
	}
	
	public void encode() throws ContradictionException {
		//r1
		int[] clause = new int[]{getIndexRVar(1)};
		encoding.getSolver().addClause(clause);
		
		//
		for(int q = 1; q <= numStates; q++){
			clause = new int[]{-encoding.getIndexZVar(q), -getIndexRVar(q)};
			encoding.getSolver().addClause(clause);
		}
		
		for(int q = 1; q <= numStates; q++){
			for(int input = 0; input < numLetters; input++){
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					int x = encoding.getTransBoolVar(q, input, input, qPrime);
					clause = new int[]{-getIndexRVar(q), -x, getIndexRVar(qPrime)};
					encoding.getSolver().addClause(clause);
				}
			}
		}
	}
	/*
	 * q start from 1
	 */
	public int getIndexRVar(int q){
		return this.startIndexOfRVars + q - 1;
	}
}
