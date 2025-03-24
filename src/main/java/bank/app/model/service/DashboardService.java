package bank.app.model.service;

import bank.app.model.entity.Card;

import java.time.LocalDate;

public class DashboardService {
    private final UserService userService;
    private final CardService cardService;
    private final TransactionService transactionService;
    private final ChequeService chequeService;

    public DashboardService(UserService userService, CardService cardService,
                            TransactionService transactionService, ChequeService chequeService) {
        this.userService = userService;
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.chequeService = chequeService;
    }

    public int getTotalUsers() throws Exception {
        return userService.findAll().size();
    }

    public double getTotalBalance() throws Exception {
        return cardService.findAll().stream().mapToDouble(Card::getBalance).sum();
    }

    public long getNewUsersToday() throws Exception {
        LocalDate today = LocalDate.now();
        return userService.findAll().stream()
                .filter(u -> u.getRegistrationDate() != null && u.getRegistrationDate().equals(today))
                .count();
    }

    public double[] getTransactionDistribution() throws Exception {
        long cardTransactions = transactionService.findAll().size();
        long chequeTransactions = chequeService.findAll().stream()
                .filter(c -> !c.getReceiver().equals("Available"))
                .count();
        long total = cardTransactions + chequeTransactions;
        double cardPercent = total > 0 ? (cardTransactions * 100.0 / total) : 0;
        double chequePercent = total > 0 ? (chequeTransactions * 100.0 / total) : 0;
        return new double[]{cardPercent, chequePercent};
    }
}