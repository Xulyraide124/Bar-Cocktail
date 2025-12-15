package com.loveinabottle.barcocktail.model;

public abstract class Employee implements Preparateur {
    protected final String id;
    protected final String name;
    protected final int speed; // units per preparation

    public Employee(String id, String name, int speed) {
        this.id = id;
        this.name = name;
        this.speed = speed;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getSpeed() { return speed; }

    @Override
    public String toString() {
        return name + " (speed=" + speed + ")";
    }
}