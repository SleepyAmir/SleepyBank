package bank.app.model.repository;

import bank.app.model.entity.User;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

public class UserRepository implements Repository<User, Integer>{
    private Connection connection;
    private PreparedStatement statement;

    @Override
    public void save(User user) throws Exception {
//        connection = ConnectionProvider.getConnectionProvider().getConnection();
//        statement = connection.prepareStatement();
    }

    @Override
    public void edit(User user) throws Exception {

    }

    @Override
    public void remove(Integer id) throws Exception {

    }

    @Override
    public List<User> findAll() throws Exception {
        return Collections.emptyList();
    }

    @Override
    public User findById(Integer id) throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {
        statement.close();
        connection.close();
    }
}
