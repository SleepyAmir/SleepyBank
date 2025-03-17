package bank.app.model.repository;

import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<User, Integer>, AutoCloseable { // Explicitly implement AutoCloseable
    private Connection connection;
    private PreparedStatement statement;

    public UserRepository() throws Exception {
        this.connection = ConnectionProvider.getConnectionProvider().getConnection();
        if (connection == null || connection.isClosed()) {
            throw new Exception("Failed to initialize database connection");
        }
    }

    @Override
    public void save(User user) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        int nextId = ConnectionProvider.getConnectionProvider().nextId("USER_SEQ");
        String sql = "INSERT INTO USERS (USER_ID, FIRSTNAME, LASTNAME, EMAIL, PHONE, ADDRESS, BIRTH_DATE, USERNAME, PASSWORD, ROLE_NAME, REGISTRATION_DATE, IS_ACTIVE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nextId);
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getAddress());
            statement.setDate(7, Date.valueOf(user.getBirthDate()));
            statement.setString(8, user.getUsername());
            statement.setString(9, user.getPassword());
            statement.setString(10, user.getRole().name());
            statement.setDate(11, user.getRegistrationDate() != null ? Date.valueOf(user.getRegistrationDate()) : null);
            statement.setInt(12, user.isActive() ? 1 : 0);
            statement.executeUpdate();
            user.setId(nextId);
        }
    }

    @Override
    public void edit(User user) throws Exception {
        String sql = "UPDATE USERS SET FIRSTNAME=?, LASTNAME=?, EMAIL=?, PHONE=?, ADDRESS=?, BIRTH_DATE=?, USERNAME=?, PASSWORD=?, ROLE_NAME=?, IS_ACTIVE=?, REGISTRATION_DATE=? " +
                "WHERE USER_ID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getAddress());
            statement.setDate(6, Date.valueOf(user.getBirthDate()));
            statement.setString(7, user.getUsername());
            statement.setString(8, user.getPassword());
            statement.setString(9, user.getRole().name());
            statement.setInt(10, user.isActive() ? 1 : 0);
            statement.setDate(11, user.getRegistrationDate() != null ? Date.valueOf(user.getRegistrationDate()) : null);
            statement.setInt(12, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM USERS WHERE USER_ID=?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS ORDER BY LASTNAME, FIRSTNAME");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt("USER_ID"))
                        .firstName(resultSet.getString("FIRSTNAME"))
                        .lastName(resultSet.getString("LASTNAME"))
                        .email(resultSet.getString("EMAIL"))
                        .phone(resultSet.getString("PHONE"))
                        .address(resultSet.getString("ADDRESS"))
                        .birthDate(resultSet.getDate("BIRTH_DATE") != null ? resultSet.getDate("BIRTH_DATE").toLocalDate() : null)
                        .username(resultSet.getString("USERNAME"))
                        .password(resultSet.getString("PASSWORD"))
                        .role(Role.valueOf(resultSet.getString("ROLE_NAME")))
                        .active(resultSet.getInt("IS_ACTIVE") == 1)
                        .registrationDate(resultSet.getDate("REGISTRATION_DATE") != null ? resultSet.getDate("REGISTRATION_DATE").toLocalDate() : null)
                        .build();
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public User findById(Integer id) throws Exception {
        String query = "SELECT * FROM USERS WHERE USER_ID=?";
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return User.builder()
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
                            .registrationDate(rs.getDate("REGISTRATION_DATE") != null ? rs.getDate("REGISTRATION_DATE").toLocalDate() : null)
                            .build();
                }
                return null;
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (statement != null && !statement.isClosed()) statement.close();
        if (connection != null && !connection.isClosed()) connection.close();
    }
}