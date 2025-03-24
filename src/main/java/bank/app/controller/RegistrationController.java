package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.entity.UserManager;
import bank.app.model.entity.enums.Role;
import bank.app.model.service.DashboardService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RegistrationController {

    // Register Tab
    @FXML private TextField firstNameTxt;
    @FXML private TextField lastNameTxt;
    @FXML private TextField emailTxt;
    @FXML private TextField phoneNumberTxt;
    @FXML private DatePicker birthDate;
    @FXML private TextField addressTxt;
    @FXML private TextField usernameTxt;
    @FXML private TextField passwordTxt;
    @FXML private Button registerBtn;

    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> firstnameCol;
    @FXML private TableColumn<User, String> lastnameCol;
    @FXML private TableColumn<User, LocalDate> dateOfBirthCol;
    @FXML private TableColumn<User, String> phoneNumberCol;
    @FXML private TextField firstNameSearchTxt;
    @FXML private TextField lastNameSearchTxt;
    @FXML private TextField phoneNumberSearchTxt;
    @FXML private TextField emailSearchTxt;
    @FXML private DatePicker dateOfBirthSearchTxt;
    @FXML private TextField addressSearchTxt;
    @FXML private TextField userNameSearchTxt;
    @FXML private TextField passwordSearchTxt;
    @FXML private TextField usernameFindTxt;
    @FXML private TextField firstnameFindTxt;
    @FXML private Button saveBtn;
    @FXML private Button editBtn;
    @FXML private Button removeBtn;
    @FXML private Button reloadBtn;
    @FXML private Button filterBtn;

    // Admin Dashboard Tab
    @FXML private TabPane tabPane;
    @FXML private Tab adminDashboardTab;
    @FXML private TextField totalUsersTxt;
    @FXML private TextField totalBalanceTxt;
    @FXML private TextField newUsersTodayTxt;
    @FXML private PieChart pieCHart;
    @FXML private TextField chartPercentCard;
    @FXML private TextField chartPercentCheque;

    private final UserManager userManager;
    private final DashboardService dashboardService;
    private ObservableList<User> userData;

    // Constructor for dependency injection
    public RegistrationController(UserManager userManager, DashboardService dashboardService) {
        this.userManager = userManager;
        this.dashboardService = dashboardService;
    }
    @FXML
    private void initialize() {
        userData = FXCollections.observableArrayList();
        setupUsersTable();

        // Register Tab
        registerBtn.setOnAction(event -> handleRegister());

        // Users Tab
        loadUsers();
        saveBtn.setOnAction(event -> handleSaveUser());
        editBtn.setOnAction(event -> handleEditUser());
        removeBtn.setOnAction(event -> handleRemoveUser());
        reloadBtn.setOnAction(event -> handleReload());
        filterBtn.setOnAction(event -> filterUsers());
        addSearchListeners();

        // Populate text fields when a user is selected
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !userData.isEmpty()) {
                populateTextFields(newSelection);
            } else if (userData.isEmpty()) {
                clearSearchFields();
            }
        });

        // Admin Dashboard Tab
        adminDashboardTab.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                populateAdminDashboard();
            }
        });
    }

    // Register Tab Logic
    private void handleRegister() {
        try {
            User user = buildUserFromRegisterFields();
            if (user == null) return;
            userManager.saveUser(user);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Please log in.");
            loadView("/templates/login.fxml", "Sleepy Bank Login");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed: " + e.getMessage());
        }
    }

    private User buildUserFromRegisterFields() {
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
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required");
            return null;
        }

        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .address(address)
                .birthDate(birthDateValue)
                .username(username)
                .password(password)
                .role(Role.Customer)
                .active(true)
                .build();
    }

    // Users Tab Logic
    private void setupUsersTable() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateOfBirthCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        usersTable.setItems(userData);
    }

    private void loadUsers() {
        Platform.runLater(() -> {
            try {
                usersTable.getSelectionModel().clearSelection();
                userData.setAll(userManager.findAllUsers());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
            }
        });
    }

    private void populateTextFields(User user) {
        firstNameSearchTxt.setText(user.getFirstName());
        lastNameSearchTxt.setText(user.getLastName());
        phoneNumberSearchTxt.setText(user.getPhone());
        emailSearchTxt.setText(user.getEmail());
        dateOfBirthSearchTxt.setValue(user.getBirthDate());
        addressSearchTxt.setText(user.getAddress());
        userNameSearchTxt.setText(user.getUsername());
        passwordSearchTxt.setText(user.getPassword());
    }

    private void handleSaveUser() {
        try {
            User newUser = buildUserFromSearchFields();
            if (newUser == null) return;
            userManager.saveUser(newUser);
            loadUsers();
            clearSearchFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User saved successfully");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user: " + e.getMessage());
        }
    }

    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to edit");
            return;
        }
        try {
            User updatedUser = buildUserFromSearchFields();
            if (updatedUser == null) return;
            updatedUser.setId(selectedUser.getId());
            updatedUser.setRegistrationDate(selectedUser.getRegistrationDate());
            userManager.editUser(updatedUser);
            loadUsers();
            clearSearchFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user: " + e.getMessage());
        }
    }

    private void handleRemoveUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to remove");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User: " + selectedUser.getUsername());
        confirmation.setContentText("This will also delete all related records. Proceed?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userManager.removeUser(selectedUser.getId());
                loadUsers();
                clearSearchFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User removed successfully");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove user: " + e.getMessage());
            }
        }
    }

    private void handleReload() {
        loadUsers();
        clearSearchFields();
        showAlert(Alert.AlertType.INFORMATION, "Reload", "Users table reloaded successfully");
    }

    private void filterUsers() {
        Platform.runLater(() -> {
            try {
                List<User> filtered = userManager.filterUsers(
                        firstNameSearchTxt.getText(),
                        lastNameSearchTxt.getText(),
                        phoneNumberSearchTxt.getText(),
                        emailSearchTxt.getText(),
                        dateOfBirthSearchTxt.getValue(),
                        addressSearchTxt.getText(),
                        userNameSearchTxt.getText(),
                        passwordSearchTxt.getText()
                );
                userData.setAll(filtered);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter users: " + e.getMessage());
            }
        });
    }

    private User buildUserFromSearchFields() {
        String firstName = firstNameSearchTxt.getText().trim();
        String lastName = lastNameSearchTxt.getText().trim();
        String phone = phoneNumberSearchTxt.getText().trim();
        String email = emailSearchTxt.getText().trim();
        LocalDate birthDate = dateOfBirthSearchTxt.getValue();
        String address = addressSearchTxt.getText().trim();
        String username = userNameSearchTxt.getText().trim();
        String password = passwordSearchTxt.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                address.isEmpty() || birthDate == null || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required");
            return null;
        }

        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .birthDate(birthDate)
                .address(address)
                .username(username)
                .password(password)
                .role(Role.Customer)
                .active(true)
                .build();
    }

    private void addSearchListeners() {
        usernameFindTxt.textProperty().addListener((obs, old, newVal) -> filterUsers());
        firstnameFindTxt.textProperty().addListener((obs, old, newVal) -> filterUsers());
    }

    private void clearSearchFields() {
        firstNameSearchTxt.clear();
        lastNameSearchTxt.clear();
        phoneNumberSearchTxt.clear();
        emailSearchTxt.clear();
        dateOfBirthSearchTxt.setValue(null);
        addressSearchTxt.clear();
        userNameSearchTxt.clear();
        passwordSearchTxt.clear();
        usernameFindTxt.clear();
        firstnameFindTxt.clear();
    }

    // Admin Dashboard Logic
    private void populateAdminDashboard() {
        try {
            totalUsersTxt.setText(String.valueOf(dashboardService.getTotalUsers()));
            totalBalanceTxt.setText(String.format("%.2f", dashboardService.getTotalBalance()));
            newUsersTodayTxt.setText(String.valueOf(dashboardService.getNewUsersToday()));
            double[] distribution = dashboardService.getTransactionDistribution();
            chartPercentCard.setText(String.format("%.1f%%", distribution[0]));
            chartPercentCheque.setText(String.format("%.1f%%", distribution[1]));
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Card", distribution[0]),
                    new PieChart.Data("Cheque", distribution[1])
            );
            pieCHart.setData(pieChartData);
            pieCHart.setTitle("Transaction Distribution");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load admin dashboard: " + e.getMessage());
        }
    }

    // Utility Methods
    private void loadView(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) registerBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}