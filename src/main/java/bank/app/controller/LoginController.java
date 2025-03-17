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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginController implements bank.app.model.repository.Repository<User, Integer> {

    @FXML private TextField userNameTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button loginBtn;
    @FXML private Hyperlink forgotPasswordLink;

    @FXML
    private void initialize() {
        loginBtn.setOnAction(event -> handleLogin());
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
    }

    private void handleLogin() {
        String username = userNameTxt.getText().trim();
        String password = passwordTxt.getText().trim();

        System.out.println("Login attempt: Username=" + username + ", Password=" + password);
        try {
            if (username.equals("admin") && password.equals("admin")) {
                System.out.println("Admin login detected, redirecting to registration");
                loadView("/templates/register.fxml", "Bank Manager Registration");
                return;
            }

            User user = authenticate(username, password);
            if (user != null) {
                System.out.println("Login successful for user: " + user.getUsername() + ", ID: " + user.getId());
                System.out.println("Loading mainApp.fxml...");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/mainApp.fxml"));
                Parent root = loader.load();
                System.out.println("mainApp.fxml loaded, getting controller...");
                MainAppController controller = loader.getController();
                System.out.println("Setting current user: " + user.getUsername());
                controller.setCurrentUser(user);

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                System.out.println("Switching scene to dashboard...");
                stage.setScene(new Scene(root));
                stage.setTitle("Sleepy Bank Dashboard");
                stage.show();
                System.out.println("Dashboard displayed.");
            } else {
                System.out.println("Login failed: No matching user found in database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Login error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private User authenticate(String username, String password) throws Exception {
        String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=? AND IS_ACTIVE=1";
        System.out.println("Starting authentication for Username: " + username + ", Password: " + password);
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection()) {
            if (conn == null) {
                throw new SQLException("Connection is null");
            }
            System.out.println("Connection acquired: " + conn);
            System.out.println("Preparing statement with query: " + query);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                System.out.println("Statement prepared: " + stmt);
                System.out.println("Setting parameter 1 (USERNAME) to: " + username);
                stmt.setString(1, username);
                System.out.println("Setting parameter 2 (PASSWORD) to: " + password);
                stmt.setString(2, password);
                System.out.println("Parameters set, executing query...");
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasResult = rs.next();
                    System.out.println("Query executed, result: " + (hasResult ? "User found" : "No user found"));
                    if (hasResult) {
                        User user = mapResultSetToUser(rs);
                        System.out.println("User mapped: " + user.getUsername() + ", Role: " + user.getRole());
                        return user;
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User.UserBuilder builder = User.builder()
                .id(rs.getInt("USER_ID"))
                .firstName(rs.getString("FIRSTNAME"))
                .lastName(rs.getString("LASTNAME"))
                .email(rs.getString("EMAIL"))
                .phone(rs.getString("PHONE"))
                .address(rs.getString("ADDRESS"))
                .birthDate(rs.getDate("BIRTH_DATE") != null ? rs.getDate("BIRTH_DATE").toLocalDate() : null)
                .username(rs.getString("USERNAME"))
                .password(rs.getString("PASSWORD"))
                .role(Role.valueOf(rs.getString("ROLE_NAME")))
                .active(rs.getInt("IS_ACTIVE") == 1);

        try {
            builder.registrationDate(rs.getDate("REGISTRATIONDATE") != null ? rs.getDate("REGISTRATIONDATE").toLocalDate() : null);
        } catch (SQLException e) {
            System.out.println("REGISTRATIONDATE column not found, setting to null");
            builder.registrationDate(null);
        }

        User user = builder.build();
        System.out.println("User built: " + user.toString()); // Detailed user info
        return user;
    }

    private void loadView(String fxmlPath, String title) throws IOException {
        System.out.println("Loading view: " + fxmlPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        System.out.println("View loaded: " + title);
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password functionality to be implemented later");
        alert.showAndWait();
    }

    @Override
    public void save(User user) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO USERS (USER_ID, FIRSTNAME, LASTNAME, EMAIL, PHONE, ADDRESS, BIRTH_DATE, USERNAME, PASSWORD, ROLE_NAME, REGISTRATION_DATE, IS_ACTIVE) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            user.setId(ConnectionProvider.getConnectionProvider().nextId("USER_SEQ"));
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
            stmt.setDate(11, user.getRegistrationDate() != null ? java.sql.Date.valueOf(user.getRegistrationDate()) : java.sql.Date.valueOf(java.time.LocalDate.now()));
            stmt.setInt(12, user.isActive() ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    @Override
    public void edit(User user) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE USERS SET FIRSTNAME=?, LASTNAME=?, EMAIL=?, PHONE=?, ADDRESS=?, BIRTH_DATE=?, USERNAME=?, PASSWORD=?, ROLE_NAME=?, REGISTRATION_DATE=?, IS_ACTIVE=? " +
                             "WHERE USER_ID=?")) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setDate(6, user.getBirthDate() != null ? java.sql.Date.valueOf(user.getBirthDate()) : null);
            stmt.setString(7, user.getUsername());
            stmt.setString(8, user.getPassword());
            stmt.setString(9, user.getRole().name());
            stmt.setDate(10, user.getRegistrationDate() != null ? java.sql.Date.valueOf(user.getRegistrationDate()) : java.sql.Date.valueOf(java.time.LocalDate.now()));
            stmt.setInt(11, user.isActive() ? 1 : 0);
            stmt.setInt(12, user.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM USERS WHERE USER_ID=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERS ORDER BY LASTNAME, FIRSTNAME")) {
            ResultSet rs = stmt.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        }
    }

    @Override
    public User findById(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERS WHERE USER_ID=?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapResultSetToUser(rs) : null;
        }
    }

    @Override
    public void close() throws Exception {
        // No longer needed with try-with-resources
    }
}