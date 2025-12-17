package com.loveinabottle.barcocktail.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    public enum OrderStatus {
        QUEUED("En attente"), IN_PROGRESS("En préparation"), COMPLETED("Terminée"), FAILED("Échouée");

        private final String displayName;
        OrderStatus(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private final String id;
    private final Client client;
    private final List<Cocktail> cocktails;
    private final ObjectProperty<OrderStatus> status = new SimpleObjectProperty<>(OrderStatus.QUEUED);
    private final IntegerProperty progress = new SimpleIntegerProperty(0);
    private LocalDateTime timeStarted;
    private LocalDateTime timeCompleted;
    private Employee assignedBartender;

    public Order(Client client, List<Cocktail> cocktails) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.client = client;
        this.cocktails = cocktails;
    }

    // Getters
    public String getId() { return id; }
    public Client getClient() { return client; }
    public List<Cocktail> getCocktails() { return cocktails; }

    public OrderStatus getStatus() { return status.get(); }
    public ObjectProperty<OrderStatus> statusProperty() { return status; }

    public int getProgress() { return progress.get(); }
    public IntegerProperty progressProperty() { return progress; }

    public LocalDateTime getTimeStarted() { return timeStarted; }
    public LocalDateTime getTimeCompleted() { return timeCompleted; }
    public Employee getAssignedBartender() { return assignedBartender; }

    // Setters
    public void setStatus(OrderStatus newStatus) { status.set(newStatus); }
    public void setProgress(int value) { progress.set(Math.min(100, Math.max(0, value))); }
    public void setTimeStarted(LocalDateTime time) { timeStarted = time; }
    public void setTimeCompleted(LocalDateTime time) { timeCompleted = time; }
    public void setAssignedBartender(Employee bartender) { assignedBartender = bartender; }

    public double calculateTotal() {
        return cocktails.stream().mapToDouble(Cocktail::getBasePrice).sum();
    }

    @Override
    public String toString() {
        return "Order#" + id + " for " + client.getName();
    }
}