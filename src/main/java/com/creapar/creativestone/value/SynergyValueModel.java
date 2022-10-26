/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.value;

import com.creapar.creativestone.util.Edge;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

import java.util.Collection;
import java.util.List;

/**
 * @author Celso
 */
public class SynergyValueModel implements ValueModel<List<Integer>> {

    private Graph<Integer, Edge> graphSynergy;

    public SynergyValueModel(Graph<Integer, Edge> graphSynergy) {
        this.graphSynergy = graphSynergy;
    }

    @Override
    public Double getValue(List<Integer> artifact) {
        // create new graph
        Graph<Integer, Edge> graph = new SparseMultigraph<>();//DirectedSparseMultigraph<>();

        // add vertex
        addVertex(graph, artifact);
        // add edges
        addEdges(graph, artifact);


        Double kc = kc(graph);
        Double p = weightDensity(graph);//density(graph);//weightDensity(graph);
        return 0.5 * (kc + p - penalty(kc, p));
    }

    /**
     * Penalty function
     *
     * @param a
     * @param b
     * @return
     */
    private double penalty(Double a, Double b) {
        Double s = a + b;
        Double d = Math.abs(a - b);
        return s * (1 - Math.exp(-1.0 * d));
    }

    private void addVertex(Graph<Integer, Edge> graph, List<Integer> artifact) {
        artifact.stream().forEach((cardId) -> {
            graph.addVertex(cardId);
        });

    }

    private void addEdges(Graph<Integer, Edge> graph, List<Integer> artifact) {
        Integer edgeIndex = 0;
        for (Integer uId : artifact) {
            for (Integer vId : graphSynergy.getSuccessors(uId)) {
                if (graph.containsVertex(vId)) {
                    Double weight = graphSynergy.findEdge(uId, vId).getWeight();
                    graph.addEdge(new Edge(edgeIndex++, weight), uId, vId);//, EdgeType.DIRECTED);
                }
            }
        }
    }

    private Double kc(Graph<Integer, Edge> graph) {
        DijkstraShortestPath<Integer, Edge> djk = new DijkstraShortestPath(graph);
        Collection<Integer> vertices = graph.getVertices();
        int vertexCount = graph.getVertexCount();
        int paths = 0;
        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (v1 != v2) {
                    if (djk.getDistance(v1, v2) != null) {
                        paths++;
                    }
                }
            }
        }
        return (paths) / (1.0 * vertexCount * (vertexCount - 1));
    }

    /**
     * Graph density
     *
     * @return
     */
    private Double density(Graph<Integer, Edge> graph) {
        int e = graph.getEdgeCount();
        int n = graph.getVertexCount();
        return 1.0 * e / (n * (n - 1));
    }

    /**
     * Graph density
     *
     * @return
     */
    private Double weightDensity(Graph<Integer, Edge> graph) {
        Double totalWeight = 0.0;
        for (Edge edge : graph.getEdges()
                ) {
            totalWeight += edge.getWeight();
        }
        Integer n = graph.getVertexCount();
        Double density = 2 * totalWeight / (n * (n - 1));
        return 1.0 - Math.exp(-1.0 * density);
    }

}
