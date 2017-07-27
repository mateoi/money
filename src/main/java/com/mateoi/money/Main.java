package com.mateoi.money;

import com.mateoi.money.io.FileIO;
import com.mateoi.money.model.Settings;
import com.mateoi.money.view.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FileIO.getInstance().loadSettings(Settings.SETTINGS_LOCATION);
        } catch (IOException ignored) {
            // Settings not found, continue...
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 768));
        MainWindowController controller = loader.getController();
        controller.populateTabs();
        controller.setPrimaryStage(primaryStage);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Icons/icons8-Money Box-96.png")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
