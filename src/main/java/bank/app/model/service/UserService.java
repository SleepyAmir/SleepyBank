package bank.app.model.service;

import bank.app.model.entity.User;
import bank.app.model.repository.UserRepository;
import bank.app.model.repository.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserService implements Service<User, Integer> {

    private final CardService cardService = new CardService();
    private final TransactionService transactionService = new TransactionService();
    private final ChequeService chequeService = new ChequeService();

    public User authenticate(String username, String password) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password) && u.isActive())
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public void save(User user) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            userRepository.save(user);
        }
    }

    @Override
    public void edit(User user) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            userRepository.edit(user);
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnectionProvider().getConnection();
            conn.setAutoCommit(false);

            // Fetch user to ensure it exists
            try (UserRepository userRepository = new UserRepository()) {
                User user = userRepository.findById(id);
                if (user == null) {
                    throw new Exception("User with ID " + id + " not found");
                }
                System.out.println("Removing user: " + user.getUsername() + " (ID: " + id + ")");
            }

            // Delete dependent records
            deleteDependentRecords(id, conn);

            // Delete the user
            String deleteUserSQL = "DELETE FROM USERS WHERE USER_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSQL)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " user(s) with ID: " + id);
                if (rowsAffected == 0) {
                    throw new Exception("No user deleted for ID: " + id);
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Rolled back transaction due to: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new Exception("Failed to remove user: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteDependentRecords(Integer userId, Connection conn) throws SQLException {
        // Step 1: Delete TRANSACTIONS linked to user's CARDs
        String deleteTransactionsSQL = "DELETE FROM TRANSACTIONS WHERE SOURCE_ACCOUNT IN (SELECT ID FROM CARD WHERE U_ID = ?) OR DESTINATION_ACCOUNT IN (SELECT ID FROM CARD WHERE U_ID = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteTransactionsSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " transaction(s) for user ID: " + userId);
        }

        // Step 2: Delete CARD records
        String deleteCardsSQL = "DELETE FROM CARD WHERE U_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteCardsSQL)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " card(s) for user ID: " + userId);
        }

        // Step 3: Delete CHEQUES records
        String deleteChequesSQL = "DELETE FROM CHEQUES WHERE U_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteChequesSQL)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " cheque(s) for user ID: " + userId);
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            return userRepository.findAll();
        }
    }

    @Override
    public User findById(Integer id) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            return userRepository.findById(id);
        }
    }
}