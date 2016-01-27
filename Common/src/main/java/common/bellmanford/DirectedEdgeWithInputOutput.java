package common.bellmanford;


public class DirectedEdgeWithInputOutput extends DirectedEdge{

	private int input;
	private int output;
	
	public DirectedEdgeWithInputOutput(int v, int w, double weight, int input, int output) {
		super(v, w, weight);
		// TODO Auto-generated constructor stub
		this.input = input;
		this.output = output;
	}
	
	public DirectedEdgeWithInputOutput(int v, int w, int input, int output) {
		super(v, w);
		// TODO Auto-generated constructor stub
		this.input = input;
		this.output = output;
	}
	
	public DirectedEdgeWithInputOutput(DirectedEdgeWithInputOutput copyEdge){
		this(copyEdge.from(), copyEdge.to(), copyEdge.input, copyEdge.output);
	}

	public int getInput() {
		return input;
	}

	public int getOutput() {
		return output;
	}
	
    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        return this.from() + " " + input + "/" + output + " " + this.to();
    }

}
