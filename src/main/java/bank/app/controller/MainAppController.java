package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import bank.app.model.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainAppController {

    @FXML private TabPane tabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab fundTab;
    @FXML private Tab transactionHistoryTab;
    @FXML private Tab userInfoTab;
    @FXML private Tab transactionManagementTab;

    @FXML private DashboardController dashboardController;
    @FXML private FundTransferController fundTransferController;
    @FXML private TransactionHistoryController transactionHistoryController;
    @FXML private UserInfoController userInfoController;
    @FXML private TransactionManagementController transactionManagementController;

    private User currentUser;
    private UserService userService;
    private CardService cardService;
    private ChequeService chequeService;
    private TransactionService transactionService;

    @FXML
    private void initialize() {
        System.out.println("MainAppController initialize start");
        userService = new UserService();
        cardService = new CardService();
        chequeService = new ChequeService();
        transactionService = new TransactionService();
        System.out.println("Services created");

        if (dashboardController != null) {
            dashboardController.setServices(cardService, chequeService, transactionService, userService);
            System.out.println("Services set for dashboardController");
        } else {
            System.out.println("dashboardController is null");
        }

        if (fundTransferController != null) {
            fundTransferController.setServices(cardService, chequeService, transactionService);
            System.out.println("Services set for fundTransferController");
        } else {
            System.out.println("fundTransferController is null");
        }

        if (transactionHistoryController != null) {
            transactionHistoryController.setTransactionService(transactionService);
            System.out.println("Services set for transactionHistoryController");
        } else {
            System.out.println("transactionHistoryController is null");
        }

        if (userInfoController != null) {
            userInfoController.setUserService(userService);
            System.out.println("Services set for userInfoController");
        } else {
            System.out.println("userInfoController is null");
        }

        if (transactionManagementController != null) {
            transactionManagementController.setTransactionService(transactionService);
            System.out.println("Services set for transactionManagementController");
        } else {
            System.out.println("transactionManagementController is null");
        }

        System.out.println("MainAppController initialize end");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        int userId = user.getId();
        dashboardController.setUserId(userId);
        fundTransferController.setUserId(userId);
        transactionHistoryController.setUserId(userId);
        userInfoController.setUserId(userId);
        transactionManagementController.setUserId(userId);
        try {
            dashboardController.loadData();
            userInfoController.loadData();
            transactionHistoryController.loadTransactions();
            transactionManagementController.loadChartData();
        } catch (Exception e) {
            System.out.println("Error setting user: " + e.getMessage());
        }
    }

    @FXML
    private void handleTransferFunds() {
        tabPane.getSelectionModel().select(fundTab);
    }

    @FXML
    private void handleViewTransactions() {
        tabPane.getSelectionModel().select(transactionHistoryTab);
    }
}