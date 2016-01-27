package symmetryencoding.cegeneration;

import java.util.List;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.encoding.TransducerEncoding;

public class InputAcceptance {
	protected TransducerEncoding encoding;
	
	public InputAcceptance(TransducerEncoding encoding){
		this.encoding = encoding;
		this.startIndexEVar = encoding.getNextSATVar();
	}
	
	/**
	 * 
	 * @param v
	 * @throws ContradictionException
	 */
	public void accept(List<Integer> v) throws ContradictionException{
		addPrimaryRules(v);
		addTransitionRule(v);
		
		encoding.setNextSATVar(this.startIndexEVar + (v.size() + 1) * encoding.getNumStates());
	}
	
	protected void addPrimaryRules(List<Integer> v) throws ContradictionException{
		int m = v.size();
		int[] clause;
		
		int numStates = encoding.getNumStates();
		
		//rule 1
		for(int i = 0; i <= m; i++){
			clause = new int[numStates];
			for(int q = 1; q <= numStates; q++){
				clause[q-1] = getE(i, q);
			}
			encoding.addClause(clause);
		}
		
		//rule 2
		for(int i = 0; i <= m ;i++){
			for(int q = 1; q <= numStates; q++){
				for(int qPrime = q + 1; qPrime <= numStates; qPrime++){
					clause = new int[2];
					clause[0] = -getE(i, q);
					clause[1] = -getE(i, qPrime);
					encoding.addClause(clause);
				}
			}
		}
		
		//e(0, 1)
		clause = new int[1];
		clause[0] = getE(0, 1);
		encoding.addClause(clause);
		
		//rule 5
		for(int q = 1; q <= numStates; q++){
			clause = new int[2];
			clause[0] = -getE(m, q);
			clause[1] = encoding.getIndexZVar(q);
			encoding.addClause(clause);
		}
	}
	
	protected void addTransitionRule(List<Integer> v) throws ContradictionException{
		int m = v.size();
		int[] clause;
		
		int numStates = encoding.getNumStates();
		int numLetters = encoding.getNumLetters();
	
		//rule 4
		for(int i = 1; i <= m; i++){
			for(int q = 1; q <= numStates; q++){
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					clause = new int[numLetters + 2];
					clause[0] = -getE(i-1, q);
					clause[1] = -getE(i, qPrime);
					
					for(int b = 0; b < numLetters; b++){
						clause[b+2] = encoding.getTransBoolVar(q, v.get(i-1), b, qPrime);
					}
					encoding.addClause(clause);
				}
			}
			
			
		}
	}
	
	protected int startIndexEVar;
	
	/**
	 * q starts from 1, i starts from 0
	 * @param i
	 * @param q
	 * @return
	 */
	protected int getE(int i, int q){
		q--;
		return (q + encoding.getNumStates() * i) + startIndexEVar;
	}
}
