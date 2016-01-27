package symmetryencoding.cegeneration;

import java.util.List;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.encoding.TransducerEncoding;

public class PairAcceptance {

	private TransducerEncoding encoding;
	
	public PairAcceptance(TransducerEncoding encoding){
		this.encoding = encoding;
		this.startIndexEVar = encoding.getNextSATVar();
	}

    /**
     * Simpler encoding that only works of the return variable is used
     * negatively
     */
    public int encodeNeg(List<Integer> v, List<Integer> w) throws ContradictionException{
	int m = v.size();
	int[] clause;
		
	int numStates = encoding.getNumStates();
		
	//rule 2
	for(int i = 1; i <= m-1; i++){
	    for(int q = 1; q <= numStates; q++){
		for(int qPrime = 1; qPrime <= numStates; qPrime++){
		    clause = new int[3];
		    clause[0] = -getEShort(i, q, v, w);
		    clause[1] = -encoding.getTransBoolVar(q, v.get(i), w.get(i), qPrime);
		    clause[2] = getEShort(i+1, qPrime, v, w);
		    encoding.addClause(clause);
		}
	    }
	}
	
	//rule 4, Philipp approach
	for(int q = 1; q <= numStates; q++){
	    clause = new int[3];
	    clause[0] = -getEShort(m, q, v, w);
	    clause[1] = -encoding.getIndexZVar(q);
	    clause[2] = getAcceptVW();
	    encoding.addClause(clause);
	}

	encoding.setNextSATVar(this.startIndexEVar + (m - 1) * numStates + 1);
		
	return getAcceptVW();
    }

    /**
     * i, q start from 1
     * @param i
     * @param q
     * @return
     */
    private int getEShort(int i, int q, List<Integer> v, List<Integer> w){
	if (i == 1)
	    return encoding.getTransBoolVar(1, v.get(0), w.get(0), q);
	i--;
	i--;
	q--;
	return (q + encoding.getNumStates() * i) + startIndexEVar + 1;
    }
	
	/**
	 * 
	 * @param v
	 * @param w
	 * @return the index of variable for accept v, w
	 * @throws ContradictionException
	 */
	public int encode(List<Integer> v, List<Integer> w) throws ContradictionException{
		int m = v.size();
		int[] clause;
		
		int numStates = encoding.getNumStates();
		
		//rule 1
		for(int q = 1; q <= numStates; q++){
			clause = new int[2];
			clause[0] = -encoding.getTransBoolVar(1, v.get(0), w.get(0), q);
			clause[1] = getE(1, q);
			encoding.addClause(clause);
			
			//
			clause[0] = -clause[0];
			clause[1] = -clause[1];
			encoding.addClause(clause);
		}
		
		//rule 2
		for(int i = 1; i <= m-1; i++){
			for(int q = 1; q <= numStates; q++){
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					clause = new int[3];
					clause[0] = -getE(i, q);
					clause[1] = -encoding.getTransBoolVar(q, v.get(i), w.get(i), qPrime);
					clause[2] = getE(i+1, qPrime);
					encoding.addClause(clause);
				}
			}
		}
		
		//rule 3
		this.startIndexFVar = this.startIndexEVar + m * numStates + 1;
		addFVarCondition(m, numStates, v, w);
		
		for(int i = 2; i <= m; i++){
			for(int q= 1; q <= numStates; q++){
				clause = new int[numStates +1];
				clause[0] = -getE(i, q);
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					clause[qPrime] = getF(i, q, qPrime);
				}
				
				encoding.addClause(clause);
				
			}
		}
		
		
		//rule 4, Philipp approach
		for(int q = 1; q <= numStates; q++){
			clause = new int[3];
			clause[0] = -getE(m, q);
			clause[1] = -encoding.getIndexZVar(q);
			clause[2] = getAcceptVW();
			encoding.addClause(clause);
		}
		
		for(int q = 1; q <= numStates; q++){
			clause = new int[3];
			clause[0] = -getE(m, q);
			clause[1] = encoding.getIndexZVar(q);
			clause[2] = -getAcceptVW();
			encoding.addClause(clause);
		}
		

		clause = new int[numStates + 1];
		clause[0] = -getAcceptVW();
		for(int q = 1; q <= numStates; q++){
			clause[q] = getE(m, q);
		}
		encoding.addClause(clause);
		
		
		//for boolean variables of e, f
		encoding.setNextSATVar(this.startIndexEVar + m * numStates + 1 + (m-1) * numStates * numStates);
		
		return getAcceptVW();
	}
	
	/**
	 * f(i, q, q') <-> x(q', a_i, b_i, q) \land e(i-1, q')
	 * @param m
	 * @param numStates
	 * @param v
	 * @param w
	 * @throws ContradictionException
	 */
	private void addFVarCondition(int m, int numStates, List<Integer> v, List<Integer> w) throws ContradictionException{
		int[] clause;
		
		for(int i = 2; i <= m; i++ ){
			for(int q = 1; q <= numStates; q++){
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					int f = getF(i, q, qPrime);
					int x = encoding.getTransBoolVar(qPrime, v.get(i-1), w.get(i-1), q);
					int e = getE(i-1, qPrime);
					
					clause = new int[2];
					clause[0] = -f;
					clause[1] = x;
					encoding.addClause(clause);
					
					clause = new int[2];
					clause[0] = -f;
					clause[1] = e;
					encoding.addClause(clause);
					
					clause = new int[3];
					clause[0] = -x;
					clause[1] = -e;
					clause[2] = f;
					encoding.addClause(clause);
				}
			}
		}
	}

	private int startIndexEVar;
	
	/**
	 * i, q start from 1
	 * @param i
	 * @param q
	 * @return
	 */
	private int getE(int i, int q){
		i--;
		q--;
		return (q + encoding.getNumStates() * i) + startIndexEVar + 1;
	}
	
	private int startIndexFVar;
	/**
	 * i coun from 2, q & qPrime count from 1
	 * @param i
	 * @param q
	 * @param qPrime
	 * @return
	 */
	private int getF(int i, int q, int qPrime){
		i-=2;
		q--;
		qPrime--;
		return (qPrime + encoding.getNumStates() * (q + encoding.getNumStates() * i)) + startIndexFVar;
	}
	
	/**
	 * Preserve variable at startIndex for accept v, w
	 * @return
	 */
	private int getAcceptVW(){
		return startIndexEVar;
	}
	
}
