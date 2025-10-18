import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection class handles connection to MySQL database
 * This is a utility class that provides database connection to other classes
 */
public class DatabaseConnection {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/event_management_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = System.getenv("PASSWORD"); // Change to your MySQL password

    private static Connection connection = null;

    /**
     * Gets a connection to the database
     * If connection doesn't exist, it creates one
     * If connection exists, it returns the existing one
     *
     * @return Connection object to interact with database
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully.");
            }
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            System.err.println("Error: Database connection failed.");
            System.err.println("Please check your URL, username, and password.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection.");
            e.printStackTrace();
        }
    }

    /**
     * Test method to verify database connection
     */
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        Connection conn = getConnection();

        if (conn != null) {
            System.out.println("SUCCESS: Database connection is working properly.");
            closeConnection();
        } else {
            System.out.println("FAILED: Unable to establish database connection.");
        }
    }
}