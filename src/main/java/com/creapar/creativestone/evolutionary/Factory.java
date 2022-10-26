/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.evolutionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 *
 * @author Celso
 */
public class Factory extends AbstractCandidateFactory<List<Integer>> {

    List<Integer> cardSet;
    Integer candidateSize;

    /**
     *
     * @param cards - list of card's name
     * @param candidateSize - the number of cardSet in a card collection
     */
    public Factory(List<Integer> cards, Integer candidateSize) {
        this.cardSet = cards;
        this.candidateSize = candidateSize;
    }

    /**
     * Generates candidates to the population
     *
     * @param rng
     * @return
     */
    @Override
    public List<Integer> generateRandomCandidate(Random rng) {
        List<Integer> cardCollection = new ArrayList<>();
        for (int i = 0; i < candidateSize; i++) {
            cardCollection.add(cardSet.get(rng.nextInt(cardSet.size())));
        }
        return cardCollection;
    }

}
