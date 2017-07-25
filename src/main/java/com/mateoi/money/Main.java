package com.mateoi.money;

import com.mateoi.money.io.FileIO;
import com.mateoi.money.view.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileIO.getInstance().load(Paths.get(getClass().getResource("/ExampleFile.txt").toURI()));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 768));
        MainWindowController controller = loader.getController();
        controller.populateTabs();
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
