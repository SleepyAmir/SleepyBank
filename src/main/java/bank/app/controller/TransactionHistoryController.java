package bank.app.controller;

import bank.app.model.entity.Transaction;
import bank.app.model.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Setter;

import java.util.List;

public class TransactionHistoryController {
    @Setter
    private TransactionService transactionService;
    @Setter
    private int userId;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> accountCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, Double> amountCol;
    @FXML private TableColumn<Transaction, String> statusCol;
    @FXML private TableColumn<Transaction, String> descriptionCol;
    @FXML private Button printTableBtn;

    @FXML
    private void initialize() {
        accountCol.setCellValueFactory(new PropertyValueFactory<>("sourceAccount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty("Completed"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        printTableBtn.setOnAction(event -> printTransactions());
    }

    public void loadTransactions() throws Exception {
        if (transactionService == null) {
            System.out.println("Error: TransactionService not initialized");
            return;
        }
        transactionTable.getItems().clear();
        List<Transaction> transactions = transactionService.findByUserId(userId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found");
        }
        transactionTable.getItems().addAll(transactions);
    }

    private void printTransactions() {
        System.out.println("Printing transactions: " + transactionTable.getItems());
    }
}