package com.mateoi.money.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private Tab transactionTab;

    @FXML
    private Tab accountsTab;

    @FXML
    private Tab savingsTab;

    @FXML
    private Tab budgetTab;

    private Stage primaryStage;

    @FXML
    private void initialize() {
    }

    public void populateTabs() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/TransactionTab.fxml"));
            Node transaction = loader.load();
            transactionTab.setContent(transaction);
            TransactionController transactionController = loader.getController();
            transactionController.setPrimaryStage(primaryStage);

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/AccountsTab.fxml"));
            Node accounts = loader.load();
            accountsTab.setContent(accounts);
            AccountsController accountsController = loader.getController();
            accountsController.setPrimaryStage(primaryStage);

            Node savings = FXMLLoader.load(getClass().getResource("/SavingsTab.fxml"));
            savingsTab.setContent(savings);
            Node budget = FXMLLoader.load(getClass().getResource("/BudgetTab.fxml"));
            budgetTab.setContent(budget);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
