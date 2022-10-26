/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Celso
 */
public class Instance implements Comparable<Instance> {

    List<Double> attributes;
    Double weight;

    /**
     * Initializes a empty Instance
     */
    public Instance() {
        this.attributes = new ArrayList<>();
    }

    /**
     * Initialize a instance by the list of data
     *
     * @param instanceData
     */
    public Instance(List<Double> instanceData) {
        this.attributes = instanceData;
    }

    /**
     * Initialize a instance by the list of data
     *
     * @param instanceData
     */
    public Instance(double[] instanceData) {
        this.attributes = new ArrayList<>();
        for (Double data : instanceData) {
            attributes.add(data);
        }
    }

    /**
     *
     * @param instanceData
     */
    public Instance(String[] instanceData) {

        this.attributes = new ArrayList<>();

        for (String data : instanceData) {
            attributes.add(Double.parseDouble(data));
        }

    }

    public List<Double> getAttributes() {
        return attributes;
    }

    public Double getWeight() {
        return weight;
    }

    /**
     * Adds a data
     *
     * @param data
     */
    public void addAttribute(Double data) {
        this.attributes.add(data);

    }

    /**
     * Adds a list of data
     *
     * @param data
     */
    public void addAttributes(List<Double> data) {
        this.attributes.addAll(data);

    }

    /**
     * Gets the number of attributes
     *
     * @return
     */
    public Integer getNumberOfAtributtes() {
        return attributes.size();
    }

    /**
     * Get the attributes data
     *
     * @return
     */
    public List<Double> getData() {
        return attributes;
    }

    public Double distanceFrom(Instance instance, Integer inputAttributes) {
        double squeredDistance = 0.0;
        List<Double> data = instance.getData();

        for (int i = 0; i < inputAttributes; i++) {
            squeredDistance += Math.pow(attributes.get(i) - data.get(i), 2);
        }
        return Math.sqrt(squeredDistance);
    }

    public double getValue(int index) {
        return this.attributes.get(index);
    }

    /**
     * Put a Instance in a String form.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder instanceData = new StringBuilder();
        attributes.stream().forEach((atributte) -> {
            instanceData.append(atributte).append("\t");
        });
        return instanceData.toString();
    }

    /**
     * Compares this Instance with another instance.
     *
     * @param instance
     * @return
     */
    @Override
    public int compareTo(Instance instance) {
        return weight.compareTo(instance.weight);
    }
}
