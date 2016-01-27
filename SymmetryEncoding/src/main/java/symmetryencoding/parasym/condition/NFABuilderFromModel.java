package symmetryencoding.parasym.condition;

import java.util.Set;

import common.VerificationUltility;
import common.finiteautomata.Automata;

public class NFABuilderFromModel{	
	/*
	 * Generate automata from transducer configuration, using projection based on isPrintingInput whether to take input or output as new label
	 */
	protected Automata generateNFA(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, boolean isPrintingInput){
		// compute accepting state
		Set<Integer> newAccept = VerificationUltility.convertAccepting(acceptingStates);
		
		//init in transducer is 1, here based 0
		int init = 0;
		Automata automata = new Automata(init, numStates, numLetters);
		
		int run = 0;
		
		for(int source = 0; source < numStates; source++){
			for(int destination = 0; destination < numStates; destination++){
				boolean [] hasTrans = new boolean[numLetters];
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						if(model[run] > 0){
							int label = (isPrintingInput)? input: output;
							if(!hasTrans[label]){
								automata.addTrans(source, label, destination);
								hasTrans[label] = true;
							}
						}
						run++;
					}
				}
			}
		}
				
		automata.setAcceptingStates(newAccept);
		
		return automata;
	}
}
