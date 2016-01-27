package symmetryencoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;

public class SymmetryProb{
	private int minNumberOfStates = 0;
	private int maxNumberOfStates = 0;
	private int numberOfLetters;
	
	private EdgeWeightedDigraph graphToCheck;
	private List<Integer> wordLengths = new ArrayList<Integer>();
	private List<List<int[]>> symmetryGenerators = new ArrayList<List<int[]>>();
	
	private List<List<Integer>> finiteSymmetryInputs = new ArrayList<List<Integer>>();
	private List<List<Integer>> finiteSymmetryOutputs = new ArrayList<List<Integer>>();

	private List<Integer> finiteOutputs = new ArrayList<Integer>();
	private List<int[]> impossiblePair = new ArrayList<int[]>();
	private Automata validConfiguration;
	
	private Map<String, Integer> transducerLabelToIndex = new HashMap<String, Integer>();

	@Override
	public String toString() {
		return "SymmetryProb [minNumberOfStates=" + minNumberOfStates
				+ ", maxNumberOfStates=" + maxNumberOfStates
				+ ", numberOfLetters=" + numberOfLetters + ", graphToCheck="
				+ graphToCheckToString() + ", wordLengths=" + wordLengths
				+ ", symmetryGenerators=" + symmetryGeneratorString()
				+ ", finiteSymmetryInputs=" + finiteSymmetryInputs
				+ ", finiteSymmetryOutputs=" + finiteSymmetryOutputs
				+ ", finiteOutputs=" + finiteOutputs + ", impossiblePair="
				+ impossiblePair + ", validConfiguration=" + validConfiguration
				+ "]";
	}
	
	private String symmetryGeneratorString(){
		StringBuilder result = new StringBuilder();
		for(List<int[]> generators: symmetryGenerators){
			result.append("(");
			for(int[] generator: generators){
				result.append("(" + Arrays.toString(generator) + ")");
			}
			result.append(")");
		}
		return result.toString();
	}
	
	private String graphToCheckToString(){
		StringBuilder result = new StringBuilder();
		
		String NEW_LINE = "\n";
		result.append(NEW_LINE);
		result.append("systemTransitions{");
		result.append(NEW_LINE);
		result.append("init: " + graphToCheck.getInitState() + ";");
		result.append(NEW_LINE);
		for(DirectedEdge edge: graphToCheck.edges()){
			DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
			if(tempEdge.getInput() == -1 && tempEdge.getOutput() == -1){
				result.append(tempEdge.from() + " -> " + tempEdge.to());
			}
			else{
				result.append(tempEdge.from() + " -> " + tempEdge.to() + " " + tempEdge.getInput() + "/" + tempEdge.getOutput());
			}
			result.append(";");
			result.append(NEW_LINE);
		}
		
		result.append("accepting: ");
		List<Integer> acceptings = new ArrayList<Integer>(graphToCheck.getAcceptingStates());
		for(int i = 0; i < acceptings.size(); i++){
			result.append(acceptings.get(i));
			if(i < acceptings.size() - 1){
				result.append(", ");
			}
		}
		result.append(";");
		result.append(NEW_LINE);
		
		result.append("}");
		result.append(NEW_LINE);
		
		return result.toString();
	}
	
	public int getMinNumberOfStates() {
		return minNumberOfStates;
	}
	public void setMinNumberOfStates(int minNumberOfStates) {
		this.minNumberOfStates = minNumberOfStates;
	}
	public int getMaxNumberOfStates() {
		return maxNumberOfStates;
	}
	public void setMaxNumberOfStates(int maxNumberOfStates) {
		this.maxNumberOfStates = maxNumberOfStates;
	}
	public int getNumberOfLetters() {
		return numberOfLetters;
	}
	public void setNumberOfLetters(int numberOfLetters) {
		this.numberOfLetters = numberOfLetters;
	}
	public EdgeWeightedDigraph getGraphToCheck() {
		return graphToCheck;
	}
	public void setGraphToCheck(EdgeWeightedDigraph graphToCheck) {
		this.graphToCheck = graphToCheck;
	}
	public List<Integer> getWordLengths() {
		return wordLengths;
	}
	public void setWordLengths(List<Integer> wordLengths) {
		this.wordLengths = wordLengths;
	}
	public List<List<int[]>> getSymmetryGenerators() {
		return symmetryGenerators;
	}
	public void setSymmetryGenerators(List<List<int[]>> symmetryGenerators) {
		this.symmetryGenerators = symmetryGenerators;
	}
	public List<List<Integer>> getFiniteSymmetryInputs() {
		return finiteSymmetryInputs;
	}
	public void setFiniteSymmetryInputs(List<List<Integer>> finiteSymmetryInputs) {
		this.finiteSymmetryInputs = finiteSymmetryInputs;
	}
	public List<List<Integer>> getFiniteSymmetryOutputs() {
		return finiteSymmetryOutputs;
	}
	public void setFiniteSymmetryOutputs(List<List<Integer>> finiteSymmetryOutputs) {
		this.finiteSymmetryOutputs = finiteSymmetryOutputs;
	}
	public List<Integer> getFiniteOutputs() {
		return finiteOutputs;
	}
	public void setFiniteOutputs(List<Integer> finiteOutputs) {
		this.finiteOutputs = finiteOutputs;
	}
	public List<int[]> getImpossiblePair() {
		return impossiblePair;
	}
	public void setImpossiblePair(List<int[]> impossiblePair) {
		this.impossiblePair = impossiblePair;
	}
	public Automata getValidConfiguration() {
		return validConfiguration;
	}
	public void setValidConfiguration(Automata validConfiguration) {
		this.validConfiguration = validConfiguration;
	}

	
	public Map<String, Integer> getTransducerLabelToIndex() {
		return transducerLabelToIndex;
	}

	public void setTransducerLabelToIndex(
			Map<String, Integer> transducerLabelToIndex) {
		this.transducerLabelToIndex = transducerLabelToIndex;
	}
	
	
}
