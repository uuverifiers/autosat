package common.finiteautomata;

import java.util.Set;

public class DotGraphiczUltility {
		
	public static String write(Automata automata){
		final String NEW_LINE = "\n";
		final String SPACE = " ";
		StringBuilder result = new StringBuilder();
		
		result.append("digraph finite_state_machine {");
		result.append(NEW_LINE);
		result.append("rankdir=LR;");
		result.append(NEW_LINE);
		result.append("size=\"8,5\"");
		result.append(NEW_LINE);
		result.append("node [shape = doublecircle]; ");
		
		for(int accepting: automata.getAcceptingStates()){
			result.append(accepting);
			result.append(SPACE);
		}
		result.append(";");
		result.append(NEW_LINE);
		result.append("node [shape = circle];");
		result.append(NEW_LINE);
		
		for(State state: automata.getStates()){
			for(int i = Automata.EPSILON_LABEL; i < automata.getNumLabels(); i++){
				String label = (i == Automata.EPSILON_LABEL)? "": String.valueOf(i);
				Set<Integer> nexts = state.getDest(i);
				for(Integer next: nexts){
					result.append(state.getId() + " -> " + next + " [ label = \"" + label + "\" ];");
					result.append(NEW_LINE);
				}
			}
		}
		
		result.append("}");
		
		return result.toString();
	}
}
