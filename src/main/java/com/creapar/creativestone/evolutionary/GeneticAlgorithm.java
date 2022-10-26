/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.evolutionary;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.novelty.Novelty;
import com.creapar.creativestone.util.Edge;
import com.creapar.creativestone.util.Result;
import com.creapar.creativestone.value.ValueModel;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.Replacement;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import java.util.*;

/**
 * Genetic Algorithm
 *
 * @author Celso - Crapar.com
 */
public class GeneticAlgorithm {

    private EvolutionEngine<List<Integer>> engine;
    private List<Integer> geneticResult;
    private Novelty noveltyModel;
    private ValueModel valueModel;
    private List<Integer> recipeCardsIds;


    /**
     *
     * @param heroCardsIds - hero's cards identifiers
     * @param neutralCardsIds - neutrals cards identifiers
     * @param candidateSize - size of cardCollection
     * @param crossoverRate - crossover rate
     * @param neutralMutationRate - neutral mutation rate
     * @param heroMutationRate - hero mutation rate
     * @param noveltyModel - novelty model
     * @param valueModel - value model
     * @param cardsMap
     */
    public GeneticAlgorithm(
            List<Integer> heroCardsIds, // hero cards identifiers 
            List<Integer> neutralCardsIds, // neutrais cards identifiers
            List<Integer> recipeCardsIds, // recipe cards identifiers
            Integer candidateSize, // size of cardCollection
            Double crossoverRate, // 
            Double neutralMutationRate, // mutation rate
            Double heroMutationRate, // hero mutation
            Novelty noveltyModel, // noveltyModel model
            ValueModel valueModel, // valueModel model
            Map<Integer, Card> cardsMap
    ) {

        // candidate factories
        CandidateFactory<List<Integer>> neutralFactory = new Factory(neutralCardsIds, candidateSize);
        CandidateFactory<List<Integer>> heroFactory = new Factory(heroCardsIds, candidateSize);
        List<Integer> allCardIds = union(neutralCardsIds, heroCardsIds);
        CandidateFactory<List<Integer>> allFactory = new Factory(allCardIds, candidateSize);

        // operators
        List<EvolutionaryOperator<List<Integer>>> operators = new LinkedList<>();
        operators.add(new ListCrossover<>(2, new Probability(crossoverRate)));
        //operators.add(new Replacement<>(neutralFactory, new Probability(neutralMutationRate)));
        operators.add(new Replacement<>(heroFactory, new Probability(heroMutationRate)));
        EvolutionaryOperator<List<Integer>> pipeline = new EvolutionPipeline<>(operators);

        // fitness evaluator
        this.noveltyModel = noveltyModel;
        this.valueModel = valueModel;
        //  FitnessEvaluator<List<Integer>> fitnessEvaluator = new ComboEvaluator(noveltyModel, valueModel, cardsMap);
        FitnessEvaluator<List<Integer>> fitnessEvaluator = new Evaluator(noveltyModel, valueModel, recipeCardsIds);

        // selection strategy
        SelectionStrategy<Object> selection = new RouletteWheelSelection();

        // random number generator
        Random rng = new MersenneTwisterRNG();

        engine = new GenerationalEvolutionEngine<>(allFactory,
                pipeline,
                fitnessEvaluator,
                selection,
                rng);
        this.recipeCardsIds = recipeCardsIds;
        this.geneticResult = null;
    }

    /**
     * Evolves the population until the best geneticResult is archive
     *
     * @param populationSize
     * @param eliteCount - number of individuals directly copied to next
     * generation
     * @param generations - generation count to stagnation
     */
    public void evolve(Integer populationSize, Integer eliteCount, Integer generations) {
       geneticResult = engine.evolve(populationSize, eliteCount, new Stagnation(generations, true));
        geneticResult = union(geneticResult, recipeCardsIds);
       // geneticResult = engine.evolve(populationSize, eliteCount, new GenerationCount(10000));

    }

    /**
     *
     * Formats and returns a Result from genetic evolution.
     *
     * @param cardsMap
     * @param synergyGraph
     * @param heroName
     * @return
     */
    public Result getResult(Map<Integer, Card> cardsMap, Graph<Integer, Edge> synergyGraph, String heroName) {

        List<String> cardsNames = getCardsNames(cardsMap);
        Result result = new Result();
        result.setHeroName(heroName);
        result.setCardsNames(cardsNames);
        result.setNovelty(noveltyModel.getNovelty(geneticResult));
        result.setValue(valueModel.getValue(geneticResult));
        result.setTotalManaCost(manaCost(geneticResult, cardsMap));
        result.setDescription(getDescription(cardsMap, synergyGraph));
        return result;
    }

