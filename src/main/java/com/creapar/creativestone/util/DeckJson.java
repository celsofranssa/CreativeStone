/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import java.util.ArrayList;

/**
 *
 * @author Celso
 */
public class DeckJson {

    String hero;

    ArrayList<CardJson> cards;

    public DeckJson() {
        cards = new ArrayList<>();
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public ArrayList<CardJson> getCards() {
        return cards;
    }

    public void setCards(ArrayList<CardJson> cards) {
        this.cards = cards;
    }

}
