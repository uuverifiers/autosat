package common.finiteautomata.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import common.finiteautomata.Automata;
import common.finiteautomata.State;

public class InclusionCheckingImpl implements InclusionChecking {

	/**
	 * Check whether the language of automata1 is a subset of the language of automata2
	 */
	public boolean isSubSet(Automata automata1, Automata completeDFA2) {
		// get accepting states
		Set<Integer> acceptingStates1 = automata1.getAcceptingStates();
		Set<Integer> acceptingStates2 = completeDFA2.getAcceptingStates();

		int NUM_STATE2 = completeDFA2.getStates().length;

		
		// store nodes waiting to visit
		Stack<Integer> working1 = new Stack<Integer>();
		Stack<Integer> working2 = new Stack<Integer>();
		Set<Integer> initClosure1 = automata1.getEpsilonClosure(automata1.getInitState());
		for(int init1: initClosure1){
			working1.push(init1);
			working2.push(completeDFA2.getInitState());
		}

		// check whether a node is visited or not
		boolean[] isVisited = new boolean[automata1.getStates().length
				* NUM_STATE2];
		int hashInit = hash(automata1.getInitState(), completeDFA2.getInitState(),
				NUM_STATE2);
		isVisited[hashInit] = true;
		while (!working2.isEmpty()) {
			int currentState1 = working1.pop();
			int currentState2 = working2.pop();

			// check acceptance condition, reach a state whether word is
			// accepted by automata1, but not automata2
			if (acceptingStates1.contains(currentState1)
					&& !acceptingStates2.contains(currentState2)) {
				return false;
			}

			State state1 = automata1.getStates()[currentState1];
			State state2 = completeDFA2.getStates()[currentState2];
			for(int nextLabel = 0; nextLabel < automata1.getNumLabels(); nextLabel++){
				Set<Integer> dests1 = automata1.getEpsilonClosure(state1.getDest(nextLabel));
				Set<Integer> dests2 = state2.getDest(nextLabel);

				for (int dest1 : dests1) {
					for (int dest2 : dests2) {
						int hashValue = hash(dest1, dest2, NUM_STATE2);
						if (!isVisited[hashValue]) {
							isVisited[hashValue] = true;

							//
							working1.push(dest1);
							working2.push(dest2);
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Check whether the language of automata1 is a subset of the language of
	 * automata2
	 * 
	 * @return if not subset, return the word accepted by automata1, but not
	 *         automata2
	 */
	public List<Integer> findCounterExample(Automata automata1, Automata completeDFA2) {
		// get accepting states
		Set<Integer> acceptingStates1 = automata1.getAcceptingStates();
		Set<Integer> acceptingStates2 = completeDFA2.getAcceptingStates();

		int NUM_STATE2 = completeDFA2.getStates().length;

		// store the path from root to current Node
		List<Integer> path = new ArrayList<Integer>();
		// for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();

		
		
		
		// store nodes waiting to visit
		Stack<Integer> working1 = new Stack<Integer>();
		Stack<Integer> working2 = new Stack<Integer>();
		Stack<Integer> labels = new Stack<Integer>();
		int DUMMY_LABEL = -1;

		// for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		
		Set<Integer> initClosure1 = automata1.getEpsilonClosure(automata1.getInitState());
		for(int init1: initClosure1){
			working1.push(init1);
			working2.push(completeDFA2.getInitState());
			labels.push(DUMMY_LABEL);
			depthStack.push(0);
		}

		// check whether a node is visited or not
		boolean[] isVisited = new boolean[automata1.getStates().length
				* NUM_STATE2];
		int hashInit = hash(automata1.getInitState(), completeDFA2.getInitState(),
				NUM_STATE2);
		isVisited[hashInit] = true;
		while (!working2.isEmpty()) {
			int currentState1 = working1.pop();
			int currentState2 = working2.pop();
			int label = labels.pop();
			int depthLevel = depthStack.pop();

			while (depthList.size() > 0) {
				int lastDepth = depthList.get(depthList.size() - 1);
				if (lastDepth >= depthLevel) {
					// back track a new node, remove nodes not in the path to
					// this node (having depth level greater than or equal its
					// depth level
					depthList.remove(depthList.size() - 1);
					path.remove(path.size() - 1);
				} else {
					break;
				}
			}

			// add this node and its depth level
			path.add(label);
			depthList.add(depthLevel);

			// check acceptance condition, reach a state whether word is
			// accepted by automata1, but not automata2
			if (acceptingStates1.contains(currentState1)
					&& !acceptingStates2.contains(currentState2)) {
				path.remove(0);
				return path;
			}

			State state1 = automata1.getStates()[currentState1];
			State state2 = completeDFA2.getStates()[currentState2];
			for(int nextLabel = 0; nextLabel < automata1.getNumLabels(); nextLabel++){
				Set<Integer> dests1 = automata1.getEpsilonClosure(state1.getDest(nextLabel));
				Set<Integer> dests2 = state2.getDest(nextLabel);

				for (int dest1 : dests1) {
					for (int dest2 : dests2) {
						int hashValue = hash(dest1, dest2, NUM_STATE2);
						if (!isVisited[hashValue]) {
							isVisited[hashValue] = true;

							//
							working1.push(dest1);
							working2.push(dest2);
							labels.push(nextLabel);
							depthStack.push(depthLevel + 1);
						}
					}
				}
			}
		}

		return null;
	}

	public List<Integer> findShortestCounterExample(Automata automata1, Automata completeDFA2){
		// get accepting states
		Set<Integer> acceptingStates1 = automata1.getAcceptingStates();
		Set<Integer> acceptingStates2 = completeDFA2.getAcceptingStates();

		int NUM_STATE2 = completeDFA2.getStates().length;
		
		// store nodes waiting to visit
		List<Integer> working1 = new Stack<Integer>();
		List<Integer> working2 = new Stack<Integer>();
		
        //for each state, store the path from root to it
        List<List<Integer>> paths = new ArrayList<List<Integer>>();
        
		Set<Integer> initClosure1 = automata1.getEpsilonClosure(automata1.getInitState());
		for(int init1: initClosure1){
			working1.add(init1);
			working2.add(completeDFA2.getInitState());
			 //add path to init
	        paths.add(new ArrayList<Integer>());
		}
		
		// check whether a node is visited or not
		boolean[] isVisited = new boolean[automata1.getStates().length * NUM_STATE2];
		int hashInit = hash(automata1.getInitState(), completeDFA2.getInitState(), NUM_STATE2);
		isVisited[hashInit] = true;
		while (!working2.isEmpty()) {
        	int currentState1 = working1.remove(0);
			int currentState2 = working2.remove(0);
            List<Integer> currentPath = paths.remove(0);
            
			// check acceptance condition, reach a state whether word is
			// accepted by automata1, but not automata2
			if (acceptingStates1.contains(currentState1)
					&& !acceptingStates2.contains(currentState2)) {
				return WordUtils.removeEmptyLabel(currentPath);
			}
            
			State state1 = automata1.getStates()[currentState1];
			State state2 = completeDFA2.getStates()[currentState2];
			for(int nextLabel = 0; nextLabel < automata1.getNumLabels(); nextLabel++){
				Set<Integer> dests1 = automata1.getEpsilonClosure(state1.getDest(nextLabel));
				Set<Integer> dests2 = state2.getDest(nextLabel);

				for (int dest1 : dests1) {
					for (int dest2 : dests2) {
						int hashValue = hash(dest1, dest2, NUM_STATE2);
						if (!isVisited[hashValue]) {
							isVisited[hashValue] = true;

							//
							working1.add(dest1);
							working2.add(dest2);
							List<Integer> pathToChild = new ArrayList<Integer>(currentPath);
							pathToChild.add(nextLabel);
		            		paths.add(pathToChild);
						}
					}
				}
			}
        }


        return null;
	}

	private int hash(int state1, int state2, int NUM_STATE2) {
		return state2 + state1 * NUM_STATE2;
	}
}
