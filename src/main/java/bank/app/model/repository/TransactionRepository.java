package bank.app.model.repository;

import bank.app.model.entity.Transaction;
import bank.app.model.entity.enums.TransactionType;
import bank.app.model.repository.utils.ConnectionProvider;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<Transaction, Integer> {
    private Connection connection;
    private PreparedStatement statement;

    @Override
    public void save(Transaction transaction) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        transaction.setId(ConnectionProvider.getConnectionProvider().nextId("transaction_seq"));
        statement = connection.prepareStatement(
                "INSERT INTO TRANSACTIONS (ID, SOURCE_ACCOUNT, DESTINATION_ACCOUNT, AMOUNT, TRANSACTION_TYPE, TRANSACTION_TIME, DESCRIPTION) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        statement.setInt(1, transaction.getId());
        statement.setInt(2, transaction.getSourceAccount().getId());
        statement.setInt(3, transaction.getDestinationAccount().getId());
        statement.setDouble(4, transaction.getAmount());
        statement.setString(5, transaction.getTransactionType().name());
        statement.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionTime() != null ? transaction.getTransactionTime() : LocalDateTime.now()));
        statement.setString(7, transaction.getDescription());
        statement.executeUpdate();
    }

    @Override
    public void edit(Transaction transaction) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "UPDATE TRANSACTIONS SET SOURCE_ACCOUNT=?, DESTINATION_ACCOUNT=?, AMOUNT=?, TRANSACTION_TYPE=?, TRANSACTION_TIME=?, DESCRIPTION=? " +
                        "WHERE ID=?"
        );
        statement.setInt(1, transaction.getSourceAccount().getId());
        statement.setInt(2, transaction.getDestinationAccount().getId());
        statement.setDouble(3, transaction.getAmount());
        statement.setString(4, transaction.getTransactionType().name());
        statement.setTimestamp(5, Timestamp.valueOf(transaction.getTransactionTime()));
        statement.setString(6, transaction.getDescription());
        statement.setInt(7, transaction.getId());
        statement.executeUpdate();
    }

    @Override
    public void remove(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("DELETE FROM TRANSACTIONS WHERE ID=?");
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM TRANSACTIONS");
        ResultSet rs = statement.executeQuery();
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

    @Override
    public Transaction findById(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM TRANSACTIONS WHERE ID=?");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

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

    @Override
    public void close() throws Exception {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
}