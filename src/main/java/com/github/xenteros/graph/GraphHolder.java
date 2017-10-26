package com.github.xenteros.graph;

import com.github.xenteros.exception.EdgeNotAllowedException;
import com.github.xenteros.exception.VertexNotAllowedException;
import com.github.xenteros.exception.VertexNotFoundException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class GraphHolder {

    private Graph<String, Integer> graph;

    public GraphHolder() {
        graph = new DefaultDirectedWeightedGraph<>(Integer.class);
    }

    public void addNode(String node) {
        synchronized (graph) {
            if (graph.containsVertex(node)) {
                throw new VertexNotAllowedException();
            }
            graph.addVertex(node);
        }
    }

    public void addEdge(String from, String to, int weight) {
        synchronized (graph) {
            if (!graph.containsVertex(from) || !graph.containsVertex(to)) {
                throw new EdgeNotAllowedException();
            }
            graph.addEdge(from, to, weight);
        }
    }

    public void removeNode(String node) {
        synchronized (graph) {
            if (!graph.containsVertex(node)) {
                throw new VertexNotFoundException();
            }
            graph.removeVertex(node);
        }
    }

    public void removeEdge(String from, String to) {
        synchronized (graph) {
            if(!graph.containsVertex(from) || !graph.containsVertex(to)) {
                throw new VertexNotFoundException();
            }
            if (!graph.containsEdge(from, to)) {
                return;
            }
            graph.removeEdge(from, to);
        }
    }

}
