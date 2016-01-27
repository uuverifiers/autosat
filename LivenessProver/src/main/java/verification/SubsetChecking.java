package verification;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;
import common.finiteautomata.language.InclusionCheckingImpl;

public class SubsetChecking {
	private static final Logger LOGGER = LogManager.getLogger();

	private Automata I0;
	private Automata B;
	
	/*
	 * Make sure that I0, label starting from 1
	 */
	public SubsetChecking(Automata I0, Automata B){
		this.I0 = I0;
		this.B = B;
	}
	
	public List<Integer> check(){
		if(!B.isCompleteDFA()){
			B = AutomataConverter.toCompleteDFA(B);
		}
		
		List<Integer> unacceptingInput = new InclusionCheckingImpl().findShortestCounterExample(I0, B);
		return unacceptingInput;
	}
}
