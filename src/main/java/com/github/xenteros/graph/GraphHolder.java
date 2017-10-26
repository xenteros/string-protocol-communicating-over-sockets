package com.github.xenteros.graph;

import com.github.xenteros.exception.VertexNotAllowedException;
import com.github.xenteros.exception.VertexNotFoundException;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.util.stream.Collectors;

public class GraphHolder {

    private DirectedWeightedPseudograph<String, DefaultWeightedEdge> graph;

    public GraphHolder() {
        graph = new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
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
            validateNodes(from, to);

            DefaultWeightedEdge edge = graph.addEdge(from, to);
            graph.setEdgeWeight(edge, weight);
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
            validateNodes(from, to);
            if (!graph.containsEdge(from, to)) {
                return;
            }
            graph.removeEdge(from, to);
        }
    }

    public int shortestPath(String from, String to) {
        synchronized (graph) {
            validateNodes(from, to);

            double result = new DijkstraShortestPath<>(graph).getPathWeight(from, to);
            return result < Integer.MAX_VALUE ? (int)result : Integer.MAX_VALUE;    //this cast is safe
        }
    }

    public String findNodesCloserThan(int weight, final String node) {
        synchronized (graph) {
            if (!graph.containsVertex(node)) {
                throw new VertexNotFoundException();
            }
            BellmanFordShortestPath<String, DefaultWeightedEdge> bellmanFord = new BellmanFordShortestPath<>(graph);
            ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> paths = bellmanFord.getPaths(node);
            return graph.vertexSet().stream()
                    .filter(v -> paths.getWeight(v) < weight)
                    .filter(v -> !v.equals(node))
                    .sorted()
                    .collect(Collectors.joining(","));

        }
    }

    private void validateNodes(String from, String to) {
        if(!graph.containsVertex(from) || !graph.containsVertex(to)) {
            throw new VertexNotFoundException();
        }
    }
}
