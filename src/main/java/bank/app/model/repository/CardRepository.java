package bank.app.model.repository;

import bank.app.model.entity.Card;
import bank.app.model.entity.User;
import bank.app.model.entity.enums.AccountType;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements Repository<Card, Integer> {
    private Connection connection;

    public CardRepository() throws Exception {
        this.connection = ConnectionProvider.getConnectionProvider().getConnection();
    }

    @Override
    public void save(Card card) throws Exception {
        Card existingCard = findByCardNumber(card.getCardNumber());
        if (existingCard != null) {
            // Update existing card
            card.setId(existingCard.getId()); // Preserve original ID
            edit(card);
        } else {
            // Insert new card
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO CARD (ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CARD_NUMBER, CVV2, EXPIRY_DATE, U_ID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                int newId = ConnectionProvider.getConnectionProvider().nextId("card_seq");
                card.setId(newId);
                statement.setInt(1, newId);
                statement.setString(2, card.getAccountType().name());
                statement.setDouble(3, card.getBalance());
                statement.setTimestamp(4, Timestamp.valueOf(card.getCreatedAt() != null ? card.getCreatedAt() : LocalDateTime.now()));
                statement.setString(5, card.getCardNumber());
                statement.setString(6, card.getCvv2());
                statement.setDate(7, Date.valueOf(card.getExpiryDate()));
                statement.setInt(8, card.getUser().getId());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void edit(Card card) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE CARD SET ACCOUNT_TYPE=?, BALANCE=?, CREATED_AT=?, CARD_NUMBER=?, CVV2=?, EXPIRY_DATE=?, U_ID=? " +
                        "WHERE ID=?")) {
            statement.setString(1, card.getAccountType().name());
            statement.setDouble(2, card.getBalance());
            statement.setTimestamp(3, Timestamp.valueOf(card.getCreatedAt()));
            statement.setString(4, card.getCardNumber());
            statement.setString(5, card.getCvv2());
            statement.setDate(6, Date.valueOf(card.getExpiryDate()));
            statement.setInt(7, card.getUser().getId());
            statement.setInt(8, card.getId());
            statement.executeUpdate();
        }
    }

    // Add helper method to check if card exists
    public Card findByCardNumber(String cardNumber) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM CARD WHERE CARD_NUMBER=?")) {
            statement.setString(1, cardNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository();
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
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM CARD WHERE ID=?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Card> findAll() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM CARD");
             ResultSet rs = statement.executeQuery()) {
            List<Card> cards = new ArrayList<>();
            UserRepository userRepo = new UserRepository();
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
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM CARD WHERE ID=?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                UserRepository userRepo = new UserRepository();
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
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}