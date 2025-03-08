package bank.app.model.repository;

import bank.app.model.entity.Card;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

public class CardRepository implements Repository<Card, Integer>{
    private Connection connection;
    private PreparedStatement statement;

    @Override
    public void save(Card card) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "INSERT INTO CARD(ID,ACCOUNT_TYPE,BALANCE,CREATED_AT,CARD_NUMBER,CVV2,EXPIRY_DATE,U_ID) VALUES (?,?,?,?,?,?,?,?)");

    }

    @Override
    public void edit(Card card) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "UPDATE CARD SET ACCOUNT_TYPE=?,BALANCE=?,CREATED_AT=?,CARD_NUMBER=?,CVV2=?,EXPIRY_DATE=?,U_ID=? WHERE ID=?");

    }

    @Override
    public void remove(Integer id) throws Exception {

    }

    @Override
    public List<Card> findAll() throws Exception {
        return Collections.emptyList();
    }

    @Override
    public Card findById(Integer id) throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}









//package bank.app.model.repository;
//
//
//import bank.app.model.entity.Card;
//import bank.app.model.repository.utils.ConnectionProvider;
//
//import java.sql.*;
//
//public class CardRepository implements Repository<Card, Long> {
//    private Connection connection;
//    private PreparedStatement statement;
//
//    @Override
//    public void save(Card card) throws Exception {
//        connection = ConnectionProvider.getConnectionProvider().getConnection();
//        card.setUser(ConnectionProvider.getConnectionProvider().nextId("card_seq"));
//        statement = connection.prepareStatement(
//                "INSERT INTO CARD (ID, ACCOUNT_TYPE, BALANCE, CREATED_AT, CARD_NUMBER, CVV2, EXPIRY_DATE, U_ID) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
//        );
//        statement.setLong(1, card.getId());
//        statement.setString(2, card.getAccountType());
//        statement.setDouble(3, card.getBalance());
//        statement.setTimestamp(4, Timestamp.valueOf(card.getCreatedAt()));
//        statement.setString(5, card.getCardNumber());
//        statement.setString(6, card.getCvv2());
//        statement.setDate(7, Date.valueOf(card.getExpiryDate()));
//        statement.setInt(8, card.getUId());
//        statement.execute();
//    }
//
//    @Override
//    public void edit(Card card) throws Exception {
//        connection = ConnectionProvider.getConnectionProvider().getConnection();
//        statement = connection.prepareStatement(
//                "UPDATE CARD SET ACCOUNT_TYPE=?, BALANCE=?, CREATED_AT=?, CARD_NUMBER=?, CVV2=?, EXPIRY_DATE=?, U_ID=? " +
//                        "WHERE ID=?"
//        );
//        statement.setString(1, card.getAccountType());
//        statement.setDouble(2, card.getBalance());
//        statement.setTimestamp(3, Timestamp.valueOf(card.getCreatedAt()));
//        statement.setString(4, card.getCardNumber());
//        statement.setString(5, card.getCvv2());
//        statement.setDate(6, Date.valueOf(card.getExpiryDate()));
//        statement.setInt(7, card.getUId());
//        statement.setLong(8, card.getId());
//        statement.execute();
//    }
//
//    // Other required methods (remove, findAll, findById, close) would follow similar pattern
//    // Implementing only the needed ones for the service
//
//    @Override
//    public Card findById(Long id) throws Exception {
//        connection = ConnectionProvider.getConnectionProvider().getConnection();
//        statement = connection.prepareStatement(
//                "SELECT * FROM CARD WHERE ID=?"
//        );
//        statement.setLong(1, id);
//        ResultSet resultSet = statement.executeQuery();
//
//        Card card = null;
//        if (resultSet.next()) {
//            card = Card.builder()
//                    .id(resultSet.getLong("ID"))
//                    .accountType(resultSet.getString("ACCOUNT_TYPE"))
//                    .balance(resultSet.getDouble("BALANCE"))
//                    .createdAt(resultSet.getTimestamp("CREATED_AT").toLocalDateTime())
//                    .cardNumber(resultSet.getString("CARD_NUMBER"))
//                    .cvv2(resultSet.getString("CVV2"))
//                    .expiryDate(resultSet.getDate("EXPIRY_DATE").toLocalDate())
//                    .uId(resultSet.getInt("U_ID"))
//                    .build();
//        }
//        return card;
//    }
//
//    @Override
//    public void close() throws Exception {
//        statement.close();
//        connection.close();
//    }
//
//    // Other methods not implemented for brevity
//    @Override public void remove(Long id) throws Exception {}
//    @Override public List<Card> findAll() throws Exception { return null; }
//}