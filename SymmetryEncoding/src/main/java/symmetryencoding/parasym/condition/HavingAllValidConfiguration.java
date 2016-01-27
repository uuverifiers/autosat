package symmetryencoding.parasym.condition;

import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.language.InclusionCheckingImpl;
import symmetryencoding.cegeneration.UpdateFromCounterExample;
import callback.Listener;


public class HavingAllValidConfiguration extends NFABuilderFromModel implements ConditionChecking{

	private Automata validConfiguration;
	private Listener listener;
	public HavingAllValidConfiguration(Automata validConfiguration, Listener listener){
		this.validConfiguration = validConfiguration;
		this.listener = listener;
	}
	
	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		List<Integer> v = findUnacceptingInput(model, numStates, numLetters, acceptingStates);
		if(v != null){
			updateSAT.acceptInput(v);
		}
		
		return (v == null);
	}
	
	private List<Integer> findUnacceptingInput(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates){
		Automata inputNFA = generateNFA(model, numStates, numLetters, acceptingStates, true);
		
		if(!inputNFA.isDFA()){
			inputNFA = AutomataConverter.toDFA(inputNFA);
		}
		if(!inputNFA.isCompleteDFA()){
			inputNFA = AutomataConverter.toCompleteDFA(inputNFA);
		}
		
		List<Integer> unacceptingInput = new InclusionCheckingImpl().findShortestCounterExample(validConfiguration, inputNFA);
		
		//
		listener.inform("Must Accept Input");
		listener.inform(String.valueOf(unacceptingInput));
		
		return unacceptingInput;
	}
}