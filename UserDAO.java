import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO handles all database operations related to users
 * Includes login, registration, profile management
 */
public class UserDAO {

    /**
     * Authenticate user login
     */
    public static User login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getString("interests")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Register a new user
     */
    public static boolean register(User user) {
        if (usernameExists(user.getUsername()) || emailExists(user.getEmail())) {
            System.err.println("Registration failed: Username or email already in use.");
            return false;
        }

        String query = "INSERT INTO users (username, password, full_name, email, phone, role, interests) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getInterests());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if username already exists
     */
    public static boolean usernameExists(String username) {
        String query = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if email already exists
     */
    public static boolean emailExists(String email) {
        String query = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get user by ID
     */
    public static User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getString("interests")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update user profile
     */
    public static boolean updateProfile(User user) {
        String query = "UPDATE users SET full_name = ?, email = ?, phone = ?, interests = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getInterests());
            pstmt.setInt(5, user.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
        return false;
    }

    /**
     * Change password for a given user.
     * This was the missing method.
     */
    public static boolean changePassword(int userId, String oldPassword, String newPassword) {
        String checkQuery = "SELECT password FROM users WHERE user_id = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // First, check if the old password is correct
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("password");
                if (!currentPassword.equals(oldPassword)) {
                    System.err.println("Password change failed: Incorrect old password.");
                    return false; // Incorrect old password
                }

                // If correct, update to the new password
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, userId);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all users (for admin)
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY user_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "", // Never return password in a list
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getString("interests")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
}