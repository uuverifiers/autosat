package symmetryencoding.parasym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import callback.Listener;
import symmetryencoding.parasym.condition.Automorphism;
import symmetryencoding.parasym.condition.ConditionChecking;
import symmetryencoding.parasym.condition.CopyCatGroup;
import symmetryencoding.parasym.condition.HavingAllValidConfiguration;
import symmetryencoding.parasym.condition.InjectiveInput;
import symmetryencoding.parasym.condition.InjectiveOutput;
import symmetryencoding.parasym.condition.InputUniversal;
import symmetryencoding.parasym.condition.OutputUniversal;
import symmetryencoding.parasym.condition.Permutative;



public class CheckingConditionBuilder {

	public enum Condition {
	    InputUniversal, OutputUnviersal, Copycat, InjectiveInput, InjectiveOutput,
	    Permutative, Automorphism, Parikh 
	}
	
	private Map<Condition, ConditionChecking> map2ConditionChecking = new HashMap<Condition, ConditionChecking>();

	public CheckingConditionBuilder(EdgeWeightedDigraph automorphismGraph, Automata validConfiguration, Listener listener){
		if(validConfiguration != null){
			map2ConditionChecking.put(Condition.InputUniversal, new HavingAllValidConfiguration(validConfiguration, listener));
		}
		else{
			map2ConditionChecking.put(Condition.InputUniversal, new InputUniversal(listener));
		}
		map2ConditionChecking.put(Condition.OutputUnviersal, new OutputUniversal(listener));
		map2ConditionChecking.put(Condition.Copycat, new CopyCatGroup());
		map2ConditionChecking.put(Condition.InjectiveInput, new InjectiveInput(listener));
		map2ConditionChecking.put(Condition.InjectiveOutput,  new InjectiveOutput(listener));
		map2ConditionChecking.put(Condition.Permutative, new Permutative(listener));
		map2ConditionChecking.put(Condition.Automorphism, new Automorphism(automorphismGraph, listener));
	}
	
	public List<ConditionChecking> getCheckers(Set<Condition> offConditions, Condition[] sequences){
		List<ConditionChecking> result = new ArrayList<ConditionChecking>();
		for(int i = 0; i < sequences.length; i++){
			if(!offConditions.contains(sequences[i])){
				result.add(map2ConditionChecking.get(sequences[i]));
			}
		}
		
		return result;
	}
}
