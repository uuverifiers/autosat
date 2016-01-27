package symmetryencoding.cegeneration;

import java.util.List;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.Configuration;
import symmetryencoding.encoding.TransducerEncoding;
import callback.Listener;

public class UpdateFromCounterExample {

	private TransducerEncoding encoding;
	private Listener listener;

	public UpdateFromCounterExample(TransducerEncoding encoding, Listener listener) {
		this.encoding = encoding;
		this.listener = listener;
	}
	
	public void acceptAtMostOne(List<Integer> v1, List<Integer> w1, List<Integer> v2, List<Integer> w2) throws ContradictionException{
		PairAcceptance ceg1 = new PairAcceptance(encoding);
		int acceptV1W1 = ceg1.encodeNeg(v1, w1);
		
		PairAcceptance ceg2 = new PairAcceptance(encoding);
		int acceptV2W2 = ceg2.encodeNeg(v2, w2);
		
		int [] clause = new int[2];
		clause[0] = -acceptV1W1;
		clause[1] = -acceptV2W2;
		encoding.addClause(clause);
	}
	
	public void acceptInput(List<Integer> v) throws ContradictionException{
		InputAcceptance inputAccept = new InputAcceptance(encoding);
		inputAccept.accept(v);
	}
	
	public void acceptInputs(List<List<Integer>> inputWords) throws ContradictionException{
		for(List<Integer> word: inputWords){
			acceptInput(word);
		}
	}
	
	public void acceptOutput(List<Integer> w) throws ContradictionException{
		OutputAcceptance outputAccept = new OutputAcceptance(encoding);
		outputAccept.accept(w);
	}
	
	public void acceptOutputs(List<List<Integer>> outputWords) throws ContradictionException{
		for(List<Integer> word: outputWords){
			acceptOutput(word);
		}
	}
	
	public void acceptSomeShortInputOutputWord() throws ContradictionException{
		WordGenerator inputOutputWord = new WordGenerator(encoding.getNumLetters(), 1, Configuration.MAX_LENGTH_TO_ACCEPT);
		List<List<Integer>> words = inputOutputWord.generate();
		
		for(List<Integer> word: words){
			acceptInput(word);
			acceptOutput(word);
		}
	}
	
	public void notAccept(List<Integer> v, List<Integer> w) throws ContradictionException{
		PairAcceptance ceg = new PairAcceptance(encoding);
		int acceptVW = ceg.encodeNeg(v, w);
		
		int [] clause = new int[]{-acceptVW};
		encoding.addClause(clause);
	}
	
	public void accept(List<Integer> v, List<Integer> w) throws ContradictionException{
		PairAcceptance ceg = new PairAcceptance(encoding);
		int acceptVW = ceg.encode(v, w);
		
		int [] clause = new int[]{acceptVW};
		encoding.addClause(clause);
	}
	
	public void acceptFiniteSymmetryInstance(List<List<Integer>> inputs, List<List<Integer>> outputs) throws ContradictionException{
		for(int i = 0; i < inputs.size(); i++){
			List<Integer> input = inputs.get(i);
			List<Integer> output = outputs.get(i);
			accept(input, output);
		}
	}
	
	public void acceptSymmetryGroups(List<Integer> wordLengths, List<List<int[]>> symmetryGenerators){
		for(int i = 0; i < wordLengths.size(); i++){
			acceptSymmetryGroup(wordLengths.get(i), symmetryGenerators.get(i));
		}
	}
	
	public void acceptSymmetryGroup(int length, List<int[]> symmetryGenerators){
		FiniteSymmetryUpdate symmetry = new FiniteSymmetryUpdate(encoding, listener);
		
		symmetry.addConstraints(length, symmetryGenerators);
	}
	
	public void updateCopyCatGroups(int numStates, int numLetters, boolean[] candidates, int num) throws ContradictionException {
		// then we have found a group of copycats,
		// add a constraint to get rid of it
		listener.inform("Copy-cat group:");

		int tempVarStart = encoding.getNextSATVar();
		int tempVar = tempVarStart;

		int[] clause = new int[(numLetters + 1) * num];
		int i = 0;

		for (int q = 1; q <= numStates; ++q)
			if (candidates[q]) {
				listener.inform(String.valueOf(q));

				clause[i] = -encoding.getIndexZVar(q);
				++i;

				for (int letter = 0; letter < numLetters; ++letter) {
					for (int r = 1; r <= numStates; ++r)
						if (candidates[r])
							encoding.addClause(new int[] {
									-encoding.getTransBoolVar(q, letter, letter, r),
									tempVar });

					clause[i] = -tempVar;
					++i;
					++tempVar;
				}
			}

		encoding.setNextSATVar(tempVar);
		assert (i == clause.length);
		encoding.addClause(clause);
	}
}
