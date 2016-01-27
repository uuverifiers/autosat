package symmetryencoding.encoding;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import symmetryencoding.Configuration;
import symmetryencoding.SymmetryProb;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;
import symmetryencoding.parasym.ParaSymChecking;
import callback.Listener;


/**
 * Note that in this class, states are counted from 1, letters are counted from 0
 * @author khanh
 *
 */
public class TransducerEncoding {
	
	final int NUM_SOLUTION = 1000000;
	final int MAXVAR = 1000000;
	final int NBCLAUSES = 1000000;
	
	
	private ISolver solver;
	private int assertedClauses = 0;
	private Map<Integer, Map<Integer, List<Integer>>> mapStates2Transitions = new HashMap<Integer, Map<Integer,List<Integer>>>();
	private List<int[]> groupTransitionsBySourceInputOutput = new ArrayList<int[]>();
	private int nextSATVar = 1;
	private int startIndexOfZVars = 1;
	private int numStates;
	private int numLetters;
	private EdgeWeightedDigraph automorphismGraph;
	private Automata validConfiguration;
	private List<Integer> wordLengths = new ArrayList<Integer>();
	private List<List<int[]>> symmetryGenerators = new ArrayList<List<int[]>>();
	private List<List<Integer>> finiteSymmetryInputs;
	private List<List<Integer>> finiteSymmetryOutputs;
	private boolean[] finiteOutputs;
	private boolean[] possibleTransitionLetters;
	
	private Map<String, Integer> transducerLabelToIndex = new HashMap<String, Integer>();

	private Listener listener;

	public void setListener(final Listener listener) {
		this.listener = listener;
	}

	public TransducerEncoding(int statesCount, SymmetryProb symmetryProblem)
	{
		this.numStates = statesCount;
		this.numLetters = symmetryProblem.getNumberOfLetters();
		this.automorphismGraph = symmetryProblem.getGraphToCheck();
		this.wordLengths = symmetryProblem.getWordLengths();
		this.symmetryGenerators = symmetryProblem.getSymmetryGenerators();
		this.finiteSymmetryInputs = symmetryProblem.getFiniteSymmetryInputs();
		this.finiteSymmetryOutputs = symmetryProblem.getFiniteSymmetryOutputs();
		this.validConfiguration = symmetryProblem.getValidConfiguration();
		this.transducerLabelToIndex = symmetryProblem.getTransducerLabelToIndex();
		
		//
		this.finiteOutputs = new boolean[numLetters];
		for(int i = 0; i < finiteOutputs.length; i++){
			this.finiteOutputs[i] = false;
		}
		for(int finiteOutput: symmetryProblem.getFiniteOutputs()){
			this.finiteOutputs[finiteOutput] = true;
		}
		
		//
		possibleTransitionLetters = new boolean[numLetters*numLetters];
		for(int i = 0; i < possibleTransitionLetters.length; i++){
			possibleTransitionLetters[i] = true;
		}
		for(int[] pair: symmetryProblem.getImpossiblePair()){
			int index = pair[0] * numLetters + pair[1];
			possibleTransitionLetters[index] = false;
		}
				
		solver = SolverFactory.newDefault();
//		solver = SolverFactory.newGlucose();
		
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
		
	}

	public void addClause(int[] clause) throws ContradictionException{
		solver.addClause(new VecInt(clause));
        assertedClauses += 1;
	}
	
	public boolean guessingTransducer(final Writer outputWriter) {
		try{
			return encodeAndGuess(outputWriter);
		}
		catch(ContradictionException e){
			listener.inform("Unsatisfiable(trivial)!");
		}
		catch(TimeoutException e){
			listener.inform("time out");
		}
		
		return false;
	}

