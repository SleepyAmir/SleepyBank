package bank.app.model.repository;

import bank.app.model.entity.User;
import bank.app.model.entity.enums.Role;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @Override
    public User findById(Integer id) throws Exception {
        connection = ConnectionProvider.getConnectionProvider().getConnection();
        statement = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USER_ID=?"
        );
        ResultSet resultSet = statement.executeQuery();

        User user = null;
        if(resultSet.next()) {
            user = User
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
        }
        return user;

    }

    @Override
    public void close() throws Exception {
        statement.close();
        connection.close();
    }
}
