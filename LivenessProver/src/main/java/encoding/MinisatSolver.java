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

public class MinisatSolver extends DimacsSolver {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private static String SOLVER_CMD = "minisat";

    static public ISatSolverFactory FACTORY = new ISatSolverFactory() {
	    public ISatSolver spawnSolver() {
		return new MinisatSolver();
	    }
	};

    public boolean isSatisfiable() throws TimeoutException {
	boolean result = false;
	model = null;

	try {
	    final File inputFile = writeDimacs();
	    final File outputFile =
		File.createTempFile("adviceSolution", ".sol");

	    LOGGER.info("Calling Minisat");

	    Process p =
		Runtime.getRuntime().exec(SOLVER_CMD + " " +
					  inputFile + " " + outputFile);
	    p.waitFor();

	    LOGGER.info("Reading solver output from " + outputFile);

	    BufferedReader reader =
		new BufferedReader(new FileReader(outputFile));
	    
	    String line;
	    List<Integer> modelElements = new ArrayList<Integer>();
	    while ((line = reader.readLine()) != null) {
		if ("SAT".equals(line)) {
		    result = true;
		} else if ("UNSAT".equals(line)) {
		    result = false;
		} else {
		    for (String token : line.split(" "))
			modelElements.add(Integer.parseInt(token));
		}
	    }

	    model = new int [modelElements.size()];
	    for (int i = 0; i < model.length; ++i)
		model[i] = modelElements.get(i);

	    reader.close();
	    
	    inputFile.delete();
	    outputFile.delete();

	} catch (IOException e) {
	    throw new IllegalArgumentException(e.getMessage());
	} catch (InterruptedException e) {
	    throw new IllegalArgumentException(e.getMessage());
	}

	return result;
    }

}