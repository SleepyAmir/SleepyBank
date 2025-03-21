package bank.app.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public class Test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting application, loading login.fxml...");
        Parent root = FXMLLoader.load(getClass().getResource("/templates/login.fxml"));
        log.info("login.fxml loaded successfully");
        Scene scene = new Scene(root);
        primaryStage.setTitle("SleepyBank - Register");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        log.info("Login window displayed");
    }

    public static void main(String[] args) {
        log.info("Launching JavaFX application");
        launch(args);
    }
}