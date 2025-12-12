package com.loveinabottle.barcocktail.model;

import java.util.Objects;

public class Ingredient {
    private final String name;
    private final double volumePerUnit;
    private final boolean alcoholic;
    private int stockUnits;

    public Ingredient(String name, double volumePerUnit, boolean alcoholic, int stockUnits) {
        this.name = name;
        this.volumePerUnit = volumePerUnit;
        this.alcoholic = alcoholic;
        this.stockUnits = stockUnits;
    }

    public String getName() { return name; }
    public double getVolumePerUnit() { return volumePerUnit; }
    public boolean isAlcoholic() { return alcoholic; }
    public int getStockUnits() { return stockUnits; }

    public void removeUnits(int u) { this.stockUnits -= u; }
    public void addUnits(int u) { this.stockUnits += u; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + " (stock=" + stockUnits + ")";
    }
}
