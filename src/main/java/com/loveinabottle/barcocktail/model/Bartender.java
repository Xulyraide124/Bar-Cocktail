package com.loveinabottle.barcocktail.model;

public class Bartender extends Employee {

    public Bartender(String id, String name, int speed) {
        super(id, name, speed);
    }

    @Override
    public boolean prepare(Cocktail cocktail, Bar bar) {
        if (!cocktail.isPrepareable(bar)) return false;
        cocktail.consumeIngredients(bar);
        return true;
    }
}
