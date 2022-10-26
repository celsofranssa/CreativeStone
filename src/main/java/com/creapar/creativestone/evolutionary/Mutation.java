/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.evolutionary;

import com.creapar.creativestone.model.CardCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 *
 * @author Celso
 */
class Mutation implements EvolutionaryOperator<CardCollection> {

    List<String> cards;
    Probability mutationProbability;

    public Mutation(List<String> cards, Probability mutationProbability) {
        this.cards = cards;
        this.mutationProbability = mutationProbability;
    }

    @Override
    public List<CardCollection> apply(List<CardCollection> selectedCandidates, Random rng) {
        double rate;
        String cardsName;
        List<CardCollection> mutationCandidates = new ArrayList<>();

        for (CardCollection selectedCandidate : selectedCandidates) {
            rate = rng.nextDouble();
            if (rate < mutationProbability.doubleValue()) {
                List<String> cardss = selectedCandidate.copyCards();
                cardsName = cards.get(rng.nextInt(cards.size()));
                cardss.set(rng.nextInt(cardss.size()), cardsName);
                mutationCandidates.add(new CardCollection(cardss));

            }
        }
        return mutationCandidates;
    }
}
