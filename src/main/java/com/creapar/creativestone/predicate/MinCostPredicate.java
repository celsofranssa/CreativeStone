package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 14/05/2016.
 */
public class MinCostPredicate extends CardPredicate {

    private final Integer minCost;

    public MinCostPredicate(Integer minCost) {
        super();
        this.minCost = minCost;
    }

    @Override
    public boolean test(Card card) {
        return card.getCost() >= this.minCost;
    }
}
