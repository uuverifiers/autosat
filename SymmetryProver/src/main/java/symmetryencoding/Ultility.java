package symmetryencoding;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import callback.Listener;


public class Ultility {
	
	public static void printModel(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, Listener listener){
		int run = 0;
		
		for(int source = 1; source <= numStates; source++){
			for(int destination = 1; destination <= numStates; destination++){
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						if(model[run] > 0){
							listener.inform(source + " " + input + "/" + output + " " + destination);
						}
						run++;
					}
				}
			}
		}
				
		listener.inform("Accepting States:");
		listener.inform(String.valueOf(acceptingStates));
		
	}
	
	
	
	public static void writeToDotFile(Writer writer, int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, Map<String, Integer> transducerLabelToIndex){
		String toDot = Ultility.toDot(model, numStates, numLetters, acceptingStates, transducerLabelToIndex);
		try {
			writer.write(toDot);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String toDot(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, Map<String, Integer> transducerLabelToIndex){
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
		for(int accepting: acceptingStates){
			result.append(accepting);
			result.append(SPACE);
		}
		result.append(";");
		result.append(NEW_LINE);
		result.append("node [shape = circle];");
		result.append(NEW_LINE);
		
		int run = 0;
		for(int source = 1; source <= numStates; source++){
			for(int destination = 1; destination <= numStates; destination++){
				for(int input = 0; input < numLetters; input++){
					for(int output = 0; output < numLetters; output++){
						if(model[run] > 0){
							String inputLabel = common.Ultility.getLabel(transducerLabelToIndex, input);
							String outputLabel = common.Ultility.getLabel(transducerLabelToIndex, output);
							result.append(source + " -> " + destination + " [ label = \"" + inputLabel + "/" + outputLabel + "\" ];");
							result.append(NEW_LINE);
						}
						run++;
					}
				}
			}
		}
		
		result.append("}");
		
		return result.toString();
	}
	

	
	public static String getOutputFileName(){
		String fileName = FilenameUtils.getBaseName(Configuration.inputFileName);
		String result = FilenameUtils.getPath(Configuration.inputFileName) +  fileName + ".dot";
		
		return result;
	}
}
