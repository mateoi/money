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
            loadTab("/TransactionTab.fxml", transactionTab);
            loadTab("/AccountsTab.fxml", accountsTab);
            loadTab("/SavingsTab.fxml", savingsTab);
            loadTab("/BudgetTab.fxml", budgetTab);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadTab(String fxml, Tab tab) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxml));
        Node node = loader.load();
        tab.setContent(node);
        SubNode controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
