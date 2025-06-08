package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/airline_db"; // URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "2001"; // MySQL password
    
    private static Connection connection;

    // Get connection
    public static Connection getConnection() {
        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection successful!");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error if the connection fails
            System.out.println("Failed to connect to the database.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
}
