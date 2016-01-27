package visitor;

import grammar.AllVisitor;
import grammar.Absyn.AutomataAcceptings;
import grammar.Absyn.AutomataEmptyTransition;
import grammar.Absyn.AutomataInitialState;
import grammar.Absyn.AutomataTransition;
import grammar.Absyn.AutomataTransitionRule;
import grammar.Absyn.AutomorphismCondition;
import grammar.Absyn.CheckingConditionRule;
import grammar.Absyn.CopycatCondition;
import grammar.Absyn.EmptyTransition;
import grammar.Absyn.FiniteOutput;
import grammar.Absyn.FulTransition;
import grammar.Absyn.Generator;
import grammar.Absyn.GeneratorRule;
import grammar.Absyn.ImpossiblePair;
import grammar.Absyn.ImpossiblePairRule;
import grammar.Absyn.ImpossiblePairs;
import grammar.Absyn.IndexSequence;
import grammar.Absyn.InjectiveInputCondition;
import grammar.Absyn.InjectiveOutputCondition;
import grammar.Absyn.InputUniversalCondition;
import grammar.Absyn.ListName;
import grammar.Absyn.LiteralName;
import grammar.Absyn.LoopingTransition;
import grammar.Absyn.Model;
import grammar.Absyn.Name;
import grammar.Absyn.NameSequence;
import grammar.Absyn.NameSpace;
import grammar.Absyn.NameSpaceRule;
import grammar.Absyn.NumberName;
import grammar.Absyn.NumberOfStatesGuess;
import grammar.Absyn.NumberSpace;
import grammar.Absyn.OutputUniversalCondition;
import grammar.Absyn.ParikhCondition;
import grammar.Absyn.PermutativeCondition;
import grammar.Absyn.Statement;
import grammar.Absyn.SymInstance;
import grammar.Absyn.SymmetryInstance;
import grammar.Absyn.SymmetryInstances;
import grammar.Absyn.SymmetryPair;
import grammar.Absyn.SymmetryPairRule;
import grammar.Absyn.SymmetryPairs;
import grammar.Absyn.Transducer;
import grammar.Absyn.TransducerAccepting;
import grammar.Absyn.TransducerInitialState;
import grammar.Absyn.TransitionRule;
import grammar.Absyn.TurnOffConditions;
import grammar.Absyn.ValidConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import symmetryencoding.Configuration;
import symmetryencoding.SymmetryProb;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;
import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class AllVisitorImpl implements AllVisitor<Object, SymmetryProb>{

	private Map<String, Integer> transducerStateToIndex = new HashMap<String, Integer>();
	private Map<String, Integer> labelToIndex = new HashMap<String, Integer>();
	
	private Map<String, Integer> automataStateToIndex = new HashMap<String, Integer>();
	
	//transducer info temporary
	private List<Integer> iStarStates = new ArrayList<Integer>();
	
	public Object visit(Model p, SymmetryProb arg) {
		EdgeWeightedDigraph graph = (EdgeWeightedDigraph) p.transducerrule_.accept(this, arg);
		arg.setGraphToCheck(graph);
		
		for(Statement statement: p.liststatement_){
			statement.accept(this, arg);
		}
		
		arg.setTransducerLabelToIndex(labelToIndex);
		arg.setNumberOfLetters(labelToIndex.size());
		
		return null;
	}

	public Object visit(Transducer p, SymmetryProb arg) {
		// reset temporary data
		transducerStateToIndex = new HashMap<String, Integer>();
		iStarStates = new ArrayList<Integer>();

		int initState = (Integer) p.initrule_.accept(this, arg);

		List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();
		for (TransitionRule transitionRule : p.listtransitionrule_) {
			edges.add((DirectedEdgeWithInputOutput) transitionRule.accept(this,
					arg));
		}

		Set<Integer> acceptingStates = (Set<Integer>) p.acceptingrule_.accept(this, arg);

		// process after collecting information
		int numStates = transducerStateToIndex.size();
		int numLetters = labelToIndex.size();

		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(numStates, initState, acceptingStates);
		for (DirectedEdgeWithInputOutput edge : edges) {
			graph.addEdge(edge);
		}

		// add i* transition
		for (int loopingState : this.iStarStates) {
			List<DirectedEdge> loopingTransitions = addIStartEdge(loopingState, numLetters);
			for (DirectedEdge loopingTransition : loopingTransitions) {
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

	public Object visit(NumberOfStatesGuess p, SymmetryProb arg) {
		arg.setMinNumberOfStates(p.integer_1);
		arg.setMaxNumberOfStates(p.integer_2);
		return null;
	}

	public Object visit(SymmetryPairs p, SymmetryProb arg) {
		for(SymmetryPairRule pair: p.listsymmetrypairrule_){
			pair.accept(this, arg);
		}
		return null;
	}

	public Object visit(SymmetryInstances p, SymmetryProb arg) {
		for(SymInstance instance: p.listsyminstance_){
			instance.accept(this, arg);
		}
		return null;
	}

	public Object visit(FiniteOutput p, SymmetryProb arg) {
		List<String> names = getNames(p.listname_, arg);
		
		List<Integer> finiteOutputs = getIndexes(names, labelToIndex);
		arg.setFiniteOutputs(finiteOutputs);
		
		return null;
	}

	public Object visit(ImpossiblePairs p, SymmetryProb arg) {
		for(ImpossiblePairRule impossiblePair: p.listimpossiblepairrule_){
			impossiblePair.accept(this, arg);
		}
		return null;
	}

	public Object visit(TurnOffConditions p, SymmetryProb arg) {
		for(CheckingConditionRule condition: p.listcheckingconditionrule_){
			condition.accept(this, arg);
		}
		return null;
	}

	public Object visit(SymmetryPair p, SymmetryProb arg) {
		List<String> inputs = (List<String>) p.namesequencerule_1.accept(this, arg);
		List<String> outputs = (List<String>) p.namesequencerule_2.accept(this, arg);
		arg.getFiniteSymmetryInputs().add(getIndexes(inputs, labelToIndex));
		arg.getFiniteSymmetryOutputs().add(getIndexes(outputs, labelToIndex));
		
		return null;
	}

	public Object visit(NameSequence p, SymmetryProb arg) {
		List<String> result = new ArrayList<String>();
		for(NameSpaceRule nameSpace: p.listnamespacerule_){
			result.add((String) nameSpace.accept(this, arg));
		}
		
		return result;
	}

	public Object visit(NameSpace p, SymmetryProb arg) {
		return getName(p.name_, arg);
	}
	
	public Object visit(SymmetryInstance p, SymmetryProb arg) {
		int length = p.integer_;
		List<int[]> generatos = new ArrayList<int[]>();
		for(GeneratorRule generator: p.listgeneratorrule_){
			generatos.add((int[]) generator.accept(this, arg));
		}
		
		arg.getWordLengths().add(length);
		arg.getSymmetryGenerators().add(generatos);
		//
		return null;
	}

	public Object visit(Generator p, SymmetryProb arg) {
		return p.indexsequencerule_.accept(this, arg);
	}


	public Object visit(IndexSequence p, SymmetryProb arg) {
		int[] indexes = new int[p.listnumberspacerule_.size()];
		
		for(int i = 0; i < p.listnumberspacerule_.size(); i++){
			indexes[i] = (Integer) p.listnumberspacerule_.get(i).accept(this, arg);
		}

		return indexes;
	}
	
	public Object visit(NumberSpace p, SymmetryProb arg) {
		return p.integer_;
	}
	
	public Object visit(ImpossiblePair p, SymmetryProb arg) {
		int input = getIndex(p.name_1, arg, labelToIndex);
		int output = getIndex(p.name_2, arg, labelToIndex);
		
		arg.getImpossiblePair().add(new int[]{input, output});
		return null;
	}

	public Object visit(ValidConfiguration p, SymmetryProb arg) {
		//reset temporary data
		automataStateToIndex = new HashMap<String, Integer>();
		
		int initState = (Integer) p.automatainitrule_.accept(this, arg);
		
		List<DirectedEdgeWithInputOutput> edges = new ArrayList<DirectedEdgeWithInputOutput>();
		for(AutomataTransitionRule transition: p.listautomatatransitionrule_){
			edges.add((DirectedEdgeWithInputOutput) transition.accept(this, arg));
		}
		
		Set<Integer> acceptingStates = (Set<Integer>) p.automataacceptingsrule_.accept(this, arg);
		
		//count number of states
		Set<Integer> states = new HashSet<Integer>();
		for(DirectedEdgeWithInputOutput edge: edges){
			states.add(edge.from());
			states.add(edge.to());
		}
		
		common.finiteautomata.Automata newAutomata = new common.finiteautomata.Automata(initState, states.size(), arg.getNumberOfLetters());
		newAutomata.setAcceptingStates(acceptingStates);
		for(DirectedEdgeWithInputOutput edge: edges){
			newAutomata.addTrans(edge.from(), edge.getInput(), edge.to());
		}
		
		//note that automata is not in correct format, 0 is preserved empty transition
		arg.setValidConfiguration(newAutomata);
		return null;
	}
	
	public Object visit(AutomataInitialState p, SymmetryProb arg) {
		return getIndex(p.name_, arg, automataStateToIndex);
	}

	public Object visit(AutomataTransition p, SymmetryProb arg) {
		int source = getIndex(p.name_1, arg, automataStateToIndex);
		int destination = getIndex(p.name_2, arg, automataStateToIndex);
		int label = getIndex(p.name_3, arg, labelToIndex);
		
		
		DirectedEdge edge = new DirectedEdgeWithInputOutput(source, destination, label, 0);
		
		return edge;
	}


	public Object visit(AutomataEmptyTransition p, SymmetryProb arg) {
		int source = getIndex(p.name_1, arg, automataStateToIndex);
		int destination = getIndex(p.name_2, arg, automataStateToIndex);
		int label = common.finiteautomata.Automata.EPSILON_LABEL;
		
		
		DirectedEdge edge = new DirectedEdgeWithInputOutput(source, destination, label, 0);
		
		return edge;
	}
	
	public Object visit(AutomataAcceptings p, SymmetryProb arg) {
		Set<Integer> acceptings = new HashSet<Integer>();
		List<String> names = getNames(p.listname_, arg);
		List<Integer> indexes = getIndexes(names, automataStateToIndex);
		
		acceptings.addAll(indexes);
		return acceptings;
	}

	public Object visit(ParikhCondition p, SymmetryProb arg) {
		Configuration.offParikh = true;
		return null;
	}

	public Object visit(AutomorphismCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.Automorphism);
		return null;
	}

	public Object visit(PermutativeCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.Permutative);
		return null;
	}

	public Object visit(InjectiveOutputCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.InjectiveOutput);
		return null;
	}

	public Object visit(InjectiveInputCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.InjectiveInput);
		return null;
	}

	public Object visit(CopycatCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.Copycat);
		return null;
	}

	public Object visit(OutputUniversalCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.OutputUnviersal);
		return null;
	}

	public Object visit(InputUniversalCondition p, SymmetryProb arg) {
		Configuration.offConditions.add(Condition.InputUniversal);
		return null;
	}

	public Object visit(NumberName p, SymmetryProb arg) {
		return p.integer_.toString();
	}

	public Object visit(LiteralName p, SymmetryProb arg) {
		return p.ident_;
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
}
