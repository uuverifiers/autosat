package verification;

import java.util.ArrayList;
import java.util.List;

public class OldCounterExamples {
	private List<List<List<Integer>>> progressCEs = new ArrayList<List<List<Integer>>>();
	private List<List<List<Integer>>> transitivityCEs = new ArrayList<List<List<Integer>>>();
	private List<List<Integer>> L0A = new ArrayList<List<Integer>>();
	private List<List<Integer>> L0B = new ArrayList<List<Integer>>();
	private List<List<List<Integer>>> L1 = new ArrayList<List<List<Integer>>>();
	private List<List<Integer>> L2 = new ArrayList<List<Integer>>();
	
	public void addProgressCE(List<List<Integer>> ce){
		this.progressCEs.add(ce);
	}
	
	public void addTransitivityCE(List<List<Integer>> ce){
		this.transitivityCEs.add(ce);
	}

	public void addL0A(List<Integer> w){
		this.L0A.add(w);
	}
	
	public void addL0B(List<Integer> w){
		this.L0B.add(w);
	}
	
	public void addL1(List<List<Integer>> ce){
		this.L1.add(ce);
	}
	
	public void addL2(List<Integer> w){
		this.L2.add(w);
	}
	
	public List<List<List<Integer>>> getProgressCEs() {
		return progressCEs;
	}

	public List<List<List<Integer>>> getTransitivityCEs() {
		return transitivityCEs;
	}

	public List<List<Integer>> getL0A() {
		return L0A;
	}

	public List<List<Integer>> getL0B() {
		return L0B;
	}

	public List<List<List<Integer>>> getL1() {
		return L1;
	}

	public List<List<Integer>> getL2() {
		return L2;
	}
	
	
}

