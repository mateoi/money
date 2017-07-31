package com.mateoi.money.view.controllers;

import com.mateoi.money.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by mateo on 26/07/2017.
 */
abstract class TabController<T> {
    private Stage primaryStage;

    private MainWindowController mainWindowController;


    T editItem(T item, String fxml, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxml));
            Stage dialogStage = new Stage();
            dialogStage.setTitle((edit ? "Edit" : "Create New") + " Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            EditDialogController<T> controller = loader.getController();
            controller.setItem(item);
            controller.setDialogStage(dialogStage);
            dialogStage.getIcons().add(Main.APPLICATION_ICON);
            dialogStage.showAndWait();

            if (controller.isOkPressed()) {
                return controller.getItem();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    private void onSave() {
        mainWindowController.onSave();
    }

    @FXML
    private void onNew() {
        mainWindowController.onNew();
    }

    @FXML
    private void onOpen() {
        mainWindowController.onOpen();
    }

    abstract void onEditItem();

    abstract void onAddItem();

    abstract void onRemoveItem();
}
