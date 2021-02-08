package com.elwarraky.vnstat;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/fxml/mainView.fxml"));
        Service service = new Service();
        Parent root = fxmlLoader.load();

        MainViewController mainViewController = fxmlLoader.getController();
        service.setMainViewController(mainViewController);
        mainViewController.setModel(service);
        Scene scene = new Scene(root);

        stage.setTitle("Vnstat");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}