	private boolean encodeAndGuess(final Writer outputWriter) throws ContradictionException,
			TimeoutException {
		encodeC123();

		InitAcceptingReachableEncoding c4Encoding = new InitAcceptingReachableEncoding(this);
		c4Encoding.encode();
		
		encodeC5();
		
		ParikhSymmetryEncoding parikhSymmetry = new ParikhSymmetryEncoding(this);
		parikhSymmetry.encode();
		
/*		
		filterTransitions(new boolean[] {
               // out:  0      1      2      3            in:
			true,  true,  false, false,    // 0
			true,  true,  false, false,    // 1
			false, false, true,  true,     // 2
			false, false, true,  true      // 3
		    });
*/

		filterTransitions(possibleTransitionLetters);
		
		//guessing
		ParaSymChecking checking = new ParaSymChecking(this);
		checking.setListener(this.listener);
		return checking.check(outputWriter);
	}

	private void encodeC123() throws ContradictionException {
		//C3
		//there are numStates^2 * numLetters * (numLetters - 1) satisfies the C3
		int numC3Transitions = numStates * numStates * numLetters * (numLetters - 1);
		int[] c3Transitions = new int[numC3Transitions];
		int countC3Transitions = 0;
		
		//states start from 1
		for(int source = 1; source <= numStates; source++){
			Map<Integer, List<Integer>> mapDestination2Transitions = new HashMap<Integer, List<Integer>>();
			
			//given input, output, index = input * numLetters + output
			//transByInputOutput[index] contains all transition with current source state, input and output
			//transByInputOutput[index][destination-1] is the transition with source, input, output, destination
			int[][]transByInputOutput = new int[numLetters*numLetters][numStates];
			
			for(int destination = 1; destination <= numStates; destination++){
				//
				int startTransition = nextSATVar;
				
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						int transitionIndex = nextSATVar;
						if(input != output){
							c3Transitions[countC3Transitions] = transitionIndex;
							countC3Transitions++;
						}
						nextSATVar++;
						
						//
						int index = input *numLetters + output;
						transByInputOutput[index][destination-1] = transitionIndex;
					}
				}
				
				//build mapping souce, destination to transitions, there are numLetters^2 transitions
				List<Integer> transitionsBySourceDestination = new ArrayList<Integer>();
				for(int i = 0; i < numLetters * numLetters; i++){
					transitionsBySourceDestination.add(i + startTransition);
				}
				mapDestination2Transitions.put(destination, transitionsBySourceDestination);
				
				encodeC1C2(startTransition);
			}
			
			//done a source state
			this.mapStates2Transitions.put(source, mapDestination2Transitions);
			
