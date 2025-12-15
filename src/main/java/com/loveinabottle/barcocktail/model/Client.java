package com.loveinabottle.barcocktail.model;

import java.util.UUID;

public class Client {
    private final String id;
    private final String name;

    public Client(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}