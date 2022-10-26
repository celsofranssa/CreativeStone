package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;

/**
 * Created by Celso on 01/05/2016.
 */
public class CardPair {

    private CardPredicate p1;
    private CardPredicate p2;

    /**
     * Constructor
     *
     * @param p1 a Predicate<Card>.
     * @param p2 another Predicate<Card>.
     */
    public CardPair(CardPredicate p1, CardPredicate p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * @param c1 a Card to be tested.
     * @param c2 other card to be tested.
     * @return true if the cards is a pair defined
     * in Predicate<Card> p1 and p2.
     */
    public Boolean isAPair(Card c1, Card c2) {
        return p1.test(c1) && p2.test(c2) ||
                p1.test(c2) && p2.test(c1);
    }
}
