/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.evolutionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 *
 * @author Celso
 */
public class FactoryToImplement implements CandidateFactory<List<Integer>> {

    List<Integer> cards;
    Integer candidateSize;

    /**
     * Constructor
     *
     * @param cards - a List of Cards represented by Card's number
     * @param candidateSize - size of a CardCollection
     */
    public FactoryToImplement(List<Integer> cards, Integer candidateSize) {
        this.cards = cards;
        this.candidateSize = candidateSize;
    }

    @Override
    public List<List<Integer>> generateInitialPopulation(int populationSize, Random rng) {
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomCandidate(rng));

        }
        return population;
    }

    @Override
    public List<List<Integer>> generateInitialPopulation(int populationSize, Collection<List<Integer>> seedCandidates, Random rng) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> generateRandomCandidate(Random rng) {
        List<Integer> cardCollection = new ArrayList<>();
        for (int i = 0; i < candidateSize; i++) {
            cardCollection.add(cards.get(rng.nextInt(cards.size())));
        }
        return cardCollection;
    }
}
