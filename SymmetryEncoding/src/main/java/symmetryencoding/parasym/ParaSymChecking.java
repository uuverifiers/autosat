package symmetryencoding.parasym;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import symmetryencoding.Configuration;
import symmetryencoding.Ultility;
import symmetryencoding.cegeneration.UpdateFromCounterExample;
import symmetryencoding.cegeneration.WordGenerator;
import symmetryencoding.encoding.TransducerEncoding;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;
import symmetryencoding.parasym.condition.ConditionChecking;
import callback.Listener;

public class ParaSymChecking {

	private Listener listener;

	public void setListener(final Listener listener) {
		this.listener = listener;
	}

	private TransducerEncoding encoding;
	
	public ParaSymChecking(TransducerEncoding encoding){
		this.encoding = encoding;
	}

	public boolean check(final Writer outputWriter) throws TimeoutException, ContradictionException {
		// we are done . Working now on the IProblem interface
		boolean unsat = true;
		boolean success = false;
		
		int numStates = encoding.getNumStates();
		int numLetters = encoding.getNumLetters();
		
		UpdateFromCounterExample updateSAT = new UpdateFromCounterExample(encoding, listener);
		
		listener.inform("Adding input/output words...");
		WordGenerator generator = new WordGenerator(encoding.getNumLetters(), 1, Configuration.MAX_LENGTH_TO_ACCEPT);
		List<List<Integer>> words = generator.generate();
		
		boolean offInputUniversal = Configuration.offConditions.contains(Condition.InputUniversal);
		boolean offOutputUnversal = Configuration.offConditions.contains(Condition.OutputUnviersal);
		
		if(!offInputUniversal){
			updateSAT.acceptInputs(words);
		}
		
		if(!offOutputUnversal){
			updateSAT.acceptOutputs(words);
		}
		
		listener.inform("Accept finite symmetry...");
		updateSAT.acceptFiniteSymmetryInstance(encoding.getFiniteSymmetryInputs(), encoding.getFiniteSymmetryOutputs());
		updateSAT.acceptSymmetryGroups(encoding.getWordLengths(), encoding.getSymmetryGenerators());
		
		int round = 0;

		CheckingConditionBuilder conditionBuilder = new CheckingConditionBuilder(encoding.getAutomorphismGraph(), encoding.getValidConfiguration(), listener);
		List<ConditionChecking> allConditions = conditionBuilder.getCheckers(Configuration.offConditions, Configuration.CHECKING_ORDER);
		
		
		listener.inform("Start checking...");
		while(encoding.getSolver().isSatisfiable()){
            round += 1;
            listener.inform("" + round + ": Found a model (" + encoding.getAssertedClauses() + " clauses), analysing ...");

			unsat = false;
			int[] model = encoding.getSolver().model();
			Set<Integer> acceptingStates = encoding.extractAcceptingStates(model);
			Ultility.printModel(model, numStates, numLetters, acceptingStates, this.listener);
			
			boolean satisfied = true;

                        long startTime = System.currentTimeMillis();
			
			for(ConditionChecking condition: allConditions){
				satisfied = condition.isSatisfied(model, numStates, numLetters, acceptingStates, updateSAT);
				if(!satisfied){
					break;
				}
			}
			
			if(!satisfied){
				continue;
			}

			
			//satisfied all conditions
			listener.inform("All conditions are satisfied (verification needed " +
                                        (System.currentTimeMillis() - startTime) + "ms)");
			if(Configuration.exportTransducer){
				Ultility.writeToDotFile(outputWriter, model, numStates, numLetters, acceptingStates, encoding.getTransducerLabelToIndex());
			}
			
			success = true;
			//			addNextAutomatonClause(model);

			break;
		}
		
		//
		if(success){
			return true;
		}
		
		if (!success) {
			listener.inform("No more models exist.");
		}

		if(unsat){
			listener.inform("Unsatisfiable!");
		}
		return false;
	}
}
