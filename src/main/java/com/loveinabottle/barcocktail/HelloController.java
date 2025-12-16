package com.loveinabottle.barcocktail;

import com.loveinabottle.barcocktail.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        // Préparer la commande
        boolean success = bar.prepareOrder(currentOrder);

        if (success) {
            double total = bar.billOrder(currentOrder);
            showAlert("Succès",
                     "Commande préparée avec succès !\n\n" +
                     "Client: " + currentOrder.getClient().getName() + "\n" +
                     "Total: " + String.format("%.2f €", total));

            // Réinitialiser
            currentOrder = null;
            prepareOrderButton.setDisable(true);
            clientNameField.clear();
            orderCocktailListView.getSelectionModel().clearSelection();
            orderDetailsArea.clear();
            totalOrderLabel.setText("Total commande: 0.00 €");

            refreshAllData();
            statusLabel.setText("Commande terminée !");
        } else {
            showAlert("Erreur", "Impossible de préparer la commande");
        }
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
