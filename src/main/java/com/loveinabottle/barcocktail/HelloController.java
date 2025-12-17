package com.loveinabottle.barcocktail;

import com.loveinabottle.barcocktail.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    // Référence au bar (sera injectée par HelloApplication)
    private Bar bar;
    private Order currentOrder;

    // ===== Labels =====
    @FXML
    private Label statusLabel;
    @FXML
    private Label ingredientStatusLabel;
    @FXML
    private Label totalOrderLabel;

    // ===== Tab 1: Menu des Cocktails =====
    @FXML
    private ListView<Cocktail> cocktailListView;
    @FXML
    private TextArea cocktailDetailsArea;

    // ===== Tab 2: Stock d'Ingrédients =====
    @FXML
    private ListView<Ingredient> ingredientListView;

    // ===== Tab 3: Commandes =====
    @FXML
    private TextField clientNameField;
    @FXML
    private ListView<Cocktail> orderCocktailListView;
    @FXML
    private Button prepareOrderButton;
    @FXML
    private TextArea orderDetailsArea;

    // ===== Tab 4: Employés =====
    @FXML
    private ListView<Employee> employeeListView;

    // ===== Bottom: Tableau de bord des commandes =====
    @FXML
    private VBox inProgressOrdersContainer;
    @FXML
    private VBox waitingOrdersContainer;
    @FXML
    private VBox completedOrdersContainer;
    @FXML
    private Label statsLabel;

    // ===== Initialisation =====

    public void setBar(Bar bar) {
        this.bar = bar;
        initialize();
    }

    @FXML
    private void initialize() {
        if (bar == null) return;

        // Configuration des ListViews
        setupCocktailListView();
        setupIngredientListView();
        setupEmployeeListView();

        // Configuration du tableau de bord
        setupOrderDashboard();

        // Charger les données
        refreshAllData();

        statusLabel.setText("Bar initialisé avec succès !");
    }

    private void setupCocktailListView() {
        // Affichage personnalisé pour les cocktails
        cocktailListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cocktail cocktail, boolean empty) {
                super.updateItem(cocktail, empty);
                if (empty || cocktail == null) {
                    setText(null);
                } else {
                    setText(cocktail.getName() + " - " + String.format("%.2f €", cocktail.getBasePrice()));
                }
            }
        });

        // Listener pour afficher les détails
        cocktailListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> displayCocktailDetails(newVal)
        );
    }

    private void setupIngredientListView() {
        ingredientListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);
                if (empty || ingredient == null) {
                    setText(null);
                } else {
                    String alcoolText = ingredient.isAlcoholic() ? "🍸" : "🥤";
                    setText(alcoolText + " " + ingredient.getName() +
                            " - Stock: " + ingredient.getStockUnits() + " unités");
                }
            }
        });
    }

    private void setupEmployeeListView() {
        employeeListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
                    setText(null);
                } else {
                    String type = employee instanceof Bartender ? "🍹 Bartender" : "👔 Serveur";
                    setText(type + " - " + employee.getName() +
                            " (Vitesse: " + employee.getSpeed() + ")");
                }
            }
        });
    }

    // ===== Configuration du Tableau de Bord des Commandes =====

    private void setupOrderDashboard() {
        // Listener pour les commandes en cours
        bar.getInProgressOrders().addListener((ListChangeListener<Order>) c -> updateDashboard());

        // Listener pour les commandes terminées
        bar.getCompletedOrders().addListener((ListChangeListener<Order>) c -> updateDashboard());

        // Initialiser le tableau de bord
        updateDashboard();
    }

    private void updateDashboard() {
        updateInProgressOrders();
        updateWaitingOrders();
        updateCompletedOrders();
        updateStats();
    }

    private void updateInProgressOrders() {
        inProgressOrdersContainer.getChildren().clear();

        for (Order order : bar.getInProgressOrders()) {
            VBox orderBox = createOrderDisplay(order, true);
            inProgressOrdersContainer.getChildren().add(orderBox);
        }

        if (bar.getInProgressOrders().isEmpty()) {
            Label label = new Label("Aucune commande en préparation");
            label.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            inProgressOrdersContainer.getChildren().add(label);
        }
    }

    private void updateWaitingOrders() {
        waitingOrdersContainer.getChildren().clear();

        for (Order order : bar.getWaitingOrders()) {
            VBox orderBox = createOrderDisplay(order, false);
            waitingOrdersContainer.getChildren().add(orderBox);
        }

        if (bar.getWaitingOrders().isEmpty()) {
            Label label = new Label("File d'attente vide");
            label.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            waitingOrdersContainer.getChildren().add(label);
        }
    }

    private void updateCompletedOrders() {
        completedOrdersContainer.getChildren().clear();

        // Afficher les 5 dernières commandes terminées
        bar.getCompletedOrders().stream()
                .skip(Math.max(0, bar.getCompletedOrders().size() - 5))
                .forEach(order -> {
                    VBox orderBox = createOrderDisplay(order, false);
                    completedOrdersContainer.getChildren().add(orderBox);
                });

        if (bar.getCompletedOrders().isEmpty()) {
            Label label = new Label("Aucune commande terminée");
            label.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            completedOrdersContainer.getChildren().add(label);
        }
    }

    private VBox createOrderDisplay(Order order, boolean showProgress) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 8; -fx-border-radius: 3;");

        // En-tête avec ID et client
        HBox header = new HBox(10);
        Label idLabel = new Label("ID: " + order.getId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label clientLabel = new Label("👤 " + order.getClient().getName());
        clientLabel.setStyle("-fx-font-weight: bold;");
        header.getChildren().addAll(idLabel, clientLabel);
        box.getChildren().add(header);

        // Cocktails
        StringBuilder cocktails = new StringBuilder("Cocktails: ");
        for (int i = 0; i < order.getCocktails().size(); i++) {
            cocktails.append(order.getCocktails().get(i).getName());
            if (i < order.getCocktails().size() - 1) cocktails.append(", ");
        }
        Label cocktailLabel = new Label(cocktails.toString());
        cocktailLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #34495e;");
        box.getChildren().add(cocktailLabel);

        // Bartender assigné
        if (order.getAssignedBartender() != null) {
            Label bartenderLabel = new Label("🍹 " + order.getAssignedBartender().getName());
            bartenderLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #27ae60;");
            box.getChildren().add(bartenderLabel);
        }

        // Barre de progression
        if (showProgress) {
            HBox progressBox = new HBox(5);
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(150);
            progressBar.progressProperty().bind(order.progressProperty().divide(100.0));
            Label progressLabel = new Label(order.getProgress() + "%");
            progressLabel.setPrefWidth(35);
            progressLabel.setStyle("-fx-font-size: 10px;");
            order.progressProperty().addListener((obs, oldVal, newVal) ->
                    progressLabel.setText(newVal + "%")
            );
            progressBox.getChildren().addAll(progressBar, progressLabel);
            box.getChildren().add(progressBox);
        }

        // Statut
        Label statusLabel = new Label("Statut: " + order.getStatus().getDisplayName());
        String statusColor = switch (order.getStatus()) {
            case QUEUED -> "#e67e22";
            case IN_PROGRESS -> "#3498db";
            case COMPLETED -> "#27ae60";
            case FAILED -> "#e74c3c";
        };
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");
        order.statusProperty().addListener((obs, oldVal, newVal) ->
                statusLabel.setText("Statut: " + newVal.getDisplayName())
        );
        box.getChildren().add(statusLabel);

        return box;
    }

    private void updateStats() {
        int inProgress = bar.getInProgressOrders().size();
        int waiting = bar.getWaitingOrders().size();
        int completed = bar.getCompletedOrders().size();
        statsLabel.setText("En cours: " + inProgress + " | En attente: " + waiting + " | Terminées: " + completed);
    }

    // ===== Rafraîchissement des données =====

    @FXML
    private void onRefresh() {
        refreshAllData();
        statusLabel.setText("Données rafraîchies !");
    }

    private void refreshAllData() {
        if (bar == null) return;

        // Rafraîchir les cocktails
        cocktailListView.setItems(FXCollections.observableArrayList(bar.getMenu()));

        // Rafraîchir et configurer la liste de commande
        orderCocktailListView.setItems(FXCollections.observableArrayList(bar.getMenu()));
        if (orderCocktailListView.getSelectionModel() != null) {
            orderCocktailListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        // Rafraîchir les ingrédients
        ingredientListView.setItems(FXCollections.observableArrayList(bar.getAllIngredients()));

        // Rafraîchir les employés
        employeeListView.setItems(FXCollections.observableArrayList(bar.getEmployees()));
    }

    // ===== Détails des Cocktails =====

    private void displayCocktailDetails(Cocktail cocktail) {
        if (cocktail == null) {
            cocktailDetailsArea.clear();
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Nom: ").append(cocktail.getName()).append("\n");
        details.append("Prix: ").append(String.format("%.2f €", cocktail.getBasePrice())).append("\n\n");
        details.append("Recette:\n");

        cocktail.getRecipe().forEach((ingredient, quantity) -> {
            details.append("  - ").append(ingredient.getName())
                   .append(": ").append(quantity).append(" unités\n");
        });

        details.append("\nDisponible: ")
               .append(cocktail.isPrepareable(bar) ? "✓ Oui" : "✗ Non (stock insuffisant)");

        cocktailDetailsArea.setText(details.toString());
    }

    // ===== Gestion du Stock =====

    @FXML
    private void onRestockIngredient() {
        Ingredient selected = ingredientListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ingredientStatusLabel.setText("⚠ Sélectionnez un ingrédient");
            return;
        }

        selected.addUnits(10);
        ingredientStatusLabel.setText("✓ " + selected.getName() + " réapprovisionné (+10 unités)");
        refreshAllData();
    }

    // ===== Gestion des Commandes =====

    @FXML
    private void onCreateOrder() {
        String clientName = clientNameField.getText().trim();
        if (clientName.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom de client");
            return;
        }

        List<Cocktail> selectedCocktails = new ArrayList<>(
            orderCocktailListView.getSelectionModel().getSelectedItems()
        );

        if (selectedCocktails.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner au moins un cocktail");
            return;
        }

        // Créer le client et la commande
        Client client = new Client(clientName);
        currentOrder = new Order(client, selectedCocktails);

        // Afficher les détails
        displayOrderDetails();

        // Activer le bouton de préparation
        prepareOrderButton.setDisable(false);

        statusLabel.setText("Commande créée pour " + clientName);
    }

    @FXML
    private void onPrepareOrder() {
        if (currentOrder == null) {
            showAlert("Erreur", "Aucune commande en cours");
            return;
        }

        // Vérifier si la commande peut être préparée
        boolean canPrepare = true;
        StringBuilder issues = new StringBuilder();

        for (Cocktail cocktail : currentOrder.getCocktails()) {
            if (!cocktail.isPrepareable(bar)) {
                canPrepare = false;
                issues.append("- ").append(cocktail.getName())
                      .append(" (stock insuffisant)\n");
            }
        }

        if (!canPrepare) {
            showAlert("Stock Insuffisant",
                     "Impossible de préparer la commande:\n\n" + issues.toString());
            return;
        }

        // Soumettre la commande à la file d'attente
        bar.submitOrderForPreparation(currentOrder);

        double total = bar.billOrder(currentOrder);
        showAlert("Succès",
                 "Commande ajoutée à la file d'attente !\n\n" +
                 "Client: " + currentOrder.getClient().getName() + "\n" +
                 "Total: " + String.format("%.2f €", total) + "\n\n" +
                 "Position dans la file: " + bar.getWaitingOrders().size());

        // Réinitialiser
        currentOrder = null;
        prepareOrderButton.setDisable(true);
        clientNameField.clear();
        orderCocktailListView.getSelectionModel().clearSelection();
        orderDetailsArea.clear();
        totalOrderLabel.setText("Total commande: 0.00 €");

        refreshAllData();
        statusLabel.setText("Commande en file d'attente !");
    }

    private void displayOrderDetails() {
        if (currentOrder == null) {
            orderDetailsArea.clear();
            totalOrderLabel.setText("Total commande: 0.00 €");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Client: ").append(currentOrder.getClient().getName()).append("\n");
        details.append("ID: ").append(currentOrder.getClient().getId()).append("\n\n");
        details.append("Cocktails commandés:\n");

        double total = 0;
        for (Cocktail cocktail : currentOrder.getCocktails()) {
            details.append("  - ").append(cocktail.getName())
                   .append(" (").append(String.format("%.2f €", cocktail.getBasePrice())).append(")\n");
            total += cocktail.getBasePrice();
        }

        details.append("\nTotal: ").append(String.format("%.2f €", total));

        orderDetailsArea.setText(details.toString());
        totalOrderLabel.setText("Total commande: " + String.format("%.2f €", total));
    }

    // ===== Utilitaires =====

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
