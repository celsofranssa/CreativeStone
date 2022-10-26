package com.creapar.creativestone.util;

/**
 * Created by Celso on 23/04/2016.
 */
public class JsonElement {
    Integer id_combo;
    String hero;
    String questao_um;
    String questao_dois;
    Integer isHuman;
    String cardList;

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public String getQuestao_um() {
        return questao_um;
    }

    public void setQuestao_um(String questao_um) {
        this.questao_um = questao_um;
    }

    public String getQuestao_dois() {
        return questao_dois;
    }

    public void setQuestao_dois(String questao_dois) {
        this.questao_dois = questao_dois;
    }

    public Integer isHuman() {
        return isHuman;
    }

    public void setHuman(Integer human) {
        isHuman = human;
    }

    public String getCardList() {
        return cardList;
    }

    public void setCardList(String cardList) {
        this.cardList = cardList;
    }

    public Integer getId_combo() {
        return id_combo;
    }

    public void setId_combo(Integer id_combo) {
        this.id_combo = id_combo;
    }
}
