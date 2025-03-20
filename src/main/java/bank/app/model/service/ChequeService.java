package bank.app.model.service;

import bank.app.model.entity.Cheque;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.ChequeRepository;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

}