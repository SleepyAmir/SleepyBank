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
}