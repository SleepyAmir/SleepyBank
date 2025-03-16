package bank.app.controller;

import bank.app.model.entity.*;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import bank.app.model.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Log4j
public class MainAppController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab fundTab;
    @FXML private Tab transactionHistoryTab;
    @FXML private Tab userInfoTab;
    @FXML private Tab transactionManagementTab;

    @FXML private Pane dashboardPane;
    @FXML private Pane fundTransferPane;
    @FXML private Pane transactionHistoryPane;
    @FXML private Pane userInfoPane;
    @FXML private Pane transactionManagementPane;

    @FXML private TextField accountBalanceTxt;
    @FXML private TextField cardNumberTxt;
    @FXML private TextField cvvTxt;
    @FXML private TextField expiryTxt;
    @FXML private Button viewTransactionBtn;
    @FXML private Button transferFundsBtn;
    @FXML private TextField totalChequeBtn;
    @FXML private TextField chequeAddressTxt;
    @FXML private TextField pendingChequeBtn;
    @FXML private TextField cashedChequeBtn;
    @FXML private TextField bouncedChequeBtn;
    @FXML private Button chequeBtn;
    @FXML private Button manageChequeBtn;
    @FXML private TextField savingBalanceTxt;
    @FXML private TextField interestRateTxt;
    @FXML private Button savingTransferTxt;

    @FXML private TextField cardNumberTxt1;
    @FXML private TextField cvvTxt1;
    @FXML private TextField expiryTxt1;

    @FXML private CheckBox byCardCheckBox;
    @FXML private CheckBox byCheckCheckBox;
    @FXML private TextField transferCardNumberTxt;
    @FXML private TextField transferCvvTxt;
    @FXML private TextField transferAmountTxt;
    @FXML private TextField receiverNumberTxt;
    @FXML private Button transferConfirmTxt;
    @FXML private Button transferCancelTxt;
    @FXML private TextField transferChequeTxt; // Used if not using ComboBox
    @FXML private ComboBox<String> transferChequeCombo; // Optional: Replace transferChequeTxt
    @FXML private TextField transferChequeAmountTxt;
    @FXML private TextField transferChequeAddressTxt; // Now "Receiver Card Number"
    @FXML private TextField chequeDescriptionTxt;
    @FXML private DatePicker chequeDate;
    @FXML private Button issueChequeBtn;
    @FXML private Button cancelChequeBtn;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> accountCol;
    @FXML private TableColumn<Transaction, TransactionType> typeCol;
    @FXML private TableColumn<Transaction, Double> amountCol;
    @FXML private TableColumn<Transaction, String> statusCol;
    @FXML private TableColumn<Transaction, String> descriptionCol;
    @FXML private Button printTableBtn;

    @FXML private TextField firstNameInfoTxt;
    @FXML private TextField lastNameInfoTxt;
    @FXML private TextField emailAddressInfoTxt;
    @FXML private TextField dateOfBirthInfoTxt;
    @FXML private TextField phoneNumberInfoTxt;
    @FXML private TextField addressInfoTxt;
    @FXML private TextField userNameInfoTxt;
    @FXML private TextField passwordInfoTxt;
    @FXML private Button editBtn;

    @FXML private StackedBarChart<String, Number> transactionsChart;

    private User currentUser;
    private CardService cardService;
    private ChequeService chequeService;
    private TransactionService transactionService;
    private UserService userService;
    private ObservableList<Transaction> transactionData;
    private boolean isEditMode = false;
    private String userChequeAddress; // Unique cheque address per user

    {
        try {
            cardService = new CardService();
            chequeService = new ChequeService();
            transactionService = new TransactionService();
            userService = new UserService();
            transactionData = FXCollections.observableArrayList();
        } catch (Exception e) {
            log.error("Failed to initialize services", e);
            throw new RuntimeException("Failed to initialize services", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("MainAppController initialized");

        if (transferCardNumberTxt == null) log.error("transferCardNumberTxt is null");
        if (transferConfirmTxt == null) log.error("transferConfirmTxt is null");

        transferFundsBtn.setOnAction(event -> {
            log.info("Transfer Funds clicked");
            tabPane.getSelectionModel().select(fundTab);
            populateFundTransferTab();
        });
        viewTransactionBtn.setOnAction(event -> {
            tabPane.getSelectionModel().select(transactionHistoryTab);
            loadTransactionHistory();
        });
        transferConfirmTxt.setOnAction(event -> confirmTransfer());
        transferCancelTxt.setOnAction(event -> resetTransferForm());
        chequeBtn.setOnAction(event -> useChequeForPurchase());
        manageChequeBtn.setOnAction(event -> manageCheques());
        savingTransferTxt.setOnAction(event -> transferToChecking());
        issueChequeBtn.setOnAction(event -> issueCheque());
        cancelChequeBtn.setOnAction(event -> resetTransferForm());
        printTableBtn.setOnAction(event -> printTransactions());
        editBtn.setOnAction(event -> toggleEditMode());

        byCardCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                byCheckCheckBox.setSelected(false);
                enableCardFields(true);
                enableChequeFields(false);
            } else if (!byCheckCheckBox.isSelected()) {
                enableCardFields(false);
                enableChequeFields(false);
            }
        });

        byCheckCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                byCardCheckBox.setSelected(false);
                enableCardFields(false);
                enableChequeFields(true);
                if (transferChequeCombo != null) populateChequeCombo();
            } else if (!byCardCheckBox.isSelected()) {
                enableCardFields(false);
                enableChequeFields(false);
            }
        });

        setupTransactionTableColumns();
        setupTransactionChart();

        setUserInfoEditable(false);
        accountBalanceTxt.setEditable(false);
        cardNumberTxt.setEditable(false);
        cvvTxt.setEditable(false);
        expiryTxt.setEditable(false);
        cardNumberTxt1.setEditable(false);
        cvvTxt1.setEditable(false);
        expiryTxt1.setEditable(false);
        chequeAddressTxt.setEditable(false);
        totalChequeBtn.setEditable(false);
        pendingChequeBtn.setEditable(false);
        cashedChequeBtn.setEditable(false);
        bouncedChequeBtn.setEditable(false);

        // Set prompt text for clarity
        transferChequeAddressTxt.setPromptText("Receiver Card Number (e.g., 1383-XXXX-XXXX-XXXX)");

        resetDashboard();
        resetTransferForm();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        log.info("Logged in user: " + (user != null ? user.getUsername() : "null"));
        populateDashboard();
        loadTransactionHistory();
        populateUserInfo();
        loadTransactionChart();
    }

    private void setupTransactionTableColumns() {
        accountCol.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            Account sourceAccount = transaction.getSourceAccount();
            if (sourceAccount instanceof Card) {
                return new javafx.beans.property.SimpleStringProperty(((Card) sourceAccount).getCardNumber());
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Completed"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        transactionTable.setItems(transactionData);
    }

    private void setupTransactionChart() {
        transactionsChart.setTitle("Transaction Amounts by Date");
    }

    private void loadTransactionChart() {
        transactionsChart.getData().clear();
        try {
            List<Transaction> transactions = transactionService.findByUserId(currentUser.getId());
            log.info("Loading chart with " + transactions.size() + " transactions");

            transactions.forEach(t -> log.info("Transaction: Date=" + t.getTransactionTime().toLocalDate() +
                    ", Type=" + t.getTransactionType() +
                    ", Amount=" + t.getAmount()));

            Map<String, Map<TransactionType, Double>> groupedData = transactions.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTransactionTime().toLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                            Collectors.groupingBy(
                                    Transaction::getTransactionType,
                                    Collectors.summingDouble(Transaction::getAmount)
                            )
                    ));

            XYChart.Series<String, Number> transferSeries = new XYChart.Series<>();
            transferSeries.setName("Transfer");

            XYChart.Series<String, Number> depositSeries = new XYChart.Series<>();
            depositSeries.setName("Deposit");

            XYChart.Series<String, Number> withdrawalSeries = new XYChart.Series<>();
            withdrawalSeries.setName("Withdrawal");

            groupedData.forEach((date, typeMap) -> {
                transferSeries.getData().add(new XYChart.Data<>(date, typeMap.getOrDefault(TransactionType.Transfer, 0.0)));
                depositSeries.getData().add(new XYChart.Data<>(date, typeMap.getOrDefault(TransactionType.Deposit, 0.0)));
                withdrawalSeries.getData().add(new XYChart.Data<>(date, typeMap.getOrDefault(TransactionType.Withdraw, 0.0)));
            });

            transactionsChart.getData().addAll(transferSeries, depositSeries, withdrawalSeries);
            log.info("Transaction chart loaded with " + transactions.size() + " transactions across " + groupedData.size() + " dates");
        } catch (Exception e) {
            log.error("Error loading transaction chart", e);
            showError("Failed to load transaction chart: " + e.getMessage());
        }
    }

    private void loadTransactionHistory() {
        try {
            transactionData.clear();
            List<Transaction> transactions = transactionService.findByUserId(currentUser.getId());
            transactionData.addAll(transactions);
            log.info("Loaded " + transactions.size() + " transactions for user " + currentUser.getUsername());
        } catch (Exception e) {
            log.error("Error loading transaction history", e);
            showError("Failed to load transaction history: " + e.getMessage());
        }
    }

    private void populateUserInfo() {
        if (currentUser == null) {
            log.error("currentUser is null in populateUserInfo");
            return;
        }
        try {
            firstNameInfoTxt.setText(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");
            lastNameInfoTxt.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "");
            emailAddressInfoTxt.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            dateOfBirthInfoTxt.setText(currentUser.getBirthDate() != null ? currentUser.getBirthDate().toString() : "");
            phoneNumberInfoTxt.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            addressInfoTxt.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
            userNameInfoTxt.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "");
            passwordInfoTxt.setText(currentUser.getPassword() != null ? currentUser.getPassword() : "");
        } catch (Exception e) {
            log.error("Error populating user info", e);
            showError("Failed to load user info: " + e.getMessage());
        }
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        setUserInfoEditable(isEditMode);
        if (isEditMode) {
            editBtn.setText("Save");
        } else {
            saveUserInfo();
            editBtn.setText("Edit");
        }
    }

    private void setUserInfoEditable(boolean editable) {
        emailAddressInfoTxt.setEditable(editable);
        phoneNumberInfoTxt.setEditable(editable);
        addressInfoTxt.setEditable(editable);
    }

    private void saveUserInfo() {
        if (currentUser == null) {
            log.error("currentUser is null in saveUserInfo");
            return;
        }
        try {
            currentUser.setEmail(emailAddressInfoTxt.getText());
            currentUser.setPhone(phoneNumberInfoTxt.getText());
            currentUser.setAddress(addressInfoTxt.getText());
            userService.save(currentUser);
            log.info("User info saved for " + currentUser.getUsername());
            showInfo("User information updated successfully");
        } catch (Exception e) {
            log.error("Error saving user info", e);
            showError("Failed to save user info: " + e.getMessage());
        }
    }

    private void populateFundTransferTab() {
        log.info("Populating Fund Transfer tab");
        if (currentUser == null) {
            log.error("currentUser is null");
            showError("No user logged in");
            return;
        }
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            if (!cards.isEmpty()) {
                transferCardNumberTxt.setText(formatCardNumber(cards.get(0).getCardNumber()));
                log.info("Default card set: " + transferCardNumberTxt.getText());
            } else {
                log.warn("No cards to display");
            }

            if (byCheckCheckBox.isSelected() && transferChequeCombo != null) {
                populateChequeCombo();
            }
        } catch (Exception e) {
            log.error("Error setting default card or cheques", e);
            showError("Failed to set default card or cheques: " + e.getMessage());
        }
    }

    private void enableCardFields(boolean enable) {
        transferCardNumberTxt.setDisable(!enable);
        transferCvvTxt.setDisable(!enable);
        transferAmountTxt.setDisable(!enable);
        receiverNumberTxt.setDisable(!enable);
    }

    private void enableChequeFields(boolean enable) {
        if (transferChequeCombo != null) {
            transferChequeCombo.setDisable(!enable);
        } else {
            transferChequeTxt.setDisable(!enable);
        }
        transferChequeAmountTxt.setDisable(!enable);
        transferChequeAddressTxt.setDisable(!enable);
        chequeDescriptionTxt.setDisable(!enable);
        chequeDate.setDisable(!enable);
        issueChequeBtn.setDisable(!enable);
        cancelChequeBtn.setDisable(!enable);
    }

    private void populateChequeCombo() {
        if (transferChequeCombo == null) return;
        try {
            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            ObservableList<String> chequeNumbers = FXCollections.observableArrayList(
                    cheques.stream()
                            .filter(c -> c.getReceiver().equals(userChequeAddress))
                            .map(Cheque::getNumber)
                            .collect(Collectors.toList())
            );
            transferChequeCombo.setItems(chequeNumbers);
            if (!chequeNumbers.isEmpty()) {
                transferChequeCombo.getSelectionModel().selectFirst();
            }
            log.info("Populated cheque combo with " + chequeNumbers.size() + " options");
        } catch (Exception e) {
            log.error("Error populating cheque combo", e);
            showError("Failed to load cheques: " + e.getMessage());
        }
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("1383");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateChequeAddress() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("5555");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String formatCardNumber(String raw) {
        if (raw == null) {
            log.warn("Attempted to format null card number or cheque address");
            return "N/A";
        }
        return raw.substring(0, 4) + "-" + raw.substring(4, 8) + "-" +
                raw.substring(8, 12) + "-" + raw.substring(12, 16);
    }

    private void populateDashboard() {
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            Card card = cards.isEmpty() ? createDefaultCard() : cards.get(0);
            String formatted = formatCardNumber(card.getCardNumber());
            accountBalanceTxt.setText(String.valueOf(card.getBalance()));
            cardNumberTxt.setText(formatted);
            cvvTxt.setText(card.getCvv2());
            expiryTxt.setText(card.getExpiryDate().toString());
            cardNumberTxt1.setText(formatted);
            cvvTxt1.setText(card.getCvv2());
            expiryTxt1.setText(card.getExpiryDate().toString());

            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            if (cheques.isEmpty()) {
                userChequeAddress = generateChequeAddress();
                log.info("Generated new cheque address for user " + currentUser.getUsername() + ": " + userChequeAddress);
                createDefaultCheques();
                cheques = chequeService.findByUserId(currentUser.getId());
            } else {
                List<Cheque> finalCheques = cheques;
                userChequeAddress = cheques.stream()
                        .filter(c -> c.getReceiver().startsWith("5555"))
                        .map(Cheque::getReceiver)
                        .findFirst()
                        .orElseGet(() -> {
                            String newAddress = generateChequeAddress();
                            log.info("No valid cheque address found, generated: " + newAddress);
                            finalCheques.stream()
                                    .filter(c -> c.getReceiver().equals("Pending") && c.getAmount() == 0)
                                    .forEach(c -> {
                                        c.setReceiver(newAddress);
                                        try {
                                            chequeService.save(c);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                        log.info("Fixed cheque " + c.getNumber() + " to " + newAddress);
                                    });
                            return newAddress;
                        });
            }

            totalChequeBtn.setText(String.valueOf(cheques.size()));
            chequeAddressTxt.setText(formatCardNumber(userChequeAddress));
            pendingChequeBtn.setText(String.valueOf(cheques.stream()
                    .filter(c -> c.getReceiver().equals("Pending")).count()));
            cashedChequeBtn.setText(String.valueOf(cheques.stream()
                    .filter(c -> c.getReceiver().equals("Cashed")).count()));
            bouncedChequeBtn.setText(String.valueOf(cheques.stream()
                    .filter(c -> c.getReceiver().equals("Bounced")).count()));

            savingBalanceTxt.setText("0.0");
            interestRateTxt.setText("2.5%");
        } catch (Exception e) {
            log.error("Error populating dashboard", e);
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }

    private Card createDefaultCard() {
        Card card = Card.builder()
                .user(currentUser)
                .accountType(AccountType.Card)
                .balance(100.0)
                .cardNumber(generateCardNumber())
                .cvv2(generateCvv())
                .expiryDate(LocalDate.now().plusYears(5))
                .build();
        try {
            cardService.save(card);
            log.info("Created default card: " + card.getCardNumber());
        } catch (Exception e) {
            log.error("Error creating default card", e);
        }
        return card;
    }

    private void createDefaultCheques() {
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            String chequeNumber = String.format("%06d", random.nextInt(1000000));
            Cheque cheque = Cheque.builder()
                    .user(currentUser)
                    .accountType(AccountType.Cheque)
                    .balance(0.0)
                    .createdAt(LocalDateTime.now())
                    .number(chequeNumber)
                    .passDate(LocalDate.now().plusMonths(1))
                    .amount(0.0)
                    .receiver(userChequeAddress)
                    .description("Cheque #" + chequeNumber)
                    .build();
            try {
                chequeService.save(cheque);
                log.info("Saved cheque: " + chequeNumber + " with address " + userChequeAddress);
            } catch (Exception e) {
                log.error("Error saving cheque: " + chequeNumber, e);
                throw new RuntimeException("Failed to save cheque: " + e.getMessage(), e);
            }
        }
    }

    private void resetDashboard() {
        accountBalanceTxt.clear();
        cardNumberTxt.clear();
        cvvTxt.clear();
        expiryTxt.clear();
        cardNumberTxt1.clear();
        cvvTxt1.clear();
        expiryTxt1.clear();
        totalChequeBtn.clear();
        chequeAddressTxt.clear();
        pendingChequeBtn.clear();
        cashedChequeBtn.clear();
        bouncedChequeBtn.clear();
        savingBalanceTxt.clear();
        interestRateTxt.clear();
    }

    private void resetTransferForm() {
        log.info("Resetting transfer form");
        transferCardNumberTxt.clear();
        transferCvvTxt.clear();
        transferAmountTxt.clear();
        receiverNumberTxt.clear();
        if (transferChequeCombo != null) {
            transferChequeCombo.getSelectionModel().clearSelection();
        } else {
            transferChequeTxt.clear();
        }
        transferChequeAmountTxt.clear();
        transferChequeAddressTxt.clear();
        chequeDescriptionTxt.clear();
        chequeDate.setValue(null);
        byCardCheckBox.setSelected(false);
        byCheckCheckBox.setSelected(false);
        enableCardFields(false);
        enableChequeFields(false);
    }

    private void confirmTransfer() {
        log.info("Confirm Transfer clicked");
        try {
            if (!byCardCheckBox.isSelected()) {
                log.warn("Card transfer not selected");
                showError("Please select 'By Card' for card transfer");
                return;
            }

            String sourceCardNumber = transferCardNumberTxt.getText();
            if (sourceCardNumber == null || sourceCardNumber.trim().isEmpty()) {
                log.error("No source card number entered");
                showError("Please enter a source card number");
                return;
            }
            sourceCardNumber = sourceCardNumber.replaceAll("-", "");
            String cvv = transferCvvTxt.getText();
            String amountText = transferAmountTxt.getText();
            String receiverCardNumber = receiverNumberTxt.getText().replaceAll("-", "");

            log.info("Source: " + sourceCardNumber + ", CVV: " + cvv + ", Amount: " + amountText + ", Receiver: " + receiverCardNumber);

            if (cvv.isEmpty() || amountText.isEmpty() || receiverCardNumber.isEmpty()) {
                log.warn("Missing fields");
                showError("All fields must be filled");
                return;
            }

            double amount = Double.parseDouble(amountText);
            Card sourceCard = cardService.findByCardNumber(sourceCardNumber);
            if (sourceCard == null || !sourceCard.getCvv2().equals(cvv) || sourceCard.getBalance() < amount) {
                log.warn("Invalid source card details or insufficient balance");
                showError("Invalid card details or insufficient balance");
                return;
            }

            Card destinationCard = cardService.findByCardNumber(receiverCardNumber);
            if (destinationCard == null) {
                log.error("Receiver card not found: " + receiverCardNumber);
                showError("Receiver card not found");
                return;
            }

            sourceCard.setBalance(sourceCard.getBalance() - amount);
            destinationCard.setBalance(destinationCard.getBalance() + amount);
            cardService.save(sourceCard);
            cardService.save(destinationCard);
            log.info("Transfer complete - Source balance: " + sourceCard.getBalance() + ", Dest balance: " + destinationCard.getBalance());

            Transaction transaction = Transaction.builder()
                    .sourceAccount(sourceCard)
                    .destinationAccount(destinationCard)
                    .amount(amount)
                    .transactionType(TransactionType.Transfer)
                    .transactionTime(LocalDateTime.now())
                    .description("Transfer from " + sourceCardNumber + " to " + receiverCardNumber)
                    .build();
            transactionService.save(transaction);

            showInfo("Transferred " + amount + " to " + receiverCardNumber);
            resetTransferForm();
            populateDashboard();
            loadTransactionHistory();
            loadTransactionChart();
        } catch (NumberFormatException e) {
            log.error("Invalid amount", e);
            showError("Amount must be a number");
        } catch (Exception e) {
            log.error("Transfer failed", e);
            showError("Transfer failed: " + e.getMessage());
        }
    }

    private void issueCheque() {
        log.info("Issue Cheque clicked");
        try {
            if (!byCheckCheckBox.isSelected()) {
                log.warn("Cheque transfer not selected");
                showError("Please select 'By Cheque'");
                return;
            }

            String chequeNumber = transferChequeCombo != null ?
                    transferChequeCombo.getValue() : transferChequeTxt.getText();
            String amountText = transferChequeAmountTxt.getText();
            String receiverCardNumber = transferChequeAddressTxt.getText().replaceAll("-", "");
            String description = chequeDescriptionTxt.getText();
            LocalDate passDate = chequeDate.getValue();

            if (chequeNumber == null || chequeNumber.trim().isEmpty() ||
                    amountText.isEmpty() || receiverCardNumber.isEmpty() || passDate == null) {
                log.warn("Missing cheque fields or pass date");
                showError("All cheque fields and pass date must be filled");
                return;
            }

            if (passDate.isBefore(LocalDate.now())) {
                log.warn("Pass date is in the past");
                showError("Pass date must be today or in the future");
                return;
            }

            if (!receiverCardNumber.startsWith("1383")) {
                log.warn("Invalid receiver card number: " + receiverCardNumber);
                showError("Receiver must be a valid card number (starts with 1383)");
                return;
            }

            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                log.warn("Invalid cheque amount");
                showError("Amount must be positive");
                return;
            }

            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            log.info("Looking for cheque " + chequeNumber + " with receiver " + userChequeAddress);
            cheques.forEach(c -> log.info("Cheque: " + c.getNumber() + ", Receiver: " + c.getReceiver()));
            Cheque cheque = cheques.stream()
                    .filter(c -> c.getNumber().equals(chequeNumber) && c.getReceiver().equals(userChequeAddress))
                    .findFirst()
                    .orElse(null);

            if (cheque == null) {
                log.error("Cheque not found or already used: " + chequeNumber + " (expected receiver: " + userChequeAddress + ")");
                showError("Cheque not found or already used");
                return;
            }

            cheque.setAmount(amount);
            cheque.setReceiver("Pending");
            cheque.setDescription(description.isEmpty() ? "To " + receiverCardNumber : description + " | To " + receiverCardNumber);
            cheque.setPassDate(passDate);
            chequeService.save(cheque);

            log.info("Cheque issued: Number=" + chequeNumber + ", Amount=" + amount +
                    ", Receiver Card=" + receiverCardNumber + ", Pass Date=" + passDate + ", Status=Pending");
            showInfo("Cheque " + chequeNumber + " issued for " + amount + " to card " + receiverCardNumber +
                    ", now Pending until " + passDate);

            resetTransferForm();
            populateDashboard();
        } catch (NumberFormatException e) {
            log.error("Invalid cheque amount", e);
            showError("Amount must be a valid number");
        } catch (Exception e) {
            log.error("Cheque issuance failed", e);
            showError("Failed to issue cheque: " + e.getMessage());
        }
    }

    private void manageCheques() {
        log.info("Managing cheques");
        try {
            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            Card sourceCard = cards.isEmpty() ? createDefaultCard() : cards.get(0);
            LocalDate today = LocalDate.now();

            for (Cheque cheque : cheques) {
                if (cheque.getReceiver().equals("Pending") &&
                        (cheque.getPassDate().isBefore(today) || cheque.getPassDate().isEqual(today))) {
                    String description = cheque.getDescription();
                    String receiverCardNumber = description.contains("| To ") ?
                            description.split("\\| To ")[1] :
                            (description.startsWith("To ") ? description.substring(3) : null);

                    if (receiverCardNumber == null) {
                        log.warn("No receiver found in description for cheque " + cheque.getNumber());
                        cheque.setReceiver("Bounced");
                        chequeService.save(cheque);
                        showError("Cheque " + cheque.getNumber() + " bounced: Invalid receiver");
                        continue;
                    }

                    double amount = cheque.getAmount();
                    Card receiverCard = cardService.findByCardNumber(receiverCardNumber);
                    if (receiverCard == null) {
                        log.warn("Receiver card not found for cheque " + cheque.getNumber());
                        cheque.setReceiver("Bounced");
                        chequeService.save(cheque);
                        showError("Cheque " + cheque.getNumber() + " bounced: Receiver card not found");
                        continue;
                    }

                    if (sourceCard.getBalance() < amount) {
                        log.warn("Insufficient balance for cheque " + cheque.getNumber());
                        cheque.setReceiver("Bounced");
                        chequeService.save(cheque);
                        showError("Cheque " + cheque.getNumber() + " bounced: Insufficient funds");
                        continue;
                    }

                    sourceCard.setBalance(sourceCard.getBalance() - amount);
                    receiverCard.setBalance(receiverCard.getBalance() + amount);
                    cardService.save(sourceCard);
                    cardService.save(receiverCard);
                    cheque.setReceiver("Cashed");
                    chequeService.save(cheque);

                    Transaction transaction = Transaction.builder()
                            .sourceAccount(sourceCard)
                            .destinationAccount(receiverCard)
                            .amount(amount)
                            .transactionType(TransactionType.Transfer)
                            .transactionTime(LocalDateTime.now())
                            .description("Cheque #" + cheque.getNumber() + " cashed to " + receiverCardNumber)
                            .build();
                    transactionService.save(transaction);

                    log.info("Cheque " + cheque.getNumber() + " cashed for " + amount + " to " + receiverCardNumber);
                    showInfo("Cheque " + cheque.getNumber() + " cashed for " + amount + " to " + receiverCardNumber);
                }
            }
            populateDashboard();
            loadTransactionHistory();
            loadTransactionChart();
        } catch (Exception e) {
            log.error("Error managing cheques", e);
            showError("Failed to manage cheques: " + e.getMessage());
        }
    }

    private void useChequeForPurchase() {
        showInfo("Cheque purchase not implemented");
    }

    private void transferToChecking() {
        showInfo("Transfer to checking not implemented");
    }

    private void printTransactions() {
        log.info("Printing transactions...");
        transactionData.forEach(t -> System.out.println(t.getDescription() + " - " + t.getAmount()));
        showInfo("Transactions printed to console (placeholder)");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }
}