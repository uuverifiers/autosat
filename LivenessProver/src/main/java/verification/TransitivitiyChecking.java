package verification;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.VerificationUltility;
import common.bellmanford.EdgeWeightedDigraph;

public class TransitivitiyChecking {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private EdgeWeightedDigraph transducer;
	private int numLetters;
	
	public TransitivitiyChecking(EdgeWeightedDigraph transducer, int numLetters){
		this.transducer = transducer;
		this.numLetters = numLetters;
	}
	
	/*
	 * return 3 words w1, w2, w3
	 */
	public List<List<Integer>> check(){
		EdgeWeightedDigraph composition = L2TransducerComposition.compose(transducer);

		//no need to call because it does not contain empty transition
//		if(!VerificationUltility.isDFA(composition, numLetters)){
//			composition = VerificationUltility.toDFA(composition, numLetters);
//		}
		
		if(!VerificationUltility.isDFA(transducer, numLetters)){
			transducer = VerificationUltility.toDFA(transducer, numLetters);
		}
		
		if(!VerificationUltility.isComplete(transducer, numLetters)){
			transducer = VerificationUltility.makeComplete(transducer, numLetters);
		}
		
		List<int[]> counterExample = L2TransducerInclusionChecking.findShortestCounterExample(composition, transducer);
		return VerificationUltility.convertToWords(counterExample, 3);
	}


}
