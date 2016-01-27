package symmetryencoding.parasym.condition;

import java.util.Set;

import org.sat4j.specs.ContradictionException;

import symmetryencoding.cegeneration.UpdateFromCounterExample;

public interface ConditionChecking {

	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT)throws ContradictionException;
}
