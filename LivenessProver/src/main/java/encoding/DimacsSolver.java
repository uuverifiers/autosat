package encoding;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public abstract class DimacsSolver implements ISatSolver {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private int nextSATVar = 1;
    private List<int[]> clauses = new ArrayList<int[]>();
    protected int[] model = null;

    public DimacsSolver(){
    }
	
    public void addClause(int[] clause) throws ContradictionException{
	//            LOGGER.debug(Arrays.toString(clause));
	clauses.add(clause);
    }

    public int getClauseNum() {
	return clauses.size();
    }

    public int getNextSATVar() {
	return nextSATVar;
    }

    public void setNextSATVar(int nextSATVar) {
	this.nextSATVar = nextSATVar;
    }
    
    protected File writeDimacs() {
	try {
	    final File inputFile =
		File.createTempFile("adviceConstraints", ".cnf");
	    
	    LOGGER.info("Writing DIMACS to " + inputFile);

	    final PrintWriter iw = new PrintWriter(inputFile);
	    iw.println("p cnf " + (nextSATVar + 1) + " " + clauses.size());
	    for (int[] clause : clauses) {
		for (int i = 0; i < clause.length; ++i)
		    iw.print("" + clause[i] + " ");
		iw.println("0");
	    }
	    iw.close();

	    return inputFile;
	} catch (IOException e) {
	    throw new IllegalArgumentException(e.getMessage());
	}
    }

    public int[] model() {
	return model;
    }

    public Set<Integer> positiveModelVars() {
        Set<Integer> res = new HashSet<Integer>();
        int[] model = model();
        for (int i = 0; i < model.length; ++i) {
            int v = model[i];
            if (v > 0)
                res.add(v);
        }
        return res;
    }

}