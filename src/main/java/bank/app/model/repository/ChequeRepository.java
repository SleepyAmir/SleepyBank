package bank.app.model.repository;

import bank.app.model.entity.Cheque;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.utils.ConnectionProvider;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChequeRepository implements Repository<Cheque, Integer> {
    private Connection connection;
    private PreparedStatement statement;

    @Override
    public void save(Cheque cheque) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        cheque.setId(ConnectionProvider.getConnectionProvider().nextId("cheque_seq"));
        statement = connection.prepareStatement(
                "INSERT INTO CHEQUES (ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CHEQUE_NUMBER, PASS_DATE, AMOUNT, RECEIVER, DESCRIPTION, U_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        statement.setInt(1, cheque.getId());
        statement.setString(2, cheque.getAccountType().name());
        statement.setDouble(3, cheque.getBalance());
        statement.setTimestamp(4, Timestamp.valueOf(cheque.getCreatedAt() != null ? cheque.getCreatedAt() : LocalDateTime.now()));
        statement.setString(5, cheque.getNumber());
        statement.setDate(6, Date.valueOf(cheque.getPassDate()));
        statement.setDouble(7, cheque.getAmount());
        statement.setString(8, cheque.getReceiver());
        statement.setString(9, cheque.getDescription());
        statement.setInt(10, cheque.getUser().getId());
        statement.executeUpdate();
    }

    @Override
    public void edit(Cheque cheque) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "UPDATE CHEQUES SET ACCOUNT_TYPE=?, BALANCE=?, CREATED_AT=?, CHEQUE_NUMBER=?, PASS_DATE=?, AMOUNT=?, RECEIVER=?, DESCRIPTION=?, U_ID=? " +
                        "WHERE ID=?"
        );
        statement.setString(1, cheque.getAccountType().name());
        statement.setDouble(2, cheque.getBalance());
        statement.setTimestamp(3, Timestamp.valueOf(cheque.getCreatedAt()));
        statement.setString(4, cheque.getNumber());
        statement.setDate(5, Date.valueOf(cheque.getPassDate()));
        statement.setDouble(6, cheque.getAmount());
        statement.setString(7, cheque.getReceiver());
        statement.setString(8, cheque.getDescription());
        statement.setInt(9, cheque.getUser().getId());
        statement.setInt(10, cheque.getId());
        statement.executeUpdate();
    }

    @Override
    public void remove(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("DELETE FROM CHEQUES WHERE ID=?");
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    @Override
    public List<Cheque> findAll() throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM CHEQUES");
        ResultSet rs = statement.executeQuery();
        List<Cheque> cheques = new ArrayList<>();

        UserRepository userRepo = new UserRepository();
        while (rs.next()) {
            Cheque cheque = new Cheque();
            cheque.setId(rs.getInt("ID"));
            cheque.setAccountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")));
            cheque.setBalance(rs.getDouble("BALANCE"));
            cheque.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
            cheque.setNumber(rs.getString("CHEQUE_NUMBER"));
            cheque.setPassDate(rs.getDate("PASS_DATE").toLocalDate());
            cheque.setAmount(rs.getDouble("AMOUNT"));
            cheque.setReceiver(rs.getString("RECEIVER"));
            cheque.setDescription(rs.getString("DESCRIPTION"));
            cheque.setUser(userRepo.findById(rs.getInt("U_ID")));
            cheques.add(cheque);
        }
        return cheques;
    }

    @Override
    public Cheque findById(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement("SELECT * FROM CHEQUES WHERE ID=?");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            Cheque cheque = new Cheque();
            UserRepository userRepo = new UserRepository();
            cheque.setId(rs.getInt("ID"));
            cheque.setAccountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")));
            cheque.setBalance(rs.getDouble("BALANCE"));
            cheque.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
            cheque.setNumber(rs.getString("CHEQUE_NUMBER"));
            cheque.setPassDate(rs.getDate("PASS_DATE").toLocalDate());
            cheque.setAmount(rs.getDouble("AMOUNT"));
            cheque.setReceiver(rs.getString("RECEIVER"));
            cheque.setDescription(rs.getString("DESCRIPTION"));
            cheque.setUser(userRepo.findById(rs.getInt("U_ID")));
            return cheque;
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
}