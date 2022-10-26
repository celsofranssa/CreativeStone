package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 14/05/2016.
 */
public class RarityPredicate extends CardPredicate {

    private final String cardRarity;

    public RarityPredicate(String cardRarity) {
        super();
        this.cardRarity = cardRarity;
    }

    @Override
    public boolean test(Card card) {

        return this.cardRarity.equals(card.getRarity());
    }
}
