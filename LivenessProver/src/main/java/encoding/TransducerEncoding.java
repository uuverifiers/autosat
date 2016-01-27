package encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;


public class TransducerEncoding implements ITransducerEncoding {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ISatSolver solver;
	private Map<Integer, Map<Integer, List<Integer>>> mapStates2Transitions = new HashMap<Integer, Map<Integer,List<Integer>>>();
	private List<int[]> groupTransitionsBySourceInputOutput = new ArrayList<int[]>();
	private int startIndexOfTransVars = 1;
	private int startIndexOfZVars = 1;
	private int numStates;
	private int numLetters;
	
	
	public TransducerEncoding(ISatSolver solver, int statesCount, int numLetters)
	{
		this.solver = solver;
		this.numStates = statesCount;
		this.numLetters = numLetters;
		allocateBoolVars(solver);
	}


	private void allocateBoolVars(ISatSolver solver) {
		//vars for transition and accepting states
		startIndexOfTransVars = solver.getNextSATVar();
		startIndexOfZVars = startIndexOfTransVars + numStates * numStates * numLetters * numLetters;
		solver.setNextSATVar(startIndexOfZVars + numStates);
	}

	public void encode() throws ContradictionException,
			TimeoutException {
		collectTransitionIndex();
		deterministicEncoding();
		
		TransducerC2Encoding c2Encoding = new TransducerC2Encoding(this);
		c2Encoding.encode();
		
		InitAcceptingReachableEncoding c3c4Encoding = new InitAcceptingReachableEncoding(this);
		c3c4Encoding.encode();

		SymmetryBreakingEncoding symEncoding = new SymmetryBreakingEncoding(this);
		symEncoding.encode();
	
		// hat letters: 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 
   /*
                // optimisation for DPP: don't allow letters 9-16 in
                // the transducer, since those letters do not occur in
                // P1 states
                for (int q1 = 1; q1 <= numStates; ++q1)
                    for (int q2 = 1; q2 <= numStates; ++q2)
                        for (int l1 = 0; l1 < numLetters; ++l1)
                            for (int l2 = 0; l2 < numLetters; ++l2)
                                if (l1 >= 12 || l2 >= 12)
                                    solver.addClause(new int [] { -getTransBoolVar(q1, l1, l2, q2) });
   */
	}

	private void collectTransitionIndex() throws ContradictionException {
		//states start from 1
		int indexRunner = startIndexOfTransVars;
		for(int source = 1; source <= numStates; source++){
			Map<Integer, List<Integer>> mapDestination2Transitions = new HashMap<Integer, List<Integer>>();
			
			//given input, output, index = input * numLetters + output
			//transByInputOutput[index] contains all transition with current source state, input and output
			//transByInputOutput[index][destination-1] is the transition with source, input, output, destination
			int[][]transByInputOutput = new int[numLetters*numLetters][numStates];
			
			for(int destination = 1; destination <= numStates; destination++){
				//
				int startTransition = indexRunner;
				
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						int index = input *numLetters + output;
						transByInputOutput[index][destination-1] = indexRunner;
						
						indexRunner++;
					}
				}
				
				//build mapping souce, destination to transitions, there are numLetters^2 transitions
				List<Integer> transitionsBySourceDestination = new ArrayList<Integer>();
				for(int i = 0; i < numLetters * numLetters; i++){
					transitionsBySourceDestination.add(i + startTransition);
				}
				mapDestination2Transitions.put(destination, transitionsBySourceDestination);
			}
			
			//done a source state
			this.mapStates2Transitions.put(source, mapDestination2Transitions);
			
			//separate transitions by source, input, output
			for(int i = 0; i < transByInputOutput.length; i++){
				this.groupTransitionsBySourceInputOutput.add(transByInputOutput[i]);
			}
		}
	}

	/*
	 * Determinisitc: No pair of transitions same source, input, output
	 */
	private void deterministicEncoding() throws ContradictionException {
		//c5 condition
		for(int[] transSameSourceInputOutput: this.groupTransitionsBySourceInputOutput){
			
			for(int i = 0; i < transSameSourceInputOutput.length; i++){
				for(int j = i + 1; j < transSameSourceInputOutput.length; j++){
					int[] notBothTransition = new int[]{-transSameSourceInputOutput[i], - transSameSourceInputOutput[j]};
					solver.addClause(notBothTransition);
				}
			}
		}
	}

	
	/*
	 * q start from 1
	 */
	public int getIndexZVar(int q){
		return this.startIndexOfZVars + q - 1;
	}
	
	public int getTransBoolVar(int source, int input, int output, int dest){
		source--;
		dest--;
		
		return this.startIndexOfTransVars + output + numLetters * (input + numLetters * (dest + numStates * source));
	}
	
	public List<Integer> getTransitions(int source, int destination){
		return mapStates2Transitions.get(source).get(destination);
	}

	public int getNumStates() {
		return numStates;
	}

	public int getNumLetters() {
		return numLetters;
	}
	
	public int getStartIndexOfTransVars() {
		return startIndexOfTransVars;
	}

	public int getStartIndexOfZVars() {
		return startIndexOfZVars;
	}

	public ISatSolver getSolver() {
		return solver;
	}
}
