package dao;

import db.DBConnection;
import java.sql.Timestamp;
//import java.beans.Statement;
import models.Airplane;
import java.sql.Statement;  // For RETURN_GENERATED_KEYS
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AirplaneDAO {

//    public static boolean addAirplane(String code, String type, Integer airportId, Boolean available) throws SQLException {
//        String sql = "INSERT INTO airplanes (code, type, current_airport_id, is_available) VALUES (?, ?, ?, ?)";
//        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, code.trim());
//            ps.setString(2, type.trim());
//            ps.setInt(3, airportId);
//            ps.setBoolean(4, available);
//            return ps.executeUpdate() > 0;
//        }
//    }
    public static Map<String, Integer> getAvailableAirplanes(Timestamp departure, Timestamp arrival) throws Exception {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT id, code FROM airplanes "
                + "WHERE id NOT IN ("
                + "  SELECT airplane_id FROM flights "
                + "  WHERE status = 'Scheduled' "
                + "    AND NOT (arrival_time <= ? OR departure_time >= ?)"
                + ") "
                + "AND is_available = TRUE";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, departure);
            ps.setTimestamp(2, arrival);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("code"), rs.getInt("id"));
            }
        }
        return result;
    }

    public static int addAirplane(String code, String type, Integer airportId, Boolean available) throws SQLException {
        String sql = "INSERT INTO airplanes (code, type, current_airport_id, is_available) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, code.trim());
            ps.setString(2, type.trim());
            ps.setInt(3, airportId);
            ps.setBoolean(4, available);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return -1; // Insert failed
            }

            // Get the generated key
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the new airplane id
                } else {
                    return -1; // No ID obtained
                }
            }
        }
    }

    public static Map<String, Integer> getAllAirplanes() throws Exception {
        Map<String, Integer> airplaneMap = new LinkedHashMap<>();

        String sql = "SELECT id, code FROM airplanes ORDER BY code";  // use 'id' and 'code', not 'airplane_id' or 'name'

        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int airplaneId = rs.getInt("id");       // correct column name
                String airplaneCode = rs.getString("code");  // display code for airplane
                airplaneMap.put(airplaneCode, airplaneId);
            }
        }

        return airplaneMap;
    }

    public static Map<String, Integer> getAvailableAirplanesByTimeRange(String fromTime, String toTime) throws SQLException {

        String sql = """
            SELECT a.id, a.code
            FROM airplanes a
            WHERE a.id NOT IN (
                SELECT f.id FROM flights f
                WHERE TIME(f.departure_time) < ? AND TIME(f.arrival_time) > ?
            )
            ORDER BY a.code
            """;

        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toTime);
            ps.setString(2, fromTime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("code");
                    result.put(name, id);
                }
            }
        }
        return result;
    }

}
