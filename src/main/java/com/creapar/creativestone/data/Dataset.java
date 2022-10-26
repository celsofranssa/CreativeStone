/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

/**
 *
 * @author Celso
 */
public class Dataset {

    ArrayList<String> labels;
    ArrayList<String> dataTypes;
    ArrayList<Instance> instances;
    Integer numberOfAttributes;
    Integer inputAttributes;
    Integer classAttributes;
    Integer numberOfInstances;

    /**
     * Constructor: Create a empty Dataset
     *
     * @param inputAttributes - number of input attributes
     * @param classAttributes - number of output attributes
     */
    public Dataset(Integer inputAttributes, Integer classAttributes) {
        this.instances = new ArrayList<>();
        this.inputAttributes = inputAttributes;
        this.classAttributes = classAttributes;
        this.numberOfAttributes = inputAttributes + classAttributes;
    }

    /**
     * Gets the number of input attributes
     *
     * @return
     */
    public Integer getInputAttributes() {
        return inputAttributes;
    }

    /**
     * Gets the number of class attributes
     *
     * @return
     */
    public Integer getClassAttributes() {
        return classAttributes;
    }

    /**
     * Set the labels of Dataset
     *
     * @param labels
     */
    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;

    }

    /**
     * Set the datatype of Dataset
     *
     * @param dataTypes
     */
    public void setDataTypes(ArrayList<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * Add a instance into Dataset
     *
     * @param instance
     */
    public void addInstance(Instance instance) {
        instances.add(instance);
        this.numberOfInstances = instances.size();
    }

    /**
     * Return a String containing all dataset, one instance per line
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder data = new StringBuilder();
        instances.stream().forEach((instance) -> {
            data.append(instance.toString()).append("\n");
        });
        return data.toString();
    }

    /**
     * Write a Dataset into file
     *
     * @param filename
     */
    public void toFile(String filename) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(toString());
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Write a Dataset into file
     *
     * @param filename
     */
    public void toFileArff(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer getNumberOfInstances() {
        return instances.size();
    }

    public Integer getNumberOfAttributes() {
        return instances.get(0).getNumberOfAtributtes();
    }

    public ArrayList<Instance> getInstances() {
        return instances;
    }

    /**
     * Return a list of average for each attribute
     *
     * @return
     */
    public List<Double> averages() {
        ArrayList<Double> averages = new ArrayList<>();
        List<Double> instanceData;
        double[] sum = new double[inputAttributes];

        for (Instance instance : instances) {
            instanceData = instance.getData();
            for (int i = 0; i < inputAttributes; i++) {
                sum[i] += instanceData.get(i);
            }

        }
        for (int i = 0; i < inputAttributes; i++) {
            averages.add(sum[i] / numberOfInstances);
        }
        return averages;
    }

    /**
     * Return a list of getMeans for each attribute
     *
     * @return
     */
    public List<Mean> getMeans() {

        List<Mean> means = new ArrayList<>();
        for (int i = 0; i < inputAttributes; i++) {
            means.add(new Mean());
        }

        List<Double> instanceData;
        for (Instance instance : instances) {
            instanceData = instance.getData();
            for (int i = 0; i < inputAttributes; i++) {
                means.get(i).increment(instanceData.get(i));
            }

        }

        return means;
    }

    /**
     * Return a list of variance for each attribute
     *
     * @return
     */
    public List<Variance> getVariances() {
        ArrayList<Variance> variances = new ArrayList<>();
        List<Double> instanceData;

        for (int i = 0; i < inputAttributes; i++) {
            variances.add(new Variance());
        }

        for (Instance instance : instances) {
            instanceData = instance.getData();
            for (int i = 0; i < inputAttributes; i++) {
                variances.get(i).increment(instanceData.get(i));
            }

        }

        return variances;
    }

    /**
     * Return a list of variance for each attribute
     *
     * @param averages
     * @return
     */
    public List<Double> variances(List<Double> averages) {
        ArrayList<Double> variances = new ArrayList<>();
        List<Double> instanceData;
        double[] sum = new double[inputAttributes];

        for (Instance instance : instances) {
            instanceData = instance.getData();
            for (int i = 0; i < inputAttributes; i++) {
                sum[i] += Math.pow(instanceData.get(i) - averages.get(i), 2);
            }

        }
        for (int i = 0; i < inputAttributes; i++) {
            variances.add(sum[i] / (numberOfInstances - 1));
        }
        return variances;
    }

    public Map<Double, Double> kNearestInstances(Instance target, Integer k) {
        Map<Double, Double> distances = weightInstance(target);

        return distances;
    }

    private void sort(Map<Double, Double> map) {

    }

    private List<Instance> theFirstKInstances(int k) {
        ArrayList<Instance> firstK = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            firstK.add(instances.get(i));

        }
        return firstK;
    }

    /**
     * Weight the instance using a target instance as reference
     *
     * @param target
     */
    private Map<Double, Double> weightInstance(Instance target) {
        int index = target.getNumberOfAtributtes() - 1;
        Map<Double, Double> distances = new TreeMap<>();
        instances.stream().forEach((instance) -> {
            distances.put(instance.distanceFrom(target, inputAttributes), instance.getValue(index));

        });
        return distances;
    }

    /**
     * Sorts the Dataset using the Instance weight
     */
    private void sort() {
        Collections.sort(this.instances);
    }
}
