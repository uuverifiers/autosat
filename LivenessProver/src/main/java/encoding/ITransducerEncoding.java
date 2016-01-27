package encoding;

import java.util.List;

public interface ITransducerEncoding {
    int getNumStates();
    int getNumLetters();
    ISatSolver getSolver();
    int getIndexZVar(int q);
    int getTransBoolVar(int source, int input, int output, int dest);
    List<Integer> getTransitions(int source, int destination);
    int getStartIndexOfTransVars();
    int getStartIndexOfZVars();
}