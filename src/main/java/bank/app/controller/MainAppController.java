package bank.app.controller;

import bank.app.model.entity.*;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

@Log4j
public class MainAppController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab fundTab;
    @FXML private Tab transactionHistoryTab;

    @FXML private Pane dashboardPane;
    @FXML private Pane fundTransferPane;
    @FXML private Pane transactionHistoryPane;

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
    @FXML private TextField transferCardNumberTxt; // Replaced ComboBox
    @FXML private TextField transferCvvTxt;
    @FXML private TextField transferAmountTxt;
    @FXML private TextField receiverNumberTxt;
    @FXML private Button transferConfirmTxt;
    @FXML private Button transferCancelTxt;
    @FXML private TextField transferChequeTxt;
    @FXML private TextField transferChequeAmountTxt;
    @FXML private TextField transferChequeAddressTxt;
    @FXML private Button issueChequeBtn;
    @FXML private Button cancelChequeBtn;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> accountCol;
    @FXML private TableColumn<Transaction, TransactionType> typeCol;
    @FXML private TableColumn<Transaction, Double> amountCol;
    @FXML private TableColumn<Transaction, String> statusCol;
    @FXML private TableColumn<Transaction, String> descriptionCol;
    @FXML private Button printTableBtn;

    private User currentUser;
    private CardService cardService = new CardService();
    private ChequeService chequeService = new ChequeService();
    private TransactionService transactionService = new TransactionService();

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
        viewTransactionBtn.setOnAction(event -> tabPane.getSelectionModel().select(transactionHistoryTab));
        transferConfirmTxt.setOnAction(event -> confirmTransfer());
        transferCancelTxt.setOnAction(event -> resetTransferForm());
        chequeBtn.setOnAction(event -> useChequeForPurchase());
        manageChequeBtn.setOnAction(event -> manageCheques());
        savingTransferTxt.setOnAction(event -> transferToChecking());
        issueChequeBtn.setOnAction(event -> issueCheque());
        cancelChequeBtn.setOnAction(event -> resetTransferForm());
        printTableBtn.setOnAction(event -> printTransactions());

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
            } else if (!byCardCheckBox.isSelected()) {
                enableCardFields(false);
                enableChequeFields(false);
            }
        });

        accountCol.setCellValueFactory(new PropertyValueFactory<>("sourceAccount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Completed"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

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

        resetDashboard();
        resetTransferForm();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        log.info("Logged in user: " + (user != null ? user.getUsername() : "null"));
        populateDashboard();
    }

    private void populateFundTransferTab() {
        log.info("Populating Fund Transfer tab");
        if (currentUser == null) {
            log.error("currentUser is null");
            showError("No user logged in");
            return;
        }
        // No need to populate card numbers since TextField is used now
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            if (!cards.isEmpty()) {
                transferCardNumberTxt.setText(formatCardNumber(cards.get(0).getCardNumber()));
                log.info("Default card set: " + transferCardNumberTxt.getText());
            } else {
                log.warn("No cards to display");
            }
        } catch (Exception e) {
            log.error("Error setting default card", e);
            showError("Failed to set default card: " + e.getMessage());
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
        issueChequeBtn.setDisable(!enable);
        cancelChequeBtn.setDisable(!enable);
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("1383");
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
                createDefaultCheques();
                cheques = chequeService.findByUserId(currentUser.getId());
            }
            totalChequeBtn.setText(String.valueOf(cheques.size()));
            chequeAddressTxt.setText(cheques.isEmpty() ? "N/A" : cheques.get(0).getNumber());
            pendingChequeBtn.setText(String.valueOf(cheques.stream().filter(c -> "Pending".equals(c.getReceiver())).count()));
            cashedChequeBtn.setText(String.valueOf(cheques.stream().filter(c -> "Cashed".equals(c.getReceiver())).count()));
            bouncedChequeBtn.setText(String.valueOf(cheques.stream().filter(c -> "Bounced".equals(c.getReceiver())).count()));

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
                    .receiver("Pending")
                    .description("Cheque #" + chequeNumber)
                    .build();
            try {
                chequeService.save(cheque);
                log.info("Saved cheque: " + chequeNumber);
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
        transferChequeTxt.clear();
        transferChequeAmountTxt.clear();
        transferChequeAddressTxt.clear();
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
                showError("Please select 'By Card'");
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
        } catch (NumberFormatException e) {
            log.error("Invalid amount", e);
            showError("Amount must be a number");
        } catch (Exception e) {
            log.error("Transfer failed", e);
            showError("Transfer failed: " + e.getMessage());
        }
    }

    private void useChequeForPurchase() { showInfo("Cheque purchase not implemented"); }
    private void manageCheques() { showInfo("Manage cheques not implemented"); }
    private void transferToChecking() { showInfo("Transfer to checking not implemented"); }
    private void issueCheque() { showInfo("Issue cheque not implemented"); }
    private void printTransactions() { showInfo("Print transactions not implemented"); }

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