package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Utility class to manage the database connection for pim
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pim?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection connection;

    private DBConnection() { }

    // Returns a live connection, creating one if needed
    public static Connection getConnection() throws SQLException {
        
        if (connection == null || connection.isClosed()) { connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); }

        return connection;

    }

    // Closes the connection if open
    public static void closeConnection() {
        
        try {
        
            if (connection != null && !connection.isClosed()) { connection.close(); }
        
        } catch (SQLException e) { e.printStackTrace(); }
    
    }

}
