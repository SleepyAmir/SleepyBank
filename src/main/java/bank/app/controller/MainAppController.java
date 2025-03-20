package bank.app.controller;

import bank.app.model.entity.*;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import bank.app.model.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.*;
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
    @FXML private Label welcomeLabel;
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
    @FXML private TextField transferChequeTxt;
    @FXML private TextField transferChequeAmountTxt;
    @FXML private TextField transferChequeAddressTxt;
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
    private String currentChequeNumber;
    private static final String CHEQUE_BASE = "12345";

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


        transferFundsBtn.setOnAction(event -> {
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

        editBtn.setOnAction(event -> {
            if (!isEditMode) {
                // Switch to edit mode
                isEditMode = true;
                setUserInfoEditable(true);
                editBtn.setText("Save");
                log.info("Switched to edit mode");
            } else {
                try {
                    currentUser.setEmail(emailAddressInfoTxt.getText());
                    currentUser.setAddress(addressInfoTxt.getText());
                    currentUser.setUsername(userNameInfoTxt.getText());
                    currentUser.setPassword(passwordInfoTxt.getText());

                    // Save to database
                    userService.edit(currentUser);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "User Info Edited", ButtonType.OK);
                    alert.show();
                    log.info("User Edited: " + currentUser.getUsername());

                    isEditMode = false;
                    setUserInfoEditable(false);
                    editBtn.setText("Edit");
                    populateUserInfo();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.show();
                    log.error("Error editing user info", e);
                }
            }
        });

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
                populateChequeField();
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
        transferChequeTxt.setEditable(false);

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
            Transaction t = cellData.getValue();
            String source;
            if (t.getSourceAccount() instanceof Card) {
                source = ((Card) t.getSourceAccount()).getCardNumber();
            } else if (t.getSourceAccount() instanceof Cheque) {
                source = ((Cheque) t.getSourceAccount()).getNumber();
            } else {
                source = "N/A";
            }
            return new javafx.beans.property.SimpleStringProperty(source);
        });

        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        statusCol.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            String status;
            if (t.getSourceAccount() instanceof Cheque) {
                Cheque cheque = (Cheque) t.getSourceAccount();
                status = cheque.getReceiver(); // "Pending", "Cashed", "Bounced", etc.
            } else {
                status = "Completed";
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        transactionTable.setItems(transactionData);
    }

    private void setupTransactionChart() {
        transactionsChart.setTitle("Transaction Amounts by Date");
    }

    private void loadTransactionChart() {
        Task<List<XYChart.Series<String, Number>>> fetchChartDataTask = new Task<List<XYChart.Series<String, Number>>>() {
            @Override
            protected List<XYChart.Series<String, Number>> call() throws Exception {
                // Fetch and process data in the background
                List<Transaction> transactions = transactionService.findByUserId(currentUser.getId());
                Map<String, Map<String, Double>> groupedData = transactions.stream()
                        .collect(Collectors.groupingBy(
                                t -> t.getTransactionTime().toLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                                Collectors.groupingBy(
                                        t -> t.getSourceAccount() instanceof Card ? "Card Transfer" : "Cheque Transfer",
                                        Collectors.summingDouble(Transaction::getAmount)
                                )
                        ));

                XYChart.Series<String, Number> cardSeries = new XYChart.Series<>();
                cardSeries.setName("Card Transfers");
                XYChart.Series<String, Number> chequeSeries = new XYChart.Series<>();
                chequeSeries.setName("Cheque Transfers");

                groupedData.forEach((date, typeMap) -> {
                    cardSeries.getData().add(new XYChart.Data<>(date, typeMap.getOrDefault("Card Transfer", 0.0)));
                    chequeSeries.getData().add(new XYChart.Data<>(date, typeMap.getOrDefault("Cheque Transfer", 0.0)));
                });

                // Use Arrays.asList() instead of List.of() for Java 8 compatibility
                return Arrays.asList(cardSeries, chequeSeries);
            }
        };

        fetchChartDataTask.setOnSucceeded(event -> {
            // Update chart on the UI thread
            transactionsChart.getData().clear();
            transactionsChart.getData().addAll(fetchChartDataTask.getValue());
        });

        fetchChartDataTask.setOnFailed(event -> {
            log.error("Error loading transaction chart", fetchChartDataTask.getException());
            showError("Failed to load transaction chart: " + fetchChartDataTask.getException().getMessage());
        });

        new Thread(fetchChartDataTask).start();
    }


    private void loadTransactionHistory() {
        Task<List<Transaction>> fetchTransactionsTask = new Task<List<Transaction>>() {
            @Override
            protected List<Transaction> call() throws Exception {
                return transactionService.findByUserId(currentUser.getId());
            }
        };

        fetchTransactionsTask.setOnSucceeded(event -> {
            transactionData.setAll(fetchTransactionsTask.getValue());
            log.info("Loaded " + transactionData.size() + " transactions for user " + currentUser.getUsername());
        });

        fetchTransactionsTask.setOnFailed(event -> {
            log.error("Error loading transaction history", fetchTransactionsTask.getException());
            showError("Failed to load transaction history: " + fetchTransactionsTask.getException().getMessage());
        });

        new Thread(fetchTransactionsTask).start();
    }

    private void populateUserInfo() {
        if (currentUser == null) return;
        firstNameInfoTxt.setText(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");
        lastNameInfoTxt.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "");
        emailAddressInfoTxt.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        dateOfBirthInfoTxt.setText(currentUser.getBirthDate() != null ? currentUser.getBirthDate().toString() : "");
        phoneNumberInfoTxt.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        addressInfoTxt.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        userNameInfoTxt.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "");
        passwordInfoTxt.setText(currentUser.getPassword() != null ? currentUser.getPassword() : "");
    }

    private void setUserInfoEditable(boolean editable) {
        log.info("Setting user info editable: " + editable);
        emailAddressInfoTxt.setEditable(editable);
        addressInfoTxt.setEditable(editable);
        userNameInfoTxt.setEditable(editable);
        passwordInfoTxt.setEditable(editable);

        firstNameInfoTxt.setEditable(false);
        lastNameInfoTxt.setEditable(false);
        dateOfBirthInfoTxt.setEditable(false);
        phoneNumberInfoTxt.setEditable(false);
    }

    private void populateFundTransferTab() {
        if (currentUser == null) {
            showError("No user logged in");
            return;
        }
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            if (!cards.isEmpty()) {
                transferCardNumberTxt.setText(formatCardNumber(cards.get(0).getCardNumber()));
            }
            if (byCheckCheckBox.isSelected()) {
                populateChequeField();
            }
        } catch (Exception e) {
            log.error("Error populating fund transfer tab", e);
            showError("Failed to populate fund transfer tab: " + e.getMessage());
        }
    }

    private void enableCardFields(boolean enable) {
        transferCardNumberTxt.setDisable(!enable);
        transferCvvTxt.setDisable(!enable);
        transferAmountTxt.setDisable(!enable);
        receiverNumberTxt.setDisable(!enable);
    }

    private void enableChequeFields(boolean enable) {
        transferChequeTxt.setDisable(!enable);
        transferChequeAmountTxt.setDisable(!enable);
        transferChequeAddressTxt.setDisable(!enable);
        chequeDescriptionTxt.setDisable(!enable);
        chequeDate.setDisable(!enable);
        issueChequeBtn.setDisable(!enable);
        cancelChequeBtn.setDisable(!enable);
    }

    private void populateChequeField() {
        transferChequeTxt.setText(currentChequeNumber != null ? currentChequeNumber : "No cheque available");
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("1383");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateChequeNumber(int sequence) {
        if (currentUser == null) {
            throw new IllegalStateException("No current user set for cheque generation");
        }
        return CHEQUE_BASE + currentUser.getId() + String.format("%02d", sequence);
    }

    private String generateNextChequeNumber(String lastNumber) {
        if (lastNumber == null || !lastNumber.startsWith(CHEQUE_BASE + currentUser.getId())) {
            return generateChequeNumber(1);
        }
        int lastSequence = Integer.parseInt(lastNumber.substring(lastNumber.length() - 2));
        int nextSequence = (lastSequence % 10) + 1;
        return generateChequeNumber(nextSequence);
    }

    private String formatCardNumber(String raw) {
        if (raw == null) return "N/A";
        return raw.length() == 16 ?
                raw.substring(0, 4) + "-" + raw.substring(4, 8) + "-" + raw.substring(8, 12) + "-" + raw.substring(12, 16) :
                raw;
    }

    private void populateDashboard() {
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            Card card = cards.isEmpty() ? createDefaultCard() : cards.get(0);
            String formattedCard = formatCardNumber(card.getCardNumber());
            accountBalanceTxt.setText(String.valueOf(card.getBalance()));
            cardNumberTxt.setText(formattedCard);
            cvvTxt.setText(card.getCvv2());
            expiryTxt.setText(card.getExpiryDate().toString());
            cardNumberTxt1.setText(formattedCard);
            cvvTxt1.setText(card.getCvv2());
            expiryTxt1.setText(card.getExpiryDate().toString());

            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            if (cheques.isEmpty()) {
                createDefaultCheques();
                cheques = chequeService.findByUserId(currentUser.getId());
                currentChequeNumber = generateChequeNumber(1);
            } else {
                List<Cheque> finalCheques = cheques;
                currentChequeNumber = cheques.stream()
                        .filter(c -> c.getReceiver().equals("Available"))
                        .map(Cheque::getNumber)
                        .findFirst()
                        .orElseGet(() -> generateNextChequeNumber(
                                finalCheques.stream()
                                        .map(Cheque::getNumber)
                                        .max(String::compareTo)
                                        .orElse(null)
                        ));
            }

            totalChequeBtn.setText(String.valueOf(cheques.size()));
            chequeAddressTxt.setText(currentChequeNumber);
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
        for (int i = 1; i <= 10; i++) {
            String chequeNumber = generateChequeNumber(i);
            Cheque cheque = Cheque.builder()
                    .user(currentUser)
                    .accountType(AccountType.Cheque)
                    .balance(0.0)
                    .createdAt(LocalDateTime.now())
                    .number(chequeNumber)
                    .passDate(LocalDate.now().plusMonths(1))
                    .amount(0.0)
                    .receiver("Available")
                    .description("Cheque #" + chequeNumber)
                    .build();
            try {
                chequeService.save(cheque);
                log.info("Saved cheque: " + chequeNumber);
            } catch (Exception e) {
                log.error("Error saving cheque: " + chequeNumber, e);
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
        transferCardNumberTxt.clear();
        transferCvvTxt.clear();
        transferAmountTxt.clear();
        receiverNumberTxt.clear();
        transferChequeTxt.clear();
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
        if (!byCardCheckBox.isSelected()) {
            showError("Please select 'By Card' for card transfer");
            return;
        }

        String sourceCardNumber = transferCardNumberTxt.getText().replaceAll("-", "");
        String cvv = transferCvvTxt.getText();
        String amountText = transferAmountTxt.getText();
        String receiverCardNumber = receiverNumberTxt.getText().replaceAll("-", "");

        if (sourceCardNumber.isEmpty() || cvv.isEmpty() || amountText.isEmpty() || receiverCardNumber.isEmpty()) {
            showError("All fields must be filled");
            return;
        }

        transferConfirmTxt.setDisable(true);

        Task<Void> transferTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                log.info("Starting transfer task");

                double amount = Double.parseDouble(amountText);

                Card sourceCard = cardService.findByCardNumber(sourceCardNumber);
                if (sourceCard == null || !sourceCard.getCvv2().equals(cvv) || sourceCard.getBalance() < amount) {
                    throw new Exception("Invalid card details or insufficient balance");
                }

                Card destinationCard = cardService.findByCardNumber(receiverCardNumber);
                if (destinationCard == null) {
                    throw new Exception("Receiver card not found");
                }

                sourceCard.setBalance(sourceCard.getBalance() - amount);
                destinationCard.setBalance(destinationCard.getBalance() + amount);

                cardService.save(sourceCard);
                cardService.save(destinationCard);

                Transaction transaction = Transaction.builder()
                        .sourceAccount(sourceCard)
                        .destinationAccount(destinationCard)
                        .amount(amount)
                        .transactionType(TransactionType.Transfer)
                        .transactionTime(LocalDateTime.now())
                        .description("Transfer from " + sourceCardNumber + " to " + receiverCardNumber)
                        .build();
                transactionService.save(transaction);

                log.info("Transfer task completed successfully");
                return null;
            }
        };

        transferTask.setOnSucceeded(event -> {
            log.info("Transfer task succeeded");
            transferConfirmTxt.setDisable(false);
            showInfo("Transferred " + amountText + " to " + receiverCardNumber);
            resetTransferForm();
            populateDashboard();
            loadTransactionHistory();
            loadTransactionChart();
        });

        transferTask.setOnFailed(event -> {
            log.error("Transfer task failed", transferTask.getException());
            transferConfirmTxt.setDisable(false);
            showError("Transfer failed: " + transferTask.getException().getMessage());
        });

        new Thread(transferTask).start();
    }

    private void issueCheque() {
        if (!byCheckCheckBox.isSelected()) {
            showError("Please select 'By Cheque'");
            return;
        }

        String chequeNumber = transferChequeTxt.getText();
        String amountText = transferChequeAmountTxt.getText();
        String receiverCardNumber = transferChequeAddressTxt.getText().replaceAll("-", "");
        String description = chequeDescriptionTxt.getText();
        LocalDate passDate = chequeDate.getValue();

        if (chequeNumber.isEmpty() || amountText.isEmpty() || receiverCardNumber.isEmpty() || passDate == null) {
            showError("All cheque fields and pass date must be filled");
            return;
        }

        if (passDate.isBefore(LocalDate.now())) {
            showError("Pass date must be today or in the future");
            return;
        }

        if (!receiverCardNumber.startsWith("1383")) {
            showError("Receiver must be a valid card number (starts with 1383)");
            return;
        }

        issueChequeBtn.setDisable(true);
        Task<Void> issueChequeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                log.info("Starting cheque issuance in background thread");

                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    throw new Exception("Amount must be positive");
                }

                List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
                Cheque cheque = cheques.stream()
                        .filter(c -> c.getNumber().equals(chequeNumber) && c.getReceiver().equals("Available"))
                        .findFirst()
                        .orElse(null);

                if (cheque == null) {
                    throw new Exception("Cheque not found or already used");
                }

                cheque.setAmount(amount);
                cheque.setReceiver("Pending");
                cheque.setDescription(description.isEmpty() ? "To " + receiverCardNumber : description + " | To " + receiverCardNumber);
                cheque.setPassDate(passDate);

                chequeService.saveOrUpdate(cheque);

                log.info("Cheque issuance completed successfully");
                return null;
            }
        };

        issueChequeTask.setOnSucceeded(event -> {
            log.info("Cheque issuance succeeded");
            issueChequeBtn.setDisable(false); // Re-enable the button
            showInfo("Cheque " + chequeNumber + " issued for " + amountText + " to card " + receiverCardNumber +
                    ", now Pending until " + passDate);
            resetTransferForm();
            populateDashboard();
        });

        issueChequeTask.setOnFailed(event -> {
            log.error("Cheque issuance failed", issueChequeTask.getException());
            issueChequeBtn.setDisable(false); // Re-enable the button
            showError("Failed to issue cheque: " + issueChequeTask.getException().getMessage());
        });

        new Thread(issueChequeTask).start();
    }

    private void manageCheques() {
        Task<Void> manageChequesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
                List<Card> cards = cardService.findByUserId(currentUser.getId());
                Card sourceCard = cards.isEmpty() ? createDefaultCard() : cards.get(0);
                LocalDate today = LocalDate.now();

                for (Cheque cheque : cheques) {
                    if (cheque.getReceiver().equals("Pending") &&
                            (cheque.getPassDate().isBefore(today) || cheque.getPassDate().isEqual(today))) {
                        String receiverCardNumber = cheque.getDescription().contains("| To ") ?
                                cheque.getDescription().split("\\| To ")[1] :
                                (cheque.getDescription().startsWith("To ") ? cheque.getDescription().substring(3) : null);

                        if (receiverCardNumber == null) {
                            cheque.setReceiver("Bounced");
                            chequeService.save(cheque);
                            continue;
                        }

                        double amount = cheque.getAmount();
                        Card receiverCard = cardService.findByCardNumber(receiverCardNumber);
                        if (receiverCard == null) {
                            cheque.setReceiver("Bounced");
                            chequeService.save(cheque);
                            continue;
                        }

                        if (sourceCard.getBalance() < amount) {
                            cheque.setReceiver("Bounced");
                            chequeService.save(cheque);
                            continue;
                        }

                        sourceCard.setBalance(sourceCard.getBalance() - amount);
                        receiverCard.setBalance(receiverCard.getBalance() + amount);
                        cardService.save(sourceCard);
                        cardService.save(receiverCard);

                        String oldChequeNumber = cheque.getNumber();
                        chequeService.delete(cheque);

                        String nextChequeNumber = generateNextChequeNumber(oldChequeNumber);
                        Cheque newCheque = Cheque.builder()
                                .user(currentUser)
                                .accountType(AccountType.Cheque)
                                .balance(0.0)
                                .createdAt(LocalDateTime.now())
                                .number(nextChequeNumber)
                                .passDate(LocalDate.now().plusMonths(1))
                                .amount(0.0)
                                .receiver("Available")
                                .description("Cheque #" + nextChequeNumber)
                                .build();
                        chequeService.save(newCheque);

                        Transaction transaction = Transaction.builder()
                                .sourceAccount(cheque)
                                .destinationAccount(receiverCard)
                                .amount(amount)
                                .transactionType(TransactionType.Transfer)
                                .transactionTime(LocalDateTime.now())
                                .description("Cheque #" + oldChequeNumber + " cashed to " + receiverCardNumber)
                                .build();
                        transactionService.save(transaction);
                    }
                }
                return null;
            }
        };

        manageChequesTask.setOnSucceeded(event -> {
            populateDashboard();
            loadTransactionHistory();
            loadTransactionChart();
            showInfo("Cheques managed successfully");
        });

        manageChequesTask.setOnFailed(event -> {
            log.error("Error managing cheques", manageChequesTask.getException());
            showError("Failed to manage cheques: " + manageChequesTask.getException().getMessage());
        });

        new Thread(manageChequesTask).start();
    }

    private void useChequeForPurchase() {
        showInfo("Cheque purchase not implemented");
    }

    private void transferToChecking() {
        showInfo("Transfer to checking not implemented");
    }

    private void printTransactions() {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String jsonOutput = gson.toJson(transactionData);

        System.out.println("Transactions in JSON format:\n" + jsonOutput);

        showInfo("Transactions printed to console as JSON");
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

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}