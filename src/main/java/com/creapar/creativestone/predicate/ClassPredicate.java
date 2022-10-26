package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 01/05/2016.
 */
public class ClassPredicate extends CardPredicate {


    private final String playerClass;

    public ClassPredicate(String playerClass) {
        super();
        this.playerClass = playerClass;
    }


    /**
     * Returns true if Card card has the same playerClass of this predicate
     *
     * @param card a card to test
     * @return true if card has or not the desired type
     */
    @Override
    public boolean test(Card card) {
        return this.playerClass.equals(card.getPlayerClass());
    }
}