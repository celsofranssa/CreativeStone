package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 19/04/2016.
 */
public class TypePredicate extends CardPredicate {


    private final String cardType;

    public TypePredicate(String cardType) {
        super();
        this.cardType = cardType;
    }

    /**
     * Returns true if Card card has the same type of this predicate
     *
     * @param card a card to test
     * @return if card has or not the desired type
     */
    @Override
    public boolean test(Card card) {
        return this.cardType.equals(card.getType());
    }
}
