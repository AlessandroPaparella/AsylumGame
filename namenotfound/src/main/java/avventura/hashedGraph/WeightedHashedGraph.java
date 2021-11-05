package hashedGraph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class WeightedHashedGraph<T, W> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7371846499712012234L;
	private Map<T, Map<T, W>> arcs = new HashMap<T, Map<T,W>>();
	private Set<T> nodes= new HashSet<T>();

	/**
	 * check if the graph is empty
	 * @return true if empty, false otherwise
	 */
	public Boolean isEmpty() {
		Boolean empty=true;
		if(nodes.isEmpty()==false) {
			empty=false;
		}
		return empty;
	}

	/**
	 * Add a node to the graph, if already exists, it will be ignored
	 * @param node the node to add in to the graph
	 */
	public void insNode(T node) {
		if(nodes.add(node)==true)
			arcs.put(node, new HashMap<T, W>());
	}

	/**
	 * Check if a node exists in the graph
	 * @param node the node to check
	 * @return true if the node exists in the graph
	 */
	public Boolean contains(T node) {
		return nodes.contains(node);
	}

	/**
	 * Remove the node and all the arcs related to it
	 * @param node
	 */
	public void removeNode(T node) {
		nodes.remove(node);
		arcs.remove(node);
		for(T k: arcs.keySet()) {
			arcs.get(k).remove(node);
		}
	}

	/**
	 *
	 * @param node
	 * @return the set of adjacents node to the node passed as parameter
	 * @throws hashedGraphException if the graph does not contain the node
	 */
	public Set<T> getAdjacents(T node) throws hashedGraphException {
		if(!this.contains(node)) {
			throw new hashedGraphException("Node doesn't exist!");
		}
		Set<T> adjacents =  arcs.get(node).keySet();
		return adjacents;
	}

	/**
	 *
	 * @param start the node from which the arc starts
	 * @param end the node to which the arc is directed
	 * @return true if an arc from "start" to "end" exists
	 * @throws hashedGraphException if node start and/or end does not exist
	 */
	public Boolean containsArc(T start, T end) throws hashedGraphException {
		if(!this.contains(start) || !this.contains(end)) {
			throw new hashedGraphException("Node doesn't exist!");
		}
		Boolean exists = false;
		if(arcs.get(start).containsKey(end))
			exists = true;
		return exists;
	}

	/**
	 * Add a new arc to the graph, if already exists it will be ignored
	 * @param start the node from which the arc have to start
	 * @param end the node to which the arc has to be directed
	 */
	public void insArc(T start, T end, W weight) {
		if(!this.contains(start)) {
			this.insNode(start);
		}
		if(!this.contains(end)) {
			this.insNode(end);
		}
		arcs.get(start).put(end, weight);
	}

	/**
	 * If exists an arc from "start" to "end" it will be removed from the graph
	 * @param start
	 * @param end
	 */
	public void removeArc(T start, T end) {
		arcs.get(start).remove(end);
	}

	public W readArc(T start, T end) throws hashedGraphException {
		W weight=null;
		if(this.containsArc(start, end)) {
			weight = arcs.get(start).get(end);
		}else {
			throw new hashedGraphException("Arc doesn't exists!");
		}
		return weight;
	}

	public void BFS(Consumer<T> c, T source) throws Exception{
		Queue<T> q = new LinkedList<T>();
		Set<T> visited = new HashSet<T>();
		q.add(source);
		visited.add(source);
		while(!q.isEmpty()) {
			source = q.poll();
			c.accept(source);
			for(T n: this.getAdjacents(source)){
				if(!visited.contains(n)) {
					visited.add(n);
					q.add(n);
				}
			}
		}
	}

}
