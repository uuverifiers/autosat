package main;

import grammar.Yylex;
import grammar.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import encoding.MinisatSolver;
import encoding.ISatSolverFactory;
import encoding.SatSolver;
import encoding.LingelingSolver;
import verification.MonolithicVerifier;
import verification.IncrementalVerifier;
import verification.FiniteStateSets;
import visitor.AllVisitorImpl;
import visitor.SymmetryProb;
import common.Ultility;
import common.VerificationUltility;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;
import common.finiteautomata.AutomataConverter;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ISatSolverFactory SOLVER_FACTORY =
	//MinisatSolver.FACTORY;            // Minisat
        SatSolver.FACTORY;                // Sat4j
        //LingelingSolver.FACTORY;          // Lingeling

    private final static boolean verifySolutions = false;

    public static void main(String[] args) {
	if(args.length < 1){
	    System.out.println("No input, doing nothing");
	    return;
	}

	String fileName = args[0];
	SymmetryProb problem = parse(fileName);

	determize(problem);

	writeInputProblem(problem);

	verifyFiniteInstances(problem, problem.getExplicitChecksUntilLength());

	if (problem.getCloseInitStates() && !problem.getAlwaysMonolithic()) {
	    IncrementalVerifier verifier =
		new IncrementalVerifier(problem, SOLVER_FACTORY,
					problem.getUseRankingFunctions(),
                                        problem.getPrecomputedInv(),
					verifySolutions);
            verifier.setup();
	    verifier.verify();
	} else {
	    MonolithicVerifier verifier =
		new MonolithicVerifier(problem, SOLVER_FACTORY,
				       problem.getUseRankingFunctions());
	    verifier.verify();
	}
    }

	public static SymmetryProb parse(String fileName) {
		SymmetryProb problem = null;
		try {
			problem = parseFromReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return problem;
	}

	private static SymmetryProb parseFromReader(Reader reader) {
		parser p;
		Yylex l = new Yylex(reader);
		p = new parser(l);

		try {
			grammar.Absyn.ModelRule parse_tree = p.pModelRule();

			SymmetryProb problem = new SymmetryProb();
			parse_tree.accept(new AllVisitorImpl(), problem);

			LOGGER.info("Parse Succesful!");
			return problem;
		} catch (Throwable e) {

			String error = ("At line " + String.valueOf(l.line_num()) + ", near \"" + l.buff() + "\" :\n") +
							("     " + e.getMessage());
			throw new RuntimeException(error);
		}
	}

	/**
	 * Determinizes all components of a problem
	 *
	 * @param[in,out]  problem  The problem to determinize
	 */
	private static void determize(SymmetryProb problem){
		EdgeWeightedDigraph player1  = problem.getPlayer1();
		if (!VerificationUltility.isDFA(player1, problem.getNumberOfLetters())) {
			player1 = VerificationUltility.toDFA(problem.getPlayer1(), problem.getNumberOfLetters());
			problem.setPlayer1(player1);
		}

		EdgeWeightedDigraph player2 = problem.getPlayer2();
		if(!VerificationUltility.isDFA(player2, problem.getNumberOfLetters())){
			player2 = VerificationUltility.toDFA(problem.getPlayer2(),  problem.getNumberOfLetters());
			problem.setPlayer2(player2);
		}

		Automata I0 = problem.getI0();
		if(!I0.isDFA()){
			I0 = AutomataConverter.toDFA(I0);
			problem.setI0(I0);
		}

		Automata F = problem.getF();
		if(!F.isDFA()){
			F = AutomataConverter.toDFA(F);
			problem.setF(F);
		}

	}

    public static void verifyFiniteInstances(SymmetryProb problem,
					     int sizeBound) {
	final FiniteStateSets finiteStates =
	    new FiniteStateSets(problem.getNumberOfLetters(),
				problem.getI0(), problem.getF(),
				problem.getPlayer1(),
				problem.getPlayer2(),
				problem.getLabelToIndex());
	for (int s = 0; s <= sizeBound; ++s) {
	    System.out.println("Verifying system instance for length " + s + " ... ");
	    finiteStates.verifyInstance(s, problem.getCloseInitStates());
	}
    }
	public static void writeInputProblem(SymmetryProb problem) {
		try {
			Ultility.writeOut(Ultility.toDot(problem.getI0(), problem.getLabelToIndex()), "automataI0.dot");

			Ultility.writeOut(Ultility.toDot(problem.getF(), problem.getLabelToIndex()), "automatonF.dot");
			Ultility.writeOut(Ultility.toDot(problem.getPlayer1(), problem.getLabelToIndex()), "transducerP1.dot");
			Ultility.writeOut(Ultility.toDot(problem.getPlayer2(), problem.getLabelToIndex()), "transducerP2.dot");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

// vim: tabstop=4
