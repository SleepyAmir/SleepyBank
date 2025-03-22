package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button loginBtn;


    @FXML
    public void handleLogin() {
        try {
            logger.info("handleLogin called");
            String username = usernameField.getText();
            String password = passwordField.getText();
            logger.info("Username: " + username + ", Password: " + password);
            UserService userService = new UserService();

            if (username.equals("admin") && password.equals("admin")) {
                logger.info("Admin login detected, loading registration view");
                CardService cardService = new CardService();
                TransactionService transactionService = new TransactionService();
                ChequeService chequeService = new ChequeService();
                UserManager userManager = new UserManager(userService);
                DashboardService dashboardService = new DashboardService(userService, cardService, transactionService, chequeService);
                RegistrationController controller = new RegistrationController(userManager, dashboardService);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/register.fxml"));
                loader.setController(controller);
                Parent root = loader.load();
                logger.info("register.fxml loaded successfully");
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Bank Manager Registration");
                stage.show();
            } else {
                User user = userService.authenticate(username, password);
                if (user != null) {
                    logger.info("User authenticated: " + user.getUsername());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/mainApp.fxml"));
                    Parent root = loader.load();
                    logger.info("mainApp.fxml loaded successfully");
                    MainAppController controller = loader.getController();
                    controller.setCurrentUser(user);
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Bank Application");
                    stage.show();
                } else {
                    logger.warn("Invalid login attempt for username: " + username);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setContentText("Invalid username or password");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            logger.error("Error in handleLogin", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }



}