package common.finiteautomata.lstar;

import java.util.List;

import common.finiteautomata.Automata;

public interface Teacher {

    boolean isAccepted(List<Integer> word);

    boolean isCorrectLanguage(Automata sol,
			      List<List<Integer>> posCEX,
			      List<List<Integer>> negCEX);

}