package com.mateoi.money.view;

import com.mateoi.money.io.FileIO;
import com.mateoi.money.model.Accounts;
import com.mateoi.money.model.MainState;
import com.mateoi.money.model.Settings;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    private StringBinding windowTitleBinding = Bindings.createStringBinding(this::createWindowTitle,
            Settings.getInstance().currentFileProperty(), MainState.getInstance().modifiedProperty());


    @FXML
    private void initialize() {
        accountOverviewLabel.textProperty().bind(Accounts.getInstance().overviewProperty());
        openRecentMenu.getItems().addAll(createRecentMenuItems(Settings.getInstance().getRecentFiles()));
        Settings.getInstance().getRecentFiles().addListener((ListChangeListener<? super Path>) c ->
                openRecentMenu.getItems().setAll(createRecentMenuItems(Settings.getInstance().getRecentFiles())));
    }

    @FXML
    private void onNew() {
        if (MainState.getInstance().isModified()) {
            ButtonType discard = new ButtonType("Discard");
            ButtonType save = new ButtonType("Save");
            Alert prompt = new Alert(Alert.AlertType.CONFIRMATION,
                    "There are unsaved changes. Do you really want to discard them and create a new file?",
                    discard, save, ButtonType.CANCEL);
            prompt.setTitle("Discard Changes?");
            ButtonType result = prompt.showAndWait().orElse(ButtonType.CANCEL);
            if (result.equals(discard)) {
                Settings.getInstance().setCurrentFile(null);
                MainState.getInstance().clearAll();
            } else if (result.equals(save)) {
                onSave();
                Settings.getInstance().setCurrentFile(null);
                MainState.getInstance().clearAll();
            }
        } else {
            Settings.getInstance().setCurrentFile(null);
            MainState.getInstance().clearAll();
        }

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
            openFile(asPath);
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
                MainState.getInstance().setModified(false);
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
                MainState.getInstance().setModified(false);
            } catch (IOException e) {
                showError("Could not save file");
            }
        }
    }

    @FXML
    private void onClose() {
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
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

    private MenuItem createMenuItem(Path path) {
        MenuItem menuItem = new MenuItem(path.getFileName().toString());
        menuItem.setOnAction(event -> {
            if (MainState.getInstance().isModified()) {
                ButtonType discard = new ButtonType("Discard");
                ButtonType save = new ButtonType("Save");
                Alert prompt = new Alert(Alert.AlertType.CONFIRMATION,
                        "There are unsaved changes. Do you really want to discard them and create a new file?",
                        discard, save, ButtonType.CANCEL);
                prompt.setTitle("Discard Changes?");
                ButtonType result = prompt.showAndWait().orElse(ButtonType.CANCEL);
                if (result.equals(discard)) {
                    openFile(path);
                } else if (result.equals(save)) {
                    onSave();
                    openFile(path);
                }
            } else {
                openFile(path);
            }


        });
        return menuItem;
    }

    private List<MenuItem> createRecentMenuItems(List<Path> paths) {
        List<MenuItem> menuItems = new ArrayList<>(paths.size());
        for (int i = paths.size() - 1; i >= 0; i--) {
            menuItems.add(createMenuItem(paths.get(i)));
        }
        return menuItems;
    }

    private void openFile(Path path) {
        try {
            Settings.getInstance().setCurrentFile(path);
            Settings.getInstance().addRecentFile(path);
            FileIO.getInstance().load(path);
            MainState.getInstance().setModified(false);
        } catch (IOException e) {
            showError("Could not open file");
        }
    }

    private void closeWindow(WindowEvent event) {
        if (MainState.getInstance().isModified()) {
            ButtonType quit = new ButtonType("Quit");
            ButtonType save = new ButtonType("Save");
            Alert prompt = new Alert(Alert.AlertType.CONFIRMATION,
                    "There are unsaved changes. Do you really want to quit?", quit, save, ButtonType.CANCEL);
            prompt.setTitle("Save before exit?");
            ButtonType result = prompt.showAndWait().orElse(ButtonType.CANCEL);
            if (result.equals(ButtonType.CANCEL)) {
                event.consume();
            } else if (result.equals(save)) {
                onSave();
            }
        }

    }

    private String createWindowTitle() {
        String title = "MoneyApp - ";
        Path file = Settings.getInstance().getCurrentFile();
        if (file == null) {
            title += "New File";
        } else {
            title += file.getFileName().toString();
        }

        if (MainState.getInstance().isModified()) {
            title += "*";
        }
        return title;
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
        primaryStage.setOnCloseRequest(this::closeWindow);
        primaryStage.titleProperty().bind(windowTitleBinding);
    }
}
