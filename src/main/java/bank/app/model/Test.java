package bank.app.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/templates/login.fxml"));


        Scene scene = new Scene(root);

        primaryStage.setTitle("SecureBank - Register");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file
//        Parent root = FXMLLoader.load(getClass().getResource("/mainApp.fxml"));
//
//        // Create the scene
//        Scene scene = new Scene(root);
//
//        // Set the stage properties
//        primaryStage.setTitle("SecureBank - Register");
//        primaryStage.setScene(scene);
//        primaryStage.setResizable(false); // Optional: Prevent resizing
//        primaryStage.show();
//    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file
//        Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
//
//        // Create the scene
//        Scene scene = new Scene(root);
//
//        // Set the stage properties
//        primaryStage.setTitle("SecureBank - Register");
//        primaryStage.setScene(scene);
//        primaryStage.setResizable(false); // Optional: Prevent resizing
//        primaryStage.show();
//    }


//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file
//        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
//
//        // Create the scene
//        Scene scene = new Scene(root);
//
//        // Set the stage properties
//        primaryStage.setTitle("SecureBank - Register");
//        primaryStage.setScene(scene);
//        primaryStage.setResizable(false); // Optional: Prevent resizing
//        primaryStage.show();
//    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}