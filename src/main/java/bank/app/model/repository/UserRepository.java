package bank.app.model.repository;

import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository implements Repository<User, Integer>{
    private Connection connection;
    private PreparedStatement statement;

    @Override
    public void save(User user) throws Exception {
//        connection = ConnectionProvider.getConnectionProvider().getConnection();
//        statement = connection.prepareStatement();
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        user.setId(ConnectionProvider.getConnectionProvider().nextId("users_seq"));
        statement = connection.prepareStatement(
                "INSERT INTO USERS (USER_ID, FIRSTNAME, LASTNAME , EMAIL, PHONE,ADDRESS,BIRTH_DATE,USERNAME,PASSWORD,ROLE_NAME, IS_ACTIVE) VALUES (?,?,?,?,?,?,?,?,?,?,?)"
        );
        statement.setInt(1, user.getId());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastName());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getPhone());
        statement.setString(6, user.getAddress());
        statement.setDate(7, Date.valueOf(user.getBirthDate()));
        statement.setString(8, user.getUsername());
        statement.setString(9, user.getPassword());
        statement.setString(10, String.valueOf(user.getRole()));
        statement.setBoolean(11, user.isActive());
        statement.execute();
    }

    @Override
    public void edit(User user) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "UPDATE USERS SET FIRSTNAME=?, LASTNAME=?, EMAIL=?,PHONE=?,ADDRESS=?,BIRTH_DATE=?,USERNAME=?, PASSWORD=?,ROLE_NAME=?, IS_ACTIVE=? WHERE USER_ID=?");
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getPhone());
        statement.setString(5, user.getAddress());
        statement.setDate(6, Date.valueOf(user.getBirthDate()));
        statement.setString(7, user.getUsername());
        statement.setString(8, user.getPassword());
        statement.setString(9, String.valueOf(user.getRole()));
        statement.setBoolean(10, user.isActive());
        statement.setInt(11, user.getId());
        statement.execute();

    }

    @Override
    public void remove(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "DELETE FROM USERS WHERE USER_ID=?"
        );
        statement.setInt(1, id);
        statement.execute();

    }

    @Override
    public List<User> findAll() throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "SELECT * FROM PERSONS ORDER BY FAMILY, NAME"
        );
        ResultSet resultSet = statement.executeQuery();

        List<User> userList = new ArrayList<>();
        while(resultSet.next()) {
            User user = User
                    .builder()
                    .id(resultSet.getInt("user_ID"))
                    .firstName(resultSet.getString("FIRSTNAME"))
                    .lastName(resultSet.getString("LASTNAME"))
                    .email(resultSet.getString("EMAIL"))
                    .phone(resultSet.getString("PHONE"))
                    .address(resultSet.getString("ADDRESS"))
                    .birthDate(resultSet.getDate("BIRTH_DATE").toLocalDate())
                    .username(resultSet.getString("USERNAME"))
                    .password(resultSet.getString("PASSWORD"))
                    .role(Role.valueOf(resultSet.getString("ROLE_NAME")))
                    .active(resultSet.getBoolean("IS_ACTIVE"))
                    .build();
            userList.add(user);
        }
        return userList;
    }

    public User findById(Integer id) throws Exception {
        String query = "SELECT * FROM USERS WHERE USER_ID=?";
        System.out.println("Finding user by ID: " + id);
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection()) {
            if (conn == null) {
                throw new SQLException("Connection is null");
            }
            System.out.println("Connection: " + conn);
            System.out.println("Query: " + query);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                System.out.println("Statement: " + stmt);
                if (id == null) {
                    throw new IllegalArgumentException("User ID cannot be null");
                }
                System.out.println("Setting USER_ID=? to: " + id);
                stmt.setInt(1, id);
                System.out.println("Executing query...");
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("Query executed successfully");
                    boolean hasResult = rs.next();
                    System.out.println("Result: " + (hasResult ? "User found" : "No user found"));
                    if (hasResult) {
                        User user = User.builder()
                                .id(rs.getInt("USER_ID"))
                                .firstName(rs.getString("FIRSTNAME"))
                                .lastName(rs.getString("LASTNAME"))
                                .email(rs.getString("EMAIL"))
                                .phone(rs.getString("PHONE"))
                                .address(rs.getString("ADDRESS"))
                                .birthDate(rs.getDate("BIRTH_DATE") != null ? rs.getDate("BIRTH_DATE").toLocalDate() : null)
                                .username(rs.getString("USERNAME"))
                                .password(rs.getString("PASSWORD"))
                                .role(Role.valueOf(rs.getString("ROLE_NAME")))
                                .active(rs.getInt("IS_ACTIVE") == 1)
                                .build();
                        System.out.println("User created: ID=" + user.getId() + ", Username=" + user.getUsername());
                        return user;
                    } else {
                        System.out.println("Returning null: No user found");
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public void close() throws Exception {
        statement.close();
        connection.close();
    }
}
