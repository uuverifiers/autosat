package symmetryencoding.parasym.condition;

import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;

import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.language.UniversalChecking;
import symmetryencoding.cegeneration.UpdateFromCounterExample;
import callback.Listener;

public class OutputUniversal extends NFABuilderFromModel implements ConditionChecking{
	
	private Listener listener;
	
	public OutputUniversal(Listener listener) {
		this.listener = listener;
	}
	
	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		List<Integer> w = findUnacceptingOutput(model, numStates, numLetters, acceptingStates);
		if(w != null){
			updateSAT.acceptOutput(w);
		}
		
		return (w == null);
	}
	
	private List<Integer> findUnacceptingOutput(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates){
		Automata outputNFA = generateNFA(model, numStates, numLetters, acceptingStates, false);

		if(!outputNFA.isDFA()){
			outputNFA = AutomataConverter.toDFA(outputNFA);
		}
		
		List<Integer> result =  UniversalChecking.findShortestUnacceptingWords(outputNFA);
		
		//
		listener.inform("Must Accept Output");
		listener.inform(String.valueOf(result));
		
		return result;
	}
}
