package visitor;

import grammar.AllVisitor;
import grammar.Absyn.AutomataAcceptings;
import grammar.Absyn.AutomataEmptyTransition;
import grammar.Absyn.AutomataInitialState;
import grammar.Absyn.AutomataTransition;
import grammar.Absyn.AutomataTransitionRule;
import grammar.Absyn.Automaton;
import grammar.Absyn.EmptyTransition;
import grammar.Absyn.FulTransition;
import grammar.Absyn.ListName;
import grammar.Absyn.LiteralName;
import grammar.Absyn.LoopingTransition;
import grammar.Absyn.Model;
import grammar.Absyn.Name;
import grammar.Absyn.NumOfStatesAutomatonGuess;
import grammar.Absyn.NumOfInitStatesAutomatonGuess;
import grammar.Absyn.NoInitStatesAutomatonGuess;
import grammar.Absyn.NumOfStatesTransducerGuess;
import grammar.Absyn.NumberName;
import grammar.Absyn.Transducer;
import grammar.Absyn.TransducerAccepting;
import grammar.Absyn.TransducerInitialState;
import grammar.Absyn.TransitionRule;
import grammar.Absyn.ClosedInit;
import grammar.Absyn.NotClosedInit;
import grammar.Absyn.VerifierOption;
import grammar.Absyn.SymmetryOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;


public class AllVisitorImpl implements AllVisitor<Object, SymmetryProb>{
	private static final Logger LOGGER = LogManager.getLogger();

	private Map<String, Integer> transducerStateToIndex = new HashMap<String, Integer>();
	private Map<String, Integer> labelToIndex = new HashMap<String, Integer>();
	
	private Map<String, Integer> automataStateToIndex = new HashMap<String, Integer>();
	
	//transducer looping transition info temporary
	private List<Integer> iStarStates = new ArrayList<Integer>();
	
	public Object visit(Model p, SymmetryProb arg) {
		
		Automata I0 = (Automata) p.automatonrule_1.accept(this, arg);
		p.maybeclosed_.accept(this, arg);
		Automata F = (Automata) p.automatonrule_2.accept(this, arg);
		EdgeWeightedDigraph player1 = (EdgeWeightedDigraph) p.transducerrule_1.accept(this, arg);
		EdgeWeightedDigraph player2 = (EdgeWeightedDigraph) p.transducerrule_2.accept(this, arg);
		
                for (VerifierOption o : p.listverifieroption_)
                    o.accept(this, arg);
		
                final int numLabels = labelToIndex.size();

                I0.setNumLabels(numLabels);
                F.setNumLabels(numLabels);

		//set mapping of Label
		arg.setI0(I0);
		arg.setF(F);
		arg.setPlayer1(player1);
		arg.setPlayer2(player2);
		arg.setLabelToIndex(labelToIndex);
		arg.setNumberOfLetters(numLabels);

                LOGGER.info("Label mapping: " + labelToIndex);
		
		return null;
	}

	public Object visit(Transducer p, SymmetryProb arg) {
		//reset temporary data
		transducerStateToIndex = new HashMap<String, Integer>();
		iStarStates = new ArrayList<Integer>();
		
		int initState = (Integer) p.initrule_.accept(this, arg);
		
		List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();
		for(TransitionRule transitionRule: p.listtransitionrule_){
			DirectedEdgeWithInputOutput newEdge = (DirectedEdgeWithInputOutput) transitionRule.accept(this, arg);
			if(newEdge != null){
				edges.add(newEdge);
			}
		}
		
		Set<Integer> acceptingStates = (Set<Integer>) p.acceptingrule_.accept(this, arg);
		
		//process after collecting information
		int numStates = transducerStateToIndex.size();
		int numLetters = labelToIndex.size();
		
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, initState, acceptingStates);
		for(DirectedEdgeWithInputOutput edge: edges){
			graph.addEdge(edge);
		}
		
		//add i* transition
		for(int loopingState: this.iStarStates){
			List<DirectedEdge> loopingTransitions = addIStartEdge(loopingState, numLetters);
			for(DirectedEdge loopingTransition: loopingTransitions){
				graph.addEdge(loopingTransition);
			}
		}
		
