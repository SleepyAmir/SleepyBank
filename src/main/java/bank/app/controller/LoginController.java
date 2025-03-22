package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField userNameTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button loginBtn;
    @FXML private Hyperlink forgotPasswordLink;

    private UserRepository userRepository;

    /**
     * Initializes the controller after the FXML fields have been loaded.
     * Sets up the UserRepository and event handlers for the login button and forgot password link.
     */
    @FXML
    private void initialize() {
        try {
            userRepository = new UserRepository();
            loginBtn.setOnAction(event -> handleLogin());
            forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        } catch (Exception e) {
            org.apache.log4j.Logger.getLogger(LoginController.class).error("Failed to initialize LoginController", e);
            showErrorAlert("Initialization Error", "Failed to start application: " + e.getMessage());
        }
    }

    /**
     * Handles the login process by validating user credentials and loading the appropriate view.
     */
    private void handleLogin() {
        String username = userNameTxt.getText().trim();
        String password = passwordTxt.getText().trim();
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LoginController.class);
        logger.info("Login attempt: Username=" + username + ", Password=" + password);

        try {
            // Special case for admin login
            if (username.equals("admin") && password.equals("admin")) {
                logger.info("Admin login detected, loading registration view");
                loadView("/templates/register.fxml", "Bank Manager Registration");
                return;
            }

            // Regular user login via UserRepository
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user != null) {
                logger.info("Login successful for user: " + user.getUsername());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/mainApp.fxml"));
                Parent root = loader.load();
                MainAppController controller = loader.getController();
                controller.setCurrentUser(user);

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Sleepy Bank Dashboard");
                stage.show();
            } else {
                logger.warn("Login failed: Invalid credentials");
                showErrorAlert("Login Failed", "Invalid username or password");
            }
        } catch (Exception e) {
            logger.error("Login error", e);
            showErrorAlert("Error", "An error occurred during login: " + e.getMessage());
        }
    }

    /**
     * Displays a placeholder alert for the forgot password functionality.
     */
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password functionality to be implemented later");
        alert.showAndWait();
    }

    /**
     * Shows an error alert with the specified title and message.
     *
     * @param title   The title of the error alert
     * @param message The message to display in the error alert
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads a view from the specified FXML path and sets it as the scene for the current stage.
     *
     * @param fxmlPath The path to the FXML file
     * @param title    The title of the stage
     * @throws IOException If the FXML file cannot be loaded
     */
    private void loadView(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}