package common.finiteautomata;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * States are labeled from 0 to V-1 where V is number of states.
 * Labels are numbered from 0. Label -1 is preserved for epsilon label (empty label)
 * @author khanh
 *
 */
public class Automata {
	public static int EPSILON_LABEL = -1;
	
	private int initState;
	private State[] states;
	
	/**
	 * Number of labels in this automata. exclude -1 for epsilon label
	 */
	private int numLabels;
	
	private Set<Integer> acceptingStates;
	
	/**
	 * @param numLabels include 0 for epsilon (empty) label
	 */
	public Automata(int initState, int numStates, int numLabels){
		this.initState = initState;
		this.states = new State[numStates];
		for(int i = 0; i < numStates; i++){
			this.states[i] = new State(i);
		}
		
		this.numLabels = numLabels;
		this.acceptingStates = new HashSet<Integer>();
	}
	
	public Automata(int initState, State[] states, int numLabels){
		this.initState = initState;
		this.states = states;
		this.numLabels = numLabels;
	}
	
	public Automata(int initState, List<State> states, int numLabels){
		this.initState = initState;
		this.states = new State[states.size()];
		for(int i = 0; i < states.size(); i++){
			this.states[i] = states.get(i);
		}
		this.numLabels = numLabels;
	}
	
    public boolean hasEpsilonTransitions() {
	for (State state : states) {
	    if (state.getOutgoingLabels().contains(EPSILON_LABEL))
		return true;
	}
	return false;
    }

	/**
	 * Add transitions from source to dest with label
	 */
	public void addTrans(int source, int label, int dest){
		this.states[source].addTrans(label, dest);
	}

	/**
	 * Set accepting states
	 */
	public void setAcceptingStates(Collection<Integer> acceptingStates){
		this.acceptingStates = new HashSet<Integer>(acceptingStates);
	}
	
	/**
	 * Get set of destinations from sources by transitions with label
	 */
	public Set<Integer> getDests(Set<Integer> sources, int label){
		Set<Integer> result = new HashSet<Integer>();
		for(Integer source: sources){
			result.addAll(states[source].getDest(label));
		}
		
		return result;
	}
	
	/**
	 * Compute epsilon closure from a set of states
	 */
	public Set<Integer> getEpsilonClosure(Set<Integer> fromStates){
		Set<Integer> result = new HashSet<Integer>();
		
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.addAll(fromStates);
		
		boolean [] isVisited = new boolean[states.length];
		for(int fromState: fromStates){
			isVisited[fromState] = true;
		}
		
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			result.add(currentState);
			
			//add new states to workingState
			for(int child: states[currentState].getDest(EPSILON_LABEL)){
				if(!isVisited[child]){
					isVisited[child] = true;
					workingStates.push(child);
				}
			}
		}
		
		return result;
	}
	
	public Set<Integer> getEpsilonClosure(int fromState){
		Set<Integer> fromStates = new HashSet<Integer>();
		fromStates.add(fromState);
		
		return getEpsilonClosure(fromStates);
	}
	
	public boolean isDFA(){
		for(State state: states){
			Set<Integer> nexts = state.getDest(EPSILON_LABEL);
			if(!nexts.isEmpty()){
				return false;
			}
			
			for(int i = 0; i < numLabels; i++){
				nexts = state.getDest(i);
				if(nexts.size() > 1){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean isCompleteDFA(){
		if(!isDFA()){
			return false;
		}
		
		for(State state: states){
			Set<Integer> nextLabels = state.getOutgoingLabels();
			if(nextLabels.size() != numLabels){
				return false;
			}
		}
		
		return true;
	}

    public boolean accepts(List<Integer> word) {
	return acceptsHelp(word, getInitState(), 0);
    }
	
    private boolean acceptsHelp(List<Integer> word,
				int state,
				int index) {
	if (index == word.size()) {
	    return getAcceptingStates().contains(state);
	} else {
	    State s = getStates()[state];
	    for (int nextState : s.getDest(word.get(index)))
		if (acceptsHelp(word, nextState, index + 1))
		    return true;
	    return false;
	}
    }
	
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        for(State v: states){
        	Set<Integer> labels = v.getOutgoingLabels();
        	for(int label: labels){
        		s.append(v.getId() + " -" + label + "-> " + v.getDest(label));
        		s.append(NEWLINE);
        	}
        }
        s.append("init: 0" + NEWLINE);
        s.append("accepting: " + acceptingStates + NEWLINE);
       
        return s.toString();
    }
    
    public State[] getStates() {
		return states;
	}


	public int getNumLabels() {
		return numLabels;
	}

	public void setNumLabels(int n) {
		numLabels = n;
	}

	public Set<Integer> getAcceptingStates() {
		return acceptingStates;
	}
	
	public int getInitState() {
		return initState;
	}
}
