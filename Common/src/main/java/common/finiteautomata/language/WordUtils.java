package common.finiteautomata.language;

import java.util.ArrayList;
import java.util.List;

import common.finiteautomata.Automata;

public class WordUtils {

	public static List<Integer> removeEmptyLabel(List<Integer> path) {
		List<Integer> result = new ArrayList<Integer>();
		for(int label: path){
			if(label != Automata.EPSILON_LABEL){
				result.add(label);
			}
		}
		
		return result;
	}
}
