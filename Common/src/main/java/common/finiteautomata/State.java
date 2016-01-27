package common.finiteautomata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State {
	private int id;
	
	/*
	 * Map label to target states
	 */
    private Map<Integer, Set<Integer>> outgoingTrans;
    private static final Set<Integer> emptySet =
	new HashSet<Integer>();

	public State(int id){
		this.id = id;
		this.outgoingTrans = new HashMap<Integer, Set<Integer>>();
	}

	public void addTrans(int label, int dest){
		Set<Integer> destSet = outgoingTrans.get(label);
		if(destSet == null){
			destSet = new HashSet<Integer>();
			outgoingTrans.put(label, destSet);
		}
		
		destSet.add(dest);
	}
	
	public Set<Integer> getDest(int label){
		Set<Integer> dest = outgoingTrans.get(label);
		if (dest != null)
		    return dest;
		return emptySet;
	}
	
	public Set<Integer> getDest(){
		Set<Integer> dest = new HashSet<Integer>();
		for(Set<Integer> destOnLabel: outgoingTrans.values()){
			dest.addAll(destOnLabel);
		}
		
		return dest;
	}

	public int getId() {
		return id;
	}

	
	public Set<Integer> getOutgoingLabels(){
		return outgoingTrans.keySet();
	}
}
