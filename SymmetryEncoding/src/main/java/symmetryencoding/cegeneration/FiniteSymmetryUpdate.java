package symmetryencoding.cegeneration;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.encoding.TransducerEncoding;
import callback.Listener;

public class FiniteSymmetryUpdate {

	private TransducerEncoding encoding;
	private Listener listener;
	
	public FiniteSymmetryUpdate(TransducerEncoding encoding, Listener listener){
		this.encoding = encoding;
		this.listener = listener;
	}
	
	public void addConstraints(int length, List<int[]> symmetryGenerators){
		WordGenerator wordGenerator = new WordGenerator(encoding.getNumLetters(), length, length);
		List<List<Integer>> words = wordGenerator.generate();
		for(List<Integer> word: words){
			addConstraints(word, symmetryGenerators);
		}
	}
	
	private void addConstraints(List<Integer> input, List<int[]> symmetryGenerators) {
		UpdateFromCounterExample updateSAT = new UpdateFromCounterExample(encoding, listener);
		List<Integer> output = new ArrayList<Integer>(input);
		
		for(int[] generator: symmetryGenerators){
			for(int i = 0 ; i < generator.length; i++){
				int index = generator[i];
				int newIndex = generator[(i+1) % generator.length];
				output.set(newIndex, input.get(index));
			}
		}
		
		try {
			updateSAT.accept(input, output);
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}
}
