package com.loveinabottle.barcocktail.model;

public class Waiter extends Employee {

    public Waiter(String id, String name, int speed) {
        super(id, name, speed);
    }

    @Override
    public boolean prepare(Cocktail cocktail, Bar bar) {
        // Waiters can also serve simple cocktails, maybe slower
        if (!cocktail.isPrepareable(bar)) return false;
        cocktail.consumeIngredients(bar);
        return true;
    }
}