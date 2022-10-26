package com.creapar.creativestone.synergy;

/**
 * Created by Celso on 18/05/2016.
 */
public class SynergyCell {
    private Integer id;
    private Double weight;

    public SynergyCell(Integer id, Double weight) {
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
}
