/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Celso
 */
public class CardCollection {

    List<String> cardsNames;
    String name;
    String hero;
    Integer wins;
    Integer losses;
    Double winrate;
    Double efficiency;
    Double surprise;

    public CardCollection() {
        this.cardsNames = new ArrayList<>();
        this.wins = 0;
        this.losses = 0;
        this.winrate = 0.0;
    }

    public CardCollection(List<String> cardsNames) {
        this.cardsNames = cardsNames;
    }

    public void add(String cardName) {
        cardsNames.add(cardName);
    }

    public List<String> copyCards() {
        List<String> cards = new ArrayList<>();
        cardsNames.stream().forEach((cardsName) -> {
            cards.add(cardsName);
        });
        return cards;
    }

    public List<String> getCardsNames() {
        return this.cardsNames;
    }

    public boolean hasAnyOf(List<String> cardsNames) {
        return cardsNames.stream().anyMatch((cardsName) -> (this.cardsNames.contains(cardsName)));
    }

    public void replaceCard(int index, String cardsName) {
        cardsNames.set(index, cardsName);
    }

    public int size() {
        return cardsNames.size();
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }

    public Double getSurprise() {
        return surprise;
    }

    public void setSurprise(Double surprise) {
        this.surprise = surprise;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Double getWinrate() {
        return winrate;
    }

    public void setWinrate(Double winrate) {
        this.winrate = winrate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        cardsNames.stream().forEach((cardsName) -> {
            builder.append(cardsName).append("\n");
        });
        return builder.toString();
    }

    public List<Integer> toInt(Map<Integer, Card> allCardsIds) {
        List<Integer> cardsId = new ArrayList<>();
        Integer id = -10;
        Set<Map.Entry<Integer, Card>> entrySet = allCardsIds.entrySet();
        for (String cardName : cardsNames) {
            for (Map.Entry<Integer, Card> entry : entrySet) {
                if (cardName.equals(entry.getValue().getName())) {
                    id = entry.getKey();
                }
            }
            cardsId.add(id);
        }
        return cardsId;

    }

    public Integer cardCount(String cardName) {
        Integer total = 0;
        for (String card : cardsNames) {
            if (card.equals(cardName)) {
                total++;
            }
        }
        return total;
    }

}