			//separate transitions by source, input, output
			for(int i = 0; i < transByInputOutput.length; i++){
				this.groupTransitionsBySourceInputOutput.add(transByInputOutput[i]);
			}
		}
		
		//apply C3
		//there exists at least one C3 transition
		addClause(c3Transitions);
		
		//add variable z_1..zn for accepting state. z_i if state i is an accepting state. Note that z_i starting at 1
		startIndexOfZVars = nextSATVar;
		nextSATVar += numStates;
		

		
		//the initial state has to be accepting
		addClause(new int[] { getIndexZVar(1) });

		//at least one final state
		int[] hasOneFinal = new int[numStates];
		for(int i = 1; i <= numStates; i++){
			hasOneFinal[i-1] = getIndexZVar(i);
		}
		addClause(hasOneFinal);
	}

	private void encodeC1C2(int startTransition) throws ContradictionException {
		//add clause for rule C1, C2
		for(int input = 0; input < numLetters; input++){
			//fix the input
			
			if(!Configuration.offConditions.contains(Condition.InjectiveInput)){
				encodeC1(startTransition, input);
			}
			
			if(!Configuration.offConditions.contains(Condition.InjectiveOutput)){
				encodeC2(startTransition, input);
			}
		}
	}

	private void encodeC2(int startTransition, int input)
			throws ContradictionException {
		//C2
		//0 n 2n (n-1)*n
		//1 n+1 2n+1 (n-1)*n+1
		//2 n+2 2n+2 (n-1)*n+2
		for(int i = input ; i <= numLetters * (numLetters - 1) + input; i = i + numLetters){
			for(int j = i + numLetters ; j <= numLetters * (numLetters - 1) + input; j = j + numLetters){
				if(i != j){
					//(i+startTransition), (j+startTransition) are 2 transitions with the same output
					//Either one of them must be false (they can not exist together)
					int[] clause = new int[]{-(i + startTransition), -(j + startTransition)};
					addClause(clause);
				}
			}
		}
	}

	private void encodeC1(int startTransition, int input)
			throws ContradictionException {
		//C1, n is numLetters
		//0.. n-1
		//n.. 2n - 1
		//...
		for(int i = input * numLetters; i < (input + 1) * numLetters; i++){
			for(int j = i + 1; j < (input + 1) * numLetters; j++){
				if(i != j){
					//(i+startTransition), (j+startTransition) are 2 transitions with the same input
					//Either one of them must be false (they can not exist together)
					int[] clause = new int[]{-(i + startTransition), -(j + startTransition)};
					addClause(clause);
				}
			}
		}
	}

	
	
	private void encodeC5() throws ContradictionException {
		//c5 condition
		for(int[] transSameSourceInputOutput: this.groupTransitionsBySourceInputOutput){
			
			for(int i = 0; i < transSameSourceInputOutput.length; i++){
				for(int j = i + 1; j < transSameSourceInputOutput.length; j++){
					int[] notBothTransition = new int[]{-transSameSourceInputOutput[i], - transSameSourceInputOutput[j]};
					addClause(notBothTransition);
				}
			}
		}
	}

    /**
     * Only allow certain combinations of input/output labels on transitions
     */
    private void filterTransitions(boolean[] possibleTransitionLetters)
                                    throws ContradictionException {
	for(int source = 1; source <= numStates; source++){
	    for(int target = 1; target <= numStates; target++){
		for (int input = 0; input < numLetters; ++input) {
		    for (int output = 0; output < numLetters; ++output) {
			if (!possibleTransitionLetters[input * numLetters + output])
			    addClause(new int[] {
				    -getTransBoolVar(source, input, output, target) });
		    }
		}
	    }
	}
    }

	/**
	 * Extract acceptantance states from SAT
	 * States are counted from 1
	 * @param model
	 * @return
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
	
	/*
	 * q start from 1
	 */
	public int getIndexZVar(int q){
		return this.startIndexOfZVars + q - 1;
	}
	
	public int getTransBoolVar(int source, int input, int output, int dest){
		source--;
		dest--;
		//transition boolean vars starting at 1
		return 1 + output + numLetters * (input + numLetters * (dest + numStates * source));
	}
	
	public List<Integer> getTransitions(int source, int destination){
		return mapStates2Transitions.get(source).get(destination);
	}
	
	public ISolver getSolver() {
		return solver;
	}
	
	public int getAssertedClauses() {
		return assertedClauses;
	}
	
	public void setNextSATVar(int nextSATVar) {
		this.nextSATVar = nextSATVar;
	}

	public int getNextSATVar() {
		return nextSATVar;
	}
	
	public int getNumStates() {
		return numStates;
	}
	
	public int getNumLetters() {
		return numLetters;
	}
	
	public Automata getValidConfiguration() {
		return validConfiguration;
	}

	public EdgeWeightedDigraph getAutomorphismGraph() {
		return automorphismGraph;
	}

	public List<Integer> getWordLengths() {
		return wordLengths;
	}

	public List<List<int[]>> getSymmetryGenerators() {
		return symmetryGenerators;
	}
	
	public List<List<Integer>> getFiniteSymmetryInputs() {
		return finiteSymmetryInputs;
	}

	public List<List<Integer>> getFiniteSymmetryOutputs() {
		return finiteSymmetryOutputs;
	}

	public boolean[] getFiniteOutputs() {
		return finiteOutputs;
	}

	public Map<String, Integer> getTransducerLabelToIndex() {
		return transducerLabelToIndex;
	}
	
	
}
