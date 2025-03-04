package bank.app.model.repository.utils;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;


public class ConnectionProvider {
    private static BasicDataSource dataSource = new BasicDataSource();

//  Singleton
    private static ConnectionProvider connectionProvider;

    public static ConnectionProvider getConnectionProvider() {
        if (connectionProvider == null) {
            connectionProvider = new ConnectionProvider();
        }
        return connectionProvider;
    }

    private ConnectionProvider() {
    }

    public Connection getConnection() throws Exception {
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        dataSource.setUsername("javase");
        dataSource.setPassword("java123");
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        return dataSource.getConnection();
    }

    public int nextId(String sequenceName) throws Exception {
        PreparedStatement preparedStatement = getConnection().prepareStatement(String.format("SELECT %s.nextval as NEXT_ID from dual", sequenceName));
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("NEXT_ID");
    }
}
