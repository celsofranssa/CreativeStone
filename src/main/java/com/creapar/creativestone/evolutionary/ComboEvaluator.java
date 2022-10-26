/*
 * Implements fitness evaluator
 */
package com.creapar.creativestone.evolutionary;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.novelty.Novelty;
import com.creapar.creativestone.value.ValueModel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 *
 * @author Celso
 */
public class ComboEvaluator implements FitnessEvaluator<List<Integer>> {

    Novelty novelty;
    //Efficiency efficiency;
    ValueModel efficiency;
    Map<Integer, Card> cards;

    DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat df = new DecimalFormat("#.######", unusualSymbols);

    /**
     * Constructor
     *
     * @param surprise - novelty model
     * @param efficiency - efficiency model
     * @param cards
     */
    public ComboEvaluator(Novelty surprise, ValueModel efficiency, Map<Integer, Card> cards) {
        this.novelty = surprise;
        this.efficiency = efficiency;
        this.cards = cards;
    }

    @Override
    public double getFitness(List<Integer> candidate, List<? extends List<Integer>> population) {
        double n, v, c = 0.00001;
        double m = manaCost(candidate);

        if (!hasDuplicate(candidate)) {
            v = efficiency.getValue(candidate);
            n = novelty.getNovelty(candidate);
            c = n + v - penalty(n, v);
            c = c / (Math.abs(m - 10 + 0.0001));
        }
        return Double.valueOf(df.format(c));
    }

    /**
     * Penalty function
     *
     * @param v_a
     * @param n_a
     * @return
     */
    private double penalty(Double a, Double b) {
        Double s = a + b;
        Double d = Math.abs(a - b);
        return s * (1 - Math.exp(-1.0 * d));
    }

    /**
     * Check if the candidate has duplication
     *
     * @param candidate
     * @return
     */
    private Boolean hasDuplicate(List<Integer> candidate) {
        Set<Integer> set = new HashSet<>();
        return candidate.stream().anyMatch((integer) -> (!set.add(integer)));
    }

    @Override
    public boolean isNatural() {
        return true;
    }

    private Double manaCost(List<Integer> candidate) {

        List<Card> comboCards = new ArrayList<>();

        for (Integer cardId : candidate) {
            Card card = new Card();
            card.setName(cards.get(cardId).getName());
            card.setCost(cards.get(cardId).getCost());
            card.setType(cards.get(cardId).getType());
            card.setRace(cards.get(cardId).getRace());

            comboCards.add(card);

        }

        for (Card card : comboCards) {
            // Summoning Portal
            if (card.getName().equals("Summoning Portal")) {
                for (Card card1 : comboCards) {
                    if (!card1.getName().equals("Summoning Portal") && card1.getType().equals("Minion")) {
                        card1.setCost(card1.getCost() - 2);
                        if (card1.getCost() < 1) {
                            card1.setCost(1);
                        }
                    }
                }

            }

            //Mechwarper
            if (card.getName().equals("Mechwarper")) {
                for (Card card1 : comboCards) {
                    if (!card1.getName().equals("Mechwarper") && card1.getRace() != null && card1.getRace().equals("Mech")) {
                        card1.setCost(card1.getCost() - 1);
                        if (card1.getCost() < 0) {
                            card1.setCost(0);
                        }
                    }
                }
            } //Sorcerer\u0027s Apprentice
            else if (card.getName().equals("Sorcerer\u0027s Apprentice")) {

                for (Card card1 : comboCards) {
                    if (card1.getType().equals("Spell")) {

                        card1.setCost(card1.getCost() - 1);
                        if (card1.getCost() < 0) {
                            card1.setCost(0);
                        }

                    }
                }
            } //Shadowstep
            else if (card.getName().equals("Shadowstep")) {
                for (int i = 0; i < comboCards.size(); i++) {
                    Card card1 = comboCards.get(i);
                    if (card1.getType().equals("Minion") && card1.getCost() >= 2) {
                        card1.setCost(card1.getCost() - 2);
                        i = comboCards.size();
                    }
                }

            } //preparation
            else if (card.getName().equals("Preparation")) {
                for (int i = 0; i < comboCards.size(); i++) {
                    Card card1 = comboCards.get(i);
                    if (card1.getType().equals("Spell") && card1.getCost() >= 3) {
                        card1.setCost(card1.getCost() - 3);
                        i = comboCards.size();
                    }
                }
            } //Innervate
            else if (card.getName().equals("Innervate")) {
                card.setCost(-2);
            } //Pint-Sized Summoner
            else if (card.getName().equals("Pint-Sized Summoner")) {
                for (int i = 0; i < comboCards.size(); i++) {
                    Card card1 = comboCards.get(i);
                    if (card1.getType().equals("Minion") && card1.getCost() >= 1) {
                        card1.setCost(card1.getCost() - 1);
                        i = comboCards.size();
                    }
                }
            } //Dragon Consort
            else if (card.getName().equals("Dragon Consort")) {
                for (int i = 0; i < comboCards.size(); i++) {
                    Card card1 = comboCards.get(i);
                    if (card1.getRace() != null && card.getRace().equals("Dragon")) {
                        card1.setCost(card1.getCost() - 2);
                        i = comboCards.size();
                    }
                }
            } //Dread Corsair
            else if (card.getName().equals("Dread Corsair")) {
                for (Card card1 : comboCards) {
                    if (card1.getType().equals("Weapon")) {
                        card.setCost(card.getCost() - card1.getAttack());
                        if (card1.getCost() < 0) {
                            card1.setCost(0);
                        }

                    }
                }
            } //Dread Corsair
            else if (card.getName().equals("Crush")) {
                card.setCost(3);
            } 
            //Sea Giant
            else if (card.getName().equals("Sea Giant")) {
                card.setCost(card.getCost() - comboCards.size());
                if (card.getCost() < 0) {
                    card.setCost(0);
                }
            }
            //Solemn Vigil
             else if (card.getName().equals("Solemn Vigil")) {
                card.setCost(0);
            } 
            //Volcanic Drake
            else if (card.getName().equals("Volcanic Drake")) {
                card.setCost(0);
            } 
            //Volcanic Lumberer
            else if (card.getName().equals("Volcanic Lumberer")) {
                card.setCost(3);
            } 
            //Molten Giant
            else if (card.getName().equals("Molten Giant")) {
                card.setCost(0);
            } 

        }
        Double manaCost = 0.0;
        for (Card comboCard : comboCards) {
            manaCost += comboCard.getCost();
        }

        return manaCost;
    }

}
