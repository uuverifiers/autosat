package encoding;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SatSolver implements ISatSolver {
    private static final Logger LOGGER = LogManager.getLogger();
    
    static public ISatSolverFactory FACTORY = new ISatSolverFactory() {
	    public ISatSolver spawnSolver() {
		return new SatSolver();
	    }
	};
    
	private ISolver solver;
	private int assertedClauses = 0;
	private int nextSATVar = 1;
	
	public SatSolver(){
//		solver = SolverFactory.newDefault();
		solver = SolverFactory.newGlucose();
		solver.newVar(Constants.MAXVAR);
		solver.setExpectedNumberOfClauses(Constants.NBCLAUSES);
	}
	
	public void addClause(int[] clause) throws ContradictionException{
            //            LOGGER.debug(Arrays.toString(clause));
		
		solver.addClause(new VecInt(clause));
        assertedClauses += 1;
        
	}

    public int getClauseNum() {
	return assertedClauses;
    }

	public int getNextSATVar() {
		return nextSATVar;
	}

	public void setNextSATVar(int nextSATVar) {
		this.nextSATVar = nextSATVar;
	}

	public ISolver getSolver() {
		return solver;
	}

	public boolean isSatisfiable() throws TimeoutException {
	    LOGGER.debug("Calling Sat4j");
	    final long startTime = System.currentTimeMillis();

	    final boolean res = solver.isSatisfiable();

	    LOGGER.debug("Solving time " +
			(System.currentTimeMillis() - startTime) + "ms");

	    return res;
	}

	public int[] model() {
		return solver.model();
	}

    public Set<Integer> positiveModelVars() {
        Set<Integer> res = new HashSet<Integer>();
        int[] model = solver.model();
        for (int i = 0; i < model.length; ++i) {
            int v = model[i];
            if (v > 0)
                res.add(v);
        }
        return res;
    }
}
