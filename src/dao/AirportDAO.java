package dao;

import db.DBConnection;
import models.Airport;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AirportDAO {

    public static boolean addAirport(String name, String location) throws SQLException {
        String sql = "INSERT INTO airports (name, location) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, location.trim());
            return ps.executeUpdate() > 0;
        }
    }

    public static List<Airport> getAllAirports() {
        List<Airport> airports = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM airports"; // Fetch all airports
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Airport airport = new Airport(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location")
                );
                airports.add(airport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return airports;
    }

    public static boolean updateAirport(int id, String name, String location) throws SQLException {
        String sql = "UPDATE airports SET name = ?, location = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, location.trim());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deleteAirport(int id) throws SQLException {
        String sql = "DELETE FROM airports WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

//    public static Airport getAirportById(int id) {
//        String sql = "SELECT * FROM airports WHERE id = ?";
//        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return new Airport(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getString("location")
//                );
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static Map<String, Integer> getAirportIdNameMap() throws Exception {
        Map<String, Integer> airportMap = new LinkedHashMap<>();

        String sql = "SELECT id, name FROM airports ORDER BY name";  // Use 'id' not 'airport_id'

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");            // correct column
                String name = rs.getString("name"); // correct column
                airportMap.put(name, id);
            }
        }

        return airportMap;
    }

//    public static Map<String, Integer> getAirportsByAirplaneId(int airplaneId) throws SQLException {
//        return getAirportIdNameMap();
//    }
}
