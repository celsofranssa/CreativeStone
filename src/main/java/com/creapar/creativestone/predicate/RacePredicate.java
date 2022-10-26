package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 09/05/2016.
 */
public class RacePredicate extends CardPredicate {

    private final String cardRace;

    public RacePredicate(String race) {
        super();
        this.cardRace = race;
    }

    @Override
    public boolean test(Card card) {

        return this.cardRace.equals(card.getRace());
    }
}
