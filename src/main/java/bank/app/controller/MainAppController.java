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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load dashboard: " + e.getMessage());
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load transactions: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void useChequeForPurchase() {
        try {
            String chequeNumber = chequeAddressTxt.getText();
            Cheque cheque = chequeService.findByUserId(currentUser.getId()).stream()
                    .filter(c -> c.getNumber().equals(chequeNumber))
                    .findFirst()
                    .orElse(null);
            if (cheque != null && cheque.getPassDate().isAfter(LocalDateTime.now().toLocalDate())) {
                Card sourceCard = cardService.findByUserId(currentUser.getId()).get(0); // Assume first card
                if (sourceCard.getBalance() >= cheque.getAmount()) {
                    sourceCard.setBalance(sourceCard.getBalance() - cheque.getAmount());
                    cardService.save(sourceCard);

                    Transaction transaction = Transaction.builder()
                            .sourceAccount(sourceCard)
                            .destinationAccount(null) // No destination for purchase
                            .amount(cheque.getAmount())
                            .transactionType(TransactionType.Withdraw)
                            .transactionTime(LocalDateTime.now())
                            .description("Cheque purchase: " + chequeNumber)
                            .build();
                    transactionService.save(transaction);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Cheque " + chequeNumber + " used for purchase");
                    alert.showAndWait();
                    populateDashboard();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Insufficient balance in card");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid or expired cheque");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error using cheque", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to use cheque: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void manageCheques() {
        // Placeholder for future implementation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Managing cheques functionality to be implemented later");
        alert.showAndWait();
    }

    private void transferToChecking() {
        try {
            double amount = Double.parseDouble(savingBalanceTxt.getText());
            List<Card> cards = cardService.findByUserId(currentUser.getId());
            Card savingCard = cards.stream()
                    .filter(c -> c.getAccountType() == AccountType.Saving)
                    .findFirst()
                    .orElse(null);
            Card checkingCard = cards.stream()
                    .filter(c -> c.getAccountType() == AccountType.Card)
                    .findFirst()
                    .orElse(null);

            if (savingCard != null && checkingCard != null && savingCard.getBalance() >= amount) {
                savingCard.setBalance(savingCard.getBalance() - amount);
                checkingCard.setBalance(checkingCard.getBalance() + amount);
                cardService.save(savingCard);
                cardService.save(checkingCard);

                Transaction transaction = Transaction.builder()
                        .sourceAccount(savingCard)
                        .destinationAccount(checkingCard)
                        .amount(amount)
                        .transactionType(TransactionType.Transfer)
                        .transactionTime(LocalDateTime.now())
                        .description("Transfer from savings to checking")
                        .build();
                transactionService.save(transaction);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Transferred " + amount + " to checking");
                alert.showAndWait();
                populateDashboard();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Insufficient savings balance or no accounts found");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error transferring to checking", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to transfer: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void confirmTransfer() {
        try {
            if (byCardCheckBox.isSelected()) {
                String cardNumber = transferCardNumberTxt.getText();
                String cvv = transferCvvTxt.getText();
                double amount = Double.parseDouble(transferAmountTxt.getText());

                Card sourceCard = cardService.findByCardNumber(cardNumber);
                if (sourceCard != null && sourceCard.getCvv2().equals(cvv) && sourceCard.getBalance() >= amount) {
                    Card destinationCard = cardService.findAll().stream()
                            .filter(c -> !c.getCardNumber().equals(cardNumber))
                            .findFirst()
                            .orElse(null); // Simplified: first other card

                    if (destinationCard != null) {
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
                                .description("Card transfer to " + destinationCard.getCardNumber())
                                .build();
                        transactionService.save(transaction);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Transfer of " + amount + " by card completed");
                        alert.showAndWait();
                        resetTransferForm();
                        populateDashboard();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("No destination card found");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid card details or insufficient balance");
                    alert.showAndWait();
                }
            } else if (byCheckCheckBox.isSelected()) {
                String chequeNumber = transferChequeTxt.getText();
                double amount = Double.parseDouble(transferChequeAmountTxt.getText());
                String receiver = transferChequeAddressTxt.getText();

                Card sourceCard = cardService.findByUserId(currentUser.getId()).get(0); // Assume first card
                if (sourceCard.getBalance() >= amount) {
                    Cheque cheque = Cheque.builder()
                            .user(currentUser)
                            .accountType(AccountType.Check)
                            .balance(0.0)
                            .createdAt(LocalDateTime.now())
                            .number(chequeNumber)
                            .passDate(LocalDateTime.now().toLocalDate().plusDays(30))
                            .amount(amount)
                            .receiver(receiver)
                            .description("Cheque transfer")
                            .build();
                    chequeService.save(cheque);

                    sourceCard.setBalance(sourceCard.getBalance() - amount);
                    cardService.save(sourceCard);

                    Transaction transaction = Transaction.builder()
                            .sourceAccount(sourceCard)
                            .destinationAccount(null) // No immediate destination for cheque
                            .amount(amount)
                            .transactionType(TransactionType.Withdraw)
                            .transactionTime(LocalDateTime.now())
                            .description("Cheque transfer: " + chequeNumber)
                            .build();
                    transactionService.save(transaction);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Transfer of " + amount + " by cheque completed");
                    alert.showAndWait();
                    resetTransferForm();
                    populateDashboard();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Insufficient balance in card");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Select a transfer method (Card or Cheque)");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error confirming transfer", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to confirm transfer: " + e.getMessage());
            alert.showAndWait();
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
            String chequeNumber = transferChequeTxt.getText();
            double amount = Double.parseDouble(transferChequeAmountTxt.getText());
            String receiver = transferChequeAddressTxt.getText();

            Card sourceCard = cardService.findByUserId(currentUser.getId()).get(0); // Assume first card
            if (sourceCard.getBalance() >= amount) {
                Cheque cheque = Cheque.builder()
                        .user(currentUser)
                        .accountType(AccountType.Check)
                        .balance(0.0)
                        .createdAt(LocalDateTime.now())
                        .number(chequeNumber)
                        .passDate(LocalDateTime.now().toLocalDate().plusDays(30))
                        .amount(amount)
                        .receiver(receiver)
                        .description("Issued cheque")
                        .build();
                chequeService.save(cheque);

                sourceCard.setBalance(sourceCard.getBalance() - amount);
                cardService.save(sourceCard);

                Transaction transaction = Transaction.builder()
                        .sourceAccount(sourceCard)
                        .destinationAccount(null)
                        .amount(amount)
                        .transactionType(TransactionType.Withdraw)
                        .transactionTime(LocalDateTime.now())
                        .description("Cheque issued: " + chequeNumber)
                        .build();
                transactionService.save(transaction);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Cheque " + chequeNumber + " issued");
                alert.showAndWait();
                resetTransferForm();
                populateDashboard();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Insufficient balance in card");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error issuing cheque", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to issue cheque: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void printTransactions() {
        // Placeholder for future implementation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Transaction printing to be implemented later");
        alert.showAndWait();
    }

    private void refreshTransactionTable(List<Transaction> transactions) {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
        transactionTable.getItems().clear();
        transactionTable.setItems(transactionList);
    }
}