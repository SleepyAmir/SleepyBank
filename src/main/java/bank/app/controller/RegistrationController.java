package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.repository.utils.ConnectionProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrationController {

    @FXML private TextField firstNameTxt;
    @FXML private TextField lastNameTxt;
    @FXML private TextField emailTxt;
    @FXML private TextField phoneNumberTxt;
    @FXML private DatePicker birthDate;
    @FXML private TextField addressTxt;
    @FXML private TextField usernameTxt;
    @FXML private TextField passwordTxt; // Note: Typo in FXML ("lasswordTxt" instead of "passwordTxt")
    @FXML private Button registerBtn;

    @FXML
    private void initialize() {
        registerBtn.setOnAction(event -> handleRegister());
    }

    private void handleRegister() {
        try {
            // Collect and validate input
            String firstName = firstNameTxt.getText().trim();
            String lastName = lastNameTxt.getText().trim();
            String email = emailTxt.getText().trim();
            String phone = phoneNumberTxt.getText().trim();
            String address = addressTxt.getText().trim();
            LocalDate birthDateValue = birthDate.getValue();
            String username = usernameTxt.getText().trim();
            String password = passwordTxt.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                    address.isEmpty() || birthDateValue == null || username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("All fields are required");
                alert.showAndWait();
                return;
            }

            // Create User object
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .birthDate(birthDateValue)
                    .username(username)
                    .password(password) // Plaintext as per your preference
                    .role(Role.Customer) // Default role for new users
                    .active(true)
                    .build();

            // Save to database
            save(user);

            // Show success message and redirect to login
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Registration successful! Please log in.");
            alert.showAndWait();

            // Redirect to login screen
            loadView("/javafx/views/login.fxml", "Sleepy Bank Login");

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Registration failed: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) registerBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private void save(User user) throws SQLException {
        try (Connection connection = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO USERS (USER_ID, FIRSTNAME, LASTNAME, EMAIL, PHONE, ADDRESS, BIRTH_DATE, USERNAME, PASSWORD, ROLE_NAME, IS_ACTIVE) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            user.setId(ConnectionProvider.getConnectionProvider().nextId("user_seq"));
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.setDate(7, user.getBirthDate() != null ? java.sql.Date.valueOf(user.getBirthDate()) : null);
            stmt.setString(8, user.getUsername());
            stmt.setString(9, user.getPassword());
            stmt.setString(10, user.getRole().name());
            stmt.setInt(11, user.isActive() ? 1 : 0);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}