package com.loveinabottle.barcocktail.model;

import java.util.List;

public class Order {
    private final Client client;
    private final List<Cocktail> cocktails;

    public Order(Client client, List<Cocktail> cocktails) {
        this.client = client;
        this.cocktails = cocktails;
    }

    public Client getClient() { return client; }
    public List<Cocktail> getCocktails() { return cocktails; }

    public double calculateTotal() {
        return cocktails.stream().mapToDouble(Cocktail::getBasePrice).sum();
    }

    @Override
    public String toString() {
        return "Order for " + client.getName() + ": " + cocktails;
    }
}