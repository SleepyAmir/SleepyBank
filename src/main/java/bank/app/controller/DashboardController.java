package bank.app.controller;

import bank.app.model.entity.Card;
import bank.app.model.entity.Cheque;
import bank.app.model.entity.Transaction;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.service.CardService;
import bank.app.model.service.ChequeService;
import bank.app.model.service.TransactionService;
import bank.app.model.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class DashboardController {
    private CardService cardService;
    private ChequeService chequeService;
    private TransactionService transactionService;
    private UserService userService;
    @Setter
    private int userId;

    @FXML private Label welcomeLabel;
    @FXML private TextField accountBalanceTxt;
    @FXML private TextField cardNumberTxt;
    @FXML private TextField cvvTxt;
    @FXML private TextField expiryTxt;
    @FXML private TextField totalChequeTxt;
    @FXML private TextField chequeAddressTxt;
    @FXML private TextField pendingChequeTxt;
    @FXML private TextField cashedChequeTxt;
    @FXML private TextField bouncedChequetxt; // Matches fx:id in dashboard.fxml
    @FXML private Button viewTransactionBtn;
    @FXML private Button transferFundsBtn;
    @FXML private TextField savingBalanceTxt;
    @FXML private Button manageChequeBtn;
    @FXML private TextField cardNumberTxt1;
    @FXML private TextField cvvTxt1;
    @FXML private TextField expiryTxt1;
    @FXML private Button chequeBtn; // Added from dashboard.fxml
    @FXML private TextField interestRateTxt; // Added from dashboard.fxml
    @FXML private Button savingTransferTxt; // Added from dashboard.fxml

    @Setter
    private Runnable onViewTransactions;
    @Setter
    private Runnable onTransferFunds;

    private String currentChequeNumber;
    private static final String CHEQUE_BASE = "12345";

    public void setServices(CardService cardService, ChequeService chequeService,
                            TransactionService transactionService, UserService userService) {
        System.out.println("Setting services in DashboardController");
        this.cardService = cardService;
        this.chequeService = chequeService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @FXML
    private void initialize() {
        System.out.println("Initializing DashboardController");
        // Null checks for debugging
        if (viewTransactionBtn == null) {
            System.out.println("Error: viewTransactionBtn is null");
        } else {
            viewTransactionBtn.setOnAction(event -> {
                if (onViewTransactions != null) onViewTransactions.run();
            });
        }

        if (transferFundsBtn == null) {
            System.out.println("Error: transferFundsBtn is null");
        } else {
            transferFundsBtn.setOnAction(event -> {
                if (onTransferFunds != null) onTransferFunds.run();
            });
        }

        if (manageChequeBtn == null) {
            System.out.println("Error: manageChequeBtn is null");
        } else {
            manageChequeBtn.setOnAction(event -> manageCheques());
        }

        if (chequeBtn == null) {
            System.out.println("Error: chequeBtn is null");
        } else {
            chequeBtn.setOnAction(event -> useChequeForPurchase());
        }

        if (savingTransferTxt == null) {
            System.out.println("Error: savingTransferTxt is null");
        } else {
            savingTransferTxt.setOnAction(event -> transferToChecking());
        }

        // Set fields to non-editable
        if (accountBalanceTxt != null) accountBalanceTxt.setEditable(false);
        if (cardNumberTxt != null) cardNumberTxt.setEditable(false);
        if (cvvTxt != null) cvvTxt.setEditable(false);
        if (expiryTxt != null) expiryTxt.setEditable(false);
        if (cardNumberTxt1 != null) cardNumberTxt1.setEditable(false);
        if (cvvTxt1 != null) cvvTxt1.setEditable(false);
        if (expiryTxt1 != null) expiryTxt1.setEditable(false);
        if (chequeAddressTxt != null) chequeAddressTxt.setEditable(false);
        if (totalChequeTxt != null) totalChequeTxt.setEditable(false);
        if (pendingChequeTxt != null) pendingChequeTxt.setEditable(false);
        if (cashedChequeTxt != null) cashedChequeTxt.setEditable(false);
        if (bouncedChequetxt != null) bouncedChequetxt.setEditable(false);
        if (savingBalanceTxt != null) savingBalanceTxt.setEditable(false);
        if (interestRateTxt != null) interestRateTxt.setEditable(false);
    }
    public void loadData() {
        System.out.println("DashboardController loadData called");
        if (cardService == null || chequeService == null || userService == null || transactionService == null) {
            System.out.println("Error: Services not initialized");
            if (welcomeLabel != null) welcomeLabel.setText("Error: Services not initialized");
            return;
        }
        try {
            populateDashboard();
            // Enable buttons after data is loaded
            viewTransactionBtn.setDisable(false);
            viewTransactionBtn.setVisible(true);
            transferFundsBtn.setDisable(false);
            transferFundsBtn.setVisible(true);
            manageChequeBtn.setDisable(false);
            manageChequeBtn.setVisible(true);
            chequeBtn.setDisable(false);
            chequeBtn.setVisible(true);
            savingTransferTxt.setDisable(false);
            savingTransferTxt.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error loading dashboard: " + e.getMessage());
        }
    }

    private void populateDashboard() throws Exception {
        List<Card> cards = cardService.findByUserId(userId);
        Card card = cards.isEmpty() ? createDefaultCard() : cards.get(0);
        String formattedCard = formatCardNumber(card.getCardNumber());
        if (accountBalanceTxt != null) accountBalanceTxt.setText(String.format("%.2f", card.getBalance()));
        if (cardNumberTxt != null) cardNumberTxt.setText(formattedCard);
        if (cvvTxt != null) cvvTxt.setText(card.getCvv2());
        if (expiryTxt != null) expiryTxt.setText(card.getExpiryDate().toString());
        if (cardNumberTxt1 != null) cardNumberTxt1.setText(formattedCard);
        if (cvvTxt1 != null) cvvTxt1.setText(card.getCvv2());
        if (expiryTxt1 != null) expiryTxt1.setText(card.getExpiryDate().toString());

        double savingBalance = cards.stream()
                .filter(c -> c.getAccountType() == AccountType.Saving)
                .mapToDouble(Card::getBalance)
                .sum();
        if (savingBalanceTxt != null) savingBalanceTxt.setText(String.format("%.2f", savingBalance));
        if (interestRateTxt != null) interestRateTxt.setText("2.5%");

        List<Cheque> cheques = chequeService.findByUserId(userId);
        System.out.println("Cheques found: " + cheques.size()); // Debug log
        if (cheques.isEmpty()) {
            System.out.println("No cheques found, creating defaults for userId: " + userId);
            createDefaultCheques();
            cheques = chequeService.findByUserId(userId);
            System.out.println("Cheques created: " + cheques.size());
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

        System.out.println("Total cheques: " + cheques.size());
        System.out.println("Current cheque: " + currentChequeNumber);
        System.out.println("Pending cheques: " + cheques.stream().filter(c -> c.getReceiver().equals("Pending")).count());
        System.out.println("Cashed cheques: " + cheques.stream().filter(c -> c.getReceiver().equals("Cashed")).count());
        System.out.println("Bounced cheques: " + cheques.stream().filter(c -> c.getReceiver().equals("Bounced")).count());

        if (totalChequeTxt != null) totalChequeTxt.setText(String.valueOf(cheques.size()));
        if (chequeAddressTxt != null) chequeAddressTxt.setText(currentChequeNumber != null ? currentChequeNumber : "No cheque available");
        if (pendingChequeTxt != null) pendingChequeTxt.setText(String.valueOf(cheques.stream()
                .filter(c -> c.getReceiver().equals("Pending")).count()));
        if (cashedChequeTxt != null) cashedChequeTxt.setText(String.valueOf(cheques.stream()
                .filter(c -> c.getReceiver().equals("Cashed")).count()));
        if (bouncedChequetxt != null) bouncedChequetxt.setText(String.valueOf(cheques.stream()
                .filter(c -> c.getReceiver().equals("Bounced")).count()));
    }
    private Card createDefaultCard() throws Exception {
        Card card = Card.builder()
                .user(userService.findById(userId))
                .accountType(AccountType.Card)
                .balance(100.0)
                .cardNumber(generateCardNumber())
                .cvv2(generateCvv())
                .expiryDate(LocalDate.now().plusYears(5))
                .build();
        cardService.save(card);
        return card;
    }

    private void createDefaultCheques() throws Exception {
        for (int i = 1; i <= 10; i++) {
            String chequeNumber = generateChequeNumber(i);
            Cheque cheque = Cheque.builder()
                    .user(userService.findById(userId))
                    .accountType(AccountType.Cheque)
                    .balance(0.0)
                    .createdAt(LocalDateTime.now())
                    .number(chequeNumber)
                    .passDate(LocalDate.now().plusMonths(1))
                    .amount(0.0)
                    .receiver("Available")
                    .description("Cheque #" + chequeNumber)
                    .build();
            chequeService.save(cheque);
        }
    }

    private void manageCheques() {
        try {
            List<Cheque> cheques = chequeService.findByUserId(userId);
            List<Card> cards = cardService.findByUserId(userId);
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

                    Card receiverCard = cardService.findByCardNumber(receiverCardNumber);
                    if (receiverCard == null || sourceCard.getBalance() < cheque.getAmount()) {
                        cheque.setReceiver("Bounced");
                        chequeService.save(cheque);
                        continue;
                    }

                    double amount = cheque.getAmount();
                    sourceCard.setBalance(sourceCard.getBalance() - amount);
                    receiverCard.setBalance(receiverCard.getBalance() + amount);
                    cardService.save(sourceCard);
                    cardService.save(receiverCard);

                    String oldChequeNumber = cheque.getNumber();
                    chequeService.delete(cheque);

                    String nextChequeNumber = generateNextChequeNumber(oldChequeNumber);
                    Cheque newCheque = Cheque.builder()
                            .user(userService.findById(userId))
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
            populateDashboard();
        } catch (Exception e) {
            System.out.println("Error managing cheques: " + e.getMessage());
            if (welcomeLabel != null) welcomeLabel.setText("Error managing cheques: " + e.getMessage());
        }
    }

    private void useChequeForPurchase() {
        System.out.println("Cheque purchase not implemented");
        if (welcomeLabel != null) welcomeLabel.setText("Cheque purchase not implemented");
    }

    private void transferToChecking() {
        System.out.println("Transfer to checking not implemented");
        if (welcomeLabel != null) welcomeLabel.setText("Transfer to checking not implemented");
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
        return CHEQUE_BASE + userId + String.format("%02d", sequence);
    }

    private String generateNextChequeNumber(String lastNumber) {
        if (lastNumber == null || !lastNumber.startsWith(CHEQUE_BASE + userId)) {
            return generateChequeNumber(1);
        }
        int lastSequence = Integer.parseInt(lastNumber.substring(lastNumber.length() - 2));
        int nextSequence = (lastSequence % 10) + 1;
        return generateChequeNumber(nextSequence);
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String formatCardNumber(String raw) {
        if (raw == null || raw.length() != 16) return "Invalid card number";
        return raw.substring(0, 4) + "-" + raw.substring(4, 8) + "-" +
                raw.substring(8, 12) + "-" + raw.substring(12, 16);
    }
}