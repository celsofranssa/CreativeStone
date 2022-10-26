/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import com.creapar.creativestone.evolutionary.GeneticAlgorithm;
import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.novelty.BayesianNovelty;
import com.creapar.creativestone.novelty.Novelty;
import com.creapar.creativestone.value.SynergyValueModel;
import com.creapar.creativestone.value.ValueModel;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Celso
 */
public class Tester {

    /**
     *
     * @param experiments
     * @param heroName
     * @param recipeName
     * @param generations
     * @param candidateSize
     * @param observeEvolution
     * @return
     */
    public List<Result> geneticTest(
            Integer experiments,
            String heroName, // only cards from specific hero
            String recipeName,
            Integer generations,
            Integer candidateSize,
            Boolean observeEvolution
    ) {


        // containers
        HashMap<Integer, Card> cardIdMap = Tools.getCardIdMap();

        List<Integer> heroCardsIds = Tools.getCardset(heroName);
        // heroCardsIds = tools.fiterCards(heroCardsIds,predicateName);

        List<Integer> neutralCardsIds = Tools.getCardset("neutral");
        // neutralCardsIds = tools.fiterCards(neutralCardsIds, predicateName);

        List<Integer> recipeCardsIds = Tools.getRecipe(heroName, recipeName);

        candidateSize = candidateSize - recipeCardsIds.size();

        Graph<Integer, Edge> synergyGraph = Tools.getGraphSynergy();// tools.getNormalizedIdsGraphs();

        // parser to convert CardCollection to Instance
        Parser parser = new Parser(cardIdMap);

        Integer numberOfAttributes = parser.attributes.size() - 1;

        List<Mean> means = Tools.getMeans();
        List<Variance> variances = Tools.getVariances();

        // rdc parameters
        Novelty novelty = new BayesianNovelty(means, variances, 0.005, numberOfAttributes, parser, cardIdMap);
        ValueModel value = new SynergyValueModel(synergyGraph);

        // genetic parameters
        double neutralMutationRate = 0.05;
        double heroMutationRate = 0.1;
        double crossoverRate = 0.75;
        Integer populationSize = 100;

        GeneticAlgorithm genetic = new GeneticAlgorithm(
                heroCardsIds,
                neutralCardsIds,
                recipeCardsIds,
                candidateSize,
                crossoverRate,
                neutralMutationRate,
                heroMutationRate,
                novelty,
                value,
                cardIdMap
        );
        List<Result> results = new ArrayList<>();
        if (observeEvolution) {
            genetic.observeEvolution();
        }
        for (int i = 0; i < experiments; i++) {
            System.out.println("\nExperiment: " + i);
            genetic.evolve(populationSize, 5, generations);
            results.add(genetic.getResult(cardIdMap, synergyGraph, heroName.toUpperCase()));
            genetic.updateDataset();
        }
        return results;

    }

}
