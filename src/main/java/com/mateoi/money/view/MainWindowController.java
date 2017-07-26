package com.mateoi.money.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab transactionTab;

    @FXML
    private Tab accountsTab;

    @FXML
    private Tab savingsTab;

    @FXML
    private Tab budgetTab;

    @FXML
    private Label accountOverviewLabel;

    @FXML
    private Menu openRecentMenu;

    private Stage primaryStage;

    private AccountsController accountsController;

    private BudgetController budgetController;

    private SavingsController savingsController;

    private TransactionController transactionController;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onNew() {

    }

    @FXML
    private void onOpen() {

    }


    @FXML
    private void onSave() {

    }

    @FXML
    private void onSaveAs() {

    }

    @FXML
    private void onClose() {

    }

    @FXML
    private void onAddItem() {
        TabController controller = getSelectedTabController();
        controller.onAddItem();
    }

    @FXML
    private void onEditItem() {
        TabController controller = getSelectedTabController();
        controller.onEditItem();
    }

    @FXML
    private void onRemoveItem() {
        TabController controller = getSelectedTabController();
        controller.onRemoveItem();
    }

    @FXML
    private void onPreferences() {

    }

    private TabController getSelectedTabController() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.equals(transactionTab)) {
            return transactionController;
        } else if (selectedTab.equals(accountsTab)) {
            return accountsController;
        } else if (selectedTab.equals(savingsTab)) {
            return savingsController;
        } else {
            return budgetController;
        }
    }

    public void populateTabs() {
        try {
            transactionController = (TransactionController) loadTab("/TransactionTab.fxml", transactionTab);
            accountsController = (AccountsController) loadTab("/AccountsTab.fxml", accountsTab);
            savingsController = (SavingsController) loadTab("/SavingsTab.fxml", savingsTab);
            budgetController = (BudgetController) loadTab("/BudgetTab.fxml", budgetTab);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private TabController loadTab(String fxml, Tab tab) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxml));
        Node node = loader.load();
        tab.setContent(node);
        TabController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        return controller;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
