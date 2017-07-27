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

import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;

public class Main extends Application {
    public static Image APPLICATION_ICON = new Image(Main.class.getResourceAsStream("/Icons/icons8-Money Box-96.png"));

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
        primaryStage.getIcons().add(APPLICATION_ICON);
        primaryStage.show();
    }


    public static void main(String[] args) {
        // Force loading of currency conversions
        CurrencyConversion conversion = MonetaryConversions.getConversion("USD");
        launch(args);
    }
}
