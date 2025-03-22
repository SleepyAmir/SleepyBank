package bank.app.model.repository;

import bank.app.model.entity.Card;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements Repository<Card, Integer>, AutoCloseable {
    @Override
    public void save(Card card) throws Exception {
        Card existingCard = findByCardNumber(card.getCardNumber());
        if (existingCard != null) {
            card.setId(existingCard.getId());
            edit(card);
        } else {
            try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO CARD (ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CARD_NUMBER, CVV2, EXPIRY_DATE, U_ID) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                int newId = ConnectionProvider.getConnectionProvider().nextId("card_seq");
                card.setId(newId);
                stmt.setInt(1, newId);
                stmt.setString(2, card.getAccountType().name());
                stmt.setDouble(3, card.getBalance());
                stmt.setTimestamp(4, Timestamp.valueOf(card.getCreatedAt() != null ? card.getCreatedAt() : LocalDateTime.now()));
                stmt.setString(5, card.getCardNumber());
                stmt.setString(6, card.getCvv2());
                stmt.setDate(7, Date.valueOf(card.getExpiryDate()));
                stmt.setInt(8, card.getUser().getId());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void edit(Card card) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE CARD SET ACCOUNT_TYPE=?, BALANCE=?, CREATED_AT=?, CARD_NUMBER=?, CVV2=?, EXPIRY_DATE=?, U_ID=? " +
                             "WHERE ID=?")) {
            stmt.setString(1, card.getAccountType().name());
            stmt.setDouble(2, card.getBalance());
            stmt.setTimestamp(3, Timestamp.valueOf(card.getCreatedAt()));
            stmt.setString(4, card.getCardNumber());
            stmt.setString(5, card.getCvv2());
            stmt.setDate(6, Date.valueOf(card.getExpiryDate()));
            stmt.setInt(7, card.getUser().getId());
            stmt.setInt(8, card.getId());
            stmt.executeUpdate();
        }
    }

    public Card findByCardNumber(String cardNumber) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CARD WHERE CARD_NUMBER=?")) {
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository(); // Ideally, inject this dependency
                Card card = new Card();
                card.setId(rs.getInt("ID"));
                card.setAccountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")));
                card.setBalance(rs.getDouble("BALANCE"));
                card.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                card.setCardNumber(rs.getString("CARD_NUMBER"));
                card.setCvv2(rs.getString("CVV2"));
                card.setExpiryDate(rs.getDate("EXPIRY_DATE").toLocalDate());
                card.setUser(userRepo.findById(rs.getInt("U_ID")));
                return card;
            }
            return null;
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM CARD WHERE ID=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Card> findAll() throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CARD");
             ResultSet rs = stmt.executeQuery()) {
            List<Card> cards = new ArrayList<>();
            UserRepository userRepo = new UserRepository(); // Inject this in production
            while (rs.next()) {
                Card card = new Card();
                card.setId(rs.getInt("ID"));
                card.setAccountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")));
                card.setBalance(rs.getDouble("BALANCE"));
                card.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                card.setCardNumber(rs.getString("CARD_NUMBER"));
                card.setCvv2(rs.getString("CVV2"));
                card.setExpiryDate(rs.getDate("EXPIRY_DATE").toLocalDate());
                card.setUser(userRepo.findById(rs.getInt("U_ID")));
                cards.add(card);
            }
            return cards;
        }
    }

    @Override
    public Card findById(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CARD WHERE ID=?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository(); // Inject this
                Card card = new Card();
                card.setId(rs.getInt("ID"));
                card.setAccountType(AccountType.valueOf(rs.getString("ACCOUNT_TYPE")));
                card.setBalance(rs.getDouble("BALANCE"));
                card.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                card.setCardNumber(rs.getString("CARD_NUMBER"));
                card.setCvv2(rs.getString("CVV2"));
                card.setExpiryDate(rs.getDate("EXPIRY_DATE").toLocalDate());
                card.setUser(userRepo.findById(rs.getInt("U_ID")));
                return card;
            }
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        // No-op since connections are managed per method
    }
}