/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.predicate.CardPredicate;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author cel
 */
public class CardsParing {

    Predicate<Card> p1;
    Predicate<Card> p2;

    List<String> cards;
    Integer cardIdA;
    Integer cardIdB;

    public CardsParing(List<String> cards) {
        this.cards = cards;
    }

    public CardsParing(Integer cardIdA, Integer cardIdB) {
        this.cardIdA = cardIdA;
        this.cardIdB = cardIdB;
    }

    /**
     * Returns a list of cards which participate of this card paring.
     *
     * @return a list containing the cards.
     */
    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }

    public Boolean isTemplate(CardPredicate p1, CardPredicate p2) {
        Map<String, Card> cardNameMap = Tools.getCardNameMap();
        return p1.test(cardNameMap.get(cards.get(0))) && p2.test(cardNameMap.get(cards.get(1))) ||
                p1.test(cardNameMap.get(cards.get(1))) && p2.test(cardNameMap.get(cards.get(0)));
    }

    public Boolean isAPair(Card c1, Card c2) {
        return p1.test(c1) && p2.test(c2) ||
                p1.test(c2) && p2.test(c1);
    }


}
