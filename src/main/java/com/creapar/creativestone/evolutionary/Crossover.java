/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.evolutionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 *
 * @author Celso
 */
class Crossover implements EvolutionaryOperator<List<Integer>> {

    @Override
    public List<List<Integer>> apply(List<List<Integer>> selectedCandidates, Random rng) {
        List<List<Integer>> offsprings = new ArrayList<>();
        List<Integer> offspring1 = new ArrayList<>();
         List<Integer> offspring2 = new ArrayList<>();
         for (Integer offspring21 : offspring2) {
            
        }
         return offsprings;
    }

   

}
