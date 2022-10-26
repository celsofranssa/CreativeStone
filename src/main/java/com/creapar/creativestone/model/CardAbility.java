/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.model;

import java.util.Objects;

/**
 *
 * @author Celso
 */
public class CardAbility {

    private String ability;
    private String target;
    private int value;

    public CardAbility() {
        this.ability = "";
        this.value = 1;
        this.target = "";
    }

    public CardAbility(String ability, String target) {
        this.ability = ability;
        this.target = target;
        this.value = 1;

    }

    public CardAbility(String ability, String target, int value) {
        this.ability = ability;
        this.target = target;
        this.value = value;

    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.ability);
        hash = 47 * hash + Objects.hashCode(this.target);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CardAbility other = (CardAbility) obj;
        if (!Objects.equals(this.ability, other.ability)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

}
