package symmetryencoding.parasym.condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.sat4j.specs.ContradictionException;

import common.bellmanford.DirectedEdge;
import common.bellmanford.DirectedEdgeWithInputOutput;
import common.bellmanford.EdgeWeightedDigraph;

import symmetryencoding.cegeneration.UpdateFromCounterExample;
import symmetryencoding.parasym.DEdgeWith4Labels;
import symmetryencoding.transducer.TransducerGenerator;
import callback.Listener;

public class Permutative implements ConditionChecking {

	private Set<Integer> X;
	private Set<Integer> Y;
	private Set<Integer> Z;
	
	private EdgeWeightedDigraph satTransducer;
	private int numStates;
	
	private EdgeWeightedDigraph productTransducer;
	private Set<Integer> Q0;
	
	private Listener listener;
	
	public Permutative(Listener listener) {
		this.listener = listener;
	}
	
	public boolean isSatisfied(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates, UpdateFromCounterExample updateSAT) throws ContradictionException{
		List<Integer>[] counterExample = check(model, numStates, numLetters, acceptingStates);
		
		if(counterExample != null){
			updateSAT.acceptAtMostOne(counterExample[0], counterExample[1], counterExample[2], counterExample[3]);
		}
		
		return (counterExample == null);
		
	}
	
	public List<Integer>[] check(int[] model, int numStates, int numLetters, Set<Integer> acceptingStates){
		this.numStates = numStates;
		satTransducer = TransducerGenerator.fromSATModel(model, numStates, numLetters, acceptingStates);
		
		productTransducer = buildProduct();
		
		//precompute
		X = computeX();
		Y = computeY();
		Z = computeZ();
		
		//check for 3 cases
		for(int inCase = 1; inCase <= 3; inCase ++){
			List<DirectedEdge> edges = DFS(inCase);
			
			if(edges != null){
				listener.inform("Permutative condition case " + inCase);
				listener.inform(String.valueOf(edges));
				return convert(edges);
			}
		}
		
		listener.inform("Permutative condition satisfied");
		return null;
	}
	
	/**
	 * Check whether the edge is satisified the condition according to the paper
	 * @param edge
	 * @param isFromQ1: either from q1 or q1'
	 * @param inCase: either case 1 or 2 or 3
	 * @return
	 */
	private boolean isSatisfied(DirectedEdgeWithInputOutput edge, int inCase, boolean isFromQ1){
		if(inCase == 1 && isFromQ1){
			return edge.getInput() == 1 && edge.getOutput() == 0;
		}
		else if(inCase == 1 && !isFromQ1){
			return edge.getInput() == 0 && edge.getOutput() == 1;
		}
		else if(inCase == 3 && isFromQ1){
			return edge.getInput() == 0 && edge.getOutput() == 1;
		}
		else if(inCase == 3 && !isFromQ1){
			return edge.getInput() == 1 && edge.getOutput() == 0;
		}
		else if(inCase == 2 && isFromQ1){
			return edge.getInput() == 1 && edge.getOutput() == 1;
		}
		
		throw new IllegalArgumentException("Wrong argument");
	}
	
