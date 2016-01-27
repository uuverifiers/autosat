package encoding;

import java.util.Set;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public interface ISatSolver {

    void addClause(int[] clause) throws ContradictionException;

    int getClauseNum();

    int getNextSATVar();

    void setNextSATVar(int nextSATVar);
    
    boolean isSatisfiable() throws TimeoutException;

    Set<Integer> positiveModelVars();

}