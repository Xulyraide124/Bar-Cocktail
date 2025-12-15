package com.loveinabottle.barcocktail.model;

import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

public class TestBar {
    public static void main(String[] args) {
        // ==== Création du bar ====
        Bar bar = new Bar();

        // ==== Ajouter ingrédients ====
        Ingredient rum = new Ingredient("Rum", 2.0, true, 5);
        Ingredient vodka = new Ingredient("Vodka", 2.5, true, 5);
        Ingredient tequila = new Ingredient("Tequila", 2.0, true, 4);
        Ingredient mint = new Ingredient("Mint Leaves", 1.0, false, 10);
        Ingredient sugar = new Ingredient("Sugar", 1.0, false, 10);
        Ingredient lime = new Ingredient("Lime", 2.0, false, 8);
        Ingredient orangeJuice = new Ingredient("Orange Juice", 10, false, 10);
        Ingredient pineappleJuice = new Ingredient("Pineapple Juice", 10, false, 5);
        Ingredient soda = new Ingredient("Soda Water", 10, false, 5);

        // Ajouter au bar
        bar.addIngredient(rum);
        bar.addIngredient(vodka);
        bar.addIngredient(tequila);
        bar.addIngredient(mint);
        bar.addIngredient(sugar);
        bar.addIngredient(lime);
        bar.addIngredient(orangeJuice);
        bar.addIngredient(pineappleJuice);
        bar.addIngredient(soda);

        // ==== Ajouter cocktails ====
        Cocktail maiTai = new Cocktail("Mai Tai", Map.of(rum,1, lime,1, orangeJuice,1, sugar,1), 8.0);
        Cocktail mojito = new Cocktail("Mojito", Map.of(rum,1, mint,2, sugar,1, lime,1, soda,1), 7.0);
        Cocktail margarita = new Cocktail("Margarita", Map.of(tequila,1, lime,1, sugar,1), 9.0);
        Cocktail virginMojito = new Cocktail("Virgin Mojito", Map.of(mint,2, sugar,1, lime,1, soda,1), 5.0);

        // Ajouter au menu
        bar.addCocktailToMenu(maiTai);
        bar.addCocktailToMenu(mojito);
        bar.addCocktailToMenu(margarita);
        bar.addCocktailToMenu(virginMojito);

        // ==== Afficher menu ====
        System.out.println("=== Bar Menu ===");
        for (Cocktail c : bar.getMenu()) {
            System.out.println(c.getName() + " - $" + c.getBasePrice());
        }

        // ==== Afficher ingrédients disponibles ====
        System.out.println("\n=== Available Ingredients ===");
        bar.getAllIngredients().forEach(i ->
                System.out.println(i.getName() + " (Stock: " + i.getStockUnits() + ")")
        );

        // ==== Ajouter employés ====
        bar.addEmployee(new Bartender("e1","Alice",8));
        bar.addEmployee(new Waiter("e2","Bob",5));

        // ==== Créer client et passer commande ====
        Client john = new Client("John Doe");
        var orderOpt = bar.placeOrder(john, List.of("Mojito", "Virgin Mojito"));

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            System.out.println("\nOrder created: " + order);

            boolean prepared = bar.prepareOrder(order);
            System.out.println("Order prepared: " + prepared);

            double total = bar.billOrder(order);
            System.out.println("Total price: $" + total);

            // Afficher ingrédients utilisés
            System.out.println("\nIngredients used for this order:");
            order.getCocktails().forEach(c ->
                    c.getRecipe().forEach((ingredient, qty) ->
                            System.out.println(ingredient.getName() + " x" + qty)
                    )
            );
        }
    }
}