package symmetryencoding.parasym.condition;

import java.util.ArrayList;
import java.util.List;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

import callback.Listener;

public class InjectiveOutput extends InjectiveInput{

	public InjectiveOutput(Listener listener) {
		super(listener);
	}
	
	@Override
	protected DirectedEdge createDummyEdge(int init){
		return new DEdgeWith2InputsOutput(0, init, 0, 0, 0);
	}
	
	@Override
	protected boolean edgeToFind(DirectedEdge edge){
		DEdgeWith2InputsOutput tempEdge = (DEdgeWith2InputsOutput) edge;
		return (tempEdge.input1 != tempEdge.input2);
	}
	
	/**
	 * If edge1, edge2 have the same output, create new edge with that output and 2 inputs from them
	 * @param edge1
	 * @param edge2
	 * @param numState
	 * @return
	 */
	@Override
	protected void addNewEdge(DirectedEdge edge1, DirectedEdge edge2, int numState, EdgeWeightedDigraph graph){
		DirectedEdgeWithInputOutput tempEdge1 = (DirectedEdgeWithInputOutput) edge1;
		DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
		
		if(tempEdge1.getOutput() == tempEdge2.getOutput()){
			int q1 = edge1.from();
			int q2 = edge2.from();
			int q3 = edge1.to();
			int q4 = edge2.to();
			
			DEdgeWith2InputsOutput newEdge = new DEdgeWith2InputsOutput(hash(q1, q2, numState), hash(q3, q4, numState),
					tempEdge1.getInput(), tempEdge2.getInput(), tempEdge1.getOutput());
			graph.addEdge(newEdge);
		}
	}
	
	@Override
	protected List<Integer>[] convert(List<DirectedEdge> edges){
		if(edges == null){
			return null;
		}
		
		List<Integer>[] result = new List[]{new ArrayList<Integer>(), new ArrayList<Integer>(),new ArrayList<Integer>(),new ArrayList<Integer>()};
		
		for(DirectedEdge edge: edges){
			DEdgeWith2InputsOutput tempEdge = (DEdgeWith2InputsOutput) edge;
			result[0].add(tempEdge.getInput1());
			result[1].add(tempEdge.getOutput());
			
			result[2].add(tempEdge.getInput2());
			result[3].add(tempEdge.getOutput());
		}
		
		return result;
	}
	
	public static class DEdgeWith2InputsOutput extends DirectedEdge{

		private int input1;
		private int input2;
		private int output;
		
		public DEdgeWith2InputsOutput(int v, int w, int input1, int input2, int output) {
			super(v, w);
			// TODO Auto-generated constructor stub
			this.input1 = input1;
			this.input2 = input2;
			this.output = output;
		}

		public int getInput1() {
			return input1;
		}
		
		public int getInput2() {
			return input2;
		}

		public int getOutput() {
			return output;
		}
		

	    /**
	     * Returns a string representation of the directed edge.
	     * @return a string representation of the directed edge
	     */
	    public String toString() {
	        return this.from() + " (" + input1 + "," + input2 + ")/" + output + " " + this.to();
	    }
	}
}
