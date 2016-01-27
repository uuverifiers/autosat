package symmetryencoding.parasym;

import common.bellmanford.DirectedEdge;

public class DEdgeWith4Labels extends DirectedEdge {

	private int label1;
	private int label2;
	private int label3;
	private int label4;

	public DEdgeWith4Labels(int v, int w, int l1, int l2, int l3, int l4) {
		super(v, w);
		// TODO Auto-generated constructor stub
		this.label1 = l1;
		this.label2 = l2;
		this.label3 = l3;
		this.label4 = l4;
	}

	public int getLabel1() {
		return label1;
	}

	public int getLabel2() {
		return label2;
	}

	public int getLabel3() {
		return label3;
	}

	public int getLabel4() {
		return label4;
	}
	
	/**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        return this.from() + "(" + label1 + "-" + label2 + "-" + label3 + "-" + label4 + ")" + this.to();
    }
}
