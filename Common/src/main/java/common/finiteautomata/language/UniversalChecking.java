package common.finiteautomata.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import common.finiteautomata.Automata;

public class UniversalChecking {

	/**
	 * Check whether dfa is universal
	 * @param dfa It must be a DFA
	 */
	public static boolean isUniversal(Automata dfa){
		Set<Integer> acceptingStates = dfa.getAcceptingStates();
		
		//check init is accepted
		if(!acceptingStates.contains(dfa.getInitState())){
			return false;
		}
		
		//store nodes waiting to visit
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.push(dfa.getInitState());
		
		//check whether a node is visited or not
		boolean [] isVisited = new boolean[dfa.getStates().length];
		isVisited[dfa.getInitState()] = true;
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			
			for(int i = 0; i < dfa.getNumLabels(); i++){
				Set<Integer> dests = dfa.getStates()[currentState].getDest(i);
				//since dfa, dests has at most 1 state
				dests.retainAll(acceptingStates);
				
				//if dfa does not accept the label i, it is not universal
				if(dests.isEmpty()){
					return false;
				}
				else{
					for(int dest: dests){
						if(!isVisited[dest]){
							workingStates.push(dest);
							
							isVisited[dest] = true;
						}
					}
				}
			}
		}
		

		return true;
	}
	
	/**
	 * Return shortest word not accepted by this dfa
	 * Return null if not exists
	 */
	public static List<Integer> findShortestUnacceptingWords(Automata dfa) {
		Set<Integer> acceptingStates = dfa.getAcceptingStates();
		
		//check init is accepted
		if(!acceptingStates.contains(dfa.getInitState())){
			return new ArrayList<Integer>();
		}
		
		//all waiting states
        List<Integer> working = new ArrayList<Integer>();
        //add init
        working.add(dfa.getInitState());
        
        //for each state, store the path from root to it
        List<List<Integer>> paths = new ArrayList<List<Integer>>();
        //add path to init
        paths.add(new ArrayList<Integer>());
                
		// check whether a node is visited or not
		boolean [] isVisited = new boolean[dfa.getStates().length];
  		isVisited[dfa.getInitState()] = true;
        while (working.size() > 0)
        {
            int currentState = working.remove(0);
            List<Integer> currentPath = paths.remove(0);
            
            for (int i = 0; i < dfa.getNumLabels(); i++) {
				Set<Integer> dests = dfa.getStates()[currentState].getDest(i);
				//since dfa, dests has at most 1 state
				dests.retainAll(acceptingStates);

				// if dfa does not accept the label i, it is not universal
				if (dests.isEmpty()) {
					List<Integer> inputMustAccept = new ArrayList<Integer>(currentPath);
					inputMustAccept.add(i);
					
					return inputMustAccept;
				} else {
					for(int dest: dests){
						if(!isVisited[dest]){
							working.add(dest);

							List<Integer> pathToChild = new ArrayList<Integer>(currentPath);
							pathToChild.add(i);
		            		paths.add(pathToChild);
							
							isVisited[dest] = true;
						}
					}
				}
			}
        }


        return null;
	}
	
	/**
	 * Return word not accepted by this dfa
	 * Return null if not exists
	 */
	public static List<Integer> findUnacceptingWord(Automata dfa){
		Set<Integer> acceptingStates = dfa.getAcceptingStates();
		
		//check init is accepted
		if(!acceptingStates.contains(dfa.getInitState())){
			return new ArrayList<Integer>();
		}
				
		//store the path from root to current Node
		List<Integer> path = new ArrayList<Integer>();
		//for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();

		//store nodes waiting to visit
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.push(dfa.getInitState());
		
		Stack<Integer> labels = new Stack<Integer>();
		int INIT_LABEL = -1;
		labels.push(INIT_LABEL);
		
		//for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.push(0);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[dfa.getStates().length];
		isVisited[dfa.getInitState()] = true;
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			int label = labels.pop();
			int depthLevel = depthStack.pop();

			while(depthList.size() > 0){
					int lastDepth = depthList.get(depthList.size() - 1);
					if(lastDepth >= depthLevel){
						//back track a new node, remove nodes not in the path to this node (having depth level greater than or equal its depth level
						depthList.remove(depthList.size() - 1);
						path.remove(path.size() - 1);
					}
					else{
						break;
					}
			}
			
			//add this node and its depth level
			path.add(label);
			depthList.add(depthLevel);

			for(int i = 0; i < dfa.getNumLabels(); i++){
				Set<Integer> dests = dfa.getStates()[currentState].getDest(i);
				//since dfa, dests has at most 1 state
				dests.retainAll(acceptingStates);
				
				//if dfa does not accept the label i, it is not universal
				if(dests.isEmpty()){
					path.add(i);
					path.remove(0);
					return path;
				}
				else{
					for(int dest: dests){
						if(!isVisited[dest]){
							workingStates.push(dest);
							labels.push(i);
							depthStack.push(depthLevel + 1);
							
							isVisited[dest] = true;
						}
					}
				}
			}
		}
		

		return null;
	}
}
