package com.creapar.creativestone.model;

import java.util.ArrayList;

/**
 *
 * @author lunnatics
 */
public class Card {

    private String name;
    private int cost;
    private int attack;
    private int health;
    private int durability;
    private ArrayList<CardAbility> abilities;
    private boolean collectible;
    private boolean elite;
    private String faction;
    private String playerClass;
    private String race;
    private String rarity;
    private String type;
    private String text;

    /**
     * Main constructor
     */
    public Card() {
        abilities = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public ArrayList<CardAbility> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<CardAbility> abilities) {
        this.abilities = abilities;
    }

    public boolean isCollectible() {
        return collectible;
    }

    public void setCollectible(boolean collectible) {
        this.collectible = collectible;
    }

    public boolean isElite() {
        return elite;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString(){
        return name;//+" \n"+
               //type+" \n"+
              // playerClass+" \n";
    }
}
