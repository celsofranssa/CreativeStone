package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.model.CardAbility;

/**
 * Created by Celso on 18/04/2016.
 */
public class AbilityPredicate extends CardPredicate {

    private final CardAbility cardAbility;

    public AbilityPredicate(CardAbility cardAbility) {
        super();
        this.cardAbility = cardAbility;
    }

    @Override
    public boolean test(Card card) {
        for (CardAbility cardAbility : card.getAbilities()) {
            if (this.cardAbility.equals(cardAbility)) {
                return true;
            }
        }
        return false;
    }
}
