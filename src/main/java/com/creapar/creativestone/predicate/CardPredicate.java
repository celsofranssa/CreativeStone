package com.creapar.creativestone.predicate;

import com.creapar.creativestone.model.Card;
import com.creapar.creativestone.util.GsonUtils;
import com.creapar.creativestone.util.RuntimeTypeAdapterFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Celso on 08/05/2016.
 */
public abstract class CardPredicate implements Predicate<Card> {


    private static final RuntimeTypeAdapterFactory<CardPredicate> adapter =
            RuntimeTypeAdapterFactory.of(CardPredicate.class);

    private static final Set<Class<?>> registeredClasses = new HashSet<>();

    static {
        GsonUtils.registerType(adapter);
    }


    public CardPredicate() {
        registerClass();
    }

    private synchronized void registerClass() {
        if (!this.registeredClasses.contains(this.getClass())) {
            adapter.registerSubtype(this.getClass());
            registeredClasses.add(this.getClass());

        }
    }
}