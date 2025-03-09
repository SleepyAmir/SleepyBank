package bank.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/login.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root, 289, 280); // Size from your login.fxml prefWidth/prefHeight
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sleepy Bank Login");
        primaryStage.setResizable(false); // Optional: makes the window non-resizable

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application
    }
}