		return graph;
	}

	public Object visit(TransducerInitialState p, SymmetryProb arg) {
		int init = getIndex(p.name_, arg, transducerStateToIndex);
		return init;
	}

	public Object visit(FulTransition p, SymmetryProb arg) {
		int from = getIndex(p.name_1, arg, transducerStateToIndex);
		int to = getIndex(p.name_2, arg, transducerStateToIndex);
		int input = getIndex(p.name_3, arg, labelToIndex);
		int output = getIndex(p.name_4, arg, labelToIndex);
		
		DirectedEdge newEdge = new DirectedEdgeWithInputOutput(from, to, input, output);
		
		return newEdge;
	}

	public Object visit(EmptyTransition p, SymmetryProb arg) {
		int from = getIndex(p.name_1, arg, transducerStateToIndex);
		int to = getIndex(p.name_2, arg, transducerStateToIndex);
		int input = common.finiteautomata.Automata.EPSILON_LABEL;
		int output = common.finiteautomata.Automata.EPSILON_LABEL;
		
		DirectedEdge newEdge = new DirectedEdgeWithInputOutput(from, to, input, output);
		
		return newEdge;
	}

	public Object visit(LoopingTransition p, SymmetryProb arg) {
		for(Name name: p.listname_){
			int stateIndex = getIndex(name, arg, transducerStateToIndex);
			this.iStarStates.add(stateIndex);
		}
		
		return null;
	}

	public Object visit(TransducerAccepting p, SymmetryProb arg) {
		List<String> names = getNames(p.listname_, arg);
		List<Integer> acceptings = getIndexes(names, transducerStateToIndex);
		
		return new HashSet<Integer>(acceptings);
	}

	public Object visit(Automaton p, SymmetryProb arg) {
		//reset temporary data
		automataStateToIndex = new HashMap<String, Integer>();
		
		int initState = (Integer) p.automatainitrule_.accept(this, arg);
		
		List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();
		for(AutomataTransitionRule transition: p.listautomatatransitionrule_){
			edges.add((DirectedEdgeWithInputOutput) transition.accept(this, arg));
		}
		
		Set<Integer> acceptingStates = (Set<Integer>) p.automataacceptingsrule_.accept(this, arg);
		
		common.finiteautomata.Automata newAutomata = new common.finiteautomata.Automata(initState, automataStateToIndex.size(), labelToIndex.size());
		newAutomata.setAcceptingStates(acceptingStates);
		for(DirectedEdgeWithInputOutput edge: edges){
			newAutomata.addTrans(edge.from(), edge.getInput(), edge.to());
		}
		
		return newAutomata;
	}
	
	public Object visit(AutomataInitialState p, SymmetryProb arg) {
		return getIndex(p.name_, arg, automataStateToIndex);
	}

	public Object visit(AutomataTransition p, SymmetryProb arg) {
		int source = getIndex(p.name_1, arg, automataStateToIndex);
		int destination = getIndex(p.name_2, arg, automataStateToIndex);
		int label = getIndex(p.name_3, arg, labelToIndex);
		
		int dummyOutput = 0;
		DirectedEdge edge = new DirectedEdgeWithInputOutput(source, destination, label, dummyOutput);
		
		return edge;
	}


	public Object visit(AutomataEmptyTransition p, SymmetryProb arg) {
		int source = getIndex(p.name_1, arg, automataStateToIndex);
		int destination = getIndex(p.name_2, arg, automataStateToIndex);
		int label = common.finiteautomata.Automata.EPSILON_LABEL;
		
		int dummyOutput = 0;
		DirectedEdge edge = new DirectedEdgeWithInputOutput(source, destination, label, dummyOutput);
		
		return edge;
	}
	
	public Object visit(AutomataAcceptings p, SymmetryProb arg) {
		Set<Integer> acceptings = new HashSet<Integer>();
		List<String> names = getNames(p.listname_, arg);
		List<Integer> indexes = getIndexes(names, automataStateToIndex);
		
		acceptings.addAll(indexes);
		return acceptings;
	}

	public Object visit(NumberName p, SymmetryProb arg) {
		return p.integer_.toString();
	}

	public Object visit(LiteralName p, SymmetryProb arg) {
		return p.ident_;
	}
	
	public Object visit(NumOfStatesTransducerGuess p, SymmetryProb arg) {
		arg.setMinNumOfStatesTransducer(p.integer_1);
		arg.setMaxNumOfStatesTransducer(p.integer_2);
		return null;
	}

	public Object visit(NumOfStatesAutomatonGuess p, SymmetryProb arg) {
		arg.setMinNumOfStatesAutomaton(p.integer_1);
		arg.setMaxNumOfStatesAutomaton(p.integer_2);
		return null;
	}

    public Object visit(NumOfInitStatesAutomatonGuess p, SymmetryProb arg)
    {
        arg.setMinNumOfInitStatesAutomaton(p.integer_1);
        arg.setMaxNumOfInitStatesAutomaton(p.integer_2);
        return null;
    }
    public Object visit(NoInitStatesAutomatonGuess p, SymmetryProb arg)
    {
        arg.setMinNumOfInitStatesAutomaton(1);
        arg.setMaxNumOfInitStatesAutomaton(1);
        return null;
    }

    public Object visit(ClosedInit p, SymmetryProb arg)
    {
        arg.setCloseInitStates(true);
        return null;
    }
    public Object visit(NotClosedInit p, SymmetryProb arg)
    {
        arg.setCloseInitStates(false);
        return null;
    }
	
	private String getName(Name name, SymmetryProb arg){
		return (String) name.accept(this, arg);
	}
	
	private List<String> getNames(ListName names, SymmetryProb arg){
		List<String> result = new ArrayList<String>();
		for(Name name: names){
			result.add(getName(name, arg));
		}
		
		return result;
	}
	
	private List<Integer> getIndexes(List<String> names, Map<String, Integer> mapping){
		List<Integer> result = new ArrayList<Integer>();
		for(String name: names){
			result.add(getIndex(name, mapping));
		}
		
		return result;
	}
	
	private Integer getIndex(String name, Map<String, Integer> mapping){
		if(mapping.containsKey(name)){
			return mapping.get(name);
		}
		else{
			int value = mapping.size();
			mapping.put(name, value);
			return value;
		}
	}
	
	private Integer getIndex(Name name, SymmetryProb arg, Map<String, Integer> mapping){
		String nameLiteral = getName(name, arg);
		
		return getIndex(nameLiteral, mapping);
	}
	
	private List<DirectedEdge> addIStartEdge(int state, int numLetters){
		List<DirectedEdge> result = new ArrayList<DirectedEdge>();
		for(int i = 0; i < numLetters; i++){
			DirectedEdgeWithInputOutput newEdge = new DirectedEdgeWithInputOutput(state, state, i, i);
			result.add(newEdge);
		}
		
		return result;
	}

    public Object visit(grammar.Absyn.SymmetryOptions p,
                        SymmetryProb arg) {
        for (SymmetryOption so : p.listsymmetryoption_)
            so.accept(this, arg);
        return null;
    }

    public Object visit(grammar.Absyn.RotationSymmetry p,
                        SymmetryProb arg) {
        arg.addSymmetry("rotation");
        return null;
    }

    public Object visit(grammar.Absyn.ExplicitChecks p, SymmetryProb arg) {
        arg.setExplicitChecksUntilLength(p.integer_);
        return null;
    }

    public Object visit(grammar.Absyn.UseRankingFunctions p, SymmetryProb arg) {
        arg.setUseRankingFunctions(true);
        return null;
    }

    public Object visit(grammar.Absyn.MonolithicWitness p, SymmetryProb arg) {
        arg.setAlwaysMonolithic(true);
        return null;
    }

    public Object visit(grammar.Absyn.NoPrecomputedInv p, SymmetryProb arg) {
        arg.setPrecomputedInv(false);
        return null;
    }

}
