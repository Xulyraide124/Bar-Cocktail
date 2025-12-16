package com.loveinabottle.barcocktail.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bar {
    private final List<Ingredient> stock = new ArrayList<>();
    private final List<Cocktail> menu = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    // ===== Gestion du stock =====
    public void addIngredient(Ingredient ingredient) {
        stock.add(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return new ArrayList<>(stock); // retourne une copie
    }

    public Ingredient findIngredientByName(String name) {
        return stock.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // ===== Gestion du menu =====
    public void addCocktailToMenu(Cocktail cocktail) {
        menu.add(cocktail);
    }

    public List<Cocktail> getMenu() {
        return new ArrayList<>(menu); // retourne une copie
    }

    // ===== Gestion des employés =====
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees); // retourne une copie
    }

    // ===== Passage de commande =====
    public Optional<Order> placeOrder(Client client, List<String> cocktailNames) {
        List<Cocktail> selected = new ArrayList<>();
        for (String name : cocktailNames) {
            Cocktail c = menu.stream()
                    .filter(m -> m.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
            if (c == null) return Optional.empty(); // cocktail non trouvé
            selected.add(c);
        }
        return Optional.of(new Order(client, selected));
    }

    // ===== Préparation de commande =====
    public boolean prepareOrder(Order order) {
        if (employees.isEmpty()) return false;

        for (Cocktail c : order.getCocktails()) {
            // Vérifier si cocktail préparables (ingrédients disponibles)
            if (!c.isPrepareable(this)) return false;

            // Employee le plus rapide
            Employee fastest = employees.stream()
                    .max((e1, e2) -> e1.getSpeed() - e2.getSpeed())
                    .orElse(employees.get(0));

            if (!fastest.prepare(c, this)) return false;
        }
        return true;
    }

    // ===== Facturation =====
    public double billOrder(Order order) {
        return order.calculateTotal();
    }
}