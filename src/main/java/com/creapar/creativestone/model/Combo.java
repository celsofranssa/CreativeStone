package com.creapar.creativestone.model;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Celso on 23/04/2016.
 */
public class Combo {
    Integer id;
    String heroName;
    Double rdcNovelty;
    Double rdcValue;
    Double humanNovelty;
    Double humanValue;
    Boolean madeByHuman;
    List<String> cardsNames;
    List<ComboAssessment> assessments;


    public Combo() {
        this.cardsNames = new ArrayList<>();
        this.assessments = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public Double getRdcNovelty() {
        return rdcNovelty;
    }

    public void setRdcNovelty(Double rdcNovelty) {
        this.rdcNovelty = rdcNovelty;
    }

    public Double getRdcValue() {
        return rdcValue;
    }

    public void setRdcValue(Double rdcValue) {
        this.rdcValue = rdcValue;
    }

    public Double getHumanNovelty() {
        Mean mean = new Mean();
        for (ComboAssessment assessment : assessments
                ) {
            mean.increment(assessment.novelty);
        }
        return mean.getResult();
    }

    public void setHumanNovelty(Double humanNovelty) {
        this.humanNovelty = humanNovelty;
    }

    public Double getHumanValue() {
        Mean mean = new Mean();
        for (ComboAssessment assessment : assessments
                ) {
            mean.increment(assessment.value);
        }
        return mean.getResult();
    }

    public void setHumanValue(Double humanValue) {
        this.humanValue = humanValue;
    }

    public Boolean getMadeByHuman() {
        return madeByHuman;
    }

    public void setMadeByHuman(Boolean madeByHuman) {
        this.madeByHuman = madeByHuman;
    }

    public List<String> getCardsNames() {
        return cardsNames;
    }

    public void setCardsNames(List<String> cardsNames) {
        this.cardsNames = cardsNames;
    }

    public List<ComboAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<ComboAssessment> assessments) {
        this.assessments = assessments;
    }

    public void addAssessment(ComboAssessment comboAssessment) {
        this.assessments.add(comboAssessment);
    }

    public void addCard(String cardName) {
        this.cardsNames.add(cardName);
    }

    public String toString() {
        return id + "\t" + humanNovelty + "\t" + humanValue + "\t" + rdcNovelty + "\t" + rdcValue;
    }
}