	private boolean isSatisfied(int state, boolean isQ1, int inCase, Set<Integer> X, Set<Integer> Y, Set<Integer> Z){
		if(inCase == 1 && isQ1){
			return X.contains(state);
		}
		else if(inCase == 1 && !isQ1){
			return Z.contains(state);
		}
		else if(inCase == 3 && isQ1){
			return Z.contains(state);
		}
		else if(inCase == 3 && !isQ1){
			return X.contains(state);
		}
		else if(inCase == 2 && isQ1){
			return Y.contains(state);
		}
		
		throw new IllegalArgumentException("Wrong argument");
	}
	private List<DirectedEdge> DFS(int inCase){
		//store the path from root to current Node
		List<DirectedEdge> path = new ArrayList<DirectedEdge>();
		//for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();

		//store nodes waiting to visit
		Stack<DirectedEdge> workingStates = new Stack<DirectedEdge>();
		DirectedEdge dummyEdge = new DirectedEdge(0, productTransducer.getInitState());
		workingStates.push(dummyEdge);

		//for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.push(0);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[productTransducer.V()];
		isVisited[productTransducer.getInitState()] = true;
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
			int q1 = getq1(currentState, numStates);
			int q2 = getq2(currentState, numStates);
			if(isSatisfied(q1, true, inCase, X, Y, Z)){
				for(DirectedEdge edgeFromQ1: satTransducer.adj(q1)){
					DirectedEdgeWithInputOutput tempEdgeFromQ1 = (DirectedEdgeWithInputOutput) edgeFromQ1;
					if(isSatisfied(tempEdgeFromQ1, inCase, true)){
						
						for(DirectedEdge edgeFromQ2: satTransducer.adj(q2)){
							DirectedEdgeWithInputOutput tempEdgeFromQ2 = (DirectedEdgeWithInputOutput) edgeFromQ2;
							int q3 = tempEdgeFromQ1.to();
							int q4 = tempEdgeFromQ2.to();
							int i = tempEdgeFromQ2.getInput();
							
							//for case 2, just make sure that (q3, q4) can reach acceptance state
							if(inCase == 2){
								if(tempEdgeFromQ2.getInput() != tempEdgeFromQ2.getOutput()){
									List<DirectedEdge> pathToAccept = productTransducer.DFS(hash(q3, q4, numStates), productTransducer.getAcceptingStates());
									if(pathToAccept != null){
										DirectedEdge edgeToQ3Q4 = new DEdgeWith4Labels(hash(q1, q2, numStates), hash(q3, q4, numStates), tempEdgeFromQ1.getInput(), tempEdgeFromQ1.getOutput(),
												tempEdgeFromQ2.getInput(), tempEdgeFromQ2.getOutput());
										path.add(edgeToQ3Q4);
										
										path.addAll(pathToAccept);
										
										//remove dummy
										path.remove(0);
										return path;
									}
								}
							}
							else{
								List<DirectedEdge> edgesFromQ3Q4 = secondDFS(hash(q3, q4, numStates), i, inCase);
								if(edgesFromQ3Q4 != null){
									DirectedEdge edgeToQ3Q4 = new DEdgeWith4Labels(hash(q1, q2, numStates), hash(q3, q4, numStates), tempEdgeFromQ1.getInput(), tempEdgeFromQ1.getOutput(),
																						tempEdgeFromQ2.getInput(), tempEdgeFromQ2.getOutput());
									path.add(edgeToQ3Q4);
									
									path.addAll(edgesFromQ3Q4);
									
									//remove dummy
									path.remove(0);
									
									return path;
								}
							}
						}
					}
				}
			}

			//add new states to workingState
			for(DirectedEdge edge: productTransducer.adj(currentState)){
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
	 * 
	 * @param productTransducer
	 * @param satTransducer
	 * @param numStates
	 * @param init
	 * @param i the input of transition from q2 to q4
	 * @param inCase
	 * @return
	 */
	private List<DirectedEdge> secondDFS(int init, int i, int inCase){
		//store the path from root to current Node
		List<DirectedEdge> path = new ArrayList<DirectedEdge>();
		//for each node in path, store its depth level
		List<Integer> depthList = new ArrayList<Integer>();

		//store nodes waiting to visit
		Stack<DirectedEdge> workingStates = new Stack<DirectedEdge>();
		DirectedEdge dummyEdge = new DirectedEdge(0, init);
		workingStates.push(dummyEdge);

		//for each node in workingStates, store its depth level
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.push(0);

		//check whether a node is visited or not
		boolean [] isVisited = new boolean[productTransducer.V()];
		isVisited[init] = true;
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
			int q1Prime = getq1(currentState, numStates);
			int q2Prime = getq2(currentState, numStates);
			if(isSatisfied(q1Prime, false, inCase, X, Y, Z)){
				for(DirectedEdge edgeFromQ1: satTransducer.adj(q1Prime)){
					DirectedEdgeWithInputOutput tempEdgeFromQ1 = (DirectedEdgeWithInputOutput) edgeFromQ1;
					if(isSatisfied(tempEdgeFromQ1, inCase, false)){
						for(DirectedEdge edgeFromQ2: satTransducer.adj(q2Prime)){
							DirectedEdgeWithInputOutput tempEdgeFromQ2 = (DirectedEdgeWithInputOutput) edgeFromQ2;
							int q3Prime = tempEdgeFromQ1.to();
							int q4Prime = tempEdgeFromQ2.to();
							int jPrime = tempEdgeFromQ2.getOutput();
							
							if(jPrime != i && Q0.contains(q3Prime)){
								List<DirectedEdge> pathToAccept = productTransducer.DFS(hash(q3Prime, q4Prime, numStates), productTransducer.getAcceptingStates());
								if(pathToAccept != null){
									DirectedEdge edgeToQ3PrimeQ4Prime = new DEdgeWith4Labels(hash(q1Prime, q2Prime, numStates), hash(q3Prime, q4Prime, numStates), tempEdgeFromQ1.getInput(), tempEdgeFromQ1.getOutput(),
											tempEdgeFromQ2.getInput(), tempEdgeFromQ2.getOutput());
									path.add(edgeToQ3PrimeQ4Prime);
									path.addAll(pathToAccept);
									
									//remove dummy
									path.remove(0);
									return path;
								}
							}
						}
					}
				}
			}

			//add new states to workingState
			for(DirectedEdge edge: productTransducer.adj(currentState)){
				if(!isVisited[edge.to()]){
					workingStates.push(edge);
					depthStack.push(depthLevel+1);
					
					isVisited[edge.to()] = true;
				}
			}
		}
		
		return null;
		
	}
	
	private Set<Integer> computeX(){
		Set<Integer> result = new HashSet<Integer>();
		for(int i = 0; i < satTransducer.V(); i++){
			for(DirectedEdge edge: satTransducer.adj(i)){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				if(tempEdge.getInput() == 1 && tempEdge.getOutput() == 0){
					result.add(i);
					break;
				}
			}
		}
		
		return result;
	}
	
	private Set<Integer> computeY(){
		Set<Integer> result = new HashSet<Integer>();
		for(int i = 0; i < satTransducer.V(); i++){
			for(DirectedEdge edge: satTransducer.adj(i)){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				if(tempEdge.getInput() == 1 && tempEdge.getOutput() == 1){
					result.add(i);
					
					break;
				}
				
			}
		}
		
		return result;
	}
	
	private Set<Integer> computeZ(){
		Set<Integer> result = new HashSet<Integer>();
		for(int i = 0; i < satTransducer.V(); i++){
			for(DirectedEdge edge: satTransducer.adj(i)){
				DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
				if(tempEdge.getInput() == 0 && tempEdge.getOutput() == 1){
					result.add(i);
					break;
				}
			}
		}
		
		return result;
	}
	
	private EdgeWeightedDigraph buildProduct(){
		//build transducer00 with 0/0 transitions
		EdgeWeightedDigraph transducer00 = new EdgeWeightedDigraph(satTransducer.V());
		for(DirectedEdge edge: satTransducer.edges()){
			DirectedEdgeWithInputOutput tempEdge = (DirectedEdgeWithInputOutput) edge;
			if(isZeroTrans(tempEdge)){
				transducer00.addEdge(edge);
			}
		}
		
		//compute Q0: reachable state + reache accepting state by 01Trans
		Set<Integer> acceptingInTransducer = satTransducer.getAcceptingStates();
		
		List<Integer> reachableState00 = transducer00.computeReachableStates(0);
		Q0 = new HashSet<Integer>();
		Set<Integer> acceptingInQ0 = new HashSet<Integer>();
		for(Integer state: reachableState00){
			if(GraphUltililty.isReachableByO1Trans(state, satTransducer.getAcceptingStates(), satTransducer)){
				Q0.add(state);
				if(acceptingInTransducer.contains(state)){
					acceptingInQ0.add(state);
				}
			}
		}

		
		//we  need to declare V*V because we want to keep the index
		int numStatesProduct = satTransducer.V() * satTransducer.V();
		int init = 0;
		Set<Integer> acceptingInProduct = new HashSet<Integer>();
		for(int q1: acceptingInQ0){
			for(int q2: acceptingInTransducer){
				acceptingInProduct.add(hash(q1, q2, numStates));
			}
		}
		
		EdgeWeightedDigraph productTransducer = new EdgeWeightedDigraph(numStatesProduct, init, acceptingInProduct);
		for(DirectedEdge edge1: transducer00.edges()){
			DirectedEdgeWithInputOutput tempEdge1 = (DirectedEdgeWithInputOutput) edge1;
			
			for(DirectedEdge edge2: satTransducer.edges()){
				DirectedEdgeWithInputOutput tempEdge2 = (DirectedEdgeWithInputOutput) edge2;
				if(Q0.contains(tempEdge1.from()) && Q0.contains(tempEdge1.to())){
					DEdgeWith4Labels newTrans = new DEdgeWith4Labels(hash(tempEdge1.from(), tempEdge2.from(), numStates), hash(tempEdge1.to(), tempEdge2.to(), numStates),
									tempEdge1.getInput(), tempEdge1.getOutput(), tempEdge2.getInput(), tempEdge2.getOutput());
					productTransducer.addEdge(newTrans);
				}
			}
		}
		
		return productTransducer;
	}
	
	/**
	 * q1 from 0 to numQ1 - 1
	 * @param q1
	 * @param q2
	 * @param numStates
	 * @return
	 */
	private int hash(int q1, int q2, int numStates){
		return q1 * numStates + q2;
	}
	
	private int getq1(int q, int numStates){
		return q / numStates;
	}
	
	private int getq2(int q, int numStates){
		return q % numStates;
	}
	
	private boolean isZeroTrans(DirectedEdgeWithInputOutput edge){
		return edge.getInput() == 0 && edge.getOutput() == 0;
	}
	
	/**
	 * Convert from edges to labels
	 * @param edges
	 * @return
	 */
	private List<Integer>[] convert(List<DirectedEdge> edges){
		if(edges == null){
			return null;
		}
		
		List<Integer>[] result = new List[]{new ArrayList<Integer>(), new ArrayList<Integer>(),new ArrayList<Integer>(),new ArrayList<Integer>()};
		
		for(DirectedEdge edge: edges){
			DEdgeWith4Labels tempEdge = (DEdgeWith4Labels) edge;
			result[0].add(tempEdge.getLabel1());
			result[1].add(tempEdge.getLabel2());
			
			result[2].add(tempEdge.getLabel3());
			result[3].add(tempEdge.getLabel4());
		}
		
		return result;
	}
}
