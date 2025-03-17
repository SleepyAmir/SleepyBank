package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.ChequeRepository;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class ChequeService {
    public void saveOrUpdate(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            if (cheque.getId() == 0) { // New cheque
                repo.save(cheque);
            } else { // Existing cheque
                repo.edit(cheque);
            }
        }
    }

    // Keep original save for compatibility
    public void save(Cheque cheque) throws Exception {
        try (ChequeRepository repo = new ChequeRepository()) {
            repo.save(cheque);
        }
    }

    public List<Cheque> findByUserId(int userId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES WHERE U_ID = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Cheque> cheques = new ArrayList<>();
            while (rs.next()) {
                Cheque cheque = Cheque.builder()
                        .id(rs.getInt("ID"))
                        .accountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")))
                        .balance(rs.getDouble("BALANCE"))
                        .createdAt(rs.getTimestamp("CREATED_AT").toLocalDateTime())
                        .number(rs.getString("CHEQUE_NUMBER"))
                        .passDate(rs.getDate("PASS_DATE").toLocalDate())
                        .amount(rs.getDouble("AMOUNT"))
                        .receiver(rs.getString("RECEIVER"))
                        .description(rs.getString("DESCRIPTION"))
                        .user(User.builder().id(rs.getInt("U_ID")).build())
                        .build();
                cheques.add(cheque);
            }
            return cheques;
        }
    }

    // Add method to find by cheque number (optional for validation)
    public Cheque findByChequeNumber(String chequeNumber) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CHEQUES WHERE CHEQUE_NUMBER = ?")) {
            stmt.setString(1, chequeNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Cheque.builder()
                        .id(rs.getInt("ID"))
                        .accountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")))
                        .balance(rs.getDouble("BALANCE"))
                        .createdAt(rs.getTimestamp("CREATED_AT").toLocalDateTime())
                        .number(rs.getString("CHEQUE_NUMBER"))
                        .passDate(rs.getDate("PASS_DATE").toLocalDate())
                        .amount(rs.getDouble("AMOUNT"))
                        .receiver(rs.getString("RECEIVER"))
                        .description(rs.getString("DESCRIPTION"))
                        .user(User.builder().id(rs.getInt("U_ID")).build())
                        .build();
            }
            return null;
        }
    }
    public void delete(Cheque cheque) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM CHEQUES WHERE ID = ?")) {
            stmt.setInt(1, cheque.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("No cheque found with ID: " + cheque.getId());
            }
        }
    }
        public List<Cheque> findAll() throws Exception {
            try (ChequeRepository chequeRepository = new ChequeRepository()) {
                List<Cheque> chequeList = chequeRepository.findAll();
                if (chequeList.isEmpty()) {
                    throw new Exception("Member not found");
                }
                return chequeList;
            }
        }

    public void saveBatch(User user, int count, String chequeBase) throws Exception {
        String sql = "INSERT INTO CHEQUES (ID, U_ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CHEQUE_NUMBER, PASS_DATE, AMOUNT, RECEIVER, DESCRIPTION) " +
                "VALUES (CHEQUE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            LocalDateTime now = LocalDateTime.now();
            LocalDate passDate = LocalDate.now().plusMonths(1);
            for (int i = 1; i <= count; i++) {
                String chequeNumber = chequeBase + user.getId() + String.format("%02d", i);
                stmt.setInt(1, user.getId());
                stmt.setString(2, AccountType.Cheque.name());
                stmt.setDouble(3, 0.0);
                stmt.setTimestamp(4, java.sql.Timestamp.valueOf(now));
                stmt.setString(5, chequeNumber);
                stmt.setDate(6, java.sql.Date.valueOf(passDate));
                stmt.setDouble(7, 0.0);
                stmt.setString(8, "Available");
                stmt.setString(9, "Cheque #" + chequeNumber);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
           log.println ("Batch saved " + count + " cheques for user " + user.getId());
        } catch (SQLException e) {
            throw new Exception("Failed to batch save cheques: " + e.getMessage(), e);
        }
    }


}