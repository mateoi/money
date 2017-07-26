package com.mateoi.money.view;

import com.mateoi.money.io.FileIO;
import com.mateoi.money.model.Accounts;
import com.mateoi.money.model.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        accountOverviewLabel.textProperty().bind(Accounts.getInstance().overviewProperty());
        primaryStage.setOnCloseRequest(event -> onClose());
    }

    @FXML
    private void onNew() {

    }

    @FXML
    private void onOpen() {
        FileChooser chooser = new FileChooser();
        if (Settings.getInstance().getCurrentFile() != null) {
            chooser.setInitialDirectory(Settings.getInstance().getCurrentFile().getParent().toFile());
        }
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            Path asPath = file.toPath();
            try {
                FileIO.getInstance().load(asPath);
                Settings.getInstance().setCurrentFile(asPath);
                Settings.getInstance().addRecentFile(asPath);
            } catch (IOException e) {
                showError("Could not open file");
            }
        }
    }


    @FXML
    private void onSave() {
        Path path = Settings.getInstance().getCurrentFile();
        if (path == null) {
            onSaveAs();
        } else {
            try {
                FileIO.getInstance().save(path);
            } catch (IOException e) {
                showError("Could not save file");
            }
        }
    }

    @FXML
    private void onSaveAs() {
        FileChooser chooser = new FileChooser();
        if (Settings.getInstance().getCurrentFile() != null) {
            chooser.setInitialDirectory(Settings.getInstance().getCurrentFile().getParent().toFile());
        }
        chooser.setTitle("Save As");
        File file = chooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                Path asPath = file.toPath();
                FileIO.getInstance().save(asPath);
                Settings.getInstance().addRecentFile(asPath);
                Settings.getInstance().setCurrentFile(asPath);
            } catch (IOException e) {
                showError("Could not save file");
            }
        }
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

    private void showError(String text) {
        Alert error = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        error.showAndWait();
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
