package visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import common.bellmanford.EdgeWeightedDigraph;
import common.finiteautomata.Automata;

public class SymmetryProb {
	private Automata I0;
	private Automata F;
	private EdgeWeightedDigraph player1;
	private EdgeWeightedDigraph player2;
	
	private Map<String, Integer> labelToIndex = new HashMap<String, Integer>();
	private int numberOfLetters;

	private int minNumOfStatesTransducer = 0;
	private int maxNumOfStatesTransducer = 0;
	
	private int minNumOfStatesAutomaton = 0;
	private int maxNumOfStatesAutomaton = 0;
	
	private int minNumOfInitStatesAutomaton = 0;
	private int maxNumOfInitStatesAutomaton = 0;

    private boolean closeInitStates = false;	
    private boolean useRankingFunctions = false;	

    private List<String> symmetries = new ArrayList<String> ();

    private int explicitChecksUntilLength = -1;

	public Automata getI0() {
		return I0;
	}
	public void setI0(Automata i0) {
		I0 = i0;
	}
	public Automata getF() {
		return F;
	}
	public void setF(Automata f) {
		F = f;
	}
	public EdgeWeightedDigraph getPlayer1() {
		return player1;
	}
	public void setPlayer1(EdgeWeightedDigraph player1) {
		this.player1 = player1;
	}
	public EdgeWeightedDigraph getPlayer2() {
		return player2;
	}
	public void setPlayer2(EdgeWeightedDigraph player2) {
		this.player2 = player2;
	}
	
	public Map<String, Integer> getLabelToIndex() {
		return labelToIndex;
	}
	public void setLabelToIndex(Map<String, Integer> labelToIndex) {
		this.labelToIndex = labelToIndex;
	}
	public int getNumberOfLetters() {
		return numberOfLetters;
	}
	public void setNumberOfLetters(int numberOfLetters) {
		this.numberOfLetters = numberOfLetters;
	}
	public int getMinNumOfStatesTransducer() {
		return minNumOfStatesTransducer;
	}
	public void setMinNumOfStatesTransducer(int minNumOfStatesTransducer) {
		this.minNumOfStatesTransducer = minNumOfStatesTransducer;
	}
	public int getMaxNumOfStatesTransducer() {
		return maxNumOfStatesTransducer;
	}
	public void setMaxNumOfStatesTransducer(int maxNumOfStatesTransducer) {
		this.maxNumOfStatesTransducer = maxNumOfStatesTransducer;
	}
	public int getMinNumOfStatesAutomaton() {
		return minNumOfStatesAutomaton;
	}
	public void setMinNumOfStatesAutomaton(int minNumOfStatesAutomaton) {
		this.minNumOfStatesAutomaton = minNumOfStatesAutomaton;
	}
	public int getMaxNumOfStatesAutomaton() {
		return maxNumOfStatesAutomaton;
	}
	public void setMaxNumOfStatesAutomaton(int maxNumOfStatesAutomaton) {
		this.maxNumOfStatesAutomaton = maxNumOfStatesAutomaton;
	}
	public int getMinNumOfInitStatesAutomaton() {
		return minNumOfInitStatesAutomaton;
	}
	public void setMinNumOfInitStatesAutomaton(int minNumOfStatesAutomaton) {
		this.minNumOfInitStatesAutomaton = minNumOfStatesAutomaton;
	}
	public int getMaxNumOfInitStatesAutomaton() {
		return maxNumOfInitStatesAutomaton;
	}
	public void setMaxNumOfInitStatesAutomaton(int maxNumOfStatesAutomaton) {
		this.maxNumOfInitStatesAutomaton = maxNumOfStatesAutomaton;
	}
    public boolean getCloseInitStates() {
        return closeInitStates;
    }
    public void setCloseInitStates(boolean t) {
        closeInitStates = t;
    }

    public void addSymmetry(String symm) {
        symmetries.add(symm);
    }

    public List<String> getSymmetries() {
        return symmetries;
    }

    public void setExplicitChecksUntilLength(int len) {
        explicitChecksUntilLength = len;
    }

    public int getExplicitChecksUntilLength() {
        return explicitChecksUntilLength;
    }

    public boolean getUseRankingFunctions() {
        return useRankingFunctions;
    }
    public void setUseRankingFunctions(boolean t) {
        useRankingFunctions = t;
    }
}
