package encoding;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import elimination.AcceptanceTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class AutomataEncoding {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ISatSolver solver;
	private int startIndexOfTransVars = 1;
	private int startIndexOfZVars = 1;
	private int numStates;
	private int numLabels;

    private AcceptanceTree acceptance = null;
	
	public AutomataEncoding(ISatSolver solver, int numStates, int numLabels)
	{
		this.solver = solver;
		this.numStates = numStates;
		this.numLabels = numLabels;
		allocateBoolVars(solver);
	}
	
	private void allocateBoolVars(ISatSolver solver) {
		//vars for transition and accepting states
		startIndexOfTransVars = solver.getNextSATVar();
		startIndexOfZVars = startIndexOfTransVars + numStates * numStates * numLabels;
		solver.setNextSATVar(startIndexOfZVars + numStates);
	}

	public void encode() throws ContradictionException,
			TimeoutException {
		int indexRunner = this.startIndexOfTransVars;
		for (int source = 1; source <= numStates; source++) {
			for (int label = 0; label < numLabels; label++) {
				int[] transWithSourceAndLabel = new int[numStates];
				
				for (int destination = 1; destination <= numStates; destination++) {
					transWithSourceAndLabel[destination-1] = indexRunner;
					indexRunner++;
				}
				
				//Determinisitc: No pair of transitions same source, input
				for(int i = 0; i < transWithSourceAndLabel.length; i++){
					for(int j = i + 1; j < transWithSourceAndLabel.length; j++){
						int[] notBothTransition = new int[]{-transWithSourceAndLabel[i], - transWithSourceAndLabel[j]};
						solver.addClause(notBothTransition);
					}
				}
			}
		}

		acceptance = new AcceptanceTree(this);
	}


	/**
	 * Extract acceptantance states from SAT
	 * States are counted from 1
	 */
	public Set<Integer> extractAcceptingStates(int[] model) {
		Set<Integer> acceptingStates = new HashSet<Integer>();
		for(int q = 1; q <= numStates; q++){
			if(model[getIndexZVar(q)-1] > 0){
				acceptingStates.add(q);
			}
		}
		return acceptingStates;
	}

    public int acceptWord(List<Integer> v)
                     throws ContradictionException {
	return acceptance.insert(v);
    }

    
	/*
	 * q start from 1
	 */
	public int getIndexZVar(int q){
		return this.startIndexOfZVars + q - 1;
	}
	
	/*
	 * source, dest start from 1
	 * label start from 0
	 */
	public int getTransBoolVar(int source, int label, int dest){
		source--;
		dest--;
		
		return startIndexOfTransVars + dest + numStates * (label + numLabels * source);
	}

	public int getStartIndexOfTransVars() {
		return startIndexOfTransVars;
	}

	public int getStartIndexOfZVars() {
		return startIndexOfZVars;
	}

	public int getNumStates() {
		return numStates;
	}

	public int getNumLabels() {
		return numLabels;
	}

	public ISatSolver getSolver() {
		return solver;
	}
	
}
