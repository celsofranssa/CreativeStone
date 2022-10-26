package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

import java.util.function.Predicate;

/**
 * Created by Celso on 18/04/2016.
 */
public class MaxCostPredicate implements Predicate<Card> {

    private Integer maxCost;

    public MaxCostPredicate(Integer maxCost) {
        this.maxCost = maxCost;
    }

    @Override
    public boolean test(Card card) {
        return card.getCost() <= maxCost;
    }
}
