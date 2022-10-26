/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.value;

/**
 *
 * @author Celso
 * @param <T>
 */
public interface ValueModel<T> {

    /**
     * Gets the efficiency value of a data
     *
     * @param artifact
     * @return
     */
    public Double getValue(T artifact);
}
