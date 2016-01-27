package symmetryencoding;

import grammar.Yylex;
import grammar.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import common.bellmanford.EdgeWeightedDigraph;

import symmetryencoding.encoding.TransducerEncoding;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;
import symmetryencoding.transducer.TransducerConverter;
import symmetryencoding.transducer.TransducerGenerator;
import visitor.AllVisitorImpl;
import callback.Listener;
import callback.LoggerListener;

public class Main {

	private static final String FileOption = "f";
	private static final String OutputOption = "o";
	private static final String HelpOption = "help";

	private Listener listener;

	public void setListener(final Listener listener) {
		this.listener = listener;
	}

	private Listener getListener() {
		if (listener == null) {
			// If no custom listener was configured, use the default one
			listener = new LoggerListener();
		}
		return listener;
	}

	public static void main(String[] args) throws ParseException {
		final Main instance = new Main();
		
		Options options = getOptions();
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption(HelpOption)){
			instance.getListener().inform(getHelpMessage());
			return;
		}
		
		if(cmd.hasOption(OutputOption)){
			Configuration.exportTransducer = true;
		}
		
		String fileName = null;
		if(cmd.hasOption(FileOption)){
			fileName = cmd.getOptionValue(FileOption);
		}
		
		//
		Configuration.inputFileName = fileName;

		try {
			instance.run(fileName, null, new PrintWriter(Ultility.getOutputFileName()));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Run the algorithm on the given <code>fileContent</code>.
	 * 
	 * @param fileContent
	 *            Content of the code file to be processed. Mandatory.
	 * @return Content of the result dot file.
	 */
	public String process(final String fileContent) {
		Configuration.exportTransducer = true;
		Configuration.inputFileName = null;

		Writer writer = new StringWriter();
		run(null, fileContent, writer);

		return writer.toString();
	}

	public void run(String fileName, String fileContent, Writer outputWriter) {
		long start = System.currentTimeMillis();

		final SymmetryProb problem = parse(fileName, fileContent);
		if (fileName != null) {
			getListener().inform("Running " + fileName);
		}
		formatTheInputModel(problem);

		//
		int minNumberOfStates = problem.getMinNumberOfStates();
		int maxNumberOfStates = problem.getMaxNumberOfStates();

		boolean found = false;
		for (int numStates = minNumberOfStates; numStates <= maxNumberOfStates; numStates++) {
			TransducerEncoding encoding = new TransducerEncoding(numStates,
					problem);
			encoding.setListener(getListener());
			found = encoding.guessingTransducer(outputWriter);
			if (found) {
				getListener().inform("A transducer with " + numStates
						+ " states is found!");
				break;
			}
		}
		
		if(!found){
			getListener().inform("No transducer is found!");
		}
		
		long end = System.currentTimeMillis();
		getListener().inform("Running Time " + (end - start));
	}

	public SymmetryProb parse(String fileName, String fileContent) {
		SymmetryProb problem = null;
		if (fileName != null) {			
			try {
				problem = parseFromReader(new FileReader(fileName));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			problem = parseFromReader(new StringReader(fileContent));
		}
		return problem;
	}

	private SymmetryProb parseFromReader(Reader reader) {
		parser p;
		Yylex l = new Yylex(reader);
		p = new parser(l);
		
		try {
			grammar.Absyn.ModelRule parse_tree = p.pModelRule();
			getListener().inform("Parse Succesful!");

			SymmetryProb problem = new SymmetryProb();
			parse_tree.accept(new AllVisitorImpl(), problem);
			
			return problem;
		} catch (Throwable e) {
			
			String error = ("At line " + String.valueOf(l.line_num()) + ", near \"" + l.buff() + "\" :\n") +
							("     " + e.getMessage());
			throw new RuntimeException(error);
		}
	}
	
	private void formatTheInputModel(SymmetryProb problem){
		// if InputUniversal + OutputUniversal + InjectiveInput, switchoff InjectiveOutput
		if (isUniversalAndInjectiveInputEnable()) {
			Configuration.offConditions.add(Condition.InjectiveOutput);
		}
		
		EdgeWeightedDigraph graph = TransducerConverter.toDFA(problem.getGraphToCheck(), problem.getNumberOfLetters());
		graph = TransducerGenerator.makeFullySpecified(graph, problem.getNumberOfLetters());
		
		//
		problem.setGraphToCheck(graph);
	}
	
	private boolean isUniversalAndInjectiveInputEnable() {
		return !Configuration.offConditions.contains(Condition.InputUniversal) &&
			!Configuration.offConditions.contains(Condition.OutputUnviersal) &&
			!Configuration.offConditions.contains(Condition.InjectiveInput);
	}

	
	private static Options getOptions(){
		Options result = new Options();
		
		Option exportOutput = new Option(OutputOption, false, "export the transducer into output.txt as dot Format in the same folder");
		result.addOption(exportOutput);
		
		Option fileName = new Option(FileOption, true, "the input file");
		result.addOption(fileName);
		
		Option help = new Option( HelpOption, getHelpMessage() );
		result.addOption(help);
		return result;
	}
	
	public static String getHelpMessage() {
		StringBuilder result = new StringBuilder();
		
		result.append("Run the program as: Main -o -off checking* -c -f fileName\n");
		result.append("The optional condition -o is to generate the output file\n");
		result.append("The required option -f is the input model file.");
		
		return result.toString();
	}
	
	
}
