/*
 * Implements fitness evaluator
 */
package com.creapar.creativestone.evolutionary;

import com.creapar.creativestone.novelty.Novelty;
import com.creapar.creativestone.value.ValueModel;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Celso
 */
public class Evaluator implements FitnessEvaluator<List<Integer>> {

    Novelty novelty;
    ValueModel efficiency;
    List<Integer> recipeCardsIds;

    /**
     * Constructor
     *
     * @param surprise - novelty model
     * @param efficiency - efficiency model
     */
    public Evaluator(Novelty surprise, ValueModel efficiency, List<Integer> recipeCardsIds) {
        this.novelty = surprise;
        this.efficiency = efficiency;
        this.recipeCardsIds = recipeCardsIds;
    }

    @Override
    public double getFitness(List<Integer> candidate, List<? extends List<Integer>> population) {
        double n, v, c = 0.0;
        candidate = union(candidate, recipeCardsIds);
        if (!hasDuplicate(candidate)) {
            v = efficiency.getValue(candidate);
            n = novelty.getNovelty(candidate);
            c = n + v - penalty(n, v);

        }
        return c;
    }

      /**
     * Penalty function
     *
     * @param v_a
     * @param n_a
     * @return
     */
    private double penalty(Double v_a, Double n_a) {
        Double s = v_a + n_a;
        Double d = Math.abs(v_a - n_a);
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

    private List<Integer> union(List<Integer> cardsIdsA, List<Integer> cardsIdsB) {
        List<Integer> union = new ArrayList<>();
        union.addAll(cardsIdsA);
        union.addAll(cardsIdsB);
        return union;
    }

    @Override
    public boolean isNatural() {
        return true;
    }

}
