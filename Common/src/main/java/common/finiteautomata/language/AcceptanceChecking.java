package common.finiteautomata.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import common.finiteautomata.Automata;

public class AcceptanceChecking {

	public static boolean acceptsWord(Automata automata, int[] word) {
		List<Integer> wordAsList = new ArrayList<Integer>();
		for(int label: word){
			wordAsList.add(label);
		}
		return acceptsWord(automata, wordAsList);
	}
	/**
	 * return an accepted word by this automata
	 * 
	 * @return the word which is accepted by this automata
	 */
	public static boolean acceptsWord(Automata automata, List<Integer> word) {
		Set<Integer> acceptingStates = automata.getAcceptingStates();

		Set<Integer> initSet = automata.getEpsilonClosure(automata.getInitState());

		// store nodes waiting to visit
		Stack<Integer> workingStates = new Stack<Integer>();

		// for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();

		for (int initState : initSet) {
			workingStates.push(initState);
			depthStack.push(0);
		}

		while (!workingStates.isEmpty()) {
			int currentState = workingStates.pop();
			int depthLevel = depthStack.pop();

			if (depthLevel == word.size()
					&& acceptingStates.contains(currentState)) {
				return true;
			}

			//
			if (depthLevel < word.size()) {
				Set<Integer> dests = automata.getStates()[currentState]
						.getDest(word.get(depthLevel));
				dests = automata.getEpsilonClosure(dests);
				for (int dest : dests) {
					workingStates.push(dest);
					depthStack.push(depthLevel + 1);
				}
			}
		}

		return false;
	}
}
