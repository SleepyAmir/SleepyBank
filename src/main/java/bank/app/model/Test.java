package bank.app.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Test extends Application {
    private static final Logger logger = Logger.getLogger(Test.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting application, loading login.fxml...");
        // Load the FXML file from the resources root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/login.fxml"));
        if (loader.getLocation() == null) {
            logger.error("FXML file location is null. Check if login.fxml exists in src/main/resources/");
            throw new IllegalStateException("Unable to find login.fxml in resources");
        }
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Sleepy Bank Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        logger.info("Launching JavaFX application");
        launch(args);
    }
}