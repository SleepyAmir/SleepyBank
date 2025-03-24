package bank.app.model.service;

import bank.app.model.entity.User;
import bank.app.model.repository.UserRepository;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserService implements Service<User, Integer> { // Properly implements Service<User, Integer>
    @Override
    public void save(User user) throws Exception {
        try (UserRepository repo = new UserRepository()) {
            repo.save(user);
        }
    }

    @Override
    public void edit(User user) throws Exception {
        try (UserRepository repo = new UserRepository()) {
            repo.edit(user);
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (Connection conn = ConnectionProvider.getConnectionProvider().getConnection()) {
            conn.setAutoCommit(false);
            try (UserRepository repo = new UserRepository()) {
                User user = repo.findById(id);
                if (user == null) throw new Exception("User with ID " + id + " not found");
                System.out.println("Removing user: " + user.getUsername() + " (ID: " + id + ")");
            }
            deleteDependentRecords(id, conn);
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM USERS WHERE USER_ID=?")) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " user(s) with ID: " + id);
                if (rowsAffected == 0) throw new Exception("No user deleted for ID: " + id);
            }
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Failed to remove user: " + e.getMessage(), e);
        }
    }

    private void deleteDependentRecords(Integer userId, Connection conn) throws SQLException {
        String deleteTransactionsSQL = "DELETE FROM TRANSACTIONS WHERE SOURCE_ACCOUNT IN (SELECT ID FROM CARD WHERE U_ID = ?) OR DESTINATION_ACCOUNT IN (SELECT ID FROM CARD WHERE U_ID = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteTransactionsSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " transaction(s) for user ID: " + userId);
        }
        String deleteCardsSQL = "DELETE FROM CARD WHERE U_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteCardsSQL)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " card(s) for user ID: " + userId);
        }
        String deleteChequesSQL = "DELETE FROM CHEQUES WHERE U_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteChequesSQL)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " cheque(s) for user ID: " + userId);
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        try (UserRepository repo = new UserRepository()) {
            return repo.findAll();
        }
    }

    @Override
    public User findById(Integer id) throws Exception {
        try (UserRepository repo = new UserRepository()) {
            return repo.findById(id);
        }
    }

    public User authenticate(String username, String password) throws Exception {
        try (UserRepository repo = new UserRepository()) {
            return repo.findByUsernameAndPassword(username, password);
        }
    }
}