package dao;

import db.DBConnection;
import models.User;

import java.sql.*;
import java.util.*;

public class UserDAO {

    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status=TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static boolean activateUser(int userId) throws SQLException {
    Connection conn = DBConnection.getConnection(); // Adjust to your connection method
    String query = "UPDATE users SET status = TRUE WHERE id = ?";
    PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, userId);
    return stmt.executeUpdate() > 0;
}


    public static boolean addUser(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean updateUser(int id, String username, String password, String role) throws SQLException {
        String sql;
        if (password != null && !password.isEmpty()) {
            sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET username = ?, role = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            if (password != null && !password.isEmpty()) {
                ps.setString(2, password);
                ps.setString(3, role);
                ps.setInt(4, id);
            } else {
                ps.setString(2, role);
                ps.setInt(3, id);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deactivateUser(int userId) throws SQLException {
        String sql = "UPDATE users SET status = FALSE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Integer> getAllCustomerUserMap() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT id, username FROM users WHERE role = 'Customer'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("username"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<String> getAllCustomerUsernames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE role = 'Customer'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
