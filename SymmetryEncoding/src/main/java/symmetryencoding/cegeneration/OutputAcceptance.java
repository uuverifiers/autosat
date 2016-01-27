package symmetryencoding.cegeneration;

import java.util.List;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.encoding.TransducerEncoding;

public class OutputAcceptance extends InputAcceptance {
	
	public OutputAcceptance(TransducerEncoding encoding){
		super(encoding);
	}
	
	/**
	 * 
	 * @param w
	 * @throws ContradictionException
	 */
	@Override
	protected void addTransitionRule(List<Integer> w) throws ContradictionException{
		int m = w.size();
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
					
					for(int a = 0; a < numLetters; a++){
						clause[a+2] = encoding.getTransBoolVar(q, a, w.get(i-1), qPrime);
					}
					
					encoding.addClause(clause);
				}
			}
			
			
		}
	}
}
