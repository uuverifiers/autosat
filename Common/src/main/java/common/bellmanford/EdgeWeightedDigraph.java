package common.bellmanford;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Stack;


/**
 *  The <tt>EdgeWeightedDigraph</tt> class represents a edge-weighted
 *  digraph of vertices named 0 through <em>V</em> - 1, where each
 *  directed edge is of type {@link DirectedEdge} and has a real-valued weight.
 *  It supports the following two primary operations: add a directed edge
 *  to the digraph and iterate over all of edges incident from a given vertex.
 *  It also provides
 *  methods for returning the number of vertices <em>V</em> and the number
 *  of edges <em>E</em>. Parallel edges and self-loops are permitted.
 *  <p>
 *  This implementation uses an adjacency-lists representation, which 
 *  is a vertex-indexed array of @link{Bag} objects.
 *  All operations take constant time (in the worst case) except
 *  iterating over the edges incident from a given vertex, which takes
 *  time proportional to the number of such edges.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class EdgeWeightedDigraph {
    private final int V;
    private int E;
    private List<DirectedEdge>[] adj;
    private List<DirectedEdge> edges;
    
    private int initState = 0;
    public int getInitState() {
		return initState;
	}

	public Set<Integer> getAcceptingStates() {
		return acceptingStates;
	}

	private Set<Integer> acceptingStates = new HashSet<Integer>();
    
    /**
     * Initializes an empty edge-weighted digraph with <tt>V</tt> vertices and 0 edges.
     * param V the number of vertices
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     */
    public EdgeWeightedDigraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = new ArrayList[V];
        for (int v = 0; v < V; v++){
            adj[v] = new ArrayList<DirectedEdge>();
        }
        
        edges = new ArrayList<DirectedEdge>();
    }
    
    public EdgeWeightedDigraph(int V, int init, Set<Integer> accepting){
    	this(V);
    	this.initState = init;
    	this.acceptingStates = accepting;
    }

    /**
     * Initializes a random edge-weighted digraph with <tt>V</tt> vertices and <em>E</em> edges.
     * param V the number of vertices
     * param E the number of edges
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     * @throws java.lang.IllegalArgumentException if <tt>E</tt> < 0
     */
    public EdgeWeightedDigraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            double weight = Math.round(100 * Math.random()) / 100.0;
            DirectedEdge e = new DirectedEdge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Returns the number of vertices in the edge-weighted digraph.
     * @return the number of vertices in the edge-weighted digraph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted digraph.
     * @return the number of edges in the edge-weighted digraph
     */
    public int E() {
        return E;
    }

    // throw an IndexOutOfBoundsException unless 0 <= v < V
    private void validateVertex(int v) {
        if (v < 0 || v >= V){
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        }
    }

    /**
     * Adds the directed edge <tt>e</tt> to the edge-weighted digraph.
     * @param e the edge
     * @throws java.lang.IndexOutOfBoundsException unless endpoints of edge are between 0 and V-1
     */
    public void addEdge(DirectedEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        edges.add(e);
        E++;
    }


    /**
     * Returns the directed edges incident from vertex <tt>v</tt>.
     * @return the directed edges incident from vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<DirectedEdge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex <tt>v</tt>.
     * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
     * @return the outdegree of vertex <tt>v</tt>
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns all directed edges in the edge-weighted digraph.
     * To iterate over the edges in the edge-weighted graph, use foreach notation:
     * <tt>for (DirectedEdge e : G.edges())</tt>.
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public Iterable<DirectedEdge> edges() {
    	return edges;
    } 

    /**
     * Returns a string representation of the edge-weighted digraph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *   followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (DirectedEdge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        s.append("init: " + initState + NEWLINE);
        s.append("accepting: " + acceptingStates + NEWLINE);
        return s.toString();
    }
    
    public String prettyPrint(String name,
                              Map<Integer, String> inputLabels,
                              Map<Integer, String> outputLabels) {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();

        s.append(name);
        s.append(" {" + NEWLINE);
        s.append("  init: s" + initState + ";" + NEWLINE);

        for (int v = 0; v < V; v++)
            for (DirectedEdge e : adj[v]) {
                s.append("  s" + v + " -> s" + e.to());
                if (e instanceof DirectedEdgeWithInputOutput) {
                    DirectedEdgeWithInputOutput ioe = (DirectedEdgeWithInputOutput)e;
                    s.append(" " +
                             inputLabels.get(ioe.getInput()) + "/" +
                             outputLabels.get(ioe.getOutput()));
                }
                s.append(";" + NEWLINE);
            }

        if (acceptingStates.isEmpty()) {
            s.append("  // no accepting states" + NEWLINE);
        } else {
            s.append("  accepting: ");
            String sep = "";
            for (int state : acceptingStates) {
                s.append(sep + "s" + state);
                sep = ", ";
            }
            s.append(";" + NEWLINE);
        }

        s.append("}" + NEWLINE);

        return s.toString();
    }
    

    /**
     * Return the edges from root to a state in goal. return null if unreachable
     * @param root
     * @param goal
     * @return
     */
    public List<DirectedEdge> DFS(int root, Set<Integer> goal){
		//store the path from root to current Node
		List<DirectedEdge> path = new ArrayList<DirectedEdge>();
		//for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();

		//store nodes waiting to visit
		Stack<DirectedEdge> workingStates = new Stack<DirectedEdge>();
		DirectedEdge dummyEdge = new DirectedEdge(0, root, 0);
		workingStates.push(dummyEdge);

		//for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.push(0);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[V];
		isVisited[root] = true;
		while(!workingStates.isEmpty()){
			DirectedEdge currentEdge = workingStates.pop();
			int currentState = currentEdge.to();
			int depthLevel = depthStack.pop();

			while(depthList.size() > 0){
					int lastDepth = depthList.get(depthList.size() - 1);
					if(lastDepth >= depthLevel){
						//back track a new node, remove nodes not in the path to this node (having depth level greater than or equal its depth level
						depthList.remove(depthList.size() - 1);
						path.remove(path.size() - 1);
					}
					else{
						break;
					}
			}
			
			//add this node and its depth level
			path.add(currentEdge);
			depthList.add(depthLevel);

			//check reachable
			if(goal.contains(currentState)){
				//remove dummy
				path.remove(0);
				return path;
			}

			//add new states to workingState
			for(DirectedEdge edge: adj[currentState]){
				if(!isVisited[edge.to()]){
					workingStates.push(edge);
					depthStack.push(depthLevel+1);
					
					isVisited[edge.to()] = true;
				}
			}
		}
		
		return null;

	}
    
    /**
     * Return the set of state reachable from root
     * @param root
     * @return
     */
    public List<Integer> computeReachableStates(int root){
    	List<Integer> allStates = new ArrayList<Integer>();
    	
		//store nodes waiting to visit
		Stack<Integer> workingStates = new Stack<Integer>();
		workingStates.push(root);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[V];
		isVisited[root] = true;
		while(!workingStates.isEmpty()){
			int currentState = workingStates.pop();
			
			allStates.add(currentState);

			//add new states to workingState
			for(DirectedEdge edge: adj(currentState)){
				if(!isVisited[edge.to()]){
					workingStates.push(edge.to());
					
					isVisited[edge.to()] = true;
				}
			}
		}
		
		return allStates;
	}

	public void setInitState(int initState) {
		this.initState = initState;
	}

	public void setAcceptingStates(Set<Integer> acceptingStates) {
		this.acceptingStates = acceptingStates;
	}
}
