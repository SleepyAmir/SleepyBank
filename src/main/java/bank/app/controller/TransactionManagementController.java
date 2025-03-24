package bank.app.controller;

import bank.app.model.entity.Transaction;
import bank.app.model.service.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

public class TransactionManagementController {
    @Setter
    private TransactionService transactionService;
    @Setter
    private int userId;

    @FXML private StackedBarChart<String, Number> transactionsChart;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;

    @FXML
    private void initialize() {
        transactionsChart.setTitle("Transaction Amounts by Date");
    }

    public void loadChartData() throws Exception {
        if (transactionService == null) {
            System.out.println("Error: TransactionService not initialized");
            return;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transactions");
        for (Transaction t : transactionService.findByUserId(userId)) {
            series.getData().add(new XYChart.Data<>(t.getTransactionTime().format(DateTimeFormatter.ISO_LOCAL_DATE), t.getAmount()));
        }
        transactionsChart.getData().clear();
        transactionsChart.getData().add(series);
    }
}