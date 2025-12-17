package com.loveinabottle.barcocktail.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class Bar {
    private static final int MAX_CONCURRENT_ORDERS = 3;
    private static final int BASE_TIME_PER_INGREDIENT_MS = 500;

    private final List<Ingredient> stock = new ArrayList<>();
    private final List<Cocktail> menu = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    private final Queue<Order> waitingQueue = new ConcurrentLinkedQueue<>();
    private final ListProperty<Order> inProgressOrders = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    private final ListProperty<Order> completedOrders = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_ORDERS);

    // ===== Gestion du stock =====
    public void addIngredient(Ingredient ingredient) {
        stock.add(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return new ArrayList<>(stock);
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
        return new ArrayList<>(menu);
    }

    // ===== Gestion des employés =====
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    // ===== Gestion des commandes =====
    public ObservableList<Order> getInProgressOrders() {
        return inProgressOrders;
    }

    public Queue<Order> getWaitingOrders() {
        return new LinkedList<>(waitingQueue);
    }

    public ObservableList<Order> getCompletedOrders() {
        return completedOrders;
    }

    public int getWaitingOrderCount() {
        return waitingQueue.size();
    }

    // ===== Passage de commande =====
    public Optional<Order> placeOrder(Client client, List<String> cocktailNames) {
        List<Cocktail> selected = new ArrayList<>();
        for (String name : cocktailNames) {
            Cocktail c = menu.stream()
                    .filter(m -> m.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
            if (c == null) return Optional.empty();
            selected.add(c);
        }
        return Optional.of(new Order(client, selected));
    }

    // ===== Soumission de commande à la file d'attente =====
    public void submitOrderForPreparation(Order order) {
        order.setStatus(Order.OrderStatus.QUEUED);
        waitingQueue.offer(order);
        processNextOrder();
    }

    // ===== Traitement des commandes en file d'attente =====
    private synchronized void processNextOrder() {
        if (inProgressOrders.size() >= MAX_CONCURRENT_ORDERS) {
            return;
        }

        Order nextOrder = waitingQueue.poll();
        if (nextOrder != null) {
            startOrderPreparation(nextOrder);
        }
    }

    private void startOrderPreparation(Order order) {
        inProgressOrders.add(order);

        executorService.submit(() -> {
            try {
                prepareOrderAsync(order);
            } catch (Exception e) {
                order.setStatus(Order.OrderStatus.FAILED);
                System.err.println("Erreur: " + e.getMessage());
            } finally {
                inProgressOrders.remove(order);
                if (order.getStatus() == Order.OrderStatus.COMPLETED) {
                    completedOrders.add(order);
                }
                processNextOrder();
            }
        });
    }

    private void prepareOrderAsync(Order order) throws InterruptedException {
        // Assigner le bartender le plus rapide
        Employee fastestBartender = employees.stream()
                .filter(e -> e instanceof Bartender)
                .max(Comparator.comparingInt(Employee::getSpeed))
                .orElse(null);

        if (fastestBartender == null) {
            order.setStatus(Order.OrderStatus.FAILED);
            return;
        }

        // Vérifier la disponibilité des ingrédients
        boolean canPrepare = order.getCocktails().stream()
                .allMatch(c -> c.isPrepareable(this));

        if (!canPrepare) {
            order.setStatus(Order.OrderStatus.FAILED);
            return;
        }

        // Démarrer la préparation
        Platform.runLater(() -> {
            order.setStatus(Order.OrderStatus.IN_PROGRESS);
            order.setTimeStarted(LocalDateTime.now());
            order.setAssignedBartender(fastestBartender);
        });

        // Calculer la durée totale
        int totalIngredients = order.getCocktails().stream()
                .mapToInt(c -> c.getRecipe().size())
                .sum();

        long estimatedDurationMs = Math.round((double) totalIngredients * BASE_TIME_PER_INGREDIENT_MS / fastestBartender.getSpeed());
        long startTime = System.currentTimeMillis();

        // Simuler la préparation
        while (System.currentTimeMillis() - startTime < estimatedDurationMs) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int progress = (int) ((elapsedTime * 100) / estimatedDurationMs);
            final int progressToSet = Math.min(progress, 99);
            Platform.runLater(() -> order.setProgress(progressToSet));
            Thread.sleep(200);
        }

        // Consommer les ingrédients
        for (Cocktail cocktail : order.getCocktails()) {
            cocktail.consumeIngredients(this);
        }

        // Marquer comme terminée
        Platform.runLater(() -> {
            order.setProgress(100);
            order.setStatus(Order.OrderStatus.COMPLETED);
            order.setTimeCompleted(LocalDateTime.now());
        });
    }

    // ===== Préparation de commande (ancienne méthode, conservée pour compatibilité) =====
    public boolean prepareOrder(Order order) {
        if (employees.isEmpty()) return false;

        for (Cocktail c : order.getCocktails()) {
            if (!c.isPrepareable(this)) return false;

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

    // ===== Arrêt du bar =====
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}