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
public class Edge {

   private Integer id;
    private Double weight;

    public Edge(Integer id, Double weight) {
        this.id = id;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    

    @Override
    public String toString() {
        return "\ne:" + id + ",w:" + weight;
    }
    
    

}
