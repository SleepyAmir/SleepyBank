package bank.app.model.repository;

import bank.app.model.entity.Cheque;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChequeRepository implements Repository<Cheque, Integer>, AutoCloseable {
    @Override
    public void save(Cheque cheque) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO CHEQUES (ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CHEQUE_NUMBER, PASS_DATE, AMOUNT, RECEIVER, DESCRIPTION, U_ID) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int newId = ConnectionProvider.getConnectionProvider().nextId("cheque_seq");
            cheque.setId(newId);
            stmt.setInt(1, newId);
            stmt.setString(2, cheque.getAccountType().name());
            stmt.setDouble(3, cheque.getBalance());
            stmt.setTimestamp(4, Timestamp.valueOf(cheque.getCreatedAt() != null ? cheque.getCreatedAt() : LocalDateTime.now()));
            stmt.setString(5, cheque.getNumber());
            stmt.setDate(6, Date.valueOf(cheque.getPassDate()));
            stmt.setDouble(7, cheque.getAmount());
            stmt.setString(8, cheque.getReceiver());
            stmt.setString(9, cheque.getDescription());
            stmt.setInt(10, cheque.getUser().getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void edit(Cheque cheque) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE CHEQUES SET ACCOUNT_TYPE=?, BALANCE=?, CREATED_AT=?, CHEQUE_NUMBER=?, PASS_DATE=?, AMOUNT=?, RECEIVER=?, DESCRIPTION=?, U_ID=? " +
                             "WHERE ID=?")) {
            stmt.setString(1, cheque.getAccountType().name());
            stmt.setDouble(2, cheque.getBalance());
            stmt.setTimestamp(3, Timestamp.valueOf(cheque.getCreatedAt()));
            stmt.setString(4, cheque.getNumber());
            stmt.setDate(5, Date.valueOf(cheque.getPassDate()));
            stmt.setDouble(6, cheque.getAmount());
            stmt.setString(7, cheque.getReceiver());
            stmt.setString(8, cheque.getDescription());
            stmt.setInt(9, cheque.getUser().getId());
            stmt.setInt(10, cheque.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM CHEQUES WHERE ID=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Cheque> findAll() throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES");
             ResultSet rs = stmt.executeQuery()) {
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
    }

    @Override
    public Cheque findById(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES WHERE ID=?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository();
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
                return cheque;
            }
            return null;
        }
    }

    // New method to find cheques by user ID
    public List<Cheque> findByUserId(int userId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES WHERE U_ID=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
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
    }

    // New method to find cheque by cheque number
    public Cheque findByNumber(String chequeNumber) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES WHERE CHEQUE_NUMBER=?")) {
            stmt.setString(1, chequeNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository();
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
                return cheque;
            }
            return null;
        }
    }

    @Override
    public void close() throws Exception {
    }
}