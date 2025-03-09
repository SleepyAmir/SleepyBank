package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.repository.utils.ConnectionProvider;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationController implements bank.app.model.repository.Repository<User, Integer> {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button registerButton;

    private Connection connection;
    private PreparedStatement statement;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
    }

    private void handleRegister() {
        try {
            User user = User.builder()
                    .firstName(firstNameField.getText())
                    .lastName(lastNameField.getText())
                    .email(emailField.getText())
                    .phone(phoneField.getText())
                    .address(addressField.getText())
                    .birthDate(birthDatePicker.getValue())
                    .username(usernameField.getText())
                    .password(passwordField.getText())
                    .role(Role.Customer)
                    .active(true)
                    .build();

            save(user);
            System.out.println("User registered successfully");
            // Add navigation back to login or success message
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        user.setId(ConnectionProvider.getConnectionProvider().nextId("user_seq"));
        statement = connection.prepareStatement(
                "INSERT INTO USERS (USER_ID, FIRSTNAME, LASTNAME, EMAIL, PHONE, ADDRESS, BIRTH_DATE, USERNAME, PASSWORD, ROLE_NAME, IS_ACTIVE) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        statement.setInt(1, user.getId());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastName());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getPhone());
        statement.setString(6, user.getAddress());
        statement.setDate(7, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
        statement.setString(8, user.getUsername());
        statement.setString(9, user.getPassword());
        statement.setString(10, user.getRole().name());
        statement.setInt(11, user.isActive() ? 1 : 0);
        statement.executeUpdate();
    }

    @Override
    public void edit(User user) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "UPDATE USERS SET FIRSTNAME=?, LASTNAME=?, EMAIL=?, PHONE=?, ADDRESS=?, BIRTH_DATE=?, USERNAME=?, PASSWORD=?, ROLE_NAME=?, IS_ACTIVE=? " +
                        "WHERE USER_ID=?"
        );
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getPhone());
        statement.setString(5, user.getAddress());
        statement.setDate(6, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
        statement.setString(7, user.getUsername());
        statement.setString(8, user.getPassword());
        statement.setString(9, user.getRole().name());
        statement.setInt(10, user.isActive() ? 1 : 0);
        statement.setInt(11, user.getId());
        statement.executeUpdate();
    }

    @Override
    public void remove(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("DELETE FROM USERS WHERE USER_ID=?");
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    @Override
    public List<User> findAll() throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM USERS ORDER BY LASTNAME, FIRSTNAME");
        ResultSet rs = statement.executeQuery();
        List<User> users = new ArrayList<>();

        while (rs.next()) {
            users.add(mapResultSetToUser(rs));
        }
        return users;
    }

    @Override
    public User findById(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM USERS WHERE USER_ID=?");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return User.builder()
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
                .active(rs.getInt("IS_ACTIVE") == 1)
                .build();
    }
}