    private Double manaCost(List<Integer> candidate, Map<Integer, Card> cards) {
        int m = 0;
        return 1.0 * candidate.stream().map((cardId) -> cards.get(cardId).getCost()).reduce(m, Integer::sum);
    }

    /**
     * Returns cardsNames
     *
     * @param cardsMap
     * @return
     */
    private List<String> getCardsNames(Map<Integer, Card> cardsMap) {
        List<String> cardsNames = new ArrayList<>();
        geneticResult.stream().forEach((cardId) -> {
            cardsNames.add(cardsMap.get(cardId).getName());
        });
        return cardsNames;
    }

    /**
     *
     * @param cardsMap
     * @param synergyGraph
     * @return
     */
    private String getDescription(Map<Integer, Card> cardsMap, Graph<Integer, Edge> synergyGraph) {
        StringBuilder builder = new StringBuilder();

        //Deck's cards
        Map<String, Integer> cardCollection = translateInCardCollection(cardsMap);

        // Deck's moves
        Graph<Integer, Integer> graph = createDeckGraph(synergyGraph);
        Map<String, Set<String>> moves = createDeckMoves(cardsMap, graph);

        cardCollection.keySet().stream().forEach((cardName) -> {
            builder.append(cardCollection.get(cardName)).append("x ").append(cardName).append("\n");
        });

        builder.append("\nPossible Combinations:\n\n");

        moves.keySet().stream().forEach((cardName) -> {
            builder.append(cardName).append(": ").append(moves.get(cardName)).append("\n");
        });

        return builder.toString();
    }

    /**
     *
     * @param synergyGraph
     * @return
     */
    private Graph createDeckGraph(Graph<Integer, Edge> synergyGraph) {

        Graph<Integer, Integer> graph = new DirectedSparseMultigraph<>();
        geneticResult.stream().forEach((cardId) -> {
            graph.addVertex(cardId);
        });
        geneticResult.stream().forEach((cardId) -> {
            Collection<Integer> synergyCards = synergyGraph.getSuccessors(cardId);
            synergyCards.stream().filter((synergyCard) -> (graph.containsVertex(synergyCard))).forEach((synergyCard) -> {
                graph.addEdge(graph.getEdgeCount() + 1, cardId, synergyCard);
            });
        });
        return graph;
    }

    /**
     * Defines some possible moves for the Deck
     *
     * @param cardsMap
     * @param graph
     * @return
     */
    private Map<String, Set<String>> createDeckMoves(Map<Integer, Card> cardsMap, Graph<Integer, Integer> graph) {

        String cardName;
        Set<String> synergyCards;
        Map<String, Set<String>> combos = new HashMap<>();
        for (Integer vertex : graph.getVertices()) {
            cardName = cardsMap.get(vertex).getName();
            synergyCards = getSynergyCards(graph.getNeighbors(vertex), cardsMap);
            if (!combos.containsKey(cardName)) {
                combos.put(cardName, synergyCards);
            } else {
                combos.get(cardName).addAll(synergyCards);
            }

        }

        return combos;
    }

    /**
     * Gets the name of the synergy cards from id
     *
     * @param cardsIds
     * @param cardsMap
     * @return
     */
    private Set<String> getSynergyCards(Collection<Integer> cardsIds, Map<Integer, Card> cardsMap) {
        Set<String> synergyCards = new HashSet<>();
        cardsIds.stream().forEach((cardId) -> {
            synergyCards.add(cardsMap.get(cardId).getName());
        });

        return synergyCards;
    }

    /**
     * Translate the Card's Id into a Card's name
     *
     * @param cardsMap
     * @return
     */
    private Map<String, Integer> translateInCardCollection(Map<Integer, Card> cardsMap) {
        Map<String, Integer> cardCollection = new HashMap<>();
        String cardName;
        for (Integer integer : geneticResult) {
            cardName = cardsMap.get(integer).getName();
            cardCollection.put(cardName, cardCollection.getOrDefault(cardName, 0) + 1);
        }
        return cardCollection;
    }

    /**
     * Show evolution history
     */
    public void observeEvolution() {
        engine.addEvolutionObserver((PopulationData<? extends List<Integer>> data) -> {

            System.out.println("\nGeneration: " + data.getGenerationNumber()
                    + "\nBest Fitness: " + data.getBestCandidateFitness()
                    + "\nAverage Fitness: " + data.getMeanFitness()
            );

        });

    }

    /**
     * Update de Knowledge from the new artifact in geneticResul
     */
    public void updateDataset() {
        noveltyModel.updateNovelty(geneticResult);
    }

    private List<Integer> union(List<Integer> neutralCardsIds, List<Integer> heroCardsIds) {
        List<Integer> union = new ArrayList<>();
        union.addAll(heroCardsIds);
        union.addAll(neutralCardsIds);
        return union;
    }

}
