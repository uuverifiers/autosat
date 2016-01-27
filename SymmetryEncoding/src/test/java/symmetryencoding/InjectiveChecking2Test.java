package symmetryencoding;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import common.bellmanford.EdgeWeightedDigraph;

import callback.LoggerListener;
import symmetryencoding.parasym.condition.InjectiveOutput;

public class InjectiveChecking2Test {
	@Test
	public void test1(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(6, init, acceptingStates);
		
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 1, 1, 1, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(1, 2, 1, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(2, 3, 1, 3, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 4, 2, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(4, 5, 1, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(5, 4, 1, 1, 0));
		
		InjectiveOutput checking1 = new InjectiveOutput(new LoggerListener());
		
		//[0 (2,2)/0 4, 4 (1,2)/0 5, 5 (1,1)/0 4]
		System.out.println(checking1.check(productGraph));
	}
	
	@Test
	public void test2(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(6, init, acceptingStates);
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 1, 1, 1, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(1, 2, 1, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(2, 3, 1, 3, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 4, 2, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(4, 5, 1, 2, 0));
		
		
		
		InjectiveOutput checking1 = new InjectiveOutput(new LoggerListener());
		
		//null
		System.out.println(checking1.check(productGraph));
	}
	
	@Test
	public void test3(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(5, init, acceptingStates);
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 1, 1, 1, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(1, 2, 1, 2, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(2, 3, 1, 3, 0));
		productGraph.addEdge(new InjectiveOutput.DEdgeWith2InputsOutput(0, 4, 1, 2, 0));
		
		InjectiveOutput checking1 = new InjectiveOutput(new LoggerListener());
		
		//[0 (1,2)/0 4]
		System.out.println(checking1.check(productGraph));
	}
}
