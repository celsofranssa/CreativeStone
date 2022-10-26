/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

/**
 *
 * @author cel
 */
public class SynergyCell {

    Double weight;
    String cardName;

    public SynergyCell(String cardName, Double weight) {
        this.weight = weight;
        this.cardName = cardName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

}
