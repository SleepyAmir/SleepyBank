package bank.app.controller;

import bank.app.model.entity.Card;
import bank.app.model.entity.Cheque;
import bank.app.model.entity.Transaction;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class FundTransferController {
    private CardService cardService;
    private ChequeService chequeService;
    private TransactionService transactionService;
    @Setter
    private int userId;

    @FXML private Pane fundTransferPane;
    @FXML private Tab fundTab;
    @FXML private CheckBox byCardCheckBox;
    @FXML private CheckBox byCheckCheckBox;
    @FXML private TextField transferCardNumberTxt;
    @FXML private TextField transferCvvTxt;
    @FXML private TextField transferAmountTxt;
    @FXML private TextField receiverNumberTxt;
    @FXML private Button transferConfirmBtn;
    @FXML private TextField transferChequeTxt;
    @FXML private TextField transferChequeAmountTxt;
    @FXML private TextField transferChequeAddressTxt;
    @FXML private TextField chequeDescriptionTxt;
    @FXML private DatePicker chequeDate;
    @FXML private Button issueChequeBtn;
    @FXML private Button cancelChequeBtn;
    @FXML private Button transferCancelBtn;
    @FXML private ImageView byChequePng;
    @FXML private ImageView byCardPng;

    public void setServices(CardService cardService, ChequeService chequeService, TransactionService transactionService) {
        this.cardService = cardService;
        this.chequeService = chequeService;
        this.transactionService = transactionService;
    }

    @FXML
    private void initialize() {
        enableCardFields(false);
        enableChequeFields(false);
        transferConfirmBtn.setDisable(true);
        transferCancelBtn.setDisable(true);
        issueChequeBtn.setDisable(true);
        cancelChequeBtn.setDisable(true);

        byCardCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                byCheckCheckBox.setSelected(false);
                enableCardFields(true);
                populateCardField();
                enableChequeFields(false);
                transferConfirmBtn.setDisable(false);
                issueChequeBtn.setDisable(true);
                cancelChequeBtn.setDisable(true);
            } else if (!byCheckCheckBox.isSelected()) {
                resetFormState();
            }
        });

        byCheckCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                byCardCheckBox.setSelected(false);
                enableCardFields(false);
                enableChequeFields(true);
                populateChequeField();
                transferConfirmBtn.setDisable(true);
                issueChequeBtn.setDisable(false);
                cancelChequeBtn.setDisable(false);
            } else if (!byCardCheckBox.isSelected()) {
                resetFormState();
            }
        });


        transferConfirmBtn.setOnAction(event -> confirmTransfer());
        issueChequeBtn.setOnAction(event -> issueCheque());
        cancelChequeBtn.setOnAction(event -> resetForm());
    }

    private void enableCardFields(boolean enable) {
        transferCardNumberTxt.setDisable(!enable);
        transferCvvTxt.setDisable(!enable);
        transferAmountTxt.setDisable(!enable);
        receiverNumberTxt.setDisable(!enable);
        byCardPng.setVisible(enable);
    }

    private void enableChequeFields(boolean enable) {
        transferChequeTxt.setDisable(!enable);
        transferChequeAmountTxt.setDisable(!enable);
        transferChequeAddressTxt.setDisable(!enable);
        chequeDescriptionTxt.setDisable(!enable);
        chequeDate.setDisable(!enable);
        byChequePng.setVisible(enable);
    }

    private void resetFormState() {
        enableCardFields(false);
        enableChequeFields(false);
        transferConfirmBtn.setDisable(true);
        transferCancelBtn.setDisable(true);
        issueChequeBtn.setDisable(true);
        cancelChequeBtn.setDisable(true);
    }

    private void populateCardField() {
        if (cardService == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Card service not initialized.");
            return;
        }
        try {
            List<Card> cards = cardService.findByUserId(userId);
            if (!cards.isEmpty()) {
                Card card = cards.get(0);
                transferCardNumberTxt.setText(formatCardNumber(card.getCardNumber()));
                transferCvvTxt.setText(card.getCvv2());
            } else {
                transferCardNumberTxt.setText("No card available");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load card data.");
        }
    }

    private void populateChequeField() {
        if (chequeService == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cheque service not initialized.");
            return;
        }
        try {
            List<Cheque> cheques = chequeService.findByUserId(userId);
            Cheque availableCheque = cheques.stream()
                    .filter(c -> "Available".equals(c.getReceiver()))
                    .findFirst()
                    .orElse(null);
            transferChequeTxt.setText(availableCheque != null ? availableCheque.getNumber() : "No cheque available");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load cheque data.");
        }
    }

    private String formatCardNumber(String raw) {
        if (raw == null || raw.length() != 16) return "Invalid card number";
        return raw.substring(0, 4) + "-" + raw.substring(4, 8) + "-" +
                raw.substring(8, 12) + "-" + raw.substring(12, 16);
    }

    private void confirmTransfer() {
        if (cardService == null || transactionService == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Services not initialized.");
            return;
        }
        try {
            String sourceCardNum = transferCardNumberTxt.getText().replaceAll("-", "");
            String destCardNum = receiverNumberTxt.getText().replaceAll("-", "");
            if (sourceCardNum.length() != 16 || destCardNum.length() != 16) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Card numbers must be 16 digits.");
                return;
            }
            Card sourceCard = cardService.findByCardNumber(sourceCardNum);
            Card destCard = cardService.findByCardNumber(destCardNum);
            if (sourceCard == null || destCard == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid card number.");
                return;
            }
            double amount = Double.parseDouble(transferAmountTxt.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Amount must be positive.");
                return;
            }
            if (!sourceCard.getCvv2().equals(transferCvvTxt.getText())) {
                showAlert(Alert.AlertType.ERROR, "Invalid CVV", "CVV does not match.");
                return;
            }
            if (sourceCard.getBalance() < amount) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Not enough balance.");
                return;
            }

            Transaction transaction = Transaction.builder()
                    .sourceAccount(sourceCard)
                    .destinationAccount(destCard)
                    .amount(amount)
                    .transactionType(TransactionType.Transfer)
                    .transactionTime(LocalDateTime.now())
                    .description("Card transfer from " + sourceCardNum + " to " + destCardNum)
                    .build();
            transactionService.save(transaction);
            sourceCard.setBalance(sourceCard.getBalance() - amount);
            destCard.setBalance(destCard.getBalance() + amount);
            cardService.save(sourceCard);
            cardService.save(destCard);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transfer of $" + amount + " completed.");
            resetForm();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Transfer Failed", "Error: " + e.getMessage());
        }
    }

    private void issueCheque() {
        if (chequeService == null || cardService == null || transactionService == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Services not initialized.");
            System.out.println("Error: One or more services are null");
            return;
        }
        try {
            String chequeNumber = transferChequeTxt.getText();
            System.out.println("Attempting cheque transaction with number: " + chequeNumber);
            if (chequeNumber == null || chequeNumber.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Cheque number cannot be empty.");
                System.out.println("Cheque number is null or empty");
                return;
            }

            double amount = Double.parseDouble(transferChequeAmountTxt.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Amount must be positive.");
                System.out.println("Invalid amount: " + amount);
                return;
            }

            Cheque cheque = chequeService.findByNumber(chequeNumber);
            System.out.println("Cheque found: " + (cheque != null ? cheque.getNumber() : "null"));
            if (cheque == null || !"Available".equals(cheque.getReceiver())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid or unavailable cheque.");
                System.out.println("Cheque is null or not available: " + (cheque != null ? cheque.getReceiver() : "null"));
                return;
            }

            List<Card> cards = cardService.findByUserId(userId);
            System.out.println("Cards found for user " + userId + ": " + cards.size());
            if (cards.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "No Card", "No card found for this user.");
                System.out.println("No cards found for userId: " + userId);
                return;
            }
            Card sourceCard = cards.get(0);
            System.out.println("Source card balance: " + sourceCard.getBalance());
            if (sourceCard.getBalance() < amount) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Not enough balance.");
                System.out.println("Insufficient funds: " + sourceCard.getBalance() + " < " + amount);
                return;
            }

            sourceCard.setBalance(sourceCard.getBalance() - amount);
            cardService.save(sourceCard);
            cheque.setAmount(amount);
            cheque.setReceiver(transferChequeAddressTxt.getText());
            cheque.setDescription(chequeDescriptionTxt.getText());
            cheque.setPassDate(chequeDate.getValue());
            cheque.setReceiver("Pending");
            chequeService.save(cheque);

            Transaction transaction = Transaction.builder()
                    .sourceAccount(sourceCard)
                    .destinationAccount(null)
                    .amount(amount)
                    .transactionType(TransactionType.Cheque)
                    .transactionTime(LocalDateTime.now())
                    .description("Cheque #" + chequeNumber + " issued to " + transferChequeAddressTxt.getText())
                    .build();
            transactionService.save(transaction);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Cheque #" + chequeNumber + " issued for $" + amount);
            System.out.println("Cheque transaction successful: #" + chequeNumber + " for $" + amount);
            resetForm();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
            System.out.println("NumberFormatException: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Cheque Issuance Failed", "Error: " + e.getMessage());
            System.out.println("Cheque issuance failed: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
    private void resetForm() {
        byCardCheckBox.setSelected(false);
        byCheckCheckBox.setSelected(false);
        transferCardNumberTxt.clear();
        transferCvvTxt.clear();
        transferAmountTxt.clear();
        receiverNumberTxt.clear();
        transferChequeTxt.clear();
        transferChequeAmountTxt.clear();
        transferChequeAddressTxt.clear();
        chequeDescriptionTxt.clear();
        chequeDate.setValue(null);
        resetFormState();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}