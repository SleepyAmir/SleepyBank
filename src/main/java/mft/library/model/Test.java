package mft.library.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/javafx/mainApp.fxml"));

        // Create the scene
        Scene scene = new Scene(root);

        // Set the stage properties
        primaryStage.setTitle("SecureBank - Register");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Optional: Prevent resizing
        primaryStage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}