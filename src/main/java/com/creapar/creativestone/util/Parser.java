/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import com.creapar.creativestone.data.Instance;
import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.model.CardAbility;
import com.creapar.creativestone.model.CardCollection;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Celso
 */
public class Parser {

    HashMap<String, Integer> attributes;

    /**
     * Constructor
     *
     * @param cards
     */
    public Parser(HashMap<Integer, Card> cards) {
        Card card;
        String attribute;
        int index = 0;
        attributes = new HashMap<>();
        attributes.put("attack", index++);
        attributes.put("health", index++);
        attributes.put("durability", index++);
        attributes.put("cost", index++);

        for (Integer cardId : cards.keySet()) {
            card = cards.get(cardId);
            for (CardAbility cardAbility : card.getAbilities()) {
                attribute = cardAbility.getAbility() + "->" + cardAbility.getTarget();
                if (!attributes.containsKey(attribute)) {
                    attributes.put(attribute, index++);
                }
            }
        }

        attributes.put("winrate", index);

    }

    /**
     * Constructor
     *
     * @param cards
     */
    public Parser(Collection<Card> cards) {
        String attribute;
        int index = 0;
        attributes = new HashMap<>();
        attributes.put("attack", index++);
        attributes.put("health", index++);
        attributes.put("durability", index++);
        attributes.put("cost", index++);

        for (Card card : cards) {

            for (CardAbility cardAbility : card.getAbilities()) {
                attribute = cardAbility.getAbility() + "->" + cardAbility.getTarget();
                if (!attributes.containsKey(attribute)) {
                    attributes.put(attribute, index++);
                }
            }
        }

        attributes.put("winrate", index);

    }

    /**
     * @param cardCollection
     * @param cards
     * @return
     */
    public Instance getInstance(List<Integer> cardCollection, Map<Integer, Card> cards) {
        Card card;
        String attribute;
        double[] dataInstance = new double[attributes.size()];
        for (Integer cardId : cardCollection) {
            card = cards.get(cardId);
            dataInstance[attributes.get("attack")] += card.getAttack();
            dataInstance[attributes.get("health")] += card.getHealth();
            dataInstance[attributes.get("durability")] += card.getDurability();
            dataInstance[attributes.get("cost")] += card.getCost();

            for (CardAbility cardAbility : card.getAbilities()) {
                attribute = cardAbility.getAbility() + "->" + cardAbility.getTarget();
                dataInstance[attributes.get(attribute)] += cardAbility.getValue();

            }

        }

        return new Instance(dataInstance);
    }

    /**
     * @param cardCollection
     * @param cards
     * @return
     */
    public Instance getInstance(CardCollection cardCollection, HashMap<String, Card> cards) {
        Card card;
        String attribute;
        double[] dataInstance = new double[attributes.size()];
        for (String cardName : cardCollection.getCardsNames()) {
            card = cards.get(cardName);


            dataInstance[attributes.get("attack")] += card.getAttack();
            dataInstance[attributes.get("health")] += card.getHealth();
            dataInstance[attributes.get("durability")] += card.getDurability();
            dataInstance[attributes.get("cost")] += card.getCost();

            for (CardAbility cardAbility : card.getAbilities()) {
                attribute = cardAbility.getAbility() + "->" + cardAbility.getTarget();
                dataInstance[attributes.get(attribute)] += cardAbility.getValue();

            }

        }

        Integer losses = cardCollection.getLosses();
        Integer wins = cardCollection.getWins();

        dataInstance[attributes.get("winrate")] = (1.0 * wins) / (wins + losses);

        return new Instance(dataInstance);
    }

    public List<Entry<String, Integer>> getAttributesEntryPositionOrdened() {

        ArrayList<Map.Entry<String, Integer>> entrySet = new ArrayList<>(attributes.entrySet());
        Collections.sort(entrySet, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o1.getValue().compareTo(o2.getValue()));
        return entrySet;
    }

    public List<Entry<String, Integer>> getAttributesEntryAbilityOrdened() {

        ArrayList<Map.Entry<String, Integer>> entrySet = new ArrayList<>(attributes.entrySet());
        Collections.sort(entrySet, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o1.getKey().compareTo(o2.getKey()));
        return entrySet;
    }

}
