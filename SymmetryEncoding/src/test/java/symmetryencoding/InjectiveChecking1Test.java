package symmetryencoding;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import symmetryencoding.parasym.condition.InjectiveInput;
import callback.LoggerListener;

import common.bellmanford.EdgeWeightedDigraph;

public class InjectiveChecking1Test {

	@Test
	public void test1(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(6, init, acceptingStates);
		
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 1, 0, 1, 1));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(1, 2, 0, 1, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(2, 3, 0, 1, 3));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 4, 0, 2, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(4, 5, 0, 1, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(5, 4, 0, 1, 1));
		

		
		InjectiveInput checking1 = new InjectiveInput(new LoggerListener());
		
		//[0 0/(2,2)4, 4 0/(1,2)5, 5 0/(1,1)4]
		System.out.println(checking1.check(productGraph));
	}
	
	@Test
	public void test2(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(6, init, acceptingStates);
		
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 1, 0, 1, 1));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(1, 2, 0, 1, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(2, 3, 0, 1, 3));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 4, 0, 2, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(4, 5, 0, 1, 2));
		
		InjectiveInput checking1 = new InjectiveInput(new LoggerListener());
		
		//null
		System.out.println(checking1.check(productGraph));
	}
	
	@Test
	public void test3(){
		int init = 0;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph productGraph = new EdgeWeightedDigraph(5, init, acceptingStates);
		
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 1, 0, 1, 1));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(1, 2, 0, 1, 2));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(2, 3, 0, 1, 3));
		productGraph.addEdge(new InjectiveInput.DEdgeWithInput2Output(0, 4, 0, 1, 2));
				
		InjectiveInput checking1 = new InjectiveInput(new LoggerListener());
		
		//[0 0/(1,2)4]
		System.out.println(checking1.check(productGraph));
	}
}
