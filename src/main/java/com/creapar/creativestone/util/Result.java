/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import java.util.List;

/**
 * @author cel
 */
public class Result implements Comparable<Result> {

    List<String> cardsNames;
    String heroName;
    Double novelty;
    Double value;
    Double creativity;
    Double winRate;
    String description;
    Double totalManaCost;

    public Result() {
    }

    public List<String> getCardsNames() {
        return cardsNames;
    }

    public void setCardsNames(List<String> cardsNames) {
        this.cardsNames = cardsNames;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public Double getNovelty() {
        return novelty;
    }

    public void setNovelty(Double novelty) {
        this.novelty = novelty;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getCreativity() {
        return novelty + value - Math.abs(novelty - value);

    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTotalManaCost(Double totalManaCost) {
        this.totalManaCost = totalManaCost;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("\nHero: ").append(heroName).append("\n");
        builder.append("Novelty: ").append(novelty).append("\n");
        builder.append("Valeu: ").append(value).append("\n");
        builder.append("Creativity: ").append(getCreativity()).append("\n");
        builder.append("\nDescription:\n").append(getDescription());

        return builder.toString();
    }

    @Override
    public int compareTo(Result result) {
        return this.winRate.compareTo(result.getWinRate());

    }

}
