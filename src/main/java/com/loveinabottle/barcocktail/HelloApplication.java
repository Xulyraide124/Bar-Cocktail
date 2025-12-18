package com.loveinabottle.barcocktail;

import com.loveinabottle.barcocktail.model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Initialiser le bar avec des données de démo
        Bar bar = initializeBar();

        // Charger le FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 720);

        // Injecter le bar dans le contrôleur
        HelloController controller = fxmlLoader.getController();
        controller.setBar(bar);

        stage.setTitle("Love in a Bottle - Bar à Cocktails");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialise le bar avec des ingrédients, cocktails et employés de démonstration
     */
    private Bar initializeBar() {
        Bar bar = new Bar();

        // ===== Créer des ingrédients =====
        Ingredient rhum = new Ingredient("Rhum", 50.0, true, 20);
        Ingredient vodka = new Ingredient("Vodka", 50.0, true, 15);
        Ingredient gin = new Ingredient("Gin", 50.0, true, 12);
        Ingredient tequila = new Ingredient("Tequila", 50.0, true, 10);
        Ingredient citron = new Ingredient("Jus de Citron", 30.0, false, 25);
        Ingredient sucre = new Ingredient("Sirop de Sucre", 20.0, false, 30);
        Ingredient menthe = new Ingredient("Menthe", 10.0, false, 15);
        Ingredient orange = new Ingredient("Jus d'Orange", 30.0, false, 20);
        Ingredient cranberry = new Ingredient("Jus de Cranberry", 30.0, false, 18);
        Ingredient soda = new Ingredient("Soda", 200.0, false, 40);

        // Ajouter au stock
        bar.addIngredient(rhum);
        bar.addIngredient(vodka);
        bar.addIngredient(gin);
        bar.addIngredient(tequila);
        bar.addIngredient(citron);
        bar.addIngredient(sucre);
        bar.addIngredient(menthe);
        bar.addIngredient(orange);
        bar.addIngredient(cranberry);
        bar.addIngredient(soda);

        // ===== Créer des cocktails =====

        // Mojito: Rhum + Citron + Sucre + Menthe + Soda
        Map<Ingredient, Integer> mojitoRecipe = new HashMap<>();
        mojitoRecipe.put(rhum, 2);
        mojitoRecipe.put(citron, 1);
        mojitoRecipe.put(sucre, 1);
        mojitoRecipe.put(menthe, 2);
        mojitoRecipe.put(soda, 1);
        Cocktail mojito = new Cocktail("Mojito", mojitoRecipe, 8.50);
        bar.addCocktailToMenu(mojito);

        // Cosmopolitan: Vodka + Cranberry + Citron
        Map<Ingredient, Integer> cosmoRecipe = new HashMap<>();
        cosmoRecipe.put(vodka, 2);
        cosmoRecipe.put(cranberry, 2);
        cosmoRecipe.put(citron, 1);
        Cocktail cosmopolitan = new Cocktail("Cosmopolitan", cosmoRecipe, 9.00);
        bar.addCocktailToMenu(cosmopolitan);

        // Gin Tonic: Gin + Soda + Citron
        Map<Ingredient, Integer> ginTonicRecipe = new HashMap<>();
        ginTonicRecipe.put(gin, 2);
        ginTonicRecipe.put(soda, 2);
        ginTonicRecipe.put(citron, 1);
        Cocktail ginTonic = new Cocktail("Gin Tonic", ginTonicRecipe, 7.50);
        bar.addCocktailToMenu(ginTonic);

        // Tequila Sunrise: Tequila + Orange + Sucre
        Map<Ingredient, Integer> tequilaSunriseRecipe = new HashMap<>();
        tequilaSunriseRecipe.put(tequila, 2);
        tequilaSunriseRecipe.put(orange, 2);
        tequilaSunriseRecipe.put(sucre, 1);
        Cocktail tequilaSunrise = new Cocktail("Tequila Sunrise", tequilaSunriseRecipe, 8.00);
        bar.addCocktailToMenu(tequilaSunrise);

        // Vodka Orange: Vodka + Orange
        Map<Ingredient, Integer> vodkaOrangeRecipe = new HashMap<>();
        vodkaOrangeRecipe.put(vodka, 2);
        vodkaOrangeRecipe.put(orange, 2);
        Cocktail vodkaOrange = new Cocktail("Vodka Orange", vodkaOrangeRecipe, 6.50);
        bar.addCocktailToMenu(vodkaOrange);

        // ===== Créer des employés =====
        Bartender bartender1 = new Bartender("B001", "Jean-Pierre", 1);
        Bartender bartender2 = new Bartender("B002", "Marie", 7);
        Bartender bartender3 = new Bartender("B003", "Lucas", 3);

        bar.addEmployee(bartender1);
        bar.addEmployee(bartender2);
        bar.addEmployee(bartender3);

        return bar;
    }
}

