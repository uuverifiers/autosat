package symmetryencoding;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import symmetryencoding.parasym.condition.Parikh;
import callback.LoggerListener;

import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

public class ParikhCheckingTest {
	
	@Test
	public void test1(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(4);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(3, 2, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 4, 1, 0, 0));
		
		
		
		//[1->2  1.00, 2->3 -1.00, 3->2 -1.00, 2->3 -1.00, 3->2 -1.00, 2->4  1.00]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
	
	@Test
	public void test2(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(3);
		acceptingStates.add(4);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 4, 1, 0, 0));
		
		
		
		//[1->4  1.00]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
	
	@Test
	public void test3(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(3);
		acceptingStates.add(4);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 4, 0, 0, 0));
		
		
		
		//[]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
	
	@Test
	public void test4(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(3);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(3, 4, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(4, 3, -1, 0, 0));
		
		
		
		//[1->2  1.00, 2->3  1.00, 3->4 -1.00, 4->3 -1.00, 3->4 -1.00]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
	
	@Test
	public void test5(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(5);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(6, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(3, 4, 1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(4, 5, 1, 0, 0));
		
		
		
		//[1->2  1.00, 2->3  1.00, 3->4  1.00, 4->5  1.00]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
	
	@Test
	public void test6(){
		int init = 1;
		Set<Integer> acceptingStates = new HashSet<Integer>();
		acceptingStates.add(3);
		
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5, init, acceptingStates);
		graph.addEdge(new DirectedEdgeWithInputOutput(1, 2, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(2, 3, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(3, 4, -1, 0, 0));
		graph.addEdge(new DirectedEdgeWithInputOutput(4, 3, -1, 0, 0));
		
		
		
		//[1->2 -1.00, 2->3 -1.00, 3->4 -1.00, 4->3 -1.00]
		Parikh parikh = new Parikh(new LoggerListener());
		System.out.println(parikh.check(graph));
	}
}
