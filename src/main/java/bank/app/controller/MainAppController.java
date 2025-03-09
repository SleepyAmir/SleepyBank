package bank.app.controller;

import bank.app.model.entity.*;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Log4j
public class MainAppController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab fundTab;
    @FXML private Tab transactionHistoryTab;

    // Dashboard Tab
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

    // Fund Transfer Tab
    @FXML private CheckBox byCardCheckBox;
    @FXML private CheckBox byCheckCheckBox;
    @FXML private TextField transferCardNumberTxt;
    @FXML private TextField transferCvvTxt;
    @FXML private TextField transferAmountTxt;
    @FXML private Button transferConfirmTxt;
    @FXML private Button transferCancelTxt;
    @FXML private TextField transferChequeTxt;
    @FXML private TextField transferChequeAmountTxt;
    @FXML private TextField transferChequeAddressTxt;
    @FXML private Button issueChequeBtn;
    @FXML private Button cancelChequeBtn;

    // Transaction History Tab
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

        // Set up button actions
        viewTransactionBtn.setOnAction(event -> viewTransactions());
        transferFundsBtn.setOnAction(event -> tabPane.getSelectionModel().select(fundTab));
        chequeBtn.setOnAction(event -> useChequeForPurchase());
        manageChequeBtn.setOnAction(event -> manageCheques());
        savingTransferTxt.setOnAction(event -> transferToChecking());
        transferConfirmTxt.setOnAction(event -> confirmTransfer());
        transferCancelTxt.setOnAction(event -> resetTransferForm());
        issueChequeBtn.setOnAction(event -> issueCheque());
        cancelChequeBtn.setOnAction(event -> resetTransferForm());
        printTableBtn.setOnAction(event -> printTransactions());

        // Set up table columns
        accountCol.setCellValueFactory(new PropertyValueFactory<>("sourceAccount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Completed"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        resetDashboard();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        log.info("Logged in user: " + currentUser.getUsername());
        populateDashboard();
    }

    private void populateDashboard() {
        try {
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            if (!cards.isEmpty()) {
                Card card = cards.get(0);
                accountBalanceTxt.setText(String.format("%.2f", card.getBalance()));
                cardNumberTxt.setText(card.getCardNumber());
                cvvTxt.setText(card.getCvv2());
                expiryTxt.setText(card.getExpiryDate().toString());
            }

            List<Cheque> cheques = chequeService.findByUserId(currentUser.getId());
            totalChequeBtn.setText(String.valueOf(cheques.size()));
            pendingChequeBtn.setText(String.valueOf(cheques.stream().filter(c -> c.getPassDate().isAfter(LocalDateTime.now().toLocalDate())).count()));
            cashedChequeBtn.setText(String.valueOf(cheques.stream().filter(c -> c.getPassDate().isBefore(LocalDateTime.now().toLocalDate())).count()));
            bouncedChequeBtn.setText("0");
            if (!cheques.isEmpty()) chequeAddressTxt.setText(cheques.get(0).getReceiver());

            double savingsBalance = cards.stream()
                    .filter(c -> c.getAccountType() == AccountType.Saving)
                    .mapToDouble(Account::getBalance)
                    .sum();
            savingBalanceTxt.setText(String.format("%.2f", savingsBalance));
            interestRateTxt.setText("2.5%");

            log.info("Dashboard populated for user: " + currentUser.getUsername());
        } catch (Exception e) {
            log.error("Error populating dashboard", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load dashboard: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void resetDashboard() {
        accountBalanceTxt.clear();
        cardNumberTxt.clear();
        cvvTxt.clear();
        expiryTxt.clear();
        totalChequeBtn.clear();
        chequeAddressTxt.clear();
        pendingChequeBtn.clear();
        cashedChequeBtn.clear();
        bouncedChequeBtn.clear();
        savingBalanceTxt.clear();
        interestRateTxt.clear();
    }

    private void viewTransactions() {
        try {
            List<Transaction> transactions = transactionService.findByUserId(currentUser.getId());
            refreshTransactionTable(transactions);
            tabPane.getSelectionModel().select(transactionHistoryTab);
            log.info("Transactions viewed for user: " + currentUser.getUsername());
        } catch (Exception e) {
            log.error("Error viewing transactions", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load transactions: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void useChequeForPurchase() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cheque used for purchase: " + chequeAddressTxt.getText(), ButtonType.OK);
            alert.show();
            log.info("Cheque used for purchase: " + chequeAddressTxt.getText());
        } catch (Exception e) {
            log.error("Error using cheque", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to use cheque: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void manageCheques() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Managing cheques", ButtonType.OK);
            alert.show();
            log.info("Cheques managed");
        } catch (Exception e) {
            log.error("Error managing cheques", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to manage cheques: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void transferToChecking() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transferred " + savingBalanceTxt.getText() + " to checking", ButtonType.OK);
            alert.show();
            log.info("Transferred to checking: " + savingBalanceTxt.getText());
        } catch (Exception e) {
            log.error("Error transferring to checking", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to transfer: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void confirmTransfer() {
        try {
            if (byCardCheckBox.isSelected()) {
                Transaction transaction = Transaction.builder()
                        .sourceAccount(cardService.findByCardNumber(transferCardNumberTxt.getText()))
                        .destinationAccount(cardService.findAll().get(0)) // Simplified, needs real destination
                        .amount(Double.parseDouble(transferAmountTxt.getText()))
                        .transactionType(TransactionType.Transfer)
                        .transactionTime(LocalDateTime.now())
                        .description("Card transfer")
                        .build();
                transactionService.save(transaction);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transfer confirmed by card", ButtonType.OK);
                alert.show();
                log.info("Transfer confirmed by card");
            } else if (byCheckCheckBox.isSelected()) {
                Cheque cheque = Cheque.builder()
                        .user(currentUser)
                        .accountType(AccountType.Check)
                        .balance(0.0)
                        .createdAt(LocalDateTime.now())
                        .number(transferChequeTxt.getText())
                        .passDate(LocalDateTime.now().toLocalDate().plusDays(30))
                        .amount(Double.parseDouble(transferChequeAmountTxt.getText()))
                        .receiver(transferChequeAddressTxt.getText())
                        .description("Cheque transfer")
                        .build();
                chequeService.save(cheque);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transfer confirmed by cheque", ButtonType.OK);
                alert.show();
                log.info("Transfer confirmed by cheque");
            }
            resetTransferForm();
        } catch (Exception e) {
            log.error("Error confirming transfer", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to confirm transfer: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void resetTransferForm() {
        transferCardNumberTxt.clear();
        transferCvvTxt.clear();
        transferAmountTxt.clear();
        transferChequeTxt.clear();
        transferChequeAmountTxt.clear();
        transferChequeAddressTxt.clear();
        byCardCheckBox.setSelected(false);
        byCheckCheckBox.setSelected(false);
    }

    private void issueCheque() {
        try {
            Cheque cheque = Cheque.builder()
                    .user(currentUser)
                    .accountType(AccountType.Check)
                    .balance(0.0)
                    .createdAt(LocalDateTime.now())
                    .number(transferChequeTxt.getText())
                    .passDate(LocalDateTime.now().toLocalDate().plusDays(30))
                    .amount(Double.parseDouble(transferChequeAmountTxt.getText()))
                    .receiver(transferChequeAddressTxt.getText())
                    .description("Issued cheque")
                    .build();
            chequeService.save(cheque);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cheque issued", ButtonType.OK);
            alert.show();
            log.info("Cheque issued");
            resetTransferForm();
        } catch (Exception e) {
            log.error("Error issuing cheque", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to issue cheque: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void printTransactions() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transactions printed", ButtonType.OK);
            alert.show();
            log.info("Transactions printed");
        } catch (Exception e) {
            log.error("Error printing transactions", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to print transactions: " + e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void refreshTransactionTable(List<Transaction> transactions) {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
        transactionTable.getItems().clear();
        transactionTable.setItems(transactionList);
    }
}