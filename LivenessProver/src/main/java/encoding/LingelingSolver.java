package encoding;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class LingelingSolver extends DimacsSolver {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private static String SOLVER_CMD = "lingeling";

    static public ISatSolverFactory FACTORY = new ISatSolverFactory() {
	    public ISatSolver spawnSolver() {
		return new LingelingSolver();
	    }
	};

    public boolean isSatisfiable() throws TimeoutException {
	boolean result = false;
	model = null;

	try {
	    final File inputFile = writeDimacs();

	    LOGGER.info("Calling Lingeling");
	    final long startTime = System.currentTimeMillis();

	    Process p =
		Runtime.getRuntime().exec(SOLVER_CMD + " " + inputFile);

	    BufferedReader reader =
		new BufferedReader(new InputStreamReader(p.getInputStream()));
	    
	    String line;
	    List<Integer> modelElements = new ArrayList<Integer>();
	    while ((line = reader.readLine()) != null) {
		if ("s SATISFIABLE".equals(line)) {
		    result = true;
		} else if ("s UNSATISFIABLE".equals(line)) {
		    result = false;
		} else if (line.startsWith("v")) {
		    for (String token : line.substring(2).split(" "))
			modelElements.add(Integer.parseInt(token));
		}
	    }

	    model = new int [modelElements.size()];
	    for (int i = 0; i < model.length; ++i)
		model[i] = modelElements.get(i);

	    p.waitFor();
	    reader.close();
	    inputFile.delete();

	    LOGGER.info("Solving time " +
			(System.currentTimeMillis() - startTime) + "ms");

	} catch (IOException e) {
	    throw new IllegalArgumentException(e.getMessage());
	} catch (InterruptedException e) {
	    throw new IllegalArgumentException(e.getMessage());
	}

	return result;
    }

}