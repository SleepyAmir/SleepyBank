package bank.app;

import bank.app.model.repository.utils.DatabaseMaker;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) throws Exception {
        DatabaseMaker.createDatabase();
//        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }
}