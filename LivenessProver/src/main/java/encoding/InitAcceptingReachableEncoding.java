package encoding;

import java.util.List;

import org.sat4j.specs.ContradictionException;

public class InitAcceptingReachableEncoding {

	private ITransducerEncoding encoding;
	private int numStates;
	private int numLetters;
	
	public InitAcceptingReachableEncoding(ITransducerEncoding encoding) {
		this.encoding = encoding;
		this.numStates = encoding.getNumStates();
		this.numLetters = encoding.getNumLetters();
		
		allocateBoolVars();
	}
	
	private void allocateBoolVars() {
		startIndexOfYVars = encoding.getSolver().getNextSATVar();
		startIndexOfYPrimeVars = startIndexOfYVars + numStates * numStates;
		
		startIndexOfTempY = startIndexOfYPrimeVars + numStates * numStates;
		startIndexOfTempYPrime = startIndexOfTempY + numStates * numStates * (numStates - 1);
		
		encoding.getSolver().setNextSATVar(startIndexOfTempYPrime + numStates * numStates * (numStates - 1));
	}
	
	public void encode() throws ContradictionException {
		//c4
		addTempYCondition();
		addTempYPrimeCondition();
		encodingBackwardReachability();
		encodingForwardReachability();
	}
	
	/*
	 * From every state of the transducer an accepting state can be reached.
	 */
	private void encodingBackwardReachability() throws ContradictionException{
		//condition 1
		for(int q = 1; q <= numStates; q++){
			int[] clause = new int[numStates];
			for(int d = 1; d <= numStates; d++){
				clause[d-1] = getYIndex(q, d);
			}
			encoding.getSolver().addClause(clause);
		}
		
		//condition 2
		for(int q = 1; q <= numStates; q++){
			int[] clause = new int[2];
			clause[0] = -getYIndex(q, 1);
			clause[1] = encoding.getIndexZVar(q);
			
			encoding.getSolver().addClause(clause);
		}
		
		//condition 3
		for(int q = 1; q <= numStates; q++){
			for(int d = 2; d <= numStates; d++){
				//y(q,d) => tempY(q, q', d-1) for all q'
				int[] clause = new int[numStates+1];
				clause[0] = -getYIndex(q, d);
				for(int qPrime = 1; qPrime <= numStates; qPrime++){
					clause[qPrime] = getTempYIndex(q, qPrime, d-1);
				}
				encoding.getSolver().addClause(clause);
			}
		}
	}
	
	/*
	 * Every state of the transducer is reachable from the initial state.
	 */
	private void encodingForwardReachability() throws ContradictionException{
		//condition 1
		for(int q = 1; q <= numStates; q++){
			int[] clause = new int[numStates];
			for(int d = 1; d <= numStates; d++){
				clause[d-1] = getYPrimeIndex(q, d);
			}
			encoding.getSolver().addClause(clause);
		}
		
		//condition 2
		for(int q = 2; q <= numStates; q++){
			int[] clause = new int[1];
			clause[0] = -getYPrimeIndex(q, 1);
			
			encoding.getSolver().addClause(clause);
		}
		
		//condition 3
		for(int qPrime = 1; qPrime <= numStates; qPrime++){
			for(int d = 2; d <= numStates; d++){
				//y'(q',d) => tempY(q, q', d-1) for all q
				int[] clause = new int[numStates+1];
				clause[0] = -getYPrimeIndex(qPrime, d);
				for(int q = 1; q <= numStates; q++){
					clause[q] = getTempYPrimeIndex(q, qPrime, d-1);
				}
				encoding.getSolver().addClause(clause);
			}
		}
	}
	
	private void addTempYCondition() throws ContradictionException{
		//tempY(q,q',d) = transition(q,q') and y(q', d)
		for(int q = 1; q <=numStates; q++){
			for(int qPrime = 1; qPrime <= numStates; qPrime++){
				for(int d = 1; d <= numStates - 1; d++){
					int tempY = getTempYIndex(q, qPrime, d);
					int y = getYIndex(qPrime, d);
					List<Integer> transitionsFromQToQPrime = encoding.getTransitions(q, qPrime);
					
					//tempY => transition
					//tempY -> y
					int[] clause = new int[1 + numLetters * numLetters];
					clause[0] = -tempY;
					
					for(int i = 0; i < transitionsFromQToQPrime.size(); i++){
						clause[i+1] = transitionsFromQToQPrime.get(i);
					}
					encoding.getSolver().addClause(clause);
					
					clause = new int[2];
					clause[0] = -tempY;
					clause[1] = y;
					encoding.getSolver().addClause(clause);
					
					//transition and y => tempY
					//(trans1 or trans2... or transN) and y => tempY
					//(not trans1 and not trans2... and not transN) or not y or tempy
					for(int i = 0; i < transitionsFromQToQPrime.size(); i++){
						int[] innerClause = new int[3];
						innerClause[0] = -transitionsFromQToQPrime.get(i);
						innerClause[1] = -y;
						innerClause[2] = tempY;
						encoding.getSolver().addClause(innerClause);
					}
				}
			}
		}
	}
	
	private void addTempYPrimeCondition() throws ContradictionException{
		//tempYPrime(q,q',d) = transition(q,q') and yPrime(q, d)
		for(int q = 1; q <=numStates; q++){
			for(int qPrime = 1; qPrime <= numStates; qPrime++){
				for(int d = 1; d <= numStates - 1; d++){
					int tempYPrime = getTempYPrimeIndex(q, qPrime, d);
					int yPrime = getYPrimeIndex(q, d);
					List<Integer> transitionsFromQToQPrime = encoding.getTransitions(q, qPrime); 
					
					//tempYPrime => transition
					//tempYPrime -> yPrime
					int[] clause = new int[1 + numLetters * numLetters];
					clause[0] = -tempYPrime;
					
					for(int i = 0; i < transitionsFromQToQPrime.size(); i++){
						clause[i+1] = transitionsFromQToQPrime.get(i);
					}
					encoding.getSolver().addClause(clause);
						
					
					clause = new int[2];
					clause[0] = -tempYPrime;
					clause[1] = yPrime;
					encoding.getSolver().addClause(clause);
					
					//transition and yPrime => tempYPrime
					//(trans1 or trans2... or transN) and yPrime => tempYPrime
					//(not trans1 and not trans2... and not transN) or not yPrime or tempYPrime
					for(int i = 0; i < transitionsFromQToQPrime.size(); i++){
						int[] innerClause = new int[3];
						innerClause[0] = -transitionsFromQToQPrime.get(i);
						innerClause[1] = -yPrime;
						innerClause[2] = tempYPrime;
						encoding.getSolver().addClause(innerClause);
					}
				}
			}
		}
	}
	
	private int startIndexOfYVars = 1;
	private int startIndexOfTempY = 1;
	private int startIndexOfYPrimeVars = 1;
	private int startIndexOfTempYPrime = 1;
	
	private int getYIndex(int q, int d){
		q--;
		d--;
		return startIndexOfYVars + (q * numStates + d);
	}
	
	private int getYPrimeIndex(int q, int d){
		q--;
		d--;
		return startIndexOfYPrimeVars + (q * numStates + d);
	}
	
	private int getTempYIndex(int q, int qPrime, int d){
		q--;
		qPrime--;
		d--;
		return startIndexOfTempY + (d * numStates* numStates + q * numStates + qPrime);
	}
	
	private int getTempYPrimeIndex(int q, int qPrime, int d){
		q--;
		qPrime--;
		d--;
		return startIndexOfTempYPrime + (d * numStates* numStates + q * numStates + qPrime);
	}
	
}
