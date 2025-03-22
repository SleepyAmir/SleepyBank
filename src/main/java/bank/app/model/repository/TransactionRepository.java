package bank.app.model.repository;

import bank.app.model.entity.Transaction;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<Transaction, Integer>, AutoCloseable {
    @Override
    public void save(Transaction transaction) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO TRANSACTIONS (ID, SOURCE_ACCOUNT, DESTINATION_ACCOUNT, AMOUNT, TRANSACTION_TYPE, TRANSACTION_TIME, DESCRIPTION) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            int newId = ConnectionProvider.getConnectionProvider().nextId("transaction_seq");
            transaction.setId(newId); // Assumes setter exists; adjust if using immutable builder
            stmt.setInt(1, newId);
            stmt.setInt(2, transaction.getSourceAccount().getId());
            stmt.setInt(3, transaction.getDestinationAccount().getId());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setString(5, transaction.getTransactionType().name());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionTime() != null ? transaction.getTransactionTime() : LocalDateTime.now()));
            stmt.setString(7, transaction.getDescription());
            stmt.executeUpdate();
        }
    }

    @Override
    public void edit(Transaction transaction) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE TRANSACTIONS SET SOURCE_ACCOUNT=?, DESTINATION_ACCOUNT=?, AMOUNT=?, TRANSACTION_TYPE=?, TRANSACTION_TIME=?, DESCRIPTION=? " +
                             "WHERE ID=?")) {
            stmt.setInt(1, transaction.getSourceAccount().getId());
            stmt.setInt(2, transaction.getDestinationAccount().getId());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getTransactionType().name());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getTransactionTime()));
            stmt.setString(6, transaction.getDescription());
            stmt.setInt(7, transaction.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM TRANSACTIONS WHERE ID=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TRANSACTIONS");
             ResultSet rs = stmt.executeQuery()) {
            List<Transaction> transactions = new ArrayList<>();
            CardRepository cardRepo = new CardRepository();
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .id(rs.getInt("ID"))
                        .sourceAccount(cardRepo.findById(rs.getInt("SOURCE_ACCOUNT")))
                        .destinationAccount(cardRepo.findById(rs.getInt("DESTINATION_ACCOUNT")))
                        .amount(rs.getDouble("AMOUNT"))
                        .transactionType(TransactionType.valueOf(rs.getString("TRANSACTION_TYPE")))
                        .transactionTime(rs.getTimestamp("TRANSACTION_TIME").toLocalDateTime())
                        .description(rs.getString("DESCRIPTION"))
                        .build();
                transactions.add(transaction);
            }
            return transactions;
        }
    }

    @Override
    public Transaction findById(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TRANSACTIONS WHERE ID=?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CardRepository cardRepo = new CardRepository();
                return Transaction.builder()
                        .id(rs.getInt("ID"))
                        .sourceAccount(cardRepo.findById(rs.getInt("SOURCE_ACCOUNT")))
                        .destinationAccount(cardRepo.findById(rs.getInt("DESTINATION_ACCOUNT")))
                        .amount(rs.getDouble("AMOUNT"))
                        .transactionType(TransactionType.valueOf(rs.getString("TRANSACTION_TYPE")))
                        .transactionTime(rs.getTimestamp("TRANSACTION_TIME").toLocalDateTime())
                        .description(rs.getString("DESCRIPTION"))
                        .build();
            }
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        // No-op since connections are managed per method
    }
}