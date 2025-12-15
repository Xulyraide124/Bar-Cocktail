package com.loveinabottle.barcocktail.model;

public interface Preparateur {
    boolean prepare(Cocktail cocktail, Bar bar);
    int getSpeed();
}
