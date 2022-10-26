package com.creapar.creativestone.synergy;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.predicate.CardPair;
import com.creapar.creativestone.util.Edge;
import com.creapar.creativestone.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Celso on 18/05/2016.
 */
public class Synergy {

    private Map<Integer, List<SynergyCell>> synergyMap;

    public Synergy() {
        Type type = new TypeToken<HashMap<Integer, List<SynergyCell>>>() {
        }.getType();
        Gson gson = Tools.getGson();
        this.synergyMap = gson.fromJson(Tools.getReader("/synergySets/cardsSynergy_v01.json"), type);
    }

    public static void updateSynergy() {
        HashMap<Integer, Card> cardIdMap = Tools.getCardIdMap();
        Map<Integer, List<SynergyCell>> synergyMap = new HashMap<>();
        for (Integer cardid : cardIdMap.keySet()
                ) {
            synergyMap.put(cardid, new ArrayList<>());
        }
        List<CardPair> pairs = Tools.getCardPairs();

        for (Integer uId : cardIdMap.keySet()) {
            for (Integer vId : cardIdMap.keySet()) {
                if (!uId.equals(vId)) {
                    Card uCard = cardIdMap.get(uId);
                    Card vCard = cardIdMap.get(vId);
                    if (uCard.getPlayerClass() == null ||
                            vCard.getPlayerClass() == null ||
                            uCard.getPlayerClass().equals(vCard.getPlayerClass())) {
                        Double weight = evaluateSynergy(uCard, vCard, pairs);
                        if (weight > 0.0) {
                            synergyMap.get(uId).add(new SynergyCell(vId, weight));

                        }

                    }
                }
            }
        }
        try (BufferedWriter writer = Tools.getWriter("/synergySets/cardsSynergy_v01.json")) {
            Tools.getGson().toJson(synergyMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Double evaluateSynergy(Card uCard, Card vCard, List<CardPair> pairs) {
        Double weight = 0.0;
        for (CardPair pair : pairs
                ) {
            if (pair.isAPair(uCard, vCard))
                weight++;
        }
        return weight;
    }

    public Graph<Integer, Edge> getGraphSynergy() {
        Graph<Integer, Edge> graph = new SparseMultigraph<>();//DirectedSparseMultigraph<>();
        addVertex(graph);
        addEdges(graph);
        return graph;
    }

    /**
     * @param graph
     */
    private void addVertex(Graph<Integer, Edge> graph) {
        for (Integer cardId : synergyMap.keySet()
                )
            graph.addVertex(cardId);
    }

    /**
     * @param graph
     */
    private void addEdges(Graph<Integer, Edge> graph) {
        Integer edgeCount = 0;
        for (Integer uId : synergyMap.keySet()) {
            for (SynergyCell cell : synergyMap.get(uId)) {
                graph.addEdge(new Edge(edgeCount++, cell.getWeight()),
                        uId,
                        cell.getId());//,
                //EdgeType.DIRECTED);

            }
        }

    }

}
