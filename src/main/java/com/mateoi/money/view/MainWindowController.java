package com.mateoi.money.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

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

    @FXML
    private void initialize() {
    }

    public void populateTabs() {
        try {
            Node transaction = FXMLLoader.load(getClass().getResource("/TransactionTab.fxml"));
            transactionTab.setContent(transaction);
            Node accounts = FXMLLoader.load(getClass().getResource("/AccountsTab.fxml"));
            accountsTab.setContent(accounts);
            Node savings = FXMLLoader.load(getClass().getResource("/SavingsTab.fxml"));
            savingsTab.setContent(savings);
            Node budget = FXMLLoader.load(getClass().getResource("/BudgetTab.fxml"));
            budgetTab.setContent(budget);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
