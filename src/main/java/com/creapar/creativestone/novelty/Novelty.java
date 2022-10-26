/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.novelty;

/**
 *
 * @author Celso
 * @param <T>
 */
public interface Novelty <T>{
    
    /**
     * Returns the amount of novelty of a artifact
     * @param artifact
     * @return 
     */
    public Double getNovelty (T artifact);   
    
    /**
     * Update the dataset used to calculate novelty
     * @param artifact 
     */
    public void updateNovelty(T artifact);
}
