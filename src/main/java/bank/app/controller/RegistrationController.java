package bank.app.controller;

import bank.app.model.entity.Card;
import bank.app.model.entity.Cheque;
import bank.app.model.entity.Transaction;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import bank.app.model.service.UserService;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @FXML private WebView webView;

    private UserService userService;
    private CardService cardService;
    private TransactionService transactionService;
    private ChequeService chequeService;
    private ObservableList<User> userData;

    @FXML
    private void initialize() {
        userService = new UserService();
        cardService = new CardService();
        transactionService = new TransactionService();
        chequeService = new ChequeService();
        userData = FXCollections.observableArrayList();

        // Register Tab
        registerBtn.setOnAction(event -> handleRegister());

        // Users Tab
        setupUsersTable();
        loadUsers();
        saveBtn.setOnAction(event -> handleSaveUser());
        editBtn.setOnAction(event -> handleEditUser());
        removeBtn.setOnAction(event -> handleRemoveUser());
        reloadBtn.setOnAction(event -> handleReload());
        filterBtn.setOnAction(event -> filterUsers());
        addSearchListeners();

        // Populate text fields when a user is selected in the table
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !userData.isEmpty()) {
                System.out.println("Selected user: " + newSelection.getUsername());
                populateTextFields(newSelection);
            } else if (userData.isEmpty()) {
                System.out.println("No users available to select.");
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
                return;
            }

            User user = User.builder()
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

            userService.save(user);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Please log in.");
            loadView("/templates/login.fxml", "Sleepy Bank Login");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed: " + e.getMessage());
        }
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
                userData.clear();
                List<User> users = userService.findAll();
                System.out.println("Loading users: " + users.size());
                userData.addAll(users);
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
            userService.save(newUser);
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
            userService.edit(updatedUser);
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

        // Confirm deletion with user
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User: " + selectedUser.getUsername());
        confirmation.setContentText("This will also delete all related cards, transactions, and cheques. Proceed?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.remove(selectedUser.getId());
                loadUsers();
                clearSearchFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User and related records removed successfully");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove user: " + e.getMessage());
            }
        }
    }

    private void handleReload() {
        System.out.println("Reloading users table...");
        loadUsers();
        clearSearchFields();
        showAlert(Alert.AlertType.INFORMATION, "Reload", "Users table reloaded successfully");
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
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required to save/edit a user");
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

    private void filterUsers() {
        Platform.runLater(() -> {
            try {
                usersTable.getSelectionModel().clearSelection();
                List<User> allUsers = userService.findAll();
                List<User> filtered = allUsers.stream()
                        .filter(u -> matchesField(u.getFirstName(), firstNameSearchTxt.getText()))
                        .filter(u -> matchesField(u.getLastName(), lastNameSearchTxt.getText()))
                        .filter(u -> matchesField(u.getPhone(), phoneNumberSearchTxt.getText()))
                        .filter(u -> matchesField(u.getEmail(), emailSearchTxt.getText()))
                        .filter(u -> matchesDate(u.getBirthDate(), dateOfBirthSearchTxt.getValue()))
                        .filter(u -> matchesField(u.getAddress(), addressSearchTxt.getText()))
                        .filter(u -> matchesField(u.getUsername(), userNameSearchTxt.getText()))
                        .filter(u -> matchesField(u.getPassword(), passwordSearchTxt.getText()))
                        .filter(u -> matchesField(u.getUsername(), usernameFindTxt.getText()))
                        .filter(u -> matchesField(u.getFirstName(), firstnameFindTxt.getText()))
                        .collect(Collectors.toList());
                userData.clear();
                userData.addAll(filtered);
                System.out.println("Filtered users: " + filtered.size());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter users: " + e.getMessage());
            }
        });
    }

    private boolean matchesField(String value, String filter) {
        return filter.isEmpty() || (value != null && value.toLowerCase().contains(filter.toLowerCase()));
    }

    private boolean matchesDate(LocalDate value, LocalDate filter) {
        return filter == null || (value != null && value.equals(filter));
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
        System.out.println("Populating Admin Dashboard...");
        try {
            List<User> users = userService.findAll();
            System.out.println("Total Users: " + users.size());
            totalUsersTxt.setText(String.valueOf(users.size()));

            List<Card> cards = cardService.findAll();
            System.out.println("Total Cards: " + cards.size());
            double totalBalance = cards.stream().mapToDouble(Card::getBalance).sum();
            totalBalanceTxt.setText(String.format("%.2f", totalBalance));

            LocalDate today = LocalDate.now();
            long newUsersToday = users.stream()
                    .filter(u -> u.getRegistrationDate() != null && u.getRegistrationDate().equals(today))
                    .count();
            System.out.println("New Users Today: " + newUsersToday);
            newUsersTodayTxt.setText(String.valueOf(newUsersToday));

            List<Transaction> transactions = transactionService.findAll();
            System.out.println("Total Transactions: " + transactions.size());
            long cardTransactions = transactions.size();
            List<Cheque> cheques = chequeService.findAll();
            System.out.println("Total Cheques: " + cheques.size());
            long chequeTransactions = cheques.stream()
                    .filter(c -> !c.getReceiver().equals("Available"))
                    .count();

            long totalTransactions = cardTransactions + chequeTransactions;
            double cardPercent = totalTransactions > 0 ? (cardTransactions * 100.0 / totalTransactions) : 0;
            double chequePercent = totalTransactions > 0 ? (chequeTransactions * 100.0 / totalTransactions) : 0;

            System.out.println("Card Percent: " + cardPercent + ", Cheque Percent: " + chequePercent);
            chartPercentCard.setText(String.format("%.1f%%", cardPercent));
            chartPercentCheque.setText(String.format("%.1f%%", chequePercent));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Card", cardPercent),
                    new PieChart.Data("Cheque", chequePercent)
            );
            pieCHart.setData(pieChartData);
            pieCHart.setTitle("Transaction Distribution");

        } catch (Exception e) {
            System.out.println("Admin Dashboard Error: " + e.getMessage());
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