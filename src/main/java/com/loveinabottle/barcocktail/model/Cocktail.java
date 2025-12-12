package com.loveinabottle.barcocktail.model;

import java.util.Collections;
import java.util.Map;

public class Cocktail {
    private final String name;
    private final Map<Ingredient, Integer> recipe;
    private final double basePrice;

    public Cocktail(String name, Map<Ingredient, Integer> recipe, double basePrice) {
        this.name = name;
        this.recipe = Collections.unmodifiableMap(recipe);
        this.basePrice = basePrice;
    }

    public String getName() { return name; }
    public Map<Ingredient, Integer> getRecipe() { return recipe; }
    public double getBasePrice() { return basePrice; }

    public boolean isPrepareable(Bar bar) {
        for (Map.Entry<Ingredient, Integer> e : recipe.entrySet()) {
            Ingredient key = bar.findIngredientByName(e.getKey().getName());
            if (key == null || key.getStockUnits() < e.getValue()) return false;
        }
        return true;
    }

    public void consumeIngredients(Bar bar) {
        for (Map.Entry<Ingredient, Integer> e : recipe.entrySet()) {
            Ingredient key = bar.findIngredientByName(e.getKey().getName());
            if (key != null) key.removeUnits(e.getValue());
        }
    }

    @Override
    public String toString() {
        return name + " - $" + basePrice;
    }
}
