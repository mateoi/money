package com.mateoi.money.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by mateo on 26/07/2017.
 */
class TabController<T> {
    private Stage primaryStage;

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

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
            dialogStage.showAndWait();

            if (controller.isOkPressed()) {
                return controller.getItem();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
